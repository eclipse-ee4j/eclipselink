/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - July 14/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement;

import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.javamodel.reflection.AnnotationHelper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Tests XmlElement via eclipselink-oxm.xml
 *
 */
public class XmlElementTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE_INVALID = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/employee-invalid.xml";

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     * @throws Exception
     */
    public XmlElementTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Employee.class});
    }

     public Map getProperties(){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/eclipselink-oxm.xml");

            HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
            metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement", new StreamSource(inputStream));
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

            // test override of 'old' context factory property - if the override
            // fails we will get a ClassCastException
            properties.put(JAXBContextFactory.ANNOTATION_HELPER_KEY, "Blah");
            properties.put(JAXBContextProperties.ANNOTATION_HELPER, new AnnotationHelper());

            return properties;
        }

       public void testSchemaGen() throws Exception{
            List controlSchemas = new ArrayList();
            InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/schema.xsd");
            InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/schema2.xsd");
            controlSchemas.add(is);
            controlSchemas.add(is2);
            super.testSchemaGen(controlSchemas);

        }


    /**
     * Tests @XmlElement override via eclipselink-oxm.xml.  The myUtilDate
     * element is set to 'required' in the xml file, but the instance
     * document does not have a myUtilDate element.
     *
     * Negative test.
     */
    public void testXmlElementOverrideInvalid() {
        InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/schema.xsd");
        StreamSource schemaSource = new StreamSource(schema);

        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_INVALID);
        String result = validateAgainstSchema(instanceDocStream, schemaSource);
        assertTrue("Schema validation passed unxepectedly", result != null);

    }

    public void testTeamWithListOfPlayersSchemaGen() throws Exception{
        ClassLoader loader = getClass().getClassLoader();
        String metadataFile = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/team-oxm.xml";

        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement", new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        JAXBContext jContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Team.class }, properties, loader);

        MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();
        jContext.generateSchema(outputResolver);


        List controlSchemas = new ArrayList();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/team-schema.xsd");
        controlSchemas.add(is);

        List<Writer> generatedSchemas = outputResolver.getSchemaFiles();
        compareSchemas(controlSchemas, generatedSchemas);
        /*

        super.testSchemaGen(controlSchemas);

        try {
            JAXBContext jContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Team.class }, properties, loader);
            MySchemaOutputResolver oResolver = new MySchemaOutputResolver();
            jContext.generateSchema(oResolver);
            // validate schema
            String controlSchema = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/team-schema.xsd";
            compareSchemas(oResolver.schemaFiles.get(""), new File(controlSchema));
        } catch (JAXBException e) {
            fail("An exception occurred creating the JAXBContext: " + e.getMessage());
        }
        */
    }
    /*
    public void testTeamWithListOfPlayersSchemaGen() {
        String metadataFile = PATH + "team-oxm.xml";

        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        try {
            JAXBContext jContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Team.class }, properties, loader);
            MySchemaOutputResolver oResolver = new MySchemaOutputResolver();
            jContext.generateSchema(oResolver);
            // validate schema
            String controlSchema = PATH + "team-schema.xsd";
            compareSchemas(oResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        } catch (JAXBException e) {
            fail("An exception occurred creating the JAXBContext: " + e.getMessage());
        }
    }
    */
    /**
     * Test setting the container class via container-type attribute.
     *
     * Positive test.
     */
    public void testContainerType() {
        XMLDescriptor xDesc = ((JAXBContext)jaxbContext).getXMLContext().getDescriptor(new QName("employee"));
        assertNotNull("No descriptor was generated for Employee.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("myEmployees");
        assertNotNull("No mapping exists on Employee for attribute [myEmployees].", mapping);
        assertTrue("Expected an XMLCompositeCollectionMapping for attribute [myEmployees], but was [" + mapping.toString() +"].", mapping instanceof XMLCompositeCollectionMapping);
        assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLCompositeCollectionMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLCompositeCollectionMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
    }

    @Override
    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.firstName = "firstName";
        emp.lastName = "LastName";
        emp.id = 66;
        emp.setMyInt(66);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(1976, Calendar.FEBRUARY, 17, 6, 15,30);

        emp.myUtilDate = cal;
        return emp;
    }

    @Override
    protected String getControlJSONDocumentContent() {
        return "{\"employee\":{\n" +
                "  \"firstName\":\"firstName\",\n" +
                "  \"last-name\":\"LastName\",\n" +
                "  \"id\":66,\n" +
                "  \"aUtilDate\":\"1976-02-17T06:15:30"+TIMEZONE_OFFSET+"\",\n" +
                "  \"myInt\":66\n" +
                "}}";
    }

    public boolean isUnmarshalTest() {
         return false;
    }

    @Override
    protected Document getControlDocument() {
        String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<employee xmlns:ns0=\"http://www.example.com/utils\">" +
                "<firstName>firstName</firstName>" +
                "<last-name>LastName</last-name>" +
                "<id>66</id>" +
                "<ns0:aUtilDate>1976-02-17T06:15:30"+TIMEZONE_OFFSET+"</ns0:aUtilDate>" +
                "<myInt>66</myInt>" +
                "</employee>";

        StringReader reader = new StringReader(contents);
        InputSource is = new InputSource(reader);
        Document doc = null;
        try {
            doc = parser.parse(is);
        } catch (Exception e) {
            fail("An error occurred setting up the control document");
        }
        return doc;
    }
}
