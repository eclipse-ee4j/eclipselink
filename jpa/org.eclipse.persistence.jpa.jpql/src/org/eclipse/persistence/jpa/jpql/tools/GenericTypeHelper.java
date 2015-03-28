/*******************************************************************************
 * Copyright (c) 2012, 2015 Oracle and/or its affiliates. All rights reserved.
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
    public IType bigDecimal() {
        return delegate.bigDecimal();
    }

    /**
     * {@inheritDoc}
     */
    public IType bigInteger() {
        return delegate.bigInteger();
    }

    /**
     * {@inheritDoc}
     */
    public IType booleanType() {
        return delegate.booleanType();
    }

    /**
     * {@inheritDoc}
     */
    public IType byteType() {
        return delegate.byteType();
    }

    /**
     * {@inheritDoc}
     */
    public IType characterType() {
        return delegate.characterType();
    }

    /**
     * {@inheritDoc}
     */
    public IType collectionType() {
        return delegate.collectionType();
    }

    /**
     * {@inheritDoc}
     */
    public IType convertPrimitive(Object type) {
        return delegate.convertPrimitive((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public IType dateType() {
        return delegate.dateType();
    }

    /**
     * {@inheritDoc}
     */
    public IType doubleType() {
        return delegate.doubleType();
    }

    /**
     * {@inheritDoc}
     */
    public IType enumType() {
        return delegate.enumType();
    }

    /**
     * {@inheritDoc}
     */
    public IType floatType() {
        return delegate.floatType();
    }

    /**
     * {@inheritDoc}
     */
    public IType getType(Class<?> type) {
        return delegate.getType(type);
    }

    /**
     * {@inheritDoc}
     */
    public IType getType(String typeName) {
        return delegate.getType(typeName);
    }

    /**
     * {@inheritDoc}
     */
    public IType integerType() {
        return delegate.integerType();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBooleanType(Object type) {
        return delegate.isBooleanType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCollectionType(Object type) {
        return delegate.isCollectionType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDateType(Object type) {
        return delegate.isDateType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnumType(Object type) {
        return delegate.isEnumType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFloatingType(Object type) {
        return delegate.isFloatingType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isIntegralType(Object type) {
        return delegate.isIntegralType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMapType(Object type) {
        return delegate.isMapType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNumericType(Object type) {
        return delegate.isNumericType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isObjectType(Object type) {
        return delegate.isObjectType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPrimitiveType(Object type) {
        return delegate.isPrimitiveType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isStringType(Object type) {
        return delegate.isStringType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public IType longType() {
        return delegate.longType();
    }

    /**
     * {@inheritDoc}
     */
    public IType longType(Object type) {
        return delegate.longType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public IType mapType() {
        return delegate.mapType();
    }

    /**
     * {@inheritDoc}
     */
    public IType numberType() {
        return delegate.numberType();
    }

    /**
     * {@inheritDoc}
     */
    public IType objectType() {
        return delegate.objectType();
    }

    /**
     * {@inheritDoc}
     */
    public ITypeDeclaration objectTypeDeclaration() {
        return delegate.objectTypeDeclaration();
    }

    /**
     * {@inheritDoc}
     */
    public IType primitiveBoolean() {
        return delegate.primitiveBoolean();
    }

    /**
     * {@inheritDoc}
     */
    public IType primitiveByte() {
        return delegate.primitiveByte();
    }

    /**
     * {@inheritDoc}
     */
    public IType primitiveChar() {
        return delegate.primitiveChar();
    }

    /**
     * {@inheritDoc}
     */
    public IType primitiveDouble() {
        return delegate.primitiveDouble();
    }

    /**
     * {@inheritDoc}
     */
    public IType primitiveFloat() {
        return delegate.primitiveFloat();
    }

    /**
     * {@inheritDoc}
     */
    public IType primitiveInteger() {
        return delegate.primitiveInteger();
    }

    /**
     * {@inheritDoc}
     */
    public IType primitiveLong() {
        return delegate.primitiveLong();
    }

    /**
     * {@inheritDoc}
     */
    public IType primitiveShort() {
        return delegate.primitiveShort();
    }

    /**
     * {@inheritDoc}
     */
    public IType shortType() {
        return delegate.shortType();
    }

    /**
     * {@inheritDoc}
     */
    public IType stringType() {
        return delegate.stringType();
    }

    /**
     * {@inheritDoc}
     */
    public IType timestampType() {
        return delegate.timestampType();
    }

    /**
     * {@inheritDoc}
     */
    public IType toBooleanType(Object type) {
        return delegate.toBooleanType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public IType toByteType(Object type) {
        return delegate.toByteType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public IType toDoubleType(Object type) {
        return delegate.toDoubleType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public IType toFloatType(Object type) {
        return delegate.toFloatType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public IType toIntegerType(Object type) {
        return delegate.toIntegerType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public IType toShortType(Object type) {
        return delegate.toShortType((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    public IType unknownType() {
        return delegate.unknownType();
    }

    /**
     * {@inheritDoc}
     */
    public ITypeDeclaration unknownTypeDeclaration() {
        return delegate.unknownTypeDeclaration();
    }
}
