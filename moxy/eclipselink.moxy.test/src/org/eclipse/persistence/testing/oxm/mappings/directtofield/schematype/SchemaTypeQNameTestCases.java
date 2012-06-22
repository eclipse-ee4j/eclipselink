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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.schematype;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class SchemaTypeQNameTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/schematype/SchemaTypeQName.xml";

    public SchemaTypeQNameTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument("org/eclipse/persistence/testing/oxm/mappings/directtofield/schematype/SchemaTypeQNameWrite.xml");
        Project project = new SchemaTypeQNameProject();
        setProject(project);
    }

    protected Object getControlObject() {
        QNameHolder qnameHolder = new QNameHolder();

        QName theQName = new QName("someURI", "someLocalName");
        qnameHolder.setTheQName(theQName);
        List theQNamesList = new ArrayList();
        theQNamesList.add(XMLConstants.STRING_QNAME);
        theQNamesList.add(XMLConstants.INTEGER_QNAME);
        theQNamesList.add(XMLConstants.DATE_TIME_QNAME);
        QName aQName = new QName("mydefaultnamespace", "someOtherLocalName");
        theQNamesList.add(aQName);
        
        qnameHolder.setTheQNames(theQNamesList);
        qnameHolder.setTheQNames2(theQNamesList);
        return qnameHolder;
    }        
}
