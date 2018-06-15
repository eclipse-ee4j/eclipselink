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
//      gonural - initial
package org.eclipse.persistence.jpa.rs.util.metadatasources;

import java.util.Map;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;

/**
 * Makes java.lang package classes available to JPA-RS JAXB context.
 *
 * @author gonural
 *
 */
public class JavaLangMetadataSource  implements MetadataSource {
    private XmlBindings xmlBindings;

    public JavaLangMetadataSource() {
        xmlBindings = new XmlBindings();
        xmlBindings.setPackageName("java.lang");
    }

    @Override
    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return this.xmlBindings;
    }
}
