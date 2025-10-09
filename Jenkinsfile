pipeline {
    agent any
    
    environment {
        // 프로젝트 설정
        DOCKER_IMAGE = 'sugyo-backend'
        DOCKER_TAG = "${BUILD_NUMBER}"
        CONTAINER_NAME = 'spring-app'
        DOCKER_NETWORK = 'infra_infra-network'
        ENV_FILE = '/home/ubuntu/infra/.env'
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
        timeout(time: 20, unit: 'MINUTES')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📦 소스 코드 체크아웃 중...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo '🔨 Spring Boot 애플리케이션 빌드 중...'
                sh '''
                    chmod +x gradlew
                    ./gradlew clean build --no-daemon -x test
                '''
            }
        }
        
        stage('Test') {
            steps {
                echo '🧪 테스트 실행 중...'
                sh './gradlew test --no-daemon'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                echo '🐳 Docker 이미지 빌드 중...'
                sh """
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                """
            }
        }
        
        stage('Stop Old Container') {
            steps {
                echo '🛑 기존 컨테이너 중지 중...'
                sh """
                    docker stop ${CONTAINER_NAME} || true
                    docker rm ${CONTAINER_NAME} || true
                """
            }
        }
        
        stage('Deploy') {
            steps {
                echo '🚀 새 컨테이너 배포 중...'
                sh """
                    docker run -d \
                        --name ${CONTAINER_NAME} \
                        --network ${DOCKER_NETWORK} \
                        --env-file ${ENV_FILE} \
                        --restart unless-stopped \
                        -p 8081:8080 \
                        ${DOCKER_IMAGE}:latest
                """
            }
        }
    
        
        stage('Cleanup') {
            steps {
                echo '🧹 이전 이미지 정리 중...'
                sh """
                    # 5개 이전 이미지 삭제
                    docker images ${DOCKER_IMAGE} --format "{{.Tag}}" | \
                    grep -E '^[0-9]+\$' | sort -rn | tail -n +6 | \
                    xargs -I {} docker rmi ${DOCKER_IMAGE}:{} || true
                """
            }
        }
    }
    
    post {
        success {
            echo '✅ 배포 성공!'
            // 슬랙/디스코드 알림 추가 가능
        }
        failure {
            echo '❌ 배포 실패!'
            sh """
                # 로그 출력
                docker logs ${CONTAINER_NAME} || true
            """
        }
        always {
            cleanWs()
        }
    }
}
