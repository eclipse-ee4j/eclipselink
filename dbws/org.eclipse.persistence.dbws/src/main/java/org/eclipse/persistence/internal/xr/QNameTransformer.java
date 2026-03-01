/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.xr;

//javase imports
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static org.eclipse.persistence.internal.oxm.Constants.ANY;
import static org.eclipse.persistence.internal.oxm.Constants.ANY_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.ANY_SIMPLE_TYPE;
import static org.eclipse.persistence.internal.oxm.Constants.ANY_SIMPLE_TYPE_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.BASE_64_BINARY;
import static org.eclipse.persistence.internal.oxm.Constants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.BOOLEAN;
import static org.eclipse.persistence.internal.oxm.Constants.BOOLEAN_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.BYTE;
import static org.eclipse.persistence.internal.oxm.Constants.BYTE_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.DATE;
import static org.eclipse.persistence.internal.oxm.Constants.DATE_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.DATE_TIME;
import static org.eclipse.persistence.internal.oxm.Constants.DATE_TIME_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.DECIMAL;
import static org.eclipse.persistence.internal.oxm.Constants.DECIMAL_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.DOUBLE;
import static org.eclipse.persistence.internal.oxm.Constants.DOUBLE_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.FLOAT;
import static org.eclipse.persistence.internal.oxm.Constants.FLOAT_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.HEX_BINARY;
import static org.eclipse.persistence.internal.oxm.Constants.HEX_BINARY_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.INT;
import static org.eclipse.persistence.internal.oxm.Constants.INTEGER;
import static org.eclipse.persistence.internal.oxm.Constants.INTEGER_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.INT_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.LONG;
import static org.eclipse.persistence.internal.oxm.Constants.LONG_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.QNAME_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.SHORT;
import static org.eclipse.persistence.internal.oxm.Constants.SHORT_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.STRING;
import static org.eclipse.persistence.internal.oxm.Constants.STRING_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.SWA_REF;
import static org.eclipse.persistence.internal.oxm.Constants.SWA_REF_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.TIME;
import static org.eclipse.persistence.internal.oxm.Constants.TIME_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.UNSIGNED_BYTE;
import static org.eclipse.persistence.internal.oxm.Constants.UNSIGNED_BYTE_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.UNSIGNED_INT;
import static org.eclipse.persistence.internal.oxm.Constants.UNSIGNED_INT_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.UNSIGNED_SHORT;
import static org.eclipse.persistence.internal.oxm.Constants.UNSIGNED_SHORT_QNAME;

import java.util.Map;

//java eXtension imports
import javax.xml.namespace.QName;

//EclipseLink imports
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

public class QNameTransformer implements AttributeTransformer, FieldTransformer {

  public static final Map<String, QName> SCHEMA_QNAMES;
  static {
      SCHEMA_QNAMES = Map.ofEntries(
              Map.entry(ANY, ANY_QNAME),
              Map.entry(ANY_SIMPLE_TYPE, ANY_SIMPLE_TYPE_QNAME),
              Map.entry(BASE_64_BINARY, BASE_64_BINARY_QNAME),
              Map.entry(BOOLEAN, BOOLEAN_QNAME),
              Map.entry(BYTE, BYTE_QNAME),
              Map.entry(DATE, DATE_QNAME),
              Map.entry(DATE_TIME, DATE_TIME_QNAME),
              Map.entry(DECIMAL, DECIMAL_QNAME),
              Map.entry(DOUBLE, DOUBLE_QNAME),
              Map.entry(FLOAT, FLOAT_QNAME),
              Map.entry(HEX_BINARY, HEX_BINARY_QNAME),
              Map.entry(INT, INT_QNAME),
              Map.entry(INTEGER, INTEGER_QNAME),
              Map.entry(LONG, LONG_QNAME),
              Map.entry(QNAME, QNAME_QNAME),
              Map.entry(SHORT, SHORT_QNAME),
              Map.entry(STRING, STRING_QNAME),
              Map.entry(TIME, TIME_QNAME),
              Map.entry(UNSIGNED_BYTE, UNSIGNED_BYTE_QNAME),
              Map.entry(UNSIGNED_INT, UNSIGNED_INT_QNAME),
              Map.entry(UNSIGNED_SHORT, UNSIGNED_SHORT_QNAME),
              Map.entry(SWA_REF, SWA_REF_QNAME));
  }
  private static final char COLON = ':';
  private static final String DEFAULT_NAMESPACE_PREFIX = "";

  AbstractTransformationMapping transformationMapping;
  private transient NamespaceResolver namespaceResolver;
  private final String xPath;

  public QNameTransformer(String xPath) {
      super();
      this.xPath = xPath;
  }

  @Override
  public void initialize(AbstractTransformationMapping mapping) {
      transformationMapping = mapping;
      namespaceResolver = ((XMLDescriptor)mapping.getDescriptor()).getNamespaceResolver();
  }

  @Override
  public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
      if (null == dataRecord) {
          return null;
      }
      String value = (String) dataRecord.get(xPath);
      if (null == value) {
          return null;
      }
      int index = value.lastIndexOf(COLON);
      if (index > -1) {
          String prefix = value.substring(0, index);
          String localName = value.substring(index + 1);
          String namespaceURI = ((XMLRecord) dataRecord).resolveNamespacePrefix(prefix);
          // check for W3C_XML_SCHEMA_NS_URI - return TL_OX pre-built QName's
          if (W3C_XML_SCHEMA_NS_URI.equals(namespaceURI)) {
              QName qName = SCHEMA_QNAMES.get(localName);
              if (qName == null) { // unknown W3C_XML_SCHEMA_NS_URI type ?
                  return new QName(W3C_XML_SCHEMA_NS_URI, localName, prefix);
              }
          }
          return new QName(namespaceURI, localName, prefix);
      }
      String namespaceURI = ((XMLRecord) dataRecord).resolveNamespacePrefix(DEFAULT_NAMESPACE_PREFIX);
      return new QName(namespaceURI, value);
  }

  @Override
  public Object buildFieldValue(Object instance, String fieldName, Session session) {
      if (null == instance) {
          return null;
      }

      QName qName = (QName)transformationMapping.getAttributeValueFromObject(instance);
      if (null == qName.getNamespaceURI()) {
          return qName.getLocalPart();
      }
      else {
          String namespaceURI = qName.getNamespaceURI();
          String prefix = namespaceResolver.resolveNamespaceURI(namespaceURI);
          if (null == prefix) {
              return qName.getLocalPart();
          }
          else {
              return prefix + COLON + qName.getLocalPart();
          }
      }
  }
}
