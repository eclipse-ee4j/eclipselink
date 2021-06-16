/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
