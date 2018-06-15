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

import java.util.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * A Hollywood Agent makes movie deals for many actors, while an actor has only
 * a single agent at one time.
 */
public class HollywoodAgent {
    public Number id;
    public String name;
    public int numberOfConnections;

    public HollywoodAgent() {
        super();
    }

    public static HollywoodAgent donKing() {
        HollywoodAgent donKing = new HollywoodAgent();
        donKing.setName("Don King");
        donKing.numberOfConnections = 200;
        return donKing;
    }

    public static HollywoodAgent waltDisney() {
        HollywoodAgent waltDisney = new HollywoodAgent();
        waltDisney.setName("Walt Disney");
        waltDisney.numberOfConnections = 2000;
        return waltDisney;
    }

    public static HollywoodAgent markyMark() {
        HollywoodAgent markyMark = new HollywoodAgent();
        markyMark.setName("Marky Mark");
        markyMark.numberOfConnections = 2;
        return markyMark;
    }

    /**
     * Return a Vector of Hollywood agents used to populate the db.
     */
    public static Vector hollywoodAgents() {
        Vector agents = new Vector();
        agents.addElement(donKing());
        agents.addElement(waltDisney());
        agents.addElement(markyMark());

        return agents;
    }

    // HollywoodAgent descriptor
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.testing.models.readonly.HollywoodAgent.class);
        descriptor.setTableName("HOLLYWOODAGENT");
        descriptor.addPrimaryKeyFieldName("HOLLYWOODAGENT_ID");
        descriptor.setSequenceNumberName("HOLLYWOODAGENT_SEQ");
        descriptor.setSequenceNumberFieldName("HOLLYWOODAGENT_ID");

        descriptor.addDirectMapping("id", "HOLLYWOODAGENT_ID");
        descriptor.addDirectMapping("name", "NAME");
        descriptor.addDirectMapping("numberOfConnections", "CONNECTIONS");

        return descriptor;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        HollywoodAgent other = (HollywoodAgent)obj;
        return (getName().equals(other.getName()));
    }

    public String getName() {
        return name;
    }

    public void setName(String newValue) {
        this.name = newValue;
    }

    // Country table definition
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("HOLLYWOODAGENT");

        definition.addIdentityField("HOLLYWOODAGENT_ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 50);
        definition.addField("CONNECTIONS", "INT");

        return definition;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + "(" + getName() + ") ";
    }
}
