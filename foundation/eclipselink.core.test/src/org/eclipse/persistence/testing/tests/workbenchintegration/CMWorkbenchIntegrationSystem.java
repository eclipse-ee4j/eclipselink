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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.models.conversion.ConversionDataObject;
import org.eclipse.persistence.testing.models.conversion.ConversionManagerSystem;

// Test some special types in expression comparison by writing to xml and 
// reading it back
public class CMWorkbenchIntegrationSystem extends ConversionManagerSystem {
    public static String PROJECT_FILE = "MWIntegrationTestCMProject";
    public ClassDescriptor cmDescriptor;

    /**
     * Override the constructor for Aggregate system to allow us to read and write XML
     */
    public CMWorkbenchIntegrationSystem() {
        super();
        
        ClassDescriptor descriptor = project.getDescriptors().get(ConversionDataObject.class);
        addNamedQueries(descriptor.getQueryManager());
        
        buildProject();
    }
    
    protected void buildProject() {
        project = WorkbenchIntegrationSystemHelper.buildProjectXML(project, PROJECT_FILE);
    }

    public void addNamedQueries(DescriptorQueryManager queryManager) {
        queryManager.addQuery("PersistenceTestEqualCalendar", buildPersistenceTestEqualCalendarQuery());
        queryManager.addQuery("PersistenceTestEqualJavaDate", buildPersistenceTestEqualJavaDateQuery());
        queryManager.addQuery("PersistenceTestEqualSqlDate", buildPersistenceTestEqualSqlDateQuery());
        queryManager.addQuery("PersistenceTestEqualTime", buildPersistenceTestEqualTimeQuery());
        queryManager.addQuery("PersistenceTestEqualTimestamp", buildPersistenceTestEqualTimestampQuery());
        queryManager.addQuery("PersistenceTestEqualBigDecimal", buildPersistenceTestEqualBigDecimalQuery());
        queryManager.addQuery("PersistenceTestEqualBigInteger", buildPersistenceTestEqualBigIntegerQuery());
        queryManager.addQuery("PersistenceTestEqualPChar", buildPersistenceTestEqualPCharQuery());
        queryManager.addQuery("PersistenceTestEqualCharacter", buildPersistenceTestEqualCharacterQuery());
        queryManager.addQuery("PersistenceTestEqualPCharArray", buildPersistenceTestEqualPCharArrayQuery());
        queryManager.addQuery("PersistenceTestEqualPByte", buildPersistenceTestEqualPByteQuery());
        queryManager.addQuery("PersistenceTestEqualPByteArray", buildPersistenceTestEqualPByteArrayQuery());
        queryManager.addQuery("PersistenceTestEqualByte", buildPersistenceTestEqualByteQuery());
    }

    public static DatabaseQuery buildPersistenceTestEqualCalendarQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        GregorianCalendar month = new GregorianCalendar();
        month.clear();
        month.set(2001, 6, 1, 11, 24, 36);
        Expression expression = builder.get("aCalendar").equal(month);
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualJavaDateQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        Date date = new Date(994007776134L);
        Expression expression = builder.get("aJavaDate").equal(date);
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualSqlDateQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        java.sql.Date date = new java.sql.Date(994007776000L);
        Expression expression = builder.get("anSQLDate").equal(date);
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualTimeQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        java.sql.Time date = new java.sql.Time(994007776134L);
        Expression expression = builder.get("aTime").equal(date);
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualTimestampQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        java.sql.Timestamp date = new java.sql.Timestamp(994007776134L);
        Expression expression = builder.get("aTimestamp").equal(date);
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualBigDecimalQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        java.math.BigDecimal num = new java.math.BigDecimal(8.0);
        Expression expression = builder.get("aBigDecimal").equal(num);
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualBigIntegerQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        java.math.BigInteger num = new java.math.BigInteger("9");
        Expression expression = builder.get("aBigInteger").equal(num);
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualPCharQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        Expression expression = builder.get("aPChar").equal('a');
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualCharacterQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        Expression expression = builder.get("aCharacter").equal(new Character('b'));
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualPCharArrayQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        Expression expression = builder.get("aPCharArray").equal(new char[] { 'd', 'e', 'f' });
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualPByteQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        Expression expression = builder.get("aPByte").equal(7);
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualPByteArrayQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        Expression expression = builder.get("aPByteArray").equal(new byte[] { 7, 8, 9 });
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEqualByteQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(ConversionDataObject.class);
        Expression expression = builder.get("aByte").equal(new Byte((byte)3));
        ReadObjectQuery query = new ReadObjectQuery(ConversionDataObject.class);
        query.setSelectionCriteria(expression);
        return query;
    }
}
