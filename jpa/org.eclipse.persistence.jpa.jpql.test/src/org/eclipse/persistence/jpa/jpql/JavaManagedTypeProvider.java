/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.example.Address;
import org.eclipse.persistence.jpa.jpql.example.Alias;
import org.eclipse.persistence.jpa.jpql.example.CodeAssist;
import org.eclipse.persistence.jpa.jpql.example.Customer;
import org.eclipse.persistence.jpa.jpql.example.Dept;
import org.eclipse.persistence.jpa.jpql.example.Employee;
import org.eclipse.persistence.jpa.jpql.example.Home;
import org.eclipse.persistence.jpa.jpql.example.Order;
import org.eclipse.persistence.jpa.jpql.example.Phone;
import org.eclipse.persistence.jpa.jpql.example.Product;
import org.eclipse.persistence.jpa.jpql.example.Project;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IPlatform;
import org.eclipse.persistence.jpa.jpql.spi.IType;

/**
 * The concrete implementation of {@link IManagedTypeProvider} that is wrapping the runtime
 * representation of a provider of a managed type.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class JavaManagedTypeProvider implements IManagedTypeProvider {

	private Map<String, IManagedType> managedTypes;
	private JavaTypeRepository typeRepository;
	private final IJPAVersion version;

	/**
	 * Creates a new <code>JavaManagedTypeProvider</code>.
	 *
	 * @param version
	 */
	JavaManagedTypeProvider(IJPAVersion version) {
		super();
		this.version = version;
	}

	private IManagedType buildEntity(Class<?> type) {
		return new JavaEntity(this, getTypeRepository().getType(type));
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<String> entityNames() {
		initializeManagedTypes();
		return managedTypes.keySet().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(IType type) {

		initializeManagedTypes();

		for (IManagedType managedType : managedTypes.values()) {
			if (managedType.getType() == type) {
				return managedType;
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(String abstractSchemaName) {
		initializeManagedTypes();
		return managedTypes.get(abstractSchemaName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IPlatform getPlatform() {
		return IPlatform.ECLIPSE_LINK;
	}

	/**
	 * {@inheritDoc}
	 */
	public JavaTypeRepository getTypeRepository() {
		if (typeRepository == null) {
			typeRepository = new JavaTypeRepository(getClass().getClassLoader());
		}
		return typeRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	public IJPAVersion getVersion() {
		return version;
	}

	private void initializeManagedTypes() {
		if (managedTypes == null) {
			managedTypes = new HashMap<String, IManagedType>();
			managedTypes.put("Address",    buildEntity(Address.class));
			managedTypes.put("Alias",      buildEntity(Alias.class));
			managedTypes.put("CodeAssist", buildEntity(CodeAssist.class));
			managedTypes.put("Customer",   buildEntity(Customer.class));
			managedTypes.put("Dept",       buildEntity(Dept.class));
			managedTypes.put("Employee",   buildEntity(Employee.class));
			managedTypes.put("Home",       buildEntity(Home.class));
			managedTypes.put("Order",      buildEntity(Order.class));
			managedTypes.put("Phone",      buildEntity(Phone.class));
			managedTypes.put("Product",    buildEntity(Product.class));
			managedTypes.put("Project",    buildEntity(Project.class));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<IManagedType> managedTypes() {
		initializeManagedTypes();
		return managedTypes.values().iterator();
	}
}