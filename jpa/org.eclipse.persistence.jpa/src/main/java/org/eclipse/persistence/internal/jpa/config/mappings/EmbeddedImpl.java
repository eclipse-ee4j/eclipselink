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

import org.eclipse.persistence.internal.jpa.config.columns.AssociationOverrideImpl;
import org.eclipse.persistence.internal.jpa.config.columns.FieldImpl;
import org.eclipse.persistence.internal.jpa.config.converters.ConvertImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.jpa.config.AssociationOverride;
import org.eclipse.persistence.jpa.config.Convert;
import org.eclipse.persistence.jpa.config.Embedded;
import org.eclipse.persistence.jpa.config.Field;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class EmbeddedImpl extends AbstractEmbeddedMappingImpl<EmbeddedAccessor, Embedded> implements Embedded {

    public EmbeddedImpl() {
        super(new EmbeddedAccessor());

        getMetadata().setAssociationOverrides(new ArrayList<AssociationOverrideMetadata>());
        getMetadata().setConverts(new ArrayList<ConvertMetadata>());
    }

    @Override
    public AssociationOverride addAssociationOverride() {
        AssociationOverrideImpl override = new AssociationOverrideImpl();
        getMetadata().getAssociationOverrides().add(override.getMetadata());
        return override;
    }

    @Override
    public Convert addConvert() {
        ConvertImpl convert = new ConvertImpl();
        getMetadata().getConverts().add(convert.getMetadata());
        return convert;
    }

    @Override
    public Field setField() {
        FieldImpl field = new FieldImpl();
        getMetadata().setField(field.getMetadata());
        return field;
    }

}
