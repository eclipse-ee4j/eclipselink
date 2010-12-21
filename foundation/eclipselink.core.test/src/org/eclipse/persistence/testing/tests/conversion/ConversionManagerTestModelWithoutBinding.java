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
package org.eclipse.persistence.testing.tests.conversion;

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
public class ConversionManagerTestModelWithoutBinding extends TestModel {
    public boolean originalBindingState;
    public boolean originalStatementCachingState;

    /**
     * ConversionManagerTestModel constructor comment.
     */
    public ConversionManagerTestModelWithoutBinding() {
        setDescription("This suite tests all possible conversions through direct field mapping.");
    }

    public void addForcedRequiredSystems() {
        this.originalBindingState = this.getSession().getLogin().getPlatform().shouldBindAllParameters();
        this.originalStatementCachingState = this.getSession().getLogin().getPlatform().shouldCacheAllStatements();
        this.getSession().getLogin().getPlatform().setShouldBindAllParameters(false);
        this.getSession().getLogin().getPlatform().setShouldCacheAllStatements(false);
        
        addForcedRequiredSystem(new ConversionManagerSystem());
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
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

    public void reset() {
        this.getSession().getLogin().getPlatform().setShouldCacheAllStatements(this.originalStatementCachingState);
        this.getSession().getLogin().getPlatform().setShouldBindAllParameters(this.originalBindingState);
    }
}
