/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     08/27/2008-1.1 Guy Pelletier 
 *       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/03/2009-2.0 Guy Pelletier
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     06/25/2009-2.0 Michael O'Brien 
 *       - 266912: change MappedSuperclass handling in stage2 to pre process accessors
 *          in support of the custom descriptors holding mappings required by the Metamodel. 
 *          We handle undefined parameterized generic types for a MappedSuperclass defined
 *          Map field by returning Void in this case.
 *     09/29/2009-2.0 Guy Pelletier 
 *       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 *     01/22/2010-2.0.1 Guy Pelletier 
 *       - 294361: incorrect generated table for element collection attribute overrides
 *     01/26/2010-2.0.1 Guy Pelletier 
 *       - 299893: @MapKeyClass does not work with ElementCollection
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     05/19/2010-2.1 Guy Pelletier 
 *       - 313574: Lower case primary key column association does not work when upper casing flag is set to true
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.eclipse.persistence.annotations.Properties;
import org.eclipse.persistence.annotations.Property;
import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.annotations.ReturnUpdate;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.descriptors.VirtualAttributeAccessor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.AbstractConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ClassInstanceMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.EnumeratedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.LobMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.SerializedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TemporalMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.MapKeyMetadata;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;

import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.EmbeddableMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.foundation.MapComponentMapping;
import org.eclipse.persistence.mappings.foundation.MapKeyMapping;

