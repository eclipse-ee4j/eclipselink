/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
    public String getRecordShortDescription() {
        return description;
    }

    /**
     * Set the description for this record.
     *
     * @param description
     */
    public void setRecordShortDescription(String theDescription) {
        description = theDescription;
    }

    /**
     * Return the record name
     *
     * @return the name of this record
     */
    public String getRecordName() {
        return name;
    }

    /**
     * Set the record name
     *
     * @param name - the name of this record
     */
    public void setRecordName(String theName) {
        name = theName;
    }
}
