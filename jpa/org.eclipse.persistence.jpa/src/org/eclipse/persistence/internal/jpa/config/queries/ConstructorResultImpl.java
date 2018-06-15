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
package org.eclipse.persistence.internal.jpa.config.queries;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.queries.ColumnResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.ConstructorResultMetadata;
import org.eclipse.persistence.jpa.config.ColumnResult;
import org.eclipse.persistence.jpa.config.ConstructorResult;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class ConstructorResultImpl extends MetadataImpl<ConstructorResultMetadata> implements ConstructorResult {

    public ConstructorResultImpl() {
        super(new ConstructorResultMetadata());

        getMetadata().setColumnResults(new ArrayList<ColumnResultMetadata>());
    }

    @Override
    public ColumnResult addColumnResult() {
        ColumnResultImpl columnResult = new ColumnResultImpl();
        getMetadata().getColumnResults().add(columnResult.getMetadata());
        return columnResult;
    }

    @Override
    public ConstructorResult setTargetClass(String targetClass) {
        getMetadata().setTargetClassName(targetClass);
        return this;
    }

}
