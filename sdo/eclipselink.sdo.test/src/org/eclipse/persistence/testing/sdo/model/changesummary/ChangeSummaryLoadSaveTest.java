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
/**
 *  @version $Header: ChangeSummaryLoadSaveTest.java 24-jan-2007.08:33:09 dmahar Exp $
 *  @author  mfobrien
 *  @since   release specific (what release of product did this appear in)
 */
package org.eclipse.persistence.testing.sdo.model.changesummary;

import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.persistence.sdo.SDODataObject;


public class ChangeSummaryLoadSaveTest extends ChangeSummaryLoadSaveTestCases {
    protected static final String XSD_CSROOT_PATH//	
     = "org/eclipse/persistence/testing/sdo/model/changesummary/team_csroot.xsd";
    protected static final String XML_CSROOT_PATH//					
     = "org/eclipse/persistence/testing/sdo/model/changesummary/team_csroot.xml";
    protected static final String XML_CSROOT_LOGGING_ON_PATH//					
     = "org/eclipse/persistence/testing/sdo/model/changesummary/team_csroot_log_on.xml";
    protected static final String XML_CSROOT_LOGGING_OFF_PATH//					
     = "org/eclipse/persistence/testing/sdo/model/changesummary/team_csroot_log_off.xml";
    protected static final String XML_CSROOT_LOGGING_ON_DEL_PATH//					
     = "org/eclipse/persistence/testing/sdo/model/changesummary/team_csroot_log_on_del.xml";
    protected static final String XML_CSROOT_LOGGING_ON_MOD_PATH//					
     = "org/eclipse/persistence/testing/sdo/model/changesummary/team_csroot_log_on_mod.xml";
    protected static final String XML_CSROOT_LOGGING_ON_DEL_MOD_PATH//					
     = "org/eclipse/persistence/testing/sdo/model/changesummary/team_csroot_log_on_del_mod.xml";
    public static final String XML_PATH_UC1_2 = "org/eclipse/persistence/testing/sdo/model/changesummary/team_csroot_uc1-2.xml";

    protected String getControlFileName() {
        return ("org/eclipse/persistence/testing/sdo/model/changesummary/team_csroot_uc1-2.xml");
    }
    public ChangeSummaryLoadSaveTest(String name) {
        super(name);
    }
    public void setUp() {
        try {
            super.setUp();
            
            // load in the schema
            String xsdString = getXSDString(XSD_CSROOT_PATH);

            // Define Types so that processing attributes completes
            xsdHelper.define(xsdString);
            // get root data object with ChangeSummary on root
            //root = loadObjectFromXML(XML_CSROOT_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            fail("ChangeSummaryLoadSaveTest.setup() failed to load DataObject");
        }
    }

    public void testLoadSaveCSonRoot() throws IOException {
        SDODataObject aRoot = null;
        SDODataObject aRoot2 = null;
        try {
            //Project p = ((SDOXMLHelper)xmlHelper).getTopLinkProject();
            //XMLProjectWriter projectWriter = new XMLProjectWriter();
            //PrintWriter pw = new PrintWriter(System.out);
            //projectWriter.write(p, pw);
            // get root data object with ChangeSummary on root
            aRoot = loadObjectFromXML(XML_CSROOT_PATH);
            // verify that changeSummary property has been populated on the instance
            assertTrue(aRoot.getChangeSummary().isLogging());
            aRoot2 = loadObjectFromXML(XML_PATH_UC1_2);
            assertTrue(aRoot2.getChangeSummary().isLogging());
            //System.out.println("1 logging: " + aRoot.getChangeSummary().isLogging());
            //System.out.println("2 logging: " + aRoot2.getChangeSummary().isLogging());
            // temp
            //setChangeSummary(aRoot);
            //setChangeSummary(aRoot2);
            // temp: set logging
            //aRoot.getChangeSummary().beginLogging();
            //aRoot2.getChangeSummary().endLogging();            
            //aRoot2.getChangeSummary().beginLogging();
            //aRoot.getChangeSummary().endLogging();
            //aRoot2.getChangeSummary().endLogging();
            // perform mod
            ((SDODataObject)aRoot2.get("manager")).delete();
            // save objects
            //System.out.println("1 logging: " + aRoot.getChangeSummary().isLogging());
            //System.out.println("2 logging: " + aRoot2.getChangeSummary().isLogging());
            xmlHelper.save(aRoot, null, "root", System.out);
            xmlHelper.save(aRoot2, null, "root", System.out);

        } catch (Exception e) {
            e.printStackTrace();
            fail("ChangeSummaryLoadSaveTest Exception: " + e.getMessage());
        }
    }

    public void testLoadSaveCSonRootLoggingOff() throws IOException {
        SDODataObject aRoot = null;

        // get root data object with ChangeSummary on root
        aRoot = loadObjectFromXML(XML_CSROOT_LOGGING_OFF_PATH);
        assertNotNull(aRoot.getChangeSummary());
        assertFalse(aRoot.getChangeSummary().isLogging());
        // perform mod
        SDODataObject aManager = (SDODataObject)aRoot.get("manager");
        assertNotNull(aManager);
        aManager.set("name", "Jane Doe2");
        // save objects
        serialize(aRoot, "object.xml");

        //xmlHelper.save(aRoot, null, "root", System.out);
        // compare
    }

    /*
        public void testUC1_2LoadRootOf3LevelWithChangeOnRootLoggingOnBeforeSubChildDelete() throws IOException {
            //
            setChangeSummary(root);
            // create/add new employee
            SDODataObject employee = (SDODataObject)root.createDataObject("manager");
            employee.set("name", "Jane Doe2");

            // create/add address
            SDODataObject address = (SDODataObject)employee.createDataObject("address");
            address.set("street", "123 A Street");

            // start logging
            root.getChangeSummary().beginLogging();
            assertTrue(root.getChangeSummary().isLogging());
            xmlHelper.save(root, null, "root", System.out);
            // delete address
            address.delete();

            // marshal object to xml
            //StringWriter writer = new StringWriter();
            //xmlHelper.save(root, writer, null);

            xmlHelper.save(root, null, "root", System.out);
            // unmarshal xml to object
            //SDODataObject objectFromXML = loadObjectFromXML(XML_PATH_UC1_2);
            SDODataObject objectFromXML = loadObjectFromXML(XML_CSROOT_PATH);
            //assertTrue(objectFromXML.getChangeSummary().isLogging());
            objectFromXML.getChangeSummary().beginLogging();
            assertTrue(objectFromXML.getChangeSummary().isLogging());

            // end logging
            //objectFromXML.getChangeSummary()
            xmlHelper.save(objectFromXML, null, "root", System.out);
            //compareXML(getControlString(getControlFileName()), writer.toString());

            // cs field is null, cs prop is filled but rootDataObject is null
            //xmlHelper.save(objectFromXML, null, "root", System.out);
            // logging should be on and the changeSummary should be populated
            assertNotNull(objectFromXML.getChangeSummary());
            assertTrue(objectFromXML.getChangeSummary().isLogging());
            // the original and deserialized objects should be the same
            //assertTrue(equalityHelper.equal(root, objectFromXML));
        }
    */
    protected String getControlString(String fileName) {
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            fail("An error occurred loading the control document");
            e.printStackTrace();
            return null;
        }
    }
}
