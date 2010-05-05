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
 * mmacivor - April 23/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import junit.textui.TestRunner;

public class SchemaResolverSystemIdTestCases extends XSDHelperDefineTestCases {
    public SchemaResolverSystemIdTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(SchemaResolverSystemIdTestCases.class);
    }

    public String getSchemaToDefine() {
        return "";
    }

    public void testDefine() {
        StreamSource source = new StreamSource("xyz:zxy");
        List types = ((SDOXSDHelper)xsdHelper).define(source, new MySchemaResolver());
        assertEquals(1, types.size());
    }

    public void testSAXSourceDefine() {
        InputSource inputSource = new InputSource("xyz:zxy");
        SAXSource source = new SAXSource(inputSource);
        List types = ((SDOXSDHelper)xsdHelper).define(source, new MySchemaResolver());
        assertEquals(1, types.size());
    }

    public void testCustomSourceDefine() {
        MySource source = new MySource();
        source.setSystemId("xyz:zxy");
        List types = ((SDOXSDHelper)xsdHelper).define(source, new MySchemaResolver());
        assertEquals(1, types.size());
    }

    public static class MySchemaResolver implements SchemaResolver {

        public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
            return null;
        }

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            String schema = "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + 
            "<xsd:complexType name=\"employee-type\">" + 
            "<xsd:sequence>" + 
            "<xsd:element name=\"name\" type=\"xsd:string\"/>" + 
            "</xsd:sequence>" + 
            "</xsd:complexType>" + 
            "<xsd:element name=\"employee\" type=\"employee-type\"/>" +
            "</xsd:schema>";
            
            return new InputSource(new StringReader(schema));
        }
        
    }

    private static class MySource implements Source {

        private String systemId;

        public String getSystemId() {
            return systemId;
        }

        public void setSystemId(String systemId) {
            this.systemId = systemId;
        }

    }

    public List getControlTypes()
    {
      return new ArrayList();
    }
}
