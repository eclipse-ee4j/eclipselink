/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.fetchgroups;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import junit.framework.TestSuite;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.IndirectCollection;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.LoadGroup;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.PhoneNumber;

import org.junit.Test;

/**
 * Test named nested FetchGroup usage.
 * 
 * @author dclarke
 * @since EclipseLink 2.1
 */
public class NestedDefaultFetchGroupTests extends BaseFetchGroupTests {

    public NestedDefaultFetchGroupTests() {
        super();
    }

    public NestedDefaultFetchGroupTests(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("NestedDefaultFetchGroupTests");
        
        suite.addTest(new NestedDefaultFetchGroupTests("testSetup"));
        suite.addTest(new NestedDefaultFetchGroupTests("findMinEmployee"));
        suite.addTest(new NestedDefaultFetchGroupTests("findMinEmployeeLoadAddress"));
        suite.addTest(new NestedDefaultFetchGroupTests("findMinEmployeeLoadPhones"));
        suite.addTest(new NestedDefaultFetchGroupTests("findMinEmployeeLoadAddressAndPhones"));
        suite.addTest(new NestedDefaultFetchGroupTests("findMinEmployeeLoadAddressAndPhoneUsingFetchGroup"));
        suite.addTest(new NestedDefaultFetchGroupTests("allAddress"));
        suite.addTest(new NestedDefaultFetchGroupTests("allPhone"));
        suite.addTest(new NestedDefaultFetchGroupTests("singleResultMinEmployeeFetchJoinAddress"));
        suite.addTest(new NestedDefaultFetchGroupTests("singleResultMinEmployeeFetchJoinAddressLoad"));
        
        return suite;
    }
    
