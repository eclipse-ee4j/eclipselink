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

import java.sql.Ref;
import java.sql.SQLException;
import java.util.Map;

public class TestRef implements Ref {

    private Ref ref;

    public TestRef(Ref ref) {
        this.ref = ref;
    }

    @Override
    public String getBaseTypeName() throws SQLException {
        return ref.getBaseTypeName();
    }

    @Override
    public Object getObject() throws SQLException {
        return ref.getObject();
    }

    @Override
    public Object getObject(Map<String, Class<?>> map) throws SQLException {
        return ref.getObject(map);
    }

    @Override
    public void setObject(Object value) throws SQLException {
        ref.setObject(value);
    }

}
