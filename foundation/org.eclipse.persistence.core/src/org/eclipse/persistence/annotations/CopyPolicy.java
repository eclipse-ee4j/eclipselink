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
 * A CopyPolicy is used to set an 
 * org.eclipse.persistence.descriptors.copying.CopyPolicy on an Entity.
 * It is required that a class that 
 * implements org.eclipse.persistence.descriptors.copying.CopyPolicy
 * be specified as the argument.
 * 
 * A CopyPolicy should be specified on an Entity, MappedSuperclass or
 * Embeddable.
 * 
 * For instance:
 * @Entity
 * @CopyPolicy("example.MyCopyPolicy")
 * 
 * @see org.eclipse.persistence.descriptors.copying.CopyPolicy
 * @see org.eclipse.persistence.annotations.CloneCopyPolicy
 * @see org.eclipse.persistence.annotations.InstantiationCopyPolicy
 * 
 * @author tware
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CopyPolicy {

    /**
     * (Required)
     * This defines the class of the copy policy.  It must specify a class that 
     * implements org.eclipse.persistence.descriptors.copying.CopyPolicy
     */
    Class value();
}
