/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
/*
   DESCRIPTION

   PRIVATE CLASSES

   NOTES


   MODIFIED    (MM/DD/YY)
    mfobrien    08/22/06 - Creation
 */

/**
 *  @version $Header: POJO.java 22-aug-2006.16:42:38 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.0
 */
package org.eclipse.persistence.testing.sdo.helper.pluggable.model;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

abstract class POJO {
    public Object get(String instanceVariableName) {
        Object anObject = null;
        return anObject;
    }

    public void set(String instanceVariableName, Object anObject) {
    }

    public String toString() {
        return toString(this);
    }

    private String toString(Object anObject) {
        StringBuffer aBuffer = new StringBuffer();
        Class aClass = anObject.getClass();
        do {
            //aBuffer.append(anObject.getClass().getSimpleName());
            aBuffer.append("[");
            // get public fields        
            Field[] fields = anObject.getClass().getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);

            // use 1.5 to iterate the objects fields
            //for(Field aField : fields) {
            Field aField = null;
            for (int i = 0; i < fields.length; i++) {
                aField = fields[i];
                if (aBuffer.toString().endsWith("[")) {
                    aBuffer.append(",");
                }
                aBuffer.append(aField.getName());
                aBuffer.append("=");
                try {
                    // override default access control denying private member access        	
                    aField.setAccessible(true);
                    // get will wrap primitives in their object class
                    Object aValue = aField.get(anObject);
                    aBuffer.append(toString(aValue));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            aBuffer.append("]");
            // iterate up the tree
            aClass = aClass.getSuperclass();
        } while (aClass != Object.class);

        //aBuffer.append(")");        
        return aBuffer.toString();
    }
}