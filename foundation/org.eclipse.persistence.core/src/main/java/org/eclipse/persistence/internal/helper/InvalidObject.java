/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
