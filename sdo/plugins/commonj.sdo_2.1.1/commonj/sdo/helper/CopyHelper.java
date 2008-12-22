/**
 * <copyright>
 *
 * Service Data Objects
 * Version 2.1.1
 * Licensed Materials
 *
 * (c) Copyright BEA Systems, Inc., International Business Machines Corporation, 
 * Oracle Corporation, Primeton Technologies Ltd., Rogue Wave Software, SAP AG., 
 * Software AG., Sun Microsystems, Sybase Inc., Xcalia, Zend Technologies, 
 * 2005-2008. All rights reserved.
 *
 * </copyright>
 * 
 */

package commonj.sdo.helper;

import commonj.sdo.DataObject;
import commonj.sdo.impl.HelperProvider;

/**
 * A helper for copying DataObjects.
 */
public interface CopyHelper
{
    /**
     * Create a shallow copy of the DataObject dataObject:
     *   Creates a new DataObject copiedDataObject with the same values
     *     as the source dataObject for each property where
     *       property.getType().isDataType() is true.
     *   The value of such a Property property in copiedDataObject is: 
     *       dataObject.get(property) for single-valued Properties 
     *       (copiedDataObject.get(property) equals() dataObject.get(property)), or 
     *       a List where each member is equal to the member at the 
     *       same index in dataObject for multi-valued Properties
     *        copiedDataObject.getList(property).get(i) equals() dataObject.getList(property).get(i)
     *   The copied Object is unset for each Property where
     *       property.getType().isDataType() is false
     *       since they are not copied.
     *   Read-only properties are copied.
     *   A copied object shares metadata with the source object
     *     sourceDO.getType() == copiedDO.getType()
     *   If a ChangeSummary is part of the source DataObject
     *     the copy has a new, empty ChangeSummary.
     *     Logging state is the same as the source ChangeSummary.
     * 
     * @param dataObject to be copied
     * @return copy of dataObject 
     */
    DataObject copyShallow(DataObject dataObject);
    
    /**
     * Create a deep copy of the DataObject tree:
     *   Copies the dataObject and all its {@link commonj.sdo.Property#isContainment() contained}
     *     DataObjects recursively.
     *   Values of Properties are copied as in shallow copy,
     *     and values of Properties where 
     *       property.getType().isDataType() is false
     *     are copied where each value copied must be a
     *       DataObject contained by the source dataObject.
     *     If a DataObject is outside the DataObject tree and the
     *       property is bidirectional, then the DataObject is skipped.
     *     If a DataObject is outside the DataObject tree and the
     *       property is unidirectional, then the same DataObject is referenced.
     *   Read-only properties are copied.
     *   If any DataObject referenced is not in the containment
     *     tree an IllegalArgumentException is thrown.
     *   If a ChangeSummary is part of the copy tree the new 
     *     ChangeSummary refers to objects in the new DataObject tree.
     *     Logging state is the same as the source ChangeSummary.
     * 
     * @param dataObject to be copied.
     * @return copy of dataObject
     * @throws IllegalArgumentException if any referenced DataObject
     *   is not part of the containment tree.
     */
    DataObject copy(DataObject dataObject);

    /**
     * The default CopyHelper.
     */
    CopyHelper INSTANCE = HelperProvider.getCopyHelper();

}
