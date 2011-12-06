/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlinlinebinary;

import java.util.ArrayList;

import javax.activation.DataHandler;

import org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class InlineDataHandlerCollectionTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinlinebinary/inlinedatahandlercollection.xml";
    
    public InlineDataHandlerCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[1];
        classes[0] = Root.class;
        setClasses(classes);
    }
    
    protected Object getControlObject() {
        Root obj = new Root();
        obj.data = new ArrayList<DataHandler>();
        byte[] bytes1 = {1, 2, 3, 4, 5, 6, 7};
        byte[] bytes2 = {2, 3, 4, 5, 6, 7, 8};
        byte[] bytes3 = {3, 4, 5, 6, 7, 8, 9};
        
        XMLBinaryDataHelper helper = XMLBinaryDataHelper.getXMLBinaryDataHelper();
        
        obj.data.add(helper.convertObjectToDataHandler(bytes1, null));
        obj.data.add(helper.convertObjectToDataHandler(bytes2, null));
        obj.data.add(helper.convertObjectToDataHandler(bytes3, null));
        
        return obj;
    }
}