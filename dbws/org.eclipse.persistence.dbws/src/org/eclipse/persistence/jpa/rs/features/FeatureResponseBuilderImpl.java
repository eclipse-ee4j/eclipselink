/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - Initial implementation
//      2014-09-01-2.6.0 Dmitry Kornilov
//        - Added buildSingleResultQueryResponse method.
package org.eclipse.persistence.jpa.rs.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultList;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultListItem;
import org.eclipse.persistence.jpa.rs.util.list.SimpleHomogeneousList;
import org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryList;

/**
 * Response builder used in JPARS 1.0 and earlier versions.
 *
 * @author gonural
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class FeatureResponseBuilderImpl implements FeatureResponseBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildReadAllQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object> items, UriInfo uriInfo) {
        return items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildReportQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> items, UriInfo uriInfo) {
        if ((results != null) && (!results.isEmpty())) {
            ReportQueryResultList list = populateReportQueryResultList(results, items);
            return list;
        } else {
            return results;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildAttributeResponse(PersistenceContext context, Map<String, Object> queryParams, String attribute, Object result, UriInfo uriInfo) {
        if (result instanceof Collection) {
            if (containsDomainObjects(result)) {
                // Classes derived from PersistenceWeavedRest class (domain objects) are already in the JAXB context
                return result;
            } else {
                // We will need to deal with collection of classes that are not in the JAXB context, such as String, Integer...
                return populateSimpleHomogeneousList((Collection) result, attribute);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildSingleResultQueryResponse(PersistenceContext context, Map<String, Object> queryParams, Object result, List<ReportItem> items, UriInfo uriInfo) {
        final SingleResultQueryList response = new SingleResultQueryList();
        final List<JAXBElement<?>> fields = new FeatureResponseBuilderImpl().createShellJAXBElementList(items, result);
        response.setFields(fields);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildSingleEntityResponse(PersistenceContext context, Map<String, Object> queryParams, Object result, UriInfo uriInfo) {
        return result;
    }

    private ReportQueryResultList populateReportQueryResultList(List<Object[]> results, List<ReportItem> reportItems) {
        ReportQueryResultList response = new ReportQueryResultList();
        for (Object result : results) {
            ReportQueryResultListItem queryResultListItem = new ReportQueryResultListItem();
            List<JAXBElement<?>> jaxbFields = createShellJAXBElementList(reportItems, result);
            queryResultListItem.setFields(jaxbFields);
            response.addItem(queryResultListItem);
        }
        return response;
    }

    /**
     * Creates the shell jaxb element list.
     *
     * @param reportItems the report items
     * @param record the record
     * @return the list. Returns an empty list if reportItems is null or empty.
     */
    public List<JAXBElement<?>> createShellJAXBElementList(List<ReportItem> reportItems, Object record) {
        if (reportItems == null || reportItems.size() == 0) {
            return Collections.emptyList();
        }

        List<JAXBElement<?>> jaxbElements = new ArrayList<>(reportItems.size());
        for (int index = 0; index < reportItems.size(); index++) {
            ReportItem reportItem = reportItems.get(index);
            Object reportItemValue = record;
            if (record instanceof Object[]) {
                reportItemValue = ((Object[]) record)[index];
            }
            if (reportItemValue != null) {
                JAXBElement element = new JAXBElement(new QName(reportItem.getName()), reportItemValue.getClass(), reportItemValue);
                jaxbElements.add(reportItem.getResultIndex(), element);
            }
        }
        return jaxbElements;
    }

    private SimpleHomogeneousList populateSimpleHomogeneousList(Collection collection, String attributeName) {
        SimpleHomogeneousList simpleList = new SimpleHomogeneousList();
        List<JAXBElement> items = new ArrayList<JAXBElement>();

        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Object collectionItem = iterator.next();
            if (!(PersistenceWeavedRest.class.isAssignableFrom(collectionItem.getClass()))) {
                JAXBElement jaxbElement = new JAXBElement(new QName(attributeName), collectionItem.getClass(), collectionItem);
                items.add(jaxbElement);
            }
        }
        simpleList.setItems(items);
        return simpleList;
    }

    private boolean containsDomainObjects(Object object) {
        Collection collection = (Collection) object;
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Object collectionItem = iterator.next();
            if (PersistenceWeavedRest.class.isAssignableFrom(collectionItem.getClass())) {
                return true;
            }
        }
        return false;
    }
}
