/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.query;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.testing.framework.wdf.Issue;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.BasicTypesFieldAccess;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedEnum;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedSerializable;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestSelectListTypes extends JPA1Base {

    @Override
    protected void setup() throws SQLException {
        super.setup();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            BasicTypesFieldAccess obj = new BasicTypesFieldAccess(0);
            obj.fill();
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            obj = em.find(BasicTypesFieldAccess.class, new Integer(0));
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    private void validateType(String subList, Class type) {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query = em.createQuery("select " + subList + " from BasicTypesFieldAccess b");
            Object object = query.getSingleResult();
            verify(type.isAssignableFrom(object.getClass()), "The expected class >>" + type.getName()
                    + "<< is not asignable from the result class >>" + object.getClass().getName() + "<<.");
        } finally {
            closeEntityManager(em);
        }
    }

    private void validateField(String fieldName, Class<?> type) {
        validateType("b." + fieldName, type);
    }

    private void validateCount(String fieldName) {
        validateType("count(distinct b." + fieldName + ")", Long.class);
    }

    private void validateAvg(String fieldName) {
        validateType("avg(b." + fieldName + ")", Double.class);
    }

    private void validateSum(String fieldName, Class<?> type) {
        validateType("sum(b." + fieldName + ")", type);
    }

    private void validateMax(String fieldName, Class<?> type) {
        validateType("max(b." + fieldName + ")", type);
    }

    // primitive types
    @Test
    @ToBeInvestigated
    public void testPrimitiveBoolean() {
        validateField("primitiveBoolean", Boolean.class);
    }

    @Test
    public void testPrimitiveByte() {
        validateField("primititveByte", Byte.class);
    }

    @Test
    public void testPrimitiveChar() {
        validateField("primitiveChar", Character.class);
    }

    @Test
    public void testPrimitiveShort() {
        validateField("primitiveShort", Short.class);
    }

    @Test
    public void testPrimitiveInt() {
        validateField("primitiveInt", Integer.class);
    }

    @Test
    public void testPrimitiveLong() {
        validateField("primitiveLong", Long.class);
    }

    @Test
    public void testPrimitiveFloat() {
        validateField("primitiveFloat", Float.class);
    }

    @Test
    public void testPrimitiveDouble() {
        validateField("primitiveDouble", Double.class);
    }

    // wrappers of primitive types
    @Test
    @ToBeInvestigated
    public void testWrapperBoolean() {
        validateField("wrapperBoolean", Boolean.class);
    }

    @Test
    public void testWrapperByte() {
        validateField("wrapperByte", Byte.class);
    }

    @Test
    public void testWrapperCharacter() {
        validateField("wrapperCharacter", Character.class);
    }

    @Test
    public void testWrapperShort() {
        validateField("wrapperShort", Short.class);
    }

    @Test
    public void testWrapperInteger() {
        validateField("wrapperInteger", Integer.class);
    }

    @Test
    public void testWrapperLong() {
        validateField("wrapperLong", Long.class);
    }

    @Test
    public void testWrapperDouble() {
        validateField("wrapperDouble", Double.class);
    }

    @Test
    public void testWrapperFloat() {
        validateField("wrapperFloat", Float.class);
    }

    // immutable reference types
    @Test
    public void testString2Varchar() {
        validateField("string2Varchar", String.class);
    }

    @Test
    public void testString2Clob() {
        validateField("string2Clob", String.class);
    }

    @Test
    public void testBigDecimal() {
        validateField("bigDecimal", BigDecimal.class);
    }

    @Test
    public void testBigInteger() {
        validateField("bigInteger", BigInteger.class);
    }

    // mutable types
    @Test
    public void testUtilDate() {
        validateField("utilDate", java.util.Date.class);
    }

    @Test
    public void testUtilCalendar() {
        validateField("utilCalendar", java.util.Calendar.class);
    }

    @Test
    public void testSqlDate() {
        validateField("sqlDate", java.sql.Date.class);
    }

    @Test
    public void testSqlTime() {
        validateField("sqlTime", java.sql.Time.class);
    }

    @Test
    public void testSqlTimestamp() {
        validateField("sqlTimestamp", java.sql.Timestamp.class);
    }

    // arrays
    @Test
    public void testPrimitiveByteArray2Binary() {
        validateField("primitiveByteArray2Binary", byte[].class);
    }

    @Test
    public void testPrimitiveByteArray2Longvarbinary() {
        validateField("primitiveByteArray2Longvarbinary", byte[].class);
    }

    @Test
    public void testPrimitiveByteArray2Blob() {
        validateField("primitiveByteArray2Blob", byte[].class);
    }

    @Test
    public void testPrimitiveCharArray2Varchar() {
        validateField("primitiveCharArray2Varchar", char[].class);
    }

    @Test
    public void testPrimitiveCharArray2Clob() {
        validateField("primitiveCharArray2Clob", char[].class);
    }

    @Test
    public void testWrapperByteArray2Binary() {
        validateField("wrapperByteArray2Binary", Byte[].class);
    }

    @Test
    public void testWrapperByteArray2Longvarbinary() {
        validateField("wrapperByteArray2Longvarbinary", Byte[].class);
    }

    @Test
    public void testWrapperByteArray2Blob() {
        validateField("wrapperByteArray2Blob", Byte[].class);
    }

    @Test
    public void testWrapperCharArray2Varchar() {
        validateField("wrapperCharacterArray2Varchar", Character[].class);
    }

    @SuppressWarnings("boxing")
    @Test
    public void testWrapperCharArray2Clob() {
        validateField("wrapperCharacterArray2Clob", Character[].class);
    }

    @Test
    public void testSerializable() {
        validateField("serializable", Serializable.class);
        validateField("serializable", UserDefinedSerializable.class);
    }

    @Test
    public void testEnumString() {
        validateField("enumString", Enum.class);
        validateField("enumString", UserDefinedEnum.class);
    }

    @Test
    public void testEnumOrdinal() {
        validateField("enumOrdinal", Enum.class);
        validateField("enumOrdinal", UserDefinedEnum.class);
    }

    // C O U N T * C O U N T * C O U N T * C O U N T * C O U N T * C O U N T
    // primitive types
    @Test
    public void testCountPrimitiveBoolean() {
        validateCount("primitiveBoolean");
    }

    @Test
    public void testCountPrimitiveByte() {
        validateCount("primititveByte");
    }

    @Test
    public void testCountPrimitiveChar() {
        validateCount("primitiveChar");
    }

    @Test
    public void testCountPrimitiveShort() {
        validateCount("primitiveShort");
    }

    @Test
    public void testCountPrimitiveInt() {
        validateCount("primitiveInt");
    }

    @Test
    public void testCountPrimitiveLong() {
        validateCount("primitiveLong");
    }

    @Test
    public void testCountPrimitiveFloat() {
        validateCount("primitiveFloat");
    }

    @Test
    public void testCountPrimitiveDouble() {
        validateCount("primitiveDouble");
    }

    // wrappers of primitive types
    @Test
    public void testCountWrapperBoolean() {
        validateCount("wrapperBoolean");
    }

    @Test
    public void testCountWrapperByte() {
        validateCount("wrapperByte");
    }

    @Test
    public void testCountWrapperCharacter() {
        validateCount("wrapperCharacter");
    }

    @Test
    public void testCountWrapperShort() {
        validateCount("wrapperShort");
    }

    @Test
    public void testCountWrapperInteger() {
        validateCount("wrapperInteger");
    }

    @Test
    public void testCountWrapperLong() {
        validateCount("wrapperLong");
    }

    @Test
    public void testCountWrapperDouble() {
        validateCount("wrapperDouble");
    }

    @Test
    public void testCountWrapperFloat() {
        validateCount("wrapperFloat");
    }

    // immutable reference types
    @Test
    public void testCountString2Varchar() {
        validateCount("string2Varchar");
    }

    @Test
    public void testCountBigDecimal() {
        validateCount("bigDecimal");
    }

    @Test
    public void testCountBigInteger() {
        validateCount("bigInteger");
    }

    // mutable types
    @Test
    public void testCountUtilDate() {
        validateCount("utilDate");
    }

    @Test
    public void testCountUtilCalendar() {
        validateCount("utilCalendar");
    }

    @Test
    public void testCountSqlDate() {
        validateCount("sqlDate");
    }

    @Test
    public void testCountSqlTime() {
        validateCount("sqlTime");
    }

    @Test
    public void testCountSqlTimestamp() {
        validateCount("sqlTimestamp");
    }

    // arrays
    @Test
    @Issue(issueid=13, databases=OraclePlatform.class)
    public void testCountPrimitiveByteArray2Binary() {
        validateCount("primitiveByteArray2Binary");
    }

    @Test
    @Issue(issueid=13, databases=OraclePlatform.class)
    public void testCountPrimitiveCharArray2Varchar() {
        validateCount("primitiveCharArray2Varchar");
    }

    @Test
    @Issue(issueid=13, databases=OraclePlatform.class)
    public void testCountWrapperByteArray2Binary() {
        validateCount("wrapperByteArray2Binary");
    }

    @Test
    @Issue(issueid=13, databases=OraclePlatform.class)
    public void testCountWrapperCharArray2Varchar() {
        validateCount("wrapperCharacterArray2Varchar");
    }

    @Test
    public void testCountEnumString() {
        validateCount("enumString");
    }

    @Test
    public void testCountEnumOrdinal() {
        validateCount("enumOrdinal");
    }

    // S U M * S U M * S U M * S U M * S U M * S U M * S U M * S U M * S U M * S U M
    // primitive types
    @Test
    public void testSumPrimitiveByte() {
        validateSum("primititveByte", Long.class);
    }

    @Test
    public void testSumPrimitiveShort() {
        validateSum("primitiveShort", Long.class);
    }

    @Test
    public void testSumPrimitiveInt() {
        validateSum("primitiveInt", Long.class);
    }

    @Test
    public void testSumPrimitiveLong() {
        validateSum("primitiveLong", Long.class);
    }

    @Test
    public void testSumPrimitiveFloat() {
        validateSum("primitiveFloat", Double.class);
    }

    @Test
    public void testSumPrimitiveDouble() {
        validateSum("primitiveDouble", Double.class);
    }

    // wrappers of primitive types
    @Test
    public void testSumWrapperByte() {
        validateSum("wrapperByte", Long.class);
    }

    @Test
    public void testSumWrapperShort() {
        validateSum("wrapperShort", Long.class);
    }

    @Test
    public void testSumWrapperInteger() {
        validateSum("wrapperInteger", Long.class);
    }

    @Test
    public void testSumWrapperLong() {
        validateSum("wrapperLong", Long.class);
    }

    @Test
    public void testSumWrapperDouble() {
        validateSum("wrapperDouble", Double.class);
    }

    @Test
    public void testSumWrapperFloat() {
        validateSum("wrapperFloat", Double.class);
    }

    // immutable reference types
    @Test
    public void testSumBigDecimal() {
        validateSum("bigDecimal", BigDecimal.class);
    }

    @Test
    public void testSumBigInteger() {
        validateSum("bigInteger", BigInteger.class);
    }

    // M A X * M A X * M A X * M A X * M A X * M A X * M A X * M A X * M A X
    // primitive types
    @Test
    @ToBeInvestigated
    public void testMaxPrimitiveByte() {
        validateMax("primititveByte", Byte.class);
    }

    @Test
    @ToBeInvestigated
    public void testMaxPrimitiveChar() {
        validateMax("primitiveChar", Character.class);
    }

    @Test
    public void testMaxPrimitiveShort() {
        validateMax("primitiveShort", Short.class);
    }

    @Test
    public void testMaxPrimitiveInt() {
        validateMax("primitiveInt", Integer.class);
    }

    @Test
    public void testMaxPrimitiveLong() {
        validateMax("primitiveLong", Long.class);
    }

    @Test
    public void testMaxPrimitiveFloat() {
        validateMax("primitiveFloat", Float.class);
    }

    @Test
    public void testMaxPrimitiveDouble() {
        validateMax("primitiveDouble", Double.class);
    }

    // wrappers of primitive types
    @Test
    @ToBeInvestigated
    public void testMaxWrapperByte() {
        validateMax("wrapperByte", Byte.class);
    }

    @Test
    @ToBeInvestigated
    public void testMaxWrapperCharacter() {
        validateMax("wrapperCharacter", Character.class);
    }

    @Test
    public void testMaxWrapperShort() {
        validateMax("wrapperShort", Short.class);
    }

    @Test
    public void testMaxWrapperInteger() {
        validateMax("wrapperInteger", Integer.class);
    }

    @Test
    public void testMaxWrapperLong() {
        validateMax("wrapperLong", Long.class);
    }

    @Test
    public void testMaxWrapperDouble() {
        validateMax("wrapperDouble", Double.class);
    }

    @Test
    public void testMaxWrapperFloat() {
        validateMax("wrapperFloat", Float.class);
    }

    // immutable reference types
    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.DB2_UDB_OS390 })
    public void testMaxString2Varchar() {
        validateMax("string2Varchar", String.class);
    }

    @Test
    public void testMaxBigDecimal() {
        validateMax("bigDecimal", BigDecimal.class);
    }

    @Test
    @ToBeInvestigated
    public void testMaxBigInteger() {
        validateMax("bigInteger", BigInteger.class);
    }

    // mutable types
    @Test
    public void testMaxUtilDate() {
        validateMax("utilDate", java.util.Date.class);
    }

    @Test
    @ToBeInvestigated
    public void testMaxUtilCalendar() {
        validateMax("utilCalendar", java.util.Calendar.class);
    }

    @Test
    public void testMaxSqlDate() {
        validateMax("sqlDate", java.sql.Date.class);
    }

    @Test
    public void testMaxSqlTime() {
        validateMax("sqlTime", java.sql.Time.class);
    }

    @Test
    public void testMaxSqlTimestamp() {
        validateMax("sqlTimestamp", java.sql.Timestamp.class);
    }

    // arrays
    @Test
    @Issue(issueid=13, databases=OraclePlatform.class)
    public void testMaxPrimitiveByteArray2Binary() {
        validateMax("primitiveByteArray2Binary", byte[].class);
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.DB2_UDB_OS390 })
    @ToBeInvestigated
    public void testMaxPrimitiveCharArray2Varchar() {
        validateMax("primitiveCharArray2Varchar", char[].class);
    }

    @Test
    @ToBeInvestigated
    public void testMaxWrapperByteArray2Binary() {
        validateMax("wrapperByteArray2Binary", Byte[].class);
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.DB2_UDB_OS390 })
    @ToBeInvestigated
    public void testMaxWrapperCharArray2Varchar() {
        validateMax("wrapperCharacterArray2Varchar", Character[].class);
    }

    // A V G * A V G * A V G * A V G * A V G * A V G * A V G * A V G * A V G * A V G
    // primitive types
    @Test
    public void testAvgSumPrimitiveByte() {
        validateAvg("primititveByte");
    }

    @Test
    public void testAvgSumPrimitiveShort() {
        validateAvg("primitiveShort");
    }

    @Test
    public void testAvgSumPrimitiveInt() {
        validateAvg("primitiveInt");
    }

    @Test
    public void testAvgSumPrimitiveLong() {
        validateAvg("primitiveLong");
    }

    @Test
    public void testAvgSumPrimitiveFloat() {
        validateAvg("primitiveFloat");
    }

    @Test
    public void testAvgSumPrimitiveDouble() {
        validateAvg("primitiveDouble");
    }

    // wrappers of primitive types
    @Test
    public void testAvgSumWrapperByte() {
        validateAvg("wrapperByte");
    }

    @Test
    public void testAvgSumWrapperShort() {
        validateAvg("wrapperShort");
    }

    @Test
    public void testAvgSumWrapperInteger() {
        validateAvg("wrapperInteger");
    }

    @Test
    public void testAvgSumWrapperLong() {
        validateAvg("wrapperLong");
    }

    @Test
    public void testAvgSumWrapperDouble() {
        validateAvg("wrapperDouble");
    }

    @Test
    public void testAvgSumWrapperFloat() {
        validateAvg("wrapperFloat");
    }

    // immutable reference types
    @Test
    public void testAvgSumBigDecimal() {
        validateAvg("bigDecimal");
    }

    @Test
    public void testAvgSumBigInteger() {
        validateAvg("bigInteger");
    }
}
