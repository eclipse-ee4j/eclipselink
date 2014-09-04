/*******************************************************************************
 * Copyright (c) 2014 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.features;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.jpa.rs.ReservedWords;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenient {@link ItemLinks} object builder.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ItemLinksBuilder {
    private List<LinkV2> links = new ArrayList<LinkV2>();

    /**
     * Adds a 'self' link.
     *
     * @param href 'self' link
     * @return {@link ItemLinksBuilder} with added 'self' link.
     */
    public ItemLinksBuilder addSelf(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_SELF, href));
        return this;
    }

    /**
     * Adds a 'canonical' link.
     *
     * @param href 'canonical' link
     * @return {@link ItemLinksBuilder} with added 'canonical' link.
     */
    public ItemLinksBuilder addCanonical(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_CANONICAL, href));
        return this;
    }

    /**
     * Adds a 'next' link.
     *
     * @param href 'next' link
     * @return {@link ItemLinksBuilder} with added 'next' link.
     */
    public ItemLinksBuilder addNext(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_NEXT, href));
        return this;
    }

    /**
     * Adds a 'prev' link.
     *
     * @param href 'prev' link
     * @return {@link ItemLinksBuilder} with added 'prev' link.
     */
    public ItemLinksBuilder addPrev(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_PREV, href));
        return this;
    }

    /**
     * Builds {@link ItemLinks} object.
     *
     * @return {@link ItemLinks} object.
     */
    public ItemLinks build() {
        final ItemLinks itemLinks = new ItemLinks();
        itemLinks.setLinks(links);
        return itemLinks;
    }
}
