package org.eclipse.persistence.internal.oxm;

/*******************************************************************************
* Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - June 19/2008 - 1.0 - Initial implementation
******************************************************************************/

import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.mappings.converters.Converter;

/** INTERNAL:
 * <p><b>Purpose</b>: This class holds onto a class name and an XMLField in order to read and write
 * choice mappings from deployment.xml 
 * @author mmacivor
 */
public class XMLChoiceFieldToClassAssociation <
   XML_FIELD extends Field
>
{
    protected String className;
    protected XML_FIELD xmlField;
    protected Converter converter;
    
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
    
    public Converter getConverter() {
        return this.converter;
    }
    
    public void setConverter(Converter valueConverter) {
        this.converter = valueConverter;
    }
}