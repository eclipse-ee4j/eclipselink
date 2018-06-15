/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.eis.interactions;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.QueryStringCall;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Defines the specification for a call to a JCA interaction that uses a query string.
 * This can be used for generic query translation support (i.e. VSAM, BETRIEVE, ADA, etc.)
 * Arguments are defined in the query string through #{@literal <field-name>} (i.e. #EMP_ID)
 * Translates the query string from the query arguments.
 * Builds the input and output records.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class QueryStringInteraction extends MappedInteraction implements QueryStringCall {
    protected String queryString;

    /**
     * Default constructor.
     */
    public QueryStringInteraction() {
        super();
        this.queryString = "";
    }

    /**
     * Construct interaction with the query string.
     */
    public QueryStringInteraction(String queryString) {
        super();
        this.queryString = queryString;
    }

    /**
     * PUBLIC:
     * Return the query string.
     */
    @Override
    public String getQueryString() {
        return queryString;
    }

    /**
     * PUBLIC:
     * Set the query string.
     */
    @Override
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
     * Allow the call to translate the query arguments.
     */
    @Override
    public void translate(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session) {
        translateQueryString(translationRow, modifyRow, session);
    }

    /**
     * Translate the custom query markers.
     */
    @Override
    public void prepare(AbstractSession session) {
        if (isPrepared()) {
            return;
        }
        translateCustomQuery();
        setIsPrepared(true);
    }

    /**
     * Return the string for logging purposes.
     */
    @Override
    public String getLogString(Accessor accessor) {
        StringWriter writer = new StringWriter();
        writer.write("Executing ");
        writer.write(toString());
        writer.write(Helper.cr());
        writer.write("\tspec => ");
        writer.write(String.valueOf(getInteractionSpec()));
        writer.write(Helper.cr());
        writer.write("\tquery => ");
        writer.write(getQueryString());
        writer.write(Helper.cr());
        writer.write("\tparameters => [");
        if (hasParameters()) {
            for (Iterator iterator = getParameters().iterator(); iterator.hasNext();) {
                Object parameter = iterator.next();
                writer.write(String.valueOf(parameter));
                if (iterator.hasNext()) {
                    writer.write(", ");
                }
            }
        }
        writer.write("]");
        return writer.toString();
    }

    @Override
    public boolean isQueryStringCall() {
        return true;
    }
}
