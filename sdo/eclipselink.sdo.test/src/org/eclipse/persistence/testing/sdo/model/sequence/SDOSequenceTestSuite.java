/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model.sequence;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOSequenceTestSuite {
    public SDOSequenceTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All SDOSequence Tests");        
        suite.addTest(new TestSuite(SDOSequenceTestCS.class));
        suite.addTest(new TestSuite(SDOSequenceTestXSD.class));
        suite.addTest(new TestSuite(SDOSequenceTest.class));
        suite.addTest(new TestSuite(SDOSequenceListTestCases.class));
        suite.addTest(new TestSuite(SequenceJIRA242TestCases.class));
        return suite;
    }
}
