/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.internal.jpa.rs.metadata.model.v2;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * JPARS 2.0 metadata catalog.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
@XmlRootElement
@XmlType(propOrder={"items", "links"})
public class MetadataCatalog {
    /** A list of resources **/
    private List<Resource> items;

    /** Links **/
    private List<LinkV2> links;

    public List<Resource> getItems() {
        return items;
    }

    public void setItems(List<Resource> items) {
        this.items = items;
    }

    public List<LinkV2> getLinks() {
        return links;
    }

    public void setLinks(List<LinkV2> links) {
        this.links = links;
    }

    public void addResource(Resource resource) {
        if (items == null) {
            items = new ArrayList<Resource>();
        }
        items.add(resource);
    }
}
