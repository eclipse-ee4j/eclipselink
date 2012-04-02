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
 *     29/10/2009-2.0  mobrien - m:1 and 1:m relationships require special handling
 *       in their internal m:m and 1:1 database mapping representations.
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_96:_20091019:_Attribute.getPersistentAttributeType.28.29_treats_ManyToOne_the_same_as_OneToOne
 *     16/06/2010-2.2  mobrien - 316991: Attribute.getJavaMember() requires reflective getMethod call
 *       when only getMethodName is available on accessor for attributes of Embeddable types.
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_95:_20091017:_Attribute.getJavaMember.28.29_returns_null_for_a_BasicType_on_a_MappedSuperclass_because_of_an_uninitialized_accessor
 *     09/08/2010-2.2  mobrien - 322166: If attribute is defined on a lower level MappedSuperclass (and not on a superclass) 
 *       - do not attempt a reflective call on a superclass  
 *       - see design issue #25
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_25:_20090616:_Inherited_parameterized_generics_for_Element_Collections_.28Basic.29
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredField;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredMethod;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Attribute interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>:
 * An attribute of a Java type  
 * 
 * @see javax.persistence.metamodel.Attribute
 * 
 * @since EclipseLink 1.2 - JPA 2.0
 * @param <X> The represented type that contains the attribute
 * @param <T> The type of the represented attribute
 *  
 */ 
public abstract class AttributeImpl<X, T> implements Attribute<X, T>, Serializable {

    /** The ManagedType associated with this attribute **/
    private ManagedTypeImpl<X> managedType;

    /** The databaseMapping associated with this attribute **/
    private DatabaseMapping mapping;
    
    /**
     * INTERNAL:
     * 
     * @param managedType
     * @param mapping
     */
    protected AttributeImpl(ManagedTypeImpl<X> managedType, DatabaseMapping mapping) {
        this.mapping = mapping;
        // Cache this Attribute on the mapping
        this.mapping.setProperty(getClass().getName(), this);
        this.managedType = managedType;
    }

    /**
     *  Return the managed type representing the type in which 
     *  the attribute was declared.
     *  @return declaring type
     */
    public ManagedType<X> getDeclaringType() {
        return getManagedTypeImpl();
    }

    /**
     * INTERNAL:
     * Return the Descriptor associated with this attribute 
     * @return
     */
    protected ClassDescriptor getDescriptor() {
        return getManagedTypeImpl().getDescriptor();
    }

    /**
     * Return the java.lang.reflect.Member for the represented attribute. 
     * In the case of property access the get method will be returned
     * 
     * @return corresponding java.lang.reflect.Member
     */
    public Member getJavaMember() {
        AttributeAccessor accessor = getMapping().getAttributeAccessor();
        if (accessor.isMethodAttributeAccessor()) {
            // Method level access here
            Method aMethod = ((MethodAttributeAccessor) accessor).getGetMethod();
            if(null == aMethod) {
                // 316991: If the getMethod is not set - use a reflective call via the getMethodName
                String getMethodName = null;
                try {
                    getMethodName = ((MethodAttributeAccessor)mapping.getAttributeAccessor()).getGetMethodName();   
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                        aMethod = (Method) AccessController.doPrivileged(new PrivilegedGetDeclaredMethod(
                                this.getManagedTypeImpl().getJavaType(), getMethodName, null));
                    } else {
                        aMethod = PrivilegedAccessHelper.getDeclaredMethod(
                                this.getManagedTypeImpl().getJavaType(),getMethodName, null);
                    } 
                    // Exceptions are to be ignored for reflective calls - if the methodName is also null - it will catch here
                } catch (PrivilegedActionException pae) {
                    //pae.printStackTrace();
                } catch (NoSuchMethodException nsfe) {
                    //nsfe.printStackTrace();
                }                
            }
            return aMethod;            
        }

