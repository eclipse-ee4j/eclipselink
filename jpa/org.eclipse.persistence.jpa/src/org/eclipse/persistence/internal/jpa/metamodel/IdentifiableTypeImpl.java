/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
 *     09/23/2009-2.0  mobrien - 266912: Implement hasSingleIdAttribute() and 
 *       all other 6 remaining methods for Id and Version support.
 *       DI 71 - 77 and 56
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_74:_20090909:_Implement_IdentifiableType.hasSingleIdAttribute.28.29 
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     06/14/2010-2.1  mobrien - 314906: getJavaType should return the 
 *       collection javaType C in <X,C,V) of <X, List<V>, V> instead off the elementType V.
 *       Because of this we switch to using getBindableJavaType() in getIdType()
 *     08/06/2010-2.2 mobrien 322018 - reduce protected instance variables to private to enforce encapsulation          
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
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
 * @since EclipseLink 1.2 - JPA 2.0
 * @param <X> The represented entity or mapped superclass type. 
 *  
 */ 
public abstract class IdentifiableTypeImpl<X> extends ManagedTypeImpl<X> implements IdentifiableType<X> {

    /** 
     * The supertype may be an entity or mappedSuperclass.<p>
     * For top-level inheritance root identifiable types with no superclass - return null (not Object) 
     */
    private IdentifiableType<? super X> superType;
    
    /**
     * The collection of SingularAttributes that are Id attributes. 
     */
    private Set<SingularAttribute<? super X, ?>> idAttributes;
    
    /**
     * The SingularAttribute if it exists that is a version attribute
     */
    private SingularAttribute<? super X, ?> versionAttribute;
    
    protected IdentifiableTypeImpl(MetamodelImpl metamodel, RelationalDescriptor descriptor) {
        super(metamodel, descriptor);        
        /* The superType field cannot be set until all ManagedType instances 
         * have been instantiated for this metamodel.
         * This is required so that any references between attributes can be resolved.
         * This occurs later in MetamodelImpl.initialize()
         * The idAttributes field is computed at the end of MetamodelImpl.initialize()
         * The versionAttribute is lazy loaded.
         */
    }

    /**
     * INTERNAL:
     * The idAttributes collection is computed at the end of MetamodelImpl.initialize()
     */
    protected void initializeIdAttributes() {
        // initialize the set of id attributes directly from the mapping
        idAttributes = new HashSet<SingularAttribute<? super X, ?>>();
        for(Attribute attribute : this.getAttributes()) {
            if(!((AttributeImpl)attribute).isPlural()) {
                if(((SingularAttribute)attribute).isId()) {
                    idAttributes.add((SingularAttribute)attribute);
                }
            }
        }
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
        /**
         * We throw an IAE in 3 cases
         * 1) If the type is different from the javaType of the attribute
         * 2) If the id is not declared on (this) type
         * 3) If the id is not part of an IdClass (it is an EmbeddedId or just an Id)
         * 4) If the id does not exist on the hierarchy - never happens
         */
        // No need to check if an id exists - on an IdentifiableType - there is always at least one
        // This call will throw an IAE for 1) and 3)
        SingularAttribute<? super X, Y> anId = this.getId(type);
        // return the id only if it is declared on this IdentifableType
        // We know that the attribute exists - so the an IAE will be thrown for 2) for us
        return (SingularAttribute<X, Y>)getDeclaredAttribute(anId.getName(), true);
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
        /**
         * We throw an IAE in 3 cases
         * 1) If the type is different from the javaType of the attribute
         * 2) If the version is not declared on (this) type
         * 3) If the version does not exist on the hierarchy
         */
        // This call will throw an IAE for 1) and 3)
        SingularAttribute<? super X, Y> aVersion = this.getVersion(type);
        // return the version only if it is declared on this IdentifableType
        // We know that the attribute exists - so the an IAE will be thrown for 2) for us
        return (SingularAttribute<X, Y>)getDeclaredAttribute(aVersion.getName(), true);
    }
    
