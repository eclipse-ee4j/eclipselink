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
//     tware -  Initial implementation

package org.eclipse.persistence.jpa.rs.util.metadatasources;

import java.util.Map;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jpa.rs.util.PreLoginMappingAdapter;

/**
 * Makes the Link class available in our Dynamic JAXB context.  This class is used to describe
 * non-private owned relationships and is not available by default because it is used in a mapping
 * that is added at preLogin.
 *
 * @see PreLoginMappingAdapter
 * @author tware
 *
 */
public class LinkMetadataSource implements MetadataSource {

    private XmlBindings xmlBindings;

    public LinkMetadataSource(){
        xmlBindings = new XmlBindings();
        xmlBindings.setPackageName(Link.class.getPackage().getName());

        JavaTypes javaTypes = new JavaTypes();
        xmlBindings.setJavaTypes(javaTypes);

        JavaType javaType = new JavaType();
        javaType.setName(Link.class.getSimpleName());
        javaType.setXmlRootElement(new org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement());
        javaTypes.getJavaType().add(javaType);
    }

    @Override
    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return this.xmlBindings;
    }
}
