/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.JoinBNF;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ListIterable;
import org.eclipse.persistence.jpa.jpql.utility.iterable.SnapshotCloneListIterable;
import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public abstract class AbstractIdentificationVariableDeclarationStateObject extends AbstractListHolderStateObject<JoinStateObject>
                                                                           implements VariableDeclarationStateObject {

    /**
     * The state object of the range variable declaration.
     */
    private AbstractRangeVariableDeclarationStateObject rangeVariableDeclaration;

    /**
     * Notifies the content of the list of {@link JoinStateObject} has changed.
     */
    public static final String JOINS_LIST = "joins";

    /**
     * Creates a new <code>AbstractIdentificationVariableDeclarationStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    protected AbstractIdentificationVariableDeclarationStateObject(AbstractFromClauseStateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>AbstractIdentificationVariableDeclarationStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param root The "root" object
     * @param identificationVariable The identification variable defining the given path
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    protected AbstractIdentificationVariableDeclarationStateObject(AbstractFromClauseStateObject parent,
                                                                   String root,
                                                                   String identificationVariable) {
        super(parent);
        setRootPath(root);
        setIdentificationVariable(identificationVariable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildren(List<StateObject> children) {
        children.add(rangeVariableDeclaration);
        super.addChildren(children);
    }

    /**
     * Adds a new <code><b>INNER JOIN</b></code> expression to this declaration.
     *
     * @param path The join association path expression
     * @param identificationVariable The new variable defining the join association path
     * @return A new {@link JoinStateObject}
     */
    public JoinStateObject addInnerJoin(String path, String identificationVariable) {
        return addJoin(INNER_JOIN, path, identificationVariable);
    }

    /**
     * Adds a new <code><b>JOIN</b></code> expression to this declaration.
     *
     * @param joinType One of the joining types: <code><b>LEFT JOIN</b></code>, <code><b>LEFT OUTER
     * JOIN</b></code>, <code><b>INNER JOIN</b></code> or <code><b>JOIN</b></code>
     * @return A new {@link JoinStateObject}
     */
    public JoinStateObject addJoin(String joinType) {
        JoinStateObject stateObject = new JoinStateObject(this, joinType, false);
        addItem(stateObject);
        return stateObject;
    }

    /**
     * Adds a new <code><b>JOIN</b></code> expression to this declaration.
     *
     * @param joinType One of the joining types: <code><b>LEFT JOIN</b></code>, <code><b>LEFT OUTER
     * JOIN</b></code>, <code><b>INNER JOIN</b></code> or <code><b>JOIN</b></code>
     * @param paths The join association path expression
     * @param identificationVariable The new variable defining the join association path
     * @return A new {@link JoinStateObject}
     */
    public JoinStateObject addJoin(String joinType, ListIterator<String> paths, String identificationVariable) {
        JoinStateObject stateObject = addJoin(joinType);
        stateObject.setJoinAssociationPaths(paths);
        stateObject.setIdentificationVariable(identificationVariable);
        return stateObject;
    }

    /**
     * Adds a new <code><b>JOIN</b></code> expression to this declaration.
     *
     * @param path The join association path expression
     * @param identificationVariable The new variable defining the join association path
     * @return A new {@link JoinStateObject}
     */
    public JoinStateObject addJoin(String path, String identificationVariable) {
        return addJoin(JOIN, path, identificationVariable);
    }

    /**
     * Adds a new <code><b>JOIN</b></code> expression to this declaration.
     *
     * @param joinType One of the joining types: <code><b>LEFT JOIN</b></code>, <code><b>LEFT OUTER
     * JOIN</b></code>, <code><b>INNER JOIN</b></code> or <code><b>JOIN</b></code>
     * @param path The join association path expression
     * @param identificationVariable The new variable defining the join association path
     * @return A new {@link JoinStateObject}
     */
    public JoinStateObject addJoin(String joinType, String path, String identificationVariable) {
        JoinStateObject stateObject = addJoin(joinType);
        stateObject.setJoinAssociationPath(path);
        stateObject.setIdentificationVariable(identificationVariable);
        return stateObject;
    }

    /**
     * Adds a new <code><b>LEFT JOIN</b></code> expression to this declaration.
     *
     * @param path The join association path expression
     * @param identificationVariable The new variable defining the join association path
     * @return A new {@link JoinStateObject}
     */
    public JoinStateObject addLeftJoin(String path, String identificationVariable) {
        return addJoin(LEFT_JOIN, path, identificationVariable);
    }

    /**
     * Adds a new <code><b>LEFT OUTER JOIN</b></code> expression to this declaration.
     *
     * @param path The join association path expression
     * @param identificationVariable The new variable defining the join association path
     * @return A new {@link JoinStateObject}
     */
    public JoinStateObject addLeftOuterJoin(String path, String identificationVariable) {
        return addJoin(LEFT_OUTER_JOIN, path, identificationVariable);
    }

    /**
     * Creates
     *
     * @return
     */
    protected abstract AbstractRangeVariableDeclarationStateObject buildRangeVariableDeclarationStateObject();

    /**
     * {@inheritDoc}
     */
    @Override
    public IdentificationVariableDeclaration getExpression() {
        return (IdentificationVariableDeclaration) super.getExpression();
    }

    /**
     * Returns the identification variable identifying the "root".
     *
     * @return A case insensitive unique identifier declaring the "root" of the declaration
     */
    public String getIdentificationVariable() {
        return getRangeVariableDeclaration().getIdentificationVariable();
    }

    /**
     * Returns the {@link IdentificationVariableStateObject} holding onto the identification variable.
     *
     * @return The {@link IdentificationVariableStateObject}, which is never <code>null</code>
     */
    public IdentificationVariableStateObject getIdentificationVariableStateObject() {
        return getRangeVariableDeclaration().getIdentificationVariableStateObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractFromClauseStateObject getParent() {
        return (AbstractFromClauseStateObject) super.getParent();
    }

    /**
     * Returns the {@link StateObject} representing the range variable declaration portion.
     *
     * @return The concrete instance
     */
    public AbstractRangeVariableDeclarationStateObject getRangeVariableDeclaration() {
        return rangeVariableDeclaration;
    }

    /**
     * Returns the "root" object for objects which may not be reachable by navigation.
     *
     * @return The "root" object
     */
    public String getRootPath() {
        return getRangeVariableDeclaration().getRootPath();
    }

    /**
     * Returns the {@link StateObject} representing the "root" for objects which may not be
     * reachable by navigation.
     *
     * @return The {@link StateObject} representing one of the possible valid "root"
     */
    public StateObject getRootStateObject() {
        return getRangeVariableDeclaration().getRootStateObject();
    }

    /**
     * {@inheritDoc}
     */
    public ListIterable<IdentificationVariableStateObject> identificationVariables() {
        List<IdentificationVariableStateObject> stateObjects = new ArrayList<IdentificationVariableStateObject>();
        stateObjects.add(rangeVariableDeclaration.getIdentificationVariableStateObject());
        for (JoinStateObject join : items()) {
            stateObjects.add(join.getIdentificationVariableStateObject());
        }
        return new SnapshotCloneListIterable<IdentificationVariableStateObject>(stateObjects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        rangeVariableDeclaration = buildRangeVariableDeclarationStateObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEquivalent(StateObject stateObject) {

        if (super.isEquivalent(stateObject)) {
            AbstractIdentificationVariableDeclarationStateObject declaration = (AbstractIdentificationVariableDeclarationStateObject) stateObject;
            return rangeVariableDeclaration.isEquivalent(declaration.rangeVariableDeclaration) &&
                   areChildrenEquivalent(declaration);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String listName() {
        return JOINS_LIST;
    }

    /**
     * Parses the given JPQL fragment that should contain a single <code><b>JOIN</b></code>
     * expression.
     *
     * @param jpqlFragment The portion representing a <code><b>JOIN</b></code> expression
     */
    public void parseJoin(String jpqlFragment) {
        // The JoinStateObject is automatically added to this state object
        buildStateObject(jpqlFragment, JoinBNF.ID);
    }

    /**
     * Keeps a reference of the {@link IdentificationVariableDeclaration parsed object} object, which
     * should only be done when this object is instantiated during the conversion of a parsed JPQL
     * query into {@link StateObject StateObjects}.
     *
     * @param expression The {@link IdentificationVariableDeclaration parsed object} representing an
     * identification variable declaration
     */
    public void setExpression(IdentificationVariableDeclaration expression) {
        super.setExpression(expression);
    }

    /**
     * Sets the new identification variable that will range over the "root".
     *
     * @param identificationVariable The new identification variable
     */
    public void setIdentificationVariable(String identificationVariable) {
        rangeVariableDeclaration.setIdentificationVariable(identificationVariable);
    }

    /**
     * Sets the "root" object for objects which may not be reachable by navigation.
     *
     * @param root The "root" object
     */
    public void setRootPath(String root) {
        getRangeVariableDeclaration().setRootPath(root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toTextInternal(Appendable writer) throws IOException {

        rangeVariableDeclaration.toString(writer);

        if (itemsSize() > 0) {
            writer.append(SPACE);
            toStringItems(writer, false);
        }
    }
}
