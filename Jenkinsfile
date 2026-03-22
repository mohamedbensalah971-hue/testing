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
                        // Exécuter les tests Gradle
                        if (isUnix()) {
                            sh './gradlew test --no-daemon || true'
                        } else {
                            bat 'gradlew.bat test --no-daemon || exit 0'
                        }
                        
                        echo "✅ Tests exécutés"
                        
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
                    // Chercher les rapports de tests
                    def testReports = findFiles(glob: '**/build/test-results/test/*.xml')
                    
                    if (testReports.length > 0) {
                        echo "✅ ${testReports.length} rapports de test trouvés"
                        
                        // Publier les résultats
                        try {
                            junit '**/build/test-results/test/*.xml'
                        } catch (Exception e) {
                            echo "⚠️  Impossible de publier les résultats JUnit: ${e.message}"
                        }
                    } else {
                        echo "⚠️  Aucun rapport de test trouvé"
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
                                "timestamp": "${new Date()}"
                            }' || true
                        """
                        
                        echo "✅ Agent IA notifié"
                    } catch (Exception e) {
                        echo "⚠️  Notification échouée: ${e.message}"
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
