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

package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.internal.oxm.mappings.Field;

/** INTERNAL:
 * <p><b>Purpose</b>: This class holds onto a class name and an XMLField in order to read and write
 * choice mappings from deployment.xml
 * @author mmacivor
 */
public class XMLChoiceFieldToClassAssociation <
   CONVERTER extends CoreConverter,
   XML_FIELD extends Field
>
{
    protected String className;
    protected XML_FIELD xmlField;
    protected CONVERTER converter;

    public XMLChoiceFieldToClassAssociation() {
    }

    public XMLChoiceFieldToClassAssociation(XML_FIELD xmlField, String className) {
        this.xmlField = xmlField;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String name) {
        this.className = name;
    }

    public XML_FIELD getXmlField() {
        return xmlField;
    }

    public void setXmlField(XML_FIELD field) {
        this.xmlField = field;
    }

    public CONVERTER getConverter() {
        return this.converter;
    }

    public void setConverter(CONVERTER valueConverter) {
        this.converter = valueConverter;
    }
}
