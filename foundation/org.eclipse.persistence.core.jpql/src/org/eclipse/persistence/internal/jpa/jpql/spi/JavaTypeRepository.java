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
package org.eclipse.persistence.internal.jpa.jpql.spi;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 * The concrete implementation of {@link ITypeRepository} that is wrapping the Java class loader.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
final class JavaTypeRepository extends org.eclipse.persistence.jpa.jpql.spi.java.JavaTypeRepository {

	/**
	 * Creates a new <code>JavaTypeRepository</code>.
	 *
	 * @param classRepository The repository used to access the application's classes
	 */
	JavaTypeRepository(ClassLoader classLoader) {
		super(classLoader);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Class<?> attemptLoadType(String typeName) {
		try {
			if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
				try {
					return (Class<?>) AccessController.doPrivileged(
						new PrivilegedClassForName(typeName, true, getClassLoader())
					);
				}
				catch (PrivilegedActionException exception) {
					return null;
				}
			}

			return PrivilegedAccessHelper.getClassForName(typeName, true, getClassLoader());
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}
}