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
package org.eclipse.persistence.jaxb.compiler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import org.eclipse.persistence.jaxb.javamodel.Helper;

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
 * @see org.eclipse.persistence.jaxb.AnnotationsProcessor
 * @author mmacivor
 *
 */
public class EnumTypeInfo extends TypeInfo {
    private String m_className;
    private QName m_restrictionBase;
    private List<Object> objectValues;
    private List<String> fieldValues;
    
    public EnumTypeInfo(Helper helper) {
        super(helper);
        objectValues = new ArrayList<Object>();
        fieldValues = new ArrayList<String>();
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
      
    public void addObjectToFieldValuePair(Object theObject, String theValue){
    	objectValues.add(theObject);
    	fieldValues.add(theValue);
    }
    
    public boolean isEnumerationType() {
        return true;
    }

	public List<String> getFieldValues() {
		return fieldValues;
	}

	public List<Object> getObjectValues() {
		return objectValues;
	}
	
}
