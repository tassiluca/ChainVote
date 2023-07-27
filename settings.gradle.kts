plugins {
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.1.9"
}

gitHooks {
    preCommit {
        tasks("check")
    }
    createHooks()
}

rootProject.name = "chain-vote"

include(
    "core",
    "presentation",
    "chaincode",
)
