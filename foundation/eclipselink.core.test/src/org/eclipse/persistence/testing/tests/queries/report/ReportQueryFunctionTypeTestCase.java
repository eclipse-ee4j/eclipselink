/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.report;

import java.math.BigDecimal;
import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * This test assumes the following to be true of the 
 * org.eclipse.persistence.testing.models.employee.domain.Employee class:
 * 
 *  - salary is an int.
 *  - id is a BigDecimal.
 *  
 *  It also assumes that report queries in general work properly. That is, if I 
 *  asked for a report item, I'll get one back in the report query result. The 
 *  verify does not check.
 */
public class ReportQueryFunctionTypeTestCase extends AutoVerifyTestCase {
    Vector<ReportQueryResult> results;
    
    public ReportQueryFunctionTypeTestCase() {
        setDescription("Tests the result types of report query that uses functions.");
    }
    
    public void test() {
        // This is currently where DataDirect is installed on DB2. If this URL
        // changes then this test will fail since DataDirect on DB2 returns
        // an Integer for the average function. In fact all the return
        // types on DataDirect for DB2 are not what is expected in the verify.
        // Every other platform seems to adhere to these types though so aside
        // from the Oracle specific variance function these tests should pass
        // on all platforms.
        if (getSession().getLogin().getDriverClassName().equals("com.oracle.ias.jdbc.db2.DB2Driver")) {
            throw new TestWarningException("This test is not supported on DB2 DataDirect");
        } else {
            ReportQuery reportQuery = new ReportQuery();
            reportQuery.setReferenceClass(Employee.class);
            ExpressionBuilder builder = reportQuery.getExpressionBuilder(); 
        
            reportQuery .addAverage("salary-ave", builder.get("salary"));
        
            // Oracle specific function.  
            if (getSession().getDatasourcePlatform().isOracle()) {
                reportQuery .addVariance("salary-var", builder.get("salary")); 
            }
            
            //Sybase and TimesTen doesn't support
            if(!(getSession().getDatasourcePlatform().isSybase() || getSession().getDatasourcePlatform().isTimesTen() || getSession().getDatasourcePlatform().isDerby()))        
            {
              reportQuery .addStandardDeviation("salary-std", builder.get("salary")); 
            }
        
            reportQuery .addSum("id-sum", builder.get("id")); 
            reportQuery .addMinimum("id-min", builder.get("id"));
            reportQuery .addMaximum("id-max", builder.get("id")); 
        
            results = (Vector<ReportQueryResult>) getSession().executeQuery(reportQuery); 
        }
    }

    protected void verify() {
        if (results.isEmpty()) {
            throw new TestErrorException("No results were returned from the report query.");
        } else {
            ReportQueryResult result = results.firstElement();
            Object value;
            // Types are not as expected for Derby.
            if (getSession().getDatasourcePlatform().isDerby()) {
                return;
            }
        
            //To fix bug 6217517, AVG(t1.SALARY) returns an Integer value with DB2
            // The Integer also returned by SyBase (JConnect 6.05) and SQLServer 2005 (SQL Server  Version: 9.00.2050; Driver: Microsoft SQL Server 2005 JDBC Driver  Version: 1.2.2828.100) 
            value = result.get("salary-ave");
            if (value instanceof Integer && !(getSession().getDatasourcePlatform().isDB2() || getSession().getDatasourcePlatform().isSybase() || getSession().getDatasourcePlatform().isSQLServer())) {
                throw new TestErrorException("Incorrect result type for average function of report query.");
            }
        
            if (getSession().getDatasourcePlatform().isOracle()) {  
                value = result.get("salary-var");
                if (value instanceof Integer) {
                    throw new TestErrorException("Incorrect result type for variance function of report query.");
                }
            }
        
            value = result.get("salary-std");
            if (value instanceof Integer) {
                throw new TestErrorException("Incorrect result type for standard deviation function of report query.");
            }
        
            //To fix bug 6217517, SUM(t0.EMP_ID) returns an Integer value with DB2
            value = result.get("id-sum");
            if (! (value instanceof BigDecimal || (value instanceof Integer && getSession().getDatasourcePlatform().isDB2()))) {
                throw new TestErrorException("Incorrect result type for sum function of report query.");
            }
        
            //To fix bug 6217517, MIN(t0.EMP_ID) returns an Integer value with DB2; Long on MySQL
            value = result.get("id-min");
            if (! (value instanceof BigDecimal || (value instanceof Integer && getSession().getDatasourcePlatform().isDB2())
                                               || (value instanceof Long && getSession().getDatasourcePlatform().isMySQL()
                                               || (value instanceof Long && getSession().getDatasourcePlatform().isPostgreSQL())))) {
                throw new TestErrorException("Incorrect result type for min function of report query.");
            }
        
            //To fix bug 6217517, MAX(t0.EMP_ID) returns an Integer value with DB2; Long on MySQL
            value = result.get("id-max");
            if (! (value instanceof BigDecimal || (value instanceof Integer && getSession().getDatasourcePlatform().isDB2())
                                               || (value instanceof Long && getSession().getDatasourcePlatform().isMySQL()
                                               || (value instanceof Long && getSession().getDatasourcePlatform().isPostgreSQL())))) {
                throw new TestErrorException("Incorrect result type for max function of report query.");
            }
        }
    }
}
