/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This generic implementation of {@link ITypeHelper} wraps {@link ITypeHelper} and delegates the
 * calls to it.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class GenericTypeHelper implements ITypeHelper {

	/**
	 * The {@link TypeHelper} that is wrapped by this one and all the calls are delegated to it.
	 */
	private final TypeHelper delegate;

	/**
	 * Creates a new <code>GenericTypeHelper</code>.
	 *
	 * @param delegate The {@link TypeHelper} that is wrapped by this one and all the calls are
	 * delegated to it
	 */
	public GenericTypeHelper(TypeHelper delegate) {
		super();
		this.delegate = delegate;
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType bigDecimal() {
		return delegate.bigDecimal();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType bigInteger() {
		return delegate.bigInteger();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType booleanType() {
		return delegate.booleanType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType byteType() {
		return delegate.byteType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType characterType() {
		return delegate.characterType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType collectionType() {
		return delegate.collectionType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType convertPrimitive(Object type) {
		return delegate.convertPrimitive((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType dateType() {
		return delegate.dateType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType doubleType() {
		return delegate.doubleType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType enumType() {
		return delegate.enumType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType floatType() {
		return delegate.floatType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType getType(Class<?> type) {
		return delegate.getType(type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType getType(String typeName) {
		return delegate.getType(typeName);
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType integerType() {
		return delegate.integerType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public boolean isBooleanType(Object type) {
		return delegate.isBooleanType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public boolean isCollectionType(Object type) {
		return delegate.isCollectionType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public boolean isDateType(Object type) {
		return delegate.isDateType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public boolean isEnumType(Object type) {
		return delegate.isEnumType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public boolean isFloatingType(Object type) {
		return delegate.isFloatingType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public boolean isIntegralType(Object type) {
		return delegate.isIntegralType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public boolean isMapType(Object type) {
		return delegate.isMapType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public boolean isNumericType(Object type) {
		return delegate.isNumericType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public boolean isObjectType(Object type) {
		return delegate.isObjectType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public boolean isPrimitiveType(Object type) {
		return delegate.isPrimitiveType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public boolean isStringType(Object type) {
		return delegate.isStringType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType longType() {
		return delegate.longType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType longType(Object type) {
		return delegate.longType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType mapType() {
		return delegate.mapType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType numberType() {
		return delegate.numberType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType objectType() {
		return delegate.objectType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public ITypeDeclaration objectTypeDeclaration() {
		return delegate.objectTypeDeclaration();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType primitiveBoolean() {
		return delegate.primitiveBoolean();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType primitiveByte() {
		return delegate.primitiveByte();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType primitiveChar() {
		return delegate.primitiveChar();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType primitiveDouble() {
		return delegate.primitiveDouble();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType primitiveFloat() {
		return delegate.primitiveFloat();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType primitiveInteger() {
		return delegate.primitiveInteger();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType primitiveLong() {
		return delegate.primitiveLong();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType primitiveShort() {
		return delegate.primitiveShort();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType shortType() {
		return delegate.shortType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType stringType() {
		return delegate.stringType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType timestampType() {
		return delegate.timestampType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType toBooleanType(Object type) {
		return delegate.toBooleanType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType toByteType(Object type) {
		return delegate.toByteType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType toDoubleType(Object type) {
		return delegate.toDoubleType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType toFloatType(Object type) {
		return delegate.toFloatType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType toIntegerType(Object type) {
		return delegate.toIntegerType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType toShortType(Object type) {
		return delegate.toShortType((IType) type);
	}

	/**
	 * {@InheritedDoc}
	 */
	public IType unknownType() {
		return delegate.unknownType();
	}

	/**
	 * {@InheritedDoc}
	 */
	public ITypeDeclaration unknownTypeDeclaration() {
		return delegate.unknownTypeDeclaration();
	}
}