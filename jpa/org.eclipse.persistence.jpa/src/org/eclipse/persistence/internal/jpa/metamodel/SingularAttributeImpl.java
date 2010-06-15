/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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
 * @since EclipseLink 1.2 - JPA 2.0
 *
 * @param <X> The type containing the represented attribute
 * @param <T> The type of the represented attribute
 * 
 */
public class SingularAttributeImpl<X, T> extends AttributeImpl<X, T> implements SingularAttribute<X, T> {

    /** Item 54: DI 89: explicit UID will avoid performance hit runtime generation of one */
    private static final long serialVersionUID = 3928292425281232234L;
    
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
        // Case: Handle primitive or java lang type (non-Entity) targets
        Class attributeClass = mapping.getAttributeClassification();
        /**
         * Case: Handle Entity targets
         * Process supported mappings by assigning their elementType.
         * For unsupported mappings we default to MetamodelImpl.DEFAULT_ELEMENT_TYPE.
         * If attribute is a primitive type (non-null) - we will wrap it in a BasicType automatically in getType below
         * The attribute classification is null for non-collection mappings such as embeddable keys.
         */
        if (null == attributeClass) {
            
            // We support @OneToOne but not EIS, Reference or VariableOneToOne
            // Note: OneToMany, ManyToMany are handled by PluralAttributeImpl
            if(mapping.isOneToOneMapping()) { // handles @ManyToOne
                attributeClass = ((OneToOneMapping)mapping).getReferenceClass();
            } else if (mapping.isDirectToFieldMapping()) { // Also handles the keys of an EmbeddedId
                attributeClass = mapping.getField().getType();
                if(null == attributeClass) {
                    // lookup the attribute on the containing class
                    attributeClass = managedType.getTypeClassFromAttributeOrMethodLevelAccessor(mapping);
                }
            } else if (mapping.isAggregateObjectMapping()) { // IE: EmbeddedId
                attributeClass = ((AggregateMapping)mapping).getReferenceClass();
            } else if (mapping.isVariableOneToOneMapping()) { // interfaces are unsupported in the JPA 2.0 spec for the Metamodel API
                if(validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_mapping_type_is_unsupported", mapping, this);                    
                }
                // see JUnitCriteriaUnitTestSuite.testSelectPhoneNumberAreaCode() line: 246
                // VariableOneToOne mappings are unsupported - default to referenceClass (Interface) anyway
                // see interface org.eclipse.persistence.testing.models.jpa.relationships.Distributor
                attributeClass = ((VariableOneToOneMapping)mapping).getReferenceClass();
            } else if (mapping.isEISMapping() || mapping.isTransformationMapping()) { // unsupported in the JPA 2.0 spec for the Metamodel API
                if(validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_mapping_type_is_unsupported", mapping, this);                    
                }
            } else if ( mapping.isReferenceMapping()) { // unsupported in the JPA 2.0 spec for the Metamodel API
                if(validationEnabled) {
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_mapping_type_is_unsupported", mapping, this);                    
                }
                // Reference mappings are unsupported - default to referenceClass anyway
                attributeClass = ((ReferenceMapping)mapping).getReferenceClass();
            }
        }
        // All unsupported mappings such as TransformationMapping
        if(null == attributeClass && validationEnabled) {
            // TODO: refactor
            attributeClass = MetamodelImpl.DEFAULT_ELEMENT_TYPE_FOR_UNSUPPORTED_MAPPINGS;
            AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);                    
        }        
        elementType = getMetamodel().getType(attributeClass);
    }

    /**
     * Return the Java type of the represented object.
     * If the bindable type of the object is <code>PLURAL_ATTRIBUTE</code>,
     * the Java element type is returned. If the bindable type is
     * <code>SINGULAR_ATTRIBUTE</code> or <code>ENTITY_TYPE</code>, 
     * the Java type of the
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
        if(this.getManagedTypeImpl().isMappedSuperclass()) {
            // The field on the mapping is the same field in the pkFields list on the descriptor
            // 288792: We can use the new isJPAId field here 
            return (this.getDescriptor().getPrimaryKeyFields().contains(this.getMapping().getField()));
        } else {
            // 288792: Some id mappings will return false for isPrimaryKeyMapping but true for isJPAId
            return getMapping().isPrimaryKeyMapping() || getMapping().isJPAId();
        }
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
            Class aJavaType = getMapping().getAttributeClassification();
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
                        if(null == aJavaType) {
                            AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_attribute_class_type_is_null", this);
                            return MetamodelImpl.DEFAULT_ELEMENT_TYPE_FOR_UNSUPPORTED_MAPPINGS;
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
        StringBuffer aBuffer = new StringBuffer("SingularAttributeImpl[");
        aBuffer.append(getType());
        aBuffer.append(",");
        aBuffer.append(getMapping());
        aBuffer.append("]");
        return aBuffer.toString();
    }
}
