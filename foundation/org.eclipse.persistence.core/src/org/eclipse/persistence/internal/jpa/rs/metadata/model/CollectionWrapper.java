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
package org.eclipse.persistence.internal.jpa.rs.metadata.model;

import java.util.Collection;
import java.util.List;

/**
 * Wrapper for collection used in JPARS 2.0.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class CollectionWrapper<T> {
    private Collection<T> items;

    private List<LinkV2> links;

    public Collection<T> getItems() {
        return items;
    }

    public void setItems(Collection<T> items) {
        this.items = items;
    }

    public List<LinkV2> getLinks() {
        return links;
    }

    public void setLinks(List<LinkV2> links) {
        this.links = links;
    }
}
