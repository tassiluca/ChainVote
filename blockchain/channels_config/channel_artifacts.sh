#!/bin/bash

# Change to your path
BIN_PATH=~/fabric-samples/bin
OUTPUT_PATH=/tmp/hyperledger


# Genesis block
$BIN_PATH/configtxgen -profile OrgsOrdererGenesis -outputBlock $OUTPUT_PATH/org0/artifacts/genesis.block -channelID syschannel

# Channel 1 transactions and anchors
$BIN_PATH/configtxgen -profile OrgsChannel1 -outputCreateChannelTx  $OUTPUT_PATH/org1/artifacts/channel.tx -channelID ch1
$BIN_PATH/configtxgen -profile OrgsChannel1 -outputAnchorPeersUpdate $OUTPUT_PATH/org1/artifacts/org1MSPanchors.tx -channelID ch1 -asOrg org1MSP

# Channel 2 transactions and anchors
$BIN_PATH/configtxgen -profile OrgsChannel2 -outputCreateChannelTx  $OUTPUT_PATH/org2/artifacts/channel.tx -channelID ch2
$BIN_PATH/configtxgen -profile OrgsChannel2 -outputAnchorPeersUpdate $OUTPUT_PATH/org2/artifacts/org2MSPanchors.tx -channelID ch2 -asOrg org2MSP

