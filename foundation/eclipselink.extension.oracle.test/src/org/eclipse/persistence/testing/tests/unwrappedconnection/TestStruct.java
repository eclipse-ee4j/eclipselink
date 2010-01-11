/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - Adding wrapping
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import java.sql.Struct;
import java.sql.SQLException;
import java.util.Map;

public class TestStruct implements Struct {

    private Struct struct;
    
    public TestStruct(Struct struct){
        this.struct = struct;
    }

    public Object[] getAttributes() throws SQLException {
        return struct.getAttributes();
    }

    public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
        return struct.getAttributes(map);
    }

    public String getSQLTypeName() throws SQLException {
        return struct.getSQLTypeName();
    }

        
}
