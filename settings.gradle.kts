/*
plugins {
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.1.9"
}

gitHooks {
    if (System.getenv("CI_ENVIRONMENT") != "gitlab-ci") {
        preCommit {
            tasks("check")
        }
        createHooks()
    }
}
*/

rootProject.name = "chain-vote"

include(
    "core",
    "presentation",
    "chaincode-org2",
    "chaincode-org1"
)
