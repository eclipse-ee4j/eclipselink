/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model.query;

import java.io.IOException;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberExpression;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This expression tests whether the designated value is a member of the collection specified by the
 * collection-valued path expression. If the collection-valued path expression designates an empty
 * collection, the value of the <code><b>MEMBER OF</b></code> expression is <b>FALSE</b> and the
 * value of the <code><b>NOT MEMBER OF</b></code> expression is <b>TRUE</b>. Otherwise, if the value
 * of the collection-valued path expression or single-valued association-field path expression in
 * the collection member expression is <b>NULL</b> or unknown, the value of the collection member
 * expression is unknown.
 * <p>
 * <div nowrap><b>BNF:</b> <code>collection_member_expression ::= entity_or_value_expression [NOT] MEMBER [OF] collection_valued_path_expression</code><p>
 *
 * @see CollectionMemberExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class CollectionMemberExpressionStateObject extends AbstractStateObject {

	/**
	 *
	 */
	private CollectionValuedPathExpressionStateObject collectionValuedPath;

	/**
	 *
	 */
	private StateObject entityStateObject;

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is part of the expression or not.
	 */
	private boolean not;

	/**
	 * Determines whether the <code><b>OF</b></code> identifier is part of the expression or not.
	 */
	private boolean of;

	/**
	 * Notifies the entity state object property has changed.
	 */
	public static final String ENTITY_STATE_OBJECT_PROPERTY = "entityStateObject";

	/**
	 * Notifies the visibility of the <code><b>NOT</b></code> identifier has changed.
	 */
	public static final String NOT_PROPERTY = "not";

	/**
	 * Notifies the visibility of the <code><b>OF</b></code> identifier has changed.
	 */
	public static final String OF_PROPERTY = "of";

	/**
	 * Creates a new <code>CollectionMemberExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CollectionMemberExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>CollectionMemberExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param entityStateObject
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param of
	 * @param collectionValuedPath
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CollectionMemberExpressionStateObject(StateObject parent,
	                                             StateObject entityStateObject,
	                                             boolean not,
	                                             boolean of,
	                                             String collectionValuedPath) {

		super(parent);
		this.not = not;
		this.of  = of;
		this.entityStateObject = parent(entityStateObject);
		this.collectionValuedPath.setPath(collectionValuedPath);
	}

	/**
	 * Creates a new <code>CollectionMemberExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param entityStateObject
	 * @param collectionValuedPath
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CollectionMemberExpressionStateObject(StateObject parent,
	                                             StateObject entityStateObject,
	                                             String collectionValuedPath) {

		this(parent, entityStateObject, false, false, collectionValuedPath);
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
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		if (entityStateObject != null) {
			children.add(entityStateObject);
		}
		if (collectionValuedPath != null) {
			children.add(collectionValuedPath);
		}
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public CollectionMemberExpressionStateObject addNot() {
		if (!not) {
			setNot(true);
		}
		return this;
	}

	/**
	 * Makes sure the <code><b>OF</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public CollectionMemberExpressionStateObject addOf() {
		if (!of) {
			setOf(true);
		}
		return this;
	}

	public CollectionValuedPathExpressionStateObject getCollectionValuedPath() {
		return collectionValuedPath;
	}

	public StateObject getEntityStateObject() {
		return entityStateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionMemberExpression getExpression() {
		return (CollectionMemberExpression) super.getExpression();
	}

	public boolean hasEntityStateObject() {
		return entityStateObject != null;
	}

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is used or not.
	 *
	 * @return <code>true</code> if the <code><b>NOT</b></code> identifier is part of the expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return not;
	}

	public boolean hasOf() {
		return of;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		collectionValuedPath = new CollectionValuedPathExpressionStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			CollectionMemberExpressionStateObject collection = (CollectionMemberExpressionStateObject) stateObject;
			return not == collection.not &&
			       of == collection.of &&
			       collectionValuedPath.isEquivalent(collection.collectionValuedPath) &&
			       areEquivalent(entityStateObject, collection.entityStateObject);
		}

		return false;
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is not specified.
	 */
	public void removeNot() {
		if (not) {
			setNot(false);
		}
	}

	/**
	 * Makes sure the <code><b>OF</b></code> identifier is not specified.
	 */
	public void removeOf() {
		if (of) {
			setOf(false);
		}
	}

	/**
	 * Sets
	 *
	 * @param entityStateObject
	 */
	public void setEntityStateObject(StateObject entityStateObject) {
		StateObject oldEntityStateObject = entityStateObject;
		this.entityStateObject = parent(entityStateObject);
		firePropertyChanged(ENTITY_STATE_OBJECT_PROPERTY, oldEntityStateObject, entityStateObject);
	}

	/**
	 * Keeps a reference of the {@link CollectionMemberExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link CollectionMemberExpression parsed object} representing a
	 * collection member expression
	 */
	public void setExpression(CollectionMemberExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets whether the <code><b>NOT</b></code> identifier should be part of the expression or not.
	 *
	 * @param not <code>true</code> if the <code><b>NOT</b></code> identifier should be part of the
	 * expression; <code>false</code> otherwise
	 */
	public void setNot(boolean not) {
		boolean oldNot = this.not;
		this.not = not;
		firePropertyChanged(NOT_PROPERTY, oldNot, not);
	}

	public void setOf(boolean of) {
		boolean oldOf = this.of;
		this.of = of;
		firePropertyChanged(OF_PROPERTY, oldOf, of);
	}

	/**
	 * Changes the visibility state of the <code><b>NOT</b></code> identifier.
	 */
	public void toggleNot() {
		setNot(!not);
	}

	/**
	 * Changes the visibility state of the <code><b>OF</b></code> identifier.
	 */
	public void toggleOf() {
		setOf(!of);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		if (entityStateObject != null) {
			entityStateObject.toString(writer);
			writer.append(SPACE);
		}

		if (not && of) {
			writer.append(NOT_MEMBER_OF);
		}
		else if (not) {
			writer.append(NOT_MEMBER);
		}
		else if (of) {
			writer.append(MEMBER_OF);
		}
		else {
			writer.append(MEMBER);
		}

		writer.append(SPACE);
		collectionValuedPath.toString(writer);
	}
}