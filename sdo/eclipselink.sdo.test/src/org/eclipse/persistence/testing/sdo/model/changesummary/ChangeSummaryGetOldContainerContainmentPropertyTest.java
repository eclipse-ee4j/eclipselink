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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;

public class ChangeSummaryGetOldContainerContainmentPropertyTest extends ChangeSummaryTestCases {
    public ChangeSummaryGetOldContainerContainmentPropertyTest(String name) {
        super(name);
    }
    
     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryGetOldContainerContainmentPropertyTest" };
        TestRunner.main(arguments);
    }

    // purpose: test if containedObject's container is changed, changesummary can still
    // return the former container
    public void testContainerValueAfterDataObjectChangeContainer() {
        SDODataObject container = (SDODataObject)containedDataObject.getContainer();
        changeSummary.beginLogging();
        SDODataObject o = null;
        root.set(rootProperty, o);
        this.assertNull(containedDataObject.getContainer());               
        this.assertEquals(root, changeSummary.getOldContainer(containedDataObject));
    }

    // purpose: test null value as parameter
    public void testContainerValueByPassingNull() {
        this.assertNull(changeSummary.getOldContainer(null));
    }

    // purpose: test pass in dataobjec not in the tree as parameter
    public void testContainerValueByPassingNotExistedDataObject() {
        SDODataObject obj = new SDODataObject();
        this.assertNull(changeSummary.getOldContainer(obj));
    }

    // purpose: test if containedObject's containment property is changed, changesummary can still
    // return the former value back
    public void testContainmentPropertyValueAfterDataObjectChangeValue() {
        SDOProperty containmentProperty = (SDOProperty)containedDataObject.getContainmentProperty();
        changeSummary.beginLogging();
        this.assertTrue(rootProperty.equals(containedDataObject.getContainmentProperty()));// it is the same before modified
        Object o = null;
        root.set(rootProperty, o);
        this.assertFalse(rootProperty.equals(containedDataObject.getContainmentProperty()));// current container in root's child should be changed
        this.assertEquals(containmentProperty, changeSummary.getOldContainmentProperty(containedDataObject));
    }

    // purpose: test null value as parameter
    public void testContainmentPropertyByPassingNull() {
        SDODataObject o = null;
        this.assertNull(changeSummary.getOldContainmentProperty(o));
    }

    // purpose: test pass in dataobjec not in the tree as parameter
    public void testContainmentPropertyByPassingNotExistedDataObject() {
        SDODataObject obj = new SDODataObject();
        this.assertNull(changeSummary.getOldContainmentProperty(obj));
    }
}
