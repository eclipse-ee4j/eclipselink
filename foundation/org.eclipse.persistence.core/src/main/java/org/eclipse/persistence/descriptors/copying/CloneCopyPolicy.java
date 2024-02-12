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
package org.eclipse.persistence.descriptors.copying;

import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;

import java.lang.reflect.Method;

/**
 * <p><b>Purpose</b>: Allows a clone of an object to be created with a method that returns
 * the cloned object.
 * <p>
 * It is possible to define methods for two types of clones
 * <ul>
 * <li>{@code methodName} can be set to define the method EclipseLink uses to clone objects for it's
 * own internal use. The objects created by this method will not be visible to the user, and
 * instead used as a basis for comparison when a {@linkplain DeferredChangeDetectionPolicy} used. This method will
 * also be in place of the {@code workingCopyMethod} if it is not provided</li>
 * <li>{@code workingCopyMethod} this method is used to create the clone that is returned to the user when an
 * {@code Object} is registered in a {@linkplain UnitOfWork}</li>
 * </ul>
 */
public class CloneCopyPolicy extends AbstractCopyPolicy {

    /** Allow for clone method to be specified. */
    protected String methodName;
    protected String workingCopyMethodName;
    protected transient Method method;
    protected transient Method workingCopyMethod;

    public CloneCopyPolicy() {
        super();
    }

    /**
     * Clone through calling the {@code clone} method.
     */
    @Override
    public Object buildClone(Object domainObject, Session session) throws DescriptorException {
        // Must allow for null clone method for 9.0.4 deployment XML.
        if (this.getMethodName() == null) {
            return getDescriptor().getObjectBuilder().buildNewInstance();
        }
        return PrivilegedAccessHelper.callDoPrivilegedWithException(
                () -> PrivilegedAccessHelper.invokeMethod(this.getMethod(), domainObject, new Object[0]),
                (ex) -> {
                    if (ex instanceof IllegalAccessException) {
                        return DescriptorException.illegalAccessWhileCloning(
                                domainObject, this.getMethodName(), this.getDescriptor(), ex);
                    }
                    return DescriptorException.targetInvocationWhileCloning(
                            domainObject, this.getMethodName(), this.getDescriptor(), ex);
                }
        );
    }

    /**
     * Clone through the {@code workingCopyClone} method, or if not specified the {@code clone} method.
     */
    @Override
    public Object buildWorkingCopyClone(Object domainObject, Session session) throws DescriptorException {
        if (this.getWorkingCopyMethodName() == null) {
            //not implemented to perform special operations.
            return this.buildClone(domainObject, session);
        }
        return PrivilegedAccessHelper.callDoPrivilegedWithException(
                () -> PrivilegedAccessHelper.invokeMethod(this.getWorkingCopyMethod(), domainObject, new Object[0]),
                (ex) -> {
                    if (ex instanceof IllegalAccessException) {
                        return DescriptorException.illegalAccessWhileCloning(domainObject, this.getMethodName(), this.getDescriptor(), ex);
                    }
                    return DescriptorException.targetInvocationWhileCloning(domainObject, this.getMethodName(), this.getDescriptor(), ex);
                }
        );
    }

    /**
     * Create a new instance, unless a {@code workingCopyClone} method is specified, then build a new instance and clone it.
     */
    @Override
    public Object buildWorkingCopyCloneFromRow(DataRecord row, ObjectBuildingQuery query, Object primaryKey, UnitOfWork uow) throws DescriptorException {
        // For now must preserve CMP code which builds heavy clones with a context.
        // Also preserve for clients who use the copy policy.
        ObjectBuilder builder = getDescriptor().getObjectBuilder();
        if (getWorkingCopyMethodName() != null) {
            Object original = builder.buildNewInstance();
            builder.buildAttributesIntoShallowObject(original, (AbstractRecord)row, query);
            return buildWorkingCopyClone(original, query.getSession());
        } else {
            return builder.buildNewInstance();
        }
    }

    /**
     * Return the clone method.
     */
    protected Method getMethod() {
        return method;
    }

    /**
     * Return the clone method name.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Return the {@code workingCopyClone} method.
     * This is used to clone within a unit of work.
     */
    protected Method getWorkingCopyMethod() {
        return workingCopyMethod;
    }

    /**
     * Return the {@code workingCopyClone} method name.
     * This is used to clone within a unit of work.
     */
    public String getWorkingCopyMethodName() {
        return workingCopyMethodName;
    }

    /**
     * Validate and build the methods.
     */
    @Override
    public void initialize(Session session) throws DescriptorException {
        final Class<?> javaClass = this.getDescriptor().getJavaClass();
        try {
            // Must allow for null clone method for 9.0.4 deployment XML.
            if (this.getMethodName() != null) {
                this.setMethod(Helper.getDeclaredMethod(javaClass, this.getMethodName(), new Class<?>[0]));
            }
        } catch (NoSuchMethodException exception) {
            session.getIntegrityChecker().handleError(DescriptorException.noSuchMethodWhileInitializingCopyPolicy(this.getMethodName(), this.getDescriptor(), exception));
        } catch (SecurityException exception) {
            session.getIntegrityChecker().handleError(DescriptorException.securityWhileInitializingCopyPolicy(this.getMethodName(), this.getDescriptor(), exception));
        }
        if (this.getWorkingCopyMethodName() != null) {
            try {
                this.setWorkingCopyMethod(Helper.getDeclaredMethod(javaClass, this.getWorkingCopyMethodName(), new Class<?>[0]));
            } catch (NoSuchMethodException exception) {
                session.getIntegrityChecker().handleError(DescriptorException.noSuchMethodWhileInitializingCopyPolicy(this.getMethodName(), this.getDescriptor(), exception));
            } catch (SecurityException exception) {
                session.getIntegrityChecker().handleError(DescriptorException.securityWhileInitializingCopyPolicy(this.getMethodName(), this.getDescriptor(), exception));
            }
        }
    }

    /**
     * Set the clone method.
     */
    protected void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Set the clone method name.
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Set the {@code workingCopyClone} method.
     * This is used to clone within a unit of work.
     */
    protected void setWorkingCopyMethod(Method method) {
        this.workingCopyMethod = method;
    }

    /**
     * Set the {@code workingCopyClone} method name.
     * This is used to clone within a unit of work.
     */
    public void setWorkingCopyMethodName(String methodName) {
        this.workingCopyMethodName = methodName;
    }

    /**
     * Return false as a shallow clone is returned, not a new instance.
     */
    @Override
    public boolean buildsNewInstance() {
        return getMethodName() == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + this.getMethodName() + ")";
    }
}