    /**
     *  Return the attributes corresponding to the id class of the
     *  identifiable type.   
     *  @return id attributes
     *  @throws IllegalArgumentException if the identifiable type
     *          does not have an id class
     */
    public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
        // Get the list of IdClass attributes previously stored on the core project during metadata processing
        List<String> idClassNamesList = getMetamodel().getProject().getMetamodelIdClassMap()
            .get(getJavaType().getCanonicalName());
        // Check for IdClass existence
        if(null != idClassNamesList) {
            // All Id attributes are part of an IdClass
            return this.idAttributes;
        } else {
            // No IdClass attributes found - the IdentifiableType may still have a single @Id or an @EmbeddedId in this case
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_identifiable_type_has_no_idclass_attribute", 
                    new Object[] {this}));
        }        
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
        // We assume that there is at most a single EmbeddedId
        SingularAttribute<? super X, Y> idAttribute = null;
        if(!hasSingleIdAttribute()) {
            // Id is part of an IdClass
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_identifiable_id_attribute_is_incorrect_idclass", 
                    new Object[] { this }));
        } else {
            // verify single id attribute type
            for(SingularAttribute<? super X, ?> anAttribute : idAttributes) {
                // Verify type is correct - relax restriction on null and Object.class (from same classLoader)
                if(null == type || Object.class == type || 
                        ((type != null) && (type.getCanonicalName().equals(anAttribute.getJavaType().getCanonicalName())))) {
                    idAttribute = (SingularAttribute<? super X, Y>) anAttribute;
                } else {
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                        "metamodel_identifiable_id_attribute_type_incorrect", 
                        new Object[] { anAttribute, this, type, anAttribute.getJavaType() }));
                }
            }
        }
        return idAttribute;
    }
    
    /**
     *  Return the type that represents the type of the id.
     *  @return type of id
     */
    public Type<?> getIdType() {
        // NOTE: This code is another good reason to abstract out a PKPolicy on the descriptor
        CMPPolicy cmpPolicy = getDescriptor().getCMPPolicy();
        if (null == cmpPolicy) {
            // Composite key support (IE: @EmbeddedId)            
            List<DatabaseMapping> pkMappings = getDescriptor().getObjectBuilder().getPrimaryKeyMappings();
            // Check the primaryKeyFields on the descriptor - for MappedSuperclass pseudo-Descriptors
            if(pkMappings.isEmpty()) {
                // Search the mappings for Id mappings
                for(DatabaseMapping aMapping : getDescriptor().getMappings()) {                    
                    if(aMapping.isJPAId()) {
                        // get the attribute Id (declared or not)
                        Attribute anAttribute = this.getAttribute(aMapping.getAttributeName());
                        if(anAttribute != null) {
                            return this.getMetamodel().getType(((Bindable)anAttribute).getBindableJavaType()); // all Attributes are Bindable
                        }                        
                    }
                }
            }
            
            if (pkMappings.size() == 1) {
                Class aClass = pkMappings.get(0).getAttributeClassification(); // null for OneToOneMapping
                // lookup class in our types map
                return this.getMetamodel().getType(aClass);
            }
        }
        
        // Single Key support using any Java class - built in or user defined
        // There already is an instance of the PKclass on the policy
        if (cmpPolicy.isCMP3Policy()) {
            // BasicType, EntityType or IdentifiableType are handled here, lookup the class in the types map and create a wrapper if it does not exist yet
            return this.getMetamodel().getType(((CMP3Policy) cmpPolicy).getPKClass());
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
        // Lazy load the version attribute if it exists
        if(null == getVersion()) {
            // No version exists - throw IAE
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                "metamodel_identifiable_no_version_attribute_present", 
                new Object[] { this }));
        } else {
            // Verify the type (Note: ClassLoaders do not have to be the same)
            // Relax restriction on null and Object.class (from same classLoader)
            if(null == type || Object.class == type || 
                    ((type != null) && (type.getCanonicalName().equals(versionAttribute.getJavaType().getCanonicalName())))) {
                return (SingularAttribute<? super X, Y>)versionAttribute;
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_identifiable_version_attribute_type_incorrect", 
                    new Object[] { versionAttribute, this, type, versionAttribute.getJavaType()}));
            }
        }
    }

    /**
     * INTERNAL:
     * Return the version attribute on this type.
     * If no version attribute exists - return null.
     * @param <Y>
     * @return
     */
    private <Y> SingularAttribute<? super X, ?> getVersion() {
        if(hasVersionAttribute()) {
            return versionAttribute;
        } else {
            return null;
        }
    }
    
    /**
     *  Whether or not the identifiable type has an id attribute.
     *  Returns true for a simple id or embedded id; returns false
     *  for an idclass.
     *  @return boolean indicating whether or not the identifiable
     *           type has a single id attribute
     */
    public boolean hasSingleIdAttribute() {
        // The following section will return false for any multiple EmbeddableId as well as multiple Ids as part of an IdClass
        // Note: there will always be at least 1 Id for an IdentifiableType

        /**
         * Since we are in IdentifiableType which involves only Entities and MappedSuperclasses,
         * we are safe to assume that there will always be an Id of some sort - we are not in 
         * Basic, Embeddable or transient types.
         * Check the core API project for any IdClass matching this IdentifiableType
         * after we check directly on the descriptor for an Id.
         *  References: SubQueryImpl.select()
         */
        
        // Note: this function will return false only if an IdClass is present
        List<DatabaseField> pkFields = this.getDescriptor().getPrimaryKeyFields();
        // return false for no Id field types
        if(pkFields.isEmpty()) {
            return false;
        } else {
            // Optional: Verify the mapping on the each field and whether it is an IdClass
            Class pkClass = null;
            if(this.getDescriptor().hasCMPPolicy()) {
                pkClass = ((CMP3Policy)this.getDescriptor().getCMPPolicy()).getPKClass();
                if(null == pkClass) {
                    return false;
                }
            } else {
                // MappedSuperclass descriptors do not have a CMP policy yet because the are not initialized
                return pkFields.size() < 2;
            }
            
            // 288792: Search the values of the list of IdClass attribute names stored on the project
            for (List<String> idClassNamesList : getMetamodel().getProject().getMetamodelIdClassMap().values()) {
                for(String idClassName : idClassNamesList) {
                    if(idClassName.equals(pkClass.getCanonicalName())) {                                        
                        return false;
                    }
                }                            
            }
        }
        return true;        
    }
    
    /**
     *  Whether or not the identifiable type has a version attribute.
     *  @return boolean indicating whether or not the identifiable
     *           type has a version attribute
     */
    public boolean hasVersionAttribute() {
        // The versionAttribute is lazy loaded
        if(null != versionAttribute) {
            return true;
        } else {
            for(Attribute attribute : this.getAttributes()) {
                if(!((AttributeImpl)attribute).isPlural() && ((SingularAttribute)attribute).isVersion()) {
                    versionAttribute = (SingularAttribute)attribute;
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * INTERNAL:
     * Return whether this type is identifiable.
     * This would be EntityType and MappedSuperclassType
     * @return
     */
    @Override
    protected boolean isIdentifiableType() {
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
