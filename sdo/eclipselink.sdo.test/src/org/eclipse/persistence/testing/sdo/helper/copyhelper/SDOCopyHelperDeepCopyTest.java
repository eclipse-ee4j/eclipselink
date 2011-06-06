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
/*
   DESCRIPTION
    Test SDOCopyHelper for deep and shallow copy
    References:
      SDO59-DeepCopy.doc
      SDO_Ref_BiDir_Relationships_DesignSpec.doc

   PRIVATE CLASSES

   NOTES

   MODIFIED    (MM/DD/YY)   
    mfobrien    05/16/07 - 
    dmahar      04/23/07 - 
    mfobrien    07/31/06 - As part of the refactor of SDODataObject.properties <Map> into
                                     the ValueStore interfaces, these tests require non-Map specific tests.
                                     Currently testing is hardcoded to additional values() and keySet() functions
                                     on our RI MapValueStore
    dmahar      08/18/06 -
    mfobrien    08/19/06 - Remove special handling - getProperties() has returned as a Pluggable interface
    mfobrien    09/09/06 - Add bidirectional support to SDOCopyHelper - see SDO57/59
    mfobrien    09/18/06 - Subclass off common copy/equality testCases model
                                   - Add bidirectional testcases for the following cases
                                   - verify all properties that are bi-directional are type.dataType=false - spec 8.4
                                   - test bi-directional with one end at containment=true - spec 8.4
                                   - test bi-directional where both ends have the same value for readOnly = true - spec 8.4
                                   - test bi-directional where both ends have the same value for readOnly = false - spec 8.4
                                   - test bi-directional with containment where the non-containment Property has many=false. - spec 8.4
    mfobrien    03/19/07 - 5853175: bidirectional link between disjoint trees (as a result of an unset on one side)
                                   are spec compliant and will result in inequality between an original and copy of the root

 */
package org.eclipse.persistence.testing.sdo.helper.copyhelper;

import commonj.sdo.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.sdo.helper.SDOCopyHelper;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.testing.sdo.helper.SDOCopyEqualityHelperTestCases;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;

