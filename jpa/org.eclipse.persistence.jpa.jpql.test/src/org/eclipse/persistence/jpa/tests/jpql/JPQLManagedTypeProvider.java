/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.spi.java.JavaManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaMappingBuilder;

/**
 * The implementation of a {@link org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider
 * IManagedTypeProvider} that is used by the Hermes unit-tests.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class JPQLManagedTypeProvider extends JavaManagedTypeProvider {

	/**
	 * Creates a new <code>JPQLManagedTypeProvider</code>.
	 */
	public JPQLManagedTypeProvider() {
		super(new JavaMappingBuilder());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();

		addMappedSuperclass(AbstractProduct.class);
		addEntity(Address.class);
		addEntity(Alias.class);
		addEntity(CodeAssist.class);
		addEntity(Customer.class);
		addEntity(Dept.class);
		addEntity(Employee.class);
		addEntity(Home.class);
		addEntity(LargeProject.class);
		addEntity(Order.class);
		addEntity(Phone.class);
		addEntity(Product.class);
		addEntity(Project.class);
		addEntity(SmallProject.class);
		addEmbeddable(ShelfLife.class);
		addEmbeddable(ZipCode.class);
	}
}