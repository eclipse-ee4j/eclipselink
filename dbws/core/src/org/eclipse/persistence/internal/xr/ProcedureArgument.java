/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports

// TopLink imports

/**
 * <p><b>INTERNAL</b>: 
 * 
 * @author Merrick Schincarol - merrick.schincariol@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public class ProcedureArgument {
    
    protected String name;
    protected String parameterName;

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String argumentName) {
        this.parameterName = argumentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
