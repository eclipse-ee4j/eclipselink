/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Martin Vojtek - 2.6.0 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.installer;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This test suite tests eclipselink.zip installer.
 *
 * @author Martin Vojtek
 *
 */
public class InstallerTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Installer Test Suite");

        suite.addTestSuite(JaxbCompilerTestCase.class);

        return suite;
    }

}
