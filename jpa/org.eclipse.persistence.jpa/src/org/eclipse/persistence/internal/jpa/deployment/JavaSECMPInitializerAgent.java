/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.deployment;

import java.lang.instrument.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * This agent is intended to be run prior to start up on a CMP3 JavaSE application.
 * It gets the globalInstrumentation and makes it available to EclipseLink's initialization code.
 * There are two kinds of initialization.  Normally, initialization occurs through reflective
 * creation and invocation of EclipseLink JavaSECMPInitializer.
 * It is possible to run it with the "main" argument to the agent in which case it will simply
 * try to set the globalInstrumentation field on the JavaSECMPInitializer.  This type of initialization
 * is useful when debugging, but imposes some restrictions on the user.  One such restriction is
 * that no domain classes that use lazy loading may be references in any way other than reflective in the application
 */
public class JavaSECMPInitializerAgent {
    public static void premain(String agentArgs, Instrumentation instr) throws Throwable {
        // Reflection allows:
        //  JavaSECMPInitializerAgent to be the *ONLY* class is the jar file specified in -javaagent;
        //  Loading JavaSECMPInitializer class using SystemClassLoader.
        if ((agentArgs != null) && agentArgs.equals("main")) {
            initializeFromMain(instr);
        } else {
            initializeFromAgent(instr);
        }
    }

    public static void initializeFromAgent(Instrumentation instr) throws Throwable {
            Class cls = Class.forName("org.eclipse.persistence.internal.jpa.deployment.JavaSECMPInitializer");
            Method method = cls.getDeclaredMethod("initializeFromAgent", new Class[] { Instrumentation.class });
            try {
                method.invoke(null, new Object[] { instr });
            } catch (InvocationTargetException exception) {
                throw exception.getCause();
            }
    }

    public static void initializeFromMain(Instrumentation instr) throws Exception {
            Class cls = Class.forName("org.eclipse.persistence.internal.jpa.deployment.JavaSECMPInitializer");
            Field field = cls.getField("globalInstrumentation");
            field.set(null, instr);
    }
}
