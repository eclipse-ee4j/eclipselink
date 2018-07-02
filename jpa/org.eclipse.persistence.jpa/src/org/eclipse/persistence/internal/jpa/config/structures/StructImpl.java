/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.structures;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.structures.StructMetadata;
import org.eclipse.persistence.jpa.config.Struct;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class StructImpl extends MetadataImpl<StructMetadata> implements Struct {

    public StructImpl() {
        super(new StructMetadata());

        getMetadata().setFields(new ArrayList<String>());
    }

    public Struct addField(String field) {
        getMetadata().getFields().add(field);
        return this;
    }

    public Struct setName(String name) {
        getMetadata().setName(name);
        return this;
    }

}
