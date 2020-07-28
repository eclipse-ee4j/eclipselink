/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ListIterable;
import org.eclipse.persistence.jpa.jpql.utility.iterable.SnapshotCloneListIterable;

/**
 * The default implementation of {@link RefactoringDelta} which contains the {@link TextEdit} that
 * were creating during the refactoring of a JPQL query.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class DefaultRefactoringDelta implements RefactoringDelta {

    /**
     * The JPQL query or JPQL fragment that will be traversed when refactoring operations will be executed.
     */
    private CharSequence jpqlQuery;

    /**
     * The list of {@link TextEdit} objects that have been added by refactoring operations.
     */
    private List<TextEdit> textEdits;

    /**
     * Creates a new <code>DefaultRefactoringDelta</code>.
     *
     * @param jpqlQuery The JPQL query or JPQL fragment that will be traversed when refactoring
     * operations will be executed
     * @exception NullPointerException The JPQL query cannot be <code>null</code>
     */
    public DefaultRefactoringDelta(CharSequence jpqlQuery) {
        super();
        initialize(jpqlQuery);
    }

    /**
     * Adds the given {@link TextEdit} at the right position. The list will be kept ordered, meaning
     * the insertion index is based on the offset, from the biggest value to the smallest value.
     *
     * @param textEdit The {@link TextEdit} to add
     * @exception NullPointerException The {@link TextEdit} cannot be <code>null</code>
     */
    public void addTextEdit(TextEdit textEdit) {

        Assert.isNotNull(textEdit, "The TextEdit cannot be null");

        if (textEdits.isEmpty()) {
            textEdits.add(textEdit);
        }
        else {
            int position = calculateInsertionPosition(textEdit);
            textEdits.add(position, textEdit);
        }
    }

    /**
     * Adds the given collection of {@link TextEdit} objects. The list will be kept ordered, meaning
     * the insertion index is based on the offset, from the biggest value to the smallest value.
     *
     * @param textEdits The collection of {@link TextEdit} objects to add
     * @exception NullPointerException The given {@link Iterable} or one of the child {@link TextEdit}
     * was <code>null</code>
     */
    public void addTextEdits(Iterable<? extends TextEdit> textEdits) {
        Assert.isNotNull(textEdits, "The Iterable<TextEdit> cannot be null");
        for (TextEdit textEdit : textEdits) {
            addTextEdit(textEdit);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String applyChanges() {

        // Nothing to apply
        if (textEdits.isEmpty()) {
            return jpqlQuery.toString();
        }

        StringBuilder result = new StringBuilder(jpqlQuery);

        // The TextEdits are already in the right order (biggest offset to smallest offset),
        // so simply iterate them in order and replace the segments
        for (int index = 0, count = size(); index < count; index++) {
            TextEdit textEdit = textEdits.get(index);
            int offset = textEdit.getOffset();
            result.replace(offset, offset + textEdit.getLength(), textEdit.getNewValue());
        }

        textEdits.clear();
        return result.toString();
    }

    /**
     * Calculates the insertion position for the given {@link TextEdit} based on those already registered
     *
     * @param textEdit The {@link TextEdit} for which its insertion position will be calculated
     * @return The insertion position for the given {@link TextEdit}
     */
    protected int calculateInsertionPosition(TextEdit textEdit) {

        int position = size();

        for (int index = position; --index >= 0; ) {
            TextEdit edit = textEdits.get(index);
            if (textEdit.getOffset() > edit.getOffset()) {
                position = index;
            }
            else {
                break;
            }
        }

        return position;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasTextEdits() {
        return !textEdits.isEmpty();
    }

    /**
     * Initializes this <code>DefaultRefactoringDelta</code>.
     *
     * @param jpqlQuery The JPQL query or JPQL fragment that will be traversed when refactoring
     * operations will be executed
     * @exception NullPointerException The JPQL query cannot be <code>null</code>
     */
    protected void initialize(CharSequence jpqlQuery) {
        Assert.isNotNull(jpqlQuery, "The JPQL query cannot be null");
        this.jpqlQuery = jpqlQuery;
        this.textEdits = new LinkedList<TextEdit>();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return textEdits.size();
    }

    /**
     * {@inheritDoc}
     */
    public ListIterable<TextEdit> textEdits() {
        return new SnapshotCloneListIterable<TextEdit>(textEdits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return textEdits.toString();
    }
}
