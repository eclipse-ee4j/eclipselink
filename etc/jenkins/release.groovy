// Copyright (c) 2020, 2023 Oracle and/or its affiliates. All rights reserved.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Distribution License v. 1.0, which is available at
// http://www.eclipse.org/org/documents/edl-v10.php.
//
// SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

// Job input parameters:
//  GIT_REPOSITORY_URL     - Git repository location (URL)
//  GIT_BRANCH             - Git branch
//  RELEASE_VERSION        - Version to release
//  NEXT_VERSION           - Next snapshot version to set (e.g. 3.0.1-SNAPSHOT).
//  DRY_RUN                - Do not publish artifacts to OSSRH and code changes to GitHub.
//  OVERWRITE_GIT          - Allows to overwrite existing version in git
//  OVERWRITE_STAGING      - Allows to overwrite existing version in OSSRH (Jakarta) staging repositories


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
        memory: "2Gi"
        cpu: "1"
      requests:
        memory: "2Gi"
        cpu: "500m"
  - name: el-build
    resources:
      limits:
        memory: "4Gi"
        cpu: "2"
      requests:
        memory: "4Gi"
        cpu: "1.5"
    image: rfelcman/el-build:2.0.3
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
    environment {
        LANG = 'en_US.UTF-8'
    }
    tools {
        maven 'apache-maven-latest'
        jdk 'openjdk-jdk21-latest'
    }
    stages {

        // Prepare and promote EclipseLink artifacts to oss.sonatype.org (staging) and to the Eclipse.org Milestone Builds area
        // Initialize release/build environment
        stage('Init') {
            steps {
                container('el-build') {
                    git branch: GIT_BRANCH_RELEASE, credentialsId: SSH_CREDENTIALS_ID, url: GIT_REPOSITORY_URL
                    sh """
                        # Directory for JEE server binaries (WildFly, Glassfish)
                        # Maven build automatically download and unpack them.
                        mkdir ~/.eclipselinktests
                    """
                    withCredentials([file(credentialsId: 'secret-subkeys.asc', variable: 'KEYRING')]) {
                        sh label: '', script: '''
                            gpg --batch --import "${KEYRING}"
                            for fpr in $(gpg --list-keys --with-colons  | awk -F: \'/fpr:/ {print $10}\' | sort -u);
                            do
                                echo -e "5\\ny\\n" |  gpg --batch --command-fd 0 --expert --edit-key $fpr trust;
                            done'''
                    }
                    // Git configuration
                    sh '''
                    git config --global user.name "${GIT_USER_NAME}"
                    git config --global user.email "${GIT_USER_EMAIL}"
                '''
                }
            }
        }

        // Build and release EclipseLink by release.sh script
        stage('Build and release EclipseLink') {
            steps {
                git branch: GIT_BRANCH_RELEASE, credentialsId: SSH_CREDENTIALS_ID, url: GIT_REPOSITORY_URL
                sshagent([SSH_CREDENTIALS_ID]) {
                    container('el-build') {
                        sh """
                            etc/jenkins/release.sh "${RELEASE_VERSION}" "${NEXT_VERSION}" "${DRY_RUN}" "${OVERWRITE_GIT}" "${OVERWRITE_STAGING}"
                        """
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