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
 *      Dmitry Kornilov - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import org.eclipse.persistence.jpa.rs.PersistenceContext;

/**
 * A collection of static methods used to build 'href' attribute values for REST 'link' elements.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class HrefHelper {
    /**
     * Returns a href to single entity resource.
     * http(s)://root:port/version/context/entity/id
     *
     * @param context persistence context.
     * @param entityClass entity class name.
     * @param entityId entity ID.
     * @return href to given entity.
     */
    public static String buildEntityHref(PersistenceContext context, String entityClass, String entityId) {
        final StringBuilder href = new StringBuilder(context.getBaseURI().toString());
        href.append(context.getVersion()).append("/")
                .append(context.getName()).append("/")
                .append("entity/")
                .append(entityClass).append("/")
                .append(entityId);
        return href.toString();
    }

    /**
     * Builds a link to an entity field.
     * http(s)://root:port/version/context/entity/id/attribute
     *
     * @param context persistence context.
     * @param entityClass entity class name.
     * @param entityId entity ID.
     * @param fieldName entity field name.
     * @return href
     */
    public static String buildEntityFieldHref(PersistenceContext context, String entityClass, String entityId, String fieldName) {
        final StringBuilder href = new StringBuilder(buildEntityHref(context, entityClass, entityId));
        href.append("/").append(fieldName);
        return href.toString();
    }
}
