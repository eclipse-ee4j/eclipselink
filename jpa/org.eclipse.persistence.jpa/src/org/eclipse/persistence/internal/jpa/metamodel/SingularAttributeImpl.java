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
 *     05/26/2009-2.0  mobrien - API update
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.lang.reflect.Field;

import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.mappings.structures.ReferenceMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the SingularAttribute interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * Instances of the type SingularAttribute represents persistent 
 * single-valued properties or fields.
 *
 * @author Michael O'Brien
 * @see javax.persistence.metamodel.SingularAttribute
 * @since EclipseLink 2.0 - JPA 2.0
 *
 * @param <X> The type containing the represented attribute
 * @param <T> The type of the represented attribute
 * 
 */
public class SingularAttributeImpl<X, T> extends AttributeImpl<X, T> implements SingularAttribute<X, T> {

    /** The Type representing this Entity or Basic type **/
    protected Type<T> elementType;

    /**
     * Create an instance of the Attribute
     * @param managedType
     * @param mapping
     */
    protected SingularAttributeImpl(ManagedTypeImpl<X> managedType, DatabaseMapping mapping) {
        this(managedType, mapping, false);
    }
    
    /**
     * INTERNAL:
     * Create an Attribute instance with a passed in validation flag (usually set to true only during Metamodel initialization)
     * @param managedType
     * @param mapping
     * @param validationEnabled
     */
    protected SingularAttributeImpl(ManagedTypeImpl<X> managedType, DatabaseMapping mapping, boolean validationEnabled) {
        super(managedType, mapping);
        Class attributeClass = mapping.getAttributeClassification();

        // The attribute classification is null for non-collection mappings
        if (null == attributeClass) { // BasicType will != null --> else clause
            // EntityType
            // We support @OneToOne but not EIS, Reference or VariableOneToOne
            if(mapping.isOneToOneMapping()) {
                attributeClass = ((OneToOneMapping)mapping).getReferenceClass();
                if(null == attributeClass && validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);                    
                }
            } else if (mapping.isOneToManyMapping()) {
                // TODO: verify
                attributeClass = ((OneToManyMapping)mapping).getReferenceClass();
                if(null == attributeClass && validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);                    
                }
            } else if (mapping.isManyToManyMapping()) {
                // TODO: verify
                attributeClass = ((ManyToManyMapping)mapping).getReferenceClass();
                if(null == attributeClass && validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);                    
                }
            } else if (mapping.isAggregateObjectMapping()) { // IE: EmbeddedId
                attributeClass = ((AggregateMapping)mapping).getReferenceClass();
                if(null == attributeClass && validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);            
                }                
            } else if (mapping.isVariableOneToOneMapping()) { // interfaces are unsupported in the JPA 2.0 spec for the Metamodel API
                if(validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_mapping_type_is_unsupported", mapping, this);                    
                }
                // see JUnitCriteriaUnitTestSuite.testSelectPhoneNumberAreaCode() line: 246
                // VariableOneToOne mappings are unsupported - default to referenceClass  (Interface) anyway
                attributeClass = ((VariableOneToOneMapping)mapping).getReferenceClass();
                if(null == attributeClass && validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);            
                }                
                
            } else if (mapping.isEISMapping()) { // unsupported in the JPA 2.0 spec for the Metamodel API
                if(validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_mapping_type_is_unsupported", mapping, this);                    
                }
                // TODO: refactor
                // VariableOneToOne mappings are unsupported - default to Object:
                attributeClass = Object.class;
            } else if ( mapping.isReferenceMapping()) { // unsupported in the JPA 2.0 spec for the Metamodel API
                if(validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_mapping_type_is_unsupported", mapping, this);                    
                }
                // VariableOneToOne mappings are unsupported - default to referenceClass anyway
                attributeClass = ((ReferenceMapping)mapping).getReferenceClass();
                if(null == attributeClass && validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);            
                }                
            } else if (mapping.isDirectToFieldMapping()) { // Also handles the keys of an EmbeddedId
                attributeClass = mapping.getField().getType();
                if(null == attributeClass) {
                    // lookup the attribute on the containing class                    
                    Class containingClass = mapping.getDescriptor().getJavaClass();
                    Field aField = null;
                    try {
                        aField = containingClass.getDeclaredField(mapping.getAttributeName());
                        attributeClass = aField.getType();
                    } catch (NoSuchFieldException nsfe) {
                        // This exception will be warned about below
                        //nsfe.printStackTrace();
                    }                    
                }
                // all Direct mappings that don't have a type on their field
                if(null == attributeClass && validationEnabled) {
                    // TODO: refactor
                    attributeClass = Object.class;
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);                    
                }
            } else {
                // All unsupported mappings
                if(null == attributeClass && validationEnabled) {
                    // TODO: refactor
                    attributeClass = Object.class;
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);                    
                }
            }
        }
        elementType = (Type<T>)getMetamodel().getType(attributeClass);        
    }

    /**
     * Return the Java type of the represented object.
     * If the bindable type of the object is PLURAL_ATTRIBUTE,
     * the Java element type is returned. If the bindable type is
     * SINGULAR_ATTRIBUTE or ENTITY_TYPE, the Java type of the
     * represented entity or attribute is returned.
     * @return Java type
     */
    public Class<T> getBindableJavaType() {
        // In SingularAttribute our BindableType is SINGLE_ATTRIBUTE - return the java type of the represented entity
        return this.elementType.getJavaType();
    }
    
    /**
     *  Is the attribute an id attribute.
     *  @return boolean indicating whether or not attribute is an id
     */
    public boolean isId() {
        return getDescriptor().getObjectBuilder().getPrimaryKeyMappings().contains(getMapping());
    }

    /** 
     *  Can the attribute be null.
     *  @return boolean indicating whether or not the attribute can
     *          be null
     */
    public boolean isOptional() {
        return getMapping().isOptional();
    }

    /**
     * INTERNAL:
     * Return whether the attribute is plural or singular
     * @return
     */
    @Override
    public boolean isPlural() {
        return false;
    }
    
    /**
     *  Is the attribute a version attribute.
     *  @return boolean indicating whether or not attribute is 
     *          a version attribute
     */
    public boolean isVersion() {
        if (getDescriptor().usesOptimisticLocking() && getMapping().isDirectToFieldMapping()) {
            OptimisticLockingPolicy policy = getDescriptor().getOptimisticLockingPolicy();
            
            return policy.getWriteLockField().equals(((DirectToFieldMapping) getMapping()).getField());
        }
        return false;
    }

    public Bindable.BindableType getBindableType() {
    	return Bindable.BindableType.SINGULAR_ATTRIBUTE;
    }
    
    /**
     *  Return the Java type of the represented attribute.
     *  @return Java type
     */
    @Override
    public Class<T> getJavaType() {
        if(null == elementType) {
            Class aJavaType = getMapping().getAttributeClassification(); // returns null for OneToManyMapping
            if(null == aJavaType) {
                aJavaType = getMapping().getField().getType();
                if(null == aJavaType) {
                    // lookup the attribute on the containing class                    
                    Class containingClass = getMapping().getDescriptor().getJavaClass();
                    Field aField = null;
                    try {
                        aField = containingClass.getDeclaredField(getMapping().getAttributeName());
                        aJavaType = aField.getType();
                        return aJavaType;
                    } catch (NoSuchFieldException nsfe) {
                        // This exception will be warned about below
                        //nsfe.printStackTrace();
                        if(null == aJavaType) {
                            AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);
                            return (Class<T>)Object.class;
                        }
                    }                    
                }
            }
            return aJavaType;
        } else {
            return this.elementType.getJavaType();
        }
    }
    
    /**
     * Return the type that represents the type of the attribute.
     * @return type of attribute
     */
    public Type<T> getType() {
        return elementType;
    }
    
    /**
     * Return the String representation of the receiver.
     */
    @Override
    public String toString() {
        return "SingularAttributeImpl[" + getMapping() + "]";
    }
    
    
}
