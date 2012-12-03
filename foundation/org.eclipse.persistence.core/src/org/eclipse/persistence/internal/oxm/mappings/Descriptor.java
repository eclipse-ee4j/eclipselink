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

import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.descriptors.CoreInheritancePolicy;
import org.eclipse.persistence.core.mappings.CoreMapping;
import org.eclipse.persistence.internal.core.descriptors.CoreInstantiationPolicy;
import org.eclipse.persistence.internal.core.descriptors.CoreObjectBuilder;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;

public interface Descriptor <
      CORE_MAPPING extends CoreMapping,      
      FIELD extends CoreField,      
      INHERITANCE_POLICY extends CoreInheritancePolicy,
      INSTANTIATION_POLICY extends CoreInstantiationPolicy,
      OBJECT_BUILDER extends CoreObjectBuilder     
      > {    
	
	/**
      * PUBLIC:
      * Get the alias
      */
     public String getAlias();
    
    
    /**
      * PUBLIC:
      * Return the default root element name for the ClassDescriptor
      * This value is stored in place of a table name
      * This value is mandatory for all root objects
      * @return the default root element specified on this ClassDescriptor
      */
     public String getDefaultRootElement();
    public QName getDefaultRootElementType();
    
    /**
     * PUBLIC:
     * The inheritance policy is used to define how a descriptor takes part in inheritance.
     * All inheritance properties for both child and parent classes is configured in inheritance policy.
     * Caution must be used in using this method as it lazy initializes an inheritance policy.
     * Calling this on a descriptor that does not use inheritance will cause problems, #hasInheritance() must always first be called.
     */
    public INHERITANCE_POLICY getInheritancePolicy();
    
    /**
     * INTERNAL:
     * Return the inheritance policy.
     */
    public INHERITANCE_POLICY getInheritancePolicyOrNull();
    
    /**
     * INTERNAL:
     * Returns the instantiation policy.
     */   
    public INSTANTIATION_POLICY getInstantiationPolicy();
    
    /**
     * PUBLIC:
     * Return the java class.
     */    
    public Class getJavaClass();
	/**
     * Return the class name, used by the MW.
     */
    public String getJavaClassName();
    
    /**
     * PUBLIC:
     * Returns mappings
     */
    public Vector<CORE_MAPPING> getMappings();
    
    /**
     * PUBLIC:
     * Return the NamespaceResolver associated with this descriptor
     * @return the NamespaceResolver associated with this descriptor
     * @see org.eclipse.persistence.oxm.NamespaceResolver
     */
     public NamespaceResolver getNamespaceResolver();
    
     public NamespaceResolver getNonNullNamespaceResolver();
     
     /**
     * INTERNAL:
     * Return the object builder
     */    
    public OBJECT_BUILDER getObjectBuilder();
     
     
     /**
     * PUBLIC:
     * Return the names of all the primary keys.
     */
    public Vector<String> getPrimaryKeyFieldNames();
     
     /**
     * INTERNAL:
     * Return all the primary key fields
     */    
    public List<FIELD> getPrimaryKeyFields();
     
     
     /**
      * PUBLIC:
      * Return the SchemaReference associated with this descriptor
      * @return the SchemaReference associated with this descriptor
      * @see org.eclipse.persistence.oxm.schema
      */
      public XMLSchemaReference getSchemaReference();
     
     /**
      * PUBLIC:
      * Return the table names.
      */
     public Vector getTableNames();
     
     /**
      * INTERNAL:
      * Return all the tables.
      */
     public Vector<DatabaseTable> getTables();
     
     /**
      * INTERNAL:
      * searches first descriptor than its ReturningPolicy for an equal field
      */
     public FIELD getTypedField(FIELD field);

     /**
     * INTERNAL:
     * Return if this descriptor is involved in inheritance, (is child or parent).
     * Note: If this class is part of table per class inheritance strategy this
     * method will return false. 
     * @see hasTablePerClassPolicy()
     */    
    public boolean hasInheritance();
     
     /**
     * If true, the descriptor may be lazily initialized.  This is useful if the
     * descriptor may not get used.
     */
    public boolean isLazilyInitialized();
     
     /**
      * INTERNAL:
      * <p>Indicates if the Object mapped by this descriptor is a sequenced data object
      * and should be marshalled accordingly.
      */
    public boolean isSequencedObject();
    
    
    public boolean isWrapper();
    /**
     * PUBLIC:
     * Set the NamespaceResolver to associate with this descriptor
     * @param newNamespaceResolver the NamespaceResolver to associate with this descriptor
     * @see org.eclipse.persistence.oxm.NamespaceResolver
     */
     public void setNamespaceResolver(NamespaceResolver newNamespaceResolver);
    
    /**
      * PUBLIC:
      * Return if unmapped information from the XML document should be maintained for this
      * descriptor
      * By default unmapped data is not preserved.
      * @return if this descriptor should preserve unmapped data
      */
     public boolean shouldPreserveDocument();
    
    /**
      * INTERNAL:
      * Determines the appropriate object to return from the unmarshal
      * call.  The method will either return the object created in the
      * xmlReader.parse() call or an instance of XMLRoot.  An XMLRoot
      * instance will be returned if the DOMRecord element being
      * unmarshalled does not equal the descriptor's default root
      * element.
      *
      * @param object
      * @param elementNamespaceUri
      * @param elementLocalName
      * @param elementPrefix
      * @return object
      */
    public Object wrapObjectInXMLRoot(Object object, String elementNamespaceUri, String elementLocalName, String elementPrefix, boolean forceWrap, boolean isNamespaceAware, XMLUnmarshaller xmlUnmarshaller);
     
     /**
     * INTERNAL:
     */
    public Object wrapObjectInXMLRoot(Object object, String elementNamespaceUri, String elementLocalName, String elementPrefix, String encoding, String version, boolean forceWrap, boolean isNamespaceAware, XMLUnmarshaller unmarshaller);
     
     /**
      * INTERNAL:
      * Determines the appropriate object to return from the unmarshal
      * call.  The method will either return the object created in the
      * xmlReader.parse() call or an instance of XMLRoot.  An XMLRoot
      * instance will be returned if the DOMRecord element being
      * unmarshalled does not equal the descriptor's default root
      * element.
      *
      * @param unmarshalRecord
      * @return object
      */
     public Object wrapObjectInXMLRoot(UnmarshalRecord unmarshalRecord, boolean forceWrap);

}
