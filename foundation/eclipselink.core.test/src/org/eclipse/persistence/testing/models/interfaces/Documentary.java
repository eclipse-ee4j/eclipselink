/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.interfaces;

import java.util.Vector;
import java.math.BigDecimal;

/**
 * A Documentary is a program.
 */
public class Documentary implements ProgramInt {
    public int id;
    public String name;
    public String description;
    public Number duration = new BigDecimal(0);
    public Vector actors; //type Actor

    public Documentary() {
        actors = new Vector();
    }

    public void addActor(Actor actor) {
        actor.program = this;
        actors.addElement(actor);
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition documentaryTable() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = 
            new org.eclipse.persistence.tools.schemaframework.TableDefinition();


        // SECTION: TABLE
        tabledefinition.setName("DOCUMENTARY");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("ID");
        field.setType(java.math.BigDecimal.class);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("NAME");
        field1.setType(String.class);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field2 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field2.setName("DESCRIPTION");
        field2.setType(Character[].class);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field3 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field3.setName("DURATION");
        field3.setType(Double.class);
        //field3.setSize(5);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);
        return tabledefinition;
    }

    public static Documentary example1() {
        Documentary doc = new Documentary();
        doc.setName("Climbing Everest");
        doc.setDescription("A climb to the summit of Mt.Everest.");
        doc.setDuration(new BigDecimal(2));
        doc.addActor(Actor.example2());
        return doc;
    }

    public String getDescription() {
        return description;
    }

    public Number getDuration() {
        return duration;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(BigDecimal duration) {
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
