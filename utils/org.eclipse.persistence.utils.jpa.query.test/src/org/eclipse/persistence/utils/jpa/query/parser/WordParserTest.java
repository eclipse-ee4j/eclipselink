/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.utils.jpa.query.parser;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class WordParserTest {
//	@Test
//	public void testEntireWord_1()
//	{
//		String query = "SELECT e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		CharSequence word = wordParser.entireWord(query.length());
//		assertEquals("e", word);
//	}
//
//	@Test
//	public void testEntireWord_2()
//	{
//		String query = "SELECT e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		CharSequence word = wordParser.entireWord(0);
//		assertEquals("SELECT", word);
//	}
//
//	@Test
//	public void testEntireWord_3()
//	{
//		String query = "SELECT AVG(e.age) FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SEL".length();
//		CharSequence word = wordParser.entireWord(position);
//		assertEquals("SELECT", word);
//	}
//
//	@Test
//	public void testEntireWord_4()
//	{
//		String query = "SELECT AVG(e.age) FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SELECT".length();
//		CharSequence word = wordParser.entireWord(position);
//		assertEquals("SELECT", word);
//	}
//
//	@Test
//	public void testEntireWord_5()
//	{
//		String query = "SELECT AVG(e.age) FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SELECT ".length();
//		CharSequence word = wordParser.entireWord(position);
//		assertEquals("AVG", word);
//	}
//
//	@Test
//	public void testEntireWord_6()
//	{
//		String query = "SELECT AVG(e.age) FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SELECT AVG(".length();
//		CharSequence word = wordParser.entireWord(position);
//		assertEquals("e.age", word);
//	}
//
//	@Test
//	public void testFirstWord_1()
//	{
//		String query = "SELECT e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		CharSequence word = wordParser.firstWord();
//		assertEquals("SELECT", word);
//	}
//
//	@Test
//	public void testFirstWord_2()
//	{
//		String query = "SELECT( e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		CharSequence word = wordParser.firstWord();
//		assertEquals("SELECT", word);
//	}
//
//	@Test
//	public void testFirstWord_3()
//	{
//		String query = "SELECT, e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		CharSequence word = wordParser.firstWord();
//		assertEquals("SELECT", word);
//	}
//
//	@Test
//	public void testFirstWord_4()
//	{
//		String query = "SELECT. e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		CharSequence word = wordParser.firstWord();
//		assertEquals("SELECT.", word);
//	}

	@Test
	public void testPartialWord_1() {
		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		CharSequence word = wordParser.partialWord(0);
		assertEquals(AbstractExpression.EMPTY_STRING, word);
	}

	@Test
	public void testPartialWord_2() {
		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		CharSequence word = wordParser.partialWord(1);
		assertEquals("S", word);
	}

	@Test
	public void testPartialWord_3() {
		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SELECT ".length();
		CharSequence word = wordParser.partialWord(position);
		assertEquals(AbstractExpression.EMPTY_STRING, word);
	}

	@Test
	public void testPartialWord_4() {
		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SELECT e".length();
		CharSequence word = wordParser.partialWord(position);
		assertEquals("e", word);
	}

	@Test
	public void testPreviousWord_1() {
		String query = "SELECT AVG(e.age) FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SEL".length();
		CharSequence word = wordParser.previousWord(position);
		assertEquals(AbstractExpression.EMPTY_STRING, word);
	}

	@Test
	public void testPreviousWord_2() {
		String query = "SELECT AVG(e.age) FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SELECT ".length();
		CharSequence word = wordParser.previousWord(position);
		assertEquals("SELECT", word);
	}

	@Test
	public void testPreviousWord_3() {
		String query = "SELECT AVG(e.age) FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SELECT A".length();
		CharSequence word = wordParser.previousWord(position);
		assertEquals("SELECT", word);
	}

	@Test
	public void testPreviousWord_4() {
		String query = "SELECT AVG(e.age) FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SELECT AVG(".length();
		CharSequence word = wordParser.previousWord(position);
		assertEquals("SELECT", word);
	}

//	@Test
//	public void testWord_01()
//	{
//		String query = "SELECT e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SELECT".length();
//		CharSequence word = wordParser.word(position);
//		assertEquals(StringTools.EMPTY_STRING, word);
//	}
//
//	@Test
//	public void testWord_02()
//	{
//		String query = "SELECT e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		CharSequence word = wordParser.word(query.length());
//		assertEquals(StringTools.EMPTY_STRING, word);
//	}
//
//	@Test
//	public void testWord_03()
//	{
//		String query = "SELECT OBJECT(e), e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SELECT OBJECT".length();
//		CharSequence word = wordParser.word(position);
//		assertEquals(StringTools.EMPTY_STRING, word);
//	}
//
//	@Test
//	public void testWord_04()
//	{
//		String query = "SELECT OBJECT(e), e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SELECT OBJECT(".length();
//		String word = wordParser.word(position);
//		assertEquals("e", word);
//	}
//
//	@Test
//	public void testWord_05()
//	{
//		String query = "SELECT OBJECT(e), e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SELECT OBJECT(e".length();
//		String word = wordParser.word(position);
//		assertEquals(StringTools.EMPTY_STRING, word);
//	}
//
//	@Test
//	public void testWord_06()
//	{
//		String query = "SELECT OBJECT(e), e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SELECT OBJECT(e)".length();
//		String word = wordParser.word(position);
//		assertEquals(StringTools.EMPTY_STRING, word);
//	}
//
//	@Test
//	public void testWord_07()
//	{
//		String query = "SELECT OBJECT(e), e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SELECT OBJECT(e),".length();
//		String word = wordParser.word(position);
//		assertEquals(StringTools.EMPTY_STRING, word);
//	}
//
//	@Test
//	public void testWord_08()
//	{
//		String query = "SELECT OBJECT(e), e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SELECT OBJECT(e), ".length();
//		String word = wordParser.word(position);
//		assertEquals("e", word);
//	}
//
//	@Test
//	public void testWord_09()
//	{
//		String query = "SELECT OBJECT(e), e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SELECT OBJECT(e), e".length();
//		String word = wordParser.word(position);
//		assertEquals(StringTools.EMPTY_STRING, word);
//	}
//
//	@Test
//	public void testWord_10()
//	{
//		String query = "SELECT OBJECT(e), e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		String word = wordParser.word(query.length());
//		assertEquals(StringTools.EMPTY_STRING, word);
//	}
//
//	@Test
//	public void testWord_11()
//	{
//		String query = "SELECT e FROM Employee e";
//		WordParser wordParser = new WordParser(query);
//
//		int position = "SEL".length();
//		CharSequence  word = wordParser.word(position);
//		assertEquals("ECT", word);
//	}
}