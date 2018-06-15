/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;
import jpql.query.AbstractProduct;
import jpql.query.Address;
import jpql.query.Alias;
import jpql.query.CodeAssist;
import jpql.query.Customer;
import jpql.query.Dept;
import jpql.query.Employee;
import jpql.query.Home;
import jpql.query.LargeProject;
import jpql.query.Order;
import jpql.query.Phone;
import jpql.query.Product;
import jpql.query.Project;
import jpql.query.ShelfLife;
import jpql.query.SmallProject;
import jpql.query.ZipCode;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMappingBuilder;
import org.eclipse.persistence.jpa.tests.jpql.tools.spi.java.JavaManagedTypeProvider;

/**
 * The default implementation of {@link JPQLQueryTestHelper} used by the unit-tests.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class JavaJPQLQueryTestHelper implements JPQLQueryTestHelper {

    /**
     * The external forms of the ORM configurations mapped to the file name.
     */
    private Map<String, IORMConfiguration> ormConfigurations;

    /**
     * The external form of the persistence unit.
     */
    private JavaManagedTypeProvider persistenceUnit;

    /**
     * Creates
     *
     * @return
     */
    protected abstract IMappingBuilder<Member> buildMappingBuilder();

    /**
     * {@inheritDoc}
     */
    public IORMConfiguration getORMConfiguration(String ormXmlFileName) throws Exception {

        if (ormConfigurations == null) {
            ormConfigurations = new HashMap<String, IORMConfiguration>();
        }

        IORMConfiguration ormConfiguration = ormConfigurations.get(ormXmlFileName);

        if (ormConfiguration == null) {
            ormConfiguration = new JavaORMConfiguration(buildMappingBuilder(), ormXmlFileName);
            ormConfigurations.put(ormXmlFileName, ormConfiguration);
        }

        return ormConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    public IManagedTypeProvider getPersistenceUnit() throws Exception {
        if (persistenceUnit == null) {
            persistenceUnit = new JavaManagedTypeProvider(buildMappingBuilder());
            initializeManagedTypeProvider();
        }
        return persistenceUnit;
    }

    private void initializeManagedTypeProvider() {
        persistenceUnit.addMappedSuperclass(AbstractProduct.class);
        persistenceUnit.addEntity(Address.class);
        persistenceUnit.addEntity(Alias.class);
        persistenceUnit.addEntity(CodeAssist.class);
        persistenceUnit.addEntity(Customer.class);
        persistenceUnit.addEntity(Dept.class);
        persistenceUnit.addEntity(Employee.class);
        persistenceUnit.addEntity(Home.class);
        persistenceUnit.addEntity(LargeProject.class);
        persistenceUnit.addEntity(Order.class);
        persistenceUnit.addEntity(Phone.class);
        persistenceUnit.addEntity(Product.class);
        persistenceUnit.addEntity(Project.class);
        persistenceUnit.addEntity(SmallProject.class);
        persistenceUnit.addEmbeddable(Employee.EmbeddedAddress.class);
        persistenceUnit.addEmbeddable(ShelfLife.class);
        persistenceUnit.addEmbeddable(ZipCode.class);
    }

    /**
     * {@inheritDoc}
     */
    public void setUp() throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    public void setUpBefore() throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    public void tearDown() throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    public void tearDownAfter() throws Exception {
    }
}
