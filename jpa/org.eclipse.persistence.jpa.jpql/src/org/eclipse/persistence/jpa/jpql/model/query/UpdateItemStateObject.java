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
import org.eclipse.persistence.jpa.jpql.model.INewValueStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.parser.NewValueBNF;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code>new_value</code> specified for an update operation must be compatible in type with the
 * field to which it is assigned.
 *
 * <div nowrap><b>BNF:</b> <code>update_item ::= [identification_variable.]{state_field | single_valued_association_field} = new_value</code><p>
 *
 * @see UpdateItem
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class UpdateItemStateObject extends AbstractStateObject {

	/**
	 * The builder is cached during the creation of the new value expression.
	 */
	private INewValueStateObjectBuilder builder;

	/**
	 * The state object representing the new value.
	 */
	private StateObject newValue;

	/**
	 * The state object representing the state field path expression.
	 */
	private StateFieldPathExpressionStateObject stateFieldPath;

	/**
	 * Notifies the new value property has changed.
	 */
	public static final String NEW_VALUE_PROPERTY = "newValue";

	/**
	 * Creates a new <code>UpdateItemStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public UpdateItemStateObject(UpdateClauseStateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>UpdateItemStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path The state field path to receive the new value
	 * @param newValue The actual expression representing the new value
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public UpdateItemStateObject(UpdateClauseStateObject parent,
	                             String path,
	                             StateObject newValue) {

		super(parent);
		this.newValue = parent(newValue);
		setPath(path);
	}

	/**
	 * Creates a new <code>UpdateItemStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path The state field path to receive the new value
	 * @param newValue The JPQL fragment representing the new value
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public UpdateItemStateObject(UpdateClauseStateObject parent,
	                             String path,
	                             String newValue) {

		super(parent);
		parseNewValue(newValue);
		setPath(path);
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
		if (stateFieldPath != null) {
			children.add(stateFieldPath);
		}
		if (newValue != null) {
			children.add(newValue);
		}
	}

	/**
	 * Appends the given sequence of characters to the path expression. If the sequence does not
	 * begin with a dot, then the first segment will be appended to the last segment and then new
	 * segments will be created.
	 *
	 * @param text The sequence of characters to append to the path expression
	 */
	public void appendToPath(String text) {
		stateFieldPath.append(text);
	}

	/**
	 * Creates and returns a new {@link INewValueStateObjectBuilder} that can be used to
	 * programmatically create a new value expression and once the expression is complete,
	 * {@link INewValueStateObjectBuilder#commit()} will push the {@link StateObject}
	 * representation of that expression as this new value object.
	 *
	 * @return A new builder that can be used to quickly create a new value expression
	 */
	public INewValueStateObjectBuilder getBuilder() {
		if (builder == null) {
			builder = getQueryBuilder().buildStateObjectBuilder(this);
		}
		return builder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UpdateItem getExpression() {
		return (UpdateItem) super.getExpression();
	}

	/**
	 * Returns the {@link StateObject} representing the new value.
	 *
	 * @return The new value expression or <code>null</code> if it's not yet defined
	 */
	public StateObject getNewValue() {
		return newValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UpdateClauseStateObject getParent() {
		return (UpdateClauseStateObject) super.getParent();
	}

	/**
	 * Returns the string representation of the path expression. If the identification variable is
	 * virtual, then it is not part of the result.
	 *
	 * @return The path expression, which is never <code>null</code>
	 */
	public String getPath() {
		return stateFieldPath.getPath();
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public StateFieldPathExpressionStateObject getStateFieldPath() {
		return stateFieldPath;
	}

	/**
	 * Determines whether the {@link StateObject} representing the new value is present.
	 *
	 * @return <code>true</code> the new value exists; otherwise <code>false</code>
	 */
	public boolean hasNewValue() {
		return newValue != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		stateFieldPath = new StateFieldPathExpressionStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			UpdateItemStateObject updateItem = (UpdateItemStateObject) stateObject;
			return stateFieldPath.isEquivalent(updateItem.stateFieldPath) &&
			       areEquivalent(newValue, updateItem.newValue);
		}

		return false;
	}

	/**
	 * Returns the segments in the state field path in order.
	 *
	 * @return An {@link IterableListIterator} over the paths of the state field path
	 */
	public IterableListIterator<String> items() {
		return stateFieldPath.items();
	}

	/**
	 * Returns the number of segments in the path expression.
	 *
	 * @return The number of segments
	 */
	public int itemsSize() {
		return stateFieldPath.itemsSize();
	}

	/**
	 * Parses the given JPQL fragment, which represents the new value.
	 *
	 * @param newValue The string representation of the new value to parse and to convert into a
	 * {@link StateObject}
	 */
	public void parseNewValue(String newValue) {
		StateObject newValueStateObject = buildStateObject(newValue, NewValueBNF.ID);
		setNewValue(newValueStateObject);
	}

	/**
	 * Keeps a reference of the {@link UpdateItem parsed object} object, which should only be done
	 * when this object is instantiated during the conversion of a parsed JPQL query into {@link
	 * StateObject StateObjects}.
	 *
	 * @param expression The {@link UpdateItem parsed object} representing an update item
	 */
	public void setExpression(UpdateItem expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the new value to be the given {@link StateObject}.
	 *
	 * @param newValue The {@link StateObject} representing the new value
	 */
	public void setNewValue(StateObject newValue) {
		builder = null;
		StateObject oldNewValue = this.newValue;
		this.newValue = parent(newValue);
		firePropertyChanged(NEW_VALUE_PROPERTY, oldNewValue, newValue);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param path The new path expression
	 */
	public void setPath(String path) {
		stateFieldPath.setPath(path);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setPaths(ListIterator<String> paths) {
		stateFieldPath.setPaths(paths);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setPaths(String[] paths) {
		stateFieldPath.setPaths(paths);
	}

	/**
	 * The state field path expression is not qualified by the identification variable.
	 *
	 * @param identificationVariable The virtual variable that was generated based on the entity name
	 */
	public void setVirtualIdentificationVariable(String identificationVariable) {

		IdentificationVariableStateObject variable = new IdentificationVariableStateObject(
			stateFieldPath,
			identificationVariable
		);

		variable.setVirtual(true);
		stateFieldPath.setIdentificationVariableInternally(variable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		stateFieldPath.toString(writer);

		writer.append(SPACE);
		writer.append(EQUAL);

		if (hasNewValue()) {
			writer.append(SPACE);
			newValue.toString(writer);
		}
	}
}