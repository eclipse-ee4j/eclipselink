//
//  Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
//
//  This program and the accompanying materials are made available under the
//  terms of the Eclipse Public License v. 2.0 which is available at
//  http://www.eclipse.org/legal/epl-2.0,
//  or the Eclipse Distribution License v. 1.0 which is available at
//  http://www.eclipse.org/org/documents/edl-v10.php.
//
//  SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
//

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
        maven 'apache-maven-latest'
        jdk 'adoptopenjdk-hotspot-jdk11-latest'
    }
    stages {
        // Initialize build environment
        stage('Init') {
            steps {
                container('el-build') {
                    sh """
                        /opt/bin/mysql-start.sh
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
                }
            }
        }
        // Build
        stage('Build') {
            steps {
                container('el-build') {
                    sh """
                        echo '-[ EclipseLink Build ]-----------------------------------------------------------'
                        mvn -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -B clean install -pl '!:eclipselink,!:org.eclipse.persistence.bundles.other,!:org.eclipse.persistence.distribution.tests,!:p2site' -DskipTests
                    """
                }
            }
        }
        // LRG tests
        stage('Run Tests') {
            parallel {
                stage('Core LRG') {
                    steps {
                        container('el-build') {
                            sh """
                                mvn verify -pl :org.eclipse.persistence.core.test -P test-core-lrg,mysql
                            """
                        }
                    }
                }
                stage('MOXy LRG') {
                    steps {
                        container('el-build') {
                            sh """
                                mvn test -pl :org.eclipse.persistence.moxy -P test-moxy-lrg
                            """
                        }
                    }
                }
                stage('NoSQL Tests') {
                    steps {
                        container('el-build') {
                            sh """
                                /opt/bin/mongo-start.sh
                                mvn verify -pl :org.eclipse.persistence.nosql -P mongodb
                                /opt/bin/mongo-stop.sh                                
                            """
                        }
                    }
                }
            }
        }
        //Not in parallel
        stage('JPA LRG') {
            steps {
                container('el-build') {
                    sh """
                                mvn verify -pl :org.eclipse.persistence.jpa.test -P test-jpa-lrg,mysql
                            """
                }
            }
        }
        stage('SDO LRG') {
            steps {
                container('el-build') {
                    sh """
                                mvn verify -pl :org.eclipse.persistence.sdo -Ptest-sdo
                            """
                }
            }
        }
        stage('CORBA') {
            steps {
                container('el-build') {
                    sh """
                                mvn verify -pl :org.eclipse.persistence.corba -P mysql
                            """
                }
            }
        }
        stage('JPA Modelgen, JPA JSE, WDF, JPARS, DBWS, DBWS Builder, Distribution') {
            steps {
                container('el-build') {
                    sh """
                                mvn clean install -pl :eclipselink
                                mvn verify -pl :org.eclipse.persistence.jpa.modelgen.processor,:org.eclipse.persistence.jpa.jse.test,:org.eclipse.persistence.extension,:org.eclipse.persistence.jpa.jpql,:org.eclipse.persistence.jpa.wdf.test,:org.eclipse.persistence.jpars,:org.eclipse.persistence.dbws,:org.eclipse.persistence.dbws.builder,:eclipselink,:org.eclipse.persistence.distribution.tests -P mysql;
                            """
                }
            }
        }
        stage('Javadoc') {
            steps {
                container('el-build') {
                    sh """
                                mvn package -DskipTests -Poss-release
                            """
                }
            }
        }
        // LRG Server tests
/*
        stage('LRG Server tests (Wildfly)') {
            steps {
                container('el-build') {
                    sh """
                            etc/jenkins/test_server.sh
                        """
                }
            }
        }
*/
    }
    post {
        always {
            container('el-build') {
                sh """
                        /opt/bin/mysql-stop.sh
                    """
            }
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