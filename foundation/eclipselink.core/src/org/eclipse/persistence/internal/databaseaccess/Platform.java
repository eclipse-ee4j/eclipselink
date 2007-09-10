/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.databaseaccess;

import java.io.*;
import java.util.Map;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * Platform is private to TopLink. It encapsulates behavior specific to a datasource platform
 * (eg. Oracle, Sybase, DB2, Attunity, MQSeries), and provides the interface for TopLink to access this behavior.
 *
 * @see DatasourcePlatform
 * @see DatabasePlatform
 * @see org.eclipse.persistence.eis.EISPlatform
 * @see deprecated.xml.XMLPlatform
 * @see deprecated.sdk.SDKPlatform
 *
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public interface Platform extends Serializable, Cloneable {
    public Object clone();

    /**
     * Convert the object to the appropriate type by invoking the appropriate
     * ConversionManager method
     * @param object - the object that must be converted
     * @param javaClass - the class that the object must be converted to
     * @exception - ConversionException, all exceptions will be thrown as this type.
     * @return - the newly converted object
     */
    public Object convertObject(Object sourceObject, Class javaClass) throws ConversionException;

    /**
     * Copy the state into the new platform.
     */
    public void copyInto(Platform platform);

    /**
     * The platform hold its own instance of conversion manager to allow customization.
     */
    public ConversionManager getConversionManager();

    /**
     * The platform hold its own instance of conversion manager to allow customization.
     */
    public void setConversionManager(ConversionManager conversionManager);

    /**
     * OBSOLETE:
     * This method lazy initializes the select sequence number query.  It
     * allows for other queries to be used instead of the default one.
     * @deprecated use ((QuerySequence)getDefaultSequence()).getSelectQuery() instead
     */
    public ValueReadQuery getSelectSequenceQuery();

    /**
     * OBSOLETE:
     * @deprecated use getDefaultSequence().getPreallocationSize() instead
     */
    public int getSequencePreallocationSize();

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

    /**
     * OBSOLETE:
     * This method lazy initializes the update sequence number query.  It
     * allows for other queries to be used instead of the default one.
     * @deprecated use ((QuerySequence)getDefaultSequence()).getUpdateQuery() instead
     */
    public DataModifyQuery getUpdateSequenceQuery();

    public boolean isAccess();

    public boolean isAttunity();

    public boolean isCloudscape();

    public boolean isDerby();

    public boolean isDB2();

    public boolean isDBase();

    public boolean isHSQL();

    public boolean isInformix();

    public boolean isMySQL();

    public boolean isODBC();

    public boolean isOracle();

    public boolean isPointBase();

    public boolean isSQLAnywhere();

    public boolean isSQLServer();

    public boolean isSybase();

    public boolean isTimesTen();

    public boolean isPostgreSQL();

    /**
     * OBSOLETE:
     * Can override the default query for returning the sequence numbers.
     * This query must be a valid query that has one parameter which is
     * the sequence name.
     * @deprecated use ((QuerySequence)getDefaultSequence()).setSelectQuery(ValueReadQuery) instead
     */
    public void setSelectSequenceNumberQuery(ValueReadQuery seqQuery);

    /**
     * OBSOLETE:
     * Set the number of sequence values to preallocate.
     * Preallocating sequence values can greatly improve insert performance.
     * @deprecated use getDefaultSequence().setPreallocationSize(int) instead
     */
    public void setSequencePreallocationSize(int size);

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
     * OBSOLETE:
     * This method sets the update sequence number query.  It
     * allows for other queries to be used instead of the default one.
     * @deprecated use ((QuerySequence)getDefaultSequence()).setUpdateQuery(DataModifyQuery) instead
     */
    public void setUpdateSequenceQuery(DataModifyQuery updateSequenceNumberQuery);

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
     */
    public void addSequence(Sequence sequence);

    /**
     * Get sequence corresponding to the name.
     * The name shouldn't be altered -
     * don't do: getSequence(name).setName(newName)
     */
    public Sequence getSequence(String seqName);

    /**
     * Remove sequence corresponding to the name
     * (the sequence was added through addSequence method)
     */
    public Sequence removeSequence(String seqName);

    /**
     * Remove all sequences that were added throud addSequence method.
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
     * Platform specific sequencing initialization.
     * This internal method should only be called by SequencingManager.
     */
    public void platformSpecificSequencingInitialization(DatabaseSession session);
}
