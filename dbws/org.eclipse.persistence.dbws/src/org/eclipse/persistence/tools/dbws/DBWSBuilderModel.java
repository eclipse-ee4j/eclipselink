/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

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
