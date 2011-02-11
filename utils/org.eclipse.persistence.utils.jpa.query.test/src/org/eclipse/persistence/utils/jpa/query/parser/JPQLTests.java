/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

import org.eclipse.persistence.utils.jpa.query.spi.IJPAVersion;
import org.junit.Test;

import static org.eclipse.persistence.utils.jpa.query.parser.JPQLQueries.*;
import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class JPQLTests
{
	/**
	 * Parses the given JPQL query and tests its generated string with the given
	 * query, which will be formatted first.
	 *
	 * @param actualQuery The JPQL query to parse into a parsed tree
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String actualQuery)
	{
		return buildQuery(actualQuery, IJPAVersion.VERSION_2_0);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given
	 * query, which will be formatted first.
	 *
	 * @param actualQuery The JPQL query to parse into a parsed tree
	 * @param tolerant Determines if the parsing system should be tolerant,
	 * meaning if it should try to parse invalid or incomplete queries
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String actualQuery, boolean tolerant)
	{
		return buildQuery(actualQuery, IJPAVersion.VERSION_2_0, tolerant);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given
	 * query, which will be formatted first.
	 *
	 * @param actualQuery The JPQL query to parse into a parsed tree
	 * @param version The JPA version used for parsing the query
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String actualQuery,
	                                        IJPAVersion version)
	{
		return buildQuery(actualQuery, version, QueryStringFormatter.DEFAULT);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given
	 * query, which will be formatted first.
	 *
	 * @param actualQuery The JPQL query to parse into a parsed tree
	 * @param version The JPA version used for parsing the query
	 * @param tolerant Determines if the parsing system should be tolerant,
	 * meaning if it should try to parse invalid or incomplete queries
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String actualQuery,
	                                        IJPAVersion version,
	                                        boolean tolerance)
	{
		return buildQuery(actualQuery, version, tolerance, QueryStringFormatter.DEFAULT);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given
	 * query, which will be formatted first.
	 *
	 * @param actualQuery The JPQL query to parse into a parsed tree
	 * @param tolerant Determines if the parsing system should be tolerant,
	 * meaning if it should try to parse invalid or incomplete queries
	 * @param version The JPA version used for parsing the query
	 * @param formatter This formatter is used to personalized the formatting of
	 * the JPQL query before it is used to test the generated string
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String actualQuery,
	                                        IJPAVersion version,
	                                        boolean tolerant,
	                                        QueryStringFormatter formatter)
	{
		// Remove any extra whitespace and make all identifiers upper case
		String realQuery = formatQuery(actualQuery);
		realQuery = formatter.format(realQuery);

		// Create the parsed tree of the JPQL query
		JPQLExpression jpqlExpression = new JPQLExpression(actualQuery, version, tolerant);

		// Make sure the query was correctly parsed
		assertEquals(realQuery, jpqlExpression.toParsedText());

		return jpqlExpression;
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given
	 * query, which will be formatted first.
	 *
	 * @param actualQuery The JPQL query to parse into a parsed tree
	 * @param version The JPA version used for parsing the query
	 * @param formatter This formatter is used to personalized the formatting of
	 * the JPQL query before it is used to test the generated string
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String actualQuery,
	                                        IJPAVersion version,
	                                        QueryStringFormatter formatter)
	{
		return buildQuery(actualQuery, version, true, formatter);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given
	 * query, which will be formatted first.
	 *
	 * @param actualQuery The JPQL query to parse into a parsed tree
	 * @param formatter This formatter is used to personalized the formatting of
	 * the JPQL query before it is used to test the generated string
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String actualQuery,
	                                        QueryStringFormatter formatter)
	{
		return buildQuery(actualQuery, IJPAVersion.VERSION_2_0, formatter);
	}

	private QueryStringFormatter buildFormatter_22()
	{
		return new QueryStringFormatter()
		{
			@Override
			public String format(String query)
			{
				query = query.replaceAll("\\s\\(", "(");
				return query;
			}
		};
	}

	private QueryStringFormatter buildQueryFormatter_191()
	{
		return new QueryStringFormatter()
		{
			@Override
			public String format(String query)
			{
				return query.replace(",", ", ");
			}
		};
	}

	private QueryStringFormatter buildQueryFormatter_44()
	{
		return new QueryStringFormatter()
		{
			@Override
			public String format(String query)
			{
				return query.replace("'NEW", "'New");
			}
		};
	}

	@Test
	public void testQuery_001()
	{
		buildQuery(query_001());
	}

	@Test
	public void testQuery_002()
	{
		buildQuery(query_002());
	}

	@Test
	public void testQuery_003()
	{
		buildQuery(query_003());
	}

	@Test
	public void testQuery_004()
	{
		buildQuery(query_004());
	}

	@Test
	public void testQuery_005()
	{
		buildQuery(query_005());
	}

	@Test
	public void testQuery_006()
	{
		buildQuery(query_006());
	}

	@Test
	public void testQuery_007()
	{
		buildQuery(query_007());
	}

	@Test
	public void testQuery_008()
	{
		buildQuery(query_008());
	}

	@Test
	public void testQuery_009()
	{
		buildQuery(query_009());
	}

	@Test
	public void testQuery_010()
	{
		buildQuery(query_010());
	}

	@Test
	public void testQuery_011()
	{
		buildQuery(query_011());
	}

	@Test
	public void testQuery_012()
	{
		buildQuery(query_012());
	}

	@Test
	public void testQuery_013()
	{
		buildQuery(query_013());
	}

	@Test
	public void testQuery_014()
	{
		buildQuery(query_014());
	}

	@Test
	public void testQuery_015()
	{
		buildQuery(query_015());
	}

	@Test
	public void testQuery_016()
	{
		buildQuery(query_016());
	}

	@Test
	public void testQuery_017()
	{
		buildQuery(query_017());
	}

	@Test
	public void testQuery_018()
	{
		buildQuery(query_018());
	}

	@Test
	public void testQuery_019()
	{
		buildQuery(query_019());
	}

	@Test
	public void testQuery_020()
	{
		buildQuery(query_020());
	}

	@Test
	public void testQuery_021()
	{
		buildQuery(query_021());
	}

	@Test
	public void testQuery_022()
	{
		buildQuery(query_022(), buildFormatter_22());
	}

	@Test
	public void testQuery_023()
	{
		buildQuery(query_023());
	}

	@Test
	public void testQuery_024()
	{
		buildQuery(query_024());
	}

	@Test
	public void testQuery_025()
	{
		buildQuery(query_025());
	}

	@Test
	public void testQuery_026()
	{
		buildQuery(query_026());
	}

	@Test
	public void testQuery_027()
	{
		buildQuery(query_027());
	}

	@Test
	public void testQuery_028()
	{
		buildQuery(query_028());
	}

	@Test
	public void testQuery_029()
	{
		buildQuery(query_029());
	}

	@Test
	public void testQuery_030()
	{
		buildQuery(query_030());
	}

	@Test
	public void testQuery_031()
	{
		buildQuery(query_031());
	}

	@Test
	public void testQuery_032()
	{
		buildQuery(query_032());
	}

	@Test
	public void testQuery_033()
	{
		buildQuery(query_033());
	}

	@Test
	public void testQuery_034()
	{
		buildQuery(query_034());
	}

	@Test
	public void testQuery_035()
	{
		buildQuery(query_035());
	}

	@Test
	public void testQuery_036()
	{
		buildQuery(query_036());
	}

	@Test
	public void testQuery_037()
	{
		buildQuery(query_037());
	}

	@Test
	public void testQuery_038()
	{
		buildQuery(query_038());
	}

	@Test
	public void testQuery_039()
	{
		buildQuery(query_039());
	}

	@Test
	public void testQuery_040()
	{
		buildQuery(query_040());
	}

	@Test
	public void testQuery_041()
	{
		buildQuery(query_041());
	}

	@Test
	public void testQuery_042()
	{
		buildQuery(query_042(), buildQueryFormatter_44());
	}

	@Test
	public void testQuery_043()
	{
		buildQuery(query_043());
	}

	@Test
	public void testQuery_044()
	{
		buildQuery(query_044());
	}

	@Test
	public void testQuery_045()
	{
		buildQuery(query_045());
	}

	@Test
	public void testQuery_046()
	{
		buildQuery(query_046());
	}

	@Test
	public void testQuery_047()
	{
		buildQuery(query_047());
	}

	@Test
	public void testQuery_048()
	{
		buildQuery(query_048());
	}

	@Test
	public void testQuery_049()
	{
		buildQuery(query_049());
	}

	@Test
	public void testQuery_050()
	{
		buildQuery(query_050());
	}

	@Test
	public void testQuery_051()
	{
		buildQuery(query_051());
	}

	@Test
	public void testQuery_052()
	{
		buildQuery(query_052());
	}

	@Test
	public void testQuery_053()
	{
		buildQuery(query_053());
	}

	@Test
	public void testQuery_054()
	{
		buildQuery(query_054());
	}

	@Test
	public void testQuery_055()
	{
		buildQuery(query_055());
	}

	@Test
	public void testQuery_056()
	{
		buildQuery(query_056());
	}

	@Test
	public void testQuery_057()
	{
		buildQuery(query_057());
	}

	@Test
	public void testQuery_058()
	{
		buildQuery(query_058());
	}

	@Test
	public void testQuery_059()
	{
		buildQuery(query_059());
	}

	@Test
	public void testQuery_060()
	{
		buildQuery(query_060());
	}

	@Test
	public void testQuery_061()
	{
		buildQuery(query_061());
	}

	@Test
	public void testQuery_062()
	{
		buildQuery(query_062());
	}

	@Test
	public void testQuery_063()
	{
		buildQuery(query_063());
	}

	@Test
	public void testQuery_064()
	{
		buildQuery(query_064());
	}

	@Test
	public void testQuery_065()
	{
		buildQuery(query_065());
	}

	@Test
	public void testQuery_066()
	{
		buildQuery(query_066());
	}

	@Test
	public void testQuery_067()
	{
		buildQuery(query_067());
	}

	@Test
	public void testQuery_068()
	{
		buildQuery(query_068());
	}

	@Test
	public void testQuery_069()
	{
		buildQuery(query_069());
	}

	@Test
	public void testQuery_070()
	{
		buildQuery(query_070());
	}

	@Test
	public void testQuery_071()
	{
		buildQuery(query_071());
	}

	@Test
	public void testQuery_072()
	{
		buildQuery(query_072());
	}

	@Test
	public void testQuery_073()
	{
		buildQuery(query_073());
	}

	@Test
	public void testQuery_074()
	{
		buildQuery(query_074());
	}

	@Test
	public void testQuery_075()
	{
		buildQuery(query_075());
	}

	@Test
	public void testQuery_076()
	{
		buildQuery(query_076());
	}

	@Test
	public void testQuery_077()
	{
		buildQuery(query_077());
	}

	@Test
	public void testQuery_078()
	{
		buildQuery(query_078());
	}

	@Test
	public void testQuery_079()
	{
		buildQuery(query_079());
	}

	@Test
	public void testQuery_080()
	{
		buildQuery(query_080());
	}

	@Test
	public void testQuery_081()
	{
		buildQuery(query_081());
	}

	@Test
	public void testQuery_082()
	{
		buildQuery(query_082());
	}

	@Test
	public void testQuery_083()
	{
		buildQuery(query_083());
	}

	@Test
	public void testQuery_084()
	{
		buildQuery(query_084());
	}

	@Test
	public void testQuery_085()
	{
		buildQuery(query_085());
	}

	@Test
	public void testQuery_086()
	{
		buildQuery(query_086());
	}

	@Test
	public void testQuery_087()
	{
		buildQuery(query_087());
	}

	@Test
	public void testQuery_088()
	{
		buildQuery(query_088());
	}

	@Test
	public void testQuery_089()
	{
		buildQuery(query_089());
	}

	@Test
	public void testQuery_090()
	{
		buildQuery(query_090());
	}

	@Test
	public void testQuery_091()
	{
		buildQuery(query_091());
	}

	@Test
	public void testQuery_092()
	{
		buildQuery(query_092());
	}

	@Test
	public void testQuery_093()
	{
		buildQuery(query_093());
	}

	@Test
	public void testQuery_094()
	{
		buildQuery(query_094());
	}

	@Test
	public void testQuery_095()
	{
		buildQuery(query_095());
	}

	@Test
	public void testQuery_096()
	{
		buildQuery(query_096());
	}

	@Test
	public void testQuery_097()
	{
		buildQuery(query_097());
	}

	@Test
	public void testQuery_098()
	{
		buildQuery(query_098());
	}

	@Test
	public void testQuery_099()
	{
		buildQuery(query_099());
	}

	@Test
	public void testQuery_100()
	{
		buildQuery(query_100());
	}

	@Test
	public void testQuery_101()
	{
		buildQuery(query_101());
	}

	@Test
	public void testQuery_102()
	{
		buildQuery(query_102());
	}

	@Test
	public void testQuery_103()
	{
		buildQuery(query_103());
	}

	@Test
	public void testQuery_104()
	{
		buildQuery(query_104());
	}

	@Test
	public void testQuery_105()
	{
		buildQuery(query_105());
	}

	@Test
	public void testQuery_106()
	{
		buildQuery(query_106());
	}

	@Test
	public void testQuery_107()
	{
		buildQuery(query_107());
	}

	@Test
	public void testQuery_108()
	{
		buildQuery(query_108());
	}

	@Test
	public void testQuery_109()
	{
		buildQuery(query_109());
	}

	@Test
	public void testQuery_110()
	{
		buildQuery(query_110());
	}

	@Test
	public void testQuery_111()
	{
		buildQuery(query_111());
	}

	@Test
	public void testQuery_112()
	{
		buildQuery(query_112());
	}

	@Test
	public void testQuery_113()
	{
		buildQuery(query_113());
	}

	@Test
	public void testQuery_114()
	{
		buildQuery(query_114());
	}

	@Test
	public void testQuery_115()
	{
		buildQuery(query_115());
	}

	@Test
	public void testQuery_116()
	{
		buildQuery(query_116());
	}

	@Test
	public void testQuery_117()
	{
		buildQuery(query_117());
	}

	@Test
	public void testQuery_118()
	{
		buildQuery(query_118());
	}

	@Test
	public void testQuery_119()
	{
		buildQuery(query_119());
	}

	@Test
	public void testQuery_120()
	{
		buildQuery(query_120());
	}

	@Test
	public void testQuery_121()
	{
		buildQuery(query_121());
	}

	@Test
	public void testQuery_122()
	{
		buildQuery(query_122());
	}

	@Test
	public void testQuery_123()
	{
		buildQuery(query_123());
	}

	@Test
	public void testQuery_124()
	{
		buildQuery(query_124());
	}

	@Test
	public void testQuery_125()
	{
		buildQuery(query_125());
	}

	@Test
	public void testQuery_126()
	{
		buildQuery(query_126());
	}

	@Test
	public void testQuery_127()
	{
		buildQuery(query_127());
	}

	@Test
	public void testQuery_128()
	{
		buildQuery(query_128());
	}

	@Test
	public void testQuery_129()
	{
		buildQuery(query_129());
	}

	@Test
	public void testQuery_130()
	{
		buildQuery(query_130());
	}

	@Test
	public void testQuery_131()
	{
		buildQuery(query_131());
	}

	@Test
	public void testQuery_132()
	{
		buildQuery(query_132());
	}

	@Test
	public void testQuery_133()
	{
		buildQuery(query_133());
	}

	@Test
	public void testQuery_134()
	{
		buildQuery(query_134());
	}

	@Test
	public void testQuery_135()
	{
		buildQuery(query_135());
	}

	@Test
	public void testQuery_136()
	{
		buildQuery(query_136());
	}

	@Test
	public void testQuery_137()
	{
		buildQuery(query_137());
	}

	@Test
	public void testQuery_138()
	{
		buildQuery(query_138());
	}

	@Test
	public void testQuery_139()
	{
		buildQuery(query_139());
	}

	@Test
	public void testQuery_140()
	{
		buildQuery(query_140());
	}

	@Test
	public void testQuery_141()
	{
		buildQuery(query_141());
	}

	@Test
	public void testQuery_142()
	{
		buildQuery(query_142());
	}

	@Test
	public void testQuery_143()
	{
		buildQuery(query_143());
	}

	@Test
	public void testQuery_144()
	{
		buildQuery(query_144());
	}

	@Test
	public void testQuery_145()
	{
		buildQuery(query_145());
	}

	@Test
	public void testQuery_146()
	{
		buildQuery(query_146());
	}

	@Test
	public void testQuery_147()
	{
		buildQuery(query_147());
	}

	@Test
	public void testQuery_148()
	{
		buildQuery(query_148());
	}

	@Test
	public void testQuery_149()
	{
		buildQuery(query_149());
	}

	@Test
	public void testQuery_150()
	{
		buildQuery(query_150());
	}

	@Test
	public void testQuery_151()
	{
		buildQuery(query_151());
	}

	@Test
	public void testQuery_152()
	{
		buildQuery(query_152());
	}

	@Test
	public void testQuery_153()
	{
		buildQuery(query_153());
	}

	@Test
	public void testQuery_154()
	{
		buildQuery(query_154());
	}

	@Test
	public void testQuery_155()
	{
		buildQuery(query_155());
	}

	@Test
	public void testQuery_156()
	{
		buildQuery(query_156());
	}

	@Test
	public void testQuery_157()
	{
		buildQuery(query_157());
	}

	@Test
	public void testQuery_158()
	{
		buildQuery(query_158());
	}

	@Test
	public void testQuery_159()
	{
		buildQuery(query_159());
	}

	@Test
	public void testQuery_160()
	{
		buildQuery(query_160());
	}

	@Test
	public void testQuery_161()
	{
		buildQuery(query_161());
	}

	@Test
	public void testQuery_162()
	{
		buildQuery(query_162());
	}

	@Test
	public void testQuery_163()
	{
		buildQuery(query_163());
	}

	@Test
	public void testQuery_164()
	{
		buildQuery(query_164());
	}

	@Test
	public void testQuery_165()
	{
		buildQuery(query_165());
	}

	@Test
	public void testQuery_166()
	{
		buildQuery(query_166());
	}

	@Test
	public void testQuery_167()
	{
		buildQuery(query_167());
	}

	@Test
	public void testQuery_168()
	{
		buildQuery(query_168());
	}

	@Test
	public void testQuery_169()
	{
		buildQuery(query_169());
	}

	@Test
	public void testQuery_170()
	{
		buildQuery(query_170());
	}

	@Test
	public void testQuery_171()
	{
		buildQuery(query_171());
	}

	@Test
	public void testQuery_172()
	{
		buildQuery(query_172());
	}

	@Test
	public void testQuery_173()
	{
		buildQuery(query_173());
	}

	@Test
	public void testQuery_174()
	{
		buildQuery(query_174());
	}

	@Test
	public void testQuery_175()
	{
		buildQuery(query_175());
	}

	@Test
	public void testQuery_176()
	{
		buildQuery(query_176());
	}

	@Test
	public void testQuery_177()
	{
		buildQuery(query_177());
	}

	@Test
	public void testQuery_178()
	{
		buildQuery(query_178());
	}

	@Test
	public void testQuery_179()
	{
		buildQuery(query_179());
	}

	@Test
	public void testQuery_180()
	{
		buildQuery(query_180());
	}

	@Test
	public void testQuery_181()
	{
		buildQuery(query_181());
	}

	@Test
	public void testQuery_182()
	{
		buildQuery(query_182());
	}

	@Test
	public void testQuery_183()
	{
		buildQuery(query_183());
	}

	@Test
	public void testQuery_184()
	{
		buildQuery(query_184());
	}

	@Test
	public void testQuery_185()
	{
		buildQuery(query_186());
	}

	@Test
	public void testQuery_186()
	{
		buildQuery(query_186());
	}

	@Test
	public void testQuery_187()
	{
		buildQuery(query_187());
	}

	@Test
	public void testQuery_188()
	{
		buildQuery(query_188());
	}

	@Test
	public void testQuery_189()
	{
		buildQuery(query_189());
	}

	@Test
	public void testQuery_190()
	{
		buildQuery(query_190());
	}

	@Test
	public void testQuery_191()
	{
		buildQuery(query_191(), buildQueryFormatter_191());
	}

	@Test
	public void testQuery_192()
	{
		buildQuery(query_192());
	}

	@Test
	public void testQuery_193()
	{
		buildQuery(query_193());
	}

	@Test
	public void testQuery_194()
	{
		buildQuery(query_194());
	}

	@Test
	public void testQuery_195()
	{
		buildQuery(query_195());
	}

	@Test
	public void testQuery_196()
	{
		buildQuery(query_196());
	}

	@Test
	public void testQuery_197()
	{
		buildQuery(query_197());
	}

	@Test
	public void testQuery_198()
	{
		buildQuery(query_198());
	}

	@Test
	public void testQuery_199()
	{
		buildQuery(query_199());
	}

	@Test
	public void testQuery_200()
	{
		buildQuery(query_200());
	}

	@Test
	public void testQuery_201()
	{
		buildQuery(query_201());
	}

	@Test
	public void testQuery_202()
	{
		buildQuery(query_202());
	}

	@Test
	public void testQuery_203()
	{
		buildQuery(query_203());
	}

	@Test
	public void testQuery_204()
	{
		buildQuery(query_204());
	}

	@Test
	public void testQuery_205()
	{
		buildQuery(query_205());
	}

	@Test
	public void testQuery_206()
	{
		buildQuery(query_206());
	}

	@Test
	public void testQuery_207()
	{
		buildQuery(query_207());
	}

	@Test
	public void testQuery_208()
	{
		buildQuery(query_208());
	}

	@Test
	public void testQuery_209()
	{
		buildQuery(query_209());
	}

	@Test
	public void testQuery_210()
	{
		buildQuery(query_210());
	}

	@Test
	public void testQuery_211()
	{
		buildQuery(query_211());
	}

	@Test
	public void testQuery_212()
	{
		buildQuery(query_212());
	}

	@Test
	public void testQuery_213()
	{
		buildQuery(query_213());
	}

	public static interface QueryStringFormatter
	{
		public static QueryStringFormatter DEFAULT = new QueryStringFormatter()
		{
			@Override
			public String format(String query)
			{
				return query;
			}
		};

		String format(String query);
	}
}