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
package org.eclipse.persistence.testing.oxm.xmlroot.complex;

import java.io.InputStream;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;
import org.w3c.dom.Document;

public class XMLRootComplexDifferentPrefixWithDRTestCases extends XMLRootComplexDifferentPrefixTestCases {
    public XMLRootComplexDifferentPrefixWithDRTestCases(String name) throws Exception {//modifyProjkect        
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexDifferentPrefixWithDRTestCases" };
        TestRunner.main(arguments);
    }

    public Project getTopLinkProject() {
        Project p = super.getTopLinkProject();
        ((XMLDescriptor)p.getDescriptor(Person.class)).setDefaultRootElement("aaa:employee");        
        NamespaceResolver nr = new NamespaceResolver();
        nr.put("aaa","test");
        nr.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        
        //((XMLDescriptor)p.getDescriptor(Person.class)).getNamespaceResolver().put("myns" , "blah2");
        //((XMLDescriptor)p.getDescriptor(Person.class)).getNamespaceResolver().put("aaa", "test");
        //((XMLDescriptor)p.getDescriptor(Person.class)).getNamespaceResolver().put("oxm" , "blah");
        ((XMLDescriptor)p.getDescriptor(Person.class)).setNamespaceResolver(nr);
        return p;
    }

    public Object getControlObject() {
        Person peep = new Person();
        peep.setName(CONTROL_PERSON_NAME);

        return peep;
    }

    public Object getWriteControlObject() {
        Person peep = new Person();
        peep.setName(CONTROL_PERSON_NAME);

        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_ELEMENT_NAME);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(peep);
        return xmlRoot;
    }

    public String getXMLResource() {
        return "org/eclipse/persistence/testing/oxm/xmlroot/complex/employee-diff-prefix-dr.xml";
    }

    public Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xmlroot/complex/employee-diff-prefix-dr-write.xml");
        Document writeDocument = parser.parse(inputStream);
        removeEmptyTextNodes(controlDocument);
        inputStream.close();
        return writeDocument;
    }
}
