/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     05/05/2011-2.3 Chris Delahunt
package org.eclipse.persistence.jpa.metadata;

import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p><b>Purpose</b>: To provide a trivial implementation of MetadataSource.
 * You may subclass this class rather than implement the MetadataSource
 * interface allowing insulation from future additions to the interface.
 *
 * @see MetadataSource
 *
 * @author Chris Delahunt
 * @since EclipseLink 2.3
 */
public abstract class MetadataSourceAdapter implements MetadataSource {

    @Override
    public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {
        return null;
    }

}
