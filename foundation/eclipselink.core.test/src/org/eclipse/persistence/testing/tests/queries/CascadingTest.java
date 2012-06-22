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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

/**
 * Abstract superclass for testing various cascading features
 * Subclasses must implement setTestConfiguration().
 *
 * @author Peter O'Blenis
 */
public abstract class CascadingTest extends TestCase {

    /*
     * The following varaibles must be set by the subclass in the setTestConfiguration() method
     */

    //Name of the test to be displayed in error messages
    protected String m_szTestName = null;

    //Maintain cache for test
    protected boolean m_bMaintainCache = false;

    //Type of cascading (See constants below)
    protected int m_nCascadeType = 0;

    //Cascading Constants
    protected static final int NO_CASCADE = 0;
    protected static final int CASCADE_PRIVATE = 1;
    protected static final int CASCADE_ALL = 2;
    protected Employee employeeFromDatabase;
    protected Employee m_reloadedEmployee;
    protected String m_szManagedEmployee;
    protected String m_szFirstName;
    protected String m_szPhoneNumber;

    public CascadingTest() {
        super();
        setTestConfiguration();
    }

    protected void cascadeCheck() {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(employeeFromDatabase);

        //If MaintainCache is turned off then nothing in the cached object should have changed.
        if (!m_bMaintainCache) {
            if (!m_reloadedEmployee.getFirstName().equals(m_szFirstName)) {
                throw new TestErrorException(m_szTestName + " test failed on firstName");
            }

            //PhoneNumber should have been refreshed from the database. Ensure that it was not refreshed from cache
            if (m_nCascadeType == CASCADE_PRIVATE) {
                if (!((PhoneNumber)m_reloadedEmployee.getPhoneNumbers().elementAt(0)).getType().equals(m_szPhoneNumber)) {
                    throw new TestErrorException(m_szTestName + ": private cascaded parts were not refreshed from db");
                }
            }

            //ManagedEmployee name should have been refreshed from the database. Ensure that it was not refreshed from cache
            if (m_nCascadeType == CASCADE_ALL) {
                if (!((Employee)m_reloadedEmployee.getManagedEmployees().elementAt(0)).getFirstName().equals(m_szManagedEmployee)) {
                    throw new TestErrorException(m_szTestName + ": public cascaded parts were not refreshed from db");
                }
            }

            for (int loop = 0; loop < 5; loop++) {
                // This test was randomloy failing because TopLink avoids refreshes within the millisecond
                // Changing to do a shallow refresh after to make the time to the other refresh less and more likely to fail.
                // so more consistent.
                employeeFromDatabase.setFirstName("Refresh too fast");
                ReadObjectQuery refreshQuery = new ReadObjectQuery(employeeFromDatabase);
                refreshQuery.refreshIdentityMapResult();
                getSession().executeQuery(refreshQuery);

                if (!employeeFromDatabase.getFirstName().equals(m_szFirstName)) {
                    throw new TestErrorException(m_szTestName + ": test failed on firstName loop-" + loop);
                }
            }
        }
        //If MaintainCache is turn on then the cached object should have changed according to the cascade policy
        else {
            if (!employeeFromDatabase.getFirstName().equals(m_szFirstName)) {
                throw new TestErrorException(m_szTestName + ": test failed on firstName");
            }

            //Check that private cascaded objects in the cache have been refreshed with db data
            if (m_nCascadeType == CASCADE_PRIVATE) {// || (m_nCascadeType == CASCADE_ALL))
                if (!((PhoneNumber)employeeFromDatabase.getPhoneNumbers().elementAt(0)).getType().equals(m_szPhoneNumber)) {
                    throw new TestErrorException(m_szTestName + ": private cache data not refreshed from db");
                }
            }

            //Check that all cascaded objects in the cache have been refreshed with db data
            if (m_nCascadeType == CASCADE_ALL) {
                if (!((Employee)employeeFromDatabase.getManagedEmployees().elementAt(0)).getFirstName().equals(m_szManagedEmployee)) {
                    throw new TestErrorException(m_szTestName + ":public cache data not refreshed from db");
                }
            }
        }
    }

    public void reset() {
        // Because the name of the employee was changed, clear the cache.
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setConfiguration(String szTestName, boolean bMaintainCache, int nCascadeSetting) {
        m_szTestName = szTestName;
        m_bMaintainCache = bMaintainCache;
        m_nCascadeType = nCascadeSetting;
    }

    abstract void setTestConfiguration();

    protected void setup() {
        //Fix bug 2722927
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        //Check that the subclass implemented setTestConfiguration() correctly 
        if (m_szTestName == null) {
            throw new TestErrorException("CascadingTest: Subclass did not initialize test properly");
        }

        ExpressionBuilder expBldr = new ExpressionBuilder();
        Expression whatWeWant = expBldr.get("lastName").equal("May");
        employeeFromDatabase = (Employee)getSession().readObject(Employee.class, whatWeWant);
    }

    public void test() {
        m_szFirstName = employeeFromDatabase.getFirstName();
        m_szPhoneNumber = ((PhoneNumber)employeeFromDatabase.getPhoneNumbers().elementAt(0)).getType();
        m_szManagedEmployee = ((Employee)employeeFromDatabase.getManagedEmployees().elementAt(0)).getFirstName();

        //Change the employees name 
        employeeFromDatabase.setFirstName("Foobar");

        //Change the first phone number
        ((PhoneNumber)employeeFromDatabase.getPhoneNumbers().elementAt(0)).setType("FooNumber");

        //Change the manager 
        ((Employee)employeeFromDatabase.getManagedEmployees().elementAt(0)).setFirstName("MrFoobar");
    }

    protected void verify() {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(employeeFromDatabase);

        //Set cache mode
        if (m_bMaintainCache) {
            //	query.maintainCache();
            query.refreshIdentityMapResult();
        } else {
            query.dontMaintainCache();
        }

        //Set Cascade mode
        switch (m_nCascadeType) {
        case CASCADE_PRIVATE:
            query.cascadePrivateParts();
            break;
        case CASCADE_ALL:
            query.cascadeAllParts();
            break;
        default:
            query.dontCascadeParts();
        }

        //Perform the query
        m_reloadedEmployee = (Employee)getSession().executeQuery(query);

        //Check that the expected changes (or lack there of) took place in the cached object
        cascadeCheck();
    }
}
