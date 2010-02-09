package org.eclipse.persistence.internal.oxm;

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
* mmacivor - June 19/2008 - 1.0 - Initial implementation
******************************************************************************/

import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.mappings.converters.Converter;

/** INTERNAL:
 * <p><b>Purpose</b>: This class holds onto a class name and an XMLField in order to read and write
 * choice mappings from deployment.xml 
 * @author mmacivor
 */
public class XMLChoiceFieldToClassAssociation {
    protected String className;
    protected XMLField xmlField;
    protected Converter converter;
    
    public XMLChoiceFieldToClassAssociation() {
    }
    
    public XMLChoiceFieldToClassAssociation(XMLField xmlField, String className) {
        this.xmlField = xmlField;
        this.className = className;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String name) {
        this.className = name;
    }
    
    public XMLField getXmlField() {
        return xmlField;
    }
    
    public void setXmlField(XMLField field) {
        this.xmlField = field;
    }
    
    public Converter getConverter() {
        return this.converter;
    }
    
    public void setConverter(Converter valueConverter) {
        this.converter = valueConverter;
    }
}