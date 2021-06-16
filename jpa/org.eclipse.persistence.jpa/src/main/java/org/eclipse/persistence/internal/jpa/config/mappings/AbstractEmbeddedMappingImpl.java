/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.mappings;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.columns.AttributeOverrideImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.jpa.config.AttributeOverride;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public abstract class AbstractEmbeddedMappingImpl<T extends EmbeddedAccessor, R> extends AbstractMappingImpl<T, R> {

    public AbstractEmbeddedMappingImpl(T t) {
        super(t);

        getMetadata().setAttributeOverrides(new ArrayList<AttributeOverrideMetadata>());
    }

    public AttributeOverride addAttributeOverride() {
        AttributeOverrideImpl override = new AttributeOverrideImpl();
        getMetadata().getAttributeOverrides().add(override.getMetadata());
        return override;
    }
}
