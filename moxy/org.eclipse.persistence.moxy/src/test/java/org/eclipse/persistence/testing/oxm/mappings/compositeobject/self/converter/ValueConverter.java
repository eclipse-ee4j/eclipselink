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
//     rbarkhouse - 2009-10-14 11:21:57 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.converter;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

public class ValueConverter implements Converter {

    public void initialize(DatabaseMapping mapping, Session session) {
    }

    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        String[] stringArray = (String[]) objectValue;
        IntermediateValue intermediateValue = new IntermediateValue();
        intermediateValue.setPartA(stringArray[0]);
        for(int x=1; x<stringArray.length; x++) {
            intermediateValue.getPartB().add(stringArray[x]);
        }
        return intermediateValue;
    }

    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        IntermediateValue intermediateValue = (IntermediateValue) dataValue;
        String[] stringArray = new String[intermediateValue.getPartB().size() + 1];
        stringArray[0] = intermediateValue.getPartA();
        for(int x=0; x<intermediateValue.getPartB().size(); x++) {
            stringArray[x + 1] = intermediateValue.getPartB().get(x);
        }
        return stringArray;
    }

    public boolean isMutable() {
        return true;
    }

}
