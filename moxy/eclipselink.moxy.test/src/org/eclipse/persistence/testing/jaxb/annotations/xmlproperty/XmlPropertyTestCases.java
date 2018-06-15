/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlproperty;

import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;

import junit.framework.TestCase;

public class XmlPropertyTestCases extends TestCase {
    static final String CLASS_PROPERTY_1_NAME = "property1";
    static final String CLASS_PROPERTY_2_NAME = "property2";
    static final String FIELD_PROPERTY_1_NAME = "barProp";
    static final String FIELD_PROPERTY_2_NAME = "bar2Prop";


    Project project;

    @Override
    public void setUp() throws Exception {
        JAXBContext ctx = (JAXBContext)JAXBContextFactory.createContext(new Class[]{Foo.class}, null);
        this.project = ctx.getXMLContext().getSession(0).getProject();
    }

    public void testClassProperties() {
        ClassDescriptor descriptor = project.getClassDescriptor(Foo.class);
        Map<Object, Object> properties = descriptor.getProperties();
        assertTrue("Incorrect number of properties", properties.size() == 2);
        Object property = properties.get(CLASS_PROPERTY_1_NAME);
        assertNotNull(property);
        assertTrue("Incorrect value for property", property.equals("value1"));
        property = properties.get(CLASS_PROPERTY_2_NAME);
        assertNotNull(property);
        assertTrue("Incorrect value for property", property.equals(new Integer("121")));
    }

    public void testXmlPropertyOnField() {
        ClassDescriptor descriptor = project.getClassDescriptor(Foo.class);
        DatabaseMapping mapping = descriptor.getMappingForAttributeName("bar");
        Map<Object, Object> properties = mapping.getProperties();
        assertTrue("Incorrect number of properties", properties.size() == 1);
        Object property = properties.get(FIELD_PROPERTY_1_NAME);
        assertNotNull(property);
        assertTrue("Incorrect value for property", property.equals("barValue"));
    }

    public void testXmlPropertiesOnField() {
        ClassDescriptor descriptor = project.getClassDescriptor(Foo.class);
        DatabaseMapping mapping = descriptor.getMappingForAttributeName("bar2");
        Map<Object, Object> properties = mapping.getProperties();
        assertTrue("Incorrect number of properties", properties.size() == 1);
        Object property = properties.get(FIELD_PROPERTY_2_NAME);
        assertNotNull(property);
        assertTrue("Incorrect value for property", property.equals("bar2Value"));

    }

}
