#!/usr/bin/env groovy

/**
*
*/

import groovy.json.JsonOutput

import java.util.Optional

import hudson.tasks.test.AbstractTestResultAction
import hudson.tasks.junit.CaseResult
import hudson.model.Actionable

def author      = ""
def message     = ""
def testSummary = ""
def total       = 0
def failed      = 0
def skipped     = 0
def failedTestsString = "```"

pipeline {

	agent {
		docker {
			image 'maven:3-alpine'
			args '-v $HOME/.m2:/root/.m2'
		}
	}

	triggers {
		pollSCM '* * * * *'
	}

	environment {
		JOB_NAME      = "${env.JOB_NAME}"

		SLACK_USER    = "Jenkins";
		SLACK_CHANNEL = "#springboot"
        SLACK_URL     = 'https://soa-developer.slack.com/services/hooks/jenkins-ci?token='
        SLACK_ICON    = 'https://wiki.jenkins-ci.org/download/attachments/2916393/logo.png'
        SLACK_TOKEN   = credentials("slack-token") //25DwwRtqn7AVWpeTDGbfmjGc

        COMPOSE_FILE  = "docker-compose.yml"
        REGISTRY_AUTH = credentials("docker-registry")

        NEXUS_VERSION  = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL      = "172.17.0.3:8081"
        NEXUS_REPOSITORY      = "microservice-repository"
        NEXUS_DOCKER_REGISTRY = "docker-registry"
        NEXUS_CREDENTIAL_ID   = "nexus-credentials"
	}

	options {
		timeout(time: 5)
	}

	parameters {

	}

	stages {

		stage('setup') {
			def buildColor = currentBuild.result == null ? "good" : "warning"
		    //def jobName    = "${env.JOB_NAME}"
		    def git_url    = sh(returnStdout: true, script: 'git config remote.origin.url').trim()

		    populateGlobalVariables()
		    def buildStatus = currentBuild.result == null ? "Success" : currentBuild.result

		    try {
	              checkout scm
	              notification("slack",
	                           buildStatus,
	                           buildColor,
	                           "Conexion exitosa al repositorio ${git_url} ...",
	                           JOB_NAME,
	                           author,
	                           testSummary,
	                           message,
	                           failedTestsString)
	        } catch (err) {
	            buildColor = "danger"
	            notification("slack",
	                         buildStatus,
	                         buildColor,
	                         "Error conectando al repositorio ${git_url} ...",
	                         jobName,
	                         author,
	                         testSummary,
	                         message,
	                         failedTestsString)
	        }  // fin try - catch
		}

		stage('code_analysis') {
			steps {
				// TODO configurar servidor de sonarquebe
				sh 'mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.organization=SONAR_ORGANIZATION -Dsonar.login=SONAR_TOKEN'
			}
		}

		stage('build') {
			steps {
				sh 'mvn -B -V -U -e clean package install'
				sh 'docker build --no-cache=true --build-arg BUILD_DATE=$(date -u +\'%Y-%m-%dT%H:%M:%SZ\') --build-arg BUILD_VERSION=1.0-stable --tag=customer-service:latest --rm=true .'
				sh 'docker-compose build'
                waitUntilServicesReady
			}
		}

		stage('unit_integration_test') {
			parallel 'unit-test': {
					 	try {
					 		sh 'mvn install -DskipITs'
					 	} catch (err) {
						 	buildColor = "danger"
				            notification("slack",
				                         buildStatus,
				                         buildColor,
				                         "Error ejecutando las pruebas unitarias ...",
				                         jobName,
				                         author,
				                         testSummary,
				                         message,
				                         failedTestsString)
					 	}
					  },
					 'integration-test': {
					 	try {
					 		sh 'mvn install -DskipUTs'
					 	} catch (err) {
						 	buildColor = "danger"
				            notification("slack",
				                         buildStatus,
				                         buildColor,
				                         "Error ejecutando las pruebas de integracion ...",
				                         jobName,
				                         author,
				                         testSummary,
				                         message,
				                         failedTestsString)
					 	}
					 }
		}

		stage('reports') {
			parallel 'ut_report': {
						steps  {
							junit 'target/surefire-reports/*.xml'
						}
			         },
			         'it_report': {
			         	steps {
			         		junit 'target/failsafe-reports/*.xml'
			         	}
			         }
		}

		stage('archive') {
			parallel 'spring-boot-artifact': {
						try {
							publis_artifacts()
						} catch (err) {
						 	buildColor = "danger"
				            notification("slack",
				                         buildStatus,
				                         buildColor,
				                         "Error cargando el artefecto en el repositorio ...",
				                         jobName,
				                         author,
				                         testSummary,
				                         message,
				                         failedTestsString)
						}
			         },
			         'docker-image-artifac': {
			         	try {
							// TODO validar como upload docker image al registry nexus
						} catch (err) {
						 	buildColor = "danger"
				            notification("slack",
				                         buildStatus,
				                         buildColor,
				                         "Error cargando el artefecto en el registro ...",
				                         jobName,
				                         author,
				                         testSummary,
				                         message,
				                         failedTestsString)
						}
			         }
		}

		stage('deploy') {
			steps {
                sh 'docker-compose up -d'
                waitUntilServicesReady
			}
		}

		stage('load_stres_test') {
			steps {
				try {
					sh 'mvn verify -Dconcurrency=500 -Drampup=2 -Dloop=10 -DTestName=customer_api -Dmaven.test.skip=true'
				} catch (err) {
					buildColor = "danger"
		            notification("slack",
		                         buildStatus,
		                         buildColor,
		                         "Error ejecutando las pruebas de carga y estres ...",
		                         jobName,
		                         author,
		                         testSummary,
		                         message,
		                         failedTestsString)
				}
			}
		}

		stage('notification') {

		}

	}

	post {

		//always {}

		//aborted {}
		// TODO implementar las notificaciones para el metodos post
		success {}

		unstable {}

		failure {}

	}

}

