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

import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Vector;
import javax.persistence.OrderBy;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link JavaType}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class JavaTypeTest {

	@Test
	public void test_equals_1() {
		JavaType type1 = new JavaType(typeRepository(), String.class);
		JavaType type2 = new JavaType(typeRepository(), String.class);
		assertTrue(type1.equals(type2));
	}

	@Test
	public void test_equals_2() {
		JavaType type1 = new JavaType(typeRepository(), String.class);
		JavaType type2 = new JavaType(typeRepository(), Object.class);
		assertFalse(type1.equals(type2));
	}

	@Test
	public void test_equals_3() {
		JavaType type1 = new JavaType(typeRepository(), String.class.getName());
		JavaType type2 = new JavaType(typeRepository(), Object.class);
		assertFalse(type1.equals(type2));
	}

	@Test
	public void test_equals_4() {
		JavaType type1 = new JavaType(typeRepository(), String.class.getName());
		JavaType type2 = new JavaType(typeRepository(), Object.class);
		assertFalse(type2.equals(type1));
	}

	@Test
	public void test_equals_5() {
		JavaType type1 = new JavaType(typeRepository(), String.class.getName());
		JavaType type2 = new JavaType(typeRepository(), String.class);
		assertTrue(type1.equals(type2));
	}

	@Test
	public void test_equals_6() {
		JavaType type1 = new JavaType(typeRepository(), String.class.getName());
		JavaType type2 = new JavaType(typeRepository(), String.class);
		assertTrue(type2.equals(type1));
	}

	@Test
	public void test_getType_1() {
		Class<?> javaType = String.class;
		JavaType type = new JavaType(typeRepository(), javaType);
		assertSame(javaType, type.getType());
	}

	@Test
	public void test_getType_2() {
		JavaType type = new JavaType(typeRepository(), String.class.getName());
		assertNull(type.getType());
	}

	@Test
	public void test_hasAnnotation_1() {
		JavaType type = new JavaType(typeRepository(), Override.class);
		assertFalse(type.hasAnnotation(OrderBy.class));
	}

	@Test
	public void test_hasAnnotation_2() {
		JavaType type = new JavaType(typeRepository(), Override.class);
		assertTrue(type.hasAnnotation(Target.class));
	}

	@Test
	public void test_hasAnnotation_3() {
		JavaType type = new JavaType(typeRepository(), Override.class.getName());
		assertFalse(type.hasAnnotation(Target.class));
	}

	@Test
	public void test_hashCode_1() {
		Class<?> javaType = String.class;
		JavaType type = new JavaType(typeRepository(), javaType);
		assertEquals(javaType.getName().hashCode(), type.hashCode());
	}

	@Test
	public void test_hashCode_2() {
		String javaType = String.class.getName();
		JavaType type = new JavaType(typeRepository(), javaType);
		assertEquals(javaType.hashCode(), type.hashCode());
	}

	@Test
	public void test_isAssignableTo_1() {
		JavaType type1 = new JavaType(typeRepository(), Vector.class);
		JavaType type2 = new JavaType(typeRepository(), Collection.class);
		assertTrue(type1.isAssignableTo(type2));
	}

	@Test
	public void test_isAssignableTo_2() {
		JavaType type1 = new JavaType(typeRepository(), Collection.class);
		JavaType type2 = new JavaType(typeRepository(), Vector.class);
		assertFalse(type1.isAssignableTo(type2));
	}

	@Test
	public void test_isResolvable_1() {
		JavaType type = new JavaType(typeRepository(), String.class);
		assertTrue(type.isResolvable());
	}

	@Test
	public void test_isResolvable_2() {
		JavaType type = new JavaType(typeRepository(), String.class.getName());
		assertFalse(type.isResolvable());
	}

	private ITypeRepository typeRepository() {
		return new ITypeRepository() {
			public IType getEnumType(String enumTypeName) {
				return null;
			}
			public IType getType(Class<?> type) {
				return null;
			}
			public IType getType(String typeName) {
				return null;
			}
		};
	}
}