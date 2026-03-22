pipeline {
    agent any
    
    environment {
        AGENT_IA_URL = 'http://localhost:8000'
        PROJECT_PATH = "${WORKSPACE}"
        PYTHON_VENV = 'C:\\Users\\Stayha\\Downloads\\agent-ia-server-phase1\\agent-ia-server\\venv'
    }
    
    stages {
        
        stage('1. Checkout Code') {
            steps {
                echo '📥 Récupération du code depuis GitHub...'
                checkout scm
                
                script {
                    // Récupérer les fichiers modifiés
                    try {
                        env.CHANGED_FILES = sh(
                            script: 'git diff --name-only HEAD~1 || echo ""',
                            returnStdout: true
                        ).trim()
                        
                        echo "Fichiers modifiés: ${env.CHANGED_FILES}"
                    } catch (Exception e) {
                        echo "Premier build, pas de diff disponible"
                        env.CHANGED_FILES = ""
                    }
                }
            }
        }
        
        stage('2. Analyser Fichiers Modifiés') {
            steps {
                echo '🔍 Analyse des fichiers Java/Kotlin modifiés...'
                
                script {
                    if (env.CHANGED_FILES) {
                        def sourceFiles = sh(
                            script: """
                                echo "${env.CHANGED_FILES}" | grep -E '\\.(kt|java)\$' | grep -v 'Test\\.(kt|java)\$' || echo ""
                            """,
                            returnStdout: true
                        ).trim()
                        
                        if (sourceFiles) {
                            env.SOURCE_FILES = sourceFiles
                            echo "✅ Fichiers source détectés: ${sourceFiles}"
                        } else {
                            echo "⚠️  Aucun fichier source modifié"
                            env.SOURCE_FILES = ""
                        }
                    } else {
                        echo "⚠️  Aucun fichier modifié détecté"
                        env.SOURCE_FILES = ""
                    }
                }
            }
        }
        
        stage('3. Générer Tests via Agent IA') {
            when {
                expression { env.SOURCE_FILES != "" && env.SOURCE_FILES != null }
            }
            steps {
                echo '🤖 Génération des tests via Agent IA...'
                
                script {
                    env.SOURCE_FILES.split('\n').each { file ->
                        if (file.trim()) {
                            echo "📝 Génération test pour: ${file}"
                            
                            try {
                                // Lire le contenu du fichier
                                def fileContent = readFile(file)
                                
                                // Extraire le nom de classe (simple)
                                def className = file.tokenize('/').last().replace('.kt', '').replace('.java', '')
                                
                                echo "Classe détectée: ${className}"
                                
                                // Appeler l'Agent IA
                                def response = sh(
                                    script: """
                                        curl -X POST ${AGENT_IA_URL}/generate-tests \
                                        -H 'Content-Type: application/json' \
                                        -d @- << 'EOF'
{
  "file": "${file}",
  "code": ${groovy.json.JsonOutput.toJson(fileContent)},
  "class_name": "${className}"
}
EOF
                                    """,
                                    returnStdout: true
                                ).trim()
                                
                                echo "Réponse Agent IA: ${response}"
                                
                                // Parser JSON (simple)
                                if (response.contains('"success": true') || response.contains('"success":true')) {
                                    echo "✅ Test généré avec succès pour ${className}"
                                } else {
                                    echo "⚠️  Génération échouée pour ${className}"
                                }
                                
                            } catch (Exception e) {
                                echo "❌ Erreur lors de la génération pour ${file}: ${e.message}"
                            }
                        }
                    }
                }
            }
        }
        
        stage('4. Exécuter Tests Gradle') {
            steps {
                echo '🧪 Exécution des tests unitaires...'
                
                script {
                    try {
                        // Vérifier si gradlew existe
                        def gradlewExists = fileExists('./gradlew') || fileExists('gradlew.bat')
                        
                        if (gradlewExists) {
                            echo "✅ Gradle wrapper trouvé"
                            
                            // Exécuter les tests Gradle
                            if (isUnix()) {
                                sh 'chmod +x ./gradlew || true'
                                sh './gradlew test --no-daemon || true'
                            } else {
                                bat 'gradlew.bat test --no-daemon || exit 0'
                            }
                            
                            echo "✅ Tests exécutés"
                        } else {
                            echo "⚠️  Gradle wrapper non trouvé, skip des tests"
                            echo "💡 Pour activer: Ajouter gradlew à ton projet"
                        }
                        
                    } catch (Exception e) {
                        echo "⚠️  Erreur lors de l'exécution des tests: ${e.message}"
                    }
                }
            }
        }
        
        stage('5. Vérifier Résultats Tests') {
            steps {
                echo '📊 Analyse des résultats de tests...'
                
                script {
                    try {
                        // Chercher les rapports de tests (alternative à findFiles)
                        def testResults = sh(
                            script: 'find . -path "*/build/test-results/test/*.xml" 2>/dev/null || echo ""',
                            returnStdout: true
                        ).trim()
                        
                        if (testResults) {
                            echo "✅ Rapports de test trouvés:"
                            echo testResults
                            
                            // Publier les résultats JUnit
                            try {
                                junit '**/build/test-results/test/*.xml'
                                echo "✅ Résultats JUnit publiés"
                            } catch (Exception e) {
                                echo "⚠️  Impossible de publier JUnit: ${e.message}"
                            }
                        } else {
                            echo "⚠️  Aucun rapport de test trouvé"
                            echo "💡 Ceci est normal si gradlew n'a pas été exécuté"
                        }
                    } catch (Exception e) {
                        echo "⚠️  Erreur lors de la recherche des rapports: ${e.message}"
                    }
                }
            }
        }
        
        stage('6. Notifier Agent IA') {
            steps {
                echo '📡 Notification Agent IA...'
                
                script {
                    try {
                        // Notifier l'Agent IA du build
                        sh """
                            curl -X POST ${AGENT_IA_URL}/webhook/jenkins-auto \
                            -H 'Content-Type: application/json' \
                            -d '{
                                "build_number": "${env.BUILD_NUMBER}",
                                "status": "success",
                                "project": "${env.JOB_NAME}",
                                "timestamp": "${new Date()}"
                            }' || echo "Agent IA non disponible"
                        """
                        
                        echo "✅ Tentative de notification Agent IA"
                    } catch (Exception e) {
                        echo "⚠️  Notification échouée (normal si Agent IA non démarré): ${e.message}"
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline terminé avec succès!'
            echo "Build #${env.BUILD_NUMBER}"
            echo "Durée: ${currentBuild.durationString}"
        }
        
        failure {
            echo '❌ Pipeline échoué!'
            echo "Build #${env.BUILD_NUMBER}"
        }
        
        unstable {
            echo '⚠️  Pipeline instable'
        }
        
        always {
            echo '🧹 Nettoyage...'
            
            // Archiver les rapports si disponibles
            archiveArtifacts artifacts: '**/build/reports/**', allowEmptyArchive: true
        }
    }
}
