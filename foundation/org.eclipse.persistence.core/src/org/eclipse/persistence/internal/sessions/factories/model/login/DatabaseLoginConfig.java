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
package org.eclipse.persistence.internal.sessions.factories.model.login;

import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequencingConfig;

/**
 * INTERNAL:
 */
public class DatabaseLoginConfig extends LoginConfig {
    private String m_driverClass;
    private String m_connectionURL;
    private String m_datasource;
    private boolean m_bindAllParameters;
    private boolean m_cacheAllStatements;
    private boolean m_byteArrayBinding;
    private boolean m_stringBinding;
    private boolean m_streamsForBinding;
    private boolean m_forceFieldNamesToUppercase;
    private boolean m_optimizeDataConversion;
    private boolean m_trimStrings;
    private boolean m_batchWriting;
    private boolean m_jdbcBatchWriting;
    private Integer m_maxBatchWritingSize;
    private boolean m_nativeSQL;
    private Integer m_lookupType;
    private StructConverterConfig m_structConverterConfig;

    private Boolean m_validateConnectionHealthOnError;
    private Integer m_delayBetweenConnectionAttempts;
    private Integer m_queryRetryAttemptCount;
    private String pingSQL;

    public DatabaseLoginConfig() {
        super();
    }

    public void setDriverClass(String driverClass) {
        m_driverClass = driverClass;
    }

    public String getDriverClass() {
        return m_driverClass;
    }

    public void setConnectionURL(String connectionURL) {
        m_connectionURL = connectionURL;
    }

    public String getConnectionURL() {
        return m_connectionURL;
    }

    public void setDatasource(String datasource) {
        m_datasource = datasource;
    }

    public String getDatasource() {
        return m_datasource;
    }

    public void setNativeSequencing(boolean nativeSequencing) {
        getSequencingConfigNonNull().setNativeSequencing(nativeSequencing);
    }

    public boolean getNativeSequencing() {
        return getSequencingConfigNonNull().getNativeSequencing();
    }

    public void setSequencePreallocationSize(Integer sequencePreallocationSize) {
        getSequencingConfigNonNull().setSequencePreallocationSize(sequencePreallocationSize);
    }

    public Integer getSequencePreallocationSize() {
        return getSequencingConfigNonNull().getSequencePreallocationSize();
    }

    public void setSequenceTable(String sequenceTable) {
        getSequencingConfigNonNull().setSequenceTable(sequenceTable);
    }

    public String getSequenceTable() {
        return getSequencingConfigNonNull().getSequenceTable();
    }

    public void setSequenceNameField(String sequenceNameField) {
        getSequencingConfigNonNull().setSequenceNameField(sequenceNameField);
    }

    public String getSequenceNameField() {
        return getSequencingConfigNonNull().getSequenceNameField();
    }

    public void setSequenceCounterField(String sequenceCounterField) {
        getSequencingConfigNonNull().setSequenceCounterField(sequenceCounterField);
    }

    public String getSequenceCounterField() {
        return getSequencingConfigNonNull().getSequenceCounterField();
    }

    public void setBindAllParameters(boolean bindAllParameters) {
        m_bindAllParameters = bindAllParameters;
    }

    public boolean getBindAllParameters() {
        return m_bindAllParameters;
    }

    public void setCacheAllStatements(boolean cacheAllStatements) {
        m_cacheAllStatements = cacheAllStatements;
    }

    public boolean getCacheAllStatements() {
        return m_cacheAllStatements;
    }

    public void setByteArrayBinding(boolean byteArrayBinding) {
        m_byteArrayBinding = byteArrayBinding;
    }

    public boolean getByteArrayBinding() {
        return m_byteArrayBinding;
    }

    public void setStringBinding(boolean stringBinding) {
        m_stringBinding = stringBinding;
    }

    public boolean getStringBinding() {
        return m_stringBinding;
    }

    public void setStreamsForBinding(boolean streamsForBinding) {
        m_streamsForBinding = streamsForBinding;
    }

    public boolean getStreamsForBinding() {
        return m_streamsForBinding;
    }

    public void setForceFieldNamesToUppercase(boolean forceFieldNamesToUppercase) {
        m_forceFieldNamesToUppercase = forceFieldNamesToUppercase;
    }

    public boolean getForceFieldNamesToUppercase() {
        return m_forceFieldNamesToUppercase;
    }

    public void setOptimizeDataConversion(boolean optimizeDataConversion) {
        m_optimizeDataConversion = optimizeDataConversion;
    }

    public boolean getOptimizeDataConversion() {
        return m_optimizeDataConversion;
    }

    public void setTrimStrings(boolean trimStrings) {
        m_trimStrings = trimStrings;
    }

    public boolean getTrimStrings() {
        return m_trimStrings;
    }

    public void setBatchWriting(boolean batchWriting) {
        m_batchWriting = batchWriting;
    }

    public boolean getBatchWriting() {
        return m_batchWriting;
    }

    public void setJdbcBatchWriting(boolean jdbcBatchWriting) {
        m_jdbcBatchWriting = jdbcBatchWriting;
    }

    public boolean getJdbcBatchWriting() {
        return m_jdbcBatchWriting;
    }

    public void setMaxBatchWritingSize(Integer maxBatchWritingSize) {
        m_maxBatchWritingSize = maxBatchWritingSize;
    }

    public Integer getMaxBatchWritingSize() {
        return m_maxBatchWritingSize;
    }

    public void setNativeSQL(boolean nativeSQL) {
        m_nativeSQL = nativeSQL;
    }

    public boolean getNativeSQL() {
        return m_nativeSQL;
    }

    public SequencingConfig getSequencingConfigNonNull() {
        if (getSequencingConfig() == null) {
            setSequencingConfig(new SequencingConfig());
        }
        return getSequencingConfig();
    }

    public void setLookupType(Integer lookupType) {
        m_lookupType = lookupType;
    }

    public Integer getLookupType() {
        return m_lookupType;
    }
    
    public void setStructConverterConfig(StructConverterConfig converterConfig) {
        m_structConverterConfig = converterConfig;
    }

    public StructConverterConfig getStructConverterConfig() {
        return m_structConverterConfig;
    }
    public Integer getDelayBetweenConnectionAttempts() {
        return m_delayBetweenConnectionAttempts;
    }

    public void setDelayBetweenConnectionAttempts(Integer betweenConnectionAttempts) {
        m_delayBetweenConnectionAttempts = betweenConnectionAttempts;
    }

    public Integer getQueryRetryAttemptCount() {
        return m_queryRetryAttemptCount;
    }

    public void setQueryRetryAttemptCount(Integer retryAttemptCount) {
        m_queryRetryAttemptCount = retryAttemptCount;
    }

    public Boolean isConnectionHealthValidatedOnError() {
        return m_validateConnectionHealthOnError;
    }

    public void setConnectionHealthValidatedOnError(Boolean connectionHealthOnError) {
        m_validateConnectionHealthOnError = connectionHealthOnError;
    }

    public String getPingSQL() {
        return pingSQL;
    }

    public void setPingSQL(String pingSQL) {
        this.pingSQL = pingSQL;
    }
}
