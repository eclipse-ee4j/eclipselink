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
*     bdoughan - December 4/2009 - 2.1 - Initial implementation
******************************************************************************/
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