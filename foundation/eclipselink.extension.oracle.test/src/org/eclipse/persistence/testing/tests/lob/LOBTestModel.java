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
package org.eclipse.persistence.testing.tests.lob;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.framework.TestVariation;
import org.eclipse.persistence.testing.framework.TestWarningException;

/**
 * The test model is used to test the TopLink BLOB/CLOB support.
 * Object Model: Image [id(int), script(String), picture(byte[]), audio(byte[]), commentary(char[])]
 * Data Schame: IMAGE [id(NUMBER), SCRIPT(CLOB), PICTURE(BLOB)]
 *              CLIP [id(NUMBER), AUDIO(BLOB), COMMENTARY(CLOB)]
 * Descriptor: Image --> (IMAGE, CLIP). i.e. multiple table.
 * Mappings:  id -> ID (direct-to-field mapping).
 *            script -> SCRIPT (TypeConversionMapping, 'converted type': java.sql.Clob).
 *            picture -> PICTURE (TypeConversionMapping, 'converted type': java.sql.Blob).
 *            audio -> AUDIO (TypeConversionMapping, 'converted type': java.sql.Blob).
 *            Commentary -> COMMENTARY (TypeConversionMapping, 'converted type': java.sql.Clob).
 *
 * NOTE: The main purpose of this test model is to test Oracle Thin driver 4k limits. TopLink overally supports
 * BLOB/CLOB well with the proper JDBC drivers. One exception is Oracle thin driver, which has had a well known 4k limits
 * for LOB INSERT/UPDATE since 8i. (OTN bug number 1273826).
 * TopLink addresss this issue on 5.0, and with the features of: (for Oracle thin driver ONLY)
 * 1. Support on database session, client/server session and theoretically session broker.
 * 2. Support multiple tables.
 * 3. Support nested unite of work.
 * 4. Minimal Locator SELECT statement.
 * 5. Client side locator settings (limits size, turn on/off the locator usage) configurable through DatabaseLogin.
 * 6. Trivial overhead (two if(true) condition calls) for non Oracle thin driver usage. Easy to turn it off if the limits bug is addressed by the driver team later.
 * 7. TypeConversionMapping is now recommended for the LOB mapping and the converted type are java.sql.Clob and java.sql.Blob respectively.
 * 8. Support localization.
 *
 * @author King Wang (Aug. 2002)
 * @since TopLink/Java 5.0
 */
// This is Oracle8 specific test model.
// It won't run on any platform other than Oracle.
// Moreover, this model is Oracle8 specific.
// In setup each test verifies if the current platform is an instance of 
// Oracle9Platform, and will attempt to usePlatform(Oracle8Platform) if not
// (reset returns back the originally used DatabasePlatform).
public class LOBTestModel extends TestModel {
    String str = "shouldBindAllParameters";

    public LOBTestModel() {
        setDescription("This suite tests TopLink LOB support with Oracle thin driver.");
    }

    public void addRequiredSystems() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("WARNING: This model is not supposed to be run on databases other than Oracle.");
        }
        addRequiredSystem(new LOBImageModelSystem());
    }

    public void addTests() {
        addTest(getLOBInsertTestSuite());
        addTest(getLOBUpdateTestSuite());
        addTest(getLOBDeleteTestSuite());
        boolean usesOCI = false;
        try {
            ((AbstractSession)getSession()).getAccessor().incrementCallCount((AbstractSession)getSession());
            java.sql.DatabaseMetaData meta =((AbstractSession)getSession()).getAccessor().getConnection().getMetaData();
            ((AbstractSession)getSession()).getAccessor().decrementCallCount();
            String url = meta.getURL();
            usesOCI = url.startsWith("jdbc:oracle:oci");
        } catch (java.sql.SQLException sqlException) {
        }
        if (usesOCI) {
            addTest(getLOBOCITestSuite());
        }
    }

    protected TestSuite getLOBInsertTestSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("LOB INSERT Test Suite");
        Object obj = getSession().getPlatform();
        suite.addTests(TestVariation.get(obj, str, new LOBTestWrapper(new LOBInsertTest(ImageSimulator.generateImageNullLOB()))));
        suite.addTests(TestVariation.get(obj, str, new LOBTestWrapper(new LOBInsertTest(ImageSimulator.generateImage(1000, 800)))));
        String str2 = "usesBatchWriting " + str;
        suite.addTests(TestVariation.get(obj, str2, new LOBTestWrapper(new LOBInsertTest(ImageSimulator.generateImage(100000, 80000)))));
        return suite;
    }

    protected TestSuite getLOBUpdateTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("LOB UPDATE Test Suite");
        Object obj = getSession().getPlatform();
        suite.addTests(TestVariation.get(obj, str, new LOBTestWrapper(new LOBUpdateTest(ImageSimulator.generateImage(100, 100), 500))));
        String str2 = "usesBatchWriting " + str;
        suite.addTests(TestVariation.get(obj, str2, new LOBTestWrapper(new LOBUpdateTest(ImageSimulator.generateImage(100, 100), 120000))));
        return suite;
    }

    protected TestSuite getLOBDeleteTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("LOB DELETE Test Suite");
        Object obj = getSession().getPlatform();
        suite.addTests(TestVariation.get(obj, str, new LOBTestWrapper(new LOBDeleteTest(ImageSimulator.generateImage(2800, 900)))));
        suite.addTests(TestVariation.get(obj, str, new LOBTestWrapper(new LOBDeleteTest(ImageSimulator.generateImage(280000, 90000)))));
        return suite;
    }

    protected TestSuite getLOBOCITestSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("LOB OCI Test Suite");
        Object obj = getSession().getPlatform();
        suite.addTests(TestVariation.get(obj, str, new LOBTestWrapper(new LOBInsertTest(ImageSimulator.generateImage(240000, 120000)), false)));
        return suite;
    }
}
