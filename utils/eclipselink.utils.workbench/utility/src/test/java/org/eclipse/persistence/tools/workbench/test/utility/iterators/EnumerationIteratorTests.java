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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.EnumerationIterator;

public class EnumerationIteratorTests extends TestCase {

    public static Test suite() {
        return new TestSuite(EnumerationIteratorTests.class);
    }

    public EnumerationIteratorTests(String name) {
        super(name);
    }

    public void testHasNext() {
        int i = 0;
        for (Iterator stream = this.buildIterator(); stream.hasNext(); ) {
            stream.next();
            i++;
        }
        assertEquals(this.buildVector().size(), i);
    }

    public void testNext() {
        Enumeration enumeration = this.buildEnumeration();
        for (Iterator stream = this.buildIterator(); stream.hasNext(); ) {
            assertEquals("bogus element", enumeration.nextElement(), stream.next());
        }
    }

    public void testNoSuchElementException() {
        boolean exCaught = false;
        Iterator stream = this.buildIterator();
        String string = null;
        while (stream.hasNext()) {
            string = (String) stream.next();
        }
        try {
            string = (String) stream.next();
        } catch (NoSuchElementException ex) {
            exCaught = true;
        }
        assertTrue("NoSuchElementException not thrown: " + string, exCaught);
    }

    public void testUnsupportedOperationException() {
        boolean exCaught = false;
        for (Iterator stream = this.buildIterator(); stream.hasNext(); ) {
            if (stream.next().equals("three")) {
                try {
                    stream.remove();
                } catch (UnsupportedOperationException ex) {
                    exCaught = true;
                }
            }
        }
        assertTrue("UnsupportedOperationException not thrown", exCaught);
    }

    private Iterator buildIterator() {
        return this.buildIterator(this.buildEnumeration());
    }

    private Iterator buildIterator(Enumeration enumeration) {
        return new EnumerationIterator(enumeration);
    }

    private Enumeration buildEnumeration() {
        return this.buildVector().elements();
    }

    private Vector buildVector() {
        Vector v = new Vector();
        v.addElement("one");
        v.addElement("two");
        v.addElement("three");
        v.addElement("four");
        v.addElement("five");
        v.addElement("six");
        v.addElement("seven");
        v.addElement("eight");
        return v;
    }

}
