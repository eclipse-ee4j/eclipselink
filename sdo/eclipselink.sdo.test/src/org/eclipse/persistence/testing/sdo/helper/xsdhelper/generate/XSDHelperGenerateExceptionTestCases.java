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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;

public class XSDHelperGenerateExceptionTestCases extends XSDHelperTestCases {
    public XSDHelperGenerateExceptionTestCases(String name) {
        super(name);
    }

    public void testGenerateFromNullList() {
        List types = null;
        try {
            xsdHelper.generate(types);
            fail("An IllegalArgument Exception should have occurred.");
        } catch (Exception e) {
        }
    }
		
		public void testGenerateFromEmptyList() {
        List types = new ArrayList();
        try {
            xsdHelper.generate(types);
            fail("An IllegalArgument Exception should have occurred.");
        } catch (Exception e) {
        }
    }
}
