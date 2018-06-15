/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.installer;

import java.io.File;
import java.util.logging.Logger;

/**
 * Tests eclipselink.zip consistency. It runs jaxb-compiler for simple xsd file
 * and asserts it has generated java classes.
 *
 * @author Martin Vojtek
 *
 */
public class JaxbCompilerTestCase extends junit.framework.TestCase {

    private static final String WORK_ITEM_RESOURCE_XSD = "org/eclipse/persistence/testing/jaxb/installer/WorkItem.xsd";

    /**
     * Tests jaxb-compiler command after unzipping eclipselink.zip.
     *
     * @throws Exception
     */
    public void testJaxbCompiler() throws Exception {

        String eclipselinkUnzipDir = System.getenv("ECLIPSELINK_UNZIP_DIR");

        String jaxbCompiler = "jaxb-compiler";

        // run jaxb-compiler.cmd or jaxb-compiler.sh
        String osName = System.getProperty("os.name");
        if (null != osName && osName.startsWith("Windows")) {
            jaxbCompiler = jaxbCompiler += ".cmd";
        } else {
            jaxbCompiler = jaxbCompiler += ".sh";
        }

        File resourceFile = new File(Thread.currentThread().getContextClassLoader().getResource(WORK_ITEM_RESOURCE_XSD).toURI());

        File binDir = new File(eclipselinkUnzipDir + File.separator + "eclipselink" + File.separator + "bin");
        File compilerFile = new File(binDir, jaxbCompiler);

        Process process = Runtime.getRuntime().exec(new String[] { compilerFile.getAbsolutePath(), resourceFile.getAbsolutePath() }, null, binDir);
        int result = process.waitFor();

        assertTrue("Process has not completed successfully.", result == 0);

        String packagePrefix = "perf" + File.separator + "testing" + File.separator + "persistence" + File.separator + "eclipse" + File.separator + "org" + File.separator + "workitem";

        String[] files = { "package-info.java", "ObjectFactory.java", "jaxb.properties", "DoWorkItemResponse.java", "DoWorkItem.java" };

        for (String file : files) {
            assertFile(binDir, packagePrefix, file);
        }
    }

    private void assertFile(File binDir, String packagePrefix, String fileName) {
        assertTrue("File " + fileName + "does not exists.", new File(binDir + File.separator + packagePrefix, fileName).exists());
    }
}
