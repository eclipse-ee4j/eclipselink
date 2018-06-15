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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.jaxb.metadata;

import java.util.Map;

import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;

/**
 * You may subclass this class rather than implement the MetadataSource
 * interface allowing insulation from future additions to the interface.
 *
 * @see MetadataSource
 */
public abstract class MetadataSourceAdapter implements MetadataSource {

    @Override
    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return null;
    }

}
