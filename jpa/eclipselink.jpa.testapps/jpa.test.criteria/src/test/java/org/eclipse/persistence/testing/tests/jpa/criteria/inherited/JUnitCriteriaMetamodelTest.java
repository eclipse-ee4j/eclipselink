/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     07/05/2010-2.1.1 Michael O'Brien
//       - 321716: modelgen and jpa versions of duplicate code in both copies of
//       JUnitCriteriaMetamodelTest must be kept in sync (to avoid only failing on WebSphere under Derby)
//       (ideally there should be only one copy of the code - the other suite should reference or subclass for changes)
//       see
//       org.eclipse.persistence.testing.tests.jpa.criteria.JUnitCriteriaMetamodelTest.simpleModTest():1796
//       org.eclipse.persistence.testing.tests.jpa.criteria.metamodel.JUnitCriteriaMetamodelTest.simpleModTest():1766
//       - 321902: this copied code should be renamed, merged or subclassed off the original
package org.eclipse.persistence.testing.tests.jpa.criteria.inherited;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer_;
import org.eclipse.persistence.testing.models.jpa.inherited.BlueLight_;

import java.util.HashMap;

/**
 * @author cdelahun
 * Converted from JUnitJPQLSimpleTestSuite
 */
public class JUnitCriteriaMetamodelTest extends JUnitCriteriaSimpleTestSuiteBase<Attribute> {

    private final class Wrapper implements CriteriaQueryWrapper {
        @Override
        public <X,Y> Root<X> from(CriteriaQuery<Y> query, Class<X> entityClass) {
            return query.from(entityClass);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <X,Y> Fetch<X,Y> fetch(Root<X> root, Attributes attributeKey, JoinType joinType) {
            return root.fetch((CollectionAttribute) attributes.get(attributeKey), JoinType.LEFT);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <X,Y> Expression<Y> get(Path<X> path, Attributes attributeKey) {
            Attribute attribute = attributes.get(attributeKey);
            if(SingularAttribute.class.isAssignableFrom(attribute.getClass())) {
                return path.get((SingularAttribute) attribute);
            }
            else {
                return path.get((CollectionAttribute) attribute);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <X,Y> Join<X,Y> join(Root<X> root, Attributes attributeKey) {
            Attribute attribute = attributes.get(attributeKey);
            if(SingularAttribute.class.isAssignableFrom(attribute.getClass())) {
                return root.join((SingularAttribute) attribute);
            }
            else if(MapAttribute.class.isAssignableFrom(attribute.getClass())) {
                return root.join((MapAttribute) attribute);
            }
            else {
                return root.join((CollectionAttribute) attribute);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <X, Y> Join<X, Y> join(Root<X> root, Attributes attributeKey, JoinType joinType) {
            return root.join((SingularAttribute) attributes.get(attributeKey), joinType);
        }
    }

    @Override
    protected void setWrapper() {
        this.wrapper = new Wrapper();
    }

    @Override
    protected void populateAttributes() {
        attributes = new HashMap<>();

        attributes.put(Attributes.BeerConsumer_blueBeersToConsume, BeerConsumer_.blueBeersToConsume);
        attributes.put(Attributes.BlueLight_discount, BlueLight_.discount);
    }

    public JUnitCriteriaMetamodelTest() {
        super();
    }

    public JUnitCriteriaMetamodelTest(String name) {
        super(name);
    }

    //This suite contains all tests contained in this class

    public static Test suite() {
        TestSuite suite = (TestSuite) JUnitCriteriaSimpleTestSuiteBase.suite(JUnitCriteriaMetamodelTest.class);

        suite.setName("JUnitCriteriaMetamodelTest");

        return suite;
    }
}
