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
package org.eclipse.persistence.testing.models.collections;

import java.util.*;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.testing.models.collections.Person;

public class Diner extends Person {
    private Vector favouriteRestaurants = new Vector();

    /**
     * Diner constructor comment.
     */
    public Diner() {
        super();
    }

    public void addFavouriteRestaurant(Restaurant aRestaurant) {
        collectionChange("favouriteRestaurants", getFavouriteRestaurants(), aRestaurant, CollectionChangeEvent.ADD, false);
        getFavouriteRestaurants().addElement(aRestaurant);
    }

    public static Diner example1() {
        Diner example1 = new Diner();

        example1.setFirstName("Ralph");
        example1.setLastName("Furley");
        return example1;
    }

    public static Diner example2() {
        Diner example2 = new Diner();

        example2.setFirstName("Heather");
        example2.setLastName("Moss");
        return example2;
    }

    public static Diner example3() {
        Diner example3 = new Diner();

        example3.setFirstName("Max");
        example3.setLastName("Adams");

        return example3;
    }

    public Vector getFavouriteRestaurants() {
        return favouriteRestaurants;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition relationTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("COL_DI_R");

        definition.addPrimaryKeyField("DINER_ID", java.math.BigDecimal.class);
        definition.addPrimaryKeyField("REST_ID", java.math.BigDecimal.class);

        return definition;
    }

    public void setFavouriteRestaurants(Vector newValue) {
        propertyChange("favouriteRestaurants", this.favouriteRestaurants, newValue);
        this.favouriteRestaurants = newValue;
    }
}
