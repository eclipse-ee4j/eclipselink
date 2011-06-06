/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - 2.3 - Initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.typehelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.textui.TestRunner;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

public class SDOTypeHelperAppInfoTestCases extends junit.framework.TestCase  {
	private boolean customContext;
    protected DocumentBuilderFactory dbf;
    protected Document doc;
    protected HelperContext aHelperContext;
    protected XSDHelper xsdHelper;
    protected DataFactory dataFactory;
    protected TypeHelper typeHelper;
    protected Element aiElement;
    protected Element aiElementProp;
    protected SDOType sdoTypeType;
	private static final String CUSTOM_CTX = "customContext";
	private static final String APP_INFO = "xsd:appinfo";
	private static final String NAME = "name";
	private static final String URI = "uri";
	private static final String TYPE = "type";
    private static final String ATT = "attribute";
    private static final String TYPENO = "Typeno";
    private static final String PROPNO = "Propno";
    private static final String XMLNS = "xmlns";
    private static final String KEY = "key";
	private static final String PROPERTY = "property"; 
    private static final String MYDO = "myDO";
    private static final String MYPROP = "myProperty";
    private static final String MYURI = "myUri";
    private static final String TYPE_APP_INFO = "http://xmlns.oracle.com/adf/svc/metadata/type";
    private static final String PROPERTY_APP_INFO = "http://xmlns.oracle.com/adf/svc/metadata/property";
    private static final String TYPE_APP_INFO_STRING = "<xsd:appinfo source=\"http://xmlns.oracle.com/adf/svc/metadata/type\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><key xmlns=\"http://xmlns.oracle.com/adf/svc/metadata/type\"><attribute>Typeno</attribute></key></xsd:appinfo>";
    private static final String PROPERTY_APP_INFO_STRING = "<xsd:appinfo source=\"http://xmlns.oracle.com/adf/svc/metadata/property\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><key xmlns=\"http://xmlns.oracle.com/adf/svc/metadata/property\"><attribute>Propno</attribute></key></xsd:appinfo>";
	
    public SDOTypeHelperAppInfoTestCases(String name) {
        super(name);
        customContext = Boolean.getBoolean(CUSTOM_CTX);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.SDOTypeHelperAppInfoTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        HelperContext aHelperContext;
    	if (customContext) {
            // default to instance of a HelperContext
            aHelperContext = new SDOHelperContext();
        } else {
            // default to static context (Global)
            aHelperContext = HelperProvider.getDefaultContext();
        }
    	typeHelper = aHelperContext.getTypeHelper();
        dbf = DocumentBuilderFactory.newInstance();
        try {
            doc = dbf.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            fail("setUp failed: \n" + e.getMessage());
        }
        xsdHelper = aHelperContext.getXSDHelper();
        typeHelper = aHelperContext.getTypeHelper();
        dataFactory = aHelperContext.getDataFactory();
        
        // setup AppInfoElement for Type:
        // <xsd:appinfo source="http://xmlns.oracle.com/adf/svc/metadata/type">
        //   <key xmlns="http://xmlns.oracle.com/adf/svc/metadata/type">
        //     <attribute>Typeno</attribute>
        //   </key>
        // </xsd:appinfo>
        Element attElement = doc.createElement(ATT);
        attElement.appendChild(doc.createTextNode(TYPENO));
        Element keyElement = doc.createElement(KEY);
        keyElement.setAttribute(XMLNS, TYPE_APP_INFO);
        keyElement.appendChild(attElement);
        aiElement = doc.createElementNS(XMLConstants.SCHEMA_URL, APP_INFO);
        aiElement.setAttribute(SDOConstants.APPINFO_SOURCE_ATTRIBUTE, TYPE_APP_INFO);
        aiElement.appendChild(keyElement);
        
        // setup AppInfoElement for Property:
        // <xsd:appinfo source="http://xmlns.oracle.com/adf/svc/metadata/property">
        //   <key xmlns="http://xmlns.oracle.com/adf/svc/metadata/property">
        //     <attribute>Propno</attribute>
        //   </key>
        // </xsd:appinfo>
        Element attElementProp = doc.createElement(ATT);
        attElementProp.appendChild(doc.createTextNode(PROPNO));
        Element keyElementProp = doc.createElement(KEY);
        keyElementProp.setAttribute(XMLNS, PROPERTY_APP_INFO);
        keyElementProp.appendChild(attElementProp);
        aiElementProp = doc.createElementNS(XMLConstants.SCHEMA_URL, APP_INFO);
        aiElementProp.setAttribute(SDOConstants.APPINFO_SOURCE_ATTRIBUTE, PROPERTY_APP_INFO);
        aiElementProp.appendChild(keyElementProp);
        
        sdoTypeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
    }

