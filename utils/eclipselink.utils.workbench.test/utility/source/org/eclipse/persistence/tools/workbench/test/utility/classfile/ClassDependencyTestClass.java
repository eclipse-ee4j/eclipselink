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
package org.eclipse.persistence.tools.workbench.test.utility.classfile;

/**
 * 
 */
public class ClassDependencyTestClass extends java.util.Vector implements Comparable {
	private java.sql.Connection connection;
	private static java.util.jar.JarFile jarFile;

	private String instanceString1 = java.sql.Date.valueOf("1961-10-14").toString();
	private String instanceString2;
	private static String staticString;
	private static final long serialVersionUID = 1L;

	static {
		staticString = "today: " + new java.util.Date();
	}

	{
		this.instanceString2 = "today: " + java.sql.Timestamp.valueOf("1961-10-14 02:02:02.123456789");
	}

	public static String getStaticString() {
		return staticString;
	}

	public int compareTo(Object o) {
		return this.hashCode() - o.hashCode();
	}

	public Object getConnection() {
		return this.connection;
	}

	public static Object getJARFile() {
		return jarFile;
	}

	public Object getInstanceString1() {
		return this.instanceString1;
	}

	public Object getInstanceString2() {
		return this.instanceString2;
	}

	public java.util.SortedSet getSortedSet() {
		return null;
	}

	public void setSet(java.util.Set set) {
		// reference to java.util.Set as method parameter
	}

	public void doSomethingRemote() throws java.rmi.AccessException {
		throw new java.rmi.AccessException("test");
	}

	public Object readFile() {
		try {
			Object reader = new java.io.FileReader("foo.txt");
			return reader;
		} catch (java.io.FileNotFoundException ex) {
			return ex;
		}
	}

	public Object javaxMethod() throws javax.naming.InvalidNameException {
		javax.naming.Name name = new javax.naming.CompositeName("foo");
		return name.clone();
	}

	public boolean testClass(Object o) {
		// this is converted into a Class.forName("java.util.ArrayList")
		// as a result java.util.ArrayList will not show up as a dependency...
		// so java.util.ArrayList is a *compile* time dependency, but
		// it will not show up in the byte codes as being referenced,
		// except as a string...
		return o.getClass() == java.util.ArrayList.class;
	}

	public Object testClassCast() {
		return (java.io.Reader) new Object();
	}

	public Object[] testObjectArray() {
		Object[][] aa = (Object[][]) new Object();
		Object[] a = aa[0];
		Object o = a;
		return (Object[]) o;
	}

	public java.awt.Color[] testColorArray() {
		return (java.awt.Color[]) new Object();
	}

	public int[] testIntArray() {
		return (int[]) new Object();
	}

	public Object testCascadingMessages() {
		return (new java.util.ArrayList()).iterator();
	}

	public Object testCascadingMessagesInterface() {
		Object foo = new java.util.ArrayList();
		return ((java.util.List) foo).iterator();
	}

	public Object testFieldAccess() {
		java.awt.Button[] array = new java.awt.Button[0];
		System.out.println("length: " + array.length);	// is length a field ref? doesn't look like it...
		return System.out;
	}

	public void testFinally() {
		try {
			this.testFieldAccess();
		} catch (Exception ex) {
			this.testClassCast();
		} finally {
			this.testFieldAccess();
		}
	}

	public void testCtor() {
		Object x = new java.math.BigDecimal(55.55);
		System.out.println(x.toString());
	}

	public void testInstanceof() {
		Object x = new Object();
		boolean result = x instanceof java.math.BigInteger;
		System.out.println("result: " + result);
	}

	public void testThrownException() {
		throw new IllegalArgumentException();
	}

	public void testReferenceToInnerClass() {
		java.util.Map map = new java.util.HashMap();
		map.put("key", "value");
		for (java.util.Iterator stream = map.entrySet().iterator(); stream.hasNext(); ) {
			java.util.Map.Entry entry = (java.util.Map.Entry) stream.next();
			System.out.print(entry.getKey());
			System.out.print("=");
			System.out.print(entry.getValue());
			System.out.println();
		}
	}

}
