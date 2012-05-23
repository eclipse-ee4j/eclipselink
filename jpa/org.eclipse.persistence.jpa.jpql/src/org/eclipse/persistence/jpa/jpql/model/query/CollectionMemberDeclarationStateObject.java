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
import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.SingleElementListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * An identification variable declared by a <code>collection_member_declaration</code> ranges over
 * values of a collection obtained by navigation using a path expression. Such a path expression
 * represents a navigation involving the association-fields of an entity abstract schema type.
 * Because a path expression can be based on another path expression, the navigation can use the
 * association-fields of related entities. An identification variable of a collection member
 * declaration is declared using a special operator, the reserved identifier <code><b>IN</b></code>.
 * The argument to the <code><b>IN</b></code> operator is a collection-valued path expression. The
 * path expression evaluates to a collection type specified as a result of navigation to a
 * collection-valued association-field of an entity abstract schema type. The syntax for declaring a
 * collection member identification variable is as follows:
 * <p>
 * <div nowrap><b>BNF:</b> <code>collection_member_declaration ::= IN(collection_valued_path_expression) [AS] identification_variable</code><p>
 * or
 * <div nowrap><b>BNF:</b> <code>derived_collection_member_declaration ::= IN superquery_identification_variable.{single_valued_object_field.}*collection_valued_field</code><p>
 *
 * @see FromClauseStateObject
 * @see CollectionMemberDeclaration
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class CollectionMemberDeclarationStateObject extends AbstractStateObject
                                                    implements VariableDeclarationStateObject {

	/**
	 * Flag used to determine if the <code><b>AS</b></code> identifier is used or not.
	 */
	private boolean as;

	/**
	 * The model object representing the collection-valued association-field of an entity abstract
	 * schema type.
	 */
	private CollectionValuedPathExpressionStateObject collectionValuedPath;

	/**
	 * Determines whether this collection member declaration is used as a deriving collection-valued
	 * path expression.
	 */
	private boolean derived;

	/**
	 * The model object representing an identification variable evaluating to a collection-valued
	 * association-field of an entity abstract schema type.
	 */
	private IdentificationVariableStateObject identificationVariable;

	/**
	 * Notifies the visibility of the <code><b>AS</b></code> identifier has changed.
	 */
	public static final String AS_PROPERTY = "as";

	/**
	 * Creates a new <code>CollectionMemberDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CollectionMemberDeclarationStateObject(AbstractFromClauseStateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>CollectionMemberDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param paths The segments that represent the collection-valued path
	 * @param as Determine whether the <code><b>AS</b></code> identifier is used or not
	 * @param identificationVariable The identification variable declaring the collection-valued path
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CollectionMemberDeclarationStateObject(AbstractFromClauseStateObject parent,
	                                              ListIterator<String> paths,
	                                              boolean as,
	                                              String identificationVariable) {

		super(parent);
		this.as = as;
		getCollectionValuedPath().setPaths(paths);
		setIdentificationVariable(identificationVariable);
	}

	/**
	 * Creates a new <code>CollectionMemberDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param paths The segments that represent the collection-valued path
	 * @param identificationVariable The identification variable declaring the collection-valued path
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CollectionMemberDeclarationStateObject(AbstractFromClauseStateObject parent,
	                                              ListIterator<String> paths,
	                                              String identificationVariable) {

		this(parent, paths, false, identificationVariable);
	}

	/**
	 * Creates a new <code>CollectionMemberDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param collectionValuedPath The derived collection-valued path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CollectionMemberDeclarationStateObject(SimpleFromClauseStateObject parent,
	                                              String collectionValuedPath) {

		super(parent);
		this.derived = true;
		setPath(collectionValuedPath);
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
	public CollectionMemberDeclarationStateObject addAs() {
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
		children.add(collectionValuedPath);
		children.add(identificationVariable);
	}

	/**
	 * Returns the model object representing the collection-valued association-field
	 * of an entity abstract schema type.
	 *
	 * @return The collection-valued association-field of an entity abstract
	 * schema type
	 */
	public CollectionValuedPathExpressionStateObject getCollectionValuedPath() {
		return collectionValuedPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionMemberDeclaration getExpression() {
		return (CollectionMemberDeclaration) super.getExpression();
	}

	/**
	 * Returns the model object representing an identification variable
	 * evaluating to a collection-valued association-field of an entity abstract
	 * schema type.
	 *
	 * @return The identification variable portion of this declaration
	 */
	public IdentificationVariableStateObject getIdentificationVariable() {
		return identificationVariable;
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(StateObject stateObject) {

		if (identificationVariable.isEquivalent(stateObject)) {
			return collectionValuedPath.getManagedType();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractFromClauseStateObject getParent() {
		return (AbstractFromClauseStateObject) super.getParent();
	}

	/**
	 * Determines whether the <code><b>AS</b></code> identifier is used or not.
	 *
	 * @return <code>true</code> if the <code><b>AS</b></code> identifier is part
	 * of the expression; <code>false</code> otherwise
	 */
	public boolean hasAs() {
		return as;
	}

	/**
	 * Determines whether an identification variable was defined.
	 *
	 * @return <code>true</code> if an identification variable is defined; <code>false</code> otherwise
	 */
	public boolean hasIdentificationVariable() {
		return identificationVariable.hasText();
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableListIterator<IdentificationVariableStateObject> identificationVariables() {
		return new SingleElementListIterator<IdentificationVariableStateObject>(identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		collectionValuedPath   = new CollectionValuedPathExpressionStateObject(this);
		identificationVariable = new IdentificationVariableStateObject(this);
	}

	/**
	 * Determines whether this collection member declaration is used as a derived collection-valued
	 * path expression.
	 *
	 * @return <code>true</code> if this collection member declaration is used as this form:
	 * "<code><b>IN</b> collection_valued_path_expression</code>" in a subquery; <code>false</code>
	 * if it's used as this form: <code><b>IN</b>(collection_valued_path_expression)
	 * [<b>AS</b>] identification_variable</code>" in a top-level or subquery queries
	 */
	public boolean isDerived() {
		return derived;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			CollectionMemberDeclarationStateObject declaration = (CollectionMemberDeclarationStateObject) stateObject;

			return as      == declaration.as      &&
			       derived == declaration.derived &&
			       identificationVariable.isEquivalent(declaration.identificationVariable) &&
			       collectionValuedPath  .isEquivalent(declaration.collectionValuedPath);
		}

		return false;
	}

	/**
	 * Makes sure the <code><b>AS</b></code> identifier is not specified.
	 */
	public void removeAs() {
		if (as) {
			setAs(false);
		}
	}

	/**
	 * Sets whether the <code><b>AS</b></code> identifier is used or not.
	 *
	 * @param as <code>true</code> if the <code><b>AS</b></code> identifier is part of the expression;
	 * <code>false</code> otherwise
	 */
	public void setAs(boolean as) {
		boolean oldAs = this.as;
		this.as = as;
		firePropertyChanged(AS_PROPERTY, oldAs, as);
	}

	/**
	 * Sets whether this collection member declaration is used as a derived collection-valued path
	 * expression.
	 *
	 * @param derived <code>true</code> if this collection member declaration is used as this form:
	 * "<code><b>IN</b> collection_valued_path_expression</code>" in a subquery; <code>false</code>
	 * if it's used as this form: <code><b>IN</b>(collection_valued_path_expression)
	 * [<b>AS</b>] identification_variable</code>" in a top-level or subquery queries
	 */
	public void setDerived(boolean derived) {
		this.derived = derived;
	}

	/**
	 * Keeps a reference of the {@link CollectionMemberDeclaration parsed object} object, which
	 * should only be done when this object is instantiated during the conversion of a parsed JPQL
	 * query into {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link CollectionMemberDeclaration parsed object} representing an <code>IN<b></b></code>
	 * expression
	 */
	public void setExpression(CollectionMemberDeclaration expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the new identification variable that will range over the collection-valued path.
	 *
	 * @param identificationVariable The new identification variable
	 */
	public void setIdentificationVariable(String identificationVariable) {
		this.identificationVariable.setText(identificationVariable);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param path The new path expression
	 */
	public void setPath(String path) {
		collectionValuedPath.setPath(path);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setPaths(List<String> paths) {
		collectionValuedPath.setPaths(paths);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setPaths(ListIterator<String> paths) {
		collectionValuedPath.setPaths(paths);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setPaths(String... paths) {
		collectionValuedPath.setPaths(paths);
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

		writer.append(IN);

		if (!derived) {
			writer.append(LEFT_PARENTHESIS);
		}
		else {
			writer.append(SPACE);
		}

		collectionValuedPath.toString(writer);

		if (!derived) {
			writer.append(RIGHT_PARENTHESIS);
		}

		if (!derived) {
			writer.append(SPACE);

			if (as) {
				writer.append(AS);
				writer.append(SPACE);
			}

			identificationVariable.toString(writer);
		}
	}
}