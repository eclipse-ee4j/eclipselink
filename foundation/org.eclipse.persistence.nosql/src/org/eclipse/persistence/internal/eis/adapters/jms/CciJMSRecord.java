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
package org.eclipse.persistence.internal.eis.adapters.jms;


//JDK imports
import java.util.ArrayList;

import javax.resource.cci.IndexedRecord;

/**
 * INTERNAL:
 * A simple indexed record.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class CciJMSRecord extends ArrayList implements IndexedRecord {
    protected String description;// a description of this record
    protected String name;// the record name

    /**
     * The default constructor
     */
    public CciJMSRecord() {
        super();
        name = "JMS record";
        description = "JMS message data";
    }

    /**
     * This constructor sets the record name.
     *
     * @param recordName - the name of the record.
     */
    public CciJMSRecord(String recordName) {
        super();
        name = recordName;
        description = "JMS message data";
    }

    /**
     * Return a description of this record
     *
     * @return the description
     */
    @Override
    public String getRecordShortDescription() {
        return description;
    }

    /**
     * Set the description for this record.
     *
     * @param theDescription
     */
    @Override
    public void setRecordShortDescription(String theDescription) {
        description = theDescription;
    }

    /**
     * Return the record name
     *
     * @return the name of this record
     */
    @Override
    public String getRecordName() {
        return name;
    }

    /**
     * Set the record name
     *
     * @param theName - the name of this record
     */
    @Override
    public void setRecordName(String theName) {
        name = theName;
    }
}
