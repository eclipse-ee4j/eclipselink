/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - allow for zero ids
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

/** 
 * The IdValidation enum determines the type value that are valid for an Id.
 * By default null is not allowed, and 0 is not allow for singleton ids of long or int type.
 * The default value is ZERO for singleton ids, and NULL for composite ids.
 * This can be set using the @PrimaryKey annotation, or ClassDescriptor API.
 * 
 * @see PrimaryKey
 * @see org.eclipse.persistence.descriptors.ClassDescriptor#setIdValidation(IdValidation)
 * @author James Sutherland
 * @since EclipseLink 1.0 
 */ 
public enum IdValidation {
    /**
     * Only null is not allowed as an id value, 0 is allowed.
     */
    NULL,

    /**
     * null and 0 are not allowed, (only int and long).
     */
    ZERO,

    /**
     * No id validation is done.
     */
    NONE
}
