/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.sql.Ref;
import java.sql.SQLException;
import java.util.Map;

public class TestRef implements Ref {

    private Ref ref;
    
    public TestRef(Ref ref){
        this.ref = ref;
    }

    public String getBaseTypeName() throws SQLException {
        return ref.getBaseTypeName();
    }

    public Object getObject() throws SQLException {
        return ref.getObject();
    }

    public Object getObject(Map<String, Class<?>> map) throws SQLException {
        return ref.getObject(map);
    }

    public void setObject(Object value) throws SQLException {
        ref.setObject(value);
    }
        
}
