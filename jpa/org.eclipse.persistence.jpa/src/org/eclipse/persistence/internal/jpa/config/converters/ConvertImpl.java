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
package org.eclipse.persistence.internal.jpa.config.converters;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.jpa.config.Convert;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class ConvertImpl extends MetadataImpl<ConvertMetadata> implements Convert {

    public ConvertImpl() {
        super(new ConvertMetadata());
    }

    @Override
    public Convert setAttributeName(String attributeName) {
        getMetadata().setAttributeName(attributeName);
        return this;
    }

    @Override
    public Convert setConverter(String converter) {
        getMetadata().setConverterClassName(converter);
        return this;
    }

    @Override
    public Convert setDisableConversion(Boolean disableConversion) {
        getMetadata().setDisableConversion(disableConversion);
        return this;
    }

}
