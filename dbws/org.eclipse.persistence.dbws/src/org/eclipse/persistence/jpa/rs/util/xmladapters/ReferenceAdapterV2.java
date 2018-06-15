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
//     Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpa.rs.util.xmladapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.ReservedWords;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.features.ItemLinksBuilder;
import org.eclipse.persistence.jpa.rs.util.HrefHelper;
import org.eclipse.persistence.jpa.rs.util.IdHelper;

/**
 * Reference adapter used in JPARS V2. Main purpose of this adapter is retrieving an entity
 * by link when unmarshalling.
 *
 * @param <T> entity class of this adapter
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ReferenceAdapterV2<T extends PersistenceWeavedRest> extends XmlAdapter<T, T> {
    protected PersistenceContext context;

    public ReferenceAdapterV2() {
        super();
    }

    /**
     * Instantiates a new reference adapter.
     *
     * @param context persistent context (mandatory)
     */
    public ReferenceAdapterV2(PersistenceContext context) {
        this.context = context;
    }

    /**
     * Marshal just passes through.
     *
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public T marshal(T o) throws Exception {
        if (o == null) {
            return null;
        }

        // Add canonical link
        final String href = HrefHelper.buildEntityHref(context, o.getClass().getSimpleName(), IdHelper.stringifyId(o, o.getClass().getSimpleName(), context));
        if (o._persistence_getLinks() == null) {
            final ItemLinks itemLinks = (new ItemLinksBuilder()).addCanonical(href).build();
            o._persistence_setLinks(itemLinks);
        } else {
            final ItemLinks itemLinks = o._persistence_getLinks();

            final LinkV2 canonicalLink = itemLinks.getCanonicalLink();
            if (canonicalLink == null) {
                o._persistence_getLinks().addLink(new LinkV2(ReservedWords.JPARS_REL_CANONICAL, href));
            }
        }

        return o;
    }

    /**
     * If 'canonical' or 'self' link is present loads entity from the database. Otherwise uses data provided.
     */
    @Override
    public T unmarshal(T o) throws Exception {
        if (o == null) {
            return null;
        }

        if (context == null) {
            return o;
        }

        // Check if links exist and load entity if it does
        if (o._persistence_getLinks() != null && o._persistence_getLinks().getLinks() != null && !o._persistence_getLinks().getLinks().isEmpty()) {
            final ItemLinks itemLinks = o._persistence_getLinks();

            // Use canonical link
            final LinkV2 canonicalLink = itemLinks.getCanonicalLink();
            if (canonicalLink != null && canonicalLink.getHref() != null) {
                return loadEntity(canonicalLink.getHref()) ;
            }

            // Use self link if canonical is not found
            final LinkV2 selfLink = itemLinks.getSelfLink();
            if (selfLink != null && selfLink.getHref() != null) {
                return loadEntity(selfLink.getHref()) ;
            }
        }

        return o;
    }

    private T loadEntity(String href) throws Exception {
        final String uri = href.replace("\\/", "/");
        String entityType = uri.substring(uri.indexOf("/entity/"), uri.lastIndexOf('/'));
        entityType = entityType.substring(entityType.lastIndexOf('/') + 1);
        final String entityId = uri.substring(uri.lastIndexOf('/') + 1);
        final ClassDescriptor descriptor = context.getDescriptor(entityType);
        final Object id = IdHelper.buildId(context, descriptor.getAlias(), entityId);

        return getObjectById(entityType, id);
    }

    private T getObjectById(String entityType, Object id) throws Exception {
        final Object entity = context.find(null, entityType, id, null);
        if (entity != null) {
            return (T)entity;
        }
        // It is an error if the object referred by a link doesn't exist, so throw exception
        throw JPARSException.objectReferredByLinkDoesNotExist(entityType, id);
    }
}
