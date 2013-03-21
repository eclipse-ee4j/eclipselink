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

import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>:
 * An abstract superclass that represents the commonalities between the main
 * result types of the SQLResultSetMapping
 * 
 * @see EntityResult
 * @see ColumnResult
 * @see ConstructorResult
 * @author Gordon Yorke
 * @since TopLink Java Essentials
 */
public abstract class SQLResult implements Serializable{
    protected SQLResultSetMapping sqlResultSetMapping;
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this SQLResult to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){};

    /**
     * INTERNAL:
     * Return the SQLResultSetMapping this SQLResult is part of.
     */
    public SQLResultSetMapping getSQLResultMapping() {
        return sqlResultSetMapping;
    }
    
    /**
     * Return true if this is a column result.
     */
    public boolean isColumnResult(){
        return false;
    }
    
    /**
     * Return true if this is a constructor result.
     */
    public boolean isConstructorResult(){
        return false;
    }
    
    /**
     * Return true if this is an entity result.
     */
    public boolean isEntityResult(){
        return false;
    }
    
    /**
     * INTERNAL:
     * Set the SQLResultSetMapping this SQLResult is part of.
     */
    public void setSQLResultMapping(SQLResultSetMapping mapping) {
        sqlResultSetMapping = mapping;
    }
    
    /**
     * INTERNAL:
     * This method is a convenience method for extracting values from Results
     */
    public abstract Object getValueFromRecord(DatabaseRecord record, ResultSetMappingQuery query);
    
}
