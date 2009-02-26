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
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     02/26/2009-2.0 Guy Pelletier 
 *       - 264001: dot notation for mapped-by and order-by
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

import org.eclipse.persistence.mappings.CollectionMapping;

/**
 * INTERNAL:
 * An annotation defined relational collections accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class CollectionAccessor extends RelationshipAccessor {
    // Order by constants
    private static final String ASCENDING = "ASC";
    private static final String DESCENDING = "DESC";
    
    // TODO: mapped but not processed.
    private Class m_mapKeyClass;
    
    // TODO: mapped but not processed.
    private ColumnMetadata m_mapKeyColumn;
    // TODO: mapped but not processed.
    private ColumnMetadata m_orderColumn;
    
    // TODO: mapped but not processed.
    private List<JoinColumnMetadata> m_mapKeyJoinColumns;
    
    private String m_mapKey;
    private String m_mappedBy;
    private String m_orderBy;
    private String m_mapKeyClassName;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    protected CollectionAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    protected CollectionAccessor(Annotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
        
        m_mappedBy = (annotation == null) ? "" : (String) MetadataHelper.invokeMethod("mappedBy", annotation);
        
        // Set the order if one is present.
        if (isAnnotationPresent(OrderBy.class)) {
            m_orderBy = (String) MetadataHelper.invokeMethod("value", getAnnotation(OrderBy.class));
        }
        
        // Set the map key if one is present.
        if (isAnnotationPresent(MapKey.class)) {
            m_mapKey = (String) MetadataHelper.invokeMethod("name", getAnnotation(MapKey.class));
        }
        
        // Set the map key column if one is defined.
        if (isAnnotationPresent(MapKeyColumn.class)) {
            m_mapKeyColumn = new ColumnMetadata(getAnnotation(MapKeyColumn.class), accessibleObject, getAttributeName());
        }
        
        // Set the map key class if one is defined.
        if (isAnnotationPresent(MapKeyClass.class)) {
            m_mapKeyClass = (Class) MetadataHelper.invokeMethod("value", getAnnotation(MapKeyClass.class));
        }
        
        // Set the order column if one is defined.
        if (isAnnotationPresent(OrderColumn.class)) {
            m_orderColumn = new ColumnMetadata(getAnnotation(OrderColumn.class), accessibleObject, getAttributeName());
        }
    }
    
    /**
     * INTERNAL:
     * Return the default fetch type for a collection mapping.
     */
    public Enum getDefaultFetchType() {
        return FetchType.valueOf("LAZY");
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getMapKey() {
        return m_mapKey;
    }
    
    /**
     * INTERNAL: 
     * TODO: Do we need this method?
     */
    public Class getMapKeyClass() {
        return m_mapKeyClass;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public String getMapKeyClassName() {
        return m_mapKeyClassName;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public ColumnMetadata getMapKeyColumn() {
        return m_mapKeyColumn;
    } 
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public List<JoinColumnMetadata> getMapKeyJoinColumns() {
        return m_mapKeyJoinColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getMappedBy() {
        return m_mappedBy;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getOrderBy() {
        return m_orderBy; 
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    protected ColumnMetadata getOrderColumn() {
        return m_orderColumn;
    }
    
    /**
     * INTERNAL:
     * If a targetEntity is specified in metadata, it will be set as the 
     * reference class, otherwise we will look to extract one from generics.
     */
    @Override
    public Class getReferenceClass() {
        if (m_referenceClass == null) {
            m_referenceClass = getTargetEntity();
        
            if (m_referenceClass == void.class) {
                // This call will attempt to extract the reference class from generics.
                m_referenceClass = getReferenceClassFromGeneric();
        
                if (m_referenceClass == null) {
                    // Throw an exception. A relationship accessor must have a 
                    // reference class either through generics or a specified
                    // target entity on the mapping metadata.
                    throw ValidationException.unableToDetermineTargetEntity(getAttributeName(), getJavaClass());
                } else {
                    // Log the defaulting contextual reference class.
                    getLogger().logConfigMessage(getLoggingContext(), getAnnotatedElement(), m_referenceClass);
                }
            } 
        }
        
        return m_referenceClass;
    }
    
    /** 
     * INTERNAL:
     * Return true if this accessor represents a collection accessor.
     */
    public boolean isCollectionAccessor() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor uses a Map.
     */
    public boolean isMapCollectionAccessor() {
        return getAccessibleObject().isSupportedDirectMapClass(getDescriptor());
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
        
        // Initialize single ORMetadata objects.
        initXMLObject(m_mapKeyColumn, accessibleObject);
        initXMLObject(m_orderColumn, accessibleObject);
        
        // Initialize the map key class name we read from XML.
        m_mapKeyClass = initXMLClassName(m_mapKeyClassName);
    }
    
    /**
     * INTERNAL:
     * This process should do any common validation processing of collection 
     * accessors.
     */
    public void process() {
        // Validate the collection type.
        if (! getAccessibleObject().isSupportedCollectionClass(getDescriptor())) {
            throw ValidationException.invalidCollectionTypeForRelationship(getJavaClass(), getRawClass(), getAttributeName());
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void process(CollectionMapping mapping) {
        mapping.setIsReadOnly(false);
        mapping.setIsLazy(isLazy());
        mapping.setIsPrivateOwned(isPrivateOwned());
        mapping.setJoinFetch(getMappingJoinFetchType(getJoinFetch()));
        mapping.setAttributeName(getAttributeName());
        mapping.setReferenceClassName(getReferenceClassName());
        
        // Will check for PROPERTY access
        setAccessorMethods(mapping);

        // Process the cascade types.
        processCascadeTypes(mapping);
        
        // Process an OrderBy if there is one.
        processOrderBy(mapping);
        
        // Process a MapKey if there is one.
        String mapKey = processMapKey(mapping);
        
        // Set the correct indirection on the collection mapping.
        // ** Note the reference class or reference class name needs to be set 
        // on the mapping before setting the indirection policy.
        setIndirectionPolicy(mapping, mapKey, usesIndirection());
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
        
        // Add the mapping to the descriptor.
        addMapping(mapping);
    }
    
    /**
     * INTERNAL:
     * Process a MapKey for a 1-M or M-M mapping. Will return the map key
     * method name that should be use, null otherwise.
     */
    protected String processMapKey(CollectionMapping mapping) {
        String mapKeyMethod = null;
        
        if (isMapCollectionAccessor()) {
            MetadataDescriptor referenceDescriptor = getReferenceDescriptor();
            
            if ((m_mapKey == null || m_mapKey.equals("")) && referenceDescriptor.hasCompositePrimaryKey()) {
                // No persistent property or field name has been provided, and
                // the reference class has a composite primary key class. Let
                // it fall through to return null for the map key. Internally,
                // EclipseLink will use an instance of the composite primary 
                // key class as the map key.
            } else {
                // A persistent property or field name may have have been 
                // provided. If one has not we will default to the primary
                // key of the reference class. The primary key cannot be 
                // composite at this point.
                String fieldOrPropertyName = getName(m_mapKey, referenceDescriptor.getIdAttributeName(), getLogger().MAP_KEY_ATTRIBUTE_NAME);
    
                // Look up the referenceAccessor
                MetadataAccessor referenceAccessor = referenceDescriptor.getAccessorFor(fieldOrPropertyName);
        
                if (referenceAccessor == null) {
                    throw ValidationException.couldNotFindMapKey(fieldOrPropertyName, referenceDescriptor.getJavaClass(), mapping);
                }
        
                mapKeyMethod = referenceAccessor.getAccessibleObjectName();
            }
        }
        
        return mapKeyMethod;
    }
    
    /**
     * INTERNAL:
     * Process an order by value (if specified) for the given collection 
     * mapping. Order by specifies the ordering of the elements of a collection 
     * valued association at the point when the association is retrieved.
     * 
     * The syntax of the value ordering element is an orderby_list, as follows:
     * 
     * orderby_list ::= orderby_item [, orderby_item]*
     * orderby_item ::= property_or_field_name [ASC | DESC]
     * 
     * When ASC or DESC is not specified, ASC is assumed.
     * 
     * If the ordering element is not specified, ordering by the primary key
     * of the associated entity is assumed.
     * 
     * The property or field name must correspond to that of a persistent
     * property or field of the associated class. The properties or fields 
     * used in the ordering must correspond to columns for which comparison
     * operators are supported.
     */
    protected void processOrderBy(CollectionMapping mapping) {
        if (m_orderBy != null) {
            MetadataDescriptor referenceDescriptor = getReferenceDescriptor();
            
            if (m_orderBy.equals("")) {
                // Default to the primary key field name(s).
                List<String> orderByAttributes = referenceDescriptor.getIdOrderByAttributeNames();
            
                if (referenceDescriptor.hasEmbeddedIdAttribute()) {
                    String embeddedIdAttributeName = referenceDescriptor.getEmbeddedIdAttributeName();
                
                    for (String orderByAttribute : orderByAttributes) {
                        mapping.addAggregateOrderBy(embeddedIdAttributeName, orderByAttribute, false);
                    }
                } else {
                    for (String orderByAttribute : orderByAttributes) {
                        mapping.addOrderBy(orderByAttribute, false);
                    }
                }
            } else {
                StringTokenizer commaTokenizer = new StringTokenizer(m_orderBy, ",");
            
                while (commaTokenizer.hasMoreTokens()) {
                    StringTokenizer spaceTokenizer = new StringTokenizer(commaTokenizer.nextToken());
                    String propertyOrFieldName = spaceTokenizer.nextToken();
                    MappingAccessor referenceAccessor = referenceDescriptor.getAccessorFor(propertyOrFieldName);
                
                    if (referenceAccessor == null) {
                        throw ValidationException.invalidOrderByValue(propertyOrFieldName, referenceDescriptor.getJavaClass(), getAccessibleObjectName(), getJavaClass());
                    }

                    String attributeName = referenceAccessor.getAttributeName();                    
                    String ordering = (spaceTokenizer.hasMoreTokens()) ? spaceTokenizer.nextToken() : ASCENDING;

                    if (referenceAccessor.isEmbedded()) {
                        for (String orderByAttributeName : referenceDescriptor.getOrderByAttributeNames()) {
                            mapping.addAggregateOrderBy(m_orderBy, orderByAttributeName, ordering.equals(DESCENDING));        
                        }
                    } else if (referenceAccessor.getClassAccessor().isEmbeddableAccessor()) {
                        // We have a specific order by from an embeddable, we need to rip off 
                        // the last bit of a dot notation if specified and pass in the chained 
                        // string names of the nested embeddables only.
                        String embeddableChain = m_orderBy;
                        if (embeddableChain.contains(".")) {
                            embeddableChain = embeddableChain.substring(0, embeddableChain.lastIndexOf("."));
                        }
                        
                        mapping.addAggregateOrderBy(embeddableChain, attributeName, ordering.equals(DESCENDING)); 
                    } else {
                        mapping.addOrderBy(attributeName, ordering.equals(DESCENDING));    
                    }
                }
            }
        }
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMapKey(String mapKey) {
        m_mapKey = mapKey;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setMapKeyClassName(String mapKeyClassName) {
        m_mapKeyClassName = mapKeyClassName;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setMapKeyColumn(ColumnMetadata mapKeyColumn) {
        m_mapKeyColumn = mapKeyColumn;
    } 
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setMapKeyJoinColumns(List<JoinColumnMetadata> mapKeyJoinColumns) {
        m_mapKeyJoinColumns = mapKeyJoinColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMappedBy(String mappedBy) {
        m_mappedBy = mappedBy;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setOrderBy(String orderBy) {
        m_orderBy = orderBy;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setOrderColumn(ColumnMetadata orderColumn) {
        m_orderColumn = orderColumn;
    }
}
