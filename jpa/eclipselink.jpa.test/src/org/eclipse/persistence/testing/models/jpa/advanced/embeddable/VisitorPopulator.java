/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     07/07/2014-2.6 Tomas Kraus
//       - 439127: Create sample instances for EntityEmbeddableTest jUnit test.
//     03/19/2018-2.7.2 Lukas Jungmann
//       - 413120: Nested Embeddable Null pointer
//       - 496836: NullPointerException on ObjectChangeSet.mergeObjectChanges
package org.eclipse.persistence.testing.models.jpa.advanced.embeddable;

/**
 * Build and populate entities for example and testing purposes.
 */
public class VisitorPopulator {

    /**
     * First sample instance.
     * @return Initialized sample instance of {@see Visitor} class.
     */
    public static Visitor visitorExample1() {
        return new Visitor("1", "Alan E. Frechette",
                new Country("USA", "United States"));
    }

    /**
     * Second sample instance.
     * @return Initialized sample instance of {@see Visitor} class.
     */
    public static Visitor visitorExample2() {
        return new Visitor("2", "Arthur D. Frechette",
                new Country("GBR", "England"));
    }

    public static Visitor visitorExample3() {
        return new Visitor("10", "Pepa Novak",
                new Country("CZE", "Czech Republic", "Europe"));
    }

    public static Visitor visitorExample4() {
        return new Visitor("11", "Arthur D. Frechette",
                new Country("GBR", "England", "ANT"));
    }

    public static Visitor visitorExample5() {
        Visitor v = new Visitor("12", "Arthur D. Frechette",
                new Country("GBR", "England", "AFR"));
        v.getCountry().getContinent().setDescription(new Description("Note about Africa"));
        return v;
    }

}
