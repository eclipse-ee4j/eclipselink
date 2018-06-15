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
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpa.rs.util;

import org.eclipse.persistence.jpa.rs.PersistenceContext;

/**
 * A collection of static methods used to build 'href' attribute values for REST 'link' elements.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public final class HrefHelper {
    /** URL to base REST schemas **/
    public static final String BASE_REST_SCHEMAS_URL = "rest-schemas/";

    /**
     * Returns StringBuilder containing application root:
     * http(s)://root:port/persistence/version/context
     *
     * @param baseUri the base URI
     * @param version the service version
     * @param context the persistent unit name
     * @return StringBuilder
     */
    public static StringBuilder getRoot(String baseUri, String version, String context) {
        final StringBuilder href = new StringBuilder(baseUri);
        href.append(version).append("/").append(context);
        return href;
    }

    /**
     * Returns StringBuilder containing application root:
     * http(s)://root:port/persistence/version/context
     *
     * @param context the persistence context
     * @return StringBuilder
     */
    public static StringBuilder getRoot(PersistenceContext context) {
        final StringBuilder href = new StringBuilder(context.getBaseURI().toString());
        href.append(context.getVersion()).append("/").append(context.getName());
        return href;
    }

    /**
     * Returns StringBuilder containing metadata-catalog root:
     * http(s)://root:port/persistence/version/context/metadata-catalog
     *
     * @param context the persistence context
     * @return StringBuilder
     */
    public static StringBuilder getMetadataRoot(PersistenceContext context) {
        return getRoot(context).append("/metadata-catalog");
    }

    /**
     * Returns StringBuilder containing entity root:
     * http(s)://root:port/persistence/version/context/entity/entityName
     *
     * @param context the persistence context
     * @param entityName the entity name
     * @return StringBuilder
     */
    public static StringBuilder getEntityRoot(PersistenceContext context, String entityName) {
        return getRoot(context).append("/entity/").append(entityName);
    }

    /**
     * Returns StringBuilder containing query root:
     * http(s)://root:port/persistence/version/context/query/queryName
     *
     * @param context the persistence context
     * @param queryName the query name
     * @return StringBuilder
     */
    public static StringBuilder getQueryRoot(PersistenceContext context, String queryName) {
        return getRoot(context).append("/query/").append(queryName);
    }

    /**
     * Returns a link to standard base schema of given type.
     *
     * @param type the schema type
     * @return URL in string
     */
    public static String buildBaseRestSchemaRef(String type) {
        return BASE_REST_SCHEMAS_URL + type;
    }

    /**
     * Returns a href to single entity resource.
     * http(s)://root:port/persistence/version/context/entity/id
     *
     * @param context persistence context.
     * @param entityName entity name.
     * @param entityId entity ID.
     * @return href to given entity.
     */
    public static String buildEntityHref(PersistenceContext context, String entityName, String entityId) {
        return getEntityRoot(context, entityName).append("/").append(entityId).toString();
    }

    /**
     * Builds a link to an entity field.
     * http(s)://root:port/persistence/version/context/entity/id/attribute
     *
     * @param context persistence context.
     * @param entityName entity name.
     * @param entityId entity ID.
     * @param fieldName entity field name.
     * @return href
     */
    public static String buildEntityFieldHref(PersistenceContext context, String entityName, String entityId, String fieldName) {
        return getEntityRoot(context, entityName).append("/").append(entityId).append("/").append(fieldName).toString();
    }

    /**
     * Returns a href to entity resource metadata.
     * http(s)://root:port/persistence/version/context/metadata-catalog/entity
     *
     * @param context persistence context.
     * @param entityName entity name.
     * @return href to given entity.
     */
    public static String buildEntityMetadataHref(PersistenceContext context, String entityName) {
        return getMetadataRoot(context).append("/entity/").append(entityName).toString();
    }

    /**
     * Returns a href to single entity resource without primary key. Used in 'describes' links in resource metadata.
     * http(s)://root:port/persistence/version/context/entity/entityName
     *
     * @param context persistence context.
     * @param entityName entity name.
     * @return href to given entity resource.
     */
    public static String buildEntityDescribesHref(PersistenceContext context, String entityName) {
        return getEntityRoot(context, entityName).toString();
    }

    /**
     * Returns a href to single entity resource without primary key. Used in 'describes' links in resource metadata.
     * http(s)://root:port/persistence/version/context/query/queryName
     *
     * @param context persistence context.
     * @param queryName query name.
     * @return href to given entity resource.
     */
    public static String buildQueryDescribesHref(PersistenceContext context, String queryName) {
        return getQueryRoot(context, queryName).toString();
    }

    /**
     * Returns a href to metadata catalog.
     * http(s)://root:port/persistence/version/context/metadata-catalog
     *
     * @param context persistence context.
     * @return href to resource catalog.
     */
    public static String buildMetadataCatalogHref(PersistenceContext context) {
        return getMetadataRoot(context).toString();
    }

    /**
     * Returns a href to query resource.
     * http(s)://root:port/persistence/version/context/query/queryName+queryParams
     *
     * @param context persistence context.
     * @param queryName name of the query
     * @param queryParams query parameters. Optional.
     * @return href to resource catalog.
     */
    public static String buildQueryHref(PersistenceContext context, String queryName, String queryParams) {
        return getQueryRoot(context, queryName).append(queryParams).toString();
    }

    /**
     * Returns a href to query resource.
     * http(s)://root:port/persistence/version/context/query/queryName
     *
     * @param context persistence context.
     * @param queryName name of the query
     * @return href to resource catalog.
     */
    public static String buildQueryMetadataHref(PersistenceContext context, String queryName) {
        return getMetadataRoot(context).append("/query/").append(queryName).toString();
    }

}
