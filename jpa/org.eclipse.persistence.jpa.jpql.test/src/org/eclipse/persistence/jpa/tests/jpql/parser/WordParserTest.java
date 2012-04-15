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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;
import org.eclipse.persistence.jpa.jpql.WordParser.WordType;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class WordParserTest {

	@Test
	public void testCharacter_01() {

		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		char character = wordParser.character();
		assertEquals('S', character);
	}

	@Test
	public void testCharacter_02() {

		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);
		wordParser.setPosition("SELECT e FROM Employee ".length());

		char character = wordParser.character();
		assertEquals('e', character);
	}

	@Test
	public void testCharacter_03() {

		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);
		wordParser.setPosition(query.length());

		char character = wordParser.character();
		assertEquals('\0', character);
	}

	@Test
	public void testCharacter_04() {

		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);
		wordParser.setPosition(Integer.MAX_VALUE);

		char character = wordParser.character();
		assertEquals('\0', character);
	}

	@Test
	public void testCharacter_05() {

		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);
		wordParser.setPosition(-10);

		char character = wordParser.character();
		assertEquals('S', character);
	}

	@Test
	public void testEntireWord_01() {

		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		CharSequence word = wordParser.entireWord(query.length());
		assertEquals("e", word);
	}

	@Test
	public void testEntireWord_02() {

		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		CharSequence word = wordParser.entireWord(0);
		assertEquals("SELECT", word);
	}

	@Test
	public void testEntireWord_03() {

		String query = "SELECT AVG(e.age) FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SEL".length();
		CharSequence word = wordParser.entireWord(position);
		assertEquals("SELECT", word);
	}

	@Test
	public void testEntireWord_04() {

		String query = "SELECT AVG(e.age) FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SELECT".length();
		CharSequence word = wordParser.entireWord(position);
		assertEquals("SELECT", word);
	}

	@Test
	public void testEntireWord_05() {

		String query = "SELECT AVG(e.age) FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SELECT ".length();
		CharSequence word = wordParser.entireWord(position);
		assertEquals("AVG", word);
	}

	@Test
	public void testEntireWord_06() {

		String query = "SELECT AVG(e.age) FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SELECT AVG(".length();
		CharSequence word = wordParser.entireWord(position);
		assertEquals("e.age", word);
	}

	@Test
	public void testPartialWord_01() {

		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		CharSequence word = wordParser.partialWord(0);
		assertEquals(ExpressionTools.EMPTY_STRING, word);
	}

	@Test
	public void testPartialWord_02() {

		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		CharSequence word = wordParser.partialWord(1);
		assertEquals("S", word);
	}

	@Test
	public void testPartialWord_03() {

		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SELECT ".length();
		CharSequence word = wordParser.partialWord(position);
		assertEquals(ExpressionTools.EMPTY_STRING, word);
	}

	@Test
	public void testPartialWord_04() {

		String query = "SELECT e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		int position = "SELECT e".length();
		CharSequence word = wordParser.partialWord(position);
		assertEquals("e", word);
	}

	@Test
	public void testWord_01() {

		String query = "SELECT e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT");

		CharSequence word = wordParser.word();
		assertEquals(ExpressionTools.EMPTY_STRING, word);
	}

	@Test
	public void testWord_02() {

		String query = "SELECT( e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		CharSequence word = wordParser.word();
		assertEquals("SELECT", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_03() {

		String query = "SELECT, e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		CharSequence word = wordParser.word();
		assertEquals("SELECT", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_04() {

		String query = "SELECT. e FROM Employee e";
		WordParser wordParser = new WordParser(query);

		CharSequence word = wordParser.word();
		assertEquals("SELECT.", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_05() {

		String query = "SELECT e!3 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("e!3", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_06() {

		String query = "SELECT e.employee.name FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("e.employee.name", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_07() {

		String query = "SELECT -7 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("-", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_08() {

		String query = "SELECT -7.3 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("-", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_09() {

		String query = "SELECT -7.3, e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("-", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_10() {

		String query = "SELECT -7.3 + e.age FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("-", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_11() {

		String query = "SELECT 'JPQL' FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("'JPQL'", word);
		assertSame(WordType.STRING_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_12() {

		String query = "SELECT ?1 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("?1", word);
	}

	@Test
	public void testWord_13() {

		String query = "SELECT ?1, FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("?1", word);
	}

	@Test
	public void testWord_14() {

		String query = "SELECT ?1, FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("?1", word);
	}

	@Test
	public void testWord_15() {

		String query = "SELECT ?12, FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("?12", word);
	}

	@Test
	public void testWord_16() {

		String query = "SELECT :name, FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals(":name", word);
	}

	@Test
	public void testWord_17() {

		String query = "SELECT e FROM Employee e WHERE e.age > 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE e.age ");

		CharSequence word = wordParser.word();
		assertEquals(">", word);
	}

	@Test
	public void testWord_18() {

		String query = "SELECT e FROM Employee e WHERE e.age != 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE e.age ");

		CharSequence word = wordParser.word();
		assertEquals("!=", word);
	}

	@Test
	public void testWord_19() {

		String query = "SELECT e FROM Employee e WHERE e.age !eaf 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE e.age ");

		CharSequence word = wordParser.word();
		assertEquals("!eaf", word);
	}

	@Test
	public void testWord_20() {

		String query = "SELECT e FROM Employee e WHERE e.age <> 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE e.age ");

		CharSequence word = wordParser.word();
		assertEquals("<>", word);
	}

	@Test
	public void testWord_21() {

		String query = "SELECT e FROM Employee e WHERE e.age <= 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE e.age ");

		CharSequence word = wordParser.word();
		assertEquals("<=", word);
	}

	@Test
	public void testWord_22() {

		String query = "SELECT e FROM Employee e WHERE e.age<= 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE ");

		CharSequence word = wordParser.word();
		assertEquals("e.age", word);
	}

	@Test
	public void testWord_23() {

		String query = "SELECT e FROM Employee e WHERE e.age!= 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE ");

		CharSequence word = wordParser.word();
		assertEquals("e.age", word);
	}

	@Test
	public void testWord_24() {

		String query = "SELECT e FROM Employee e WHERE e.age> 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE ");

		CharSequence word = wordParser.word();
		assertEquals("e.age", word);
	}

	@Test
	public void testWord_25() {

		String query = "SELECT object(e) FROM Employee e WHERE e.age> 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("object", word);
	}

	@Test
	public void testWord_26() {

		String query = "SELECT object(e) FROM Employee e WHERE e.age> 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT object(");

		CharSequence word = wordParser.word();
		assertEquals("e", word);
	}

	@Test
	public void testWord_27() {

		String query = "SELECT object(e FROM Employee e WHERE e.age> 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT object(");

		CharSequence word = wordParser.word();
		assertEquals("e", word);
	}

	@Test
	public void testWord_28() {

		String query = "SELECT object(e+e.age FROM Employee e WHERE e.age> 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT object(");

		CharSequence word = wordParser.word();
		assertEquals("e", word);
	}

	@Test
	public void testWord_29() {

		String query = "SELECT object(e+e.age FROM Employee e WHERE e.age> 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT object(e");

		CharSequence word = wordParser.word();
		assertEquals("+", word);
	}

	@Test
	public void testWord_30() {

		String query = "SELECT object(e++4 FROM Employee e WHERE e.age> 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT object(e");

		CharSequence word = wordParser.word();
		assertEquals("+", word);
	}

	@Test
	public void testWord_31() {

		String query = "SELECT object(e++4 FROM Employee e WHERE e.age> 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT object(e+");

		CharSequence word = wordParser.word();
		assertEquals("+", word);
	}

	@Test
	public void testWord_32() {

		String query = "SELECT new java.lang.String1_2";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT new ");

		CharSequence word = wordParser.word();
		assertEquals("java.lang.String1_2", word);
	}

	@Test
	public void testWord_33() {

		String query = "SELECT new java.lang.String1_2(";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT new ");

		CharSequence word = wordParser.word();
		assertEquals("java.lang.String1_2", word);
	}

	@Test
	public void testWord_34() {

		String query = "SELECT {d 'yyyy-mm-dd'} from Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("{", word);
	}

	@Test
	public void testWord_35() {

		String query = "SELECT OBJECT(e), e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT OBJECT".length());

		CharSequence word = wordParser.word();
		assertEquals(ExpressionTools.EMPTY_STRING, word);
	}

	@Test
	public void testWord_36() {

		String query = "SELECT OBJECT(e), e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT OBJECT(");

		String word = wordParser.word();
		assertEquals("e", word);
	}

	@Test
	public void testWord_37() {

		String query = "SELECT OBJECT(e), e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT OBJECT(e");

		String word = wordParser.word();
		assertEquals(ExpressionTools.EMPTY_STRING, word);
	}

	@Test
	public void testWord_38() {

		String query = "SELECT OBJECT(e), e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT OBJECT(e)");

		String word = wordParser.word();
		assertEquals(ExpressionTools.EMPTY_STRING, word);
	}

	@Test
	public void testWord_39() {

		String query = "SELECT OBJECT(e), e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT OBJECT(e),");

		String word = wordParser.word();
		assertEquals(ExpressionTools.EMPTY_STRING, word);
	}

	@Test
	public void testWord_40() {

		String query = "SELECT OBJECT(e), e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT OBJECT(e), ");

		String word = wordParser.word();
		assertEquals("e", word);
	}

	@Test
	public void testWord_41() {

		String query = "SELECT OBJECT(e), e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT OBJECT(e), e");

		String word = wordParser.word();
		assertEquals(ExpressionTools.EMPTY_STRING, word);
	}

	@Test
	public void testWord_42() {

		String query = "SELECT OBJECT(e), e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward(query.length());

		String word = wordParser.word();
		assertEquals(ExpressionTools.EMPTY_STRING, word);
	}

	@Test
	public void testWord_43() {

		String query = "SELECT e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SEL");

		CharSequence  word = wordParser.word();
		assertEquals("ECT", word);
	}

	@Test
	public void testWord_44() {

		String query = "SELECT +4.2e2 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT +");

		CharSequence  word = wordParser.word();
		assertEquals("4.2e2", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_45() {

		String query = "SELECT +4.2e-2 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT +");

		CharSequence  word = wordParser.word();
		assertEquals("4.2e-2", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_46() {

		String query = "SELECT +4.2e+2 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT +");

		CharSequence  word = wordParser.word();
		assertEquals("4.2e+2", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_47() {

		String query = "SELECT +4.2 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT +");

		CharSequence  word = wordParser.word();
		assertEquals("4.2", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_48() {

		String query = "SELECT -4.2 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT -");

		CharSequence  word = wordParser.word();
		assertEquals("4.2", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_49() {

		String query = "SELECT -4. FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT -");

		CharSequence  word = wordParser.word();
		assertEquals("4.", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_50() {

		String query = "SELECT -4.abs FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT -");

		CharSequence  word = wordParser.word();
		assertEquals("4.abs", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_51() {

		String query = "SELECT 4e4 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence  word = wordParser.word();
		assertEquals("4e4", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_52() {

		String query = "SELECT e FROM Employee e WHERE e.age >= 18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE e.age ");

		CharSequence word = wordParser.word();
		assertEquals(">=", word);
	}

	@Test
	public void testWord_53() {

		String query = "SELECT e FROM Employee e WHERE e.age >=18";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE e.age ");

		CharSequence word = wordParser.word();
		assertEquals(">=", word);
	}

	@Test
	public void testWord_54() {

		String query = "SELECT e FROM Employee e WHERE e.age >=(";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE e.age ");

		CharSequence word = wordParser.word();
		assertEquals(">=", word);
	}

	@Test
	public void testWord_55() {

		String query = "SELECT e FROM Employee e WHERE e.age <>(";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE e.age ");

		CharSequence word = wordParser.word();
		assertEquals("<>", word);
	}

	@Test
	public void testWord_56() {

		String query = "SELECT 4e4e FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence  word = wordParser.word();
		assertEquals("4e4e", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_57() {

		String query = "SELECT 4e4e.name.employee FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence  word = wordParser.word();
		assertEquals("4e4e.name.employee", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_58() {

		String query = "SELECT \"JPQL\" FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("\"JPQL\"", word);
		assertSame(WordType.STRING_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_59() {

		String query = "SELECT \"JPQL\"";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("\"JPQL\"", word);
		assertSame(WordType.STRING_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_60() {

		String query = "SELECT 'JPQL'";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("'JPQL'", word);
		assertSame(WordType.STRING_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_61() {

		String query = "SELECT 'JPQL'+e.name FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("'JPQL'", word);
		assertSame(WordType.STRING_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_62() {

		String query = "SELECT e FROM Employee e WHERE e.name = 'JPQL'";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE e.name ");

		CharSequence word = wordParser.word();
		assertEquals("=", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_63() {

		String query = "SELECT 4 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("4", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_64() {

		String query = "SELECT 4, FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("4", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_65() {

		String query = "SELECT 4f FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("4f", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_66() {

		String query = "SELECT 4.1f FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("4.1f", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_67() {

		String query = "SELECT -4.1D FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT -");

		CharSequence word = wordParser.word();
		assertEquals("4.1D", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_68() {

		String query = "SELECT -4.1D";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT -");

		CharSequence word = wordParser.word();
		assertEquals("4.1D", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_69() {

		String query = "SELECT -4.1Dasd";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT -");

		CharSequence word = wordParser.word();
		assertEquals("4.1Dasd", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_70() {

		String query = "SELECT -LENGTH(e.name) FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("-", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_71() {

		String query = "SELECT :e!3 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals(":e!3", word);
		assertSame(WordType.INPUT_PARAMETER, wordParser.getWordType());
	}

	@Test
	public void testWord_72() {

		String query = "SELECT -AVG(e.name) FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals("-", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_73() {

		String query = "SELECT .name FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence word = wordParser.word();
		assertEquals(".name", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_74() {

		String query = "SELECT e FROM Employee e HAVING COUNT(e)>1";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e HAVING COUNT(e)");

		CharSequence word = wordParser.word();
		assertEquals(">", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_75() {

		String query = "SELECT e FROM Employee e HAVING COUNT(e)<1";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e HAVING COUNT(e)");

		CharSequence word = wordParser.word();
		assertEquals("<", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_76() {

		String query = "SELECT e FROM Employee e HAVING 1+1.1-2";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e HAVING ");

		CharSequence word = wordParser.word();
		assertEquals("1", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_77() {

		String query = "SELECT e FROM Employee e HAVING 1+1.1-2";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e HAVING 1");

		CharSequence word = wordParser.word();
		assertEquals("+", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_78() {

		String query = "SELECT e FROM Employee e HAVING 1+1.1-2";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e HAVING 1+");

		CharSequence word = wordParser.word();
		assertEquals("1.1", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_79() {

		String query = "SELECT e FROM Employee e HAVING 1+1.1-2";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e HAVING 1+1.1");

		CharSequence word = wordParser.word();
		assertEquals("-", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_80() {

		String query = "SELECT e FROM Employee e WHERE e.name = all.persistence.eclipse.jpa.jpql.TYPE.FULL_TIME";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE e.name = ");

		CharSequence word = wordParser.word();
		assertEquals("all.persistence.eclipse.jpa.jpql.TYPE.FULL_TIME", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_81() {

		String query = "SELECT e FROM Employee e WHERE all.persistence.eclipse.jpa.jpql.TYPE.FULL_TIME = e.name";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE ");

		CharSequence word = wordParser.word();
		assertEquals("all.persistence.eclipse.jpa.jpql.TYPE.FULL_TIME", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_82() {

		String query = "SELECT e FROM Employee e WHERE all.persistence.eclipse.jpa.jpql.TYPE.FULL_TIME= e.name";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT e FROM Employee e WHERE ");

		CharSequence word = wordParser.word();
		assertEquals("all.persistence.eclipse.jpa.jpql.TYPE.FULL_TIME", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_83() {

		String query = "SELECT +4.2e+2d FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT +");

		CharSequence  word = wordParser.word();
		assertEquals("4.2e+2d", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_84() {

		String query = "SELECT +4.2e+2l FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT +");

		CharSequence  word = wordParser.word();
		assertEquals("4.2e+2l", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_85() {

		String query = "SELECT 4e+2F FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence  word = wordParser.word();
		assertEquals("4e+2F", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_86() {

		String query = "SELECT 2L FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence  word = wordParser.word();
		assertEquals("2L", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_87() {

		String query = "SELECT 2l FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence  word = wordParser.word();
		assertEquals("2l", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_88() {

		String query = "SELECT 2l+2 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence  word = wordParser.word();
		assertEquals("2l", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_89() {

		String query = "SELECT 0xA FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence  word = wordParser.word();
		assertEquals("0xA", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_90() {

		String query = "SELECT 0x1.02ADP+2 FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT ");

		CharSequence  word = wordParser.word();
		assertEquals("0x1.02ADP+2", word);
		assertSame(WordType.NUMERIC_LITERAL, wordParser.getWordType());
	}

	@Test
	public void testWord_91() {

		String query = "SELECT TREAT(e.project AS LargeProject).name FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT TREAT(e.project AS LargeProject)");

		CharSequence  word = wordParser.word();
		assertEquals(".name", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}

	@Test
	public void testWord_92() {

		String query = "SELECT TREAT(e.project AS LargeProject).endDate FROM Employee e";

		WordParser wordParser = new WordParser(query);
		wordParser.moveForward("SELECT TREAT(e.project AS LargeProject)");

		CharSequence  word = wordParser.word();
		assertEquals(".endDate", word);
		assertSame(WordType.WORD, wordParser.getWordType());
	}
}