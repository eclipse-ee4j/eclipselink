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
import org.eclipse.persistence.jpa.jpql.ExpressionTools;

/**
 * This {@link StateObject} simply holds onto a string.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class SimpleStateObject extends AbstractStateObject {

	/**
	 * The text held by this state object.
	 */
	private String text;

	/**
	 * Notifies the text property has changed.
	 */
	public static final String TEXT_PROPERTY = "text";

	/**
	 * Creates a new <code>SimpleStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected SimpleStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>SimpleStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param text The text held by this state object
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected SimpleStateObject(StateObject parent, String text) {
		super(parent);
		this.text = text;
	}

	/**
	 * Returns the text held by this state object.
	 *
	 * @return This model's text value
	 */
	public String getText() {
		return text;
	}

	/**
	 * Determines whether this state object is holding a non-empty string.
	 *
	 * @return <code>true</code> if the text is non-empty; <code>false</code> otherwise
	 */
	public boolean hasText() {
		return !ExpressionTools.stringIsEmpty(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			SimpleStateObject simple = (SimpleStateObject) stateObject;
			return ExpressionTools.valuesAreEqual(text, simple.text);
		}

		return false;
	}

	/**
	 * Sets the text held by this state object.
	 *
	 * @param text This model's text value
	 */
	public void setText(String text) {
		String oldText = this.text;
		this.text = text;
		firePropertyChanged(TEXT_PROPERTY, oldText, text);
	}

	/**
	 * Sets the text to the given value without notifying the listeners.
	 *
	 * @param text This model's text value
	 */
	protected void setTextInternally(String text) {
		this.text = text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		if (text != null) {
			writer.append(text);
		}
	}
}