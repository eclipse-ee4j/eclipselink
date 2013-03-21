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
package org.eclipse.persistence.annotations;

/**
 * An enum type that is used within the JoinFetch annotation.
 * 
 * @see org.eclipse.persistence.annotations.JoinFetch
 * @author James Sutherland
 * @since Oracle TopLink 11.1.1.0.0 
 */
public enum JoinFetchType {
    /**
     * An inner join is used to fetch the related object.
     * This does not allow for null/empty values.
     */
    INNER,

    /**
     * An inner join is used to fetch the related object.
     * This allows for null/empty values.
     */
    OUTER,
}
