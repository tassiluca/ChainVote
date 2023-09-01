#!/bin/bash
#
# Script to deploy a Java chaincode on the HF network.

source utils.sh

if [[ $# -lt 2 || ! -d $1 || ($2 != "org1" && $2 != "org2") ]]; then
  echo "Usage: ./deploy-chaincode <chaincode-path> <organization-name> [<peers>]"
  echo "Description:"
  echo "  Script used to deploy **java** chaincode to the Hyperledger Fabric network."
  echo "  Note: if private data are used this script expect to find the \"collections-config.json\" config file located in the resources folder of the java project"
  echo
  echo "Options:"
  echo "  <organization-name>      - The name of the organization, i.e. [org1|org2]"
  echo "  <chaincode-path>         - The path to the java chaincode project"
  echo "  [OPTIONAL] <peers>       - The comma-separated list of peers on which deploy the chaincode, i.e. [peer1|peer2]. Default value: all org peers'"
  exit 1
fi;

CHAINCODE_PATH=$1
CHAINCODE_ORG=$2
if [[ $# == 2 ]]; then
  PEERS=(peer1 peer2)
else
  PEERS=()
  for i in $(seq 3 1 $#); do
    PEERS+=("${!i}")
  done
fi;
if [[ $CHAINCODE_ORG == "org1" ]]; then
    CHANNEL_NAME="ch1"
    PEER1_ADDRESS=localhost:7051
    PEER2_ADDRESS=localhost:8051
else
    CHANNEL_NAME="ch2"
    PEER1_ADDRESS=localhost:9051
    PEER2_ADDRESS=localhost:10051
fi;
COLLECTIONS_CONFIG_ARG=()
if [[ -f "$CHAINCODE_PATH/src/main/resources/collections-config.json" ]]; then
  COLLECTIONS_CONFIG_ARG+=(
    "--collections-config" "$(realpath "$CHAINCODE_PATH/src/main/resources/collections-config.json")"
  )
fi;
CHAINCODE_NAME=$(basename "$CHAINCODE_PATH")
CHAINCODE_PACKAGE_NAME=${CHAINCODE_NAME}.tar.gz

export PATH="$PATH:$PWD/bin"
export FABRIC_CFG_PATH=$PWD/channels_config/$CHAINCODE_ORG

echo "Generating chaincode package"
pushd "$CHAINCODE_PATH"/.. || exit 2
./gradlew clean
./gradlew "$CHAINCODE_NAME":installDist
cp -r "${CHAINCODE_NAME}"/META-INF "${CHAINCODE_NAME}"/build/install/"${CHAINCODE_NAME}"
popd || exit 2
peer lifecycle chaincode package "${CHAINCODE_PACKAGE_NAME}" --path "${CHAINCODE_PATH}/build/install/${CHAINCODE_NAME}" --lang java --label "${CHAINCODE_PACKAGE_NAME}_1.0"

export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="${CHAINCODE_ORG}MSP"
export CORE_PEER_MSPCONFIGPATH=/tmp/hyperledger/${CHAINCODE_ORG}/admin/msp

if in_array "peer1" "${PEERS[@]}"; then
  echo "Installation on peer1-${CHAINCODE_ORG}"
  export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/${CHAINCODE_ORG}/peer1/assets/tls-ca/tls-ca-cert.pem
  export CORE_PEER_ADDRESS=${PEER1_ADDRESS}
  peer lifecycle chaincode install "${CHAINCODE_PACKAGE_NAME}"
fi;
if in_array "peer2" "${PEERS[@]}"; then
  echo "Installation on peer2-${CHAINCODE_ORG}"
  export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/${CHAINCODE_ORG}/peer2/assets/tls-ca/tls-ca-cert.pem
  export CORE_PEER_ADDRESS=${PEER2_ADDRESS}
  peer lifecycle chaincode install "${CHAINCODE_PACKAGE_NAME}"
fi;

echo "Checking installation"
INSTALLED_CHAINCODE=$(peer lifecycle chaincode queryinstalled)
PACKAGE_ID=$(echo "${INSTALLED_CHAINCODE}" | grep -oE 'Package ID: ([^,]+)' | cut -d ' ' -f 3)

if in_array "peer1" "${PEERS[@]}"; then
  CA_FILE=/tmp/hyperledger/"${CHAINCODE_ORG}"/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
elif in_array "peer2" "${PEERS[@]}"; then
  CA_FILE=/tmp/hyperledger/"${CHAINCODE_ORG}"/peer2/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
fi;

echo "Chaincode approval"
peer lifecycle chaincode approveformyorg --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --name "$CHAINCODE_NAME" --channelID "$CHANNEL_NAME" --version "1.0" --package-id "$PACKAGE_ID" --sequence "1" --cafile "$CA_FILE" --tls "${COLLECTIONS_CONFIG_ARG[@]}"
echo "Verify approval"
peer lifecycle chaincode checkcommitreadiness --channelID "$CHANNEL_NAME" --name "$CHAINCODE_NAME" --version 1.0 --sequence 1 --tls --cafile "$CA_FILE" --output json "${COLLECTIONS_CONFIG_ARG[@]}"

PEER_ADDRESSES_ARGS=()
if in_array "peer1" "${PEERS[@]}"; then
  PEER_ADDRESSES_ARGS+=(
    "--peerAddresses" "$PEER1_ADDRESS"
    "--tlsRootCertFiles" "/tmp/hyperledger/$CHAINCODE_ORG/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem"
  )
fi;
if in_array "peer2" "${PEERS[@]}"; then
  PEER_ADDRESSES_ARGS=(
    "--peerAddresses" "$PEER2_ADDRESS"
    "--tlsRootCertFiles" "/tmp/hyperledger/$CHAINCODE_ORG/peer2/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem"
  )
fi;

echo "Committing"
peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --channelID "$CHANNEL_NAME" --name "$CHAINCODE_NAME" --version 1.0 --sequence 1 --tls --cafile "$CA_FILE" "${COLLECTIONS_CONFIG_ARG[@]}" "${PEER_ADDRESSES_ARGS[@]}"
echo "Checking commit"
peer lifecycle chaincode querycommitted --channelID ${CHANNEL_NAME} --name "${CHAINCODE_NAME}" --cafile "$CA_FILE"

echo "Cleaning"
rm "${CHAINCODE_PACKAGE_NAME}"
