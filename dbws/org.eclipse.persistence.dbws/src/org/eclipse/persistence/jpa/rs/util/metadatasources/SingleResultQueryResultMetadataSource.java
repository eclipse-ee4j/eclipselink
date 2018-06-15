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
//     Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpa.rs.util.metadatasources;

import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryResult;

import java.util.Map;

/**
 * Makes the SingleResultQueryResult class available to JPA-RS JAXB context.
 *
 * @see org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryResult
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class SingleResultQueryResultMetadataSource implements MetadataSource {
    private XmlBindings xmlBindings;

    /**
     * Creates a new SingleResultQueryResultMetadataSource.
     */
    public SingleResultQueryResultMetadataSource() {
        xmlBindings = new XmlBindings();
        xmlBindings.setPackageName(SingleResultQueryResult.class.getPackage().getName());
        final JavaTypes javaTypes = new JavaTypes();
        xmlBindings.setJavaTypes(javaTypes);
        JavaType javaType = new JavaType();
        javaType.setName(SingleResultQueryResult.class.getSimpleName());
        javaTypes.getJavaType().add(javaType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return this.xmlBindings;
    }
}
