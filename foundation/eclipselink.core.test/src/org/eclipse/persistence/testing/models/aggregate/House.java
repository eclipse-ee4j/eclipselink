/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.aggregate;

import java.util.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class House {
    private java.lang.String Location;
    private java.lang.String descriptions;
    private java.sql.Timestamp version;
    private java.lang.String type;
    private Vector sellingPoints;
    private Oid insuranceId;

    public House() {
        super();
        sellingPoints = new Vector();
        insuranceId = new Oid();
        insuranceId.setOid(new Integer(0));
    }

    public java.lang.String getDescriptions() {
        return descriptions;
    }

    public java.lang.String getLocation() {
        return Location;
    }

    public Vector getSellingPoints() {
        return sellingPoints;
    }

    /**
     * For bug 2863721 Add AggregateObject to AggregateCollection.
     * Represents an insurance policy number associated with the house,
     * which is not part of the primary key.
     */
    public Oid getInsuranceId() {
        return insuranceId;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/14/2000 10:51:13 AM)
     * @return java.lang.String
     */
    public java.lang.String getType() {
        return type;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/14/2000 9:19:37 AM)
     * @return java.sql.Timestamp
     */
    public java.sql.Timestamp getVersion() {
        return version;
    }

    public void setDescriptions(java.lang.String newDescriptions) {
        descriptions = newDescriptions;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/11/2000 11:36:25 AM)
     * @param newLocation java.lang.String
     */
    public void setLocation(java.lang.String newLocation) {
        Location = newLocation;
    }

    public void setSellingPoints(Vector sellingPoints) {
        this.sellingPoints = sellingPoints;
    }

    /**
     * For bug 2863721 Add AggregateObject to AggregateCollection.
     * Represents an insurance policy number associated with the house,
     * which is not part of the primary key.
     */
    public void setInsuranceId(Oid insuranceId) {
        this.insuranceId = insuranceId;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/14/2000 10:51:13 AM)
     * @param newType java.lang.String
     */
    public void setType(java.lang.String newType) {
        type = newType;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/14/2000 9:19:37 AM)
     * @param newVersion java.sql.Timestamp
     */
    public void setVersion(java.sql.Timestamp newVersion) {
        version = newVersion;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("HOUSE");

        definition.addPrimaryKeyField("AGENT_ID", java.math.BigDecimal.class, 15);
        definition.addPrimaryKeyField("LOCATION", String.class, 200);
        definition.addField("DESCRIPTIONS", String.class, 200);
        definition.addField("INS_ID", java.math.BigDecimal.class, 15);
        definition.addField("VERSION", java.sql.Timestamp.class, 200);
        definition.addField("TYPE", String.class, 4);
        return definition;
    }
}
