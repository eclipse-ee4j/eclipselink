/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpa.rs.util.metadatasources;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.CollectionWrapper;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;

import java.util.Map;

/**
 * Makes the CollectionWrapper class available in our Dynamic JAXB context.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class CollectionWrapperMetadataSource implements MetadataSource {

    private final XmlBindings xmlBindings;

    /**
     * Creates a new CollectionWrapperMetadataSource.
     */
    public CollectionWrapperMetadataSource() {
        xmlBindings = new XmlBindings();
        xmlBindings.setPackageName(CollectionWrapper.class.getPackage().getName());

        final JavaType javaType = new JavaType();
        javaType.setName(CollectionWrapper.class.getSimpleName());
        javaType.setXmlRootElement(new org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement());

        final JavaTypes javaTypes = new JavaTypes();
        javaTypes.getJavaType().add(javaType);

        xmlBindings.setJavaTypes(javaTypes);
    }

    @Override
    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return this.xmlBindings;
    }
}
