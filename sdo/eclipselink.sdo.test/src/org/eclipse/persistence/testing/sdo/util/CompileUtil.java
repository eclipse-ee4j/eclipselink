/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.testing.sdo.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;

import org.eclipse.persistence.internal.helper.JavaSEPlatform;

public class CompileUtil {

    private static CompileUtil _instance;

    private CompileUtil() {
    }

    public static synchronized CompileUtil instance() {
        if (_instance == null) {
            _instance = new CompileUtil();
        }
        return _instance;
    }

    public int compile(String classpath, Object[] javaFiles) {
        int jv = JavaSEPlatform.CURRENT.getMajor();
        final String javaVersion = "" + ((jv >= 9) ? jv : JavaSEPlatform.CURRENT.toString());
        final String[] args = new String[javaFiles.length + ((jv >= 9) ? 9 : 7)];
        final String javac = getJavaC();

        args[0] = javac;
        args[1] = "-cp";
        args[2] = classpath;
        args[3] = "-source";
        args[4] = javaVersion;
        args[5] = "-target";
        args[6] = javaVersion;
        if (jv >= 9) {
            args[7] = "--add-modules";
            args[8] = "java.activation";
            System.arraycopy(javaFiles, 0, args, 9, javaFiles.length);
        } else {
           System.arraycopy(javaFiles, 0, args, 7, javaFiles.length);
        }

        int exitVal = -1;

        try {
            Process proc = Runtime.getRuntime().exec(args);
            InputStream stderr = proc.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            if (line != null) {
                System.out.println("<ERROR>");
                while (line != null) {
                    System.out.println(line);
                    if ((line = br.readLine()) == null) {
                        System.out.println("</ERROR>");
                    }
                }
            }

            exitVal = proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return exitVal;
    }

    private String getJavaC() {
        String javaHome = System.getenv("JAVA_HOME");
        if (null != javaHome) {
            StringBuilder javacBuilder = new StringBuilder(35);
            javacBuilder.append(javaHome);
            if (!javaHome.endsWith(File.separator)) {
                javacBuilder.append(File.separator);
            }
            javacBuilder.append("bin");
            javacBuilder.append(File.separator);
            javacBuilder.append("javac");
            return javacBuilder.toString();
        }
        return "javac";
    }

    public int compileOld(String classpath, Object[] javaFiles) {
        String[] args = new String[javaFiles.length + 2];
        args[0] = "-cp";
        args[1] = classpath;
        System.arraycopy(javaFiles, 0, args, 2, javaFiles.length);

        return -1;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
