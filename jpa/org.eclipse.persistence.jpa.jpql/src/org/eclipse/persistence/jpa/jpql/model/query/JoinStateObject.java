/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * A <code><b>JOIN</b></code> enables the fetching of an association as a side effect of the
 * execution of a query. A <code><b>JOIN</b></code> is specified over an entity and its related
 * entities.
 * <p>
 * <div nowrap><b>BNF:</b> <code>join ::= join_spec join_association_path_expression [AS] identification_variable</code><p>
 * <p>
 * A <b>JOIN FETCH</b> enables the fetching of an association as a side effect of the execution of
 * a query. A <b>JOIN FETCH</b> is specified over an entity and its related entities.
 * <p>
 * <div nowrap><b>BNF:</b> <code>fetch_join ::= join_spec FETCH join_association_path_expression</code><p>
 *
 * @see Join
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class JoinStateObject extends AbstractStateObject {

	/**
	 * Flag used to determine if the <code><b>AS</b></code> identifier is used or not.
	 */
	private boolean as;

	/**
	 * The state object holding the identification variable.
	 */
	private IdentificationVariableStateObject identificationVariable;

	/**
	 * The state object holding the join association path.
	 */
	private CollectionValuedPathExpressionStateObject joinAssociationPath;

	/**
	 * One of the joining types used by this state object.
	 */
	private String joinType;

	/**
	 * Notifies the visibility of the <code><b>AS</b></code> identifier has changed.
	 */
	public static final String AS_PROPERTY = "as";

	/**
	 * Notifies the join type property has changed.
	 */
	public static final String JOIN_TYPE_PROPERTY = "joinType";

	/**
	 * Creates a new <code>JoinStateObject</code>.
	 *
	 * @param parent The parent of this state object
	 * @param joinType One of the joining types
	 */
	public JoinStateObject(AbstractIdentificationVariableDeclarationStateObject parent,
	                       String joinType) {

		this(parent, joinType, false);
	}

	/**
	 * Creates a new <code>JoinStateObject</code>.
	 *
	 * @param parent The parent of this state object
	 * @param joinType One of the joining types
	 * @param as Determine whether the <code><b>AS</b></code> identifier is used or not
	 */
	public JoinStateObject(AbstractIdentificationVariableDeclarationStateObject parent,
	                       String joinType,
	                       boolean as) {

		super(parent);
		validateJoinType(joinType);
		this.joinType = joinType;
		this.as = as;
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Makes sure the <code><b>AS</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public JoinStateObject addAs() {
		if (!as) {
			setAs(true);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		children.add(joinAssociationPath);
		children.add(identificationVariable);
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
	 * {@inheritDoc}
	 */
	@Override
	public Join getExpression() {
		return (Join) super.getExpression();
	}

	/**
	 * Returns the name of the identification variable that defines the join association path.
	 *
	 * @return The variable defining the join association path
	 */
	public String getIdentificationVariable() {
		return identificationVariable.getText();
	}

	/**
	 * Returns the state object holding the identification variable.
	 *
	 * @return The portion of the joining expression representing the identification variable
	 */
	public IdentificationVariableStateObject getIdentificationVariableStateObject() {
		return identificationVariable;
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
	 * Determines whether the <code><b>AS</b></code> identifier is used or not.
	 *
	 * @return <code>true</code> if the <code><b>AS</b></code> identifier is part of the expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasAs() {
		return as;
	}

	/**
	 * Determines whether the identifier <b>FETCH</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>FETCH</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasFetch() {
		String joinType = getJoinType();
		return joinType == JOIN_FETCH ||
		       joinType == LEFT_JOIN_FETCH ||
		       joinType == LEFT_OUTER_JOIN_FETCH;
	}

	/**
	 * Determines whether the identification variable has been defined.
	 *
	 * @return <code>true</code> if the identification has been defined; <code>false</code> otherwise
	 */
	public boolean hasIdentificationVariable() {
		return identificationVariable.hasText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		joinAssociationPath    = new CollectionValuedPathExpressionStateObject(this);
		identificationVariable = new IdentificationVariableStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			JoinStateObject join = (JoinStateObject) stateObject;
			return joinType.equals(join.joinType) &&
			       areEquivalent(joinAssociationPath, join.joinAssociationPath) &&
			       (as == join.as) &&
			       identificationVariable.isEquivalent(join.identificationVariable);
		}

		return false;
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
	 * Makes sure the <code><b>AS</b></code> identifier is not specified.
	 */
	public void removeNot() {
		if (as) {
			setAs(false);
		}
	}

	/**
	 * Sets whether the <code><b>AS</b></code> identifier is used or not.
	 *
	 * @param hasAs <code>true</code> if the <code><b>AS</b></code> identifier is part of the
	 * expression; <code>false</code> otherwise
	 */
	public void setAs(boolean as) {
		boolean oldAs = this.as;
		this.as = as;
		firePropertyChanged(AS_PROPERTY, oldAs, as);
	}

	/**
	 * Keeps a reference of the {@link Join parsed object} object, which should only be done when
	 * this object is instantiated during the conversion of a parsed JPQL query into {@link
	 * StateObject StateObjects}.
	 *
	 * @param expression The {@link Join parsed object} representing a <code><b>JOIN</b></code>
	 * expression
	 */
	public void setExpression(Join expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the name of the identification variable that defines the join association path.
	 *
	 * @param identificationVariable The new variable defining the join association path
	 */
	public void setIdentificationVariable(String identificationVariable) {
		this.identificationVariable.setText(identificationVariable);
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
		validateJoinType(joinType);
		String oldJoinType = this.joinType;
		this.joinType = joinType;
		firePropertyChanged(JOIN_TYPE_PROPERTY, oldJoinType, joinType);
	}

	/**
	 * Toggles the usage of the <code><b>AS</b></code> identifier.
	 */
	public void toggleAs() {
		setAs(!as);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		writer.append(joinType);
		writer.append(SPACE);

		joinAssociationPath.toString(writer);
		writer.append(SPACE);

		if (as) {
			writer.append(AS);
			writer.append(SPACE);
		}

		identificationVariable.toString(writer);
	}

	/**
	 * Validates the given join type.
	 *
	 * @param joinType One of the possible joining types
	 */
	protected void validateJoinType(String joinType) {
		Assert.isValid(
			joinType,
			"The join type is not valid",
			JOIN,       LEFT_JOIN,       LEFT_OUTER_JOIN,       INNER_JOIN,
			JOIN_FETCH, LEFT_JOIN_FETCH, LEFT_OUTER_JOIN_FETCH, INNER_JOIN_FETCH
		);
	}
}