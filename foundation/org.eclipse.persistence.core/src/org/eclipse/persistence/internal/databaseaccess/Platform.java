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
package org.eclipse.persistence.internal.databaseaccess;

import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.core.databaseaccess.CorePlatform;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.Session;

/**
 * Platform is private to TopLink. It encapsulates behavior specific to a datasource platform
 * (eg. Oracle, Sybase, DB2, Attunity, MQSeries), and provides the interface for TopLink to access this behavior.
 *
 * @see DatasourcePlatform
 * @see DatabasePlatform
 * @see org.eclipse.persistence.eis.EISPlatform
 *
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public interface Platform extends CorePlatform<ConversionManager>, Serializable, Cloneable {
    public Object clone();

    /**
     * Convert the object to the appropriate type by invoking the appropriate
     * ConversionManager method
     * @param object - the object that must be converted
     * @param javaClass - the class that the object must be converted to
     * @exception - ConversionException, all exceptions will be thrown as this type.
     * @return - the newly converted object
     */
    @Override
    public Object convertObject(Object sourceObject, Class javaClass) throws ConversionException;

    /**
     * Copy the state into the new platform.
     */
    public void copyInto(Platform platform);

    /**
     * The platform hold its own instance of conversion manager to allow customization.
     */
    @Override
    public ConversionManager getConversionManager();

    /**
     * The platform hold its own instance of conversion manager to allow customization.
     */
    public void setConversionManager(ConversionManager conversionManager);

    /**
     * Return the qualifier for the table. Required by some
     * databases such as Oracle and DB2
     */
    public String getTableQualifier();

    /**
     * Answer the timestamp from the server.
     */
    public java.sql.Timestamp getTimestampFromServer(AbstractSession session, String sessionName);

    /**
     * This method can be overridden by subclasses to return a
     * query that will return the timestamp from the server.
     * return null if the time should be the local time.
     */
    public ValueReadQuery getTimestampQuery();

    public boolean isH2();

    public boolean isAccess();

    public boolean isAttunity();

    public boolean isCloudscape();

    public boolean isDerby();

    public boolean isDB2();

    public boolean isDBase();

    public boolean isHANA();

    public boolean isHSQL();

    public boolean isInformix();

    public boolean isMaxDB();

    public boolean isMySQL();

    public boolean isODBC();

    public boolean isOracle();

    public boolean isOracle9();

    public boolean isOracle12();

    public boolean isPointBase();

    public boolean isSQLAnywhere();

    public boolean isSQLServer();

    public boolean isSybase();

    public boolean isSymfoware();

    public boolean isTimesTen();

    public boolean isTimesTen7();

    public boolean isPostgreSQL();

    /**
     * Allow the platform to initialize itself after login/init.
     */
    public void initialize();

    /**
     * Set the qualifier for the table. Required by some
     * databases such as Oracle and DB2
     */
    public void setTableQualifier(String qualifier);

    /**
     * Can override the default query for returning a timestamp from the server.
     * See: getTimestampFromServer
     */
    public void setTimestampQuery(ValueReadQuery tsQuery);

    /**
     * Add the parameter.
     * Convert the parameter to a string and write it.
     */
    public void appendParameter(Call call, Writer writer, Object parameter);

    /**
     * Allow for the platform to handle the representation of parameters specially.
     */
    public Object getCustomModifyValueForCall(Call call, Object value, DatabaseField field, boolean shouldBind);

    /**
     * Delimiter to use for fields and tables using spaces or other special values.
     *
     * Some databases use different delimiters for the beginning and end of the value.
     * This delimiter indicates the end of the value.
     */
    public String getEndDelimiter();

    /**
     * Delimiter to use for fields and tables using spaces or other special values.
     *
     * Some databases use different delimiters for the beginning and end of the value.
     * This delimiter indicates the start of the value.
     */
    public String getStartDelimiter();

    /**
     * Allow for the platform to handle the representation of parameters specially.
     */
    public boolean shouldUseCustomModifyForCall(DatabaseField field);

    /**
     * Get default sequence.
     * Sequence name shouldn't be altered -
     * don't do: getDefaultSequence().setName(newName).
     */
    public Sequence getDefaultSequence();

    /**
     * Set default sequence.
     * The sequence should have a unique name
     * that shouldn't be altered after the sequence has been set:
     * don't do: getDefaultSequence().setName(newName)).
     * Default constructors for Sequence subclasses
     * set name to "SEQ".
     */
    public void setDefaultSequence(Sequence sequence);

    /**
     * Add sequence.
     * The sequence should have a unique name
     * that shouldn't be altered after the sequence has been added -
     * don't do: getSequence(name).setName(newName))
     * Don't use if the session is connected.
     */
    public void addSequence(Sequence sequence);

    /**
     * Add sequence.
     * The sequence should have a unique name
     * that shouldn't be altered after the sequence has been added -
     * don't do: getSequence(name).setName(newName))
     * Use this method with isConnected parameter set to true
     * to add a sequence to connected session.
     * If sequencing is connected then the sequence is added only
     * if there is no sequence with the same name already in use.
     */
    public void addSequence(Sequence sequence, boolean isConnected);

    /**
     * Get sequence corresponding to the name.
     * The name shouldn't be altered -
     * don't do: getSequence(name).setName(newName)
     */
    public Sequence getSequence(String seqName);

    /**
     * Remove sequence corresponding to the name
     * (the sequence was added through addSequence method)
     * Don't use if the session is connected.
     */
    public Sequence removeSequence(String seqName);

    /**
     * Remove all sequences that were added through addSequence method.
     */
    public void removeAllSequences();

    /**
     * INTERNAL:
     * Returns a map of sequence names to Sequences (may be null).
     */
    public Map getSequences();

    /**
     * INTERNAL:
     * Used only for writing into XML or Java.
     */
    public Map getSequencesToWrite();

    /**
     * INTERNAL:
     * Used only for writing into XML or Java.
     */
    public Sequence getDefaultSequenceToWrite();

    /**
     * INTERNAL:
     * Used only for reading from XML.
     */
    public void setSequences(Map sequences);

    /**
     * INTERNAL:
     * Indicates whether defaultSequence is the same as platform default sequence.
     */
    public boolean usesPlatformDefaultSequence();

    /**
     * INTERNAL:
     * Initialize platform specific identity sequences.
     * This method is called from {@code EntityManagerSetupImpl} after login and optional schema generation.
     * Method is also called from {@code TableCreator} class during tables creation and update..
     * @param session Active database session (in connected state).
     * @param defaultIdentityGenerator Default identity generator sequence name.
     * @since 2.7
     */
    public void initIdentitySequences(final Session session, final String defaultIdentityGenerator);

    /**
     * INTERNAL:
     * Remove platform specific identity sequences for specified tables. Default identity sequences are restored.
     * Method is also called from {@code TableCreator} class during tables removal.
     * @param dbSession Active database session (in connected state).
     * @param defaultIdentityGenerator Default identity generator sequence name.
     * @param tableNames Set of table names to check for identity sequence removal.
     * @since 2.7
     */
    public void removeIdentitySequences(
            final Session session, final String defaultIdentityGenerator, final Set<String> tableNames);

}
