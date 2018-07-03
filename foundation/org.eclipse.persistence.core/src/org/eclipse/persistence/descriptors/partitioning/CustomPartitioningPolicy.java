/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland (Oracle) - initial API and implementation
package org.eclipse.persistence.descriptors.partitioning;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.List;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * PUBLIC:
 * Defines a user defined partitioning policy.
 * Used by metadata to defer class loading to init.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class CustomPartitioningPolicy extends PartitioningPolicy {

    protected String partitioningClasName;

    protected PartitioningPolicy policy;

    public CustomPartitioningPolicy() {
    }

    /**
     * INTERNAL:
     * Initialize the policy.
     */
    public void initialize(AbstractSession session) {

    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        if (getPartitioningClasName() == null) {
            setPartitioningClasName("");
        }
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                Class partitioningClass = AccessController.doPrivileged(new PrivilegedClassForName(getPartitioningClasName(), true, classLoader));
                this.policy = (PartitioningPolicy)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(partitioningClass));
            } else {
                Class partitioningClass = PrivilegedAccessHelper.getClassForName(getPartitioningClasName(), true, classLoader);
                this.policy = (PartitioningPolicy)PrivilegedAccessHelper.newInstanceFromClass(partitioningClass);
            }
        } catch (PrivilegedActionException exception) {
            throw ValidationException.classNotFoundWhileConvertingClassNames(getPartitioningClasName(), exception.getException());
        } catch (ClassNotFoundException exception) {
            throw ValidationException.classNotFoundWhileConvertingClassNames(getPartitioningClasName(), exception);
        } catch (IllegalAccessException exception) {
            throw ValidationException.reflectiveExceptionWhileCreatingClassInstance(getPartitioningClasName(), exception);
        } catch (InstantiationException exception) {
            throw ValidationException.reflectiveExceptionWhileCreatingClassInstance(getPartitioningClasName(), exception);
        }
    }

    /**
     * INTERNAL:
     * Forward to custom policy
     */
    public List<Accessor> getConnectionsForQuery(AbstractSession session, DatabaseQuery query, AbstractRecord arguments) {
        return policy.getConnectionsForQuery(session, query, arguments);
    }

    public String getPartitioningClasName() {
        return partitioningClasName;
    }

    public void setPartitioningClasName(String partitioningClasName) {
        this.partitioningClasName = partitioningClasName;
    }

    public PartitioningPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(PartitioningPolicy policy) {
        this.policy = policy;
    }

}
