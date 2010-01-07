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
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *               http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.internal.dynamic;

//javase imports
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//EclipseLink imports
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * Simple {@link InstantiationPolicy} to call
 * {@link DynamicTypeImpl#newDynamicEntity()}
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public class DynamicTypeInstantiationPolicy extends InstantiationPolicy {

    private DynamicTypeImpl type;

    /*
     * Needed since InstantiationPolicy.defaultConstructor is not accessible 
     */
    private transient Constructor<?> constructor = null;

    public DynamicTypeInstantiationPolicy(DynamicTypeImpl type) {
        this.type = type;
        this.descriptor = type.getDescriptor();
    }

    public DynamicTypeImpl getType() {
        return this.type;
    }

    /**
     * Lookup the constructor.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        try {
            this.constructor = PrivilegedAccessHelper.getDeclaredConstructorFor(getType().getJavaClass(), new Class[] { DynamicTypeImpl.class }, true);
            this.constructor.setAccessible(true);
        } catch (NoSuchMethodException exception) {
            throw DescriptorException.noSuchMethodWhileInitializingInstantiationPolicy(getType().getName() + ".<Default Constructor>", getDescriptor(), exception);
        }
    }

    /**
     * Using privileged reflection create a new instance of the dynamic type
     * passing in this {@link DynamicTypeImpl} so the dynamic entity can have a
     * reference to its type. After creation initialize all required attributes.
     * 
     * @see DynamicTypeInstantiationPolicy
     * @return new DynamicEntity with initialized attributes
     */
    /*
     * It would be easier to subclass InstantiationPolicy if its build methods
     * accepted an Object[] of args or requested the args from the policy. This
     * would avoid the large block of code below required to pass the type into
     * the constructor. Enhancement 288918 filed
     */
    @Override
    public Object buildNewInstance() {
        DynamicEntityImpl entity = null;

        try {
            entity = (DynamicEntityImpl) PrivilegedAccessHelper.invokeConstructor(this.getTypeConstructor(), new Object[] { getType() });
        } catch (InvocationTargetException exception) {
            throw DescriptorException.targetInvocationWhileConstructorInstantiation(this.getDescriptor(), exception);
        } catch (IllegalAccessException exception) {
            throw DescriptorException.illegalAccessWhileConstructorInstantiation(this.getDescriptor(), exception);
        } catch (InstantiationException exception) {
            throw DescriptorException.instantiationWhileConstructorInstantiation(this.getDescriptor(), exception);
        } catch (NoSuchMethodError exception) {
            // This exception is not documented but gets thrown.
            throw DescriptorException.noSuchMethodWhileConstructorInstantiation(this.getDescriptor(), exception);
        } catch (NullPointerException exception) {
            // Some JVMs will throw a NULL pointer exception here
            throw DescriptorException.nullPointerWhileConstructorInstantiation(this.getDescriptor(), exception);
        }

        for (DatabaseMapping mapping : getType().getMappingsRequiringInitialization()) {
            initializeValue(mapping, entity);
        }

        return entity;
    }

    /**
     * Initialize the default value handling primitives, collections and
     * indirection.
     * 
     * @param mapping
     * @param entity
     */
    private void initializeValue(DatabaseMapping mapping, DynamicEntityImpl entity) {
        Object value = null;

        if (mapping.isDirectToFieldMapping() && mapping.getAttributeClassification().isPrimitive()) {
            Class<?> primClass = mapping.getAttributeClassification();

            if (primClass == ClassConstants.PBOOLEAN) {
                value = false;
            } else if (primClass == ClassConstants.PINT) {
                value = 0;
            } else if (primClass == ClassConstants.PLONG) {
                value = 0L;
            } else if (primClass == ClassConstants.PCHAR) {
                value = Character.MIN_VALUE;
            } else if (primClass == ClassConstants.PDOUBLE) {
                value = 0.0d;
            } else if (primClass == ClassConstants.PFLOAT) {
                value = 0.0f;
            } else if (primClass == ClassConstants.PSHORT) {
                value = Short.MIN_VALUE;
            } else if (primClass == ClassConstants.PBYTE) {
                value = Byte.MIN_VALUE;
            }
        } else if (mapping.isForeignReferenceMapping()) {
            ForeignReferenceMapping refMapping = (ForeignReferenceMapping) mapping;

            if (refMapping.usesIndirection() && refMapping.getIndirectionPolicy() instanceof BasicIndirectionPolicy) {
                value = new ValueHolder(value);
            } else if (refMapping.isCollectionMapping()) {
                value = ((CollectionMapping) refMapping).getContainerPolicy().containerInstance();
            }
        } else if (mapping.isAggregateObjectMapping()) {
            value = mapping.getReferenceDescriptor().getObjectBuilder().buildNewInstance();
        }

        mapping.setAttributeValueInObject(entity, value);
    }

    /**
     * Return the default (zero-argument) constructor for the descriptor class.
     */
    protected Constructor<?> getTypeConstructor() throws DescriptorException {
        if (this.constructor == null) {
            initialize(null);
        }
        return this.constructor;
    }

}
