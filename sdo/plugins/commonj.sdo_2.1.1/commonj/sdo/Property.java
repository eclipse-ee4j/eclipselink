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

package commonj.sdo;

import java.util.List;

/**
 * A representation of a Property in the {@link Type type} of a {@link DataObject data object}.
 */
public interface Property
{
  /**
   * Returns the name of the Property.
   * @return the Property name.
   */
  String getName();
  
  /**
   * Returns the type of the Property.
   * @return the Property type.
   */
  Type getType();
  
  /**
   * Returns whether the Property is many-valued.
   * @return <code>true</code> if the Property is many-valued.
   */
  boolean isMany();
  
  /**
   * Returns whether the Property is containment, i.e., whether it represents by-value composition.
   * @return <code>true</code> if the Property is containment.
   */
  boolean isContainment();
  
  /**
   * Returns the containing type of this Property.
   * @return the Property's containing type.
   * @see Type#getProperties()
   */
  Type getContainingType();

  /**
   * Returns the default value this Property will have in a {@link DataObject data object} where the Property hasn't been set.
   * @return the default value.
   */
  Object getDefault();

  /**
   * Returns true if values for this Property cannot be modified using the SDO APIs.
   * When true, DataObject.set(Property property, Object value) throws an exception.
   * Values may change due to other factors, such as services operating on DataObjects.
   * @return true if values for this Property cannot be modified.
   */
  boolean isReadOnly();

  /**
   * Returns the opposite Property if the Property is bi-directional or null otherwise.
   * @return the opposite Property if the Property is bi-directional or null
   */
  Property getOpposite();

  /**
   * Returns a list of alias names for this Property.
   * @return a list of alias names for this Property.
   */
  List /*String*/ getAliasNames();

  /**
   * Returns whether or not instances of this property can be set to null. The effect of calling set(null) on a non-nullable
   * property is not specified by SDO.
   * @return true if this property is nullable.
   */
  boolean isNullable();

  /**
   * Returns whether or not this is an open content Property.
   * @return true if this property is an open content Property.
   */
  boolean isOpenContent();

  /**
   * Returns a read-only List of instance Properties available on this Property.
   * <p>
   * This list includes, at a minimum, any open content properties (extensions) added to
   * the object before {@link commonj.sdo.helper.TypeHelper#define(DataObject) defining
   * the Property's Type}. Implementations may, but are not required to in the 2.1 version
   * of SDO, provide additional instance properties.
   * @return the List of instance Properties on this Property.
   */
  List /*Property*/ getInstanceProperties();

  /**
   * Returns the value of the specified instance property of this Property.
   * @param property one of the properties returned by {@link #getInstanceProperties()}.
   * @return the value of the specified property.
   * @see DataObject#get(Property)
   */
  Object get(Property property);

}
