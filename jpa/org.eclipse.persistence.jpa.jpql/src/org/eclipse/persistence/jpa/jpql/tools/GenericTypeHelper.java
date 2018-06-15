/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools;

import org.eclipse.persistence.jpa.jpql.ITypeHelper;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

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
     * {@inheritDoc}
     */
    @Override
    public IType bigDecimal() {
        return delegate.bigDecimal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType bigInteger() {
        return delegate.bigInteger();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType booleanType() {
        return delegate.booleanType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType byteType() {
        return delegate.byteType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType characterType() {
        return delegate.characterType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType collectionType() {
        return delegate.collectionType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType convertPrimitive(Object type) {
        return delegate.convertPrimitive((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType dateType() {
        return delegate.dateType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType doubleType() {
        return delegate.doubleType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType enumType() {
        return delegate.enumType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType floatType() {
        return delegate.floatType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType getType(Class<?> type) {
        return delegate.getType(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType getType(String typeName) {
        return delegate.getType(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType integerType() {
        return delegate.integerType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBooleanType(Object type) {
        return delegate.isBooleanType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCollectionType(Object type) {
        return delegate.isCollectionType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDateType(Object type) {
        return delegate.isDateType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnumType(Object type) {
        return delegate.isEnumType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFloatingType(Object type) {
        return delegate.isFloatingType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIntegralType(Object type) {
        return delegate.isIntegralType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMapType(Object type) {
        return delegate.isMapType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNumericType(Object type) {
        return delegate.isNumericType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isObjectType(Object type) {
        return delegate.isObjectType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrimitiveType(Object type) {
        return delegate.isPrimitiveType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStringType(Object type) {
        return delegate.isStringType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType longType() {
        return delegate.longType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType longType(Object type) {
        return delegate.longType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType mapType() {
        return delegate.mapType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType numberType() {
        return delegate.numberType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType objectType() {
        return delegate.objectType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITypeDeclaration objectTypeDeclaration() {
        return delegate.objectTypeDeclaration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType primitiveBoolean() {
        return delegate.primitiveBoolean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType primitiveByte() {
        return delegate.primitiveByte();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType primitiveChar() {
        return delegate.primitiveChar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType primitiveDouble() {
        return delegate.primitiveDouble();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType primitiveFloat() {
        return delegate.primitiveFloat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType primitiveInteger() {
        return delegate.primitiveInteger();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType primitiveLong() {
        return delegate.primitiveLong();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType primitiveShort() {
        return delegate.primitiveShort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType shortType() {
        return delegate.shortType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType stringType() {
        return delegate.stringType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType timestampType() {
        return delegate.timestampType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType toBooleanType(Object type) {
        return delegate.toBooleanType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType toByteType(Object type) {
        return delegate.toByteType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType toDoubleType(Object type) {
        return delegate.toDoubleType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType toFloatType(Object type) {
        return delegate.toFloatType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType toIntegerType(Object type) {
        return delegate.toIntegerType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType toShortType(Object type) {
        return delegate.toShortType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType unknownType() {
        return delegate.unknownType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITypeDeclaration unknownTypeDeclaration() {
        return delegate.unknownTypeDeclaration();
    }
}
