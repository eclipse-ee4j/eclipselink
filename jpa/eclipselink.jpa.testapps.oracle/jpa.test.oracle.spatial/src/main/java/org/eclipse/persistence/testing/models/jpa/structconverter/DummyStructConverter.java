/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.structconverter;

import org.eclipse.persistence.platform.database.converters.StructConverter;

import java.sql.Connection;
import java.sql.Struct;

public class DummyStructConverter implements StructConverter {

    public DummyStructConverter() {
    }

    @Override
    public String getStructName() {
        return "Dummy Struct";
    }

    @Override
    public Class<?> getJavaType() {
        return DummyStructConverterType.class;
    }

    @Override
    public Object convertToObject(Struct struct) {
        return null;
    }

    @Override
    public Struct convertToStruct(Object geometry, Connection connection) {
        return null;
    }

}
