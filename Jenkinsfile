pipeline {
    agent any

    environment {
        DOCKER_REGISTRY_CREDENTIALS = 'DockerHubCred'
        DOCKER_IMAGE_NAME = 'astar-shortest-path-finder'
    }

    stages {
        stage('Clone Repository') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/master']],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [],
                    submoduleCfg: [],
                    userRemoteConfigs: [[url: 'https://github.com/ARYAN-PATEL-11/Astar.git']]
                ])
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE_NAME}", '.')
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('', 'DockerHubCred') {
                        sh "docker tag ${DOCKER_IMAGE_NAME}:latest aryanpatel111/${DOCKER_IMAGE_NAME}:latest"
                        sh "docker push aryanpatel111/${DOCKER_IMAGE_NAME}:latest"
                    }
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                script {
                    ansiblePlaybook(
                        playbook: 'deploy.yml',
                        inventory: 'inventory'
                    )
                }
            }
        }

        stage('Run Python Script') {
            steps {
                script {
                    // Install necessary Python dependencies
                    sh 'pip3 install elasticsearch'

                    // Run the Python script to upload logs
                    sh 'python3 parsernew2.py'
                }
            }
        }
    }
}
