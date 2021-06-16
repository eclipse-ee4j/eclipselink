/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     08/18/2014-2.5 Jody Grassel (IBM Corporation)
//       - 440802: xml-mapping-metadata-complete does not exclude @Entity annotated entities

package org.eclipse.persistence.testing.models.jpa.xml.xmlmetadatacomplete;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class XLMMappingMetadataCompleteTableManager extends TableCreator {
    public XLMMappingMetadataCompleteTableManager() {
        setName("XMLMappingMetadataComplete");
        addTableDefinition(buildXMLOnlyEntityTable());
        addTableDefinition(buildAnoOnlyEntityTable());
    }

    public static TableDefinition buildXMLOnlyEntityTable() {
        TableDefinition table = new TableDefinition();

        table.setName("XMLONLYENTITY");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);

        FieldDefinition fieldSTRDATA = new FieldDefinition();
        fieldSTRDATA.setName("STRDATA");
        fieldSTRDATA.setTypeName("VARCHAR2");
        fieldSTRDATA.setSize(80);
        fieldSTRDATA.setSubSize(0);
        fieldSTRDATA.setIsPrimaryKey(false);
        fieldSTRDATA.setIsIdentity(false);
        fieldSTRDATA.setUnique(false);
        fieldSTRDATA.setShouldAllowNull(true);
        table.addField(fieldSTRDATA);

        return table;
    }

    public static TableDefinition buildAnoOnlyEntityTable() {
        TableDefinition table = new TableDefinition();

        table.setName("ANOONLYENT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);

        FieldDefinition fieldSTRDATA = new FieldDefinition();
        fieldSTRDATA.setName("STRDATA");
        fieldSTRDATA.setTypeName("VARCHAR2");
        fieldSTRDATA.setSize(80);
        fieldSTRDATA.setSubSize(0);
        fieldSTRDATA.setIsPrimaryKey(false);
        fieldSTRDATA.setIsIdentity(false);
        fieldSTRDATA.setUnique(false);
        fieldSTRDATA.setShouldAllowNull(true);
        table.addField(fieldSTRDATA);

        return table;
    }
}