public class SDOCopyHelperDeepCopyTest extends SDOCopyEqualityHelperTestCases {
    public SDOCopyHelperDeepCopyTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SDOCopyHelperDeepCopyTest.class);
    }

    private int getPropertiesSize(SDODataObject anObject) {
        int size = 0;
        for (int i = 0; i < anObject.getInstanceProperties().size(); i++) {
            Property next = (Property)anObject.getInstanceProperties().get(i);
            if (anObject.isSet(next)) {
                size++;
            }
        }
        return size;
    }

    public SDOChangeSummary getChangeSummary() {
        return null;
    }

    private Set getPropertiesKeySet(SDODataObject anObject) {
        HashSet aSet = new HashSet();
        if (anObject != null) {
            for (int i = 0; i < anObject.getType().getProperties().size(); i++) {
                //boolean isSet = anObject.getTypePropertiesIsSetStatus()[i];
                boolean isSet = anObject.isSet(i);
                if (isSet) {
                    aSet.add(((Property)anObject.getType().getProperties().get(i)).getName());
                }
            }
            for (int i = 0; i < anObject._getOpenContentProperties().size(); i++) {
                aSet.add(((Property)anObject._getOpenContentProperties().get(i)).getName());
            }
            return aSet;
        } else {
            return new HashSet();
        }
    }

    /**
     * Spec 8.4 "Properties that are bi-directional require that no more than one end has containment=true"
     * @param oppositeProperty1
     * @param oppositeProperty2
     * @return
     */
    private boolean areBothBidirectionalOppositePropertiesContainmentTrue(//
    SDOProperty oppositeProperty1, SDOProperty oppositeProperty2) {
        return oppositeProperty1.isContainment() && oppositeProperty2.isContainment();
    }


    /**
     * Test whether EqualityHelper.compareProperty() handles nested objects that are null but isSet=true
     * (Where we also have a bidirectional child property)
     * See SDOEqualityHelperTest for the same tests without the bidirectional child
     * 
     * Disjoint trees can maintain a bidirectional link
     * 
     * Results: This test will verify that the bidirectional property is left unchanged 
     * after an unset on its parent - in effect creating a nc link between 2 separate dataobject trees.
     * See SDO_Ref_BiDir_Relationships_DesignSpec.doc section 4.2.2
     * 	    See spec 3.1.7. If other DataObjects have one-way, non-containment properties that refer to deleted DataObjects, 
     * then these references are not modified. - we retain both sides of the bidirectional link here.
     * 	    See spec 3.1.7. However, these properties can need changing to other values, 
     * in order to restore closure to the data graph? - the implementer will need to be aware and fix any set/reset issues 
     * - the SDO API will not attempt to remove any of these NC references for deleted objects.
     *     No code changes are required because non-equality is expected in this case. 
     *    	Leaving a bidirectional property set between two trees that do not share a common root is not an invalid state (step 2 above)
     * 
     * root
     *   rp2 -> cdo (unset this one - placing it outside the copy tree)
     *                 cp2 -> cbcdo
     *                                cbcp1 -> cdo1 (bidir)     
     *                                c1p3 -> cdo1 (bidir)     
     *                 cp3 -> cbcdo3
     *                 cpcs -> cs
     *   rp3 -> cdo1
     *                 c1p1 -> cbcdo (bidir)
     *   rp4 -> lw
     */
    public void testDeepCopyAfterUnsetComplexChildWithBidirectionalChild_generatingLinkedDisjointTrees() {
    	int aRootsize = preOrderTraversalDataObjectList(root).size();
    	assertEquals(7, aRootsize);
    	
    	// save cdo before we unset it
    	DataObject aCDO = (DataObject)root.get("rootproperty2-notdatatype");
    	assertNotNull(aCDO);
    	int aCDOsize = preOrderTraversalDataObjectList((SDODataObject)aCDO).size();
    	// save child of cdo
    	DataObject aCDO_CBCDO = (DataObject)aCDO.get("containedProperty2-notdataType");
    	assertNotNull(aCDO_CBCDO);    	
    	int aCDO_CBCDOsize = preOrderTraversalDataObjectList((SDODataObject)aCDO_CBCDO).size();
    	// save bidirectional partner of cdo    	
    	DataObject aCDO1 = (DataObject)root.get("rootproperty3-notdatatype");
    	assertNotNull(aCDO1);    	
    	int aCDO1size = preOrderTraversalDataObjectList((SDODataObject)aCDO1).size();
    	
    	// get a list of nodes (NC references do not recursively iterate)
    	List<DataObject> rootPreOrderListBeforeUnset = preOrderTraversalDataObjectList(root);
    	assertNotNull(rootPreOrderListBeforeUnset);
    	assertEquals(aRootsize, rootPreOrderListBeforeUnset.size());
    	rootPreOrderListBeforeUnset.contains(aCDO_CBCDO);
    	rootPreOrderListBeforeUnset.contains(aCDO1);
    	
    	
        // NOTE: unset does not affect bidirectional properties between disjoint trees of dataObjects
    	// the remaining unidirectional property is left unaffected.
        root.unset("rootproperty2-notdatatype");
        
        
        // verify that bidirectional property is still set
        DataObject aCDO1_CBCDO = (DataObject)aCDO1.get("contained1Property1-notdataType");
        assertNotNull(aCDO1_CBCDO);
        assertEquals(aCDO1_CBCDO, aCDO_CBCDO);
        DataObject aCBCDO_CDO1 = (DataObject)aCDO_CBCDO.get("containedByContainedProperty1-notdataType");
        assertNotNull(aCBCDO_CDO1);
        assertEquals(aCBCDO_CDO1, aCDO1);
        
    	// get a list of nodes (NC references do not recursively iterate)
    	List<DataObject> rootPreOrderListAfterUnset = preOrderTraversalDataObjectList(root);
    	assertNotNull(rootPreOrderListAfterUnset);
    	// 4 not 3 because we shallow iterate to the non-contaiment property c1p1
    	assertEquals(aRootsize - aCDOsize, rootPreOrderListAfterUnset.size());
    	rootPreOrderListAfterUnset.contains(aCDO_CBCDO);
    	rootPreOrderListAfterUnset.contains(aCDO1);

    	// get a list of nodes (NC references do not recursively iterate)
    	List<DataObject> cdoPreOrderListAfterUnset = preOrderTraversalDataObjectList((SDODataObject)aCDO);
    	assertNotNull(cdoPreOrderListAfterUnset);
    	// 4 not 3 because we shallow iterate to the non-contaiment property c1p1
    	assertEquals(aCDOsize, cdoPreOrderListAfterUnset.size());
    	cdoPreOrderListAfterUnset.contains(aCDO_CBCDO);
    	
        SDODataObject copyOfRoot = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());
        assertFalse(root.isSet("rootproperty2-notdatatype"));
        assertNotNull(copyOfRoot);
        
    	DataObject aCDOc = (DataObject)copyOfRoot.get("rootproperty2-notdatatype");
    	assertNull(aCDOc);
    	DataObject aCDO1c = (DataObject)copyOfRoot.get("rootproperty3-notdatatype");
    	assertNotNull(aCDO1c);
    	int aCDO1sizec = preOrderTraversalDataObjectList((SDODataObject)aCDO1c).size();
    	// 2 not 1 because the bidirectional property was not copied
    	assertEquals(1, aCDO1sizec);
        
        // test copy helper results outside of equality helper (to keep equality helper issues outside of copyhelper testing)
        // get non-containment property between original cdo1 and unset cdo/cbcdo
        // verify that bidirectional property is not set on copy
        DataObject aCDO1_CBCDOc = (DataObject)aCDO1c.get("contained1Property1-notdataType");
        assertNull(aCDO1_CBCDOc);
        
        // lastly use equality helper to test copy helper
        assertFalse(equalityHelper.equal(root, copyOfRoot));
        
        // we need a diff function on cs or equalityHelper to show us that aCDO (copy) is not on copyOfRoot
    }

    // see same comments for testDeepCopyAfterUnsetComplexChildWithBidirectionalChild_generatingLinkedDisjointTrees() above 
    public void testDeepCopyAfterSetNullComplexChildWithBidirectionalChild_generatingLinkedDisjointTrees() {
        // clear complex child
        root.set("rootproperty2-notdatatype", null);
        SDODataObject copyOfRoot = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());
        assertFalse(root.isSet("rootproperty2-notdatatype"));
        assertNotNull(copyOfRoot);
        // this assertion previously failed before fix for #5852525    	
        assertFalse(equalityHelper.equal(root, copyOfRoot));
    }
    
    // test R21 p44	3.9.4	For each Property where property.getType().isDataType() is false 
    // the value is copied if it is a DataObject contained by the source dataObject.
    public void testR21P44Sect3_9_4_NonContainedDOsAreNotCopied() {

    }
    
    // test R22 p44	3.9.4	If a DataObject is outside the copy tree and the property is bidirectional then //
    // the DataObject is not copied and references to the object are also not copied.
    public void testR22P44Sect3_9_4_DOisOutsideCopyTreeWithBidirectionalPropertyNotCopied() {
        // cdo/cbcdo (depth2) and cdo1 (depth1) are bidirectional with each other
        // create copy of cdo1 and verify that cdo/cbcdo was not copied since it is outside the copy tree
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(containedDataObject, getChangeSummary());

        // verify that container is null
        assertNull(copy.getContainer());
        // get bidirectional node from the copy root
        SDODataObject bidirCopyViaCopyRoot = (SDODataObject)copy.get(//
        "containedProperty2-notdataType/containedByContainedProperty1-notdataType");
        assertNull(bidirCopyViaCopyRoot);
        // get bidirectional start node via opposite property
        SDODataObject bidirCopyStart = (SDODataObject)copy.get(//
        "containedProperty2-notdataType");
        assertNotNull(bidirCopyStart);
        // get property containing opposite property link (inside copy tree)
        SDOProperty bidirCopyOppositeStartProp = (SDOProperty)bidirCopyStart.getType().getProperty(//
        "containedByContainedProperty1-notdataType");
        assertNotNull(bidirCopyOppositeStartProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(bidirCopyOppositeStartProp.getType().isDataType());
        // get bidir property outside of copy tree
        SDOProperty bidirCopyViaOpposite = (SDOProperty)bidirCopyOppositeStartProp.getType()//
        .getProperty("contained1Property1-notdataType");
        assertNull(bidirCopyViaOpposite);
        SDODataObject bidirCopyViaOppositeDO = (SDODataObject)copy.get(//
        "rootproperty3-notdatatype/contained1Property1-notdataType");
        assertNull(bidirCopyViaOppositeDO);
    }
    
    // test R23 p44	3.9.4	If a DataObject is outside the copy tree and the property is unidirectional then //
    // the same DataObject is referenced.
    public void testR23P44Sect3_9_4_DOisOutsideCopyTreeWithUnidirectionalPropertyThenSameDOreferenced() {
    	
    }

    // we dont want changes to occur during the embedded set() calls in copy()
    // this test needs a deeper model in order to fully test the bug see ChangeSummaryXSDWithCSonChildUndoTestCases.testDeepCopyObjectWithCSLoggingOnDoesNotLogChangesInTheDeepCopy()
    public void testDeepCopyObjectWithCSLoggingOnDoesNotLogChangesInTheDeepCopy() {
        // verify logging is on
        assertNotNull(containedDataObject.getChangeSummary());
        assertTrue(containedDataObject.getChangeSummary().isLogging());
        //containedDataObject.getChangeSummary().endLogging();
        // take an object with CS on and deep copy it
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(containedDataObject, getChangeSummary());

        //containedDataObject.getChangeSummary().beginLogging();    	
        // verify that logging is still on
        assertTrue(containedDataObject.getChangeSummary().isLogging());
        assertTrue(copy.getChangeSummary().isLogging());
        ChangeSummary cs = copy.getChangeSummary();

        // verify that we have not logged changes during the copy
        assertEquals(0, cs.getChangedDataObjects().size());
    }
    
    
    // we dont want changes to occur during the embedded set() calls in copy()
    // #5878436 12-FEB-07 do not recurse into a non-containment relationship
    public void testNoInfiniteLoopTurningLoggingBackOnAfterADeepCopyObjectWithCSLoggingOff() {
        // verify logging is on
        assertNotNull(containedDataObject.getChangeSummary());
        assertTrue(containedDataObject.getChangeSummary().isLogging());
        containedDataObject.getChangeSummary().endLogging();
        // take an object with CS on and deep copy it
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(containedDataObject, getChangeSummary());
        containedDataObject.getChangeSummary().beginLogging();// infinite loop on resetChanges()    	
    }

    public void testUC010xDeepCopy1_1BidirectionalPropOutsideCopyTreeNotCopied() {
        // cdo/cbcdo (depth2) and cdo1 (depth1) are bidirectional with each other
        // create copy of cdo1 and verify that cdo/cbcdo was not copied since it is outside the copy tree
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(containedDataObject, getChangeSummary());

        // verify that container is null
        assertNull(copy.getContainer());
        // get bidirectional node from the copy root
        SDODataObject bidirCopyViaCopyRoot = (SDODataObject)copy.get(//
        "containedProperty2-notdataType/containedByContainedProperty1-notdataType");
        assertNull(bidirCopyViaCopyRoot);
        // get bidirectional start node via opposite property
        SDODataObject bidirCopyStart = (SDODataObject)copy.get(//
        "containedProperty2-notdataType");
        assertNotNull(bidirCopyStart);
        // get property containing opposite property link (inside copy tree)
        SDOProperty bidirCopyOppositeStartProp = (SDOProperty)bidirCopyStart.getType().getProperty(//
        "containedByContainedProperty1-notdataType");
        assertNotNull(bidirCopyOppositeStartProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(bidirCopyOppositeStartProp.getType().isDataType());
        // get bidir property outside of copy tree
        SDOProperty bidirCopyViaOpposite = (SDOProperty)bidirCopyOppositeStartProp.getType()//
        .getProperty("contained1Property1-notdataType");
        assertNull(bidirCopyViaOpposite);
        SDODataObject bidirCopyViaOppositeDO = (SDODataObject)copy.get(//
        "rootproperty3-notdatatype/contained1Property1-notdataType");
        assertNull(bidirCopyViaOppositeDO);
    }

    public void testUC030xDeepCopy1_1BidirectionalPropInsideCopyTreeCopiedContTreeSameAsCopyTree() {
        // cdo/cbcdo (depth2) and cdo1 (depth1) are bidirectional with each other
        // create copy of the root and verifyt that both cdo1 and cdo/cbcdo were both copied as they are inside the copy tree
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());

        // verify that container is null
        assertNull(copy.getContainer());
        // get bidirectional node from the copy root (direction 1)
        SDODataObject bidirCopyViaCopyRoot = (SDODataObject)copy.get(//
        "rootproperty2-notdatatype/containedProperty2-notdataType/containedByContainedProperty1-notdataType");
        assertNotNull(bidirCopyViaCopyRoot);
        // get bidirectional start node via opposite property
        SDODataObject bidirCopyStart = (SDODataObject)copy.get(//
        "rootproperty2-notdatatype/containedProperty2-notdataType");
        assertNotNull(bidirCopyStart);
        // verify changeSummary is set on opposites
        ChangeSummary aCS2 = bidirCopyStart.getChangeSummary();
        assertNotNull(aCS2);
        // get property containing opposite property link (inside copy tree)
        SDOProperty bidirCopyOppositeStartProp = (SDOProperty)bidirCopyStart.getType().getProperty(//
        "containedByContainedProperty1-notdataType");
        assertNotNull(bidirCopyOppositeStartProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(bidirCopyOppositeStartProp.getType().isDataType());
        // get bidir property inside copy tree
        SDOProperty oppositeEndProp = (SDOProperty)((SDODataObject)copy.get(//
        "rootproperty3-notdatatype")).getType().getProperty("contained1Property1-notdataType").getOpposite();
        assertNotNull(oppositeEndProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(oppositeEndProp.getType().isDataType());
        // get opposite DO (direction 2)
        SDODataObject bidirCopyViaOppositeDO = (SDODataObject)copy.get(//
        "rootproperty3-notdatatype/contained1Property1-notdataType");
        assertNotNull(bidirCopyViaOppositeDO);
        // verify changeSummary is set on opposites
        ChangeSummary aCS1 = bidirCopyViaOppositeDO.getChangeSummary();
        assertNotNull(aCS1);

        // Constraint C3 - Spec 3.9.4
        // verify there are no object pointers between source and copy (all objects are instance independent)
        // ie: a' != a  - or there are no references to "root" from "copy"
        assertNotSame((SDODataObject)root.get("rootproperty2-notdatatype/containedProperty2-notdataType"),//
                      (SDODataObject)copy.get("rootproperty2-notdatatype/containedProperty2-notdataType"));
        assertNotSame((SDODataObject)root.get("rootproperty3-notdatatype"),//
                      (SDODataObject)copy.get("rootproperty3-notdatatype"));
        assertNotSame((SDODataObject)root.get(//
        "rootproperty2-notdatatype/containedProperty2-notdataType/containedByContainedProperty1-notdataType"),//
                      (SDODataObject)copy.get("//" +//
                                              "rootproperty2-notdatatype/containedProperty2-notdataType/containedByContainedProperty1-notdataType"));

        // Spec 8.4 "Properties that are bi-directional require that no more than one end has containment=true"
        assertFalse(areBothBidirectionalOppositePropertiesContainmentTrue(//
        bidirCopyOppositeStartProp, oppositeEndProp));
    }

    public void testUC0302DeepCopy1_1BidirectionalPropInsideCopyTreeCopiedContTreeSameAsCopyTree() {
        // create copy of the root 
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(rootUC4, getChangeSummary());

        // verify that container is null
        assertNull(copy.getContainer());
        // get bidirectional node from the copy root (direction 1)
        SDODataObject bidirCopyViaCopyRoot = (SDODataObject)copy.get(//
        "rootHome/homeAddress/addressWork");
        assertNotNull(bidirCopyViaCopyRoot);
        // get bidirectional start node via opposite property
        SDODataObject bidirCopyStart = (SDODataObject)copy.get(//
        "rootHome/homeAddress");
        assertNotNull(bidirCopyStart);

        // verify changeSummary is set on opposites
        //ChangeSummary aCS2 = bidirCopyStart.getChangeSummary();
        //assertNotNull(aCS2);
        // get property containing opposite property link (inside copy tree)
        SDOProperty bidirCopyOppositeStartProp = (SDOProperty)bidirCopyStart.getType().getProperty(//
        "addressWork");
        assertNotNull(bidirCopyOppositeStartProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(bidirCopyOppositeStartProp.getType().isDataType());
        // get bidir property inside copy tree
        SDOProperty oppositeEndProp = (SDOProperty)((SDODataObject)copy.get(//
        "rootWork")).getType().getProperty("workAddress").getOpposite();
        assertNotNull(oppositeEndProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(oppositeEndProp.getType().isDataType());
        // get opposite DO (direction 2)
        SDODataObject bidirCopyViaOppositeDO = (SDODataObject)copy.get(//
        "rootWork/workAddress");
        assertNotNull(bidirCopyViaOppositeDO);
        // verify changeSummary is set on opposites
        //ChangeSummary aCS1 = bidirCopyViaOppositeDO.getChangeSummary();
        //assertNotNull(aCS1);
        // Constraint C3 - Spec 3.9.4
        // verify there are no object pointers between source and copy (all objects are instance independent)
        // ie: a' != a  - or there are no references to "root" from "copy"
        assertNotSame((SDODataObject)rootUC4.get("rootHome/homeAddress"),//
                      (SDODataObject)copy.get("rootHome/homeAddress"));
        assertNotSame((SDODataObject)rootUC4.get("rootWork"),//
                      (SDODataObject)copy.get("rootWork"));
        assertNotSame((SDODataObject)rootUC4.get(//
        "rootHome/homeAddress/addressWork"),//
                      (SDODataObject)copy.get("//" +//
                                              "rootHome/homeAddress/addressWork"));

        // Spec 8.4 "Properties that are bi-directional require that no more than one end has containment=true"
        assertFalse(areBothBidirectionalOppositePropertiesContainmentTrue(//
        bidirCopyOppositeStartProp, (SDOProperty)bidirCopyOppositeStartProp.getOpposite()));
    }

    // bidirectional property target is cached during containment iteration
    public void testUC0312DeepCopy1_nBidirectionalPropInsideCopyTreeCopiedContTreeSameAsCopyTree2ndNodeIsMany() {
        // set containment so that the bidirectional target is cached
        rootWorkm.setContainment(true);
        // create copy of the root
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(rootUC4m, getChangeSummary());

        // verify that container is null
        assertNull(copy.getContainer());
        // get bidirectional node from the copy root (direction 1)
        ListWrapper bidirCopyViaCopyRoot = (ListWrapper)copy.get(//
        "rootHome/homeAddress/addressWork");
        assertNotNull(bidirCopyViaCopyRoot);
        // get bidirectional start node via opposite property
        SDODataObject bidirCopyStart = (SDODataObject)copy.get(//
        "rootHome/homeAddress");
        assertNotNull(bidirCopyStart);

        // verify changeSummary is set on opposites
        //ChangeSummary aCS2 = bidirCopyStart.getChangeSummary();
        //assertNotNull(aCS2);
        // get property containing opposite property link (inside copy tree)
        SDOProperty bidirCopyOppositeStartProp = (SDOProperty)bidirCopyStart.getType().getProperty(//
        "addressWork");
        assertNotNull(bidirCopyOppositeStartProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(bidirCopyOppositeStartProp.getType().isDataType());
        // get bidir property inside copy tree
        SDOProperty oppositeEndProp = (SDOProperty)((SDODataObject)copy.get(//
        "rootHome/homeAddress")).getType().getProperty("addressWork");//.getOpposite();
        assertNotNull(oppositeEndProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(oppositeEndProp.getType().isDataType());
        // get opposite DO (direction 2)
        SDODataObject bidirCopyViaOppositeDO = (SDODataObject)copy.get(//
        "rootWork/workAddress");

        //assertNotNull(bidirCopyViaOppositeDO);
        // verify changeSummary is set on opposites
        //ChangeSummary aCS1 = bidirCopyViaOppositeDO.getChangeSummary();
        //assertNotNull(aCS1);
        // Constraint C3 - Spec 3.9.4
        // verify there are no object pointers between source and copy (all objects are instance independent)
        // ie: a' != a  - or there are no references to "root" from "copy"
        assertNotSame((SDODataObject)rootUC4m.get("rootHome/homeAddress"),//
                      (SDODataObject)copy.get("rootHome/homeAddress"));
        // verify isMany objects are distinct
        ListWrapper do1 = (ListWrapper)rootUC4m.get("rootWork");
        ListWrapper do2 = (ListWrapper)copy.get("rootWork");

        // verify we are not just getting back empty lists for a null object lookup
        assertTrue(do1.size() > 0);
        assertTrue(do2.size() > 0);
        // verify lists are different (shallow check)
        assertNotSame(do1, do2);
        // verify list elements are different (deep check)
        assertNotNull(do1.get(0));
        assertNotNull(do2.get(0));
        assertFalse(do1.get(0) == do2.get(0));
        //assertNotSame((SDODataObject)rootUC4m.get(//
        //"rootHome/homeAddress/addressWork"),//
        //             (SDODataObject)copy.get("//" +//
        //                                    "rootHome/homeAddress/addressWork"));
        // Spec 8.4 "Properties that are bi-directional require that no more than one end has containment=true"
        assertFalse(areBothBidirectionalOppositePropertiesContainmentTrue(//
        bidirCopyOppositeStartProp, (SDOProperty)bidirCopyOppositeStartProp.getOpposite()));
    }

    // bidirectional property target is not cached during containment iteration - original unidirectional is cached
    public void testUC0313DeepCopy1_nBidirectionalPropInsideCopyTreeCopiedContTreeSameAsCopyTree2ndNodeIsManyNotPreCached() {
        // set containment so that the bidirectional target is notcached
        rootWorkm.setContainment(false);
        // create copy of the root
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(rootUC4m, getChangeSummary());

        // verify that container is null
        assertNull(copy.getContainer());
        // get bidirectional node from the copy root (direction 1)
        ListWrapper bidirCopyViaCopyRoot = (ListWrapper)copy.get(//
        "rootHome/homeAddress/addressWork");
        assertNotNull(bidirCopyViaCopyRoot);
        // get bidirectional start node via opposite property
        SDODataObject bidirCopyStart = (SDODataObject)copy.get(//
        "rootHome/homeAddress");
        assertNotNull(bidirCopyStart);

        // verify changeSummary is set on opposites
        //ChangeSummary aCS2 = bidirCopyStart.getChangeSummary();
        //assertNotNull(aCS2);
        // get property containing opposite property link (inside copy tree)
        SDOProperty bidirCopyOppositeStartProp = (SDOProperty)bidirCopyStart.getType().getProperty(//
        "addressWork");
        assertNotNull(bidirCopyOppositeStartProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(bidirCopyOppositeStartProp.getType().isDataType());
        // get bidir property inside copy tree
        SDOProperty oppositeEndProp = (SDOProperty)((SDODataObject)copy.get(//
        "rootHome/homeAddress")).getType().getProperty("addressWork");//.getOpposite();
        assertNotNull(oppositeEndProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(oppositeEndProp.getType().isDataType());
        // get opposite DO (direction 2)
        SDODataObject bidirCopyViaOppositeDO = (SDODataObject)copy.get(//
        "rootWork/workAddress");

        //assertNotNull(bidirCopyViaOppositeDO);
        // verify changeSummary is set on opposites
        //ChangeSummary aCS1 = bidirCopyViaOppositeDO.getChangeSummary();
        //assertNotNull(aCS1);
        // Constraint C3 - Spec 3.9.4
        // verify there are no object pointers between source and copy (all objects are instance independent)
        // ie: a' != a  - or there are no references to "root" from "copy"
        assertNotSame((SDODataObject)rootUC4m.get("rootHome/homeAddress"),//
                      (SDODataObject)copy.get("rootHome/homeAddress"));
        // verify isMany objects are distinct
        ListWrapper do1 = (ListWrapper)rootUC4m.get("rootWork");
        ListWrapper do2 = (ListWrapper)copy.get("rootWork");

        // verify we are not just getting back empty lists for a null object lookup
        assertTrue(do1.size() > 0);
        assertTrue(do2.size() > 0);
        // verify lists are different (shallow check)
        assertNotSame(do1, do2);
        // verify list elements are different (deep check)
        assertNotNull(do1.get(0));
        assertNotNull(do2.get(0));
        // the unidirectional property rootWork gives us the original list in the copy
        assertTrue(do1.get(0) == do2.get(0));
        //assertNotSame((SDODataObject)rootUC4m.get(//
        //"rootHome/homeAddress/addressWork"),//
        //             (SDODataObject)copy.get("//" +//
        //                                    "rootHome/homeAddress/addressWork"));
        // Spec 8.4 "Properties that are bi-directional require that no more than one end has containment=true"
        assertFalse(areBothBidirectionalOppositePropertiesContainmentTrue(//
        bidirCopyOppositeStartProp, (SDOProperty)bidirCopyOppositeStartProp.getOpposite()));
    }

    // bidirectional property target is not cached during containment iteration - it is unset
    public void testUC0314DeepCopy1_nBidirectionalPropInsideCopyTreeCopiedContTreeSameAsCopyTree2ndNodeIsMany_unidirectionalUnset() {
        // set containment so that the bidirectional target is notcached
        //rootWorkm.setContainment(false);
        rootUC4m.unset(rootWorkm);
        // create copy of the root
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(rootUC4m, getChangeSummary());

        // verify that container is null
        assertNull(copy.getContainer());
        // get bidirectional node from the copy root (direction 1)
        ListWrapper bidirCopyViaCopyRoot = (ListWrapper)copy.get(//
        "rootHome/homeAddress/addressWork");
        assertNotNull(bidirCopyViaCopyRoot);
        // get bidirectional start node via opposite property
        SDODataObject bidirCopyStart = (SDODataObject)copy.get(//
        "rootHome/homeAddress");
        assertNotNull(bidirCopyStart);

        // verify changeSummary is set on opposites
        //ChangeSummary aCS2 = bidirCopyStart.getChangeSummary();
        //assertNotNull(aCS2);
        // get property containing opposite property link (inside copy tree)
        SDOProperty bidirCopyOppositeStartProp = (SDOProperty)bidirCopyStart.getType().getProperty(//
        "addressWork");
        assertNotNull(bidirCopyOppositeStartProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(bidirCopyOppositeStartProp.getType().isDataType());
        // get bidir property inside copy tree
        SDOProperty oppositeEndProp = (SDOProperty)((SDODataObject)copy.get(//
        "rootHome/homeAddress")).getType().getProperty("addressWork");//.getOpposite();
        assertNotNull(oppositeEndProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(oppositeEndProp.getType().isDataType());
        // get opposite DO (direction 2)
        SDODataObject bidirCopyViaOppositeDO = (SDODataObject)copy.get(//
        "rootWork/workAddress");

        //assertNotNull(bidirCopyViaOppositeDO);
        // verify changeSummary is set on opposites
        //ChangeSummary aCS1 = bidirCopyViaOppositeDO.getChangeSummary();
        //assertNotNull(aCS1);
        // Constraint C3 - Spec 3.9.4
        // verify there are no object pointers between source and copy (all objects are instance independent)
        // ie: a' != a  - or there are no references to "root" from "copy"
        assertNotSame((SDODataObject)rootUC4m.get("rootHome/homeAddress"),//
                      (SDODataObject)copy.get("rootHome/homeAddress"));
        // verify isMany objects are distinct
        ListWrapper do1 = (ListWrapper)rootUC4m.get("rootWork");
        ListWrapper do2 = (ListWrapper)copy.get("rootWork");

        // Verify that bidirectional outside the copytree is not set
        assertTrue(do2.size() == 0);
        assertTrue(do1.size() == 0);
        // verify lists are different (shallow check)
        assertNotSame(do1, do2);
        // verify list elements are different (deep check)
        assertNull(do2.get(0));
        assertNull(do1.get(0));
        // the unidirectional property rootWork (unset) gives us no copy of the list 
        //assertFalse(do1.get(0) == do2.get(0));
        //assertNotSame((SDODataObject)rootUC4m.get(//
        //"rootHome/homeAddress/addressWork"),//
        //             (SDODataObject)copy.get("//" +//
        //                                    "rootHome/homeAddress/addressWork"));
        // Spec 8.4 "Properties that are bi-directional require that no more than one end has containment=true"
        assertFalse(areBothBidirectionalOppositePropertiesContainmentTrue(//
        bidirCopyOppositeStartProp, (SDOProperty)bidirCopyOppositeStartProp.getOpposite()));
    }

    public void testUC0102DeepCopy1_1BidirectionalPropSubTreeOutsideCopyTreeNotCopied() {
        // create copy of home and verify that rw was not copied since it is outside the copy tree
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(homeObject, getChangeSummary());

        // verify that container is null
        assertNull(copy.getContainer());
        // get bidirectional node from the copy root
        SDODataObject bidirCopyViaCopyRoot = (SDODataObject)copy.get(//
        "homeAddress/addressWork");
        assertNotNull(bidirCopyViaCopyRoot);
        // get bidirectional start node via opposite property
        SDODataObject bidirCopyStart = (SDODataObject)copy.get(//
        "homeAddress");
        assertNotNull(bidirCopyStart);
        // get property containing opposite property link (inside copy tree)
        SDOProperty bidirCopyOppositeStartProp = (SDOProperty)bidirCopyStart.getType().getProperty(//
        "addressWork");
        assertNotNull(bidirCopyOppositeStartProp);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(bidirCopyOppositeStartProp.getType().isDataType());
        // get bidir property outside of copy tree
        SDOProperty bidirCopyViaOpposite = (SDOProperty)bidirCopyOppositeStartProp.getType()//
        .getProperty("workAddress");
        assertNull(bidirCopyViaOpposite);
        SDODataObject bidirCopyViaOppositeDO = (SDODataObject)copy.get(//
        "rootWork/workAddress");
        assertNull(bidirCopyViaOppositeDO);
    }

    // purpose: copy and source reference same type and properties
    // TODO: 20060906 bidirectional/reference
    public void testCopySourceHaveSameType() {
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());
        assertFalse(copy.equals(root));
        assertEquals(copy.getType(), root.getType());
        assertEqualityHelperEqual(root, copy);
    }

    // purpose: copy and source have same map if source has no properties having not null opposite
    // or if source has but these properties are not set. 
    // TODO: this tests only tests the MapValueStore impl, separate generic test required
    public void testCopySourceHaveSameMap() {
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());
        int copySize = getPropertiesSize(copy);
        assertEquals(copySize, getPropertiesSize(root));
        testCopySourceMap(root, copy);
        assertEqualityHelperEqual(root, copy);
    }

    // recursively compare source and copy DataObjects when they both contain othe DataObject
    // otherwise, just compare their primitive value and containment properties in the map
    // purpose: test case 1 and test case 2 : in tree dataobject and datatype value
    // TODO: this tests only tests the MapValueStore impl, separate generic test required
    private void testCopySourceMap(SDODataObject source, SDODataObject copy) {
        assertFalse(source.equals(copy));// make sure copy source are not the same object
        Set copyKeys = getPropertiesKeySet(copy);
        Set keys = getPropertiesKeySet(source);
        Iterator iterKeys = copyKeys.iterator();
        while (iterKeys.hasNext()) {
            String propertyName = (String)iterKeys.next();
            assertTrue(keys.contains(propertyName));
            Object s = source.get(propertyName);
            Object c = copy.get(propertyName);
            Property cProperty = copy.getInstanceProperty(propertyName);
            if (!(c instanceof SDODataObject) && !(c instanceof SDOChangeSummary) &&//
                    !cProperty.isMany()) {
                assertEquals(c, s);// DataType, then value should equals
            } else {
                if (c instanceof SDODataObject) {
                    if (cProperty.isContainment()) {
                        testCopySourceMap((SDODataObject)s, (SDODataObject)c);// contained DataObject, recursive
                    }
                }
                if (c instanceof SDOChangeSummary) {// ChangeSummary Type
                    assertEquals(((SDOChangeSummary)c).isLogging(),//
                                 ((SDOChangeSummary)s).isLogging());
                    assertEquals(copy, ((SDOChangeSummary)c).getRootObject());
                }
                if (cProperty.isMany()) {
                    Iterator iterList = ((List)c).iterator();
                    int i = 0;
                    while (iterList.hasNext()) {
                        SDODataObject cO = (SDODataObject)iterList.next();
                        SDODataObject sO = (SDODataObject)((List)s).get(i);
                        testCopySourceMap(sO, cO);
                        i++;
                    }
                }
            }
        }
    }

    // purpose: source add a new not containment property with null opposite and set its value, 
    // then copy and source should still have same map.
    // Test case 4: unidirectional 
    // TODO: this tests only tests the DefaultValueStore impl, separate generic test required
    // TODO: 20060906 bidirectional/reference
    public void testUnidirectionalOutsideCopyTreeReferencesSameDO() {
        // set this property value to containedDataObject
        //root.set("rootProperty_NotContainment", containedDataObject);
        SDODataObject copyOriginal = (SDODataObject)rootUCUniOutside.get("rootHome");
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(copyOriginal, getChangeSummary());
        int copySize = getPropertiesSize(copy);
        assertTrue(copySize == getPropertiesSize(copyOriginal));
        //  unidirectional, reference the same DataObject outside the tree
        // Constraint C4 - Spec 3.9.4
        assertTrue(copy.get("rootWork") == copyOriginal.get("rootWork"));
        assertEqualityHelperEqual(copyOriginal, copy);
    }

    public void testUnidirectionalInsideCopyTreeReferencesCopyDO() {
        // set this property value to containedDataObject
        root.set("rootProperty_NotContainment", containedDataObject);

        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());
        int copySize = getPropertiesSize(copy);
        assertTrue(copySize == getPropertiesSize(root));
        //  unidirectional, reference the the copy DataObject inside the tree
        // Constraint C4 - Spec 3.9.4
        assertTrue(copy.get("rootProperty_NotContainment") != root.get("rootProperty_NotContainment"));
        assertEqualityHelperEqual(root, copy);
    }

    // purpose: test case 6 about changesummary
    public void testSourceHasPropertyWithChangeSummaryValue() {
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());
        SDODataObject copyContainedDataObject = (SDODataObject)copy.get(rootProperty2.getName());
        assertEqualityHelperEqual(root, copy);

        assertNotNull(copyContainedDataObject);

        SDOChangeSummary copyChSum = (SDOChangeSummary)copyContainedDataObject.get(containedProperty_ChangeSummary.getName());

        assertNotNull(copyChSum);
        ChangeSummary originalChSum = root.getDataObject("rootproperty2-notdatatype").getChangeSummary();
        assertFalse(copyChSum.equals(originalChSum));

        assertEquals(originalChSum.isLogging(), copyChSum.isLogging());// logging status is same
        // their root dataObject have the same setting
        testCopySourceMap(containedDataObject, (SDODataObject)copyChSum.getRootObject());

    }

    // Test case 3: for bidirectional property, skip it and no value will be referenced or copied.
    // TODO: 20060906 bidirectional/reference
    public void testBiDirectionalRelationInDeepCopy() {
        SDODataObject copyRoot = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());

        SDODataObject copyContainedByContainedDataObject = (SDODataObject)copyRoot//
        .get("rootproperty2-notdatatype/containedProperty2-notdataType");

        assertNotNull(copyContainedByContainedDataObject);
        assertFalse(copyContainedByContainedDataObject.equals(containedDataObject));// make sure not the same
        SDODataObject oppositeObject = (SDODataObject)copyContainedByContainedDataObject//
        .get("containedByContainedProperty1-notdataType");

        //assertNull(oppositeObject);
        assertNotNull(oppositeObject);// 20060906: bidirectional support added

        SDODataObject copyContainedDataObject1 = (SDODataObject)copyRoot.get(rootProperty3);

        assertNotNull(copyContainedDataObject1);
        assertFalse(copyContainedDataObject1.equals(containedDataObject1));
        SDODataObject oppositeObject1 = (SDODataObject)copyContainedDataObject1//
        .get(contained1Property1);

        //assertNull(oppositeObject1);
        assertNotNull(oppositeObject1);// 20060906: bidirectional support added
        assertEqualityHelperEqual(root, copyRoot);
    }

    // purpose: test case 5: not in containment tree
    public void testDataObjectNotInContainmentTree() {
        // create DataObject dataObjectNotInTreeroot contains another not in tree DataObject
        // dataObjectNotInTree
        SDOType notInTreeRootType = new SDOType((SDOTypeHelper) aHelperContext.getTypeHelper());
        SDOProperty notInTreeProperty = new SDOProperty(aHelperContext);
        notInTreeProperty.setName("notInTreeProperty");
        SDOType notInTreePropertyType = new SDOType((SDOTypeHelper) aHelperContext.getTypeHelper());
        notInTreePropertyType.setDataType(false);
        notInTreeProperty.setContainment(true);
        notInTreeProperty.setType(notInTreePropertyType);
        notInTreeRootType.addDeclaredProperty(notInTreeProperty);

        //SDODataObject dataObjectNotInTree = new SDODataObject();
        SDODataObject dataObjectNotInTree = (SDODataObject)dataFactory.create(notInTreeRootType);
        SDODataObject dataObjectNotInTreeroot = (SDODataObject)dataFactory.create(notInTreeRootType);
        dataObjectNotInTreeroot.set(notInTreeProperty, dataObjectNotInTree);

        // set dataObjectNotInTree as not containment property's value
        containedByContainedDataObject.set(containedByContainedProperty2, dataObjectNotInTree);

        SDODataObject copyRoot = null;
        try {
            copyRoot = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());
            //fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
            assertNull(copyRoot);
        }
    }

    // purpose: negative test by passed in null value
    public void testCopyWithNullValue() {
        DataObject source = null;
        DataObject o = ((SDOCopyHelper)copyHelper).copy(source, getChangeSummary());
        assertNull(o);
    }

    // purpose: test containment property is many
    public void testCopyWithContainmentPropertyIsMany() {
        SDODataObject c = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());
        assertEqualityHelperEqual(root, c);
        List cL = (List)c.get(rootProperty4);
        List sL = (List)root.get(rootProperty4);
        assertFalse(cL == sL);
        assertTrue(cL.size() == sL.size());
        // check that items in the copied list are distinct objects from their original - same as !.equals
        assertNotNull(sL.get(0));
        assertNotNull(cL.get(0));
        assertFalse(sL.get(0) == cL.get(0));
        testCopySourceMap(root, c);

    }

    // purpose: test non-containment property is many (unidirectional)
    public void testCopyWithNotContainmentPropertyIsMany() {
        rootProperty4.setContainment(false);
        SDODataObject c = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());
        assertEqualityHelperEqual(root, c);
        List cL = (List)c.get(rootProperty4);
        List sL = (List)root.get(rootProperty4);
        assertFalse(cL == sL);
        //System.out.println(cL.size());
        assertTrue(sL.size() > 0);
        assertEquals(sL.size(), cL.size());
        assertEquals(sL.get(0), cL.get(0));
        // check that items in the copied list are not distinct objects from their original - same as .equals
        assertTrue(sL.get(0) == cL.get(0));
    }

    // purpose: test containment property is many
    public void testCopyWithNotContainmentPropertyIsManyValueNotInTree() {
        Type testType = new SDOType("uri", "name");
        SDODataObject test = (SDODataObject)dataFactory.create(testType);
        objects.add(test);
        root.set(rootProperty4, objects);
        rootProperty4.setContainment(false);
        //rootProperty4.setContainment(true);
        test._setContainer(null);
        SDODataObject c = null;
        try {
            c = (SDODataObject)((SDOCopyHelper)copyHelper).copy(root, getChangeSummary());
            //fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
            assertNull(c);
        }
    }

    public void testShallowCopySourceHaveSameType() {
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copyShallow(root);
        assertEquals(copy.getType(), root.getType());
    }

    // TODO: this tests only tests the MapValueStore impl, separate generic test required
    public void testShallowCopySourceHaveDifferentMap() {
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copyShallow(root);
        int copySize = getPropertiesSize(copy);
        assertFalse(copySize == getPropertiesSize(root));
        //testCopySourceMap(root, copy);
    }

    // TODO: this tests only tests the MapValueStore impl, separate generic test required
    public void testShallowCopySourceMapsHaveSameDataTypeObj() {
        SDODataObject copy = (SDODataObject)((SDOCopyHelper)copyHelper).copyShallow(root);
        int copySize = getPropertiesSize(copy);
        assertFalse(copySize == getPropertiesSize(root));

        Set copyKeys = getPropertiesKeySet(copy);
        Set keys = getPropertiesKeySet(root);
        Iterator iterKeys = copyKeys.iterator();
        while (iterKeys.hasNext()) {
            String propertyName = (String)iterKeys.next();
            assertTrue(keys.contains(propertyName));
            Object s = root.get(propertyName);
            Object c = copy.get(propertyName);
            if (!(c instanceof SDODataObject) && !(c instanceof SDOChangeSummary)) {
                assertEquals(c, s);
            }
        }
    }

    public void testShallowCopySourceHasPropertyWithChangeSummaryValue() {
        SDODataObject copy = (SDODataObject)copyHelper.copyShallow(containedDataObject);

        assertNotNull(copy);

        SDOChangeSummary copyChSum = (SDOChangeSummary)copy//
        .get(containedProperty_ChangeSummary.getName());

        assertNotNull(copyChSum);
        ChangeSummary originalChSum = root.getDataObject("rootproperty2-notdatatype").getChangeSummary();
        assertEquals(originalChSum.isLogging(), copyChSum.isLogging());// loggin status is same
    }

    public void testShallowCopyWithNullValue() {
        DataObject source = null;
        DataObject o = copyHelper.copyShallow(source);
        assertNull(o);
    }

    // add oc test
    public void assertEqualityHelperEqual(DataObject root, DataObject copy) {
        assertTrue(equalityHelper.equal(root, copy));

    }

    public void testDeleteLeaf() {
        assertEquals(containedByContainedDataObject, containedDataObject.get(containedProperty2));
        containedByContainedDataObject.getChangeSummary().endLogging();
        containedByContainedDataObject.unset(containedByContainedProperty1);
        containedByContainedDataObject.getChangeSummary().beginLogging();
        containedByContainedDataObject.delete();
        assertNull(containedDataObject.get(containedProperty2));        
        DataObject deepCopy = ((SDOCopyHelper)copyHelper).copy(containedDataObject, getChangeSummary());
        assertEqualityHelperEqual(containedDataObject, deepCopy);
    }
}
