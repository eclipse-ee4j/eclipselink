//  Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
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
        memory: "6Gi"
        cpu: "3"
      requests:
        memory: "6Gi"
        cpu: "3"
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
        ANT_HOME="${env.HOME}/apache-ant-1.10.7"
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
                        mkdir $HOME/extension.lib.external
                        wget -nc https://repo1.maven.org/maven2/junit/junit/4.12/junit-4.12.jar -O $HOME/extension.lib.external/junit-4.12.jar
                        wget -nc https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar -O $HOME/extension.lib.external/hamcrest-core-1.3.jar
                        wget -nc https://repo1.maven.org/maven2/org/jmockit/jmockit/1.35/jmockit-1.35.jar -O $HOME/extension.lib.external/jmockit-1.35.jar
                        wget -nc https://repo1.maven.org/maven2/org/jboss/logging/jboss-logging/3.4.1.Final/jboss-logging-3.4.1.Final.jar -O $HOME/extension.lib.external/jboss-logging-3.4.1.Final.jar
                        wget -nc https://repo1.maven.org/maven2/org/glassfish/javax.el/3.0.1-b08/javax.el-3.0.1-b08.jar -O $HOME/extension.lib.external/javax.el-3.0.1-b08.jar
                        wget -nc https://repo1.maven.org/maven2/com/fasterxml/classmate/1.5.1/classmate-1.5.1.jar -O $HOME/extension.lib.external/classmate-1.5.1.jar
                        wget -nc https://archive.apache.org/dist/ant/binaries/apache-ant-1.10.7-bin.tar.gz -O $HOME/extension.lib.external/apache-ant-1.10.7-bin.tar.gz
                        wget -nc https://download.eclipse.org/eclipse/downloads/drops4/R-4.10-201812060815/eclipse-SDK-4.10-linux-gtk-x86_64.tar.gz -O $HOME/extension.lib.external/eclipse-SDK-4.10-linux-gtk-x86_64.tar.gz
                        wget -nc https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar -O $HOME/extension.lib.external/mysql-connector-java.jar
                        tar -x -z -C $HOME -f $HOME/extension.lib.external/apache-ant-1.10.7-bin.tar.gz
                        tar -x -z -C $HOME/extension.lib.external -f $HOME/extension.lib.external/eclipse-SDK-4.10-linux-gtk-x86_64.tar.gz
                    """
                    withCredentials([file(credentialsId: 'secret-subkeys.asc', variable: 'KEYRING')]) {
                        sh label: '', script: '''
                            gpg --batch --import "${KEYRING}"
                            for fpr in $(gpg --list-keys --with-colons  | awk -F: \'/fpr:/ {print $10}\' | sort -u);
                            do
                                echo -e "5\\ny\\n" |  gpg --batch --command-fd 0 --expert --edit-key $fpr trust;
                            done'''
                    }
                    sh """
                        echo "extensions.depend.dir=$HOME/extension.lib.external" >> $HOME/build.properties
                        echo "junit.lib=$HOME/extension.lib.external/junit-4.12.jar:$HOME/extension.lib.external/hamcrest-core-1.3.jar" >> $HOME/build.properties
                        echo "jdbc.driver.jar=$HOME/extension.lib.external/mysql-connector-java.jar" >> $HOME/build.properties
                        echo 'db.driver=com.mysql.cj.jdbc.Driver' >> $HOME/build.properties
                        echo 'db.url=jdbc:mysql://localhost/ecltests?allowPublicKeyRetrieval=true' >> $HOME/build.properties
                        echo 'db.user=root' >> $HOME/build.properties
                        echo 'db.pwd=root' >> $HOME/build.properties
                        echo 'db.platform=org.eclipse.persistence.platform.database.MySQLPlatform' >> $HOME/build.properties
                        echo "eclipse.install.dir=$HOME/extension.lib.external/eclipse" >> $HOME/build.properties
                    """
                }
            }
        }
        // Build
        stage('Build') {
            steps {
                container('el-build') {
                    sh """
                        echo '-[ EclipseLink Build ]-----------------------------------------------------------'
                        $ANT_HOME/bin/ant -f antbuild.xml build
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
                                echo '-[ EclipseLink Core LRG ]-----------------------------------------------------------'
                                $ANT_HOME/bin/ant -f antbuild.xml -Dfail.on.error=true test-core
                            """
                        }
                    }
                }
                stage('MOXy LRG') {
                    steps {
                        container('el-build') {
                            sh """
                                $ANT_HOME/bin/ant -f antbuild.xml -Dfail.on.error=true test-moxy
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
                        $ANT_HOME/bin/ant -f antbuild.xml -Dfail.on.error=true test-jpa22
                    """
                }
            }
        }
        stage('SDO LRG') {
            steps {
                container('el-build') {
                    sh """
                        $ANT_HOME/bin/ant -f antbuild.xml -Dfail.on.error=true test-sdo
                    """
                }
            }
        }
        stage('JPA Modelgen, JPA JSE, WDF, JPARS, DBWS, DBWS Builder') {
            steps {
                container('el-build') {
                    sh """
                        $ANT_HOME/bin/ant -f antbuild.xml -Dfail.on.error=true test-jpa-jse test-ext test-jpql test-wdf test-jpars test-dbws test-dbws-builder test-osgi
                    """
                }
            }
        }
        stage('Distribution') {
            steps {
                container('el-build') {
                    sh """
                        $ANT_HOME/bin/ant -f antbuild.xml -Dfail.on.error=true build-distribution
                    """
                }
            }
        }
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