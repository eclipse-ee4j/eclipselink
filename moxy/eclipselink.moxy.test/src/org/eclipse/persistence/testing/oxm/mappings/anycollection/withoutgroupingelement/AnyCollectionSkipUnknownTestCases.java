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

import java.io.InputStream;
import java.util.Vector;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class AnyCollectionSkipUnknownTestCases extends XMLMappingTestCases {
    public AnyCollectionSkipUnknownTestCases(String name) throws Exception {
        super(name);
        Project project = new AnyCollectionWithGroupingElementProject();
        ((XMLAnyCollectionMapping)((XMLDescriptor)project.getDescriptor(Root.class)).getMappingForAttributeName("any")).setUseXMLRoot(true);
        setProject(project);
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withoutgroupingelement/skip_unknown_children.xml");
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

        XMLRoot blahRoot = new XMLRoot();
        blahRoot.setLocalName("unknownchild2");
        blahRoot.setNamespaceURI(null);
        blahRoot.setObject("blah");

        any.addElement(blahRoot);
        root.setAny(any);
        return root;
    }

    protected Document getWriteControlDocument() throws Exception {
        String resource = "org/eclipse/persistence/testing/oxm/mappings/anycollection/withoutgroupingelement/skip_unknown_children_write.xml";
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(resource);
        Document doc = parser.parse(inputStream);
        removeEmptyTextNodes(doc);
        inputStream.close();
        return doc;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionSkipUnknownTestCases" };
        TestRunner.main(arguments);
    }
}
