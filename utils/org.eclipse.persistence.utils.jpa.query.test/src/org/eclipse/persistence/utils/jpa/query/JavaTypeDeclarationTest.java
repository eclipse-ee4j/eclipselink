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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * The unit-tests for {@link JavaTypeDeclaration}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings({"unused", "nls"})
public final class JavaTypeDeclarationTest {

	private Collection<JavaTypeDeclarationTest> attribute1;
	private Collection<?> attribute2;
	private Collection<String[]> attribute3;
	private Map<String[], String> attribute4;
	private Map<String[][][], String> attribute5;
	private String[][][] attribute6;

	@Test
	public void test_getDimensionality_1() throws Exception {

		ITypeRepository typeRepository = typeRepository();
		Field attributeField = getClass().getDeclaredField("attribute1");

		JavaType type = new JavaType(typeRepository, attributeField.getType());
		ITypeDeclaration typeDeclaration = new JavaTypeDeclaration(typeRepository, type, attributeField.getGenericType(), true);

		assertEquals(0, typeDeclaration.getDimensionality());
	}

	@Test
	public void test_getDimensionality_2() throws Exception {

		ITypeRepository typeRepository = typeRepository();
		Field attributeField = getClass().getDeclaredField("attribute6");

		JavaType type = new JavaType(typeRepository, attributeField.getType());
		ITypeDeclaration typeDeclaration = new JavaTypeDeclaration(typeRepository, type, attributeField.getGenericType(), true);

		assertEquals(3, typeDeclaration.getDimensionality());
	}

	@Test
	public void test_getType() {
		ITypeRepository typeRepository = typeRepository();
		JavaType type = new JavaType(typeRepository, String.class);
		ITypeDeclaration typeDeclaration = new JavaTypeDeclaration(typeRepository, type, null, false);
		assertSame(type, typeDeclaration.getType());
	}

	@Test
	public void test_getTypeParameters_1() throws Exception {

		ITypeRepository typeRepository = typeRepository();
		Field attributeField = getClass().getDeclaredField("attribute1");

		JavaType type = new JavaType(typeRepository, attributeField.getType());
		ITypeDeclaration typeDeclaration = new JavaTypeDeclaration(typeRepository, type, attributeField.getGenericType(), false);

		ITypeDeclaration[] parameterTypes = typeDeclaration.getTypeParameters();
		assertNotNull(parameterTypes);
		assertTrue(parameterTypes.length > 0);

		ITypeDeclaration parameterType = parameterTypes[0];
		assertEquals(JavaTypeDeclarationTest.class.getName(), parameterType.getType().getName());
	}

	@Test
	public void test_getTypeParameters_2() throws Exception {

		ITypeRepository typeRepository = typeRepository();
		Field attributeField = getClass().getDeclaredField("attribute2");

		JavaType type = new JavaType(typeRepository, attributeField.getType());
		ITypeDeclaration typeDeclaration = new JavaTypeDeclaration(typeRepository, type, attributeField.getGenericType(), false);

		ITypeDeclaration[] parameterTypes = typeDeclaration.getTypeParameters();
		assertNotNull(parameterTypes);
		assertEquals(1, parameterTypes.length);

		ITypeDeclaration parameterType = parameterTypes[0];
		assertEquals(Object.class.getName(), parameterType.getType().getName());
	}

	@Test
	public void test_getTypeParameters_3() throws Exception {

		ITypeRepository typeRepository = typeRepository();
		Field attributeField = getClass().getDeclaredField("attribute3");

		JavaType type = new JavaType(typeRepository, attributeField.getType());
		ITypeDeclaration typeDeclaration = new JavaTypeDeclaration(typeRepository, type, attributeField.getGenericType(), false);

		ITypeDeclaration[] parameterTypes = typeDeclaration.getTypeParameters();
		assertNotNull(parameterTypes);
		assertEquals(1, parameterTypes.length);

		ITypeDeclaration parameterType = parameterTypes[0];
		assertNotNull(parameterType);
		assertEquals(String[].class.getName(), parameterType.getType().getName());
		assertTrue(parameterType.isArray());

		parameterTypes = parameterType.getTypeParameters();
		assertNotNull(parameterTypes);
		assertEquals(1, parameterTypes.length);

		parameterType = parameterTypes[0];
		assertNotNull(parameterType);
		assertEquals(String.class.getName(), parameterType.getType().getName());
		assertFalse(parameterType.isArray());
	}

	@Test
	public void test_getTypeParameters_4() throws Exception {

		ITypeRepository typeRepository = typeRepository();
		Field attributeField = getClass().getDeclaredField("attribute4");

		JavaType type = new JavaType(typeRepository, attributeField.getType());
		ITypeDeclaration typeDeclaration = new JavaTypeDeclaration(typeRepository, type, attributeField.getGenericType(), false);

		ITypeDeclaration[] parameterTypes = typeDeclaration.getTypeParameters();
		assertNotNull(parameterTypes);
		assertEquals(2, parameterTypes.length);

		// String[]
		ITypeDeclaration parameterType = parameterTypes[0];
		assertEquals(String[].class.getName(), parameterType.getType().getName());
		assertTrue(parameterType.isArray());
		assertEquals(1, parameterType.getDimensionality());

		// String
		parameterType = parameterTypes[1];
		assertEquals(String.class.getName(), parameterType.getType().getName());
	}

	@Test
	public void test_getTypeParameters_5() throws Exception {

		ITypeRepository typeRepository = typeRepository();
		Field attributeField = getClass().getDeclaredField("attribute5");

		JavaType type = new JavaType(typeRepository, attributeField.getType());
		ITypeDeclaration typeDeclaration = new JavaTypeDeclaration(typeRepository, type, attributeField.getGenericType(), false);

		ITypeDeclaration[] parameterTypes = typeDeclaration.getTypeParameters();
		assertNotNull(parameterTypes);
		assertEquals(2, parameterTypes.length);

		// String[][][]
		ITypeDeclaration parameterType = parameterTypes[0];
		assertEquals(String[][][].class.getName(), parameterType.getType().getName());
		assertTrue(parameterType.isArray());
		assertEquals(3, parameterType.getDimensionality());

		// String
		parameterType = parameterTypes[1];
		assertEquals(String.class.getName(), parameterType.getType().getName());
	}

	@Test
	public void test_isArray() throws Exception {

		ITypeRepository typeRepository = typeRepository();
		Field attributeField = getClass().getDeclaredField("attribute6");

		JavaType type = new JavaType(typeRepository, attributeField.getType());
		ITypeDeclaration typeDeclaration = new JavaTypeDeclaration(typeRepository, type, attributeField.getGenericType(), true);

		assertTrue(attributeField.getType().isArray());
		assertTrue(typeDeclaration.isArray());
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