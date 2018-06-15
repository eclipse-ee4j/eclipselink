/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.jpa.rs.util.xmladapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.ReservedWords;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;

public class ReferenceAdapter<T extends PersistenceWeavedRest> extends XmlAdapter<Object, Object> {
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
    public Link marshal(Object o) throws Exception {
        if (o == null) {
            return null;
        }

        ClassDescriptor descriptor = context.getJAXBDescriptorForClass(o.getClass());
        T t = (T) descriptor.getObjectBuilder().buildNewInstance();
        Link link = new Link();
        link.setMethod("GET");
        link.setRel(ReservedWords.JPARS_REL_SELF);
        String id = IdHelper.stringifyId(o, descriptor.getAlias(), context);
        String version = context.getVersion();
        if (version != null) {
            link.setHref(baseURI + version + "/" + context.getName() + "/entity/" + descriptor.getAlias() + "/" + id);
        } else {
            link.setHref(baseURI + context.getName() + "/entity/" + descriptor.getAlias() + "/" + id);
        }
        descriptor.getMappingForAttributeName("_persistence_href").setAttributeValueInObject(t, link);
        return link;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public T unmarshal(Object object) throws Exception {
        if (object == null) {
            return null;
        }

        PersistenceWeavedRest persistenceWeavedRest = null;
        Link link;

        if (object instanceof PersistenceWeavedRest) {
            persistenceWeavedRest = (PersistenceWeavedRest) object;
            link = persistenceWeavedRest._persistence_getHref();
        } else if (object instanceof Link) {
            link = (Link) object;
        } else {
            return null;
        }

        if (null == link || link.getHref() == null) {
            if (persistenceWeavedRest != null) {
                ClassDescriptor descriptor = context.getJAXBDescriptorForClass(persistenceWeavedRest.getClass());
                if (persistenceWeavedRest instanceof FetchGroupTracker && JpaHelper.getDatabaseSession(context.getEmf()).doesObjectExist(persistenceWeavedRest)) {
                    if (context.doesExist(null, persistenceWeavedRest)) {
                        FetchGroup fetchGroup = new FetchGroup();
                        for (DatabaseMapping mapping : descriptor.getMappings()) {
                            if (!(mapping instanceof XMLInverseReferenceMapping)) {
                                fetchGroup.addAttribute(mapping.getAttributeName());
                            }
                        }
                        (new FetchGroupManager()).setObjectFetchGroup(persistenceWeavedRest, fetchGroup, null);
                    }
                }
                return (T) persistenceWeavedRest;
            }
        }

        // Construct object from the href
        if ((link != null) && (link.getHref() != null)) {
            String uri = link.getHref().replace("\\/", "/");
            String entityType = uri.substring(uri.indexOf("/entity/"), uri.lastIndexOf('/'));
            entityType = entityType.substring(entityType.lastIndexOf('/') + 1);
            String entityId = uri.substring(uri.lastIndexOf('/') + 1);
            ClassDescriptor descriptor = context.getDescriptor(entityType);
            Object id = IdHelper.buildId(context, descriptor.getAlias(), entityId);

            T foundEntity = (T) getObjectById(entityType, id);
            return foundEntity;
        }

        return null;
    }

    private Object getObjectById(String entityType, Object id) throws Exception {
        Object entity = context.find(null, entityType, id, null);
        if (entity != null) {
            return entity;
        }
        // It is an error if the object referred by a link doesn't exist, so throw exception
        throw JPARSException.objectReferredByLinkDoesNotExist(entityType, id);
    }
}
