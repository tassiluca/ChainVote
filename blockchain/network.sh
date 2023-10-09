#!/bin/bash
#
# Script to automatize the bring up and down of the blockchain network.

set -e  # Exit immediately if some command (simple or compound) returns a non-zero status

# Directory where store blockchain artifacts
ARTIFACTS_DIR=$HOME/$(jq -r '.blockchainDataDirectory' ../config.json)

if [[ "$#" != 1 || ($1 != "up" && $1 != "down") ]]; then
    echo "Usage: ./network [up|down]"
    echo "Description:"
    echo "  Script used to easily bring up and down the network."
    echo 
    echo "Options:"
    echo "  up  - Bring up the network"
    echo "  down - Bring down the network"
    exit 1
fi;

function upNetwork() {
    echo "Artifacts: $ARTIFACTS_DIR"
    if [[ ! -d $ARTIFACTS_DIR ]]; then 
        mkdir -p $ARTIFACTS_DIR 
    fi;
    export ARTIFACTS_DIR=$ARTIFACTS_DIR
    echo "Setup binaries"
    if [[ ! -d ./bin/ ]]; then
        ./install-binaries.sh
    fi;
    export PATH="$PATH:$PWD/bin"
    echo "Up ca-tls rca-org0 rca-org1 rca-org2"
    docker compose up -d --wait ca-tls rca-org0 rca-org1 rca-org2
    echo "Services up and running!"
    echo "Enrol registrar of each CA and register all entities"
    ./reg.sh
    echo "Enrol entities for each organization"
    ./enroll.sh
    echo "Creating crypto material"
    cd ./channels_config
    ./channel_artifacts.sh #$ARTIFACTS_DIR
    echo "Bring up the whole network"
    docker compose up -d --wait
    echo "Services up and running!"
    echo "Create and joining channels"
    ./channel_creation.sh #$ARTIFACTS_DIR
}

function downNetwork() {
    export ARTIFACTS_DIR=$ARTIFACTS_DIR
    echo "Downing network..."
    docker-compose down
    echo "Delete $ARTIFACTS_DIR..."
    rm -rf $ARTIFACTS_DIR
    echo "Done."
}

if [[ $1 == "up" ]]; then
    upNetwork
elif [[ $1 == "down" ]]; then
    downNetwork
fi;
