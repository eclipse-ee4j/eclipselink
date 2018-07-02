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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.tools.model.Problem;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.utility.iterable.SnapshotCloneIterable;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ListIterable;
import static org.eclipse.persistence.jpa.jpql.ExpressionTools.*;
import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This state object represents the abstract definition of a <code><b>FROM</b></code> clause, which
 * is either the <code>FROM</code> clause of the query or of a sub-query expression.
 *
 * @see AbstractSelectStatementStateObject
 * @see FromClauseStateObject
 *
 * @see AbstractFromClause
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public abstract class AbstractFromClauseStateObject extends AbstractListHolderStateObject<VariableDeclarationStateObject>
                                                    implements DeclarationStateObject {

    /**
     * Notifies the content of the list of {@link VariableDeclarationStateObject} has changed.
     */
    public static final String VARIABLE_DECLARATIONS_LIST = "variableDeclarations";

    /**
     * Creates a new <code>AbstractFromClauseStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    protected AbstractFromClauseStateObject(AbstractSelectStatementStateObject parent) {
        super(parent);
    }

    /**
     * Adds a new collection declaration to the <code><b>FROM</b></code> clause.
     *
     * @return The {@link CollectionMemberDeclarationStateObject} representing the collection declaration
     */
    public CollectionMemberDeclarationStateObject addCollectionDeclaration() {
        CollectionMemberDeclarationStateObject stateObject = new CollectionMemberDeclarationStateObject(this);
        addItem(stateObject);
        return stateObject;
    }

    /**
     * Adds a new collection declaration to the <code><b>FROM</b></code> clause.
     *
     * @param collectionValuedPath The collection-valued path expression
     * @param identificationVariable The variable defining the collection-valued path expression
     * @return The {@link CollectionMemberDeclarationStateObject} representing the collection
     * declaration
     */
    public CollectionMemberDeclarationStateObject addCollectionDeclaration(String collectionValuedPath,
                                                                           String identificationVariable) {

        CollectionMemberDeclarationStateObject stateObject = new CollectionMemberDeclarationStateObject(this);
        stateObject.setPath(collectionValuedPath);
        stateObject.setIdentificationVariable(identificationVariable);

        addItem(stateObject);
        return stateObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addProblems(List<Problem> currentProblems) {
        super.addProblems(currentProblems);
        // TODO
    }

    /**
     * Adds a new range variable declaration to the <code><b>FROM</b></code> clause.
     *
     * @return The {@link StateObject} representing the range variable declaration
     */
    public IdentificationVariableDeclarationStateObject addRangeDeclaration() {
        IdentificationVariableDeclarationStateObject stateObject = new IdentificationVariableDeclarationStateObject(this);
        addItem(stateObject);
        return stateObject;
    }

    /**
     * Adds to this select statement a new range variable declaration.
     *
     * @param entity The external form of the entity to add to the declaration list
     * @param identificationVariable The unique identifier identifying the abstract schema name
     * @return The state object of the new declaration
     */
    public IdentificationVariableDeclarationStateObject addRangeDeclaration(IEntity entity,
                                                                            String identificationVariable) {

        IdentificationVariableDeclarationStateObject stateObject = new IdentificationVariableDeclarationStateObject(
            this,
            entity,
            identificationVariable
        );

        addItem(stateObject);
        return stateObject;
    }

    /**
     * Adds a new range variable declaration.
     *
     * @param entityName The name of the entity name
     * @param identificationVariable The new identification variable
     * @return The state object of the new declaration
     */
    public IdentificationVariableDeclarationStateObject addRangeDeclaration(String entityName,
                                                                            String identificationVariable) {

        IdentificationVariableDeclarationStateObject stateObject = new IdentificationVariableDeclarationStateObject(
            this,
            entityName,
            identificationVariable
        );

        addItem(stateObject);
        return stateObject;
    }

    /**
     * Returns the BNF of the declaration part of this clause.
     *
     * @return The BNF of the declaration part of this clause
     */
    protected abstract String declarationBNF();

    /**
     * {@inheritDoc}
     */
    public ListIterable<? extends VariableDeclarationStateObject> declarations() {
        return items();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IdentificationVariableStateObject findIdentificationVariable(String variable) {

        for (IdentificationVariableStateObject identificationVariable : identificationVariables()) {
            if (stringsAreEqualIgnoreCase(identificationVariable.getText(), variable)) {
                return identificationVariable;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeclarationStateObject getDeclaration() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractFromClause getExpression() {
        return (AbstractFromClause) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    public IManagedType getManagedType(StateObject stateObject) {

        for (VariableDeclarationStateObject declaration : declarations()) {

            IManagedType managedType = declaration.getManagedType(stateObject);

            if (managedType != null) {
                return managedType;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSelectStatementStateObject getParent() {
        return (AbstractSelectStatementStateObject) super.getParent();
    }

    /**
     * Returns the {@link IdentificationVariableStateObject IdentificationVariableStateObjects}
     * holding onto the identification variables, which are the variables defined in the
     * <code><b>FROM</b></code> clause.
     * <p>
     * Example:
     * <ul>
     * <li><code>Employee e</code>; <i>e</i> is returned</li>
     * <li><code>IN (e.employees) AS emps</code>; <i>emps</i> is returned</li>
     * <li><code>Manager m JOIN m.employees emps</code>; <i>m</i> and <i>emps</i> are returned</li>
     * </ul>
     *
     * @return The list of {@link IdentificationVariableStateObject IdentificationVariableStateObjects}
     */
    public Iterable<IdentificationVariableStateObject> identificationVariables() {

        List<IdentificationVariableStateObject> stateObjects = new ArrayList<IdentificationVariableStateObject>();

        for (VariableDeclarationStateObject stateObject : items()) {
            CollectionTools.addAll(stateObjects, stateObject.identificationVariables());
        }

        return new SnapshotCloneIterable<IdentificationVariableStateObject>(stateObjects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEquivalent(StateObject stateObject) {
        return super.isEquivalent(stateObject) &&
               areChildrenEquivalent((AbstractListHolderStateObject<?>) stateObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String listName() {
        return VARIABLE_DECLARATIONS_LIST;
    }

    /**
     * Parses the given JPQL fragment and create the select item. For the top-level query, the
     * fragment can contain several select items but for a subquery, it can represent only one.
     *
     * @param jpqlFragment The portion of the query representing one or several select items
     */
    public void parse(String jpqlFragment) {
        // No need to add the items, they are automatically added by the builder
        buildStateObjects(jpqlFragment, declarationBNF());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toTextInternal(Appendable writer) throws IOException {
        writer.append(FROM);
        writer.append(SPACE);
        toStringItems(writer, true);
    }
}
