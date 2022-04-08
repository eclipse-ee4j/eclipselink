//
//  Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
//
//  This program and the accompanying materials are made available under the
//  terms of the Eclipse Public License v. 2.0 which is available at
//  http://www.eclipse.org/legal/epl-2.0,
//  or the Eclipse Distribution License v. 1.0 which is available at
//  http://www.eclipse.org/org/documents/edl-v10.php.
//
//  SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
//

// Job input parameters:
//  - ECLIPSELINK_VERSION
//  - JPA_TCK_URL
//  - JDBC_BUNDLE_URL

pipeline {

    agent {
        kubernetes {
            label 'eclipselink-tck-run-pod'
            yaml """
apiVersion: v1
kind: Pod
spec:

  volumes:
  - name: workspace-volume
    emptyDir: {}
  - name: m2-repo
    emptyDir: {}
  - name: tools
    persistentVolumeClaim:
      claimName: tools-claim-jiro-eclipselink
  - name: volume-known-hosts
    configMap:
      name: known-hosts

  containers:
  - name: jnlp
    resources:
      limits:
        memory: "1Gi"
        cpu: "1"
      requests:
        memory: "1Gi"
        cpu: "500m"
  - name: eclipselink-tck-run
    image:  tkraus/el-build:2.0.0-EA3
    resources:
      limits:
        memory: "3Gi"
        cpu: "3"
      requests:
        memory: "3Gi"
        cpu: "2.5"
    volumeMounts:
    - name: tools
      mountPath: /opt/tools
    - name: volume-known-hosts
      mountPath: /home/jenkins/.ssh
    - mountPath: "/home/jenkins"
      name: workspace-volume
      readOnly: false
    - name: m2-repo
      mountPath: /home/jenkins/.m2/repository
    tty: true
    command:
    - cat
"""
        }
    }

    stages {
        stage('Init') {
            steps {
                container('eclipselink-tck-run') {
                    sh label: 'Init', script: 'eclipselink/etc/jenkins/tck_init.sh'
                }
            }
        }
        stage('Run') {
            steps {
                container('eclipselink-tck-run') {
                  sh label: 'Run', script: 'eclipselink/etc/jenkins/tck_run.sh'
                }
            }
        }
    }

}
