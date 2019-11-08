/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Martin Vojtek - 2.6.0 - initial implementation
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
