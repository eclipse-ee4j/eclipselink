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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.create;
import junit.framework.Test;
import junit.framework.TestSuite;

public class ChangeSummaryLoadAndSaveCreateTestSuite {
public ChangeSummaryLoadAndSaveCreateTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper Create ChangeSummary Tests");

        suite.addTestSuite(ChangeSummaryRootCreateAlreadySetTestCases.class);
        suite.addTestSuite(ChangeSummaryCreateBug6120161TestCases.class);
        suite.addTestSuite(ChangeSummaryCreateBug6346754TestCases.class);
        
        return suite;
    }
}
