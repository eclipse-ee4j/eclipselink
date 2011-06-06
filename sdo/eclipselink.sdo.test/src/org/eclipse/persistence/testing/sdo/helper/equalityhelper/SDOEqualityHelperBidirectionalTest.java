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
    Test equality helper using bidirectional properties

   NOTES
    References:
      SDO59-DeepCopy.doc
      SDO_Ref_BiDir_Relationships_DesignSpec.doc

    mfobrien    09/14/06 - Creation
    mfobrien    09/18/06 - Add bidirectional testcases for the following cases

                                   - verify all properties that are bi-directional are type.dataType=false - spec 8.4
                                   - test bi-directional with one end at containment=true - spec 8.4
                                   - test bi-directional where both ends have the same value for readOnly = true - spec 8.4
                                   - test bi-directional where both ends have the same value for readOnly = false - spec 8.4
                                   - test bi-directional with containment where the non-containment Property has many=false. - spec 8.4
    mfobrien    09/18/06 - add unidirectional tests (inside/outside) copy tree
 */
package org.eclipse.persistence.testing.sdo.helper.equalityhelper;

import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.testing.sdo.helper.SDOCopyEqualityHelperTestCases;
import commonj.sdo.DataObject;

public class SDOEqualityHelperBidirectionalTest extends SDOCopyEqualityHelperTestCases {
    public SDOEqualityHelperBidirectionalTest(String name) {
        super(name);
    }

    /*
     * Testcases
     * shallow copy:
     *   opposite if set is unset, not copied
     *
     * deep copy:
     *   opposites if set and in do-tree?
     *
     * shallow equality:
     *
     *
     * deep equality:
     *
     *
     *
     */
    public void testDeepEqualWithADataObjectToItsInternalCopyBidirectionalOutsideCopyTreeNotEqual() {
        DataObject deepCopy = copyHelper.copy(containedDataObject);
        assertFalse(equalityHelper.equal(containedDataObject, deepCopy));
    }

    public void testDeepEqualWithADataObjectToItsBidirectionalInsideCopyTreeEqual() {
        DataObject deepCopy = copyHelper.copy(rootUC4);

        //
        assertTrue(equalityHelper.equal(rootUC4, deepCopy));
    }

    public void testDeepEqualWithADataObjectToItsUnidirectionalInsideCopyTreeEqual() {
        DataObject deepCopy = copyHelper.copy(rootUCUniOutside);

        // the unidirectional property target will be a copy of the original
        assertTrue(equalityHelper.equal(rootUCUniOutside, deepCopy));
    }

    public void testDeepEqualWithADataObjectToItsUnidirectionalOutsideCopyTreeEqual() {
        DataObject deepCopy = copyHelper.copy(homeObjectUCUniOutside);

        // the unidirectional property target will be the original object (not a copy)
        assertTrue(equalityHelper.equal(homeObjectUCUniOutside, deepCopy));
    }

    // compare opposite properties are equal after a copy
    public void testDeepEqualWithABidirectionaDataObjectToItself() {
        // Objective: 
        //  Primary: Verify that opposite properties 
        //  Secondary: Verify that opposite properties are maintained during deep copy
        DataObject deepCopy = copyHelper.copy(root);
        SDOProperty oppositePropertySrc = contained1Property1;

        // first verify properties opposite is not null causing npe's
        assertNotNull(oppositePropertySrc);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(oppositePropertySrc.getType().isDataType());
        SDOProperty oppositePropertyDest = (SDOProperty)oppositePropertySrc.getOpposite();
        assertNotNull(oppositePropertyDest);
        // Spec 8.4 "Properties that are bi-directional require type.dataType=false"
        assertFalse(oppositePropertyDest.getType().isDataType());

        // verify we are not checking outside the copytree
        assertTrue(equalityHelper.equal(root, deepCopy));
    }

    // compare a DataObject with itself by deep equal --- true
    public void testDeepEqualWithADataObjectToItself() {
        //
        assertTrue(equalityHelper.equal(root, root));
    }

    // compare a DataObject with its Deep copy by deep equal --- true
    public void testDeepEqualWithADataObjectToItsDeepCopy() {
        DataObject deepCopy = copyHelper.copy(root);

        //
        assertTrue(equalityHelper.equal(root, deepCopy));
    }
}
