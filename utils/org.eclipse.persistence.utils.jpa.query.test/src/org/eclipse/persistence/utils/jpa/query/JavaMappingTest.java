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

import javax.persistence.Basic;
import javax.persistence.Version;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;
import org.eclipse.persistence.utils.jpa.query.spi.IMappingType;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link JavaMapping}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JavaMappingTest extends AbstractJPQLQueryTest {

	private String mappingTypeToMappingName(IMappingType mappingType) {

		String name = mappingType.name().toLowerCase();

		// transient is a Java keyword
		if (mappingType == IMappingType.TRANSIENT) {
			name += "_";
		}

		return name;
	}

	@Test
	public void test_getMappingType() throws Exception {
		IManagedType parent = persistenceUnit().getManagedType("MappingType");
		assertNotNull(parent);

		for (IMappingType mappingType : IMappingType.values()) {

			// @BasicCollection and @BasicMap are deprecated
			// @Id is tested after this
			// @Transient is not a mapping
			if (mappingType != IMappingType.BASIC_COLLECTION    &&
			    mappingType != IMappingType.BASIC_MAP           &&
			    mappingType != IMappingType.ID                  &&
			    mappingType != IMappingType.TRANSIENT)
			{
				testMappingType(parent, mappingType);
			}
		}

		// Test @Id
		parent = persistenceUnit().getManagedType("MappingType$Something");
		assertNotNull(parent);
		testMappingType(parent, IMappingType.ID);
	}

	@Test
	public void test_getName() throws Exception {
		String name = "version";
		IManagedType parent = persistenceUnit().getManagedType("MappingType");
		IMapping mapping = parent.getMappingNamed(name);
		assertNotNull(mapping);
		assertEquals(name, mapping.getName());
	}

	@Test
	public void test_getParent() throws Exception {
		IManagedType parent = persistenceUnit().getManagedType("MappingType");
		IMapping mapping = parent.getMappingNamed("version");
		assertNotNull(mapping);
		assertSame(parent, mapping.getParent());
	}

	@Test
	public void test_getTypeDeclaration_1() throws Exception {
		IManagedType parent = persistenceUnit().getManagedType("MappingType");
		assertNotNull(parent);

		IMapping mapping = parent.getMappingNamed("element_collection");
		assertNotNull(mapping);

		ITypeDeclaration typeDeclaration = mapping.getTypeDeclaration();
		assertNotNull(typeDeclaration);

		IType indirectList = parent.getProvider().getTypeRepository().getType(IndirectList.class);
		assertNotNull(indirectList);
		assertEquals(indirectList, typeDeclaration.getType());

		ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();
		assertNotNull(typeParameters);
		assertEquals(1, typeParameters.length);

		ITypeDeclaration typeParameter = typeParameters[0];
		assertNotNull(typeParameter);

		IType stringType = parent.getProvider().getTypeRepository().getType(String.class);
		assertEquals(stringType, typeParameter.getType());
	}

	@Test
	public void test_getTypeDeclaration_2() throws Exception {
		IManagedType parent = persistenceUnit().getManagedType("MappingType");
		assertNotNull(parent);

		IMapping mapping = parent.getMappingNamed("basic");
		assertNotNull(mapping);

		ITypeDeclaration typeDeclaration = mapping.getTypeDeclaration();
		assertNotNull(typeDeclaration);

		ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();
		assertNotNull(typeParameters);
		assertEquals(0, typeParameters.length);
	}

	@Test
	public void test_hasAnnotation_1() throws Exception {
		IManagedType parent = persistenceUnit().getManagedType("MappingType");
		IMapping mapping = parent.getMappingNamed("version");
		assertNotNull(mapping);
		assertTrue(mapping.hasAnnotation(Version.class));
	}

	@Test
	public void test_hasAnnotation_2() throws Exception {
		IManagedType parent = persistenceUnit().getManagedType("MappingType");
		IMapping mapping = parent.getMappingNamed("version");
		assertNotNull(mapping);
		assertFalse(mapping.hasAnnotation(Basic.class));
	}

	private void testMappingType(IManagedType parent, IMappingType mappingType) {
		String name = mappingTypeToMappingName(mappingType);
		IMapping mapping = parent.getMappingNamed(name);
		assertNotNull(mapping);
		assertSame(mappingType, mapping.getMappingType());
	}
}