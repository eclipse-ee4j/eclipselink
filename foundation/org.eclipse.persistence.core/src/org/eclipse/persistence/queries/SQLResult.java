/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.queries;

import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>:
 * An abstract superclass that represents the commonalities between the main
 * result types of the SQLResultSetMapping
 * 
 * @see EntityResult
 * @see ColumnResult
 * @author Gordon Yorke
 * @since TopLink Java Essentials
 */

public abstract class SQLResult {

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this SQLResult to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){};


    public boolean isColumnResult(){
        return false;
    }
    
    public boolean isEntityResult(){
        return false;
    }
    
    /**
     * INTERNAL:
     * This method is a convenience method for extracting values from Results
     */
    public abstract Object getValueFromRecord(DatabaseRecord record, ResultSetMappingQuery query);
    
}
