/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.platform.database.oracle.dcn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import oracle.jdbc.OracleStatement;
import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.jdbc.dcn.RowChangeDescription;
import oracle.jdbc.dcn.TableChangeDescription;
import oracle.jdbc.driver.OracleConnection;

import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.descriptors.CacheIndex;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.platform.database.events.DatabaseEventListener;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sessions.Session;

/**
 * PUBLIC:
 * Listener for Oracle Database Change event Notification (DCN).
 * This allows the EclipseLink cache to be invalidated by database events.
 * 
 * @see org.eclipse.persistence.descriptors.invalidation.DatabaseEventNotificationPolicy
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
public class OracleChangeNotificationListener implements DatabaseEventListener {
    
    public static String ORA_TRANSACTION_ID = "oracle.dcn.transaction-id";
    public static String ROWID = "ROWID";

    /** The Oracle JDBC registration object. */
    protected DatabaseChangeRegistration register;
    /** Map each table to the descriptor that needs to be invalidated. */
    protected Map<DatabaseTable, ClassDescriptor> descriptorsByTable;

    /** Cache query for transaction id. */
    protected ValueReadQuery transactionIdQuery;
    
    public OracleChangeNotificationListener() {
        this.transactionIdQuery = new ValueReadQuery("SELECT DBMS_TRANSACTION.LOCAL_TRANSACTION_ID FROM DUAL");
        transactionIdQuery.setName(ORA_TRANSACTION_ID);
    }
    
    /**
     * INTERNAL:
     * Register the event listener with the database.
     */
    public void register(Session session) {
        final AbstractSession databaseSession = (AbstractSession)session;
        // Determine which tables should be tracked for change events.
        this.descriptorsByTable = new HashMap<DatabaseTable, ClassDescriptor>();
        for (ClassDescriptor descriptor : session.getDescriptors().values()) {
            if (!descriptor.getTables().isEmpty()) {
                if ((descriptor.getCachePolicy().getDatabaseChangeNotificationType() != null)
                            && (descriptor.getCachePolicy().getDatabaseChangeNotificationType() != DatabaseChangeNotificationType.NONE)) {
                    this.descriptorsByTable.put(descriptor.getTables().get(0), descriptor);
                }
            }
        }
        Accessor accessor = databaseSession.getAccessor();
        accessor.incrementCallCount(databaseSession);
        try {
            OracleConnection connection = (OracleConnection)databaseSession.getServerPlatform().unwrapConnection(accessor.getConnection());
            databaseSession.log(SessionLog.FINEST, SessionLog.CONNECTION, "dcn_registering");
            Properties properties = new Properties();
            properties.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS, "true");
            properties.setProperty(OracleConnection.DCN_IGNORE_INSERTOP, "true");
            try {
                // Register with the database change notification, the connection is not relevant, the events occur after the connection is closed,
                // and a different connection can be used to unregister the event listener.
                this.register = connection.registerDatabaseChangeNotification(properties);
                final List<DatabaseField> fields = new ArrayList<DatabaseField>();
                fields.add(new DatabaseField(ROWID));
                this.register.addListener(new DatabaseChangeListener() {                
                    public void onDatabaseChangeNotification(DatabaseChangeEvent changeEvent) {
                        databaseSession.log(SessionLog.FINEST, SessionLog.CONNECTION, "dcn_change_event", changeEvent);
                        if (changeEvent.getTableChangeDescription() != null) {
                            for (TableChangeDescription tableChange : changeEvent.getTableChangeDescription()) {
                                ClassDescriptor descriptor = OracleChangeNotificationListener.this.descriptorsByTable.get(new DatabaseTable(tableChange.getTableName()));
                                if (descriptor != null) {
                                    CacheIndex index = descriptor.getCachePolicy().getCacheIndex(fields);                                
                                    for (RowChangeDescription rowChange : tableChange.getRowChangeDescription()) {
                                        CacheId id = new CacheId(new Object[]{rowChange.getRowid().stringValue()});
                                        CacheKey key = databaseSession.getIdentityMapAccessorInstance().getIdentityMapManager().getCacheKeyByIndex(
                                                index, id, true, descriptor);
                                        if (key != null) {
                                            if ((key.getTransactionId() == null) || !key.getTransactionId().equals(changeEvent.getTransactionId(true))) {
                                                databaseSession.log(SessionLog.FINEST, SessionLog.CONNECTION, "dcn_invalidate", key.getKey(), descriptor.getJavaClass().getName());
                                                key.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
                // Register each table for database events, this is done by executing a select from the table.
                for (DatabaseTable table : this.descriptorsByTable.keySet()) {
                    OracleStatement statement = (OracleStatement)connection.createStatement();
                    statement.setDatabaseChangeRegistration(this.register);                
                    try {
                        statement.executeQuery("SELECT ROWID FROM " + table.getQualifiedName()).close();
                        databaseSession.log(SessionLog.FINEST, SessionLog.CONNECTION, "dcn_register_table", table.getQualifiedName());
                    } catch (Exception failed) {
                        // This will fail if the table does not exist,
                        // just log the error to allow table creation to work.
                        databaseSession.logThrowable(SessionLog.WARNING, SessionLog.SQL, failed);
                    } finally {
                        statement.close();
                    }
                }
            } catch (SQLException exception) {
                throw DatabaseException.sqlException(exception, databaseSession.getAccessor(), databaseSession, false);
            }
        } finally {
            accessor.decrementCallCount();
        }
    }

    /**
     * Initialize the descriptor to receive database change events.
     * This is called when the descriptor is initialized.
     */
    public void initialize(final ClassDescriptor descriptor, AbstractSession session) {
        if (descriptor.getOptimisticLockingPolicy() == null) {
            boolean requiresLocking = descriptor.hasMultipleTables();
            for (DatabaseMapping mapping : descriptor.getMappings()) {
                if (mapping.isCollectionMapping()) {
                    requiresLocking = true;
                }
            }
            if (requiresLocking) {
                session.log(SessionLog.WARNING, SessionLog.EJB_OR_METADATA, "locking_required_for_database_change_notification", descriptor.getJavaClass());
            }
        }
        final DatabaseField rowId = descriptor.buildField(new DatabaseField(ROWID));
        final List<DatabaseField> fields = new ArrayList<DatabaseField>();
        fields.add(rowId);
        // May already have the index if has inheritance.
        CacheIndex existingIndex = descriptor.getCachePolicy().getCacheIndex(fields);
        if (existingIndex == null) {
            if (descriptor.isChildDescriptor()) {
                existingIndex = descriptor.getInheritancePolicy().getRootParentDescriptor().getCachePolicy().getCacheIndex(fields);
            }
            if (existingIndex == null) {
                existingIndex = new CacheIndex(fields);
                existingIndex.setIsUpdateable(false);
                existingIndex.setIsInsertable(false);
            }
            descriptor.getCachePolicy().addCacheIndex(existingIndex);
        }
        
        final CacheIndex index = existingIndex;
        rowId.setInsertable(false);
        rowId.setUpdatable(false);
        rowId.setCreatable(false);
        descriptor.getFields().add(rowId);
        descriptor.getAllFields().add(rowId);
        
        final ValueReadQuery rowIdQuery = new ValueReadQuery();
        rowIdQuery.setName(ROWID);
        SQLSelectStatement sqlStatement = new SQLSelectStatement();
        sqlStatement.setWhereClause(descriptor.getObjectBuilder().getPrimaryKeyExpression());
        sqlStatement.addField(rowId);
        sqlStatement.addTable(descriptor.getTables().get(0));
        rowIdQuery.setSQLStatement(sqlStatement);
        sqlStatement.normalize(session, null);
        
        descriptor.getEventManager().addListener(new DescriptorEventAdapter() {
            @Override
            public void postMerge(DescriptorEvent event) {
                if ((event.getChangeSet() != null) && event.getChangeSet().hasChanges()) {
                    Object id = event.getChangeSet().getId();
                    CacheKey cacheKey = event.getChangeSet().getActiveCacheKey();
                    if (cacheKey == null) {
                        cacheKey = event.getSession().getParent().getIdentityMapAccessorInstance().getIdentityMapManager().getCacheKeyForObject(id, descriptor.getJavaClass(), descriptor, false);
                    }
                    cacheKey.setTransactionId(event.getSession().getProperty(ORA_TRANSACTION_ID));
                    if (event.getChangeSet().isNew()) {
                        AbstractRecord row = descriptor.getObjectBuilder().buildRowFromPrimaryKeyValues(id, event.getSession());
                        Object rowid = event.getSession().executeQuery(rowIdQuery, row);
                        CacheId indexValue = new CacheId(new Object[]{rowid});
                        event.getSession().getParent().getIdentityMapAccessorInstance().getIdentityMapManager().putCacheKeyByIndex(index, indexValue, cacheKey, descriptor);
                    }
                }
            }
            @Override
            public void postUpdate(DescriptorEvent event) {
                Object txId = event.getSession().getProperty(ORA_TRANSACTION_ID);
                if (txId == null) {
                    txId = event.getSession().executeQuery(transactionIdQuery);
                    event.getSession().setProperty(ORA_TRANSACTION_ID, txId);
                }
            }
        });
    }

    /**
     * INTERNAL:
     * Remove the event listener from the database.
     */
    public void remove(Session session) {
        if (this.register == null) {
            return;
        }
        AbstractSession databaseSession = (AbstractSession)session;
        Accessor accessor = databaseSession.getAccessor();
        accessor.incrementCallCount(databaseSession);
        try {
            OracleConnection connection = (OracleConnection)databaseSession.getServerPlatform().unwrapConnection(accessor.getConnection());
            databaseSession.log(SessionLog.FINEST, SessionLog.CONNECTION, "dcn_unregister");
            try {
                connection.unregisterDatabaseChangeNotification(this.register);
            } catch (SQLException exception) {
                throw DatabaseException.sqlException(exception, databaseSession.getAccessor(), databaseSession, false);
            }
        } finally {
            accessor.decrementCallCount();            
        }
    }
    
    /**
     * INTERNAL:
     * Return the database register.
     */
    public DatabaseChangeRegistration getRegister() {
        return register;
    }
    
    /**
     * INTERNAL:
     * Set the database register.
     */
    protected void setRegister(DatabaseChangeRegistration register) {
        this.register = register;
    }
    
    /**
     * INTERNAL:
     * Return the mapping of tables to descriptors.
     */
    public Map<DatabaseTable, ClassDescriptor> getDescriptorsByTable() {
        return descriptorsByTable;
    }
    
    /**
     * INTERNAL:
     * Set the mapping of tables to descriptors.
     */
    protected void setDescriptorsByTable(Map<DatabaseTable, ClassDescriptor> descriptorsByTable) {
        this.descriptorsByTable = descriptorsByTable;
    }
        
}
