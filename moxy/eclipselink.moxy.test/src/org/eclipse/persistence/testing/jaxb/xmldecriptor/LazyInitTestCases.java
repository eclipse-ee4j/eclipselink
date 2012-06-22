/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.1.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmldecriptor;

import java.lang.reflect.Field;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.oxm.TreeObjectBuilder;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBHelper;
import org.eclipse.persistence.oxm.XMLDescriptor;

import junit.framework.TestCase;

public class LazyInitTestCases extends TestCase {

    public LazyInitTestCases(String name) {
        super(name);
    }

    public void testEventManager() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {Root.class}, null);
        XMLDescriptor xmlDescriptor = JAXBHelper.getJAXBContext(jc).getXMLContext().getDescriptor(new QName("root"));
        assertFalse(xmlDescriptor.hasEventManager());
    }

    public void testCopyPolicy() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {Root.class}, null);
        XMLDescriptor xmlDescriptor = JAXBHelper.getJAXBContext(jc).getXMLContext().getDescriptor(new QName("root"));

        assertNull(getFieldValue(XMLDescriptor.class, "copyPolicy", xmlDescriptor));
        assertNull(getFieldValue(XMLDescriptor.class, "primaryKeyFields", xmlDescriptor));
    }

    public void testTreeObjectBuilder() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {Root.class}, null);
        XMLDescriptor xmlDescriptor = JAXBHelper.getJAXBContext(jc).getXMLContext().getDescriptor(new QName("root"));
        TreeObjectBuilder treeObjectBuilder = (TreeObjectBuilder) xmlDescriptor.getObjectBuilder();

        assertNull(getFieldValue(TreeObjectBuilder.class, "readOnlyMappingsByField", treeObjectBuilder));
        assertNull(getFieldValue(TreeObjectBuilder.class, "mappingsByAttribute", treeObjectBuilder));
        assertNull(getFieldValue(TreeObjectBuilder.class, "primaryKeyMappings", treeObjectBuilder));
        assertNull(getFieldValue(TreeObjectBuilder.class, "nonPrimaryKeyMappings", treeObjectBuilder));
        assertNull(getFieldValue(TreeObjectBuilder.class, "eagerMappings", treeObjectBuilder));
        assertNull(getFieldValue(TreeObjectBuilder.class, "relationshipMappings", treeObjectBuilder));
    }

    private Object getFieldValue(Class clazz, String fieldName, Object object) throws Exception {
        Field field = PrivilegedAccessHelper.getField(clazz, fieldName, true);
        return field.get(object);
    }

}
