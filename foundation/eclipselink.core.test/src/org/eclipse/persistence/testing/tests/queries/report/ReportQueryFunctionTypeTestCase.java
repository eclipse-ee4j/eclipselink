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
package org.eclipse.persistence.testing.tests.queries.report;

import java.math.BigDecimal;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
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
    // shouldHaveReadAllQueryInDescriptor == true added for Bug 290311: ReadAllQuery executed instead of ReportQuery 
    boolean shouldHaveReadAllQueryInDescriptor;
    boolean hasSetReadAllQueryIntoDescriptor;
    
    public ReportQueryFunctionTypeTestCase() {
        this(false);
    }
    public ReportQueryFunctionTypeTestCase(boolean shouldHaveReadAllQueryInDescriptor) {
        this.shouldHaveReadAllQueryInDescriptor = shouldHaveReadAllQueryInDescriptor;
        String suffix = shouldHaveReadAllQueryInDescriptor ? " ReadAllQuery in descriptor." : "";
        setDescription("Tests the result types of report query that uses functions." + suffix);
        setName(getName() + suffix);
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
            
            //Sybase, Symfoware (bug 304909) and TimesTen don't support
            if(!(getSession().getDatasourcePlatform().isSybase() || getSession().getDatasourcePlatform().isTimesTen() || getSession().getDatasourcePlatform().isDerby() || getSession().getDatasourcePlatform().isSymfoware()))        
            {
              reportQuery .addStandardDeviation("salary-std", builder.get("salary")); 
            }
        
            reportQuery .addSum("id-sum", builder.get("id")); 
            reportQuery .addMinimum("id-min", builder.get("id"));
            reportQuery .addMaximum("id-max", builder.get("id")); 
        
            if(this.shouldHaveReadAllQueryInDescriptor) {
                ClassDescriptor desc = getSession().getDescriptor(Employee.class);
                if(!desc.getQueryManager().hasReadAllQuery()) {
                    desc.getQueryManager().setReadAllQuery(new ReadAllQuery());
                    hasSetReadAllQueryIntoDescriptor = true;
                }
            }
            
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
            if (getSession().getDatasourcePlatform().isOracle()) {
                value = result.get("salary-ave");
                if (value instanceof Integer) {
                    throw new TestErrorException("Incorrect result type for average function of report query.");
                }
                value = result.get("salary-var");
                if (value instanceof Integer) {
                    throw new TestErrorException("Incorrect result type for variance function of report query.");
                }
                value = result.get("id-sum");
                if (!(value instanceof BigDecimal)) {
                    throw new TestErrorException("Incorrect result type for sum function of report query.");
                }
                value = result.get("id-min");
                if (!(value instanceof BigDecimal)) {
                    throw new TestErrorException("Incorrect result type for min function of report query.");
                }
                value = result.get("id-max");
                if (!(value instanceof BigDecimal)) {
                    throw new TestErrorException("Incorrect result type for max function of report query.");
                }
            }
        
            value = result.get("salary-std");
            if (value instanceof Integer) {
                throw new TestErrorException("Incorrect result type for standard deviation function of report query.");
            }
        }
    }
    
    public void reset() {
        if(this.hasSetReadAllQueryIntoDescriptor) {
            ClassDescriptor desc = getSession().getDescriptor(Employee.class);
            desc.getQueryManager().setReadAllQuery(null);
        }
    }
}
