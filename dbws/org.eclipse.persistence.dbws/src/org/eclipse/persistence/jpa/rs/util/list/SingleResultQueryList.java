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
//      09-01-2014-2.6.0 Dmitry Kornilov
//        - implements SingleResultQuery interface
package org.eclipse.persistence.jpa.rs.util.list;

import org.eclipse.persistence.jpa.rs.ReservedWords;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * This class is used to wrap collection of attributes returned by the single-result JPA report query.
 *
 * @author gonural
 * @since EclipseLink 2.6.0.
 */
@XmlRootElement(name = ReservedWords.JPARS_LIST_ITEM_NAME)
public class SingleResultQueryList implements SingleResultQuery {
    private List<JAXBElement<?>> fields;

    /**
     * Instantiates a new query result list item.
     */
    public SingleResultQueryList() {
    }

    /**
     * Gets the fields.
     *
     * @return the fields
     */
    @Override
    @XmlAnyElement(lax = true)
    public List<JAXBElement<?>> getFields() {
        return fields;
    }

    /**
     * Sets the fields.
     *
     * @param fields the new fields
     */
    @Override
    public void setFields(List<JAXBElement<?>> fields) {
        this.fields = fields;
    }
}
