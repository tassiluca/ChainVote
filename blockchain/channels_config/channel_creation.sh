#!/bin/bash
#
# Script to create the channels and join peers to them.

export CORE_PEER_TLS_ENABLED=true
export FABRIC_CFG_PATH=$PWD/org1

# Org1
echo "Creating ch1 and join peer1-org1 and peer2-org1 to it"

# export CORE_PEER_LOCALMSPID="org1MSP"
# export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/org1/peer1/assets/tls-ca/tls-ca-cert.pem
# export CORE_PEER_MSPCONFIGPATH=/tmp/hyperledger/org1/admin/msp
# export CORE_PEER_ADDRESS=localhost:7051

peer channel create --channelID ch1 --file /tmp/hyperledger/org1/artifacts/channel.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --outputBlock /tmp/hyperledger/org1/peer1/assets/ch1.block --tls --cafile /tmp/hyperledger/org1/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
# join peer1-org1
peer channel join --blockpath /tmp/hyperledger/org1/peer1/assets/ch1.block
# join peer2-org1
CORE_PEER_ADDRESS=localhost:8051 peer channel join --blockpath /tmp/hyperledger/org1/peer1/assets/ch1.block
# update anchor peer
peer channel update --channelID ch1 --file /tmp/hyperledger/org1/artifacts/org1MSPanchors.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --tls --cafile /tmp/hyperledger/org1/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
echo "Checking if peer1-org1 and peer2-org1 have the same ledger"
peer channel getinfo --channelID ch1
CORE_PEER_ADDRESS=localhost:8051 peer channel getinfo --channelID ch1

sleep 3

# Org2
echo "Creating ch2 and join peer1-org2 and peer2-org2 to it"
export FABRIC_CFG_PATH=$PWD/org2

# export CORE_PEER_LOCALMSPID="org2MSP"
# export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/org2/peer1/assets/tls-ca/tls-ca-cert.pem
# export CORE_PEER_MSPCONFIGPATH=/tmp/hyperledger/org2/admin/msp
# export CORE_PEER_ADDRESS=localhost:9051
# export CORE_PEER_GOSSIP_BOOTSTRAP=localhost:9051

peer channel create --channelID ch2 --file /tmp/hyperledger/org2/artifacts/channel.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --outputBlock /tmp/hyperledger/org2/peer1/assets/ch2.block --tls --cafile /tmp/hyperledger/org2/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
# join peer1-org2
peer channel join --blockpath /tmp/hyperledger/org2/peer1/assets/ch2.block
# join peer2-org2
CORE_PEER_ADDRESS=localhost:10051 peer channel join --blockpath /tmp/hyperledger/org2/peer1/assets/ch2.block

#update anchor peer
peer channel update --channelID ch2 --file /tmp/hyperledger/org2/artifacts/org2MSPanchors.tx --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --tls --cafile /tmp/hyperledger/org2/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
echo "Checking if peer1-org2 and peer2-org2 have the same ledger"
peer channel getinfo --channelID ch2
CORE_PEER_ADDRESS=localhost:10051 peer channel getinfo --channelID ch2