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
package org.eclipse.persistence.testing.models.readonly;

import java.util.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * A Hollywood Charity champions good causes, and actors can be spokespersons
 * for multiple charities.
 */
public class Charity {
    public Number id;
    public String name;
    public int donationsRaised;

    public Charity() {
        super();
    }

    public static Charity greenPeace() {
        Charity example = new Charity();
        example.setName("Green Peace");
        example.donationsRaised = 2000000;
        return example;
    }

    public static Charity redCross() {
        Charity example = new Charity();
        example.setName("Red Cross");
        example.donationsRaised = 20000000;
        return example;
    }

    public static Charity unitedWay() {
        Charity example = new Charity();
        example.setName("United Way");
        example.donationsRaised = 200000000;
        return example;
    }

    public static Vector charities() {
        Vector charities = new Vector();
        charities.addElement(greenPeace());
        charities.addElement(redCross());
        charities.addElement(unitedWay());

        return charities;
    }

    // Charity descriptor
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.testing.models.readonly.Charity.class);
        descriptor.setTableName("CHARITY");
        descriptor.addPrimaryKeyFieldName("CHARITY_ID");
        descriptor.setSequenceNumberName("CHARITY_SEQ");
        descriptor.setSequenceNumberFieldName("CHARITY_ID");

        descriptor.addDirectMapping("id", "CHARITY_ID");
        descriptor.addDirectMapping("name", "NAME");
        descriptor.addDirectMapping("donationsRaised", "DONATIONS_RAISED");

        return descriptor;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Charity other = (Charity)obj;
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

        definition.setName("CHARITY");

        definition.addIdentityField("CHARITY_ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 50);
        definition.addField("DONATIONS_RAISED", "INT");

        return definition;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + "(" + getName() + ") ";
    }
}
