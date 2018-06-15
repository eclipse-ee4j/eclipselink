/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - initial
package org.eclipse.persistence.jpa.rs.util.list;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;

/**
 * This class is used to wrap collection of attributes of a record returned by a JPA report query.
 *
 * @author gonural
 *
 */
public class ReportQueryResultListItem {

    private List<JAXBElement<?>> fields;

    /**
     * Instantiates a new query result list item.
     */
    public ReportQueryResultListItem() {
    }

    /**
     * Gets the fields.
     *
     * @return the fields
     */
    @XmlAnyElement(lax = true)
    public List<JAXBElement<?>> getFields() {
        return fields;
    }

    /**
     * Sets the fields.
     *
     * @param fields the new fields
     */
    public void setFields(List<JAXBElement<?>> fields) {
        this.fields = fields;
    }
}
