/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.types;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Vector;

/**
 * This test case will create a table consisting of a column to map to each of the JAVA data types.
 */
public class NumericTester extends TypeTester {
    public int intValue;
    public Integer integerClassValue;
    public float floatValue;
    public Float floatClassValue;
    public long longValue;
    public Long longClassValue;
    public double doubleValue;
    public Double doubleClassValue;
    public short shortValue;
    public Short shortClassValue;
    public byte byteValue;
    public Byte byteClassValue;
    public BigInteger bigIntegerValue;
    public BigDecimal bigDecimalValue;

    public NumericTester() {
        super("ZERO");
        intValue = 0;
        integerClassValue = 0;
        floatValue = 0;
        floatClassValue = (float) 0;
        longValue = 0;
        longClassValue = 0L;
        doubleValue = 0;
        doubleClassValue = (double) 0;
        shortValue = (short)0;
        shortClassValue = (short) 0;
        byteValue = (byte)0;
        byteClassValue = (byte) 0;
        bigIntegerValue = new BigInteger("0");
        bigDecimalValue = new BigDecimal(bigIntegerValue, 19);
    }

    public NumericTester(String name) {
        super(name);
    }

    private static void addBigDecimalField(TableDefinition definition, DatabasePlatform platform) {
        FieldDefinition.DatabaseType fieldDef = platform.getDatabaseTypes().get(BigDecimal.class);

        if (fieldDef.allowSize()) {
            int scale = fieldDef.maxPrecision() / 2;
            definition.addField("BIGDECF", Float.class, fieldDef.maxPrecision(), scale);
        } else {
            definition.addField("BIGDECF", Float.class);
        }
    }

    /**
     * Assumes that if size is allowed that the sum of the scale and the precision
     * must be less than or equal to the maximum precision.
     *    If building sizes it splits the number (ie. 38 -{@literal >} 19,19 or 19 -{@literal >} 10, 9)
     */
    private static void addDoubleField(TableDefinition definition, DatabasePlatform platform) {
        FieldDefinition.DatabaseType fieldDef = platform.getDatabaseTypes().get(Float.class);

        if (fieldDef.allowSize()) {
            int scale = fieldDef.maxPrecision() / 2;
            definition.addField("DOUBLEF", Float.class, fieldDef.maxPrecision(), scale);
            definition.addField("DOUBLEFC", Float.class, fieldDef.maxPrecision(), scale);
        } else {
            definition.addField("DOUBLEF", Float.class);
            definition.addField("DOUBLEFC", Float.class);
        }
    }

