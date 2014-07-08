/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     07/07/2014-2.6 Tomas Kraus
 *       - 439127: Create sample instances for EntityEmbeddableTest jUnit test.
 ******************************************************************************/
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

}
