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
 *     rbarkhouse - 2009-10-14 11:21:57 - initial implementation
 ******************************************************************************/
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