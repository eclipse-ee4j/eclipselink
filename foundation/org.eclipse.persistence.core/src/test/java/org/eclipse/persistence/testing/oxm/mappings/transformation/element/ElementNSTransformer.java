/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - Feb 23/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.transformation.element;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

public class ElementNSTransformer implements AttributeTransformer, FieldTransformer {

    private static String XML_START = "START";
    private static String XML_INTERMEDIATE = "INTERMEDIATE";
    private static String XML_END = "END";

    private XMLField startField;
    private XMLField intermediateField;
    private XMLField endField;

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns", "urn:element");

        startField = new XMLField("ns:" + XML_START);
        startField.setNamespaceResolver(nsResolver);

        intermediateField = new XMLField("ns:" + XML_INTERMEDIATE);
        intermediateField.setNamespaceResolver(nsResolver);

        endField = new XMLField("ns:" + XML_END);
        endField.setNamespaceResolver(nsResolver);
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        if(null == dataRecord) {
            return null;
        }
        XMLRecord xmlRecord = (XMLRecord) dataRecord;

        if(xmlRecord.getIndicatingNoEntry(startField) != null) {
            return XML_START;
        } else if (xmlRecord.getIndicatingNoEntry(intermediateField) != null) {
            return XML_INTERMEDIATE;
        } else if (xmlRecord.getIndicatingNoEntry(endField) != null) {
            return XML_END;
        } else {
            return "FAIL";
        }
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        Root root = (Root) instance;
        if(fieldName.endsWith(root.getElement())) {
            return "";
        } else {
            return null;
        }
    }

}
