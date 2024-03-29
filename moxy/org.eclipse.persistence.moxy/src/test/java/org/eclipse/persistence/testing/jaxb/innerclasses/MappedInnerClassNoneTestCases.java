/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class MappedInnerClassNoneTestCases extends TestCase {

    public MappedInnerClassNoneTestCases(String name) {
        super(name);
    }

    public void testCreateJAXBContext() {
        try {
            JAXBContextFactory.createContext(new Class<?>[] {MappedInnerClassNoneRoot.class}, null);
        } catch(Exception e) {
            fail("An exception should not have been thrown.");
        }
    }

}
