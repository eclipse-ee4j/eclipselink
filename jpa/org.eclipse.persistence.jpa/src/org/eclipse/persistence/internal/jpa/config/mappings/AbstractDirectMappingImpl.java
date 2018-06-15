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

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.converters.ConvertImpl;
import org.eclipse.persistence.internal.jpa.config.converters.EnumeratedImpl;
import org.eclipse.persistence.internal.jpa.config.converters.LobImpl;
import org.eclipse.persistence.internal.jpa.config.converters.TemporalImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.DirectAccessor;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.jpa.config.Convert;
import org.eclipse.persistence.jpa.config.Enumerated;
import org.eclipse.persistence.jpa.config.Lob;
import org.eclipse.persistence.jpa.config.Temporal;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public class AbstractDirectMappingImpl<T extends DirectAccessor, R> extends AbstractMappingImpl<T, R> {

    public AbstractDirectMappingImpl(T t) {
        super(t);

        getMetadata().setConverts(new ArrayList<ConvertMetadata>());
    }

    /**
     * This covers the JPA 2.1 use case where multiple converts can be added.
     */
    public Convert addConvert() {
        ConvertImpl convert = new ConvertImpl();
        getMetadata().getConverts().add(convert.getMetadata());
        return convert;
    }

    /**
     * This covers the EclipseLink Convert, single TEXT convert element.
     */
    public R setConvert(String name) {
        ConvertMetadata convert = new ConvertMetadata();
        convert.setText(name);
        getMetadata().getConverts().add(convert);
        return (R) this;
    }

    public Enumerated setEnumerated() {
        EnumeratedImpl enumerated = new EnumeratedImpl();
        getMetadata().setEnumerated(enumerated.getMetadata());
        return enumerated;
    }

    public R setFetch(String fetch) {
        getMetadata().setFetch(fetch);
        return (R) this;
    }

    public Lob setLob() {
        LobImpl lob = new LobImpl();
        getMetadata().setLob(lob.getMetadata());
        return lob;
    }

    public R setOptional(Boolean optional) {
        getMetadata().setOptional(optional);
        return (R) this;
    }

    public Temporal setTemporal() {
        TemporalImpl temporal = new TemporalImpl();
        getMetadata().setTemporal(temporal.getMetadata());
        return temporal;
    }

}
