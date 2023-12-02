<div align="center">

**Distributed Systems Final Project**

```
 ██████╗██╗  ██╗ █████╗ ██╗███╗   ██╗██╗   ██╗ ██████╗ ████████╗███████╗
██╔════╝██║  ██║██╔══██╗██║████╗  ██║██║   ██║██╔═══██╗╚══██╔══╝██╔════╝
██║     ███████║███████║██║██╔██╗ ██║██║   ██║██║   ██║   ██║   █████╗  
██║     ██╔══██║██╔══██║██║██║╚██╗██║╚██╗ ██╔╝██║   ██║   ██║   ██╔══╝  
╚██████╗██║  ██║██║  ██║██║██║ ╚████║ ╚████╔╝ ╚██████╔╝   ██║   ███████╗
 ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝  ╚═══╝   ╚═════╝    ╚═╝   ╚══════╝
```

</div>

## Requirements

- Unix system (either Linux or MacOS)
- Docker
  - on Mac OS make sure the file sharing implementation for the container is set to `osfx (Legacy)` (Settings -> General)
- Java 11 or higher
- `npm`

## Startup

To bring up all services:

```bash
./services up
```
The first time it will pull Hyperledger Fabric binaries and docker images to create blockchain artifacts.
Therefore, it will take a while ($\approx$ 10-15 minutes, depending on your system) to bring up the entire system.

:warning: `TODO`: sudo ...

To shutdown the services without cleaning the blockchain artifacts (it will speed up the bring up of the network next times):
```bash
./services down
```

To remove blockchain artifacts:

```bash
./services clean
```

## Documentation

- [Here](https://github.com/tassiLuca/ds-project-antonioni-rubboli-tassinari-ay2223/releases/latest) you can find the last release of the report.

- **Smart Contracts** Javadoc
  - [`chaincode-elections`](https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/chaincode-elections/)
  - [`chaincode-votes`](https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/chaincode-votes/)

- **RESTful API**
  - :warning: `TODO`

## Troubleshooting

:warning: `TODO`

## Authors

- Luca Tassinari
- Luca Rubboli
- Giovanni Antonioni
