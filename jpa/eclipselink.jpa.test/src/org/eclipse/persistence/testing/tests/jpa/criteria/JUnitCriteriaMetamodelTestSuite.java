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
//     07/05/2010-2.1.1 Michael O'Brien
//       - 321716: modelgen and jpa versions of duplicate code in both copies of
//       JUnitCriteriaMetamodelTestSuite must be kept in sync (to avoid only failing on WebSphere under Derby)
//       (ideally there should be only one copy of the code - the other suite should reference or subclass for changes)
//       see
//       org.eclipse.persistence.testing.tests.jpa.criteria.JUnitCriteriaMetamodelTestSuite.simpleModTest():1796
//       org.eclipse.persistence.testing.tests.jpa.criteria.metamodel.JUnitCriteriaMetamodelTestSuite.simpleModTest():1766
//       - 321902: this copied code should be renamed, merged or subclassed off the original
package org.eclipse.persistence.testing.tests.jpa.criteria;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.models.jpa.advanced.Address_;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee_;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject_;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber_;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person_;
import org.eclipse.persistence.testing.models.jpa.inheritance.SportsCar_;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer_;
import org.eclipse.persistence.testing.models.jpa.inherited.BlueLight_;

import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.HashMap;

import static org.eclipse.persistence.testing.tests.jpa.criteria.JUnitCriteriaSimpleTestSuiteBase.Attributes.*;

/**
 * @author cdelahun
 * Converted from JUnitJPQLSimpleTestSuite
 */
public class JUnitCriteriaMetamodelTestSuite extends JUnitCriteriaSimpleTestSuiteBase<Attribute> {

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
        attributes.put(Employee_id, Employee_.id);
        attributes.put(Employee_firstName, Employee_.firstName);
        attributes.put(Employee_lastName, Employee_.lastName);
        attributes.put(Employee_salary, Employee_.salary);
        attributes.put(Employee_normalHours, Employee_.normalHours);
        attributes.put(Employee_phoneNumbers, Employee_.phoneNumbers);
        attributes.put(Employee_managedEmployees, Employee_.managedEmployees);
        attributes.put(Employee_projects, Employee_.projects);
        attributes.put(Employee_address, Employee_.address);
        attributes.put(Employee_status, Employee_.status);
        attributes.put(Employee_hugeProject, Employee_.hugeProject);
        attributes.put(PhoneNumber_number, PhoneNumber_.number);
        attributes.put(PhoneNumber_areaCode, PhoneNumber_.areaCode);
        attributes.put(PhoneNumber_owner, PhoneNumber_.owner);
        attributes.put(Address_street, Address_.street);
        attributes.put(Address_postalCode, Address_.postalCode);
        attributes.put(Address_city, Address_.city);
        attributes.put(LargeProject_budget, LargeProject_.budget);
        attributes.put(BeerConsumer_blueBeersToConsume, BeerConsumer_.blueBeersToConsume);
        attributes.put(BlueLight_discount, BlueLight_.discount);
        attributes.put(Person_car, Person_.car);
        attributes.put(SportsCar_maxSpeed, SportsCar_.maxSpeed);
    }

    public JUnitCriteriaMetamodelTestSuite() {
        super();
    }

    public JUnitCriteriaMetamodelTestSuite(String name) {
        super(name);
    }

    //This suite contains all tests contained in this class

    public static Test suite() {
        TestSuite suite = (TestSuite) JUnitCriteriaSimpleTestSuiteBase.suite(JUnitCriteriaMetamodelTestSuite.class);

        suite.setName("JUnitCriteriaMetamodelTestSuite");
        suite.addTest(new JUnitCriteriaMetamodelTestSuite("testNotLoggedIn"));

        return suite;
    }

    public void testNotLoggedIn(){
        Persistence.createEntityManagerFactory(getPersistenceUnitName());
        assertNotNull(Employee_.address);
    }

}


