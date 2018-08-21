/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.jaxb.metadata;

import java.util.Map;

import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;

/**
 *
 *
 * @see MetadataSourceAdapter
 */
public interface MetadataSource {

    /**
     *
     * @param properties - The properties passed in to create the JAXBContext
     * @param classLoader - The ClassLoader passed in to create the JAXBContext
     * @return the XmlBindings object representing the metadata
     */
    XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader);

}
