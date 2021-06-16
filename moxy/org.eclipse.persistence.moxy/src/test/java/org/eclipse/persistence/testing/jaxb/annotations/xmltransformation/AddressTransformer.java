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
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class AddressTransformer implements FieldTransformer, AttributeTransformer {

    public void initialize(AbstractTransformationMapping mapping) {
    }

    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        if (instance instanceof EmployeeWithAddress) {
            if (fieldName.contains("street")) {
                return ((EmployeeWithAddress) instance).address.street;
            } else if (fieldName.contains("city")) {
                return ((EmployeeWithAddress) instance).address.city;
            }
        }
        if (instance instanceof EmployeeWithAddressAndTransformer) {
            if (fieldName.contains("street")) {
                return ((EmployeeWithAddressAndTransformer) instance).address.street;
            }else if (fieldName.contains("city")) {
                return ((EmployeeWithAddressAndTransformer) instance).address.city;
            }
        }
        return null;
    }

    public Object buildAttributeValue(Record record, Object object, Session session) {
        String street = (String) record.get("address/street/text()");
        String city =(String) record.get("address/city/text()");
        return new AddressNoCtor(street, city);
    }

}
