/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * The abstract definition of a joining expression.
 *
 * @see JoinStateObject
 * @see FetchJoinStateObject
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractJoinStateObject extends AbstractStateObject {

	/**
	 * The state object holding the join association path.
	 */
	private CollectionValuedPathExpressionStateObject joinAssociationPath;

	/**
	 * One of the joining types used by this state object.
	 */
	private String joinType;

	/**
	 * Notifies the join type property has changed.
	 */
	public static final String JOIN_TYPE_PROPERTY = "joinType";

	/**
	 * Creates a new <code>AbstractJoinStateObject</code>.
	 *
	 * @param parent The parent of this state object
	 * @param joinType One of the joining types
	 */
	protected AbstractJoinStateObject(AbstractIdentificationVariableDeclarationStateObject parent,
	                                  String joinType) {

		super(parent);
		this.joinType = joinType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		children.add(joinAssociationPath);
	}

	/**
	 * Adds the given segments to the end of the join association path expression. The identification
	 * variable will not be affected.
	 *
	 * @param paths The new path expression
	 */
	public void addJoinAssociationPaths(List<String> paths) {
		joinAssociationPath.addItems(paths);
	}

	/**
	 * Returns the {@link StateObject} representing the identification variable that starts the path
	 * expression, which can be a sample identification variable, a map value, map key or map entry
	 * expression.
	 *
	 * @return The root of the path expression
	 */
	public StateObject getJoinAssociationIdentificationVariable() {
		return joinAssociationPath.getIdentificationVariable();
	}

	/**
	 * Returns the {@link CollectionValuedPathExpressionStateObject} representing the join
	 * association path.
	 *
	 * @return The state object representing the join association path
	 */
	public CollectionValuedPathExpressionStateObject getJoinAssociationPathStateObject() {
		return joinAssociationPath;
	}

	/**
	 * Returns the joining type.
	 *
	 * @return The joining type of this joining expression
	 */
	public String getJoinType() {
		return joinType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractIdentificationVariableDeclarationStateObject getParent() {
		return (AbstractIdentificationVariableDeclarationStateObject) super.getParent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		joinAssociationPath = new CollectionValuedPathExpressionStateObject(this);
	}

	/**
	 * Returns the segments in the state field path in order.
	 *
	 * @return An <code>Iterator</code> over the segments of the state field path
	 */
	public IterableListIterator<String> joinAssociationPaths() {
		return joinAssociationPath.items();
	}

	/**
	 * Returns the number of segments in the path expression.
	 *
	 * @return The number of segments
	 */
	public int joinAssociationPathSize() {
		return joinAssociationPath.itemsSize();
	}

	/**
	 * Sets the {@link StateObject} representing the identification variable that starts the path
	 * expression, which can be a sample identification variable, a map value, map key or map entry
	 * expression.
	 *
	 * @param identificationVariable The root of the path expression
	 */
	public void setJoinAssociationIdentificationVariable(StateObject identificationVariable) {
		joinAssociationPath.setIdentificationVariable(identificationVariable);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param path The new path expression
	 */
	public void setJoinAssociationPath(String path) {
		joinAssociationPath.setPath(path);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setJoinAssociationPaths(ListIterator<String> paths) {
		joinAssociationPath.setPaths(paths);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setJoinAssociationPaths(String[] paths) {
		joinAssociationPath.setPaths(paths);
	}

	/**
	 * Sets the joining type.
	 *
	 * @param joinType One of the joining types
	 */
	public void setJoinType(String joinType) {
		String oldJoinType = this.joinType;
		this.joinType = joinType;
		firePropertyChanged(JOIN_TYPE_PROPERTY, oldJoinType, joinType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		writer.append(joinType);
		writer.append(SPACE);
		joinAssociationPath.toString(writer);
	}
}