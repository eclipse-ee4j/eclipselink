/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.helpercontext;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.helper.SDODataFactory;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.delegates.SDODataFactoryDelegator;
import org.eclipse.persistence.sdo.helper.delegates.SDOTypeHelperDelegator;
import org.eclipse.persistence.sdo.helper.delegates.SDOXMLHelperDelegator;
import org.eclipse.persistence.sdo.helper.delegates.SDOXSDHelperDelegator;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBValueStore;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

public class HelperContextTestCases extends SDOTestCase {

    private JAXBHelperContext jaxbHelperContext;

    public HelperContextTestCases(String name) {
        super(name);
    }

    public void setUp() {
        try {
            Class[] classes = new Class[1];
            classes[0] = Root.class;
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            jaxbHelperContext = new JAXBHelperContext(jaxbContext);
            jaxbHelperContext.makeDefaultContext();
            JAXBSchemaOutputResolver sor = new JAXBSchemaOutputResolver();
            jaxbContext.generateSchema(sor);
            String xmlSchema = sor.getSchema();
            jaxbHelperContext.getXSDHelper().define(xmlSchema);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testGetType() {
        Type pojoType = jaxbHelperContext.getType(Root.class);
        assertNotNull(pojoType);
        
        Type rootType = jaxbHelperContext.getTypeHelper().getType("urn:helpercontext", "root");
        assertSame(rootType, pojoType);
    }

    public void testGetClass() {
        Type rootType = jaxbHelperContext.getTypeHelper().getType("urn:helpercontext", "root");
        Class pojoClass = jaxbHelperContext.getClass(rootType);
        assertSame(Root.class, pojoClass);
    }

    public void testGetClassNull() {
        Class pojoClass = jaxbHelperContext.getClass(null);
        assertNull(pojoClass);
    }

    public void testGetClassNegative() {
        try {
            Type propertyType = jaxbHelperContext.getTypeHelper().getType("commonj.sdo", "Property");
            Class pojoClass = jaxbHelperContext.getClass(propertyType);
        } catch(SDOException e) {
            assertEquals(SDOException.SDO_JAXB_NO_DESCRIPTOR_FOR_TYPE, e.getErrorCode());
            return;
        }
        fail("An exception should have been thrown, but was not.");
    }

    public void testDataFactoryGetHelperContext() {
        SDODataFactoryDelegator sdoDataFactoryDelegator = (SDODataFactoryDelegator) DataFactory.INSTANCE;

        HelperContext testDefaultHelperContext = sdoDataFactoryDelegator.getHelperContext();
        assertSame(HelperProvider.getDefaultContext(), testDefaultHelperContext);

        HelperContext testHelperContext = sdoDataFactoryDelegator.getDataFactoryDelegate().getHelperContext(); 
        assertSame(jaxbHelperContext, testHelperContext);
    }

    public void testTypeHelperGetHelperContext() {
        SDOTypeHelperDelegator sdoTypeHelperDelegator = (SDOTypeHelperDelegator) TypeHelper.INSTANCE;

        HelperContext testDefaultHelperContext = sdoTypeHelperDelegator.getHelperContext();
        assertSame(HelperProvider.getDefaultContext(), testDefaultHelperContext);

        HelperContext testHelperContext = sdoTypeHelperDelegator.getTypeHelperDelegate().getHelperContext(); 
        assertSame(jaxbHelperContext, testHelperContext);
    }

    public void testXMLHelperGetHelperContext() {
        SDOXMLHelperDelegator sdoXMLHelperDelegator = (SDOXMLHelperDelegator) XMLHelper.INSTANCE;

        HelperContext testDefaultHelperContext = sdoXMLHelperDelegator.getHelperContext();
        assertSame(HelperProvider.getDefaultContext(), testDefaultHelperContext);

        HelperContext testHelperContext = sdoXMLHelperDelegator.getXMLHelperDelegate().getHelperContext(); 
        assertSame(jaxbHelperContext, testHelperContext);
    }

    public void testXSDHelperGetHelperContext() {
        SDOXSDHelperDelegator sdoXSDHelperDelegator = (SDOXSDHelperDelegator) XSDHelper.INSTANCE;

        HelperContext testDefaultHelperContext = sdoXSDHelperDelegator.getHelperContext();
        assertSame(HelperProvider.getDefaultContext(), testDefaultHelperContext);

        HelperContext testHelperContext = sdoXSDHelperDelegator.getXSDHelperDelegate().getHelperContext(); 
        assertSame(jaxbHelperContext, testHelperContext);
    }

    public void testWrap() {
        Root root = new Root();
        SDODataObject rootDO = (SDODataObject) jaxbHelperContext.wrap(root);
        assertNotNull(rootDO);
        
        assertSame(JAXBValueStore.class, rootDO._getCurrentValueStore().getClass());
    }

    public void testWrapTwice() {
        Root root = new Root();

        SDODataObject rootDO1 = (SDODataObject) jaxbHelperContext.wrap(root);
        assertNotNull(rootDO1);

        SDODataObject rootDO2 = (SDODataObject) jaxbHelperContext.wrap(root);
        assertNotNull(rootDO2);

        assertSame(rootDO1, rootDO2);
    }

    public void testWrapCollection() {
        Root root1 = new Root();
        Root root2 = new Root();
        Set collection = new HashSet(2);
        collection.add(root1);
        collection.add(root2);
        List<DataObject> wrappedList = jaxbHelperContext.wrap(collection);

        for(DataObject dataObject: wrappedList) {
            assertNotNull(dataObject);
            assertSame(JAXBValueStore.class, ((SDODataObject) dataObject)._getCurrentValueStore().getClass());
        }
    }

    public void testWrapNullCollection() {
        List<DataObject> wrappedList = jaxbHelperContext.wrap(null);
        assertNotNull(wrappedList);
        assertEquals(0, wrappedList.size());
    }

    public void testWrapEmptyCollection() {
        Set collection = new HashSet(0);
        List<DataObject> wrappedList = jaxbHelperContext.wrap(collection);
        assertNotNull(wrappedList);
        assertEquals(0, wrappedList.size());
    }

    public void testWrapNull() {
        DataObject nullDO = jaxbHelperContext.wrap((Object) null);
        assertNull(nullDO);
    }

    public void testWrapUnknownObject() {
        boolean fail = true;
        try {
            Object unknownObject = "FOO";
            jaxbHelperContext.wrap(unknownObject);
        } catch(SDOException e) {
            if(e.getErrorCode() == SDOException.SDO_JAXB_NO_TYPE_FOR_CLASS) {
                fail = false;
            }
        }
        if(fail) {
            fail();
        }
    }

    public void testWrapObjectWithoutSchemaReference() {
        boolean fail = true;
        try {
            org.eclipse.persistence.jaxb.JAXBContext jaxbContext = (org.eclipse.persistence.jaxb.JAXBContext) jaxbHelperContext.getJAXBContext();
            XMLDescriptor xmlDescriptor = (XMLDescriptor) jaxbContext.getXMLContext().getSession(Root.class).getDescriptor(Root.class);
            xmlDescriptor.setSchemaReference(null);
            jaxbHelperContext.wrap(new Root());
        } catch(SDOException e) {
            if(e.getErrorCode() == SDOException.SDO_JAXB_NO_SCHEMA_REFERENCE) {
                fail = false;
            }
        }
        if(fail) {
            fail();
        }
    }

    public void testWrapObjectWithoutSchemaContext() {
        boolean fail = true;
        try {
            org.eclipse.persistence.jaxb.JAXBContext jaxbContext = (org.eclipse.persistence.jaxb.JAXBContext) jaxbHelperContext.getJAXBContext();
            XMLDescriptor xmlDescriptor = (XMLDescriptor) jaxbContext.getXMLContext().getSession(Root.class).getDescriptor(Root.class);
            xmlDescriptor.getSchemaReference().setSchemaContext(null);
            xmlDescriptor.getSchemaReference().setSchemaContextAsQName(null);
            jaxbHelperContext.wrap(new Root());
        } catch(SDOException e) {
            if(e.getErrorCode() == SDOException.SDO_JAXB_NO_SCHEMA_CONTEXT) {
                fail = false;
            }
        }
        if(fail) {
            fail();
        }
    }

    public void testWrapObjectWithIncorrectSchemaContext() {
        boolean fail = true;
        try {
            org.eclipse.persistence.jaxb.JAXBContext jaxbContext = (org.eclipse.persistence.jaxb.JAXBContext) jaxbHelperContext.getJAXBContext();
            XMLDescriptor xmlDescriptor = (XMLDescriptor) jaxbContext.getXMLContext().getSession(Root.class).getDescriptor(Root.class);
            xmlDescriptor.getSchemaReference().setSchemaContext("/INVALID");
            xmlDescriptor.getSchemaReference().setSchemaContextAsQName(null);
            jaxbHelperContext.wrap(new Root());
        } catch(SDOException e) {
            if(e.getErrorCode() == SDOException.SDO_JAXB_NO_TYPE_FOR_CLASS_BY_SCHEMA_CONTEXT) {
                fail = false;
            }
        }
        if(fail) {
            fail();
        }
    }

    public void testUnwrap() {
        Root root = new Root();
        SDODataObject rootDO = (SDODataObject) jaxbHelperContext.wrap(root);
        assertSame(root, jaxbHelperContext.unwrap(rootDO));
    }

    public void testUnwrapNull() {
        Object object = jaxbHelperContext.unwrap((DataObject) null);
        assertNull(object);
    }

    public void testUnwrapCollection() {
        Root root1 = new Root();
        Root root2 = new Root();
        Set collection = new HashSet(2);
        collection.add(root1);
        collection.add(root2);
        Collection<DataObject> wrappedEntities = jaxbHelperContext.wrap(collection);

        List<Object> entities = jaxbHelperContext.unwrap(wrappedEntities);
        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertTrue(entities.contains(root1));
        assertTrue(entities.contains(root2));
    }

    public void testUnwrapNullCollection() {
        List<Object> entities = jaxbHelperContext.unwrap((Collection) null);
        assertNotNull(entities);
        assertEquals(0, entities.size());
    }

    public void testUnwrapEmptyCollection() {
        Collection wrappedCollection = new HashSet(0);
        List entities = jaxbHelperContext.unwrap(wrappedCollection);
        assertNotNull(entities);
        assertEquals(0, entities.size());
    }

    public void tearDown() {
    }

    private class JAXBSchemaOutputResolver extends SchemaOutputResolver {

        private StringWriter schemaWriter;

        public String getSchema() {
            return schemaWriter.toString();
        }

        public Result createOutput(String arg0, String arg1) throws IOException {
            schemaWriter = new StringWriter();
            return new StreamResult(schemaWriter);
        }

    }

}
