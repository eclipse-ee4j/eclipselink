/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - December 4/2009 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.lexicalhandler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LexicalHandlerTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("LexicalHandler Test Suite");
        suite.addTestSuite(UnmarshalRecordTestCases.class);
        return suite;
    }

}