properties ([
		parameters ([
				choise(name        : 'environment',
				       description : 'Seleccione el ambiente:',
					   choises     : ['test','qa'])
		])
])


def isPublishingBranch = { ->
    return env.GIT_BRANCH == 'origin/master' || env.GIT_BRANCH =~ /release.+/
}

def getGitAuthor = {
    def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
    author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
}

def getLastCommitMessage = {
    message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim()
}

@NonCPS
def getTestSummary = { ->
    def testAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    def summary = ""

    if (testAction != null) {
        total   = testAction.getTotalCount()
        failed  = testAction.getFailCount()
        skipped = testAction.getSkipCount()

        summary = "Passed : ${total - failed - skipped}"
        summary = "${summary}, Failed: ${failed}"
        summary = "${summary}, Skipped: ${skipped}"
    } else {
        summary = "No se encontraron test para ejecutar"
    }

    return summary
}

@NonCPS
def getFailedTests = { ->
    def testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)

    if (testResultAction != null) {
        def failedTests = testResultAction.getFailedTests()

        if (failedTests.size() > 9) {
            failedTests = failedTests.subList(0, 8)
        }

        for(CaseResult cr : failedTests) {
            failedTestsString = failedTestsString + "${cr.getFullDisplayName()}:\n${cr.getErrorDetails()}\n\n"
        }
        failedTestsString = failedTestsString + "```"
    }
    return failedTestsString
}

def populateGlobalVariables = {
    getLastCommitMessage()
    getGitAuthor()
    testSummary = getTestSummary()
}

def publis_artifacts() {
    pom = readMavenPom file: "pom.xml";
    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
    artifactPath = filesByGlob[0].path;
    artifactExists = fileExists artifactPath;

    if(artifactExists) {
        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";
        nexusArtifactUploader(
            nexusVersion: NEXUS_VERSION,
            protocol: NEXUS_PROTOCOL,
            nexusUrl: NEXUS_URL,
            groupId: pom.groupId,
            version: pom.version,
            repository: NEXUS_REPOSITORY,
            credentialsId: NEXUS_CREDENTIAL_ID,

            artifacts: [
			             [
			               artifactId: pom.artifactId,
			               classifier: '',
			               file: artifactPath,
			               type: pom.packaging],
			               [artifactId: pom.artifactId,
			               classifier: '',
			               file: "pom.xml",
			               type: "pom"
			             ]
			           ]
        )
    } else {
        error "*** File: ${artifactPath}, could not be found";
    }
}

def notification(String type,
                 String status,
                 String color,
                 String text,
                 String job_name,
                 String author,
                 String testSummary,
                 String message,
                 String failedTestsString) {
    switch(type) {
        case "slack" :
                      slack_notification(text,
                                         SLACK_CHANNEL,
                                         [
                                            [
                                                title       : "${job_name}, build #${env.BUILD_NUMBER}",
                                                title_link  : "${env.BUILD_URL}",
                                                color       : "${color}",
                                                text        : "${status}\n${author}",
                                                "mrkdwn_in" : [
                                                              "fields"
                                                ],
                                                fields: [
                                                          [
                                                           title: "Branch",
                                                           value: "${env.GIT_BRANCH}",
                                                           short: true
                                                          ],
                                                          [
                                                           title: "Test Results",
                                                           value: "${testSummary}",
                                                           short: true
                                                          ],
                                                          [
                                                           title: "Last Commit",
                                                           value: "${message}",
                                                           short: false
                                                          ]
                                                ]
                                            ],
                                            [
                                              title: "Failed Tests",
                                              color: "${color}",
                                              text: "${failedTestsString}",
                                              "mrkdwn_in": [
                                                            "text"
                                                           ],
                                            ]
                                         ])

        case "email" : email_notification(text, slackchannel)
        break
            println "default ${type} - ${status}"
    }
}

def slack_notification(text, channel, attachments) {
    def slack_data  = JsonOutput.toJson([
	                                     channel     : channel,
	                                     text        : text,
	                                     username    : SLACK_USER,
	                                     icon_url    : SLACK_ICON,
	                                     attachments : attachments
	                                    ])

    sh "curl -v -X POST --data-urlencode \'payload=${slack_data}\' ${SLACK_URL}${SLACK_TOKEN}"
}

def email_notification(text, channel) {
    //TODO Configurar email plugin y envio de correo
}
