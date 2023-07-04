#!/bin/bash
#
# Create and join channel script

# Genesis block
configtxgen -profile OrgsOrdererGenesis -outputBlock /tmp/hyperledger/org0/artifacts/genesis.block -channelID syschannel

# Channel 1 transactions and anchors
configtxgen -profile OrgsChannel1 -outputCreateChannelTx /tmp/hyperledger/org1/artifacts/channel.tx -channelID ch1
configtxgen -profile OrgsChannel1 -outputAnchorPeersUpdate /tmp/hyperledger/org1/artifacts/org1MSPanchors.tx -channelID ch1 -asOrg org1MSP

# Channel 2 transactions and anchors
configtxgen -profile OrgsChannel2 -outputCreateChannelTx /tmp/hyperledger/org2/artifacts/channel.tx -channelID ch2
configtxgen -profile OrgsChannel2 -outputAnchorPeersUpdate /tmp/hyperledger/org2/artifacts/org2MSPanchors.tx -channelID ch2 -asOrg org2MSP

# now this works...
#peer channel create -c ch1 -f /tmp/hyperledger/org1/artifacts/channel.tx -o localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --outputBlock /tmp/hyperledger/org1/peer1/assets/mychannel.block --tls --cafile /tmp/hyperledger/org1/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem