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
//     Denise Smith, Nov. 19, 2009
package org.eclipse.persistence.testing.jaxb.listofobjects.externalizedmetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.listofobjects.JAXBListOfObjectsTestCases;
import org.w3c.dom.Document;

public class JAXBTypedListTestCases  extends JAXBListOfObjectsTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/externalizedmetadata/typedlist.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/externalizedmetadata/typedlist.json";
    protected final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/externalizedmetadata/typedlistwrite.json";
    protected final static String XML_RESOURCE_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/externalizedmetadata/typedlistXsiType.xml";

    public JAXBTypedListTestCases(String name) throws Exception {
        super(name);
        init();
    }

    protected Type getTypeToUnmarshalTo() throws Exception {
        return Company.class;
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        setWriteControlDocument(XML_RESOURCE_XSI_TYPE);
        Class[] classes = new Class[1];
        classes[0] = Company.class;

        setClasses(classes);
        initXsiType();
    }

    @Override
    protected Map<String, String> getAdditationalNamespaces() {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("examplenamespace", "ns0");
        return namespaces;
    }

    protected Map getProperties() {
        String pkg = "org.eclipse.persistence.testing.jaxb.listofobjects.externalizedmetadata";
        HashMap<String, Source> overrides = new HashMap<String, Source>();
        overrides.put(pkg, generateXmlSchemaOxm());
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return properties;
    }

    private Source generateXmlSchemaOxm() {
        String oxm =
            "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +
               "<java-types>" +
                  "<java-type name='org.eclipse.persistence.testing.jaxb.listofobjects.externalizedmetadata.Company'>"+
                      "<java-attributes>" +
                          "<xml-element java-attribute='departments' name='departments' type='java.lang.Integer'/>" +
                          "<xml-element java-attribute='departmentIdToName' name='departmentIdToName'>" +
                             "<xml-map>" +
                                 "<key type='java.lang.Integer'/>" +
                                 "<value type='java.lang.String'/>" +
                             "</xml-map>" +
                          "</xml-element>" +
                          "<xml-element java-attribute='intObjectMap' name='int-to-object'>" +
                             "<xml-map>" +
                                 "<key type='java.lang.Integer'/>" +
                             "</xml-map>" +
                          "</xml-element>"+
                          "<xml-element java-attribute='objectStringMap' name='object-to-string'>" +
                             "<xml-map>" +
                                 "<value type='java.lang.String'/>" +
                             "</xml-map>" +
                          "</xml-element>" +
                          "<xml-element java-attribute='objectIntMap' name='object-to-int' type='java.lang.String'>" +
                             "<xml-map>" +
                                 "<value type='java.lang.Integer'/>" +
                             "</xml-map>" +
                          "</xml-element>" +
                   "</java-attributes>" +
               "</java-type>" +
              "</java-types>" +
           "</xml-bindings>";

        try{
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);
            Document doc = docBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(oxm.getBytes()));
            return new DOMSource(doc.getDocumentElement());
        }catch (Exception e){
            e.printStackTrace();
            fail("An error occurred during getProperties");
        }
        return null;
    }

    protected Object getControlObject() {
        Company company = new Company();
        List depts = new ArrayList();
        depts.add(1);
        depts.add(2);
        depts.add(3);
        company.setDepartments(depts);

        QName qname = new QName("examplenamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class ,null);
        jaxbElement.setValue(company);

        return jaxbElement;

    }

    public List< InputStream> getControlSchemaFiles(){
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/externalizedmetadata/typedlist.xsd");

        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);
        return controlSchema;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_XSI_TYPE;
    }
}
