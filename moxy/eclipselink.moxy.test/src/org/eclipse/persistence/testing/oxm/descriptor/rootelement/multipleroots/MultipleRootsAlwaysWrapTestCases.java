/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - initial contribution 05-12-2009
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
