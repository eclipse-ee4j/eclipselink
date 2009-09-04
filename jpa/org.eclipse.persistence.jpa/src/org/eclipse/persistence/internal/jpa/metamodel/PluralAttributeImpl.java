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
 *     03/19/2009-2.0  dclarke  - initial API start    
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 *    08/19/2009-2.0  mobrien - Extend Collection support
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_59:_20090818:_PluralAttribute.elementType_not_set_for_non-lazy_instantiated_Collection_Attribute
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the PluralAttribute interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * Instances of the type PluralAttribute represent 
 * persistent collection-valued attributes.
 * 
 * @see javax.persistence.metamodel.PluralAttribute
 * 
 * @since EclipseLink 1.2 - JPA 2.0
 * 
 * @param <X> The type the represented collection belongs to
 * @param <C> The type of the represented collection
 * @param <V> The element type of the represented collection
 *  
 */ 
public abstract class PluralAttributeImpl<X, C, V> extends AttributeImpl<X, C> implements PluralAttribute<X, C, V> {
    
    /** The type representing this collection type **/
    protected Type<V> elementType;
    

    /**
     * INTERNAL:
     * Create an Attribute instance with a passed in validation flag (usually set to true only during Metamodel initialization)
     * @param managedType
     * @param mapping
     * @param validationEnabled
     */
    protected PluralAttributeImpl(ManagedTypeImpl<X> managedType, CollectionMapping mapping, boolean validationEnabled) {
        super(managedType, mapping);        
        // 1) Check the mapping type first        
        // 2) Get the type set on the policy        
        ClassDescriptor elementDesc = mapping.getContainerPolicy().getElementDescriptor();

        // Set the element type on this attribute
        if (elementDesc != null) {
            // Mappings: OneToMany, UnidirectionalOneToMany, ManyToMany
            this.elementType = (Type<V>)getMetamodel().getType(elementDesc.getJavaClass());
        } else {
            // See CollectionContainerPolicy
            Class attributeClass = null;
            // TODO: handle AggregateCollectionMapping
            if(mapping.isDirectCollectionMapping() || mapping.isAbstractCompositeDirectCollectionMapping()) {// || mapping.isAbstractDirectMapping() ) {
                //CollectionContainerPolicy policy = (CollectionContainerPolicy) mapping.getContainerPolicy();
                //this.elementType = getMetamodel().getType(policy.getElementDescriptor().getJavaClass());                
                if(mapping.isDirectCollectionMapping()) {
                    attributeClass = ((DirectCollectionMapping)mapping).getDirectField().getType();                    
                }
                /**
                 * TODO: REFACTOR
                 * We were unable to get the type because it is not declared as a generic parameter on the method level attribute.
                 * It may be declared on the field.
                 * See design issue 65 - we need a way to parse the signature of the declaration on the java class - if it exists.
                 * http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_65:_20090827:_Handle_DirectCollection_elementType_retrieval_in_the_absence_of_a_generic_type
                 * Example:
                 *  
                 * Collection<String> basicCollection; 
                 *  @BasicCollection
                 *  Collection getBasicCollection()
                 */
                // Note: a call to managedType.getTypeClassFromAttributeOrMethodLevelAccesso will only return the class of the collection on the method accessor
                if(null == attributeClass) {
                    //attributeClass = managedType.getTypeClassFromAttributeOrMethodLevelAccessor(mapping);
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_unable_to_determine_element_type_in_absence_of_generic_parameters", this);
                }
            } else if(mapping.isMapKeyMapping()) {
                    // TODO: Handle DirectMapContainerPolicy                    
                    MapContainerPolicy policy = (MapContainerPolicy) mapping.getContainerPolicy();
                    attributeClass = policy.getElementClass();
            } else if(mapping.isManyToManyMapping() || mapping.isOneToManyMapping()) {
                // Example: Collection with an instantiated Set/List
                attributeClass = ((CollectionMapping)mapping).getReferenceClass();
            } else if(mapping.isAggregateCollectionMapping()) {
                // get reference class and check if managedType is a MappedSuperclass
                attributeClass = ((AggregateCollectionMapping)mapping).getReferenceClass();
            }

            // TODO: refactor exception handling
            if(null == attributeClass && validationEnabled) {
                attributeClass = Object.class;
                AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);                    
            }
            this.elementType = (Type<V>)getMetamodel().getType(attributeClass);            
        }
    }

    /**
     * Return the Java type of the represented object.
     * If the bindable type of the object is PLURAL_ATTRIBUTE,
     * the Java element type is returned. If the bindable type is
     * SINGULAR_ATTRIBUTE or ENTITY_TYPE, the Java type of the
     * represented entity or attribute is returned.
     * @return Java type
     */
    public Class<V> getBindableJavaType() {
        // For PLURAL_ATTRIBUTE - return the java element type
        return elementType.getJavaType();
    }
    
    public BindableType getBindableType() {
    	return Bindable.BindableType.PLURAL_ATTRIBUTE;
    }
    
    public CollectionMapping getCollectionMapping() {
        return (CollectionMapping) getMapping();
    }

    /**
     * Return the collection type.
     * @return collection type
     */
    public abstract CollectionType getCollectionType();

    /**
     * Return the type representing the element type of the 
     * collection.
     * @return element type
     */
    public Type<V> getElementType() {
        return this.elementType;
    }
    
    /**
     *  Return the Java type of the represented attribute.
     *  @return Java type
     */
    @Override
    public Class<C> getJavaType() {
        return (Class<C>)elementType.getJavaType();
    }
    
    @Override
    public boolean isPlural() {
        return true;
    }

}
