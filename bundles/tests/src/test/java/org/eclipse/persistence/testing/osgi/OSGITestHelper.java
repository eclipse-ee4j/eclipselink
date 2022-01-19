/*
 * Copyright (c) 2016, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Dmitry Kornilov - initial implementation
package org.eclipse.persistence.testing.osgi;

import org.ops4j.pax.exam.Option;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;

/**
 * Helper class with PAX options for different kind of OSGi tests.
 *
 * @author Dmitry Kornilov
 * @since 2.7.0
 */
public class OSGITestHelper {
    // Environment variables defined in antbuild.properties/antbuild.xml
    private static final String PLUGINS_DIR = System.getProperty("plugins.dir","target/osgi-test-plugins/");
    private static final String ACTIVATION_JAR = System.getProperty("activation.jar", "jakarta.activation-api.jar");
    private static final String JAXB_API_JAR = System.getProperty("jaxb-api.jar", "jakarta.xml.bind-api.jar");
    private static final String JAXB_OSGI = System.getProperty("jaxb-osgi.jar", "jaxb-xjc.jar");
    private static final String JAXRS_JAR = System.getProperty("jaxrs.jar", "jakarta.ws.rs-api.jar");
    private static final String JSON_API_JAR = System.getProperty("json-api.jar", "jakarta.json-api.jar");
    private static final String ASM_JAR = System.getProperty("asm.jar", "org.eclipse.persistence.asm.jar");

    public static Option[] getDefaultOptions() {

        return options(
                // JAXB API
                bundle("file:" + PLUGINS_DIR + ACTIVATION_JAR),
                bundle("file:" + PLUGINS_DIR + JAXB_API_JAR),
                //JAXB_OSGI
                bundle("file:" + PLUGINS_DIR + JAXB_OSGI),

                systemPackage("javax.rmi"),
                systemPackage("javax.rmi.CORBA"),
                systemPackage("org.omg.CORBA"),
                systemPackage("org.omg.CORBA.portable"),
                systemPackage("org.omg.CORBA_2_3.portable"),
                // JAX-RS API
                bundle("file:" + PLUGINS_DIR + JAXRS_JAR),

                // JSON API
                bundle("file:" + PLUGINS_DIR + JSON_API_JAR),

                // EclipseLink bundles
                bundle("file:" + PLUGINS_DIR + "org.eclipse.persistence.moxy.jar"),
                bundle("file:" + PLUGINS_DIR + "org.eclipse.persistence.core.jar"),
                bundle("file:" + PLUGINS_DIR + ASM_JAR),

                cleanCaches(),
                junitBundles());
    }

    public static Option[] getOptionsWithBeanValidation() {
        return options(
                // JAXB API
                bundle("file:" + PLUGINS_DIR + ACTIVATION_JAR),
                bundle("file:" + PLUGINS_DIR + JAXB_API_JAR),

                // JAX-RS API
                bundle("file:" + PLUGINS_DIR + JAXRS_JAR),

                // JSON API
                bundle("file:" + PLUGINS_DIR + JSON_API_JAR),

                // EclipseLink bundles
                bundle("file:" + PLUGINS_DIR + "org.eclipse.persistence.moxy.jar"),
                bundle("file:" + PLUGINS_DIR + "org.eclipse.persistence.core.jar"),
                bundle("file:" + PLUGINS_DIR + ASM_JAR),

                bundle("file:" + PLUGINS_DIR + "jakarta.validation-api.jar"),
                bundle("file:" + PLUGINS_DIR + "hibernate-validator.jar"),
                bundle("file:" + PLUGINS_DIR + "classmate.jar"),
                bundle("file:" + PLUGINS_DIR + "jakarta.el-api.jar"),
                bundle("file:" + PLUGINS_DIR + "jakarta.el.jar"),
                bundle("file:" + PLUGINS_DIR + "jboss-logging.jar"),
                cleanCaches(),
                junitBundles()
        );
    }
}
