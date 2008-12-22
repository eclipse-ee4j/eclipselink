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
 * A helper for comparing DataObjects.
 */
public interface EqualityHelper
{
  /**
   * <p>Two DataObjects are equalShallow if
   *  they have the same {@link DataObject#getType Type}
   *  and all their compared Properties are equal.
   *  The set of Properties compared are the 
   *  {@link DataObject#getInstanceProperties() instance properties}
   *  where property.getType().isDataType() is true
   *  and property.getType() is not ChangeSummaryType.
   * <br/>Two of these Property values are equal if they are both not
   *  {@link DataObject#isSet(Property) set}, or set to an equal value
   *  dataObject1.get(property).equals(dataObject2.get(property))
   * <br/>If the type is a sequenced type, the sequence entries must be the same.
   * This includes only text entries and entries where property.getType().isDataType() is true and
   * their relative order must be the same.
   *  For each pair of entries x and y in the sequence where the property is used in the comparison,
   *  dataObject1.getSequence().getValue(x).equals(
   *   dataObject2.getSequence().getValue(y)) and
   *  dataObject1.getSequence().getProperty(x) == 
   *   dataObject2.getSequence().getProperty(y)
   *  must be true.
   * </p>
   *  Returns true the objects have the same Type and all values of all compared Properties are equal.
   *  @param dataObject1 DataObject to be compared
   *  @param dataObject2 DataObject to be compared
   *  @return true the objects have the same Type and all values of all compared Properties are equal.
   */
  boolean equalShallow(DataObject dataObject1, DataObject dataObject2);
    
  /**
   * <p>Two DataObjects are equal(Deep) if they are equalShallow,
   *  all their compared Properties are equal, and all reachable DataObjects in their
   *  graphs excluding containers are equal.
   *  The set of Properties compared are the 
   *  {@link DataObject#getInstanceProperties() instance properties}
   *  where property.getType().isDataType() is false, 
   *  and is not a container property, ie !property.getOpposite().isContainment()
   * <br/>Two of these Property values are equal if they are both not
   *  {@link DataObject#isSet(Property) set}, or all the DataObjects
   *  they refer to are {@link #equal(DataObject, DataObject) equal} in the 
   *  context of dataObject1 and dataObject2.
   * <br/>Note that properties to a containing DataObject are not compared
   *  which means two DataObject trees can be equal even if their containers are not equal.
   * <br/>If the type is a sequenced type, the sequence entries must be the same.
   *  For each entry x in the sequence where the property is used in the comparison,
   *  equal(dataObject1.getSequence().getValue(x), 
   *   dataObject2.getSequence().getValue(x)) and
   *  dataObject1.getSequence().getProperty(x) == 
   *   dataObject2.getSequence().getProperty(x)
   *  must be true.
   * </p><p>
   * A DataObject directly or indirectly referenced by dataObject1 or dataObject2 
   *  can only be equal to exactly one DataObject directly or indirectly referenced 
   *  by dataObject1 or dataObject2, respectively.
   *  This ensures that dataObject1 and dataObject2 are equal if the graph formed by 
   *  all their referenced DataObjects have the same shape.
   * </p>
   *  Returns true if the trees of DataObjects are equal(Deep). 
   *  @param dataObject1 DataObject to be compared
   *  @param dataObject2 DataObject to be compared
   *  @return true if the trees of DataObjects are equal(Deep). 
   */
  boolean equal(DataObject dataObject1, DataObject dataObject2);

  /**
   * The default EqualityHelper.
   */
  EqualityHelper INSTANCE = HelperProvider.getEqualityHelper();
}
