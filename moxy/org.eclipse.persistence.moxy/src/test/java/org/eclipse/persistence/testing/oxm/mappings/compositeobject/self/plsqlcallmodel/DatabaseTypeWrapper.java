/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

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
