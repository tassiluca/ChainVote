<div align="center">

**Distributed Systems + Applications and Web Services Final Project @ UniBo**

<img src="./frontend/src/assets/logo.svg" width="180px" />

[Abstract](#abstract) | [Requirements](#requirements) | [Startup](#startup) | [Docs](#documentation) | [Develop](#develop) | [Troubleshooting](#troubleshooting) | [Authors](#authors)

</div>

## Abstract

> Electronic voting systems based on blockchain technology have emerged
as a potential solution to enhance the security and transparency of traditional voting methods. In this system, voters cast their votes electronically,
and the results are stored on a decentralized blockchain ledger, which ensures the integrity of the vote by preventing any tampering or manipulation.
This system provides a transparent and immutable record of votes, which
can be accessed by anyone in the network, thus increasing trust in the electoral process. Nevertheless, the use of blockchain technology in electronic
voting systems holds promise for creating a more secure, transparent, and
democratic electoral process.
> 
> The project consists of the implementation of a small-scale distributed electronic voting system based on blockchain technology. Specifically, the goal is to create a **distributed architecture** that exposes a **uniform API** allowing users to interact with the system using a web application.

**Warning:** This repository is part of an academic project developed for the course in Distributed Systems and Application and Web Services @ UniBo. It is intended for educational purposes and may not be suitable for production use.

## Requirements

- Unix system (either Linux or MacOS)
- Docker
  - on Mac OS make sure the file sharing implementation for the container is set to `osfx (Legacy)` (in `Settings -> General`)
- Java 11 or higher
- Node.js 18 or higher
- `npm`

## Startup

---

**Important**

For security reasons the password for using the mailer is not stored in the repository, but it is passed to the container using a docker secret. Before starting up the system, it’s necessary to create a password file, at the position defined in the secrets section of the `docker-compose.yaml` file:

```yaml
secrets:
  google_api_secret:
    file: ~/secrets/api-pass.txt
```

It is also possible to specify the mail address that will be used to send the emails by changing the `MAIL ADDRESS` environment variable on the `api-server` service:

```yaml
environment:
  ...
  - MAIL_USER=chainvote.01@gmail.com
  ...
```

---

To bring up the blockchain network, deploy the smart contracts on top of the peers, start the API services and the frontend web app you can use the `services.sh` script on the root of the project.

To bring up all the system's services:

```bash
./services up
```

Since the first time it will pull Hyperledger Fabric binaries and docker images to create blockchain artifacts and start API services, it will take a while to bring up the entire system.

---

While it reaches the API server creation phase, the script will execute the `npm login` command. 
We've preconfigured some default credentials for this purpose; these should be used when login request is prompted:

- username: `user`
- password: `password`

During the startup phase of the API layer, the script will require the sudo privileges in order to ensure that the `verdaccio`, `cache` and `dbdata` folders have write and read permissions, which are needed on Linux environment.

---

The full working system consists of 33 containers: one for each peer and chaincode deployed on it, five containers for the API services and one for the frontend web app.

To bring them down without cleaning the blockchain artifacts (it will speed up the creation of the network next times):

```bash
./services down
```

To clean network artifacts:

```
./services clean
```

For more details have a look at the project documentation.

## Documentation

- [Here](https://github.com/tassiLuca/ds-project-antonioni-rubboli-tassinari-ay2223/releases/latest) you can find the last release of the Distributed Systems report.
- [Here](https://tassiluca.github.io/ChainVote/presentation/) is the presentation for the Distributed System exam.

- **Smart Contracts** Javadoc
  - [`chaincode-elections`](https://tassiluca.github.io/ChainVote/smart-contracts/javadoc/chaincode-elections/)
  - [`chaincode-votes`](https://tassiluca.github.io/ChainVote/smart-contracts/javadoc/chaincode-votes/)

- **RESTful API**
  - [`api-server`](https://tassiluca.github.io/ChainVote/swagger-ui-api/)
  - [`auth-server`](https://tassiluca.github.io/ChainVote/swagger-ui-auth/)

## Develop

To clone the project:

```bash
git clone --recurse-submodules <URL> <DESTINATION>
```

If the repository has been cloned plainly, then submodules can be initialized manually:

```bash
git submodule update --init --recursive
```

## Troubleshooting

If you are encountering issues or observing unexpected behavior during the deployment, this troubleshooting guide is designed to assist you in identifying and resolving common problems.

- :exclamation: **Linux/MacOS: `[common.tools.configtxgen] main -> Error on outputBlock: could not create bootstrapper: could not create channel group ...: open /Projects/distributed-systems/ds-project/chain-vote/blockchain/../.chainvote/blockchain/org0/orderer1/tls-msp/signcerts/cert.pem: no such file or directory`**

  A similar error occurs the next time a problem arises during the creation of channel artifacts because it tries to get some files inside the artifacts folder but, since the previous deployment failed, those files don't exist. Cleaning the artifacts with `./services clean` should solve the problem.

- :exclamation: **MacOS: resource management**

  On MacOS systems make sure to increase allocated resources in Docker Desktop, particularly for CPU and memory limit (8GBs are enough).

- :exclamation: **MacOS: `Error response from daemon: cannot stop container: <container-id>: tried to kill container, but did not receive an exit event`**
  
  Sometimes happens that, when the services are torn down (with `./services down` or `./services clean`), one Hyperledger CA container remains hanging. Restarting Docker Desktop solves this issue.

- :exclamation: **MacOS: `container peer1-org2 is unhealthy`**

  If, during the deployment of the network, the shown message (or a similar one) appears on a MacOS host, make sure the file-sharing implementation for the containers in `Settings -> General` is set to osxfs (Legacy).

- :exclamation: **MacOS/Linux: General build failure**
  
  While the system is starting up docker will cache lots of data that is used in the various building phases. This could lead to a general build failure in future runs. Is possible to try these solutions for solving the problems:
  - `docker builder clean` to clean the docker cache;
  - Delete dangling images and volumes;
  - Restart docker engine;

## Authors

- Luca Tassinari
- Luca Rubboli
- Giovanni Antonioni
