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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.meta;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.models.employee.Address;
import org.eclipse.persistence.tools.workbench.test.models.employee.Employee;
import org.eclipse.persistence.tools.workbench.test.models.employee.PhoneNumber;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.utility.Classpath;


public class MWClassAttributeTests extends TestCase {
	private MWRelationalProject project;
	
	// primitive types
	private MWClass voidType;
	private MWClass charType;
	
	// object types
	private MWClass objectType;
	private MWClass booleanType;
	private MWClass integerType;
	private MWClass stringType;
	private MWClass valueHolderInterfaceType;
	
	// container types
	private MWClass collectionType;
	private MWClass mapType;
	private MWClass setType;
	
	// test model types
	private MWClass employeeType;
	private MWClass addressType;
	private MWClass phoneNumberType;
		
	// created types
	private MWClass fooType;
	
	// attributes
	private MWClassAttribute firstNameAttribute;
	private MWClassAttribute addressAttribute;
	private MWClassAttribute phoneNumbersAttribute;
	
	public static Test suite() 
	{
		return new TestSuite(MWClassAttributeTests.class);
	}
	
	public MWClassAttributeTests(String name) 
	{
		super(name);
	}
	
	protected void setUp() throws Exception 
	{
		super.setUp();
		this.project = this.buildProject();
		this.project.getRepository().addClasspathEntry(Classpath.locationFor(this.getClass()));
		
		this.voidType = this.fullyPopulatedTypeFor(void.class);
		this.charType = this.fullyPopulatedTypeFor(char.class);
		
		this.objectType = this.fullyPopulatedTypeFor(Object.class);
		this.booleanType = this.fullyPopulatedTypeFor(Boolean.class);
		this.integerType = this.fullyPopulatedTypeFor(Integer.class);
		this.stringType = this.fullyPopulatedTypeFor(String.class);
		this.valueHolderInterfaceType = this.fullyPopulatedTypeFor(ValueHolderInterface.class);
		
		this.collectionType = this.fullyPopulatedTypeFor(Collection.class);
		this.mapType = this.fullyPopulatedTypeFor(Map.class);
		this.setType = this.fullyPopulatedTypeFor(Set.class);
		
		this.employeeType = this.fullyPopulatedTypeFor(Employee.class);
		this.addressType = this.fullyPopulatedTypeFor(Address.class);
		this.phoneNumberType = this.fullyPopulatedTypeFor(PhoneNumber.class);
		
		this.fooType = this.project.typeNamed("foo.bar.Foo");
		this.fooType.addAttribute("objectAttribute", this.objectType);
		this.fooType.addAttribute("stringAttribute", this.stringType);
		this.fooType.addMethod("bar");
		
		this.firstNameAttribute = this.employeeType.attributeNamed("firstName");
		this.addressAttribute = this.employeeType.attributeNamed("address");
		this.addressAttribute.setValueType(this.addressType);
		this.phoneNumbersAttribute = this.employeeType.attributeNamed("phoneNumbers");
		this.phoneNumbersAttribute.setType(this.collectionType);
		this.phoneNumbersAttribute.setItemType(this.phoneNumberType);
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	private MWRelationalProject buildProject() {
		return new MWRelationalProject(this.getClass().getName(), MappingsModelTestTools.buildSPIManager(), null);
	}
	
	private void verifySetUp() {
		// Make sure that everything is set to what we expect
		
		assertEquals("The type of the firstName attribute was not preset to java.lang.String.",
					this.firstNameAttribute.getType(), this.stringType);
		assertTrue("The dimensionality of the firstName attribute was not preset to 0.", 
					this.firstNameAttribute.getDimensionality() == 0);
		assertNull("The item type of the firstName attribute was not preset to null.", 
					this.firstNameAttribute.getItemType());
		assertNull("The value type of the firstName attribute was not preset to null.", 
					this.firstNameAttribute.getValueType());
		assertNull("The key type of the firstName attribute was not preset to null.",
					this.firstNameAttribute.getKeyType());
	}
	
	public void testState() throws Exception 
	{
		MWClassAttribute attribute;
		attribute = this.stringType.attributeNamed("value");
		assertEquals(this.charType, attribute.getType());
		assertEquals(1, attribute.getDimensionality());
		assertTrue(attribute.getModifier().isPrivate());
	
		MWClass compType = this.fullyPopulatedTypeFor(java.util.Comparator.class);
		attribute = this.stringType.attributeNamed("CASE_INSENSITIVE_ORDER");
		assertTrue(attribute.getType().isAssignableFrom(compType));
		assertTrue( ! attribute.getType().isAssignableFrom(this.stringType));
		assertEquals(0, attribute.getDimensionality());
		assertTrue(attribute.getModifier().isPublic());
		assertTrue(attribute.getModifier().isStatic());
		assertTrue(attribute.getModifier().isFinal());
	}
	
	public void testRefresh() throws Exception 
	{
		MWClassAttribute attribute;
		attribute = this.stringType.attributeNamed("value");
		assertTrue(attribute.getModifier().isPrivate());
		attribute.getModifier().setPublic(true);
		assertTrue( ! attribute.getModifier().isPrivate());
		assertTrue(attribute.getModifier().isPublic());
	
		this.stringType.refresh();

		assertTrue(attribute.getModifier().isPrivate());
		assertTrue( ! attribute.getModifier().isPublic());
	}
	
	public void testVoidType() {
		MWClassAttribute attribute = this.stringType.attributeNamed("value");
		try {
			attribute.setType(this.voidType);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail();
	}
	/**
	 * Basic testing for item type for Collection attribute types.
	 */
	public void testSetUnsetItemTypeForCollection()
	{
		verifySetUp();
		
		// ** TEST 1 **
		// Set the item type to anything and make sure that an IllegalArgumentException is thrown, 
		// since the type is not a Collection or Map
		
		try
		{
			this.firstNameAttribute.setItemType(this.integerType);
		
			assertTrue("An IllegalArgumentException was not thrown while trying to set the item type of the firstName attribute.  The type is not a Collection or Map.",
						false);		
		} catch (IllegalArgumentException iae) {
			// this is expected
		}
		
		assertNull("The item type of the firstName attribute was incorrectly set.  The type is not a Collection or Map.",
			this.firstNameAttribute.getItemType());

			
		// ** TEST 2 **
		// Set the type to Collection and make sure the item type defaults to Object.
		
		this.firstNameAttribute.setType(this.collectionType);
		
		assertEquals("The item type of the firstName attribute did not default to String.",
					this.firstNameAttribute.getItemType(), this.stringType);
		
		
		// ** TEST 3 **
		// Set the item type anything and make sure it is set correctly.
		
		this.firstNameAttribute.setItemType(this.objectType);
		
		assertEquals("The item type of the firstName attribute was not set to Object.",
					this.firstNameAttribute.getItemType(), this.objectType);
		
		
		// ** TEST 4 **
		// Set the type to Set and make sure the item type stays the same.
		
		this.firstNameAttribute.setType(this.setType);
		
		assertEquals("The item type of the firstName attribute did not stay set to Object.",
					this.firstNameAttribute.getItemType(), this.objectType);
		
		
		// ** TEST 5 **
		// Set the type to a non-Collection and make sure the item type reverts to null.
		
		this.firstNameAttribute.setType(this.stringType);
		
		assertNull("The item type of the firstName attribute did not revert to null.",
					this.firstNameAttribute.getItemType());
	}
	
	/**
	 * Testing for item type for ValueHolders holding Collection types.
	 */
	public void testSetUnsetItemTypeForCollectionInValueHolder()
	{
		verifySetUp();
		
		// ** SETUP **
		// Set the type to ValueHolderInterface
		
		this.firstNameAttribute.setType(this.valueHolderInterfaceType);
		
		
		// ** TEST 1 **
		// Set the item type to anything and make sure that an IllegalArgumentException is thrown, 
		// since the type is not a Collection or Map and the value type is not a Collection or Map
		
		try
		{
			this.firstNameAttribute.setItemType(this.integerType);
		
			assertTrue("An IllegalArgumentException was not thrown while trying to set the item type of the firstName attribute.  The value type is not a Collection or Map.",
						false);		
		} catch (IllegalArgumentException iae) {
			// this is expected
		}
		
		assertNull("The item type of the firstName attribute was incorrectly set.  The value type is not a Collection or Map.",
			this.firstNameAttribute.getItemType());
		
		
		// ** TEST 2 **
		// Set the value type to Collection and make sure the item type defaults to Object.
		
		this.firstNameAttribute.setValueType(this.collectionType);
		
		assertEquals("The item type of the firstName attribute did not default to Object.",
					this.firstNameAttribute.getItemType(), this.objectType);
		
		
		// ** TEST 3 **
		// Set the item type anything and make sure it is set correctly.
		
		this.firstNameAttribute.setItemType(this.stringType);
		
		assertEquals("The item type of the firstName attribute was not set to String.",
					this.firstNameAttribute.getItemType(), this.stringType);
		
		
		// ** TEST 4 **
		// Set the value type to Set and make sure the item type stays the same.
		
		this.firstNameAttribute.setValueType(this.setType);
		
		assertEquals("The item type of the firstName attribute did not stay set to String.",
					this.firstNameAttribute.getItemType(), this.stringType);
		
		
		// ** TEST 5 **
		// Set the value type to a non-Collection and make sure the item type reverts to null.
		
		this.firstNameAttribute.setValueType(this.objectType);
		
		assertNull("The item type of the firstName attribute did not revert to null.",
					this.firstNameAttribute.getItemType());
	}
	
	/**
	 * Basic testing for key type for Map attribute types.
	 */
	public void testSetUnsetKeyTypeAndItemTypeForMap()
	{
		verifySetUp();
		
	
		// ** TEST 1 **
		// Set the key type to anything and the item type to anything and make sure that 
		// IllegalArgumentExceptions are thrown, since the type is not a Collection or Map
		
		try
		{
			this.firstNameAttribute.setKeyType(this.integerType);
		
			assertTrue("An IllegalArgumentException was not thrown while trying to set the key type of the firstName attribute.  The type is not a Collection or Map.",
						false);		
		} catch (IllegalArgumentException iae) {
			// this is expected
		}
		try
		{
			this.firstNameAttribute.setItemType(this.integerType);
		
			assertTrue("An IllegalArgumentException was not thrown while trying to set the item type of the firstName attribute.  The type is not a Collection or Map.",
						false);		
		} catch (IllegalArgumentException iae) {
			// this is expected
		}
		
		assertNull("The key type of the firstName attribute was incorrectly set.  The type is not a Map.",
					this.firstNameAttribute.getKeyType());
		assertNull("The item type of the firstName attribute was incorrectly set.  The type is not a Collection or Map.",
					this.firstNameAttribute.getItemType());
		
			
		// ** TEST 2 **
		// Set the type to Collection and set the item type.  Then set the type to 
		// Map and make sure the item type is the same and the key type defaults to Object.
		
		this.firstNameAttribute.setType(this.collectionType);
		this.firstNameAttribute.setItemType(this.stringType);
		this.firstNameAttribute.setType(this.mapType);
		
		assertEquals("The key type of the firstName attribute did not default to java.lang.Object.",
					this.firstNameAttribute.getKeyType(), this.objectType);
		assertEquals("The item type of the firstName attribute was not set to String.",
					this.firstNameAttribute.getItemType(), this.stringType);
		
		
		// ** TEST 3 **
		// Set the key type anything and the item type to anything and make sure they are set correctly.
		
		this.firstNameAttribute.setKeyType(this.integerType);
		this.firstNameAttribute.setItemType(this.booleanType);
		
		assertEquals("The key type of the firstName attribute was not set to Integer.",
					this.firstNameAttribute.getKeyType(), this.integerType);
		assertEquals("The item type of the firstName attribute was not set to Boolean.",
					this.firstNameAttribute.getItemType(), this.booleanType);
		
		
		// ** TEST 4 **
		// Set the type to Collection and make sure the key type reverts to null and the item type stays the same.
		
		this.firstNameAttribute.setType(this.collectionType);
		
		assertNull("The key type of the firstName attribute did not revert to null.",
					this.firstNameAttribute.getKeyType());
		assertEquals("The item type of the firstName attribute did not stay set to Boolean.",
					this.firstNameAttribute.getItemType(), this.booleanType);
	}
	
	/**
	 * Testing for key type and item type for ValueHolders holding Map types.
	 */
	public void testSetUnsetKeyTypeAndItemTypeForMapInValueHolder()
	{
		verifySetUp();	
		
		
		// ** SETUP **
		// Set the type to ValueHolderInterface
		this.firstNameAttribute.setType(this.valueHolderInterfaceType);
		
		
		// ** TEST 1 **
		// Set the key type to anything and the item type to anything and make sure that 
		// IllegalArgumentExceptions are thrown, since the value type is not a Collection or Map
		
		try
		{
			this.firstNameAttribute.setKeyType(this.integerType);
		
			assertTrue("An IllegalArgumentException was not thrown while trying to set the key type of the firstName attribute.  The value type is not a Collection or Map.",
						false);		
		} catch (IllegalArgumentException iae) {
			// this is expected
		}
		try
		{
			this.firstNameAttribute.setItemType(this.integerType);
		
			assertTrue("An IllegalArgumentException was not thrown while trying to set the item type of the firstName attribute.  The value type is not a Collection or Map.",
						false);		
		} catch (IllegalArgumentException iae) {
			// this is expected
		}
		
		assertNull("The key type of the firstName attribute was incorrectly set.  The value type is not a Map.",
					this.firstNameAttribute.getKeyType());
		assertNull("The item type of the firstName attribute was incorrectly set.  The value type is not a Collection or Map.",
					this.firstNameAttribute.getItemType());
		
			
		// ** TEST 2 **
		// Set the value type to Collection and set the item type.  Then set the type to 
		// Map and make sure the item type is the same and the key type defaults to Object.
		
		this.firstNameAttribute.setValueType(this.collectionType);
		this.firstNameAttribute.setItemType(this.stringType);
		this.firstNameAttribute.setValueType(this.mapType);
		
		assertEquals("The key type of the firstName attribute did not default to java.lang.Object.",
					this.firstNameAttribute.getKeyType(), this.objectType);
		assertEquals("The item type of the firstName attribute was not set to String.",
					this.firstNameAttribute.getItemType(), this.stringType);
		
		
		// ** TEST 3 **
		// Set the key type anything and the item type to anything and make sure they are set correctly.
		
		this.firstNameAttribute.setKeyType(this.integerType);
		this.firstNameAttribute.setItemType(this.booleanType);
		
		assertEquals("The key type of the firstName attribute was not set to Integer.",
					this.firstNameAttribute.getKeyType(), this.integerType);
		assertEquals("The item type of the firstName attribute was not set to Boolean.",
					this.firstNameAttribute.getItemType(), this.booleanType);
		
		
		// ** TEST 4 **
		// Set the value type to Collection and make sure the key type reverts to null and the item type stays the same.
		
		this.firstNameAttribute.setValueType(this.collectionType);
		
		assertNull("The key type of the firstName attribute did not revert to null.",
					this.firstNameAttribute.getKeyType());
		assertEquals("The item type of the firstName attribute did not stay set to Boolean.",
					this.firstNameAttribute.getItemType(), this.booleanType);
	}
	
	/**
	 * Basic testing for value type for ValueHolderInterface attribute types
	 */
	public void testSetUnsetValueType()
	{
		verifySetUp();
		
		
		// ** TEST 1 **
		// Set the value type to anything and make sure that an IllegalArgumentException was thrown.
		try
		{
			this.firstNameAttribute.setValueType(this.integerType);
		
			assertTrue("An IllegalArgumentException was not thrown while trying to set the value type of the firstName attribute.  The type is not a ValueHolderInterface.",
						false);		
		} catch (IllegalArgumentException iae) {
			// this is expected
		}
		
		assertNull("The value type of the firstName attribute was incorrectly set.  The type is not a ValueHolderInterface.",
					this.firstNameAttribute.getValueType());
		
		
		// ** TEST 2 **
		// Set the attribute type to ValueHolderInterface, and make sure that the attribute type was set 
		// and the value type was set to the old attribute type
		
		this.firstNameAttribute.setType(this.valueHolderInterfaceType);
		
		assertEquals("The type of the firstName attribute was not correctly set to ValueHolderInterface.", 
					this.firstNameAttribute.getType(), this.valueHolderInterfaceType);
		assertEquals("The value type of the firstName attribute was not correctly set to java.lang.String.",
					this.firstNameAttribute.getValueType(), this.stringType);
		
		
		// ** TEST 3 **
		// Now, set the value type to something else and make sure that it was set correctly
		
		this.firstNameAttribute.setValueType(this.booleanType);
		
		assertEquals("The value type of the firstName attribute was not correctly set to java.lang.Boolean.",
					this.firstNameAttribute.getValueType(), this.booleanType);
		
		
		// ** TEST 4 **
		// Now, set the attribute type to a non-ValueHolderInterface and make sure
		// that the attribute type was set and the value type reverted to null
		
		this.firstNameAttribute.setType(this.stringType);
		
		assertEquals("The type of the firstName attribute was not correctly set to java.lang.String.",
					this.firstNameAttribute.getType(), this.stringType);
		assertNull("The value type of the firstName attribute did not correctly revert to null.",
					this.firstNameAttribute.getValueType());
	}
	
	/**
	 * Test changing between collection types and ValueHolders containing those collection types
	 */
	public void testSetUnsetValueTypeForCollection()
	{
		verifySetUp();
		
		
		// ** SETUP **
		// Set the type to a collection and set the item type to anything
		
		this.firstNameAttribute.setType(this.collectionType);
		this.firstNameAttribute.setItemType(this.integerType);
		
		
		// ** TEST 1 **
		// Set the type to ValueHolderInterface and make sure the value type is set to the old type
		// and the item type is still the same.
		
		this.firstNameAttribute.setType(this.valueHolderInterfaceType);
		
		assertEquals("The value type was not set to the old type, java.util.Collection",
					this.firstNameAttribute.getValueType(), this.collectionType);
		assertEquals("The item type did not remain set to the old item type, java.lang.Integer",
					this.firstNameAttribute.getItemType(), this.integerType);
		
		
		// ** TEST 2 **
		// Set the type to Set and make sure that the type is set, the value type reverted to null, and the 
		// item type stayed the same.
		
		this.firstNameAttribute.setType(this.setType);
		
		assertEquals("The type was not set to java.util.Set.",
					this.firstNameAttribute.getType(), this.setType);
		assertEquals("The item type did not remain set to the old item type, java.lang.Integer.",
					this.firstNameAttribute.getItemType(), this.integerType);
		assertNull("The value type did not revert to null.",
					this.firstNameAttribute.getValueType());
		
		
		// ** TEST 3 **
		// Set the type to ValueHolderInterface.  Then set the type to String and make sure that the type is set, 
		// the value type reverted to null, and the item type reverted to null.
		
		this.firstNameAttribute.setType(this.valueHolderInterfaceType);
		this.firstNameAttribute.setType(this.stringType);
		
		assertEquals("The type was not set to java.lang.String.",
					this.firstNameAttribute.getType(), this.stringType);
		assertNull("The item type did not revert to null.",
					this.firstNameAttribute.getItemType());
		assertNull("The value type did not revert to null.",
					this.firstNameAttribute.getValueType());	
	}
	
	/**
	 * Test changing between map types and ValueHolders containing those map types
	 */
	public void testSetUnsetValueTypeForMap()
	{
		verifySetUp();
		
		
		// ** SETUP **
		// Set the type to a map and set the item type and key type to anything
		
		this.firstNameAttribute.setType(this.mapType);
		this.firstNameAttribute.setItemType(this.stringType);
		this.firstNameAttribute.setKeyType(this.integerType);
		
		
		// ** TEST 1 **
		// Set the type to ValueHolderInterface and make sure the value type is set to the old type
		// and the item type and key type are still the same.
		
		this.firstNameAttribute.setType(this.valueHolderInterfaceType);
		
		assertEquals("The value type was not set to the old type, java.util.Map",
					this.firstNameAttribute.getValueType(), this.mapType);
		assertEquals("The item type did not remain set to the old item type, java.lang.String",
					this.firstNameAttribute.getItemType(), this.stringType);
		assertEquals("The key type did not remain set to the old key type, java.lang.Integer",
					this.firstNameAttribute.getKeyType(), this.integerType);
		
		
		// ** TEST 2 **
		// Set the type to Map and make sure that the type is set, the value type reverted to null, and the 
		// item type and key type stayed the same.
		
		this.firstNameAttribute.setType(this.mapType);
		
		assertEquals("The type was not set to java.util.Map	.",
					this.firstNameAttribute.getType(), this.mapType);
		assertEquals("The item type did not remain set to the old item type, java.lang.String.",
					this.firstNameAttribute.getItemType(), this.stringType);
		assertEquals("The key type did not remain set to the old key type, java.lang.Integer.",
					this.firstNameAttribute.getKeyType(), this.integerType);
		assertNull("The value type did not revert to null.",
					this.firstNameAttribute.getValueType());
					
		
		// ** TEST 3 **
		// Set the type to ValueHolderInterface.  Then set the type to String and make sure that the type is set, 
		// and the value type, item type, and key type all reverted to null.
		
		this.firstNameAttribute.setType(this.valueHolderInterfaceType);
		this.firstNameAttribute.setType(this.stringType);
		
		assertEquals("The type was not set to java.lang.String.",
					this.firstNameAttribute.getType(), this.stringType);
		assertNull("The item type did not revert to null.",
					this.firstNameAttribute.getItemType());
		assertNull("The key type did not revert to null.",
					this.firstNameAttribute.getKeyType());
		assertNull("The value type did not revert to null.",
					this.firstNameAttribute.getValueType());	
	}
	
	public void testUpdateGetAndSetMethodsFromAttributeType()
	{
		MWMethod getFirstNameMethod = this.employeeType.methodWithSignature("getFirstName()");
		MWMethod setFirstNameMethod = this.employeeType.methodWithSignature("setFirstName(java.lang.String)");
		
		
		// ** TEST 1 **
		// Set the get and set methods and make sure they are set correctly
		
		this.firstNameAttribute.setGetMethod(getFirstNameMethod);
		this.firstNameAttribute.setSetMethod(setFirstNameMethod);
		
		assertTrue("The get method for the first name attribute was not set correctly to getFirstName().",
				   this.firstNameAttribute.getGetMethod() == getFirstNameMethod);
		assertTrue("The set method for the first name attribute was not set correctly to setFirstName(java.lang.String).",
				   this.firstNameAttribute.getSetMethod() == setFirstNameMethod);
		
		
		// ** TEST 2 **
		// Change the type of the attribute and make sure that the get and set methods change correctly.
		
		this.firstNameAttribute.setType(this.booleanType);
		
		assertEquals("The method getFirstName() did not change to return a type java.lang.Boolean.",
				   getFirstNameMethod.getReturnType(), this.booleanType);
		assertEquals("The method setFirstName(java.lang.String) did not change to take a parameter of type java.lang.Boolean.",
				   setFirstNameMethod.getMethodParameter(0).getType(), this.booleanType);
		
	}
	
	public void testUpdateValueGetAndSetMethodsFromAttributeValueType()
	{
		MWMethod getAddressValueMethod = this.employeeType.methodWithSignature("getAddress()");
		MWMethod setAddressValueMethod = this.employeeType.methodWithSignature("setAddress(" + this.addressType.getName() + ")");
		
		
		// ** TEST 1 **
		// Set the value get and set methods and make sure they are set correctly
		
		this.addressAttribute.setValueGetMethod(getAddressValueMethod);
		this.addressAttribute.setValueSetMethod(setAddressValueMethod);
		
		assertTrue("The value get method for the address attribute was not set correctly to getAddress().",
				   this.addressAttribute.getValueGetMethod() == getAddressValueMethod);
		assertTrue("The value set method for the address attribute was not set correctly to setAddress(Address).",
				   this.addressAttribute.getValueSetMethod() == setAddressValueMethod);
		
		
		// ** TEST 2 **
		// Set the value type of the attribute to String and make sure the value get and set methods change correctly.
		
		this.addressAttribute.setValueType(this.stringType);
		
		assertEquals("The method getAddress() did not change to return a type java.lang.String.",
				   getAddressValueMethod.getReturnType(), this.stringType);
		assertEquals("The method setAddress(Address) did not change to take a parameter of type java.lang.String.",
				   setAddressValueMethod.getMethodParameter(0).getType(), this.stringType);
		
		
		// ** TEST 3 **
		// Set the type of the attribute to Address and make sure the value get and set methods get set to null.
		
		this.addressAttribute.setType(this.addressType);
		
		assertTrue("The value get method for the address attribute did not revert to null.",
				   this.addressAttribute.getValueGetMethod() == null);
		assertTrue("The value set method for the address attribute did not revert to null.",
				   this.addressAttribute.getValueSetMethod() == null);		
	} 
	
	public void testUpdateAddAndRemoveMethodsFromAttributeItemTypeForCollection()
	{
		MWMethod addPhoneNumberMethod = this.employeeType.methodWithSignature("addPhoneNumber(" + this.phoneNumberType.getName() + ")");
		MWMethod removePhoneNumberMethod = this.employeeType.methodWithSignature("removePhoneNumber(" + this.phoneNumberType.getName() + ")");
		
		
		// ** TEST 1 **
		// Set the add and remove methods and make sure they are set correctly
		
		this.phoneNumbersAttribute.setAddMethod(addPhoneNumberMethod);
		this.phoneNumbersAttribute.setRemoveMethod(removePhoneNumberMethod);
		
		assertTrue("The add method for the phone numbers attribute was not set correctly to addPhoneNumber(PhoneNumber).",
				   this.phoneNumbersAttribute.getAddMethod() == addPhoneNumberMethod);
		assertTrue("The remove method for the phone numbers attribute was not set correctly to removePhoneNumber(PhoneNumber).",
				   this.phoneNumbersAttribute.getRemoveMethod() == removePhoneNumberMethod);
		
		
		// ** TEST 2 **
		// Set the item type of the attribute to String and make sure the add and remove methods change correctly.
		
		this.phoneNumbersAttribute.setItemType(this.stringType);
		
		assertEquals("The method addPhoneNumber(PhoneNumber) did not change to take a parameter of type java.lang.String.",
				   addPhoneNumberMethod.getMethodParameter(0).getType(), this.stringType);
		assertEquals("The method removePhoneNumber(PhoneNumber) did not change to take a parameter of type java.lang.String.",
				   removePhoneNumberMethod.getMethodParameter(0).getType(), this.stringType);
		
		
		// ** TEST 3 **
		// Set the type of the attribute to PhoneNumber and make sure the add and remove methods get set to null.
		
		this.phoneNumbersAttribute.setType(this.phoneNumberType);
		
		assertTrue("The add method for the phone numbers attribute did not revert to null.",
				   this.phoneNumbersAttribute.getAddMethod() == null);
		assertTrue("The remove method for the phone numbers attribute did not revert to null.",
				   this.phoneNumbersAttribute.getRemoveMethod() == null);	
	}
	
	public void testUpdateAddAndRemoveMethodsFromAttributeItemTypeForMap()
	{
		this.phoneNumbersAttribute.setType(this.mapType);
		this.phoneNumbersAttribute.setKeyType(this.integerType);
		
		MWMethod addPhoneNumberMethod = this.employeeType.addMethod("addPhoneNumber");
		addPhoneNumberMethod.addMethodParameter(this.integerType);
		addPhoneNumberMethod.addMethodParameter(this.phoneNumberType);
		
		MWMethod removePhoneNumberMethod = this.employeeType.addMethod("removePhoneNumber");
		removePhoneNumberMethod.addMethodParameter(this.integerType);
		
		
		// ** TEST 1 **
		// Set the add and remove methods and make sure they are set correctly
		
		this.phoneNumbersAttribute.setAddMethod(addPhoneNumberMethod);
		this.phoneNumbersAttribute.setRemoveMethod(removePhoneNumberMethod);
		
		assertTrue("The add method for the phone numbers attribute was not set correctly to addPhoneNumber(Integer, PhoneNumber).",
				   this.phoneNumbersAttribute.getAddMethod() == addPhoneNumberMethod);
		assertTrue("The remove method for the phone numbers attribute was not set correctly to removePhoneNumber(Integer).",
				   this.phoneNumbersAttribute.getRemoveMethod() == removePhoneNumberMethod);
		
		
		// ** TEST 2 **
		// Set the item type of the attribute to String and make sure the add method changes correctly.
		
		this.phoneNumbersAttribute.setItemType(this.stringType);
		
		assertEquals("The method addPhoneNumber(Integer, PhoneNumber) did not change to addPhoneNumber(Integer, String).",
				   addPhoneNumberMethod.getMethodParameter(1).getType(), this.stringType);
		
		
		// ** TEST 3 **
		// Set the type of the attribute to PhoneNumber and make sure the add and remove methods get set to null.
		
		this.phoneNumbersAttribute.setType(this.phoneNumberType);
		
		assertTrue("The add method for the phone numbers attribute did not revert to null.",
				   this.phoneNumbersAttribute.getAddMethod() == null);
		assertTrue("The remove method for the phone numbers attribute did not revert to null.",
				   this.phoneNumbersAttribute.getRemoveMethod() == null);	
	}
	
	public void testUpdateAddAndRemoveMethodsWithValueHolderMapAndCollection()
	{
		MWMethod addPhoneNumberMethod = this.employeeType.addMethod("addPhoneNumber");
		addPhoneNumberMethod.addMethodParameter(this.phoneNumberType);
		
		MWMethod removePhoneNumberMethod = this.employeeType.addMethod("removePhoneNumber");
		removePhoneNumberMethod.addMethodParameter(this.phoneNumberType);
		
		this.phoneNumbersAttribute.setAddMethod(addPhoneNumberMethod);
		this.phoneNumbersAttribute.setRemoveMethod(removePhoneNumberMethod);
		
		
		// ** TEST 1 **
		// Set the type of the attribute to a ValueHolderInterface and make sure the add and remove methods remain the same.
		
		this.phoneNumbersAttribute.setType(this.valueHolderInterfaceType);
		
		assertTrue("The add and remove methods did not remain the same.",
				   this.phoneNumbersAttribute.getAddMethod() == addPhoneNumberMethod
				   && this.phoneNumbersAttribute.getRemoveMethod() == removePhoneNumberMethod);
		
		
		// ** TEST 2 **
		// Set the value type of the attribute to a Map and make sure the add and remove methods change correctly.
		
		this.phoneNumbersAttribute.setValueType(this.mapType);
		
		assertTrue("The method addPhoneNumber(PhoneNumber) did not change to addPhoneNumber(Object, PhoneNumber).",
				   addPhoneNumberMethod.methodParametersSize() == 2
				   && addPhoneNumberMethod.getMethodParameter(0).getType() == this.objectType
				   && addPhoneNumberMethod.getMethodParameter(1).getType() == this.phoneNumberType);
		assertTrue("The method removePhoneNumber(PhoneNumber) did not change to removePhoneNumber(Object).",
				   removePhoneNumberMethod.methodParametersSize() == 1
				   && removePhoneNumberMethod.getMethodParameter(0).getType() == this.objectType);
		
		
		// ** TEST 3 **
		// Set the type of the attribute to a Collection and make sure the add and remove method are still set and that they change correctly.
		
		this.phoneNumbersAttribute.setType(this.collectionType);
		
		assertTrue("The add and remove methods did not stay set.",
				   this.phoneNumbersAttribute.getAddMethod() == addPhoneNumberMethod
				   && this.phoneNumbersAttribute.getRemoveMethod() == removePhoneNumberMethod);
		assertTrue("The method addPhoneNumber(Object, PhoneNumber) did not change to addPhoneNumber(PhoneNumber).",
				   addPhoneNumberMethod.methodParametersSize() == 1
				   && addPhoneNumberMethod.getMethodParameter(0).getType() == this.phoneNumberType);
		assertTrue("The method removePhoneNumber(Object) did not change to removePhoneNumber(PhoneNumber).",
				   removePhoneNumberMethod.methodParametersSize() == 1
				   && removePhoneNumberMethod.getMethodParameter(0).getType() == this.phoneNumberType);				   
	}

	private MWClass fullyPopulatedTypeFor(Class javaClass) throws ExternalClassNotFoundException {
		MWClass result = this.project.typeFor(javaClass);
		result.refresh();
		return result;
	}
	
}
