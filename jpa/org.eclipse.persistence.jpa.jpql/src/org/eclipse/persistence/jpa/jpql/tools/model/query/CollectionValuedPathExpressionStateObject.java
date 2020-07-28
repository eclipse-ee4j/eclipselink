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

import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.tools.TypeHelper;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * A collection-valued field is designated by the name of an association field in a one-to-many or a
 * many-to-many relationship or by the name of an element collection field. The type of a
 * collection-valued field is a collection of values of the abstract schema type of the related
 * entity or element type.
 *
 * <div><b>BNF:</b> <code>collection_valued_path_expression ::= general_identification_variable.{single_valued_object_field.}*collection_valued_field</code><p></div>
 *
 * @see CollectionValuedPathExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class CollectionValuedPathExpressionStateObject extends AbstractPathExpressionStateObject {

    /**
     * Creates a new <code>CollectionValuedPathExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public CollectionValuedPathExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>CollectionValuedPathExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param path The collection-valued path expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public CollectionValuedPathExpressionStateObject(StateObject parent, String path) {
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
    public CollectionValuedPathExpression getExpression() {
        return (CollectionValuedPathExpression) super.getExpression();
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
        // Example: SELECT e.employees. FROM Employee e where employees is a collection,
        // it cannot be traversed
        if (typeHelper.isCollectionType(type)) {
            return null;
        }

        // Primitive types cannot have a managed type
        if (typeHelper.isPrimitiveType(type)) {
            return null;
        }

        // Retrieve the corresponding managed type for the mapping's type
        return getManagedTypeProvider().getManagedType(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType resolveType() {

        TypeHelper typeHelper = getTypeHelper();
        ITypeDeclaration typeDeclaration = getTypeDeclaration();
        IType type = typeDeclaration.getType();

        // For a collection type, return the first type parameter
        if (typeHelper.isCollectionType(type)) {

            ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();

            if (typeParameters.length > 0) {
                type = typeParameters[0].getType();
            }
        }
        // For a map type, by default the value is the actual type to return
        else if (typeHelper.isMapType(type)) {

            ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();

            if (typeParameters.length == 2) {
                type = typeParameters[1].getType();
            }
        }

        // A collection-valued path expression should not reference a primitive,
        // however, in an invalid query, this could potentially happen and the API
        // only deals with the primitive wrapper type
        return typeHelper.convertPrimitive(type);
    }

    /**
     * Keeps a reference of the {@link CollectionValuedPathExpression parsed object} object, which
     * should only be done when this object is instantiated during the conversion of a parsed JPQL
     * query into {@link StateObject StateObjects}.
     *
     * @param expression The {@link CollectionValuedPathExpression parsed object} representing a
     * collection-valued path expression
     */
    public void setExpression(CollectionValuedPathExpression expression) {
        super.setExpression(expression);
    }
}
