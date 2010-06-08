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
package org.eclipse.persistence.testing.models.interfaces;

public class Actor {
    public int id;
    public String fname;
    public String lname;
    public ProgramInt program; //Variable One-to-One (Film or Documentary)

    public Actor() {
        super();
        this.fname = "";
        this.lname = "";
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition actorTable() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = 
            new org.eclipse.persistence.tools.schemaframework.TableDefinition();


        // SECTION: TABLE
        tabledefinition.setName("ACTOR");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("FNAME");
        field.setType(String.class);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("LNAME");
        field1.setType(String.class);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field2 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field2.setName("ID");
        field2.setType(java.math.BigDecimal.class);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(true);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field3 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field3.setName("PROGRAM_ID");
        field3.setType(java.math.BigDecimal.class);
        field3.setShouldAllowNull(false);
        field3.setIsPrimaryKey(true);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field4 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field4.setName("PROGRAM_TYPE");
        field4.setType(String.class);
        field4.setShouldAllowNull(false);
        field4.setIsPrimaryKey(true);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        tabledefinition.addField(field4);
        return tabledefinition;
    }

    public static Actor example1() {
        Actor actor = new Actor();
        actor.setId(1);
        actor.setFName("Stephen");
        actor.setLName("Baldwin");
        return actor;
    }

    public static Actor example2() {
        Actor actor = new Actor();
        actor.setId(2);
        actor.setFName("Troy");
        actor.setLName("MacLure");
        return actor;
    }

    public static Actor example3() {
        Actor actor = new Actor();
        actor.setId(3);
        actor.setFName("Drew");
        actor.setLName("Barrymore");
        return actor;
    }

    public static Actor example4() {
        //include in populate
        Film film = new Film();
        film.setName("Scream 4");
        film.setDescription("Scream 3 was the last one!  What's going on?");
        film.setDuration(new java.math.BigDecimal(2));

        Actor actor = new Actor();
        actor.setId(4);
        actor.setFName("Nev");
        actor.setLName("Campbell");

        film.addActor(actor);
        return actor;
    }

    public static Actor example5() {
        Actor actor = new Actor();
        actor.setId(5);
        actor.setFName("Tom");
        actor.setLName("Green");
        return actor;
    }

    public String getFName() {
        return this.fname;
    }

    public int getId() {
        return this.id;
    }

    public String getLName() {
        return this.lname;
    }

    public void setFName(String name) {
        this.fname = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLName(String name) {
        this.lname = name;
    }

    public String toString() {
        return this.fname + " " + this.lname;
    }
}
