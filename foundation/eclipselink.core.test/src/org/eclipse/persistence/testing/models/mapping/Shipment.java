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
package org.eclipse.persistence.testing.models.mapping;

import java.util.*;
import java.sql.Timestamp;
import java.io.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.tools.schemaframework.*;

public class Shipment implements Serializable, Cloneable {
    public static Timestamp lastCreationTimestamp;
    public Timestamp creationTimestamp;
    public int creationTimestampMillis;
    public String quantityShipped;
    public String shipMode;
    public Vector employees;

    public Shipment() {
        this.employees = new Vector();
    }

    /**
     * NOTE: this should not be needed.  This is only
     * here because there seems to be a VisualAge bug
     * that can't reflectively call superclass methods.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
        }
        return null;
    }

    public static Shipment example1() {
        Shipment example = new Shipment();

        example.quantityShipped = "1 ton";
        example.shipMode = "Air";

        return example;
    }

    public static Shipment example2() {
        Shipment example = new Shipment();

        example.quantityShipped = "2 ton";
        example.shipMode = "Air";

        return example;
    }

    public static Shipment example3() {
        Shipment example = new Shipment();

        example.quantityShipped = "3 ton";
        example.shipMode = "Ship";

        return example;
    }

    public static Shipment example4() {
        Shipment example = new Shipment();

        example.quantityShipped = "4 ton";
        example.shipMode = "Ship";

        return example;
    }

    public static Shipment example5() {
        Shipment example = new Shipment();

        example.quantityShipped = "6 ton";
        example.shipMode = "Plane";

        return example;
    }

    public static Shipment example6() {
        Shipment example = new Shipment();

        example.quantityShipped = "2 kg";
        example.shipMode = "Pony Express";

        return example;
    }

    public void prepareForInsert(DescriptorEvent event) {
        Session session = event.getSession();
        if (this.creationTimestamp != null) {
            throw new RuntimeException("Insert event fired twice");
        }
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
        while (this.creationTimestamp.equals(lastCreationTimestamp)) {
            try {
                // Sleep to ensure milliseconds is ok.
                Thread.sleep(1);
                // Symfoware does not support nanos and millis.
                // Need to wait at least a second to prevent unique key
                // constraint violation at next insert
                if (session.getLogin().getPlatform().isSymfoware()) Thread.sleep(1000);
            } catch (InterruptedException exception) {
            }
            this.creationTimestamp = new Timestamp(System.currentTimeMillis());
        }
        lastCreationTimestamp = (Timestamp)this.creationTimestamp.clone();
        if (session.getLogin().getPlatform().isDB2() || session.getLogin().getPlatform().isAccess() || session.getLogin().getPlatform().isSQLServer() || session.getLogin().getPlatform().isOracle() || session.getLogin().getPlatform().isSybase() || session.getLogin().getPlatform().isSQLAnywhere() || session.getLogin().getPlatform().isMySQL() || session.getLogin().getPlatform().isSymfoware()) {
            // Oracle does not support millis, Sybase stores them only within 1-2 millis...
            //MySQL does not support millis as of 5.0
            this.creationTimestampMillis = creationTimestamp.getNanos();
            this.creationTimestamp.setNanos(0);
        }
    }

    public void removeEmployee(Employee employee) {
        employees.removeElement(employee);
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_SHIP");

        definition.addPrimaryKeyField("SP_TS", Timestamp.class);
        definition.addPrimaryKeyField("SP_TSMIL", Integer.class);
        definition.addField("QUANTITY", String.class, 20);
        definition.addField("SHP_MODE", String.class, 50);

        return definition;
    }

    public String toString() {
        return "Shipment(" + quantityShipped + ")";
    }
}
