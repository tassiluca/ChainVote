#!/bin/bash
#
# Script to automatize the bring up and down of the blockchain network.

set -e  # Exit immediately if some command (simple or compound) returns a non-zero status

if [[ $# != 1 || ($1 != "up" && $1 != "down") ]]; then
    echo "Usage: ./network [up|down]"
    echo "Description:"
    echo "  Script used to easily bring up and down the network."
    echo 
    echo "Options:"
    echo "  up  - Bring up the network"
    echo "  down - Bring down the network"
    exit 1
fi;

function test() {
    echo "Ended"
    exit 1
}

function upNetwork() {
    echo "Setup binaries"
    if [[ ! -d ./bin/ ]]; then
        ./install-binaries.sh
    fi;
    export PATH="$PATH:$PWD/bin"
    echo "Up ca-tls rca-org0 rca-org1 rca-org2"
    docker-compose up ca-tls rca-org0 rca-org1 rca-org2 --wait
    sleep 10
    echo "Enrol registrar of each CA and register all entities"
    ./reg.sh
    echo "Enrol entities for each organization"
    ./enroll.sh
    echo "Creating crypto material"
    cd ./channels_config
    ./channel_artifacts.sh
    echo "Bring up the whole network"
    docker-compose up --wait
    sleep 10
    echo "Create and joining channels"
    ./channel_creation.sh
}

function downNetwork() {
    echo "Downing network..."
    docker-compose down
    echo "Delete /tmp/hyperledger/..."
    rm -rf /tmp/hyperledger
    echo "Done."
}

if [[ $1 == "up" ]]; then
    upNetwork
elif [[ $1 == "down" ]]; then
    downNetwork
fi;
