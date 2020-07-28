/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Denise Smith - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm.mappings;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.ConversionManager;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.AbstractUnmarshalRecord;

public interface Field<
        CONVERSION_MANAGER extends ConversionManager,
        NAMESPACE_RESOLVER extends NamespaceResolver> extends CoreField {

    /**
     * INTERNAL:
     * Called from DOMRecord and XMLReader.  MappingNodeValues call XMLReader which calls this method so that other XMLReader subclasses can override.
     */
    Object convertValueBasedOnSchemaType(Object value, CONVERSION_MANAGER xmlConversionManager, AbstractUnmarshalRecord record);

    /**
     * Return the class for a given qualified XML Schema type
     *
     * @param qname The qualified name of the XML Schema type to use as a key in the lookup
     * @return The class corresponding to the specified schema type, if no corresponding match found returns null
     */
    Class getJavaClass(QName qname, ConversionManager conversionManager);

    /**
     * INTERNAL:
     * Return the last XPathFragment.
     */
    XPathFragment getLastXPathFragment();


    QName getLeafElementType();

    /**
     * Return the unqualified name of the field.
     */
    String getName();

    /**
     * Get the NamespaceResolver associated with this XMLField
     *
     * @return The NamespaceResolver associated with this XMLField
     */
    NAMESPACE_RESOLVER getNamespaceResolver();

    /**
     * Return the schema type associated with this field
     *
     * @return the schema type
     */
    QName getSchemaType();

    /**
     * INTERNAL:
     */
    QName getSchemaTypeForValue(Object value, CoreAbstractSession session);

    Class getType();

    /**
     * Return the qualified XML Schema type for a given class
     *
     * @param javaClass The class to use as a key in the lookup
     * @return QName The qualified XML Schema type, if no corresponding match found returns null
     */
    QName getXMLType(Class javaClass, ConversionManager conversionManager);

    /**
     * Returns the xpath statement associated with this XMLField
     *
     * @return The xpath statement associated with this XMLField
     */
    String getXPath();

    /**
     * INTERNAL:
     * Maintain a direct pointer to the first XPathFragment.  For example given
     * the following XPath first/middle/@last, first is the first XPathFragment.
     */
    XPathFragment getXPathFragment();

    /**
     * INTERNAL:
     *
     * @return
     */
    boolean hasLastXPathFragment();


    void initialize();

    /**
     * INTERNAL:
     *
     * @return
     */
    boolean isCDATA();

    /**
     * Indicates if this XMLField represents a "required" XML element or attribute
     * ([minOccurs="1"] for elements, [use="required"] for attributes).  NOTE: This
     * API is used only for Schema Generation.
     *
     * @see org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator
     */
    boolean isRequired();

    /**
     * INTERNAL
     */
    boolean isSchemaType(QName schemaType);

    /**
     * INTERNAL:
     * Indicates if the xpath for this field is "."
     *
     * @return true if the xpath is ".", false otherwise
     */
    boolean isSelfField();

    /**
     * Returns if the field is a typed text field
     * True when we should base conversions on the "type" attribute on elements
     *
     * @return True when we should base conversions on the "type" attribute on elements, otherwise false
     */
    boolean isTypedTextField();

    /**
     * INTERNAL:
     * Returns false since this is a union field
     * The subclass XMLUnionField returns true for this
     */
    boolean isUnionField();

    /**
     * INTERNAL:
     *
     * @param CDATA
     */
    void setIsCDATA(boolean CDATA);

    /**
     * Set if the field is a typed text field
     * True when we should base conversions on the "type" attribute on elements
     *
     * @param value The boolean value specifiy if  this is a typed text field
     */
    void setIsTypedTextField(boolean value);

    /**
     * Set the NamespaceResolver associated with this XMLField
     *
     * @param newNamespaceResolver The namespaceResolver to be associated with this XMLField
     */
    void setNamespaceResolver(NAMESPACE_RESOLVER newNamespaceResolver);

    /**
     * Set whether this XMLField represents a "required" XML element or attribute
     * ([minOccurs="1"] for elements, [use="required"] for attributes).  NOTE: This
     * API is used only for Schema Generation.
     *
     * @see org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator
     */
    void setRequired(boolean isRequired);

    /**
     * Sets the schematype associated with this XMLField
     * This is an optional setting; when set the schema type will be used to format the XML appropriately
     *
     * @param value QName to be added to the list of schema types
     */
    void setSchemaType(QName value);

    /**
     * Sets whether the mapping uses a single node.
     *
     * @param usesSingleNode True if the items in the collection are in a single node or false if each of the items in the collection is in its own node
     */
    void setUsesSingleNode(boolean usesSingleNode);

    /**
     * Set the xpath statment for this XMLField.
     *
     * @param xPath The xpath statement to be associated with this XMLField
     */
    void setXPath(String xPath);

    /**
     * Checks whether the mapping uses a single node.
     *
     * @return True if the items in the collection are in a single node or false if each of the items in the collection is in its own node.
     */
    boolean usesSingleNode();

    /**
     * Set nested array flag. Used in JSON marshalling.
     *
     * @param nestedArray flag.
     */
    void setNestedArray(boolean nestedArray);

    /**
     * INTERNAL:
     *
     * @return True if content is nested array.
     */
    boolean isNestedArray();
}
