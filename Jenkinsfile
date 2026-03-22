pipeline {
    agent any
    
    stages {
        stage('1. Checkout Code') {
            steps {
                echo '📥 Récupération du code depuis GitHub...'
                checkout scm
            }
        }
        
        stage('2. Build Info') {
            steps {
                echo '📊 Informations du build'
                script {
                    echo "Build Number: ${env.BUILD_NUMBER}"
                    echo "Job Name: ${env.JOB_NAME}"
                    echo "Git Branch: ${env.GIT_BRANCH}"
                    echo "Git Commit: ${env.GIT_COMMIT}"
                }
            }
        }
        
        stage('3. Liste Fichiers') {
            steps {
                echo '📂 Contenu du projet:'
                script {
                    if (isUnix()) {
                        sh 'ls -la'
                    } else {
                        bat 'dir'
                    }
                }
            }
        }
        
        stage('4. Test Message') {
            steps {
                echo '✅ LE PIPELINE FONCTIONNE!'
                echo '✅ WEBHOOK GITHUB → JENKINS OK!'
                echo '🎉 Build automatique réussi!'
            }
        }
    }
    
    post {
        success {
            echo '✅ Build terminé avec succès!'
            echo "Durée: ${currentBuild.durationString}"
        }
        failure {
            echo '❌ Build échoué!'
        }
        always {
            echo '🧹 Nettoyage terminé'
        }
    }
}
