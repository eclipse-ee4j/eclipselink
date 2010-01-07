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
 *     Denise Smith - initial contribution 05-12-2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots;

import junit.textui.TestRunner;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.MailingAddress;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class MultipleRootsAlwaysWrapTestCases extends XMLMappingTestCases {
    private final static String THEADDRESS = "org/eclipse/persistence/testing/oxm/descriptor/rootelement/multipleroots/MultipleRootsTheAddress.xml";
     
    public MultipleRootsAlwaysWrapTestCases(String name) throws Exception {
        super(name);        
        setControlDocument(THEADDRESS);
        MultipleRootsProject p = new MultipleRootsProject(); 
        ((XMLDescriptor)p.getDescriptor(MailingAddress.class)).setResultAlwaysXMLRoot(true);
        setProject(p);     
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsAlwaysWrapTestCases" };
        TestRunner.main(arguments);
    }

    protected Object getControlObject() {
        XMLRoot xmlRoot = new XMLRoot();
        MailingAddress address = new MailingAddress();
        xmlRoot.setLocalName("theAddress");
        xmlRoot.setObject(address);
        return xmlRoot;
    }
}
