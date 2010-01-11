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
package org.eclipse.persistence.testing.models.legacy;

import java.math.*;
import java.sql.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class LegacyTables extends TableCreator {
    public static String computerDescriptionFieldName;

    public LegacyTables() {
        super();
        addTableDefinition(addressTableDefinition());
        addTableDefinition(computerTableDefinition());
        addTableDefinition(insuredTableDefinition());
        addTableDefinition(orderTableDefinition());
        addTableDefinition(employeeTableDefinition());
        addTableDefinition(shipmentTableDefinition());
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition addressTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("LEG_ADD");

        definition.addPrimaryKeyField("FIRST_NM", String.class, 20);
        definition.addPrimaryKeyField("LNAME", String.class, 20);
        definition.addField("ADDR", String.class, 200);

        definition.addForeignKeyConstraint("LEG_ADD_LEG_EMP", "FIRST_NM,LNAME", "FNAME,LNAME", "LEG_EMP");

        return definition;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition computerTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("LEG_COM");

        definition.addPrimaryKeyField("CREATE_TS", Timestamp.class);
        definition.addPrimaryKeyField("CREATE_TSM", Integer.class);
        if (computerDescriptionFieldName == null) {
            definition.addField("DESCRIP", String.class, 30);
        } else {
            definition.addField(computerDescriptionFieldName, String.class, 30);
        }

        definition.addField("EMP_FNAME", String.class, 20);
        definition.addField("EMP_LNAME", String.class, 20);

        definition.addForeignKeyConstraint("LEG_COM_LEG_EMP", "EMP_FNAME,EMP_LNAME", "FNAME,LNAME", "LEG_EMP");

        return definition;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition employeeTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("LEG_EMP");

        definition.addPrimaryKeyField("FNAME", String.class, 20);
        definition.addPrimaryKeyField("LNAME", String.class, 20);

        return definition;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition insuredTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("LEG_ISHP");

        definition.addPrimaryKeyField("F_NAME", String.class, 20);
        definition.addPrimaryKeyField("L_NAME", String.class, 20);
        definition.addPrimaryKeyField("SHIP_NO", BigDecimal.class);
        definition.addField("INS_AMT", Double.class, 10);

        definition.addForeignKeyConstraint("LEG_ISHP_LEG_SHP", "F_NAME,L_NAME,SHIP_NO", "FNAME,LNAME,SHIP_NO", "LEG_SHP");

        return definition;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition orderTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("LEG_ORD");

        definition.addPrimaryKeyField("FNAME", String.class, 20);
        definition.addPrimaryKeyField("LNAME", String.class, 20);
        definition.addPrimaryKeyField("SHIP_NO", BigDecimal.class);
        definition.addPrimaryKeyField("ORDER_NO", BigDecimal.class);
        definition.addField("DESCRIP", String.class, 50);

        definition.addForeignKeyConstraint("LEG_ORD_LEG_SHP", "FNAME,LNAME,SHIP_NO", "FNAME,LNAME,SHIP_NO", "LEG_SHP");

        return definition;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition shipmentTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("LEG_SHP");

        definition.addPrimaryKeyField("FNAME", String.class, 20);
        definition.addPrimaryKeyField("LNAME", String.class, 20);
        definition.addPrimaryKeyField("SHIP_NO", BigDecimal.class);
        definition.addField("QUANTITY", String.class, 10);
        definition.addField("SHP_MODE", String.class, 15);
        definition.addField("TYPE", String.class, 10);
        definition.addField("INS_AMT", Double.class, 10);

        definition.addForeignKeyConstraint("LEG_SHP_LEG_EMP", "FNAME,LNAME", "FNAME,LNAME", "LEG_EMP");

        return definition;
    }
}
