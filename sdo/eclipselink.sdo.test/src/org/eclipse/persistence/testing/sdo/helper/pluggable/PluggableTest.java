/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.pluggable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.testing.sdo.helper.pluggable.impl.POJOValueStore;
import org.eclipse.persistence.testing.sdo.helper.pluggable.impl.POJOValueStoreReadWrite;
import org.eclipse.persistence.testing.sdo.helper.pluggable.model.Address;
import org.eclipse.persistence.testing.sdo.helper.pluggable.model.Employee;
import org.eclipse.persistence.testing.sdo.helper.pluggable.model.Phone;

public class PluggableTest extends PluggableTestCases {
    // constants
    public static final String TEST_PHONE_1_ORIGINAL = "1014455";
    public static final String TEST_PHONE_2_ORIGINAL = "1014466";
    public static final String TEST_PHONE_3_ORIGINAL = "1014477";
    public static final String TEST_EMPLOYEE_ADDRESS_FIELD_NAME = "address";
    public static final String TEST_EMPLOYEE_NAME_FIELD_NAME = "name";
    public static final String TEST_EMPLOYEE_PHONE_FIELD_NAME = "phones";

    //public static final String TEST_EMPLOYEE_PHONE_FIELD_NAME = "phone";    
    public static final String TEST_PHONE_FIRST_IN_ARRAY_XPATH = "phones[1]";
    public static final String TEST_PHONE_NUMBER_FIELD_NAME = "number";
    public static final String TEST_ADDRESS_STREET_FIELD_NAME = "street";
    public static final String TEST_ADDRESS_STREET_ORIGINAL = "101 A Street";
    public static final String TEST_ADDRESS_STREET_MODIFICATION = "102 A New Street";
    public static final String TEST_EMPLOYEE_NAME_ORIGINAL = "Employee1";
    public static final String TEST_EMPLOYEE_NAME_MODIFICATION = "Employee2";
    public static final String POJO_PLUGGABLE_IMPL_CLASS_NAME = "org.eclipse.persistence.testing.sdo.helper.pluggable.impl.POJOValueStoreReadWrite";

    public PluggableTest(String name) {
        super(name);
    }

    /**
     * Test Objectives:
     *   T0010 Test simple datatype get
     *   T0011 Test simple datatype set
     *
     *   (Before any get fills the properties map)
     *   T0021 1-1 POJO(DataObject) get
     *   T0031 1-1 POJO(DataObject) set(new)
     *   T0032 1-1 POJO(DataObject) set(move)
     *   T0041 1-1 POJO(DataObject) unset
     *   T0042 1-1 POJO(DataObject) delete
     *   T0121 1-n POJO(DataObject via List) get(index)
     *   T0122 1-n POJO(DataObject via List) get(List)
     *   T0131 1-n POJO(DataObject via List) set(index, new)
     *   T0132 1-n POJO(DataObject via List) set(index, new)
     *   T0133 1-n POJO(DataObject via List) set(index, move) - delete attributes
     *   T0134 1-n POJO(DataObject via List) set(index, move) - delete attributes
     *   T0141 1-n POJO(DataObject via List) unset(index)
     *   T0151 1-n POJO(DataObject via List) delete(index)
     *   T0152 1-n POJO(DataObject via List) delete(List)
     *   T0161 1-n POJO(DataObject via List) detach(index) - attributes remain
     *   T0162 1-n POJO(DataObject via List) detach(List) - attributes remain
     *
     *
     *   (After a get that fills the properties map for this object)
     *   T1021 1-1 POJO(DataObject) get
     *   T1031 1-1 POJO(DataObject) set(new)
     *   T1032 1-1 POJO(DataObject) set(new)
     *   T1041 1-1 POJO(DataObject) unset
     *   T1042 1-1 POJO(DataObject) delete
     *   T1121 1-n POJO(DataObject via List) get(index)
     *   T1122 1-n POJO(DataObject via List) get(List)
     *   T1131 1-n POJO(DataObject via List) set(index, new)
     *   T1132 1-n POJO(DataObject via List) set(index, new)
     *   T1133 1-n POJO(DataObject via List) set(index, move)
     *   T1134 1-n POJO(DataObject via List) set(index, move)
     *   T1141 1-n POJO(DataObject via List) unset(index)
     *   T1151 1-n POJO(DataObject via List) delete(index)
     *   T1152 1-n POJO(DataObject via List) delete(List)
     *   T0161 1-n POJO(DataObject via List) detach(index) - attributes remain
     *   T0162 1-n POJO(DataObject via List) detach(List) - attributes remain
     *
     *   Change Summary
     *   T8xxx with ChangeSummary on after loading/creation
     */

