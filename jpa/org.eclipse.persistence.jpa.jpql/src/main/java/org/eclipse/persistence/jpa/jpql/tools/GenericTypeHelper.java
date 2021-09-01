/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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

    @Override
    public IType bigDecimal() {
        return delegate.bigDecimal();
    }

    @Override
    public IType bigInteger() {
        return delegate.bigInteger();
    }

    @Override
    public IType booleanType() {
        return delegate.booleanType();
    }

    @Override
    public IType byteType() {
        return delegate.byteType();
    }

    @Override
    public IType characterType() {
        return delegate.characterType();
    }

    @Override
    public IType collectionType() {
        return delegate.collectionType();
    }

    @Override
    public IType convertPrimitive(Object type) {
        return delegate.convertPrimitive((IType) type);
    }

    @Override
    public IType dateType() {
        return delegate.dateType();
    }

    @Override
    public IType doubleType() {
        return delegate.doubleType();
    }

    @Override
    public IType enumType() {
        return delegate.enumType();
    }

    @Override
    public IType floatType() {
        return delegate.floatType();
    }

    @Override
    public IType getType(Class<?> type) {
        return delegate.getType(type);
    }

    @Override
    public IType getType(String typeName) {
        return delegate.getType(typeName);
    }

    @Override
    public IType integerType() {
        return delegate.integerType();
    }

    @Override
    public boolean isBooleanType(Object type) {
        return delegate.isBooleanType((IType) type);
    }

    @Override
    public boolean isCollectionType(Object type) {
        return delegate.isCollectionType((IType) type);
    }

    @Override
    public boolean isDateType(Object type) {
        return delegate.isDateType((IType) type);
    }

    @Override
    public boolean isEnumType(Object type) {
        return delegate.isEnumType((IType) type);
    }

    @Override
    public boolean isFloatingType(Object type) {
        return delegate.isFloatingType((IType) type);
    }

    @Override
    public boolean isIntegralType(Object type) {
        return delegate.isIntegralType((IType) type);
    }

    @Override
    public boolean isMapType(Object type) {
        return delegate.isMapType((IType) type);
    }

    @Override
    public boolean isNumericType(Object type) {
        return delegate.isNumericType((IType) type);
    }

    @Override
    public boolean isObjectType(Object type) {
        return delegate.isObjectType((IType) type);
    }

    @Override
    public boolean isPrimitiveType(Object type) {
        return delegate.isPrimitiveType((IType) type);
    }

    @Override
    public boolean isStringType(Object type) {
        return delegate.isStringType((IType) type);
    }

    @Override
    public IType longType() {
        return delegate.longType();
    }

    @Override
    public IType longType(Object type) {
        return delegate.longType((IType) type);
    }

    @Override
    public IType mapType() {
        return delegate.mapType();
    }

    @Override
    public IType numberType() {
        return delegate.numberType();
    }

    @Override
    public IType objectType() {
        return delegate.objectType();
    }

    @Override
    public ITypeDeclaration objectTypeDeclaration() {
        return delegate.objectTypeDeclaration();
    }

    @Override
    public IType primitiveBoolean() {
        return delegate.primitiveBoolean();
    }

    @Override
    public IType primitiveByte() {
        return delegate.primitiveByte();
    }

    @Override
    public IType primitiveChar() {
        return delegate.primitiveChar();
    }

    @Override
    public IType primitiveDouble() {
        return delegate.primitiveDouble();
    }

    @Override
    public IType primitiveFloat() {
        return delegate.primitiveFloat();
    }

    @Override
    public IType primitiveInteger() {
        return delegate.primitiveInteger();
    }

    @Override
    public IType primitiveLong() {
        return delegate.primitiveLong();
    }

    @Override
    public IType primitiveShort() {
        return delegate.primitiveShort();
    }

    @Override
    public IType shortType() {
        return delegate.shortType();
    }

    @Override
    public IType stringType() {
        return delegate.stringType();
    }

    @Override
    public IType timestampType() {
        return delegate.timestampType();
    }

    @Override
    public IType toBooleanType(Object type) {
        return delegate.toBooleanType((IType) type);
    }

    @Override
    public IType toByteType(Object type) {
        return delegate.toByteType((IType) type);
    }

    @Override
    public IType toDoubleType(Object type) {
        return delegate.toDoubleType((IType) type);
    }

    @Override
    public IType toFloatType(Object type) {
        return delegate.toFloatType((IType) type);
    }

    @Override
    public IType toIntegerType(Object type) {
        return delegate.toIntegerType((IType) type);
    }

    @Override
    public IType toShortType(Object type) {
        return delegate.toShortType((IType) type);
    }

    @Override
    public IType unknownType() {
        return delegate.unknownType();
    }

    @Override
    public ITypeDeclaration unknownTypeDeclaration() {
        return delegate.unknownTypeDeclaration();
    }
}
