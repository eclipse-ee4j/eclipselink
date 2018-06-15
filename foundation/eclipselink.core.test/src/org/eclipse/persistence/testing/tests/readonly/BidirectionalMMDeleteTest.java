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
package org.eclipse.persistence.testing.tests.readonly;

import java.util.Vector;
import org.eclipse.persistence.testing.models.readonly.Movie;
import org.eclipse.persistence.testing.models.readonly.Actor;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;

public class BidirectionalMMDeleteTest extends AutoVerifyTestCase {
    public Movie movieToDelete;
    public Actor actorFromDeletedMovie;
    public Vector actorsFromDatabase;

    public BidirectionalMMDeleteTest() {
        super();
    }

    protected void setup() {
        movieToDelete = (Movie)getSession().readObject(Movie.class);
        beginTransaction();
    }

    public void reset() {
        rollbackTransaction();
    }

    protected void test() {
        Actor actor = (Actor)movieToDelete.getActors().elementAt(0);
        movieToDelete.getActors().removeElementAt(0);
        actor.getMovies().removeElement(movieToDelete);
        getDatabaseSession().writeObject(movieToDelete);
    }
}
