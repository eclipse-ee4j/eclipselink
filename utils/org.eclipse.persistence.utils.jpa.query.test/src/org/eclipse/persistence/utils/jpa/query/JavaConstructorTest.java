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
package org.eclipse.persistence.utils.jpa.query;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.persistence.utils.jpa.query.spi.IConstructor;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;
import org.junit.Test;

import static org.junit.Assert.*;

public final class JavaConstructorTest<T extends Collection<? extends IConstructor>> {

	public JavaConstructorTest() {
		super();
	}

	@SuppressWarnings("unused")
	private JavaConstructorTest(Collection<? extends T> parameter) {
		super();
	}

	@SuppressWarnings("unused")
	private JavaConstructorTest(String[] parameter1, Collection<? extends String[][]> parameter2) {
		super();
	}

	@Test
	public void test_getConstructors() {

		ITypeRepository typeRepository = typeRepository();
		JavaType type = new JavaType(typeRepository, JavaConstructorTest.class);

		Iterator<IConstructor> constructors = type.constructors();
		assertNotNull(constructors);
		assertTrue(constructors.hasNext());

		while (constructors.hasNext()) {

			IConstructor constructor = constructors.next();
			ITypeDeclaration[] parameterTypes = constructor.getParameterTypes();
			assertNotNull(parameterTypes);

			if (parameterTypes.length == 0) {
				// Nothing to test
			}
			else if (parameterTypes.length == 1) {

				// <T extends Collection<? extends IConstructor>>
				ITypeDeclaration parameterType = parameterTypes[0];
				assertNotNull(parameterType);
				assertEquals(Collection.class.getName(), parameterType.getType().getName());
				assertEquals(0, parameterType.getDimensionality());

				// <? extends Collection<? extends IConstructor>>
				ITypeDeclaration[] typeParameters = parameterType.getTypeParameters();
				assertNotNull(typeParameters);
				assertEquals(1, typeParameters.length);

				// Collection<? extends IConstructor>
				ITypeDeclaration typeParameter = typeParameters[0];
				assertNotNull(typeParameter);
				assertEquals(Collection.class.getName(), typeParameter.getType().getName());
				assertEquals(0, typeParameter.getDimensionality());
			}
			else if (parameterTypes.length == 2) {

				// String[]
				ITypeDeclaration parameterType = parameterTypes[0];
				assertNotNull(parameterType);
				assertEquals(String[].class.getName(), parameterType.getType().getName());
				assertEquals(1, parameterType.getDimensionality());

				// String
				ITypeDeclaration[] typeParameters = parameterType.getTypeParameters();
				assertNotNull(typeParameters);
				assertEquals(1, typeParameters.length);

				ITypeDeclaration typeParameter = typeParameters[0];
				assertNotNull(typeParameter);
				assertEquals(String.class.getName(), typeParameter.getType().getName());
				assertEquals(0, typeParameter.getDimensionality());

				// Collection<String[][]>
				parameterType = parameterTypes[1];
				assertNotNull(parameterType);
				assertEquals(Collection.class.getName(), parameterType.getType().getName());
				assertEquals(0, typeParameter.getDimensionality());

				// <String[][]>
				typeParameters = parameterType.getTypeParameters();
				assertNotNull(typeParameters);
				assertEquals(1, typeParameters.length);

				// String[][]
				typeParameter = typeParameters[0];
				assertEquals(String[][].class.getName(), typeParameter.getType().getName());
				assertEquals(2, typeParameter.getDimensionality());

				// String
				typeParameters = typeParameter.getTypeParameters();
				assertNotNull(typeParameters);
				assertEquals(1, typeParameters.length);

				typeParameter = typeParameters[0];
				assertEquals(String.class.getName(), typeParameter.getType().getName());
				assertEquals(0, typeParameter.getDimensionality());
			}
			else {
				fail();
			}
		}
	}

	private ITypeRepository typeRepository() {
		return new ITypeRepository() {
			public IType getEnumType(String enumTypeName) {
				return null;
			}
			public IType getType(Class<?> type) {
				return new JavaType(this, type);
			}
			public IType getType(String typeName) {
				try {
					return getType(Class.forName(typeName));
				}
				catch (Exception e) {
					return new JavaType(this, typeName);
				}
			}
		};
	}
}