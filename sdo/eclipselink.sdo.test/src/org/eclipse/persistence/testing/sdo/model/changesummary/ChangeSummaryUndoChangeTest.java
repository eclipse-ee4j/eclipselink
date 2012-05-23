/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import commonj.sdo.DataObject;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.DefaultValueStore;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.ValueStore;

public class ChangeSummaryUndoChangeTest extends ChangeSummaryTestCases {
    protected static final String PROPERTY_NAME_LIST = "list";
    protected static final String PROPERTY_LIST_PATH = "list";
    protected static final String TYPENAME_LIST = "TypeNameList";
    protected static final String URINAME = "uri";
	
    public ChangeSummaryUndoChangeTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryUndoChangeTest" };
        TestRunner.main(arguments);
    }
    
    // purpose: after logging, and nothing is changed, then undoChange(), 
    // dataobject should have same setting as before
    public void testUndoChangeWhenNothingHappened() {
        SDODataObject original = (SDODataObject)copyHelper.copy(root);
        changeSummary.beginLogging();
        // change nothing
        changeSummary.undoChanges();
        assertTrue(equalityHelper.equal(original, root));
    }

    // purpose: after logging, change property value, and undoChange() should bring the 
    // original value back to property
    public void testUndoChangeAfterChangeDataObjectContainer() {
        SDODataObject original = (SDODataObject)copyHelper.copy(root);
        changeSummary.beginLogging();// logging
        // verify original VS is null and save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStore = root._getCurrentValueStore();
        assertNotNull(aCurrentValueStore);
        ValueStore anOriginalValueStore = (ValueStore)changeSummary.getOriginalValueStores().get(root);
        assertNull(anOriginalValueStore);        

        assertNotNull(containedDataObject.getChangeSummary());        
        root.unset(rootProperty);// unset property
        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor((DataObject)containedDataObject, true);
        
        assertNotNull(aCurrentValueStore);
        ValueStore anOriginalValueStoreAfterOperation =(ValueStore)changeSummary.getOriginalValueStores().get(root);
        ValueStore aCurrentValueStoreAfterOperation = root._getCurrentValueStore();        
        assertNotNull(anOriginalValueStoreAfterOperation);        
        assertNotNull(aCurrentValueStoreAfterOperation); 
        assertTrue(anOriginalValueStoreAfterOperation == aCurrentValueStore);

        assertFalse(root.isSet(rootProperty));
        assertNull((SDODataObject)containedDataObject.getContainer());// make sure it is changed
        // undo and verify equality
        assertUndoChangesEqualToOriginal(changeSummary, root, original);
        
        // verify that property is reset
        assertTrue(root.isSet(rootProperty));
        // we have object identity
        assertTrue(equalityHelper.equal(original, root));
        
        ValueStore anOriginalValueStoreAfterUndo = (ValueStore)changeSummary.getOriginalValueStores().get(root);
        ValueStore aCurrentValueStoreAfterUndo = root._getCurrentValueStore();        
        assertNull(anOriginalValueStoreAfterUndo);        
        assertNotNull(aCurrentValueStoreAfterUndo);
        // we return the original value store back to the current VS
        assertTrue(aCurrentValueStoreAfterUndo == aCurrentValueStore);

    }

    public void testUndoDetachComplexAtRoot() {
        SDODataObject original = (SDODataObject)copyHelper.copy(root);
        changeSummary.beginLogging();// logging
        // verify original VS is null and save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStore = root._getCurrentValueStore();
        assertNotNull(aCurrentValueStore);
        ValueStore anOriginalValueStore = (ValueStore)changeSummary.getOriginalValueStores().get(root);
        assertNull(anOriginalValueStore);        
        // save original child
        SDODataObject aChild = (SDODataObject)root.get("property-Containment");

        assertNotNull(aChild.getChangeSummary());        
        // operation on complex child of root
        aChild.detach();
        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor((DataObject)aChild, true);
        
        
        assertNotNull(aCurrentValueStore);
        ValueStore anOriginalValueStoreAfterOperation = (ValueStore)changeSummary.getOriginalValueStores().get(root);
        ValueStore aCurrentValueStoreAfterOperation = root._getCurrentValueStore();        
        assertNotNull(anOriginalValueStoreAfterOperation);        
        assertNotNull(aCurrentValueStoreAfterOperation); 
        assertTrue(anOriginalValueStoreAfterOperation == aCurrentValueStore);

        assertFalse(root.isSet(rootProperty));
        assertNull((SDODataObject)containedDataObject.getContainer());// make sure it is changed
        
        // undo and verify equality
        assertUndoChangesEqualToOriginal(changeSummary, root, original);

        // verify that property is reset
        assertTrue(root.isSet(rootProperty));
        // we have object identity
        assertTrue(equalityHelper.equal(original, root));
        
        ValueStore anOriginalValueStoreAfterUndo = (ValueStore)changeSummary.getOriginalValueStores().get(root);
        ValueStore aCurrentValueStoreAfterUndo = root._getCurrentValueStore();        
        assertNull(anOriginalValueStoreAfterUndo);        
        assertNotNull(aCurrentValueStoreAfterUndo);
        // we return the original value store back to the current VS
        assertTrue(aCurrentValueStoreAfterUndo == aCurrentValueStore);
    }
    
    public void testObjectIdentityAfterUndoChangeAfterModifyingSimpleSingleOffRootWithCSonRoot() {
    	// setup simple property
    	// save a copy of the property
    	// track changes
    	// unset the property
    	// undo changes
    	// verify property is reset
    	// verify object value equality
    	
    }
    
}
