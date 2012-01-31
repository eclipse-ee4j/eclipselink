/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     09/29/2009-2.0 Guy Pelletier 
 *       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 *     11/02/2009-2.0 Michael O'Brien
 *       - 266912: JPA 2.0 Metamodel support for 1:m as 1:1 in DI 96
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     09/03/2010-2.2 Guy Pelletier 
 *       - 317286: DB column lenght not in sync between @Column and @JoinColumn
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.List;
import java.util.Map;

import org.eclipse.persistence.eis.mappings.EISOneToManyMapping;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.EmbeddableMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.UnidirectionalOneToManyMapping;

/**
 * INTERNAL:
 * A OneToMany relationship accessor. A OneToMany annotation currently is not
 * required to be on the accessible object, that is, a 1-M can default.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level)
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject  method.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class OneToManyAccessor extends CollectionAccessor {
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public OneToManyAccessor() {
        super("<one-to-many>");
    }
    
    /**
     * INTERNAL:
     */
    public OneToManyAccessor(MetadataAnnotation oneToMany, MetadataAnnotatedElement annotatedElement, ClassAccessor classAccessor) {
        super(oneToMany, annotatedElement, classAccessor);
        
        // A one to many mapping can default.
        if (oneToMany != null) {
            setOrphanRemoval((Boolean) oneToMany.getAttribute("orphanRemoval"));
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return super.equals(objectToCompare) && objectToCompare instanceof OneToManyAccessor;
    }
    
    /**
     * INTERNAL:
     * 
     * Return the logging context for this accessor.
     */
    protected String getLoggingContext() {
        return MetadataLogger.ONE_TO_MANY_MAPPING_REFERENCE_CLASS;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isOneToMany() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process a OneToMany accessor into an EclipseLink OneToManyMapping. If a 
     * JoinTable is found however, we must create a ManyToManyMapping.
     */
    @Override
    public void process() {
        super.process();
        
        if (hasMappedBy()) {
            // Process a 1-M using the mapped by mapping values.
            processOneToManyMapping();
        } else if (getJoinColumns().isEmpty() || getDescriptor().getClassDescriptor().isEISDescriptor()) {
            // No join columns and no mapped by value, default to
            // unidirectional 1-M using a M-M mapping and a join table.
            processManyToManyMapping();
        } else {
            // If we find join column(s) then process a uni-directional 1-M.
            processUnidirectionalOneToManyMapping();
        }
    }
    
    /**
     * INTERNAL:
     * Process an association override for either an embedded object mapping, 
     * or a map mapping (element-collection, 1-M and M-M) containing an
     * embeddable object as the value or key. 
     */
    @Override
    protected void processAssociationOverride(AssociationOverrideMetadata associationOverride, EmbeddableMapping embeddableMapping, MetadataDescriptor owningDescriptor) {
        if (getMapping().isUnidirectionalOneToManyMapping()) {
            // Create an override mapping and process the join columns to it.
            UnidirectionalOneToManyMapping overrideMapping = new UnidirectionalOneToManyMapping();
            overrideMapping.setAttributeName(getAttributeName());
            processUnidirectionalOneToManyTargetForeignKeyRelationship(overrideMapping, associationOverride.getJoinColumns(), owningDescriptor);
        
            // The override mapping will have the correct source, sourceRelation, 
            // target and targetRelation keys. Along with the correct relation table.
            embeddableMapping.addOverrideUnidirectionalOneToManyMapping(overrideMapping);
            
            // Set the override mapping which will have the correct metadata
            // set. This is the metadata any non-owning relationship accessor
            // referring to this accessor will need.
            setOverrideMapping(overrideMapping);
        } else {
            super.processAssociationOverride(associationOverride, embeddableMapping, owningDescriptor);
        }
    }
    
    /**
     * INTERNAL:
     * Process an many to many mapping for this accessor since a join table 
     * was specified.
     */
    protected void processManyToManyMapping() {
        // Create a M-M mapping and process common collection mapping metadata
        // first followed by specific metadata.
        // Allow for different descriptor types (EIS) to create different mapping types.
        CollectionMapping mapping = getDescriptor().getClassDescriptor().newManyToManyMapping();
        process(mapping);
        
        if (mapping instanceof ManyToManyMapping) {
            // 266912: If this 1:n accessor is different than the n:n mapping - track this
            ((ManyToManyMapping)mapping).setDefinedAsOneToManyMapping(true);
                    
            // Process the JoinTable metadata.
            processJoinTable(mapping, ((ManyToManyMapping)mapping).getRelationTableMechanism(), getJoinTableMetadata());
        } else if (mapping instanceof EISOneToManyMapping) {
            processEISOneToManyMapping((EISOneToManyMapping)mapping);
        }
    }
    
    /**
     * INTERNAL:
     * Process an one to many mapping for this accessor.
     */
    protected void processOneToManyMapping() {
       // Non-owning side, process the foreign keys from the owner.
       DatabaseMapping owningMapping = getOwningMappingAccessor();
       if (owningMapping.isOneToOneMapping()){ 
           OneToOneMapping ownerMapping = (OneToOneMapping) owningMapping;
           
           // If the owner uses a relation table mechanism we must map a M-M.
           if (ownerMapping.hasRelationTableMechanism()) {
              ManyToManyMapping mapping = new ManyToManyMapping();
              // Process the common collection mapping. 
              process(mapping);
              // Process the mapped by relation table metadata.
              processMappedByRelationTable(ownerMapping.getRelationTableMechanism(), mapping.getRelationTableMechanism());
              // Set the mapping to read only
              mapping.setIsReadOnly(true);
           } else {
               // Create a 1-M mapping and process common collection mapping 
               // metadata first followed by specific metadata.
              OneToManyMapping mapping = new OneToManyMapping();
              process(mapping);
              
               Map<DatabaseField, DatabaseField> keys = ownerMapping.getSourceToTargetKeyFields();
               for (DatabaseField fkField : keys.keySet()) {
                   DatabaseField pkField = keys.get(fkField);
                    
                   // If we are within a table per class strategy we have to update
                   // the primary key field to point to our own database table. 
                   // The extra table check is if the mapping is actually defined
                   // on our java class (meaning we have the right table at this
                   // point and can avoid the cloning)
                   if (getDescriptor().usesTablePerClassInheritanceStrategy() && ! pkField.getTable().equals(getDescriptor().getPrimaryTable())) {
                       // We need to update the pk field to be to our table.
                       pkField = pkField.clone();
                       pkField.setTable(getDescriptor().getPrimaryTable());
                   }
                
                   mapping.addTargetForeignKeyField(fkField, pkField);
               }
           }
       } else {
           // If improper mapping encountered, throw an exception.
           throw ValidationException.invalidMapping(getJavaClass(), getReferenceClass()); 
       }
    }
    
    /**
     * INTERNAL:
     * Process an unidirectional one to many mapping for this accessor since 
     * join columns were specified and no mapped by value. 
     */
    protected void processUnidirectionalOneToManyMapping() {
        // Create a 1-M unidirectional mapping and process common collection 
        // mapping metadata first followed by specific metadata.
        UnidirectionalOneToManyMapping mapping = new UnidirectionalOneToManyMapping();
        process(mapping);
        
        // Process the JoinColumn metadata.
        processUnidirectionalOneToManyTargetForeignKeyRelationship(mapping, getJoinColumns(getJoinColumns(), getOwningDescriptor()), getOwningDescriptor());
    }
    
    /**
     * INTERNAL: 
     * Process the join column(s) metadata for the owning side of a 
     * unidirectional one to many mapping. The default pk used only with single 
     * primary key  entities. The processor should never get as far as to use 
     * them with entities that have a composite primary key (validation 
     * exception will be thrown).
     */
    protected void processUnidirectionalOneToManyTargetForeignKeyRelationship(UnidirectionalOneToManyMapping mapping, List<JoinColumnMetadata> joinColumns, MetadataDescriptor owningDescriptor) {         
        // If the fk field (name) is not specified, it defaults to the 
        // concatenation of the following: the name of the referencing 
        // relationship property or field of the referencing entity; "_"; 
        // the name of the referenced primary key column.
        String defaultFKFieldName = getDefaultAttributeName() + "_" + owningDescriptor.getPrimaryKeyFieldName();
            
        // Join columns will come from a @JoinColumn(s).
        // Add the source foreign key fields to the mapping.
        for (JoinColumnMetadata joinColumn : joinColumns) {
            // Look up the primary key field from the referenced column name.
            DatabaseField pkField = getReferencedField(joinColumn.getReferencedColumnName(), owningDescriptor, MetadataLogger.PK_COLUMN);
            
            DatabaseField fkField = joinColumn.getForeignKeyField(pkField);
            setFieldName(fkField, defaultFKFieldName, MetadataLogger.FK_COLUMN);

            // Set the table name if one is not already set.
            if (!fkField.hasTableName()) {
                fkField.setTable(getReferenceDescriptor().getPrimaryTable());
            }
            
            // Uni-directional 12M mapping would like a type on the foreign key 
            // field. If one is not set, a unidirectional one to many mapping
            // will try to set one itself in its postInitialize. There is 
            // currently a bug against this since the postInitiaze does not 
            // handle the fact that the descriptor may be an aggregate, hence 
            // not be able to look up a primary key mapping correctly. 
            // From a metadata processing standpoint, we'll attempt to make sure 
            // one is set. Meaning in some cases we won't be able to if we don't
            // have an associated mapping accessor for the pkField. So why
            // wouldn't we? One, it could be a bogus field specified only for 
            // testing purposes to ensure an override is correctly applied and 
            // two, the field could be part of a derived id (which at this point 
            // we don't have the mapping accessor readily accessible. (may be 
            // able to fix this if it becomes a problem). And thirdly, there is 
            // the 'off' chance we've screwed up metadata processing somewhere 
            // ( yeah right! ) so instead of show casing our mistakes, let's 
            // hide them! :-) Anyway, long story short, if there is no
            // mappingAccessor for the pkField, don't do anything and silently 
            // continue. Best we can do right now ...
            MappingAccessor mappingAccessor = owningDescriptor.getPrimaryKeyAccessorForField(pkField);
            if (mappingAccessor != null) {
                // If the mapping specified a converter then the field
                // classification may be set so check it first.
                Class fieldClassification = mappingAccessor.getMapping().getFieldClassification(mappingAccessor.getMapping().getField());
                
                String typeName;
                if (fieldClassification == null) {
                    // No fieldClassification, use the raw class from the 
                    // mapping accessor.
                    typeName = mappingAccessor.getRawClass().getName();
                } else {
                    typeName = fieldClassification.getName();
                } 
                
                fkField.setTypeName(typeName);
            }
           
            // Add target foreign key to the mapping.
            mapping.addTargetForeignKeyField(fkField, pkField);
            
            // If any of the join columns is marked read-only then set the 
            // mapping to be read only.
            if (fkField.isReadOnly()) {
                mapping.setIsReadOnly(true);
            }
        }
    }
}
