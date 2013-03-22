/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.descriptors;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.internal.security.*;

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
    public Class getAttributeClass() {
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
    public Class getAttributeType() {
        return attributeField.getType();
    }

    /**
     * Returns the value of the attribute on the specified object.
     */
    public Object getAttributeValueFromObject(Object anObject) throws DescriptorException {
        try {
            // PERF: Direct variable access.
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedGetValueFromField(this.attributeField, anObject));
                } catch (PrivilegedActionException exception) {
                    throw DescriptorException.illegalAccesstWhileGettingValueThruInstanceVaraibleAccessor(getAttributeName(), anObject.getClass().getName(), exception.getException());
                }
            } else {
                // PERF: Direct-var access.
                return this.attributeField.get(anObject);
            }
        } catch (IllegalArgumentException exception) {
            throw DescriptorException.illegalArgumentWhileGettingValueThruInstanceVariableAccessor(getAttributeName(), getAttributeType().getName(), anObject.getClass().getName(), exception);
        } catch (IllegalAccessException exception) {
            throw DescriptorException.illegalAccesstWhileGettingValueThruInstanceVaraibleAccessor(getAttributeName(), anObject.getClass().getName(), exception);
        } catch (NullPointerException exception) {
            String className = null;
            if (anObject != null) {
                // Some JVM's throw this exception for some very odd reason
                className = anObject.getClass().getName();
            }
            throw DescriptorException.nullPointerWhileGettingValueThruInstanceVariableAccessor(getAttributeName(), className, exception);
        }
    }

    /**
     * instanceVariableName is converted to Field type.
     */
    public void initializeAttributes(Class theJavaClass) throws DescriptorException {
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
    public boolean isInitialized(){
        return this.attributeField !=  null;
    }
    
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
    public void setAttributeValueInObject(Object anObject, Object value) throws DescriptorException {
         try {
            // PERF: Direct variable access.
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    AccessController.doPrivileged(new PrivilegedSetValueInField(this.attributeField, anObject, value));
                } catch (PrivilegedActionException exception) {
                    throw DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), value, exception.getException());
                }
            } else {
                // PERF: Direct-var access.
                this.attributeField.set(anObject, value);
            }
        } catch (IllegalArgumentException exception) {
            // This is done to overcome VA Java bug because VA Java does not allow null to be set reflectively.
            try {
                // This is done to overcome VA Java bug because VA Java does not allow null to be set reflectively.
                // Bug2910086 In JDK1.4, IllegalArgumentException is thrown if value is null.
                // TODO: This code should be removed, it should not be required and may cause unwanted side-effects.
                if (value == null) {
                    // cr 3737  If a null pointer was thrown because we attempted to set a null reference into a
                    // primitive create a primitive of value 0 to set in the object.
                    Class fieldClass = getAttributeClass();
                    if (org.eclipse.persistence.internal.helper.Helper.isPrimitiveWrapper(fieldClass)) {
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                            try {
                                AccessController.doPrivileged(new PrivilegedSetValueInField(this.attributeField, anObject, ConversionManager.getDefaultManager().convertObject(Integer.valueOf(0), fieldClass)));
                            } catch (PrivilegedActionException exc) {
                                throw DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), value, exc.getException());
                                                        }
                        } else {
                            org.eclipse.persistence.internal.security.PrivilegedAccessHelper.setValueInField(this.attributeField, anObject, ConversionManager.getDefaultManager().convertObject(Integer.valueOf(0), fieldClass));
                        }
                    }
                    return;
                }
            } catch (IllegalAccessException accessException) {
                throw DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), value, exception);
            }

            // TODO: This code should be removed, it should not be required and may cause unwanted side-effects.
            // Allow XML change set to merge correctly since new value in XML change set is always String
            try {
                if (value instanceof String) {
                    Object newValue = ConversionManager.getDefaultManager().convertObject(value, getAttributeClass());
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try {
                            AccessController.doPrivileged(new PrivilegedSetValueInField(this.attributeField, anObject, newValue));
                        } catch (PrivilegedActionException exc) {
                        }
                    } else {
                        org.eclipse.persistence.internal.security.PrivilegedAccessHelper.setValueInField(this.attributeField, anObject, newValue);
                    }
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
            try {
                // TODO: This code should be removed, it should not be required and may cause unwanted side-effects.
                //Bug2910086 In JDK1.3, NullPointerException is thrown if value is null.  Add a null pointer check so that the TopLink exception is thrown if anObject is null.
                if (anObject != null) {
                    // cr 3737  If a null pointer was thrown because we attempted to set a null reference into a
                    // primitive create a primitive of value 0 to set in the object.
                    Class fieldClass = getAttributeClass();
                    if (org.eclipse.persistence.internal.helper.Helper.isPrimitiveWrapper(fieldClass) && (value == null)) {
                        if (org.eclipse.persistence.internal.helper.Helper.isPrimitiveWrapper(fieldClass)) {
                            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                                try {
                                    AccessController.doPrivileged(new PrivilegedSetValueInField(this.attributeField, anObject, ConversionManager.getDefaultManager().convertObject(Integer.valueOf(0), fieldClass)));
                                } catch (PrivilegedActionException exc) {
                                    throw DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), value, exc.getException());
                                }
                            } else {
                                org.eclipse.persistence.internal.security.PrivilegedAccessHelper.setValueInField(this.attributeField, anObject, ConversionManager.getDefaultManager().convertObject(Integer.valueOf(0), fieldClass));
                            }
                        }
                    } else {
                        throw DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), value, exception);
                    }
                } else {
                    // Some JVM's throw this exception for some very odd reason
                    throw DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), value, exception);
                }
            } catch (IllegalAccessException accessException) {
                throw DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(getAttributeName(), value, exception);
            }
        }
    }
}
