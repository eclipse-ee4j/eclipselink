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

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.CollectionWrapper;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.ReservedWords;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.util.CollectionProxy;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Collections adapter used in JPARS V2. Collections are wrapped into CollectionWrapper which has 'links'.
 * @see CollectionWrapper
 *
 * @param <T> list generac type
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class RestCollectionAdapter<T extends PersistenceWeavedRest> extends XmlAdapter<CollectionWrapper<T>, Collection<T>> {
    protected PersistenceContext context;

    public RestCollectionAdapter() {
        super();
    }

    /**
     * Instantiates a new RestCollectionAdapter.
     *
     * @param context persistent context
     */
    public RestCollectionAdapter(PersistenceContext context) {
        this.context = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<T> unmarshal(CollectionWrapper<T> v) throws Exception {
        if (v == null) {
            return null;
        }

        final Collection<T> result = new ArrayList<T>();

        // Check if links exist and items from there if it does
        if (v.getLinks() != null && !v.getLinks().isEmpty()) {
            for (final LinkV2 link : v.getLinks()) {
                if (link.getRel().equals(ReservedWords.JPARS_REL_SELF) && link.getHref() != null) {
                    return loadItems(link.getHref());
                }
            }
        }

        if (v.getItems() != null) {
            // Process each item (load from href, etc.)
            final ReferenceAdapterV2<T> referenceAdapter = new ReferenceAdapterV2<T>(context);
            for (T item : v.getItems()) {
                if (context != null) {
                    result.add(referenceAdapter.unmarshal(item));
                } else {
                    result.add(item);
                }
            }
        }
        return result;
    }

    @Override
    public CollectionWrapper<T> marshal(Collection<T> v) throws Exception {
        if (v == null || v.isEmpty()) {
            return null;
        }

        final CollectionWrapper<T> result = new CollectionWrapper<T>();

        // Currently it returns only links. It will return items when 'expand'
        // feature will be implemented.
        //result.setItems(v);

        // Read links from the RestCollection wrapper
        if (v instanceof CollectionProxy) {
            CollectionProxy restCollection = (CollectionProxy)v;
            result.setLinks(restCollection.getLinks());
        }

        return result;
    }

    private Collection loadItems(String href) throws Exception {
        String uri = href.replace("\\/", "/");
        uri = uri.substring(uri.indexOf("entity/"));
        uri = uri.substring(uri.indexOf('/') + 1);

        final String[] uriItems = uri.split("/");
        final String entityType = uriItems[0];
        final String entityId = uriItems[1];
        final String attributeName = uriItems[2];

        final ClassDescriptor descriptor = context.getDescriptor(entityType);
        final Object id = IdHelper.buildId(context, descriptor.getAlias(), entityId);

        final T entity = getObjectById(entityType, id);
        final DatabaseMapping attributeMapping = descriptor.getMappingForAttributeName(attributeName);
        if ((attributeMapping == null) || (entity == null)) {
            throw JPARSException.databaseMappingCouldNotBeFoundForEntityAttribute(attributeName, entityType, entityId, null);
        }

        return (Collection) attributeMapping.getRealAttributeValueFromAttribute(attributeMapping.getAttributeValueFromObject(entity), entity, (AbstractSession)context.getServerSession());
    }

    @SuppressWarnings("unchecked")
    private T getObjectById(String entityType, Object id) throws Exception {
        final Object entity = context.find(null, entityType, id, null);
        if (entity != null) {
            return (T)entity;
        }
        // It is an error if the object referred by a link doesn't exist, so throw exception
        throw JPARSException.objectReferredByLinkDoesNotExist(entityType, id);
    }
}
