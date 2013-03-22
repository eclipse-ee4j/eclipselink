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
package org.eclipse.persistence.internal.databaseaccess;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 * <b>Purpose<b>: Used to define query string calls.
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
    public List getParameters();

    /**
     * The parameter types determine if the parameter is a modify, translation or litteral type.
     */
    public List<Integer> getParameterTypes();

    /**
     * The parameters are the values in order of occurance in call.
     * This is lazy initialized to conserv space on calls that have no parameters.
     */
    public boolean hasParameters();

    /**
     * Allow pre-printing of the query/SQL string for fully bound calls, to save from reprinting.
     * This should call translateCustomQuery() in the call implementation.
     */
    public void prepare(AbstractSession session);

    /**
     * Allow the call to translate from the translation for predefined calls.
     * This should call translateQueryString() in the call implementation.
     */
    public void translate(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session);

    /**
     * Return the query string of the call.
     * This must be overwritten by subclasses that support query language translation (SQLCall, XQueryCall).
     */
    public String getQueryString();

    /**
     * Set the query string of the call.
     * This must be overwritten by subclasses that support query language translation (SQLCall, XQueryCall).
     */
    public void setQueryString(String queryString);

    /**
     * Parse the query string for # markers for custom query based on a query language.
     * This is used by SQLCall and XQuery call, but can be reused by other query languages.
     */
    public void translateCustomQuery();

    /**
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    public void appendLiteral(Writer writer, Object literal);

    /**
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    public void appendTranslation(Writer writer, DatabaseField modifyField);

    /**
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    public void appendModify(Writer writer, DatabaseField modifyField);

    /**
     * Add the parameter.
     * If using binding bind the parameter otherwise let the platform print it.
     * The platform may also decide to bind the value.
     */
    public void appendParameter(Writer writer, Object parameter, AbstractSession session);

    /**
     * Allow the call to translate from the translation for predefined calls.
     */
    public void translateQueryString(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session);

    /**
     * Should return true.
     */
    public boolean isQueryStringCall();
}
