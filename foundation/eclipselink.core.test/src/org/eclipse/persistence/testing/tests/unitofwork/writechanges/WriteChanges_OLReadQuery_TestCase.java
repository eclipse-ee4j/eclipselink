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
package org.eclipse.persistence.testing.tests.unitofwork.writechanges;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 *  @version $Header: WriteChanges_OLReadQuery_TestCase.java 23-nov-2006.11:42:26 gyorke Exp $
 *  @author  smcritch
 *  @since   release specific (what release of product did this appear in)
 */
public class WriteChanges_OLReadQuery_TestCase extends AutoVerifyTestCase {
    protected Employee employee;

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        try {
            employee = (Employee)uow.readObject(Employee.class);
            employee.setFirstName("Stephen");
            uow.writeChanges();

            ReadObjectQuery query = new ReadObjectQuery(Employee.class);
            query.setSelectionCriteria((new ExpressionBuilder()).get("firstName").equal("Stephen"));

            employee = (Employee)uow.executeQuery(query);
        } finally {
            uow.release();
        }
    }

    public void verify() {
        if (employee != null) {
            Employee localEmp = 
                (Employee)getAbstractSession().getIdentityMapAccessor().getFromIdentityMap(this.employee);
            if (localEmp != null && getAbstractSession().compareObjects(this.employee, localEmp)) {
                throw new TestErrorException("The object in the UOW and the object in the Shared cache match post writeChanges.  This should not be the case");
            }
        }
    }

    public void reset() {
    }
}
