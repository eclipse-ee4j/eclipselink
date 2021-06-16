/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
