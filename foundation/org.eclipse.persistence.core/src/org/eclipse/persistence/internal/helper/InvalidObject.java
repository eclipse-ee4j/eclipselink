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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.helper;


/**
 * <b>Purpose</b>:Indicates an object that should not be returned from
 * query execution.
 * <p>
 * When conforming if checkEarly return finds a matching object by exact primary
 * key, but that object is deleted, want to return null from query execution.
 * <p>
 * However if null is returned from checkEarly return that will indicate that
 * no object was found and to go to the database.  Hence returning null is not
 * enough, something else needed to be returned, indicating not only that
 * checkEarlyReturn had failed but query execution should not proceed.
 * <p>
 * Can be used in other instances where returning null is ambiguous.
 * <p>
 * Implements singleton pattern
 * @author  Stephen McRitchie
 */
public class InvalidObject {
    public static final InvalidObject instance = new InvalidObject();

    private InvalidObject() {
    }

    /**
     * @return singleton invalid object.
     */
    public static InvalidObject instance() {
        return instance;
    }
}
