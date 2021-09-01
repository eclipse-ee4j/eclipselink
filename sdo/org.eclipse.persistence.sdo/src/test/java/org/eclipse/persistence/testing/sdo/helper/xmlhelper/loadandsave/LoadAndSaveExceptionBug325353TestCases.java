/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

import junit.textui.TestRunner;

public class LoadAndSaveExceptionBug325353TestCases extends LoadAndSaveTestCases{

    public LoadAndSaveExceptionBug325353TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveExceptionBug325353TestCases" };
        TestRunner.main(arguments);
    }

    @Override
    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/Customer.xsd";
    }

    @Override
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleElement.xml");
    }

    @Override
    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleElementNoSchema.xml");
    }

    @Override
    protected String getControlRootURI() {
        return NON_DEFAULT_URI;
    }

    @Override
    protected String getControlRootName() {
        return "customer";
    }

    @Override
    protected String getRootInterfaceName() {
        return "CustomerType";
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    @Override
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
        return packages;
    }

    @Override
    public void registerTypes() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        //create a new Type for Customers
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");
        customerType.set("uri", getControlRootURI());
        customerType.set("name", "customer");

        // create a first name property
        addProperty(customerType, "firstName", stringType, false, false, true);

        // create a last name property
        addProperty(customerType, "lastName", stringType, false, false, true);

        // now define the Customer type so that customers can be made
        Type customerSDOType = typeHelper.define(customerType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", customerSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    @Override
    protected void verifyAfterLoad(XMLDocument document) {
        xmlHelper = new SDOHelperContext().getXMLHelper();
        assertNotNull(document);
        assertNotNull(document.getRootObject());
        assertNull(document.getRootObject().getContainer());
    }

    @Override
    public void testLoadFromAndSaveAfterDefineMultipleSchemas() throws Exception {
        try{
            super.testLoadFromAndSaveAfterDefineMultipleSchemas();
        }catch(SDOException sdoException){
            assertEquals(SDOException.DATAOBJECT_FROM_DIFFERENT_HELPERCONTEXT, sdoException.getErrorCode());
            return;
        }
        fail("An SDOException should have been thrown.");
    }

    @Override
    public void testLoadFromDomSourceWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        try{
            super.testLoadFromDomSourceWithURIAndOptionsSaveDataObjectToStreamResult();
        }catch(SDOException sdoException){
            assertEquals(SDOException.DATAOBJECT_FROM_DIFFERENT_HELPERCONTEXT, sdoException.getErrorCode());
            return;
        }
        fail("An SDOException should have been thrown.");
    }

    @Override
    public void testLoadFromFileReaderWithURIAndOptionsStreamSaveDataObjectToWriter() throws Exception {
        try{
            super.testLoadFromFileReaderWithURIAndOptionsStreamSaveDataObjectToWriter();
        }catch(SDOException sdoException){
            assertEquals(SDOException.DATAOBJECT_FROM_DIFFERENT_HELPERCONTEXT, sdoException.getErrorCode());
            return;
        }
        fail("An SDOException should have been thrown.");
    }

    @Override
    public void testLoadFromInputStreamSaveDocumentToOutputStream() throws Exception {
        try{
            super.testLoadFromInputStreamSaveDocumentToOutputStream();
        }catch(SDOException sdoException){
            assertEquals(SDOException.DATAOBJECT_FROM_DIFFERENT_HELPERCONTEXT, sdoException.getErrorCode());
            return;
        }
        fail("An SDOException should have been thrown.");
    }

    @Override
    public void testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToOutputStream() throws Exception {
        try{
            super.testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToOutputStream();
        }catch(SDOException sdoException){
            assertEquals(SDOException.DATAOBJECT_FROM_DIFFERENT_HELPERCONTEXT, sdoException.getErrorCode());
            return;
        }
        fail("An SDOException should have been thrown.");
    }

    @Override
    public void testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        try{
            super.testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToStreamResult();
        }catch(SDOException sdoException){
            assertEquals(SDOException.DATAOBJECT_FROM_DIFFERENT_HELPERCONTEXT, sdoException.getErrorCode());
            return;
        }
        fail("An SDOException should have been thrown.");
    }

    @Override
    public void testLoadFromSAXSourceWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        try{
            super.testLoadFromSAXSourceWithURIAndOptionsSaveDataObjectToStreamResult();
        }catch(SDOException sdoException){
            assertEquals(SDOException.DATAOBJECT_FROM_DIFFERENT_HELPERCONTEXT, sdoException.getErrorCode());
            return;
        }
        fail("An SDOException should have been thrown.");
    }

    @Override
    public void testLoadFromStreamSourceWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        try{
            super.testLoadFromStreamSourceWithURIAndOptionsSaveDataObjectToStreamResult();
        }catch(SDOException sdoException){
            assertEquals(SDOException.DATAOBJECT_FROM_DIFFERENT_HELPERCONTEXT, sdoException.getErrorCode());
            return;
        }
        fail("An SDOException should have been thrown.");
    }

    @Override
    public void testLoadFromStringSaveDocumentToWriter() throws Exception {
        try{
            super.testLoadFromStringSaveDocumentToWriter();
        }catch(SDOException sdoException){
            assertEquals(SDOException.DATAOBJECT_FROM_DIFFERENT_HELPERCONTEXT, sdoException.getErrorCode());
            return;
        }
        fail("An SDOException should have been thrown.");
    }

    @Override
    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() throws Exception {
        try{
            super.testNoSchemaLoadFromInputStreamSaveDataObjectToString();
        }catch(SDOException sdoException){
            assertEquals(SDOException.DATAOBJECT_FROM_DIFFERENT_HELPERCONTEXT, sdoException.getErrorCode());
            return;
        }
        fail("An SDOException should have been thrown.");
    }

    @Override
    public void testClassGenerationLoadAndSave() throws Exception {
        try{
            super.testClassGenerationLoadAndSave();
        }catch(SDOException sdoException){
            assertEquals(SDOException.DATAOBJECT_FROM_DIFFERENT_HELPERCONTEXT, sdoException.getErrorCode());
            return;
        }
        fail("An SDOException should have been thrown.");
    }

}
