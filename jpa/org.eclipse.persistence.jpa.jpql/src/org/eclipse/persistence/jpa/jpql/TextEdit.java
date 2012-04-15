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
 * A <code>TextEdit</code> contains the information of a change that can be made to the JPQL query
 * after performing a refactoring operation. {@link TextRange} objects are stored in a {@link
 * RefactoringDelta}.
 *
 * @see BasicRefactoringTool
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface TextEdit {

	/**
	 * Returns the length of the text to replace with the new value.
	 *
	 * @return The old value's length
	 */
	int getLength();

	/**
	 * Returns the new value that should replace the old value.
	 *
	 * @return The value to replace the old value
	 */
	String getNewValue();

	/**
	 * Returns the location of the old value within the text.
	 *
	 * @return The location of the old value within the text
	 */
	int getOffset();

	/**
	 * Returns the value that was found within the text that should be replaced by the new value.
	 *
	 * @return The value to replace
	 */
	String getOldValue();
}