#!/bin/bash
#
# Script to create channel artifacts.

set -e  # Exit immediately if some command (simple or compound) returns a non-zero status

# Substitute ${ARTIFACTS_DIR} placeholder in configtx.yaml
if [[ ! -d generated ]]; then 
    mkdir generated 
fi;
cp -f ./core.yaml ./generated/core.yaml
cp -f ./orderer.yaml ./generated/orderer.yaml
cp -f ./configtx.yaml ./generated/configtx.yaml
sed -i '' 's#$(ARTIFACTS_DIR)#'$ARTIFACTS_DIR'#g' ./generated/configtx.yaml

export FABRIC_CFG_PATH=$PWD/generated

# Genesis block
configtxgen -profile OrgsOrdererGenesis -outputBlock $ARTIFACTS_DIR/org0/artifacts/genesis.block -channelID syschannel

# Channel 1 transactions and anchors
configtxgen -profile OrgsChannel1 -outputCreateChannelTx $ARTIFACTS_DIR/org1/artifacts/channel.tx -channelID ch1
configtxgen -profile OrgsChannel1 -outputAnchorPeersUpdate $ARTIFACTS_DIR/org1/artifacts/org1MSPanchors.tx -channelID ch1 -asOrg org1MSP

# Channel 2 transactions and anchors
configtxgen -profile OrgsChannel2 -outputCreateChannelTx $ARTIFACTS_DIR/org2/artifacts/channel.tx -channelID ch2
configtxgen -profile OrgsChannel2 -outputAnchorPeersUpdate $ARTIFACTS_DIR/org2/artifacts/org2MSPanchors.tx -channelID ch2 -asOrg org2MSP
