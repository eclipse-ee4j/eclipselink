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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * A data object is a representation of some structured data. 
 * It is the fundamental component in the SDO (Service Data Objects) package.
 * Data objects support reflection, path-based accesss, convenience creation and deletion methods, 
 * and the ability to be part of a data graph.
 * <p>
 * Each data object holds its data as a series of {@link Property Properties}. 
 * Properties can be accessed by name, property index, or using the property meta object itself. 
 * A data object can also contain references to other data objects, through reference-type Properties.
 * <p>
 * A data object has a series of convenience accessors for its Properties. 
 * These methods either use a path (String), 
 * a property index, 
 * or the {@link Property property's meta object} itself, to identify the property.
 * Some examples of the path-based accessors are as follows:
 *<pre>
 * DataObject company = ...;
 * company.get("name");                   is the same as company.get(company.getType().getProperty("name"))
 * company.set("name", "acme");
 * company.get("department.0/name")       is the same as ((DataObject)((List)company.get("department")).get(0)).get("name")
 *                                        .n  indexes from 0 ... implies the name property of the first department
 * company.get("department[1]/name")      [] indexes from 1 ... implies the name property of the first department
 * company.get("department[number=123]")  returns the first department where number=123
 * company.get("..")                      returns the containing data object
 * company.get("/")                       returns the root containing data object
 *</pre> 
 * <p> There are general accessors for Properties, i.e., {@link #get(Property) get} and {@link #set(Property, Object) set}, 
 * as well as specific accessors for the primitive types and commonly used data types like 
 * String, Date, List, BigInteger, and BigDecimal.
 */
public interface DataObject extends Serializable
{
  /**
   * Returns the value of a property of either this object or an object reachable from it, as identified by the
   * specified path.
   * @param path the path to a valid object and property.
   * @return the value of the specified property.
   * @see #get(Property)
   */
  Object get(String path);

  /**
   * Sets a property of either this object or an object reachable from it, as identified by the specified path,
   * to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void set(String path, Object value);

  /**
   * Returns whether a property of either this object or an object reachable from it, as identified by the specified path,
   * is considered to be set.
   * @param path the path to a valid object and property.
   * @see #isSet(Property)
   */
  boolean isSet(String path);

  /**
   * Unsets a property of either this object or an object reachable from it, as identified by the specified path.
   * @param path the path to a valid object and property.
   * @see #unset(Property)
   */
  void unset(String path);

  /**
   * Returns the value of a <code>boolean</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>boolean</code> value of the specified property.
   * @see #get(String)
   */
  boolean getBoolean(String path);

  /**
   * Returns the value of a <code>byte</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>byte</code> value of the specified property.
   * @see #get(String)
   */
  byte getByte(String path);

  /**
   * Returns the value of a <code>char</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>char</code> value of the specified property.
   * @see #get(String)
   */
  char getChar(String path);

  /**
   * Returns the value of a <code>double</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>double</code> value of the specified property.
   * @see #get(String)
   */
  double getDouble(String path);

  /**
   * Returns the value of a <code>float</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>float</code> value of the specified property.
   * @see #get(String)
   */
  float getFloat(String path);

  /**
   * Returns the value of a <code>int</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>int</code> value of the specified property.
   * @see #get(String)
   */
  int getInt(String path);

  /**
   * Returns the value of a <code>long</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>long</code> value of the specified property.
   * @see #get(String)
   */
  long getLong(String path);

  /**
   * Returns the value of a <code>short</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>short</code> value of the specified property.
   * @see #get(String)
   */
  short getShort(String path);

  /**
   * Returns the value of a <code>byte[]</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>byte[]</code> value of the specified property.
   * @see #get(String)
   */
  byte[] getBytes(String path);

  /**
   * Returns the value of a <code>BigDecimal</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>BigDecimal</code> value of the specified property.
   * @see #get(String)
   */
  BigDecimal getBigDecimal(String path);

  /**
   * Returns the value of a <code>BigInteger</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>BigInteger</code> value of the specified property.
   * @see #get(String)
   */
  BigInteger getBigInteger(String path);

  /**
   * Returns the value of a <code>DataObject</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>DataObject</code> value of the specified property.
   * @see #get(String)
   */
  DataObject getDataObject(String path);

  /**
   * Returns the value of a <code>Date</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>Date</code> value of the specified property.
   * @see #get(String)
   */
  Date getDate(String path);

  /**
   * Returns the value of a <code>String</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>String</code> value of the specified property.
   * @see #get(String)
   */
  String getString(String path);

  /**
   * Returns the value of a <code>List</code> property identified by the specified path.
   * @param path the path to a valid object and property.
   * @return the <code>List</code> value of the specified property.
   * @see #get(String)
   */
  List getList(String path);

  /**
   * @see #getSequence()
   * Returns the value of a <code>Sequence</code> property identified by the specified path.
   * An implementation may throw an UnsupportedOperationException.
   * @param path the path to a valid object and property.
   * @return the <code>Sequence</code> value of the specified property.
   * @see #get(String)
   * @deprecated in 2.1.0.
   */
  Sequence getSequence(String path);

  /**
   * Sets the value of a <code>boolean</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setBoolean(String path, boolean value);

  /**
   * Sets the value of a <code>byte</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setByte(String path, byte value);

  /**
   * Sets the value of a <code>char</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setChar(String path, char value);

  /**
   * Sets the value of a <code>double</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setDouble(String path, double value);

  /**
   * Sets the value of a <code>float</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setFloat(String path, float value);

  /**
   * Sets the value of a <code>int</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setInt(String path, int value);

  /**
   * Sets the value of a <code>long</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setLong(String path, long value);

  /**
   * Sets the value of a <code>short</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setShort(String path, short value);

  /**
   * Sets the value of a <code>byte[]</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setBytes(String path, byte[] value);

  /**
   * Sets the value of a <code>BigDecimal</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setBigDecimal(String path, BigDecimal value);

  /**
   * Sets the value of a <code>BigInteger</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setBigInteger(String path, BigInteger value);

  /**
   * Sets the value of a <code>DataObject</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setDataObject(String path, DataObject value);

  /**
   * Sets the value of a <code>Date</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setDate(String path, Date value);

  /**
   * Sets the value of a <code>String</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   */
  void setString(String path, String value);

  /**
   * Sets the value of a <code>List</code> property identified by the specified path, to the specified value.
   * @param path the path to a valid object and property.
   * @param value the new value for the property.
   * @see #set(String, Object)
   * @see #setList(Property, List)
   */
  void setList(String path, List value);

  /**
   * Returns the value of the property at the specified index in {@link Type#getProperties property list} 
   * of this object's {@link Type type}.
   * @param propertyIndex the index of the property.
   * @return the value of the specified property.
   * @see #get(Property)
   */
  Object get(int propertyIndex);

  /**
   * Sets the property at the specified index in {@link Type#getProperties property list} of this object's
   * {@link Type type}, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void set(int propertyIndex, Object value);

  /**
   * Returns whether the the property at the specified index in {@link Type#getProperties property list} of this object's
   * {@link Type type}, is considered to be set.
   * @param propertyIndex the index of the property.
   * @return whether the specified property is set.
   * @see #isSet(Property)
   */
  boolean isSet(int propertyIndex);

  /**
   * Unsets the property at the specified index in {@link Type#getProperties property list} of this object's {@link Type type}.
   * @param propertyIndex the index of the property.
   * @see #unset(Property)
   */
  void unset(int propertyIndex);

  /**
   * Returns the value of a <code>boolean</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>boolean</code> value of the specified property.
   * @see #get(int)
   */
  boolean getBoolean(int propertyIndex);

  /**
   * Returns the value of a <code>byte</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>byte</code> value of the specified property.
   * @see #get(int)
   */
  byte getByte(int propertyIndex);

  /**
   * Returns the value of a <code>char</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>char</code> value of the specified property.
   * @see #get(int)
   */
  char getChar(int propertyIndex);

  /**
   * Returns the value of a <code>double</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>double</code> value of the specified property.
   * @see #get(int)
   */
  double getDouble(int propertyIndex);

  /**
   * Returns the value of a <code>float</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>float</code> value of the specified property.
   * @see #get(int)
   */
  float getFloat(int propertyIndex);

  /**
   * Returns the value of a <code>int</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>int</code> value of the specified property.
   * @see #get(int)
   */
  int getInt(int propertyIndex);

  /**
   * Returns the value of a <code>long</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>long</code> value of the specified property.
   * @see #get(int)
   */
  long getLong(int propertyIndex);

  /**
   * Returns the value of a <code>short</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>short</code> value of the specified property.
   * @see #get(int)
   */
  short getShort(int propertyIndex);

  /**
   * Returns the value of a <code>byte[]</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>byte[]</code> value of the specified property.
   * @see #get(int)
   */
  byte[] getBytes(int propertyIndex);

  /**
   * Returns the value of a <code>BigDecimal</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>BigDecimal</code> value of the specified property.
   * @see #get(int)
   */
  BigDecimal getBigDecimal(int propertyIndex);

  /**
   * Returns the value of a <code>BigInteger</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>BigInteger</code> value of the specified property.
   * @see #get(int)
   */
  BigInteger getBigInteger(int propertyIndex);

  /**
   * Returns the value of a <code>DataObject</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>DataObject</code> value of the specified property.
   * @see #get(int)
   */
  DataObject getDataObject(int propertyIndex);

  /**
   * Returns the value of a <code>Date</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>Date</code> value of the specified property.
   * @see #get(int)
   */
  Date getDate(int propertyIndex);

  /**
   * Returns the value of a <code>String</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>String</code> value of the specified property.
   * @see #get(int)
   */
  String getString(int propertyIndex);

  /**
   * Returns the value of a <code>List</code> property identified by the specified property index.
   * @param propertyIndex the index of the property.
   * @return the <code>List</code> value of the specified property.
   * @see #get(int)
   */
  List getList(int propertyIndex);

  /**
   * @see #getSequence()
   * Returns the value of a <code>Sequence</code> property identified by the specified property index.
   * An implementation may throw an UnsupportedOperationException.
   * @param propertyIndex the index of the property.
   * @return the <code>Sequence</code> value of the specified property.
   * @see #get(int)
   * @deprecated in 2.1.0.
   */
  Sequence getSequence(int propertyIndex);

  /**
   * Sets the value of a <code>boolean</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setBoolean(int propertyIndex, boolean value);

  /**
   * Sets the value of a <code>byte</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setByte(int propertyIndex, byte value);

  /**
   * Sets the value of a <code>char</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setChar(int propertyIndex, char value);

  /**
   * Sets the value of a <code>double</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setDouble(int propertyIndex, double value);

  /**
   * Sets the value of a <code>float</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setFloat(int propertyIndex, float value);

  /**
   * Sets the value of a <code>int</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setInt(int propertyIndex, int value);

  /**
   * Sets the value of a <code>long</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setLong(int propertyIndex, long value);

  /**
   * Sets the value of a <code>short</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setShort(int propertyIndex, short value);

  /**
   * Sets the value of a <code>byte[]</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setBytes(int propertyIndex, byte[] value);

  /**
   * Sets the value of a <code>BigDecimal</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setBigDecimal(int propertyIndex, BigDecimal value);

  /**
   * Sets the value of a <code>BigInteger</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setBigInteger(int propertyIndex, BigInteger value);

  /**
   * Sets the value of a <code>DataObject</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setDataObject(int propertyIndex, DataObject value);

  /**
   * Sets the value of a <code>Date</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setDate(int propertyIndex, Date value);

  /**
   * Sets the value of a <code>String</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   */
  void setString(int propertyIndex, String value);

  /**
   * Sets the value of a <code>List</code> property identified by the specified property index, to the specified value.
   * @param propertyIndex the index of the property.
   * @param value the new value for the property.
   * @see #set(int, Object)
   * @see #setList(Property, List)
   */
  void setList(int propertyIndex, List value);

  /**
   * Returns the value of the given property of this object.
   * <p>
   * If the property is {@link Property#isMany many-valued},
   * the result will be a {@link java.util.List}
   * and each object in the List will be {@link Type#isInstance an instance of} 
   * the property's {@link Property#getType type}.
   * Otherwise the result will directly be an instance of the property's type.
   * @param property the property of the value to fetch.
   * @return the value of the given property of the object.
   * @see #set(Property, Object)
   * @see #unset(Property)
   * @see #isSet(Property)
   */
  Object get(Property property);

  /**
   * Sets the value of the given property of the object to the new value.
   * <p>
   * If the property is {@link Property#isMany many-valued},
   * the new value must be a {@link java.util.List}
   * and each object in that list must be {@link Type#isInstance an instance of} 
   * the property's {@link Property#getType type};
   * the existing contents are cleared and the contents of the new value are added.
   * Otherwise the new value directly must be an instance of the property's type
   * and it becomes the new value of the property of the object.
   * @param property the property of the value to set.
   * @param value the new value for the property.
   * @see #unset(Property)
   * @see #isSet(Property)
   * @see #get(Property)
   */
  void set(Property property, Object value);

  /**
   * Returns whether the property of the object is considered to be set.
   * <p>
   * isSet() for many-valued Properties returns true if the List is not empty and 
   * false if the List is empty. For single-valued Properties it returns true if the Property
   * has been set() and not unset(), and false otherwise.
   * Any call to set() without a call to unset() will cause isSet() to return true, regardless of
   * the value being set. For example, after calling set(property, property.getDefault()) on a
   * previously unset property, isSet(property) will return true, even though the value of
   * get(property) will be unchanged.
   * @param property the property in question.
   * @return whether the property of the object is set.
   * @see #set(Property, Object)
   * @see #unset(Property)
   * @see #get(Property)
   */
  boolean isSet(Property property);

  /**
   * Unsets the property of the object.
   * <p>
   * If the property is {@link Property#isMany many-valued},
   * the value must be an {@link java.util.List}
   * and that list is cleared.
   * Otherwise, 
   * the value of the property of the object 
   * is set to the property's {@link Property#getDefault default value}.
   * The property will no longer be considered {@link #isSet set}.
   * @param property the property in question.
   * @see #isSet(Property)
   * @see #set(Property, Object)
   * @see #get(Property)
   */
  void unset(Property property);

  /**
   * Returns the value of the specified <code>boolean</code> property.
   * @param property the property to get.
   * @return the <code>boolean</code> value of the specified property.
   * @see #get(Property)
   */
  boolean getBoolean(Property property);

  /**
   * Returns the value of the specified <code>byte</code> property.
   * @param property the property to get.
   * @return the <code>byte</code> value of the specified property.
   * @see #get(Property)
   */
  byte getByte(Property property);

  /**
   * Returns the value of the specified <code>char</code> property.
   * @param property the property to get.
   * @return the <code>char</code> value of the specified property.
   * @see #get(Property)
   */
  char getChar(Property property);

  /**
   * Returns the value of the specified <code>double</code> property.
   * @param property the property to get.
   * @return the <code>double</code> value of the specified property.
   * @see #get(Property)
   */
  double getDouble(Property property);

  /**
   * Returns the value of the specified <code>float</code> property.
   * @param property the property to get.
   * @return the <code>float</code> value of the specified property.
   * @see #get(Property)
   */
  float getFloat(Property property);

  /**
   * Returns the value of the specified <code>int</code> property.
   * @param property the property to get.
   * @return the <code>int</code> value of the specified property.
   * @see #get(Property)
   */
  int getInt(Property property);

  /**
   * Returns the value of the specified <code>long</code> property.
   * @param property the property to get.
   * @return the <code>long</code> value of the specified property.
   * @see #get(Property)
   */
  long getLong(Property property);

  /**
   * Returns the value of the specified <code>short</code> property.
   * @param property the property to get.
   * @return the <code>short</code> value of the specified property.
   * @see #get(Property)
   */
  short getShort(Property property);

  /**
   * Returns the value of the specified <code>byte[]</code> property.
   * @param property the property to get.
   * @return the <code>byte[]</code> value of the specified property.
   * @see #get(Property)
   */
  byte[] getBytes(Property property);

  /**
   * Returns the value of the specified <code>BigDecimal</code> property.
   * @param property the property to get.
   * @return the <code>BigDecimal</code> value of the specified property.
   * @see #get(Property)
   */
  BigDecimal getBigDecimal(Property property);

  /**
   * Returns the value of the specified <code>BigInteger</code> property.
   * @param property the property to get.
   * @return the <code>BigInteger</code> value of the specified property.
   * @see #get(Property)
   */
  BigInteger getBigInteger(Property property);

  /**
   * Returns the value of the specified <code>DataObject</code> property.
   * @param property the property to get.
   * @return the <code>DataObject</code> value of the specified property.
   * @see #get(Property)
   */
  DataObject getDataObject(Property property);

  /**
   * Returns the value of the specified <code>Date</code> property.
   * @param property the property to get.
   * @return the <code>Date</code> value of the specified property.
   * @see #get(Property)
   */
  Date getDate(Property property);

  /**
   * Returns the value of the specified <code>String</code> property.
   * @param property the property to get.
   * @return the <code>String</code> value of the specified property.
   * @see #get(Property)
   */
  String getString(Property property);

  /**
   * Returns the value of the specified <code>List</code> property.
   * The List returned contains the current values.
   * Updates through the List interface operate on the current values of the DataObject.
   * Each access returns the same List object.
   * @param property the property to get.
   * @return the <code>List</code> value of the specified property.
   * @see #get(Property)
   */
  List getList(Property property);

  /**
   * @see #getSequence()
   * Returns the value of the specified <code>Sequence</code> property.
   * An implementation may throw an UnsupportedOperationException.
   * @param property the property to get.
   * @return the <code>Sequence</code> value of the specified property.
   * @see #get(Property)
   * @deprecated in 2.1.0.
   */
  Sequence getSequence(Property property);

  /**
   * Sets the value of the specified <code>boolean</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setBoolean(Property property, boolean value);

  /**
   * Sets the value of the specified <code>byte</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setByte(Property property, byte value);

  /**
   * Sets the value of the specified <code>char</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setChar(Property property, char value);

  /**
   * Sets the value of the specified <code>double</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setDouble(Property property, double value);

  /**
   * Sets the value of the specified <code>float</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setFloat(Property property, float value);

  /**
   * Sets the value of the specified <code>int</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setInt(Property property, int value);

  /**
   * Sets the value of the specified <code>long</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setLong(Property property, long value);

  /**
   * Sets the value of the specified <code>short</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setShort(Property property, short value);

  /**
   * Sets the value of the specified <code>byte[]</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setBytes(Property property, byte[] value);

  /**
   * Sets the value of the specified <code>BigDecimal</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setBigDecimal(Property property, BigDecimal value);

  /**
   * Sets the value of the specified <code>BigInteger</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setBigInteger(Property property, BigInteger value);

  /**
   * Sets the value of the specified <code>DataObject</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setDataObject(Property property, DataObject value);

  /**
   * Sets the value of the specified <code>Date</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setDate(Property property, Date value);

  /**
   * Sets the value of the specified <code>String</code> property, to the specified value.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setString(Property property, String value);

  /**
   * Sets the value of the specified <code>List</code> property, to the specified value.
   * <p> The new value must be a {@link java.util.List}
   * and each object in that list must be {@link Type#isInstance an instance of} 
   * the property's {@link Property#getType type};
   * the existing contents are cleared and the contents of the new value are added.
   * @param property the property to set.
   * @param value the new value for the property.
   * @see #set(Property, Object)
   */
  void setList(Property property, List value);

  /**
   * Returns a new {@link DataObject data object} contained by this object using the specified property,
   * which must be a {@link Property#isContainment containment property}.
   * The type of the created object is the {@link Property#getType declared type} of the specified property.
   * @param propertyName the name of the specified containment property.
   * @return the created data object.
   * @see #createDataObject(String, String, String)
   */
  DataObject createDataObject(String propertyName);

  /**
   * Returns a new {@link DataObject data object} contained by this object using the specified property,
   * which must be a {@link Property#isContainment containment property}.
   * The type of the created object is the {@link Property#getType declared type} of the specified property.
   * @param propertyIndex the index of the specified containment property.
   * @return the created data object.
   * @see #createDataObject(int, String, String)
   */
  DataObject createDataObject(int propertyIndex);

  /**
   * Returns a new {@link DataObject data object} contained by this object using the specified property,
   * which must be a {@link Property#isContainment containment property}.
   * The type of the created object is the {@link Property#getType declared type} of the specified property.
   * @param property the specified containment property.
   * @return the created data object.
   * @see #createDataObject(Property, Type)
   */
  DataObject createDataObject(Property property);

  /**
   * Returns a new {@link DataObject data object} contained by this object using the specified property,
   * which must be a {@link Property#isContainment containment property}.
   * The type of the created object is specified by the packageURI and typeName arguments.
   * The specified type must be a compatible target for the property identified by propertyName.
   * @param propertyName the name of the specified containment property.
   * @param namespaceURI the namespace URI of the package containing the type of object to be created.
   * @param typeName the name of a type in the specified package.
   * @return the created data object.
   * @see #createDataObject(String)
   * @see commonj.sdo.helper.TypeHelper#getType
   */
  DataObject createDataObject(String propertyName, String namespaceURI, String typeName);

  /**
   * Returns a new {@link DataObject data object} contained by this object using the specified property,
   * which must be a {@link Property#isContainment containment property}.
   * The type of the created object is specified by the packageURI and typeName arguments.
   * The specified type must be a compatible target for the property identified by propertyIndex.
   * @param propertyIndex the index of the specified containment property.
   * @param namespaceURI the namespace URI of the package containing the type of object to be created.
   * @param typeName the name of a type in the specified package.
   * @return the created data object.
   * @see #createDataObject(int)
   * @see commonj.sdo.helper.TypeHelper#getType
   */
  DataObject createDataObject(int propertyIndex, String namespaceURI, String typeName);

  /**
   * Returns a new {@link DataObject data object} contained by this object using the specified property,
   * which must be of {@link Property#isContainment containment type}.
   * The type of the created object is specified by the type argument,
   * which must be a compatible target for the speicifed property.
   * @param property a containment property of this object.
   * @param type the type of object to be created.
   * @return the created data object.
   * @see #createDataObject(int)
   */
  DataObject createDataObject(Property property, Type type);

  /**
   * Remove this object from its container and then unset all its non-{@link Property#isReadOnly readOnly} Properties.
   * If this object is contained by a {@link Property#isReadOnly readOnly} {@link Property#isContainment containment property}, its non-{@link Property#isReadOnly readOnly} Properties will be unset but the object will not be removed from its container.
   * All DataObjects recursively contained by {@link Property#isContainment containment Properties} will also be deleted.
   */
  void delete();

  /**
   * Returns the containing {@link DataObject data object}
   * or <code>null</code> if there is no container.
   * @return the containing data object or <code>null</code>.
   */
  DataObject getContainer();

  /**
   * Return the Property of the {@link DataObject data object} containing this data object
   * or <code>null</code> if there is no container.
   * @return the property containing this data object.
   */
  Property getContainmentProperty();

  /**
   * Returns the {@link DataGraph data graph} for this object or <code>null</code> if there isn't one.
   * @return the containing data graph or <code>null</code>.
   * @deprecated
   */
  DataGraph getDataGraph();

  /**
   * Returns the data object's type.
   * <p>
   * The type defines the Properties available for reflective access.
   * @return the type.
   */
  Type getType();

  /**
   * Returns the <code>Sequence</code> for this DataObject.
   * When getType().isSequencedType() == true,
   * the Sequence of a DataObject corresponds to the
   * XML elements representing the values of its Properties.
   * Updates through DataObject and the Lists or Sequences returned
   * from DataObject operate on the same data.
   * When getType().isSequencedType() == false, null is returned.  
   * @return the <code>Sequence</code> or null.
   */
  Sequence getSequence();
  
  /**
   * Returns a read-only List of the Properties currently used in this DataObject.
   * This list will contain all of the Properties in getType().getProperties()
   * and any Properties where isSet(property) is true.
   * For example, Properties resulting from the use of
   * open or mixed XML content are present if allowed by the Type.
   * the List does not contain duplicates. 
   * The order of the Properties in the List begins with getType().getProperties()
   * and the order of the remaining Properties is determined by the implementation.
   * The same list will be returned unless the DataObject is updated so that 
   * the contents of the List change.
   * @return the List of Properties currently used in this DataObject.
   */
  List /* Property */ getInstanceProperties();

  /**
   * Returns the named Property from the current instance properties,
   * or null if not found.  The instance properties are getInstanceProperties().  
   * @param propertyName the name of the Property
   * @return the named Property from the DataObject's current instance properties, or null.
   */
  Property getInstanceProperty(String propertyName);

  /**
   * @deprecated replaced by {@link #getInstanceProperty(String)} in 2.1.0
   */
  Property getProperty(String propertyName);

  /**
   * Returns the root {@link DataObject data object}.
   * @return the root data object.
   */
  DataObject getRootObject();

  /**
   * Returns the ChangeSummary with scope covering this dataObject, or null
   * if there is no ChangeSummary. 
   * @return the ChangeSummary with scope covering this dataObject, or null.
   */
  ChangeSummary getChangeSummary();

  /**
   * Removes this DataObject from its container, if any.
   * Same as 
   *  getContainer().getList(getContainmentProperty()).remove(this) or
   *  getContainer().unset(getContainmentProperty())
   * depending on getContainmentProperty().isMany() respectively.
   */
  void detach();
}
