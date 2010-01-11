/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

package org.eclipse.persistence.tools.dbws.jdbc;

// Javase imports

// Java extension imports
public class DbStoredFunction extends DbStoredProcedure {

    protected DbStoredArgument returnArg;

    public DbStoredFunction(String name) {
        super(name);
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    public DbStoredArgument getReturnArg() {
        return returnArg;
    }
    public void setReturnArg(DbStoredArgument returnArg) {
        this.returnArg = returnArg;
    }

    @Override
    public String getStoredType() {
        return "FUNCTION";
    }
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(" RETURNS ");
        sb.append(returnArg);
        sb.append("\n");
        return sb.toString();
    }
}
