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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

/**
 * A class with lots of different fields and methods that
 * can be used to test the ClassFile code.
 */
public class ClassFileTestClass implements Serializable, Cloneable {

	private Class objectClass;

	private String privateString;
	String packageString;
	protected String protectedString;
	public String publicString;
	public String[] publicStringArray1D;
	String[] packageStringArray1D;
	String[][] packageStringArray2D;

	private int privateInt;
	int packageInt;
	protected int protectedInt;
	public int publicInt;
	public int[] publicIntArray1D;
	int[] packageIntArray1D;
	int[][] packageIntArray2D;

	/** @deprecated */
	public int deprecatedInt;
	/** @deprecated */
	public Object deprecatedObject;

	public static final byte byteStatic_55 = 55;
	public static final short shortStatic_55 = 55;
	public static final int intStatic_55 = 55;
	public static final long longStatic_55L = 55L;

	public static final float floatStatic_5_55F = 5.55F;
	public static final float floatStatic_5_0e7F = 5e7F;
	public static final double doubleStatic_5_55D = 5.55D;
	public static final double doubleStatic_5_0e55D = 5e55D;

	public static final boolean booleanStatic_true = true;
	public static final boolean booleanStatic_false = false;

	public static final char charStatic_A = 'A';
	public static final String stringStatic_A_String = "A String";

	public String initializedString;
	public static String initializedStaticString1;
	public static String initializedStaticString2;

	{
		this.initializedString = "ClassFileTestClass init: " + new Date();
	}

	static {
		initializedStaticString1 = "ClassFileTestClass static init 1: " + new Date();
	}

	static {
		initializedStaticString2 = "ClassFileTestClass static init 2: " + new Date();
	}

	public ClassFileTestClass() {
		super();
		this.privateVoidMethod();
		this.privateString = "stop the compiler warning";
		this.privateInt = 77;
		this.objectClass = Object.class;
	}

	public ClassFileTestClass(String privateString) {
		super();
		this.privateString = privateString;
	}

	ClassFileTestClass(int[][] packageIntArray2D) throws IOException, RuntimeException {
		this();
		this.packageIntArray2D = packageIntArray2D;
		this.randomIOException();
	}
	
	private void randomIOException() throws IOException {
		if ((new Random().nextInt() % 10) == 0) {	// % = remainder
			// just add some references to private fields:
			System.out.println("objectClass: " + this.objectClass);
			System.out.println("privateString: " + this.privateString);
			System.out.println("privateInt: " + this.privateInt);
			throw new IOException("must fail 10% of the the time");
		}
	}

	private void privateVoidMethod() {
		this.deprecatedMethod();
		PropertyChangeListener listener1 = this.anonymousInnerClassMethod1();
		listener1.toString();
		PropertyChangeListener listener2 = this.anonymousInnerClassMethod2();
		listener2.toString();
		InnerInterface1 x = new InnerClass1();
		x.toString();
	}

	void packageVoidMethod() {
		this.privateVoidMethod();
	}

	protected void protectedVoidMethod() {
		this.privateVoidMethod();
	}

	public void publicVoidMethod() {
		this.privateVoidMethod();
	}

	public void publicVoidMethodIOException() throws IOException {
		this.privateVoidMethod();
		this.randomIOException();
	}

	public void publicVoidMethodIOExceptionRuntimeException() throws IOException, RuntimeException {
		this.privateVoidMethod();
		this.randomIOException();
	}

	public int[] publicIntArray1DMethodIntObject(int index, Object object) {
		return null;
	}

	public int[][] publicIntArray2DMethodIntInt(int int1, int int2) {
		return null;
	}

	public String[] publicStringArray1DMethodIntArray1DObjectArray1D(int[] index, Object[] object) {
		return null;
	}

	public String[][] publicStringArray2DMethodIntArray1DObjectArray2D(int[] index, Object[][] object) {
		return null;
	}

	/**
	 * @deprecated
	 */
	private void deprecatedMethod() {
		this.toString();
	}

	private PropertyChangeListener anonymousInnerClassMethod1() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				// do nothing
			}
		};
	}

	private PropertyChangeListener anonymousInnerClassMethod2() {
		this.deprecatedMethod();
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				// do nothing
			}
			public PropertyChangeListener doubleAnonymousInnerClassMethod() {
				return new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						// do nothing
					}
					public void jaxb() {
						class JAXBLocalClass {
							void jaxbFOO() {
								System.out.println("JAXB FOO");
							}
						}
					}
				};
			}
		};
	}

	public void localClassMethod() {
		class LocalClass1 implements InnerInterface1 {
			public void foo() {
				System.out.println(this.getClass().getName() + "#foo()");
			}
		}
		InnerInterface1 x = new LocalClass1();
		x.foo();
	}

	public void localClassMethodDuplicate() {
		class LocalClass1 implements InnerInterface1 {
			public void foo() {
				System.out.println(this.getClass().getName() + "#foo()");
			}
		}
		InnerInterface1 x = new LocalClass1();
		x.foo();
	}

	public void localClassMethod2() {
		class LocalClass2 implements InnerInterface1 {
			public void foo() {
				System.out.println(this.getClass().getName() + "#foo()");
			}
		}
		InnerInterface1 x = new LocalClass2();
		x.foo();
	}

	// ********** inner classes **********

	private interface InnerInterface1 {
		void foo();
	}

	private class InnerClass1 implements InnerInterface1 {
		public void foo() {
			NestedInnerClass x = new NestedInnerClass();
			x.foofoo();
		}
	
		private class NestedInnerClass {
			public void foofoo() {
				// do nothing
			}
		}
	}

	public static interface StaticInnerInterface {
		void bar();
	}

	public static class StaticInnerClass implements StaticInnerInterface {
		public void bar() {
			// do nothing
		}
	}

	/**
	 * @deprecated
	 */
	public static interface DeprecatedStaticInnerInterface {
		void deprecateMe();
	}

	public class AnotherClass {
		public AnotherClass() {
			super();
		}
		public AnotherClass(String s) {
			this();
		}
		void baz() {
	 		PropertyChangeListener listener1 = this.anotherAnonymousInnerClassMethod();
			listener1.toString();
		}
		private PropertyChangeListener anotherAnonymousInnerClassMethod() {
			return new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					// do nothing
				}
			};
		}
		void jarjar() {
			class AnotherLocalClass {
				public void anotherJarJar() {
					System.out.println(this.getClass().getName() + "#anotherJarJar()");
				}
			}
			AnotherLocalClass x = new AnotherLocalClass();
			x.anotherJarJar();
		}
		public class DoubleNestedClass {
			public DoubleNestedClass(Object o) {
				super();
			}
			void joo() {
				// do nothing
			}
			void jar() {
				// do nothing
			}
		}
	}

}
