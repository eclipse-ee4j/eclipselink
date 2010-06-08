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
 
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

public class DatabaseTypeWrapper {

    protected DatabaseType wrappedDatabaseType;

    public DatabaseTypeWrapper() {
    }

    public DatabaseTypeWrapper(DatabaseType wrappedDatabaseType) {
        this.wrappedDatabaseType = wrappedDatabaseType;
    }

    public DatabaseType getWrappedType() {
        return wrappedDatabaseType;
    }
    public void setWrappedDatabaseType(DatabaseType wrappedDatabaseType) {
        this.wrappedDatabaseType = wrappedDatabaseType;
    }
    
    public boolean equals(Object obj) {
        DatabaseTypeWrapper dWrap = null;
        try {
            dWrap = (DatabaseTypeWrapper) obj;
        } catch (ClassCastException ccex) {
            return false;
        }
        DatabaseType dType = dWrap.getWrappedType();
        if (dType == null) {
            return this.getWrappedType() == null;
        }
        if (this.getWrappedType() == null) {
            return false;
        }
        if (dType.isComplexDatabaseType()) {
            if (!this.getWrappedType().isComplexDatabaseType()) {
                return false;
            }
        } else {
            if (this.getWrappedType().isComplexDatabaseType()) {
                return false;
            }
        }
        return dType.equals(this.getWrappedType());
    }
}
