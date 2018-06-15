/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb.compiler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>A specialized TypeInfo that stores additional information for a
 * Java 5 Enumeration type.
 * <p><b>Responsibilities:</b><ul>
 * <li>Hold onto the restriction base type for schema generation</li>
 * <li>Hold onto a map of Object Enum values to String values for Mapping generation</li>
 * </ul>
 *
 * @see org.eclipse.persistence.jaxb.compiler.TypeInfo
 * @see org.eclipse.persistence.jaxb.compiler.AnnotationsProcessor
 * @author mmacivor
 *
 */
public class EnumTypeInfo extends TypeInfo {
    private String m_className;
    private QName m_restrictionBase;
    private List<String> m_fieldNames;
    private List<Object> m_xmlEnumValues;

    public EnumTypeInfo(Helper helper, JavaClass javaClass) {
        super(helper, javaClass);
        m_fieldNames = new ArrayList<String>();
        m_xmlEnumValues = new ArrayList<Object>();
    }

    @Override
    public boolean isEnumerationType() {
        return true;
    }

    public String getClassName() {
        return m_className;
    }

    public void setClassName(String className) {
        m_className = className;
    }

    public QName getRestrictionBase() {
        return m_restrictionBase;
    }

    public void setRestrictionBase(QName restrictionBase) {
        m_restrictionBase = restrictionBase;
    }

    /**
     * Add a Java field name to XmlEnumValue pair.
     *
     * @param fieldName
     * @param xmlEnumValue
     */
    public void addJavaFieldToXmlEnumValuePair(String fieldName, Object xmlEnumValue) {
        m_fieldNames.add(fieldName);
        m_xmlEnumValues.add(xmlEnumValue);
    }

    /**
     * Add a Java field name to XmlEnumValue pair.  If an entry exists at the specified
     * with the same fieldName, its value will be overridden.  A value of true for
     * 'override' will typically be used when performing overrides via XML metadata
     * in XmlProcessor.
     *
     * @param override
     * @param fieldName
     * @param xmlEnumValue
     */
    public void addJavaFieldToXmlEnumValuePair(boolean override, String fieldName, Object xmlEnumValue) {
        if (!override) {
            addJavaFieldToXmlEnumValuePair(fieldName, xmlEnumValue);
        } else {
            int idx = getIndexForJavaField(fieldName);
            if (idx == -1) {
                // the entry doesn't exist, so add a new one
                addJavaFieldToXmlEnumValuePair(fieldName, xmlEnumValue);
            } else {
                // entry already exists, so replace the existing value
                m_xmlEnumValues.remove(idx);
                m_xmlEnumValues.add(idx, xmlEnumValue);
            }
        }
    }

    public List<String> getFieldNames() {
        return m_fieldNames;
    }

    public List<Object> getXmlEnumValues() {
        return m_xmlEnumValues;
    }

    /**
     * Return the index in the fieldNames List for a given Java field
     * name, or -1 if it doesn't exist.
     *
     * @param fieldName
     * @return
     */
    private int getIndexForJavaField(String fieldName) {
        for (int i=0; i<m_fieldNames.size(); i++) {
            if (m_fieldNames.get(i).equals(fieldName)) {
                return i;
            }
        }
        return -1;
    }
}
