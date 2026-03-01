/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.descriptors;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * <p><b>Purpose</b>:
 * Used to allow complex inheritance support.  Typically class indicators are used to define inheritance in the database,
 * however in complex cases the class type may be determined through another mechanism.
 * The method calls a static method on the descriptor class to determine the class to use for the database row.
 *
 * @see org.eclipse.persistence.descriptors.InheritancePolicy#setClassExtractor
 */
public class MethodClassExtractor extends ClassExtractor {
    protected transient ClassDescriptor descriptor;
    protected String classExtractionMethodName;
    protected transient Method classExtractionMethod;

    /**
     * INTERNAL:
     * Return all the classExtractionMethod
     */
    public Method getClassExtractionMethod() {
        return classExtractionMethod;
    }

    /**
     * PUBLIC:
     * A class extraction method can be registered with the descriptor to override the default inheritance mechanism.
     * This allows for the class indicator field to not be used, and a user defined one instead.
     * The method registered must be a static method on the class that the descriptor is for,
     * the method must take DatabaseRow as argument, and must return the class to use for that row.
     * This method will be used to decide which class to instantiate when reading from the database.
     * It is the application's responsibility to populate any typing information in the database required
     * to determine the class from the row.
     * If this method is used then the class indicator field and mapping cannot be used,
     * also the descriptor's withAllSubclasses and onlyInstances expressions must also be setup correctly.
     */
    public String getClassExtractionMethodName() {
        return classExtractionMethodName;
    }

    /**
     * INTERNAL:
     */
    protected void setClassExtractionMethod(Method classExtractionMethod) {
        this.classExtractionMethod = classExtractionMethod;
    }

    /**
     * PUBLIC:
     * A class extraction method can be registered with the descriptor to override the default inheritance mechanism.
     * This allows for the class indicator field to not be used, and a user defined one instead.
     * The method registered must be a static method on the class that the descriptor is for,
     * the method must take DatabaseRow as argument, and must return the class to use for that row.
     * This method will be used to decide which class to instantiate when reading from the database.
     * It is the application's responsibility to populate any typing information in the database required
     * to determine the class from the row.
     * If this method is used then the class indicator field and mapping cannot be used,
     * also the descriptor's withAllSubclasses and onlyInstances expressions must also be setup correctly.
     */
    public void setClassExtractionMethodName(String staticClassClassExtractionMethod) {
        this.classExtractionMethodName = staticClassClassExtractionMethod;
    }

    /**
     * INTERNAL:
     * Setup the default classExtractionMethod, or if one was specified by the user make sure it is valid.
     */
    @Override
    public void initialize(ClassDescriptor descriptor, Session session) throws DescriptorException {
        setDescriptor(descriptor);
        Class<?>[] declarationParameters = new Class<?>[1];
        declarationParameters[0] = ClassConstants.DatabaseRow_Class;

        try {
            setClassExtractionMethod(Helper.getDeclaredMethod(descriptor.getJavaClass(), getClassExtractionMethodName(), declarationParameters));
        } catch (NoSuchMethodException ignore) {
            declarationParameters[0] = ClassConstants.Record_Class;
            try {
                setClassExtractionMethod(Helper.getDeclaredMethod(descriptor.getJavaClass(), getClassExtractionMethodName(), declarationParameters));
            } catch (NoSuchMethodException exception) {
                throw DescriptorException.noSuchMethodWhileInitializingClassExtractionMethod(getClassExtractionMethodName(), descriptor, exception);
            } catch (SecurityException exception) {
                throw DescriptorException.securityWhileInitializingClassExtractionMethod(getClassExtractionMethodName(), descriptor, exception);
            }
        } catch (SecurityException exception) {
            throw DescriptorException.securityWhileInitializingClassExtractionMethod(getClassExtractionMethodName(), descriptor, exception);
        }

        // CR#2818667 Ensure the method is static.
        if (!Modifier.isStatic(getClassExtractionMethod().getModifiers())) {
            throw DescriptorException.classExtractionMethodMustBeStatic(getClassExtractionMethodName(), descriptor);
        }
    }

    /**
     * INTERNAL
     * Extract/compute the class from the database row and return the class.
     * Map is used as the public interface to database row, the key is the field name,
     * the value is the database value.
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public <T> Class<T> extractClassFromRow(DataRecord row, org.eclipse.persistence.sessions.Session session) {
        return PrivilegedAccessHelper.callDoPrivilegedWithException(
                () -> PrivilegedAccessHelper.invokeMethod(getClassExtractionMethod(), null, new Object[] {row}),
                (ex) -> {
                    if (ex instanceof IllegalAccessException) {
                        return DescriptorException.illegalAccessWhileInvokingRowExtractionMethod(
                                (AbstractRecord) row, getClassExtractionMethod(), getDescriptor(), ex);
                    } else if (ex instanceof InvocationTargetException) {
                        return DescriptorException.targetInvocationWhileInvokingRowExtractionMethod(
                                (AbstractRecord) row, getClassExtractionMethod(), getDescriptor(), ex);
                    }
                    throw new RuntimeException(String.format("Invocation of %s method failed", getClassExtractionMethod().getName()), ex);
                }
        );
    }

    /**
     * INTERNAL:
     * Return the descriptor.
     */
    protected ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * INTERNAL:
     * Set the descriptor.
     */
    protected void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }
}
