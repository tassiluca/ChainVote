#!/bin/bash
#
# Script to create the channels and join peers to them.

set -e  # Exit immediately if some command (simple or compound) returns a non-zero status

export CORE_PEER_TLS_ENABLED=true
export FABRIC_CFG_PATH=$PWD/generated

# Org1
export CORE_PEER_ADDRESS=localhost:7051
export CORE_PEER_LOCALMSPID="org1MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=$ARTIFACTS_DIR/org1/peer1/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_MSPCONFIGPATH=$ARTIFACTS_DIR/org1/admin/msp

echo "Creating ch1 and join peer1-org1 and peer2-org1 to it"
peer channel create --channelID ch1 --file $ARTIFACTS_DIR/org1/artifacts/channel.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --outputBlock $ARTIFACTS_DIR/org1/peer1/assets/ch1.block --tls --cafile $ARTIFACTS_DIR/org1/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
# join peer1-org1 to ch1
peer channel join --blockpath $ARTIFACTS_DIR/org1/peer1/assets/ch1.block
# update anchor peer
peer channel update --channelID ch1 --file $ARTIFACTS_DIR/org1/artifacts/org1MSPanchors.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --tls --cafile $ARTIFACTS_DIR/org1/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
echo "Checking peer1-org1 ledger infos"
peer channel getinfo --channelID ch1

sleep 3

# Org2
export CORE_PEER_ADDRESS=localhost:9051
export CORE_PEER_LOCALMSPID="org2MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=$ARTIFACTS_DIR/org2/peer1/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_MSPCONFIGPATH=$ARTIFACTS_DIR/org2/admin/msp

echo "Creating ch2 and join peer1-org2 and peer2-org2 to ch1+ch2"
cp $ARTIFACTS_DIR/org1/peer1/assets/ch1.block $ARTIFACTS_DIR/org2/peer1/assets/
peer channel create --channelID ch2 --file $ARTIFACTS_DIR/org2/artifacts/channel.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --outputBlock $ARTIFACTS_DIR/org2/peer1/assets/ch2.block --tls --cafile $ARTIFACTS_DIR/org2/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
# join peer1-org2 to both ch1 and ch2
peer channel join --blockpath $ARTIFACTS_DIR/org2/peer1/assets/ch2.block
peer channel join --blockpath $ARTIFACTS_DIR/org2/peer1/assets/ch1.block
# join peer2-org2 to both ch1 and ch2
CORE_PEER_ADDRESS=localhost:10051 peer channel join --blockpath $ARTIFACTS_DIR/org2/peer1/assets/ch2.block
CORE_PEER_ADDRESS=localhost:10051 peer channel join --blockpath $ARTIFACTS_DIR/org2/peer1/assets/ch1.block
# update anchor peer
peer channel update --channelID ch2 --file $ARTIFACTS_DIR/org2/artifacts/org2MSPanchors.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --tls --cafile $ARTIFACTS_DIR/org2/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
echo "Checking peer1-org2 and peer2-org2 ledger infos"
peer channel getinfo --channelID ch2
CORE_PEER_ADDRESS=localhost:10051 peer channel getinfo --channelID ch2

sleep 3

# Org3
export CORE_PEER_ADDRESS=localhost:20051
export CORE_PEER_LOCALMSPID="org3MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=$ARTIFACTS_DIR/org3/peer1/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_MSPCONFIGPATH=$ARTIFACTS_DIR/org3/admin/msp

cp $ARTIFACTS_DIR/org1/peer1/assets/ch1.block $ARTIFACTS_DIR/org3/peer1/assets/
cp $ARTIFACTS_DIR/org2/peer1/assets/ch2.block $ARTIFACTS_DIR/org3/peer1/assets/
peer channel join --blockpath $ARTIFACTS_DIR/org3/peer1/assets/ch1.block
peer channel join --blockpath $ARTIFACTS_DIR/org3/peer1/assets/ch2.block
CORE_PEER_ADDRESS=localhost:30051 peer channel join --blockpath $ARTIFACTS_DIR/org3/peer1/assets/ch1.block
CORE_PEER_ADDRESS=localhost:30051 peer channel join --blockpath $ARTIFACTS_DIR/org3/peer1/assets/ch2.block
