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

/**
 * Represents an XML Document containing a tree of DataObjects.
 * 
 * An example XMLDocument fragment is:
 * <?xml version="1.0"?>
 * <purchaseOrder orderDate="1999-10-20">
 * 
 * created from this XML Schema fragment:
 * <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">
 *   <xsd:element name="purchaseOrder" type="PurchaseOrderType"/>
 *   <xsd:complexType name="PurchaseOrderType">
 *
 * Upon loading this XMLDocument:
 *   DataObject is an instance of Type PurchaseOrderType.
 *   RootElementURI is null because the XSD has no targetNamespace URI.
 *   RootElementName is purchaseOrder.
 *   Encoding is null because the document did not specify an encoding.
 *   XMLDeclaration is true because the document contained an XML declaration.
 *   XMLVersion is 1.0
 *   SchemaLocation and noNamespaceSchemaLocation are null because they are
 *     not specified in the document.
 * 
 * When saving the root element, if the type of the root dataObject is not the
 *   type of global element specified by rootElementURI and rootElementName, 
 *   or if a global element does not exist for rootElementURI and rootElementName,
 *   then an xsi:type declaration is written to record the root DataObject's Type.
 * 
 * When loading the root element and an xsi:type declaration is found
 *   it is used as the type of the root DataObject.  In this case,
 *   if validation is not being performed, it is not an error if the
 *   rootElementName is not a global element.
 */
public interface XMLDocument
{
  /**
   * Return the root DataObject for the XMLDocument.
   * @return root DataObject for the XMLDocument.
   */
  DataObject getRootObject();
  
  /**
   * Return the targetNamespace URI for the root element.
   * If there is no targetNamespace URI, the value is null.
   * The root element is a global element of the XML Schema
   *   with a type compatible to the DataObject.
   * @return the targetNamespace URI for the root element.
   */
  String getRootElementURI();
  
  /**
   * Return the name of the root element.
   * The root element is a global element of the XML Schema
   *   with a type compatible to the DataObject.
   * @return the name of the root element.
   */
  String getRootElementName();
  
  /**
   * Return the XML encoding of the document, or null if not specified.
   * The default value is "UTF-8".
   * Specification of other values is implementation-dependent.
   * @return the XML encoding of the document, or null if not specified.
   */
  String getEncoding();
  
  /**
   * Set the XML encoding of the document, or null if not specified.
   * @param encoding
   */
  void setEncoding(String encoding);

  /**
   * Return the XML declaration of the document.  If true,
   *   XMLHelper save() will produce a declaration of the form:
   * <?xml version="1.0" encoding="UTF-8"?>
   *   Encoding will be suppressed if getEncoding() is null.
   * The default value is true.
   * @return the XML declaration of the document.
   */
  boolean isXMLDeclaration();
  
  /**
   * Set the XML declaration version of the document.
   * @param xmlDeclaration the XML declaration version of the document.
   */
  void setXMLDeclaration(boolean xmlDeclaration);
  
  /**
   * Return the XML version of the document, or null if not specified. 
   * The default value is "1.0".
   * Specification of other values is implementation-dependent.
   * @return the XML version of the document, or null if not specified.
   */
  String getXMLVersion();
  
  /**
   * Set the XML version of the document, or null if not specified.
   * @param xmlVersion the XML version of the document, or null if not specified.
   */
  void setXMLVersion(String xmlVersion);

  /**
   * Return the value of the schemaLocation declaration
   * for the http://www.w3.org/2001/XMLSchema-instance namespace in the
   * root element, or null if not present.
   * @return the value of the schemaLocation declaration,
   * or null if not present.
   */
  String getSchemaLocation();
  
  /**
   * Sets the value of the schemaLocation declaration
   * for the http://www.w3.org/2001/XMLSchema-instance namespace in the
   * root element, or null if it should not be present.
   * @param schemaLocation the value of the schemaLocation declaration, or null.
   */
  void setSchemaLocation(String schemaLocation);

  /**
   * Return the value of the noNamespaceSchemaLocation declaration
   * for the http://www.w3.org/2001/XMLSchema-instance namespace in the
   * root element, or null if not present.
   * @return the value of the noNamespaceSchemaLocation declaration,
   * or null if not present.
   */
  String getNoNamespaceSchemaLocation();
  
  /**
   * Sets the value of the noNamespaceSchemaLocation declaration
   * for the http://www.w3.org/2001/XMLSchema-instance namespace in the
   * root element, or null if it should not be present.
   * @param schemaLocation the value of the noNamespaceSchemaLocation declaration, or null.
   */
  void setNoNamespaceSchemaLocation(String schemaLocation);
}
