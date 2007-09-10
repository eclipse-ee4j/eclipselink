/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.sdk;

import java.io.*;
import org.eclipse.persistence.internal.databaseaccess.DatasourceAccessor;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <code>SDKAccessor</code> is a facile concrete subclass
 * of <code>DatasourceAccessor</code>.
 * It is extensible for non-relational data access.
 *
 * @see SDKCall
 * @see SDKLogin
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public class SDKAccessor extends DatasourceAccessor {

    /**
     * Default Constructor.
     */
    public SDKAccessor() {
        super();
    }

    /**
     * By default the SDK does not have a connection,
     * so use an object as a placeholder.
     * Subclasses can make use of their own connection.
     */
    protected void connectInternal(Login login, AbstractSession session) throws DatabaseException {
        setDatasourceConnection(new Object());
        setIsInTransaction(false);
        setIsConnected(true);
    }

    /**
     * Begin a transaction on the "data store".
     */
    public void basicBeginTransaction(AbstractSession session) throws SDKDataStoreException {
        // do nothing
    }

    /**
     * Commit the transaction to the "data store".
     */
    public void basicCommitTransaction(AbstractSession session) throws SDKDataStoreException {
        // do nothing
    }

    /**
     * Rollback the transaction on the "data store".
     */
    public void basicRollbackTransaction(AbstractSession session) throws SDKDataStoreException {
        // do nothing
    }

    /**
     * Return the appropriate exception.
     */
    protected SDKDataStoreException buildIncorrectLoginInstanceProvidedException(Class validLoginClass) {
        return SDKDataStoreException.incorrectLoginInstanceProvided(validLoginClass);
    }

    /**
     * Close the connection to the "data source".
     */
    protected void closeDatasourceConnection() {
        // do nothing
    }

    /**
     * Return if the connection to the "data source" is connected.
     */
    protected boolean isDatasourceConnected() {
        return isConnected;
    }

    /**
     * Log any driver level connection meta-data if available.
     */
    protected void buildConnectLog(AbstractSession session) {
        session.log(SessionLog.CONFIG, SessionLog.CONNECTION, "connected_sdk", null, this);
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
     * Execute the specified call with the specified
     * parameter row.
     */
    public Object basicExecuteCall(Call call, AbstractRecord translationRow, AbstractSession session) throws SDKDataStoreException {
        SDKCall sdkCall = null;
        try {
            sdkCall = (SDKCall)call;
        } catch (ClassCastException e) {
            throw SDKQueryException.invalidSDKCall(call);
        }
        return sdkCall.execute(this.convert(translationRow, session), this);
    }

    /**
     * Call <code>#toString(PrintWriter)</code>, to allow subclasses to
     * insert additional information.
     */
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
