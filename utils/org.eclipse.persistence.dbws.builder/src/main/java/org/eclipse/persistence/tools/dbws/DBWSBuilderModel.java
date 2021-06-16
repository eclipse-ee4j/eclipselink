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
//     Mike Norman - May 01 2008, creating DBWS tools package

package org.eclipse.persistence.tools.dbws;

// javase imports
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class DBWSBuilderModel {

    public Map<String, String> properties = new LinkedHashMap<String, String>();
    public ArrayList<OperationModel> operations = new ArrayList<OperationModel>();

    public DBWSBuilderModel() {
        super();
    }

    public Map<String, String> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, String> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    public ArrayList<OperationModel> getOperations() {
        return operations;
    }
    public void setOperations(ArrayList<OperationModel> operations) {
        this.operations = operations;
    }
}
