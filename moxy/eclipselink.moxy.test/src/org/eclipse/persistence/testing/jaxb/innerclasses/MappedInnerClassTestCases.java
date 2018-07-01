/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.innerclasses;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class MappedInnerClassTestCases extends TestCase {

    public MappedInnerClassTestCases(String name) {
        super(name);
    }

    public void testCreateJAXBContext() {
        try {
            JAXBContextFactory.createContext(new Class[] {MappedInnerClassRoot.class}, null);
        } catch(JAXBException e) {
            return;
        }
        fail("A JAXBException should have been thrown but wasn't");
    }

}