    /**
     * Tests setting appinfo elements on a Type and Property via 
     * dataObject.set(Property, Object) API.
     * 
     * Positive test.
     */
    public void testAppInfoElements() {
        List<Element> aieList = new ArrayList<Element>();
        aieList.add(aiElement);
        List<Element> aiePropList = new ArrayList<Element>();
        aiePropList.add(aiElementProp);

        // create the data object
        SDODataObject dataObject = (SDODataObject) dataFactory.create(sdoTypeType);
        dataObject.set(NAME, MYDO);
        dataObject.set(URI, MYURI);
        dataObject.set(SDOConstants.APPINFO_PROPERTY, aieList);
        
        // create a property
        SDODataObject prop = (SDODataObject) dataObject.createDataObject(PROPERTY);
        prop.set(NAME, MYPROP);
        prop.set(TYPE, SDOConstants.SDO_STRING);
        prop.set(SDOConstants.APPINFO_PROPERTY, aiePropList);
        
        // create the type
        SDOType newType = (SDOType) typeHelper.define(dataObject);
        
        //======
        //DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(new HashMap<QName, String>());
        //List<Object> types = new ArrayList<Object>();
        //types.add(newType);
        //String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        //System.out.println(generatedSchema);
        //======
        
        // validate that AppInfoElements were handled correctly on Type and Property
        // Type
        assertNotNull("Type [newType] is null", newType);
        assertTrue("Type does not have AppInfoElements set as expected", newType.getAppInfoElements() != null);
        assertTrue("Expected AppInfoElements list of size [1] on Type, but was [" + newType.getAppInfoElements().size() + "]", newType.getAppInfoElements().size() == 1);
        String val = ((Element)newType.getAppInfoElements().get(0)).getAttribute(SDOConstants.APPINFO_SOURCE_ATTRIBUTE);
        assertNotNull("AppInfoElement source on Type is empty/null", val != null && val.length() > 0);
        assertTrue("Expected AppInfoElement source ["+TYPE_APP_INFO+"] on Type but was [" + val + "]", val.equals(TYPE_APP_INFO));
        String appInfoString = xsdHelper.getAppinfo(newType, TYPE_APP_INFO);
        assertEquals("Expected getAppInfo() to return ["+TYPE_APP_INFO_STRING+"] but was ["+appInfoString+"]", TYPE_APP_INFO_STRING, appInfoString);
        // Property
        SDOProperty myProp = newType.getProperty(MYPROP);
        assertNotNull("Property [myProperty] is null", myProp);
        assertTrue("Property does not have AppInfoElements set as expected", myProp.getAppInfoElements() != null);
        assertTrue("Expected AppInfoElements list of size [1] on Property, but was [" + myProp.getAppInfoElements().size() + "]", myProp.getAppInfoElements().size() == 1);
        val = ((Element)myProp.getAppInfoElements().get(0)).getAttribute(SDOConstants.APPINFO_SOURCE_ATTRIBUTE);
        assertNotNull("AppInfoElement source on Property is empty/null", val != null && val.length() > 0);
        assertTrue("Expected AppInfoElement source ["+PROPERTY_APP_INFO+"] on Property but was [" + val + "]", val.equals(PROPERTY_APP_INFO));
        appInfoString = xsdHelper.getAppinfo(myProp, PROPERTY_APP_INFO);
        assertEquals("Expected getAppInfo() to return ["+PROPERTY_APP_INFO_STRING+"] but was ["+appInfoString+"]", PROPERTY_APP_INFO_STRING, appInfoString);
    }
    
    /**
     * Test error handling by setting a single Element as opposed to the
     * expected List<Element>.
     * 
     * Negative test.
     */
    public void testSingleAppInfoElement() {
        // create the data object
        SDODataObject dataObject = (SDODataObject) dataFactory.create(sdoTypeType);
        dataObject.set(NAME, MYDO);
        dataObject.set(URI, MYURI);
        
        // the following should cause an IllegalArgumentException
        try {
            dataObject.set(SDOConstants.APPINFO_PROPERTY, aiElement);
        } catch (IllegalArgumentException iaex) {
            // success
        } catch (Exception x) {
            fail("An unexpected exception occurred: \n" + x.getMessage());
        }
    }
}