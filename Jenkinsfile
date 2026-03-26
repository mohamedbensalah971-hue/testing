pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        AGENT_IA_URL = 'http://localhost:8000'
        PROJECT_PATH = "${WORKSPACE}"
        // ANDROID_HOME sera configuré dans Jenkins globalement
        // Ou décommenter la ligne suivante avec le bon chemin:
        // ANDROID_HOME = 'C:\\Users\\Stayha\\AppData\\Local\\Android\\Sdk'
    }

    stages {

        stage('1. Checkout Code') {
            steps {
                echo '📥 Récupération du code depuis GitHub...'
                checkout scm

                script {
                    sh 'git checkout -B main origin/main'

                    try {
                        env.CHANGED_FILES = sh(
                            script: 'git show --pretty="" --name-only HEAD || echo ""',
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
                                echo "${env.CHANGED_FILES}" | grep -E '^app/src/main/.*\\.(kt|java)\$' | grep -v '/test/' | grep -v 'Test\\.(kt|java)\$' || echo ""
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
                                def fileContent = readFile(file)
                                def className = file.tokenize('/').last().replace('.kt', '').replace('.java', '')

                                echo "Classe détectée: ${className}"

                                // Appel Agent IA avec retry
                                def response = ""
                                def maxRetries = 3
                                for (int attempt = 1; attempt <= maxRetries; attempt++) {
                                    response = sh(
                                        script: """
                                            curl -sS -X POST ${AGENT_IA_URL}/generate-tests \\
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

                                    def isRateLimited = response.contains('"rate_limit_exceeded":true') ||
                                        response.contains('"error":"Rate limit exceeded')

                                    if (!isRateLimited) {
                                        break
                                    }

                                    if (attempt < maxRetries) {
                                        def waitSeconds = 15 * attempt
                                        echo "⏳ Agent IA rate-limited (tentative ${attempt}/${maxRetries}), retry dans ${waitSeconds}s..."
                                        sleep(time: waitSeconds, unit: 'SECONDS')
                                    }
                                }

                                echo "Réponse Agent IA: ${response}"

                                if (response.contains('"success": true') || response.contains('"success":true')) {
                                    echo "✅ Test généré avec succès pour ${className}"

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

                                        def testFilePath = file.replace('/main/', '/test/')
                                                       .replace('.kt', 'Test.kt')
                                                       .replace('.java', 'Test.java')

                                        echo "📁 Création du fichier: ${testFilePath}"

                                        sh "mkdir -p \$(dirname ${testFilePath})"
                                        writeFile file: testFilePath, text: testCode

                                        echo "✅ Fichier test sauvegardé: ${testFilePath}"

                                        // Métriques
                                        if (response.contains('"confidence":')) {
                                            def confMatch = (response =~ /"confidence":([0-9.]+)/)
                                            if (confMatch) {
                                                echo "📊 Confiance: ${confMatch[0][1]}"
                                            }
                                        }

                                        if (response.contains('"tokens_used":')) {
                                            def tokensMatch = (response =~ /"tokens_used":([0-9]+)/)
                                            if (tokensMatch) {
                                                echo "📊 Tokens utilisés: ${tokensMatch[0][1]}"
                                            }
                                        }

                                        if (response.contains('"rag_context_used":')) {
                                            def ragMatch = (response =~ /"rag_context_used":(true|false)/)
                                            if (ragMatch) {
                                                echo "📊 RAG utilisé: ${ragMatch[0][1]}"
                                            }
                                        }

                                        // ========================================
                                        // COMMIT ET PUSH AUTOMATIQUE
                                        // ========================================
                                        try {
                                            sh """
                                                git add ${testFilePath}
                                                git config user.name "Jenkins CI"
                                                git config user.email "jenkins@ci.local"
                                                git commit -m "test: Add ${className}Test.kt (Auto-generated by Agent IA)" || echo "Rien à committer"
                                            """
                                            echo "✅ Test commité automatiquement"
                                            
                                            withCredentials([usernamePassword(
                                                credentialsId: 'github-credentials',
                                                usernameVariable: 'GIT_USERNAME',
                                                passwordVariable: 'GIT_PASSWORD'
                                            )]) {
                                                sh """
                                                    git checkout main || git checkout -b main
                                                    git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/mohamedbensalah971/testing.git main
                                                """
                                            }
                                            echo "🚀 Test pushé vers GitHub avec succès!"
                                        } catch (Exception pushError) {
                                            echo "⚠️  Push échoué: ${pushError.message}"
                                            echo "💡 Test archivé dans Jenkins artifacts"
                                        }
                                        // ========================================

                                    } else {
                                        echo "⚠️  Impossible d'extraire le code du test"
                                    }

                                } else {
                                    if (response.contains('"rate_limit_exceeded":true')) {
                                        echo "⚠️  Agent IA en rate-limit pour ${className}"
                                    } else {
                                        echo "⚠️  Génération échouée pour ${className}"
                                    }
                                }

                            } catch (Exception e) {
                                echo "❌ Erreur: ${e.message}"
                            }
                        }
                    }
                }
            }
        }

        stage('4. Archiver Tests Générés') {
            steps {
                echo '📦 Archivage des tests générés...'
                archiveArtifacts artifacts: 'app/src/test/**/*.kt, app/src/test/**/*.java', allowEmptyArchive: true
                echo '✅ Tests archivés dans Jenkins artifacts'
            }
        }

        stage('5. Exécuter Tests Gradle (Optionnel)') {
            steps {
                echo '🧪 Exécution des tests unitaires...'

                script {
                    try {
                        def gradlewExists = fileExists('./gradlew') || fileExists('gradlew.bat')

                        if (gradlewExists) {
                            echo "✅ Gradle wrapper trouvé"

                            // Vérifier ANDROID_HOME
                            def androidHome = env.ANDROID_HOME
                            if (!androidHome) {
                                echo "⚠️  ANDROID_HOME non défini, skip des tests Gradle"
                                echo "💡 Configurer ANDROID_HOME dans Jenkins → Manage → System → Global properties"
                                return
                            }

                            // Créer local.properties
                            writeFile file: 'local.properties', text: "sdk.dir=${androidHome.replace('\\\\', '\\\\\\\\')}"
                            
                            if (isUnix()) {
                                sh 'chmod +x ./gradlew'
                                sh './gradlew test --no-daemon || true'
                            } else {
                                bat 'gradlew.bat test --no-daemon || exit 0'
                            }

                            echo "✅ Tests exécutés"
                        } else {
                            echo "⚠️  Gradle wrapper non trouvé"
                            echo "💡 Ajouter gradlew à ton projet: gradle wrapper"
                        }
                    } catch (Exception e) {
                        echo "⚠️  Erreur Gradle: ${e.message}"
                    }
                }
            }
        }

        stage('6. Publier Résultats Tests') {
            steps {
                echo '📊 Publication des résultats...'
                script {
                    junit testResults: '**/build/test-results/test/*.xml', allowEmptyResults: true
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

        always {
            echo '🧹 Nettoyage...'
            archiveArtifacts artifacts: '**/build/reports/**', allowEmptyArchive: true
        }
    }
}
