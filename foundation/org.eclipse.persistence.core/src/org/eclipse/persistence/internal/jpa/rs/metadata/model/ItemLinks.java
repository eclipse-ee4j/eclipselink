/*******************************************************************************
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial
 *      2014-09-01-2.6.0 Dmitry Kornilov
 *        - Added convenient methods to retrieve links by rel.
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.rs.metadata.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of links for REST collection or entity resource.
 *
 * @author gonural
 */
public class ItemLinks {
    private List<LinkV2> links;

    /**
     * Returns a list of all links.
     *
     * @return a list of links.
     */
    public List<LinkV2> getLinks() {
        return links;
    }

    /**
     * Sets links.
     *
     * @param links links to set.
     */
    public void setLinks(List<LinkV2> links) {
        if (this.links == null) {
            this.links = new ArrayList<LinkV2>();
        }

        this.links = links;
    }

    /**
     * Adds a link.
     *
     * @param link link to add.
     */
    public void addLink(LinkV2 link) {
        if (this.links == null) {
            this.links = new ArrayList<LinkV2>();
        }

        this.links.add(link);
    }

    /**
     * Gets 'canonical' link.
     *
     * @return 'Canonical' link or null if not found.
     */
    public LinkV2 getCanonicalLink() {
        return getLinkByRel("canonical");
    }

    /**
     * Gets 'self' link.
     *
     * @return 'Self' link or null if not found.
     */
    public LinkV2 getSelfLink() {
        return getLinkByRel("self");
    }

    /**
     * Finds a link by rel..
     *
     * @return Found link or null if not found.
     */
    public LinkV2 getLinkByRel(String rel) {
        if (links == null || links.isEmpty()) {
            return null;
        }

        for (LinkV2 link : links) {
            if (link.getRel().equals(rel)) {
                return link;
            }
        }
        return null;
    }
}
