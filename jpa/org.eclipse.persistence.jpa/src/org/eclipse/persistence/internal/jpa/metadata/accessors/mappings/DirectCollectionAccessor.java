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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 ******************************************************************************/ 
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;

import javax.persistence.FetchType;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.converters.Converter;

/**
 * An absrtact direct collection accessor.
 * 
 * Used to support DirectCollection, DirectMap, AggregateCollection through 
 * the ElementCollection, BasicCollection and BasicMap metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.0
 */
public abstract class DirectCollectionAccessor extends DirectAccessor {
    private Enum m_joinFetch;
    private boolean m_keyContextProcessing;
   
    /**
     * INTERNAL:
     */
    protected DirectCollectionAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    protected DirectCollectionAccessor(Annotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
        
        // Set the fetch type. A basic map may have no annotation (will default).
        if (annotation != null) {
            // Set the fetch type.
            setFetch((Enum) MetadataHelper.invokeMethod("fetch", annotation));
        }
        
        // Set the join fetch if one is present.
        Annotation joinFetch = getAnnotation(JoinFetch.class);            
        if (joinFetch != null) {
            m_joinFetch = (Enum) MetadataHelper.invokeMethod("value", joinFetch);
        }
    }
    
    /**
     * INTERNAL:
     */
    protected String getDefaultCollectionTableName() {
        return getDescriptor().getAlias() + "_" + getUpperCaseAttributeName(); 
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public FetchType getDefaultFetchType() {
        return FetchType.LAZY; 
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public Enum getJoinFetch() {
        return m_joinFetch;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public String getPrivateOwned() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the reference class for this accessor. It will try to extract
     * a reference class from a generic specification. If no generics are used,
     * then it will return void.class. This avoids NPE's when processing
     * JPA converters that can default (Enumerated and Temporal) based on the
     * reference class.
     */
    @Override
    public Class getReferenceClass() {
        Class cls = getReferenceClassFromGeneric();
        return (cls == null) ? void.class : cls;
    }
    
    /**
     * INTERNAL:
     * Return the converter name for a map key.
     */
    protected abstract String getKeyConverter();
    
    /**
     * INTERNAL:
     * Return the converter name for a collection value.
     * @see BasicMapAccessor for override details. An EclipseLink 
     * BasicMapAccessor can specify a value converter within the BasicMap
     * metadata. Otherwise for all other cases, BasicCollectionAccessor and
     * ElementCollectionAccessor, the value converter is returned from a Convert
     * metadata specification.
     */
    protected String getValueConverter() {
        return getConvert();
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a direct collection mapping, 
     * which include basic collection, basic map and element collection 
     * accessors.
     */
    @Override
    public boolean isDirectCollection() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Returns true if we are processing metadata for the key context. Only 
     * applies in a direct map case. The default is false indicating a value
     * column. Those accessors that process key metadata, 
     * ElementCollectionAccessor and BasicMapAccessor are responsible for 
     * setting the context when processing converters.
     */ 
    protected boolean isKeyContextProcessing() {
        return m_keyContextProcessing;
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is a valid basic collection type.
     */ 
    protected boolean isValidDirectCollectionType() {
        return getAccessibleObject().isSupportedDirectCollectionClass(getDescriptor());
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is a valid basic map type.
     */ 
    protected boolean isValidDirectMapType() {
        return getAccessibleObject().isSupportedDirectMapClass(getDescriptor());
    }
    
    /**
     * INTERNAL:
     */
    protected void process(CollectionMapping mapping) {
        // Set the attribute name.
        mapping.setAttributeName(getAttributeName());
        
        // Will check for PROPERTY access
        setAccessorMethods(mapping);
        
        // Process join fetch type.
        mapping.setJoinFetch(getMappingJoinFetchType(m_joinFetch));
        
        // Process the collection table.
        processCollectionTable(mapping);
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
        
        // Add the mapping to the descriptor.
        addMapping(mapping);
    }
    
    /**
     * INTERNAL:
     * Process a MetadataCollectionTable.
     */
    protected abstract void processCollectionTable(CollectionMapping mapping);
    
    /**
     * INTERNAL:
     * Process a convert value to apply a specified EclipseLink converter 
     * (Converter, TypeConverter, ObjectTypeConverter) to the given mapping.
     * 
     * This method is called in second stage processing and should only be
     * called on accessors that have a @Convert specified.
     */
    @Override
    public void processConvert() {
        DatabaseMapping mapping = getDescriptor().getMappingForAttributeName(getAttributeName());
        
        if (mapping.isDirectMapMapping()) {
            m_keyContextProcessing = true;
            processConvert(mapping, getKeyConverter());
        }
        
        m_keyContextProcessing = false;
        processConvert(mapping, getValueConverter());
    }
    
    /**
     * INTERNAL:
     */
    protected void processDirectCollectionMapping() {
        // Initialize our mapping.
        DirectCollectionMapping mapping = new DirectCollectionMapping();
        
        // Process common direct collection metadata. This must be done 
        // before any field processing since field processing requires that 
        // the collection table be processed before hand.
        process(mapping);
        
        // Process the fetch type and set the correct indirection on the 
        // mapping.
        setIndirectionPolicy(mapping, null, usesIndirection());
        
        // Process the value column (we must process this field before the 
        // call to processConverter, since it may set a field classification)
        mapping.setDirectField(getDatabaseField(mapping.getReferenceTable(), MetadataLogger.VALUE_COLUMN));
        
        // Process a converter for this mapping. We will look for a convert
        // value. If none is found then we'll look for a JPA converter, that 
        // is, Enumerated, Lob and Temporal. With everything falling into 
        // a serialized mapping if no converter whatsoever is found.
        processMappingConverter(mapping, getValueConverter());
    }
    
    /**
     * INTERNAL:
     */
    protected void processDirectMapMapping() {
        // Initialize our mapping.
        DirectMapMapping mapping = new DirectMapMapping();
        
        // Process common direct collection metadata. This must be done 
        // before any field processing since field processing requires that 
        // the collection table be processed before hand.
        process(mapping);
        
        // Process the fetch type
        setIndirectionPolicy(mapping, null, usesIndirection());
        
        // Process the key column (we must process this field before the call
        // to processConverter, since it may set a field classification)
        mapping.setDirectKeyField(getDatabaseField(mapping.getReferenceTable(), MetadataLogger.MAP_KEY_COLUMN));
        
        // Process a converter for the key column of this mapping.
        processMappingConverter(mapping, getKeyConverter());
        
        // Process the value column (we must process this field before the call
        // to processConverter, since it may set a field classification)
        mapping.setDirectField(getDatabaseField(mapping.getReferenceTable(), MetadataLogger.VALUE_COLUMN));
        
        // Process a converter for value column of this mapping.
        processMappingConverter(mapping, getValueConverter());    
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void setConverter(DatabaseMapping mapping, Converter converter) {
        if (mapping.isDirectMapMapping() && isKeyContextProcessing()) {
            ((DirectMapMapping) mapping).setKeyConverter(converter);
        } else {
            ((DirectCollectionMapping) mapping).setValueConverter(converter);
        }
        
        // TODO: AggregateCollections should only be able to get in here for a key converter
    }

    /**
     * INTERNAL:
     */
    @Override
    public void setConverterClassName(DatabaseMapping mapping, String converterClassName) {
        if (mapping.isDirectMapMapping() && isKeyContextProcessing()) {
            ((DirectMapMapping) mapping).setKeyConverterClassName(converterClassName);
        } else {
            ((DirectCollectionMapping) mapping).setValueConverterClassName(converterClassName);
        }
        
        // TODO: AggregateCollections should only be able to get in here for a key converter
    }
    
    /**
     * INTERNAL: 
     */
    @Override
    public void setFieldClassification(DatabaseMapping mapping, Class classification) {
        if (mapping.isDirectMapMapping() && isKeyContextProcessing()) {
            ((DirectMapMapping) mapping).setDirectKeyFieldClassification(classification);
        } else {
            ((DirectCollectionMapping) mapping).setDirectFieldClassification(classification);
        }
        
        // TODO: AggregateCollections going to get in here??
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setJoinFetch(Enum joinFetch) {
        m_joinFetch = joinFetch;
    }
}
