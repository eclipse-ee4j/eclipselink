/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.structconverter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;

import org.eclipse.persistence.platform.database.converters.StructConverter;

public class DummyStructConverter implements StructConverter {

    public String getStructName() {
        return "Dummy Struct";
    }

    public Class getJavaType() {
        return DummyStructConverterType.class;
    }

    public Object convertToObject(Struct struct) throws SQLException {
        return null;
    }

    public Struct convertToStruct(Object geometry, Connection connection) throws SQLException {
        return null;
    }

}
