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
package org.eclipse.persistence.testing.models.readonly;

import java.util.Vector;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Actor {
    public Number id;
    public String name;
    public float minimumSalary;
    public Vector movies;
    public Vector charities;
    public ReadOnlyHollywoodAgent hollywoodAgent;

    public Actor() {
        super();
        this.movies = new Vector();
        this.charities = new Vector();
    }

    public static TableDefinition actorCharityJoinTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("ACT_CHA");

        definition.addPrimaryKeyField("ACT_ID", java.math.BigDecimal.class, 15);
        definition.addPrimaryKeyField("CHARITY_ID", java.math.BigDecimal.class, 15);

        return definition;
    }

    public static TableDefinition actorMovieJoinTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("ACT_MOV");

        definition.addPrimaryKeyField("ACT_ID", java.math.BigDecimal.class, 15);
        definition.addPrimaryKeyField("MOV_ID", java.math.BigDecimal.class, 15);

        return definition;
    }

    public void addCharity(ReadOnlyCharity charity) {
        this.charities.addElement(charity);
    }

    public void addMovie(Movie movie) {
        this.movies.addElement(movie);
    }

    // Actor descriptor
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.testing.models.readonly.Actor.class);
        descriptor.setTableName("RO_ACTOR");
        descriptor.setPrimaryKeyFieldName("ACT_ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ACT_ID");

        descriptor.addDirectMapping("id", "ACT_ID");
        descriptor.addDirectMapping("name", "ACT_NAME");
        descriptor.addDirectMapping("minimumSalary", "MIN_SAL");

        ManyToManyMapping moviesMapping = new ManyToManyMapping();
        moviesMapping.setAttributeName("movies");
        moviesMapping.setReferenceClass(Movie.class);
        moviesMapping.setRelationTableName("ACT_MOV");
        moviesMapping.setSourceRelationKeyFieldName("ACT_ID");
        moviesMapping.setTargetRelationKeyFieldName("MOV_ID");
        moviesMapping.dontUseIndirection();
        moviesMapping.readOnly();
        descriptor.addMapping(moviesMapping);

        // ManyToManyMapping: charities
        ManyToManyMapping charitiesMapping = new ManyToManyMapping();
        charitiesMapping.setAttributeName("charities");
        charitiesMapping.setReferenceClass(ReadOnlyCharity.class);
        charitiesMapping.setRelationTableName("ACT_CHA");
        charitiesMapping.setSourceRelationKeyFieldName("ACT_ID");
        charitiesMapping.setTargetRelationKeyFieldName("CHARITY_ID");
        charitiesMapping.dontUseIndirection();
        descriptor.addMapping(charitiesMapping);

        // OneToOneMapping: hollywoodAgent
        OneToOneMapping hollywoodAgentMapping = new OneToOneMapping();
        hollywoodAgentMapping.setAttributeName("hollywoodAgent");
        hollywoodAgentMapping.setReferenceClass(ReadOnlyHollywoodAgent.class);
        hollywoodAgentMapping.addForeignKeyFieldName("HOLLYWOODAGENT_ID", "HOLLYWOODAGENT_ID");
        hollywoodAgentMapping.dontUseIndirection();
        //hollywoodAgentMapping.readOnly();
        descriptor.addMapping(hollywoodAgentMapping);

        return descriptor;
    }

    public static Actor example1() {
        Actor example = new Actor();

        example.setName("Harrison Ford");
        example.setMinimumSalary((float)200000.00);

        return example;
    }

    public static Actor example2() {
        Actor example = new Actor();

        example.setName("Glenn Close");
        example.setMinimumSalary((float)15000.00);

        return example;
    }

    public static Actor example3() {
        Actor example = new Actor();

        example.setName("Morgan Freeman");
        example.setMinimumSalary((float)180000.00);

        return example;
    }

    public static Actor example4() {
        Actor example = new Actor();

        example.setName("Mark Hamill");
        example.setMinimumSalary((float)2000000.00);

        return example;
    }

    public static Actor example5() {
        Actor example = new Actor();

        example.setName("Bill Murray");
        example.setMinimumSalary((float)1000000.00);

        return example;
    }

    public static Actor example6() {
        Actor example = new Actor();

        example.setName("Martin Sheen");
        example.setMinimumSalary((float)500000.00);

        return example;
    }

    public static Actor example7() {
        Actor example = new Actor();

        example.setName("Marlon Brando");
        example.setMinimumSalary((float)1000000.00);

        return example;
    }

    public Vector getCharities() {
        return charities;
    }

    public ReadOnlyHollywoodAgent getHollywoodAgent() {
        return hollywoodAgent;
    }

    public float getMinimumSalary() {
        return minimumSalary;
    }

    public Vector getMovies() {
        return movies;
    }

    public String getName() {
        return name;
    }

    public void setHollywoodAgent(ReadOnlyHollywoodAgent hollywoodAgent) {
        this.hollywoodAgent = hollywoodAgent;
    }

    public void setMinimumSalary(float minimumSalary) {
        this.minimumSalary = minimumSalary;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Actor table definition
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("RO_ACTOR");

        definition.addIdentityField("ACT_ID", java.math.BigDecimal.class, 15);
        definition.addField("ACT_NAME", String.class, 50);
        definition.addField("MIN_SAL", Float.class);
        definition.addField("HOLLYWOODAGENT_ID", java.math.BigDecimal.class, 15);

        return definition;
    }

    public String toString() {
        return new String("Actor: " + getName());
    }
}
