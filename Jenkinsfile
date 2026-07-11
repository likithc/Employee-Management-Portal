pipeline {
    agent any

    tools {
        maven 'Maven_3.9' // Must match the name configured in Jenkins Global Tool Configuration
        jdk 'JDK_17'      // Must match the name configured in Jenkins Global Tool Configuration
    }

    environment {
        DOCKER_HUB_USER = 'your_dockerhub_username' // Replace with your actual Docker Hub username
        IMAGE_NAME      = 'employee-management-app'
        IMAGE_TAG       = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG} ."
                sh "docker tag ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG} ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USER')]) {
                    sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USER --password-stdin"
                    sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Remove Old Container') {
            steps {
                script {
                    // Stop and remove existing container if it exists to avoid port conflicts
                    sh '''
                        docker stop employee-service || true
                        docker rm employee-service || true
                    '''
                }
            }
        }

        stage('Run New Container') {
            steps {
                // Running local container bound to the same network or standalone
                // Note: If deploying the full stack on the server via compose, see Task 5.
                sh "docker run -d --name employee-service -p 8080:8080 ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }

        stage('Health Check') {
            steps {
                echo 'Waiting for application to start...'
                sleep 15
                script {
                    def response = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/actuator/health || curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/api/employees || true", returnStdout: true).trim()
                    // Accept typical successful or API response codes (200, 404 if no data, or 401 if secured)
                    if (response != "000") {
                        echo "Health Check Passed with response code: ${response}"
                    } else {
                        error "Health Check Failed. Application is unreachable."
                    }
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout'
        }
    }
}
