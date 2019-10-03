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
package org.eclipse.persistence.internal.databaseaccess;

import java.io.Writer;
import java.util.List;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.Call;

/**
 * INTERNAL:
 * <b>Purpose</b>: Used to define query string calls.
 * These include SQLCall, XQueryInteraction which reuse translation behavior through
 * this interface.
 *
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public interface QueryStringCall extends Call {

    /**
     * The parameters are the values in order of occurance in the SQL statement.
     * This is lazy initialized to conserv space on calls that have no parameters.
     */
    List getParameters();

    /**
     * The parameter types determine if the parameter is a modify, translation or litteral type.
     */
    List<Integer> getParameterTypes();

    /**
     * The parameters are the values in order of occurance in call.
     * This is lazy initialized to conserv space on calls that have no parameters.
     */
    boolean hasParameters();

    /**
     * Allow pre-printing of the query/SQL string for fully bound calls, to save from reprinting.
     * This should call translateCustomQuery() in the call implementation.
     */
    void prepare(AbstractSession session);

    /**
     * Allow the call to translate from the translation for predefined calls.
     * This should call translateQueryString() in the call implementation.
     */
    void translate(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session);

    /**
     * Return the query string of the call.
     * This must be overwritten by subclasses that support query language translation (SQLCall, XQueryCall).
     */
    String getQueryString();

    /**
     * Set the query string of the call.
     * This must be overwritten by subclasses that support query language translation (SQLCall, XQueryCall).
     */
    void setQueryString(String queryString);

    /**
     * Parse the query string for # markers for custom query based on a query language.
     * This is used by SQLCall and XQuery call, but can be reused by other query languages.
     */
    void translateCustomQuery();

    /**
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    void appendLiteral(Writer writer, Object literal);

    /**
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    void appendTranslation(Writer writer, DatabaseField modifyField);

    /**
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    void appendModify(Writer writer, DatabaseField modifyField);

    /**
     * Add the parameter.
     * If using binding bind the parameter otherwise let the platform print it.
     * The platform may also decide to bind the value.
     */
    void appendParameter(Writer writer, Object parameter, AbstractSession session);

    /**
     * Allow the call to translate from the translation for predefined calls.
     */
    void translateQueryString(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session);

    /**
     * Should return true.
     */
    boolean isQueryStringCall();
}
