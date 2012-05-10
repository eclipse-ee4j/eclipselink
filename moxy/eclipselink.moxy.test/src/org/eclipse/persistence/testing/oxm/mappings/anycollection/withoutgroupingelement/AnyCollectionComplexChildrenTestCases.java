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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement;


/**
 *  @version $Header: AnyCollectionComplexChildrenTestCases.java 11-jul-2006.13:37:04 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
import java.io.InputStream;
import java.util.Vector;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class AnyCollectionComplexChildrenTestCases extends XMLMappingTestCases {
    //private Document writeControlDoc;
    public AnyCollectionComplexChildrenTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyCollectionWithGroupingElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withoutgroupingelement/complex_children.xml");
        //setWriteControlDoc("org/eclipse/persistence/testing/oxm/mappings/anycollection/withoutgroupingelement/complex_children_write.xml");
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
        }
    */
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionComplexChildrenTestCases" };
        TestRunner.main(arguments);
    }
}
