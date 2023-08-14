/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.mappings.AttributeAccessor;

import java.lang.reflect.Field;

/**
 * <p><b>Purpose</b>: A wrapper class for handling cases when the domain object has instance variable
 * to map to the database field.
 *
 * @author Sati
 * @since TOPLink/Java 1.0
 */
public class InstanceVariableAttributeAccessor extends AttributeAccessor {

    /** The attribute name of an object is converted to Field type to access it reflectively */
    protected transient Field attributeField;

    /**
     * Returns the class type of the attribute.
     */
    @Override
    public Class<?> getAttributeClass() {
        if (getAttributeField() == null) {
            return null;
        }

        return getAttributeType();
    }

    /**
     * Returns the value of attributeField.
     * 266912: For Metamodel API - change visibility from protected
     */
    public Field getAttributeField() {
        return attributeField;
    }

    /**
     * Returns the declared type of attributeField.
     */
    public Class<?> getAttributeType() {
        return attributeField.getType();
    }

    /**
     * Returns the value of the attribute on the specified object.
     */
    @Override
    public Object getAttributeValueFromObject(Object anObject) throws DescriptorException {
        return PrivilegedAccessHelper.callDoPrivilegedWithException(
                () -> attributeField.get(anObject),
                (ex) -> {
                    if (ex instanceof IllegalArgumentException) {
                        return DescriptorException.illegalArgumentWhileGettingValueThruInstanceVariableAccessor(
                                getAttributeName(), getAttributeType().getName(), anObject.getClass().getName(), ex);
                    } else if (ex instanceof IllegalAccessException) {
                        return DescriptorException.illegalAccesstWhileGettingValueThruInstanceVaraibleAccessor(getAttributeName(), anObject.getClass().getName(), ex);
                    } else if (ex instanceof NullPointerException) {
                        final String className = anObject != null ? anObject.getClass().getName() : null;
                        return DescriptorException.nullPointerWhileGettingValueThruInstanceVariableAccessor(getAttributeName(), className, ex);
                    }
                    // This indicates unexpected problem in the code
                    throw new RuntimeException(String.format("Getting value from %s field failed", this.attributeField.getName()), ex);
                }
        );
    }

    /**
     * instanceVariableName is converted to Field type.
     */
    @Override
    public void initializeAttributes(Class<?> theJavaClass) throws DescriptorException {
        if (getAttributeName() == null) {
            throw DescriptorException.attributeNameNotSpecified();
        }
        try {
            setAttributeField(Helper.getField(theJavaClass, getAttributeName()));
        } catch (NoSuchFieldException exception) {
            throw DescriptorException.noSuchFieldWhileInitializingAttributesInInstanceVariableAccessor(getAttributeName(), theJavaClass.getName(), exception);
        } catch (SecurityException exception) {
            throw DescriptorException.securityWhileInitializingAttributesInInstanceVariableAccessor(getAttributeName(), theJavaClass.getName(), exception);
        }
    }

    /**
     * Returns true if this attribute accessor has been initialized and now stores a reference to the
     * class's attribute.  An attribute accessor can become uninitialized on serialization.
     */
    @Override
    public boolean isInitialized(){
        return this.attributeField !=  null;
    }

    @Override
    public boolean isInstanceVariableAttributeAccessor() {
        return true;
    }

    /**
     * Sets the value of the attributeField.
     */
    protected void setAttributeField(Field field) {
        attributeField = field;
    }

    /**
     * Sets the value of the instance variable in the object to the value.
     */
    @Override
    public void setAttributeValueInObject(final Object anObject, final Object value) throws DescriptorException {
        try {
            // PERF: Direct variable access.
            PrivilegedAccessHelper.callDoPrivilegedWithException(
                    () -> this.attributeField.set(anObject, value)
            );
        } catch (IllegalArgumentException exception) {
            // This is done to overcome VA Java bug because VA Java does not allow null to be set reflectively.
            // This is done to overcome VA Java bug because VA Java does not allow null to be set reflectively.
            // Bug2910086 In JDK1.4, IllegalArgumentException is thrown if value is null.
            // TODO: This code should be removed, it should not be required and may cause unwanted side-effects.
            if (value == null) {
                // cr 3737  If a null pointer was thrown because we attempted to set a null reference into a
                // primitive create a primitive of value 0 to set in the object.
                final Class<?> fieldClass = getAttributeClass();
                if (org.eclipse.persistence.internal.helper.Helper.isPrimitiveWrapper(fieldClass)) {
                    PrivilegedAccessHelper.callDoPrivilegedWithException(
                            () -> PrivilegedAccessHelper.setValueInField(this.attributeField, anObject, ConversionManager.getDefaultManager().convertObject(0, fieldClass)),
                            (ex) -> DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), null, ex)
                    );
                }
                return;
            }
            // TODO: This code should be removed, it should not be required and may cause unwanted side-effects.
            // Allow XML change set to merge correctly since new value in XML change set is always String
            try {
                if (value instanceof String) {
                    final Object newValue = ConversionManager.getDefaultManager().convertObject(value, getAttributeClass());
                    PrivilegedAccessHelper.callDoPrivilegedWithException(
                            () -> PrivilegedAccessHelper.setValueInField(this.attributeField, anObject, newValue)
                    );
                    return;
                }
            } catch (Exception e) {
                // Do nothing and move on to throw the original exception
            }
            throw DescriptorException.illegalArgumentWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), getAttributeType().getName(), value, exception);
        } catch (IllegalAccessException exception) {
            if (value == null) {
                return;
            }
            throw DescriptorException.illegalAccessWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), anObject.getClass().getName(), value, exception);
        } catch (NullPointerException exception) {
            // TODO: This code should be removed, it should not be required and may cause unwanted side-effects.
            //Bug2910086 In JDK1.3, NullPointerException is thrown if value is null.  Add a null pointer check so that the TopLink exception is thrown if anObject is null.
            if (anObject != null) {
                // cr 3737  If a null pointer was thrown because we attempted to set a null reference into a
                // primitive create a primitive of value 0 to set in the object.
                final Class<?> fieldClass = getAttributeClass();
                if (org.eclipse.persistence.internal.helper.Helper.isPrimitiveWrapper(fieldClass) && (value == null)) {
                    if (org.eclipse.persistence.internal.helper.Helper.isPrimitiveWrapper(fieldClass)) {
                        PrivilegedAccessHelper.callDoPrivilegedWithException(
                                () -> PrivilegedAccessHelper.setValueInField(this.attributeField, anObject, ConversionManager.getDefaultManager().convertObject(0, fieldClass)),
                                (ex) -> DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), null, ex)
                        );
                    }
                } else {
                    throw DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), value, exception);
                }
            } else {
                // Some JVM's throw this exception for some very odd reason
                throw DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), value, exception);
            }
        // This indicates unexpected problem in the code
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Setting value in %s field failed", this.attributeField.getName()), ex);
        }
    }
}
