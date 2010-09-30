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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.xr;

//javase imports
import java.util.HashMap;
import java.util.Map;

//java eXtension imports
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.NULL_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

//EclipseLink imports
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.oxm.XMLConstants.ANY;
import static org.eclipse.persistence.oxm.XMLConstants.ANY_QNAME;
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
import static org.eclipse.persistence.oxm.XMLConstants.INTEGER;
import static org.eclipse.persistence.oxm.XMLConstants.INTEGER_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.INT_QNAME;
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
import static org.eclipse.persistence.oxm.XMLConstants.SWA_REF;
import static org.eclipse.persistence.oxm.XMLConstants.SWA_REF_QNAME;

public class QNameTransformer implements AttributeTransformer, FieldTransformer {

  public static final Map<String, QName> SCHEMA_QNAMES;
  static {
      SCHEMA_QNAMES = new HashMap<String, QName>() {
          {
              put(ANY, ANY_QNAME);
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
              put(SWA_REF, SWA_REF_QNAME);
          }
      };
  }
  private static final char COLON = ':';
  private static final String DEFAULT_NAMESPACE_PREFIX = "";

  AbstractTransformationMapping transformationMapping;
  private NamespaceResolver namespaceResolver;
  private String xPath;

  public QNameTransformer(String xPath) {
      super();
      this.xPath = xPath;
  }

  public void initialize(AbstractTransformationMapping mapping) {
      transformationMapping = mapping;
      namespaceResolver = ((XMLDescriptor)mapping.getDescriptor()).getNamespaceResolver();
  }

  public Object buildAttributeValue(Record record, Object object, Session session) {
      if (null == record) {
          return null;
      }
      String value = (String)record.get(xPath);
      if (null == value) {
          return null;
      }
      QName qName = null;
      int index = value.lastIndexOf(COLON);
      if (index > -1) {
          String prefix = value.substring(0, index);
          String localName = value.substring(index + 1);
          String namespaceURI = ((XMLRecord)record).resolveNamespacePrefix(prefix);
          // check for W3C_XML_SCHEMA_NS_URI - return TL_OX pre-built QName's
          if (W3C_XML_SCHEMA_NS_URI.equals(namespaceURI)) {
              qName = SCHEMA_QNAMES.get(localName);
              if (qName == null) { // unknown W3C_XML_SCHEMA_NS_URI type ?
                  qName = new QName(W3C_XML_SCHEMA_NS_URI, localName,
                      prefix == null ? DEFAULT_NS_PREFIX : prefix);
              }
          }
          else {
              qName = new QName(namespaceURI == null ? NULL_NS_URI : namespaceURI, localName,
                  prefix == null ? DEFAULT_NS_PREFIX : prefix);
          }
          return qName;
      }
      else {
          String namespaceURI = ((XMLRecord)record)
              .resolveNamespacePrefix(DEFAULT_NAMESPACE_PREFIX);
          qName = new QName(namespaceURI, value);
      }
      return qName;
  }

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
