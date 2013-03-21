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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:Provide a means to Convert an Enumeration type to/from either a string representation
 * of the enum facet or a user defined value. 
 * 
 * <p><b>Responsibilities:</b><ul>
 * <li>Initialize the conversion values to be the Enum facets</li>
 * <li>Don't overwrite any existing, user defined conversion value</li>
 * 
 */
public class JAXBEnumTypeConverter extends ObjectTypeConverter {
    private Class m_enumClass;
    private String m_enumClassName;
	private boolean m_usesOrdinalValues;

    /**
     * PUBLIC:
     */
    public JAXBEnumTypeConverter(Mapping mapping, String enumClassName, boolean usesOrdinalValues) {
        super((DatabaseMapping)mapping);

        m_enumClassName = enumClassName;
		m_usesOrdinalValues = usesOrdinalValues;
    }
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this converter to actual 
     * class-based settings. This method is used when converting a project 
     * that has been built with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    m_enumClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(m_enumClassName, true, classLoader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(m_enumClassName, exception.getException());
                }
            } else {
                m_enumClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(m_enumClassName, true, classLoader);
            }
        } catch (ClassNotFoundException exception){
            throw ValidationException.classNotFoundWhileConvertingClassNames(m_enumClassName, exception);
        }
    }
    
    /**
     * INTERNAL:
     */
    public void initialize(DatabaseMapping mapping, Session session) {
        Iterator<Enum> i = EnumSet.allOf(m_enumClass).iterator();
        while (i.hasNext()) {
            Enum theEnum = i.next();
            if (this.getAttributeToFieldValues().get(theEnum) == null) {
                Object existingVal = this.getAttributeToFieldValues().get(theEnum.name()); 
                if (existingVal != null) {
                    this.getAttributeToFieldValues().remove(theEnum.name());
                    addConversionValue(existingVal, theEnum);
                } else {
                    // if there's no user defined value, create a default
                    if (m_usesOrdinalValues) {
                        addConversionValue(theEnum.ordinal(), theEnum);
                    } else {
                        addConversionValue(theEnum.name(), theEnum);
                    }
                }
            }
        }
        
        super.initialize(mapping, session);
    }
    
    /**
     * PUBLIC:
     * Returns true if this converter uses ordinal values for the enum
     * conversion.
     */
	public boolean usesOrdinalValues() {
		return m_usesOrdinalValues;   
    }
}
