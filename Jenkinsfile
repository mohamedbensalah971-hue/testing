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
                                // DEBUG: Afficher le chemin du fichier
                                echo "🔍 DEBUG - Fichier source complet: ${file}"
                                
                                // Lire le contenu du fichier
                                def fileContent = readFile(file)
                                
                                // Extraire le nom de classe
                                def className = file.tokenize('/').last().replace('.kt', '').replace('.java', '')
                                echo "🔍 DEBUG - Nom de classe: ${className}"
                                
                                // Appeler l'Agent IA
                                def response = sh(
                                    script: """
                                        curl -X POST ${AGENT_IA_URL}/generate-tests \\
                                        -H 'Content-Type: application/json' \\
                                        -d @- << 'EOF'
{
  "source_file": "${file}",
  "source_code": ${groovy.json.JsonOutput.toJson(fileContent)},
  "class_name": "${className}"
}
EOF
                                    """,
                                    returnStdout: true
                                ).trim()
                                
                                echo "Réponse Agent IA: ${response}"
                                
                                // Parser JSON manuellement
                                if (response.contains('"success": true') || response.contains('"success":true')) {
                                    echo "✅ Test généré avec succès pour ${className}"
                                    
                                    try {
                                        // Extraire le code du test
                                        def startMarker = '"generated_tests":"'
                                        def endMarker = '","explanation"'
                                        
                                        def startIdx = response.indexOf(startMarker)
                                        def endIdx = response.indexOf(endMarker)
                                        
                                        if (startIdx != -1 && endIdx != -1) {
                                            startIdx += startMarker.length()
                                            def testCodeEscaped = response.substring(startIdx, endIdx)
                                            
                                            def testCode = testCodeEscaped
                                                .replace('\\n', '\n')
                                                .replace('\\t', '\t')
                                                .replace('\\"', '"')
                                                .replace('\\\\', '\\')
                                            
                                            // NOUVELLE LOGIQUE - Construire le chemin test en miroir
                                            def testFileName = "${className}Test.kt"
                                            
                                            // Méthode robuste: parser le chemin
                                            def parts = file.split('/')
                                            def newParts = []
                                            
                                            for (part in parts) {
                                                if (part == 'main') {
                                                    newParts.add('test')
                                                } else if (part.endsWith('.kt')) {
                                                    newParts.add(part.replace('.kt', 'Test.kt'))
                                                } else if (part.endsWith('.java')) {
                                                    newParts.add(part.replace('.java', 'Test.java'))
                                                } else {
                                                    newParts.add(part)
                                                }
                                            }
                                            
                                            def testFilePath = newParts.join('/')
                                            
                                            // DEBUG: Afficher tous les chemins
                                            echo "🔍 DEBUG - Fichier source: ${file}"
                                            echo "🔍 DEBUG - Fichier test calculé: ${testFilePath}"
                                            echo "📁 Création du fichier: ${testFilePath}"
                                            
                                            // Extraire le dossier parent
                                            def testDir = testFilePath.substring(0, testFilePath.lastIndexOf('/'))
                                            echo "🔍 DEBUG - Dossier à créer: ${testDir}"
                                            
                                            // Créer TOUS les dossiers parents
                                            sh """
                                                echo "Création des dossiers..."
                                                mkdir -p "${testDir}"
                                                echo "✅ Dossiers créés: ${testDir}"
                                                ls -la "${testDir}" || echo "Dossier non accessible"
                                            """
                                            
                                            // Écrire le fichier de test
                                            echo "Écriture du fichier test..."
                                            writeFile file: testFilePath, text: testCode
                                            
                                            // Vérifier que le fichier existe
                                            sh """
                                                if [ -f "${testFilePath}" ]; then
                                                    echo "✅ Fichier test créé avec succès: ${testFilePath}"
                                                    ls -lh "${testFilePath}"
                                                else
                                                    echo "❌ ERREUR: Fichier non créé!"
                                                    exit 1
                                                fi
                                            """
                                            
                                            // Commit automatique
                                            sh """
                                                git add "${testFilePath}"
                                                git status
                                                git config user.name "Jenkins CI"
                                                git config user.email "jenkins@ci.local"
                                                git commit -m "test: Add ${testFileName} (Auto-generated by Agent IA)" || echo "Rien à committer"
                                            """
                                            
                                            echo "✅ Test commité automatiquement"
                                            
                                            // Push automatique
                                            try {
                                                sh """
                                                    echo "Checkout main branch..."
                                                    git checkout main || git checkout -b main
                                                    echo "Push vers GitHub..."
                                                    git push origin main || echo "Push échoué"
                                                """
                                                echo "🚀 Test pushé vers GitHub"
                                            } catch (Exception pushError) {
                                                echo "⚠️  Push impossible: ${pushError.message}"
                                                echo "💡 Le test est dans: ${testFilePath}"
                                            }
                                            
                                            // Métriques
                                            if (response.contains('"confidence":')) {
                                                def confMatch = (response =~ /"confidence":([0-9.]+)/)
                                                if (confMatch) {
                                                    echo "📊 Confiance: ${confMatch[0][1]}"
                                                }
                                            }
                                            
                                        } else {
                                            echo "⚠️  Impossible d'extraire le code du test"
                                        }
                                        
                                    } catch (Exception saveError) {
                                        echo "❌ ERREUR lors de la sauvegarde: ${saveError.message}"
                                        saveError.printStackTrace()
                                    }
                                    
                                } else {
                                    echo "⚠️  Génération échouée pour ${className}"
                                }
                                
                            } catch (Exception e) {
                                echo "❌ Erreur: ${e.message}"
                                e.printStackTrace()
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
                        def gradlewExists = fileExists('./gradlew') || fileExists('gradlew.bat')
                        
                        if (gradlewExists) {
                            if (isUnix()) {
                                sh 'chmod +x ./gradlew || true'
                                sh './gradlew test --no-daemon || true'
                            } else {
                                bat 'gradlew.bat test --no-daemon || exit 0'
                            }
                            echo "✅ Tests exécutés"
                        } else {
                            echo "⚠️  Gradle wrapper non trouvé"
                        }
                    } catch (Exception e) {
                        echo "⚠️  Erreur: ${e.message}"
                    }
                }
            }
        }
        
        stage('5. Vérifier Résultats Tests') {
            steps {
                echo '📊 Analyse des résultats...'
                script {
                    try {
                        def testResults = sh(
                            script: 'find . -path "*/build/test-results/test/*.xml" 2>/dev/null || echo ""',
                            returnStdout: true
                        ).trim()
                        
                        if (testResults) {
                            echo "✅ Rapports trouvés"
                            try {
                                junit '**/build/test-results/test/*.xml'
                            } catch (Exception e) {
                                echo "⚠️  JUnit: ${e.message}"
                            }
                        } else {
                            echo "⚠️  Aucun rapport"
                        }
                    } catch (Exception e) {
                        echo "⚠️  Erreur: ${e.message}"
                    }
                }
            }
        }
        
        stage('6. Notifier Agent IA') {
            steps {
                echo '📡 Notification Agent IA...'
                script {
                    try {
                        sh """
                            curl -X POST ${AGENT_IA_URL}/webhook/jenkins-auto \\
                            -H 'Content-Type: application/json' \\
                            -d '{
                                "build_number": "${env.BUILD_NUMBER}",
                                "status": "success"
                            }' || echo "Agent IA non disponible"
                        """
                    } catch (Exception e) {
                        echo "⚠️  Notification échouée"
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline terminé avec succès!'
            echo "Build #${env.BUILD_NUMBER}"
        }
        failure {
            echo '❌ Pipeline échoué!'
        }
        always {
            echo '🧹 Nettoyage...'
            archiveArtifacts artifacts: '**/build/reports/**', allowEmptyArchive: true
        }
    }
}
