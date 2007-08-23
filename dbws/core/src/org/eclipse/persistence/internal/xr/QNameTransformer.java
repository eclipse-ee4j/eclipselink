/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

package org.eclipse.persistence.internal.xr;

// Javase imports
import java.util.HashMap;
import java.util.Map;

//Java extension imports
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.NULL_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// TopLink imports
import org.eclipse.persistence.mappings.transformers.AttributeTransformerAdapter;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.oxm.XMLConstants.ANY_SIMPLE_TYPE;
import static org.eclipse.persistence.oxm.XMLConstants.ANY_SIMPLE_TYPE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.BOOLEAN;
import static org.eclipse.persistence.oxm.XMLConstants.BOOLEAN_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.BYTE;
import static org.eclipse.persistence.oxm.XMLConstants.BYTE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_TIME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_TIME_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DECIMAL;
import static org.eclipse.persistence.oxm.XMLConstants.DECIMAL_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DOUBLE;
import static org.eclipse.persistence.oxm.XMLConstants.DOUBLE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.FLOAT;
import static org.eclipse.persistence.oxm.XMLConstants.FLOAT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.HEX_BINARY;
import static org.eclipse.persistence.oxm.XMLConstants.HEX_BINARY_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.INT;
import static org.eclipse.persistence.oxm.XMLConstants.INT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.INTEGER;
import static org.eclipse.persistence.oxm.XMLConstants.INTEGER_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.LONG;
import static org.eclipse.persistence.oxm.XMLConstants.LONG_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.QNAME_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.SHORT;
import static org.eclipse.persistence.oxm.XMLConstants.SHORT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.STRING;
import static org.eclipse.persistence.oxm.XMLConstants.STRING_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.TIME;
import static org.eclipse.persistence.oxm.XMLConstants.TIME_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.UNSIGNED_BYTE;
import static org.eclipse.persistence.oxm.XMLConstants.UNSIGNED_BYTE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.UNSIGNED_INT;
import static org.eclipse.persistence.oxm.XMLConstants.UNSIGNED_INT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.UNSIGNED_SHORT;
import static org.eclipse.persistence.oxm.XMLConstants.UNSIGNED_SHORT_QNAME;

@SuppressWarnings("serial")
public class QNameTransformer extends AttributeTransformerAdapter {
    
    public static final Map<String, QName> SCHEMA_QNAMES;
    static {
        SCHEMA_QNAMES = new HashMap<String, QName>() {{
            put(ANY_SIMPLE_TYPE, ANY_SIMPLE_TYPE_QNAME);
            put(BASE_64_BINARY, BASE_64_BINARY_QNAME);
            put(BOOLEAN, BOOLEAN_QNAME);
            put(BYTE, BYTE_QNAME);
            put(DATE, DATE_QNAME);
            put(DATE_TIME, DATE_TIME_QNAME);
            put(DECIMAL, DECIMAL_QNAME);
            put(DOUBLE, DOUBLE_QNAME);
            put(FLOAT, FLOAT_QNAME);
            put(HEX_BINARY, HEX_BINARY_QNAME);
            put(INT, INT_QNAME);
            put(INTEGER, INTEGER_QNAME);
            put(LONG, LONG_QNAME);
            put(QNAME, QNAME_QNAME);
            put(SHORT, SHORT_QNAME);
            put(STRING, STRING_QNAME);
            put(TIME, TIME_QNAME);
            put(UNSIGNED_BYTE, UNSIGNED_BYTE_QNAME);
            put(UNSIGNED_INT, UNSIGNED_INT_QNAME);
            put(UNSIGNED_SHORT, UNSIGNED_SHORT_QNAME);
        }};
    }

    @Override
    public Object buildAttributeValue(Record record, Object object, Session session) {
        QName qname = null;
        String qNameAsString = record == null ? null : (String)record.get("type/text()");
        String nsURI = null;
        String prefix = null;
        String localPart = null;
        if (qNameAsString != null) {
            if (qNameAsString.charAt(0) == '{') {
                int endOfNamespaceURI = qNameAsString.indexOf('}');
                if (endOfNamespaceURI == -1) {
                    throw new IllegalArgumentException(
                       "cannot create QName from \"" + qNameAsString + "\", missing closing \"}\"");
                }
                nsURI = qNameAsString.substring(1, endOfNamespaceURI);
                localPart = qNameAsString.substring(endOfNamespaceURI + 1);
            }
            else {
                int colonIdx = qNameAsString.indexOf(':');
                if (colonIdx > 0) {
                    prefix = qNameAsString.substring(0, colonIdx);
                    localPart = qNameAsString.substring(colonIdx+1);
                    DOMRecord domRecord = (DOMRecord)record;
                    nsURI = domRecord.resolveNamespacePrefix(prefix);
                    if (nsURI == null) {
                        nsURI =  DEFAULT_NS_PREFIX;
                    }
                }
                else {
                    localPart = qNameAsString;
                }
            }
            // check for W3C_XML_SCHEMA_NS_URI - return TL_OX pre-built QName's
            if (W3C_XML_SCHEMA_NS_URI.equals(nsURI)) {
                qname = SCHEMA_QNAMES.get(localPart);
                if (qname == null) { // unknown W3C_XML_SCHEMA_NS_URI type ? 
                    qname = new QName(W3C_XML_SCHEMA_NS_URI, localPart, 
                        prefix == null ? DEFAULT_NS_PREFIX : prefix);
                }
            }
            else {
                qname = new QName(nsURI == null ? NULL_NS_URI : nsURI, 
                    localPart, prefix == null ? DEFAULT_NS_PREFIX : prefix);
            }
        }
        return qname;
    }
}