/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 *     06/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     01/19/2010-2.1 Guy Pelletier 
 *       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
 *     06/18/2010-2.2 Guy Pelletier 
 *       - 300458: EclispeLink should throw a more specific exception than NPE
 *     07/16/2010-2.2 Guy Pelletier 
 *       - 260296: mixed access with no Transient annotation does not result in error
 *     07/23/2010-2.2 Guy Pelletier 
 *       - 237902: DDL GEN doesn't qualify SEQUENCE table with persistence unit schema
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 *     10/28/2010-2.2 Guy Pelletier 
 *       - 3223850: Primary key metadata issues
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     04/05/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 3)
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 8)
 *     07/11/2011-2.4 Guy Pelletier
 *       - 343632: Can't map a compound constraint because of exception: 
 *                 The reference column name [y] mapped on the element [field x] 
 *                 does not correspond to a valid field on the mapping reference
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata;

import java.util.HashMap;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;

/**
 * INTERNAL:
 * Logger class for the metadata processors. It defines the specific and
 * common log messages used by the metadata processor for the XML and 
 * annotation contexts.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataLogger {
    /*************************************************************************/
    /*           OVERRIDE MESSSAGES FOR ANNOTATIONS AND XML                  */ 
    /*************************************************************************/
    public static final String OVERRIDE_ANNOTATION_WITH_XML = "metadata_warning_override_annotation_with_xml";
    public static final String OVERRIDE_NAMED_ANNOTATION_WITH_XML = "metadata_warning_override_named_annotation_with_xml";
    
    public static final String OVERRIDE_XML_WITH_ECLIPSELINK_XML = "metadata_warning_override_xml_with_eclipselink_xml";
    public static final String OVERRIDE_NAMED_XML_WITH_ECLIPSELINK_XML = "metadata_warning_override_named_xml_with_eclipselink_xml";
    
    /*************************************************************************/
    /*              ANNOTATION SPECIFIC IGNORE MESSSAGES                     */ 
    /*************************************************************************/
    public static final String IGNORE_ANNOTATION = "annotation_warning_ignore_annotation";
    public static final String IGNORE_PRIVATE_OWNED_ANNOTATION = "annotation_warning_ignore_private_owned";

    public static final String IGNORE_RETURN_INSERT_ANNOTATION = "annotation_warning_ignore_return_insert";
    public static final String IGNORE_RETURN_UPDATE_ANNOTATION = "annotation_warning_ignore_return_update";
    
    /*************************************************************************/
    /*                       GENERIC IGNORE MESSSAGES                        */ 
    /*************************************************************************/
    public static final String IGNORE_LOB = "metadata_warning_ignore_lob";
    public static final String IGNORE_ENUMERATED = "metadata_warning_ignore_enumerated";
    public static final String IGNORE_SERIALIZED = "metadata_warning_ignore_serialized";
    public static final String IGNORE_TEMPORAL = "metadata_warning_ignore_temporal";
    
    public static final String IGNORE_CACHEABLE_FALSE = "metadata_warning_ignore_cacheable_false";
    public static final String IGNORE_CACHEABLE_TRUE = "metadata_warning_ignore_cacheable_true";
    
    public static final String IGNORE_ATTRIBUTE_OVERRIDE = "metadata_warning_ignore_attribute_override";
    public static final String IGNORE_ASSOCIATION_OVERRIDE = "metadata_warning_ignore_association_override";
    
    public static final String IGNORE_VERSION_LOCKING = "metadata_warning_ignore_version_locking";
    
    public static final String IGNORE_INHERITANCE_SUBCLASS_CACHE = "metadata_warning_ignore_inheritance_subclass_cache";
    public static final String IGNORE_INHERITANCE_SUBCLASS_CACHE_INTERCEPTOR = "metadata_warning_ignore_inheritance_subclass_cache_interceptor";
    public static final String IGNORE_INHERITANCE_SUBCLASS_DEFAULT_REDIRECTORS = "metadata_warning_ignore_inheritance_subclass_default_redirectors";
    public static final String IGNORE_INHERITANCE_SUBCLASS_READ_ONLY = "metadata_warning_ignore_inheritance_subclass_read_only";
    public static final String IGNORE_INHERITANCE_TENANT_DISCRIMINATOR_COLUMN = "metadata_warning_ignore_inheritance_tenant_discriminator_column";
    
    public static final String IGNORE_MAPPED_SUPERCLASS_COPY_POLICY = "metadata_warning_ignore_mapped_superclass_copy_policy";
    public static final String IGNORE_MAPPED_SUPERCLASS_ADDITIONAL_CRITERIA = "metadata_warning_ignore_mapped_superclass_additional_criteria";
    public static final String IGNORE_MAPPED_SUPERCLASS_ASSOCIATION_OVERRIDE = "metadata_warning_ignore_mapped_superclass_association_override";
    public static final String IGNORE_MAPPED_SUPERCLASS_ATTRIBUTE_OVERRIDE = "metadata_warning_ignore_mapped_superclass_attribute_override";
    public static final String IGNORE_MAPPED_SUPERCLASS_CACHE = "metadata_warning_ignore_mapped_superclass_cache";
    public static final String IGNORE_MAPPED_SUPERCLASS_CACHEABLE = "metadata_warning_ignore_mapped_superclass_cacheable";
    public static final String IGNORE_MAPPED_SUPERCLASS_CACHE_INTERCEPTOR = "metadata_warning_ignore_mapped_superclass_cache_interceptor";
    public static final String IGNORE_MAPPED_SUPERCLASS_DEFAULT_REDIRECTORS = "metadata_warning_ignore_mapped_superclass_default_redirectors";
    public static final String IGNORE_MAPPED_SUPERCLASS_CHANGE_TRACKING = "metadata_warning_ignore_mapped_superclass_change_tracking";
    public static final String IGNORE_MAPPED_SUPERCLASS_CUSTOMIZER = "metadata_warning_ignore_mapped_superclass_customizer";
    public static final String IGNORE_MAPPED_SUPERCLASS_ID_CLASS = "metadata_warning_ignore_mapped_superclass_id_class";
    public static final String IGNORE_MAPPED_SUPERCLASS_EXISTENCE_CHECKING = "metadata_warning_ignore_mapped_superclass_existence_checking";
    public static final String IGNORE_MAPPED_SUPERCLASS_OPTIMISTIC_LOCKING = "metadata_warning_ignore_mapped_superclass_optimistic_locking";
    public static final String IGNORE_MAPPED_SUPERCLASS_READ_ONLY = "metadata_warning_ignore_mapped_superclass_read_only";
    public static final String IGNORE_MAPPED_SUPERCLASS_FETCH_GROUP = "metadata_warning_ignore_mapped_superclass_fetch_group";
    public static final String IGNORE_MAPPED_SUPERCLASS_ANNOTATION = "metadata_warning_ignore_mapped_superclass_annotation";
    public static final String IGNORE_MAPPED_SUPERCLASS_PRIMARY_KEY = "metadata_warning_ignore_mapped_superclass_primary_key";
    public static final String IGNORE_MAPPED_SUPERCLASS_MULTITENANT = "metadata_warning_ignore_mapped_superclass_multitenant";
    
    public static final String IGNORE_FETCH_GROUP = "metadata_warning_ignore_fetch_group";
    public static final String IGNORE_MAPPING_METADATA = "metadata_warning_ignore_mapping_metadata";
    
    /*************************************************************************/
    /*                       GENERIC DEFAULT MESSSAGES                       */ 
    /*************************************************************************/
    public static final String ACCESS_TYPE = "metadata_access_type";
    
    public static final String ALIAS = "metadata_default_alias";
    public static final String MAP_KEY_ATTRIBUTE_NAME = "metadata_default_map_key_attribute_name";
    
    public static final String TABLE_NAME = "metadata_default_table_name"; 
    public static final String TABLE_SCHEMA = "metadata_default_table_schema";
    public static final String TABLE_CATALOG = "metadata_default_table_catalog";
    
    public static final String TABLE_GENERATOR_NAME = "metadata_default_table_generator_name"; 
    public static final String TABLE_GENERATOR_SCHEMA = "metadata_default_table_generator_schema";
    public static final String TABLE_GENERATOR_CATALOG = "metadata_default_table_generator_catalog";
    public static final String TABLE_GENERATOR_PK_COLUMN_VALUE = "metadata_default_table_generator_pk_column_value"; 
    
    public static final String SEQUENCE_GENERATOR_SCHEMA = "metadata_default_sequence_generator_schema";
    public static final String SEQUENCE_GENERATOR_CATALOG = "metadata_default_sequence_generator_catalog";
    public static final String SEQUENCE_GENERATOR_SEQUENCE_NAME = "metadata_default_sequence_generator_sequence_name";
    
    public static final String JOIN_TABLE_NAME = "metadata_default_join_table_name";
    public static final String JOIN_TABLE_SCHEMA = "metadata_default_join_table_schema";
    public static final String JOIN_TABLE_CATALOG = "metadata_default_join_table_catalog";
    
    public static final String SECONDARY_TABLE_NAME = "metadata_default_secondary_table_name";
    public static final String SECONDARY_TABLE_SCHEMA = "metadata_default_secondary_table_schema";
    public static final String SECONDARY_TABLE_CATALOG = "metadata_default_secondary_table_catalog";
    
    public static final String COLLECTION_TABLE_NAME = "metadata_default_collection_table_name";
    public static final String COLLECTION_TABLE_SCHEMA = "metadata_default_collection_table_schema";
    public static final String COLLECTION_TABLE_CATALOG = "metadata_default_collection_table_catalog";
    
    public static final String CONVERTER_DATA_TYPE = "metadata_default_converter_data_type";
    public static final String CONVERTER_OBJECT_TYPE = "metadata_default_converter_object_type";
    
    public static final String COLUMN = "metadata_default_column";
    public static final String PK_COLUMN = "metadata_default_pk_column";
    public static final String FK_COLUMN = "metadata_default_fk_column";
    public static final String QK_COLUMN = "metadata_default_qk_column";
    public static final String ORDER_COLUMN = "metadata_default_order_column";
    public static final String MAP_KEY_COLUMN = "metadata_default_key_column";
    public static final String VALUE_COLUMN = "metadata_default_value_column"; // applies to maps and collections
    public static final String SOURCE_PK_COLUMN = "metadata_default_source_pk_column";
    public static final String SOURCE_FK_COLUMN = "metadata_default_source_fk_column";
    public static final String TARGET_PK_COLUMN = "metadata_default_target_pk_column";
    public static final String TARGET_FK_COLUMN = "metadata_default_target_fk_column";
    public static final String VARIABLE_ONE_TO_ONE_DISCRIMINATOR_COLUMN = "metadata_default_variable_one_to_one_discriminator_column";
    public static final String INHERITANCE_DISCRIMINATOR_COLUMN = "metadata_default_inheritance_discriminator_column";
    public static final String INHERITANCE_PK_COLUMN = "metadata_default_inheritance_pk_column";
    public static final String INHERITANCE_FK_COLUMN = "metadata_default_inheritance_fk_column";
    public static final String SECONDARY_TABLE_PK_COLUMN = "metadata_default_secondary_table_pk_column";
    public static final String SECONDARY_TABLE_FK_COLUMN = "metadata_default_secondary_table_fk_column";
    public static final String TENANT_DISCRIMINATOR_COLUMN = "metadata_default_tenant_discriminator_column";
    public static final String TENANT_DISCRIMINATOR_CONTEXT_PROPERTY = "metadata_default_tenant_discriminator_context_property";
    
    public static final String ONE_TO_ONE_MAPPING = "metadata_default_one_to_one_mapping";
    public static final String ONE_TO_MANY_MAPPING = "metadata_default_one_to_many_mapping";
    public static final String VARIABLE_ONE_TO_ONE_MAPPING = "metadata_default_variable_one_to_one_mapping";
    public static final String ONE_TO_ONE_MAPPING_REFERENCE_CLASS = "metadata_default_one_to_one_reference_class";
    public static final String ONE_TO_MANY_MAPPING_REFERENCE_CLASS = "metadata_default_one_to_many_reference_class";
    public static final String MANY_TO_ONE_MAPPING_REFERENCE_CLASS = "metadata_default_many_to_one_reference_class";
    public static final String MANY_TO_MANY_MAPPING_REFERENCE_CLASS = "metadata_default_many_to_many_reference_class";
    public static final String VARIABLE_ONE_TO_ONE_MAPPING_REFERENCE_CLASS = "metadata_default_variable_one_to_one_reference_class";
    public static final String ELEMENT_COLLECTION_MAPPING_REFERENCE_CLASS = "metadata_default_element_collection_reference_class";
    
    /*************************************************************************/
    /*                       OTHER WARNING MESSSAGES                         */ 
    /*************************************************************************/
    public static final String WARNING_INVALID_COLLECTION_USED_ON_LAZY_RELATION = "non_jpa_allowed_type_used_for_collection_using_lazy_access";
    public static final String WARNING_INCORRECT_DISCRIMINATOR_FORMAT = "metadata_warning_integer_discriminator_could_not_be_built";
    public static final String WARNING_PARTIONED_NOT_SET = "metadata_warning_partitioned_not_set";
    public static final String REFERENCED_COLUMN_NOT_FOUND = "metadata_warning_reference_column_not_found";
    public static final String MULTIPLE_ID_FIELDS_WITHOUT_ID_CLASS = "metadata_warning_multiple_id_fields_without_id_class";
    public static final String INVERSE_ACCESS_TYPE_MAPPING_OVERRIDE = "metadata_warning_inverse_access_type_mapping_override";
    
    /*************************************************************************/
    
    protected AbstractSession m_session; 
    protected HashMap m_ctxStrings;

    /**
     * INTERNAL:
     */
    public MetadataLogger(AbstractSession session) {
        m_session = session;
        
        // Initialize the context strings.
        m_ctxStrings = new HashMap();
        
        // Generic override messages for XML and annotations.
        addContextString(OVERRIDE_ANNOTATION_WITH_XML);
        addContextString(OVERRIDE_NAMED_ANNOTATION_WITH_XML);
        addContextString(OVERRIDE_XML_WITH_ECLIPSELINK_XML);
        addContextString(OVERRIDE_NAMED_XML_WITH_ECLIPSELINK_XML);
        
        // Annotation specific ignore messages. These are typically used when
        // ignoring annotations from an incorrect location on a mapping or 
        // class. Since we have XML schema validation these do not apply to XML.
        addContextString(IGNORE_ANNOTATION);
        addContextString(IGNORE_PRIVATE_OWNED_ANNOTATION);
        addContextString(IGNORE_RETURN_INSERT_ANNOTATION);
        addContextString(IGNORE_RETURN_UPDATE_ANNOTATION);

        // Generic ignore messages that could apply to XML and annotation
        // configurations.
        addContextString(IGNORE_LOB);
        addContextString(IGNORE_TEMPORAL);
        addContextString(IGNORE_ENUMERATED);
        addContextString(IGNORE_SERIALIZED);
        addContextString(IGNORE_VERSION_LOCKING);
        
        addContextString(IGNORE_CACHEABLE_FALSE);
        addContextString(IGNORE_CACHEABLE_TRUE);
        
        addContextString(IGNORE_ATTRIBUTE_OVERRIDE);
        addContextString(IGNORE_ASSOCIATION_OVERRIDE);
        
        addContextString(IGNORE_INHERITANCE_SUBCLASS_CACHE);
        addContextString(IGNORE_INHERITANCE_SUBCLASS_CACHE_INTERCEPTOR);
        addContextString(IGNORE_INHERITANCE_SUBCLASS_DEFAULT_REDIRECTORS);
        addContextString(IGNORE_INHERITANCE_SUBCLASS_READ_ONLY);
        addContextString(IGNORE_INHERITANCE_TENANT_DISCRIMINATOR_COLUMN);
       
        addContextString(IGNORE_MAPPED_SUPERCLASS_COPY_POLICY);
        addContextString(IGNORE_MAPPED_SUPERCLASS_ADDITIONAL_CRITERIA);
        addContextString(IGNORE_MAPPED_SUPERCLASS_ASSOCIATION_OVERRIDE);
        addContextString(IGNORE_MAPPED_SUPERCLASS_ATTRIBUTE_OVERRIDE);
        addContextString(IGNORE_MAPPED_SUPERCLASS_OPTIMISTIC_LOCKING);
        addContextString(IGNORE_MAPPED_SUPERCLASS_CACHE);
        addContextString(IGNORE_MAPPED_SUPERCLASS_CACHEABLE);
        addContextString(IGNORE_MAPPED_SUPERCLASS_CACHE_INTERCEPTOR);
        addContextString(IGNORE_MAPPED_SUPERCLASS_DEFAULT_REDIRECTORS);
        addContextString(IGNORE_MAPPED_SUPERCLASS_CHANGE_TRACKING);
        addContextString(IGNORE_MAPPED_SUPERCLASS_CUSTOMIZER);
        addContextString(IGNORE_MAPPED_SUPERCLASS_ID_CLASS);
        addContextString(IGNORE_MAPPED_SUPERCLASS_READ_ONLY);
        addContextString(IGNORE_MAPPED_SUPERCLASS_EXISTENCE_CHECKING);
        addContextString(IGNORE_MAPPED_SUPERCLASS_FETCH_GROUP);
        addContextString(IGNORE_MAPPED_SUPERCLASS_ANNOTATION);
        addContextString(IGNORE_MAPPED_SUPERCLASS_PRIMARY_KEY);
        addContextString(IGNORE_MAPPED_SUPERCLASS_MULTITENANT);
        
        addContextString(IGNORE_FETCH_GROUP);
        addContextString(IGNORE_MAPPING_METADATA);
        
        // Generic default messages that could apply to XML and annotation
        // configurations.
        addContextString(ACCESS_TYPE);
        
        addContextString(ALIAS);
        addContextString(MAP_KEY_ATTRIBUTE_NAME);
        
        addContextString(TABLE_NAME);
        addContextString(TABLE_SCHEMA);
        addContextString(TABLE_CATALOG);
        
        addContextString(TABLE_GENERATOR_NAME);
        addContextString(TABLE_GENERATOR_SCHEMA);
        addContextString(TABLE_GENERATOR_CATALOG);
        addContextString(TABLE_GENERATOR_PK_COLUMN_VALUE);
        
        addContextString(SEQUENCE_GENERATOR_SCHEMA);
        addContextString(SEQUENCE_GENERATOR_CATALOG);
        addContextString(SEQUENCE_GENERATOR_SEQUENCE_NAME);

        addContextString(JOIN_TABLE_NAME);
        addContextString(JOIN_TABLE_SCHEMA);
        addContextString(JOIN_TABLE_CATALOG);
        
        addContextString(SECONDARY_TABLE_NAME);
        addContextString(SECONDARY_TABLE_SCHEMA);
        addContextString(SECONDARY_TABLE_CATALOG);
        
        addContextString(COLLECTION_TABLE_NAME);
        addContextString(COLLECTION_TABLE_SCHEMA);
        addContextString(COLLECTION_TABLE_CATALOG);
    
        addContextString(CONVERTER_DATA_TYPE);
        addContextString(CONVERTER_OBJECT_TYPE);
        
        addContextString(COLUMN);
        addContextString(PK_COLUMN);
        addContextString(FK_COLUMN);
        addContextString(QK_COLUMN);      
        addContextString(ORDER_COLUMN);
        addContextString(VALUE_COLUMN);
        addContextString(MAP_KEY_COLUMN);
        addContextString(SOURCE_PK_COLUMN);
        addContextString(SOURCE_FK_COLUMN);
        addContextString(TARGET_PK_COLUMN);
        addContextString(TARGET_FK_COLUMN);
        addContextString(VARIABLE_ONE_TO_ONE_DISCRIMINATOR_COLUMN); 
        addContextString(INHERITANCE_DISCRIMINATOR_COLUMN);
        addContextString(INHERITANCE_PK_COLUMN);
        addContextString(INHERITANCE_FK_COLUMN);
        addContextString(SECONDARY_TABLE_PK_COLUMN);
        addContextString(SECONDARY_TABLE_FK_COLUMN);
        addContextString(TENANT_DISCRIMINATOR_COLUMN);
        addContextString(TENANT_DISCRIMINATOR_CONTEXT_PROPERTY);
        
        addContextString(ONE_TO_ONE_MAPPING);
        addContextString(ONE_TO_MANY_MAPPING);
        addContextString(VARIABLE_ONE_TO_ONE_MAPPING);
        addContextString(ONE_TO_ONE_MAPPING_REFERENCE_CLASS);
        addContextString(ONE_TO_MANY_MAPPING_REFERENCE_CLASS);
        addContextString(MANY_TO_ONE_MAPPING_REFERENCE_CLASS);
        addContextString(MANY_TO_MANY_MAPPING_REFERENCE_CLASS);
        addContextString(VARIABLE_ONE_TO_ONE_MAPPING_REFERENCE_CLASS);
        addContextString(ELEMENT_COLLECTION_MAPPING_REFERENCE_CLASS);
        
        addContextString(WARNING_INVALID_COLLECTION_USED_ON_LAZY_RELATION);
        addContextString(WARNING_INCORRECT_DISCRIMINATOR_FORMAT);
        addContextString(WARNING_PARTIONED_NOT_SET);
        addContextString(REFERENCED_COLUMN_NOT_FOUND);
        addContextString(MULTIPLE_ID_FIELDS_WITHOUT_ID_CLASS);
        addContextString(INVERSE_ACCESS_TYPE_MAPPING_OVERRIDE);
    }
    
    /**
     * INTERNAL:
     * Add a context string to the map of contexts.
     */
    protected void addContextString(String context) {
        m_ctxStrings.put(context, context);
    }
    
    /**
     * INTERNAL:
     * Return the logging context string for the given context.
     */
    protected String getLoggingContextString(String context) {
        String ctxString = (String) m_ctxStrings.get(context);
        
        if (ctxString == null) {
           throw ValidationException.missingContextStringForContext(context);
        }
        
        return ctxString;
    }  
    
    /**
     * INTERNAL:
     * Logging utility method.
     */
    public void log(int level, String ctx, Object[] params) {
        m_session.log(level, SessionLog.EJB_OR_METADATA, getLoggingContextString(ctx), params);
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     * We currently can not log any lower than CONFIG since all our metadata
     * logging messages are located in LoggingLocalizationResource. Any lower
     * than CONFIG and those message strings would have to move to 
     * TraceLocalizationResource.
     */
    public void logConfigMessage(String ctx, MetadataAccessor accessor, Object param) {
        log(SessionLog.CONFIG, ctx, new Object[] { accessor.getAnnotatedElement(), param });
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     * We currently can not log any lower than CONFIG since all our metadata
     * logging messages are located in LoggingLocalizationResource. Any lower
     * than CONFIG and those message strings would have to move to 
     * TraceLocalizationResource.
     */
    public void logConfigMessage(String ctx, MetadataAccessor accessor, Object param1, Object param2) {
        log(SessionLog.CONFIG, ctx, new Object[] { accessor.getJavaClass(), accessor.getAnnotatedElement(), param1, param2 });
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     * We currently can not log any lower than CONFIG since all our metadata
     * logging messages are located in LoggingLocalizationResource. Any lower
     * than CONFIG and those message strings would have to move to 
     * TraceLocalizationResource.
     */
    public void logConfigMessage(String ctx, MetadataDescriptor descriptor, Object param) {
        log(SessionLog.CONFIG, ctx, new Object[] { descriptor.getJavaClass(), param });
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     * We currently can not log any lower than CONFIG since all our metadata
     * logging messages are located in LoggingLocalizationResource. Any lower
     * than CONFIG and those message strings would have to move to 
     * TraceLocalizationResource.
     */
    public void logConfigMessage(String ctx, Object object) {
        log(SessionLog.CONFIG, ctx, new Object[] { object });    
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     * We currently can not log any lower than CONFIG since all our metadata
     * logging messages are located in LoggingLocalizationResource. Any lower
     * than CONFIG and those message strings would have to move to 
     * TraceLocalizationResource.
     */
    public void logConfigMessage(String ctx, Object param1, Object param2) {
        log(SessionLog.CONFIG, ctx, new Object[] { param1, param2 });    
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     * We currently can not log any lower than CONFIG since all our metadata
     * logging messages are located in LoggingLocalizationResource. Any lower
     * than CONFIG and those message strings would have to move to 
     * TraceLocalizationResource.
     */
    public void logConfigMessage(String ctx, Object param1, Object param2, Object param3) {
        log(SessionLog.CONFIG, ctx, new Object[] { param1, param2, param3 });    
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     * We currently can not log any lower than CONFIG since all our metadata
     * logging messages are located in LoggingLocalizationResource. Any lower
     * than CONFIG and those message strings would have to move to 
     * TraceLocalizationResource.
     */
    public void logConfigMessage(String ctx, Object param1, Object param2, Object param3, Object param4) {
        log(SessionLog.CONFIG, ctx, new Object[] { param1, param2, param3, param4 });    
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     */
    public void logWarningMessage(String ctx, MetadataAccessor accessor) {
        log(SessionLog.WARNING, ctx, new Object[] { accessor.getJavaClass(), accessor.getAnnotatedElement() });
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     */
    public void logWarningMessage(String ctx, Object param) {
        log(SessionLog.WARNING, ctx, new Object[] { param });    
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     */
    public void logWarningMessage(String ctx, Object param1, Object param2) {
        log(SessionLog.WARNING, ctx, new Object[] { param1, param2 });    
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     */
    public void logWarningMessage(String ctx, Object param1, Object param2, Object param3) {
        log(SessionLog.WARNING, ctx, new Object[] {param1, param2, param3});
    }
    
    /**
     * INTERNAL:
     * Logging utility method.
     */
    public void logWarningMessage(String ctx, Object param1, Object param2, Object param3, Object param4, Object param5) {
        log(SessionLog.WARNING, ctx, new Object[] {param1, param2, param3, param4, param5});
    }
    
    /**
     * INTERNAL:
     * Set the session to log to.
     */
    public AbstractSession getSession() {
        return m_session;
    }
    
    /**
     * INTERNAL:
     * Set the session to log to.
     */
    public void setSession(AbstractSession session) {
        m_session = session;
    }
}
