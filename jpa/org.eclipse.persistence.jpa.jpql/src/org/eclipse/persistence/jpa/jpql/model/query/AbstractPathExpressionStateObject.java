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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.model.IListChangeEvent.EventType;
import org.eclipse.persistence.jpa.jpql.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.model.ListChangeEvent;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableBNF;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.util.CollectionTools;
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
 * @version 2.5
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
	 *
	 */
	private IManagedType managedType;

	/**
	 *
	 */
	private ArrayList<IMapping> mappings;

	/**
	 * The list of segments, including the general identification variable.
	 */
	private List<String> paths;

	/**
	 *
	 */
	private boolean resolved;

	/**
	 * The {@link IType} of the object being mapped to this identification variable.
	 */
	private IType type;

	/**
	 * The {@link ITypeDeclaration} of the object being mapped to this identification variable.
	 */
	private ITypeDeclaration typeDeclaration;

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
	@SuppressWarnings("unchecked")
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
	 * Clears the values related to the managed type and type.
	 */
	protected void clearResolvedObjects() {

		mappings.clear();

		resolved        = false;
		type            = null;
		typeDeclaration = null;
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
			identificationVariable = buildStateObject(getItem(0), IdentificationVariableBNF.ID);
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
	 * Returns
	 *
	 * @return
	 */
	public IManagedType getManagedType() {
		if (managedType == null) {
			managedType = resolveManagedType();
		}
		return managedType;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public IMapping getMapping() {
		resolveMappings();
		return mappings.get(itemsSize() - 1);
	}

	/**
	 * Retrieves the {@link IMapping} for the path at the given position.
	 *
	 * @param index The index of the path for which its {@link IMapping} should be retrieved, which
	 * should start at 1 to skip the identification variable
	 */
	public IMapping getMapping(int index) {
		resolveMappings();
		return mappings.get(index);
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
	 * Returns the {@link IType} of the field handled by this object.
	 *
	 * @return Either the {@link IType} that was resolved by this state object or the {@link IType}
	 * for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	public IType getType() {
		if (type == null) {
			type = resolveType();
		}
		return type;
	}

	/**
	 * Returns the {@link ITypeDeclaration} of the field handled by this object.
	 *
	 * @return Either the {@link ITypeDeclaration} that was resolved by this object or the {@link
	 * ITypeDeclaration} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	public ITypeDeclaration getTypeDeclaration() {
		if (typeDeclaration == null) {
			typeDeclaration = resolveTypeDeclaration();
		}
		return typeDeclaration;
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
		paths    = new ArrayList<String>();
		mappings = new ArrayList<IMapping>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (!super.isEquivalent(stateObject)) {
			return false;
		}

		AbstractPathExpressionStateObject path = (AbstractPathExpressionStateObject) stateObject;

		if (!areEquivalent(getIdentificationVariable(), path.getIdentificationVariable())) {
			return false;
		}

		int index = itemsSize();

		if (index != path.itemsSize()) {
			return false;
		}

		// Skip index 0, it is already tested and two identification
		// variables with different case are equivalent
		while (--index > 0) {
			String path1 = getItem(index);
			String path2 = path.getItem(index);

			if (ExpressionTools.valuesAreDifferent(path1, path2)) {
				return false;
			}
		}

		return true;
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
	 * Resolves
	 *
	 * @return
	 */
	protected abstract IManagedType resolveManagedType();

	/**
	 * Resolves the {@link IMapping} objects that constitutes the path expression.
	 */
	protected void resolveMappings() {

		if (!resolved) {
			resolved = true;
			IManagedTypeProvider provider = getManagedTypeProvider();
			IManagedType managedType = null;

			for (int index = 0, count = itemsSize(); index < count; index++) {

				// Identification variable
				if (index == 0) {
					StateObject stateObject = getIdentificationVariable();

					// The identification variable is not set, which means the traversal can happen
					if (stateObject != null) {
						managedType = getDeclaration().findManagedType(stateObject);
					}

					mappings.add(null);
				}
				// Resolve the path expression after the identification variable
				else if (managedType != null) {

					String path = getItem(index);

					// Cache the mapping
					IMapping mapping = managedType.getMappingNamed(path);
					mappings.add(mapping);

					// Continue by retrieving the managed type
					if (mapping != null) {
						managedType = provider.getManagedType(mapping.getType());
					}
					else {
						managedType = null;
					}
				}
				else {
					mappings.add(null);
				}
			}
		}
	}

	/**
	 * Resolves the {@link IType} of the property handled by this object.
	 *
	 * @return Either the {@link IType} that was resolved by this object or the {@link IType} for
	 * {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	protected abstract IType resolveType();

	/**
	 * Resolves the {@link ITypeDeclaration} of the property handled by this object.
	 *
	 * @return Either the {@link ITypeDeclaration} that was resolved by this object or the {@link
	 * ITypeDeclaration} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	protected ITypeDeclaration resolveTypeDeclaration() {
		resolveMappings();
		return getMapping().getTypeDeclaration();
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
		clearResolvedObjects();
		StateObject oldIdentificationVariable = this.identificationVariable;
		this.identificationVariable = parent(identificationVariable);
		firePropertyChanged(IDENTIFICATION_VARIABLE_PROPERTY, oldIdentificationVariable, identificationVariable);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param path The new path expression
	 */
	public void setPath(CharSequence path) {

		List<String> paths = new ArrayList<String>();

		for (StringTokenizer tokenizer = new StringTokenizer(path.toString(), ".", true); tokenizer.hasMoreTokens(); ) {
			String token = tokenizer.nextToken();
			if (!token.equals(".")) {
				paths.add(token);
			}
			else if (!tokenizer.hasMoreTokens()) {
				paths.add(ExpressionTools.EMPTY_STRING);
			}
		}

		setPaths(paths.listIterator());
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
		else {
			clearResolvedObjects();
		}

		getChangeSupport().replaceItem(this, paths, PATHS_LIST, index, path);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setPaths(List<String> paths) {
		setIdentificationVariableInternally(null);
		getChangeSupport().replaceItems(this, this.paths, PATHS_LIST, paths);
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setPaths(ListIterator<String> paths) {
		setPaths(CollectionTools.list(paths));
	}

	/**
	 * Changes the path expression with the list of segments, the identification variable will also
	 * be updated with the first segment.
	 *
	 * @param paths The new path expression
	 */
	public void setPaths(String... paths) {
		setPaths(Arrays.asList(paths));
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