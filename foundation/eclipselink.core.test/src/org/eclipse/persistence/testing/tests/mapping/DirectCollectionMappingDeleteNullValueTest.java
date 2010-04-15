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
 *     John Vandale - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.mapping.Employee;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

/* Bug 306075 - Tests that deleting an item with a null value in a DirectCollectionMapping 
 * actually removes the item from the DB table.
 */
public class DirectCollectionMappingDeleteNullValueTest extends org.eclipse.persistence.testing.framework.TestCase {
	
	final String lastName = "DCMDeleteNullValueT";
    ExpressionBuilder expb = new ExpressionBuilder();
    Expression exp = expb.get("lastName").equal(lastName); 

    public DirectCollectionMappingDeleteNullValueTest() {
        super();
    }

    protected void setup() {
    	// create and write an obj with a DirectCollectionMapping that has a null value
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employee = (Employee) uow.registerObject(new Employee());
        employee.firstName = "DCM";
        employee.lastName = lastName;
        employee.sex = "male";
        employee.getPolicies().add("somepolicy");
        employee.getPolicies().add(null);
        uow.commit();
    }

    protected void test() {
    	// remove the null value from the DirectCollectionMapping
    	UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employee = (Employee) uow.readObject(Employee.class, exp);
        employee.getPolicies().remove(null);
        uow.commit();
    }

    protected void verify() {
    	// verify the null value was removed
    	Employee employee = (Employee) getSession().readObject(Employee.class, exp);
    	getSession().refreshObject(employee);
    	
    	if (employee.getPolicies().contains(null)) {
    		throwError("Null value not deleted from DirectCollectionMapping");
    	}
    }
    
    public void reset() {
       	UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employee = (Employee) uow.readObject(Employee.class, exp);
        uow.deleteObject(employee);
        uow.commit();
    }
}
