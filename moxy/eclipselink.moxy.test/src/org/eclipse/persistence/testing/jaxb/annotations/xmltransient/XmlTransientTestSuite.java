/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class XmlTransientTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("XmlTransient Test Suite");
        suite.addTestSuite(XmlTransientFieldTestCases.class);
        suite.addTestSuite(XmlTransientPropertyTestCases.class);
        suite.addTestSuite(DoubleTransientTestCases.class);
        suite.addTestSuite(PropOrderTestCases.class);
        return suite;
    }

}