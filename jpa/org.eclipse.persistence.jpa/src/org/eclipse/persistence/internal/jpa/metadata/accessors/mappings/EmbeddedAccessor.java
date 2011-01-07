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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     11/23/2009-2.0 Guy Pelletier 
 *       - 295790: JPA 2.0 adding @MapsId to one entity causes initialization errors in other entities
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.EmbeddableMapping;

/**
 * An embedded relationship accessor. It may define all the same attributes
 * as an entity, therefore, it also must handle nesting embedded's to the nth
 * level. An embedded owning descriptor is a reference back to the actual
 * owning entity's descriptor where the first embedded was discovered.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class EmbeddedAccessor extends MappingAccessor {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    private List<AssociationOverrideMetadata> m_associationOverrides;
    private List<AttributeOverrideMetadata> m_attributeOverrides;

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public EmbeddedAccessor() {
        super("<embedded>");
    }
    
    /**
     * INTERNAL:
     */
    protected EmbeddedAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    public EmbeddedAccessor(MetadataAnnotation embedded, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(embedded, accessibleObject, classAccessor);
        
        // Set the attribute overrides if some are present.
        m_attributeOverrides = new ArrayList<AttributeOverrideMetadata>();
        // Process the attribute overrides first.
        MetadataAnnotation attributeOverrides = getAnnotation(AttributeOverrides.class);
        if (attributeOverrides != null) {
            for (Object attributeOverride : (Object[]) attributeOverrides.getAttributeArray("value")) {
                m_attributeOverrides.add(new AttributeOverrideMetadata((MetadataAnnotation)attributeOverride, accessibleObject));
            }
        }
        
        // Process the single attribute override second.  
        MetadataAnnotation attributeOverride = getAnnotation(AttributeOverride.class);  
        if (attributeOverride != null) {
            m_attributeOverrides.add(new AttributeOverrideMetadata(attributeOverride, accessibleObject));
        }
        
        // Set the association overrides if some are present.
        m_associationOverrides = new ArrayList<AssociationOverrideMetadata>();
        // Process the attribute overrides first.
        MetadataAnnotation associationOverrides = getAnnotation(AssociationOverrides.class);
        if (associationOverrides != null) {
            for (Object associationOverride : (Object[]) associationOverrides.getAttributeArray("value")) {
                m_associationOverrides.add(new AssociationOverrideMetadata((MetadataAnnotation) associationOverride, accessibleObject));
            }
        }
        
        // Process the single attribute override second.
        MetadataAnnotation associationOverride = getAnnotation(AssociationOverride.class);  
        if (associationOverride != null) {
            m_associationOverrides.add(new AssociationOverrideMetadata(associationOverride, accessibleObject));
        }
    }
    
    /**
     * INTERNAL:
     */
    public void addMapsIdAccessor(MappingAccessor mapsIdAccessor) {
        ((AggregateObjectMapping) getMapping()).addMapsIdMapping(mapsIdAccessor.getMapping());
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof EmbeddedAccessor) {
            EmbeddedAccessor embeddedAccessor = (EmbeddedAccessor) objectToCompare;
            
            if (! valuesMatch(m_associationOverrides, embeddedAccessor.getAssociationOverrides())) {
                return false;
            }
            
            return valuesMatch(m_attributeOverrides, embeddedAccessor.getAttributeOverrides());
        }
        
        return false;
    }
    
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AssociationOverrideMetadata> getAssociationOverrides() {
        return m_associationOverrides;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AttributeOverrideMetadata> getAttributeOverrides() {
        return m_attributeOverrides;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
    
        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_attributeOverrides, accessibleObject);
        initXMLObjects(m_associationOverrides, accessibleObject);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isEmbedded() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process an embedded.
     */    
    public void process() {
        // Build and aggregate object mapping and add it to the descriptor.
        AggregateObjectMapping mapping = new AggregateObjectMapping();
        setMapping(mapping);
        
        mapping.setIsNullAllowed(true);
        mapping.setReferenceClassName(getReferenceClassName());
        mapping.setAttributeName(getAttributeName());    
        
        // Will check for PROPERTY access
        setAccessorMethods(mapping);
        
        // Process attribute overrides.
        processAttributeOverrides(m_attributeOverrides, mapping, getReferenceDescriptor());
       
        // Process association overrides.
        processAssociationOverrides(m_associationOverrides, mapping, getReferenceDescriptor());
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAssociationOverrides(List<AssociationOverrideMetadata> associationOverrides) {
        m_associationOverrides = associationOverrides;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAttributeOverrides(List<AttributeOverrideMetadata> attributeOverrides) {
        m_attributeOverrides = attributeOverrides;
    }
    
    /**
     * INTERNAL:
     * Called when process the mapsId metadata. The id fields for this owning
     * descriptor must have it's id fields update to those from the one to one
     * accessor that maps them. We process embedded and embedded id mappings
     * first, so by default they get mapped and processed as they normally
     * would. When we go through the relationship accessors and discover a
     * mapsId we then need to make some updates to our list of primary key
     * fields.
     */
    protected void updateDerivedIdField(EmbeddableMapping embeddableMapping, String overrideName, DatabaseField overrideField, MappingAccessor mappingAccessor) {
       addFieldNameTranslation(embeddableMapping, overrideName, overrideField, mappingAccessor);
       
       // Update the primary key field.
       updatePrimaryKeyField(mappingAccessor, overrideField);
    }
}
