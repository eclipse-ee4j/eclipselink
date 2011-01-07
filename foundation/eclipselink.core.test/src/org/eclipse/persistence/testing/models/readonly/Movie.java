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

public class Movie {
    public Number id;
    public String title;
    public Studio studio;
    public Vector actors;
    public Promoter promoter;

    public Movie() {
        super();
        this.actors = new Vector();
    }

    public void addActor(Actor actor) {
        this.actors.addElement(actor);
    }

    // Movie descriptor
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.testing.models.readonly.Movie.class);
        descriptor.setTableName("RO_MOVIE");
        descriptor.addPrimaryKeyFieldName("MOV_ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("MOV_ID");

        descriptor.addDirectMapping("id", "MOV_ID");
        descriptor.addDirectMapping("title", "TITLE");

        ManyToManyMapping actorsMapping = new ManyToManyMapping();
        actorsMapping.setAttributeName("actors");
        actorsMapping.setReferenceClass(Actor.class);
        actorsMapping.setRelationTableName("ACT_MOV");
        actorsMapping.setSourceRelationKeyFieldName("MOV_ID");
        actorsMapping.setTargetRelationKeyFieldName("ACT_ID");
        actorsMapping.dontUseIndirection();
        actorsMapping.privateOwnedRelationship();
        descriptor.addMapping(actorsMapping);

        AggregateObjectMapping studioMapping = new AggregateObjectMapping();
        studioMapping.setAttributeName("studio");
        studioMapping.setReferenceClass(Studio.class);
        studioMapping.readOnly();
        descriptor.addMapping(studioMapping);

        OneToOneMapping oneToOneMapping = new OneToOneMapping();
        oneToOneMapping.setAttributeName("promoter");
        oneToOneMapping.setReferenceClass(Promoter.class);
        oneToOneMapping.addForeignKeyFieldName("PROMO_ID", "PROMO_ID");
        oneToOneMapping.dontUseIndirection();
        descriptor.addMapping(oneToOneMapping);

        return descriptor;
    }

    public static Movie example1() {
        Movie example = new Movie();

        example.setTitle("The Empire Strikes Back");
        example.setStudio(Studio.example1());

        Actor actor1 = Actor.example1();
        actor1.addMovie(example);
        Actor actor2 = Actor.example4();
        actor2.addMovie(example);

        example.addActor(actor1);
        example.addActor(actor2);

        example.setPromoter(Promoter.example1());
        return example;
    }

    public static Movie example2() {
        Movie example = new Movie();

        example.setTitle("Air Force One");
        example.setStudio(Studio.example2());

        Actor actor1 = Actor.example1();
        actor1.addMovie(example);
        Actor actor2 = Actor.example2();
        actor2.addMovie(example);

        example.addActor(actor1);
        example.addActor(actor2);

        example.setPromoter(Promoter.example2());

        return example;
    }

    public static Movie example3() {
        Movie example = new Movie();

        example.setTitle("Deep Impact");
        example.setStudio(Studio.example3());

        Actor actor1 = Actor.example2();
        actor1.addMovie(example);
        Actor actor2 = Actor.example3();
        actor2.addMovie(example);

        example.addActor(actor1);
        example.addActor(actor2);

        example.setStudio(Studio.example4());

        example.setPromoter(Promoter.example3());

        return example;
    }

    public static Movie example4() {
        Movie example = new Movie();

        example.setTitle("Meatballs");

        example.setStudio(Studio.example4());

        Actor actor1 = Actor.example2();
        actor1.addMovie(example);
        Actor actor2 = Actor.example3();
        actor2.addMovie(example);
        Actor actor5 = Actor.example5();
        actor5.addMovie(example);

        example.addActor(actor1);
        example.addActor(actor2);
        example.addActor(actor5);

        example.setPromoter(Promoter.example4());

        return example;
    }

    public static Movie example5() {
        Movie example = new Movie();

        example.setTitle("Apocalype Now");

        example.setStudio(Studio.example5());

        Actor actor1 = Actor.example1();
        actor1.addMovie(example);
        Actor actor2 = Actor.example2();
        actor2.addMovie(example);

        example.addActor(actor1);
        example.addActor(actor2);

        example.setPromoter(Promoter.example5());

        return example;
    }

    public Vector getActors() {
        return actors;
    }

    public Promoter getPromoter() {
        return promoter;
    }

    public Studio getStudio() {
        return studio;
    }

    public String getTitle() {
        return title;
    }

    public void setPromoter(Promoter newValue) {
        this.promoter = newValue;
    }

    public void setStudio(Studio studio) {
        this.studio = studio;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Movie table definition
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("RO_MOVIE");

        definition.addIdentityField("MOV_ID", java.math.BigDecimal.class, 15);
        definition.addField("TITLE", String.class, 50);
        definition.addField("STD_NAME", String.class, 50);
        definition.addField("STD_OWN", String.class, 50);
        definition.addField("STD_ADD", java.math.BigDecimal.class, 15);
        definition.addField("PROMO_ID", java.math.BigDecimal.class, 15);

        return definition;
    }

    public String toString() {
        return new String("Movie: " + getTitle());
    }
}
