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
package org.eclipse.persistence.testing.tests.conversion;

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.conversion.ConversionDataObject;
import org.eclipse.persistence.testing.models.conversion.ConversionManagerSystem;

/**
 * <P>
 * <B>Purpose</B>: This test model tests the Conversion Manager.<P>
 *
 * <B>Motivation</B>: The Conversion Manager is crucial to TopLink since all conversions from
 * and to the Database are handled by it. This test case was written to ensure that all the
 * conversions that are being handled by the Conversion Manager are being done properly.<P>
 *
 * <B>Design</B>: This test model contains a domain class ConversionDataObject
 * which has one instance variable of each type that the Conversion Manager supports.
 * The test instantiates several of these objects and runs through the standard Read,
 * Write, Insert, Delete and Update tests.<P>
 *
 * <B>Responsibilities</B>: Ensure that the Conversion Manager can convert all types
 * when writing to or reading from the Database.<P>
 *
 * <B>Features Used</B>:
 * <UL>
 *     <LI>ConversionManager
 * </UL>
 *
 * <B>Paths Covered</B>: The following conversions are made:
 * <UL>
 *     <LI>Character value (both primitive and wrapper) to/from Database
 *        <LI>Integer value (both primitive and wrapper) to/from Database
 *        <LI>Float value (both primitive and wrapper) to/from Database
 *        <LI>Boolean value (both primitive and wrapper) to/from Database
 *        <LI>Long value (both primitive and wrapper) to/from Database
 *        <LI>Double value (both primitive and wrapper) to/from Database
 *        <LI>Byte value (both primitive and wrapper) to/from Database
 *        <LI>Byte array to/from Database
 *        <LI>Short value (both primitive and wrapper) to/from Database
 *        <LI><CODE>Number</CODE> value to/from Database
 *        <LI><CODE>java.math.BigDecimal</CODE> value to/from Database
 *        <LI><CODE>java.math.BigInteger</CODE> value to/from Database
 *        <LI><CODE>java.sql.Date</CODE> value to/from Database
 *        <LI><CODE>java.sql.Time</CODE> value to/from Database
 *        <LI><CODE>java.sql.Timestamp</CODE> value to/from Database
 *        <LI><CODE>java.util.Date</CODE> value to/from Database
 *        <LI><CODE>String</CODE> value to/from Database
 * </UL>
 *
 * @author Rick Barkhouse
 */
public class ConversionManagerTestModel extends TestModel {

    /**
     * ConversionManagerTestModel constructor comment.
     */
    public ConversionManagerTestModel() {
        setDescription("This suite tests all possible conversions through direct field mapping.");
    }

