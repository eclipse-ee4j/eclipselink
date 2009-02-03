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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     08/27/2008-1.1 Guy Pelletier 
 *       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     01/28/2009-1.1 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.eclipse.persistence.annotations.Properties;
import org.eclipse.persistence.annotations.Property;
import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.annotations.ReturnUpdate;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.AccessMethodsMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;

import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.EmbeddableMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * INTERNAL:
 * An abstract mapping accessor. Holds common metadata for all mappings.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public abstract class MappingAccessor extends MetadataAccessor {
    private AccessMethodsMetadata m_accessMethods;
    private ClassAccessor m_classAccessor;
    private Map<String, PropertyMetadata> m_properties = new HashMap<String, PropertyMetadata>();
    private DatabaseMapping m_mapping;
    
    /**
     * INTERNAL:
     */
    protected MappingAccessor(Annotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
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
     * Process an attribute override for either an embedded object mapping, or
     * an element collection mapping containing embeddable objects.
     */
    protected void addFieldNameTranslation(EmbeddableMapping embeddableMapping, DatabaseField overrideField, DatabaseTable defaultTable, DatabaseMapping aggregateMapping) {
        // Make sure we have a table set on the attribute override field,
        // otherwise use the default table provided.
        if (overrideField.getTableName().equals("")) {
            overrideField.setTable(defaultTable);
        }

        DatabaseField aggregateField = aggregateMapping.getField();
        
        // If the override field is to an id field, we need to update the 
        // list of primary keys on the owning descriptor. Embeddables can be 
        // shared and different owners may want to override the attribute 
        // with a different column.
        if (getOwningDescriptor().isPrimaryKeyField(aggregateField)) {
            getOwningDescriptor().removePrimaryKeyField(aggregateField);
            getOwningDescriptor().addPrimaryKeyField(overrideField);
        }

        // Set the field name translation on the mapping.
        embeddableMapping.addFieldNameTranslation(overrideField.getQualifiedName(), aggregateField.getName());
        
        // TODO: nestedFieldNameTranslations need to be dealt with for element collection.
    }
    
    /**
     * INTERNAL:
     */
    public AccessMethodsMetadata getAccessMethods(){
        return m_accessMethods;
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
     */
    protected Enum getDefaultFetchType() {
        return FetchType.valueOf("EAGER"); 
    }
    
    /**
     * INTERNAL:
     * Returns the get method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    protected String getGetMethodName() {
        if (m_accessMethods != null && m_accessMethods.getGetMethodName() != null) {
            return m_accessMethods.getGetMethodName();
        }
        
        return getAccessibleObjectName();
    }
    
    /**
     * INTERNAL:
     * Return the mapping that this accessor is associated to.
     */
    protected DatabaseMapping getMapping(){
        return m_mapping;
    }
    
    /**
     * INTERNAL:
     * Return the mapping join fetch type.
     */
    protected int getMappingJoinFetchType(Enum joinFetchType) {
        if (joinFetchType == null) {
            return ForeignReferenceMapping.NONE;
        } else if (joinFetchType.name().equals(JoinFetchType.INNER.name())) {
            return ForeignReferenceMapping.INNER_JOIN;
        }

        return ForeignReferenceMapping.OUTER_JOIN;
    }
    
    /**
     * INTERNAL:
     * Return the raw class for this accessor. 
     * Eg. For an accessor with a type of java.util.Collection<Employee>, this 
     * method will return java.util.Collection
     */
    public Class getRawClass() {
        return getAccessibleObject().getRawClass(getDescriptor());   
    }
    
    /**
     * INTERNAL: 
     * Return the reference class for this accessor. By default the reference
     * class is the raw class. Some accessors may need to override this
     * method to drill down further. That is, try to extract a reference class
     * from generics.
     */
    public Class getReferenceClass() {
        return getAccessibleObject().getRawClass(getDescriptor());
    }
    
    /**
     * INTERNAL:
     * Attempts to return a reference class from a generic specification. Note,
     * this method may return null.
     */
    public Class getReferenceClassFromGeneric() {
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
     * Return the reference metadata descriptor for this accessor.
     */
    public MetadataDescriptor getReferenceDescriptor() {
        return getReferenceDescriptor(getReferenceClassName());
    }
    
    /**
     * INTERNAL:
     * Return the reference metadata descriptor for this accessor.
     */
    public MetadataDescriptor getReferenceDescriptor(String referenceClassName) {
        ClassAccessor accessor = getProject().getAccessor(referenceClassName);
        
        if (accessor == null) {
            throw ValidationException.classNotListedInPersistenceUnit(referenceClassName);
        }
        
        return accessor.getDescriptor();
    }
    
    /**
     * INTERNAL:
     * Returns the set method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    protected String getSetMethodName() {
        if (m_accessMethods != null && m_accessMethods.getSetMethodName() != null) {
            return m_accessMethods.getSetMethodName();
        }

        return ((MetadataMethod) getAccessibleObject()).getSetMethodName();
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
     * Method to check if an annotated element has a convert specified.
     */
    protected boolean hasConvert() {
        return isAnnotationPresent(Convert.class);
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
     * Init an xml mapping accessor with its necessary components. 
     */
    public void initXMLMappingAccessor(ClassAccessor classAccessor) {
        m_classAccessor = classAccessor;
        
        initXMLAccessor(classAccessor.getDescriptor(), classAccessor.getProject());   
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
        
        // Initialize single objects.
        initXMLObject(m_accessMethods, accessibleObject);
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
     * Checks if this accessor is part of the Id
     */
    public boolean isId(){
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
     * Return true if this accessor method represents a relationship.
     */
    public boolean isRelationship() {
        return isManyToOne() || isManyToMany() || isOneToMany() || isOneToOne() || isVariableOneToOne();
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
     * Return true if this accessor represents a variable one to one mapping.
     */
    public boolean isVariableOneToOne() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Process an association override for either an embedded object mapping, or
     * an element collection mapping containing embeddable objects.
     */
    protected void processAssociationOverride(AssociationOverrideMetadata associationOverride, EmbeddableMapping embeddableMapping, DatabaseTable defaultTable, MetadataDescriptor aggregateDescriptor) {
        // AssociationOverride.name(), the name of the attribute we want to
        // override.
        String attributeName = associationOverride.getName();
        DatabaseMapping mapping = aggregateDescriptor.getMappingForAttributeName(attributeName);
        
        if (mapping != null && mapping.isOneToOneMapping()) {
            MappingAccessor accessor = aggregateDescriptor.getAccessorFor(mapping.getAttributeName());
            MetadataDescriptor referenceDescriptor = accessor.getReferenceDescriptor();
            
            // The process join columns will validate the join column specifics.
            for (JoinColumnMetadata joinColumn : processJoinColumns(associationOverride.getJoinColumns(), referenceDescriptor)) {
                DatabaseField pkField = joinColumn.getPrimaryKeyField();
                pkField.setTable(referenceDescriptor.getPrimaryKeyTable());
                DatabaseField fkField = ((OneToOneMapping) mapping).getTargetToSourceKeyFields().get(pkField);
                
                if (fkField == null) {
                    // TODO: invalid pk field specified
                    // TODO: composite primary key case (and correct table)
                } else {
                    // Make sure we have a table set on the association override 
                    // field, otherwise use the default table provided.
                    DatabaseField translationFKField = joinColumn.getForeignKeyField();
                    if (! translationFKField.hasTableName()) {
                        translationFKField.setTable(defaultTable);
                    }
                    
                    embeddableMapping.addFieldNameTranslation(translationFKField.getQualifiedName(),fkField.getName());
                }
            }   
        } else {
            // TODO: fail loudly now since it is a supported feature from the spec.
        }
    }
    
    /**
     * INTERNAL:
     * Process the join column metadata. Will look for association overrides.
     */    
    protected List<JoinColumnMetadata> processJoinColumns(List<JoinColumnMetadata> joinColumns) { 
        if (getDescriptor().hasAssociationOverrideFor(getAttributeName())) {
            return processJoinColumns(getDescriptor().getAssociationOverrideFor(getAttributeName()).getJoinColumns(), getReferenceDescriptor());
        } else {
            return processJoinColumns(joinColumns, getReferenceDescriptor());
        }
    }
    
    /**
     * INTERNAL:
     * Process the join column metadata. Note: if an accessor requires a check 
     * for association overrides, it should call processJoinColumns(),
     * otherwise, calling this method directly assumes your accessor doesn't 
     * support association overrides.
     */    
    protected List<JoinColumnMetadata> processJoinColumns(List<JoinColumnMetadata> joinColumns, MetadataDescriptor descriptor) {
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
                joinColumn.setReferencedColumnName(descriptor.getPrimaryKeyJoinColumnAssociation(joinColumn.getReferencedColumnName()));
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
            Annotation properties = getAnnotation(Properties.class);
            if (properties != null) {
                for (Annotation property : (Annotation[]) MetadataHelper.invokeMethod("value", properties)) {
                    processProperty(mapping, new PropertyMetadata(property, getAccessibleObject()));
                }
            }
            
            Annotation property = getAnnotation(Property.class);
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
     */
    protected void processReturnUpdate() {
        if (hasReturnUpdate()) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_RETURN_UPDATE_ANNOTATION, getAnnotatedElement());
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setAccessMethods(AccessMethodsMetadata accessMethods){
        m_accessMethods = accessMethods;
    }
    
    /**
     * INTERNAL:
     * Set the getter and setter access methods for this accessor.
     */
    protected void setAccessorMethods(DatabaseMapping mapping) {
        if (usesPropertyAccess(getDescriptor())) {
            mapping.setGetMethodName(getGetMethodName());
            mapping.setSetMethodName(getSetMethodName());
        }
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
    public void setIndirectionPolicy(CollectionMapping mapping, String mapKey, boolean usesIndirection) {
        Class rawClass = getRawClass();
        
        if (usesIndirection) {            
            if (rawClass == Map.class) {
                mapping.useTransparentMap(mapKey);
            } else if (rawClass == List.class) {
                mapping.useTransparentList();
            } else if (rawClass == Collection.class) {
                mapping.useTransparentCollection();
                mapping.setContainerPolicy(new CollectionContainerPolicy(ClassConstants.IndirectList_Class));
            } else if (rawClass == Set.class) {
                mapping.useTransparentSet();
            } else {
                //bug221577: This should be supported when a transparent indirection class can be set through eclipseLink_orm.xml, or basic indirection is used
                getLogger().logWarningMessage(MetadataLogger.WARNING_INVALID_COLLECTION_USED_ON_LAZY_RELATION, getJavaClass(), getAnnotatedElement(), rawClass);
            }
        } else {
            mapping.dontUseIndirection();
            
            if (rawClass == Map.class) {
                mapping.useMapClass(java.util.Hashtable.class, mapKey);
            } else if (rawClass == Set.class) {
                //this will cause it to use a CollectionContainerPolicy type
                mapping.useCollectionClass(java.util.HashSet.class);
            } else if (rawClass == List.class) {
                //this will cause a ListContainerPolicy type to be used or OrderedListContainerPolicy if ordering is specified
                mapping.useCollectionClass(java.util.Vector.class);
            } else if (rawClass == Collection.class) {
                //bug236275 : Force CollectionContainerPolicy type to be used with a collection implementation
                mapping.setContainerPolicy(new CollectionContainerPolicy(java.util.Vector.class));
            } else {
                //bug221577: use the supplied collection class type if its not an interface
                if (mapKey == null || mapKey.equals("")){
                    mapping.useCollectionClass(rawClass);
                } else {
                    mapping.useMapClass(rawClass, mapKey);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Sets the DatabaseMapping associated to this mapping accessor.
     */
    protected void setMapping(DatabaseMapping mapping){
        m_mapping = mapping;
    }
    
    /**
     * INTERNAL:
     * Returns true if this mapping or class uses property access. In an 
     * inheritance hierarchy, the subclasses inherit their access type from 
     * the parent (unless there is an explicit access setting).
     */
    public boolean usesPropertyAccess(MetadataDescriptor descriptor) {
        if (hasAccess()) {
            return hasPropertyAccess();
        } else {
            return (m_accessMethods == null) ? m_classAccessor.usesPropertyAccess() : true;
        }
    }
}