        // Field level access here
        Member aMember = ((InstanceVariableAttributeAccessor) accessor).getAttributeField();
        // For primitive and basic types - we should not return null - the attributeAccessor on the MappedSuperclass is not initialized - see
        // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_95:_20091017:_Attribute.getJavaMember.28.29_returns_null_for_a_BasicType_on_a_MappedSuperclass_because_of_an_uninitialized_accessor
        // MappedSuperclasses need special handling to get their type from an inheriting subclass
        // Note: This code does not handle attribute overrides on any entity subclass tree - use descriptor initialization instead
        if(null == aMember) {
            if(this.getManagedTypeImpl().isMappedSuperclass()) {
                // get inheriting subtype member (without handling @override annotations)                
                AttributeImpl inheritingTypeMember = ((MappedSuperclassTypeImpl)this.getManagedTypeImpl())
                    .getMemberFromInheritingType(mapping.getAttributeName());
                // 322166: If attribute is defined on this current ManagedType (and not on a superclass) - do not attempt a reflective call on a superclass
                if(null != inheritingTypeMember) {
                    // Verify we have an attributeAccessor
                    aMember = ((InstanceVariableAttributeAccessor)inheritingTypeMember.getMapping()
                            .getAttributeAccessor()).getAttributeField();
                }
            }
            
            if(null == aMember) {
                // 316991: Handle Embeddable types
                // If field level access - perform a getDeclaredField call
                if(null == aMember) {
                    // Field level access
                    // Check declaredFields in the case where we have no getMethod or getMethodName
                    try {
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                            aMember = (Field)AccessController.doPrivileged(new PrivilegedGetDeclaredField(
                                this.getManagedTypeImpl().getJavaType(), mapping.getAttributeName(), false));
                        } else {
                            aMember = PrivilegedAccessHelper.getDeclaredField(
                                this.getManagedTypeImpl().getJavaType(), mapping.getAttributeName(), false);
                        }                                        
                        // Exceptions are to be ignored for reflective calls - if the methodName is also null - it will catch here
                    } catch (PrivilegedActionException pae) {
                        //pae.printStackTrace();
                    } catch (NoSuchFieldException nsfe) {
                        //nsfe.printStackTrace();
                    }                
                }
            }
        }
        
        // 303063: secondary check for attribute override case - this will show on code coverage
        if(null == aMember) {
            AbstractSessionLog.getLog().log(SessionLog.FINEST, AbstractSessionLog.METAMODEL, "metamodel_attribute_getmember_is_null", this, this.getManagedTypeImpl(), this.getDescriptor());
        }
        
        return aMember;
    }

    /**
     *  Return the Java type of the represented attribute.
     *  @return Java type
     */
    public abstract Class<T> getJavaType();

    /**
     * INTERNAL:
     * Return the managed type representing the type in which the member was
     * declared.
     * @return
     */
    public ManagedTypeImpl<X> getManagedTypeImpl() {
        return this.managedType;
    }

    /**
     * INTERNAL:
     * Return the databaseMapping that represents the type
     * @return
     */
    public DatabaseMapping getMapping() {
        return this.mapping;
    }
    
    /**
     * INTERNAL: 
     * Return the concrete metamodel that this attribute is associated with.
     * @return MetamodelImpl
     */
    protected MetamodelImpl getMetamodel() {
        return this.managedType.getMetamodel();
    }
    
    /**
     * Return the name of the attribute.
     * @return name
     */
    public String getName() {
        return this.getMapping().getAttributeName();
    }

    /**
     *  Return the persistent attribute type for the attribute.
     *  @return persistent attribute type
     */
    public Attribute.PersistentAttributeType getPersistentAttributeType() {
        /**
         * process the following mappings by referencing the Core API Mapping.
         * MANY_TO_ONE (ONE_TO_ONE internally)
         * ONE_TO_ONE (May originally be a MANY_TO_ONE)
         * BASIC
         * EMBEDDED
         * MANY_TO_MANY (May originally be a unidirectional ONE_TO_MANY on a mappedSuperclass)
         * ONE_TO_MANY (MANY_TO_MANY internally for unidirectional mappings on MappedSuperclasses)
         * ELEMENT_COLLECTION
         */ 
        if (mapping.isAbstractDirectMapping()) {
            return PersistentAttributeType.BASIC;
        }
        if (mapping.isAggregateObjectMapping()) {
            return PersistentAttributeType.EMBEDDED;
        }
        if (mapping.isOneToManyMapping()) {
            return PersistentAttributeType.ONE_TO_MANY;
        }

        /**
         * EclipseLink internally processes a ONE_TO_MANY on a MappedSuperclass as a MANY_TO_MANY
         * because the relationship is unidirectional.
         */
        if (mapping.isManyToManyMapping()) {
            // Check for a OneToMany on a MappedSuperclass being processed internally as a ManyToMany
            if(((ManyToManyMapping)mapping).isDefinedAsOneToManyMapping()) {
                return PersistentAttributeType.ONE_TO_MANY;
            } else {
                // Test coverage required
                return PersistentAttributeType.MANY_TO_MANY;
            }
        }

        if (mapping.isManyToOneMapping()) {
            return PersistentAttributeType.MANY_TO_ONE;
        }
        if (mapping.isOneToOneMapping()) {
            return PersistentAttributeType.ONE_TO_ONE;
        }
        // Test coverage required
        return PersistentAttributeType.ELEMENT_COLLECTION;
    }
    
    /**
     *  Is the attribute an association.
     *  @return whether boolean indicating whether attribute 
     *          corresponds to an association
     */
    public boolean isAssociation() {
        // If the mapping a relationship to another entity.
        return getMapping().isForeignReferenceMapping() && !getMapping().isDirectCollectionMapping()
            && !getMapping().isAggregateCollectionMapping();
    }

    /**
     *  Is the attribute collection-valued.
     *  @return boolean indicating whether attribute is 
     *          collection-valued.<p>
     *  This will be true for the mappings CollectionMapping, AbstractCompositeCollectionMapping, 
     *  AbstractCompositeDirectCollectionMapping and their subclasses         
     *          
     */
    public boolean isCollection() {        
        return getMapping().isCollectionMapping();
    }
    
    /**
     * INTERNAL:
     * Return whether the attribute is plural or singular
     * @return
     */
    public abstract boolean isPlural();
}
