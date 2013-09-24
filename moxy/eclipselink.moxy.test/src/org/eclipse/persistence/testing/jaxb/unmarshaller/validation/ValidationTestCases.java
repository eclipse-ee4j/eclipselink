/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.unmarshaller.validation;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class ValidationTestCases extends JAXBTestCases {

    private static final String XML = "org/eclipse/persistence/testing/jaxb/unmarshaller/validation/input.xml";
    private static final String XSD = "org/eclipse/persistence/testing/jaxb/unmarshaller/validation/schema.xsd";

    public ValidationTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML);
        setClasses(new Class[] {CanadianAddress.class});
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(Thread.currentThread().getContextClassLoader().getResource(XSD));
        this.jaxbUnmarshaller.setSchema(schema);
    }

    @Override
    protected JAXBElement<Address> getControlObject() {
        CanadianAddress control = new CanadianAddress();
        control.street = "1 A Street";
        control.city = "Any Town";
        control.province = "Ontario";
        control.postalCode = "A1B 2C3";
        return new JAXBElement<Address>(new QName("urn:foo", "address"), Address.class, control);
    }

    @Override
    public CanadianAddress getReadControlObject() {
        CanadianAddress control = new CanadianAddress();
        control.street = "1 A Street";
        control.city = "Any Town";
        control.province = "Ontario";
        control.postalCode = "A1B 2C3";
        return control;
    }
}
