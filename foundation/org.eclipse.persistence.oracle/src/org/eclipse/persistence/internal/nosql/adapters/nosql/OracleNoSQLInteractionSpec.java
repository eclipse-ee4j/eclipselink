/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.nosql.adapters.nosql;

import javax.resource.cci.*;

/**
 * Interaction spec for Oracle NoSQL JCA adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLInteractionSpec implements InteractionSpec {    
    protected OracleNoSQLOperation operation;
    protected String key;

    /**
     * Default constructor.
     */
    public OracleNoSQLInteractionSpec() {
    }

    public OracleNoSQLOperation getOperation() {
        return operation;
    }

    public void setOperation(OracleNoSQLOperation operation) {
        this.operation = operation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String toString() {
        return getClass().getName() + "(" + getOperation() + ")";
    }
}
