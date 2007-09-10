/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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