/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
    
    public static final String IGNORE_ATTRIBUTE_OVERRIDE = "metadata_warning_ignore_attribute_override";
    public static final String IGNORE_ASSOCIATION_OVERRIDE = "metadata_warning_ignore_association_override";
    
    public static final String IGNORE_VERSION_LOCKING = "metadata_warning_ignore_version_locking";
    public static final String IGNORE_INHERITANCE_SUBCLASS_CACHE = "metadata_warning_ignore_inheritance_subclass_cache";
    public static final String IGNORE_INHERITANCE_SUBCLASS_CACHE_INTERCEPTOR = "metadata_warning_ignore_inheritance_subclass_cache_interceptor";
    public static final String IGNORE_INHERITANCE_SUBCLASS_DEFAULT_REDIRECTORS = "metadata_warning_ignore_inheritance_subclass_default_redirectors";
    public static final String IGNORE_INHERITANCE_SUBCLASS_READ_ONLY = "metadata_warning_ignore_inheritance_subclass_read_only";
    
    public static final String IGNORE_MAPPED_SUPERCLASS_COPY_POLICY = "metadata_warning_ignore_mapped_superclass_copy_policy";
    public static final String IGNORE_MAPPED_SUPERCLASS_ASSOCIATION_OVERRIDE = "metadata_warning_ignore_mapped_superclass_association_override";
    public static final String IGNORE_MAPPED_SUPERCLASS_ATTRIBUTE_OVERRIDE = "metadata_warning_ignore_mapped_superclass_attribute_override";
    public static final String IGNORE_MAPPED_SUPERCLASS_CACHE = "metadata_warning_ignore_mapped_superclass_cache";
    public static final String IGNORE_MAPPED_SUPERCLASS_CACHE_INTERCEPTOR = "metadata_warning_ignore_mapped_superclass_cache_interceptor";
    public static final String IGNORE_MAPPED_SUPERCLASS_DEFAULT_REDIRECTORS = "metadata_warning_ignore_mapped_superclass_default_redirectors";
    public static final String IGNORE_MAPPED_SUPERCLASS_CHANGE_TRACKING = "metadata_warning_ignore_mapped_superclass_change_tracking";
    public static final String IGNORE_MAPPED_SUPERCLASS_CUSTOMIZER = "metadata_warning_ignore_mapped_superclass_customizer";
    public static final String IGNORE_MAPPED_SUPERCLASS_ID_CLASS = "metadata_warning_ignore_mapped_superclass_id_class";
    public static final String IGNORE_MAPPED_SUPERCLASS_EXISTENCE_CHECKING = "metadata_warning_ignore_mapped_superclass_existence_checking";
    public static final String IGNORE_MAPPED_SUPERCLASS_OPTIMISTIC_LOCKING = "metadata_warning_ignore_mapped_superclass_optimistic_locking";
    public static final String IGNORE_MAPPED_SUPERCLASS_READ_ONLY = "metadata_warning_ignore_mapped_superclass_read_only";
    
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
        m_ctxStrings.put(OVERRIDE_ANNOTATION_WITH_XML, OVERRIDE_ANNOTATION_WITH_XML);
        m_ctxStrings.put(OVERRIDE_NAMED_ANNOTATION_WITH_XML, OVERRIDE_NAMED_ANNOTATION_WITH_XML);
        m_ctxStrings.put(OVERRIDE_XML_WITH_ECLIPSELINK_XML, OVERRIDE_XML_WITH_ECLIPSELINK_XML);
        m_ctxStrings.put(OVERRIDE_NAMED_XML_WITH_ECLIPSELINK_XML, OVERRIDE_NAMED_XML_WITH_ECLIPSELINK_XML);
        
        // Annotation specific ignore messages. These are typically used when
        // ignoring annotations from an incorrect location on a mapping or 
        // class. Since we have XML schema validation these do not apply to XML.
        m_ctxStrings.put(IGNORE_ANNOTATION, IGNORE_ANNOTATION);
        m_ctxStrings.put(IGNORE_PRIVATE_OWNED_ANNOTATION, IGNORE_PRIVATE_OWNED_ANNOTATION);
        m_ctxStrings.put(IGNORE_RETURN_INSERT_ANNOTATION, IGNORE_RETURN_INSERT_ANNOTATION);
        m_ctxStrings.put(IGNORE_RETURN_UPDATE_ANNOTATION, IGNORE_RETURN_UPDATE_ANNOTATION);

        // Generic ignore messages that could apply to XML and annotation
        // configurations.
        m_ctxStrings.put(IGNORE_LOB, IGNORE_LOB);
        m_ctxStrings.put(IGNORE_TEMPORAL, IGNORE_TEMPORAL);
        m_ctxStrings.put(IGNORE_ENUMERATED, IGNORE_ENUMERATED);
        m_ctxStrings.put(IGNORE_SERIALIZED, IGNORE_SERIALIZED);
        m_ctxStrings.put(IGNORE_VERSION_LOCKING, IGNORE_VERSION_LOCKING);
        
        m_ctxStrings.put(IGNORE_ATTRIBUTE_OVERRIDE, IGNORE_ATTRIBUTE_OVERRIDE);
        m_ctxStrings.put(IGNORE_ASSOCIATION_OVERRIDE, IGNORE_ASSOCIATION_OVERRIDE);
        
        m_ctxStrings.put(IGNORE_INHERITANCE_SUBCLASS_CACHE, IGNORE_INHERITANCE_SUBCLASS_CACHE);
        m_ctxStrings.put(IGNORE_INHERITANCE_SUBCLASS_CACHE_INTERCEPTOR, IGNORE_INHERITANCE_SUBCLASS_CACHE_INTERCEPTOR);
        m_ctxStrings.put(IGNORE_INHERITANCE_SUBCLASS_DEFAULT_REDIRECTORS, IGNORE_INHERITANCE_SUBCLASS_DEFAULT_REDIRECTORS);
        m_ctxStrings.put(IGNORE_INHERITANCE_SUBCLASS_READ_ONLY, IGNORE_INHERITANCE_SUBCLASS_READ_ONLY);
       
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_COPY_POLICY, IGNORE_MAPPED_SUPERCLASS_COPY_POLICY);
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_ASSOCIATION_OVERRIDE, IGNORE_MAPPED_SUPERCLASS_ASSOCIATION_OVERRIDE);
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_ATTRIBUTE_OVERRIDE, IGNORE_MAPPED_SUPERCLASS_ATTRIBUTE_OVERRIDE);
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_OPTIMISTIC_LOCKING, IGNORE_MAPPED_SUPERCLASS_OPTIMISTIC_LOCKING);
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_CACHE, IGNORE_MAPPED_SUPERCLASS_CACHE);
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_CACHE_INTERCEPTOR, IGNORE_MAPPED_SUPERCLASS_CACHE_INTERCEPTOR);
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_DEFAULT_REDIRECTORS, IGNORE_MAPPED_SUPERCLASS_DEFAULT_REDIRECTORS);
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_CHANGE_TRACKING, IGNORE_MAPPED_SUPERCLASS_CHANGE_TRACKING);
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_CUSTOMIZER, IGNORE_MAPPED_SUPERCLASS_CUSTOMIZER);
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_ID_CLASS, IGNORE_MAPPED_SUPERCLASS_ID_CLASS);
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_READ_ONLY, IGNORE_MAPPED_SUPERCLASS_READ_ONLY);
        m_ctxStrings.put(IGNORE_MAPPED_SUPERCLASS_EXISTENCE_CHECKING, IGNORE_MAPPED_SUPERCLASS_EXISTENCE_CHECKING);
        
        // Generic default messages that could apply to XML and annotation
        // configurations.
        m_ctxStrings.put(ACCESS_TYPE, ACCESS_TYPE);
        
        m_ctxStrings.put(ALIAS, ALIAS);
        m_ctxStrings.put(MAP_KEY_ATTRIBUTE_NAME, MAP_KEY_ATTRIBUTE_NAME);
        
        m_ctxStrings.put(TABLE_NAME, TABLE_NAME);
        m_ctxStrings.put(TABLE_SCHEMA, TABLE_SCHEMA);
        m_ctxStrings.put(TABLE_CATALOG, TABLE_CATALOG);
        
        m_ctxStrings.put(TABLE_GENERATOR_NAME, TABLE_GENERATOR_NAME);
        m_ctxStrings.put(TABLE_GENERATOR_SCHEMA, TABLE_GENERATOR_SCHEMA);
        m_ctxStrings.put(TABLE_GENERATOR_CATALOG, TABLE_GENERATOR_CATALOG);
        
        m_ctxStrings.put(JOIN_TABLE_NAME, JOIN_TABLE_NAME);
        m_ctxStrings.put(JOIN_TABLE_SCHEMA, JOIN_TABLE_SCHEMA);
        m_ctxStrings.put(JOIN_TABLE_CATALOG, JOIN_TABLE_CATALOG);
        
        m_ctxStrings.put(SECONDARY_TABLE_NAME, SECONDARY_TABLE_NAME);
        m_ctxStrings.put(SECONDARY_TABLE_SCHEMA, SECONDARY_TABLE_SCHEMA);
        m_ctxStrings.put(SECONDARY_TABLE_CATALOG, SECONDARY_TABLE_CATALOG);
        
        m_ctxStrings.put(COLLECTION_TABLE_NAME, COLLECTION_TABLE_NAME);
        m_ctxStrings.put(COLLECTION_TABLE_SCHEMA, COLLECTION_TABLE_SCHEMA);
        m_ctxStrings.put(COLLECTION_TABLE_CATALOG, COLLECTION_TABLE_CATALOG);
    
        m_ctxStrings.put(CONVERTER_DATA_TYPE, CONVERTER_DATA_TYPE);
        m_ctxStrings.put(CONVERTER_OBJECT_TYPE, CONVERTER_OBJECT_TYPE);
        
        m_ctxStrings.put(COLUMN, COLUMN);
        m_ctxStrings.put(PK_COLUMN, PK_COLUMN);
        m_ctxStrings.put(FK_COLUMN, FK_COLUMN);
        m_ctxStrings.put(QK_COLUMN, QK_COLUMN);      
        m_ctxStrings.put(ORDER_COLUMN, ORDER_COLUMN);
        m_ctxStrings.put(VALUE_COLUMN, VALUE_COLUMN);
        m_ctxStrings.put(MAP_KEY_COLUMN, MAP_KEY_COLUMN);
        m_ctxStrings.put(SOURCE_PK_COLUMN, SOURCE_PK_COLUMN);
        m_ctxStrings.put(SOURCE_FK_COLUMN, SOURCE_FK_COLUMN);
        m_ctxStrings.put(TARGET_PK_COLUMN, TARGET_PK_COLUMN);
        m_ctxStrings.put(TARGET_FK_COLUMN, TARGET_FK_COLUMN);
        m_ctxStrings.put(VARIABLE_ONE_TO_ONE_DISCRIMINATOR_COLUMN, VARIABLE_ONE_TO_ONE_DISCRIMINATOR_COLUMN); 
        m_ctxStrings.put(INHERITANCE_DISCRIMINATOR_COLUMN, INHERITANCE_DISCRIMINATOR_COLUMN);
        m_ctxStrings.put(INHERITANCE_PK_COLUMN, INHERITANCE_PK_COLUMN);
        m_ctxStrings.put(INHERITANCE_FK_COLUMN, INHERITANCE_FK_COLUMN);
        m_ctxStrings.put(SECONDARY_TABLE_PK_COLUMN, SECONDARY_TABLE_PK_COLUMN);
        m_ctxStrings.put(SECONDARY_TABLE_FK_COLUMN, SECONDARY_TABLE_FK_COLUMN);
        
        m_ctxStrings.put(ONE_TO_ONE_MAPPING, ONE_TO_ONE_MAPPING);
        m_ctxStrings.put(ONE_TO_MANY_MAPPING, ONE_TO_MANY_MAPPING);
        m_ctxStrings.put(VARIABLE_ONE_TO_ONE_MAPPING, VARIABLE_ONE_TO_ONE_MAPPING);
        m_ctxStrings.put(ONE_TO_ONE_MAPPING_REFERENCE_CLASS, ONE_TO_ONE_MAPPING_REFERENCE_CLASS);
        m_ctxStrings.put(ONE_TO_MANY_MAPPING_REFERENCE_CLASS, ONE_TO_MANY_MAPPING_REFERENCE_CLASS);
        m_ctxStrings.put(MANY_TO_ONE_MAPPING_REFERENCE_CLASS, MANY_TO_ONE_MAPPING_REFERENCE_CLASS);
        m_ctxStrings.put(MANY_TO_MANY_MAPPING_REFERENCE_CLASS, MANY_TO_MANY_MAPPING_REFERENCE_CLASS);
        m_ctxStrings.put(VARIABLE_ONE_TO_ONE_MAPPING_REFERENCE_CLASS, VARIABLE_ONE_TO_ONE_MAPPING_REFERENCE_CLASS);
        m_ctxStrings.put(ELEMENT_COLLECTION_MAPPING_REFERENCE_CLASS, ELEMENT_COLLECTION_MAPPING_REFERENCE_CLASS);
        
        m_ctxStrings.put(WARNING_INVALID_COLLECTION_USED_ON_LAZY_RELATION, WARNING_INVALID_COLLECTION_USED_ON_LAZY_RELATION);
        m_ctxStrings.put(WARNING_INCORRECT_DISCRIMINATOR_FORMAT, WARNING_INCORRECT_DISCRIMINATOR_FORMAT);
   
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
    public void logWarningMessage(String ctx, Object param1, Object param2, Object param3, Object param4) {
        log(SessionLog.WARNING, ctx, new Object[] {param1, param2, param3, param4});
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
    public void setSession(AbstractSession session) {
        m_session = session;
    }
}
