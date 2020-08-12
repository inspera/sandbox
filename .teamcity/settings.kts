import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.1"

project {

    vcsRoot(SandboxPr)
    vcsRoot(Sandbox1)

    buildType(Analytics)
    buildType(Build)
    buildType(Pr)
    buildType(Dataflow)
}

object Analytics : BuildType({
    name = "analytics"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        gradle {
            tasks = "tasks"
        }
    }

    triggers {
        vcs {
            triggerRules = "+:analytics/**"
            branchFilter = ""
        }
    }
})

object Build : BuildType({
    name = "master"

    vcs {
        root(DslContext.settingsRoot)

        branchFilter = ""
    }

    steps {
        gradle {
            tasks = "clean build"
            buildFile = ""
            gradleWrapperPath = ""
        }
    }

    triggers {
        vcs {
            branchFilter = ""
        }
    }
})

object Dataflow : BuildType({
    name = "dataflow"

    vcs {
        root(Sandbox1)
    }

    steps {
        script {
            scriptContent = """
                tag=`echo %vcsroot.branch% | sed "s/\//_/"`
                echo ${'$'}tag
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            triggerRules = "+:*"
            branchFilter = ""
        }
    }
})

object Pr : BuildType({
    name = "PR"

    vcs {
        root(SandboxPr)
    }

    steps {
        gradle {
            tasks = "clean ass"
            buildFile = ""
        }
    }

    triggers {
        vcs {
            branchFilter = """
                +:*
                -:<default>
            """.trimIndent()
        }
    }

    features {
        commitStatusPublisher {
            vcsRootExtId = "${SandboxPr.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:011b7cac-9d61-46ea-8d68-69db4f22b09c"
                }
            }
        }
    }
})

object Sandbox1 : GitVcsRoot({
    name = "sandbox (1)"
    url = "git@github.com:inspera/sandbox.git"
    branchSpec = "+:refs/heads/*"
    authMethod = uploadedKey {
        uploadedKey = "hugin_rsa"
    }
})

object SandboxPr : GitVcsRoot({
    name = "sandbox_pr"
    url = "git@github.com:inspera/sandbox.git"
    branchSpec = "+:refs/pull/*/head"
    authMethod = uploadedKey {
        uploadedKey = "hugin_rsa"
    }
})
