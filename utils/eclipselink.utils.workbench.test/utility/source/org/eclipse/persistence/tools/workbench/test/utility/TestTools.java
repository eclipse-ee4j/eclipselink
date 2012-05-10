/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

import org.eclipse.persistence.tools.workbench.utility.io.InvalidOutputStream;

/**
 * various tools that can be used by test cases
 */
public final class TestTools {

	/**
	 * test an object's implementation of Serializable by
	 * serializing the specified object to a byte array; then de-serializing the
	 * byte array and returning the resultant object
	 */
	public static Object serialize(Object o) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream(2000);
		ObjectOutputStream outStream = new ObjectOutputStream(baOutStream);
		outStream.writeObject(o);
		outStream.close();

		ByteArrayInputStream baInStream = new ByteArrayInputStream(baOutStream.toByteArray());
		ObjectInputStream inStream = new ObjectInputStream(baInStream);
		Object o2 = inStream.readObject();
		inStream.close();

		return o2;
	}

	/**
	 * some tests require access to the Web (e.g. any tests that parse an XML
	 * document that specifies a DTD or Schema that is loaded from the Web);
	 * use this method to configure the JDK proxy settings
	 */
	public static void setUpOracleProxy() {
		System.setProperty("http.proxyHost", "www-proxy.us.oracle.com");
		System.setProperty("http.proxyPort", "80");
	}

	/**
	 * some tests require access to the Web (e.g. any tests that parse an XML
	 * document that specifies a DTD or Schema that is loaded from the Web);
	 * use this method to configure the JDK proxy settings via the command-line
	 */
	public static String[] buildOracleProxyCommandLineOptions() {
		return new String[] {
			"-Dhttp.proxyHost=www-proxy.us.oracle.com",
			"-Dhttp.proxyPort=80"
		};
	}

	/**
	 * this will uncover any code that writes to standard out or standard err
	 */
	public static void invalidateSystemStreams() {
		redirectSystemStreamsTo(InvalidOutputStream.instance());
	}

	/**
	 * redirect std out and std err to the specified stream
	 */
	public static void redirectSystemStreamsTo(OutputStream outputStream) {
		redirectSystemStreamsTo(new PrintStream(outputStream));
	}

	/**
	 * redirect std out and std err to the specified stream
	 */
	public static void redirectSystemStreamsTo(PrintStream printStream) {
		System.setOut(printStream);
		System.setErr(printStream);
	}

	/**
	 * execute the specified test and dump the results to the console
	 */
	public static String execute(TestCase testCase) {
		long start = System.currentTimeMillis();
		TestResult result = testCase.run();
		long end = System.currentTimeMillis();

		StringWriter stringWriter = new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		writer.print(testCase.getName());
		writer.print(": ");
		if (result.wasSuccessful()) {
			writer.println("OK");
		} else {
			TestFailure failure = null;
			if (result.failures().hasMoreElements()) {
				failure = (TestFailure) result.failures().nextElement();
			} else {
				failure = (TestFailure) result.errors().nextElement();
			}
			failure.thrownException().printStackTrace(writer);
		}
		writer.print("elapsed time: ");
		long elapsed = end - start;
		writer.print(elapsed / 1000L);
		writer.println(" sec.");
		return stringWriter.toString();
	}
	
	private static final Class TestCase_class = TestCase.class;
	/**
	 * Clear out all the instance variable of the specified test case,
	 * allowing the various test fixtures to be garbage-collected.
	 * Typically this is called in the #tearDown() method.
	 */
	public static void clear(TestCase testCase) throws IllegalAccessException {
		for (Class tempClass = testCase.getClass(); tempClass != TestCase_class; tempClass = tempClass.getSuperclass()) {
			Field[] fields = tempClass.getDeclaredFields();
			for (int i = fields.length; i-- > 0; ) {
				Field field = fields[i];
				// leave primitives alone - they don't get garbage-collected, and we can't set them to null...
				if (field.getType().isPrimitive()) {
					continue;
				}
				// leave static fields alone (?)
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				field.setAccessible(true);
				field.set(testCase, null);
			}
		}
	}

	/**
	 * Workaround for a JUnit bug: JUnit does not configure the testing
	 * Thread with a context class loader. This should probably happen
	 * in TestRunner.doRunTest(Test), just before starting the thread.
	 */
	public static void setUpJUnitThreadContextClassLoader() {
		Thread.currentThread().setContextClassLoader(TestTools.class.getClassLoader());
	}

	private TestTools() {
		super();
		throw new UnsupportedOperationException();
	}

}
