/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     tware - March 25/2008 - 1.0M6 - Initial implementation

package org.eclipse.persistence.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A CloneCopyPolicy is used to set an
 * org.eclipse.persistence.descriptors.copying.CloneCopyPolicy on an Entity.
 * A CloneCopyPolicy must specify at one or both of the "method" or
 * "workingCopyMethod".
 *
 * "workingCopyMethod" is used to clone objects that will be returned to the
 * user as they are registered in EclipseLink's transactional mechanism, the
 * UnitOfWork.
 *
 * "method" will be used for the clone that is used for comparison in
 * conjunction with EclipseLink's DeferredChangeDetectionPolicy
 *
 * A CloneCopyPolicy should be specified on an Entity, MappedSuperclass or
 * Embeddable.
 *
 * Example:
 * {@literal @}Entity
 * {@literal @}CloneCopyPolicy(method="myCloneMethod")
 *
 * or:
 *
 * {@literal @}Entity
 * {@literal @}CloneCopyPolicy(method="myCloneMethod", workingCopyMethod="myWorkingCopyCloneMethod")
 *
 * or:
 * {@literal @}Entity
 * {@literal @}CloneCopyPolicy(workingCopyMethodName="myWorkingCopyClone")
 *
 * @see org.eclipse.persistence.descriptors.copying.CloneCopyPolicy
 * @see org.eclipse.persistence.annotations.CloneCopyPolicy
 * @see org.eclipse.persistence.annotations.CopyPolicy
 *
 * @author tware
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CloneCopyPolicy {

    /**
     * (Optional)
     * Either method or workingCopyMethod must be specified
     * this defines a method that will be used to create a clone that will be
     * used for comparison by EclipseLink's DeferredChangeDetectionPolicy
     */
    String method();

    /**
     * (Optional)
     * Either method or workingCopyMethod must be specified
     * this defines a method that will be used to create a clone that will be
     * used to create the object returned when registering an Object in an
     * EclipseLink UnitOfWork
     */
    String workingCopyMethod();
}
