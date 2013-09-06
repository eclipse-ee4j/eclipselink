/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
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
    
    public Convert setAttributeName(String attributeName) {
        getMetadata().setAttributeName(attributeName);
        return this;
    }

    public Convert setConverter(String converter) {
        getMetadata().setConverterClassName(converter);
        return this;
    }

    public Convert setDisableConversion(Boolean disableConversion) {
        getMetadata().setDisableConversion(disableConversion);
        return this;
    }

}
