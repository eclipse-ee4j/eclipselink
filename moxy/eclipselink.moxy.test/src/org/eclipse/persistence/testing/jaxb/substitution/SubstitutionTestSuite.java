/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.substitution;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SubstitutionTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB Substitution Groups Test Suite");
        suite.addTestSuite(SubstitutionEnglishTestCases.class);
        suite.addTestSuite(SubstitutionFrenchTestCases.class);
        return suite;
    }

}