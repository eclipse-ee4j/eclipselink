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
 *     06/20/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * <p><b>Purpose</b>:
 * Concrete class to represent the SQLResultSetMapping structure as defined by
 * the EJB 3.0 Persistence specification.  This class is used by the 
 * ResultSetMappingQuery and is a component of the EclipsepLink Project
 * 
 * @see org.eclipse.persistence.sessions.Project
 * @author Gordon Yorke
 * @since TopLink Java Essentials
 */
public class SQLResultSetMapping implements Serializable{
    /** Stores the name of this SQLResultSetMapping.  This name is unique within
     * The project.
     */
    protected String name;
    
    /** Stores the list of SQLResult in the order they were
     * added to the Mapping
     */
    protected List<SQLResult> results;
    
    /**
     * Defaulting constructor. Will set the name to the result class name
     * and add an EntityResult for the result class.
     */
    public SQLResultSetMapping(Class resultClass) {
        this.name = resultClass.getName();
        this.addResult(new EntityResult(resultClass));
    }
    
    public SQLResultSetMapping(String name){
        this.name = name;
        if (this.name == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_value_in_sqlresultsetmapping"));
        }
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this SQLResultSetMapping to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        Iterator<SQLResult> iterator = getResults().iterator();
        while (iterator.hasNext()){
            iterator.next().convertClassNamesToClasses(classLoader);
        }
    };   

    public String getName(){
        return this.name;
    }
    
    public void addResult(SQLResult result){
        if (result == null){
            return;
        }
        getResults().add(result);
        result.setSQLResultMapping(this);
    }
    
    /**
     * Accessor for the internally stored list of ColumnResult.  Calling this
     * method will result in a collection being created to store the ColumnResult
     */
    public List<SQLResult> getResults(){
        if (this.results == null){
            this.results = new ArrayList();
        }
        return this.results;
    }
}
