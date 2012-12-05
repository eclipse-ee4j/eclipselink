/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.mappings;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.oxm.NamespaceResolver;

public interface Field extends CoreField{
	
    /**
     * INTERNAL:
     * Called from DOMRecord and XMLReader.  MappingNodeValues call XMLReader which calls this method so that other XMLReader subclasses can override.
     */
     public Object convertValueBasedOnSchemaType(Object value, XMLConversionManager xmlConversionManager, XMLRecord record);
	
    /**
    * Return the class for a given qualified XML Schema type
    * @param qname The qualified name of the XML Schema type to use as a key in the lookup
    * @return The class corresponding to the specified schema type, if no corresponding match found returns null
    */
    public Class getJavaClass(QName qname);
    
	/**
     * INTERNAL:
     * Return the last XPathFragment.
     */
    public XPathFragment getLastXPathFragment();
	

    public QName getLeafElementType();
    
    /**
     * Return the unqualified name of the field.
     */    
    public String getName();
    
    /**
     * Get the NamespaceResolver associated with this XMLField
     * @return The NamespaceResolver associated with this XMLField
     * @see org.eclipse.persistence.oxm.NamespaceResolver
     */
    public NamespaceResolver getNamespaceResolver();
    
    /**
     * Return the schema type associated with this field
     * @return the schema type
     */
     public QName getSchemaType();
 	
     /**
      * INTERNAL:
      */
     public QName getSchemaTypeForValue(Object value, CoreAbstractSession session);
     
     public Class getType();
     
     /**
      * Return the qualified XML Schema type for a given class
      * @param javaClass The class to use as a key in the lookup
      * @return QName The qualified XML Schema type, if no corresponding match found returns null
      */
     public QName getXMLType(Class javaClass);
    	 
     /**
     * Returns the xpath statement associated with this XMLField
     * @return The xpath statement associated with this XMLField
     */
    public String getXPath();
     
     /**
      * INTERNAL:
      * Maintain a direct pointer to the first XPathFragment.  For example given
      * the following XPath first/middle/@last, first is the first XPathFragment.
      */
     public XPathFragment getXPathFragment();

     /**
      * INTERNAL:
      * @return
      */
     public boolean hasLastXPathFragment();
     
     /**
     * INTERNAL:
     * @return
     */
    public boolean isCDATA();
     
     /**
     * Indicates if this XMLField represents a "required" XML element or attribute
     * ([minOccurs="1"] for elements, [use="required"] for attributes).  NOTE: This
     * API is used only for Schema Generation.
     * 
     * @see org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator
     */
    public boolean isRequired();
     
     /**
      * INTERNAL
      */
     public boolean isSchemaType(QName schemaType);
    
    /**
      * INTERNAL:
      * Indicates if the xpath for this field is "."
      *
      * @return true if the xpath is ".", false otherwise
      */
     public boolean isSelfField();
     
     /**
      * Returns if the field is a typed text field
      * True when we should base conversions on the "type" attribute on elements
      * @return True when we should base conversions on the "type" attribute on elements, otherwise false
      */
    public boolean isTypedTextField();
     
    /**
      * INTERNAL:
      * Returns false since this is a union field
       * The subclass XMLUnionField returns true for this
      */
      public boolean isUnionField();
    
    /**
	     * INTERNAL:
	     * @param CDATA
	     */
	    public void setIsCDATA(boolean CDATA);
    
    /**
     * Set the NamespaceResolver associated with this XMLField
     * @param newNamespaceResolver The namespaceResolver to be associated with this XMLField
     * @see org.eclipse.persistence.oxm.NamespaceResolver
     */
    public void setNamespaceResolver(NamespaceResolver newNamespaceResolver);
    
    /**
    * PUBLIC:
    * Sets whether the mapping uses a single node.
    * @param usesSingleNode True if the items in the collection are in a single node or false if each of the items in the collection is in its own node
    */
    public void setUsesSingleNode(boolean usesSingleNode);
    
   /**
 * Set the xpath statment for this XMLField.
 * @param xPath The xpath statement to be associated with this XMLField
 */
public void setXPath(String xPath);
	
	/**
     * PUBLIC:
     * Checks whether the mapping uses a single node.
     *
     * @return True if the items in the collection are in a single node or false if each of the items in the collection is in its own node.
     */
     public boolean usesSingleNode();
}
