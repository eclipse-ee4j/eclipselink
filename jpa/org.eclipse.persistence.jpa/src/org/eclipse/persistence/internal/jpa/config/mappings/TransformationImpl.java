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

import org.eclipse.persistence.internal.jpa.config.transformers.ReadTransformerImpl;
import org.eclipse.persistence.internal.jpa.config.transformers.WriteTransformerImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransformationAccessor;
import org.eclipse.persistence.internal.jpa.metadata.transformers.WriteTransformerMetadata;
import org.eclipse.persistence.jpa.config.ReadTransformer;
import org.eclipse.persistence.jpa.config.Transformation;
import org.eclipse.persistence.jpa.config.WriteTransformer;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class TransformationImpl extends AbstractDirectMappingImpl<TransformationAccessor, Transformation> implements Transformation {

    public TransformationImpl() {
        super(new TransformationAccessor());

        getMetadata().setWriteTransformers(new ArrayList<WriteTransformerMetadata>());
    }

    @Override
    public WriteTransformer addWriteTransformer() {
        WriteTransformerImpl transformer = new WriteTransformerImpl();
        getMetadata().getWriteTransformers().add(transformer.getMetadata());
        return transformer;
    }

    @Override
    public Transformation setMutable(Boolean mutable) {
        getMetadata().setMutable(mutable);
        return this;
    }

    @Override
    public ReadTransformer setReadTransformer() {
        ReadTransformerImpl transformer = new ReadTransformerImpl();
        getMetadata().setReadTransformer(transformer.getMetadata());
        return transformer;
    }

}
