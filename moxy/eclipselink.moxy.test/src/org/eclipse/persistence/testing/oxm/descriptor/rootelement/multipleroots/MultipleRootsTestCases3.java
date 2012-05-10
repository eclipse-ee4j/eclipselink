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
package org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots;

import java.io.InputStream;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.MailingAddress;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class MultipleRootsTestCases3 extends XMLMappingTestCases {
    
    private final static String THEADDRESS = "org/eclipse/persistence/testing/oxm/descriptor/rootelement/multipleroots/MultipleRootsTheAddress.xml";
        
    public MultipleRootsTestCases3(String name) throws Exception {
        super(name);        
        setControlDocument(THEADDRESS);
        setProject(new MultipleRootsProject());     
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsTestCases3" };
        TestRunner.main(arguments);
    }

    protected Object getControlObject() {
        MailingAddress address = new MailingAddress();
        return address;
    }

}
