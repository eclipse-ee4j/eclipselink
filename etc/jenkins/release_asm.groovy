// Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Distribution License v. 1.0, which is available at
// http://www.eclipse.org/org/documents/edl-v10.php.
// or the Eclipse Distribution License v. 1.0 which is available at
// http://www.eclipse.org/org/documents/edl-v10.php.
//
//  SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

// Job input parameters:
//   ASM_VERSION       - EclipseLink ASM version to release
//   NEXT_ASM_VERSION  - Next EclipseLink ASM snapshot version to set (e.g. 1.2.4-SNAPSHOT)
//   BRANCH            - Branch to release
//   DRY_RUN           - Do not publish artifacts to OSSRH and code changes to GitHub
//   OVERWRITE         - Allows to overwrite existing version in git and OSSRH staging repositories

// Job internal argumets:
//   GIT_USER_NAME       - Git user name (for commits)
//   GIT_USER_EMAIL      - Git user e-mail (for commits)
//   GIT_BRANCH_RELEASE  - Git branch to release
//   SSH_CREDENTIALS_ID  - Jenkins ID of SSH credentials
//   GPG_CREDENTIALS_ID  - Jenkins ID of GPG credentials (stored as KEYRING variable)

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
        memory: "1Gi"
        cpu: "1"
      requests:
        memory: "1Gi"
        cpu: "500m"
  - name: el-build
    resources:
      limits:
        memory: "2Gi"
        cpu: "2"
      requests:
        memory: "2Gi"
        cpu: "1.5"
    image: tkraus/el-build:1.1.9
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

    tools {
        jdk 'openjdk-jdk11-latest'
        maven 'apache-maven-latest'
    }

    environment {
        ASM_DIR="${WORKSPACE}/plugins/org.eclipse.persistence.asm"
    }

    stages {
        // Initialize build environment
        stage('Init') {
            steps {
                container('el-build') {
                    git branch: GIT_BRANCH_RELEASE, credentialsId: SSH_CREDENTIALS_ID, url: GIT_REPOSITORY_URL
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
        // Perform release
        stage('Build and release EclipseLink ASM') {
            steps {
                container('el-build') {
                    git branch: GIT_BRANCH_RELEASE, credentialsId: SSH_CREDENTIALS_ID, url: GIT_REPOSITORY_URL
                    sshagent([SSH_CREDENTIALS_ID]) {
                        sh '''
                            etc/jenkins/release_asm.sh "${ASM_VERSION}" "${NEXT_ASM_VERSION}" "${DRY_RUN}" "${OVERWRITE}"
                        '''
                    }
                }
            }
        }
    }

}
