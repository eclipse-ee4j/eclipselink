/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *    -  Initial implementation
 *    
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;

public class ReferenceAdapter<T extends PersistenceWeavedRest> extends
        XmlAdapter<T, T> {
    private String baseURI = null;
    private PersistenceContext context = null;

    /**
     * Instantiates a new reference adapter.
     */
    public ReferenceAdapter() {
        super();
    }

    /**
     * Instantiates a new reference adapter.
     * 
     * @param baseURI
     *            the base uri
     * @param context
     *            the context
     */
    public ReferenceAdapter(String baseURI, PersistenceContext context) {
        this.baseURI = baseURI;
        this.context = context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public T marshal(T persistenceWeavedRest) throws Exception {
        if (persistenceWeavedRest == null) {
            return null;
        }
        ClassDescriptor descriptor = context
                .getJAXBDescriptorForClass(persistenceWeavedRest.getClass());
        T returnT = (T) descriptor.getObjectBuilder().buildNewInstance();
        Link link = new Link();
        link.setMethod("GET");
        link.setRel("self");
        String id = IdHelper.stringifyId(persistenceWeavedRest,
                descriptor.getAlias(), context);
        link.setHref(baseURI + context.getName() + "/entity/"
                + descriptor.getAlias() + "/" + id);
        descriptor.getMappingForAttributeName("persistence_href")
                .setAttributeValueInObject(returnT, link);
        return returnT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public T unmarshal(T persistenceWeavedRest) throws Exception {
        Link href = persistenceWeavedRest.getPersistence_href();
        if (null == href) {
            ClassDescriptor descriptor = context.getJAXBDescriptorForClass(persistenceWeavedRest.getClass());
            if (persistenceWeavedRest instanceof FetchGroupTracker && JpaHelper.getDatabaseSession(context.getEmf()).doesObjectExist(persistenceWeavedRest)){
                if (context.doesExist(null, persistenceWeavedRest)){
                    FetchGroup fetchGroup = new FetchGroup();
                    for (DatabaseMapping mapping: descriptor.getMappings()){
                        if (!(mapping instanceof XMLInverseReferenceMapping)){
                            fetchGroup.addAttribute(mapping.getAttributeName());
                        }
                    }
                    (new FetchGroupManager()).setObjectFetchGroup(persistenceWeavedRest, fetchGroup, null);
                }
            }
            return persistenceWeavedRest;
        }
        // Construct object from the href
        String uri = href.getHref().replace("\\/", "/");
        String entityType = uri.substring(uri.indexOf("/entity/"),
                uri.lastIndexOf('/'));
        entityType = entityType.substring(entityType.lastIndexOf("/") + 1);
        String entityId = uri.substring(uri.lastIndexOf("/") + 1);
        ClassDescriptor descriptor = context.getDescriptor(entityType);
        Object id = IdHelper.buildId(context, descriptor.getAlias(), entityId,
                null);

        T foundEntity = (T) getObjectById(entityType, id);
        return foundEntity;
    }

    private Object getObjectById(String entityType, Object id) throws Exception {
        Object entity = context.find(null, entityType, id, null);
        if (entity != null) {
            return entity;
        }
        // It is an error if the object referred by a link in unmarshal doesn't
        // exist, so throw exception
        throw new RuntimeException("Entity " + entityType + " with id " + id
                + " does not exist.");
    }
}