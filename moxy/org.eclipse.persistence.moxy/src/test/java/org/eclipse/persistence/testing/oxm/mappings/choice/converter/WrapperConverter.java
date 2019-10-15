/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - August 6/2009 - 2.0 - Initial implementation
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
