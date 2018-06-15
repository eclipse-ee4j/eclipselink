/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - initial implementation
//      2014-09-01-2.6.0 Dmitry Kornilov
//        - JPARS 2.0 fixes
package org.eclipse.persistence.jpa.rs.features.core.selflinks;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.ReservedWords;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilderImpl;
import org.eclipse.persistence.jpa.rs.features.ItemLinksBuilder;
import org.eclipse.persistence.jpa.rs.util.HrefHelper;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.jpa.rs.util.list.ReadAllQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultListItem;
import org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryResult;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * FeatureResponseBuilder implementation generating 'self' and 'canonical' links. Used in JPARS 2.0.
 *
 * @author gonural, Dmitry Kornilov
 * @since EclipseList 2.6.0
 */
@SuppressWarnings({ "unchecked"})
public class SelfLinksResponseBuilder extends FeatureResponseBuilderImpl {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildReadAllQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object> items, UriInfo uriInfo) {
        return collectionResponse(context, items, uriInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildReportQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> items, UriInfo uriInfo) {
        ReportQueryResultCollection response = new ReportQueryResultCollection();
        for (Object result : results) {
            ReportQueryResultListItem queryResultListItem = new ReportQueryResultListItem();
            List<JAXBElement<?>> jaxbFields = createShellJAXBElementList(items, result);
            generateLinksInElementsList(context, jaxbFields);
            queryResultListItem.setFields(jaxbFields);
            response.addItem(queryResultListItem);
        }
        response.addLink(new LinkV2(ReservedWords.JPARS_REL_SELF, uriInfo.getRequestUri().toString()));
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildSingleResultQueryResponse(PersistenceContext context, Map<String, Object> queryParams, Object result, List<ReportItem> items, UriInfo uriInfo) {
        final SingleResultQueryResult response = new SingleResultQueryResult();
        final List<JAXBElement<?>> fields = createShellJAXBElementList(items, result);

        // If there are entities in fields insert links there
        generateLinksInElementsList(context, fields);

        response.setFields(fields);
        response.addLink(new LinkV2(ReservedWords.JPARS_REL_SELF, uriInfo.getRequestUri().toString()));
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildAttributeResponse(PersistenceContext context, Map<String, Object> queryParams, String attribute, Object item, UriInfo uriInfo) {
        if (item instanceof List) {
            return collectionResponse(context, (List<Object>) item, uriInfo);
        }
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildSingleEntityResponse(PersistenceContext context, Map<String, Object> queryParams, Object result, UriInfo uriInfo) {
        if (result instanceof PersistenceWeavedRest) {
            final PersistenceWeavedRest entity = (PersistenceWeavedRest)result;
            final ClassDescriptor classDescriptor = context.getServerSession().getProject().getDescriptor(result.getClass());
            final String entityClassName = classDescriptor.getAlias();
            final String entityId = IdHelper.stringifyId(entity, entityClassName, context);

            final ItemLinksBuilder itemLinksBuilder = (new ItemLinksBuilder())
                    .addSelf(uriInfo.getRequestUri().toString())
                    .addCanonical(HrefHelper.buildEntityHref(context, entityClassName, entityId));

            generateLinksForRelationships(context, entity);
            entity._persistence_setLinks(itemLinksBuilder.build());
            return entity;
        }
        return result;
    }

    private Object collectionResponse(PersistenceContext context, List<Object> results, UriInfo uriInfo) {
        if ((results != null) && (!results.isEmpty())) {
            final ReadAllQueryResultCollection response = new ReadAllQueryResultCollection();
            for (Object item : results) {
                if (item instanceof PersistenceWeavedRest) {
                    final PersistenceWeavedRest entity = (PersistenceWeavedRest) item;
                    final ClassDescriptor classDescriptor = context.getServerSession().getProject().getDescriptor(item.getClass());
                    final String entityClassName = classDescriptor.getAlias();
                    final String entityId = IdHelper.stringifyId(entity, entityClassName, context);

                    final String href = HrefHelper.buildEntityHref(context, entityClassName, entityId);
                    final ItemLinksBuilder itemLinksBuilder = (new ItemLinksBuilder())
                            .addCanonical(href);
                    entity._persistence_setLinks(itemLinksBuilder.build());

                    generateLinksForRelationships(context, entity);
                    response.addItem(entity);
                } else {
                    response.addItem(item);
                }
            }

            response.addLink(new LinkV2(ReservedWords.JPARS_REL_SELF, uriInfo.getRequestUri().toString()));
            return response;
        }
        return results;
    }

    private void generateLinksInElementsList(PersistenceContext context, List<JAXBElement<?>> fields) {
        for (JAXBElement<?> field : fields) {
            if (field.getValue() instanceof PersistenceWeavedRest) {
                final PersistenceWeavedRest entity = (PersistenceWeavedRest) field.getValue();
                final ClassDescriptor classDescriptor = context.getServerSession().getProject().getDescriptor(entity.getClass());
                final String entityClassName = classDescriptor.getAlias();
                final String entityId = IdHelper.stringifyId(entity, entityClassName, context);

                // No links for embedded objects
                if (!classDescriptor.isAggregateDescriptor()) {
                    final String href = HrefHelper.buildEntityHref(context, entityClassName, entityId);
                    final ItemLinksBuilder itemLinksBuilder = (new ItemLinksBuilder())
                    .addSelf(href)
                    .addCanonical(href);
                    entity._persistence_setLinks(itemLinksBuilder.build());
                }
            }
        }
    }

    private void generateLinksForRelationships(PersistenceContext context, PersistenceWeavedRest entity) {
        final ClassDescriptor classDescriptor = context.getServerSession().getProject().getDescriptor(entity.getClass());
        final String entityClassName = classDescriptor.getAlias();
        final String entityId = IdHelper.stringifyId(entity, entityClassName, context);

        for (final Field field : entity.getClass().getDeclaredFields()) {
            if (PersistenceWeavedRest.class.isAssignableFrom(field.getType())) {
                final PersistenceWeavedRest obj = (PersistenceWeavedRest) callGetterForProperty(entity, field.getName());
                if (obj != null) {
                    final String fieldClassName = context.getJAXBDescriptorForClass(field.getType()).getAlias();
                    final String fieldId = IdHelper.stringifyId(obj, fieldClassName, context);

                    final ItemLinks links = (new ItemLinksBuilder())
                            .addSelf(HrefHelper.buildEntityFieldHref(context, entityClassName, entityId, field.getName()))
                            .addCanonical(HrefHelper.buildEntityHref(context, fieldClassName, fieldId))
                            .build();

                    obj._persistence_setLinks(links);
                }
            }
        }
    }

    private Object callGetterForProperty(Object bean, String propertyName) {
        try {
            final BeanInfo info = Introspector.getBeanInfo(bean.getClass(), Object.class);
            final PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {
                if (propertyName.equals(pd.getName())) {
                    return pd.getReadMethod().invoke(bean);
                }
            }
        } catch (InvocationTargetException| IntrospectionException | IllegalAccessException e) {
            throw JPARSException.exceptionOccurred(e);
        }

        return null;
    }
}
