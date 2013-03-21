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
package org.eclipse.persistence.queries;

import org.eclipse.persistence.mappings.converters.Converter;

/**
 * <p><b>Purpose</b>:
 * Concrete class to perform a direct read.
 * <p>
 * <p><b>Responsibilities</b>:
 * Used in conjunction with DirectCollectionMapping.
 * This can be used to read a single column of data (i.e. one field).
 * A container (implementing Collection) of the data values is returned.
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class DirectReadQuery extends DataReadQuery {

    /** Allows user defined conversion between the result value and the database value. */
    protected Converter valueConverter;

    /**
     * PUBLIC:
     * Initialize the state of the query.
     */
    public DirectReadQuery() {
        super();
        this.resultType = ATTRIBUTE;
    }

    /**
     * PUBLIC:
     * Initialize the query to use the specified SQL string.
     * Warning: Allowing an unverified SQL string to be passed into this 
	 * method makes your application vulnerable to SQL injection attacks. 
     */
    public DirectReadQuery(String sqlString) {
        super(sqlString);
        this.resultType = ATTRIBUTE;
    }

    /**
     * PUBLIC:
     * Initialize the query to use the specified call.
     */
    public DirectReadQuery(Call call) {
        super(call);
        this.resultType = ATTRIBUTE;
    }

    /**
     * PUBLIC:
     * Return the converter on the query.
     * A converter can be used to convert between the result value and database value.
     */
    public Converter getValueConverter() {
        return valueConverter;
    }

    /**
     * PUBLIC:
     * Set the converter on the query.
     * A converter can be used to convert between the result value and database value.
     */
    public void setValueConverter(Converter valueConverter) {
        this.valueConverter = valueConverter;
    }
    
    /**
     * PUBLIC:
     * Return if this is a direct read query.
     */
    public boolean isDirectReadQuery() {
        return true;
    }
}