    /**
     * Assumes that if size is allowed that the sum of the scale and the precision
     * must be less than or equal to the maximum precision.
     *    If building sizes it splits the number (ie. 38 -{@literal >} 19,19 or 19 -{@literal >} 10, 9)
     */
    private static void addFloatField(TableDefinition definition, DatabasePlatform platform) {
        FieldDefinition.DatabaseType fieldDef = platform.getDatabaseTypes().get(Float.class);

        if (fieldDef.allowSize()) {
            int scale = fieldDef.maxPrecision() / 2;
            definition.addField("FLOATF", Float.class, fieldDef.maxPrecision(), scale);
            definition.addField("FLOATFC", Float.class, fieldDef.maxPrecision(), scale);
        } else {
            definition.addField("FLOATF", Float.class);
            definition.addField("FLOATFC", Float.class);
        }
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(NumericTester.class);
        descriptor.setTableName("NUMBERS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        descriptor.addDirectMapping("intValue", "INTF");
        descriptor.addDirectMapping("integerClassValue", "INTFC");
        descriptor.addDirectMapping("floatValue", "FLOATF");
        descriptor.addDirectMapping("floatClassValue", "FLOATFC");
        descriptor.addDirectMapping("longValue", "LONGF");
        descriptor.addDirectMapping("longClassValue", "LONGFC");
        descriptor.addDirectMapping("doubleValue", "DOUBLEF");
        descriptor.addDirectMapping("doubleClassValue", "DOUBLEFC");
        descriptor.addDirectMapping("shortValue", "SHORTF");
        descriptor.addDirectMapping("shortClassValue", "SHORTFC");
        descriptor.addDirectMapping("byteValue", "BYTEF");
        descriptor.addDirectMapping("byteClassValue", "BYTEFC");
        descriptor.addDirectMapping("bigIntegerValue", "BIGINTF");
        descriptor.addDirectMapping("bigDecimalValue", "BIGDECF");

        return descriptor;
    }

    public static RelationalDescriptor descriptorWithAccessors() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(NumericTester.class);
        descriptor.setTableName("NUMBERS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        descriptor.addDirectMapping("intValue", "getIntegerValue", "setIntegerValue", "INTF");
        descriptor.addDirectMapping("integerClassValue", "getIntegerClassValue", "setIntegerClassValue", "INTFC");
        descriptor.addDirectMapping("floatValue", "getFloatValue", "setFloatValue", "FLOATF");
        descriptor.addDirectMapping("floatClassValue", "getFloatClassValue", "setFloatClassValue", "FLOATFC");
        descriptor.addDirectMapping("longValue", "getLongValue", "setLongValue", "LONGF");
        descriptor.addDirectMapping("longClassValue", "getLongClassValue", "setLongClassValue", "LONGFC");
        descriptor.addDirectMapping("doubleValue", "getDoubleValue", "setDoubleValue", "DOUBLEF");
        descriptor.addDirectMapping("doubleClassValue", "getDoubleClassValue", "setDoubleClassValue", "DOUBLEFC");
        descriptor.addDirectMapping("shortValue", "getShortValue", "setShortValue", "SHORTF");
        descriptor.addDirectMapping("shortClassValue", "getShortClassValue", "setShortClassValue", "SHORTFC");
        descriptor.addDirectMapping("byteValue", "getByteValue", "setByteValue", "BYTEF");
        descriptor.addDirectMapping("byteClassValue", "getByteClassValue", "setByteClassValue", "BYTEFC");
        descriptor.addDirectMapping("bigIntegerValue", "getBigIntegerValue", "setBigIntegerValue", "BIGINTF");
        descriptor.addDirectMapping("bigDecimalValue", "getBigDecimalValue", "setBigDecimalValue", "BIGDECF");
        return descriptor;
    }

    public BigDecimal getBigDecimalValue() {
        return bigDecimalValue;
    }

    public BigInteger getBigIntegerValue() {
        return bigIntegerValue;
    }

    public Byte getByteClassValue() {
        return byteClassValue;
    }

    public byte getByteValue() {
        return byteValue;
    }

    public Double getDoubleClassValue() {
        return doubleClassValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public Float getFloatClassValue() {
        return floatClassValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public Integer getIntegerClassValue() {
        return integerClassValue;
    }

    public int getIntegerValue() {
        return intValue;
    }

    public Long getLongClassValue() {
        return longClassValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public Short getShortClassValue() {
        return shortClassValue;
    }

    public short getShortValue() {
        return shortValue;
    }

    protected static NumericTester maximumValues(DatabasePlatform platform) {
        NumericTester tester = new NumericTester("MAXIMUM");
        Map<Class<? extends Number>, ? super Number> maximums = platform.maximumNumericValues();

        tester.setIntegerValue((Integer) maximums.get(Integer.class));
        tester.setIntegerClassValue((Integer)maximums.get(Integer.class));
        tester.setFloatValue((Float) maximums.get(Float.class));
        tester.setFloatClassValue((Float)maximums.get(Float.class));
        tester.setLongValue((Long) maximums.get(Long.class));
        tester.setLongClassValue((Long)maximums.get(Long.class));
        tester.setDoubleValue((Double) maximums.get(Double.class));
        tester.setDoubleClassValue((Double)maximums.get(Double.class));
        tester.setShortValue((Short) maximums.get(Short.class));
        tester.setShortClassValue((Short)maximums.get(Short.class));
        tester.setByteValue((Byte) maximums.get(Byte.class));
        tester.setByteClassValue((Byte)maximums.get(Byte.class));
        tester.setBigIntegerValue((BigInteger)maximums.get(BigInteger.class));
        tester.setBigDecimalValue((BigDecimal)maximums.get(BigDecimal.class));

        return tester;
    }

    protected static NumericTester minimumValues(DatabasePlatform platform) {
        NumericTester tester = new NumericTester("MINIMUM");
        Map<Class<? extends Number>, ? super Number> minimums = platform.minimumNumericValues();

        tester.setIntegerValue((Integer) minimums.get(Integer.class));
        tester.setIntegerClassValue((Integer)minimums.get(Integer.class));
        tester.setFloatValue((Float) minimums.get(Float.class));
        tester.setFloatClassValue((Float)minimums.get(Float.class));
        tester.setLongValue((Long) minimums.get(Long.class));
        tester.setLongClassValue((Long)minimums.get(Long.class));
        tester.setDoubleValue((Double) minimums.get(Double.class));
        tester.setDoubleClassValue((Double)minimums.get(Double.class));
        tester.setShortValue((Short) minimums.get(Short.class));
        tester.setShortClassValue((Short)minimums.get(Short.class));
        tester.setByteValue((Byte) minimums.get(Byte.class));
        tester.setByteClassValue((Byte)minimums.get(Byte.class));
        tester.setBigIntegerValue((BigInteger)minimums.get(BigInteger.class));
        tester.setBigDecimalValue((BigDecimal)minimums.get(BigDecimal.class));

        return tester;
    }

    public void setBigDecimalValue(BigDecimal aBigDecimal) {
        bigDecimalValue = aBigDecimal;
    }

    public void setBigIntegerValue(BigInteger aBigInteger) {
        bigIntegerValue = aBigInteger;
    }

    public void setByteClassValue(byte aByte) {
        byteClassValue = aByte;
    }

    public void setByteClassValue(Byte aByte) {
        byteClassValue = aByte;
    }

    public void setByteValue(byte aByte) {
        byteValue = aByte;
    }

    public void setDoubleClassValue(double aDouble) {
        doubleClassValue = aDouble;
    }

    public void setDoubleClassValue(Double aDouble) {
        doubleClassValue = aDouble;
    }

    public void setDoubleValue(double aDouble) {
        doubleValue = aDouble;
    }

    public void setFloatClassValue(float aFloat) {
        floatClassValue = aFloat;
    }

    public void setFloatClassValue(Float aFloat) {
        floatClassValue = aFloat;
    }

    public void setFloatValue(float newFloat) {
        floatValue = newFloat;
    }

    public void setIntegerClassValue(int anInteger) {
        integerClassValue = anInteger;
    }

    public void setIntegerClassValue(Integer anInteger) {
        integerClassValue = anInteger;
    }

    public void setIntegerValue(int anInt) {
        intValue = anInt;
    }

    public void setLongClassValue(long aLong) {
        longClassValue = aLong;
    }

    public void setLongClassValue(Long aLong) {
        longClassValue = aLong;
    }

    public void setLongValue(long aLong) {
        longValue = aLong;
    }

    public void setShortClassValue(Short aShort) {
        shortClassValue = aShort;
    }

    public void setShortClassValue(short aShort) {
        shortClassValue = aShort;
    }

    public void setShortValue(short aShort) {
        shortValue = aShort;
    }

    public static TableDefinition tableDefinition(Session session) {
        DatabasePlatform platform = session.getPlatform();
        TableDefinition definition = TypeTester.tableDefinition();

        definition.setName("NUMBERS");
        definition.addField("INTF", Integer.class);
        definition.addField("INTFC", Integer.class);
        definition.addField("LONGF", Long.class);
        definition.addField("LONGFC", Long.class);
        definition.addField("SHORTF", Short.class);
        definition.addField("SHORTFC", Short.class);
        definition.addField("BYTEF", Byte.class);
        definition.addField("BYTEFC", Byte.class);
        addFloatField(definition, platform);
        addDoubleField(definition, platform);
        definition.addField("BIGINTF", BigInteger.class);
        addBigDecimalField(definition, platform);

        return definition;
    }

    /**
     *  Returns a vector of test instances (zero case, min case, max case)
     */
    public static Vector testInstances(Session session) {
        Vector tests = new Vector(3);

        tests.add(NumericTester.zeroValues(session.getPlatform()));
        tests.add(NumericTester.minimumValues(session.getPlatform()));
        tests.add(NumericTester.maximumValues(session.getPlatform()));
        return tests;
    }

    public String toString() {
        return "NumericTester(" + getTestName() + ")";
    }

    @Override
    protected void verify(WriteTypeObjectTest testCase) throws TestException {
        if(getTestName().equals("MINIMUM") || getTestName().equals("MAXIMUM")) {
            // Bug 210153
            throw new TestWarningException("MINIMUM and MAXIMUM tests fail on several platforms due to precision loss.");
        }
        try {
            super.verify(testCase);
        } catch (TestException exception) {
            if (testCase.getSession().getLogin().isJDBCODBCBridge()) {
                throw new TestWarningException("This error occurred because driver data optimization is used " + "and JDBC-ODBC looses precision on numerics over 15 digits.");
            } else {
                throw exception;
            }
        }
    }

    protected static NumericTester zeroValues(DatabasePlatform platform) {
        NumericTester tester = new NumericTester("ZERO");

        tester.setIntegerValue(0);
        tester.setIntegerClassValue(Integer.valueOf(0));
        tester.setFloatValue(0);
        tester.setFloatClassValue(Float.valueOf(0));
        tester.setLongValue(0);
        tester.setLongClassValue(Long.valueOf(0));
        tester.setDoubleValue(0);
        tester.setDoubleClassValue(Double.valueOf(0));
        tester.setShortValue((short)0);
        tester.setShortClassValue(Short.valueOf((short)0));
        tester.setByteValue((byte)0);
        tester.setByteClassValue(Byte.valueOf((byte)0));
        tester.setBigIntegerValue(new BigInteger("0"));
        tester.setBigDecimalValue(new BigDecimal("0"));

        return tester;
    }
}
