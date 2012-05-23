/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.tests.jpql;

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
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.java.EclipseLinkMappingBuilder;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaManagedTypeProvider;

/**
 * The default implementation of {@link JPQLQueryTestHelper} used by the unit-tests.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class JavaJPQLQueryTestHelper implements JPQLQueryTestHelper {

	/**
	 * The external forms of the ORM configurations mapped to the file name.
	 */
	private Map<String, IORMConfiguration> ormConfigurations;

	/**
	 * The external form of the persistence unit.
	 */
	private JavaManagedTypeProvider persistenceUnit;

	/**
	 * {@inheritDoc}
	 */
	public IORMConfiguration getORMConfiguration(String ormXmlFileName) throws Exception {

		if (ormConfigurations == null) {
			ormConfigurations = new HashMap<String, IORMConfiguration>();
		}

		IORMConfiguration ormConfiguration = ormConfigurations.get(ormXmlFileName);

		if (ormConfiguration == null) {
			ormConfiguration = new JavaORMConfiguration(ormXmlFileName);
			ormConfigurations.put(ormXmlFileName, ormConfiguration);
		}

		return ormConfiguration;
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedTypeProvider getPersistenceUnit() throws Exception {
		if (persistenceUnit == null) {
			// TODO
			persistenceUnit = new JavaManagedTypeProvider(new EclipseLinkMappingBuilder());
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