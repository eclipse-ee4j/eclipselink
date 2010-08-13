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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     05/1/2009-2.0 Guy Pelletier 
 *       - 249033: JPA 2.0 Orphan removal
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 *     09/29/2009-2.0 Guy Pelletier 
 *       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     11/23/2009-2.0 Guy Pelletier 
 *       - 295790: JPA 2.0 adding @MapsId to one entity causes initialization errors in other entities
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     07/05/2010-2.1.1 Guy Pelletier 
 *       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
 *     08/04/2010-2.1.1 Guy Pelletier
 *       - 315782: JPA2 derived identity metadata processing validation doesn't account for autoboxing
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnsMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.indirection.WeavedObjectBasicIndirectionPolicy;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.EmbeddableMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.RelationTableMechanism;

/**
 * INTERNAL:
 * A single object relationship accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class ObjectAccessor extends RelationshipAccessor {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    private Boolean m_id;
    private Boolean m_optional;
    private List<PrimaryKeyJoinColumnMetadata> m_primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
    private String m_mapsId;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    protected ObjectAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    protected ObjectAccessor(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
        
        if (annotation != null) {
            m_optional = (Boolean) annotation.getAttribute("optional");
        }
        
        // Set the primary key join columns if some are present.
        // Process all the primary key join columns first.
        MetadataAnnotation primaryKeyJoinColumns = getAnnotation(PrimaryKeyJoinColumns.class);
        if (primaryKeyJoinColumns != null) {
            for (Object primaryKeyJoinColumn : (Object[]) primaryKeyJoinColumns.getAttributeArray("value")) { 
                m_primaryKeyJoinColumns.add(new PrimaryKeyJoinColumnMetadata((MetadataAnnotation)primaryKeyJoinColumn, accessibleObject));
            }
        }
        
        // Process the single primary key join column second.
        MetadataAnnotation primaryKeyJoinColumn = getAnnotation(PrimaryKeyJoinColumn.class);
        if (primaryKeyJoinColumn != null) {
            m_primaryKeyJoinColumns.add(new PrimaryKeyJoinColumnMetadata(primaryKeyJoinColumn, accessibleObject));
        }
        
        // Set the mapped by id if one is present.
        if (isAnnotationPresent(MapsId.class)) {
            // Call getAttributeString in this case because we rely on the
            // mapsId not being null and it's value of "" means we need to
            // default. getAttribute returns null which kills hasMapsId() logic
            m_mapsId = (String) getAnnotation(MapsId.class).getAttributeString("value");
        }
        
        // Set the derived id if one is specified.
        m_id = isAnnotationPresent(Id.class);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof ObjectAccessor) {
            ObjectAccessor objectAccessor = (ObjectAccessor) objectToCompare;
            
            if (! valuesMatch(m_id, objectAccessor.getId())) {
                return false;
            }
            
            if (! valuesMatch(m_optional, objectAccessor.getOptional())) {
                return false;
            }

            if (! valuesMatch(m_primaryKeyJoinColumns, objectAccessor.getPrimaryKeyJoinColumns())) {
                return false;
            }
            
            return valuesMatch(m_mapsId, objectAccessor.getMapsId());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true is this accessor is a derived id accessor.
     */
    @Override
    public boolean derivesId() {
        return hasId() || hasMapsId();
    }
    
    /**
     * INTERNAL:
     * Return the default fetch type for an object mapping.
     */
    public String getDefaultFetchType() {
        return FetchType.EAGER.name();
    }
    
    /**
     * INTERNAL:
     */
    public Boolean getId(){
        return m_id;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getMapsId(){
        return m_mapsId;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getOptional() {
        return m_optional;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */    
    public List<PrimaryKeyJoinColumnMetadata> getPrimaryKeyJoinColumns() {
        return m_primaryKeyJoinColumns;
    }
    
    /**
     * INTERNAL:
     * If a target entity is specified in metadata, it will be set as the 
     * reference class, otherwise we will use the raw class.
     */
    @Override
    public MetadataClass getReferenceClass() {
        if (m_referenceClass == null) {
            m_referenceClass = getTargetEntity();
        
            if (m_referenceClass.isVoid()) {
                // Get the reference class from the accessible object and
                // log the defaulting contextual reference class.
                m_referenceClass = super.getReferenceClass();
                getLogger().logConfigMessage(getLoggingContext(), getAnnotatedElement(), m_referenceClass);
            } 
        }
        
        return m_referenceClass;
    }
    
    /**
     * INTERNAL:
     * Used to process primary keys and DerivedIds.
     */
    protected MetadataClass getSimplePKType(){
        MetadataDescriptor referenceDescriptor = getReferenceDescriptor();
        ClassAccessor referenceAccessor = referenceDescriptor.getClassAccessor();
        
        if (referenceAccessor.hasDerivedId()) {
            // Referenced object has a derived ID and must be a simple pk type.  
            // Recurse through to get the simple type.
            return ((ObjectAccessor) referenceDescriptor.getAccessorFor(referenceDescriptor.getIdAttributeName())).getSimplePKType();
        } else {
            // Validate on their basic mapping.
            return referenceDescriptor.getAccessorFor(referenceDescriptor.getIdAttributeName()).getRawClass();
        }
    }
    
    /**
     * INTERNAL:
     * Object accessors don't require a separate attribute-type specification
     * in XML, instead they can use the reference class to determine the
     * attribute-type.
     */
    @Override
    public boolean hasAttributeType() {
        return getReferenceClass() != null;
    }
    
    /**
     * INTERNAL:
     */
    protected boolean hasId() {
        return m_id != null && m_id;
    }
    
    /**
     * INTERNAL:
     */
    protected boolean hasMapsId() {
        return m_mapsId != null;
    }
    
    /**
     * INTERNAL:
     * Initialize a OneToOneMapping.
     */
    protected OneToOneMapping initOneToOneMapping() {
        OneToOneMapping mapping = new OneToOneMapping();
        mapping.setIsOneToOneRelationship(true);
        mapping.setIsReadOnly(false);
        mapping.setIsOptional(isOptional());
        mapping.setAttributeName(getAttributeName());
        mapping.setReferenceClassName(getReferenceClassName());
        mapping.setDerivesId(derivesId());
        
        // Process join fetch type.
        processJoinFetch(getJoinFetch(), mapping);
        
        // Process the batch fetch if specified.
        processBatchFetch(getBatchFetch(), mapping);
        
        // Process the orphanRemoval or PrivateOwned
        processOrphanRemoval(mapping);
        
        // Process the indirection.
        processIndirection(mapping);
        
        // Set the getter and setter methods if access is PROPERTY.
        setAccessorMethods(mapping);
        
        // Process the cascade types.
        processCascadeTypes(mapping);
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
        
        return mapping;
    }
    
    /**
     * INTERNAL:
     * Initialize a ManyToOneMapping.
     */
    protected ManyToOneMapping initManyToOneMapping() {
        ManyToOneMapping mapping = new ManyToOneMapping();
        mapping.setIsReadOnly(false);
        mapping.setIsOptional(isOptional());
        mapping.setAttributeName(getAttributeName());
        mapping.setReferenceClassName(getReferenceClassName());
        mapping.setDerivesId(derivesId());
        
        // Process join fetch type.
        processJoinFetch(getJoinFetch(), mapping);
        
        // Process the batch fetch if specified.
        processBatchFetch(getBatchFetch(), mapping);
        
        // Process the orphanRemoval or PrivateOwned
        processOrphanRemoval(mapping);
        
        // Process the indirection.
        processIndirection(mapping);
        
        // Set the getter and setter methods if access is PROPERTY.
        setAccessorMethods(mapping);
        
        // Process the cascade types.
        processCascadeTypes(mapping);
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
        
        return mapping;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
    
        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_primaryKeyJoinColumns, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a 1-1 primary key relationship.
     */
    public boolean isOneToOnePrimaryKeyRelationship() {
        return isOneToOne() && ! m_primaryKeyJoinColumns.isEmpty();
    }
    
    /**
     * INTERNAL:
     */
    public boolean isOptional() {
        return m_optional != null && m_optional;
    }
    
    /**
     * INTERNAL:
     * Process an association override for either an embedded object mapping, 
     * or a map mapping (element-collection, 1-M and M-M) containing an
     * embeddable object as the value or key. 
     */
    @Override
    protected void processAssociationOverride(AssociationOverrideMetadata associationOverride, EmbeddableMapping embeddableMapping, MetadataDescriptor owningDescriptor) {
        if (getMapping().isOneToOneMapping()) {
            processAssociationOverride(associationOverride, embeddableMapping, owningDescriptor.getPrimaryTable(), owningDescriptor);
        } else {
            super.processAssociationOverride(associationOverride, embeddableMapping, owningDescriptor);
        }
    }
    
    /**
     * INTERNAL:
     * Process an association override for either an embedded object mapping, 
     * or a map mapping (element-collection, 1-M and M-M) containing an
     * embeddable object as the value or key. 
     */
    protected void processAssociationOverride(AssociationOverrideMetadata associationOverride, EmbeddableMapping embeddableMapping, DatabaseTable defaultTable, MetadataDescriptor owningDescriptor) {
        // Process and use the association override's joinColumns. Avoid calling 
        // getJoinColumns since, by default, that method looks for an association
        // override on the descriptor. In this case that has already been taken 
        // care of for use before calling this method.
        for (JoinColumnMetadata joinColumn : getJoinColumnsAndValidate(associationOverride.getJoinColumns(), getReferenceDescriptor())) {
            DatabaseField pkField = joinColumn.getPrimaryKeyField();
            // This will default the reference column name in the single primary
            // key case (when the user does not specify it).
            setFieldName(pkField, getReferenceDescriptor().getPrimaryKeyFieldName(), MetadataLogger.PK_COLUMN);
            pkField.setTable(getReferenceDescriptor().getPrimaryKeyTable());
            DatabaseField fkField = ((OneToOneMapping) getMapping()).getTargetToSourceKeyFields().get(pkField);
                
            if (fkField == null) {
                throw ValidationException.invalidAssociationOverrideReferenceColumnName(pkField.getName(), associationOverride.getName(), embeddableMapping.getAttributeName(), owningDescriptor.getJavaClassName());
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
    }
    
    /**
     * INTERNAL:
     * Used to process primary keys and DerivedIds.
     */
    protected void processId(OneToOneMapping mapping) {
        // If this entity has a pk class, we need to validate our ids.
        MetadataDescriptor referenceDescriptor = getReferenceDescriptor();
        String referencePKClassName = referenceDescriptor.getPKClassName();

        if (referencePKClassName != null) {
            // They have a pk class
            String pkClassName = getDescriptor().getPKClassName();
            if (pkClassName == null){
                throw ValidationException.invalidCompositePKSpecification(getJavaClass(), pkClassName);
            }
            
            if (pkClassName.equals(referencePKClassName)){
                // This pk is the reference pk, so all pk attributes are 
                // accounted through this relationship.
                getOwningDescriptor().getPKClassIDs().clear();
            } else {
                // Validate our pk contains their pk.
                getOwningDescriptor().validateDerivedPKClassId(getAttributeName(), referencePKClassName, getReferenceClassName());
            }
        } else {
            MetadataClass type = null;
            if (referenceDescriptor.getClassAccessor().hasDerivedId()){
                // Referenced object has a derived ID but no PK class defined,
                // so it must be a simple pk type. Recurse through to get the 
                // simple type
                type = ((ObjectAccessor) referenceDescriptor.getAccessorFor(referenceDescriptor.getIdAttributeName())).getSimplePKType();
            } else {
                // Validate on their basic mapping.
                type = referenceDescriptor.getAccessorFor(referenceDescriptor.getIdAttributeName()).getRawClass();
            }
            
            getOwningDescriptor().validateDerivedPKClassId(getAttributeName(), getBoxedType(type.getName()), getReferenceClassName());
        }

        // Store the Id attribute name. Used with validation and OrderBy.
        getOwningDescriptor().addIdAttributeName(getAttributeName());

        // Add the primary key fields to the descriptor.  
        for (DatabaseField pkField : mapping.getForeignKeyFields()) {
            getOwningDescriptor().addPrimaryKeyField(pkField, null);
        }
    }
    
    /**
     * INTERNAL:
     * Process the indirection (aka fetch type)
     */
    protected void processIndirection(ObjectReferenceMapping mapping) {
        boolean usesIndirection = usesIndirection();
        
        // If weaving was disabled, and the class was not static weaved,
        // then disable indirection.
        if (usesIndirection && (!getProject().isWeavingEnabled())
                && (!ClassConstants.PersistenceWeavedLazy_Class.isAssignableFrom(getJavaClass(getDescriptor().getJavaClass())))) {
            usesIndirection = false;
        }
        
        if (usesIndirection && usesPropertyAccess()) {
            mapping.setIndirectionPolicy(new WeavedObjectBasicIndirectionPolicy(getGetMethodName(), getSetMethodName(), true));
        } else if (usesIndirection && usesFieldAccess()) {
            mapping.setIndirectionPolicy(new WeavedObjectBasicIndirectionPolicy(Helper.getWeavedGetMethodName(mapping.getAttributeName()), Helper.getWeavedSetMethodName(mapping.getAttributeName()), false));
        } else {
            mapping.setUsesIndirection(usesIndirection);
        }
        
        mapping.setIsLazy(isLazy());
    }
    
    /**
     * INTERNAL:
     * Process the mapping keys from the maps id value.
     */
    protected void processMapsId(OneToOneMapping oneToOneMapping) {
        EmbeddedIdAccessor embeddedIdAccessor = getDescriptor().getEmbeddedIdAccessor();
        
        if (embeddedIdAccessor == null) {
            // Case #4: a simple id association
            MappingAccessor idAccessor = getDescriptor().getAccessorFor(getDescriptor().getIdAttributeName());
            DatabaseMapping idMapping = idAccessor.getMapping();
                        
            // Grab the foreign key field and set it as the descriptor's id field.
            DatabaseField foreignKeyField = oneToOneMapping.getForeignKeyFields().elementAt(0);
            updatePrimaryKeyField(idAccessor, foreignKeyField);
            
            // Update the field on the mapping.
            ((DirectToFieldMapping) idMapping).setField(foreignKeyField);
            
            // Set the primary key mapping as read only.
            idMapping.setIsReadOnly(true);
            
            // Set the maps id mapping.
            oneToOneMapping.setDerivedIdMapping(idMapping);
        } else {
            if (embeddedIdAccessor.getReferenceClassName().equals(getReferenceDescriptor().getPKClassName())) {
                // Case #5: Parent's id class is the same as dependent's embedded id class
                // Case #6: Both parent and dependent use same embedded id class.            
                processMapsIdFields(oneToOneMapping, embeddedIdAccessor, embeddedIdAccessor);
            } else {
                if (m_mapsId.equals("")) {
                    // User didn't specify a mapsId value. By default the attribute name from this object accessor is used.
                    m_mapsId = getAttributeName();
                }
                
                // Set the maps id value on the mapping.
                oneToOneMapping.setMapsIdValue(m_mapsId);
                MappingAccessor mappingAccessor = embeddedIdAccessor.getReferenceDescriptor().getAccessorFor(m_mapsId);
        
                if (mappingAccessor == null) {
                    throw ValidationException.invalidMappedByIdValue(m_mapsId, getAnnotatedElementName(), embeddedIdAccessor.getReferenceClass());
                } else {
                    // Case #1: Dependent's embedded id maps a basic mapping to parent entity.
                    // Case #2: Dependent's embedded id maps the parent's id class
                    // Case #3: Dependent's embedded if maps the parent's embedded id class
                    processMapsIdFields(oneToOneMapping, embeddedIdAccessor, mappingAccessor);
                }
            }
            
            // Set the maps id mapping.
            oneToOneMapping.setDerivedIdMapping(embeddedIdAccessor.getMapping());
        }
    }
    
    /**
     * INTERNAL:
     * We're going to add field name translations where necessary. If the user
     * specified (erroneously that is) attribute overrides this will override
     * them.
     */
    protected void processMapsIdFields(OneToOneMapping oneToOneMapping, EmbeddedIdAccessor embeddedIdAccessor, MappingAccessor mapsIdAccessor) { 
        // At this point we have a one to one mapping to the reference class 
        // with specified join columns or defaulted ones. The foreign key
        // fields are the fields we want to use to map our id fields.
        for (DatabaseField fkField : oneToOneMapping.getForeignKeyFields()) {
            if (mapsIdAccessor.isBasic()) {
                // Case #1: Dependent's embedded id maps a basic mapping to parent entity.
                
                // Add the maps id accessor to the embedded id accessor's list 
                // of mappings that need to be set to read only at initialize
                // time on the cloned aggregate descriptor.
                embeddedIdAccessor.addMapsIdAccessor(mapsIdAccessor);

                // Add a field name translation to the mapping.
                embeddedIdAccessor.updateDerivedIdField((EmbeddableMapping) embeddedIdAccessor.getMapping(), mapsIdAccessor.getAttributeName(), fkField, mapsIdAccessor);
            } else {
                if (mapsIdAccessor.isDerivedIdClass()) {
                    // Case #2: Dependent's embedded id maps the parent's id class
                    // Case #3: Dependent's embedded if maps the parent's embedded id class
                    
                    // Add the maps id accessor to the embedded id accessor's 
                    // list  of mappings that need to be set to read only at 
                    // initialize time on the cloned aggregate descriptor.
                    embeddedIdAccessor.addMapsIdAccessor(mapsIdAccessor);
                } else {
                    // Case #5: Parent's id class is the same as dependent's embedded id class
                    // Case #6: Both parent and dependent use same embedded id class.
                    
                    // Set the mapping to read only.
                    embeddedIdAccessor.getMapping().setIsReadOnly(true);
                }
                
                // For this foreign key relation, get the primary key accessor 
                // from the reference descriptor.
                DatabaseField referencePKField = oneToOneMapping.getSourceToTargetKeyFields().get(fkField);
                MappingAccessor referencePKAccessor = getReferenceDescriptor().getPrimaryKeyAccessorForField(referencePKField);
        
                // If there is no primary key accessor then the user must have
                // specified an incorrect reference column name. Throw an exception.
                if (referencePKAccessor == null) {
                    throw ValidationException.invalidDerivedIdPrimaryKeyField(getReferenceClassName(), referencePKField.getQualifiedName(), getAttributeName(), getJavaClassName());
                } else {
                    // The reference primary key accessor will tell us which attribute 
                    // accessor we need to map a field name translation for.
                    MappingAccessor idAccessor = mapsIdAccessor.getReferenceDescriptor().getAccessorFor(referencePKAccessor.getAttributeName());
                
                    // Add a field name translation to the mapping.
                    ((EmbeddedAccessor) mapsIdAccessor).updateDerivedIdField((EmbeddableMapping) mapsIdAccessor.getMapping(), idAccessor.getAttributeName(), fkField, idAccessor);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the join columns for the owning side of a one to one mapping. 
     * The default pk and pk field names are used only with single primary key 
     * entities. The processor should never get as far as to use them with 
     * entities that have a composite primary key (validation exception will be 
     * thrown).
     */
    protected void processOneToOneForeignKeyRelationship(OneToOneMapping mapping) {
        // If the pk field (referencedColumnName) is not specified, it 
        // defaults to the primary key of the referenced table.
        String defaultPKFieldName = getReferenceDescriptor().getPrimaryKeyFieldName();
        
        // If the fk field (name) is not specified, it defaults to the 
        // concatenation of the following: the name of the referencing 
        // relationship property or field of the referencing entity or
        // embeddable class; "_"; the name of the referenced primary key 
        // column.
        String defaultFKFieldName = getDefaultAttributeName() + "_" + defaultPKFieldName;
        
        processOneToOneForeignKeyRelationship(mapping, getJoinColumns(getJoinColumns(), getReferenceDescriptor()), defaultPKFieldName, getReferenceDatabaseTable(), defaultFKFieldName, getDescriptor().getPrimaryTable());        
    }
    
    /**
     * INTERNAL:
     * Process the primary key join columns for the owning side of a one to one 
     * mapping. The default pk and pk field names are used only with single 
     * primary key entities. The processor should never get as far as to use 
     * them with entities that have a composite primary key (validation 
     * exception will be thrown).
     */
    protected void processOneToOnePrimaryKeyRelationship(OneToOneMapping mapping) {
        MetadataDescriptor referenceDescriptor = getReferenceDescriptor();
        List<PrimaryKeyJoinColumnMetadata> pkJoinColumns = processPrimaryKeyJoinColumns(new PrimaryKeyJoinColumnsMetadata(getPrimaryKeyJoinColumns()));
 
        // Add the source foreign key fields to the mapping.
        for (PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn : pkJoinColumns) {
            // The default primary key name is the primary key field name of the
            // referenced entity.
            DatabaseField pkField = primaryKeyJoinColumn.getPrimaryKeyField();
            setFieldName(pkField, referenceDescriptor.getPrimaryKeyFieldName(), MetadataLogger.PK_COLUMN);
            pkField.setTable(referenceDescriptor.getPrimaryTable());
            
            // The default foreign key name is the primary key of the
            // referencing entity.
            DatabaseField fkField = primaryKeyJoinColumn.getForeignKeyField();
            setFieldName(fkField, getDescriptor().getPrimaryKeyFieldName(), MetadataLogger.FK_COLUMN);
            fkField.setTable(getDescriptor().getPrimaryTable());
            
            // Add a source foreign key to the mapping.
            mapping.addForeignKeyField(fkField, pkField);
            
            // Mark the mapping read only
            mapping.setIsReadOnly(true);
        }
    }
    
    /**
     * INTERNAL:
     * Process the the correct metadata for the owning side of a one to one 
     * mapping. Note, the order of checking is important, that is, check for
     * a mapsId first.
     */
    protected void processOwningMappingKeys(OneToOneMapping mapping) {
        if (derivesId()) {
            // We need to process the join columns as we normally would.
            // Then we must update the fields from our derived id accessor.
            processOneToOneForeignKeyRelationship(mapping);
            
            if (hasMapsId()) {
                processMapsId(mapping);
            } else {
                processId(mapping);
            }
        } else if (isOneToOnePrimaryKeyRelationship()) {
            processOneToOnePrimaryKeyRelationship(mapping);
        } else if (hasJoinTable()) {
            mapping.setRelationTableMechanism(new RelationTableMechanism());
            processJoinTable(mapping, mapping.getRelationTableMechanism(), getJoinTable()); 
        } else {
            processOneToOneForeignKeyRelationship(mapping);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setId(Boolean id){
        m_id = id;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMapsId(String mapsId){
        m_mapsId = mapsId;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setOptional(Boolean isOptional) {
        m_optional = isOptional;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns) {
        m_primaryKeyJoinColumns = primaryKeyJoinColumns;
    }
}
