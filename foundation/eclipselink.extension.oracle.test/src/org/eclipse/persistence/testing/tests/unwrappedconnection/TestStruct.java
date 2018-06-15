/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     James Sutherland - Adding wrapping
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import java.sql.SQLException;
import java.sql.Struct;
import java.util.Map;

public class TestStruct implements Struct {

    private Struct struct;

    public TestStruct(Struct struct){
        this.struct = struct;
    }

    @Override
    public Object[] getAttributes() throws SQLException {
        return struct.getAttributes();
    }

    @Override
    public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
        return struct.getAttributes(map);
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return struct.getSQLTypeName();
    }

}
