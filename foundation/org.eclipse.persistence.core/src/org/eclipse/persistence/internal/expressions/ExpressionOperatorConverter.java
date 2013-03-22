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
package org.eclipse.persistence.internal.expressions;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 *  INTERNAL:
 *  Used by function operators in deployment xml generation to accomodate custom functions.
 *  There is no more validation on read because any custom function has to be accepted.
 *  The custom function is assumed to be a normal prefix function.  The first element in the 
 *  databaseStrings of the operator is in the format of databaseString(, e.g. AVG(.  "(" will
 *  be removed on write and attached back on read.
 */

public class ExpressionOperatorConverter extends ObjectTypeConverter  {
    /**
     *  INTERNAL:
     *  Convert to the data value.
     */
    public Object convertObjectValueToDataValue(Object attributeValue, Session session) {
        Object fieldValue;
        if (attributeValue == null) {
            fieldValue = getAttributeToFieldValues().get(Helper.NULL_VALUE);
        } else {
            fieldValue = getAttributeToFieldValues().get(attributeValue);
            if (fieldValue == null) {
                //Custom function.  Remove "(".
                if (((ExpressionOperator)attributeValue).getDatabaseStrings() != null) {
                    String databaseString = ((ExpressionOperator)attributeValue).getDatabaseStrings()[0];
                    fieldValue = databaseString.substring(0, databaseString.length()-1);                    
                } else {
                    throw DescriptorException.noAttributeValueConversionToFieldValueProvided(attributeValue, getMapping());
                }
            }
        }
        return fieldValue;
    }

    /**
     * INTERNAL:
     * Returns the corresponding attribute value for the specified field value.
     */
    public Object convertDataValueToObjectValue(Object fieldValue, Session session) {
        Object attributeValue = null;

        if (fieldValue == null) {
            attributeValue = getFieldToAttributeValues().get(Helper.NULL_VALUE);
        } else {
            try {
                fieldValue = ((AbstractSession)session).getDatasourcePlatform().getConversionManager().convertObject(fieldValue, getFieldClassification());
            } catch (ConversionException e) {
                throw ConversionException.couldNotBeConverted(mapping, mapping.getDescriptor(), e);
            }

            attributeValue = getFieldToAttributeValues().get(fieldValue);
            //Custom function.  Create an operator for it.
            if (attributeValue == null) {
                attributeValue = ExpressionOperator.simpleFunction(0, (String)fieldValue);
            }
        }
        return attributeValue;
    }
}