    public void addForcedRequiredSystems() {
        addForcedRequiredSystem(new ConversionManagerSystem());
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getConvertObjectTestSuite());
        addTest(getConvertClassTypeTestSuite());
        addTest(getClassLoaderTestSuite());
    }

    public TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConversionManagerDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the conversion manager model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(ConversionDataObject.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(ConversionDataObject.class, "example2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(ConversionDataObject.class, "example3")));

        return suite;
    }

    public TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConversionManagerInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the conversion manager model.");

	suite.addTest(new InsertObjectTest(ConversionDataObject.example1()));
	suite.addTest(new InsertObjectTest(ConversionDataObject.example2()));
	suite.addTest(new InsertObjectTest(ConversionDataObject.example3()));

        return suite;
    }

    public TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConversionManagerReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the conversion manager model.");

        suite.addTest(new ReadAllTest(ConversionDataObject.class, 3));

        return suite;
    }

    public TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConversionManagerReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the conversion manager model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(ConversionDataObject.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(ConversionDataObject.class, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(ConversionDataObject.class, "example3")));

        return suite;
    }

    public TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConversionManagerUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the conversion manager model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(ConversionDataObject.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(ConversionDataObject.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(ConversionDataObject.class, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(ConversionDataObject.class, "example3")));

        return suite;
    }

    public TestSuite getConvertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConversionManagerConvertObjectTestSuite");
        suite.setDescription("This test suite verifies object conversion.");

        // test conversions
        suite.addTest(new ConvertObjectTest(Helper.buildHexStringFromBytes(new byte[] { 4, 5, 6 }), ClassConstants.ABYTE));
        suite.addTest(new ConvertObjectTest(new char[] { 'a', 'b', 'c' }, ClassConstants.ACHAR));
        suite.addTest(new ConvertObjectTest(new java.math.BigInteger("100"), ClassConstants.BIGDECIMAL));
        suite.addTest(new ConvertObjectTest(new java.math.BigInteger("100"), ClassConstants.BIGINTEGER));
        suite.addTest(new ConvertObjectTest(new String("100"), ClassConstants.BIGINTEGER));
        suite.addTest(new ConvertObjectTest(new Integer(100), ClassConstants.BIGINTEGER));
        suite.addTest(new ConvertObjectTest(new Character('1'), ClassConstants.BOOLEAN));
        suite.addTest(new ConvertObjectTest(new Character('t'), ClassConstants.BOOLEAN));
        suite.addTest(new ConvertObjectTest(new Character('0'), ClassConstants.BOOLEAN));
        suite.addTest(new ConvertObjectTest(new Character('f'), ClassConstants.BOOLEAN));
        suite.addTest(new ConvertObjectTest(Helper.buildHexStringFromBytes(new byte[] { 4 }), ClassConstants.PBYTE));
        suite.addTest(new ConvertObjectTest(new Integer(100), ClassConstants.CHAR));
        //suite.addTest(new ConvertObjectTest(new org.eclipse.persistence.internal.helper.Date(1), ClassConstants.SQLDATE));
        suite.addTest(new ConvertObjectTest(new java.util.GregorianCalendar(), ClassConstants.SQLDATE));
        suite.addTest(new ConvertObjectTest(new Long(100), ClassConstants.SQLDATE));
        suite.addTest(new ConvertObjectTest(new Boolean("true"), ClassConstants.LONG));
        suite.addTest(new ConvertObjectTest(new Boolean("false"), ClassConstants.LONG));
        suite.addTest(new ConvertObjectTest(new String("1"), ClassConstants.NUMBER));
        suite.addTest(new ConvertObjectTest(new Boolean("true"), ClassConstants.NUMBER));
        suite.addTest(new ConvertObjectTest(new Boolean("false"), ClassConstants.NUMBER));
        suite.addTest(new ConvertObjectTest(new Integer(1), ClassConstants.SHORT));
        suite.addTest(new ConvertObjectTest(new Boolean("true"), ClassConstants.SHORT));
        suite.addTest(new ConvertObjectTest(new Boolean("false"), ClassConstants.SHORT));

        //suite.addTest(new ConvertObjectTest(new org.eclipse.persistence.internal.helper.Time(100), ClassConstants.TIME));
        suite.addTest(new ConvertObjectTest(new String("12:00:00"), ClassConstants.TIME));
        suite.addTest(new ConvertObjectTest(new java.util.Date(100), ClassConstants.TIME));
        suite.addTest(new ConvertObjectTest(new java.util.GregorianCalendar(), ClassConstants.TIME));
        suite.addTest(new ConvertObjectTest(new Long(100), ClassConstants.TIME));
        //suite.addTest(new ConvertObjectTest(new org.eclipse.persistence.internal.helper.Timestamp(100), ClassConstants.TIMESTAMP));
        suite.addTest(new ConvertObjectTest(new String("12:00:00"), ClassConstants.TIMESTAMP));
        suite.addTest(new ConvertObjectTest(new java.util.Date(100), ClassConstants.TIMESTAMP));
        suite.addTest(new ConvertObjectTest(new java.util.GregorianCalendar(), ClassConstants.TIMESTAMP));
        suite.addTest(new ConvertObjectTest(new java.util.GregorianCalendar(), ClassConstants.UTILDATE));
        suite.addTest(new ConvertObjectTest(new Long(100), ClassConstants.UTILDATE));

        // test exception handling
        suite.addTest(new ConvertObjectTest(new Character('1'), ClassConstants.BIGDECIMAL, true));
        suite.addTest(new ConvertObjectTest(new Character('1'), ClassConstants.BIGINTEGER, true));
        suite.addTest(new ConvertObjectTest(new String("a"), ClassConstants.BIGINTEGER, true));
        suite.addTest(new ConvertObjectTest(new java.sql.Date(1), ClassConstants.BOOLEAN, true));
        suite.addTest(new ConvertObjectTest(new java.sql.Date(1), ClassConstants.BYTE, true));
        suite.addTest(new ConvertObjectTest(new String("a"), ClassConstants.BYTE, true));
        suite.addTest(new ConvertObjectTest(new char[] { 'a' }, ClassConstants.APBYTE, true));
        suite.addTest(new ConvertObjectTest(new java.util.GregorianCalendar(), ClassConstants.CHAR, true));
        suite.addTest(new ConvertObjectTest(new Boolean("true"), ClassConstants.SQLDATE, true));
        suite.addTest(new ConvertObjectTest(new String("a"), ClassConstants.DOUBLE, true));
        suite.addTest(new ConvertObjectTest(new Boolean("true"), ClassConstants.DOUBLE, true));
        suite.addTest(new ConvertObjectTest(new String("a"), ClassConstants.FLOAT, true));
        suite.addTest(new ConvertObjectTest(new Boolean("true"), ClassConstants.FLOAT, true));
        suite.addTest(new ConvertObjectTest(new java.sql.Date(1), ClassConstants.INTEGER, true));
        suite.addTest(new ConvertObjectTest(new String("a"), ClassConstants.LONG, true));
        suite.addTest(new ConvertObjectTest(new java.util.GregorianCalendar(), ClassConstants.LONG, true));
        suite.addTest(new ConvertObjectTest(new String("a"), ClassConstants.NUMBER, true));
        suite.addTest(new ConvertObjectTest(new java.util.GregorianCalendar(), ClassConstants.NUMBER, true));
        suite.addTest(new ConvertObjectTest(new String("a"), ClassConstants.SHORT, true));
        suite.addTest(new ConvertObjectTest(new java.util.GregorianCalendar(), ClassConstants.SHORT, true));
        suite.addTest(new ConvertObjectTest(new Boolean("true"), ClassConstants.TIME, true));
        suite.addTest(new ConvertObjectTest(new Boolean("true"), ClassConstants.TIMESTAMP, true));
        suite.addTest(new ConvertObjectTest(new Boolean("true"), ClassConstants.UTILDATE, true));
        suite.addTest(new ConvertObjectTest(new Integer(1), ConversionManager.class, true));

        suite.addTest(new ConvertByteCharArrayToStringTest());

        return suite;
    }

    public TestSuite getConvertClassTypeTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConversionManagerConvertClassTypeTestSuite");
        suite.setDescription("This test suite verifies class type conversion.");

        suite.addTest(new ConvertClassTypeTest("int", Integer.TYPE));
        suite.addTest(new ConvertClassTypeTest("boolean", Boolean.TYPE));
        suite.addTest(new ConvertClassTypeTest("char", Character.TYPE));
        suite.addTest(new ConvertClassTypeTest("short", Short.TYPE));
        suite.addTest(new ConvertClassTypeTest("byte", Byte.TYPE));
        suite.addTest(new ConvertClassTypeTest("float", Float.TYPE));
        suite.addTest(new ConvertClassTypeTest("double", Double.TYPE));
        suite.addTest(new ConvertClassTypeTest("long", Long.TYPE));
        suite.addTest(new ConvertClassTypeTest("", null));
        suite.addTest(new ConvertClassTypeTest(char.class, Character.class));
        suite.addTest(new ConvertClassTypeTest(float.class, Float.class));
        suite.addTest(new ConvertClassTypeTest(short.class, Short.class));
        suite.addTest(new ConvertClassTypeTest(byte.class, Byte.class));
        suite.addTest(new ConvertClassTypeTest(boolean.class, Boolean.class));

        return suite;
    }

    public TestSuite getClassLoaderTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConversionManagerClassLoaderTestSuite");
        suite.setDescription("This test suite tests the getLoader method.");

        suite.addTest(new ClassLoaderTest());

        return suite;
    }
}
