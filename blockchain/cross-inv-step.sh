
export PATH="$PATH:$PWD/bin"
export FABRIC_CFG_PATH=$PWD/channels_config/org1

peer lifecycle chaincode package chaincode-org1.tar.gz --path ../chaincode-org1/build/install/chaincode-org1 --lang java --label chaincode-org1.tar.gz_1.0

export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="org1MSP"
export CORE_PEER_MSPCONFIGPATH=/tmp/hyperledger/org1/admin/msp

export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/org1/peer1/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_ADDRESS=localhost:7051

peer lifecycle chaincode install chaincode-org1.tar.gz

export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/org1/peer2/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_ADDRESS=localhost:8051

peer lifecycle chaincode install chaincode-org1.tar.gz

export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="org2MSP"
export CORE_PEER_MSPCONFIGPATH=/tmp/hyperledger/org2/admin/msp

export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/org2/peer1/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_ADDRESS=localhost:9051
peer lifecycle chaincode install chaincode-org1.tar.gz

export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/org2/peer2/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_ADDRESS=localhost:10051
peer lifecycle chaincode install chaincode-org1.tar.gz
PACKAGE_ID=chaincode-org1.tar.gz_1.0:<something>
CA_FILE=/tmp/hyperledger/org2/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem

export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="org1MSP"
export CORE_PEER_MSPCONFIGPATH=/tmp/hyperledger/org1/admin/msp
export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/org1/peer1/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_ADDRESS=localhost:7051

peer lifecycle chaincode approveformyorg --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --name chaincode-org1 --channelID ch1 --version "1.0" --package-id "$PACKAGE_ID" --sequence "1" --cafile "$CA_FILE" --tls

export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="org2MSP"
export CORE_PEER_MSPCONFIGPATH=/tmp/hyperledger/org2/admin/msp
export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/org2/peer1/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_ADDRESS=localhost:9051

peer lifecycle chaincode approveformyorg --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --name chaincode-org1 --channelID ch2 --version "1.0" --package-id "$PACKAGE_ID" --sequence "1" --cafile "$CA_FILE" --tls
peer lifecycle chaincode approveformyorg --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --name chaincode-org1 --channelID ch1 --version "1.0" --package-id "$PACKAGE_ID" --sequence "1" --cafile "$CA_FILE" --tls
peer lifecycle chaincode checkcommitreadiness --channelID ch1 --name chaincode-org1 --version 1.0 --sequence 1 --tls --cafile "$CA_FILE" --output json


export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="org1MSP"
export CORE_PEER_MSPCONFIGPATH=/tmp/hyperledger/org1/admin/msp
export CORE_PEER_TLS_ROOTCERT_FILE=/tmp/hyperledger/org1/peer1/assets/tls-ca/tls-ca-cert.pem
export CORE_PEER_ADDRESS=localhost:7051

PEER_ADDRESSES_ARGS=()
PEER_ADDRESSES_ARGS+=(
    "--peerAddresses" "localhost:7051"
    "--tlsRootCertFiles" "/tmp/hyperledger/org1/peer1/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem"
)
PEER_ADDRESSES_ARGS+=(
    "--peerAddresses" "localhost:8051"
    "--tlsRootCertFiles" "/tmp/hyperledger/org1/peer2/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem"
)

peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --channelID ch1 --name chaincode-org1 --version 1.0 --sequence 1 --tls --cafile "$CA_FILE" "${PEER_ADDRESSES_ARGS[@]}"