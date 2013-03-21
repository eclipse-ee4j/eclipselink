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
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.io.Serializable;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>:
 * Concrete class to represent the FieldResult structure as defined by
 * the EJB 3.0 Persistence specification.  This class is a subcomponent of the 
 * EntityResult.
 * 
 * @see EntityResult
 * @author Gordon Yorke
 * @since TopLink Java Essentials
 */

public class FieldResult implements Serializable{
    /** Stores the name of the bean attribute  */
    protected String attributeName;
    /** Stores passed in field name split on the '.' character */
    protected String[] multipleFieldIdentifiers;
    /** FieldResult now can contain multiple FieldResults in a collection if an attribute has multiple fields */
    java.util.Vector<FieldResult> fieldResults;
    
    /** Stores the Columns name from the result set that contains the attribute value */
    protected DatabaseField column;
    
    public FieldResult(String attributeName, String columnName){
        if (attributeName == null || columnName == null ){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_values_for_field_result"));
        }
        this.column = new DatabaseField(columnName);
        multipleFieldIdentifiers = attributeName.split("\\.",0);
        this.attributeName = multipleFieldIdentifiers[0];
    }
    
    public FieldResult(String attributeName, DatabaseField column){
        if (attributeName == null || column == null || column.getName() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_values_for_field_result"));
        }
        this.column = column;
        multipleFieldIdentifiers = attributeName.split("\\.",0);
        this.attributeName = multipleFieldIdentifiers[0];
    }
    
    public String getAttributeName(){
        return this.attributeName;
    }
    
    public DatabaseField getColumn(){
        return this.column;
    }
    
    /**
     * INTERNAL:
     * This method is a convenience method for extracting values from Results
     */
    public Object getValueFromRecord(DatabaseRecord record){
        return record.get(this.column);
    }
    
    /**
     * INTERNAL:
     */
    public java.util.Vector<FieldResult> getFieldResults(){
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
          fieldResults = new java.util.Vector<FieldResult>();
          fieldResults.add(this);
      }
      fieldResults.add(newFieldResult);
    }
    
}
