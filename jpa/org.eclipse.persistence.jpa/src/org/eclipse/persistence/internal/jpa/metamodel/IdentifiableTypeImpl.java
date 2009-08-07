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
 *     05/26/2009-2.0  mobrien - 266912: Add implementation of IdentifiableType 
 *       as EntityType inherits here instead of ManagedType as of rev# 4265 
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.CMP3Policy;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Entity interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 *  Instances of the type IdentifiableType represent entity or 
 *  mapped superclass types.
 * 
 * @see javax.persistence.metamodel.Entity
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 * @param <X> The represented entity or mapped superclass type. 
 *  
 */ 
public abstract class IdentifiableTypeImpl<X> extends ManagedTypeImpl<X> implements IdentifiableType<X> {

    /** 
     * The supertype may be an entity or mappedSuperclass.<p>
     * For top-level identifiable types with no superclass - return null (not Object) 
     */
    protected IdentifiableType<? super X> superType;
    
    protected IdentifiableTypeImpl(MetamodelImpl metamodel, RelationalDescriptor descriptor) {
        super(metamodel, descriptor);
        /* The superType field cannot be set until all ManagedType instances 
         * have been instantiated for this metamodel.
         * This occurs later in MetamodelImpl.initialize()
         */
    }

    /**
     *  Return the attribute that corresponds to the id attribute 
     *  declared by the entity or mapped superclass.
     *  @param type  the type of the represented declared id attribute
     *  @return declared id attribute
     *  @throws IllegalArgumentException if id attribute of the given
     *          type is not declared in the identifiable type or if
     *          the identifiable type has an id class
     */
    public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
        // return the Id only if it is declared on this entity
        throw new PersistenceException("Not Yet Implemented");
    }
    
    /**
     *  Return the attribute that corresponds to the version 
     *  attribute declared by the entity or mapped superclass.
     *  @param type  the type of the represented declared version 
     *               attribute
     *  @return declared version attribute
     *  @throws IllegalArgumentException if version attribute of the 
     *          type is not declared in the identifiable type
     */
    public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
        throw new PersistenceException("Not Yet Implemented");
    }
    
    /**
     *   Return the attributes corresponding to the id class of the
     *   identifiable type.
     *   @return id attributes
     *   @throws IllegalArgumentException if the identifiable type
     *           does not have an id class
     */
    public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
        throw new PersistenceException("Not Yet Implemented");
    }
    
    /**
     *  Return the attribute that corresponds to the id attribute of 
     *  the entity or mapped superclass.
     *  @param type  the type of the represented id attribute
     *  @return id attribute
     *  @throws IllegalArgumentException if id attribute of the given
     *          type is not present in the identifiable type or if
     *          the identifiable type has an id class
     */
    public <Y> SingularAttribute<? super X, Y> getId(Class<Y> type) {
        throw new PersistenceException("Not Yet Implemented");
/*        SingularAttribute<? super X, Y> anAttribute = null;
        Class<Y> realType = null;
        //boolean isId = this.accessor.isId();
        if(type != realType) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                "metamodel_identifiable_id_attribute_type_incorrect", 
                new Object[] { anAttribute, this, type, realType}));
        }
        return anAttribute;*/
    }
    
    /**
     *  Return the type that represents the type of the id.
     *  @return type of id
     */
    public Type<?> getIdType() {
        // NOTE: This code is another good reason to abstract out a PKPolicy on the descriptor
        // descriptor.getPrimaryKeyPolicy().getIdClass();
        CMPPolicy cmpPolicy = getDescriptor().getCMPPolicy();

        if (null == cmpPolicy) {
            // Composite key support (IE: @EmbeddedId)            
            List<DatabaseMapping> pkMappings = getDescriptor().getObjectBuilder().getPrimaryKeyMappings();
            // Check the primaryKeyFields on the descriptor - for MappedSuperclass pseudo-Descriptors
            if(pkMappings.size() == 0) {
                // Search the mappings for Id mappings
                DatabaseMapping aMapping = null;
                for(Iterator<DatabaseMapping> mappingsIterator = getDescriptor().getMappings().iterator(); mappingsIterator.hasNext();) {
                    aMapping = mappingsIterator.next();
                    if(aMapping.isDerivedIdMapping()) {
                        String attributeName = aMapping.getAttributeName();
                        // get the attribute Id (declared or not)
                        Attribute anAttribute = this.getAttribute(attributeName);
                        if(anAttribute != null) {
                            return this.getMetamodel().getType(anAttribute.getJavaType());
                        }
                        
                    }
                }
            }
            
            if (pkMappings.size() == 1) {
                Class aClass = pkMappings.get(0).getAttributeClassification(); // null for OneToOneMapping
                // lookup class in our types map
                Type<?> aType = this.metamodel.getType(aClass);
                return aType;
            }
        }
        
        // Single Key support using any Java class - built in or user defined
        // There already is an instance of the PKclass on the policy
        if (cmpPolicy instanceof CMP3Policy) {
            // BasicType, EntityType or IdentifiableType are handled here, lookup the class in the types map and create a wrapper if it does not exist yet
            return this.metamodel.getType(((CMP3Policy) cmpPolicy).getPKClass());
        }
        // Non-specification mandated exception        
        throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                "metamodel_incompatible_persistence_config_for_getIdType", 
                new Object[] { this }));        
    }
    
    /**
     *  Return the identifiable type that corresponds to the most
     *  specific mapped superclass or entity extended by the entity 
     *  or mapped superclass. 
     *  @return supertype of identifiable type or null if no such supertype
     */
    public IdentifiableType<? super X> getSupertype() {
        return this.superType;
    }

    /**
     *  Return the attribute that corresponds to the version 
     *    attribute of the entity or mapped superclass.
     *  @param type  the type of the represented version attribute
     *  @return version attribute
     *  @throws IllegalArgumentException if version attribute of the 
     *          given type is not present in the identifiable type
     */
    public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> type) {
        throw new PersistenceException("Not Yet Implemented");
/*        SingularAttribute<? super X, Y> anAttribute = null;
        Class<Y> realType = null;
        if(type != realType) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                "metamodel_identifiable_version_attribute_type_incorrect", 
                new Object[] { anAttribute, this, type, realType}));
        }
        return anAttribute;*/
    }
    
    /**
     *  Whether or not the identifiable type has an id attribute.
     *  Returns true for a simple id or embedded id; returns false
     *  for an idclass.
     *  @return boolean indicating whether or not the identifiable
     *           type has a single id attribute
     */
    public boolean hasSingleIdAttribute() {
        throw new PersistenceException("Not Yet Implemented");
    }
    
    /**
     *  Whether or not the identifiable type has a version attribute.
     *  @return boolean indicating whether or not the identifiable
     *           type has a version attribute
     */
    public boolean hasVersionAttribute() {
        throw new PersistenceException("Not Yet Implemented");
    }
    
    /**
     * INTERNAL:
     * Return whether this type is identifiable.
     * This would be EntityType and MappedSuperclassType
     * @return
     */
    @Override
    public boolean isIdentifiableType() {
        return true;
    }

    /**
     * INTERNAL:
     * Set the superType for this IdentifiableType - only after all ManagedTypes 
     * have been instantiated for this Metamodel.<p>
     * Top-level identifiable types have their supertype set to null.
     * 
     * @param superType - the entity or mappedSuperclass superType
     */
    protected void setSupertype(IdentifiableType<? super X> superType) {
        // See design issue #42 - we return null for top-level types (with no superclass) as well as unset supertypes
        // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_42:_20090709:_IdentifiableType.supertype_-_what_do_top-level_types_set_it_to
        this.superType = superType;
    }
}
