// Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Distribution License v. 1.0, which is available at
// http://www.eclipse.org/org/documents/edl-v10.php.
//
// SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

// Job input parameters:
//  RELEASE                - Release switch
//  MAJOR_VERSION          - EclipseLink major version like 2.6 , 2.7 , 3.0
//  VERSION                - EclipseLink version. Must match with first part of NIGHTLY_BUILD_ID parameter like 2.7.5
//  NIGHTLY_BUILD_ID       - Nightly build ID in following form <version>-<classifier>-<git commit short id> .
//                           For example -  2.7.5.v20190614-4681d7f571
//                           Check directory with nightly builds.
//  RELEASE_CANDIDATE_ID   - Typical values are: RC1, RC2....
//  SIGN                   - Sing content of promoted jar files true - sign false - don't sign.
//  DEBUG                  - Show debug messages.


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
        memory: "2Gi"
        cpu: "1"
      requests:
        memory: "2Gi"
        cpu: "1"
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
        // Prepare and promote EclipseLink artifacts to oss.sonatype.org (staging) and to the Eclipse.org Milestone Builds area
        stage('Promote') {
            steps {
                container('el-build') {
                    git branch: '${GIT_BRANCH}', url: '${GIT_REPOSITORY_URL}'
                    sshagent(['projects-storage.eclipse.org-bot-ssh']) {
                        withCredentials([file(credentialsId: 'secret-subkeys.asc', variable: 'KEYRING')]) {
                            sh label: '', script: '''
                                gpg --batch --import "${KEYRING}"
                                for fpr in $(gpg --list-keys --with-colons  | awk -F: \'/fpr:/ {print $10}\' | sort -u);
                                do
                                    echo -e "5\\ny\\n" |  gpg --batch --command-fd 0 --expert --edit-key $fpr trust;
                                done'''
                        }
                        // Download selected nightly build from Eclipse.org Nightly Builds
                        sh """
                            mkdir -p ${HOME}/etc/jenkins
                            cp -r ${WORKSPACE}/etc/jenkins/* ${HOME}/etc/jenkins
                            cp ${WORKSPACE}/buildsystem/ant_customizations.jar ${HOME}/etc/jenkins
                            ${HOME}/etc/jenkins/promote_init.sh
                            """
                        // Prepare and promote EclipseLink artifacts to oss.sonatype.org (staging)
                        sh """
                            . /etc/profile
                            curl --version
                            echo ${RELEASE}
                            echo ${MAJOR_VERSION}
                            echo ${NIGHTLY_BUILD_ID}
                            echo ${RELEASE_CANDIDATE_ID}
                            echo ${MAJOR_VERSION}
                            echo ${SIGN}
                            echo ${DEBUG}
                            if [ ${RELEASE} == 'false' ]
                            then
                                echo calling "promote.sh ${NIGHTLY_BUILD_ID} ${RELEASE_CANDIDATE_ID} ${MAJOR_VERSION} ${SIGN} ${DEBUG}"
                                ${HOME}/etc/jenkins/promote.sh ${NIGHTLY_BUILD_ID} ${RELEASE_CANDIDATE_ID} ${MAJOR_VERSION} ${SIGN} ${DEBUG}
                            else
                                echo calling "promote.sh release ${RELEASE_CANDIDATE_ID} ${MAJOR_VERSION} ${SIGN} ${DEBUG}"
                                ${HOME}/etc/jenkins/promote.sh release ${RELEASE_CANDIDATE_ID} ${MAJOR_VERSION} ${SIGN} ${DEBUG}
                            fi
                        """
                        // Promote EclipseLink bundles to the Eclipse.org Milestone Builds area
                        sh """
                            echo ${RELEASE}
                            if [ ${RELEASE} == 'false' ]
                            then
                                ${HOME}/etc/jenkins/publish_milestone.sh
                            else
                                ${HOME}/etc/jenkins/publish_release.sh                            
                            fi
                            """
                    }
                }
            }
        }
    }
}