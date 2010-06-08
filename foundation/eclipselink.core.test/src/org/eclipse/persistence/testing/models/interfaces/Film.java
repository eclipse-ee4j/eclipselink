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

import java.util.Vector;
import java.math.BigDecimal;

/**
 * A Film is a program.
 */
public class Film implements ProgramInt {
    public int id;
    public String name;
    public String description;
    public BigDecimal duration = new BigDecimal(0);
    public Vector actors; //type Actor

    public Film() {
        actors = new Vector();
    }

    public void addActor(Actor actor) {
        actor.program = this;
        actors.addElement(actor);
    }

    public static Film example1() {
        Film film = new Film();
        film.setName("Never Been Kissed");
        film.setDescription("Writer goes undercover as a student at a local high school.");
        film.setDuration(new BigDecimal(2));
        film.addActor(Actor.example3());
        return film;
    }

    public static Film example2() {
        Film film = new Film();
        film.setName("The Flinstones");
        film.setDescription("How Fred, Wilma, Barney, and Betty came to be...");
        film.setDuration(new BigDecimal(1));
        film.addActor(Actor.example1());
        return film;
    }

    public static Film example3() {
        Film film = new Film();
        film.setName("Road Trip");
        film.setDescription("Tom Green stars in this comedy about a road trip.");
        film.setDuration(new BigDecimal(1));
        film.addActor(Actor.example5());
        return film;
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition filmTable() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = 
            new org.eclipse.persistence.tools.schemaframework.TableDefinition();


        // SECTION: TABLE
        tabledefinition.setName("FILM");

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
