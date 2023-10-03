#!/bin/bash
#
# Script to create the channels and join peers to them.

set -e  # Exit immediately if some command (simple or compound) returns a non-zero status

export CORE_PEER_TLS_ENABLED=true
export FABRIC_CFG_PATH=$PWD/org1

# Org1
echo "Creating ch1 and join peer1-org1 and peer2-org1 to it"
peer channel create --channelID ch1 --file /tmp/hyperledger/org1/artifacts/channel.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --outputBlock /tmp/hyperledger/org1/peer1/assets/ch1.block --tls --cafile /tmp/hyperledger/org1/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
# join peer1-org1 to ch1
peer channel join --blockpath /tmp/hyperledger/org1/peer1/assets/ch1.block
# join peer2-org1 to ch1
CORE_PEER_ADDRESS=localhost:8051 peer channel join --blockpath /tmp/hyperledger/org1/peer1/assets/ch1.block
# update anchor peer
peer channel update --channelID ch1 --file /tmp/hyperledger/org1/artifacts/org1MSPanchors.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --tls --cafile /tmp/hyperledger/org1/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
echo "Checking if peer1-org1 and peer2-org1 have the same ledger"
peer channel getinfo --channelID ch1
CORE_PEER_ADDRESS=localhost:8051 peer channel getinfo --channelID ch1

sleep 3

# Org2
export FABRIC_CFG_PATH=$PWD/org2

echo "Creating ch2 and join peer1-org2 and peer2-org2 to ch1+ch2"
cp /tmp/hyperledger/org1/peer1/assets/ch1.block /tmp/hyperledger/org2/peer1/assets/
peer channel create --channelID ch2 --file /tmp/hyperledger/org2/artifacts/channel.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --outputBlock /tmp/hyperledger/org2/peer1/assets/ch2.block --tls --cafile /tmp/hyperledger/org2/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
# join peer1-org2 to both ch1 and ch2
peer channel join --blockpath /tmp/hyperledger/org2/peer1/assets/ch2.block
peer channel join --blockpath /tmp/hyperledger/org2/peer1/assets/ch1.block
# join peer2-org2 to both ch1 and ch2
CORE_PEER_ADDRESS=localhost:10051 peer channel join --blockpath /tmp/hyperledger/org2/peer1/assets/ch2.block
CORE_PEER_ADDRESS=localhost:10051 peer channel join --blockpath /tmp/hyperledger/org2/peer1/assets/ch1.block
# update anchor peer
peer channel update --channelID ch2 --file /tmp/hyperledger/org2/artifacts/org2MSPanchors.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --tls --cafile /tmp/hyperledger/org2/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
echo "Checking if peer1-org2 and peer2-org2 have the same ledger"
peer channel getinfo --channelID ch2

CORE_PEER_ADDRESS=localhost:10051 peer channel getinfo --channelID ch2
