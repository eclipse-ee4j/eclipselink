/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     gonural -  Initial implementation
 ******************************************************************************/

package org.eclipse.persistence.jpa.rs.util.metadatasources;

import java.util.Map;

import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryList;

/**
 * Makes the SingleResultQueryList class available to JPA-RS JAXB context.  
 * 
 * @see SingleResultQueryList
 * @author gonural
 *
 */
public class SingleResultQueryListMetadataSource implements MetadataSource {
    private XmlBindings xmlBindings;

    public SingleResultQueryListMetadataSource() {
        xmlBindings = new XmlBindings();
        xmlBindings.setPackageName(SingleResultQueryList.class.getPackage().getName());
        JavaTypes javaTypes = new JavaTypes();
        xmlBindings.setJavaTypes(javaTypes);
        JavaType javaType = new JavaType();
        javaType.setName(SingleResultQueryList.class.getSimpleName());
        javaTypes.getJavaType().add(javaType);
    }

    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return this.xmlBindings;
    }
}
