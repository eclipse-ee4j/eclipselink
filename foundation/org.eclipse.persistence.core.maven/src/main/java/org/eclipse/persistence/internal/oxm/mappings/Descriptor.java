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

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.descriptors.CoreInheritancePolicy;
import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.core.mappings.CoreMapping;
import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.internal.core.descriptors.CoreInstantiationPolicy;
import org.eclipse.persistence.internal.core.descriptors.CoreObjectBuilder;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.helper.CoreTable;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;

public interface Descriptor <
    ATTRIBUTE_ACCESSOR extends CoreAttributeAccessor,
    CORE_MAPPING extends CoreMapping,
    FIELD extends CoreField,
    INHERITANCE_POLICY extends CoreInheritancePolicy,
    INSTANTIATION_POLICY extends CoreInstantiationPolicy,
    NAMESPACE_RESOLVER extends NamespaceResolver,
    OBJECT_BUILDER extends CoreObjectBuilder,
    TABLE extends CoreTable,
    UNMARSHAL_RECORD extends UnmarshalRecord,
    UNMARSHALLER extends Unmarshaller> {

    /**
     * Add a database mapping to the receiver. Perform any required
     * initialization of both the mapping and the receiving descriptor
     * as a result of adding the new mapping.
     */
    CORE_MAPPING addMapping(CORE_MAPPING mapping);

    /**
     * ADVANCED:
     * Specify the primary key field of the descriptors table.
     * This should be called for each field that makes up the primary key of the table.
     * This can be used for advanced field types, such as XML nodes, or to set the field type.
     */
    void addPrimaryKeyField(FIELD field);

    /**
        * Add a root element name for the Descriptor
        * This value is stored in place of a table name
        * @param rootElementName a root element to specify on this Descriptor
        */
        void addRootElement(String rootElementName);
    /**
      * Get the alias
      */
     String getAlias();

    /**
      * Return the default root element name for the ClassDescriptor
      * This value is stored in place of a table name
      * This value is mandatory for all root objects
      * @return the default root element specified on this ClassDescriptor
      */
     String getDefaultRootElement();

     QName getDefaultRootElementType();

     Field getDefaultRootElementField();

    /**
     * The inheritance policy is used to define how a descriptor takes part in inheritance.
     * All inheritance properties for both child and parent classes is configured in inheritance policy.
     * Caution must be used in using this method as it lazy initializes an inheritance policy.
     * Calling this on a descriptor that does not use inheritance will cause problems, #hasInheritance() must always first be called.
     */
    INHERITANCE_POLICY getInheritancePolicy();

    /**
     * INTERNAL:
     * Return the inheritance policy.
     */
    INHERITANCE_POLICY getInheritancePolicyOrNull();

    /**
     * INTERNAL:
     * Returns the instantiation policy.
     */
    INSTANTIATION_POLICY getInstantiationPolicy();

    /**
     * Return the java class.
     */
    Class getJavaClass();

    /**
     * Return the class name, used by the MW.
     */
    String getJavaClassName();

    /**
     * INTERNAL:
     * Returns this Descriptor's location accessor, if one is defined.
     */
    ATTRIBUTE_ACCESSOR getLocationAccessor();

     /**
     * Returns the mapping associated with a given attribute name.
     * This can be used to find a descriptors mapping in a amendment method before the descriptor has been initialized.
     */
    CORE_MAPPING getMappingForAttributeName(String attributeName);

     /**
     * Returns mappings
     */
    Vector<CORE_MAPPING> getMappings();


     /**
     * Return the NamespaceResolver associated with this descriptor
     * @return the NamespaceResolver associated with this descriptor
     */
     NAMESPACE_RESOLVER getNamespaceResolver();

     NAMESPACE_RESOLVER getNonNullNamespaceResolver();


     /**
     * INTERNAL:
     * Return the object builder
     */
    OBJECT_BUILDER getObjectBuilder();

     /**
     * Return the names of all the primary keys.
     */
    Vector<String> getPrimaryKeyFieldNames();

     /**
     * INTERNAL:
     * Return all the primary key fields
     */
    List<FIELD> getPrimaryKeyFields();

     /**
      * Return the SchemaReference associated with this descriptor
      * @return the SchemaReference associated with this descriptor
      * @see org.eclipse.persistence.oxm.schema
      */
      XMLSchemaReference getSchemaReference();

     /**
      * Return the table names.
      */
     Vector getTableNames();

     /**
      * INTERNAL:
      * Return all the tables.
      */
     Vector<TABLE> getTables();

     /**
      * INTERNAL:
      * searches first descriptor than its ReturningPolicy for an equal field
      */
     FIELD getTypedField(FIELD field);


    /**
     * INTERNAL:
     * Return if this descriptor is involved in inheritance, (is child or parent).
     * Note: If this class is part of table per class inheritance strategy this
     * method will return false.
     */
    boolean hasInheritance();
    /**
     * If true, the descriptor may be lazily initialized.  This is useful if the
     * descriptor may not get used.
     */
    boolean isLazilyInitialized();

    boolean isResultAlwaysXMLRoot();

    /**
      * INTERNAL:
      * <p>Indicates if the Object mapped by this descriptor is a sequenced data object
      * and should be marshalled accordingly.
      */
    boolean isSequencedObject();

     boolean isWrapper();

     /**
       * Return the default root element name for the ClassDescriptor
       * This value is stored in place of a table name
       * This value is mandatory for all root objects
       * @param newDefaultRootElement the default root element to specify on this ClassDescriptor
       */
       void setDefaultRootElement(String newDefaultRootElement);

     /**
       * INTERNAL:
       * Sets the instantiation policy.
       */
      void setInstantiationPolicy(INSTANTIATION_POLICY instantiationPolicy);

      /**
      * Set the Java class that this descriptor maps.
      * Every descriptor maps one and only one class.
      */
      void setJavaClass(Class theJavaClass);

      /**
       * INTERNAL:
       * Return the java class name, used by the MW.
       */
      void setJavaClassName(String theJavaClassName);

      /**
     * INTERNAL:
     * Set this Descriptor's location accessor.
     */
    void setLocationAccessor(ATTRIBUTE_ACCESSOR attributeAccessor);

       /**
     * Set the NamespaceResolver to associate with this descriptor
     * @param newNamespaceResolver the NamespaceResolver to associate with this descriptor
     */
     void setNamespaceResolver(NAMESPACE_RESOLVER newNamespaceResolver);

        /**
         * INTERNAL:
         * Set the user defined properties.
         */
        void setProperties(Map properties);

        void setResultAlwaysXMLRoot(boolean resultAlwaysXMLRoot);

        /**
         * Set the SchemaReference to associate with this descriptor
         * @param newSchemaReference the SchemaReference to associate with this descriptor
         * @see org.eclipse.persistence.oxm.schema
         */
        void setSchemaReference(XMLSchemaReference newSchemaReference);

        /**
          * Return if unmapped information from the XML document should be maintained for this
          * descriptor
          * By default unmapped data is not preserved.
          * @return if this descriptor should preserve unmapped data
          */
         boolean shouldPreserveDocument();

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
        Object wrapObjectInXMLRoot(Object object, String elementNamespaceUri, String elementLocalName, String elementPrefix, boolean forceWrap, boolean isNamespaceAware, UNMARSHALLER xmlUnmarshaller);

        /**
         * INTERNAL:
         */
        Object wrapObjectInXMLRoot(Object object, String elementNamespaceUri, String elementLocalName, String elementPrefix, String encoding, String version, boolean forceWrap, boolean isNamespaceAware, UNMARSHALLER unmarshaller);

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
         Object wrapObjectInXMLRoot(UNMARSHAL_RECORD unmarshalRecord, boolean forceWrap);

        CoreAttributeGroup getAttributeGroup(String subgraph);

}
