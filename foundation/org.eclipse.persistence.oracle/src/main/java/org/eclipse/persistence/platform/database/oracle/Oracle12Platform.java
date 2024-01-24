/*
 * Copyright (c) 2014, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     09/29/2016-2.7 Tomas Kraus
//       - 426852: @GeneratedValue(strategy=GenerationType.IDENTITY) support in Oracle 12c
package org.eclipse.persistence.platform.database.oracle;

import java.io.IOException;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose:</b>
 * Supports usage of certain Oracle JDBC specific APIs for the Oracle 12 database.<br>
 * Identity column:<br>
 * {@code GENERATED [ ALWAYS | BY DEFAULT [ ON NULL ] ] AS IDENTITY [ ( identity_options ) ]}
 */
public class Oracle12Platform extends Oracle11Platform {

    /** Table name to identity sequence name storage. */
    private final Map<String, String> identitySequences;

    public Oracle12Platform() {
        super();
        supportsIdentity = true;
        identitySequences = new ConcurrentHashMap<>();
    }

    /**
     * INTERNAL:
     * Check whether current platform is Oracle 12c or later.
     * @return Always returns {@code true} for instances of Oracle 12c platform.
     * @since 2.7
     */
    @Override
    public boolean isOracle12() {
        return true;
    }

    /**
     * INTERNAL:
     * Initialize platform specific identity sequences.
     * @param session Active database session (in connected state).
     * @param defaultIdentityGenerator Default identity generator sequence name.
     * @since 2.7
     */
    @Override
    public void initIdentitySequences(final Session session, final String defaultIdentityGenerator) {
        if (sequences != null && sequences.containsKey(defaultIdentityGenerator)) {
            for (final ClassDescriptor descriptor : session.getDescriptors().values()) {
                final Sequence sequence = descriptor.getSequence();
                if (sequence != null && defaultIdentityGenerator.equals(sequence.getName())) {
                    final String tableName = descriptor.getTableName();
                    final String seqName = getIdentitySequence(tableName, session);
                    if (seqName != null) {
                        final NativeSequence newSequence = new NativeSequence(seqName, 1, true);
                        newSequence.setShouldAcquireValueAfterInsert(true);
                        newSequence.onConnect(this);
                        descriptor.setSequence(newSequence);
                        descriptor.setSequenceNumberName(seqName);
                        identitySequences.put(tableName, seqName);
                        addSequence(newSequence);
                        if (session.getSessionLog().shouldLog(SessionLog.FINE)) {
                            session.getSessionLog().log(SessionLog.FINE, "platform_ora_init_id_seq",
                                    new Object[] {defaultIdentityGenerator, seqName, tableName});
                        }
                    }
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Remove platform specific identity sequence for specified table. Default identity sequence is restored.
     * @param session Active database session (in connected state).
     * @param defaultIdentityGenerator Default identity generator sequence name.
     * @param tableNames Set of table names to check for identity sequence removal.
     * @since 2.7
     */
    @Override
    public void removeIdentitySequences(final Session session, final String defaultIdentityGenerator, final Set<String> tableNames) {
        if (sequences != null && sequences.containsKey(defaultIdentityGenerator)) {
            final Sequence defaultSeq = getSequence(defaultIdentityGenerator);
            for (final ClassDescriptor descriptor : session.getDescriptors().values()) {
                final String tableName = descriptor.getTableName();
                if (tableName != null && identitySequences.containsKey(tableName)) {
                    final String seqName = identitySequences.remove(tableName);
                    removeSequence(seqName);
                    descriptor.setSequence(defaultSeq);
                    descriptor.setSequenceNumberName(defaultIdentityGenerator);
                    if (session.getSessionLog().shouldLog(SessionLog.FINE)) {
                        session.getSessionLog().log(SessionLog.FINE, "platform_ora_remove_id_seq",
                                new Object[] {seqName, defaultIdentityGenerator, tableName});
                    }
                }
            }
        }
    }

    /**
     * Get sequence name corresponding to the table name.
     * @param tableName Name of the table.
     * @param session Active data source session.
     * @return Sequence name corresponding to the table name or {@code null} if no such sequence exists.
     * @since 2.7
     */
    private String getIdentitySequence(final String tableName, final Session session) {
        // TABLE_NAME values are converted to upper case by default.
        // Also TableDefinition.buildCreationWriter(AbstractSession,Writer) does not have support for quoting table names
        // to make them case sensitive on Oracle DB.
        final String sql = "SELECT SEQUENCE_NAME FROM USER_TAB_IDENTITY_COLS WHERE TABLE_NAME='" + tableName.toUpperCase() + "'";
        final ValueReadQuery query = new ValueReadQuery(sql);
        return (String) session.executeQuery(query);
    }

    /**
     * INTERNAL:
     * Append the receiver's field 'identity' constraint clause to a writer.
     * @param writer Target writer.
     * @since 2.7
     */
    @Override
    public void printFieldIdentityClause(final Writer writer) throws ValidationException {
        try {
            writer.write(" GENERATED AS IDENTITY");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * INTERNAL:
     * This method builds a Struct using the unwrapped connection within the session
     * @return Struct
     */
    @Override
    public Struct createStruct(String structTypeName, Object[] attributes, AbstractRecord row, List<DatabaseField> orderedFields, AbstractSession session, Connection connection) throws SQLException {
        for (int index = 0; index < orderedFields.size(); index++) {
            DatabaseField field = orderedFields.get(index);
            if (row.getField(field) != null && row.getField(field).getTypeName() != null) {
                if (ClassConstants.BLOB.getTypeName().equals(row.getField(field).getTypeName())) {
                    Blob blob = connection.createBlob();
                    blob.setBytes(1L, (byte[]) row.get(field));
                    attributes[index] = blob;
                } else if (ClassConstants.CLOB.getTypeName().equals(row.getField(field).getTypeName())) {
                    Clob clob = connection.createClob();
                    clob.setString(1L, (String) attributes[index]);
                    attributes[index] = clob;
                }
            } else {
                attributes[index] = row.get(field);
            }
        }
        return createStruct(structTypeName, attributes, connection);
    }

    /**
     * Create java.sql.Struct from given parameters.
     * @param structTypeName - the SQL type name of the SQL structured type that this Struct object maps to.
     * @param attributes - the attributes that populate the returned object
     * @param connection - DB connection
     * @return Struct
     */
    @Override
    public Struct createStruct(String structTypeName, Object[] attributes, Connection connection) throws SQLException {
        return connection.createStruct(structTypeName, attributes);
    }

    /**
     * INTERNAL:
     * This method returns the query to select the UUID
     * from the server for Oracle.
     */
    @Override
    public ValueReadQuery getUUIDQuery() {
        if (uuidQuery == null) {
            uuidQuery = new ValueReadQuery();
            uuidQuery.setSQLString("SELECT LOWER(REGEXP_REPLACE(RAWTOHEX(SYS_GUID()), '([A-F0-9]{8})([A-F0-9]{4})([A-F0-9]{4})([A-F0-9]{4})([A-F0-9]{12})', '\\1-\\2-\\3-\\4-\\5')) AS uuid FROM dual");
            uuidQuery.setAllowNativeSQLQuery(true);
        }
        return uuidQuery;
    }
}
