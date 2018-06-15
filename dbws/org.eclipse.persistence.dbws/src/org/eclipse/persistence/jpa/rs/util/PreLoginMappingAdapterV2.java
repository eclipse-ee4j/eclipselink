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
//     Dmitry Kornilov -  Initial implementation
package org.eclipse.persistence.jpa.rs.util;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jaxb.SessionEventListener;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jaxb.DefaultXMLNameTransformer;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.SessionEvent;

/**
 * This listener is used for crating XML mappings for weaved fields.
 *
 * @author Dmitry Kornilov
 */
public class PreLoginMappingAdapterV2 extends SessionEventListener {

    /**
     * Instantiates a new pre login mapping adapter.
     */
    public PreLoginMappingAdapterV2() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preLogin(SessionEvent event) {
        final Project project = event.getSession().getProject();
        final DefaultXMLNameTransformer xmlNameTransformer = new DefaultXMLNameTransformer();
        for (Object descriptorAlias : project.getAliasDescriptors().keySet()) {
            final ClassDescriptor descriptor = (ClassDescriptor) project.getAliasDescriptors().get(descriptorAlias);

            if (!PersistenceWeavedRest.class.isAssignableFrom(descriptor.getJavaClass())) {
                continue;
            }

            if (descriptor.isXMLDescriptor()) {
                XMLDescriptor xmlDescriptor = (XMLDescriptor) project.getAliasDescriptors().get(descriptorAlias);
                if (null != xmlDescriptor) {
                    if (null == xmlDescriptor.getDefaultRootElement()) {
                        // set root element
                        xmlDescriptor.setDefaultRootElement(xmlNameTransformer.transformRootElementName(xmlDescriptor.getJavaClass().getName()));
                        // set resultAlwaysXMLRoot to false, so that the elements are not wrapped in JAXBElements when unmarshalling.
                        xmlDescriptor.setResultAlwaysXMLRoot(false);
                    }
                }
            }

            final XMLCompositeObjectMapping itemLinksMapping = new XMLCompositeObjectMapping();
            itemLinksMapping.setAttributeName("_persistence_links");
            itemLinksMapping.setGetMethodName("_persistence_getLinks");
            itemLinksMapping.setSetMethodName("_persistence_setLinks");
            itemLinksMapping.setDescriptor(descriptor);
            itemLinksMapping.setReferenceClass(ItemLinks.class);
            itemLinksMapping.setXPath(".");
            descriptor.addMapping(itemLinksMapping);
        }
    }
}
