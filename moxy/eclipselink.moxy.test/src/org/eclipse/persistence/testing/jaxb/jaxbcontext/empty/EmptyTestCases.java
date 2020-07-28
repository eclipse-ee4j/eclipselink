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
package org.eclipse.persistence.testing.jaxb.jaxbcontext.empty;

import org.eclipse.persistence.testing.jaxb.jaxbcontext.empty.jaxbindex.JAXBIndexTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.empty.negative.NegativeTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.empty.objectfactory.ObjectFactoryTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class EmptyTestCases extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Empty JAXBContext Test Suite");
        suite.addTestSuite(JAXBIndexTestCases.class);
        suite.addTestSuite(ObjectFactoryTestCases.class);
        suite.addTestSuite(NegativeTestCases.class);
        return suite;
    }

}
