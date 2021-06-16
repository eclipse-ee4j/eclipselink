/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import java.util.*;
import jakarta.persistence.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Superclass that adds read-only option for read tests.
 */
public class JPAReadPerformanceComparisonTest extends PerformanceRegressionTestCase {
    protected boolean isReadOnly;

    public JPAReadPerformanceComparisonTest() {
        this.isReadOnly = true;
    }

    public JPAReadPerformanceComparisonTest(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public List list(Query query, EntityManager session) {
        List result = null;
        if (!isReadOnly()) {
            session.getTransaction().begin();
            result = query.getResultList();
            session.getTransaction().commit();
        } else {
            session.getTransaction().begin();
            query.setHint("org.hibernate.readOnly", Boolean.TRUE);
            query.setHint("eclipselink.read-only", Boolean.TRUE);
            query.setHint("toplink.return-shared", Boolean.TRUE);
            result = query.getResultList();
            session.getTransaction().commit();
        }
        return result;
    }

    public Object uniqueResult(Query query, EntityManager session) {
        Object result = null;
        if (!isReadOnly()) {
            session.getTransaction().begin();
            result = query.getSingleResult();
            session.getTransaction().commit();
        } else {
            session.getTransaction().begin();
            query.setHint("org.hibernate.readOnly", Boolean.TRUE);
            query.setHint("eclipselink.return-shared", Boolean.TRUE);
            result = query.getSingleResult();
            session.getTransaction().commit();
        }
        return result;
    }
}
