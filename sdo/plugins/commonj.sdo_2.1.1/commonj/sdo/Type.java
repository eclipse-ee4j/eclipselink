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
 * A representation of the type of a {@link Property property} of a {@link DataObject data object}.
 */
public interface Type
{
  /**
   * Returns the name of the type.
   * @return the type name.
   */
  String getName();
  
  /**
   * Returns the namespace URI of the type or <code>null</code> if the type has no URI
   * (for example it was generated from a Schema with no target namespace).
   * @return the namespace URI.
   */
  String getURI();

  /**
   * Returns the Java class that this type represents.
   * @return the Java class.
   */
  Class getInstanceClass();

  /**
   * Returns whether the specified object is an instance of this type.
   * @param object the object in question.
   * @return <code>true</code> if the object is an instance.
   * @see Class#isInstance
   */
  boolean isInstance(Object object);

  /**
   * Returns the List of the {@link Property Properties} of this type.
   * <p>
   * The expression
   *<pre>
   *   type.getProperties().indexOf(property)
   *</pre>
   * yields the property's index relative to this type.
   * As such, these expressions are equivalent:
   *<pre>
   *    dataObject.{@link DataObject#get(int) get}(i)
   *    dataObject.{@link DataObject#get(Property) get}((Property)dataObject.getType().getProperties().get(i));
   *</pre>
   * </p>
   * @return the Properties of the type.
   * @see Property#getContainingType
   */
  List /*Property*/ getProperties();
  
  /**
   * Returns from {@link #getProperties all the Properties} of this type, the one with the specified name.
   * As such, these expressions are equivalent:
   *<pre>
   *    dataObject.{@link DataObject#get(String) get}("name")
   *    dataObject.{@link DataObject#get(Property) get}(dataObject.getType().getProperty("name"))
   *</pre>
   * </p>
   * @return the Property with the specified name.
   * @see #getProperties
   */
  Property getProperty(String propertyName);
  
  /**
   * Indicates if this Type specifies DataTypes (true) or DataObjects (false).
   * When false, any object that is an instance of this type
   * also implements the DataObject interface.
   * True for simple types such as Strings and numbers.
   * For any object:
   * <pre>
   *   isInstance(object) && !isDataType() implies
   *   DataObject.class.isInstance(object) returns true. 
   * </pre>
   * @return true if Type specifies DataTypes, false for DataObjects.
   */
  boolean isDataType();
  
  /**
   * Indicates if this Type allows any form of open content.  If false,
   * dataObject.getInstanceProperties() must be the same as 
   * dataObject.getType().getProperties() for any DataObject dataObject of this Type.
   * @return true if this Type allows open content.
   */
  boolean isOpen();

  /**
   * Indicates if this Type specifies Sequenced DataObjects.
   * Sequenced DataObjects are used when the order of values 
   * between Properties must be preserved.
   * When true, a DataObject will return a Sequence.  For example,
   * <pre>
   *  Sequence elements = dataObject.{@link DataObject#getSequence() getSequence}();
   * </pre>
   * @return true if this Type specifies Sequenced DataObjects.
   */
  boolean isSequenced();

  /**
   * Indicates if this Type is abstract.  If true, this Type cannot be
   * instantiated.  Abstract types cannot be used in DataObject or 
   * DataFactory create methods.
   * @return true if this Type is abstract.
   */
  boolean isAbstract();

  /**
   * Returns the List of base Types for this Type.  The List is empty
   * if there are no base Types.  XSD <extension>, <restriction>, and
   * Java extends keyword are mapped to this list.
   * @return the List of base Types for this Type.
   */
  List /*Type*/ getBaseTypes();
  
  /**
   * Returns the Properties declared in this Type as opposed to
   * those declared in base Types.
   * @return the Properties declared in this Type.
   */
  List /*Property*/ getDeclaredProperties();

  /**
   * Return a list of alias names for this Type.
   * @return a list of alias names for this Type.
   */
  List /*String*/ getAliasNames();

  /**
   * Returns a read-only List of instance Properties available on this Type.
   * <p>
   * This list includes, at a minimum, any open content properties (extensions) added to
   * the object before {@link commonj.sdo.helper.TypeHelper#define(DataObject) defining
   * the Type's Type}. Implementations may, but are not required to in the 2.1 version
   * of SDO, provide additional instance properties.
   * @return the List of instance Properties on this Type.
   */
  List /*Property*/ getInstanceProperties();

  /**
   * Returns the value of the specified instance property of this Type.
   * @param property one of the properties returned by {@link #getInstanceProperties()}.
   * @return the value of the specified property.
   * @see DataObject#get(Property)
   */
  Object get(Property property);

}