    // Note Order is significant when setting the system property
    // Excercise the default system property value

    /*    public void testInitializeDefaultPluggableMapUsingSystemPropertyDuringNew() {
            // create a dataObject
            SDODataObject anObject = (SDODataObject)aHelperContext.getDataFactory().create(//
            aHelperContext.getTypeHelper().getType(rootTypeUri, rootTypeName));
            ValueStore aPluggableMap = anObject.getCurrentValueStore();

            // check that we received a DataObject
            assertNotNull(anObject);
            assertNotNull(aPluggableMap);
            // check for type
            assertEquals(SDO_PLUGGABLE_MAP_IMPL_CLASS_VALUE,//
                         aPluggableMap.getClass().getName());
        }
    */

    // Prerequisites: system property must be set This test exercises the
    // Pluggable initializer sections of DataFactory and SDODataObject's constructor

    /*    public void testInitializeDefaultPluggableMapUsingSystemPropertyDuringLoad() {
            // set property
            System.setProperty(SDO_PLUGGABLE_MAP_IMPL_CLASS_KEY,//
                               SDO_PLUGGABLE_MAP_IMPL_CLASS_VALUE);
            String pluggableClassName = System.getProperty(//
            SDO_PLUGGABLE_MAP_IMPL_CLASS_KEY,//
            SDO_PLUGGABLE_MAP_IMPL_CLASS_VALUE);
            assertNotNull(pluggableClassName);
            assertEquals(SDO_PLUGGABLE_MAP_IMPL_CLASS_VALUE,//
                         pluggableClassName);
            // create a dataObject
            Object anEmployee = load(DATAOBJECT_XML_PATH);

            // check that we received a DataObject
            assertNotNull(anEmployee);
            assertTrue(anEmployee instanceof SDODataObject);
            // check for type
            assertEquals(SDO_PLUGGABLE_MAP_IMPL_CLASS_VALUE,//
                         ((SDODataObject)anEmployee).getCurrentValueStore().getClass().getName());
            Object anAddress = ((SDODataObject)anEmployee).get(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
            assertNotNull(anAddress);
            assertTrue(anEmployee instanceof SDODataObject);
            // verify contianment
            assertEquals(TEST_EMPLOYEE_ADDRESS_FIELD_NAME,//
                         ((SDODataObject)anAddress).getContainmentPropertyName());
            assertEquals(anEmployee, ((SDODataObject)anAddress).getContainer());
        }
    */
    public void testPOJOValueStoreCreatedObjectWithIsManyOperations() {
        // setup root DO wrapped around a POJO with types set
        SDODataObject anEmployee = setupDataObjectWithPOJOValueStore(//
        POJO_PLUGGABLE_IMPL_CLASS_NAME, true, true);

        // verify ValueStore is POJO type
        assertEquals(POJO_PLUGGABLE_IMPL_CLASS_NAME,//
                     ((SDODataObject)anEmployee)._getCurrentValueStore().getClass().getName());
        // get root POJO
        Employee rootPOJO = (Employee)((POJOValueStore)anEmployee._getCurrentValueStore()).getObject();

        // verify isMany=true for phone
        SDOProperty aPhonesProperty = (SDOProperty)anEmployee.getInstanceProperty(//
        TEST_EMPLOYEE_PHONE_FIELD_NAME);
        assertTrue(aPhonesProperty.isMany());
        // verify isMany=false for address
        SDOProperty anAddressProperty = (SDOProperty)anEmployee//
        .getType().getProperty(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        assertFalse(anAddressProperty.isMany());

        // complex 1-1 property set (before a previous get of this object)
        SDODataObject aNewAddress = wrap(new Address(TEST_ADDRESS_STREET_MODIFICATION), "AddressType");

        //see public void updateContainment(Property property, Object value) {
        anEmployee.set(TEST_EMPLOYEE_ADDRESS_FIELD_NAME, aNewAddress);
        Object anAddressMod = anEmployee.get(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        assertNotNull(anAddressMod);
        // verify we are wrapping inside a DataObject
        assertTrue(anAddressMod instanceof SDODataObject);
        String aStreetName = (String)((SDODataObject)anAddressMod)//
        .get(TEST_ADDRESS_STREET_FIELD_NAME);
        assertNotNull(aStreetName);
        // verify dataType property was modified
        assertEquals(TEST_ADDRESS_STREET_MODIFICATION, aStreetName);

        // simple property get
        Object aName = anEmployee.get(TEST_EMPLOYEE_NAME_FIELD_NAME);
        assertNotNull(aName);
        assertTrue(aName instanceof String);
        assertEquals(TEST_EMPLOYEE_NAME_ORIGINAL, aName);

        // complex 1-1 property get
        Object anAddress = anEmployee.get(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        assertNotNull(anAddress);
        // verify we are wrapping inside a DataObject
        assertTrue(anAddress instanceof SDODataObject);
        String aStreet = (String)((SDODataObject)anAddress).get(TEST_ADDRESS_STREET_FIELD_NAME);
        assertNotNull(aStreet);
        assertEquals(TEST_ADDRESS_STREET_MODIFICATION, aStreet);
        // get the same object and test hash equality
        Object anAddress2 = anEmployee.get(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        assertNotNull(anAddress2);
        // verify we are wrapping inside a DataObject
        assertTrue(anAddress2 instanceof SDODataObject);
        assertEquals(TEST_ADDRESS_STREET_MODIFICATION, ((SDODataObject)anAddress)//
        .get(TEST_ADDRESS_STREET_FIELD_NAME));
        // objects should be the same object
        assertEquals(anAddress, anAddress2);

        // Test 1-n POJO(DataObject via List) get
        // get list first (do not perform update containmet on first wrap)
        Object aPhoneList = anEmployee.get(TEST_EMPLOYEE_PHONE_FIELD_NAME);
        assertNotNull(aPhoneList);
        assertTrue(aPhoneList instanceof ListWrapper);
        // TODO: This test will exercise the dataObject.updateContainment(property, items); in ListWrapper.java 
        assertEquals(3, ((List)aPhoneList).size());

        // test cached object equality on 2nd get
        Object aPhoneList2 = anEmployee.get(TEST_EMPLOYEE_PHONE_FIELD_NAME);
        assertEquals(aPhoneList, aPhoneList2);

        // get single isMany object - this object should be wrapped as an SDODataObject
        //Object aPhone = ((List)aPhoneList).get(1);//"phone[1]");//1);
        Object aPhone = anEmployee.get(TEST_PHONE_FIRST_IN_ARRAY_XPATH);
        assertNotNull(aPhone);
        assertTrue(aPhone instanceof SDODataObject);
        // verify containment
        assertEquals(TEST_EMPLOYEE_PHONE_FIELD_NAME,//
                     ((SDODataObject)aPhone)._getContainmentPropertyName());
        assertEquals(anEmployee, ((SDODataObject)aPhone).getContainer());
        // get back POJO from wrapped PhoneImpl
        Phone aPhonePOJO = (Phone)((POJOValueStore)((SDODataObject)aPhone)._getCurrentValueStore()).getObject();
        assertNotNull(aPhonePOJO);
        assertTrue(aPhonePOJO instanceof Phone);

        // get phone# (non-wrapped) dataType object
        Object number = ((SDODataObject)aPhone).get(TEST_PHONE_NUMBER_FIELD_NAME);
        assertNotNull(number);
        assertTrue(number instanceof String);
        assertEquals(TEST_PHONE_1_ORIGINAL, number);

        // 2nd time verify we are getting the wrapped object from the cache
        SDODataObject aPhone2 = (SDODataObject)anEmployee.get(TEST_PHONE_FIRST_IN_ARRAY_XPATH);
        assertNotNull(aPhone2);
        //assertTrue(aPhone2 instanceof SDODataObject);
        assertEquals(aPhone, aPhone2);

        // Object modification integrity: clear cached value on modifications/deletions
        // NOTE: modify backend POJO, get it again, Synchronization of the cached property is not supported outside of interface calls
        Phone aPhone2POJO = rootPOJO.getPhone(0);
        aPhone2POJO.setNumber("0000000");
        // 3rd time verify we are not getting the wrapped unupdated object from the cache
        SDODataObject aPhone3 = (SDODataObject)anEmployee.get(TEST_PHONE_FIRST_IN_ARRAY_XPATH);
        assertNotNull(aPhone3);
        assertEquals(aPhone2, aPhone3);
        assertNotSame(aPhone2POJO.getNumber(), aPhone3.get(TEST_PHONE_NUMBER_FIELD_NAME));

        // set isMany object's property, check that we get the modified object on a subsequent get
        aPhone3.set(TEST_PHONE_NUMBER_FIELD_NAME, "1111111");
        SDODataObject aPhone4 = (SDODataObject)anEmployee.get(TEST_PHONE_FIRST_IN_ARRAY_XPATH);
        assertNotNull(aPhone4);
        assertEquals(aPhone3, aPhone4);
        Object number4 = ((SDODataObject)aPhone4).get(TEST_PHONE_NUMBER_FIELD_NAME);
        assertEquals("1111111", number4);

        // swap out an isMany object, verify that we picked up the change on a subsequent get
        ((SDODataObject)aPhone).delete();
        SDODataObject aPhone1 = (SDODataObject)anEmployee.get(TEST_PHONE_FIRST_IN_ARRAY_XPATH);
        assertNotNull(aPhone1);
        assertTrue(aPhone1 instanceof SDODataObject);
        // objects deleted are removed from the Map, the next get is a "different" object hash
        assertFalse(aPhone2.equals(aPhone1));

        // complex 1-1 property set (after a previous get of this object)
        SDODataObject aNewAddress2 = wrap(new Address(TEST_ADDRESS_STREET_MODIFICATION), "AddressType");

        //see public void updateContainment(Property property, Object value) {
        anEmployee.set(TEST_EMPLOYEE_ADDRESS_FIELD_NAME, aNewAddress2);
        Object anAddressMod2 = anEmployee.get(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        assertNotNull(anAddressMod2);
        // verify we are wrapping inside a DataObject
        assertTrue(anAddressMod2 instanceof SDODataObject);
        String aStreetName2 = (String)((SDODataObject)anAddressMod2)//
        .get(TEST_ADDRESS_STREET_FIELD_NAME);
        assertNotNull(aStreetName2);
        // verify dataType property was modified
        assertEquals(TEST_ADDRESS_STREET_MODIFICATION, aStreetName2);

        // test set/unset
        // simple property set
        anEmployee.set(TEST_EMPLOYEE_NAME_FIELD_NAME,//
                       TEST_EMPLOYEE_NAME_MODIFICATION);
        anEmployee.unset(TEST_EMPLOYEE_NAME_FIELD_NAME);
        Object aName3 = anEmployee.get(TEST_EMPLOYEE_NAME_FIELD_NAME);

        // default value is null in XSD
        assertNull(aName3);
        Object defaultValue = anEmployee.getInstanceProperty(TEST_EMPLOYEE_NAME_FIELD_NAME)//
        .getDefault();
        assertEquals(defaultValue, aName3);

        // unset complex property
        anEmployee.unset("phones[2]");
        try {
            Object anUnsetAddress = anEmployee.get("phones[2]");
        } catch (IndexOutOfBoundsException e) {
            // get() should not throw exceptions (SDO 2.1 Spec)
            fail("An IndexOutOfBoundsException occurred but was not expected.");
        }
    }

    public void testPOJOValueStoreUnSetPreviouslyUnSet() {
        // setup root DO wrapped around a POJO with types set
        SDODataObject anEmployee = setupDataObjectWithPOJOValueStore(true, true);

        // set address to null
        SDODataObject anAddress = null;
        anEmployee.unset(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        // verify set status
        boolean addressSet = anEmployee.isSet(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);

        // isSet status should be false (but not for set(property, null))
        assertFalse(addressSet);

        // check pojo for default value
        Address anAddressPOJO = null;//(Address)((POJOValueStore)((SDODataObject)anAddress).getCurrentValueStore()).getObject();
        Object defaultValue = anEmployee.getInstanceProperty(TEST_EMPLOYEE_NAME_FIELD_NAME)//
        .getDefault();
        assertEquals(defaultValue, anAddressPOJO);
    }

    /**
     * UnSet tests
     * Issue: Unset using default values other than null.
     * An unset of a property will clear the cache value associated with this property (Cached) and
     * do a set on the pojo to the default (usually null).
     * The result of this is that an isSet() call will check the cache, will not find it and will do a
     * get on the pojo which will return null or the default ??? if this value is not null then isSet() =
     * true after an unset() using a non-null default value.
     */
    public void testPOJOValueStoreUnSetPreviouslySet() {
        // setup root DO wrapped around a POJO with types set
        SDODataObject anEmployee = setupDataObjectWithPOJOValueStore(true, true);

        // set address 
        //SDODataObject anAddress = (SDODataObject)anEmployee.get(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        anEmployee.unset(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        // verify set status
        boolean addressSet = anEmployee.isSet(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);

        // isSet status should be false (but not for set(property, null))
        assertFalse(addressSet);

        // get address the sdo way (should be null)
        SDODataObject anAddress2 = (SDODataObject)anEmployee.get(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        Object defaultValue = anEmployee.getInstanceProperty(TEST_EMPLOYEE_NAME_FIELD_NAME)//
        .getDefault();
        assertEquals(defaultValue, anAddress2);
    }

    /**
     * UnSet tests
     * Issue: Unset using default values other than null.
     * An unset of a property will clear the cache value associated with this property (Cached) and
     * do a set on the pojo to the default (usually null).
     * The result of this is that an isSet() call will check the cache, will not find it and will do a
     * get on the pojo which will return null or the default ??? if this value is not null then isSet() =
     * true after an unset() using a non-null default value.
     *
     * Issue: 20060831-2: external references are not updated after unset
     * Here we get a reference to address, unset the address in its parent employee and verify that
     * the address reference was also updated
     */
    public void testFailPOJOValueStoreUnSetPreviouslySetReferenceNotUpdated() {
        // setup root DO wrapped around a POJO with types set
        SDODataObject anEmployee = setupDataObjectWithPOJOValueStore(true, true);

        // set address 
        SDODataObject anAddress = (SDODataObject)anEmployee.get(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        anEmployee.unset(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        // verify set status
        boolean addressSet = anEmployee.isSet(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);

        // isSet status should be false (but not for set(property, null))
        assertFalse(addressSet);

        // check old addressImpl reference to pojo for default value (it was not garbage collected in the unset above)
        // get address directly via unsupported interface
        Address anAddressPOJO = (Address)((POJOValueStore)((SDODataObject)anAddress)._getCurrentValueStore()).getObject();

        // get address the sdo way (should be null)
        SDODataObject anAddress2 = (SDODataObject)anEmployee.get(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        Object defaultValue = anEmployee.getInstanceProperty(TEST_EMPLOYEE_NAME_FIELD_NAME)//
        .getDefault();
        assertEquals(defaultValue, anAddress2);
        // TODO: Note: external references to pojos will not be updated after unset, a new get(property) is required
        //assertNotSame(defaultValue, anAddressPOJO);
        assertEquals(defaultValue, anAddressPOJO);
    }

    // simple types will be cached so we can check for isSet status after an unSet
    // testcase: get/unset/isset is different from unset/isset because the first get will cache the value
    public void testPOJOValueStoreUnSetSimpleTypePreviouslySet() {
        // setup root DO wrapped around a POJO with types set
        SDODataObject anEmployee = setupDataObjectWithPOJOValueStore(true, true);

        // set address 
        SDODataObject anAddress = (SDODataObject)anEmployee.get(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
        anAddress.unset(TEST_ADDRESS_STREET_FIELD_NAME);
        // verify set status
        boolean addressSet = anAddress.isSet(TEST_ADDRESS_STREET_FIELD_NAME);

        // isSet status should be false (even for set(property, null)
        assertFalse(addressSet);

        // check pojo for default value
        Address anAddressPOJO = (Address)((POJOValueStore)((SDODataObject)anAddress)._getCurrentValueStore()).getObject();
        Object defaultValue = anEmployee.getInstanceProperty(TEST_EMPLOYEE_NAME_FIELD_NAME)//
        .getDefault();

        //assertEquals(defaultValue, anAddressPOJO);        
    }

    public void testPOJOValueStoreSetNullPreviouslySet() {
        // setup root DO wrapped around a POJO with types set
        SDODataObject anEmployee = setupDataObjectWithPOJOValueStore(true, true);

        // set address to null
        SDODataObject anAddress = null;
        anEmployee.set(TEST_EMPLOYEE_ADDRESS_FIELD_NAME, anAddress);
        // verify set status
        boolean addressSet = anEmployee.isSet(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);

        // isSet status should be false for set(property, null)
        assertFalse(addressSet);

    }

    /*
        public void testPOJOValueStoreIsManySetNullPreviouslySet() {
            // setup root DO wrapped around a POJO with types set
            SDODataObject anEmployee = setupDataObjectWithPOJOValueStore(true, true);
            // set address to null
            SDODataObject anAddress = null;
            anEmployee.set("address", anAddress);
            // verify set status
            boolean addressSet = anEmployee.isSet("address");
            assertTrue(addressSet);

        }

        public void testPOJOValueStoreSetNullPreviouslyUnSet() {
            // setup root DO wrapped around a POJO with types set
            SDODataObject anEmployee = setupDataObjectWithPOJOValueStore(false, false);

            // set address to null
            SDODataObject anAddress = null;
            anEmployee.set("address", anAddress);
            // verify set status
            boolean addressSet = anEmployee.isSet("address");
            assertTrue(addressSet);
        }

        public void testPOJOValueStoreIsManySetNullPreviouslyUnSet() {
            // setup root DO wrapped around a POJO with types set
            SDODataObject anEmployee = setupDataObjectWithPOJOValueStore(false, false);

            // set phones to null
            SDODataObject anAddress = null;
            anEmployee.set("address", anAddress);
            // verify set status
            boolean addressSet = anEmployee.isSet("address");
            assertTrue(addressSet);
        }
    */
    /*
        public void testFailInitializePOJOPluggableMapUsingSystemPropertyDuringLoad() {
            // set property
            System.setProperty(SDO_PLUGGABLE_MAP_IMPL_CLASS_KEY,//
                    POJO_PLUGGABLE_IMPL_CLASS_NAME);
            String pluggableClassName = System.getProperty(//
            SDO_PLUGGABLE_MAP_IMPL_CLASS_KEY,//
            POJO_PLUGGABLE_IMPL_CLASS_NAME);
            assertNotNull(pluggableClassName);
            assertEquals(POJO_PLUGGABLE_IMPL_CLASS_NAME,//
                         pluggableClassName);
            // create a dataObject
            Object anEmployee = load(DATAOBJECT_XML_PATH);
            // check that we received a DataObject
            assertNotNull(anEmployee);
            assertTrue(anEmployee instanceof SDODataObject);
            // verify ValueStore is POJO type
            assertEquals(POJO_PLUGGABLE_IMPL_CLASS_NAME,//
                         ((SDODataObject)anEmployee).getCurrentValueStore().getClass().getName());
            Object anAddress = ((SDODataObject)anEmployee).get(TEST_EMPLOYEE_ADDRESS_FIELD_NAME);
            assertNotNull(anAddress);
            assertTrue(anEmployee instanceof SDODataObject);
            // verify containment
            assertEquals(TEST_EMPLOYEE_ADDRESS_FIELD_NAME,//
                    ((SDODataObject)anAddress).getContainmentPropertyName());
            assertEquals(anEmployee, ((SDODataObject)anAddress).getContainer());
            // check embedded POJO objects
            Address anAddressPOJO = (Address)((POJOValueStore)((SDODataObject)anEmployee).getCurrentValueStore()).getObject();
            assertNotNull(anAddressPOJO);
        }
    */

    /**
     * We need to verify that calling createDataObject will not enter an infinite loop
     * Also, we would like to know if reflectively setting an empty pojo before we actualy use
     * getCurrentValueStore().setObject(pojo) will fix the invalid state the DO is in before this 2nd call
     */

    /*    public void testFailPOJOValueStoreUsingCreateDataObject() {
            // create data object
            // create an empty DataObject
            SDODataObject anEmployee = (SDODataObject)aHelperContext.getDataFactory().create(//
            aHelperContext.getTypeHelper().getType(rootTypeUri, rootTypeName));
            POJOValueStore aPluggableMap = (POJOValueStore)anEmployee.getCurrentValueStore();
            assertNotNull(aPluggableMap);
            assertTrue(aPluggableMap instanceof POJOValueStore);

            // types are not set until we call getType() on first get after SDODataObject initialization - force the check here
            //SDOType aType = (SDOType)anEmployee.getType();

            // set POJO
            Employee anEmployeePOJO = new Employee("2", TEST_EMPLOYEE_NAME_ORIGINAL);
            // associate pojo
            aPluggableMap.setObject(anEmployeePOJO);
            aPluggableMap.initialize(anEmployee);
           // try {
                // add address
                SDODataObject anAddressWrapper = (SDODataObject)anEmployee.createDataObject(//
                    "address", rootTypeUri, "AddressType");

                // get property
                SDODataObject anAddress = (SDODataObject)anEmployeePOJO.get("address");
                // TODO: Issue20060830-1: an unset Address POJO is returned as null or an empty Address?
                assertNull(anAddress);
                //assertTrue(anAddress instanceof SDODataObject);
            //} catch (Exception e) {
            //    System.out.println("createDataObject not supported: " + e.getMessage());
            //}
        }
    */
    private Object getControlObject() {
        return getControlObject(true, true);
    }

    /**
     * @param withPhones
     * @return
     */
    private Object getControlObject(boolean isAddressSet, boolean withPhones) {
        // add address
        Address anAddress = null;
        List phones = null;

        if (isAddressSet) {
            anAddress = new Address(TEST_ADDRESS_STREET_ORIGINAL);
        }

        // create a list of phones to add to an employee
        if (withPhones) {
            phones = new ArrayList();
            phones.add(new Phone("0", TEST_PHONE_1_ORIGINAL));
            phones.add(new Phone("1", TEST_PHONE_2_ORIGINAL));
            phones.add(new Phone("2", TEST_PHONE_3_ORIGINAL));
        }

        Employee anEmployee = new Employee("1", TEST_EMPLOYEE_NAME_ORIGINAL, anAddress, phones);
        return anEmployee;
    }

    private SDODataObject wrap(Object pojo, String typeName) {
        // create an empty DataObject
        SDODataObject anSDO = (SDODataObject)aHelperContext.getDataFactory().create(//
        aHelperContext.getTypeHelper().getType(rootTypeUri, typeName));
        POJOValueStore aPluggableMap = new POJOValueStoreReadWrite(aHelperContext);

        //aPluggableMap = (POJOValueStore)anEmployee.getCurrentValueStore();
        // set valueStore
        anSDO._setCurrentValueStore(aPluggableMap);
        //aPluggableMap.initialize(anSDO);
        //POJOValueStore aPluggableMap = (POJOValueStore)anSDO.getCurrentValueStore();
        assertNotNull(aPluggableMap);
        assertTrue(aPluggableMap instanceof POJOValueStore);

        // types are not set until we call getType() on first get after SDODataObject initialization - force the check here
        //SDOType aType = (SDOType)anSDO.getType();
        // set POJO        
        aPluggableMap.setObject(pojo);
        aPluggableMap.initialize(anSDO);
        return anSDO;
    }

    private SDODataObject setupDataObjectWithPOJOValueStore(boolean isPhoneArraySet) {
        return setupDataObjectWithPOJOValueStore(POJO_PLUGGABLE_IMPL_CLASS_NAME,//
                                                 true, isPhoneArraySet);
    }

    private SDODataObject setupDataObjectWithPOJOValueStore(//
    boolean isAddressSet, boolean isPhoneArraySet) {
        return setupDataObjectWithPOJOValueStore(POJO_PLUGGABLE_IMPL_CLASS_NAME,//
                                                 isAddressSet, isPhoneArraySet);
    }

    private SDODataObject setupDataObjectWithPOJOValueStore(//
    String implClass, boolean isAddressNull, boolean isPhoneArrayNull) {
        // setup system for POJO    (override JVM System property)
        System.setProperty(SDOConstants.SDO_PLUGGABLE_MAP_IMPL_CLASS_KEY,//
                           implClass);
        assertEquals(System.getProperty(//
        SDOConstants.SDO_PLUGGABLE_MAP_IMPL_CLASS_KEY,//
        SDOConstants.EMPTY_STRING),//
                     implClass);
        // create an empty DataObject
        SDODataObject anEmployee = (SDODataObject)dataFactory.create(//
        typeHelper.getType(rootTypeUri, rootTypeName));

        // The following code section overwrites the default ValueStore
        // Why? normally you would not change the ValueStore impl class in a JVM, therefore
        // we set and cache the ValueStore cache on the first read from the system property
        // in a testing environment where we test multiple implementations we need the code section below
        // NOTE: valueStore cannot be changed after first DO intitialization - the classname is a static variable on DataObject
        // directly create the valueStore (discard the default if it is a default implementation)
        try {
            POJOValueStore aPluggableMap = (POJOValueStore)Class.forName(implClass).newInstance();

            //aPluggableMap = (POJOValueStore)anEmployee.getCurrentValueStore();
            //aPluggableMap = (POJOValueStore)anEmployee.getCurrentValueStore();
            // set valueStore
            anEmployee._setCurrentValueStore(aPluggableMap);
            assertNotNull(aPluggableMap);
            assertTrue(aPluggableMap instanceof POJOValueStore);

            // types are not set until we call getType() on first get after SDODataObject initialization - force the check here
            SDOType aType = (SDOType)anEmployee.getType();

            // set POJO
            Employee rootPOJO = (Employee)getControlObject(isAddressNull, isPhoneArrayNull);
            aPluggableMap.setObject(rootPOJO);
            aPluggableMap.initialize(anEmployee);
        } catch (ClassNotFoundException cnfe) {
            // TODO: throw or propagate these properly
            throw new IllegalArgumentException(cnfe.getMessage());
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException(iae.getMessage());
        } catch (InstantiationException ie) {
            throw new IllegalArgumentException(ie.getMessage());
        }

        //anEmployee.setProperties(aPluggableMap);
        return anEmployee;
    }
}