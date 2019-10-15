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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement;


/**
 *  @version $Header: AnyCollectionComplexChildrenNSTestCases.java 29-jun-2007.13:21:17 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
import java.io.InputStream;
import java.util.Vector;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class AnyCollectionComplexChildrenNSTestCases extends XMLMappingTestCases {
    // private Document writeControlDoc;
    public AnyCollectionComplexChildrenNSTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyCollectionWithGroupingElementProjectNS());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/complex_children_ns.xml");
        //setWriteControlDoc("org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/complex_children_ns_write.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        Vector any = new Vector();
        Child child = new Child();
        child.setContent("Child1");
        any.addElement(child);
        child = new Child();
        child.setContent("Child2");
        any.addElement(child);
        root.setAny(any);

        return root;
    }

    /*
        public Document getWriteControlDocument() {
            return writeControlDoc;
        }

        public void setWriteControlDoc(String xmlResource) throws Exception {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);
            writeControlDoc = parser.parse(inputStream);
            removeEmptyTextNodes(writeControlDoc);
            inputStream.close();
        }*/
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionComplexChildrenNSTestCases" };
        TestRunner.main(arguments);
    }
}
