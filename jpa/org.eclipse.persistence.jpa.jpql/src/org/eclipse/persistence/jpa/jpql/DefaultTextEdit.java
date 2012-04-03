/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

/**
 * The default implementation of a {@link Text}, which contains the location of the change within
 * the JPQL query (offset) and the old and new values.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class DefaultTextEdit implements TextEdit {

	/**
	 * The new value that should replace the old value.
	 */
	private String newValue;

	/**
	 * The location of the old value within the text.
	 */
	private int offset;

	/**
	 * The value that was found within the text that should be replaced by the new value.
	 */
	private String oldValue;

	/**
	 * Creates a new <code>DefaultTextEdit</code>.
	 *
	 * @param offset The location of the old value within the text
	 * @param oldValue the value that was found within the text that should be replaced by the new value
	 * @param newValue The new value that should replace the old value
	 */
	public DefaultTextEdit(int offset, String oldValue, String newValue) {
		super();
		this.offset   = offset;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getLength() {
		return oldValue.length();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getNewValue() {
		return newValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getOldValue() {
		return oldValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(offset);
		sb.append("] ");
		sb.append(oldValue);
		sb.append(" -> ");
		sb.append(newValue);
		return sb.toString();
	}
}