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
 *     James Sutherland - allow for zero ids
 ******************************************************************************/
package org.eclipse.persistence.annotations;


/**
 * IdValidation configures which id values are considered valid when loading
 * from the database. This configuration is set either using the
 * {@link PrimaryKey} annotation or using the {@link ClassDescriptor} API.
 * This configuration is also used to determine if a sequence number is
 * required.
 * 
 * @see PrimaryKey#validation()
 * @see ClassDescriptor#setIdValidation(IdValidation)
 * @author James Sutherland
 * @since EclipseLink 1.0
 */
public enum IdValidation {
    /**
     * Only null is invalid All other values are considered valid. This is the
     * default configuration when composite identifiers are used and sequencing is not used.
     */
    NULL,

    /**
     * null and 0 are not allowed for primitive int and long ids. This is the
     * default configuration for a simple identifier.
     */
    ZERO,

    /**
     * null, 0 and negative values are not allowed for ids extending Number and primitive int and long ids. 
     */
    NEGATIVE,

    /**
     * No identifier validation is performed.
     */
    NONE
}
