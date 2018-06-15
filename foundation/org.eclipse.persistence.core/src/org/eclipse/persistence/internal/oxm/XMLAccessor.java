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
package org.eclipse.persistence.internal.oxm;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatasourceAccessor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.sessions.Login;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This class implements the Accessor for XMLLogins.
 * Provides empty implementations of most methods, since the Accessor isn't
 * used apart from during the login.</p>
 * @author  mmacivor
 * @since   10.1.3
 */

public class XMLAccessor extends DatasourceAccessor {

    /**
     * Default Constructor.
     */
    public XMLAccessor() {
        super();
    }

    /**
     * By default the SDK does not have a connection,
     * so use an object as a placeholder.
     * Subclasses can make use of their own connection.
     */
    @Override
    protected void connectInternal(Login login, AbstractSession session) throws DatabaseException {
        setDatasourceConnection(new Object());
        setIsInTransaction(false);
        setIsConnected(true);
    }

    /**
     * Begin a transaction on the "data store".
     */
    @Override
    public void basicBeginTransaction(AbstractSession session) {
        // do nothing
    }

    /**
     * Commit the transaction to the "data store".
     */
    @Override
    public void basicCommitTransaction(AbstractSession session) {
        // do nothing
    }

    /**
     * Rollback the transaction on the "data store".
     */
    @Override
    public void basicRollbackTransaction(AbstractSession session) {
        // do nothing
    }

    @Override
    public Object basicExecuteCall(Call call, AbstractRecord translationRow, AbstractSession session) throws DatabaseException {
        //return null since this should never be called
        return null;
    }

    /* Close the connection to the "data source".*/
    @Override
    protected void closeDatasourceConnection() {
        // do nothing
    }

    /**
     * Return if the connection to the "data source" is connected.
     */
    @Override
    protected boolean isDatasourceConnected() {
        return isConnected;
    }

    /**
     * Log any driver level connection meta-data if available.
     */
    @Override
    protected void buildConnectLog(AbstractSession session) {
    }

    /**
     * Convert the specified row into something
     * suitable for the calls.
     * The default is to leave the row unconverted.
     */
    protected AbstractRecord convert(AbstractRecord row, AbstractSession session) {
        return row;
    }

    /**
     * Call <code>#toString(PrintWriter)</code>, to allow subclasses to
     * insert additional information.
     */
    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        writer.write(Helper.getShortClassName(this));
        writer.write("(");
        this.toString(writer);
        writer.write(")");
        return sw.toString();
    }

    /**
     * Append more information to the writer.
     */
    protected void toString(PrintWriter writer) {
        writer.print(this.getLogin());
    }
}
