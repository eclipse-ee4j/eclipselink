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
package org.eclipse.persistence.internal.jpa.config.queries;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.queries.OracleObjectTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLParameterMetadata;
import org.eclipse.persistence.jpa.config.OracleObject;
import org.eclipse.persistence.jpa.config.PlsqlParameter;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class OracleObjectImpl extends MetadataImpl<OracleObjectTypeMetadata> implements OracleObject {

    public OracleObjectImpl() {
        super(new OracleObjectTypeMetadata());

        getMetadata().setFields(new ArrayList<PLSQLParameterMetadata>());
    }

    public PlsqlParameter addField() {
        PlsqlParameterImpl field = new PlsqlParameterImpl();
        getMetadata().getFields().add(field.getMetadata());
        return field;
    }

    public OracleObject setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    public OracleObject setJavaType(String javaType) {
        getMetadata().setJavaType(javaType);
        return this;
    }

}
