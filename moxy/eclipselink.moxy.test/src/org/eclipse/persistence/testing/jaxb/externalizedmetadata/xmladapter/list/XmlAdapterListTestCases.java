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
 * dmccann - December 30/2010 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.list;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class XmlAdapterListTestCases extends ExternalizedMetadataTestCases {
    public XmlAdapterListTestCases(String name) {
        super(name);
    }

    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/list/";
    private static final String SINGLE_RESOURCE = PATH + "singlebar.xml";
    private static final String MULTIPLE_RESOURCE = PATH + "multiplebar.xml";
    
    /**
     * Tests adapting a list.
     * 
     * Positive test.
     */
    public void testSingleBar() {
        FooWithBar fooWithBar = new FooWithBar();
        List<String> itemlist = new ArrayList<String>();
        itemlist = new ArrayList<String>();
        itemlist.add(MyAdapter.VAL0);
        itemlist.add(MyAdapter.VAL1);
        itemlist.add(MyAdapter.VAL2);
        fooWithBar.items = itemlist;
        
        JAXBContext jCtx = null;
        try {
            jCtx = JAXBContext.newInstance(new Class[] {FooWithBar.class, Bar.class});
            Object result = jCtx.createUnmarshaller().unmarshal(new File(SINGLE_RESOURCE));
            assertTrue("Unmarshal failed; objects are not equal", fooWithBar.equals(result));

            Document testDoc = parser.newDocument();
            Document ctrlDoc = parser.newDocument();
            try {
                ctrlDoc = getControlDocument(SINGLE_RESOURCE);
            } catch (Exception e) {
                e.printStackTrace();
                fail("An unexpected exception occurred loading control document [" + SINGLE_RESOURCE + "].");
            }
            Marshaller marshaller = jCtx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(fooWithBar, testDoc);
            assertTrue("Marshal failed; documents are not equal", compareDocuments(ctrlDoc, testDoc));
        } catch (Exception x) {
            x.printStackTrace();
            fail("An unexpected exception occurred.");
        }
    }

    /**
     * Tests adapting each entry in a given list.
     * 
     * Positive test.
     */
    public void testMultipleBars() {
        FooWithBars foo = new FooWithBars();
        List<String> itemlist = new ArrayList<String>();
        itemlist.add(MyAdapter.VAL0);
        itemlist.add(MyAdapter.VAL1);
        itemlist.add(MyAdapter.VAL2);
        foo.items = itemlist;
        
        JAXBContext jCtx = null;
        try {
            jCtx = JAXBContext.newInstance(new Class[] {FooWithBars.class, Bar.class});
            Object result = jCtx.createUnmarshaller().unmarshal(new File(MULTIPLE_RESOURCE));
            assertTrue("Unmarshal failed; objects are not equal", foo.equals(result));
            Document testDoc = parser.newDocument();
            Document ctrlDoc = parser.newDocument();
            try {
                ctrlDoc = getControlDocument(MULTIPLE_RESOURCE);
            } catch (Exception e) {
                e.printStackTrace();
                fail("An unexpected exception occurred loading control document [" + MULTIPLE_RESOURCE + "].");
            }
            Marshaller marshaller = jCtx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(foo, testDoc);
            assertTrue("Marshal failed; documents are not equal", compareDocuments(ctrlDoc, testDoc));
        } catch (Exception x) {
            x.printStackTrace();
            fail("An unexpected exception occurred.");
        }
    }
}