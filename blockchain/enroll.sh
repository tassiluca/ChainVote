#!/bin/bash
#
# This script executes, for each organizion, the enrollment of each entity (network component and user) from the CAs.

set -e  # Exit immediately if some command (simple or compound) returns a non-zero status

#####################################################################################################################
# ORG0 entities enrollment
#####################################################################################################################
echo "Enroll Orderers"

# Copy certificates
mkdir -p $ARTIFACTS_DIR/org0/orderer1/assets/ca 
cp $ARTIFACTS_DIR/org0/ca/admin/msp/cacerts/0-0-0-0-7053.pem $ARTIFACTS_DIR/org0/orderer1/assets/ca/org0-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org0/orderer1/assets/tls-ca 
cp $ARTIFACTS_DIR/tls-ca/admin/msp/cacerts/0-0-0-0-7052.pem $ARTIFACTS_DIR/org0/orderer1/assets/tls-ca/tls-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org0/orderer2/assets/ca 
cp $ARTIFACTS_DIR/org0/ca/admin/msp/cacerts/0-0-0-0-7053.pem $ARTIFACTS_DIR/org0/orderer2/assets/ca/org0-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org0/orderer2/assets/tls-ca 
cp $ARTIFACTS_DIR/tls-ca/admin/msp/cacerts/0-0-0-0-7052.pem $ARTIFACTS_DIR/org0/orderer2/assets/tls-ca/tls-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org0/orderer3/assets/ca 
cp $ARTIFACTS_DIR/org0/ca/admin/msp/cacerts/0-0-0-0-7053.pem $ARTIFACTS_DIR/org0/orderer3/assets/ca/org0-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org0/orderer3/assets/tls-ca 
cp $ARTIFACTS_DIR/tls-ca/admin/msp/cacerts/0-0-0-0-7052.pem $ARTIFACTS_DIR/org0/orderer3/assets/tls-ca/tls-ca-cert.pem

# Identities enrollment
export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org0/orderer1
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org0/orderer1/assets/ca/org0-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://orderer1-org0:orderer1pw@0.0.0.0:7053
sleep 5

# TLS
export FABRIC_CA_CLIENT_MSPDIR=tls-msp
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org0/orderer1/assets/tls-ca/tls-ca-cert.pem

fabric-ca-client enroll -d -u https://orderer1-org0:orderer1PW@0.0.0.0:7052 --enrollment.profile tls --csr.hosts orderer1-org0 --csr.hosts localhost
sleep 5

