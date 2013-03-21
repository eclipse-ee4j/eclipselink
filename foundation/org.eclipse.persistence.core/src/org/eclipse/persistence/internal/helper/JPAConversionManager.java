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
package org.eclipse.persistence.internal.helper;

/**
 * <p>
 * <b>Purpose</b>: Extension to the existing conversion manager to support the
 * EJB 3.0 spec. 
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Allow a null value default to be read into primitives. With the current
 * conversion manager, setting a null into a primitive causes and exception. 
 * This conversion manager was added to avoid that exception and therefore, add 
 * support for schemas that were built before the object model was mapped 
 * (using a primitive). Therefore, EclipseLink will not change the null column value 
 * in the database through this conversion. The value on the database will only 
 * be changed if the user actually sets a new primitive value.
 * <li> Allows users to define their own set of default null values to be used
 * in the conversion.
 * </ul>
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.4 RI
 */
public class JPAConversionManager extends ConversionManager {
    public JPAConversionManager() {
        super();
    }

    /**
     * INTERNAL:
     */
    public Object getDefaultNullValue(Class theClass) {
        Object defaultNullValue = null;
        if (this.defaultNullValues != null){
            defaultNullValue = getDefaultNullValues().get(theClass);
        }
        if (defaultNullValue == null && theClass.isPrimitive()) {
            if(Double.TYPE.equals(theClass)){
                return Double.valueOf(0D);
            }  else if(Long.TYPE.equals(theClass)) {
                return Long.valueOf(0L);
            } else if(Character.TYPE.equals(theClass)){
                return Character.valueOf('\u0000');
            } else if(Float.TYPE.equals(theClass)){
                return Float.valueOf(0F); 
            } else if(Short.TYPE.equals(theClass)){
                return Short.valueOf((short)0);
            } else if(Byte.TYPE.equals(theClass)){
                return Byte.valueOf((byte)0);
            } else if(Boolean.TYPE.equals(theClass)){
                return Boolean.FALSE;
            } else {
                return 0;
            }
        } else {
            return defaultNullValue;
        }
    }
}
