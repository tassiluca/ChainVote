#!/bin/bash
#
# Script to automatize the bring up and down of the blockchain network.

set -e  # Exit immediately if some command (simple or compound) returns a non-zero status

# Directory where store blockchain artifacts
export $(cat ../config.env | xargs)
export ARTIFACTS_DIR=$(pwd)/../$ARTIFACTS_DIR

function printHelp() {
    echo "Usage: ./network [up|down]"
    echo "Description:"
    echo "  Script used to easily bring up and down the network."
    echo 
    echo "Options:"
    echo "  up  - Bring up the network"
    echo "  down - Bring down the network (without removing artifacts)"
    echo "  clean - Bring down the network and remove artifacts"
}

function containers() {
    local uid=$(id -u)
    local gid=$(id -g)
    local os=$(uname -s)
    if [ "$1" == "down" ]; then
        HOST_UID=$uid HOST_GID=$gid HOST_OS=$os docker compose down
    elif [ "$1" == "up" ]; then
        HOST_UID=$uid HOST_GID=$gid HOST_OS=$os docker compose up -d --build --wait "${@:2}"
    else
        echo "Invalid command."
        exit 1
    fi
}

function upNetwork() {
    echo "Setup binaries"
    if [[ ! -d ./bin/ ]]; then
        ./install-binaries.sh
    fi;
    export PATH="$PATH:$PWD/bin"
    if [[ ! -d $ARTIFACTS_DIR ]]; then 
        echo "Artifacts directory: $ARTIFACTS_DIR"
        mkdir -p $ARTIFACTS_DIR/org0/{ca,artifacts,orderer1,orderer2,orderer3}
        mkdir -p $ARTIFACTS_DIR/org1/{ca,peer1}
        mkdir -p $ARTIFACTS_DIR/org2/{ca,peer1,peer2}
        mkdir -p $ARTIFACTS_DIR/org3/{ca,peer1,peer2}
        mkdir -p $ARTIFACTS_DIR/tls-ca
        echo "Up ca-tls rca-org0 rca-org1 rca-org2"
        containers up ca-tls rca-org0 rca-org1 rca-org2 rca-org3
        echo "Enrol registrar of each CA and register all entities"
        ./reg.sh
        echo "Enrol entities for each organization"
        ./enroll.sh
    fi;
    echo "Creating crypto material"
    cd ./channels_config
    ./channel_artifacts.sh
    echo "Bring up the whole network"
    containers up
    sleep 10
    echo "Create and joining channels"
    ./channel_creation.sh
}

function downNetwork() {
    export ARTIFACTS_DIR=$ARTIFACTS_DIR
    echo "Downing network..."
    containers down
    echo "Done."
}

function downNetworkAndClean() {
    downNetwork
    echo "Removing artifacts..."
    rm -rf $ARTIFACTS_DIR
    echo "Done."
}

if [[ $1 == "up" ]]; then
    upNetwork
elif [[ $1 == "down" ]]; then
    downNetwork
elif [[ $1 == "clean" ]]; then
    downNetworkAndClean
else
    printHelp
    exit 1
fi;
