/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model.changesummary;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class ChangeSummaryExceptionTestCases extends SDOTestCase {
    public ChangeSummaryExceptionTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryExceptionTestCases" };
        TestRunner.main(arguments);
    }

    public void testManyPropForCS() {
        DataObject rootTypeDO = defineType("rootUri", "rootTypeName");
        addProperty(rootTypeDO, "csmProp", SDOConstants.SDO_CHANGESUMMARY, true, true, true);
        SDOType rootType = (SDOType)typeHelper.define(rootTypeDO);

        try {
            DataObject root = (SDODataObject)dataFactory.create(rootType);
        } catch (IllegalArgumentException e) {            
            assertTrue(e.getMessage().equals("ChangeSummary can not be on a property with many set to true."));
            return;
        }
        fail("An Illegalargument should have occurred");
    }   
}