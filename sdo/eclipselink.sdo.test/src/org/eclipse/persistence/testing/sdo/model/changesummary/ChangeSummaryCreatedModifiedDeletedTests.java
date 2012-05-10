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

import commonj.sdo.DataObject;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOSetting;
import org.eclipse.persistence.sdo.SDOType;

public class ChangeSummaryCreatedModifiedDeletedTests extends ChangeSummaryCreatedModifiedDeletedTestCase {
    public ChangeSummaryCreatedModifiedDeletedTests(String name) {
        super(name);
    }
    
    

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryCreatedModifiedDeletedTests" };
        TestRunner.main(arguments);
    }

    // Failure condition test cases
    public void testGetFunctionsWithNullParameters() {
        buildTree();
        changeSummaryB = (SDOChangeSummary)dataObjectB.getChangeSummary();
        changeSummaryC = (SDOChangeSummary)dataObjectC.getChangeSummary();
        changeSummaryB.beginLogging();
        changeSummaryC.beginLogging();

        // check a null DO call
        assertFalse(changeSummaryB.isDeleted(null));
        assertFalse(changeSummaryB.isCreated(null));
        assertFalse(changeSummaryB.isModified(null));

        assertEquals(new ArrayList(), changeSummaryB.getOldValues(null));

        assertNull(changeSummaryB.getOldValue(null, null));
        assertNull(changeSummaryB.getOldContainer(null));
        assertNull(changeSummaryB.getOldContainmentProperty(null));
        //assertNull(changeSummaryB.getOldSequence(null));        
    }

    public void testSetFunctionsWithNullParameters() {
        //commented out this test case because it doesn't really make sense

        /* buildTree();
         changeSummaryB = (SDOChangeSummary)dataObjectB.getChangeSummary();
         changeSummaryC = (SDOChangeSummary)dataObjectC.getChangeSummary();
         changeSummaryB.beginLogging();
         changeSummaryC.beginLogging();

         // these operations are not really public and are set from resetChanges
         changeSummaryB.setOldContainer(null, null);// HashMap allows null:null
         assertNull(changeSummaryB.getOldContainer(null));

         changeSummaryB.setOldContainer(null, root);// HashMap allows null:null
         // even though we get null=root, the get function always returns null for non-do not in deleted|modified state
         assertEquals(null, changeSummaryB.getOldContainer(null));
         */
    }

    
    //
    public void testIsCreatedAfterMoveDataObjectFromOneOwnerToAnotherSameChangeSummary() {
        rootType.addDeclaredProperty(rootChangeSummaryProperty);
        root = (SDODataObject)dataFactory.create(rootType);

        changeSummaryA = (SDOChangeSummary)root.getChangeSummary();

        buildTreeWithoutChildChangeSummaries();

        changeSummaryA.beginLogging();
        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        assertEquals(0, changeSummaryA.getChangedDataObjects().size());

        assertNotNull(dataObjectD.getChangeSummary());        
        dataObjectD.detach();
        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(dataObjectD, true);
        

        assertEquals(2, changeSummaryA.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryA);
        assertModified(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertDetached(dataObjectD, changeSummaryA);

        // move D as child of C
        dataObjectC.set(propertyC, dataObjectD);

        // the 3 normally modified-c, modifed-b, deleted-d/created-d flags are reduced to 
        // 2 modified flags on c and b because created-d cancels deleted-d
        assertEquals(2, changeSummaryA.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryA);
        assertModified(dataObjectB, changeSummaryA);
        assertModified(dataObjectC, changeSummaryA);
        //assertModified(dataObjectD, changeSummaryA);
        //TODO: A move in a single CS is a delete and create but together they cancel
        // the question is do we include the oldContainer change in the change scope
        // (with scope limited to the created|modified|deleted sets)
        // Spec value
        // A: 20070106: Yes we keep oldSettings in the old cs - currently we do not cancel out operations
        // unset + (re)set != no changes
        //assertUnchanged(dataObjectD, changeSummaryA);
        
        assertFalse(changeSummaryA.isCreated(dataObjectD));
        assertFalse(changeSummaryA.isModified(dataObjectD));
        assertFalse(changeSummaryA.isDeleted(dataObjectD));

        // current value (wrong)
        //assertDeleted(dataObjectD, changeSummaryA);
        // with oldContainer change in the change scope
        //assertModified(dataObjectD, changeSummaryA);        
    }

    public void testIsCreatedAfterMoveDataObjectFromOneOwnerToAnotherDiffChangeSummary() {
        buildTree();
        changeSummaryB = (SDOChangeSummary)dataObjectB.getChangeSummary();
        changeSummaryC = (SDOChangeSummary)dataObjectC.getChangeSummary();
        changeSummaryB.beginLogging();
        changeSummaryC.beginLogging();

        assertEquals(0, changeSummaryB.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertUnchanged(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertUnchanged(dataObjectD, changeSummaryB);

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        assertNotNull(dataObjectD.getChangeSummary());        
        dataObjectD.detach();
        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(dataObjectD, true);

        assertEquals(2, changeSummaryB.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);

        assertDetached(dataObjectD, changeSummaryB);
        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);
        dataObjectC.set(propertyC, dataObjectD);

        assertEquals(2, changeSummaryB.getChangedDataObjects().size());
        assertEquals(2, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertDetached(dataObjectD, changeSummaryB, false); // set after a detach will result in a non-null container
        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertModified(dataObjectC, changeSummaryC);
        assertCreated(dataObjectD, changeSummaryC);

    }

    // verify recursive delete sets isSet properly
    public void testOldSettingsAfterInternalDeleteOfLevel2of4AfterLoggingOn() {
        buildTreeWith4LevelsOfProperties();
        changeSummaryB = (SDOChangeSummary)dataObjectB.getChangeSummary();
        changeSummaryB.beginLogging();
        
        assertNotNull(dataObjectD.getChangeSummary());        
        dataObjectD.delete();
        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(dataObjectD, true);
        
        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        // both d and child e are deleted (recursively)
        assertDeleted(dataObjectD, changeSummaryB);
        assertDeleted(dataObjectE, changeSummaryB);
        assertEquals(1, changeSummaryB.getOldValues(dataObjectB).size());
        assertTrue(changeSummaryB.getOldValue(dataObjectB, propertyB).isSet());        
        DataObject deepCopyD = (DataObject)changeSummaryB.getDeepCopies().get(dataObjectD);
        assertTrue(changeSummaryB.getOldValue(dataObjectB, propertyB).getValue().equals(deepCopyD));
        assertTrue(changeSummaryB.getOldContainer(dataObjectD).equals(dataObjectB));
        assertTrue(changeSummaryB.getOldContainmentProperty(dataObjectD).equals(propertyB));

        //assertEquals(1, dataObjectD.getOldSettings().size());
        //assertEquals(1, changeSummaryB.dataObjectD.getOldSettings().size());        
        SDOSetting aSetting = (SDOSetting)changeSummaryB.getOldValue(dataObjectD, propertyD);
        assertTrue(aSetting.isSet());
        DataObject deepCopyE = (DataObject)changeSummaryB.getDeepCopies().get(dataObjectE);
        assertTrue(aSetting.getValue().equals(deepCopyE));
    }

    // perform a move of a cs root (delete CCB) to another cs as child  and observe the old* instance variables
    // TODO: THIS NEEDS TO BE VERIFIED WITH SPEC    
    //public void testOldSettingsAfterMoveCSDataObjectFromOneOwnerToAnotherDiffChangeSummary() {
    public void testDetachCSRoot() {
        buildTree();
        changeSummaryB = (SDOChangeSummary)dataObjectB.getChangeSummary();
        changeSummaryC = (SDOChangeSummary)dataObjectC.getChangeSummary();
        changeSummaryB.beginLogging();
        changeSummaryC.beginLogging();

        assertEquals(0, changeSummaryB.getChangedDataObjects().size());
        assertEquals(0, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertUnchanged(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertUnchanged(dataObjectD, changeSummaryB);

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        // oldSettings should not be set
        checkOldSettingsSizeTree("0000", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);
        checkOldSettingsSizeTree("0000", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope

        assertNotNull(dataObjectB.getChangeSummary());
        dataObjectB.detach();
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(dataObjectB, false);
        
        assertNotNull(dataObjectB.getChangeSummary());
        assertNotNull(dataObjectD.getChangeSummary());
        assertEquals(dataObjectB.getChangeSummary(),dataObjectD.getChangeSummary());
                
        assertEquals(0, changeSummaryB.getChangedDataObjects().size());// was 0
        assertEquals(0, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertUnchanged(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertUnchanged(dataObjectD, changeSummaryB);// unchanged

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        // oldSettings 
        checkOldSettingsSizeTree("0000", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);// 0000
        checkOldSettingsSizeTree("0000", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope      
    }

     public void testDeleteCSRoot() {
        buildTree();
        changeSummaryB = (SDOChangeSummary)dataObjectB.getChangeSummary();
        changeSummaryC = (SDOChangeSummary)dataObjectC.getChangeSummary();
        changeSummaryB.beginLogging();
        changeSummaryC.beginLogging();

        assertEquals(0, changeSummaryB.getChangedDataObjects().size());
        assertEquals(0, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertUnchanged(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertUnchanged(dataObjectD, changeSummaryB);

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        // oldSettings should not be set
        checkOldSettingsSizeTree("0000", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);
        checkOldSettingsSizeTree("0000", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope

        assertNotNull(dataObjectB.getChangeSummary());
        dataObjectB.delete();
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(dataObjectB, false); 
        
        assertEquals(2, changeSummaryB.getChangedDataObjects().size());// 1
        assertEquals(0, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertDeleted(dataObjectD, changeSummaryB);// unchanged

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        // oldSettings 
        checkOldSettingsSizeTree("0101", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);// 0000
        checkOldSettingsSizeTree("0000", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope      
    }

    // perform a move of a cs root (delete CCB) to another cs as child  and observe the old* instance variables
    public void testOldSettingsAfterMoveCSDataObjectFromOneOwnerToAnotherDiffChangeSummaryByUnset() {
        buildTree();
        changeSummaryB = (SDOChangeSummary)dataObjectB.getChangeSummary();
        changeSummaryC = (SDOChangeSummary)dataObjectC.getChangeSummary();
        changeSummaryB.beginLogging();
        changeSummaryC.beginLogging();

        assertEquals(0, changeSummaryB.getChangedDataObjects().size());
        assertEquals(0, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertUnchanged(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertUnchanged(dataObjectD, changeSummaryB);

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        // oldSettings should not be set
        checkOldSettingsSizeTree("0000", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);
        checkOldSettingsSizeTree("0000", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope

        assertNotNull(dataObjectD.getChangeSummary());
        // remove leaf from CS-B off of B
        dataObjectB.unset(propertyB);
        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(dataObjectB, false);
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(dataObjectD, true);

        assertEquals(2, changeSummaryB.getChangedDataObjects().size());
        assertEquals(0, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertDetached(dataObjectD, changeSummaryB);

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        // oldSettings 
        checkOldSettingsSizeTree("0101", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);
        checkOldSettingsSizeTree("0000", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope

        // add leaf (move) to CS-C off of C
        dataObjectC.set(propertyC, dataObjectD);

        assertEquals(2, changeSummaryB.getChangedDataObjects().size());
        assertEquals(2, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertDetached(dataObjectD, changeSummaryB, false); // set after a detach will result in a non-null container

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertModified(dataObjectC, changeSummaryC);
        assertCreated(dataObjectD, changeSummaryC);// set propagates recursively

        // oldSettings
        checkOldSettingsSizeTree("0101", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);
        checkOldSettingsSizeTree("0010", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope
    }

    // perform a move between changeSummaries and observe the old* instance variables
    public void testOldSettingsAfterMoveDataObjectFromOneOwnerToAnotherDiffChangeSummary() {
        buildTree();
        changeSummaryB = (SDOChangeSummary)dataObjectB.getChangeSummary();
        changeSummaryC = (SDOChangeSummary)dataObjectC.getChangeSummary();
        changeSummaryB.beginLogging();
        changeSummaryC.beginLogging();

        assertEquals(0, changeSummaryB.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertUnchanged(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertUnchanged(dataObjectD, changeSummaryB);

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        // oldSettings should not be set
        checkOldSettingsSizeTree("0000", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);
        checkOldSettingsSizeTree("0000", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope

        // remove leaf from CS-B off of B
        assertNotNull(dataObjectD.getChangeSummary());
        dataObjectD.detach();
        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(dataObjectD, true);

        assertEquals(2, changeSummaryB.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertDetached(dataObjectD, changeSummaryB);

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        // oldSettings 
        // not in changeSummary        
        checkOldSettingsSizeTree("0101", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);// 0100
        checkOldSettingsSizeTree("0000", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope

        // add leaf (move) to CS-C off of C
        dataObjectC.set(propertyC, dataObjectD);

        assertEquals(2, changeSummaryB.getChangedDataObjects().size());
        assertEquals(2, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertDetached(dataObjectD, changeSummaryB, false); // set after a detach will result in a non-null container

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertModified(dataObjectC, changeSummaryC);
        assertCreated(dataObjectD, changeSummaryC);

        // oldSettings
        checkOldSettingsSizeTree("0101", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);// 1000
        checkOldSettingsSizeTree("0010", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope

    }

    public void testIsCreatedAfterUseDataFactory() {
        rootType.addDeclaredProperty(rootChangeSummaryProperty);
        root = (SDODataObject)dataFactory.create(rootType);
        changeSummaryA = (SDOChangeSummary)root.getChangeSummary();

        buildTreeWithoutChildChangeSummaries();

        changeSummaryA.beginLogging();

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        SDOType type_E = new SDOType("EURI", "E");
        SDODataObject dataObjectE = (SDODataObject)dataFactory.create(type_E);
        dataObjectC.set(propertyC, dataObjectE);

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertModified(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);
        assertCreated(dataObjectE, changeSummaryA);

    }

    // purpose: verify that the CS is cleared 
    public void testUnsetChildOfCS() {
        rootType.addDeclaredProperty(rootChangeSummaryProperty);
        root = (SDODataObject)dataFactory.create(rootType);
        changeSummaryA = (SDOChangeSummary)root.getChangeSummary();
        buildTreeWithoutChildChangeSummaries();
        changeSummaryA.beginLogging();

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        assertNotNull(dataObjectD.getChangeSummary());        
        dataObjectB.unset(propertyB); // unset D
        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(dataObjectD, true);       

        assertUnchanged(root, changeSummaryA);
        assertModified(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertDetached(dataObjectD, changeSummaryA);
    }
    
    public void testIsCreatedAfterUseUnset() {
        rootType.addDeclaredProperty(rootChangeSummaryProperty);
        root = (SDODataObject)dataFactory.create(rootType);
        changeSummaryA = (SDOChangeSummary)root.getChangeSummary();
        buildTreeWithoutChildChangeSummaries();
        changeSummaryA.beginLogging();

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        assertNotNull(dataObjectD.getChangeSummary());
        dataObjectB.unset(propertyB);
        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(dataObjectB, false);       

        assertUnchanged(root, changeSummaryA);
        assertModified(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertDetached(dataObjectD, changeSummaryA);
    }

    public void testIsCreatedAfterUnSetDiffChangeSummary() {
        root = (SDODataObject)dataFactory.create(rootType);
        changeSummaryA = (SDOChangeSummary)root.getChangeSummary();
        assertNull(changeSummaryA);
        buildTree();
        changeSummaryB = (SDOChangeSummary)dataObjectB.getChangeSummary();
        changeSummaryC = (SDOChangeSummary)dataObjectC.getChangeSummary();
        changeSummaryB.beginLogging();
        changeSummaryC.beginLogging();

        assertUnchanged(root, changeSummaryB);
        assertUnchanged(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertUnchanged(dataObjectD, changeSummaryB);
        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        dataObjectB.unset(propertyB);
        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(dataObjectB, false);

        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertDetached(dataObjectD, changeSummaryB);
        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        dataObjectC.set(propertyC, dataObjectD);
        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertDetached(dataObjectD, changeSummaryB, false); // set after a detach will result in a non-null container
        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertModified(dataObjectC, changeSummaryC);
        assertCreated(dataObjectD, changeSummaryC);
    }
}
