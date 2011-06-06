/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.ClassToolsTests;
import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.ClassDeclaration;
import org.eclipse.persistence.tools.workbench.utility.classfile.ClassFile;
import org.eclipse.persistence.tools.workbench.utility.classfile.Field;
import org.eclipse.persistence.tools.workbench.utility.classfile.FieldPool;
import org.eclipse.persistence.tools.workbench.utility.classfile.InnerClass;
import org.eclipse.persistence.tools.workbench.utility.classfile.Method;
import org.eclipse.persistence.tools.workbench.utility.classfile.MethodPool;


public class ClassFileTests extends TestCase {
	private static final String[] NESTED_CLASS_NAMES =
		new String[] {
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$1$LocalClass1",
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$1$LocalClass2",
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$1",
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$2",
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$AnotherClass$DoubleNestedClass",
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$AnotherClass",
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$DeprecatedStaticInnerInterface",
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$InnerClass1$NestedInnerClass",
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$InnerClass1",
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$InnerInterface1",
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$StaticInnerClass",
			"org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$StaticInnerInterface"
		};

	/**
	 * WARNING: hack-o-rama - we munge the class names above, depending
	 * on the compiler-generated class name; see the comment in ClassToolsTests
	 */
	static {
		for (int i = 0; i < NESTED_CLASS_NAMES.length; i++) {
			NESTED_CLASS_NAMES[i] = munge(NESTED_CLASS_NAMES[i]);
		}
	}

	private static String munge(String className) {
		return ClassToolsTests.compilerDependentClassNameFor(className);
	}

	public static Test suite() {
		return new TestSuite(ClassFileTests.class);
	}

	public ClassFileTests(String name) {
		super(name);
	}

	public void testClassFileHeader() throws Exception {
		this.verifyClassFileHeader(ClassFileTestClass.class);
		for (int i = 0; i < NESTED_CLASS_NAMES.length; i++) {
			this.verifyClassFileHeader(Class.forName(NESTED_CLASS_NAMES[i]));
		}
	}

	private void verifyClassFileHeader(Class javaClass) throws Exception {
		ClassFile classFile = ClassFile.forClass(javaClass);
		assertEquals(0xCAFEBABE, classFile.getHeader().getMagic());
	}

