/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     09/29/2016-2.7 Tomas Kraus
//       - 426852: @GeneratedValue(strategy=GenerationType.IDENTITY) support in Oracle 12c
package org.eclipse.persistence.platform.database.oracle;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.EmptyRecord;
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
        return (String)(new ValueReadQuery(sql)).execute((AbstractSession)session, EmptyRecord.getEmptyRecord());
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

}
