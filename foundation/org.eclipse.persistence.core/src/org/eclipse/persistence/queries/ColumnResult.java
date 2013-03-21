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
 *     08/24/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>:
 * Concrete class to represent the ColumnResult structure as defined by
 * the EJB 3.0 Persistence specification.  This class is a subcomponent of the 
 * EntityResult
 * 
 * @see EntityResult
 * @author Gordon Yorke
 * @since TopLink Java Essentials
 */

public class ColumnResult extends SQLResult{
    
    /** Stores the Columns name from the result set */
    protected DatabaseField column;
    
    public ColumnResult(DatabaseField column){
        if (column == null || column.getName() == null ){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_value_for_column_result"));
        }
        this.column = column;
    }
    
    public ColumnResult(String column){
        if (column == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_value_for_column_result"));
        }
        this.column = new DatabaseField(column);
    }
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this query to actual 
     * class-based settings. This method is used when converting a project that 
     * has been built with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);
        
        column.convertClassNamesToClasses(classLoader);
    }
    
    public DatabaseField getColumn(){
        return this.column;
    }
    
    /**
     * INTERNAL:
     * This method is a convenience method for extracting values from Results
     */
    public Object getValueFromRecord(DatabaseRecord record, ResultSetMappingQuery query){
        return record.get(this.column);
    }
    
    public boolean isColumnResult(){
        return true;
    }
    
}
