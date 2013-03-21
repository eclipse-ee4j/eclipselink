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
 *     tware - March 25/2008 - 1.0M6 - Initial implementation
 ******************************************************************************/  

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
 * @Entity
 * @CloneCopyPolicy(method="myCloneMethod")
 * 
 * or:
 * 
 * @Entity
 * @CloneCopyPolicy(method="myCloneMethod", workingCopyMethod="myWorkingCopyCloneMethod")
 * 
 * or:
 * 
  @Entity
 * @CloneCopyPolicy(workingCopyMethodName="myWorkingCopyClone")
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
