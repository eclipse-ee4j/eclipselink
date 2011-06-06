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
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * This test verifies that if we do a refresh while in transaction, we do
 * not update the original in the shared cache.  As we are reading via the
 * write connection, we would just end up merging uncommitted changes into the
 * shared cache.
 * @author  smcritch
 */
public class TransactionIsolationRefreshTest extends AutoVerifyTestCase {
    UnitOfWork unitOfWork;
    String oldName;
    Address oldAddress;
    ReadObjectQuery refreshQuery;

    protected void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        unitOfWork = getSession().acquireUnitOfWork();

        refreshQuery = new ReadObjectQuery(Employee.class);
        refreshQuery.refreshIdentityMapResult();
        refreshQuery.cascadeAllParts();
    }

    public void reset() throws Exception {
        if (unitOfWork != null) {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            unitOfWork.release();
            unitOfWork = null;
            refreshQuery = null;
        }
    }

    public void test() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression criteria = builder.get("period").get("endDate").notNull();

        Employee employee = (Employee)getSession().readObject(Employee.class, criteria);

        Address dummyAddress = new Address();
        dummyAddress.setCity("El Paso");
        Employee dummyManager = new Employee();
        dummyManager.setFirstName("Bill");
        Project dummyProject = new SmallProject();
        dummyProject.setName("survive");

        // set up a pretend refresh: make changes to the original!
        employee.setFirstName("budd");
        employee.setAddress(dummyAddress);
        employee.getProjects().clear();
        employee.getProjects().add(dummyProject);
        employee.getPeriod().setEndDate(null);
        employee.setManager(dummyManager);

        Employee clone = (Employee)unitOfWork.registerObject(employee);

        unitOfWork.beginEarlyTransaction();

        refreshQuery.setSelectionObject(clone);
        clone = (Employee)unitOfWork.executeQuery(refreshQuery);

        // first verify that we did not touch the original...
        strongAssert(employee.getFirstName() == "budd", 
                     "Should not have refreshed the original (direct to field)");
        strongAssert(employee.getAddress() == dummyAddress, 
                     "Should not have refreshed the original (private one to one)");
        strongAssert(employee.getProjects().elementAt(0) == dummyProject, 
                     "Should not have refreshed the originial (many to many)");
        strongAssert(employee.getPeriod().getEndDate() == null, 
                     "Should not have refreshed the originial (aggregate)");
        strongAssert(employee.getManager() == dummyManager, 
                     "Should not have refreshed the originial (one to one)");

        // Now verify that we correctly refreshed the clone.
        strongAssert(!clone.getFirstName().equals("budd"), "Did not refresh the clone (direct to field)");
        strongAssert(!clone.getAddress().getCity().equals("El Paso"), 
                     "Did not refresh the clone (private one to one)");
        strongAssert(clone.getProjects().size() != 1 || 
                    !(clone.getProjects().elementAt(0) instanceof SmallProject) || 
                    !((SmallProject)clone.getProjects().elementAt(0)).getName().equals("survive"), 
                     "Did not refresh the clone (many to many)");
        strongAssert(clone.getPeriod().getEndDate() != null, "Did not refresh the clone (aggregate)");
        strongAssert((clone.getManager() == null) || !clone.getManager().getFirstName().equals("Bill"), 
                     "Did not refresh the clone (one to one)");
    }
}
