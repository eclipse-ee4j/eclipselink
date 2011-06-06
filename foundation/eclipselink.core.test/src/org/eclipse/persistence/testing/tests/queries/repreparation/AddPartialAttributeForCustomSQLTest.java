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
 package org.eclipse.persistence.testing.tests.queries.repreparation;

import java.util.Vector;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;

@SuppressWarnings("deprecation")
public class AddPartialAttributeForCustomSQLTest extends TestCase {
    static String lastName = "AddPartialAttributeForCustomSQLTest";
    
    public AddPartialAttributeForCustomSQLTest() {
        setDescription("Test PartialAttrubute function on custom SQL for ReadAllQuery, ReadObjectQuery");
    }
    
    public void setup(){
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

    }
    
    public void test() {
        try{
            Employee empOne = (Employee)getSession().executeQuery(createReadObjectQueryOne());
            Employee empTwo = (Employee)getSession().executeQuery(createReadObjectQueryTwo());
            Vector  empsOne = (Vector)getSession().executeQuery(createReadAllQueryOne());
            Vector  empsTwo = (Vector)getSession().executeQuery(createReadAllQueryTwo());
            Vector  emps = (Vector)getSession().executeQuery(createReportQuery());
        }catch(Exception e){
            throw new TestErrorException("AddPartialAttributeForCustomSQLTest failed.");
        }
    }
    

    protected ReadObjectQuery createReadObjectQueryOne() {
        ReadObjectQuery query = new  ReadObjectQuery(Employee.class, new org.eclipse.persistence.queries.SQLCall("SELECT t1.EMP_ID, t1.L_NAME, t1.F_NAME, t0.CITY FROM ADDRESS t0, SALARY t2, EMPLOYEE t1 WHERE ((t2.EMP_ID = t1.EMP_ID) AND (t0.ADDRESS_ID = t1.ADDR_ID))"));
        query.dontMaintainCache ();
        query.addPartialAttribute("lastName");
        query.addPartialAttribute("firstName");
        query.addPartialAttribute(query.getExpressionBuilder().get("address").get("city"));
        return query;
    }
    
    protected ReadObjectQuery createReadObjectQueryTwo() {
        ReadObjectQuery query = new  ReadObjectQuery(Employee.class, new org.eclipse.persistence.queries.SQLCall("SELECT t1.EMP_ID, t1.L_NAME, t1.F_NAME, t0.ADDRESS_ID, t0.P_CODE, t0.COUNTRY, t0.PROVINCE, t0.CITY, t0.STREET FROM ADDRESS t0, SALARY t2, EMPLOYEE t1 WHERE ((t2.EMP_ID = t1.EMP_ID) AND (t0.ADDRESS_ID = t1.ADDR_ID))"));
        query.dontMaintainCache ();
        query.addPartialAttribute("lastName");
        query.addPartialAttribute("firstName");
        query.addPartialAttribute(query.getExpressionBuilder().get("address"));
        return query;
    }
    

    protected ReadAllQuery createReadAllQueryOne() {
        ReadAllQuery query = new  ReadAllQuery(Employee.class, new org.eclipse.persistence.queries.SQLCall("SELECT t1.EMP_ID, t1.L_NAME, t1.F_NAME, t0.CITY FROM ADDRESS t0, SALARY t2, EMPLOYEE t1 WHERE ((t2.EMP_ID = t1.EMP_ID) AND (t0.ADDRESS_ID = t1.ADDR_ID))"));
        query.dontMaintainCache ();
        query.addPartialAttribute("lastName");
        query.addPartialAttribute("firstName");
        query.addPartialAttribute(query.getExpressionBuilder().get("address").get("city"));
        return query;
    }

    protected ReadAllQuery createReadAllQueryTwo() {
        ReadAllQuery query = new  ReadAllQuery(Employee.class, new org.eclipse.persistence.queries.SQLCall("SELECT t1.EMP_ID, t1.L_NAME, t1.F_NAME, t0.ADDRESS_ID, t0.P_CODE, t0.COUNTRY, t0.PROVINCE, t0.CITY, t0.STREET FROM ADDRESS t0, SALARY t2, EMPLOYEE t1 WHERE ((t2.EMP_ID = t1.EMP_ID) AND (t0.ADDRESS_ID = t1.ADDR_ID))"));
        query.dontMaintainCache ();
        query.addPartialAttribute("lastName");
        query.addPartialAttribute("firstName");
        query.addPartialAttribute(query.getExpressionBuilder().get("address"));
        return query;
    }
    
    protected ReportQuery createReportQuery() {
        ReportQuery query = new ReportQuery();
        query.setReferenceClass(Employee.class);
        query.setCall(new SQLCall("SELECT t1.EMP_ID, t1.F_NAME, t1.L_NAME FROM ADDRESS t0, SALARY t2, EMPLOYEE t1 WHERE (((t0.PROVINCE = ?) AND (t2.EMP_ID = t1.EMP_ID)) AND (t0.ADDRESS_ID = t1.ADDR_ID))"));
        query.dontMaintainCache ();
        query.addAttribute("firstName");
        query.addAttribute("lastName");
        query.addJoinedAttribute("address");
        query.setSelectionCriteria(new ExpressionBuilder().get("address").get("province").equal("ONT"));
        return query;
    }    

}
