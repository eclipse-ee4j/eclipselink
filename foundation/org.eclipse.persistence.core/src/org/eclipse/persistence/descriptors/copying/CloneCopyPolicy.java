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
package org.eclipse.persistence.descriptors.copying;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;

/**
 * <p><b>Purpose</b>: Allows a clone of an object to be created with a method that returns
 * the cloned object.  
 *  
 * It is possible to define methods for two types of clones
 * 
 * 1. methodName can be set to define the method EclipseLink uses to clone objects for it's
 * own internal use.  The objects created by this method will not be visible to the user, and
 * instead used as a basis for comparison when a DeferredChangeDetectionPolicy used.  This method will
 * also be in place of the workingCopyMethod if it is not provided
 * 
 * 2. workingCopyMethod this method is used to create the clone that is returned to the user when an
 * Object is registered in a UnitOfWork
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
     * Clone through calling the clone method.
     */
    public Object buildClone(Object domainObject, Session session) throws DescriptorException {
        // Must allow for null clone method for 9.0.4 deployment XML.
        if (this.getMethodName() == null) {
            return getDescriptor().getObjectBuilder().buildNewInstance();
        }
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedMethodInvoker(this.getMethod(), domainObject, new Object[0]));
                } catch (PrivilegedActionException exception) {
                    Exception throwableException = exception.getException();
                    if (throwableException instanceof IllegalAccessException) {
                        throw DescriptorException.illegalAccessWhileCloning(domainObject, this.getMethodName(), this.getDescriptor(), throwableException);
                    } else {
                        throw DescriptorException.targetInvocationWhileCloning(domainObject, this.getMethodName(), this.getDescriptor(), throwableException);

                    }
                }
            } else {
                return PrivilegedAccessHelper.invokeMethod(this.getMethod(), domainObject, new Object[0]);
            }
        } catch (IllegalAccessException exception) {
            throw DescriptorException.illegalAccessWhileCloning(domainObject, this.getMethodName(), this.getDescriptor(), exception);
        } catch (InvocationTargetException exception) {
            throw DescriptorException.targetInvocationWhileCloning(domainObject, this.getMethodName(), this.getDescriptor(), exception);
        }
    }

    /**
     * Clone through the workingCopyClone method, or if not specified the clone method.
     */
    public Object buildWorkingCopyClone(Object domainObject, Session session) throws DescriptorException {
        if (this.getWorkingCopyMethodName() == null) {
            //not implemented to perform special operations.
            return this.buildClone(domainObject, session);
        }

        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedMethodInvoker(this.getWorkingCopyMethod(), domainObject, new Object[0]));
                } catch (PrivilegedActionException exception) {
                    Exception throwableException = exception.getException();
                    if (throwableException instanceof IllegalAccessException) {
                        throw DescriptorException.illegalAccessWhileCloning(domainObject, this.getMethodName(), this.getDescriptor(), throwableException);
                    } else {
                        throw DescriptorException.targetInvocationWhileCloning(domainObject, this.getMethodName(), this.getDescriptor(), throwableException);
                    }
                }
            } else {
                return PrivilegedAccessHelper.invokeMethod(this.getWorkingCopyMethod(), domainObject, new Object[0]);
            }
        
        } catch (IllegalAccessException exception) {
            throw DescriptorException.illegalAccessWhileCloning(domainObject, this.getMethodName(), this.getDescriptor(), exception);
        } catch (InvocationTargetException exception) {
            throw DescriptorException.targetInvocationWhileCloning(domainObject, this.getMethodName(), this.getDescriptor(), exception);
        }
    }

    /**
     * Create a new instance, unless a workingCopyClone method is specified, then build a new instance and clone it.
     */
    @Override
    public Object buildWorkingCopyCloneFromRow(Record row, ObjectBuildingQuery query, Object primaryKey, UnitOfWork uow) throws DescriptorException {
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
     * Return the workingCopyClone method.
     * This is used to clone within a unit of work.
     */
    protected Method getWorkingCopyMethod() {
        return workingCopyMethod;
    }

    /**
     * Return the workingCopyClone method name.
     * This is used to clone within a unit of work.
     */
    public String getWorkingCopyMethodName() {
        return workingCopyMethodName;
    }

    /**
     * Validate and build the methods.
     */
    public void initialize(Session session) throws DescriptorException {
        try {
            // Must allow for null clone method for 9.0.4 deployment XML.
            if (this.getMethodName() != null) {
                this.setMethod(Helper.getDeclaredMethod(this.getDescriptor().getJavaClass(), this.getMethodName(), new Class[0]));
            }
        } catch (NoSuchMethodException exception) {
            session.getIntegrityChecker().handleError(DescriptorException.noSuchMethodWhileInitializingCopyPolicy(this.getMethodName(), this.getDescriptor(), exception));
        } catch (SecurityException exception) {
            session.getIntegrityChecker().handleError(DescriptorException.securityWhileInitializingCopyPolicy(this.getMethodName(), this.getDescriptor(), exception));
        }
        if (this.getWorkingCopyMethodName() != null) {
            try {
                this.setWorkingCopyMethod(Helper.getDeclaredMethod(this.getDescriptor().getJavaClass(), this.getWorkingCopyMethodName(), new Class[0]));
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
     * Set the workingCopyClone method.
     * This is used to clone within a unit of work.
     */
    protected void setWorkingCopyMethod(Method method) {
        this.workingCopyMethod = method;
    }

    /**
     * Set the workingCopyClone method name.
     * This is used to clone within a unit of work.
     */
    public void setWorkingCopyMethodName(String methodName) {
        this.workingCopyMethodName = methodName;
    }

    /**
     * Return false as a shallow clone is returned, not a new instance.
     */
    public boolean buildsNewInstance() {
        return getMethodName() == null;
    }

    public String toString() {
        return Helper.getShortClassName(this) + "(" + this.getMethodName() + ")";
    }
}
