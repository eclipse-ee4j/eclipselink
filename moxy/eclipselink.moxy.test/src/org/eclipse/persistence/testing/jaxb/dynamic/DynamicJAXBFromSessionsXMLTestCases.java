/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.jaxb.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.DynamicJAXBContextFactory;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class DynamicJAXBFromSessionsXMLTestCases extends AbstractDynamicJAXBTestCases {

    private static final String SESSION_NAMES = 
        "org.eclipse.persistence.testing.jaxb.dynamic:org.eclipse.persistence.testing.jaxb.dynamic.secondproject";
    private static final String XML_RESOURCE = 
        "org/eclipse/persistence/testing/jaxb/dynamic/root-instance.xml";

    public DynamicJAXBFromSessionsXMLTestCases(String name) throws Exception {
        super(name);

        setControlDocument(XML_RESOURCE);

        // Calling newInstance will end up eventually end up in DynamicJAXBContextFactory.createContext  
        jaxbContext = DynamicJAXBContext.newInstance(SESSION_NAMES);
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        xmlContext = ((DynamicJAXBContext) jaxbContext).getXMLContext();
    }

}