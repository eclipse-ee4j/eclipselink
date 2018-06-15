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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.utility.iterators;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.NullEnumeration;

public class NullEnumerationTests extends TestCase {

    public static Test suite() {
        return new TestSuite(NullEnumerationTests.class);
    }

    public NullEnumerationTests(String name) {
        super(name);
    }

    public void testHasMoreElements() {
        int i = 0;
        for (Enumeration stream = NullEnumeration.instance(); stream.hasMoreElements(); ) {
            stream.nextElement();
            i++;
        }
        assertEquals(0, i);
    }

    public void testNextElement() {
        for (Enumeration stream = NullEnumeration.instance(); stream.hasMoreElements(); ) {
            fail("bogus element: " + stream.nextElement());
        }
    }

    public void testNoSuchElementException() {
        boolean exCaught = false;
        Enumeration stream = NullEnumeration.instance();
        Object element = null;
        while (stream.hasMoreElements()) {
            element = stream.nextElement();
        }
        try {
            element = stream.nextElement();
        } catch (NoSuchElementException ex) {
            exCaught = true;
        }
        assertTrue("NoSuchElementException not thrown: " + element, exCaught);
    }

}
