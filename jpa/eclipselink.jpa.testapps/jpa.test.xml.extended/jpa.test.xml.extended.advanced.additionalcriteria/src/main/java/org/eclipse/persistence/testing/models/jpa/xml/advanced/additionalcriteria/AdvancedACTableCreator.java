/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     01/28/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 1)
//     02/25/2009-2.0 Guy Pelletier
//       - 265359: JPA 2.0 Element Collections - Metadata processing portions
//     11/06/2009-2.0 Guy Pelletier
//       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
//     03/08/2010-2.1 Guy Pelletier
//       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
//     03/29/2010-2.1 Guy Pelletier
//       - 267217: Add Named Access Type to EclipseLink-ORM
//     06/16/2010-2.2 Guy Pelletier
//       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
package org.eclipse.persistence.testing.models.jpa.xml.advanced.additionalcriteria;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class AdvancedACTableCreator extends TogglingFastTableCreator {

    public AdvancedACTableCreator() {
        setName("EJB3EmployeeProjectAC");

        addTableDefinition(buildSTUDENTTable());
        addTableDefinition(buildSCHOOLTable());
        addTableDefinition(buildBOLTTable());
        addTableDefinition(buildNUTTable());
    }

    public TableDefinition buildSCHOOLTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_XML_AC_SCHOOL");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
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

        return table;
    }

    public TableDefinition buildSTUDENTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_XML_AC_STUDENT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
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

        FieldDefinition fieldSCHOOL_ID = new FieldDefinition();
        fieldSCHOOL_ID.setName("SCHOOL_ID");
        fieldSCHOOL_ID.setTypeName("NUMERIC");
        fieldSCHOOL_ID.setSize(15);
        fieldSCHOOL_ID.setIsPrimaryKey(false);
        fieldSCHOOL_ID.setIsIdentity(false);
        fieldSCHOOL_ID.setUnique(false);
        fieldSCHOOL_ID.setShouldAllowNull(false);
        fieldSCHOOL_ID.setForeignKeyFieldName("JPA_XML_AC_SCHOOL.ID");
        table.addField(fieldSCHOOL_ID);

        return table;
    }

    public TableDefinition buildBOLTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_XML_AC_BOLT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);

        FieldDefinition fieldNUT_ID = new FieldDefinition();
        fieldNUT_ID.setName("NUT_ID");
        fieldNUT_ID.setTypeName("NUMERIC");
        fieldNUT_ID.setSize(15);
        fieldNUT_ID.setIsPrimaryKey(false);
        fieldNUT_ID.setIsIdentity(false);
        fieldNUT_ID.setUnique(false);
        fieldNUT_ID.setShouldAllowNull(false);
        fieldNUT_ID.setForeignKeyFieldName("JPA_XML_AC_NUT.ID");
        table.addField(fieldNUT_ID);

        return table;
    }

    public TableDefinition buildNUTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_XML_AC_NUT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);

        FieldDefinition fieldCOLOR = new FieldDefinition();
        fieldCOLOR.setName("COLOR");
        fieldCOLOR.setTypeName("VARCHAR2");
        fieldCOLOR.setSize(60);
        fieldCOLOR.setSubSize(0);
        fieldCOLOR.setIsPrimaryKey(false);
        fieldCOLOR.setIsIdentity(false);
        fieldCOLOR.setUnique(false);
        fieldCOLOR.setShouldAllowNull(true);
        table.addField(fieldCOLOR);

        FieldDefinition fieldSIZE = new FieldDefinition();
        fieldSIZE.setName("B_SIZE");
        fieldSIZE.setTypeName("NUMERIC");
        fieldSIZE.setSize(15);
        fieldSIZE.setShouldAllowNull(true);
        fieldSIZE.setIsPrimaryKey(false);
        fieldSIZE.setUnique(false);
        fieldSIZE.setIsIdentity(false);
        table.addField(fieldSIZE);

        return table;
    }
}
