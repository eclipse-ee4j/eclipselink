/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

//  Contributors:
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpa.rs.util;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.internal.jpa.weaving.CollectionProxyClassWriter;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.ReservedWords;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.util.list.PageableCollection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Helper class used to create proxies for collections. Used in JPARS 2.0.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class CollectionWrapperBuilder {
    private final PersistenceContext context;

    /**
     * Creates a new CollectionWrapperBuilder.
     *
     * @param context the persistence context.
     */
    public CollectionWrapperBuilder(PersistenceContext context) {
        this.context = context;
    }

    /**
     * Builds proxies for collections containing in a given object. Replaces original collections with
     * newly created proxies.
     *
     * @param object the object to wrap collections.
     */
    public void wrapCollections(Object object) {
        if (object instanceof PageableCollection) {
            for (Object o : ((PageableCollection<?>) object).getItems()) {
                wrapCollectionsForEntity(o);
            }
        } else {
            wrapCollectionsForEntity(object);
        }
    }

    private void wrapCollectionsForEntity(Object entity) {
        if (!PersistenceWeavedRest.class.isAssignableFrom(entity.getClass())) {
            return;
        }

        for (final Field field : entity.getClass().getDeclaredFields()) {
            if (Collection.class.isAssignableFrom(field.getType())) {
                // Get entity id
                final String id = IdHelper.stringifyId(entity, entity.getClass().getSimpleName(), context);

                // Generate links
                final List<LinkV2> links = new ArrayList<LinkV2>(2);
                final String href = HrefHelper.buildEntityFieldHref(context, entity.getClass().getSimpleName(), id, field.getName());
                links.add(new LinkV2(ReservedWords.JPARS_REL_SELF, href));
                links.add(new LinkV2(ReservedWords.JPARS_REL_CANONICAL, href));

                // Get accessibility
                boolean accessible = field.isAccessible();
                if (!accessible) {
                    field.setAccessible(true);
                }

                // Make proxy
                try {
                    // No proxy for weaved fields and null fields
                    if (!field.getName().startsWith("_") && field.get(entity) != null) {
                        CollectionProxy proxy = getRestCollectionProxy((Collection<?>) field.get(entity), entity.getClass().getName(), field.getName());
                        proxy.setLinks(links);
                        field.set(entity, field.getType().cast(proxy));
                    }
                } catch (IllegalAccessException e) {
                    throw JPARSException.exceptionOccurred(e);
                }

                // Restore accessibility
                if (!accessible) {
                    field.setAccessible(false);
                }
            }
        }
    }

    private CollectionProxy getRestCollectionProxy(final Collection<?> toProxy, final String entityName, final String fieldname) {
        try {
            final DynamicClassLoader classLoader = (DynamicClassLoader)context.getServerSession().getDatasourcePlatform().getConversionManager().getLoader();
            final CollectionProxyClassWriter writer = new CollectionProxyClassWriter(toProxy.getClass().getName(), entityName, fieldname);
            final String proxyClassName = writer.getClassName();

            if (classLoader.getClassWriter(proxyClassName) == null) {
                classLoader.addClass(proxyClassName, writer);
            }

            final Class<?> referenceAdaptorClass = Class.forName(proxyClassName, true, classLoader);
            final Class<?>[] argTypes = {Collection.class};
            final Constructor<?> referenceAdaptorConstructor = referenceAdaptorClass.getDeclaredConstructor(argTypes);
            final Object[] args = new Object[]{toProxy};

            return (CollectionProxy)referenceAdaptorConstructor.newInstance(args);
        } catch (Exception e) {
            throw JPARSException.exceptionOccurred(e);
        }
    }
}
