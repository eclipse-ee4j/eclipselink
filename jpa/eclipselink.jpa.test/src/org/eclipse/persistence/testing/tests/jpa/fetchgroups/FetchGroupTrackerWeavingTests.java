/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.fetchgroups;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestSuite;

import org.eclipse.persistence.internal.jpa.weaving.ClassWeaver;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;

import org.junit.Test;



/**
 * Test to verify changes made to {@link FetchGroupTracker} and
 * {@link ClassWeaver} are working as expected.
 * 
 * @author dclarke
 * @since EclipseLink 2.1
 */
public class FetchGroupTrackerWeavingTests extends JUnitTestCase {

    String checkAttribute = null;
    String checkForSetAttribute = null;

    public FetchGroupTrackerWeavingTests() {
        super();
    }
    
    public FetchGroupTrackerWeavingTests(String name) {
        super(name);
    }
    
    /*
     * Fetch Group tests require weaving.
     */
    public void runBare() throws Throwable {
        if (isWeavingEnabled()) {
            super.runBare();
        }
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("FetchGroupTrackerWeavingTests");
        
        suite.addTest(new FetchGroupTrackerWeavingTests("testSetup"));
        suite.addTest(new FetchGroupTrackerWeavingTests("verifyCheckFetchedForSetWithFetchGroup"));
        suite.addTest(new FetchGroupTrackerWeavingTests("verifyCheckFetchedWithFetchGroup"));
        suite.addTest(new FetchGroupTrackerWeavingTests("verifyCheckFetchedForSetWithFetchGroup_OneToOne"));
        suite.addTest(new FetchGroupTrackerWeavingTests("verifyCheckFetchedWithFetchGroup_OneToOne"));
        suite.addTest(new FetchGroupTrackerWeavingTests("verifyCheckFetchedForSetWithFetchGroup_OneToMany"));
        suite.addTest(new FetchGroupTrackerWeavingTests("verifyCheckFetchedWithFetchGroup_OneToMany"));
        
        return suite;
    }
    
    public void tearDown() {
        this.checkAttribute = null;
        this.checkForSetAttribute = null;
    }
    
    @Test
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());

        // Force uppercase for Postgres.
        if (getServerSession().getPlatform().isPostgreSQL()) {
            getServerSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
        }
    }
       
    @Test
    public void verifyCheckFetchedForSetWithFetchGroup() {
        Employee emp = new Employee();
        TestFetchGroup fg = new TestFetchGroup();
        ((FetchGroupTracker)emp)._persistence_setFetchGroup(fg);

        assertNull(this.checkAttribute);
        assertNull(this.checkForSetAttribute);

        emp.setFirstName("John");

        assertNull(this.checkAttribute);
        assertNotNull(this.checkForSetAttribute);
        assertEquals("firstName", this.checkForSetAttribute);
    }

    @Test
    public void verifyCheckFetchedWithFetchGroup() {
        Employee emp = new Employee();
        TestFetchGroup fg = new TestFetchGroup();
        ((FetchGroupTracker)emp)._persistence_setFetchGroup(fg);

        assertNull(this.checkAttribute);
        assertNull(this.checkForSetAttribute);

        emp.getFirstName();

        assertNull(this.checkForSetAttribute);
        assertNotNull(this.checkAttribute);
        assertEquals("firstName", this.checkAttribute);
    }

    @Test
    public void verifyCheckFetchedForSetWithFetchGroup_OneToOne() {
        Employee emp = new Employee();
        TestFetchGroup fg = new TestFetchGroup();
        ((FetchGroupTracker)emp)._persistence_setFetchGroup(fg);

        assertNull(this.checkAttribute);
        assertNull(this.checkForSetAttribute);

        emp.setAddress(new Address());

        assertNull(this.checkAttribute);
        assertNotNull(this.checkForSetAttribute);
        assertEquals("address", this.checkForSetAttribute);
    }

    @Test
    public void verifyCheckFetchedWithFetchGroup_OneToOne() {
        Employee emp = new Employee();
        TestFetchGroup fg = new TestFetchGroup();
        ((FetchGroupTracker)emp)._persistence_setFetchGroup(fg);

        assertNull(this.checkAttribute);
        assertNull(this.checkForSetAttribute);

        Address addr = emp.getAddress();

        assertNull(addr);
        assertNull(this.checkForSetAttribute);
        assertNotNull(this.checkAttribute);
        assertEquals("address", this.checkAttribute);
    }

    @Test
    public void verifyCheckFetchedForSetWithFetchGroup_OneToMany() {
        Employee emp = new Employee();
        TestFetchGroup fg = new TestFetchGroup();
        ((FetchGroupTracker)emp)._persistence_setFetchGroup(fg);

        assertNull(this.checkAttribute);
        assertNull(this.checkForSetAttribute);

        emp.setPhoneNumbers(new ArrayList<PhoneNumber>());

        assertNull(this.checkAttribute);
        assertNotNull(this.checkForSetAttribute);
        assertEquals("phoneNumbers", this.checkForSetAttribute);
    }

    @Test
    public void verifyCheckFetchedWithFetchGroup_OneToMany() {
        Employee emp = new Employee();
        TestFetchGroup fg = new TestFetchGroup();
        ((FetchGroupTracker)emp)._persistence_setFetchGroup(fg);

        assertNull(this.checkAttribute);
        assertNull(this.checkForSetAttribute);

        Collection<PhoneNumber> phones = emp.getPhoneNumbers();

        assertNotNull(phones);
        assertTrue(phones.isEmpty());

        assertNull(this.checkForSetAttribute);
        assertNotNull(this.checkAttribute);
        assertEquals("phoneNumbers", this.checkAttribute);
    }

    class TestFetchGroup extends FetchGroup {

        @Override
        public String onUnfetchedAttribute(FetchGroupTracker entity, String attributeName) {
            checkAttribute = attributeName;
            return null;
        }

        @Override
        public String onUnfetchedAttributeForSet(FetchGroupTracker entity, String attributeName) {
            checkForSetAttribute = attributeName;
            return null;
        }
    }
}
