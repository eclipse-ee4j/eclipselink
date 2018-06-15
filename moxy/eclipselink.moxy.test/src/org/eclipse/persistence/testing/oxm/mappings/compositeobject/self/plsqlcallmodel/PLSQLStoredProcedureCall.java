/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

import java.util.ArrayList;
import java.util.List;

import static org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel.PLSQLargument.IN;

public class PLSQLStoredProcedureCall {

    protected String procedureName;
    protected List<PLSQLargument> arguments =  new ArrayList<PLSQLargument>();
    protected int originalIndex = 0;

    public PLSQLStoredProcedureCall() {
    }

    public String getProcedureName() {
        return procedureName;
    }
    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public List<PLSQLargument> getArguments() {
        return arguments;
    }
    public void setArguments(List<PLSQLargument> arguments) {
        this.arguments = arguments;
    }

    public void addNamedArgument(String argumentName, DatabaseType databaseType) {
        arguments.add(new PLSQLargument(argumentName, databaseType));
    }

    public void addNamedArgument(String argumentName, DatabaseType databaseType, int length) {
        arguments.add(new PLSQLargument(argumentName, -1, IN, databaseType, length));
    }

    public String toString() {
        return "PLSQLStoredProcedureCall(procedureName="+procedureName+", arguments="+arguments;
    }

    public boolean equals(Object obj) {
        PLSQLStoredProcedureCall pCall = null;
        try {
            pCall = (PLSQLStoredProcedureCall) obj;
        } catch (ClassCastException ccex) {
            return false;
        }
        if (pCall.getArguments() == null) {
            return this.getArguments() == null;
        }
        if (this.getArguments() == null) {
            return false;
        }
        if (!pCall.procedureName.equals(this.procedureName) || pCall.getArguments().size() != this.getArguments().size()) {
            return false;
        }
        for (int i=0; i<pCall.getArguments().size(); i++) {
            if (!this.getArguments().contains(pCall.getArguments().get(i))) {
                return false;
            }
        }
        return true;
    }
}
