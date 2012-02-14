/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
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