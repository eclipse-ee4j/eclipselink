/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.queries;


import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>:
 * Concrete class to represent the FieldResult structure as defined by
 * the EJB 3.0 Persistence specification.  This class is a subcompent of the 
 * EntityResult.
 * 
 * @see EntityResult
 * @author Gordon Yorke
 * @since TopLink Java Essentials
 */

public class FieldResult {
    /** Stores the name of the bean attribute  */
    protected String attributeName;
    /** Stores passed in field name split on the '.' character */
    protected String[] multipleFieldIdentifiers;
    /** FieldResult now can contain multiple FieldResults in a collection if an attribute has multiple fields */
    java.util.Vector fieldResults;
    
    /** Stores the Columns name from the result set that contains the attribute value */
    protected String columnName;
    
    public FieldResult(String attributeName, String column){
        this.columnName = column;
        if (attributeName == null || this.columnName == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_values_for_field_result"));
        }
        multipleFieldIdentifiers = attributeName.split("\\.",0);
        this.attributeName = multipleFieldIdentifiers[0];
    }
    
    public String getAttributeName(){
        return this.attributeName;
    }
    
    public String getColumnName(){
        return this.columnName;
    }
    
    /**
     * INTERNAL:
     * This method is a convience method for extracting values from Results
     */
    public Object getValueFromRecord(DatabaseRecord record){
        return record.get(this.columnName);
    }
    
    /**
     * INTERNAL:
     */
    public java.util.Vector getFieldResults(){
        return fieldResults;
    }
    
    /**
     * INTERNAL:
     */
    public String[] getMultipleFieldIdentifiers(){
        return multipleFieldIdentifiers;
    }
    
    /**
     * INTERNAL:
     * This method is used to support mapping multiple fields, fields are 
     * concatenated/added to one fieldResult.
     */
    public void add(FieldResult newFieldResult){
      if( fieldResults ==null){
          fieldResults = new java.util.Vector();
          fieldResults.add(this);
      }
      fieldResults.add(newFieldResult);
    }
    
}
