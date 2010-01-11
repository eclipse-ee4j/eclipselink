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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * Merge test to handle the following advanced scenario:
 * <ul>
 *    <li> begin early transaction and read bill and Beatrix in UOW
 *  <li> change both names, and make Beatrix an employee of bill.
 *    <li> read bill and beatrix into the UOW.
 *    <li> commit.  verify no clones in shared cache, and all pointers are valid.
 * </ul>
 *  @author  smcritch
 */
public class TransactionIsolationMergeCircularIndirectionTest extends AutoVerifyTestCase {
    UnitOfWork unitOfWork;
    Employee teamLead;
    Employee freelance;
    String teamLeadFirstName;
    String freelanceFirstName;

    protected void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        unitOfWork = getSession().acquireUnitOfWork();
    }

    public void reset() throws Exception {
        if (unitOfWork != null) {
            unitOfWork.release();
            unitOfWork = null;
        }
        if (teamLead != null) {
            unitOfWork = getSession().acquireUnitOfWork();
            Employee clone = (Employee)unitOfWork.readObject(teamLead);
            clone.setFirstName(teamLeadFirstName);
            Employee clone2 = (Employee)unitOfWork.readObject(freelance);
            clone2.setFirstName(freelanceFirstName);
            clone2.setManager(null);
            unitOfWork.commit();
            unitOfWork = null;
            teamLeadFirstName = null;
            teamLead = null;
            freelance = null;
            freelanceFirstName = null;
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        }
    }

    public void test() {
        unitOfWork.beginEarlyTransaction();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("gender").equal("Male");
        Employee teamLeadClone = (Employee)unitOfWork.readObject(Employee.class, expression);
        teamLeadClone.setFirstName("Bill");

        // By picking a male + female guaranteed to be different.
        builder = new ExpressionBuilder();
        expression = builder.get("manager").isNull().and(builder.get("gender").equal("Female"));
        Employee freelanceClone = (Employee)unitOfWork.readObject(Employee.class, expression);
        freelanceClone.setFirstName("Beatrix");

        teamLeadClone.addManagedEmployee(freelanceClone);

        // Due to the isolation it is possible to read the original values last.
        // Identity should not be lost when the uow commits.
        teamLead = (Employee)getSession().readObject(teamLeadClone);
        teamLeadFirstName = teamLead.getFirstName();

        freelance = (Employee)getSession().readObject(freelanceClone);
        freelanceFirstName = freelance.getFirstName();

        unitOfWork.commit();
        unitOfWork = null;

        Employee newTeamLead = (Employee)getSession().getIdentityMapAccessor().getFromIdentityMap(teamLeadClone);
        strongAssert(newTeamLead == teamLead, 
                     "The previous original should have been merged into the shared cache.");
        strongAssert(newTeamLead.managedEmployees.isInstantiated(), 
                     "The valueholder [managedEmployees] in the shared cache should be triggered now.");

        Employee newFreelance = (Employee)getSession().getIdentityMapAccessor().getFromIdentityMap(freelanceClone);
        strongAssert(newFreelance == freelance, "Identity was lost on managed employees accross the 1-1");
        strongAssert(newTeamLead.getManagedEmployees().contains(freelance), 
                     "teamLead is not pointing to the shared cache version of its new employee.");
        strongAssert(newFreelance.manager.isInstantiated(), 
                     "The valueholder [manager] in the shared cache should be triggered now.");
        strongAssert(newFreelance.getManager() == newTeamLead, 
                     "freelance is not pointing to the shared cache version of its new manager.");
    }
}
