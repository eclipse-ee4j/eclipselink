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
package org.eclipse.persistence.testing.models.readonly;

import java.io.Serializable;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 *  DefaultReadOnlyTestClass is designed to test the our default read only
 *  Setting on the project
 *  @author Tom Ware
 **/
public class DefaultReadOnlyTestClass implements Serializable {
    private int data;

    public int getData() {
        return this.data;
    }

    public void setData(int v) {
        this.data = v;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();
        definition.setName("DEFAULT_READ_ONLY");
        definition.addPrimaryKeyField("DATA", java.lang.Integer.class);
        return definition;
    }
}
