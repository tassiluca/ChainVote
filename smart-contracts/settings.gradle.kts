plugins {
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.1.9"
}

rootProject.name = "chain-vote"

include(
    "core",
    "presentation",
    "chaincode",
    "chaincode-org2",
    "chaincode-org1"
)
