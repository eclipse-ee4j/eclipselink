/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.innerclasses.unreferenced;

import javax.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class UnreferencedInnerClassTestCases extends TestCase {

    public void testCreateContext() throws Exception {
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{ClassWithInnerClass.class}, null);
    }
}
