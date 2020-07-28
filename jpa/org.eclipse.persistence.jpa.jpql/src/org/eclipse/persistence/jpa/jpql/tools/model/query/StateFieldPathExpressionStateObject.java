/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.tools.TypeHelper;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.tools.utility.iterable.EmptyIterable;

/**
 * A single-valued association field is designated by the name of an association-field in a
 * one-to-one or many-to-one relationship. The type of a single-valued association field and thus a
 * single-valued association path expression is the abstract schema type of the related entity.
 *
 * <div><b>BNF:</b> <code>state_field_path_expression ::= {identification_variable | single_valued_association_path_expression}.state_field</code><p></div>
 *
 * <div><b>BNF:</b> <code>single_valued_association_path_expression ::= identification_variable.{single_valued_association_field.}*single_valued_association_field</code><p></div>
 *
 * @see StateFieldPathExpression
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public class StateFieldPathExpressionStateObject extends AbstractPathExpressionStateObject {

    /**
     * Creates a new <code>StateFieldPathExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public StateFieldPathExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>StateFieldPathExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param path The state field path expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public StateFieldPathExpressionStateObject(StateObject parent, String path) {
        super(parent, path);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StateFieldPathExpression getExpression() {
        return (StateFieldPathExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IManagedType resolveManagedType() {

        IMapping mapping = getMapping();

        if (mapping == null) {
            return null;
        }

        TypeHelper typeHelper = getTypeHelper();
        ITypeDeclaration typeDeclaration = mapping.getTypeDeclaration();
        IType type = typeDeclaration.getType();

        // Collection type cannot be traversed
        if (typeHelper.isCollectionType(type)) {

            ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();

            if (typeParameters.length == 0) {
                return null;
            }

            type = typeParameters[0].getType();
        }
        // Wrap the Map into a virtual IManagedType so it can be returned and the
        // IType for the Map can be used to retrieve the type of the key and value
        else if (typeHelper.isMapType(type)) {
            return new MapManagedType(getManagedTypeProvider(), type);
        }

        // Retrieve the corresponding managed type for the mapping's type
        return getManagedTypeProvider().getManagedType(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType resolveType() {
        return getTypeHelper().convertPrimitive(getTypeDeclaration().getType());
    }

    /**
     * Keeps a reference of the {@link StateFieldPathExpression parsed object} object, which
     * should only be done when this object is instantiated during the conversion of a parsed JPQL
     * query into {@link StateObject StateObjects}.
     *
     * @param expression The {@link StateFieldPathExpression parsed object} representing a state
     * field path expression
     */
    public void setExpression(StateFieldPathExpression expression) {
        super.setExpression(expression);
    }

    protected static class MapManagedType implements IManagedType {

        protected final IType mapType;
        protected final IManagedTypeProvider provider;

        protected MapManagedType(IManagedTypeProvider provider, IType mapType) {
            super();
            this.mapType  = mapType;
            this.provider = provider;
        }

        /**
         * {@inheritDoc}
         */
        public void accept(IManagedTypeVisitor visitor) {
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo(IManagedType managedType) {
            return getType().getName().compareTo(managedType.getType().getName());
        }

        /**
         * {@inheritDoc}
         */
        public IMapping getMappingNamed(String name) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public IManagedTypeProvider getProvider() {
            return provider;
        }

        /**
         * {@inheritDoc}
         */
        public IType getType() {
            return mapType;
        }

        /**
         * {@inheritDoc}
         */
        public Iterable<IMapping> mappings() {
            return EmptyIterable.instance();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return getType().getName();
        }
    }
}
