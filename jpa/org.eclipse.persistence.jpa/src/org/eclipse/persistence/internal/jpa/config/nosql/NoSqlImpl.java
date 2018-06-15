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
package org.eclipse.persistence.internal.jpa.config.nosql;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.nosql.NoSqlMetadata;
import org.eclipse.persistence.jpa.config.NoSql;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class NoSqlImpl extends MetadataImpl<NoSqlMetadata> implements NoSql {

    public NoSqlImpl() {
        super(new NoSqlMetadata());
    }

    @Override
    public NoSql setDataFormat(String dataFormat) {
        getMetadata().setDataFormat(dataFormat);
        return this;
    }

    @Override
    public NoSql setDataType(String dataType) {
        getMetadata().setDataType(dataType);
        return this;
    }

}
