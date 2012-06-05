/*******************************************************************************
 * Copyright (c) 2008 - 2012 Oracle Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.1
 *     Linda DeMichiel - Java Persistence 2.0
 *
 ******************************************************************************/ 
package javax.persistence; 

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies the mode of a parameter of a stored procedure query.
 *
 * @see StoredProcedureQuery
 * @see StoredProcedureParameter
 *
 * @since Java Persistence 2.1
 */
public enum ParameterMode {

    /**
     *  Stored procedure input parameter
     */
    IN,

    /**
     *  Stored procedure input/output parameter
     */
    INOUT,

    /**
     *  Stored procedure output parameter
     */
    OUT,

    /**
     *  Stored procedure reference cursor parameter.   Some databases use
     *  REF_CURSOR parameters to return result sets from stored procedures.
     */
    REF_CURSOR,

}
