/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: 
 *     03/19/2009-2.0  dclarke  - initial API start    
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 *     07/06/2009-2.0  mobrien - 266912: Introduce IdentifiableTypeImpl between ManagedTypeImpl
 *       - EntityTypeImpl now inherits from IdentifiableTypeImpl instead of ManagedTypeImpl
 *       - MappedSuperclassTypeImpl now inherits from IdentifiableTypeImpl instead
 *       of implementing IdentifiableType indirectly
 *     08/06/2010-2.2 mobrien 322018 - reduce protected instance variables to private to enforce encapsulation  
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.metamodel.MappedSuperclassType;

import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the MappedSuperclassType interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 *  Instances of the type MappedSuperclassType represent mapped
 *  superclass types.
 * 
 * @see javax.persistence.metamodel.MappedSuperclassType
 * 
 * @since EclipseLink 1.2 - JPA 2.0
 *  
 * @param <X> The represented entity type  
 */ 
public class MappedSuperclassTypeImpl<X> extends IdentifiableTypeImpl<X> implements MappedSuperclassType<X> {
    
    /** Item 54: DI 89: explicit UID will avoid performance hit runtime generation of one */
    private static final long serialVersionUID = 3770722221322920646L;
    
    /** 
     * INTERNAL:
     * The map of Identifiable types that inherit from this MappedSuperclass.
     * The scope of this map is outside of the JPA 2.0 specification and is limited to MappedSuperclass types.
     * The types in this map are keyed on the Java class of the inheriting type.
     * This map acts as the reverse of all superType fields that point to "this" MappedSuperclass.
     **/
    private Map<Class, IdentifiableTypeImpl> inheritingIdentifiableTypes;
    
    protected MappedSuperclassTypeImpl(MetamodelImpl metamodel, RelationalDescriptor relationalDescriptor) {        
        super(metamodel, relationalDescriptor);
        inheritingIdentifiableTypes = new HashMap<Class, IdentifiableTypeImpl>();
        // The supertype field will remain uninstantiated until MetamodelImpl.initialize() is complete
    }
    
    /**
     * INTERNAL:
     * Add an inheriting subclass to the map of Identifiable types that inherit from this mappedSuperclass.
     * @param identifiableType
     */
    protected void addInheritingType(IdentifiableTypeImpl identifiableType) {
        // The Map will always be instantiated in the constructor
        inheritingIdentifiableTypes.put(identifiableType.getJavaType(), identifiableType);
    }
    
    /**
     * INTERNAL:
     * Return an instance of a MappedSuperclassType based on the RelationalDescriptor.
     * @param metamodel
     * @param relationalDescriptor
     * @return
     */
    protected static MappedSuperclassTypeImpl<?> create(MetamodelImpl metamodel, RelationalDescriptor relationalDescriptor) {
        /**
         * Set the javaClass on the descriptor for the current classLoader (normally done in MetadataProject.addMetamodelMappedSuperclass).
         * This will ensure the class is both set and is in the right classLoader - even if the class is already set.
         * Perform this conversion only for our custom pseudo descriptors for MappedSuperclasses.
         * The classLoader should be obtained from the ConversionManager so we handle EE deployments using a shared-library
         */ 
        relationalDescriptor.convertClassNamesToClasses(metamodel.getSession().getDatasourcePlatform().getConversionManager().getLoader());
        MappedSuperclassTypeImpl<?> mappedSuperclassTypeImpl = new MappedSuperclassTypeImpl(metamodel, relationalDescriptor);
        return mappedSuperclassTypeImpl;
    }

    /**
     * INTERNAL:
     *    MappedSuperclasses need special handling to get their type from an inheriting subclass.
     *    This function determines the type for an attribute by returning the same inherited attribute from a subclass
     * @param name
     * @return
     */
    public AttributeImpl getMemberFromInheritingType(String name) {
        AttributeImpl inheritedAttribute = null;
        // search the inheriting types map for an attribute matching the attribute name
        for(IdentifiableTypeImpl inheritingType : inheritingIdentifiableTypes.values()) {            
            if(inheritingType.getMembers().containsKey(name)) {
                inheritedAttribute = (AttributeImpl)inheritingType.getAttribute(name);
                break;
            }
        }
        // we will return a null attribute in the case that a MappedSuperclass has no implementing entities
        return inheritedAttribute;
    }    
    
    /**
     *  Return the persistence type.
     *  @return persistence type
     */ 
    public javax.persistence.metamodel.Type.PersistenceType getPersistenceType() {
        return PersistenceType.MAPPED_SUPERCLASS;
    }

    /**
     * INTERNAL:
     * Return whether this type is an Entity (true) or MappedSuperclass (false) or Embeddable (false)
     * @return
     */
    @Override
    public boolean isEntity() {
        return false;
    }
   
    /**
     * INTERNAL:
     * Return whether this type is an MappedSuperclass (true) or Entity (false) or Embeddable (false)
     * @return
     */
    @Override
    public boolean isMappedSuperclass() {
        return !isEntity();
    }
}
