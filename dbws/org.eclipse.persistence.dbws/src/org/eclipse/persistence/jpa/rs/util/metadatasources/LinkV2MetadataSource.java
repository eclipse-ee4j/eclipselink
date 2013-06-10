/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
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

import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;

/**
 * Makes the LinkV2 class available in our Dynamic JAXB context.  
 * 
 * @author gonural
 *
 */
public class LinkV2MetadataSource implements MetadataSource {

    private XmlBindings xmlBindings;

    public LinkV2MetadataSource() {
        xmlBindings = new XmlBindings();
        xmlBindings.setPackageName(LinkV2.class.getPackage().getName());

        JavaTypes javaTypes = new JavaTypes();
        xmlBindings.setJavaTypes(javaTypes);

        JavaType javaType = new JavaType();
        javaType.setName(LinkV2.class.getSimpleName());
        javaType.setXmlRootElement(new org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement());
        javaTypes.getJavaType().add(javaType);
    }

    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return this.xmlBindings;
    }
}