cp $ARTIFACTS_DIR/org0/orderer1/tls-msp/keystore/*_sk $ARTIFACTS_DIR/org0/orderer1/tls-msp/keystore/key.pem

export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org0/orderer2
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org0/orderer2/assets/ca/org0-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://orderer2-org0:orderer2pw@0.0.0.0:7053
sleep 5

export FABRIC_CA_CLIENT_MSPDIR=tls-msp
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org0/orderer2/assets/tls-ca/tls-ca-cert.pem

fabric-ca-client enroll -d -u https://orderer2-org0:orderer2PW@0.0.0.0:7052 --enrollment.profile tls --csr.hosts orderer2-org0 --csr.hosts localhost
sleep 5

cp $ARTIFACTS_DIR/org0/orderer2/tls-msp/keystore/*_sk $ARTIFACTS_DIR/org0/orderer2/tls-msp/keystore/key.pem

export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org0/orderer3
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org0/orderer3/assets/ca/org0-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://orderer3-org0:orderer3pw@0.0.0.0:7053
sleep 5

export FABRIC_CA_CLIENT_MSPDIR=tls-msp
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org0/orderer3/assets/tls-ca/tls-ca-cert.pem

fabric-ca-client enroll -d -u https://orderer3-org0:orderer3PW@0.0.0.0:7052 --enrollment.profile tls --csr.hosts orderer3-org0 --csr.hosts localhost
sleep 5

cp $ARTIFACTS_DIR/org0/orderer3/tls-msp/keystore/*_sk $ARTIFACTS_DIR/org0/orderer3/tls-msp/keystore/key.pem

echo "Enroll Admin of ORG0"

export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org0/admin
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org0/orderer1/assets/ca/org0-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://admin-org0:org0adminpw@0.0.0.0:7053

mkdir -p $ARTIFACTS_DIR/org0/orderer1/msp/admincerts
cp $ARTIFACTS_DIR/org0/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org0/orderer1/msp/admincerts/orderer-admin-cert.pem

mkdir -p $ARTIFACTS_DIR/org0/orderer2/msp/admincerts
cp $ARTIFACTS_DIR/org0/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org0/orderer2/msp/admincerts/orderer-admin-cert.pem

mkdir -p $ARTIFACTS_DIR/org0/orderer3/msp/admincerts
cp $ARTIFACTS_DIR/org0/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org0/orderer3/msp/admincerts/orderer-admin-cert.pem

mkdir -p $ARTIFACTS_DIR/org0/msp/{admincerts,cacerts,tlscacerts,users}
cp $ARTIFACTS_DIR/org0/orderer1/assets/ca/org0-ca-cert.pem $ARTIFACTS_DIR/org0/msp/cacerts/
cp $ARTIFACTS_DIR/org0/orderer1/assets/tls-ca/tls-ca-cert.pem $ARTIFACTS_DIR/org0/msp/tlscacerts/
cp $ARTIFACTS_DIR/org0/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org0/msp/admincerts/admin-org0-cert.pem
cp ./orgs_config/org0.yaml $ARTIFACTS_DIR/org0/msp/config.yaml
echo "Org0 done"
sleep 5

#####################################################################################################################
# ORG1 entities enrollment
#####################################################################################################################
echo 
echo "Enroll Peer1 Org1"

# preparation
mkdir -p $ARTIFACTS_DIR/org1/peer1/assets/ca 
cp $ARTIFACTS_DIR/org1/ca/admin/msp/cacerts/0-0-0-0-7054.pem $ARTIFACTS_DIR/org1/peer1/assets/ca/org1-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org1/peer1/assets/tls-ca 
cp $ARTIFACTS_DIR/tls-ca/admin/msp/cacerts/0-0-0-0-7052.pem $ARTIFACTS_DIR/org1/peer1/assets/tls-ca/tls-ca-cert.pem

# for identity
export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org1/peer1
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org1/peer1/assets/ca/org1-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://peer1-org1:peer1PW@0.0.0.0:7054
sleep 5

# for TLS
export FABRIC_CA_CLIENT_MSPDIR=tls-msp
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org1/peer1/assets/tls-ca/tls-ca-cert.pem

fabric-ca-client enroll -d -u https://peer1-org1:peer1PW@0.0.0.0:7052 --enrollment.profile tls --csr.hosts peer1-org1 --csr.hosts localhost
sleep 5

cp $ARTIFACTS_DIR/org1/peer1/tls-msp/keystore/*_sk $ARTIFACTS_DIR/org1/peer1/tls-msp/keystore/key.pem

echo "Enroll Client Org1"

# preparation
mkdir -p $ARTIFACTS_DIR/org1/client/assets/ca 
cp $ARTIFACTS_DIR/org1/ca/admin/msp/cacerts/0-0-0-0-7054.pem $ARTIFACTS_DIR/org1/client/assets/ca/org1-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org1/client/assets/tls-ca 
cp $ARTIFACTS_DIR/tls-ca/admin/msp/cacerts/0-0-0-0-7052.pem $ARTIFACTS_DIR/org1/client/assets/tls-ca/tls-ca-cert.pem

# for identity
export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org1/client
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org1/client/assets/ca/org1-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://client-org1:clientPW@0.0.0.0:7054
sleep 5

# for TLS
export FABRIC_CA_CLIENT_MSPDIR=tls-msp
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org1/client/assets/tls-ca/tls-ca-cert.pem

fabric-ca-client enroll -d -u https://client-org1:clientPW@0.0.0.0:7052 --enrollment.profile tls --csr.hosts client-org1 --csr.hosts localhost
sleep 5

cp $ARTIFACTS_DIR/org1/client/tls-msp/keystore/*_sk $ARTIFACTS_DIR/org1/client/tls-msp/keystore/key.pem

echo "Enroll Admin Org1"

export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org1/admin
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org1/peer1/assets/ca/org1-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://admin-org1:org1AdminPW@0.0.0.0:7054

mkdir -p $ARTIFACTS_DIR/org1/peer1/msp/admincerts
cp $ARTIFACTS_DIR/org1/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org1/peer1/msp/admincerts/org1-admin-cert.pem

mkdir -p $ARTIFACTS_DIR/org1/admin/msp/admincerts
cp $ARTIFACTS_DIR/org1/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org1/admin/msp/admincerts/org1-admin-cert.pem

mkdir -p $ARTIFACTS_DIR/org1/msp/{admincerts,cacerts,tlscacerts,users}
cp $ARTIFACTS_DIR/org1/peer1/assets/ca/org1-ca-cert.pem $ARTIFACTS_DIR/org1/msp/cacerts/
cp $ARTIFACTS_DIR/org1/peer1/assets/tls-ca/tls-ca-cert.pem $ARTIFACTS_DIR/org1/msp/tlscacerts/
cp $ARTIFACTS_DIR/org1/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org1/msp/admincerts/admin-org1-cert.pem
cp ./orgs_config/org1.yaml $ARTIFACTS_DIR/org1/msp/config.yaml
echo "Org1 done"
sleep 5

#####################################################################################################################
# ORG2 entities enrollment
#####################################################################################################################
echo 
echo "Enroll Peer1 Org2"

# preparation
mkdir -p $ARTIFACTS_DIR/org2/peer1/assets/ca 
cp $ARTIFACTS_DIR/org2/ca/admin/msp/cacerts/0-0-0-0-7055.pem $ARTIFACTS_DIR/org2/peer1/assets/ca/org2-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org2/peer1/assets/tls-ca 
cp $ARTIFACTS_DIR/tls-ca/admin/msp/cacerts/0-0-0-0-7052.pem $ARTIFACTS_DIR/org2/peer1/assets/tls-ca/tls-ca-cert.pem

# for identity
export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org2/peer1
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org2/peer1/assets/ca/org2-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://peer1-org2:peer1PW@0.0.0.0:7055
sleep 5

# for TLS
export FABRIC_CA_CLIENT_MSPDIR=tls-msp
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org2/peer1/assets/tls-ca/tls-ca-cert.pem

fabric-ca-client enroll -d -u https://peer1-org2:peer1PW@0.0.0.0:7052 --enrollment.profile tls --csr.hosts peer1-org2 --csr.hosts localhost
sleep 5

cp $ARTIFACTS_DIR/org2/peer1/tls-msp/keystore/*_sk $ARTIFACTS_DIR/org2/peer1/tls-msp/keystore/key.pem

echo "Enroll Peer2 Org2"

# preparation
mkdir -p $ARTIFACTS_DIR/org2/peer2/assets/ca 
cp $ARTIFACTS_DIR/org2/ca/admin/msp/cacerts/0-0-0-0-7055.pem $ARTIFACTS_DIR/org2/peer2/assets/ca/org2-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org2/peer2/assets/tls-ca 
cp $ARTIFACTS_DIR/tls-ca/admin/msp/cacerts/0-0-0-0-7052.pem $ARTIFACTS_DIR/org2/peer2/assets/tls-ca/tls-ca-cert.pem

# for identity
export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org2/peer2
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org2/peer2/assets/ca/org2-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://peer2-org2:peer2PW@0.0.0.0:7055
sleep 5

# for TLS
export FABRIC_CA_CLIENT_MSPDIR=tls-msp
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org2/peer2/assets/tls-ca/tls-ca-cert.pem

fabric-ca-client enroll -d -u https://peer2-org2:peer2PW@0.0.0.0:7052 --enrollment.profile tls --csr.hosts peer2-org2 --csr.hosts localhost
sleep 5

cp $ARTIFACTS_DIR/org2/peer2/tls-msp/keystore/*_sk $ARTIFACTS_DIR/org2/peer2/tls-msp/keystore/key.pem

echo "Enroll Client Org2"

# preparation
mkdir -p $ARTIFACTS_DIR/org2/client/assets/ca 
cp $ARTIFACTS_DIR/org2/ca/admin/msp/cacerts/0-0-0-0-7055.pem $ARTIFACTS_DIR/org2/client/assets/ca/org2-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org2/client/assets/tls-ca 
cp $ARTIFACTS_DIR/tls-ca/admin/msp/cacerts/0-0-0-0-7052.pem $ARTIFACTS_DIR/org2/client/assets/tls-ca/tls-ca-cert.pem

# for identity
export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org2/client
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org2/client/assets/ca/org2-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://client-org2:clientPW@0.0.0.0:7055
sleep 5

# for TLS
export FABRIC_CA_CLIENT_MSPDIR=tls-msp
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org2/client/assets/tls-ca/tls-ca-cert.pem

fabric-ca-client enroll -d -u https://client-org2:clientPW@0.0.0.0:7052 --enrollment.profile tls --csr.hosts client-org1 --csr.hosts localhost
sleep 5

cp $ARTIFACTS_DIR/org1/client/tls-msp/keystore/*_sk $ARTIFACTS_DIR/org1/client/tls-msp/keystore/key.pem

echo "Enroll Admin Org2"

export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org2/admin
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org2/peer1/assets/ca/org2-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://admin-org2:org2AdminPW@0.0.0.0:7055

mkdir -p $ARTIFACTS_DIR/org2/peer1/msp/admincerts
cp $ARTIFACTS_DIR/org2/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org2/peer1/msp/admincerts/org2-admin-cert.pem

mkdir -p $ARTIFACTS_DIR/org2/peer2/msp/admincerts
cp $ARTIFACTS_DIR/org2/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org2/peer2/msp/admincerts/org2-admin-cert.pem

mkdir -p $ARTIFACTS_DIR/org2/admin/msp/admincerts
cp $ARTIFACTS_DIR/org2/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org2/admin/msp/admincerts/org2-admin-cert.pem

mkdir -p $ARTIFACTS_DIR/org2/msp/{admincerts,cacerts,tlscacerts,users}
cp $ARTIFACTS_DIR/org2/peer1/assets/ca/org2-ca-cert.pem $ARTIFACTS_DIR/org2/msp/cacerts/
cp $ARTIFACTS_DIR/org2/peer1/assets/tls-ca/tls-ca-cert.pem $ARTIFACTS_DIR/org2/msp/tlscacerts/
cp $ARTIFACTS_DIR/org2/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org2/msp/admincerts/admin-org2-cert.pem
cp ./orgs_config/org2.yaml $ARTIFACTS_DIR/org2/msp/config.yaml
echo "Org2 done"

#####################################################################################################################
# Enroll entities for ORG3
#####################################################################################################################
echo 
echo "Enroll Peer1 Org3"

# preparation
mkdir -p $ARTIFACTS_DIR/org3/peer1/assets/ca 
cp $ARTIFACTS_DIR/org3/ca/admin/msp/cacerts/0-0-0-0-7056.pem $ARTIFACTS_DIR/org3/peer1/assets/ca/org3-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org3/peer1/assets/tls-ca 
cp $ARTIFACTS_DIR/tls-ca/admin/msp/cacerts/0-0-0-0-7052.pem $ARTIFACTS_DIR/org3/peer1/assets/tls-ca/tls-ca-cert.pem

# for identity
export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org3/peer1
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org3/peer1/assets/ca/org3-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://peer1-org3:peer1PW@0.0.0.0:7056
sleep 5

# for TLS
export FABRIC_CA_CLIENT_MSPDIR=tls-msp
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org3/peer1/assets/tls-ca/tls-ca-cert.pem

fabric-ca-client enroll -d -u https://peer1-org3:peer1PW@0.0.0.0:7052 --enrollment.profile tls --csr.hosts peer1-org3 --csr.hosts localhost
sleep 5

cp $ARTIFACTS_DIR/org3/peer1/tls-msp/keystore/*_sk $ARTIFACTS_DIR/org3/peer1/tls-msp/keystore/key.pem

echo "Enroll Peer2 Org3"

# preparation
mkdir -p $ARTIFACTS_DIR/org3/peer2/assets/ca 
cp $ARTIFACTS_DIR/org3/ca/admin/msp/cacerts/0-0-0-0-7056.pem $ARTIFACTS_DIR/org3/peer2/assets/ca/org3-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org3/peer2/assets/tls-ca 
cp $ARTIFACTS_DIR/tls-ca/admin/msp/cacerts/0-0-0-0-7052.pem $ARTIFACTS_DIR/org3/peer2/assets/tls-ca/tls-ca-cert.pem

# for identity
export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org3/peer2
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org3/peer2/assets/ca/org3-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://peer2-org3:peer2PW@0.0.0.0:7056
sleep 5

# for TLS
export FABRIC_CA_CLIENT_MSPDIR=tls-msp
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org3/peer2/assets/tls-ca/tls-ca-cert.pem

fabric-ca-client enroll -d -u https://peer2-org3:peer2PW@0.0.0.0:7052 --enrollment.profile tls --csr.hosts peer2-org3 --csr.hosts localhost
sleep 5

cp $ARTIFACTS_DIR/org3/peer2/tls-msp/keystore/*_sk $ARTIFACTS_DIR/org3/peer2/tls-msp/keystore/key.pem

echo "Enroll Client Org3"

# preparation
mkdir -p $ARTIFACTS_DIR/org3/client/assets/ca 
cp $ARTIFACTS_DIR/org3/ca/admin/msp/cacerts/0-0-0-0-7056.pem $ARTIFACTS_DIR/org3/client/assets/ca/org3-ca-cert.pem

mkdir -p $ARTIFACTS_DIR/org3/client/assets/tls-ca 
cp $ARTIFACTS_DIR/tls-ca/admin/msp/cacerts/0-0-0-0-7052.pem $ARTIFACTS_DIR/org3/client/assets/tls-ca/tls-ca-cert.pem

# for identity
export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org3/client
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org3/client/assets/ca/org3-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://client-org3:clientPW@0.0.0.0:7056
sleep 5

# for TLS
export FABRIC_CA_CLIENT_MSPDIR=tls-msp
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org3/client/assets/tls-ca/tls-ca-cert.pem

fabric-ca-client enroll -d -u https://client-org3:clientPW@0.0.0.0:7052 --enrollment.profile tls --csr.hosts client-org1 --csr.hosts localhost
sleep 5

cp $ARTIFACTS_DIR/org1/client/tls-msp/keystore/*_sk $ARTIFACTS_DIR/org1/client/tls-msp/keystore/key.pem

echo "Enroll Admin Org3"

export FABRIC_CA_CLIENT_HOME=$ARTIFACTS_DIR/org3/admin
export FABRIC_CA_CLIENT_TLS_CERTFILES=$ARTIFACTS_DIR/org3/peer1/assets/ca/org3-ca-cert.pem
export FABRIC_CA_CLIENT_MSPDIR=msp

fabric-ca-client enroll -d -u https://admin-org3:org3AdminPW@0.0.0.0:7056

mkdir -p $ARTIFACTS_DIR/org3/peer1/msp/admincerts
cp $ARTIFACTS_DIR/org3/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org3/peer1/msp/admincerts/org3-admin-cert.pem

mkdir -p $ARTIFACTS_DIR/org3/peer2/msp/admincerts
cp $ARTIFACTS_DIR/org3/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org3/peer2/msp/admincerts/org3-admin-cert.pem

mkdir -p $ARTIFACTS_DIR/org3/admin/msp/admincerts
cp $ARTIFACTS_DIR/org3/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org3/admin/msp/admincerts/org3-admin-cert.pem

mkdir -p $ARTIFACTS_DIR/org3/msp/{admincerts,cacerts,tlscacerts,users}
cp $ARTIFACTS_DIR/org3/peer1/assets/ca/org3-ca-cert.pem $ARTIFACTS_DIR/org3/msp/cacerts/
cp $ARTIFACTS_DIR/org3/peer1/assets/tls-ca/tls-ca-cert.pem $ARTIFACTS_DIR/org3/msp/tlscacerts/
cp $ARTIFACTS_DIR/org3/admin/msp/signcerts/cert.pem $ARTIFACTS_DIR/org3/msp/admincerts/admin-org3-cert.pem
cp ./orgs_config/org3.yaml $ARTIFACTS_DIR/org3/msp/config.yaml
echo "org3 done"
