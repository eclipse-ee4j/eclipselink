/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.copyhelper;

import commonj.sdo.DataObject;
import org.eclipse.persistence.sdo.SDOChangeSummary;

public class SDOCopyHelperOriginalDeepCopyTestCases extends SDOCopyHelperDeepCopyTest {
    public SDOCopyHelperOriginalDeepCopyTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SDOCopyHelperOriginalDeepCopyTestCases.class);
    }

    public SDOChangeSummary getChangeSummary() {        
        return(SDOChangeSummary)containedDataObject.getChangeSummary();
    }

    public void assertEqualityHelperEqual(DataObject original, DataObject copy) {
        if (original.getChangeSummary() != null) {
            if (original.getChangeSummary().getChangedDataObjects().size() > 0) {
                assertFalse(equalityHelper.equal(original, copy));
                original.getChangeSummary().undoChanges();
            }
        }
        assertTrue(equalityHelper.equal(original, copy));
    }
       
}