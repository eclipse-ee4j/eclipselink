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
package org.eclipse.persistence.testing.models.jpa.customfeatures;

import org.eclipse.persistence.tools.schemaframework.*;

public class EmployeeTableCreator extends TableCreator {
    public EmployeeTableCreator() {
        setName("CustomFeatureEmployeeProject");

        addTableDefinition(buildEmployeeTable());
    }

    public static TableDefinition buildEmployeeTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CUSTOM_FEATURE_EMPLOYEE");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(60);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);

        FieldDefinition fieldNCH = new FieldDefinition();
        fieldNCH.setName("NCHARTYPE");
        fieldNCH.setTypeName("NCHAR");
        fieldNCH.setSize(1);
        fieldNCH.setSubSize(0);
        fieldNCH.setIsPrimaryKey(false);
        fieldNCH.setIsIdentity(false);
        fieldNCH.setUnique(false);
        fieldNCH.setShouldAllowNull(true);
        table.addField(fieldNCH);

        FieldDefinition fieldXMLDATA = new FieldDefinition();
        fieldXMLDATA.setName("XMLData");
        fieldXMLDATA.setTypeName("XMLTYPE");
        fieldXMLDATA.setIsPrimaryKey(false);
        fieldXMLDATA.setIsIdentity(false);
        fieldXMLDATA.setUnique(false);
        fieldXMLDATA.setShouldAllowNull(true);
        table.addField(fieldXMLDATA);

        FieldDefinition fieldXMLDOM = new FieldDefinition();
        fieldXMLDOM.setName("XMLDom");
        fieldXMLDOM.setTypeName("XMLTYPE");
        fieldXMLDOM.setIsPrimaryKey(false);
        fieldXMLDOM.setIsIdentity(false);
        fieldXMLDOM.setUnique(false);
        fieldXMLDOM.setShouldAllowNull(true);
        table.addField(fieldXMLDOM);

        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        fieldVERSION.setShouldAllowNull(true);
        fieldVERSION.setIsPrimaryKey(false);
        fieldVERSION.setUnique(false);
        fieldVERSION.setIsIdentity(false);
        table.addField(fieldVERSION);

        return table;
    }


}
