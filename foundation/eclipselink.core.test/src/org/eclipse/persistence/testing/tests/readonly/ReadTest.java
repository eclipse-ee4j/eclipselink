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

import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.models.readonly.Movie;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class ReadTest extends ReadObjectTest {
    public Movie movieFromDatabase;

    public ReadTest() {
        super();
    }

    public ReadTest(Object originalObject) {
        super(originalObject);
    }

    protected void setup() {
    }

    protected void test() {
        movieFromDatabase = (Movie)getSession().readObject(Movie.class);
    }

    protected void verify() {
        if (movieFromDatabase.getStudio() != null) {
            if (movieFromDatabase.getStudio().getName() != null) {
                throw new TestErrorException("Movie's Studio's name was not null but should have been.");
            }
            if (movieFromDatabase.getStudio().getOwner() != null) {
                throw new TestErrorException("Movie's Studio's owner was not null but should have been.");
            }
            if (movieFromDatabase.getStudio().getAddress() != null) {
                throw new TestErrorException("Movie's Studio's address was not null but should have been.");
            }
        }
    }
}
