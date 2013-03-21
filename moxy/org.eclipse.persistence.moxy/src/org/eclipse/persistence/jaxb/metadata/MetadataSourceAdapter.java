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
 * You may subclass this class rather than implement the MetadataSource
 * interface allowing insulation from future additions to the interface.
 * 
 * @See MetadataSource
 */
public abstract class MetadataSourceAdapter implements MetadataSource {

    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return null;
    }

}