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
*     bdoughan - Feb 23/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.transformation.element;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class ElementTransformer implements AttributeTransformer, FieldTransformer {

    private static String XML_START = "START";
    private static String XML_INTERMEDIATE = "INTERMEDIATE";
    private static String XML_END = "END";

    public void initialize(AbstractTransformationMapping mapping) {
    }

    public Object buildAttributeValue(Record record, Object object, Session session) {
        if(null == record) {
            return null;
        }
        XMLRecord xmlRecord = (XMLRecord) record;
        if(xmlRecord.getIndicatingNoEntry(XML_START) != null) {
            return XML_START;
        } else if (xmlRecord.getIndicatingNoEntry(XML_INTERMEDIATE) != null) {
            return XML_INTERMEDIATE;
        } else if (xmlRecord.getIndicatingNoEntry(XML_END) != null) {
            return XML_END;
        } else {
            return "FAIL";
        }
    }

    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        Root root = (Root) instance;
        if(fieldName.equals(root.getElement())) {
            return "";
        } else {
            return null;
        }
    }

}
