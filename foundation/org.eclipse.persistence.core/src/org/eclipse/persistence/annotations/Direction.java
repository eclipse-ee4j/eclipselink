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
package org.eclipse.persistence.annotations;

/** 
 * An enum that is used within the StoredProcedureParameter annotation. It is 
 * used to specify the direction of the stored procedure parameters of a named 
 * stored procedure query.
 * 
 * @see org.eclipse.persistence.annotations.StoredProcedureParameter
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0 
 */ 
public enum Direction {
    /**
     * Input parameter
     */
    IN,

    /**
     * Output parameter
     */
    OUT,

    /**
     * Input and output parameter
     */
    IN_OUT,

    /**
     * Output cursor
     */
    OUT_CURSOR
}
