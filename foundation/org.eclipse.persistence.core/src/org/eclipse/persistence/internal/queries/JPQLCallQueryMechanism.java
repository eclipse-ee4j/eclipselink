/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.queries;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.JPQLCall;

/**
 * INTERNAL
 * <p><b>Purpose</b>:
 * Mechanism used for EJBQL.
 * <p><b>Responsibilities</b>:
 * Executes the appropriate call.
 *
 * @author Jon Driscoll, Joel Lucuik
 * @since TopLink 4.0
 */
public class JPQLCallQueryMechanism extends ExpressionQueryMechanism {
    //JPQLCall gets its own variable, rather than inheriting
    //call (because an JPQLCall is out on its own)
    protected JPQLCall ejbqlCall;

    public JPQLCallQueryMechanism() {
    }

    /**
     * Initialize the state of the query
     * @param query - owner of mechanism
     */
    public JPQLCallQueryMechanism(DatabaseQuery query) {
        super(query);
    }

    /**
     * INTERNAL
     * Initialize the state of the query
     * @param query - owner of mechanism
     * @param call - Database call
     */
    public JPQLCallQueryMechanism(DatabaseQuery query, JPQLCall call) {
        this(query);
        this.ejbqlCall = call;
        call.setQuery(query);
    }

    @Override
    public Object clone() {
        JPQLCallQueryMechanism copyOfMyself = (JPQLCallQueryMechanism)super.clone();
        copyOfMyself.ejbqlCall = (JPQLCall)ejbqlCall.clone();
        return copyOfMyself;

    }

    /**
     * Internal:
     * In the case of EJBQL, an expression needs to be generated, and the query populated.
     */
    @Override
    public void buildSelectionCriteria(AbstractSession newSession) {
        getJPQLCall().setQuery(getQuery());
        getJPQLCall().populateQuery(newSession);
    }

    public JPQLCall getJPQLCall() {
        return ejbqlCall;
    }

    @Override
    public boolean isJPQLCallQueryMechanism() {
        return true;
    }

    public void setJPQLCall(JPQLCall newJPQLCall) {
        ejbqlCall = newJPQLCall;
    }
}
