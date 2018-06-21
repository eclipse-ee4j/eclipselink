/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.utility.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;

public class SingleElementIteratorTests extends TestCase {

    public static Test suite() {
        return new TestSuite(SingleElementIteratorTests.class);
    }

    public SingleElementIteratorTests(String name) {
        super(name);
    }

    public void testHasNext() {
        int i = 0;
        for (Iterator stream = this.buildSingleElementIterator(); stream.hasNext(); ) {
            stream.next();
            i++;
        }
        assertEquals(1, i);
    }

    public void testNext() {
        for (Iterator stream = this.buildSingleElementIterator(); stream.hasNext(); ) {
            assertEquals("bogus element", this.singleElement(), stream.next());
        }
    }

    public void testNoSuchElementException() {
        boolean exCaught = false;
        Iterator stream = this.buildSingleElementIterator();
        String string = (String) stream.next();
        try {
            string = (String) stream.next();
        } catch (NoSuchElementException ex) {
            exCaught = true;
        }
        assertTrue("NoSuchElementException not thrown: " + string, exCaught);
    }

    public void testRemove() {
        boolean exCaught = false;
        for (Iterator stream = this.buildSingleElementIterator(); stream.hasNext(); ) {
            if (stream.next().equals(this.singleElement())) {
                try {
                    stream.remove();
                } catch (UnsupportedOperationException ex) {
                    exCaught = true;
                }
            }
        }
        assertTrue("UnsupportedOperationException not thrown", exCaught);
    }

    protected Iterator buildSingleElementIterator() {
        return new SingleElementIterator(this.singleElement());
    }

    protected Object singleElement() {
        return "single element";
    }
}
