/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - initial API and implementation
 ******************************************************************************/   
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlValueByteArrayTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/bytesholder.xml";

    public XmlValueByteArrayTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[1];
        classes[0] = BytesHolder.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        BytesHolder holder = new BytesHolder();
        byte[] bytes = "ASTRING".getBytes();
        holder.theBytes = bytes;

        return holder;
    }           
}
