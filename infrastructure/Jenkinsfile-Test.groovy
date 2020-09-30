pipeline {
    agent any

    environment {
        DOCKER_PROJECT_NAME = "e-ink-display-api"
        DOCKER_IMAGE = '755952719952.dkr.ecr.eu-west-1.amazonaws.com/e-ink-displays-api'
        DOCKER_TAG = "test-$BUILD_NUMBER"

		SERVER_PORT = "1012"

        DB_URL = "jdbc:postgresql://test-pg-bdp.co90ybcr8iim.eu-west-1.rds.amazonaws.com:5432/epaper"
        DB_USERNAME = "epaper"
        DB_PASSWORD = credentials('epaper-api-test-db-password')

		APP_DEBUG = true

		PROXY_ENABLED = true
		PROXY_URL = "localhost:19998"

		NOI_EVENTS_ENABLED = true
		NOI_CRON_EVENTS = "0 0 0/12 * * ?"
		NOI_CRON_DISPLAYS = "0 0/10 6-22 * * ?"

		CRON_HEARTBEAT = "0 0 0/1 * * ?"
    }

    stages {
        stage('Configure') {
            steps {
                sh """
                    rm -f .env
                    cp .env.example .env
                    echo 'COMPOSE_PROJECT_NAME=${DOCKER_PROJECT_NAME}' >> .env
                    echo 'DOCKER_IMAGE=${DOCKER_IMAGE}' >> .env
                    echo 'DOCKER_TAG=${DOCKER_TAG}' >> .env

					echo 'SERVER_PORT=${SERVER_PORT}' >> .env

                    echo 'DB_URL=${DB_URL}' >> .env
                    echo 'DB_USERNAME=${DB_USERNAME}' >> .env
                    echo 'DB_PASSWORD=${DB_PASSWORD}' >> .env

					echo 'APP_DEBUG=${APP_DEBUG}' >> .env

					echo 'PROXY_ENABLED=${PROXY_ENABLED}' >> .env
					echo 'PROXY_URL=${PROXY_URL}' >> .env

					echo 'NOI_EVENTS_ENABLED=${NOI_EVENTS_ENABLED}' >> .env
					echo 'NOI_CRON_EVENTS=${NOI_CRON_EVENTS}' >> .env
					echo 'NOI_CRON_DISPLAYS=${NOI_CRON_DISPLAYS}' >> .env

					echo 'CRON_HEARTBEAT=${CRON_HEARTBEAT}' >> .env
                """
            }
        }
        stage('Test') {
            steps {
                sh '''
                    docker-compose --no-ansi build --pull --build-arg JENKINS_USER_ID=$(id -u jenkins) --build-arg JENKINS_GROUP_ID=$(id -g jenkins)
                    docker-compose --no-ansi run --rm --no-deps -u $(id -u jenkins):$(id -g jenkins) app "mvn -U -B clean test"
                '''
            }
        }
        stage('Build') {
            steps {
                sh '''
                    aws ecr get-login --region eu-west-1 --no-include-email | bash
                    docker-compose --no-ansi -f infrastructure/docker-compose.build.yml build --pull
                    docker-compose --no-ansi -f infrastructure/docker-compose.build.yml push
                '''
            }
        }
        stage('Deploy') {
            steps {
               sshagent(['jenkins-ssh-key']) {
                    sh """
                        (cd infrastructure/ansible && ansible-galaxy install -f -r requirements.yml)
                        (cd infrastructure/ansible && ansible-playbook --limit=test deploy.yml --extra-vars "release_name=${BUILD_NUMBER}")
                    """
                }
            }
        }
    }
}