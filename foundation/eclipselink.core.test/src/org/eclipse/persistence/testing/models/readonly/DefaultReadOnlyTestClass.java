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
