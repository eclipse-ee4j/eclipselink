/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLRoot;

/**
 * <p><b>Purpose</b>: Represents an XML Document containing a tree of DataObjects.
 *
 * <p>An example XMLDocument fragment is:<br>
 * &nbsp;{@literal <?xml version="1.0"?>}<br>
 * &nbsp;{@literal <purchaseOrder orderDate="1999-10-20">}
 *
 * <p>created from this XML Schema fragment:<br>
 * &nbsp;{@literal <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">}<br>
 * &nbsp;{@literal <xsd:element name="purchaseOrder" type="PurchaseOrderType"/>}<br>
 * &nbsp;{@literal <xsd:complexType name="PurchaseOrderType">}
 *
 * <p>Upon loading this XMLDocument:<br>
 * &nbsp;DataObject is an instance of Type PurchaseOrderType.<br>
 * &nbsp;RootElementURI is null because the XSD has no targetNamespace URI.<br>
 * &nbsp;RootElementName is purchaseOrder.<br>
 * &nbsp;Encoding is null because the document did not specify an encoding.<br>
 * &nbsp;XMLDeclaration is true because the document contained an XML declaration.<br>
 * &nbsp;XMLVersion is 1.0<br>
 * &nbsp;SchemaLocation and noNamespaceSchemaLocation are null because they are
 *     not specified in the document.
 *
 * <p>When saving the root element, if the type of the root dataObject is not the
 *   type of global element specified by rootElementURI and rootElementName,
 *   or if a global element does not exist for rootElementURI and rootElementName,
 *   then an xsi:type declaration is written to record the root DataObject's Type.
 *
 * <p>When loading the root element and an xsi:type declaration is found
 *   it is used as the type of the root DataObject.  In this case,
 *   if validation is not being performed, it is not an error if the
 *   rootElementName is not a global element.
 */
public class SDOXMLDocument extends XMLRoot implements XMLDocument {
    //default values defined in the spec
    public final static String DEFAULT_XML_ENCODING = "UTF-8";
    public final static String DEFAULT_XML_VERSION = "1.0";
    private boolean xmlDeclaration;
    private NamespaceResolver namespaceResolver;

    public SDOXMLDocument() {
        xmlDeclaration = true;
        namespaceResolver = new NamespaceResolver();
    }

    /**
     * Return the root DataObject for the XMLDocument.
     * @return root DataObject for the XMLDocument.
     */
    @Override
    public DataObject getRootObject() {
        return (DataObject)rootObject;
    }

    /**
     * Return the targetNamespace URI for the root element.
     * If there is no targetNamespace URI, the value is null.
     * The root element is a global element of the XML Schema
     *   with a type compatible to the DataObject.
     * @return the targetNamespace URI for the root element.
     */
    @Override
    public String getRootElementURI() {
        return super.getNamespaceURI();
    }

    /**
     * Return the name of the root element.
     * The root element is a global element of the XML Schema
     *   with a type compatible to the DataObject.
     * @return the name of the root element.
     */
    @Override
    public String getRootElementName() {
        return super.getLocalName();
    }

    /**
     * Return the XML encoding of the document, or null if not specified.
     * The default value is "UTF-8".
     * Specification of other values is implementation-dependent.
     * @return the XML encoding of the document, or null if not specified.
     */
    @Override
    public String getEncoding() {
        return encoding;
    }

    /**
     * Set the XML encoding of the document, or null if not specified.
     * @param encoding
     */
    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Return the XML declaration of the document.  If true,
     *   XMLHelper save() will produce a declaration of the form:
     * {@literal <?xml version="1.0" encoding="UTF-8"?>}
     *   Encoding will be suppressed if getEncoding() is null.
     * The default value is true.
     * @return the XML declaration of the document.
     */
    @Override
    public boolean isXMLDeclaration() {
        return xmlDeclaration;
    }

    /**
      * Set the XML declaration version of the document.
      * @param xmlDeclaration the XML declaration version of the document.
      */
    @Override
    public void setXMLDeclaration(boolean xmlDeclaration) {
        this.xmlDeclaration = xmlDeclaration;
    }

    /**
     * Return the XML version of the document, or null if not specified.
     * The default value is "1.0".
     * Specification of other values is implementation-dependent.
     * @return the XML version of the document, or null if not specified.
     */
    @Override
    public String getXMLVersion() {
        return xmlVersion;
    }

    /**
     * Set the XML version of the document, or null if not specified.
     * @param xmlVersion the XML version of the document, or null if not specified.
     */
    @Override
    public void setXMLVersion(String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }

    /**
     * Return the value of the schemaLocation declaration
     * for the http://www.w3.org/2001/XMLSchema-instance namespace in the
     * root element, or null if not present.
     * @return the value of the schemaLocation declaration,
     *  or null if not present.
     */
    @Override
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * Sets the value of the schemaLocation declaration
     * for the http://www.w3.org/2001/XMLSchema-instance namespace in the
     * root element, or null if it should not be present.
     * @param schemaLocation the value of the schemaLocation declaration, or null.
     */
    @Override
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;

    }

    /**
     * Return the value of the noNamespaceSchemaLocation declaration
     * for the http://www.w3.org/2001/XMLSchema-instance namespace in the
     * root element, or null if not present.
     * @return the value of the noNamespaceSchemaLocation declaration,
     *  or null if not present.
     */
    @Override
    public String getNoNamespaceSchemaLocation() {
        return noNamespaceSchemaLocation;
    }

    /**
     * Sets the value of the noNamespaceSchemaLocation declaration
     * for the http://www.w3.org/2001/XMLSchema-instance namespace in the
     * root element, or null if it should not be present.
     * @param schemaLocation the value of the noNamespaceSchemaLocation declaration, or null.
     */
    @Override
    public void setNoNamespaceSchemaLocation(String schemaLocation) {
        noNamespaceSchemaLocation = schemaLocation;
    }

    /**
      * INTERNAL:
      * Set the root data object corresponding to the document
      * @param rootObject the root DataObject corresponding to the document
      */
    public void setRootObject(DataObject rootObject) {
        this.rootObject = rootObject;
    }

    /**
      * INTERNAL:
      * Set the root element name of the document
      * @param rootElementName the root element name of the document
      */
    public void setRootElementName(String rootElementName) {
        super.setLocalName(rootElementName);
    }

    /**
      * INTERNAL:
      * Set the root element uri of the document
      * @param rootElementUri the root element uri of the document
      */
    public void setRootElementURI(String rootElementUri) {
        super.setNamespaceURI(rootElementUri);
    }

    /**
      * INTERNAL:
      * Set the namespaceresolver of the document
      * @param namespaceResolver the namespaceResolver to be used with the document
      */
    public void setNamespaceResolver(NamespaceResolver namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }

    /**
      * INTERNAL:
      * Return the namespaceResolver to be used with this document.
      * @return the value of the namespaceResolver being used with this document
      */
    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }
}
