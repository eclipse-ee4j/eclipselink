/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools;

/**
 * A <code>TextEdit</code> contains the information of a change that can be made to the JPQL query
 * after performing a refactoring operation.
 * <a href="http://git.eclipse.org/c/dali/webtools.dali.git/tree/common/plugins/org.eclipse.jpt.common.core/src/org/eclipse/jpt/common/core/utility/TextRange.java">TextRange</a>
 * objects are stored in a {@link RefactoringDelta}.
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
