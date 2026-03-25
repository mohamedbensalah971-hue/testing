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
        PYTHON_VENV = 'C:\\Users\\Stayha\\Downloads\\agent-ia-server-phase1\\agent-ia-server\\venv'
    }

    stages {

        stage('1. Checkout Code') {
            steps {
                echo 'Recuperation du code depuis GitHub...'
                checkout scm

                script {
                    // Ensure we are on a real local branch, not detached HEAD.
                    sh 'git checkout -B main origin/main'

                    try {
                        env.CHANGED_FILES = sh(
                            script: 'git show --pretty="" --name-only HEAD || echo ""',
                            returnStdout: true
                        ).trim()

                        echo "Fichiers modifies: ${env.CHANGED_FILES}"
                    } catch (Exception e) {
                        echo "Premier build, pas de diff disponible"
                        env.CHANGED_FILES = ""
                    }
                }
            }
        }

        stage('2. Analyser Fichiers Modifies') {
            steps {
                echo 'Analyse des fichiers Java/Kotlin modifies...'

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
                            echo "Fichiers source detectes: ${sourceFiles}"
                        } else {
                            echo "Aucun fichier source modifie"
                            env.SOURCE_FILES = ""
                        }
                    } else {
                        echo "Aucun fichier modifie detecte"
                        env.SOURCE_FILES = ""
                    }
                }
            }
        }

        stage('3. Generer Tests via Agent IA') {
            when {
                expression { env.SOURCE_FILES != "" && env.SOURCE_FILES != null }
            }
            steps {
                echo 'Generation des tests via Agent IA...'

                script {
                    env.SOURCE_FILES.split('\n').each { file ->
                        if (file.trim()) {
                            echo "Generation test pour: ${file}"

                            try {
                                def fileContent = readFile(file)
                                def className = file.tokenize('/').last().replace('.kt', '').replace('.java', '')

                                echo "Classe detectee: ${className}"

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
                                        echo "Agent IA rate-limited (tentative ${attempt}/${maxRetries}), retry dans ${waitSeconds}s..."
                                        sleep(time: waitSeconds, unit: 'SECONDS')
                                    }
                                }

                                echo "Reponse Agent IA: ${response}"

                                if (response.contains('"success": true') || response.contains('"success":true')) {
                                    echo "Test genere avec succes pour ${className}"

                                    try {
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

                                            def testFileName = "${className}Test.kt"
                                            def testFilePath = file.replace('/main/', '/test/')
                                                           .replace('.kt', 'Test.kt')
                                                           .replace('.java', 'Test.java')

                                            echo "Creation du fichier: ${testFilePath}"

                                            sh "mkdir -p \$(dirname ${testFilePath})"
                                            writeFile file: testFilePath, text: testCode

                                            echo "Fichier test sauvegarde: ${testFilePath}"

                                            // Commit only if staged diff exists.
                                            sh """
                                                set -e
                                                git add ${testFilePath}
                                                git config user.name "Jenkins CI"
                                                git config user.email "jenkins@ci.local"
                                                if git diff --cached --quiet; then
                                                  echo "Aucun changement a committer"
                                                else
                                                  git commit -m "test: Add ${testFileName} (Auto-generated by Agent IA)"
                                                fi
                                            """

                                            // Logging checkpoint after commit.
                                            sh '''
                                                echo "Checkpoint git apres commit:"
                                                git status --short
                                                git log -1 --oneline || true
                                            '''

                                            // Rebase before push to reduce non-fast-forward failures.
                                            sh 'git pull --rebase origin main'

                                            // Explicit authenticated push. Fail build if push fails.
                                            withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PAT')]) {
                                                sh '''
                                                    set -e
                                                    git remote set-url origin https://${GIT_USER}:${GIT_PAT}@github.com/mohamedbensalah971/testing.git
                                                    git push origin main
                                                '''
                                            }

                                            // Logging checkpoint after push.
                                            sh '''
                                                echo "Checkpoint git apres push:"
                                                git ls-remote --heads origin main
                                            '''

                                            echo "Test pousse vers GitHub"

                                            if (response.contains('"confidence":')) {
                                                def confMatch = (response =~ /"confidence":([0-9.]+)/)
                                                if (confMatch) {
                                                    echo "Confiance: ${confMatch[0][1]}"
                                                }
                                            }

                                            if (response.contains('"tokens_used":')) {
                                                def tokensMatch = (response =~ /"tokens_used":([0-9]+)/)
                                                if (tokensMatch) {
                                                    echo "Tokens utilises: ${tokensMatch[0][1]}"
                                                }
                                            }

                                            if (response.contains('"rag_context_used":')) {
                                                def ragMatch = (response =~ /"rag_context_used":(true|false)/)
                                                if (ragMatch) {
                                                    echo "RAG utilise: ${ragMatch[0][1]}"
                                                }
                                            }

                                        } else {
                                            error("Impossible d'extraire le code du test de la reponse")
                                        }

                                    } catch (Exception saveError) {
                                        error("Erreur lors de la sauvegarde/commit/push: ${saveError.message}")
                                    }

                                } else {
                                    if (response.contains('"rate_limit_exceeded":true')) {
                                        error("Agent IA en rate-limit pour ${className}. Reessayez dans quelques minutes.")
                                    } else {
                                        error("Generation echouee pour ${className}")
                                    }
                                }

                            } catch (Exception e) {
                                error("Erreur lors de la generation pour ${file}: ${e.message}")
                            }
                        }
                    }
                }
            }
        }

        stage('4. Executer Tests Gradle') {
            steps {
                echo 'Execution des tests unitaires...'

                script {
                    def gradlewExists = fileExists('./gradlew') || fileExists('gradlew.bat')

                    if (gradlewExists) {
                        echo "Gradle wrapper trouve"

                        if (isUnix()) {
                            sh '''
                                set -e
                                chmod +x ./gradlew
                                ./gradlew test --no-daemon
                            '''
                        } else {
                            bat 'gradlew.bat test --no-daemon'
                        }

                        echo "Tests executes"
                    } else {
                        // Keep pipeline successful but explicit.
                        currentBuild.result = 'UNSTABLE'
                        echo "Gradle wrapper non trouve, tests non executes"
                        echo "Pour activer: ajouter gradlew, gradlew.bat, gradle/wrapper/gradle-wrapper.jar, gradle/wrapper/gradle-wrapper.properties"
                    }
                }
            }
        }

        stage('5. Verifier Resultats Tests') {
            steps {
                echo 'Analyse des resultats de tests...'
                script {
                    // JUnit publisher: does not fail build when no reports.
                    junit testResults: '**/build/test-results/test/*.xml', allowEmptyResults: true
                    echo "Publication JUnit terminee"
                }
            }
        }

        stage('6. Notifier Agent IA') {
            steps {
                echo 'Notification Agent IA...'
                script {
                    // Optional webhook: if missing endpoint, mark UNSTABLE (visible), do not fail full pipeline.
                    catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                        sh """
                            set -e
                            curl -f -sS -X POST ${AGENT_IA_URL}/webhook/jenkins-auto \
                            -H 'Content-Type: application/json' \
                            -d '{
                                "build_number": "${env.BUILD_NUMBER}",
                                "status": "${currentBuild.currentResult}",
                                "project": "${env.JOB_NAME}",
                                "timestamp": "${new Date()}"
                            }'
                        """
                        echo "Notification Agent IA envoyee"
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline termine avec succes'
            echo "Build #${env.BUILD_NUMBER}"
            echo "Duree: ${currentBuild.durationString}"
        }

        failure {
            echo 'Pipeline echoue'
            echo "Build #${env.BUILD_NUMBER}"
        }

        unstable {
            echo 'Pipeline instable'
        }

        always {
            echo 'Nettoyage...'
            archiveArtifacts artifacts: '**/build/reports/**', allowEmptyArchive: true
        }
    }
}