package org.eclipse.persistence.jpa.rs.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultList;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultListItem;
import org.eclipse.persistence.jpa.rs.util.list.SimpleHomogeneousList;
import org.eclipse.persistence.mappings.DatabaseMapping;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class FeatureResponseBuilderImpl implements FeatureResponseBuilder {

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder#buildReadAllQueryResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.util.List, javax.ws.rs.core.UriInfo)
     */
    public Object buildReadAllQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object> items, UriInfo uriInfo) {
        return items;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder#buildReportQueryResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.util.List, java.util.List, javax.ws.rs.core.UriInfo)
     */
    public Object buildReportQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> items, UriInfo uriInfo) {
        if ((results != null) && (!results.isEmpty())) {
            ReportQueryResultList list = populateReportQueryResultList(results, items);
            return list;
        } else {
            return results;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder#buildAttributeResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.lang.String, java.lang.Object, javax.ws.rs.core.UriInfo)
     */
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

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder#buildSingleEntityResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.lang.Object, javax.ws.rs.core.UriInfo)
     */
    public Object buildSingleEntityResponse(PersistenceContext context, Map<String, Object> queryParams, Object result, UriInfo uriInfo) {
        return result;
    }

    private ReportQueryResultList populateReportQueryResultList(List<Object[]> results, List<ReportItem> reportItems) {
        ReportQueryResultList response = new ReportQueryResultList();
        for (Object result : results) {
            ReportQueryResultListItem queryResultListItem = new ReportQueryResultListItem();
            List<JAXBElement> jaxbFields = createShellJAXBElementList(reportItems, result);
            if (jaxbFields == null) {
                return null;
            }
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
     * @return the list
     */
    public List<JAXBElement> createShellJAXBElementList(List<ReportItem> reportItems, Object record) {
        List<JAXBElement> jaxbElements = new ArrayList<JAXBElement>(reportItems.size());
        if ((reportItems != null) && (reportItems.size() > 0)) {
            for (int index = 0; index < reportItems.size(); index++) {
                ReportItem reportItem = reportItems.get(index);
                Object reportItemValue = record;
                if (record instanceof Object[]) {
                    reportItemValue = ((Object[]) record)[index];
                }
                Class reportItemValueType = null;
                if (reportItemValue != null) {
                    reportItemValueType = reportItemValue.getClass();
                    if (reportItemValueType == null) {
                        // try other paths to determine the type of the report item 
                        DatabaseMapping dbMapping = reportItem.getMapping();
                        if (dbMapping != null) {
                            reportItemValueType = dbMapping.getAttributeClassification();
                        } else {
                            ClassDescriptor desc = reportItem.getDescriptor();
                            if (desc != null) {
                                reportItemValueType = desc.getJavaClass();
                            }
                        }
                    }

                    // so, we couldn't determine the type of the report item, stop here... 
                    if (reportItemValueType == null) {
                        return null;
                    }

                    JAXBElement element = new JAXBElement(new QName(reportItem.getName()), reportItemValueType, reportItemValue);
                    jaxbElements.add(reportItem.getResultIndex(), element);
                }
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