    /*
     * Set default fetch groups. 
     * 
     * @see EmployeeCustomizer
     * @see PhoneCustomizer
     */
    public void setUp() {
        super.setUp();
        
        try {
            (new EmployeeCustomizer()).customize(employeeDescriptor);
            (new PhoneCustomizer()).customize(phoneDescriptor);
            // reprepare read queries after all fetch groups set into all descriptors.
            reprepareReadQueries(employeeDescriptor);
            reprepareReadQueries(phoneDescriptor);
        } catch (RuntimeException rtEx) {
            throw rtEx;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        assertConfig(employeeDescriptor, defaultEmployeeFG, 0);
        assertConfig(phoneDescriptor, defaultPhoneFG, 0);
    }
    
    @Test
    public void findMinEmployee() {
        internalFindMinEmployee(false, false, false);
    }

    @Test
    public void findMinEmployeeLoadAddress() {
        internalFindMinEmployee(true, false, true);
    }

    public void findMinEmployeeLoadPhones() {
        internalFindMinEmployee(false, true, true);
    }

    public void findMinEmployeeLoadAddressAndPhones() {
        internalFindMinEmployee(true, true, true);
    }

    public void findMinEmployeeLoadAddressAndPhoneUsingFetchGroup() {
        internalFindMinEmployee(true, true, false);
    }

    void internalFindMinEmployee(boolean loadAddress, boolean loadPhones, boolean useLoadGroup) {        
        EntityManager em = createEntityManager("fieldaccess");
        int minId = minEmployeeIdWithAddressAndPhones(em);
        assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        boolean load = false;
        boolean originalLoad = false;        
        if(!useLoadGroup) {
            assertTrue(loadAddress == loadPhones);
            load = loadAddress;
            originalLoad = defaultEmployeeFG.shouldLoad();
            if(load != originalLoad) {
                defaultEmployeeFG.setShouldLoad(load);
            }
        }

        try {            
            Employee emp;
            if(useLoadGroup) {
                LoadGroup lg = defaultEmployeeFG.toLoadGroup();
                if(!loadAddress) {
                    lg.removeAttribute("address");
                }
                if(!loadPhones) {
                    lg.removeAttribute("phoneNumbers");
                }
                HashMap<String, Object> hints = new HashMap(1);
                hints.put(QueryHints.LOAD_GROUP, lg);

                emp = em.find(Employee.class, minId, hints);
            } else {
                emp = em.find(Employee.class, minId);
            }
    
            assertNotNull(emp);
            int nExpected = 2;
            if(loadAddress) {
                nExpected++;
            }
            if(loadPhones) {
                nExpected++;
            }
            assertEquals(nExpected, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            
            boolean addressInstantiated = ((ValueHolderInterface)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getAttributeValueFromObject(emp)).isInstantiated();
            assertTrue(loadAddress == addressInstantiated);

            boolean phonesInstantiated = ((IndirectCollection)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("phoneNumbers")).getAttributeValueFromObject(emp)).isInstantiated();
            assertTrue(loadPhones == phonesInstantiated);
            
            emp.getAddress();
            emp.getPhoneNumbers().size();
            
            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertFetched(emp, defaultEmployeeFG);
            assertFetchedAttribute(emp, "address");
            assertFetchedAttribute(emp, "phoneNumbers");
            
            // Check Address
            FetchGroup fgAddress = defaultEmployeeFG.getGroup("address");
            assertFetched(emp.getAddress(), fgAddress);
            
            // Check phones
            FetchGroup fgPhones = defaultEmployeeFG.getGroup("phoneNumbers");            
            for (PhoneNumber phone: emp.getPhoneNumbers()) {
                assertFetched(phone, fgPhones);
            }
        } finally {
            if(!useLoadGroup) {
                if(load != originalLoad) {
                    defaultEmployeeFG.setShouldLoad(originalLoad);
                }
            }
        }
    }
/*    void internalFindMinEmployee(boolean loadAddress, boolean loadPhones) {
        EntityManager em = createEntityManager();
        int minId = minEmployeeIdWithAddressAndPhones(em);
        assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        FetchGroup fg = employeeDescriptor.getFetchGroupManager().getDefaultFetchGroup();
        FetchGroup phonesFg = fg.getGroup("phoneNumbers");

        FetchGroup fgAddress = fg.getGroup("address");
        boolean originalLoadAddress = fgAddress.shouldLoad(); 
        if(originalLoadAddress != loadAddress) {
            fgAddress.setShouldLoad(loadAddress);
        }

        FetchGroup fgPhones = fg.getGroup("phoneNumbers");
        boolean originalLoadPhones = fgPhones.shouldLoad(); 
        if(originalLoadPhones != loadPhones) {
            fgPhones.setShouldLoad(loadPhones);
        }
        
        try {            
            Employee emp = em.find(Employee.class, minId);
    
            assertNotNull(emp);
            int nExpected = 2;
            if(loadAddress) {
                nExpected++;
            }
            if(loadPhones) {
                nExpected++;
            }
            assertEquals(nExpected, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            
            boolean addressInstantiated = ((ValueHolderInterface)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getAttributeValueFromObject(emp)).isInstantiated();
            boolean phonesInstantiated = ((IndirectCollection)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("phoneNumbers")).getAttributeValueFromObject(emp)).isInstantiated();

            if(loadAddress) {
                assertTrue(addressInstantiated);
            }
            if(loadPhones) {
                assertTrue(phonesInstantiated);
            }
            
            emp.getAddress();
            emp.getPhoneNumbers().size();
            
            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertFetched(emp, defaultEmployeeFG);
            assertFetchedAttribute(emp, "address");
            assertFetchedAttribute(emp, "phoneNumbers");
            
            // Check Address
            assertFetched(emp.getAddress(), fgAddress);
            
            // Check phones
            for (PhoneNumber phone: emp.getPhoneNumbers()) {
                assertFetched(phone, fgPhones);
            }
        } finally {
            if(originalLoadAddress != loadAddress) {
                fgAddress.setShouldLoad(originalLoadAddress);
            }
            if(originalLoadPhones != loadPhones) {
                fgPhones.setShouldLoad(originalLoadPhones);
            }
        }
    }*/

    @Test
    public void allAddress() {
        EntityManager em = createEntityManager("fieldaccess");
        
        
        List<Address> allAddresses = em.createQuery("SELECT a FROM Address a", Address.class).getResultList();
        
        for (Address address: allAddresses) {
            assertNoFetchGroup(address);
        }
    }

    @Test
    public void allPhone() {
        EntityManager em = createEntityManager("fieldaccess");
        
        
        List<PhoneNumber> allPhones = em.createQuery("SELECT p FROM PhoneNumber p", PhoneNumber.class).getResultList();
        
        for (PhoneNumber phone: allPhones) {
            assertFetched(phone, defaultPhoneFG);
        }
    }

    @Test
    public void singleResultMinEmployeeFetchJoinAddress() {
        internalSingleResultMinEmployeeFetchJoinAddress(false);
    }
    public void singleResultMinEmployeeFetchJoinAddressLoad() {
        internalSingleResultMinEmployeeFetchJoinAddress(true);
    }
    void internalSingleResultMinEmployeeFetchJoinAddress(boolean shouldLoad) {
        EntityManager em = createEntityManager("fieldaccess");

        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e JOIN FETCH e.address WHERE e.id IN (SELECT MIN(p.owner.id) FROM PhoneNumber p)", Employee.class);
        if(shouldLoad) {
            query.setHint(QueryHints.LOAD_GROUP_ATTRIBUTE, "phoneNumbers");
        }
        Employee emp = query.getSingleResult();

        assertNotNull(emp);
        int nExpected = 1;
        if(shouldLoad) {
            nExpected++;
        }
        assertEquals(nExpected, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
    }

    public static class EmployeeCustomizer implements DescriptorCustomizer {

        public void customize(ClassDescriptor descriptor) throws Exception {
            defaultEmployeeFG = new FetchGroup("Employee.default");
            defaultEmployeeFG.addAttribute("firstName");
            defaultEmployeeFG.addAttribute("lastName");
            defaultEmployeeFG.addAttribute("address.country");
            defaultEmployeeFG.addAttribute("phoneNumbers.areaCode");

            descriptor.getFetchGroupManager().setDefaultFetchGroup(defaultEmployeeFG);
        }

    }

/*    public static class PhoneCustomizer implements DescriptorCustomizer {

        public void customize(ClassDescriptor descriptor) throws Exception {
            defaultPhoneFG = new FetchGroup<PhoneNumber>("PhoneNumber.default");
            defaultPhoneFG.addAttribute("number");
            descriptor.getFetchGroupManager().setDefaultFetchGroup(defaultPhoneFG);
        }

    }*/

}
