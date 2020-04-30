// Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Distribution License v. 1.0, which is available at
// http://www.eclipse.org/org/documents/edl-v10.php.
//
// SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

// Job input parameters (passed from Properties Content field from Jenkins job):
//  GIT_REPOSITORY_URL          - Git repository location (URL)
//  GIT_BRANCH                  - Git branch
//  SSH_CREDENTIALS_ID          - SSH credentials is used to access Git repository at the GitHub
//  BUILD_RESULTS_TARGET_DIR    - Location in the projects-storage.eclipse.org server for nightly builds (jar files)
//  TEST_RESULTS_TARGET_DIR     - Location in the projects-storage.eclipse.org server for nightly builds (test results)
//  TEST_DB_URL                 - Test database URL
//  TEST_DB_USERNAME            - Test database username
//  TEST_DB_PASSWORD            - Test databse password
//  CONTINUOUS_BUILD            - false - full nightly build with LRG and server tests and nightly build publish
//                                true - continuous build with SRG tests without publishing nightly build results


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

  - name: el-build
    resources:
      limits:
        memory: "12Gi"
        cpu: "6"
      requests:
        memory: "12Gi"
        cpu: "6"
    image: tkraus/el-build:1.1.8
    volumeMounts:
    - name: tools
      mountPath: /opt/tools
    - name: volume-known-hosts
      mountPath: /home/jenkins/.ssh      
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
    stages {
        // Initialize build environment
        stage('Init') {
            steps {
                container('el-build') {
                    git branch: '${GIT_BRANCH}', url: '${GIT_REPOSITORY_URL}'
                    sshagent(['SSH_CREDENTIALS_ID']) {
                        sh """
                            etc/jenkins/init.sh
                            """
                    }
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
                    sshagent(['SSH_CREDENTIALS_ID']) {
                        sh """
                            etc/jenkins/build.sh
                        """
                    }
                }
            }
        }
        // Publish to nightly
        stage('Publish to nightly') {
            steps {
                container('el-build') {
                    sshagent(['projects-storage.eclipse.org-bot-ssh']) {
                        sh """
                            etc/jenkins/publish_nightly.sh
                            """
                    }
                }
            }
        }
        // Proceed test results
        stage('Proceed test results') {
            steps {
                script {
                    //Multiple Jenkins junit plugin calls due java.nio.channels.ClosedChannelException in new/cloud Eclipse.org build infrastructure if it's called once
                    //Retry is there to try (in case of crash) junit test upload again.
                    retryCount = 5
                    junitReportFiles = [
                            'dbws/**/reports/**/TESTS-TestSuites.xml',
                            'foundation/**/reports/**/TESTS-TestSuites.xml',
                            'jpa/**/reports/**/TESTS-TestSuites.xml',
                            'moxy/**/reports/installer/TESTS-TestSuites.xml',
                            'moxy/**/reports/jaxb/TESTS-TestSuites.xml',
                            'moxy/**/reports/oxm/TESTS-TestSuites.xml',
                            'moxy/**/reports/srg/TESTS-TestSuites.xml',
                            'sdo/**/reports/**/TESTS-TestSuites.xml',
                            'utils/**/reports/**/TESTS-TestSuites.xml'
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
}