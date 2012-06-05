/*******************************************************************************
 * Copyright (c) 2008 - 2012 Oracle Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.1
 *     Linda DeMichiel - Java Persistence 2.0
 *
 ******************************************************************************/
package javax.persistence;

/**
 * Used with the {@link Access} annotation to specify an access
 * type to be applied to an entity class, mapped superclass, or
 * embeddable class, or to a specific attribute of such a class.
 * 
 * @see Access
 *
 * @since Java Persistence 2.0
 */
public enum AccessType {

    /** Field-based access is used. */
    FIELD,

    /** Property-based access is used. */
    PROPERTY
}
