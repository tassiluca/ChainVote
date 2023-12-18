+++
title = "ChainVote Presentation"
outputs = ["Reveal"]
+++

{{< slide id="hello" background="" transition="zoom" transition-speed="fast" >}}

# `ChainVote`

## Project for Distributed Systems Course

### ISI @ UniBo

Authors: [Luca Tassinari](https://github.com/tassiLuca), [Giovanni Antonioni](https://github.com/giovaz94), [Luca Rubboli](https://github.com/Luca1313)

---

## Outline

1. Project's Goals
2. Use cases
3. Non-functional properties
4. Technologies
5. System Architecture
6. Blockchain Architecture
7. Video Demo

---

### Project's Goals

- **Small-scale** distributed electronic voting system
- _Blockchain technology_
- Uniform API to interact with the system by means of a web application

<!--
The project consists of the implementation of a **small-scale** distributed electronic voting system based on _blockchain technology_. Specifically, the goal is to create a distributed architecture that exposes a uniform API allowing users to interact with the system using a web application
-->
---

{{% multicol %}}
{{% col %}}

### Use cases

{{% /col %}}{{% col %}}

{{< figure src="./use-cases.svg" width="75%" >}}

{{% /col %}}
{{% /multicol %}}

---

### Non-functional properties (I)

**Thanks to _blockchain technology_ we achieve:**

- Operational integrity of the system:
  - Correctness of the operations performed by the system
- Accountability:
  - Node Peers logs the actions performed
- Confidentiality:
  - We used a **permissioned blockchain** that restrict access to data;
- Verifiability
  - Blockchain keep **tracks of all the transactions** executed to obtain final results;
- Availability
  - System will continue to serve requests even if some peers are down;

---

### Non-functional properties (II)

**We also designed the system to achieve:**

- Non-coercibility:
  - **Voters are not identifiable** during the voting process $\Rightarrow$ one time codes
- Voter Authentication:
  - Voters authentication provided by the API layer

---

### Technologies

- [Hyperledger Fabric](https://github.com/hyperledger/fabric) as the blockchain infrastructure technology
  - modular and highly-configurable
    - _Java_ as a language for smart contracts
  - offers mechanisms raising the bar of confidentiality and higher fine-grained control over ledger access (e.g. channels and private data)
- _Docker_ for deployment
- ReSTful API
  - _Nodejs + Express_ for the API layer

---

### System Architecture

{{< figure src="./system-architecture.svg" width="90%" >}}

---

### Blockchain network

![network architecture](network-architecture.svg)

---

### Demo

---

{{< slide background-color="#000" >}}

<div class="stretch">
    <iframe name="report" width="150%" height="100%" allow="fullscreen;"
        src="https://www.youtube.com/embed/yN0lqKVyrPA?autoplay=1&mute=1">
    </iframe>
</div>

---

### That's all, thank you for your attention!

### 😊
