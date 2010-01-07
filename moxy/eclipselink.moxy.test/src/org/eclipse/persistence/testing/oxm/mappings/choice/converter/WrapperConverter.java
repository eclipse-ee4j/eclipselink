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
*     bdoughan - August 6/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.choice.converter;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

public class WrapperConverter implements Converter {

    public void initialize(DatabaseMapping mapping, Session session) {
    }

    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        Wrapper wrapper = new Wrapper();
        wrapper.setValue(dataValue);
        return wrapper;
    }

    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        Wrapper wrapper = (Wrapper) objectValue;
        return wrapper.getValue();
    }

    public boolean isMutable() {
        return false;
    }

}