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
package org.eclipse.persistence.internal.jpa.config.sequencing;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;
import org.eclipse.persistence.jpa.config.GeneratedValue;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class GeneratedValueImpl extends MetadataImpl<GeneratedValueMetadata> implements GeneratedValue {

    public GeneratedValueImpl() {
        super(new GeneratedValueMetadata());
    }

    @Override
    public GeneratedValue setStrategy(String strategy) {
        getMetadata().setStrategy(strategy);
        return this;
    }

    @Override
    public GeneratedValue setGenerator(String generator) {
        getMetadata().setGenerator(generator);
        return this;
    }

}
