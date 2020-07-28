/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     gonural -  Initial implementation

package org.eclipse.persistence.jpa.rs.util.metadatasources;

import java.util.Map;

import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultCollection;

/**
 * Makes the ReportQueryResultCollection class available to JPA-RS JAXB context.
 *
 * @see ReportQueryResultCollection
 * @author gonural
 *
 */
public class ReportQueryResultCollectionMetadataSource implements MetadataSource {
    private XmlBindings xmlBindings;

    public ReportQueryResultCollectionMetadataSource() {
        xmlBindings = new XmlBindings();
        xmlBindings.setPackageName(ReportQueryResultCollection.class.getPackage().getName());
        JavaTypes javaTypes = new JavaTypes();
        xmlBindings.setJavaTypes(javaTypes);
        JavaType javaType = new JavaType();
        javaType.setName(ReportQueryResultCollection.class.getSimpleName());
        javaTypes.getJavaType().add(javaType);
    }

    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return this.xmlBindings;
    }
}
