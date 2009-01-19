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

import java.util.List;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.impl.HelperProvider;

/**
 * Look up a Type given the uri and typeName or interfaceClass.
 * SDO Types are available through the
 *   getType("commonj.sdo", typeName) method.
 * Defines Types from DataObjects.
 */
public interface TypeHelper
{
  /**
   * Return the Type specified by typeName with the given uri,
   *   or null if not found. If the XSD uri (in the case of built-in Schema types)
   * or the XSD typeName (in case an sdo:name annotation has been used)
   * are different from the SDO name and uri (as returned by type.getURI())
   * and type.getName()), only the SDO uri and name are used for the lookup.
   * <br/>If <code>null</code> or <code>""</code> is passed as the value of the
   * <code>uri</code> parameter, then a type with no URI will be returned,
   * if found.
   * @param uri The uri of the Type - type.getURI();
   * @param typeName The name of the Type - type.getName();
   * @return the Type specified by typeName with the given uri,
   *   or null if not found.
   */
  Type getType(String uri, String typeName);
  
  /**
   * Return the Type for this interfaceClass or null if not found.
   * @param interfaceClass is the interface for the DataObject's Type -  
   *   type.getInstanceClass();
   * @return the Type for this interfaceClass or null if not found.
   */
  Type getType(Class interfaceClass);
  
  /**
   * Get the open content (global) Property with the specified uri and name, or null
   * if not found. If the Schema name of the Property is different than its SDO name,
   * only the SDO name is used for the lookup.
   * <br/>If <code>null</code> or <code>""</code> is passed as the value of the
   * <code>uri</code> parameter, then a Property with no URI will be returned.
   * (for example, a property mapped from a global element in an XSD with no target namespace)
   * @param uri the namespace URI of the open content Property.
   * @param propertyName the name of the open content Property.
   * @return the global Property.
   */
  Property getOpenContentProperty(String uri, String propertyName);
  
  /**
   * Define the DataObject as a Type.
   * The Type is available through {@link TypeHelper#getType} methods.
   * If a type with the same name already exists, it is returned and no new definition takes place.
   * If the <code>uri</code> property of the type to be defined is set to <code>""</code>, then the
   * resulting type will have no uri, same as if the <code>uri</code> property was set to <code>null</code>.
   * @param type the DataObject representing the Type.
   * @return the defined Type.
   * @throws IllegalArgumentException if the Type could not be defined.
   */
  Type define(DataObject type);

  /**
   * Define the list of DataObjects as Types.
   * The Types are available through {@link TypeHelper#getType} methods.
   * The output list will contain, for every item in the input list, either 
   * the Type newly defined or a pre-existing Type in case a Type with the 
   * given name already exists,  followed by any other types defined as a 
   * result of this call.
   * 
   * @param types a List of DataObjects representing the Types.
   * @return the defined Types.
   * @throws IllegalArgumentException if the Types could not be defined.
   */
  List /*Type*/ define(List /*DataObject*/ types);

  /**
   * Define the DataObject as a Property for setting open content.
   * The containing Type of the open content property is not specified by SDO.
   * If the specified uri is not null the defined property is accessible through
   * TypeHelper.getOpenContentProperty(uri, propertyName).
   * If a null uri is specified, the location and management of the open content property
   * is not specified by SDO.
   * @param uri the namespace URI of the open content Property or null.
   * @return the defined open content Property.
   * @throws IllegalArgumentException if the Property could not be defined.
   */
  Property defineOpenContentProperty(String uri, DataObject property);

  /**
   * The default TypeHelper.
   */
  TypeHelper INSTANCE = HelperProvider.getTypeHelper();
}
