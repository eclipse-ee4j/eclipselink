//
//  Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
//
//  This program and the accompanying materials are made available under the
//  terms of the Eclipse Public License v. 2.0 which is available at
//  http://www.eclipse.org/legal/epl-2.0,
//  or the Eclipse Distribution License v. 1.0 which is available at
//  http://www.eclipse.org/org/documents/edl-v10.php.
//
//  SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
//

// Job input parameters (passed from Properties Content field from Jenkins job):
//  GIT_REPOSITORY_URL          - Git repository location (URL)
//  GIT_BRANCH                  - Git branch
//  SSH_CREDENTIALS_ID          - SSH credentials is used to access Git repository at the GitHub
//  BUILD_RESULTS_TARGET_DIR    - Location in the projects-storage.eclipse.org server for nightly builds (jar files and test results)
//  CONTINUOUS_BUILD            - false - full nightly build with LRG and server tests and nightly build publish
//                                true - continuous build with SRG tests without publishing nightly build results
//  NOTIFICATION_ADDRESS        - E-Mail address where Jenkins job send notification in case of failure or return back to normal


pipeline {
    agent {
        kubernetes {
            label 'el-master-agent-pod'
            yaml """
apiVersion: v1
kind: Pod
spec:

  volumes:
  - name: tools
    persistentVolumeClaim:
      claimName: tools-claim-jiro-eclipselink
  - name: volume-known-hosts
    configMap:
      name: known-hosts      
  - name: settings-xml
    secret:
      secretName: m2-secret-dir
      items:
      - key: settings.xml
        path: settings.xml
  - name: toolchains-xml
    configMap:
      name: m2-dir
      items:
      - key: toolchains.xml
        path: toolchains.xml
  - name: settings-security-xml
    secret:
      secretName: m2-secret-dir
      items:
      - key: settings-security.xml
        path: settings-security.xml
  - name: m2-repo
    emptyDir: {}
    
  containers:
  - name: jnlp
    resources:
      limits:
        memory: "4Gi"
        cpu: "2"
      requests:
        memory: "4Gi"
        cpu: "1"
    volumeMounts:
    - name: volume-known-hosts
      mountPath: /home/jenkins/.ssh    
  - name: el-build
    resources:
      limits:
        memory: "12Gi"
        cpu: "6"
      requests:
        memory: "12Gi"
        cpu: "5.5"
    image: tkraus/el-build:1.1.9
    volumeMounts:
    - name: tools
      mountPath: /opt/tools
    - name: settings-xml
      mountPath: /home/jenkins/.m2/settings.xml
      subPath: settings.xml
      readOnly: true
    - name: toolchains-xml
      mountPath: /home/jenkins/.m2/toolchains.xml
      subPath: toolchains.xml
      readOnly: true
    - name: settings-security-xml
      mountPath: /home/jenkins/.m2/settings-security.xml
      subPath: settings-security.xml
      readOnly: true
    - name: m2-repo
      mountPath: /home/jenkins/.m2/repository
    tty: true
    command:
    - cat
"""
        }
    }
    tools {
        maven 'apache-maven-latest'
        jdk 'adoptopenjdk-hotspot-jdk11-latest'
    }
    stages {
        // Initialize build environment
        stage('Init') {
            steps {
                container('el-build') {
                    git branch: '${GIT_BRANCH}', url: '${GIT_REPOSITORY_URL}'
                    sh """
                        etc/jenkins/init.sh
                    """
                    withCredentials([file(credentialsId: 'secret-subkeys.asc', variable: 'KEYRING')]) {
                        sh label: '', script: '''
                            gpg --batch --import "${KEYRING}"
                            for fpr in $(gpg --list-keys --with-colons  | awk -F: \'/fpr:/ {print $10}\' | sort -u);
                            do
                                echo -e "5\\ny\\n" |  gpg --batch --command-fd 0 --expert --edit-key $fpr trust;
                            done'''
                    }
                }
            }
        }
        // Build
        stage('Build') {
            steps {
                container('el-build') {
                    sh """
                        etc/jenkins/build.sh
                    """
                }
            }
        }
        // LRG tests
        stage('Tests') {
            steps {
                container('el-build') {
                    sh """
                            etc/jenkins/test.sh
                        """
                }
            }
        }
        // NoSQL tests
        stage('NoSQL tests') {
            steps {
                container('el-build') {
                    sh """
                            etc/jenkins/test_nosql.sh
                        """
                }
            }
        }
        // LRG Server tests
        stage('LRG Server tests (Wildfly)') {
            steps {
                container('el-build') {
                    sh """
                            etc/jenkins/test_server.sh
                        """
                }
            }
        }
        // Publish to nightly
        stage('Publish to nightly') {
            steps {
                sshagent(['projects-storage.eclipse.org-bot-ssh']) {
                    sh """
                        etc/jenkins/publish_nightly.sh
                    """
                }
            }
        }
        // Publish to snapshots
        stage('Publish to snapshots') {
            steps {
                container('el-build') {
                    sh """
                        etc/jenkins/publish_snapshots.sh
                    """
                }
            }
        }
        stage('Proceed test results') {
            steps {
                script {
                    //Multiple Jenkins junit plugin calls due java.nio.channels.ClosedChannelException in new/cloud Eclipse.org build infrastructure if it's called once
                    //Retry is there to try (in case of crash) junit test upload again.
                    retryCount = 5
                    junitReportFiles = [
                            'bundles/**/target/surefire-reports/*.xml,bundles/**/target/failsafe-reports/*.xml',
                            'dbws/**/target/surefire-reports/*.xml,dbws/**/target/failsafe-reports/*.xml',
                            'foundation/**/target/surefire-reports/*.xml,foundation/**/target/failsafe-reports/*.xml',
                            'jpa/**/target/surefire-reports/*.xml,jpa/**/target/failsafe-reports/*.xml',
                            'moxy/**/target/surefire-reports/*.xml,moxy/**/target/failsafe-reports/*.xml',
                            'sdo/**/target/surefire-reports/*.xml,sdo/**/target/failsafe-reports/*.xml',
                            'utils/**/target/surefire-reports/*.xml,utils/**/target/failsafe-reports/*.xml'
                    ]
                    for (item in junitReportFiles) {
                        echo 'Processing file: ' + item
                        retry(retryCount) {
                            junit allowEmptyResults: true, testResults: item
                        }
                    }
                }
            }
        }
    }
    post {
        // Send a mail on unsuccessful and fixed builds
        unsuccessful { // means unstable || failure || aborted
            emailext subject: 'Build $BUILD_STATUS $PROJECT_NAME #$BUILD_NUMBER failed!',
                    body: '''Check console output at $BUILD_URL to view the results.''',
                    recipientProviders: [culprits(), requestor()],
                    to: '${NOTIFICATION_ADDRESS}'
        }
        fixed { // back to normal
            emailext subject: 'Build $BUILD_STATUS $PROJECT_NAME #$BUILD_NUMBER is back to normal!',
                    body: '''Check console output at $BUILD_URL to view the results.''',
                    recipientProviders: [culprits(), requestor()],
                    to: '${NOTIFICATION_ADDRESS}'
        }
    }
}
