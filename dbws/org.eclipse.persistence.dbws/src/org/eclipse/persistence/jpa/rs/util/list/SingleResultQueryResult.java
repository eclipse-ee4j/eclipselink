/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpa.rs.util.list;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.jpa.rs.ReservedWords;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The result of single result query.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
@XmlRootElement(name = ReservedWords.JPARS_LIST_ITEM_NAME)
public class SingleResultQueryResult implements SingleResultQuery {
    private List<JAXBElement<?>> fields;
    private List<LinkV2> links;

    /**
     * Creates a new SingleResultQueryResult.
     */
    public SingleResultQueryResult() {
    }

    @Override
    @XmlAnyElement(lax = true)
    public List<JAXBElement<?>> getFields() {
        return fields;
    }

    @Override
    public void setFields(List<JAXBElement<?>> fields) {
        this.fields = fields;
    }

    public List<LinkV2> getLinks() {
        return links;
    }

    public void setLinks(List<LinkV2> links) {
        this.links = links;
    }

    public void addLink(LinkV2 link) {
        if (links == null) {
            links = new ArrayList<LinkV2>();
        }
        links.add(link);
    }
}
