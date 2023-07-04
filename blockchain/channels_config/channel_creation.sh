#!/bin/bash

# Org1
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="org1MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/org1/peer1/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_MSPCONFIGPATH=/tmp/hyperledger/org1/admin/msp
export CORE_PEER_ADDRESS=localhost:7051

export FABRIC_CFG_PATH=$PWD

peer channel create -c ch1 -f /tmp/hyperledger/org1/artifacts/channel.tx -o localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --outputBlock /tmp/hyperledger/org1/peer1/assets/ch1.block --tls --cafile /tmp/hyperledger/org1/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
peer channel join -b /tmp/hyperledger/org1/peer1/assets/ch1.block
CORE_PEER_ADDRESS=localhost:8051 peer channel join -b /tmp/hyperledger/org1/peer1/assets/ch1.block

# anchor peer update
peer channel update -c ch1 -f /tmp/hyperledger/org1/artifacts/org1MSPanchors.tx -o localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --tls --cafile /tmp/hyperledger/org1/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem

echo "Checking if peers have the same ledger"
peer channel getinfo -c ch1
CORE_PEER_ADDRESS=localhost:8051 peer channel getinfo -c ch1

sleep 3

# Org2
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="org2MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/org2/peer1/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_MSPCONFIGPATH=/tmp/hyperledger/org2/admin/msp
export CORE_PEER_ADDRESS=localhost:9051

export FABRIC_CFG_PATH=$PWD

peer channel create -c ch2 -f /tmp/hyperledger/org2/artifacts/channel.tx -o localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --outputBlock /tmp/hyperledger/org2/peer1/assets/ch2.block --tls --cafile /tmp/hyperledger/org2/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem
peer channel join -b /tmp/hyperledger/org2/peer1/assets/ch2.block
CORE_PEER_ADDRESS=localhost:10051 peer channel join -b /tmp/hyperledger/org2/peer1/assets/ch2.block

# anchor peer update
peer channel update -c ch2 -f /tmp/hyperledger/org2/artifacts/org2MSPanchors.tx -o localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --tls --cafile /tmp/hyperledger/org2/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem

echo "Checking if peers have the same ledger"
peer channel getinfo -c ch2
CORE_PEER_ADDRESS=localhost:10051 peer channel getinfo -c ch2