/**
 * INTERNAL:
 * An abstract mapping accessor. Holds common metadata for all mappings.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public abstract class MappingAccessor extends MetadataAccessor {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.
    
    // Reserved converter names
    private static final String CONVERT_NONE = "none";
    private static final String CONVERT_SERIALIZED = "serialized";
    private static final String CONVERT_CLASS_INSTANCE = "class-instance";

    // Used for looking up attribute overrides for a map accessor. 
    protected static final String KEY_DOT_NOTATION = "key.";
    protected static final String VALUE_DOT_NOTATION = "value.";

    private final static String DEFAULT_MAP_KEY_COLUMN_SUFFIX = "_KEY";

    private ClassAccessor m_classAccessor;
    private DatabaseMapping m_mapping;
    private DatabaseMapping m_overrideMapping;
    private Map<String, PropertyMetadata> m_properties = new HashMap<String, PropertyMetadata>();
    private String m_attributeType;
    
    /**
     * INTERNAL:
     */
    protected MappingAccessor(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor.getDescriptor(), classAccessor.getProject());
        
        // We must keep a reference to the class accessors where this
        // mapping accessor is defined. We need it to determine access types.
        m_classAccessor = classAccessor;
    }
    
    /**
     * INTERNAL:
     */
    protected MappingAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof MappingAccessor) {
            MappingAccessor mappingAccessor = (MappingAccessor) objectToCompare;
            
            return valuesMatch(m_attributeType, mappingAccessor.getAttributeType());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true is this accessor is a derived id accessor.
     * @see ObjectAccessor
     */
    public boolean derivesId() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Process an attribute override for either an embedded object mapping, or
     * an element collection mapping containing embeddable objects.
     */
    protected void addFieldNameTranslation(EmbeddableMapping embeddableMapping, String overrideName, DatabaseField overrideField, MappingAccessor aggregatesAccessor) {
        DatabaseMapping aggregatesMapping = aggregatesAccessor.getMapping();
        DatabaseField aggregatesMappingField = aggregatesMapping.getField();
        
        // If we are specifying an attribute override to an id field that is
        // within the embeddable we must update the primary key field on the
        // owning descriptor.
        if (aggregatesAccessor.isId()) {
            updatePrimaryKeyField(aggregatesAccessor, overrideField);
        }
        
        if (overrideName.indexOf(".") > -1) {
            // Set the nested field name translation on the mapping. In an
            // Embedded case, this call will do the same thing that 
            // addFieldNameTranslation would do, there is no special treatment
            // and is implemented on aggregate object mapping only to satisfy
            // the EmbeddableMapping interface requirements. Nested attribute
            // overrides on an aggregate collection mapping are handled slightly
            // different though.
            embeddableMapping.addNestedFieldNameTranslation(overrideName, overrideField.getQualifiedName(), aggregatesMappingField.getName());
        } else {
            // Set the field name translation on the mapping.
            embeddableMapping.addFieldNameTranslation(overrideField.getQualifiedName(), aggregatesMappingField.getName()); 
        }
    }
    
    /**
     * INTERNAL:
     * Process the list of association overrides into a map, merging and 
     * overriding any association overrides where necessary with descriptor
     * level association overrides.
     */
    protected Map<String, AssociationOverrideMetadata> getAssociationOverrides(List<AssociationOverrideMetadata> associationOverrides) {
        // TODO: Be nice to look for duplicates within the same list.
        Map<String, AssociationOverrideMetadata> associationOverridesMap = new HashMap<String, AssociationOverrideMetadata>();
        
        for (AssociationOverrideMetadata associationOverride : associationOverrides) {
            String name = associationOverride.getName();
            
            // An association override from a sub-entity class will name its
            // association override slightly different in that it will have 
            // one extra dot notation at the front. E.G. A mapped superclass 
            // that defines an embedded attribute named 'record' can define 
            // association overrides directly on the mapping, that is, 
            // 'date'. Whereas from an entity class to override 'date' on 
            // 'record', the attribute name will be 'record.date'
            String dotNotationName = getAttributeName() + "." + name;
            if (getClassAccessor().isMappedSuperclass() && getDescriptor().hasAssociationOverrideFor(dotNotationName)) {
                getLogger().logConfigMessage(getLogger().IGNORE_ASSOCIATION_OVERRIDE, name, getAttributeName(), getClassAccessor().getJavaClassName(), getJavaClassName());
                associationOverridesMap.put(name, getDescriptor().getAssociationOverrideFor(dotNotationName));
            } else {
                associationOverridesMap.put(name, associationOverride);
            }
        }
        
        // Now add every other descriptor association override that didn't 
        // override a mapping level one (if we are processing a mapping from
        // a mapped superclass level). We'll check the attribute names match
        // and rip off the extra qualifying when adding it to the override map.
        if (getClassAccessor().isMappedSuperclass()) {
            for (AssociationOverrideMetadata associationOverride : getDescriptor().getAssociationOverrides()) {
                String name = associationOverride.getName();
                String attributeName = name;
                String overrideName = name;
                int indexOfFirstDot = name.indexOf(".");                

                if (indexOfFirstDot > -1) {
                    attributeName = name.substring(0, indexOfFirstDot);
                    overrideName = name.substring(indexOfFirstDot + 1);
                }
                 
                if (attributeName.equals(getAttributeName()) && ! associationOverridesMap.containsKey(attributeName)) {
                    associationOverridesMap.put(overrideName, associationOverride);
                }
            }
        }
        
        return associationOverridesMap;
    }
    
    /**
     * INTERNAL:
     * Return the attribute name for this accessor. This is typically the
     * attribute name on the accessible object (i.e., field or property name),
     * however, if access-methods have been specified, use the name attribute
     * that was specified in XML. (e.g. basic name="sin") and not the property
     * name of the get method from the access-methods specification.
     */
    @Override
    public String getAttributeName() {
        if (hasAccessMethods()) {
            return getName();
        } else {
            return getAccessibleObject().getAttributeName();
        }
    }
    
    /**
     * INTERNAL:
     * Return the attribute override for this accessor.
     */
    protected AttributeOverrideMetadata getAttributeOverride(String loggingCtx) {
        if (loggingCtx.equals(MetadataLogger.MAP_KEY_COLUMN)) {
            return getDescriptor().getAttributeOverrideFor(KEY_DOT_NOTATION + getAttributeName());
        } else if (loggingCtx.equals(MetadataLogger.VALUE_COLUMN)) {
            if (getDescriptor().hasAttributeOverrideFor(VALUE_DOT_NOTATION + getAttributeName())) {
                return getDescriptor().getAttributeOverrideFor(VALUE_DOT_NOTATION + getAttributeName());
            }
        } 
            
        return getDescriptor().getAttributeOverrideFor(getAttributeName());
    }
    
    /**
     * INTERNAL:
     * Process the list of attribute overrides into a map, merging and 
     * overriding any attribute overrides where necessary with descriptor
     * level attribute overrides.
     */
    protected Map<String, AttributeOverrideMetadata> getAttributeOverrides(List<AttributeOverrideMetadata> attributeOverrides) {
        // TODO: Be nice to look for duplicates within the same list.
        HashMap<String, AttributeOverrideMetadata> attributeOverridesMap = new HashMap<String, AttributeOverrideMetadata>();
        
        for (AttributeOverrideMetadata attributeOverride : attributeOverrides) {
            String name = attributeOverride.getName();
            
            // An attribute override from a sub-entity class will name its
            // attribute override slightly different in that it will have one
            // extra dot notation at the front. E.G. A mapped superclass that
            // defines an embedded attribute named 'record' can define attribute
            // overrides directly on the mapping, that is, 'date'. Whereas
            // from an entity class to override 'date' on 'record', the
            // attribute name will be 'record.date'
            String dotNotationName = getAttributeName() + "." + name;
            if (getClassAccessor().isMappedSuperclass() && getDescriptor().hasAttributeOverrideFor(dotNotationName)) {
                getLogger().logConfigMessage(getLogger().IGNORE_ATTRIBUTE_OVERRIDE, name, getAttributeName(), getClassAccessor().getJavaClassName(), getJavaClassName());
                attributeOverridesMap.put(name, getDescriptor().getAttributeOverrideFor(dotNotationName));
            } else {
                attributeOverridesMap.put(name, attributeOverride);
            }
        }
        
        // Now add every other descriptor association override that didn't 
        // override a mapping level one (if we are processing a mapping from
        // a mapped superclass level). We'll check the attribute names match
        // and rip off the extra qualifying when adding it to the override map.
        if (getClassAccessor().isMappedSuperclass()) {
            for (AttributeOverrideMetadata attributeOverride : getDescriptor().getAttributeOverrides()) {
                String name = attributeOverride.getName();
                String attributeName = name;
                String overrideName = name;
                int indexOfFirstDot = name.indexOf(".");                

                if (indexOfFirstDot > -1) {
                    attributeName = name.substring(0, indexOfFirstDot);
                    overrideName = name.substring(indexOfFirstDot + 1);
                }
                 
                if (attributeName.equals(getAttributeName()) && ! attributeOverridesMap.containsKey(attributeName)) {
                    attributeOverridesMap.put(overrideName, attributeOverride);
                }
            }
        }
        
        return attributeOverridesMap;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getAttributeType() {
        return m_attributeType;
    }
    
    /**
     * INTERNAL:
     * Returns the class accessor on which this mapping was defined.
     */
    public ClassAccessor getClassAccessor(){
        return m_classAccessor;
    }
    
    /**
     * INTERNAL:
     * Subclasses should override this method to return the appropriate
     * column for their mapping.
     * @see BasicAccessor
     * @see BasicCollectionAccessor
     * @see BasicMapAccessor
     * @see ElementCollectionAccessor
     * @see CollectionAccessor
     */
    protected ColumnMetadata getColumn(String loggingCtx) {
        return new ColumnMetadata(getAccessibleObject());
    }
    
    /**
     * INTERNAL:
     * Process column metadata details into a database field. This will set 
     * correct metadata and log defaulting messages to the user. It also looks 
     * for an attribute override.
     * 
     * This method will call getColumn() which assumes the subclasses will
     * return the appropriate ColumnMetadata to process based on the context
     * provided.
     * 
     * @See BasicCollectionAccessor and BasicMapAccessor.
     */
    protected DatabaseField getDatabaseField(DatabaseTable defaultTable, String loggingCtx) {
        // Check if we have an attribute override first, otherwise process for a column
        ColumnMetadata column  = hasAttributeOverride(loggingCtx) ? getAttributeOverride(loggingCtx).getColumn() : getColumn(loggingCtx);
        
        // Get the actual database field and apply any defaults.
        DatabaseField field = column.getDatabaseField();
           
        // Make sure there is a table name on the field.
        if (!field.hasTableName()) {
            field.setTable(defaultTable);
        }
          
        // Set the correct field name, defaulting and logging when necessary.
        String defaultName = getDefaultAttributeName();
           
        // If this is for a map key column, append a suffix.
        if (loggingCtx.equals(MetadataLogger.MAP_KEY_COLUMN)) {
            defaultName += DEFAULT_MAP_KEY_COLUMN_SUFFIX;
        }
        
        setFieldName(field, defaultName, loggingCtx);
        
        if (field.getTable() != null) {
            if (useDelimitedIdentifier()) {
                field.getTable().setUseDelimiters(useDelimitedIdentifier());
            }
        }

        return field;
    }
    
    /**
     * INTERNAL:
     */
    protected String getDefaultFetchType() {
        return FetchType.EAGER.name(); 
    }
    
    /**
     * INTERNAL:
     * Return the default table to hold the foreign key of a MapKey when
     * and Entity is used as the MapKey
     * @return
     */
    protected DatabaseTable getDefaultTableForEntityMapKey(){
        return getReferenceDescriptor().getPrimaryTable();
    }
    
    /**
     * INTERNAL:
     * Return the enumerated metadata for this accessor.
     * @see DirectAccessor
     * @see ElementCollectionAccessor
     * @see CollectionAccessor
     */
    public EnumeratedMetadata getEnumerated(boolean isForMapKey) {
        return null;
    }
    
    /**
     * INTERNAL:
     * Returns the get method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    public String getGetMethodName() {
        return hasAccessMethods() ? getAccessMethods().getGetMethodName() : getAccessibleObjectName(); 
    }
    
    /**
     * INTERNAL:
     * Return the join columns to use with this mapping accessor. This method 
     * will look for association overrides and use those instead if some are 
     * available. This method will validate the join columns and default
     * any where necessary.
     */
    protected List<JoinColumnMetadata> getJoinColumns(List<JoinColumnMetadata> potentialJoinColumns, MetadataDescriptor descriptor) {
        if (getDescriptor().hasAssociationOverrideFor(getAttributeName())) {
            return getJoinColumnsAndValidate(getDescriptor().getAssociationOverrideFor(getAttributeName()).getJoinColumns(), descriptor);
        } else {
            return getJoinColumnsAndValidate(potentialJoinColumns, descriptor);
        }
    }
    
    /**
     * INTERNAL:
     * This method will validate the join columns and default any where 
     * necessary.
     */    
    protected List<JoinColumnMetadata> getJoinColumnsAndValidate(List<JoinColumnMetadata> joinColumns, MetadataDescriptor descriptor) {
        if (joinColumns.isEmpty()) {
            if (descriptor.hasCompositePrimaryKey()) {
                // Add a default one for each part of the composite primary
                // key. Foreign and primary key to have the same name.
                for (String primaryKeyField : descriptor.getPrimaryKeyFieldNames()) {
                    JoinColumnMetadata joinColumn = new JoinColumnMetadata();
                    joinColumn.setReferencedColumnName(primaryKeyField);
                    joinColumn.setName(primaryKeyField);
                    joinColumns.add(joinColumn);
                }
            } else {
                // Add a default one for the single case, not setting any
                // foreign and primary key names. They will default based
                // on which accessor is using them.
                joinColumns.add(new JoinColumnMetadata());
            }
        } else {
            // Need to update any join columns that use a foreign key name
            // for the primary key name. E.G. User specifies the renamed id
            // field name from a primary key join column as the primary key in
            // an inheritance subclass.
            for (JoinColumnMetadata joinColumn : joinColumns) {
                // Doing this could potentially change a value entered in XML.
                // However, in this case I think that is ok since in theory we 
                // are writing out the correct value that EclipseLink needs to 
                // form valid queries.
                String referencedColumnName = joinColumn.getReferencedColumnName();
                // The referenced column name in a variable one to one case is a 
                // query key name and not a column name so bypass any of this
                // code.
                if (referencedColumnName != null && !isVariableOneToOne()) {
                    DatabaseField referencedField = joinColumn.getPrimaryKeyField();
                    setFieldName(referencedField, referencedField.getName(), MetadataLogger.PK_COLUMN);
                    joinColumn.setReferencedColumnName(descriptor.getPrimaryKeyJoinColumnAssociation(referencedField.getName()));
                }
            }
        }
        
        if (descriptor.hasCompositePrimaryKey()) {
            // The number of join columns should equal the number of primary key fields.
            if (joinColumns.size() != descriptor.getPrimaryKeyFields().size()) {
                throw ValidationException.incompleteJoinColumnsSpecified(getAnnotatedElement(), getJavaClass());
            }
            
            // All the primary and foreign key field names should be specified.
            for (JoinColumnMetadata joinColumn : joinColumns) {
                if (joinColumn.isPrimaryKeyFieldNotSpecified() || joinColumn.isForeignKeyFieldNotSpecified()) {
                    throw ValidationException.incompleteJoinColumnsSpecified(getAnnotatedElement(), getJavaClass());
                }
            }
        }
        
        return joinColumns;
    }
    
    /**
     * INTERNAL:
     * Return the lob metadata for this accessor.
     * @see DirectAccessor
     */
    public LobMetadata getLob(boolean isForMapKey) {
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the mapping that this accessor is associated to.
     */
    public DatabaseMapping getMapping(){
        return (m_overrideMapping == null) ? m_mapping : m_overrideMapping;
    }
    
    /**
     * INTERNAL:
     * Return the owning descriptor of this accessor.
     */
    public MetadataDescriptor getOwningDescriptor() {
        return getClassAccessor().getOwningDescriptor();
    }
    
    /**
     * INTERNAL:
     * Return the owning descriptors of this accessor. In most cases this is
     * a single descriptors. Multiples can only exist when dealing with 
     * accessors for an embeddable that is shared.
     */
    public List<MetadataDescriptor> getOwningDescriptors() {
        return getClassAccessor().getOwningDescriptors();
    }
    
    /**
     * INTERNAL:
     * Return the map key if this mapping accessor employs one. Those accessors
     * that support it should override this method.
     * @see CollectionAccessor
     * @see ElementCollectionAccessor
     */
    public MapKeyMetadata getMapKey() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the map key reference class for this accessor if applicable. It 
     * will try to extract a reference class from a generic specification.
     * Parameterized generic keys on a MappedSuperclass will return void.class.  
     * If no generics are used, then it will return void.class. This avoids NPE's 
     * when processing JPA converters that can default (Enumerated and Temporal) 
     * based on the reference class.
     */
    public MetadataClass getMapKeyReferenceClass() {
        // First check if we are a mapped key map accessor and return its map
        // key class if specified. Otherwise continue on to extract it from
        // a generic specification. We do this to avoid going to the class
        // with is needed for dynamic persistence.
        if (isMappedKeyMapAccessor()) {
            MetadataClass mapKeyClass = ((MappedKeyMapAccessor) this).getMapKeyClass();
            if (mapKeyClass != null && ! mapKeyClass.equals(void.class)) {
                return mapKeyClass;
            }
        }
        
        if (isMapAccessor()) {
            MetadataClass referenceClass = getAccessibleObject().getMapKeyClass(getDescriptor());
        
            if (referenceClass == null) {
                throw ValidationException.unableToDetermineMapKeyClass(getAttributeName(), getJavaClass());
            }            
        
            // 266912:  Use of parameterized generic types like Map<X,Y> 
            // inherits from class<T> in a MappedSuperclass field will cause 
            // referencing issues - as in we are unable to determine the correct 
            // type for T. A workaround for this is to detect when we are in 
            // this state and return a standard top level class. An invalid 
            // class will be of the form MetadataClass.m_name="T" 
            if (getClassAccessor().isMappedSuperclass()) {
                // Determine whether we are directly referencing a class or 
                // using a parameterized generic reference by trying to load the 
                // class and catching any validationException. If we do not get 
                // an exception on getClass then the referenceClass.m_name is 
                // valid and should be directly returned
                try {
                    MetadataHelper.getClassForName(referenceClass.getName(), getMetadataFactory().getLoader());
                } catch (ValidationException exception) {
                    // Default to Void for parameterized types
                    // Ideally we would need a MetadataClass.isParameterized() to inform us instead.
                    return getMetadataClass(Void.class);
                }                          
            }
            
            return referenceClass;
        } else {
            return getMetadataClass(void.class);
        }
    }
    
    /**
     * INTERNAL:
     * Return the map key reference class name
     */
    public String getMapKeyReferenceClassName() {
        return getMapKeyReferenceClass().getName();
    }
    
    /**
     * INTERNAL:
     * Return the raw class for this accessor. 
     * E.g. For an accessor with a type of java.util.Collection<Employee>, this 
     * method will return java.util.Collection
     */
    public MetadataClass getRawClass() {
        if (m_attributeType == null) {
            MetadataClass rawClass = getAccessibleObject().getRawClass(getDescriptor());

            // Raw class == null and no attribute-type is specified, try the
            // reference class since some mappings (1-1, M-1 or V1-1) don't 
            // require an attribute-type specification since it can be specified
            // through the target entity/interface specification.
            return (rawClass == null) ? getReferenceClass() : rawClass;         
        } else {
            // If the class doesn't exist the factory we'll just return a
            // generic MetadataClass
            return getMetadataClass(m_attributeType);
        }
    }
    
    /**
     * INTERNAL:
     * Return the mapping accessors associated with the reference descriptor.
     */
    public Collection<MappingAccessor> getReferenceAccessors() {
        return getReferenceDescriptor().getMappingAccessors();
    }
    
    /**
     * INTERNAL: 
     * Return the reference class for this accessor. By default the reference
     * class is the raw class. Some accessors may need to override this
     * method to drill down further. That is, try to extract a reference class
     * from generics.
     */
    public MetadataClass getReferenceClass() {
        return getRawClass();
    }
    
    /**
     * INTERNAL:
     * Attempts to return a reference class from a generic specification. Note,
     * this method may return null.
     */
    public MetadataClass getReferenceClassFromGeneric() {
        return getAccessibleObject().getReferenceClassFromGeneric(getDescriptor());
    }

    /**
     * INTERNAL:
     * Return the reference class name for this accessor.
     */
    public String getReferenceClassName() {
        return getReferenceClass().getName();
    }
    
    /**
     * INTERNAL:
     * Return the reference descriptors table. By default it is the primary
     * key table off the reference descriptor. Subclasses that care to return
     * a different class should override this method.
     * @see DirectCollectionAccessor
     * @see ManyToManyAccessor
     */
    protected DatabaseTable getReferenceDatabaseTable() {
        return getReferenceDescriptor().getPrimaryKeyTable();
    }
    
    /**
     * INTERNAL:
     * Return the reference metadata descriptor for this accessor.
     */
    public MetadataDescriptor getReferenceDescriptor() {
        ClassAccessor accessor = getProject().getAccessor(getReferenceClassName());
        
        if (accessor == null) {
            throw ValidationException.classNotListedInPersistenceUnit(getReferenceClassName());
        }
        
        return accessor.getDescriptor();
    }
    
    /**
     * INTERNAL:
     * Returns the set method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    public String getSetMethodName() {
        return hasAccessMethods() ? getAccessMethods().getSetMethodName() : ((MetadataMethod) getAccessibleObject()).getSetMethodName();
    }
    
    /**
     * INTERNAL:
     * Return the temporal metadata for this accessor.
     * @see DirectAccessor
     * @see CollectionAccessor
     */
    public TemporalMetadata getTemporal(boolean isForMapKey) {
        return null;
    }
    
    /**
     * INTERNAL:
     * Return true if we have an attribute override for this accessor.
     */
    protected boolean hasAttributeOverride(String loggingCtx) {
        if (loggingCtx.equals(MetadataLogger.MAP_KEY_COLUMN)) {
            return getDescriptor().hasAttributeOverrideFor(KEY_DOT_NOTATION + getAttributeName());
        } else if (loggingCtx.equals(MetadataLogger.VALUE_COLUMN)) {
            if (getDescriptor().hasAttributeOverrideFor(VALUE_DOT_NOTATION + getAttributeName())) {
                return true;
            }
        } 
            
        return getDescriptor().hasAttributeOverrideFor(getAttributeName());
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasAttributeType() {
        return m_attributeType != null;
    }
    
    /**
     * INTERNAL:
     * Method to check if an annotated element has a Column annotation.
     */
    protected boolean hasColumn() {
        return isAnnotationPresent(Column.class);
    }
    
    /**
     * INTERNAL:
     * Method to check if an annotated element has a convert specified. In XML
     * we can restrict where converts can be specified.
     * @see DirectAccessor
     * @see BasicMapAccessor
     * @see ElementCollectionAccessor
     * @see CollectionAccessor
     */
    protected boolean hasConvert(boolean isForMapKey) {
        return isAnnotationPresent(Convert.class);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has temporal metadata.
     * @see DirectAccessor
     * @see ElementCollectionAccessor
     * @see CollectionAccessor
     */
    protected boolean hasEnumerated(boolean isForMapKey) {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has lob metadata.
     * @see DirectAccessor
     * @see ElementCollectionAccessor
     * @see CollectionAccessor
     */
    protected boolean hasLob(boolean isForMapKey) {
        return false;
    }
    
    /**
     * INTERNAL:
     * Method should be overridden by those accessors that accept and use a map 
     * key.
     * @see CollectionAccessor
     * @see ElementCollectionAccessor
     * @see BasicMapAccessor
     */
    public boolean hasMapKey() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Method to check if this accessor has a ReturnInsert annotation.
     */
    protected boolean hasReturnInsert() {
        return isAnnotationPresent(ReturnInsert.class);
    }
    
    /**
     * INTERNAL:
     * Method to check if this accessor has a ReturnUpdate annotation.
     */
    protected boolean hasReturnUpdate() {
        return isAnnotationPresent(ReturnUpdate.class);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has temporal metadata.
     * @see DirectAccessor
     * @see ElementCollectionAccessor
     * @see CollectionAccessor
     */
    public boolean hasTemporal(boolean isForMapKey) {
        return false;
    }
    
    /**
     * INTERNAL: 
     * Init an xml mapping accessor with its necessary components. 
     */
    public void initXMLMappingAccessor(ClassAccessor classAccessor) {
        m_classAccessor = classAccessor;
        setEntityMappings(classAccessor.getEntityMappings());
        initXMLAccessor(classAccessor.getDescriptor(), classAccessor.getProject());   
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a basic mapping.
     */
    public boolean isBasic() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a basic collection mapping.
     */
    public boolean isBasicCollection() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a basic map mapping.
     */
    public boolean isBasicMap() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor is a derived id class accessor.
     */
    public boolean isDerivedIdClass(){
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a direct collection mapping, 
     * which include basic collection, basic map and element collection 
     * accessors.
     */
    public boolean isDirectCollection() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents an element collection that
     * contains embeddable objects.
     */
    public boolean isDirectEmbeddableCollection() {
        return false;
    }
    
    /** 
     * INTERNAL:
     * Return true if this accessor represents a collection accessor.
     */
    public boolean isCollectionAccessor() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents an aggregate mapping.
     */
    public boolean isEmbedded() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents an aggregate id mapping.
     */
    public boolean isEmbeddedId() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this represents an enum type mapping. Will return true
     * if the accessor's reference class is an enum or if enumerated metadata
     * exists.
     */
    protected boolean isEnumerated(MetadataClass referenceClass, boolean isForMapKey) {
        if (hasConvert(isForMapKey)) {
            // If we have an @Enumerated with a @Convert, the @Convert takes
            // precedence and we will ignore the @Enumerated and log a message.
            if (hasEnumerated(isForMapKey)) {
                getLogger().logWarningMessage(MetadataLogger.IGNORE_ENUMERATED, getJavaClass(), getAnnotatedElement());
            }
            
            return false;
        } else {
            return hasEnumerated(isForMapKey) || EnumeratedMetadata.isValidEnumeratedType(referenceClass);
        }
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor is part of the id.
     */
    public boolean isId(){
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a BLOB/CLOB mapping.
     */
    protected boolean isLob(MetadataClass referenceClass, boolean isForMapKey) {
        if (hasConvert(isForMapKey)) {
            // If we have a Lob specified with a Convert, the Convert takes 
            // precedence and we will ignore the Lob and log a message.
            if (hasLob(isForMapKey)) {
                getLogger().logWarningMessage(MetadataLogger.IGNORE_LOB, getJavaClass(), getAnnotatedElement());
            }
            
            return false;
        } else {
            return hasLob(isForMapKey);
        }
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a m-m relationship.
     */
    public boolean isManyToMany() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a m-1 relationship.
     */
    public boolean isManyToOne() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor uses a Map.
     */
    public boolean isMapAccessor() {
        return getAccessibleObject().isSupportedMapClass(getRawClass());
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor is a mapped key map accessor. It is a
     * map key accessor for two reasons, it's a map and it does not have 
     * a map key specified. NOTE: we can't check for a map key class since
     * one may not have been explicitly specified. In this case, a generic 
     * value must be set and we check for one when adding accessors (and in 
     * turn set the map key class at that point)
     */
    public boolean isMappedKeyMapAccessor() {
        return MappedKeyMapAccessor.class.isAssignableFrom(getClass()) && isMapAccessor() && ! hasMapKey();
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a 1-m relationship.
     */
    public boolean isOneToMany() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a 1-1 relationship.
     */
    public boolean isOneToOne() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Returns true is the given class is primitive wrapper type.
     */
    protected boolean isPrimitiveWrapperClass(MetadataClass cls) {
        return cls.extendsClass(Number.class) ||
            cls.equals(Boolean.class) ||
            cls.equals(Character.class) ||
            cls.equals(String.class) ||
            cls.extendsClass(java.math.BigInteger.class) ||
            cls.extendsClass(java.math.BigDecimal.class) ||
            cls.extendsClass(java.util.Date.class) ||
            cls.extendsClass(java.util.Calendar.class);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has been processed. If there is a mapping
     * set, we have processed this accessor.
     */
    @Override
    public boolean isProcessed() {
        return m_mapping != null;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor method represents a relationship.
     */
    public boolean isRelationship() {
        return isManyToOne() || isManyToMany() || isOneToMany() || isOneToOne() || isVariableOneToOne();
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a serialized mapping.
     */
    public boolean isSerialized(MetadataClass referenceClass, boolean isForMapKey) {
        if (hasConvert(isForMapKey)) {
            getLogger().logConfigMessage(MetadataLogger.IGNORE_SERIALIZED, getJavaClass(), getAnnotatedElement());
            return false;
        } else {
            return isValidSerializedType(referenceClass);
        }
    }
    
    /**
     * INTERNAL:
     * Return true if this represents a temporal type mapping.
     */
    protected boolean isTemporal(MetadataClass referenceClass, boolean isForMapKey) {
        if (hasConvert(isForMapKey)) {
            // If we have a Temporal specification with a Convert specification, 
            // the Convert takes precedence and we will ignore the Temporal and 
            // log a message.
            if (hasTemporal(isForMapKey)) {
                getLogger().logWarningMessage(MetadataLogger.IGNORE_TEMPORAL, getJavaClass(), getAnnotatedElement());
            }
            
            return false;
        } else {
            return hasTemporal(isForMapKey) || TemporalMetadata.isValidTemporalType(referenceClass);
        }
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a transient mapping.
     */
    public boolean isTransient() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is valid for SerializedObjectMapping.
     */
    protected boolean isValidSerializedType(MetadataClass cls) {
        if (cls.isPrimitive()) {
            return false;
        }
        
        if (isPrimitiveWrapperClass(cls)) {    
            return false;
        }   
        
        if (LobMetadata.isValidLobType(cls)) {
            return false;
        }
        
        if (TemporalMetadata.isValidTemporalType(cls)) {
            return false;
        }
     
        return true;   
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a variable one to one mapping.
     */
    public boolean isVariableOneToOne() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Process an association override for either an embedded object mapping, 
     * or a map mapping (element-collection, 1-M and M-M) containing an
     * embeddable object as the value or key.
     * This method should be implemented in those accessors that support 
     * association overrides. An exception is thrown otherwise the association
     * is called against an unsupported accessor/relationship. 
     */
    protected void processAssociationOverride(AssociationOverrideMetadata associationOverride, EmbeddableMapping embeddableMapping, MetadataDescriptor owningDescriptor) {
        throw ValidationException.invalidEmbeddableAttributeForAssociationOverride(getJavaClass(), getAttributeName(), associationOverride.getName(), associationOverride.getLocation()); 
    }
    
    /**
     * INTERNAL:
     * Process the association overrides for the given embeddable mapping which
     * is either an embedded or element collection mapping. Association 
     * overrides are used to specify different keys to a shared mapping.
     */
    protected void processAssociationOverrides(List<AssociationOverrideMetadata> associationOverrides, EmbeddableMapping embeddableMapping, MetadataDescriptor embeddableDescriptor) {
        // Get the processible map of association overrides. This will take dot 
        // notation overrides into consideration (from a sub-entity to a mapped 
        // superclass accessor) and merge the lists. Once the map is returned, 
        // use the map keys as the attribute name and not the name from the 
        // individual association override since they could still contain dot 
        // notation names meaning you will not find their respective mapping 
        // accessor on the embeddable descriptor.
        Map<String, AssociationOverrideMetadata> mergedAssociationOverrides = getAssociationOverrides(associationOverrides);
        
        for (String attributeName : mergedAssociationOverrides.keySet()) {
            AssociationOverrideMetadata associationOverride = mergedAssociationOverrides.get(attributeName);
            // The getAccessorFor call will take care of any sub dot notation 
            // attribute names when looking for the mapping. It will traverse 
            // the embeddable chain. 
            MappingAccessor mappingAccessor = embeddableDescriptor.getMappingAccessor(attributeName);
            
            if (mappingAccessor == null) {
                throw ValidationException.embeddableAssociationOverrideNotFound(embeddableDescriptor.getJavaClass(), attributeName, getJavaClass(), getAttributeName());
            } else {
                mappingAccessor.processAssociationOverride(associationOverride, embeddableMapping, getOwningDescriptor());
            }  
        }
    }
    
    /**
     * INTERNAL:
     * Process the attribute overrides for the given embedded mapping. Attribute 
     * overrides are used to apply the correct field name translations of direct 
     * fields. Note an embedded object mapping may be supported as the map key
     * to an element-collection, 1-M and M-M mapping.
     */
    protected void processAttributeOverrides(List<AttributeOverrideMetadata> attributeOverrides, AggregateObjectMapping aggregateObjectMapping, MetadataDescriptor embeddableDescriptor) {
        // Get the processible map of attribute overrides. This will take dot 
        // notation overrides into consideration (from a sub-entity to a mapped 
        // superclass accessor) and merge the lists. Once the map is returned, 
        // use the map keys as the attribute name and not the name from the 
        // individual attribute override since they could still contain dot 
        // notation names meaning you will not find their respective mapping 
        // accessor on the embeddable descriptor.
        Map<String, AttributeOverrideMetadata> mergedAttributeOverrides = getAttributeOverrides(attributeOverrides);
        
        for (String attributeName : mergedAttributeOverrides.keySet()) {
            AttributeOverrideMetadata attributeOverride = mergedAttributeOverrides.get(attributeName);
            // The getMappingForAttributeName call will take care of any sub dot 
            // notation attribute names when looking for the mapping. It will
            // traverse the embeddable chain. 
            MappingAccessor mappingAccessor = embeddableDescriptor.getMappingAccessor(attributeName);
                
            if (mappingAccessor == null) {
                throw ValidationException.embeddableAttributeOverrideNotFound(embeddableDescriptor.getJavaClass(), attributeName, getJavaClass(), getAttributeName());
            } else if (! mappingAccessor.isBasic()) {
                throw ValidationException.invalidEmbeddableAttributeForAttributeOverride(embeddableDescriptor.getJavaClass(), attributeName, getJavaClass(), getAttributeName());
            } else {
                addFieldNameTranslation(aggregateObjectMapping, attributeName, attributeOverride.getColumn().getDatabaseField(), mappingAccessor);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Set the batch fetch type on the collection mapping.
     */
    protected void processBatchFetch(String batchFetch, ForeignReferenceMapping mapping) {
        if (batchFetch != null) {
            mapping.setBatchFetchType(BatchFetchType.valueOf(batchFetch));
        }   
    }
    
    /**
     * INTERNAL:
     * Process the map metadata if this is a valid map accessor. Will return 
     * the map key method name that should be use, null otherwise.
     * @see CollectionAccessor
     * @see ElementCollectionAccessor
     */
    protected void processContainerPolicyAndIndirection(CollectionMapping mapping) {
        if (isMappedKeyMapAccessor()) {
            // If we are a map key map accessor then the following is true:
            // 1 - we implement the mapped key map accessor interface
            // 2 - we are a map accessor
            // 3 - there is no map key metadata specified
            processMapKeyClass(mapping, (MappedKeyMapAccessor) this);
        } else if (isMapAccessor()) {
            // If we are not a mapped key map accessor, but a map accessor,
            // we need a map key metadata object to process. Default one if
            // one is not provided.
            MapKeyMetadata mapKey = getMapKey();
            if (mapKey == null) {
                setIndirectionPolicy(mapping, new MapKeyMetadata().process(mapping, this), usesIndirection());
            } else {
                setIndirectionPolicy(mapping, mapKey.process(mapping, this), usesIndirection());
            }
        } else {
            // Set the indirection policy on the mapping.
            setIndirectionPolicy(mapping, null, usesIndirection());
        }
    } 
    
    /**
     * INTERNAL:
     * Process a Convert annotation or convert element to apply to specified 
     * EclipseLink converter (Converter, TypeConverter, ObjectTypeConverter) 
     * to the given mapping.
     */
    protected void processConvert(DatabaseMapping mapping, String converterName, MetadataClass referenceClass, boolean isForMapKey) {
        // There is no work to do if the converter's name is "none".
        if (! converterName.equals(CONVERT_NONE)) {
            if (converterName.equals(CONVERT_SERIALIZED)) {
                processSerialized(mapping, referenceClass, isForMapKey);
            } else if (converterName.equals(CONVERT_CLASS_INSTANCE)){
                new ClassInstanceMetadata().process(mapping, this, referenceClass, isForMapKey);
            } else {
                AbstractConverterMetadata converter = getProject().getConverter(converterName);
                
                if (converter == null) {
                    throw ValidationException.converterNotFound(getJavaClass(), converterName, getAnnotatedElement());
                } else {
                    // Process the converter for this mapping.
                    converter.process(mapping, this, referenceClass, isForMapKey);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected DirectToFieldMapping processDirectMapKeyClass(MappedKeyMapAccessor mappedKeyMapAccessor) {
        DirectToFieldMapping keyMapping = new DirectToFieldMapping();

        // Get the map key field, defaulting and looking for attribute 
        // overrides. Set the field before applying a converter.
        DatabaseField mapKeyField = getDatabaseField(getReferenceDatabaseTable(), MetadataLogger.MAP_KEY_COLUMN);
        keyMapping.setField(mapKeyField);
        keyMapping.setIsReadOnly(mapKeyField.isReadOnly());
        keyMapping.setAttributeClassificationName(mappedKeyMapAccessor.getMapKeyClass().getName());
        keyMapping.setDescriptor(getDescriptor().getClassDescriptor());
        
        // Process a convert key or jpa converter for the map key if specified.
        processMappingKeyConverter(keyMapping, mappedKeyMapAccessor.getMapKeyConvert(), mappedKeyMapAccessor.getMapKeyClass());
        
        return keyMapping;
    }
    
    /**
     * INTERNAL:
     */
    protected AggregateObjectMapping processEmbeddableMapKeyClass(MappedKeyMapAccessor mappedKeyMapAccessor) {
        AggregateObjectMapping keyMapping = new AggregateObjectMapping();
        MetadataClass mapKeyClass = mappedKeyMapAccessor.getMapKeyClass();
        keyMapping.setReferenceClassName(mapKeyClass.getName());
        
        // The embeddable accessor must be processed by now. If it is not then
        // we are in trouble since by the time we get here, we are too late in
        // the cycle to process embeddable classes and their accessors. See
        // MetadataProject processStage3(), processEmbeddableMappingAccessors. 
        // At this stage all class accessors (embeddable, entity and mapped 
        // superclass) have to have been processed to gather all their 
        // relational and embedded mappings. 
        EmbeddableAccessor mapKeyAccessor = getProject().getEmbeddableAccessor(mapKeyClass);
        
        // Ensure the reference descriptor is marked as an embeddable collection.
        mapKeyAccessor.getDescriptor().setIsEmbeddableCollection();
        
        // Process the attribute overrides for this may key embeddable.
        processAttributeOverrides(mappedKeyMapAccessor.getMapKeyAttributeOverrides(), keyMapping, mapKeyAccessor.getDescriptor());
        
        // Process the association overrides for this may key embeddable.
        processAssociationOverrides(mappedKeyMapAccessor.getMapKeyAssociationOverrides(), keyMapping, mapKeyAccessor.getDescriptor());
        
        keyMapping.setDescriptor(getDescriptor().getClassDescriptor());
        
        return keyMapping;
    }
    
    /**
     * INTERNAL:
     * Process the map key to be an entity class.
     */
    protected OneToOneMapping processEntityMapKeyClass(MappedKeyMapAccessor mappedKeyMapAccessor) {
        String mapKeyClassName = mappedKeyMapAccessor.getMapKeyClass().getName();
        
        // Create the one to one map key mapping.
        OneToOneMapping keyMapping = new OneToOneMapping();
        keyMapping.setReferenceClassName(mapKeyClassName);
        keyMapping.dontUseIndirection();
        keyMapping.setDescriptor(getDescriptor().getClassDescriptor());
        
        // Process the map key join columns.
        EntityAccessor mapKeyAccessor = getProject().getEntityAccessor(mapKeyClassName);
        MetadataDescriptor mapKeyClassDescriptor = mapKeyAccessor.getDescriptor();
        
        // If the pk field (referencedColumnName) is not specified, it 
        // defaults to the primary key of the referenced table.
        String defaultPKFieldName = mapKeyClassDescriptor.getPrimaryKeyFieldName();
        
        // If the fk field (name) is not specified, it defaults to the 
        // concatenation of the following: the name of the referencing 
        // relationship property or field of the referencing entity or 
        // embeddable; "_"; "KEY"
        String defaultFKFieldName = getAttributeName() + DEFAULT_MAP_KEY_COLUMN_SUFFIX;
        
        processOneToOneForeignKeyRelationship(keyMapping, getJoinColumns(mappedKeyMapAccessor.getMapKeyJoinColumns(), mapKeyClassDescriptor), defaultPKFieldName, mapKeyClassDescriptor.getPrimaryTable(), defaultFKFieldName, getDefaultTableForEntityMapKey());

        return keyMapping;
    }
    
    /**
     * INTERNAL:
     * Process an Enumerated setting. The method may still be called if no 
     * Enumerated metadata has been specified but the accessor's reference 
     * class is a valid enumerated type.
     */
    protected void processEnumerated(EnumeratedMetadata enumerated, DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {
        if (enumerated == null) {
            enumerated = new EnumeratedMetadata(getAccessibleObject());
        }
        
        enumerated.process(mapping, this, referenceClass, isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Return the mapping join fetch type.
     */
    protected void processJoinFetch(String joinFetch, ForeignReferenceMapping mapping) {
        if (joinFetch == null) {
            mapping.setJoinFetch(ForeignReferenceMapping.NONE);
        } else if (joinFetch.equals(JoinFetchType.INNER.name())) {
            mapping.setJoinFetch(ForeignReferenceMapping.INNER_JOIN);
        } else {
            mapping.setJoinFetch(ForeignReferenceMapping.OUTER_JOIN);
        }
    }
    
    /**
     * INTERNAL:
     * Process an Enumerated, Lob, Temporal, MapKeyEnumerated, MapKeyTempora 
     * specification. Will default a serialized converter if necessary. JPA 
     * converters can be applied to basics and to map keys/values of a map 
     * accessor.
     */
    protected void processJPAConverters(DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {
        // Check for an enum first since it will fall into a serializable 
        // mapping otherwise (Enums are serialized)
        if (isEnumerated(referenceClass, isForMapKey)) {
            processEnumerated(getEnumerated(isForMapKey), mapping, referenceClass, isForMapKey);
        } else if (isLob(referenceClass, isForMapKey)) {
            processLob(getLob(isForMapKey), mapping, referenceClass, isForMapKey);
        } else if (isTemporal(referenceClass, isForMapKey)) {
            processTemporal(getTemporal(isForMapKey), mapping, referenceClass, isForMapKey);
        } else if (isSerialized(referenceClass, isForMapKey)) {
            processSerialized(mapping, referenceClass, isForMapKey);
        }
    }
    
    /**
     * INTERNAL:
     * Process a lob specification. The lob must be specified to process and 
     * create a lob type mapping.
     */
    protected void processLob(LobMetadata lob, DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {
        lob.process(mapping, this, referenceClass, isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Process a map key class for the given map key map accessor.
     */
    protected void processMapKeyClass(CollectionMapping mapping, MappedKeyMapAccessor mappedKeyMapAccessor) {
        MapKeyMapping keyMapping;
        MetadataClass mapKeyClass = mappedKeyMapAccessor.getMapKeyClass();
        
        if (getProject().hasEntity(mapKeyClass)) {
            keyMapping = processEntityMapKeyClass(mappedKeyMapAccessor);
        } else if (getProject().hasEmbeddable(mapKeyClass)) {
            keyMapping = processEmbeddableMapKeyClass(mappedKeyMapAccessor);
        } else {
            keyMapping = processDirectMapKeyClass(mappedKeyMapAccessor);
        }
          
        Class containerClass;
        if (usesIndirection()) {
            containerClass = ClassConstants.IndirectMap_Class;
            mapping.setIndirectionPolicy(new TransparentIndirectionPolicy());
        } else {
            containerClass = java.util.Hashtable.class;
            mapping.dontUseIndirection();
        }

        MappedKeyMapContainerPolicy policy = new MappedKeyMapContainerPolicy(containerClass);
        policy.setKeyMapping(keyMapping);
        policy.setValueMapping((MapComponentMapping) mapping);
        mapping.setContainerPolicy(policy);
    }
    
    /**
     * INTERNAL:
     * Process a convert value which specifies the name of an EclipseLink
     * converter to process with this accessor's mapping.     
     */
    protected void processMappingConverter(DatabaseMapping mapping, String convertValue, MetadataClass referenceClass, boolean isForMapKey) {
        if (convertValue != null && ! convertValue.equals(CONVERT_NONE)) {
            processConvert(mapping, convertValue, referenceClass, isForMapKey);
        } 

        // Regardless if we found a convert or not, look for JPA converters. 
        // This ensures two things; 
        // 1 - if no Convert is specified, then any JPA converter that is 
        // specified will be applied (see BasicMapAccessor's override of the
        // method hasConvert()). 
        // 2 - if a convert and a JPA converter are specified, then a log 
        // warning will be issued stating that we are ignoring the JPA 
        // converter.
        processJPAConverters(mapping, referenceClass, isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Process a convert value which specifies the name of an EclipseLink
     * converter to process with this accessor's mapping key.
     */
    protected void processMappingKeyConverter(DatabaseMapping mapping, String convertValue, MetadataClass referenceClass) {
        processMappingConverter(mapping, convertValue, referenceClass, true); 
    }
    
    /**
     * INTERNAL:
     * Process a convert value which specifies the name of an EclipseLink
     * converter to process with this accessor's mapping.
     */
    protected void processMappingValueConverter(DatabaseMapping mapping, String convertValue, MetadataClass referenceClass) {
        processMappingConverter(mapping, convertValue, referenceClass, false); 
    }
    
    /**
     * INTERNAL:
     * Process the join columns for the owning side of a one to one mapping.
     * The default pk and fk field names are used only with single primary key 
     * entities. The processor should never get as far as to use them with 
     * entities that have a composite primary key (validation exception will be 
     * thrown).
     */
    protected void processOneToOneForeignKeyRelationship(OneToOneMapping mapping, List<JoinColumnMetadata> joinColumns, String defaultPKFieldName, DatabaseTable defaultPKTable, String defaultFKFieldName, DatabaseTable defaultFKTable) {         
        // we need to know if all the mappings are read-only so we can determine if we use target foreign keys
        // to represent read-only parts of the join, or if we simply set the whole mapping as read-only
        boolean allReadOnly = true;
        for (JoinColumnMetadata joinColumn : joinColumns) {
            allReadOnly = allReadOnly && joinColumn.getForeignKeyField().isReadOnly();
        }
        // Add the source foreign key fields to the mapping.
        for (JoinColumnMetadata joinColumn : joinColumns) {
            DatabaseField pkField = joinColumn.getPrimaryKeyField();
            setFieldName(pkField, defaultPKFieldName, MetadataLogger.PK_COLUMN);
            pkField.setTable(defaultPKTable);
            
            DatabaseField fkField = joinColumn.getForeignKeyField();
            setFieldName(fkField, defaultFKFieldName, MetadataLogger.FK_COLUMN);
            // Set the table name if one is not already set.
            if (!fkField.hasTableName()) {
                fkField.setTable(defaultFKTable);
            }
            if (allReadOnly || !fkField.isReadOnly()){
                // Add a source foreign key to the mapping.
                mapping.addForeignKeyField(fkField, pkField);
            } else {
                // this is a read-only join column that is part of a set of 
                // join columns that are not all read only - hence this
                // is not a read-only mapping, but instead uses a target 
                // foreign key field to enable the read-only functionality
                mapping.addTargetForeignKeyField(pkField, fkField);
            }
        }
        
        // If any of the join columns is marked read-only then set the 
        // mapping to be read only.
        if (allReadOnly) {
            mapping.setIsReadOnly(true);
        }
    }
    
    /**
     * INTERNAL:
     * Adds properties to the mapping.
     */
    protected void processProperties(DatabaseMapping mapping) {
        // If we were loaded from XML use the properties loaded from there
        // only. Otherwise look for annotations.
        if (loadedFromXML()) {
            for (PropertyMetadata property : getProperties()) {
                processProperty(mapping, property);
            }
        } else {
            // Look for annotations.
            MetadataAnnotation properties = getAnnotation(Properties.class);
            if (properties != null) {
                for (Object property : (Object[]) properties.getAttribute("value")) {
                    processProperty(mapping, new PropertyMetadata((MetadataAnnotation)property, getAccessibleObject()));
                }
            }
            
            MetadataAnnotation property = getAnnotation(Property.class);
            if (property != null) {
                processProperty(mapping, new PropertyMetadata(property, getAccessibleObject()));
            }    
        }
    }
    
    /**
     * INTERNAL:
     * Adds properties to the mapping. They can only come from one place,
     * therefore is we add the same one twice we know to throw an exception.
     */
    protected void processProperty(DatabaseMapping mapping, PropertyMetadata property) {
        if (property.shouldOverride(m_properties.get(property.getName()))) {
            m_properties.put(property.getName(), property);
            mapping.getProperties().put(property.getName(), property.getConvertedValue());
        }
    }
    
    /**
     * INTERNAL:
     * Subclasses should call this method if they want the warning message or
     * override the method if they want/support different behavior.
     * @see BasicAccessor
     */
    protected void processReturnInsert() {
        if (hasReturnInsert()) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_RETURN_INSERT_ANNOTATION, getAnnotatedElement());
        }
    }
    
    /**
     * INTERNAL:
     * Subclasses should call this method if they want the warning message.
     */
    protected void processReturnInsertAndUpdate() {
        processReturnInsert();
        processReturnUpdate();
    }
    
    /**
     * INTERNAL:
     * Subclasses should call this method if they want the warning message or
     * override the method if they want/support different behavior.
     * @see BasicAccessor
     */
    protected void processReturnUpdate() {
        if (hasReturnUpdate()) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_RETURN_UPDATE_ANNOTATION, getAnnotatedElement());
        }
    }
    
    /**
     * INTERNAL:
     * Process a potential serializable attribute. If the class implements 
     * the Serializable interface then set a SerializedObjectConverter on 
     * the mapping.
     */
    protected void processSerialized(DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {        
        new SerializedMetadata(getAccessibleObject()).process(mapping, this, referenceClass, isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Process a potential serializable attribute. If the class implements 
     * the Serializable interface then set a SerializedObjectConverter on 
     * the mapping.
     */
    protected void processSerialized(DatabaseMapping mapping, MetadataClass referenceClass, MetadataClass classification, boolean isForMapKey) {
        new SerializedMetadata(getAccessibleObject()).process(mapping, this, referenceClass, classification, isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Process a temporal type accessor.
     */
    protected void processTemporal(TemporalMetadata temporal, DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {
        if (temporal == null) {
            // We have a temporal type on either a basic mapping or the key to
            // a collection mapping. Since the temporal type was not specified, 
            // per the JPA spec we must throw an exception.
            throw ValidationException.noTemporalTypeSpecified(getAttributeName(), getJavaClass());
        }
        
        temporal.process(mapping, this, referenceClass, isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Set the getter and setter access methods for this accessor.
     */
    protected void setAccessorMethods(DatabaseMapping mapping) {
        if (usesPropertyAccess() || usesVirtualAccess()) {
            if (usesVirtualAccess()) {
                mapping.setAttributeAccessor(new VirtualAttributeAccessor());
            }
            
            mapping.setGetMethodName(getGetMethodName());
            mapping.setSetMethodName(getSetMethodName());
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAttributeType(String attributeType) {
        m_attributeType = attributeType;
    }
    
    /**
     * INTERNAL:
     * Sets the class accessor for this mapping accessor.
     */
    public void setClassAccessor(ClassAccessor classAccessor) {
        m_classAccessor = classAccessor;
    }
    
    /** 
     * INTERNAL:
     * Set the correct indirection policy on a collection mapping. Method
     * assume that the reference class has been set on the mapping before
     * calling this method.
     */
    protected void setIndirectionPolicy(CollectionMapping mapping, String mapKey, boolean usesIndirection) {
        MetadataClass rawClass = getRawClass();
        
        if (usesIndirection) {            
            if (rawClass.equals(Map.class)) {
                if (mapping.isDirectMapMapping()) {
                    ((DirectMapMapping) mapping).useTransparentMap();
                } else {
                    mapping.useTransparentMap(mapKey);
                }
            } else if (rawClass.equals(List.class)) {
                mapping.useTransparentList();
            } else if (rawClass.equals(Collection.class)) {
                mapping.useTransparentCollection();
            } else if (rawClass.equals(Set.class)) {
                mapping.useTransparentSet();
            } else {
                //bug221577: This should be supported when a transparent indirection class can be set through eclipseLink_orm.xml, or basic indirection is used
                getLogger().logWarningMessage(MetadataLogger.WARNING_INVALID_COLLECTION_USED_ON_LAZY_RELATION, getJavaClass(), getAnnotatedElement(), rawClass);
            }
        } else {
            mapping.dontUseIndirection();
            
            if (rawClass.equals(Map.class)) {
                if (mapping.isDirectMapMapping()) {
                    ((DirectMapMapping) mapping).useMapClass(java.util.Hashtable.class);
                } else {
                    mapping.useMapClass(java.util.Hashtable.class, mapKey);
                }
            } else if (rawClass.equals(Set.class)) {
                // This will cause it to use a CollectionContainerPolicy type
                mapping.useCollectionClass(java.util.HashSet.class);
            } else if (rawClass.equals(List.class)) {
                // This will cause a ListContainerPolicy type to be used or 
                // OrderedListContainerPolicy if ordering is specified.
                mapping.useCollectionClass(java.util.Vector.class);
            } else if (rawClass.equals(Collection.class)) {
                // Force CollectionContainerPolicy type to be used with a 
                // collection implementation.
                mapping.setContainerPolicy(new CollectionContainerPolicy(java.util.Vector.class));
            } else {
                // Use the supplied collection class type if its not an interface
                if (mapKey == null || mapKey.equals("")){
                    mapping.useCollectionClassName(rawClass.getName());
                } else {
                    mapping.useMapClassName(rawClass.getName(), mapKey);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * This will do three things:
     * 1 - process any common level metadata for all mappings.
     * 2 - add the mapping to the internal descriptor.
     * 3 - store the actual database mapping associated with this accessor.
     * 
     * Calling this method is a must for all mapping accessors since it will 
     * help to:
     * 1 - determine if the accessor has been processed, and
     * 2 - sub processing will may need access to the mapping to set its 
     *     metadata.
     */
    protected void setMapping(DatabaseMapping mapping) {
        // Before adding the mapping to the descriptor, process the properties
        // for this mapping (if any)
        processProperties(mapping);
        
        // Add the mapping to the class descriptor.
        getDescriptor().getClassDescriptor().addMapping(mapping);
        
        // Keep a reference back to this mapping for quick look up.
        m_mapping = mapping;  
    }
    
    /**
     * INTERNAL:
     * An override mapping is created when an association override is specified
     * to a relationship accessor on an embeddable class. For any non-owning 
     * relationship accessor referring to this accessor will need its override
     * mapping and not the original mapping from the embeddable so that it can
     * populate the right metadata.
     */
    protected void setOverrideMapping(DatabaseMapping mapping) {
        m_overrideMapping = mapping;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return getAnnotatedElementName();
    }
    
    /**
     * INTERNAL:
     * Update the primary key field on the owning descriptor the override field
     * given.
     */
    protected void updatePrimaryKeyField(MappingAccessor idAccessor, DatabaseField overrideField) {
        getOwningDescriptor().removePrimaryKeyField(idAccessor.getMapping().getField());
        getOwningDescriptor().addPrimaryKeyField(overrideField, idAccessor);
    }
    
    /**
     * INTERNAL:
     * @see RelationshipAccessor
     * @see DirectAccessor
     */
    protected boolean usesIndirection() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Returns true if this mapping or class uses property access. In an 
     * inheritance hierarchy, the subclasses inherit their access type from 
     * the parent (unless there is an explicit access setting).
     */
    public boolean usesPropertyAccess() {
        if (hasAccess()) {
            return getAccess().equals(MetadataConstants.PROPERTY);
        } else {
            return hasAccessMethods() ? true : m_classAccessor.usesPropertyAccess();
        }
    }
    
    /**
     * INTERNAL:
     * Returns true if this mapping or class uses virtual access. In an 
     * inheritance hierarchy, the subclasses inherit their access type from 
     * the parent (unless there is an explicit access setting).
     */
    public boolean usesVirtualAccess() {
        if (hasAccess()) {
            return getAccess().equals(MetadataConstants.VIRTUAL);
        } else {
            return m_classAccessor.usesVirtualAccess();
        }
    }
}
