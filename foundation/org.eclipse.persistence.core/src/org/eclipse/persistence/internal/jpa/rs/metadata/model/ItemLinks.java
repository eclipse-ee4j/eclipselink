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
package org.eclipse.persistence.internal.jpa.rs.metadata.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class ItemLinks {
    private List<LinkV2> links;

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    public List<LinkV2> getLinks() {
        return links;
    }

    public void setLinks(List<LinkV2> links) {
        if (this.links == null) {
            this.links = new ArrayList<LinkV2>();
        }

        this.links = links;
    }

    public void addItem(LinkV2 item) {
        if (this.links == null) {
            this.links = new ArrayList<LinkV2>();
        }

        this.links.add(item);
    }
}
