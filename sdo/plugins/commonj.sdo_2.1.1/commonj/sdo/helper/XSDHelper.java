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

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.impl.HelperProvider;

/**
 * Provides access to additional information when the 
 * Type or Property is defined by an XML Schema (XSD).
 * In case Types or Properties are not defined from an XML Schema,
 * methods return information corresponding to what would be the generated XSD.
 * Defines Types from an XSD.
*/
public interface XSDHelper
{
  /**
   * Returns the local name as declared in the XSD.
   * @param type to return local name for.
   * @return the local name as declared in the XSD.  If <code>type</code>
   * is one of the predefined SDO types, the result is undefined.
   */
  String getLocalName(Type type);

  /**
   * Returns the local name as declared in the XSD.
   * @param property to return local name for.
   * @return the local name as declared in the XSD.
   */
  String getLocalName(Property property);
  
  /**
   * Returns the namespace URI as declared in the XSD. If the XSD has
   * no namespace URI, then <code>""</code> is returned.
   * @param property to return namespace URI for.
   * @return the namespace URI as declared in the XSD.
   */
  String getNamespaceURI(Property property);

  /**
   * Returns the namespace URI as declared in the XSD. If the XSD has
   * no namespace URI, then <code>""</code> is returned.
   * @param type to return namespace URI for
   * @return the namespace URI as declared in the XSD.
   */
  String getNamespaceURI(Type type);
  
  /**
   * Returns true if the property is declared as an attribute in the XSD.
   * Returns false if not known or for advanced cases.
   * It is possible for both isAttribute and isElement to return false
   * but they will not both return true.
   * @param property to identify if an attribute.
   * @return true if the property is declared as an attribute in the XSD.
   */
  boolean isAttribute(Property property);
  
  /**
   * Returns true if the property is declared as an element in the XSD.
   * Returns false if not known or for advanced cases.
   * It is possible for both isAttribute and isElement to return false
   * but they will not both return true.
   * @param property to identify if an element.
   * @return true if the property is declared as an element in the XSD.
   */
  boolean isElement(Property property);

  /**
   * Returns true if the Type is declared to contain mixed content.  
   * A DataObject's mixed content values are typically accessed via a Sequence.
   * @param type to identify if mixed content.
   * @return true if the Type is declared to contain mixed content.
   */
  boolean isMixed(Type type);

  /**
   * Indicates if this helper contains XSD information for the specified type.
   * @param type the type.
   * @return true if this helper contains XSD information for the specified type.
   */
  boolean isXSD(Type type);
  
  /**
   * Returns the Property defined by the named global element or attribute 
   *   in the targetNamespace uri, or null if not found. If the SDO name for a
   * property is different than the XSD name, then only the XSD name is used
   * for the lookup. Passing in <code>null</code> or <code>""</code> for the
   * <code>uri</code> parameter means that an element/attribute defined in a
   * Schema with no target namespace will be returned.
   * @param uri The uri of the targetNamespace.
   * @param propertyName The name of the global property.
   * @param isElement is true for global elements, false for global attributes.
   * @return the Property defined by the named global element or attribute
   *   in the targetNamespace uri, or null if not found.
   */
  Property getGlobalProperty(String uri, String propertyName, boolean isElement);
  
  /**
   * Return the appinfo declared for this Type and source.
   * The appinfo start and end tags and content are returned.
   * The xml namespace context is preserved in the appinfo element.
   * If more than one appinfo with the same source is declared on the same
   * Type their contents are concatenated.
   * @param type the type with the appinfo declaration
   * @param source the source of the appinfo declaration.
   * @return the appinfo declared for this Type and source.
   */
  String getAppinfo(Type type, String source);

  /**
   * Return the content of the appinfo declared for this Property and source.
   * If the property is defined by ref= the appinfo of the referenced
   * element or attribute is included.
   * The appinfo start and end tags and content are returned.
   * The xml namespace context is preserved in the appinfo element.
   * If more than one appinfo with the same source is declared on the same
   * Type their contents are concatenated.
   * @param property the Property with the appinfo declaration
   * @param source the source of the appinfo declaration.
   * @return the appinfo declared for this Property and source.
   */
  String getAppinfo(Property property, String source);

  /**
   * Define the XML Schema as Types.
   * The Types are available through {@link TypeHelper#getType} methods.
   * Same as define(new StringReader(xsd), null)
   * @param xsd the XML Schema.
   * @return the defined Types.
   * @throws IllegalArgumentException if the Types could not be defined.
   */
  List /*Type*/ define(String xsd);
  
  /**
   * Define XML Schema as Types.
   * The Types are available through {@link TypeHelper#getType} methods.
   * @param xsdReader reader to an XML Schema.
   * @param schemaLocation the URI of the location of the schema, used 
   *   for processing relative imports and includes.  May be null if not used.
   * @return the defined Types.
   * @throws IllegalArgumentException if the Types could not be defined.
   */
  List /*Type*/ define(Reader xsdReader, String schemaLocation);

  /**
   * Define XML Schema as Types.
   * The Types are available through {@link TypeHelper#getType} methods.
   * @param xsdInputStream input stream to an XML Schema.
   * @param schemaLocation the URI of the location of the schema, used 
   *   for processing relative imports and includes.  May be null if not used.
   * @return the defined Types.
   * @throws IllegalArgumentException if the Types could not be defined.
   */
  List /*Type*/ define(InputStream xsdInputStream, String schemaLocation);

  /**
   * Generate an XML Schema Declaration (XSD) from Types.
   * Same as generate(types, null);
   * @param types a List containing the Types
   * @return a String containing the generated XSD. 
   * @throws IllegalArgumentException if the XSD could not be generated.
   */
  String generate(List /*Type*/ types);
  
  /**
   * Generate an XML Schema Declaration (XSD) from Types.
   * Round trip from SDO to XSD to SDO is supported.
   * Round trip from XSD to SDO to XSD is not supported.
   *  Use the original schema if one exists instead of generating a new one, as
   *  the generated XSD validates a different set of documents than the original XSD.
   * Generating an XSD does not affect the XSDHelper or the Types.
   * The Types must all have the same URI.
   * The result is a String containing the generated XSD. 
   * All Types referenced with the same URI will be generated in the XSD
   *  and the list will be expanded to include all types generated.
   * Any Types referenced with other URIs will cause 
   *  imports to be produced as appropriate.
   * Imports will include a schemaLocation if a Map is provided with an entry
   *  of the form key=import target namespace, value=schemaLocation
   * @param types a List containing the Types
   * @param namespaceToSchemaLocation map of target namespace to schema locations or null
   * @return a String containing the generated XSD. 
   * @throws IllegalArgumentException if the XSD could not be generated.
   */
  String generate(List /*Type*/ types, Map /*String, String*/ namespaceToSchemaLocation);

  /**
   * The default XSDHelper.
   */
  XSDHelper INSTANCE = HelperProvider.getXSDHelper();
}
