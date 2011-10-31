/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - October 24, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.objecttabletype;

//javase imports
import java.io.StringReader;
import org.w3c.dom.Document;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

import org.eclipse.persistence.tools.dbws.DBWSBuilder;

//test imports
import dbws.testing.TestHelper;

/**
 * Tests Oracle Object Table types. 
 *
 */
public class ObjectTableTypeTestSuite extends TestHelper {
    
    @BeforeClass
    public static void setUp() throws WSDLException, SecurityException, NoSuchFieldException,
        IllegalArgumentException, IllegalAccessException {
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">ObjectTableTypeTests</property>" +
                  "<property name=\"logLevel\">off</property>" +
                  "<property name=\"username\">";
          DBWS_BUILDER_XML_PASSWORD =
                  "</property><property name=\"password\">";
          DBWS_BUILDER_XML_URL =
                  "</property><property name=\"url\">";
          DBWS_BUILDER_XML_DRIVER =
                  "</property><property name=\"driver\">";
          DBWS_BUILDER_XML_PLATFORM =
                  "</property><property name=\"platformClassname\">";
          DBWS_BUILDER_XML_MAIN =
                  "</property>" +
              "</properties>" +
              "<procedure " +
                  "name=\"GetPersonTypeTable\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_PERSONTYPE_TABLE\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"persontype_tableType\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetPersonTypeTable2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_PERSONTYPE_TABLE2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"persontype_tableType\" " +
              "/>" +
              "<procedure " +
                  "name=\"AddPersonTypeToTable\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"ADD_PERSONTYPE_TO_TABLE\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"persontype_tableType\" " +
              "/>" +
              "<procedure " +
                  "name=\"AddPersonTypeToTable2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"ADD_PERSONTYPE_TO_TABLE2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"persontype_tableType\" " +
              "/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          TestHelper.setUp(".");
    }

    @Test
    public void getPersonTypeTable() {
        Invocation invocation = new Invocation("GetPersonTypeTable");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        //marshaller.marshal(result, System.out);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    
    @Test
    public void getPersonTypeTable2() {
        Invocation invocation = new Invocation("GetPersonTypeTable2");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        //marshaller.marshal(result, System.out);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String RESULT_XML = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<persontype_tableType xmlns=\"urn:ObjectTableTypeTests\">" +
        "<item>" + 
        "<name>BUBBLES</name>" +
        "<age>32</age>" +
        "<gender>M</gender>" +
        "<incarcerated>1990-11-19</incarcerated>" +
        "</item>" +
        "<item>" +
        "<name>RICKY</name>" +
        "<age>33</age>" +
        "<gender>M</gender>" +
        "<incarcerated>1985-10-01</incarcerated>" +
        "</item>" +
        "<item>" +
        "<name>JULIAN</name>" +
        "<age>35</age>" +
        "<gender>M</gender>" +
        "<incarcerated>1988-02-07</incarcerated>" +
        "</item>" +
        "<item>" +
        "<name>SARAH</name>" +
        "<age>25</age>" +
        "<gender>F</gender>" +
        "<incarcerated>2002-05-12</incarcerated>" +
        "</item>" +
        "<item>" +
        "<name>J-ROC</name>" +
        "<age>27</age>" +
        "<gender>M</gender>" +
        "<incarcerated>1998-12-17</incarcerated>" +
        "</item>" +
        "</persontype_tableType>";
    
    @Test
    public void addPersonTypeToTable() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object personType = unmarshaller.unmarshal(new StringReader(PTYPE_INPUT_XML));
        Object tableType = unmarshaller.unmarshal(new StringReader(PTABLE_INPUT_XML));

        Invocation invocation = new Invocation("AddPersonTypeToTable");
        invocation.setParameter("PTYPETOADD", personType);
        invocation.setParameter("OLDTABLE", tableType);

        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        //marshaller.marshal(result, System.out);
        Document controlDoc = xmlParser.parse(new StringReader(NEW_PTABLE_OUTPUT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void addPersonTypeToTable2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object personType = unmarshaller.unmarshal(new StringReader(PTYPE_INPUT_XML));
        Object tableType = unmarshaller.unmarshal(new StringReader(PTABLE_INPUT_XML));

        Invocation invocation = new Invocation("AddPersonTypeToTable2");
        invocation.setParameter("PTYPETOADD", personType);
        invocation.setParameter("OLDTABLE", tableType);

        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        //marshaller.marshal(result, System.out);
        Document controlDoc = xmlParser.parse(new StringReader(NEW_PTABLE_OUTPUT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String PTYPE_INPUT_XML = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<persontypeType xmlns=\"urn:ObjectTableTypeTests\">" +
        "<name>COREY</name>" +
        "<age>20</age>" +
        "<gender>M</gender>" +
        "<incarcerated>1997-12-09</incarcerated>" +
        "</persontypeType>";
    
    static String PTABLE_INPUT_XML = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<persontype_tableType xmlns=\"urn:ObjectTableTypeTests\">" +
            "<item>" + 
            "<name>BUBBLES</name>" +
            "<age>32</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1990-11-19</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>RICKY</name>" +
            "<age>33</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1985-10-01</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>JULIAN</name>" +
            "<age>35</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1988-02-07</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>SARAH</name>" +
            "<age>25</age>" +
            "<gender>F</gender>" +
            "<incarcerated>2002-05-12</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>J-ROC</name>" +
            "<age>27</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1998-12-17</incarcerated>" +
            "</item>" +
            "</persontype_tableType>";
    
    static String NEW_PTABLE_OUTPUT_XML = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<persontype_tableType xmlns=\"urn:ObjectTableTypeTests\">" +
            "<item>" + 
            "<name>BUBBLES</name>" +
            "<age>32</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1990-11-19</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>RICKY</name>" +
            "<age>33</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1985-10-01</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>JULIAN</name>" +
            "<age>35</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1988-02-07</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>SARAH</name>" +
            "<age>25</age>" +
            "<gender>F</gender>" +
            "<incarcerated>2002-05-12</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>J-ROC</name>" +
            "<age>27</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1998-12-17</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>COREY</name>" +
            "<age>20</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1997-12-09</incarcerated>" +
            "</item>" +
            "</persontype_tableType>";
}