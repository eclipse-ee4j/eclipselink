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
 *      *     30/05/2012-2.4 Guy Pelletier    
 *       - 354678: Temp classloader is still being used during metadata processing
 ******************************************************************************/  
package org.eclipse.persistence.mappings.converters;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.EnumSet;
import java.util.Iterator;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 * <b>Purpose</b>: Object type converter is used to match a fixed number of 
 * database data values to a Java enum object value. It can be used when the 
 * values on the database and in the Java differ. To create an object type 
 * converter, simply specify the set of conversion value pairs. A default value 
 * and one-way conversion are also supported for legacy data situations.
 *
 * @author Guy Pelletier
 * @since Toplink 10.1.4RI
 */
public class EnumTypeConverter extends ObjectTypeConverter {
    private Class m_enumClass;
    private String m_enumClassName;
    private boolean m_useOrdinalValues;
    
    /**
     * PUBLIC:
     * Creating an enum converter this way will create the conversion values
     * for you using ordinal or name values.
     */
    public EnumTypeConverter(DatabaseMapping mapping, Class enumClass, boolean useOrdinalValues) {
        super(mapping);
        m_enumClass = enumClass;
        m_enumClassName = enumClass.getName();
        m_useOrdinalValues = useOrdinalValues;
        initializeConversions(m_enumClass);
    }

    /**
     * PUBLIC:
     * Creating an enum converter this way will create the conversion values
     * for you using ordinal or name values.
     */
    public EnumTypeConverter(DatabaseMapping mapping, String enumClassName, boolean useOrdinalValues) {
        this(mapping, enumClassName);
        m_useOrdinalValues = useOrdinalValues;
    }

    /**
     * PUBLIC:
     * Creating an enum converter this way expects that you will provide
     * the conversion values separately.
     */
    public EnumTypeConverter(DatabaseMapping mapping, String enumClassName) {
        super(mapping);
        m_enumClassName = enumClassName;
    }

    protected void initializeConversions(Class enumClass) {
        // Initialize conversion if not already set by Converter
        if (getFieldToAttributeValues().isEmpty()) {
            EnumSet theEnums = EnumSet.allOf(enumClass);
            Iterator<Enum> i = theEnums.iterator();
            
            while (i.hasNext()) {
                Enum theEnum = i.next();
                
                if (m_useOrdinalValues) {
                    addConversionValue(theEnum.ordinal(), theEnum.name());
                } else {
                    addConversionValue(theEnum.name(), theEnum.name());
                }
            }
        }
    }

    public Class getEnumClass() {
        return m_enumClass;
    }
    
    public String getEnumClassName() {
        return m_enumClassName;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this converter to actual 
     * class-based settings. This method is used when converting a project 
     * that has been built with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        super.convertClassNamesToClasses(classLoader);
        
        // convert if enumClass is null or if different classLoader
        if (m_enumClass == null || 
            (m_enumClass != null && !m_enumClass.getClassLoader().equals(classLoader))) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        m_enumClass = (Class)AccessController.doPrivileged(
                            new PrivilegedClassForName(m_enumClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(
                            m_enumClassName, exception.getException());
                    }
                } else {
                    m_enumClass = PrivilegedAccessHelper.getClassForName(m_enumClassName, true,
                        classLoader);
                }
            } catch (ClassNotFoundException exception){
                throw ValidationException.classNotFoundWhileConvertingClassNames(m_enumClassName,
                    exception);
            }
        }
        initializeConversions(m_enumClass);
    }
    
    /**
     * INTERNAL:
     * Returns the corresponding attribute value for the specified field value.
     * Wraps the super method to return an Enum type from the string conversion.
     */
    public Object convertDataValueToObjectValue(Object fieldValue, Session session) {
        Object obj = super.convertDataValueToObjectValue(fieldValue, session);
        
        if (fieldValue == null || obj == null) {
            return obj;
        } else {
            return Enum.valueOf(m_enumClass, (String) obj);
        }
    }
    
    /**
     * INTERNAL:
     * Convert Enum object to the data value. Internal enums are stored as
     * strings (names) so this method wraps the super method in that if
     * breaks down the enum to a string name before converting it.
     */
    public Object convertObjectValueToDataValue(Object attributeValue, Session session) {
        if (attributeValue == null) {
            return super.convertObjectValueToDataValue(null, session);
        } else {
            return super.convertObjectValueToDataValue(((Enum)attributeValue).name(), session);
        }
    }
}
