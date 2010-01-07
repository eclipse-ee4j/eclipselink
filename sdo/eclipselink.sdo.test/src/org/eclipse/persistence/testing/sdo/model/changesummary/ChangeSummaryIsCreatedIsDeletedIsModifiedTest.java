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
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class ChangeSummaryIsCreatedIsDeletedIsModifiedTest extends ChangeSummaryTestCases {
    public ChangeSummaryIsCreatedIsDeletedIsModifiedTest(String name) {
        super(name);
    }
    
     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryIsCreatedIsDeletedIsModifiedTest" };
        TestRunner.main(arguments);
    }

    // purpose: after logging, create a new DataObject, then check it with isCreated()
    public void testIsCreated() {
        changeSummary.beginLogging();
        SDODataObject newObj = (SDODataObject)root.createDataObject(rootProperty);
        assertCreated(newObj, changeSummary);
    }

    // purpose: check a DataObject not created by any DataObject in the tree with isCreated()
    public void testIsCreatedWithValueNotInTree() {
        changeSummary.beginLogging();
        SDODataObject newObj = (SDODataObject)dataFactory.create(rootType);
        assertUnchanged(newObj, changeSummary);
    }

    // purpose: check null with isCreated()
    public void testIsCreatedWithNull() {
        changeSummary.beginLogging();
        SDODataObject newObj = null;
        assertUnchanged(newObj, changeSummary);
    }

    // purpose: after logging, delete a in tree DataObject, then check it with isDeleted()
    public void testIsDeleted() {
        changeSummary.beginLogging();
        containedDataObject.delete();
        assertDeleted(containedDataObject, changeSummary);
    }

    // purpose: after logging, check a DataObject still in the tree with isDeleted()
    public void testIsDeletedWihValueInTreeStill() {
        changeSummary.beginLogging();
        assertUnchanged(containedDataObject, changeSummary);
    }

    // purpose: check a DataObject not in tree before and after logging with isDeleted()
    public void testIsDeltedWithValueNotInTreeBeforeAfterLogging() {
        changeSummary.beginLogging();

        SDODataObject newObj = (SDODataObject)dataFactory.create(rootType);
        assertUnchanged(containedDataObject, changeSummary);
    }

    // purpose: check null with isCreated()
    public void testIsDeletedWithNull() {
        changeSummary.beginLogging();
        SDODataObject newObj = null;
        assertUnchanged(containedDataObject, changeSummary);
    }

    // purpose: after logging, dataobject create dataobject
    public void testIsModifiedFactory() {
        changeSummary.beginLogging();
        SDODataObject o;//= new SDODataObject();
        SDOType ty = new SDOType("newTypeUri", "newType");
        ty.setOpen(true);
        SDOProperty p = new SDOProperty(aHelperContext);
        p.setName("new Property");
        p.setContainment(false);
        p.setType(SDOConstants.SDO_STRING);
        ty.addDeclaredProperty(p);

        o = (SDODataObject)root.createDataObject(rootProperty, ty);

        assertCreated(o, changeSummary);
    }

    // purpose: ChangeSummary usecase 1: DataFactory create dataObject, see changesummary doc. issue number 9
    public void testIsModified() {
        changeSummary.beginLogging();
        SDODataObject o;//= new SDODataObject();
        SDOType ty = new SDOType("newTypeUri", "newType");
        ty.setOpen(true);
        SDOProperty p = new SDOProperty(aHelperContext);
        p.setName("new Property");
        p.setContainment(false);
        p.setType(SDOConstants.SDO_STRING);
        ty.addDeclaredProperty(p);

        o = (SDODataObject)dataFactory.create(ty);

        root.set(rootProperty, o);

        assertCreated(o, changeSummary);
    }

    // purpose: detach a dataobject from one tree and set it to another tree, see changesummary doc. issue number 9
    public void testIsModifiedMoveDataObjectFromOneTreeToAnother() {
        changeSummary.beginLogging();

        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        SDODataObject o;//= new SDODataObject();
        SDOType ty = new SDOType("newTypeUri", "newType");
        ty.setOpen(true);
        SDOProperty p = new SDOProperty(aHelperContext);
        p.setName("new Property");
        p.setContainment(true);
        p.setType(dataObjectType);
        ty.addDeclaredProperty(p);
        SDOProperty p1 = new SDOProperty(aHelperContext);
        p1.setName("new Property ChangeSummary");
        p1.setContainment(false);
        p1.setType(changeSummaryType);
        ty.addDeclaredProperty(p1);

        o = (SDODataObject)dataFactory.create(ty);
        o.getChangeSummary().beginLogging();

        containedDataObject.detach();
        assertDetached(containedDataObject, changeSummary, true);// container and cs will not be set

        o.set(p, containedDataObject);

        assertCreated(containedDataObject, o.getChangeSummary());
        assertModified(o, o.getChangeSummary());
        assertDetached(containedDataObject, changeSummary, false);// container and cs will be set
        assertModified(root, changeSummary);

    }

    public void testIsModifiedWithValueInTree() {
        root.set(rootProperty1, "test");
        changeSummary.beginLogging();
        root.set(rootProperty1, "test1");
        assertModified(root, changeSummary);
    }

    // purpose: check null with isModified()
    public void testIsModifiedWithNull() {
        changeSummary.beginLogging();
        SDODataObject newObj = null;
        assertUnchanged(newObj, changeSummary);
    }

    public void testEndLogging() {
        changeSummary.beginLogging();
        //changeSummary.setChangedDataObjectList(null);
        changeSummary.endLogging();
        this.assertFalse(changeSummary.isLogging());
    }
}
