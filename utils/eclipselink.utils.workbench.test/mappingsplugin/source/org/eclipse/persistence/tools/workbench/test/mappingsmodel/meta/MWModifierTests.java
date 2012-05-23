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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.meta;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWModifier;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.Classpath;


public class MWModifierTests extends TestCase {
	private MWRelationalProject project;
	private MWClass type;
	private MWModifier modifier;
	private volatile Object volatileField;

	private static final boolean JDK14 = jdkIsVersion("1.4");
	private static final boolean JDK15 = jdkIsVersion("1.5");
	private static final boolean JDK16 = jdkIsVersion("1.6");

	private static boolean jdkIsVersion(String version) {
		return System.getProperty("java.version").indexOf(version) != -1;
	}

	public static Test suite() {
		return new TestSuite(MWModifierTests.class);
	}
	
	public MWModifierTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.project = this.buildProject();
		this.project.getRepository().addClasspathEntry(Classpath.locationFor(Bag.class));	// "core" jar
		this.project.getRepository().addClasspathEntry(Classpath.locationFor(Displayable.class));	// "ui" jar
		this.project.getRepository().addClasspathEntry(Classpath.locationFor(this.getClass()));
		this.type = this.project.typeNamed("com.foo.Foo");
		this.modifier = this.type.getModifier();
	}
	
	private MWRelationalProject buildProject() {
		return new MWRelationalProject(this.getClass().getName(), MappingsModelTestTools.buildSPIManager(), null);		// we don't need a database platform
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testNewModifier() {
		assertTrue("invalid new modifier: " + this.modifier,
			this.modifier.isDefaultValue() &&
			! this.modifier.isAbstract() && 
			! this.modifier.isFinal() &&
			! this.modifier.isNative() &&
			! this.modifier.isPackage() &&
			! this.modifier.isPrivate() &&
			! this.modifier.isProtected() &&
			this.modifier.isPublic() &&
			! this.modifier.isStatic() &&
			! this.modifier.isStrict() &&
			! this.modifier.isSynchronized() &&
			! this.modifier.isTransient() &&
			! this.modifier.isVolatile()
		);
	}

	public void testDisplayString() throws Exception {
	
		this.verifyClassModifierDisplayString(java.lang.String.class, "public final");
		this.verifyClassModifierDisplayString(java.lang.Thread.class, "public");
		this.verifyClassModifierDisplayString(java.lang.Cloneable.class, "public abstract");
		this.verifyClassModifierDisplayString(java.util.AbstractCollection.class, "public abstract");
	
		this.verifyMethodModifierDisplayString(java.lang.String.class, "valueOf(java.lang.Object)", "public static");
		this.verifyMethodModifierDisplayString(java.lang.Thread.class, "interrupt0()", "private native");
		this.verifyMethodModifierDisplayString(java.util.Vector.class, "add(java.lang.Object)", "public synchronized");
		if (JDK16 || JDK15) {
			this.verifyMethodModifierDisplayString(java.util.Vector.class, "removeRange(int, int)", "protected synchronized");			
		} else {
			this.verifyMethodModifierDisplayString(java.util.Vector.class, "removeRange(int, int)", "protected");
		}
		
		if (JDK16 || JDK15) {
			this.verifyMethodModifierDisplayString(java.lang.Math.class, "sin(double)", "public static");			
		} else {
			this.verifyMethodModifierDisplayString(java.lang.Math.class, "sin(double)", "public static strictfp");
		}
	
		if (JDK16 || JDK15 || JDK14) {
			this.verifyAttributeModifierDisplayString(java.util.HashMap.class, "size", "transient");
		} else {
			this.verifyAttributeModifierDisplayString(java.util.HashMap.class, "count", "private transient");
		}
	
		this.volatileField = "something to keep the compiler from complaining...";
		assertEquals(this.volatileField, this.volatileField);
		this.verifyAttributeModifierDisplayString(this.getClass(), "volatileField", "private volatile");
	}
	
	private void verifyClassModifierDisplayString(Class javaClass, String expected) throws Exception {
		MWModifier m = this.modifierForClass(javaClass);
		assertEquals("invalid display string for " + javaClass.getName(),
				expected, m.displayString());
	}
	
	private MWModifier modifierForClass(Class javaClass) throws ExternalClassNotFoundException {
		return this.fullyPopulatedTypeFor(javaClass).getModifier();
	}
	
	private void verifyMethodModifierDisplayString(Class javaClass, String methodSignature, String expected) throws Exception {
		MWModifier m = this.modifierForMethod(javaClass, methodSignature);
		assertEquals("invalid display string for " + javaClass.getName() + "#" + methodSignature,
				expected, m.displayString());
	}
	
	private MWModifier modifierForMethod(Class javaClass, String methodSignature) throws ExternalClassNotFoundException {
		return this.fullyPopulatedTypeFor(javaClass).methodWithSignature(methodSignature).getModifier();
	}
	
	private void verifyAttributeModifierDisplayString(Class javaClass, String attributeName, String expected) throws ExternalClassNotFoundException {
		MWModifier m = this.modifierForAttribute(javaClass, attributeName);
		assertEquals("invalid display string for " + javaClass.getName() + "." + attributeName,
				expected, m.displayString());
	}
	
	private MWModifier modifierForAttribute(Class javaClass, String attributeName) throws ExternalClassNotFoundException {
		return this.fullyPopulatedTypeFor(javaClass).attributeNamed(attributeName).getModifier();
	}
	
	public void testCanBeSetAbstract() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.lang.Boolean.class);	// final
		assertTrue( ! m.canBeSetAbstract());
		m = this.modifierForClass(java.util.Vector.class);
		assertTrue(m.canBeSetAbstract());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");	// unsupported
		assertTrue( ! m.canBeSetAbstract());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "AbstractNodeModel(org.eclipse.persistence.tools.workbench.utility.node.Node)");	// ctor
		assertTrue( ! m.canBeSetAbstract());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "markDirty()");	// private
		assertTrue( ! m.canBeSetAbstract());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel.class, "legacy60BuildStandardDescriptor()");	// static
		assertTrue( ! m.canBeSetAbstract());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "children()");	// final
		assertTrue( ! m.canBeSetAbstract());
		m = this.modifierForMethod(java.lang.Object.class, "hashCode()");	// native
		assertTrue( ! m.canBeSetAbstract());
		m = this.modifierForMethod(java.util.Vector.class, "firstElement()");	// synchronized
		assertTrue( ! m.canBeSetAbstract());
		m = this.modifierForMethod(java.util.Vector.class, "clear()");	// concrete class
		assertTrue( ! m.canBeSetAbstract());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");
		assertTrue(m.canBeSetAbstract());
	}
	
	public void testCanBeSetFinal() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.util.AbstractList.class);	// abstract
		assertTrue( ! m.canBeSetFinal());
		m = this.modifierForClass(java.util.Vector.class);
		assertTrue(m.canBeSetFinal());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.DefaultProblem.class, "source");
		assertTrue(m.canBeSetFinal());
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");
		assertTrue( ! m.canBeSetFinal());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "AbstractNodeModel(org.eclipse.persistence.tools.workbench.utility.node.Node)");	// ctor
		assertTrue( ! m.canBeSetFinal());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.uitools.Displayable.class, "displayString()");	// abstract
		assertTrue( ! m.canBeSetFinal());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");
		assertTrue(m.canBeSetFinal());
	}
	
	public void testCanBeSetNative() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.util.Vector.class);	// unsupported
		assertTrue( ! m.canBeSetNative());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");	// unsupported
		assertTrue( ! m.canBeSetNative());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "AbstractNodeModel(org.eclipse.persistence.tools.workbench.utility.node.Node)");	// ctor
		assertTrue( ! m.canBeSetNative());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.uitools.Displayable.class, "displayString()");	// abstract
		assertTrue( ! m.canBeSetNative());
		// need a strictfp method...
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");
		assertTrue(m.canBeSetNative());
	}
	
	public void testCanBeSetPackage() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.util.Vector.class);	// unrestricted
		assertTrue(m.canBeSetPackage());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");	// unrestricted
		assertTrue(m.canBeSetPackage());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");	// unrestricted
		assertTrue(m.canBeSetPackage());
	}
	
	public void testCanBeSetPrivate() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.util.Vector.class);	// top level class
		assertTrue( ! m.canBeSetPrivate());
		m = this.modifierForClass(java.util.Map.Entry.class);
		assertTrue(m.canBeSetPrivate());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");	// unrestricted
		assertTrue(m.canBeSetPrivate());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.uitools.Displayable.class, "displayString()");	// abstract
		assertTrue( ! m.canBeSetPrivate());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");
		assertTrue(m.canBeSetPrivate());
	}
	
	public void testCanBeSetProtected() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.util.Vector.class);	// top level class
		assertTrue( ! m.canBeSetProtected());
		m = this.modifierForClass(java.util.Map.Entry.class);
		assertTrue(m.canBeSetProtected());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");	// unrestricted
		assertTrue(m.canBeSetProtected());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");	// unrestricted
		assertTrue(m.canBeSetProtected());
	}
	
	public void testCanBeSetPublic() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.util.Map.Entry.class);	// unrestricted
		assertTrue(m.canBeSetPublic());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");	// unrestricted
		assertTrue(m.canBeSetPublic());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");	// unrestricted
		assertTrue(m.canBeSetPublic());
	}
	
	public void testCanBeSetStatic() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.util.Vector.class);	// top level class
		assertTrue( ! m.canBeSetStatic());
		m = this.modifierForClass(java.util.Map.Entry.class);
		assertTrue(m.canBeSetStatic());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");	// unrestricted
		assertTrue(m.canBeSetStatic());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "AbstractNodeModel(org.eclipse.persistence.tools.workbench.utility.node.Node)");	// ctor
		assertTrue( ! m.canBeSetStatic());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.uitools.Displayable.class, "displayString()");	// abstract
		assertTrue( ! m.canBeSetStatic());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");
		assertTrue(m.canBeSetStatic());
	}
	
	public void testCanBeSetStrict() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.util.Vector.class);	// unrestricted
		assertTrue(m.canBeSetStrict());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");	// unsupported
		assertTrue( ! m.canBeSetStrict());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");	// unrestricted
		assertTrue(m.canBeSetStrict());
	}
	
	public void testCanBeSetSychronized() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.util.Vector.class);	// unsupported
		assertTrue( ! m.canBeSetSynchronized());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");	// unsupported
		assertTrue( ! m.canBeSetSynchronized());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "AbstractNodeModel(org.eclipse.persistence.tools.workbench.utility.node.Node)");	// ctor
		assertTrue( ! m.canBeSetSynchronized());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.uitools.Displayable.class, "displayString()");	// abstract
		assertTrue( ! m.canBeSetSynchronized());
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");
		assertTrue(m.canBeSetSynchronized());
	}
	
	public void testCanBeSetTransient() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.util.Vector.class);	// unsupported
		assertTrue( ! m.canBeSetTransient());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");	// unrestricted
		assertTrue(m.canBeSetTransient());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");	// unsupported
		assertTrue( ! m.canBeSetTransient());
	}
	
	public void testCanBeSetVolatile() throws Exception {
		MWModifier m;
		m = this.modifierForClass(java.util.Vector.class);	// unsupported
		assertTrue( ! m.canBeSetVolatile());
	
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "COMMENT_PROPERTY");	// final
		assertTrue( ! m.canBeSetVolatile());
		m = this.modifierForAttribute(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "parent");
		assertTrue(m.canBeSetVolatile());
	
		m = this.modifierForMethod(org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel.class, "initialize()");	// unsupported
		assertTrue( ! m.canBeSetVolatile());
	}
	
	public void testSetAbstract() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setAbstract(false);
		assertTrue("invalid modifier: " + this.modifier, ! this.modifier.isAbstract());
		this.modifier.setAbstract(true);
		assertTrue("invalid modifier: " + this.modifier, this.modifier.isAbstract());
	}
	
	public void testSetFinal() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setFinal(false);
		assertTrue("invalid modifier: " + this.modifier, ! this.modifier.isFinal());
		this.modifier.setFinal(true);
		assertTrue("invalid modifier: " + this.modifier, this.modifier.isFinal());
	}
	
	public void testSetNative() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setNative(false);
		assertTrue("invalid modifier: " + this.modifier, ! this.modifier.isNative());
		this.modifier.setNative(true);
		assertTrue("invalid modifier: " + this.modifier, this.modifier.isNative());
	}
	
	public void testSetPackage() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setPackage(false);
		assertTrue("invalid modifier: " + this.modifier,
			this.modifier.isPublic() &&
			! this.modifier.isProtected() &&
			! this.modifier.isPackage() &&
			! this.modifier.isPrivate()
		);
		this.modifier.setPackage(true);
		assertTrue("invalid modifier: " + this.modifier,
			! this.modifier.isPublic() &&
			! this.modifier.isProtected() &&
			this.modifier.isPackage() &&
			! this.modifier.isPrivate()
		);
	}
	
	public void testSetPrivate() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setPublic(false);		// need to clean up flags a bit...
		this.modifier.setProtected(false);
		this.modifier.setPrivate(false);
		assertTrue("invalid modifier: " + this.modifier,
			! this.modifier.isPublic() &&
			! this.modifier.isProtected() &&
			this.modifier.isPackage() &&
			! this.modifier.isPrivate()
		);
		this.modifier.setPrivate(true);
		assertTrue("invalid modifier: " + this.modifier,
			! this.modifier.isPublic() &&
			! this.modifier.isProtected() &&
			! this.modifier.isPackage() &&
			this.modifier.isPrivate()
		);
	}
	
	public void testSetProtected() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setPublic(false);		// need to clean up flags a bit...
		this.modifier.setProtected(false);
		this.modifier.setPrivate(false);
		assertTrue("invalid modifier: " + this.modifier,
			! this.modifier.isPublic() &&
			! this.modifier.isProtected() &&
			this.modifier.isPackage() &&
			! this.modifier.isPrivate()
		);
		this.modifier.setProtected(true);
		assertTrue("invalid modifier: " + this.modifier,
			! this.modifier.isPublic() &&
			this.modifier.isProtected() &&
			! this.modifier.isPackage() &&
			! this.modifier.isPrivate()
		);
	}
	
	public void testSetPublic() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setPublic(false);		// need to clean up flags a bit...
		this.modifier.setProtected(false);
		this.modifier.setPrivate(false);
		assertTrue("invalid modifier: " + this.modifier,
			! this.modifier.isPublic() &&
			! this.modifier.isProtected() &&
			this.modifier.isPackage() &&
			! this.modifier.isPrivate()
		);
		this.modifier.setPublic(true);
		assertTrue("invalid modifier: " + this.modifier,
			this.modifier.isPublic() &&
			! this.modifier.isProtected() &&
			! this.modifier.isPackage() &&
			! this.modifier.isPrivate()
		);
	}
	
	public void testSetStatic() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setStatic(false);
		assertTrue("invalid modifier: " + this.modifier, ! this.modifier.isStatic());
		this.modifier.setStatic(true);
		assertTrue("invalid modifier: " + this.modifier, this.modifier.isStatic());
	}
	
	public void testSetStrict() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setStrict(false);
		assertTrue("invalid modifier: " + this.modifier, ! this.modifier.isStrict());
		this.modifier.setStrict(true);
		assertTrue("invalid modifier: " + this.modifier, this.modifier.isStrict());
	}
	
	public void testSetSynchronized() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setSynchronized(false);
		assertTrue("invalid modifier: " + this.modifier, ! this.modifier.isSynchronized());
		this.modifier.setSynchronized(true);
		assertTrue("invalid modifier: " + this.modifier, this.modifier.isSynchronized());
	}
	
	public void testSetTransient() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setTransient(false);
		assertTrue("invalid modifier: " + this.modifier, ! this.modifier.isTransient());
		this.modifier.setTransient(true);
		assertTrue("invalid modifier: " + this.modifier, this.modifier.isTransient());
	}
	
	public void testSetVolatile() {
		this.setAllFlagsIn(this.modifier);
		this.modifier.setVolatile(false);
		assertTrue("invalid modifier: " + this.modifier, ! this.modifier.isVolatile());
		this.modifier.setVolatile(true);
		assertTrue("invalid modifier: " + this.modifier, this.modifier.isVolatile());
	}
	
	private void setAllFlagsIn(MWModifier m) {
		m.setCode(0xFFFFFFFF);
	}
	
	private MWClass fullyPopulatedTypeFor(Class javaClass) throws ExternalClassNotFoundException {
		MWClass result = this.project.typeFor(javaClass);
		result.refresh();
		return result;
	}
	
}
