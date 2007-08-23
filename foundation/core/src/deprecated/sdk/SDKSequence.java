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

import org.eclipse.persistence.sequencing.QuerySequence;
import org.eclipse.persistence.queries.*;

/**
 * <p>
 * <code>SDKSequence</code>
 * builds the queries for using sequence numbers.
 * Subclasses will need to override the appropriate methods that build
 * the platform-specific <code>Call</code>s.
 * <p>
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public class SDKSequence extends QuerySequence {

    /** Hold the name of the column in the sequence table which specifies the sequence numeric value */
    protected String counterFieldName = "SEQ_COUNT";

    /** Hold the name of the column in the sequence table which specifies the sequence name */
    protected String nameFieldName = "SEQ_NAME";

    public SDKSequence() {
        super();
    }

    public SDKSequence(String name) {
        super(name);
        setShouldSelectBeforeUpdate(true);
    }

    public SDKSequence(String name, int size) {
        super(name, size);
        setShouldSelectBeforeUpdate(true);
    }

    public SDKSequence(String name, String nameFieldName, String counterFieldName) {
        this(name);
        setNameFieldName(nameFieldName);
        setCounterFieldName(counterFieldName);
    }

    public SDKSequence(String name, int size, String counterFieldName, String nameFieldName) {
        this(name, size);
        setCounterFieldName(counterFieldName);
        setNameFieldName(nameFieldName);
    }

    public boolean equals(Object obj) {
        if (obj instanceof SDKSequence) {
            SDKSequence other = (SDKSequence)obj;
            if (equalNameAndSize(this, other)) {
                return getNameFieldName().equals(other.getNameFieldName()) && getCounterFieldName().equals(other.getCounterFieldName());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String getCounterFieldName() {
        return counterFieldName;
    }

    public void setCounterFieldName(String name) {
        counterFieldName = name;
    }

    public String getNameFieldName() {
        return nameFieldName;
    }

    public void setNameFieldName(String name) {
        nameFieldName = name;
    }

    /**
     * Build and return the default call for reading the value
     * of a given sequence number.
     */
    protected Call buildSelectCall() {
        throw SDKDataStoreException.sdkPlatformDoesNotSupportSequences();
    }

    /**
     * Build the default query for reading the value
     * of a given sequence number.
     */
    protected ValueReadQuery buildSelectQuery() {
        ValueReadQuery query = new ValueReadQuery();
        query.addArgument(this.getNameFieldName());
        query.setCall(this.buildSelectCall());

        return query;
    }

    /**
     * Build and return the default call for updating the value
     * of a given sequence number.
     */
    protected Call buildUpdateCall() {
        throw SDKDataStoreException.sdkPlatformDoesNotSupportSequences();
    }

    /**
     * Build the default query for updating the value
     * of a given sequence number.
     */
    protected DataModifyQuery buildUpdateQuery() {
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument(this.getNameFieldName());
        query.addArgument(this.getCounterFieldName());
        query.setCall(this.buildUpdateCall());

        return query;
    }
}