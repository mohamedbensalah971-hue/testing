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
                            script: '''
                                set +e
                                git fetch --unshallow origin >/dev/null 2>&1 || true

                                CHANGED=$(git diff-tree --no-commit-id --name-only -r -m HEAD 2>/dev/null)
                                if [ -z "$CHANGED" ]; then
                                    CHANGED=$(git show --pretty="" --name-only HEAD 2>/dev/null)
                                fi
                                if [ -z "$CHANGED" ]; then
                                    CHANGED=$(git diff --name-only HEAD~1 HEAD 2>/dev/null)
                                fi

                                printf "%s" "$CHANGED"
                            ''',
                            returnStdout: true
                        ).trim()

                        env.DELETED_FILES = sh(
                            script: '''
                                set +e
                                git fetch --unshallow origin >/dev/null 2>&1 || true

                                DELETED=$(git diff-tree --no-commit-id --name-status -r -m HEAD 2>/dev/null | grep '^D' | cut -f2)
                                if [ -z "$DELETED" ]; then
                                    DELETED=$(git diff --name-only --diff-filter=D HEAD~1 HEAD 2>/dev/null)
                                fi

                                printf "%s" "$DELETED"
                            ''',
                            returnStdout: true
                        ).trim()
                        
                        echo "Fichiers modifiés: ${env.CHANGED_FILES}"
                        echo "Fichiers supprimés: ${env.DELETED_FILES}"
                    } catch (Exception e) {
                        echo "Premier build, pas de diff disponible"
                        env.CHANGED_FILES = ""
                        env.DELETED_FILES = ""
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

                        if (sourceFiles && env.DELETED_FILES) {
                            def deletedSet = env.DELETED_FILES.split('\n') as Set
                            sourceFiles = sourceFiles
                                .split('\n')
                                .findAll { it?.trim() && !deletedSet.contains(it.trim()) }
                                .join('\n')
                                .trim()
                        }
                        
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

                    if (env.DELETED_FILES) {
                        def deletedSourceFiles = sh(
                            script: """
                                echo "${env.DELETED_FILES}" | grep -E '^app/src/main/.*\\.(kt|java)\$' | grep -v '/test/' | grep -v 'Test\\.(kt|java)\$' || echo ""
                            """,
                            returnStdout: true
                        ).trim()

                        env.DELETED_SOURCE_FILES = deletedSourceFiles
                        if (deletedSourceFiles) {
                            echo "🗑️  Fichiers source supprimés détectés: ${deletedSourceFiles}"
                        }
                    } else {
                        env.DELETED_SOURCE_FILES = ""
                    }
                }
            }
        }

        stage('3. Supprimer Tests Obsolètes') {
            when {
                expression { env.DELETED_SOURCE_FILES != "" && env.DELETED_SOURCE_FILES != null }
            }
            steps {
                echo '🧹 Suppression des tests obsolètes...'

                script {
                    def removedTests = []

                    env.DELETED_SOURCE_FILES.split('\n').each { file ->
                        if (file.trim()) {
                            def className = file.tokenize('/').last().replace('.kt', '').replace('.java', '')
                            def sourceParts = file.split('/')
                            def sourceDir = sourceParts.length >= 2 ? sourceParts[sourceParts.length - 2] : ""

                            // Chemin "standard" (main -> test)
                            def testFilePath = file.replace('/main/', '/test/')
                                .replace('.kt', 'Test.kt')
                                .replace('.java', 'Test.java')
                            removedTests << testFilePath

                            // Chemin historique utilisé précédemment: app/src/test/java/com/quickchat/app/data/{sourceDir}/ClassTest.kt
                            if (sourceDir) {
                                removedTests << "app/src/test/java/com/quickchat/app/data/${sourceDir}/${className}Test.kt"
                            }
                        }
                    }

                    removedTests = removedTests.unique()

                    if (removedTests.size() > 0) {
                        withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_TOKEN')]) {
                            sh """
                                set -e
                                git config user.name "Jenkins CI"
                                git config user.email "jenkins@ci.local"

                                git fetch origin main
                                git checkout main || git checkout -b main origin/main
                                git pull --rebase origin main || true

                                CHANGED=0
                                ${removedTests.collect { "if [ -f \"${it}\" ]; then git rm -f \"${it}\"; CHANGED=1; fi" }.join('\n                                ')}

                                if [ "\$CHANGED" -eq 0 ]; then
                                    echo "ℹ️  Aucun test obsolète à supprimer"
                                    exit 0
                                fi

                                git commit -m "test: Remove obsolete tests (Auto-generated by Agent IA)"

                                REMOTE_URL=\$(git config --get remote.origin.url)
                                AUTH_URL=\$(echo "\$REMOTE_URL" | sed "s#https://#https://${GIT_USERNAME}:${GIT_TOKEN}@#")
                                git push "\$AUTH_URL" main
                            """
                        }
                        echo "✅ Suppression des tests obsolètes poussée vers GitHub"
                    } else {
                        echo "ℹ️  Aucun test obsolète à supprimer"
                    }
                }
            }
        }
        
        stage('4. Générer Tests via Agent IA') {
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
                                if (!fileExists(file)) {
                                    echo "ℹ️  Fichier source introuvable (probablement supprimé): ${file}. Skip génération."
                                    return
                                }

                                // Lire le contenu du fichier
                                def fileContent = readFile(file)
                                
                                // Extraire le nom de classe (simple)
                                def className = file.tokenize('/').last().replace('.kt', '').replace('.java', '')
                                
                                echo "Classe détectée: ${className}"
                                
                                // Appeler l'Agent IA - AVEC RETRY AUTOMATIQUE
                                def response = ""
                                def maxRetries = 3
                                for (int attempt = 1; attempt <= maxRetries; attempt++) {
                                    response = sh(
                                        script: """
                                            curl -sS -X POST ${AGENT_IA_URL}/generate-tests \
                                            -H 'Content-Type: application/json' \
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
                                
                                // Parser JSON (sans readJSON - méthode manuelle)
                                if (response.contains('"success": true') || response.contains('"success":true')) {
                                    echo "✅ Test généré avec succès pour ${className}"
                                    
                                    // NOUVEAU: Extraire et sauvegarder le test
                                    try {
                                        // Extraire le code du test de la réponse JSON
                                        def startMarker = '"generated_tests":"'
                                        def endMarker = '","explanation"'
                                        
                                        def startIdx = response.indexOf(startMarker)
                                        def endIdx = response.indexOf(endMarker)
                                        
                                        if (startIdx != -1 && endIdx != -1) {
                                            startIdx += startMarker.length()
                                            def testCodeEscaped = response.substring(startIdx, endIdx)
                                            
                                            // Décoder les caractères échappés JSON
                                            def testCode = testCodeEscaped
                                                .replace('\\n', '\n')
                                                .replace('\\t', '\t')
                                                .replace('\\"', '"')
                                                .replace('\\\\', '\\')
                                            
                                            // Déterminer le chemin du fichier de test
                                            def testFileName = "${className}Test.kt"
                                            
                                            // Créer le chemin de test (remplacer main par test)
                                            def testFilePath = file.replace('/main/', '/test/')
                                                                   .replace('.kt', 'Test.kt')
                                                                   .replace('.java', 'Test.java')
                                            
                                            echo "📁 Création du fichier: ${testFilePath}"
                                            
                                            // Créer les dossiers si nécessaire
                                            sh "mkdir -p \$(dirname ${testFilePath})"
                                            
                                            // Écrire le fichier de test
                                            writeFile file: testFilePath, text: testCode
                                            
                                            echo "✅ Fichier test sauvegardé: ${testFilePath}"
                                            
                                            // Commit + push automatique du test vers GitHub (auth Jenkins)
                                            try {
                                                withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_TOKEN')]) {
                                                    sh """
                                                        set -e

                                                        git config user.name "Jenkins CI"
                                                        git config user.email "jenkins@ci.local"

                                                        # Sauvegarder le fichier généré avant changement éventuel de branche
                                                        git stash push -u -- "${testFilePath}" || true

                                                        # Se positionner sur main et synchroniser
                                                        git fetch origin main
                                                        git checkout main || git checkout -b main origin/main
                                                        git pull --rebase origin main || true

                                                        # Restaurer le fichier généré
                                                        git stash pop || true

                                                        git add "${testFilePath}"
                                                        if git diff --cached --quiet; then
                                                            echo "⚠️  Aucun changement à committer"
                                                            exit 0
                                                        fi

                                                        git commit -m "test: Add ${testFileName} (Auto-generated by Agent IA)"
                                                        echo "✅ Test commité automatiquement"

                                                        REMOTE_URL=\$(git config --get remote.origin.url)
                                                        AUTH_URL=\$(echo "\$REMOTE_URL" | sed "s#https://#https://${GIT_USERNAME}:${GIT_TOKEN}@#")
                                                        git push "\$AUTH_URL" main
                                                        echo "🚀 Test pushé vers GitHub"
                                                    """
                                                }
                                            } catch (Exception pushError) {
                                                echo "⚠️  Push impossible: ${pushError.message}"
                                                echo "💡 Le test est commité localement, push manuel possible"
                                            }
                                            
                                            // Extraire et afficher les métriques
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
                                            
                                        } else {
                                            echo "⚠️  Impossible d'extraire le code du test de la réponse"
                                        }
                                        
                                    } catch (Exception saveError) {
                                        echo "⚠️  Erreur lors de la sauvegarde: ${saveError.message}"
                                        echo "Le test a été généré mais pas sauvegardé automatiquement"
                                    }
                                    
                                } else {
                                    if (response.contains('"rate_limit_exceeded":true')) {
                                        echo "⚠️  Agent IA en rate-limit pour ${className}. Réessayez dans quelques minutes."
                                    } else {
                                        echo "⚠️  Génération échouée pour ${className}"
                                    }
                                }
                                
                            } catch (Exception e) {
                                echo "❌ Erreur lors de la génération pour ${file}: ${e.message}"
                            }
                        }
                    }
                }
            }
        }
        
        stage('5. Exécuter Tests Gradle') {
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
        
        stage('6. Vérifier Résultats Tests') {
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
        
        stage('7. Notifier Agent IA') {
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