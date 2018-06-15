/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.mappings;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.mappings.AccessMethodsMetadata;
import org.eclipse.persistence.jpa.config.AccessMethods;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class AccessMethodsImpl extends MetadataImpl<AccessMethodsMetadata> implements AccessMethods {

    public AccessMethodsImpl() {
        super(new AccessMethodsMetadata());
    }

    @Override
    public AccessMethods setGetMethod(String getMethod) {
        getMetadata().setGetMethodName(getMethod);
        return this;
    }

    @Override
    public AccessMethods setSetMethod(String setMethod) {
        getMetadata().setSetMethodName(setMethod);
        return this;
    }

}
