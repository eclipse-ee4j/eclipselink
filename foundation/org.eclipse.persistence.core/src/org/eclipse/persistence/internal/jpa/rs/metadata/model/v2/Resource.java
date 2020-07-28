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
import java.util.List;

/**
 * REST resource. Used in JPARS 2.0 metadata model.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
@XmlRootElement
@XmlType(propOrder={"name", "links"})
public class Resource {

    /** Resource name **/
    private String name;

    /** Links to this resource **/
    private List<LinkV2> links;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LinkV2> getLinks() {
        return links;
    }

    public void setLinks(List<LinkV2> links) {
        this.links = links;
    }
}
