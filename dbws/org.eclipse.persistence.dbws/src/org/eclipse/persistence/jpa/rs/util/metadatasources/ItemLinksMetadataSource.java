/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     gonural -  Initial implementation

package org.eclipse.persistence.jpa.rs.util.metadatasources;

import java.util.Map;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;

/**
 * Makes the ItemLinks class available in our Dynamic JAXB context.
 *
 */
public class ItemLinksMetadataSource implements MetadataSource {

    private XmlBindings xmlBindings;

    public ItemLinksMetadataSource() {
        xmlBindings = new XmlBindings();
        xmlBindings.setPackageName(ItemLinks.class.getPackage().getName());
        JavaTypes javaTypes = new JavaTypes();
        xmlBindings.setJavaTypes(javaTypes);
        JavaType javaType = new JavaType();
        javaType.setName(ItemLinks.class.getSimpleName());
        javaTypes.getJavaType().add(javaType);
    }

    @Override
    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return this.xmlBindings;
    }
}
