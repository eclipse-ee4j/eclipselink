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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.unmarshaller;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class DefaultValueTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/unmarshaller/default.xml";
    private static final String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/unmarshaller/default.xsd";

    private Schema schema;

    public DefaultValueTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {DefaultValueRoot.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schema = sf.newSchema(new File(XSD_RESOURCE)); 
    }


    @Override
    public Unmarshaller getJAXBUnmarshaller() {
        Unmarshaller unmarshaller =  super.getJAXBUnmarshaller();
        unmarshaller.setSchema(schema);
        return unmarshaller;
    }

    @Override
    protected DefaultValueRoot getControlObject() {
        DefaultValueRoot root = new DefaultValueRoot();
        root.setElement("");
        return root;
    }

}
