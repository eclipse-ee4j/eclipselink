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

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.databaseaccess.*;

/**
 * The <code>SDKQueryMechanism</code> class implements the
 * <code>DatabaseQueryMechanism</code> protocol by redirecting
 * all queries to a collection
 * of calls that is provided to the mechanism when it is built.
 * So just about everything ends up going through
 * <code>executeSelectCalls()</code> or
 * <code>executeNoSelectCalls()</code>, which, in turn, send the appropriate
 * message to the <code>SDKAccessor</code>.
 *
 * @see SDKAccessor
 * @see SDKCall
 * @see AbstractSDKCall
 *
 * @author Big Country
 *    @since TOPLink/Java 1.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public class SDKQueryMechanism extends DatasourceCallQueryMechanism {

    /**
     * Construct a new mechanism for the specified query.
     */
    public SDKQueryMechanism(DatabaseQuery query) {
        super(query);
    }

    /**
     * Construct a new mechanism for the specified query and call.
     */
    public SDKQueryMechanism(DatabaseQuery query, AbstractSDKCall call) {
        super(query);
        addCall(call);
    }

    /**
     * Set the call.
     */
    public void setCall(DatasourceCall call) {
        this.calls = new Vector(1);
        addCall(call);
    }

    /**
     * Add a translation. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translation will also be added.
     */
    protected void addReadTranslation(String dataStoreFieldName, String mappingFieldName) {
        for (Enumeration stream = this.getCalls().elements(); stream.hasMoreElements();) {
            AbstractSDKCall call = (AbstractSDKCall)stream.nextElement();
            call.addReadTranslation(dataStoreFieldName, mappingFieldName);
        }
    }

    /**
     * Add translations. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translations will also be added.
     */
    protected void addReadTranslations(String[] dataStoreFieldNames, String[] mappingFieldNames) {
        for (Enumeration stream = this.getCalls().elements(); stream.hasMoreElements();) {
            AbstractSDKCall call = (AbstractSDKCall)stream.nextElement();
            call.addReadTranslations(dataStoreFieldNames, mappingFieldNames);
        }
    }

    /**
     * Add translations. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translations will also be added.
     */
    protected void addReadTranslations(Vector translations) {
        for (Enumeration stream = this.getCalls().elements(); stream.hasMoreElements();) {
            AbstractSDKCall call = (AbstractSDKCall)stream.nextElement();
            call.addReadTranslations(translations);
        }
    }

    /**
     * Add a translation. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translation will also be added.
     */
    protected void addWriteTranslation(String mappingFieldName, String dataStoreFieldName) {
        for (Enumeration stream = this.getCalls().elements(); stream.hasMoreElements();) {
            AbstractSDKCall call = (AbstractSDKCall)stream.nextElement();
            call.addWriteTranslation(mappingFieldName, dataStoreFieldName);
        }
    }

    /**
     * Add translations. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translations will also be added.
     */
    protected void addWriteTranslations(String[] mappingFieldNames, String[] dataStoreFieldNames) {
        for (Enumeration stream = this.getCalls().elements(); stream.hasMoreElements();) {
            AbstractSDKCall call = (AbstractSDKCall)stream.nextElement();
            call.addWriteTranslations(mappingFieldNames, dataStoreFieldNames);
        }
    }

    /**
     * Add translations. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translations will also be added.
     */
    protected void addWriteTranslations(Vector translations) {
        for (Enumeration stream = this.getCalls().elements(); stream.hasMoreElements();) {
            AbstractSDKCall call = (AbstractSDKCall)stream.nextElement();
            call.addWriteTranslations(translations);
        }
    }

    public DatabaseCall cursorSelectAllRows() throws SDKDataStoreException {
        //Helper.toDo("deprecated");
        return this.startCursor();
    }

    /**
     * Convenience method - cast the accessor.
     */
    protected SDKAccessor getSDKAccessor() {
        try {
            return (SDKAccessor)this.getQuery().getAccessor();
        } catch (ClassCastException e) {
            throw SDKQueryException.invalidSDKAccessor(this.getQuery().getAccessor());
        }
    }

    public void prepareCursorSelectAllRows() throws SDKQueryException {
        //Helper.toDo("deprecated");
        this.prepareStartCursor();
    }

    /**
     * Prepare for a cursored select.
     * This is sent to the original query before cloning.
     * @exception SDKQueryException if something is amiss
     */
    public void prepareStartCursor() throws SDKQueryException {
        //do nothing...
    }

    /**
     * Start reading all the rows from the database for a cursored stream
     * or scrollable cursor. Return the DatabaseCall - the CursorPolicy
     * will perform the actual reading of the rows from the database.
     * <p> Assume the calls are correct.
     * <p> <i>Cursors are not currently supported.</i>
     * @return the call
     * @exception SDKDataStoreException if an error has occurred on the "data store"
     */
    public org.eclipse.persistence.internal.databaseaccess.DatabaseCall startCursor() throws SDKDataStoreException {
        throw SDKDataStoreException.unsupported("Cursor");
    }
}