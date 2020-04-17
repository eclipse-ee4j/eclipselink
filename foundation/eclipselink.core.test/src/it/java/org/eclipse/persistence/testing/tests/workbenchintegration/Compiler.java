/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.eclipse.persistence.sessions.Session;

/**
 *
 * @author lukas
 */
final class Compiler {

    public static boolean compile(String... source) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        StandardJavaFileManager sfm = compiler.getStandardFileManager(diagnostics, null, null);
        URL apiUrl = Session.class.getProtectionDomain().getCodeSource().getLocation();
        URL generatedUrl = Compiler.class.getProtectionDomain().getCodeSource().getLocation();
        sfm.setLocation(StandardLocation.CLASS_PATH, Arrays.asList(new File(apiUrl.getFile()), new File(generatedUrl.getFile())));

        JavaCompiler.CompilationTask task = compiler.getTask(new PrintWriter(System.out), sfm, diagnostics,
                null, null, sfm.getJavaFileObjects(source));
        boolean result = task.call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.println(diagnostic);
        }
        return result;
    }

}
