/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql;

import java.util.Arrays;
import org.eclipse.persistence.jpa.jpql.BasicRefactoringTool;
import org.eclipse.persistence.jpa.jpql.RefactoringDelta;
import org.eclipse.persistence.jpa.jpql.TextEdit;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLGrammarTestHelper;

import static org.junit.Assert.*;

/**
 * The abstract definition of a unit-test that tests {@link org.eclipse.persistence.jpa.jpql.
 * RefactoringTool RefactoringTool}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractBasicRefactoringToolTest extends JPQLCoreTest {

	@JPQLGrammarTestHelper
	private JPQLGrammar jpqlGrammar;

	protected JPQLGrammar getGrammar() {
		return jpqlGrammar;
	}

	protected final void testChange(BasicRefactoringTool refactoringTool,
	                                String jpqlQuery,
	                                String expectedJPQLQuery,
	                                int offset,
	                                String oldValue,
	                                String newValue) {

		RefactoringDelta delta = refactoringTool.getDelta();
		assertNotNull("The RefactoringDelta cannot be null", delta);
		assertEquals("One MultiTextEdit should have been created", 1, delta.size());

		IterableListIterator<TextEdit> textEdits = delta.textEdits();
		assertNotNull("The list of Edit cannot be null", textEdits);

		TextEdit textEdit = textEdits.next();
		assertNotNull("The Edit cannot be null", textEdit);

		assertEquals(offset,            textEdit.getOffset());
		assertEquals(oldValue.length(), textEdit.getLength());
		assertEquals(oldValue,          textEdit.getOldValue());
		assertEquals(newValue,          textEdit.getNewValue());

		assertTrue("The TextEdit's range is invalid", offset + oldValue.length() < expectedJPQLQuery.length());

		assertEquals(expectedJPQLQuery, delta.applyChanges());
		assertFalse(delta.hasTextEdits());
		assertFalse(delta.textEdits().hasNext());
	}

	protected final void testChanges(BasicRefactoringTool refactoringTool,
	                                 String jpqlQuery,
	                                 String expectedJPQLQuery,
	                                 String oldValue,
	                                 String newValue,
	                                 int... offsets) {

		String[] oldValues = new String[offsets.length];
		String[] newValues = new String[offsets.length];

		Arrays.fill(oldValues, oldValue);
		Arrays.fill(newValues, newValue);

		testChanges(refactoringTool, jpqlQuery, expectedJPQLQuery, offsets, oldValues, newValues);
	}

	protected final void testChanges(BasicRefactoringTool refactoringTool,
	                                 String jpqlQuery,
	                                 String expectedJPQLQuery,
	                                 int[] offsets,
	                                 String[] oldValues,
	                                 String[] newValues) {

		assertEquals(oldValues.length, newValues.length);
		assertEquals(oldValues.length, offsets.length);

		RefactoringDelta delta = refactoringTool.getDelta();
		assertNotNull("The RefactoringDelta cannot be null", delta);
		assertEquals("Incorrect number of TextEdit were created", offsets.length, delta.size());

		int index = 0;

		for (TextEdit textEdit : delta.textEdits()) {
			assertNotNull("The TextEdit cannot be null", textEdit);
			assertEquals(  offsets[index],          textEdit.getOffset());
			assertEquals(oldValues[index].length(), textEdit.getLength());
			assertEquals(oldValues[index],          textEdit.getOldValue());
			assertEquals(newValues[index],          textEdit.getNewValue());
			index++;
		}

		assertEquals(expectedJPQLQuery, delta.applyChanges());
		assertFalse(delta.hasTextEdits());
		assertFalse(delta.textEdits().hasNext());
	}

	protected final void testHasNoChanges(BasicRefactoringTool refactoringTool) {
		RefactoringDelta delta = refactoringTool.getDelta();
		assertNotNull("The RefactoringDelta cannot be null", delta);
		assertEquals("No MultiTextEdit should have been created", 0, delta.size());
	}
}