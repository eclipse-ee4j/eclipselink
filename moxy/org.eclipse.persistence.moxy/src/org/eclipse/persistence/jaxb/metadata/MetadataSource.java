/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.metadata;

import java.util.Map;

import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;

/**
 * 
 * 
 * @See MetadataSourceAdapter 
 */
public interface MetadataSource {

    /**
     * 
     * @param properties - The properties passed in to create the JAXBContext
     * @param classLoader - The ClassLoader passed in to create the JAXBContext
     * @param log
     * @return the XmlBindings object representing the metadata
     */
    XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader);

}