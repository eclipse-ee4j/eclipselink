/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.meta;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NullPreferences;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class MWClassTests extends TestCase {
    private MWRelationalProject project;

    private MWClass objectType;
    private MWClass abstractCollectionType;
    private MWClass abstractListType;
    private MWClass arrayListType;
    private MWClass vectorType;
    private MWClass stackType;
    private MWClass abstractSetType;
    private MWClass treeSetType;

    private MWClass mapType;
    private MWClass mapEntryType;

    private MWClass listType;
    private MWClass sortedSetType;

    private MWClass stringType;
    private MWClass vhiType;


    private static final boolean JDK7 = jdkIsVersion("1.7");
    private static final boolean JDK8 = jdkIsVersion("1.8");

    private static boolean jdkIsVersion(String version) {
        return System.getProperty("java.version").indexOf(version) != -1;
    }

    public static Test suite() {
        return new TestSuite(MWClassTests.class);
    }

    public MWClassTests(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.project = this.buildProject();

        this.objectType = this.fullyPopulatedTypeFor(java.lang.Object.class);
        this.abstractCollectionType = this.fullyPopulatedTypeFor(java.util.AbstractCollection.class);
        this.abstractListType = this.fullyPopulatedTypeFor(java.util.AbstractList.class);
        this.arrayListType = this.fullyPopulatedTypeFor(java.util.ArrayList.class);
        this.vectorType = this.fullyPopulatedTypeFor(java.util.Vector.class);
        this.stackType = this.fullyPopulatedTypeFor(java.util.Stack.class);
        this.abstractSetType = this.fullyPopulatedTypeFor(java.util.AbstractSet.class);
        this.treeSetType = this.fullyPopulatedTypeFor(java.util.TreeSet.class);

        this.mapType = this.fullyPopulatedTypeFor(java.util.Map.class);
        this.mapEntryType = this.fullyPopulatedTypeFor(java.util.Map.Entry.class);

        this.listType = this.fullyPopulatedTypeFor(java.util.List.class);
        this.sortedSetType = this.fullyPopulatedTypeFor(java.util.SortedSet.class);

        this.stringType = this.fullyPopulatedTypeFor(java.lang.String.class);
        this.vhiType = this.fullyPopulatedTypeFor(org.eclipse.persistence.indirection.ValueHolderInterface.class);
    }

    private MWRelationalProject buildProject() {
        return new MWRelationalProject(
                this.getClass().getName(),
                MappingsModelTestTools.buildSPIManager(),
                DatabasePlatformRepository.getDefault().platformNamed("Oracle")
        );
    }

    @Override
    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testState() throws Exception {
        assertEquals(this.project.getRepository(), this.stringType.getRepository());

        assertEquals("java.lang.String", this.stringType.getName());
        assertEquals("String", this.stringType.shortName());
        assertEquals("java.lang", this.stringType.packageName());
        assertEquals("java.lang", this.stringType.packageDisplayName());

        assertEquals(this.mapType, this.mapEntryType.getDeclaringType());

        assertTrue(this.stringType.getModifier().isPublic());
        assertTrue(this.stringType.getModifier().isFinal());
        assertTrue( ! this.stringType.isInterface());
        assertTrue( ! this.stringType.isPrimitive());
        assertEquals(this.project.typeFor(java.lang.Object.class), this.stringType.getSuperclass());

        int stringInterfacesSize = this.stringType.interfacesSize();
        assertEquals(3, stringInterfacesSize);
        assertTrue(CollectionTools.contains(this.stringType.interfaces(), this.fullyPopulatedTypeFor(java.lang.Comparable.class)));
        assertTrue(CollectionTools.contains(this.stringType.interfaces(), this.fullyPopulatedTypeFor(java.io.Serializable.class)));

        int stringAttributesSize = this.stringType.attributesSize();
        if (JDK8) {
            assertEquals(5, stringAttributesSize);
        } else if (JDK7) {
            assertEquals(7, stringAttributesSize);
            assertTrue(this.stringType.attributeNamed("hash32") != null);
            assertTrue(this.stringType.attributeNamed("HASHING_SEED") != null);
        } else {
            fail("Unknown JDK...");
        }
        assertTrue(this.stringType.attributeNamed("value") != null);
        assertTrue(this.stringType.attributeNamed("hash") != null);
        assertTrue(this.stringType.attributeNamed("serialVersionUID") != null);
        assertTrue(this.stringType.attributeNamed("serialPersistentFields") != null);
        assertTrue(this.stringType.attributeNamed("CASE_INSENSITIVE_ORDER") != null);

        assertEquals(0, this.stringType.ejb20AttributesSize());

        int stringMethodsSize = this.stringType.methodsSize();
        if (JDK8) {
            assertEquals(92, stringMethodsSize);
        } else if (JDK7) {
            assertEquals(89, stringMethodsSize);
        } else {
            fail("Unknown JDK...");
        }

        assertTrue(this.stringType.methodWithSignature("String()") != null);
        assertTrue(this.stringType.methodWithSignature("String(byte[], int, int)") != null);
        assertTrue(this.stringType.methodWithSignature("valueOf(double)") != null);
        assertTrue(this.stringType.methodWithSignature("endsWith(java.lang.String)") != null);

        assertTrue(CollectionTools.contains(this.mapType.types(), this.mapEntryType));
    }

    public void testPrimitiveState() throws Exception {
        MWClass intType = this.fullyPopulatedTypeFor(int.class);
        assertEquals(this.project.getRepository(), intType.getRepository());

        assertEquals("int", intType.getName());
        assertEquals("int", intType.shortName());
        assertEquals("", intType.packageName());
        assertEquals("", intType.packageDisplayName());

        assertTrue(intType.getModifier().isPublic());
        assertTrue(intType.getModifier().isAbstract());    // ???
        assertTrue(intType.getModifier().isFinal());    // ???
        assertTrue( ! intType.isInterface());
        assertTrue(intType.isPrimitive());
        assertEquals(null, intType.getSuperclass());

        assertEquals(0, intType.interfacesSize());
        assertEquals(0, intType.attributesSize());
        assertEquals(0, intType.ejb20AttributesSize());
        assertEquals(0, intType.methodsSize());
    }

    public void testDefaults() throws Exception {
        MWClass primType = this.project.typeFor(float.class);
        assertTrue(primType.isPrimitive());
        assertEquals(null, primType.getSuperclass());

        MWClass regularType = this.project.typeNamed("foo.bar.Baz");
        assertTrue( ! regularType.isPrimitive());
        assertEquals(this.objectType, regularType.getSuperclass());
    }

    public void testPrimitiveClassNamed() throws Exception {
        assertEquals(boolean.class, MWClass.primitiveClassNamed("boolean"));
        assertEquals(byte.class, MWClass.primitiveClassNamed("byte"));
        assertEquals(short.class, MWClass.primitiveClassNamed("short"));
        assertEquals(int.class, MWClass.primitiveClassNamed("int"));
        assertEquals(long.class, MWClass.primitiveClassNamed("long"));
        assertEquals(float.class, MWClass.primitiveClassNamed("float"));
        assertEquals(double.class, MWClass.primitiveClassNamed("double"));
    }

    public void testNonReferenceClasses() throws Exception {
        assertTrue(MWClass.nonReferenceClassNamesContains("void"));
        assertTrue(MWClass.nonReferenceClassNamesContains("boolean"));
        assertEquals(int.class, MWClass.primitiveClassNamed("int"));
    }

    public void testSetInterface() throws Exception {
        MWClass intType = this.fullyPopulatedTypeFor(int.class);
        boolean exCaught = false;
        try {
            intType.setInterface(true);
        } catch (IllegalStateException ex) {
            exCaught = true;
        }
        assertTrue("IllegalStateException not thrown", exCaught);

        MWClass regularType = this.project.typeNamed("foo.bar.Baz");
        assertEquals(this.objectType, regularType.getSuperclass());
        regularType.setInterface(true);
        assertEquals(null, regularType.getSuperclass());
    }

    public void testSetSuperclass() throws Exception {
        boolean exCaught = false;
        try {
            this.mapType.setSuperclass(this.objectType);
        } catch (IllegalStateException ex) {
            exCaught = true;
        }
        assertTrue("IllegalStateException not thrown", exCaught);

        MWClass intType = this.fullyPopulatedTypeFor(int.class);
        exCaught = false;
        try {
            intType.setSuperclass(this.objectType);
        } catch (IllegalStateException ex) {
            exCaught = true;
        }
        assertTrue("IllegalStateException not thrown", exCaught);

        MWClass regularType = this.project.typeNamed("foo.bar.Baz");
        assertEquals(this.objectType, regularType.getSuperclass());
        exCaught = false;
        try {
            regularType.setSuperclass(null);
        } catch (IllegalStateException ex) {
            exCaught = true;
        }
        assertTrue("IllegalStateException not thrown", exCaught);
    }

    public void testIsStub() throws Exception {
        assertTrue( ! this.objectType.isStub());

        MWClass bazType = this.project.typeNamed("foo.bar.Baz");
        assertTrue(bazType.isStub());
        MWClassAttribute attr = bazType.addAttribute("fop");
        assertFalse(bazType.isStub());
        bazType.removeAttribute(attr);
        assertTrue(bazType.isStub());
        bazType.setSuperclass(this.project.typeNamed("foo.bar.Bay"));
        assertFalse(bazType.isStub());
        bazType.setSuperclass(this.objectType);
        assertTrue(bazType.isStub());

        // interface
        bazType.setInterface(true);
        assertTrue(bazType.isStub());
        bazType.getModifier().setPackage(true);
        assertFalse(bazType.isStub());
        bazType.setInterface(false);
        assertFalse(bazType.isStub());
        bazType.getModifier().setPackage(false);
        assertTrue(bazType.isStub());
    }

    public void testInterfaces() throws Exception {
        int size = CollectionTools.size(this.treeSetType.allInterfacesWithoutDuplicates());
        assertEquals(7, size);

        assertTrue(this.treeSetType.allInterfacesContains(this.project.typeNamed("java.lang.Iterable")));
        assertTrue(this.treeSetType.allInterfacesContains(this.project.typeNamed("java.util.NavigableSet")));
        assertTrue(this.treeSetType.allInterfacesContains(this.project.typeFor(java.util.SortedSet.class)));
        assertTrue(this.treeSetType.allInterfacesContains(this.project.typeFor(java.io.Serializable.class)));
        assertTrue(this.treeSetType.allInterfacesContains(this.project.typeFor(java.lang.Cloneable.class)));
        assertTrue(this.treeSetType.allInterfacesContains(this.project.typeFor(java.util.Set.class)));
        assertTrue(this.treeSetType.allInterfacesContains(this.project.typeFor(java.util.Collection.class)));
    }

    public void testAttributes() throws Exception {
        assertEquals(8, CollectionTools.size(this.stackType.allAttributes()));
        assertEquals(1, CollectionTools.size(this.stackType.attributes()));
        assertTrue(CollectionTools.contains(this.stackType.allAttributes(), this.stackType.attributeNamedFromAll("elementData")));
        assertTrue( ! CollectionTools.contains(this.stackType.attributes(), this.stackType.attributeNamed("elementData")));

        int stringAllAttributesSize = CollectionTools.size(this.stringType.allAttributes());
        if (JDK8) {
            assertEquals(5, stringAllAttributesSize);
            assertEquals(2, CollectionTools.size(this.stringType.instanceVariables()));
        } else if (JDK7) {
            assertEquals(7, stringAllAttributesSize);
            assertEquals(3, CollectionTools.size(this.stringType.instanceVariables()));
        } else {
            fail("Unknown JDK...");
        }
    }

    public void testMethods() throws Exception {
        int abstractCollectionAllMethodsSize = CollectionTools.size(this.abstractCollectionType.allMethods());
        assertEquals(30, abstractCollectionAllMethodsSize);
        assertEquals(1, CollectionTools.size(this.abstractCollectionType.constructors()));

        int abstractCollectionAllNonConstructorsSize = CollectionTools.size(this.abstractCollectionType.allNonConstructors());
        assertEquals(28, abstractCollectionAllNonConstructorsSize);

        int stringInstanceMethodsSize = CollectionTools.size(this.stringType.instanceMethods());
        int stringStaticMethodsSize = CollectionTools.size(this.stringType.staticMethods());
        int stringAllStaticMethodsSize = CollectionTools.size(this.stringType.allStaticMethods());
        assertEquals(56, stringInstanceMethodsSize);
        if (JDK8) {
            assertEquals(20, stringStaticMethodsSize);
            assertEquals(21, stringAllStaticMethodsSize);
        } else if (JDK7) {
            assertEquals(16, stringStaticMethodsSize);
            assertEquals(17, stringAllStaticMethodsSize);
        } else {
            fail("Unknown JDK...");
        }

        MWClass classType = this.fullyPopulatedTypeFor(java.lang.Class.class);

        MWClass fooType = this.project.typeNamed("bar.baz.Foo");
        MWMethod method;
        method = fooType.addMethod("extractClassFrom", classType);
        method.getModifier().setStatic(true);
        method.addMethodParameter(this.fullyPopulatedTypeFor(org.eclipse.persistence.sessions.Record.class));
        assertEquals(1, CollectionTools.size(fooType.candidateClassExtractionMethods()));
        assertTrue(CollectionTools.contains(fooType.candidateClassExtractionMethods(),
                fooType.methodWithSignatureFromAll("extractClassFrom(org.eclipse.persistence.sessions.Record)")));
        method = fooType.addMethod("extractClassFrom", classType);
        method.getModifier().setStatic(true);
        method.addMethodParameter(this.fullyPopulatedTypeFor(org.eclipse.persistence.sessions.Record.class));
        assertEquals(2, CollectionTools.size(fooType.candidateClassExtractionMethods()));
        assertTrue(CollectionTools.contains(fooType.candidateClassExtractionMethods(),
                fooType.methodWithSignatureFromAll("extractClassFrom(org.eclipse.persistence.sessions.Record)")));

        method = fooType.addMethod("cloneForTopLink", this.objectType);
        assertEquals(2, CollectionTools.size(fooType.candidateCloneMethods()));
        assertTrue(CollectionTools.contains(fooType.candidateCloneMethods(),
                fooType.methodWithSignatureFromAll("cloneForTopLink()")));
        assertTrue(CollectionTools.contains(fooType.candidateCloneMethods(),
                fooType.methodWithSignatureFromAll("clone()")));

        method = fooType.addMethod("buildNewInstance", this.objectType);
        method.getModifier().setStatic(true);
        assertEquals(1, CollectionTools.size(fooType.candidateInstantiationMethods()));
        assertTrue(CollectionTools.contains(fooType.candidateInstantiationMethods(),
                fooType.methodWithSignatureFromAll("buildNewInstance()")));

        MWClass fooFactoryType = this.project.typeNamed("bar.baz.FooFactory");
        method = fooFactoryType.addMethod("instance", fooFactoryType);
        method.getModifier().setStatic(true);
        method = fooFactoryType.addMethod("newFoo", fooType);
        assertEquals(2, CollectionTools.size(fooFactoryType.candidateFactoryInstantiationMethodsFor(fooType)));
        assertTrue(CollectionTools.contains(fooFactoryType.candidateFactoryInstantiationMethodsFor(fooType),
                fooFactoryType.methodWithSignatureFromAll("newFoo()")));
        assertTrue(CollectionTools.contains(fooFactoryType.candidateFactoryInstantiationMethodsFor(fooType),
                fooFactoryType.methodWithSignatureFromAll("clone()")));
        assertEquals(1, CollectionTools.size(fooFactoryType.candidateFactoryMethods()));
        assertTrue(CollectionTools.contains(fooFactoryType.candidateFactoryMethods(),
                fooFactoryType.methodWithSignatureFromAll("instance()")));

        MWClassAttribute attribute;
        attribute = fooType.addAttribute("attr1", this.stringType);
        method = fooType.addMethod("getAttr1", this.stringType);
        assertTrue(CollectionTools.contains(attribute.candidateGetMethods(), method));
        assertEquals(method, attribute.standardGetMethod());
        method = fooType.addMethod("setAttr1");
        method.addMethodParameter(this.stringType);
        assertTrue(CollectionTools.contains(attribute.candidateSetMethods(), method));
        assertEquals(method, attribute.standardSetMethod());

        attribute = fooType.addAttribute("attr2", this.vhiType);
        attribute.setValueType(this.stringType);
        method = fooType.addMethod("getAttr2", this.stringType);
        method = fooType.addMethod("setAttr2");
        method.addMethodParameter(this.stringType);
        method = fooType.addMethod("getAttr2Holder", this.vhiType);
        assertEquals(method, attribute.standardGetMethod());
        method = fooType.addMethod("setAttr2Holder");
        method.addMethodParameter(this.vhiType);
        assertEquals(method, attribute.standardSetMethod());

        assertTrue(fooType.methodWithSignature("setAttr2Holder(org.eclipse.persistence.indirection.ValueHolderInterface)") != null);
        assertTrue(fooType.methodWithSignature("equals()") == null);
        assertTrue(fooType.methodWithSignatureFromAll("equals(java.lang.Object)") != null);

        assertTrue(fooType.zeroArgumentConstructor() == null); // ???
        assertTrue(this.stringType.zeroArgumentConstructor() != null); // ???
    }

    public void testAssignment() throws Exception {
        assertTrue(this.objectType.isAssignableFrom(this.abstractCollectionType));
        assertTrue( ! this.abstractCollectionType.isAssignableFrom(this.objectType));
        assertTrue(this.abstractCollectionType.isAssignableTo(this.objectType));
        assertTrue(this.objectType.isAssignableFrom(this.objectType));
        assertTrue(this.listType.isAssignableFrom(this.vectorType));
        assertTrue( ! this.vectorType.isAssignableFrom(this.listType));
        assertTrue(this.vectorType.isAssignableToCollection());
        assertTrue(this.objectType.isAssignableFrom(this.sortedSetType));
        assertTrue( ! this.sortedSetType.isAssignableFrom(this.objectType));
        MWClass indirectListType = this.project.typeFor(org.eclipse.persistence.indirection.IndirectList.class);
        assertTrue(indirectListType.isAssignableToIndirectContainer());
        assertTrue(this.sortedSetType.isAssignableToSet());
        assertTrue(this.treeSetType.isAssignableToSet());
    }

    public void testFuzzyAssignment() {
        MWClass collectionType = this.project.typeFor(java.util.Collection.class);
        // list1Type starts off as a stub
        MWClass list1Type = this.project.typeNamed("foo.bar.FooList1");
        assertTrue(list1Type.isStub());

        MWClass list2Type = this.project.typeNamed("foo.bar.FooList2");
        list2Type.addZeroArgumentConstructor();
        list2Type.setSuperclass(list1Type);
        // strict tests:
        assertFalse(list2Type.isAssignableTo(collectionType));
        assertFalse(collectionType.isAssignableFrom(list2Type));
        // fuzzy tests:
        assertTrue(list2Type.mightBeAssignableTo(collectionType));
        assertTrue(collectionType.mightBeAssignableFrom(list2Type));

        // now convert list1Type to non-stub
        list1Type.setSuperclass(this.arrayListType);
        // strict tests:
        assertTrue(list2Type.isAssignableTo(collectionType));
        assertTrue(collectionType.isAssignableFrom(list2Type));
        // fuzzy tests:
        assertTrue(list2Type.mightBeAssignableTo(collectionType));
        assertTrue(collectionType.mightBeAssignableFrom(list2Type));
    }

    public void testHierarchy() throws Exception {
        assertEquals(4, CollectionTools.size(this.treeSetType.lineage()));

        int treeSetLineageIncludingInterfacesSize = CollectionTools.set(this.treeSetType.lineageIncludingInterfaces()).size();
        assertEquals(11, treeSetLineageIncludingInterfacesSize);

        assertEquals(2, CollectionTools.size(this.treeSetType.lineageTo(this.abstractSetType)));
        assertEquals(3, CollectionTools.size(this.treeSetType.lineageTo(this.abstractCollectionType)));

        assertTrue(this.treeSetType.lineageContains(this.treeSetType));
        assertTrue(this.treeSetType.lineageContains(this.abstractSetType));
        assertTrue(this.treeSetType.lineageContains(this.abstractCollectionType));
        assertTrue(this.treeSetType.lineageContains(this.objectType));

        assertEquals(3, CollectionTools.size(this.treeSetType.superclasses()));
        assertTrue( ! CollectionTools.contains(this.treeSetType.superclasses(), this.treeSetType));
        assertTrue(CollectionTools.contains(this.treeSetType.superclasses(), this.abstractSetType));
        assertTrue(CollectionTools.contains(this.treeSetType.superclasses(), this.abstractCollectionType));
        assertTrue(CollectionTools.contains(this.treeSetType.superclasses(), this.objectType));

        assertEquals(0, CollectionTools.size(this.objectType.superclasses()));

        assertTrue(CollectionTools.contains(this.abstractCollectionType.subclasses(), this.abstractListType));
        assertTrue(CollectionTools.contains(this.abstractCollectionType.subclasses(), this.abstractSetType));
        assertTrue( ! CollectionTools.contains(this.abstractCollectionType.subclasses(), this.arrayListType));
        assertTrue(CollectionTools.contains(this.abstractCollectionType.allSubclasses(), this.arrayListType));
        assertTrue( ! CollectionTools.contains(this.abstractCollectionType.allSubclasses(), this.objectType));
        assertTrue(CollectionTools.contains(this.abstractCollectionType.hierarchy(), this.objectType));
    }

    public void testTableName() throws Exception {
        assertEquals("OBJECT", this.objectType.defaultTableNameWithLength(20));
        assertEquals("OBJ", this.objectType.defaultTableNameWithLength(3));
    }

    public void testPrinting() throws Exception {
        assertEquals("String", this.stringType.displayString());
        assertEquals("String (java.lang)", this.stringType.displayStringWithPackage());

        MWClass intType = this.fullyPopulatedTypeFor(int.class);
        assertEquals("int", intType.displayString());
        assertEquals("int", intType.displayStringWithPackage());
    }

    public void testPackageInterface() throws Exception {
        this.project.getRepository().addClasspathEntry(Classpath.locationFor(PackageInterface.class));
        MWClass pi = this.fullyPopulatedTypeFor(PackageInterface.class);
        assertTrue(pi.isInterface());
        assertTrue(pi.getModifier().isPackage());
    }

    public void testMarkerInterface() throws Exception {
        // make sure an actual "marker" interface doesn't load up as a "stub" - which would be a nuisance;
        // a "marker" interface ends up marked 'public abstract', so we should be OK...
        this.project.getRepository().addClasspathEntry(Classpath.locationFor(MarkerInterface.class));
        MWClass mi = this.fullyPopulatedTypeFor(MarkerInterface.class);
        assertTrue(mi.isInterface());
        assertTrue(mi.getModifier().isPublic());
        assertFalse(mi.isStub());
    }

    public void testSetName_cannotBeNull() {
        String typeName = "bar.baz.Foo";
        MWClass fooType = this.project.typeNamed(typeName);
        boolean exCaught = false;
        try {
            fooType.setName(null);
            fail("bogus name: " + fooType);
        } catch (NullPointerException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
        assertEquals(typeName, fooType.getName());
    }

    public void testSetName_cannotBeEmpty() {
        String typeName = "bar.baz.Foo";
        MWClass fooType = this.project.typeNamed(typeName);
        boolean exCaught = false;
        try {
            fooType.setName("");
            fail("bogus name: " + fooType);
        } catch (IllegalArgumentException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
        assertEquals(typeName, fooType.getName());
    }

    public void testSetName_cannotBeArray() {
        String typeName = "bar.baz.Foo";
        MWClass fooType = this.project.typeNamed(typeName);
        boolean exCaught = false;
        try {
            fooType.setName(new Object[0].getClass().getName());
            fail("bogus name: " + fooType);
        } catch (IllegalArgumentException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
        assertEquals(typeName, fooType.getName());
    }

    public void testSetName_cannotBeCoreType() {
        String typeName = "bar.baz.Foo";
        MWClass fooType = this.project.typeNamed(typeName);
        boolean exCaught = false;
        try {
            fooType.setName(java.lang.Object.class.getName());
            fail("bogus name: " + fooType);
        } catch (IllegalArgumentException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
        assertEquals(typeName, fooType.getName());
    }

    public void testSetName_cannotBeCoreTypeCaseInsensitive() {
        String typeName = "bar.baz.Foo";
        MWClass fooType = this.project.typeNamed(typeName);
        boolean exCaught = false;
        try {
            fooType.setName(java.lang.Object.class.getName().toUpperCase());
            fail("bogus name: " + fooType);
        } catch (IllegalArgumentException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
        assertEquals(typeName, fooType.getName());
    }

    public void testSetName_cannotBeDuplicate() {
        String typeName = "bar.baz.Foo";
        MWClass fooType = this.project.typeNamed(typeName);
        MWClass barType = this.project.typeNamed("bar.baz.Bar");
        boolean exCaught = false;
        try {
            fooType.setName(barType.getName());
            fail("bogus name: " + fooType);
        } catch (IllegalArgumentException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
        assertEquals(typeName, fooType.getName());
    }

    public void testSetName_cannotBeDuplicateCaseInsensitive() {
        String typeName = "bar.baz.Foo";
        MWClass fooType = this.project.typeNamed(typeName);
        MWClass barType = this.project.typeNamed("bar.baz.Bar");
        boolean exCaught = false;
        try {
            fooType.setName(barType.getName().toUpperCase());
            fail("bogus name: " + fooType);
        } catch (IllegalArgumentException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
        assertEquals(typeName, fooType.getName());
    }

    public void testSetName_garbageCollectionUnusedType() {
        MWClass fooType = this.project.typeNamed("bar.baz.Foo");
        MWClass barType = this.project.typeNamed("bar.baz.Bar");
        MWClass garbage = this.project.getRepository().typeNamedIgnoreCase(barType.getName());
        assertNull(garbage);
        fooType.setName(barType.getName());
        assertEquals(barType.getName(), fooType.getName());
    }

    public void testSetName_changeCase() {
        MWClass fooType = this.project.typeNamed("bar.baz.Foo");
        String newName = fooType.getName().toUpperCase();
        fooType.setName(newName);
        assertEquals(newName, fooType.getName());
    }

    /**
     * test the problem where we read a project in,
     * rename a class/descriptor pair by only changing the
     * case of the name, and write it back out;
     * then when we read it back in the class and
     * descriptor are gone because the files were deleted;
     * this only occurs on Windows, where file names are
     * case-insensitive
     */
    public void testSetName_changeCase_IO() throws Exception {
        String oldName = "bar.baz.Foo";
        String newName = oldName.toUpperCase();
        MWClass fooType = this.project.typeNamed(oldName);
        MWDescriptor fooDescriptor = this.project.addDescriptorForType(fooType);

        assertEquals(oldName, this.project.getRepository().typeNamedIgnoreCase(oldName).getName());
        assertEquals(oldName, this.project.getRepository().typeNamedIgnoreCase(newName).getName());
        assertEquals(fooDescriptor, this.project.descriptorNamed(oldName));
        assertNull(this.project.descriptorNamed(newName));

        this.project.setSaveDirectory(FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName()));

        ProjectIOManager ioMgr = new ProjectIOManager();
        ioMgr.write(this.project);
        MWRelationalProject project1 = (MWRelationalProject) ioMgr.read(this.project.saveFile(), NullPreferences.instance());

        assertEquals(oldName, project1.getRepository().typeNamedIgnoreCase(oldName).getName());
        assertEquals(oldName, project1.getRepository().typeNamedIgnoreCase(newName).getName());
        assertNotNull(project1.descriptorNamed(oldName));
        assertNull(project1.descriptorNamed(newName));

        MWClass fooType1 = project1.typeNamed(oldName);
        MWDescriptor fooDescriptor1 = project1.descriptorForType(fooType1);
        fooType1.setName(newName);
        fooDescriptor1.setName(newName);

        assertEquals(newName, project1.getRepository().typeNamedIgnoreCase(oldName).getName());
        assertEquals(newName, project1.getRepository().typeNamedIgnoreCase(newName).getName());
        assertNull(project1.descriptorNamed(oldName));
        assertNotNull(project1.descriptorNamed(newName));

        ioMgr.write(project1);

        // re-read the project and make sure the descriptor and class are still there
        MWRelationalProject project2 = (MWRelationalProject) ioMgr.read(project1.saveFile(), NullPreferences.instance());

        assertEquals(newName, project2.getRepository().typeNamedIgnoreCase(oldName).getName());
        assertEquals(newName, project2.getRepository().typeNamedIgnoreCase(newName).getName());
        assertNull(project2.descriptorNamed(oldName));
        assertNotNull(project2.descriptorNamed(newName));
    }

    // useful for debugging
    public void dump(Iterator stream) {
        while (stream.hasNext()) {
            System.out.println(stream.next().toString());
        }
    }

    private MWClass fullyPopulatedTypeFor(Class javaClass) throws ExternalClassNotFoundException {
        MWClass result = this.project.typeFor(javaClass);
        result.refresh();
        return result;
    }

}
