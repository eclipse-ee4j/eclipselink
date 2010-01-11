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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.localization;

import org.eclipse.persistence.testing.framework.*;

public class LocalizationTestModel extends TestModel {
    public LocalizationTestModel() {
        setDescription("This suite tests localization.");
    }

    public void addTests() {
        addTest(getLocalizationTestSuite());
    }

    public TestSuite getLocalizationTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Localization Test Suite");
        suite.addTest(new LocalizationTest());

        return suite;
    }
}
