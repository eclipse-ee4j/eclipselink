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

import java.io.*;
import java.util.*;

import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sequencing.*;

/**
 * DatasourcePlatform is private to TopLink. It encapsulates behavior specific to a datasource platform
 * (eg. Oracle, Sybase, DB2, Attunity, MQSeries), and provides protocol for TopLink to access this behavior.
 *
 * @see DatabasePlatform
 * @see org.eclipse.persistence.eis.EISPlatform
 *
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class DatasourcePlatform implements Platform {

    /** Supporting name scopes in database by prefixing the table names with the table qualifier/creator. */
    protected String tableQualifier;

    /** Allow for conversion to be customized in the platform. */
    protected transient ConversionManager conversionManager;

    /** Store the query use to query the current server time. */
    protected ValueReadQuery timestampQuery;

    /** Operators specific to this platform */
    protected transient Map platformOperators;

    /** Store the list of Classes that can be converted to from the key. */
    protected Hashtable dataTypesConvertedFromAClass;

    /** Store the list of Classes that can be converted from to the key. */
    protected Hashtable dataTypesConvertedToAClass;

    /** Store default sequence */
    protected Sequence defaultSequence;

    /** Store map of sequence names to sequences */
    protected Map sequences;
    
    /** Delimiter to use for fields and tables using spaces or other special values */
    protected String startDelimiter = null;
    protected String endDelimiter = null;
    
    /** Ensures that only one thread at a time can add/remove sequences */
    protected Object sequencesLock = new Boolean(true);

    /** If the native sequence type is not supported, if table sequencing should be used. */
    protected boolean defaultNativeSequenceToTable;

    public DatasourcePlatform() {
        this.tableQualifier = "";
        this.startDelimiter = "";
        this.endDelimiter = "";
    }

    /**
     * Return if the native sequence type is not supported, if table sequencing should be used.
     */
    public boolean getDefaultNativeSequenceToTable() {
        return defaultNativeSequenceToTable;
    }

    /**
     * Set if the native sequence type is not supported, if table sequencing should be used.
     */
    public void setDefaultNativeSequenceToTable(boolean defaultNativeSequenceToTable) {
        this.defaultNativeSequenceToTable = defaultNativeSequenceToTable;
    }

    protected void addOperator(ExpressionOperator operator) {
        platformOperators.put(Integer.valueOf(operator.getSelector()), operator);
    }

    /**
     * Add the parameter.
     * Convert the parameter to a string and write it.
     */
    public void appendParameter(Call call, Writer writer, Object parameter) {
        String parameterValue = (String)getConversionManager().convertObject(parameter, ClassConstants.STRING);
        if (parameterValue == null) {
            parameterValue = "";
        }
        try {
            writer.write(parameterValue);
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    /**
     * Allow for the platform to handle the representation of parameters specially.
     */
    public Object getCustomModifyValueForCall(Call call, Object value, DatabaseField field, boolean shouldBind) {
        return value;
    }

    /**
     * Used by SQLCall.appendModify(..)
     * If the field should be passed to customModifyInDatabaseCall, retun true,
     * otherwise false.
     * Methods shouldCustomModifyInDatabaseCall and customModifyInDatabaseCall should be
     * kept in sync: shouldCustomModifyInDatabaseCall should return true if and only if the field
     * is handled by customModifyInDatabaseCall.
     */
    public boolean shouldUseCustomModifyForCall(DatabaseField field) {
        return false;
    }

    public Object clone() {
        try {
            DatasourcePlatform clone = (DatasourcePlatform)super.clone();
            clone.sequencesAfterCloneCleanup();
            return clone;
        } catch (CloneNotSupportedException exception) {
            ;//Do nothing
        }

        return null;
    }

    protected void sequencesAfterCloneCleanup() {
        Sequence defaultSequenceClone = null;
        if (hasDefaultSequence()) {
            defaultSequenceClone = (Sequence)getDefaultSequence().clone();
            setDefaultSequence(defaultSequenceClone);
        }
        if (getSequences() != null) {
            HashMap sequencesCopy = new HashMap(getSequences());
            HashMap sequencesDeepClone = new HashMap(getSequences().size());
            Iterator it = sequencesCopy.values().iterator();
            while (it.hasNext()) {
                Sequence sequence = (Sequence)it.next();
                if ((defaultSequenceClone != null) && (sequence == getDefaultSequence())) {
                    sequencesDeepClone.put(defaultSequenceClone.getName(), defaultSequenceClone);
                } else {
                    Sequence sequenceClone = (Sequence)sequence.clone();
                    if (sequenceClone instanceof DefaultSequence) {
                        if (!((DefaultSequence)sequenceClone).hasPreallocationSize()) {
                            continue;
                        }
                    }
                    sequencesDeepClone.put(sequenceClone.getName(), sequenceClone);
                }
            }
            this.setSequences(sequencesDeepClone);
        }
    }

    /**
     * Convert the object to the appropriate type by invoking the appropriate
     * ConversionManager method
     * @param object - the object that must be converted
     * @param javaClass - the class that the object must be converted to
     * @exception - ConversionException, all exceptions will be thrown as this type.
     * @return - the newly converted object
     */
    public Object convertObject(Object sourceObject, Class javaClass) throws ConversionException {
        return getConversionManager().convertObject(sourceObject, javaClass);
    }

    /**
     * Copy the state into the new platform.
     */
    public void copyInto(Platform platform) {
        if (!(platform instanceof DatasourcePlatform)) {
            return;
        }
        DatasourcePlatform datasourcePlatform = (DatasourcePlatform)platform;
        datasourcePlatform.setTableQualifier(getTableQualifier());
        datasourcePlatform.setTimestampQuery(this.timestampQuery);
        datasourcePlatform.setConversionManager(getConversionManager());
        if (hasDefaultSequence()) {
            datasourcePlatform.setDefaultSequence(getDefaultSequence());
        }
        datasourcePlatform.setSequences(getSequences());
        datasourcePlatform.sequencesAfterCloneCleanup();
        datasourcePlatform.defaultNativeSequenceToTable = this.defaultNativeSequenceToTable;
    }

    /**
     * The platform hold its own instance of conversion manager to allow customization.
     */
    public ConversionManager getConversionManager() {
        // Lazy init for serialization.
        if (conversionManager == null) {
            //Clone the default to allow customers to easily override the conversion manager
            conversionManager = (ConversionManager)ConversionManager.getDefaultManager().clone();
        }
        return conversionManager;
    }
    
    /**
     * The platform hold its own instance of conversion manager to allow customization.
     */
    public void setConversionManager(ConversionManager conversionManager) {
        this.conversionManager = conversionManager;
    }

    /**
     * Delimiter to use for fields and tables using spaces or other special values.
     * 
     * Some databases use different delimiters for the beginning and end of the value.
     * This delimiter indicates the end of the value.
     */
    public String getEndDelimiter() {
        return endDelimiter;
    }
    
    /**
     * Delimiter to use for fields and tables using spaces or other special values.
     * 
     * Some databases use different delimiters for the beginning and end of the value.
     * This delimiter indicates the end of the value.
     */
    public void setEndDelimiter(String endDelimiter) {
        this.endDelimiter = endDelimiter;
    }
    
    /**
     * Return the operator for the operator constant defined in ExpressionOperator.
     */
    public ExpressionOperator getOperator(int selector) {
        return (ExpressionOperator)getPlatformOperators().get(Integer.valueOf(selector));
    }

    /**
     * Return any platform-specific operators
     */
    public Map getPlatformOperators() {
        if (platformOperators == null) {
            synchronized (this) {
                if (platformOperators == null) {
                    initializePlatformOperators();
                }
            }
        }
        return platformOperators;
    }

    /**
     * OBSOLETE:
     * This method lazy initializes the select sequence number query.  It
     * allows for other queries to be used instead of the default one.
     */
    public ValueReadQuery getSelectSequenceQuery() {
        if (getDefaultSequence() instanceof QuerySequence) {
            return ((QuerySequence)getDefaultSequence()).getSelectQuery();
        } else {
            throw ValidationException.wrongSequenceType(Helper.getShortClassName(getDefaultSequence()), "getSelectQuery");
        }
    }

    public int getSequencePreallocationSize() {
        return getDefaultSequence().getPreallocationSize();
    }


    /**
     * Delimiter to use for fields and tables using spaces or other special values.
     * 
     * Some databases use different delimiters for the beginning and end of the value.
     * This delimiter indicates the start of the value.
     */
    public String getStartDelimiter() {
        return startDelimiter;
    }
    
    /**
     * Delimiter to use for fields and tables using spaces or other special values.
     * 
     * Some databases use different delimiters for the beginning and end of the value.
     * This delimiter indicates the start of the value.
     */
    public void setStartDelimiter(String startDelimiter) {
        this.startDelimiter = startDelimiter;
    }
    
    /**
     * Return the qualifier for the table. Required by some
     * databases such as Oracle and DB2
     */
    public String getTableQualifier() {
        return tableQualifier;
    }

    /**
     * Answer the timestamp from the server.
     */
    public java.sql.Timestamp getTimestampFromServer(AbstractSession session, String sessionName) {
        if (getTimestampQuery() == null) {
            return new java.sql.Timestamp(System.currentTimeMillis());
        } else {
            getTimestampQuery().setSessionName(sessionName);
            return (java.sql.Timestamp)session.executeQuery(getTimestampQuery());
        }
    }

    /**
     * This method can be overridden by subclasses to return a
     * query that will return the timestamp from the server.
     * return null if the time should be the local time.
     */
    public ValueReadQuery getTimestampQuery() {
        return timestampQuery;
    }

    /**
     * OBSOLETE:
     * This method lazy initializes the update sequence number query.  It
     * allows for other queries to be used instead of the default one.
     */
    public DataModifyQuery getUpdateSequenceQuery() {
        if (getDefaultSequence() instanceof QuerySequence) {
            return ((QuerySequence)getDefaultSequence()).getUpdateQuery();
        } else {
            throw ValidationException.wrongSequenceType(Helper.getShortClassName(getDefaultSequence()), "getUpdateQuery");
        }
    }

    /**
     * Initialize any platform-specific operators
     */
    protected void initializePlatformOperators() {
        this.platformOperators = new HashMap();

        // Outer join
        addOperator(ExpressionOperator.equalOuterJoin());

        // General
        addOperator(ExpressionOperator.toUpperCase());
        addOperator(ExpressionOperator.toLowerCase());
        addOperator(ExpressionOperator.chr());
        addOperator(ExpressionOperator.concat());
        addOperator(ExpressionOperator.hexToRaw());
        addOperator(ExpressionOperator.initcap());
        addOperator(ExpressionOperator.instring());
        addOperator(ExpressionOperator.soundex());
        addOperator(ExpressionOperator.leftPad());
        addOperator(ExpressionOperator.leftTrim());
        addOperator(ExpressionOperator.leftTrim2());
        addOperator(ExpressionOperator.replace());
        addOperator(ExpressionOperator.rightPad());
        addOperator(ExpressionOperator.rightTrim());
        addOperator(ExpressionOperator.rightTrim2());
        addOperator(ExpressionOperator.substring());
        addOperator(ExpressionOperator.substringSingleArg());
        addOperator(ExpressionOperator.toNumber());
        addOperator(ExpressionOperator.toChar());
        addOperator(ExpressionOperator.toCharWithFormat());
        addOperator(ExpressionOperator.translate());
        addOperator(ExpressionOperator.trim());
        addOperator(ExpressionOperator.trim2());
        addOperator(ExpressionOperator.ascii());
        addOperator(ExpressionOperator.length());
        addOperator(ExpressionOperator.locate());
        addOperator(ExpressionOperator.locate2());
        addOperator(ExpressionOperator.nullIf());
        addOperator(ExpressionOperator.ifNull());
        addOperator(ExpressionOperator.cast());
        addOperator(ExpressionOperator.regexp());
        addOperator(ExpressionOperator.union());
        addOperator(ExpressionOperator.unionAll());
        addOperator(ExpressionOperator.intersect());
        addOperator(ExpressionOperator.intersectAll());
        addOperator(ExpressionOperator.except());
        addOperator(ExpressionOperator.exceptAll());

        // Date
        addOperator(ExpressionOperator.addMonths());
        addOperator(ExpressionOperator.dateToString());
        addOperator(ExpressionOperator.lastDay());
        addOperator(ExpressionOperator.monthsBetween());
        addOperator(ExpressionOperator.nextDay());
        addOperator(ExpressionOperator.roundDate());
        addOperator(ExpressionOperator.toDate());
        addOperator(ExpressionOperator.today());
        addOperator(ExpressionOperator.currentDate());
        addOperator(ExpressionOperator.currentTime());
        addOperator(ExpressionOperator.extract());

        // Math
        addOperator(ExpressionOperator.simpleMath(ExpressionOperator.Add, "+"));
        addOperator(ExpressionOperator.simpleMath(ExpressionOperator.Subtract, "-"));
        addOperator(ExpressionOperator.simpleMath(ExpressionOperator.Multiply, "*"));
        addOperator(ExpressionOperator.simpleMath(ExpressionOperator.Divide, "/"));
        addOperator(ExpressionOperator.negate());

        addOperator(ExpressionOperator.ceil());
        addOperator(ExpressionOperator.cos());
        addOperator(ExpressionOperator.cosh());
        addOperator(ExpressionOperator.abs());
        addOperator(ExpressionOperator.acos());
        addOperator(ExpressionOperator.asin());
        addOperator(ExpressionOperator.atan());
        addOperator(ExpressionOperator.exp());
        addOperator(ExpressionOperator.sqrt());
        addOperator(ExpressionOperator.floor());
        addOperator(ExpressionOperator.ln());
        addOperator(ExpressionOperator.log());
        addOperator(ExpressionOperator.mod());
        addOperator(ExpressionOperator.power());
        addOperator(ExpressionOperator.round());
        addOperator(ExpressionOperator.sign());
        addOperator(ExpressionOperator.sin());
        addOperator(ExpressionOperator.sinh());
        addOperator(ExpressionOperator.tan());
        addOperator(ExpressionOperator.tanh());
        addOperator(ExpressionOperator.trunc());
        addOperator(ExpressionOperator.greatest());
        addOperator(ExpressionOperator.least());
        
        addOperator(ExpressionOperator.standardDeviation());
        addOperator(ExpressionOperator.variance());

        // Object-relational
        addOperator(ExpressionOperator.deref());
        addOperator(ExpressionOperator.ref());
        addOperator(ExpressionOperator.refToHex());
        addOperator(ExpressionOperator.value());
        
        addOperator(ExpressionOperator.coalesce());
        addOperator(ExpressionOperator.caseStatement());
        addOperator(ExpressionOperator.caseConditionStatement());
    }
    
    /**
     * INTERNAL:
     * Allow the platform to initialize the CRUD queries to defaults.
     * This is mainly used by EIS platforms, but could be used by relational ones for special behavior.
     */
    public void initializeDefaultQueries(DescriptorQueryManager queryManager, AbstractSession session) {
        
    }
    
    public boolean isAccess() {
        return false;
    }

    public boolean isAttunity() {
        return false;
    }

    public boolean isCloudscape() {
        return false;
    }

    public boolean isDerby() {
        return false;
    }

    public boolean isDB2() {
        return false;
    }
    
     public boolean isHANA() {
         return false;
     }

    public boolean isH2() {
        return false;
    }

    public boolean isDBase() {
        return false;
    }

    public boolean isHSQL() {
        return false;
    }

    public boolean isInformix() {
        return false;
    }

    public boolean isMySQL() {
        return false;
    }

    public boolean isODBC() {
        return false;
    }

    public boolean isOracle() {
        return false;
    }

    public boolean isOracle9() {
        return false;
    }

    public boolean isPervasive(){
        return false;
    }

    public boolean isPostgreSQL(){
    	return false;
    }
    
    public boolean isPointBase() {
        return false;
    }

    public boolean isSQLAnywhere() {
        return false;
    }

    public boolean isFirebird() {
        return false;
    }    
    
    public boolean isSQLServer() {
        return false;
    }

    public boolean isSybase() {
        return false;
    }

    public boolean isSymfoware() {
        return false;
    }

    public boolean isTimesTen() {
        return false;
    }

    public boolean isTimesTen7() {
        return false;
    }
    
    public boolean isMaxDB() {
        return false;
    }

    /**
     * Allow the platform to initialize itself after login/init.
     */
    public void initialize() {
        
    }
    
    /**
     * OBSOLETE:
     * Can override the default query for returning the sequence numbers.
     * This query must be a valid query that has one parameter which is
     * the sequence name.
     */
    public void setSelectSequenceNumberQuery(ValueReadQuery seqQuery) {
        if (getDefaultSequence() instanceof QuerySequence) {
            ((QuerySequence)getDefaultSequence()).setSelectQuery(seqQuery);
        } else {
            throw ValidationException.wrongSequenceType(Helper.getShortClassName(getDefaultSequence()), "setSelectQuery");
        }
    }

    /**
     *    Set the number of sequence values to preallocate.
     *    Preallocating sequence values can greatly improve insert performance.
     */
    public void setSequencePreallocationSize(int size) {
        getDefaultSequence().setPreallocationSize(size);
    }

    /**
     * Set the qualifier for the table. Required by some
     * databases such as Oracle and DB2
     */
    public void setTableQualifier(String qualifier) {
        tableQualifier = qualifier;
    }

    /**
     * Can override the default query for returning a timestamp from the server.
     * See: getTimestampFromServer
     */
    public void setTimestampQuery(ValueReadQuery tsQuery) {
        timestampQuery = tsQuery;
    }

    /**
     * This method sets the update sequence number query.  It
     * allows for other queries to be used instead of the default one.
     */
    public void setUpdateSequenceQuery(DataModifyQuery updateSequenceNumberQuery) {
        if (getDefaultSequence() instanceof QuerySequence) {
            ((QuerySequence)getDefaultSequence()).setUpdateQuery(updateSequenceNumberQuery);
        } else {
            throw ValidationException.wrongSequenceType(Helper.getShortClassName(getDefaultSequence()), "setUpdateQuery");
        }
    }

    public String toString() {
        return Helper.getShortClassName(this.getClass());
    }

    /**
     * PUBLIC:
     * Return the list of Classes that can be converted to from the passed in javaClass.
     * @param javaClass - the class that is converted from
     * @return - a vector of classes
     */
    public Vector getDataTypesConvertedFrom(Class javaClass) {
        return getConversionManager().getDataTypesConvertedFrom(javaClass);
    }

    /**
     * PUBLIC:
     * Return the list of Classes that can be converted from to the passed in javaClass.
     * @param javaClass - the class that is converted to
     * @return - a vector of classes
     */
    public Vector getDataTypesConvertedTo(Class javaClass) {
        return getConversionManager().getDataTypesConvertedTo(javaClass);
    }

    /**
     * Get default sequence
     */
    public Sequence getDefaultSequence() {
        if (!hasDefaultSequence()) {
            setDefaultSequence(createPlatformDefaultSequence());
        }
        return defaultSequence;
    }

    /**
     * Get default sequence
     */
    public boolean hasDefaultSequence() {
        return defaultSequence != null;
    }

    /**
     * Set default sequence. In case the passed sequence is of type DefaultSequence - use platformDefaultSequence
     * with name and size of the passed sequence.
     */
    public void setDefaultSequence(Sequence sequence) {
        if (sequence instanceof DefaultSequence) {
            Sequence platformDefaultSequence = createPlatformDefaultSequence();
            if (platformDefaultSequence != null) {
                platformDefaultSequence.setName(sequence.getName());
                if (((DefaultSequence)sequence).hasPreallocationSize()) {
                    platformDefaultSequence.setPreallocationSize(sequence.getPreallocationSize());
                }
            }
            defaultSequence = platformDefaultSequence;
        } else {
            defaultSequence = sequence;
        }
    }

    /**
     * Add sequence corresponding to the name
     */
    public void addSequence(Sequence sequence) {
        addSequence(sequence, false);
    }

    /**
     * Add sequence corresponding to the name.
     * Use this method with isSessionConnected parameter set to true
     * to add a sequence to connected session.
     * If the session is connected then the sequence is added only 
     * if there is no sequence with the same name already in use.
     */
    public void addSequence(Sequence sequence, boolean isSessionConnected) {
        synchronized(sequencesLock) {
            if (isSessionConnected) {
                if (this.sequences == null) {
                    this.sequences = new HashMap();
                    this.sequences.put(sequence.getName(), sequence);
                } else {
                    if (!this.sequences.containsKey(sequence.getName())) {
                        Map newSequences = (Map)((HashMap)this.sequences).clone();
                        newSequences.put(sequence.getName(), sequence);
                        this.sequences = newSequences;
                    }
                }
            } else {
                if (this.sequences == null) {
                    this.sequences = new HashMap();
                }
                this.sequences.put(sequence.getName(), sequence);
            }
        }
    }

    /**
     * Get sequence corresponding to the name
     */
    public Sequence getSequence(String seqName) {
        if (seqName == null) {
            return getDefaultSequence();
        } else {
            if (this.sequences != null) {
                return (Sequence)this.sequences.get(seqName);
            } else {
                return null;
            }
        }
    }

    /**
     * INTERNAL:
     * Create platform-default Sequence
     */
    protected Sequence createPlatformDefaultSequence() {
        throw ValidationException.createPlatformDefaultSequenceUndefined(Helper.getShortClassName(this));
    }

    /**
     * Remove sequence corresponding to name.
     * Doesn't remove default sequence.
     */
    public Sequence removeSequence(String seqName) {
        if (this.sequences != null) {
            synchronized(sequencesLock) {
                return (Sequence)this.sequences.remove(seqName);
            }
        } else {
            return null;
        }
    }

    /**
     * Remove all sequences, but the default one.
     */
    public void removeAllSequences() {
        this.sequences = null;
    }

    /**
     * INTERNAL:
     * Returns a map of sequence names to Sequences (may be null).
     */
    public Map getSequences() {
        return this.sequences;
    }

    /**
     * INTERNAL:
     * Used only for writing into XML or Java.
     */
    public Map getSequencesToWrite() {
        if ((getSequences() == null) || getSequences().isEmpty()) {
            return null;
        }
        Map sequencesCopy = new HashMap(getSequences());
        Map sequencesToWrite = new HashMap();
        Iterator it = sequencesCopy.values().iterator();
        while (it.hasNext()) {
            Sequence sequence = (Sequence)it.next();
            if (!(sequence instanceof DefaultSequence) || ((DefaultSequence)sequence).hasPreallocationSize()) {
                sequencesToWrite.put(sequence.getName(), sequence);
            }
        }
        return sequencesToWrite;
    }

    /**
     * INTERNAL:
     * Used only for writing into XML or Java.
     */
    public Sequence getDefaultSequenceToWrite() {
        if (usesPlatformDefaultSequence()) {
            return null;
        } else {
            return getDefaultSequence();
        }
    }

    /**
     * INTERNAL:
     * Sets sequences - for XML support only
     */
    public void setSequences(Map sequences) {
        this.sequences = sequences;
    }
    
    /**
     * INTERNAL:
     * Indicates whether defaultSequence is the same as platform default sequence.
     */
    public boolean usesPlatformDefaultSequence() {
        if (!hasDefaultSequence()) {
            return true;
        } else {
            return getDefaultSequence().equals(createPlatformDefaultSequence());
        }
    }
    
    /**
     * INTERNAL:
     * Returns the correct quote character to use around SQL Identifiers that contain
     * Space characters
     * @deprecated
     * @see getStartDelimiter()
     * @see getEndDelimiter()
     * @return The quote character for this platform
     */
    public String getIdentifierQuoteCharacter() {
        return "";
    }
    
    /**
     * INTERNAL:
     */
    public ConnectionCustomizer createConnectionCustomizer(Accessor accessor, AbstractSession session) {
        return null;
    }
    
    /**
     * Allows query prepare to be disable in the platform.
     * This is required for some EIS platforms, that cannot prepare the call.
     */
    public boolean shouldPrepare(DatabaseQuery query) {
        return true;
    }
    
    /**
     * Return if the database requires the ORDER BY fields to be part of the select clause.
     */
    public boolean shouldSelectIncludeOrderBy() {
        return false;
    }
    
    /**
     * Return if the database requires the ORDER BY fields to be part of the select clause.
     */
    public boolean shouldSelectDistinctIncludeOrderBy() {
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether a separate transaction is required for NativeSequence.
     * This method is to be used *ONLY* by sequencing classes
     */
    public boolean shouldNativeSequenceUseTransaction() {
        return false;
    }

    /**
     * INTERNAL:
     * Indicates whether the platform supports identity.
     * This method is to be used *ONLY* by sequencing classes
     */
    public boolean supportsIdentity() {
        return false;
    }

    public boolean supportsNativeSequenceNumbers() {
        return this.supportsSequenceObjects() || this.supportsIdentity();
    }

    /**
     *  INTERNAL:
     *  Indicates whether the platform supports sequence objects.
     *  This method is to be used *ONLY* by sequencing classes
     */
    public boolean supportsSequenceObjects() {
        return false;
    }

    /**
     * INTERNAL:
     * Returns query used to read value generated by sequence object (like Oracle sequence).
     * This method is called when sequence object NativeSequence is connected,
     * the returned query used until the sequence is disconnected.
     * If the platform supportsSequenceObjects then (at least) one of buildSelectQueryForSequenceObject
     * methods should return non-null query.
     */
    public ValueReadQuery buildSelectQueryForSequenceObject() {
        return null;
    }

    /**
     * INTERNAL:
     * Returns query used to read value generated by sequence object (like Oracle sequence).
     * In case the other version of this method (taking no parameters) returns null,
     * this method is called every time sequence object NativeSequence reads.
     * If the platform supportsSequenceObjects then (at least) one of buildSelectQueryForSequenceObject
     * methods should return non-null query.
     */
    public ValueReadQuery buildSelectQueryForSequenceObject(String qualifiedSeqName, Integer size) {
        return null;
    }

    /**
     * INTERNAL:
     * Returns query used to read back the value generated by Identity.
     * This method is called when identity NativeSequence is connected,
     * the returned query used until the sequence is disconnected.
     * If the platform supportsIdentity then (at least) one of buildSelectQueryForIdentity
     * methods should return non-null query.
     */
    public ValueReadQuery buildSelectQueryForIdentity() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Returns query used to read back the value generated by Identity.
     * In case the other version of this method (taking no parameters) returns null,
     * this method is called every time identity NativeSequence reads.
     * If the platform supportsIdentity then (at least) one of buildSelectQueryForIdentity
     * methods should return non-null query.
     */
    public ValueReadQuery buildSelectQueryForIdentity(String seqName, Integer size) {
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the correct call type for the native query string.
     * This allows EIS platforms to use different types of native calls.
     */
    public DatasourceCall buildNativeCall(String queryString) {
        return new SQLCall(queryString);
    }
}
