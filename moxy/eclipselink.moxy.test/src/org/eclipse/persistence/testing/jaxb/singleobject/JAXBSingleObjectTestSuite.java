/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.jaxb.singleobject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.xmladapter.composite.XmlAdapterCompositeTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.compositecollection.XmlAdapterCompositeCollectionTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection.XmlAdapterCompositeDirectCollectionTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.direct.XmlAdapterDirectTestCases;

public class JAXBSingleObjectTestSuite extends TestCase {
    public JAXBSingleObjectTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.jaxb.singleobject.JAXBSingleObjectTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Object Test Suite");
        suite.addTestSuite(JAXBSingleObjectIntegerNoXsiTestCases.class);
        suite.addTestSuite(JAXBSingleObjectIntegerXsiTestCases.class);
        suite.addTestSuite(JAXBSingleObjectStringNoXsiTestCases.class);
        suite.addTestSuite(JAXBSingleObjectStringXsiTestCases.class);
        suite.addTestSuite(JAXBSingleObjectObjectNoXsiTestCases.class);
        suite.addTestSuite(JAXBSingleObjectObjectXsiTestCases.class);

        return suite;
    }
}
