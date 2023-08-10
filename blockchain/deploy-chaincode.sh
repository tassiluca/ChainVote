#!/bin/bash
#
# Script to deploy a Java chaincode on the HF network.

if [[ $# != 2 || ($1 != "org1" && $1 != "org2") || ! -d $2 ]]; then
    echo "Usage: ./deploy-chaincode <organization-name> <chaincode-path>"
    echo "Description:"
    echo "  Script used to deploy **java** chaincode to the Hyperledger Fabric network."
    echo
    echo "Options:"
    echo "  <organization-name>  - The name of the organization, i.e. [org1|org2]"
    echo "  <chaincode-path> - The path to the chaincode executable, e.g. ~/chain-vote/chaincode/build/install/<chaincode-name>"
    exit 1
fi;

CHAINCODE_ORG=$1
if [[ ${CHAINCODE_ORG} == "org1" ]]; then
    CHANNEL_NAME="ch1"
    PEER1_ADDRESS=localhost:7051
    PEER2_ADDRESS=localhost:8051
else
    CHANNEL_NAME="ch2"
    PEER1_ADDRESS=localhost:9051
    PEER2_ADDRESS=localhost:10051
fi;
CHAINCODE_PATH=$2
CHAINCODE_NAME=$(basename "$CHAINCODE_PATH")
CHAINCODE_PACKAGE_NAME=${CHAINCODE_NAME}.tar.gz

export PATH="$PATH:$PWD/bin"
export FABRIC_CFG_PATH=$PWD/channels_config

echo "Generating chaincode package"
peer lifecycle chaincode package "${CHAINCODE_PACKAGE_NAME}" --path "${CHAINCODE_PATH}" --lang java --label "${CHAINCODE_PACKAGE_NAME}"_1.0

export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="${CHAINCODE_ORG}MSP"
export CORE_PEER_MSPCONFIGPATH=/tmp/hyperledger/${CHAINCODE_ORG}/admin/msp

echo "Installation on peer1-${CHAINCODE_ORG}"
export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/${CHAINCODE_ORG}/peer1/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_ADDRESS=${PEER1_ADDRESS}
peer lifecycle chaincode install "${CHAINCODE_PACKAGE_NAME}"

echo "Installation on peer2-${CHAINCODE_ORG}"
export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/${CHAINCODE_ORG}/peer2/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_ADDRESS=${PEER2_ADDRESS}
peer lifecycle chaincode install "${CHAINCODE_PACKAGE_NAME}"

echo "Checking installation"
INSTALLED_CHAINCODE=$(peer lifecycle chaincode queryinstalled)
PACKAGE_ID=$(echo "${INSTALLED_CHAINCODE}" | grep -oE 'Package ID: ([^,]+)' | cut -d ' ' -f 3)

echo "Chaincode approval"
peer lifecycle chaincode approveformyorg --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --channelID ${CHANNEL_NAME} --name "${CHAINCODE_NAME}" --version 1.0 --package-id "${PACKAGE_ID}" --sequence 1 --tls --cafile /tmp/hyperledger/"${CHAINCODE_ORG}"/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
echo "Verify approval"
peer lifecycle chaincode checkcommitreadiness --channelID ${CHANNEL_NAME} --name "${CHAINCODE_NAME}" --version 1.0 --sequence 1 --tls --cafile /tmp/hyperledger/"${CHAINCODE_ORG}"/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem --output json

echo "Committing"
peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --channelID ${CHANNEL_NAME} --name "${CHAINCODE_NAME}" --version 1.0 --sequence 1 --tls --cafile /tmp/hyperledger/"${CHAINCODE_ORG}"/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem --peerAddresses ${PEER1_ADDRESS} --tlsRootCertFiles /tmp/hyperledger/"${CHAINCODE_ORG}"/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem --peerAddresses ${PEER2_ADDRESS} --tlsRootCertFiles /tmp/hyperledger/"${CHAINCODE_ORG}"/peer2/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
echo "Checking commit"
peer lifecycle chaincode querycommitted --channelID ${CHANNEL_NAME} --name "${CHAINCODE_NAME}" --cafile /tmp/hyperledger/"${CHAINCODE_ORG}"/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem

echo "Cleaning"
rm "${CHAINCODE_PACKAGE_NAME}"
