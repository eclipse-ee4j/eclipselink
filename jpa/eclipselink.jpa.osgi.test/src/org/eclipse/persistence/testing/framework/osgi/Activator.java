/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestResult;

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
            Test test = (Test)Class.forName(testClass).getMethod("suite", (Class[])null).invoke(null, (Object[])null);
            TestResult result = new TestResult();
            test.run(result);
            StringWriter writer = new StringWriter();
            logJUnitResult(test, result, writer, "");
            System.out.println(writer.toString());
        } catch (Throwable fail) {
            fail.printStackTrace();
        }
    }

    public static String CR = "\n";
    
    /**
     * Log any JUnit results if present.
     */
    public static void logJUnitResult(Test test, TestResult result, Writer log, String indent) {
        try {
            log.write(CR);
            log.write(CR);
            log.write(indent + "TEST MODEL NAME: (JUnit test): " + test);
            log.write(CR);
            if (result == null) {
                log.write(indent + "## SETUP FAILURE ## (no tests run)");                
                log.write(CR);
                log.flush();
                return;
            }
            if ((result.failureCount() > 0) || (result.errorCount() > 0)) {
                log.write(indent + "###ERRORS###" + CR);
            }
            log.write(CR);
            log.write(indent + "Errors: (failures): " + result.failureCount());
            log.write(CR);
            log.write(indent + "Fatal Errors: (errors): " + result.errorCount());
            log.write(CR);
            log.write(indent + "Passed: " + (result.runCount() - result.errorCount() - result.failureCount()));
            log.write(CR);
            log.write(indent + "Total Tests: " + result.runCount());
            log.write(CR);
            if (result.failureCount() > 0) {
                log.write(CR);
                log.write(indent + "Failures:");
                log.write(CR);
                for (Enumeration failures = result.failures(); failures.hasMoreElements();) {
                    junit.framework.TestFailure failure = (junit.framework.TestFailure)failures.nextElement();
                    String testString = failure.failedTest().toString();
                    int startIndex = testString.indexOf("(");
                    if (startIndex != -1) {
                        log.write(indent + "TEST SUITE NAME: " + testString.substring(startIndex + 1, testString.length() - 1));
                        log.write(CR);
                    }
                    log.write(indent + "TEST NAME: " + testString);
                    log.write(CR);
                    log.write(indent + "##FAILURE##" + CR);
                    log.write(indent + "RESULT:      Error (failure)");
                    log.write(CR);
                    log.write(indent + failure.trace());
                    log.write(CR);
                }
            }
            if (result.errorCount() > 0) {
                log.write(CR);
                log.write(indent + "Errors:");
                log.write(CR);
                for (Enumeration errors = result.errors(); errors.hasMoreElements();) {
                    junit.framework.TestFailure error = (junit.framework.TestFailure)errors.nextElement();
                    String testString = error.failedTest().toString();
                    int startIndex = testString.indexOf("(");                    
                    if (startIndex != -1) {
                        log.write(indent + "TEST SUITE NAME: " + testString.substring(startIndex + 1, testString.length() - 1));
                        log.write(CR);
                    }
                    log.write(indent + "TEST NAME: " + testString);
                    log.write(CR);
                    log.write(indent + "##FAILURE##" + CR);
                    log.write(indent + "RESULT:      FatalError (error)");
                    log.write(CR);
                    log.write(indent + error.trace());
                    log.write(CR);
                }
            }
            log.write(CR);
            log.flush();
        } catch (IOException exception) {
        }
    }

    public void stop(BundleContext bundlecontext) throws Exception {
    }
}
