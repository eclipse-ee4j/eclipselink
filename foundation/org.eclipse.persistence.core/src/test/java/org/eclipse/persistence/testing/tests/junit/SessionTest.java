/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//      07/17/2024-5.0 Tomas Kraus

package org.eclipse.persistence.testing.tests.junit;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.jpa.jpql.HermesParser;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.JPAQueryBuilder;
import org.eclipse.persistence.sessions.Project;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SessionTest {

    // AbstractSession test. Using DatabaseSessionImpl which does not override tested code.
    // Verify that default JPAQueryBuilder returned by session Project instance is HermesParser
    @Test
    public void testDefaultProjectQueryBuilder() {
        AbstractSession session = new DatabaseSessionImpl();
        // Session parent may affect returned JPAQueryBuilder so make sure no one exists
        assertThat(session.getParent(), is(nullValue()));
        // Set Project used to initialize JPAQueryBuilder instance
        session.setProject(new Project());
        JPAQueryBuilder builder = session.getQueryBuilder();
        assertThat(builder, instanceOf(HermesParser.class));
    }

    // AbstractSession test. Using DatabaseSessionImpl which does not override tested code.
    // Verify that JPAQueryBuilder returned by session Project instance matches CustomQueryBuilder
    // after CustomQueryBuilder::new is set as JPAQueryBuilder instance factory
    @Test
    public void testCustomProjectQueryBuilder() {

        class CustomQueryBuilder implements JPAQueryBuilder {
            @Override
            public void setValidationLevel(String level) {
                throw new UnsupportedOperationException();
            }
            @Override
            public DatabaseQuery buildQuery(CharSequence jpqlQuery, AbstractSession session) {
                throw new UnsupportedOperationException();
            }
            @Override
            public Expression buildSelectionCriteria(String entityName, String selectionCriteria, AbstractSession session) {
                throw new UnsupportedOperationException();
            }
            @Override
            public void populateQuery(CharSequence jpqlQuery, DatabaseQuery query, AbstractSession session) {
                throw new UnsupportedOperationException();
            }
        }

        Project project = new Project();
        project.setQueryBuilderSupplier(CustomQueryBuilder::new);
        AbstractSession session = new DatabaseSessionImpl();
        // Session parent may affect returned JPAQueryBuilder so make sure no one exists
        assertThat(session.getParent(), is(nullValue()));
        // Set Project used to initialize JPAQueryBuilder instance
        session.setProject(project);
        JPAQueryBuilder builder = session.getQueryBuilder();
        assertThat(builder, instanceOf(JPAQueryBuilder.class));
    }

}
