/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;

/**
 * The abstract implementation of a {@link TypeResolver} that has a parent.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
abstract class AbstractTypeResolver implements TypeResolver
{
	/**
	 * The parent of this resolver, which is never <code>null</code>.
	 */
	private final TypeResolver parent;

	/**
	 * Creates a new <code>AbstractTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 */
	AbstractTypeResolver(TypeResolver parent)
	{
		super();
		this.parent = parent;
	}

	/**
	 * Retrieves the {@link IType} for {@link BigDecimal}.
	 *
	 * @return The external form of the <code>BigDecimal</code> class
	 */
	final IType bigDecimal()
	{
		return getType(BigDecimal.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link BigInteger}.
	 *
	 * @return The external form of the <code>BigInteger</code> class
	 */
	final IType bigInteger()
	{
		return getType(BigInteger.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Collection}.
	 *
	 * @return The external form of the <code>Collection</code> class
	 */
	final IType collectionType()
	{
		return getType(Collection.class);
	}

	/**
	 * Converts the given {@link IType}, if it's representing a primitive type,
	 * into the class of the same type.
	 *
	 * @param type Type to possibly convert from the primitive into the class
	 * @return The given {@link IType} if it's not a primitive type otherwise
	 * the primitive type will have been converted into the class of that
	 * primitive
	 */
	final IType convertPrimitive(IType type)
	{
		type = doubleType(type);
		type = floatType(type);
		type = integerType(type);
		type = longType(type);
		return type;
	}

	/**
	 * Retrieves the {@link IType} for {@link Date}.
	 *
	 * @return The external form of the <code>Date</code> class
	 */
	final IType dateType()
	{
		return getType(Date.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Double}.
	 *
	 * @return The external form of the <code>Double</code> class
	 */
	final IType doubleType()
	{
		return getType(Double.class);
	}

	/**
	 * Converts the given {@link IType}, if it's the primitive double, into the
	 * <code>Double</code> type.
	 *
	 * @param type The {@link IType} to possibly convert
	 * @return The given type if it's not the primitive double or the {@link IType}
	 * for the class <code>Double</code>
	 */
	final IType doubleType(IType type)
	{
		if (type.equals(primitiveDouble()))
		{
			return doubleType();
		}

		return type;
	}

	/**
	 * Retrieves the {@link IType} for {@link Float}.
	 *
	 * @return The external form of the <code>Float</code> class
	 */
	final IType floatType()
	{
		return getType(Float.class);
	}

	/**
	 * Converts the given {@link IType}, if it's the primitive float, into the
	 * <code>Float</code> type.
	 *
	 * @param type The {@link IType} to possibly convert
	 * @return The given type if it's not the primitive float or the {@link IType}
	 * for the class <code>Float</code>
	 */
	final IType floatType(IType type)
	{
		if (type.equals(primitiveFloat()))
		{
			return floatType();
		}

		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType()
	{
		return parent.getManagedType();
	}

	/**
	 * Returns the managed type of the parent resolver.
	 *
	 * @return The managed type of the parent resolver
	 */
	final IManagedType getParentManagedType()
	{
		return parent.getManagedType();
	}

	/**
	 * Returns the type of the parent resolver.
	 *
	 * @return The type of the parent resolver
	 */
	final IType getParentType()
	{
		return parent.getType();
	}

	/**
	 * Returns the type declaration of the parent resolver.
	 *
	 * @return The type declaration of the parent resolver
	 */
	final ITypeDeclaration getParentTypeDeclaration()
	{
		return parent.getTypeDeclaration();
	}

	/**
	 * Returns the provider of managed types.
	 *
	 * @return The container holding the managed types
	 */
	final IManagedTypeProvider getProvider()
	{
		return getQuery().getProvider();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IQuery getQuery()
	{
		return parent.getQuery();
	}

	/**
	 * Returns the {@link IType} of the given Java type.
	 *
	 * @param type The Java type for which its external form will be returned
	 * @return The {@link IType} representing the given Java type
	 */
	final IType getType(Class<?> type)
	{
		return getTypeRepository().getType(type);
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	final IType getType(String typeName)
	{
		return getTypeRepository().getType(typeName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration getTypeDeclaration()
	{
		return getType().getTypeDeclaration();
	}

	/**
	 * Returns the class repository, which gives access to the application's
	 * classes.
	 *
	 * @return The external form of the class repository
	 */
	final ITypeRepository getTypeRepository()
	{
		return getQuery().getProvider().getTypeRepository();
	}

	/**
	 * Retrieves the {@link IType} for {@link Integer}.
	 *
	 * @return The external form of the <code>Integer</code> class
	 */
	final IType integerType()
	{
		return getType(Integer.class);
	}

	/**
	 * Converts the given {@link IType}, if it's the primitive int, into the
	 * <code>Integer</code> type.
	 *
	 * @param type The {@link IType} to possibly convert
	 * @return The given type if it's not the primitive int or the {@link IType}
	 * for the class <code>Integer</code>
	 */
	final IType integerType(IType type)
	{
		if (type.equals(primitiveInteger()))
		{
			return integerType();
		}

		return type;
	}

	/**
	 * Determines whether the given {@link IType} is an instance of {@link Collection}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is an instance of
	 * {@link Collection}, <code>false</code> otherwise
	 */
	final boolean isCollectionType(IType type)
	{
		return type.isAssignableTo(collectionType());
	}

	/**
	 * Determines whether the given {@link IType} is an instance of a floating
	 * type, which is either <code>Float</code>, <code>Double</code>, float or
	 * double.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is a floating type,
	 * <code>false</code> otherwise
	 */
	final boolean isFloatingType(IType type)
	{
		return type.equals(floatType())      ||
		       type.equals(doubleType())     ||
		       type.equals(primitiveFloat()) ||
		       type.equals(primitiveDouble());
	}

	/**
	 * Determines whether the given {@link IType} is an instance of a floating
	 * type, which is either <code>Integer</code>, <code>Long</code>, int or
	 * float.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is a integral type,
	 * <code>false</code> otherwise
	 */
	final boolean isIntegralType(IType type)
	{
		return type.equals(integerType())      ||
		       type.equals(longType())         ||
		       type.equals(primitiveInteger()) ||
		       type.equals(primitiveLong());
	}

	/**
	 * Determines whether the given {@link IType} is an instance of {@link Map}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is an instance of
	 * {@link Map}, <code>false</code> otherwise
	 */
	final boolean isMapType(IType type)
	{
		return type.isAssignableTo(mapType());
	}

	/**
	 * Determines whether the given {@link IType} represents the <code>String</code>
	 * class.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} represents the
	 * <code>String</code> class, <code>false</code> otherwise
	 */
	final boolean isStringType(IType type)
	{
		return type.equals(stringType());
	}

	/**
	 * Retrieves the {@link IType} for {@link Long}.
	 *
	 * @return The external form of the <code>Long</code> class
	 */
	final IType longType()
	{
		return getType(Long.class);
	}

	/**
	 * Converts the given {@link IType}, if it's the primitive long, into the
	 * <code>Long</code> type.
	 *
	 * @param type The {@link IType} to possibly convert
	 * @return The given type if it's not the primitive long or the {@link IType}
	 * for the class <code>Long</code>
	 */
	final IType longType(IType type)
	{
		if (type.equals(primitiveLong()))
		{
			return longType();
		}

		return type;
	}

	/**
	 * Retrieves the {@link IType} for {@link Map}.
	 *
	 * @return The external form of the <code>Map</code> class
	 */
	final IType mapType()
	{
		return getType(Map.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Number}.
	 *
	 * @return The external form of the <code>Number</code> class
	 */
	final IType numberType()
	{
		return getType(Number.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Object}.
	 *
	 * @return The external form of the <code>Object</code> class
	 */
	final IType objectType()
	{
		return getType(Object.class);
	}

	/**
	 * Returns the {@link ITypeDeclaration} for the {@link IType} representing
	 * the <code>Object</code> class.
	 *
	 * @return The {@link ITypeDeclaration} of the <code>Object</code> class
	 */
	final ITypeDeclaration objectTypeDeclaration()
	{
		return objectType().getTypeDeclaration();
	}

	/**
	 * Retrieves the {@link IType} for the primitive double.
	 *
	 * @return The external form of the primitive double
	 */
	final IType primitiveDouble()
	{
		return getType(double.class);
	}

	/**
	 * Retrieves the {@link IType} for the primitive float.
	 *
	 * @return The external form of the primitive float
	 */
	final IType primitiveFloat()
	{
		return getType(float.class);
	}

	/**
	 * Retrieves the {@link IType} for the primitive int.
	 *
	 * @return The external form of the primitive int
	 */
	final IType primitiveInteger()
	{
		return getType(int.class);
	}

	/**
	 * Retrieves the {@link IType} for the primitive long.
	 *
	 * @return The external form of the primitive long
	 */
	final IType primitiveLong()
	{
		return getType(long.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(IType type)
	{
		return parent.resolveManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(String abstractSchemaName)
	{
		return parent.resolveManagedType(abstractSchemaName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType resolveType(String variableName)
	{
		return parent.resolveType(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration resolveTypeDeclaration(String variableName)
	{
		return parent.resolveTypeDeclaration(variableName);
	}

	/**
	 * Retrieves the {@link IType} for {@link String}.
	 *
	 * @return The external form of the <code>String</code> class
	 */
	final IType stringType()
	{
		return getType(String.class);
	}
}