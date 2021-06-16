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
package org.eclipse.persistence.queries;

/**
 * <p><b>Purpose</b>:
 * Concrete class to perform a read of a single data value.
 *
 * <p><b>Responsibilities</b>:
 * Used in conjunction with CursoredStream size and Platform getSequence.
 * This can be used to read a single data value (i.e. one field).
 * A single data value is returned, or null if no rows are returned.
 *
 * @author James Sutherland
 * @since TOPLink/Java 1.2
 */
public class ValueReadQuery extends DirectReadQuery {

    /**
     * PUBLIC:
     * Initialize the state of the query.
     */
    public ValueReadQuery() {
        super();
        this.resultType = VALUE;
    }

    /**
     * PUBLIC:
     * Initialize the query to use the specified SQL string.
     * Warning: Allowing an unverified SQL string to be passed into this
     * method makes your application vulnerable to SQL injection attacks.
     */
    public ValueReadQuery(String sqlString) {
        super(sqlString);
        this.resultType = VALUE;
    }

    /**
     * PUBLIC:
     * Initialize the query to use the specified call.
     */
    public ValueReadQuery(Call call) {
        super(call);
        this.resultType = VALUE;
    }

    /**
     * PUBLIC:
     * Return if this is a value read query.
     */
    @Override
    public boolean isValueReadQuery() {
        return true;
    }
}
