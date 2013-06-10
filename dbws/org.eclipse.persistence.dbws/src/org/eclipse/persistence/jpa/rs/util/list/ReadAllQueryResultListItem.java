/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util.list;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;

/**
 * This class is used to wrap collection of records returned by a JPA read all query.
 *
 * @author gonural
 *
 */
@SuppressWarnings("rawtypes")
public class ReadAllQueryResultListItem {
    private List<JAXBElement> records;

    /**
     * Gets the records.
     *
     * @return the records
     */
    @XmlAnyElement(lax = true)
    public List<JAXBElement> getRecords() {
        return records;
    }

    /**
     * Sets the records.
     *
     * @param records the new records
     */
    public void setRecords(List<JAXBElement> records) {
        this.records = records;
    }

    /**
     * Adds the record.
     *
     * @param element the element
     */
    public void addRecord(JAXBElement element) {
        if (this.records == null) {
            this.records = new ArrayList<JAXBElement>();
        }
        this.records.add(element);
    }
}