	public void testNestedClasses() throws Exception {
		ClassFile classFile;
		classFile = ClassFile.forClass(ClassFileTestClass.class);
		assertTrue(classFile.isTopLevelClass());
		assertFalse(classFile.isNestedClass());
		assertFalse(classFile.isMemberClass());
		assertFalse(classFile.isLocalClass());
		assertFalse(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName(munge("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$1$LocalClass1")));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertFalse(classFile.isMemberClass());
		assertTrue(classFile.isLocalClass());
		assertFalse(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName(munge("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$1$LocalClass2")));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertFalse(classFile.isMemberClass());
		assertTrue(classFile.isLocalClass());
		assertFalse(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$1"));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertFalse(classFile.isMemberClass());
		assertFalse(classFile.isLocalClass());
		assertTrue(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$2"));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertFalse(classFile.isMemberClass());
		assertFalse(classFile.isLocalClass());
		assertTrue(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$AnotherClass$DoubleNestedClass"));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertTrue(classFile.isMemberClass());
		assertFalse(classFile.isLocalClass());
		assertFalse(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$AnotherClass"));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertTrue(classFile.isMemberClass());
		assertFalse(classFile.isLocalClass());
		assertFalse(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$DeprecatedStaticInnerInterface"));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertTrue(classFile.isMemberClass());
		assertFalse(classFile.isLocalClass());
		assertFalse(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$InnerClass1$NestedInnerClass"));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertTrue(classFile.isMemberClass());
		assertFalse(classFile.isLocalClass());
		assertFalse(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$InnerClass1"));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertTrue(classFile.isMemberClass());
		assertFalse(classFile.isLocalClass());
		assertFalse(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$InnerInterface1"));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertTrue(classFile.isMemberClass());
		assertFalse(classFile.isLocalClass());
		assertFalse(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$StaticInnerClass"));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertTrue(classFile.isMemberClass());
		assertFalse(classFile.isLocalClass());
		assertFalse(classFile.isAnonymousClass());

		classFile = ClassFile.forClass(Class.forName("org.eclipse.persistence.tools.workbench.test.utility.classfile.ClassFileTestClass$StaticInnerInterface"));
		assertFalse(classFile.isTopLevelClass());
		assertTrue(classFile.isNestedClass());
		assertTrue(classFile.isMemberClass());
		assertFalse(classFile.isLocalClass());
		assertFalse(classFile.isAnonymousClass());
	}

	public void testClassDeclaration() throws Exception {
		this.verifyClassDeclaration(ClassFileTestClass.class);
		for (int i = 0; i < NESTED_CLASS_NAMES.length; i++) {
			this.verifyClassDeclaration(Class.forName(NESTED_CLASS_NAMES[i]));
		}
	}

	private void verifyClassDeclaration(Class javaClass) throws Exception {
		ClassFile classFile = ClassFile.forClass(javaClass);
		ClassDeclaration declaration = classFile.getDeclaration();
		assertEquals(Modifier.toString(javaClass.getModifiers()), Modifier.toString(declaration.standardAccessFlags()));
		assertTrue(declaration.isInterface() || declaration.isSuper());	// all new (non-interface) classes should have this bit set
		assertEquals(javaClass.getName(), declaration.thisClassName());
		if ( ! javaClass.isInterface()) {
			assertEquals(javaClass.getSuperclass().getName(), declaration.superClassName());
		}
		this.verifyClasses(javaClass.getInterfaces(), declaration.interfaceNames());
	}

	private void verifyClasses(Class[] expectedClasses, String[] actualClassNames) {
		String[] expectedClassNames = new String[expectedClasses.length];
		for (int i = expectedClasses.length; i-- > 0; ) {
			expectedClassNames[i] = expectedClasses[i].getName();
		}
		Bag expected = CollectionTools.bag(expectedClassNames);
		Bag actual = CollectionTools.bag(actualClassNames);
		assertEquals(expected, actual);
	}

	public void testFields() throws Exception {
		this.verifyFields(ClassFileTestClass.class);
		for (int i = 0; i < NESTED_CLASS_NAMES.length; i++) {
			this.verifyFields(Class.forName(NESTED_CLASS_NAMES[i]));
		}
	}

	private void verifyFields(Class javaClass) throws Exception {
		ClassFile classFile = ClassFile.forClass(javaClass);
		FieldPool fieldPool = classFile.getFieldPool();
		for (short i = fieldPool.getCount(); i-- > 0; ) {
			this.verifyField(fieldPool.get(i), javaClass);
		}
	}

	private void verifyField(Field actualField, Class javaClass) throws Exception {
		java.lang.reflect.Field expectedField = javaClass.getDeclaredField(actualField.name());
		assertNotNull(expectedField);
		assertEquals(Modifier.toString(expectedField.getModifiers()), Modifier.toString(actualField.standardAccessFlags()));
		assertEquals(expectedField.getType(), actualField.getFieldDescriptor().javaClass());
		assertEquals(expectedField.getType().getName(), actualField.javaTypeName());
		if (actualField.name().toLowerCase().indexOf("deprecated") == -1) {
			assertFalse(actualField.isDeprecated());
		} else {
			assertTrue(actualField.isDeprecated());
		}
		if (actualField.name().toLowerCase().indexOf('$') == -1) {
			assertFalse(actualField.isSynthetic());
		} else {
			assertTrue(actualField.isSynthetic());
		}
	}

	public void testFieldConstantValue() throws Exception {
		this.verifyFieldConstantValue(ClassFileTestClass.class);
	}

	private void verifyFieldConstantValue(Class javaClass) throws Exception {
		ClassFile classFile = ClassFile.forClass(javaClass);
		FieldPool fieldPool = classFile.getFieldPool();
		Field field;

		field = fieldPool.fieldNamed("byteStatic_55");
		assertEquals(new Integer(55), field.constantValue());

		field = fieldPool.fieldNamed("shortStatic_55");
		assertEquals(new Integer(55), field.constantValue());

		field = fieldPool.fieldNamed("intStatic_55");
		assertEquals(new Integer(55), field.constantValue());

		field = fieldPool.fieldNamed("longStatic_55L");
		assertEquals(new Long(55L), field.constantValue());

		field = fieldPool.fieldNamed("floatStatic_5_55F");
		assertEquals(new Float(5.55F), field.constantValue());

		field = fieldPool.fieldNamed("floatStatic_5_0e7F");
		assertEquals(new Float(5e7F), field.constantValue());

		field = fieldPool.fieldNamed("doubleStatic_5_55D");
		assertEquals(new Double(5.55D), field.constantValue());

		field = fieldPool.fieldNamed("doubleStatic_5_0e55D");
		assertEquals(new Double(5e55D), field.constantValue());

		field = fieldPool.fieldNamed("booleanStatic_true");
		assertEquals(new Integer(1), field.constantValue());

		field = fieldPool.fieldNamed("booleanStatic_false");
		assertEquals(new Integer(0), field.constantValue());

		field = fieldPool.fieldNamed("charStatic_A");
		assertEquals(new Integer('A'), field.constantValue());

		field = fieldPool.fieldNamed("stringStatic_A_String");
		assertEquals("A String", field.constantValue());

		field = fieldPool.fieldNamed("privateString");
		boolean exCaught = false;
		Object constantValue = null;
		try {
			constantValue = field.constantValue();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("bogus constant value: " + constantValue, exCaught);
	}

	public void testFieldArrayDepth() throws Exception {
		this.verifyFieldArrayDepth(ClassFileTestClass.class);
	}

	private void verifyFieldArrayDepth(Class javaClass) throws Exception {
		ClassFile classFile = ClassFile.forClass(javaClass);
		FieldPool fieldPool = classFile.getFieldPool();

		Field field = fieldPool.fieldNamed("byteStatic_55");
		assertEquals(0, field.getFieldDescriptor().arrayDepth());

		field = fieldPool.fieldNamed("privateString");
		assertEquals(0, field.getFieldDescriptor().arrayDepth());

		field = fieldPool.fieldNamed("packageStringArray1D");
		assertEquals(1, field.getFieldDescriptor().arrayDepth());

		field = fieldPool.fieldNamed("packageStringArray2D");
		assertEquals(2, field.getFieldDescriptor().arrayDepth());

		field = fieldPool.fieldNamed("packageIntArray2D");
		assertEquals(2, field.getFieldDescriptor().arrayDepth());
	}

	public void testFieldElementType() throws Exception {
		this.verifyFieldElementType(ClassFileTestClass.class);
	}

	private void verifyFieldElementType(Class javaClass) throws Exception {
		ClassFile classFile = ClassFile.forClass(javaClass);
		FieldPool fieldPool = classFile.getFieldPool();

		Field field = fieldPool.fieldNamed("byteStatic_55");
		assertEquals("byte", field.getFieldDescriptor().elementTypeName());

		field = fieldPool.fieldNamed("privateString");
		assertEquals("java.lang.String", field.getFieldDescriptor().elementTypeName());

		field = fieldPool.fieldNamed("packageStringArray1D");
		assertEquals("java.lang.String", field.getFieldDescriptor().elementTypeName());

		field = fieldPool.fieldNamed("packageStringArray2D");
		assertEquals("java.lang.String", field.getFieldDescriptor().elementTypeName());

		field = fieldPool.fieldNamed("packageIntArray2D");
		assertEquals("int", field.getFieldDescriptor().elementTypeName());
	}

	public void testMethods() throws Exception {
		this.verifyMethods(ClassFileTestClass.class);
		for (int i = 0; i < NESTED_CLASS_NAMES.length; i++) {
			this.verifyMethods(Class.forName(NESTED_CLASS_NAMES[i]));
		}
	}

	private void verifyMethods(Class javaClass) throws Exception {
		ClassFile classFile = ClassFile.forClass(javaClass);
		MethodPool methodPool = classFile.getMethodPool();
		int staticInitializationMethodCount = 0;
		for (short i = methodPool.getCount(); i-- > 0; ) {
			Method actualMethod = methodPool.get(i);
			this.verifyMethod(actualMethod, javaClass);
			if (actualMethod.isStaticInitializationMethod()) {
				staticInitializationMethodCount++;
			}
		}
		assertTrue(staticInitializationMethodCount <= 1);
	}

	private void verifyMethod(Method actualMethod, Class javaClass) throws Exception {
		if (actualMethod.isStaticInitializationMethod()) {
			this.verifyStaticInitializationMethod(actualMethod, javaClass);
		} else if (actualMethod.isConstructor()) {
			this.verifyConstructor(actualMethod, javaClass);
		} else {
			this.verifyNormalMethod(actualMethod, javaClass);
		}
	}

	private void verifyStaticInitializationMethod(Method actualMethod, Class javaClass) throws Exception {
		assertEquals(0, actualMethod.getParameterDescriptors().length);
		assertEquals(Modifier.STATIC, actualMethod.standardAccessFlags());
		assertEquals(void.class, actualMethod.getReturnDescriptor().javaClass());
		assertEquals("void", actualMethod.javaReturnTypeName());
	}

	private void verifyConstructor(Method actualMethod, Class javaClass) throws Exception {
		Class[] actualParmTypes = new Class[actualMethod.getParameterDescriptors().length];
		for (int i = 0; i < actualMethod.getParameterDescriptors().length; i++) {
			actualParmTypes[i] = actualMethod.getParameterDescriptor(i).javaClass();
		}
		Constructor expectedCtor = javaClass.getDeclaredConstructor(actualParmTypes);
		assertNotNull(expectedCtor);
		assertEquals(Modifier.toString(expectedCtor.getModifiers()), Modifier.toString(actualMethod.standardAccessFlags()));
		assertEquals(void.class, actualMethod.getReturnDescriptor().javaClass());
		assertEquals("<init>", actualMethod.name());
		assertEquals(expectedCtor.getName(), actualMethod.constructorName());
		this.verifyClasses(expectedCtor.getExceptionTypes(), actualMethod.exceptionClassNames());
	}

	private void verifyNormalMethod(Method actualMethod, Class javaClass) throws Exception {
		Class[] actualParmTypes = new Class[actualMethod.getParameterDescriptors().length];
		for (int i = 0; i < actualMethod.getParameterDescriptors().length; i++) {
			actualParmTypes[i] = actualMethod.getParameterDescriptor(i).javaClass();
		}
		java.lang.reflect.Method expectedMethod = javaClass.getDeclaredMethod(actualMethod.name(), actualParmTypes);
		assertNotNull(expectedMethod);
		assertEquals(Modifier.toString(expectedMethod.getModifiers()), Modifier.toString(actualMethod.standardAccessFlags()));
		assertEquals(expectedMethod.getReturnType(), actualMethod.getReturnDescriptor().javaClass());
		assertEquals(expectedMethod.getReturnType().getName(), actualMethod.javaReturnTypeName());
		this.verifyClasses(expectedMethod.getExceptionTypes(), actualMethod.exceptionClassNames());
		if (actualMethod.name().toLowerCase().indexOf("deprecated") == -1) {
			assertFalse(actualMethod.isDeprecated());
		} else {
			assertTrue(actualMethod.isDeprecated());
		}
	}

	public void testDeclaringClass() throws Exception {
		this.verifyDeclaringClass(ClassFileTestClass.class);
		for (int i = 0; i < NESTED_CLASS_NAMES.length; i++) {
			this.verifyDeclaringClass(Class.forName(NESTED_CLASS_NAMES[i]));
		}
	}

	private void verifyDeclaringClass(Class javaClass) throws Exception {
		ClassFile classFile = ClassFile.forClass(javaClass);
		String actualDeclaringClassName = classFile.declaringClassName();
		Class expectedDeclaringClass = javaClass.getDeclaringClass();
		assertEquals((expectedDeclaringClass == null) ? null : expectedDeclaringClass.getName(), actualDeclaringClassName);
	}

	public void testMemberClasses() throws Exception {
		this.verifyMemberClasses(ClassFileTestClass.class);
		for (int i = 0; i < NESTED_CLASS_NAMES.length; i++) {
			this.verifyMemberClasses(Class.forName(NESTED_CLASS_NAMES[i]));
		}
	}

	private void verifyMemberClasses(Class javaClass) throws Exception {
		ClassFile classFile = ClassFile.forClass(javaClass);
		String[] memberClassNames = classFile.declaredMemberClassNames();

		// "member" classes should match up with the "declared" classes
		this.verifyClasses(javaClass.getDeclaredClasses(), memberClassNames);

		for (short i = 0; i < memberClassNames.length; i++) {
			InnerClass memberClass = classFile.getAttributePool().innerClassNamed(memberClassNames[i]);
			assertEquals(javaClass.getName(), memberClass.outerClassInfoName());
		}
	}

	public void testNonMemberClasses() throws Exception {
		this.verifyNonMemberClasses(ClassFileTestClass.class);
	}

	private void verifyNonMemberClasses(Class javaClass) throws Exception {
		ClassFile classFile = ClassFile.forClass(javaClass);
		String[] innerClassNames = classFile.nestedClassNames();
		// these can change over time and are compiler-dependent, so the tests
		// may fail in the future - use ClassFileDumper to find the expected values
		assertTrue(CollectionTools.contains(innerClassNames, javaClass.getName() + "$2"));
		// Eclipse generates ...$3 while the jdk generates ...$1
		assertTrue(CollectionTools.contains(innerClassNames, javaClass.getName() + "$3") ||
				CollectionTools.contains(innerClassNames, javaClass.getName() + "$1"));
		assertTrue(CollectionTools.contains(innerClassNames, munge(javaClass.getName() + "$1$LocalClass1")));
		assertTrue(CollectionTools.contains(innerClassNames, munge(javaClass.getName() + "$1$LocalClass2")));
	}

	public void testInterfaceDeclaration() throws Exception {
		this.verifyClassDeclaration(ClassFileTestInterface.class);
	}

	public void testInterfaceFields() throws Exception {
		this.verifyFields(ClassFileTestInterface.class);
	}

	public void testInterfaceMethods() throws Exception {
		this.verifyMethods(ClassFileTestInterface.class);
	}

	public void testInterfaceMemberClasses() throws Exception {
		this.verifyMemberClasses(ClassFileTestInterface.class);
	}

	public void testSyntheticClass() throws Exception {
		ClassFile classFile = ClassFile.forClass(ClassFileTestClass.class);
		assertFalse(classFile.isSynthetic());

	// this class was not marked synthetic until jdk 1.4.2 and it doesn't seem to be marked synthetic in 1.5
		String javaVersion = System.getProperty("java.version");
		if (javaVersion.compareTo("1.4.2") >= 0 && javaVersion.compareTo("1.5.0") < 0) {
			Class syntheticClass = null;
			try {
				syntheticClass = Class.forName(String.class.getName() + "$1");
			} catch (ClassNotFoundException ex) {
				// the eclipse compiler does not generate this class;
				// the jdk compiler generates it, but it does nothing (according to JAD)
				return;
			}
			classFile = ClassFile.forClass(syntheticClass);
			assertTrue(classFile.isSynthetic());
		}
	}

	public void testDeprecatedClass() throws Exception {
		ClassFile classFile = ClassFile.forClass(ClassFileTestClass.class);
		assertFalse(classFile.isDeprecated());

		classFile = ClassFile.forClass(Class.forName(ClassFileTestClass.class.getName() + "$DeprecatedStaticInnerInterface"));
		assertTrue(classFile.isDeprecated());
	}

	public void testSourceFile() throws Exception {
		this.verifySourceFile(ClassFileTestClass.class);
	}

	private void verifySourceFile(Class javaClass) throws Exception {
		String expectedSourceFileName = ClassTools.shortNameFor(javaClass) + ".java";
		ClassFile classFile = ClassFile.forClass(javaClass);
		assertEquals(expectedSourceFileName, classFile.sourceFileName());

		// the source file for the inner class is the outer class's source file
		classFile = ClassFile.forClass(Class.forName(javaClass.getName() + "$1"));
		assertEquals(expectedSourceFileName, classFile.sourceFileName());
	}

}
