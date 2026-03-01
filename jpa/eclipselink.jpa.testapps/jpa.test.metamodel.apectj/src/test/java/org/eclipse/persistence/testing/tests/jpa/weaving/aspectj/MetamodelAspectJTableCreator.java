/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.weaving.aspectj;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class MetamodelAspectJTableCreator extends TableCreator {

    public MetamodelAspectJTableCreator() {
        setName("JPAOrphanRemovalModelTableCreator");
        addTableDefinition(buildVEHICLETable());
    }

        public static TableDefinition buildVEHICLETable() {
        // CREATE TABLE ASPECTJ_ITEM (ID NUMBER(10) NOT NULL, NAME VARCHAR2(255) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("ASPECTJ_ITEM");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(10);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        fieldID.setUnique(false);
        table.addField(fieldID);

        FieldDefinition fieldMODEL = new FieldDefinition();
        fieldMODEL.setName("NAME");
        fieldMODEL.setTypeName("VARCHAR");
        fieldMODEL.setSize(60);
        fieldMODEL.setSubSize(0);
        fieldMODEL.setIsPrimaryKey(false);
        fieldMODEL.setIsIdentity(false);
        fieldMODEL.setShouldAllowNull(true);
        fieldMODEL.setUnique(false);
        table.addField(fieldMODEL);

        return table;
    }

}
