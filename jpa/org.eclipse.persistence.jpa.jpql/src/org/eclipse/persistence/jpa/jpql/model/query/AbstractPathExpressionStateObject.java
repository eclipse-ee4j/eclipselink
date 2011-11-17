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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import org.eclipse.persistence.jpa.jpql.model.IListChangeEvent.EventType;
import org.eclipse.persistence.jpa.jpql.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.model.ListChangeEvent;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpressionBNF;
import org.eclipse.persistence.jpa.jpql.util.CollectionTools;
import org.eclipse.persistence.jpa.jpql.util.iterator.ArrayListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * An identification variable followed by the navigation operator (.) and a state field or
 * association field is a path expression. The type of the path expression is the type computed as
 * the result of navigation; that is, the type of the state field or association field to which the
 * expression navigates.
 *
 * @see AbstractPathExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractPathExpressionStateObject extends AbstractStateObject
                                                        implements ListHolderStateObject<String> {

	/**
	 * The {@link StateObject} that represents the identification variable portion of the path
	 * expression, which can be one of the following when the JPA version is 2.0 or later:
	 * <ul>
	 * <li>{@link IdentificationVariableStateObject}</li>
	 * <li>{@link KeyExpressionStateObject}</li>
	 * <li>{@link ValueExpressionStateObject}</li>
	 * </ul>
	 */
	private StateObject identificationVariable;

	/**
	 * The list of segments, including the general identification variable.
	 */
	private List<String> paths;

	/**
	 * Notifies the identification variable property has changed.
	 */
	public static final String IDENTIFICATION_VARIABLE_PROPERTY = "identificationVariable";

	/**
	 * Notifies the content of the paths list has changed.
	 */
	public static final String PATHS_LIST = "paths";

	/**
	 * Creates a new <code>AbstractPathExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractPathExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>AbstractPathExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path The path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractPathExpressionStateObject(StateObject parent, String path) {
		super(parent);
		setPath(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {

		super.addChildren(children);

		StateObject stateObject = getIdentificationVariable();

		if (stateObject != null) {
			children.add(stateObject);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String addItem(String item) {
		getChangeSupport().addItem(this, paths, PATHS_LIST, item);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addItems(List<? extends String> items) {
		getChangeSupport().addItems(this, paths, PATHS_LIST, items);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addListChangeListener(String listName, IListChangeListener<String> listener) {
		getChangeSupport().addListChangeListener(listName, listener);
	}

	/**
	 * Appends the given sequence of characters to the path expression. If the sequence does not
	 * begin with a dot, then the first segment will be appended to the last segment and then new
	 * segments will be created.
	 *
	 * @param text The sequence of characters to append to the path expression
	 */
	public void append(String text) {

		StringBuilder word = new StringBuilder();
		int pathCount = paths.size();
		boolean appendToLastSegment = true;
		int startIndex = pathCount;

		for (int index = 0, count = text.length(); index < count; index++) {
			char character = text.charAt(index);

			if (character == DOT) {
				if (word.length() > 0) {
					// Append the content of the buffer to the end of the last segment
					if (appendToLastSegment) {
						String currentPath = paths.get(pathCount - 1);
						paths.set(pathCount - 1, currentPath + word);
						startIndex = pathCount - 1;
					}
					// Add a new segment
					else {
						paths.add(word.toString());
						pathCount++;
					}

					// Clear the buffer
					word.delete(0, word.length());
				}

				appendToLastSegment = false;
				continue;
			}
			// Add the character to the buffer
			else {
				word.append(character);
			}
		}

		if (word.length() > 0) {
			// Append the content of the buffer to the end of the last segment
			if (appendToLastSegment) {
				String currentPath = paths.get(pathCount - 1);
				paths.set(pathCount - 1, currentPath + word);
				startIndex = pathCount - 1;
			}
			// Add a new segment
			else {
				paths.add(word.toString());
			}
		}

		ListChangeEvent<String> event = new ListChangeEvent<String>(this, paths, EventType.CHANGED, PATHS_LIST, paths, startIndex, itemsSize());
		getChangeSupport().fireListChangeEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canMoveDown(String item) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canMoveUp(String item) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractPathExpression getExpression() {
		return (AbstractPathExpression) super.getExpression();
	}

	/**
	 * Returns the {@link StateObject} representing the identification variable that starts the path
	 * expression, which can be a sample identification variable, a map value, map key or map entry
	 * expression.
	 *
	 * @return The root of the path expression
	 */
	public StateObject getIdentificationVariable() {
		if ((identificationVariable == null) && hasItems()) {
			identificationVariable = buildStateObject(getItem(0), StateFieldPathExpressionBNF.ID);
		}
		return identificationVariable;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getItem(int index) {
		return paths.get(index);
	}

	/**
	 * Returns the string representation of the path expression. If the identification variable is
	 * virtual, then it is not part of the result.
	 *
	 * @return The path expression, which is never <code>null</code>
	 */
	public String getPath() {
		return toString();
	}

	/**
	 * Determines whether the identification variable is present.
	 *
	 * @return <code>true</code> the identification variable is present; <code>false</code> otherwise
	 */
	public boolean hasIdentificationVariable() {
		return getIdentificationVariable() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasItems() {
		return !paths.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		paths = new ArrayList<String>();
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableListIterator<String> items() {
		return new CloneListIterator<String>(paths);
	}

	/**
	 * {@inheritDoc}
	 */
	public int itemsSize() {
		return paths.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public String moveDown(String item) {
		throw new RuntimeException(getClass().getName() + " does not support moveDown(String).");
	}

	/**
	 * {@inheritDoc}
	 */
	public String moveUp(String item) {
		throw new RuntimeException(getClass().getName() + " does not support moveUp(String).");
	}

	/**
	 * Removes the single path at the given index.
	 *
	 * @param index The position of the single path to remove. If the index is 0, then the
	 * identification variable is nullified
	 */
	public void removeItem(int index) {
		if (index == 0) {
			setIdentificationVariableInternally(null);
		}
		removeItem(getItem(index));
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItem(String item) {
		getChangeSupport().removeItem(this, paths, PATHS_LIST, item);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItems(Collection<String> items) {
		getChangeSupport().removeItems(this, this.paths, PATHS_LIST, items);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListChangeListener(String listName, IListChangeListener<String> listener) {
		getChangeSupport().removeListChangeListener(listName, listener);
	}

	/**
	 * Sets the {@link StateObject} representing the identification variable that starts the path
	 * expression, which can be a sample identification variable, a map value, map key or map entry
	 * expression.
	 *
	 * @param identificationVariable The root of the path expression
	 */
	public void setIdentificationVariable(StateObject identificationVariable) {
		setIdentificationVariableInternally(identificationVariable);
		getChangeSupport().replaceItem(this, paths, PATHS_LIST, 0, identificationVariable.toString());
	}

	/**
	 * Sets the {@link StateObject} representing the identification variable that starts the path
	 * expression, which can be a sample identification variable, a map value, map key or map entry
	 * expression. This method does not replace the first path in the list of paths.
	 *
	 * @param identificationVariable The root of the path expression
	 */
	protected void setIdentificationVariableInternally(StateObject identificationVariable) {
		StateObject oldIdentificationVariable = this.identificationVariable;
		this.identificationVariable = parent(identificationVariable);
		firePropertyChanged(IDENTIFICATION_VARIABLE_PROPERTY, oldIdentificationVariable, identificationVariable);
	}

	/**
	 * Replaces the existing path segment to become the given one.
	 *
	 * @param index The position of the path segment to replace
	 * @param path The replacement
	 */
	public void setPath(int index, String path) {
		if (index == 0) {
			setIdentificationVariableInternally(null);
		}
		getChangeSupport().replaceItem(this, paths, PATHS_LIST, index, path);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param path The new path expression
	 */
	public void setPath(String path) {

		List<String> paths = new ArrayList<String>();

		for (StringTokenizer tokenizer = new StringTokenizer(path, "."); tokenizer.hasMoreTokens(); ) {
			String token = tokenizer.nextToken();
			paths.add(token);
		}

		setPaths(paths.listIterator());
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setPaths(ListIterator<String> paths) {
		setIdentificationVariableInternally(null);
		getChangeSupport().replaceItems(this, this.paths, PATHS_LIST, CollectionTools.list(paths));
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setPaths(String... paths) {
		setPaths(new ArrayListIterator<String>(paths));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		StateObject stateObject = getIdentificationVariable();

		if (stateObject != null) {
			String variable = stateObject.toString();

			if (variable.length() > 0) {
				writer.append(variable);

				if (hasItems()) {
					writer.append(DOT);
				}
			}
		}

		for (int index = 1, count = paths.size(); index < count; index++) {
			writer.append(paths.get(index));
			if (index < count - 1) {
				writer.append(DOT);
			}
		}
	}
}