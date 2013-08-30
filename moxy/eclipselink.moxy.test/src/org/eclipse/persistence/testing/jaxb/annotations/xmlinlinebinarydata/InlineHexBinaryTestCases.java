/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.5.1 - Initial Implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InlineHexBinaryTestCases extends JAXBWithJSONTestCases {
    
    private static final String XML_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmlinlinebinary/hexbinary.xml";
    private static final String JSON_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmlinlinebinary/hexbinary.json";
    
    public InlineHexBinaryTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{HexRoot.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Object getControlObject() {
        HexRoot r = new HexRoot();
        r.bytes = "String to be encoded as hex".getBytes();
        return r;
        
    }

}
