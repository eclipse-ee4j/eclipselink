/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework.osgi;

import java.io.FileOutputStream;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner;
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * OSGi activator to run tests.
 * @author James
 */

public class Activator implements BundleActivator
{
    public static final String TEST_CLASS = "TEST_CLASS";

    public Activator() {
    }

    public void start(BundleContext context) throws Exception {
        try {
            JUnitTestCase.isOSGi = true;
            String testClass = System.getProperty("TEST_CLASS");
            String reportFile = System.getProperty("REPORT_FILE");

            JUnitTest junitTest = new JUnitTest(testClass);
            JUnitTestRunner runner = new JUnitTestRunner(junitTest,false,false,false,true);

            XMLJUnitResultFormatter xmlResult = new XMLJUnitResultFormatter();
            xmlResult.setOutput(new FileOutputStream(reportFile));
            runner.addFormatter(xmlResult);
            
            runner.run();

        } catch (Throwable fail) {
            fail.printStackTrace();
        }
    }


    public void stop(BundleContext bundlecontext) throws Exception {
    }
}
