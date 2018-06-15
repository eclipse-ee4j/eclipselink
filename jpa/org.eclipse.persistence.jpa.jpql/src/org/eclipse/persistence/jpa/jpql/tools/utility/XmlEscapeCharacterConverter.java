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
package org.eclipse.persistence.jpa.jpql.tools.utility;

import java.util.HashMap;
import java.util.Map;

/**
 * This converter handles references when dealing with text or markup in an XML document. Those
 * references (escape characters) are defined in ISO-8859-1 Reference.
 * <p>
 * The conversion supports both converting a numeric character reference (&amp;#nnnn; where nnnn is
 * the code point in decimal form or &amp;xhhhh; where hhhh is the code point in hexadecimal point)
 * and a character entity reference (&amp;name; where name is the case-sensitive name of the entity).
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class XmlEscapeCharacterConverter {

    /**
     * The entity name for ampersand: <b>&amp;</b>.
     */
    public static final String AMPERSAND_ENTITY_NAME = "&amp;";

    /**
     * The entity name for apostrophe: <b>&amp;apos;</b>.
     */
    public static final String APOSTROPHE_ENTITY_NAME = "&apos;";

    /**
     * The map of symbol mapped to the unicode character.
     */
    private static final Map<String, String> dictionary = buildDictionary();

    /**
     * The entity name for greater-than symbol: <b>&gt;</b>.
     */
    public static final String GREATER_THAN_ENTITY_NAME = "&gt;";

    /**
     * The entity name for less-than symbol: <b>&lt;</b>.
     */
    public static final String LESS_THAN_ENTITY_NAME = "&lt;";

    /**
     * The entity name for quotation mark: <b>&quot;</b>.
     */
    public static final String QUOTATION_MARK_NAME = "&quot;";

    /**
     * Cannot instantiate <code>XmlEscapeCharacterConverter</code>.
     */
    private XmlEscapeCharacterConverter() {
        super();
    }

    private static Map<String, String> buildDictionary() {

        Map<String, String> dictionary = new HashMap<String, String>();

        // Reserved characters
        dictionary.put("quot",    "\"");     // Quotation Mark
        dictionary.put("apos",    "'");      // Apostrophe
        dictionary.put("amp",     "&");      // Ampersand
        dictionary.put("lt",      "<");      // Less Than Symbol
        dictionary.put("gt",      ">");      // Greater Than Symbol

        // ISO-8859-1 symbols
        dictionary.put("nbsp",    "\u00A0"); // Nonbreaking space
        dictionary.put("iexcl",   "\u00A1"); // Inverted Exclamation Point
        dictionary.put("cent",    "\u00A2"); // Cent Sign
        dictionary.put("pound",   "\u00A3"); // Pound Sterling
        dictionary.put("curren",  "\u00A4"); // General Currency Sign
        dictionary.put("yen",     "\u00A5"); // Yen Sign
        dictionary.put("brvbar",  "\u00A6"); // Broken Vertical Bar
        dictionary.put("sect",    "\u00A7"); // Section Sign
        dictionary.put("uml",     "\u00A8"); // Umlaut
        dictionary.put("copy",    "\u00A9"); // Copyright
        dictionary.put("ordf",    "\u00AA"); // Feminine Ordinal
        dictionary.put("laquo",   "\u00AB"); // Left Angle Quote
        dictionary.put("not",     "\u00AC"); // Not Sign
        dictionary.put("shy",     "\u00AD"); // Soft Hyphen
        dictionary.put("reg",     "\u00AE"); // Registered Trademark
        dictionary.put("macr",    "\u00AF"); // Macron Accent
        dictionary.put("deg",     "\u00B0"); // Degree Sign
        dictionary.put("plusmn",  "\u00B1"); // Plus or Minus
        dictionary.put("sup2",    "\u00B2"); // Superscript Two
        dictionary.put("sup3",    "\u00B3"); // Superscript Three
        dictionary.put("acute",   "\u00B4"); // Acute Accent
        dictionary.put("micro",   "\u00B5"); // Micro Sign
        dictionary.put("para",    "\u00B6"); // Paragraph Sign
        dictionary.put("middot",  "\u00B7"); // Middle Dot
        dictionary.put("cedil",   "\u00B8"); // Cedilla
        dictionary.put("sup1",    "\u00B9"); // Superscript One
        dictionary.put("ordm",    "\u00BA"); // Masculine Ordinal
        dictionary.put("raquo",   "\u00BB"); // Right Angle Quote
        dictionary.put("frac14",  "\u00BC"); // Fraction One-Forth
        dictionary.put("frac12",  "\u00BD"); // Fraction One-Half
        dictionary.put("frac34",  "\u00BE"); // Fraction Three-Fourths
        dictionary.put("iquest",  "\u00BF"); // Inverted Question Mark
        dictionary.put("times",   "\u00D7"); // Multiplication
        dictionary.put("divide",  "\u00F7"); // Division

        // ISO-8859-1 characters
        dictionary.put("Agrave",  "\u00C0"); // Latin capital letter A with grave accent
        dictionary.put("Aacute",  "\u00C1"); // Latin capital letter A with acute accent
        dictionary.put("Acirc",   "\u00C2"); // Latin capital letter A with circumflex
        dictionary.put("Atilde",  "\u00C3"); // Latin capital letter A with tilde
        dictionary.put("Auml",    "\u00C4"); // Latin capital letter A with diaeresis
        dictionary.put("Aring",   "\u00C5"); // Latin capital letter A with ring above
        dictionary.put("AElig",   "\u00C6"); // Latin capital letter AE
        dictionary.put("Ccedil",  "\u00C7"); // Latin capital letter C with cedilla
        dictionary.put("Egrave",  "\u00C8"); // Latin capital letter E with grave accent
        dictionary.put("Eacute",  "\u00C9"); // Latin capital letter E with acute accent
        dictionary.put("Ecirc",   "\u00CA"); // Latin capital letter E with circumflex
        dictionary.put("Euml",    "\u00CB"); // Latin capital letter E with diaeresis
        dictionary.put("Igrave",  "\u00CC"); // Latin capital letter I with grave accent
        dictionary.put("Iacute",  "\u00CD"); // Latin capital letter I with acute accent
        dictionary.put("Icirc",   "\u00CE"); // Latin capital letter I with circumflex
        dictionary.put("Iuml",    "\u00CF"); // Latin capital letter I with diaeresis
        dictionary.put("ETH",     "\u00D0"); // Latin capital letter Eth
        dictionary.put("Ntilde",  "\u00D1"); // Latin capital letter N with tilde
        dictionary.put("Ograve",  "\u00D2"); // Latin capital letter O with grave accent
        dictionary.put("Oacute",  "\u00D3"); // Latin capital letter O with acute accent
        dictionary.put("Ocirc",   "\u00D4"); // Latin capital letter O with circumflex
        dictionary.put("Otilde",  "\u00D5"); // Latin capital letter O with tilde
        dictionary.put("Ouml",    "\u00D6"); // Latin capital letter O with diaeresis
        dictionary.put("Oslash",  "\u00D8"); // Latin capital letter O with stroke
        dictionary.put("Ugrave",  "\u00D9"); // Latin capital letter U with grave accent
        dictionary.put("Uacute",  "\u00DA"); // Latin capital letter U with acute accent
        dictionary.put("Ucirc",   "\u00DB"); // Latin capital letter U with circumflex
        dictionary.put("Uuml",    "\u00DC"); // Latin capital letter U with diaeresis
        dictionary.put("Yacute",  "\u00DD"); // Latin capital letter Y with acute accent
        dictionary.put("THORN",   "\u00DE"); // Latin capital letter THORN
        dictionary.put("szlig",   "\u00DF"); // Latin small letter sharp s
        dictionary.put("agrave",  "\u00E0"); // Latin small letter a with grave accent
        dictionary.put("aacute",  "\u00E1"); // Latin small letter a with acute accent
        dictionary.put("acirc",   "\u00E2"); // Latin small letter a with circumflex
        dictionary.put("atilde",  "\u00E3"); // Latin small letter a with tilde
        dictionary.put("auml",    "\u00E4"); // Latin small letter a with diaeresis
        dictionary.put("aring",   "\u00E5"); // Latin small letter a with ring above
        dictionary.put("aelig",   "\u00E6"); // Latin small letter ae
        dictionary.put("ccedil",  "\u00E7"); // Latin small letter c with cedilla
        dictionary.put("egrave",  "\u00E8"); // Latin small letter e with grave accent
        dictionary.put("eacute",  "\u00E9"); // Latin small letter e with acute accent
        dictionary.put("ecirc",   "\u00EA"); // Latin small letter e with circumflex
        dictionary.put("euml",    "\u00EB"); // Latin small letter e with diaeresis
        dictionary.put("igrave",  "\u00EC"); // Latin small letter i with grave accent
        dictionary.put("iacute",  "\u00ED"); // Latin small letter i with acute accent
        dictionary.put("icirc",   "\u00EE"); // Latin small letter i with circumflex
        dictionary.put("iuml",    "\u00EF"); // Latin small letter i with diaeresis
        dictionary.put("eth",     "\u00F0"); // Latin small letter eth
        dictionary.put("ntilde",  "\u00F1"); // Latin small letter n with tilde
        dictionary.put("ograve",  "\u00F2"); // Latin small letter o with grave accent
        dictionary.put("oacute",  "\u00F3"); // Latin small letter o with acute accent
        dictionary.put("ocirc",   "\u00F4"); // Latin small letter o with circumflex
        dictionary.put("otilde",  "\u00F5"); // Latin small letter o with tilde
        dictionary.put("ouml",    "\u00F6"); // Latin small letter o with diaeresis
        dictionary.put("oslash",  "\u00F8"); // Latin small letter o with stroke
        dictionary.put("ugrave",  "\u00F9"); // Latin small letter u with grave accent
        dictionary.put("uacute",  "\u00FA"); // Latin small letter u with acute accent
        dictionary.put("ucirc",   "\u00FB"); // Latin small letter u with circumflex
        dictionary.put("uuml",    "\u00FC"); // Latin small letter u with diaeresis
        dictionary.put("yacute",  "\u00FD"); // Latin small letter y with acute accent
        dictionary.put("thorn",   "\u00FE"); // Latin small letter thorn
        dictionary.put("yuml",    "\u00FF"); // Latin small letter y with diaeresis

        // Math Symbols
        dictionary.put("forall",  "\u2200"); // For all
        dictionary.put("part",    "\u2202"); // Partial differential
        dictionary.put("exist",   "\u2203"); // There exists
        dictionary.put("empty",   "\u2205"); // Empty set; Null Set; Diameter
        dictionary.put("nabla",   "\u2207"); // Nabla; Backward difference
        dictionary.put("isin",    "\u2208"); // Element of
        dictionary.put("notin",   "\u2209"); // Not an element of
        dictionary.put("ni",      "\u220B"); // Contains as member
        dictionary.put("prod",    "\u220F"); // N-ary product; Product sign
        dictionary.put("sum",     "\u2211"); // N-ary sumation
        dictionary.put("minus",   "\u2212"); // Minus sign
        dictionary.put("lowast",  "\u2217"); // Asterisk operator
        dictionary.put("radic",   "\u221A"); // Square root; Radical sign
        dictionary.put("prop",    "\u221D"); // Proportional to
        dictionary.put("infin",   "\u221E"); // Infinity
        dictionary.put("ang",     "\u2220"); // Angle
        dictionary.put("and",     "\u2227"); // Logical and; Wedge
        dictionary.put("or",      "\u2228"); // Logical or; Vee
        dictionary.put("cap",     "\u2229"); // Intersection; Cap
        dictionary.put("cup",     "\u222A"); // Union; Cup
        dictionary.put("int",     "\u222B"); // Integral
        dictionary.put("there4",  "\u2234"); // Therefore
        dictionary.put("sim",     "\u223C"); // Tilde operator; Varies with; Similar to
        dictionary.put("cong",    "\u2245"); // Approximately equal to
        dictionary.put("asymp",   "\u2248"); // Almost equal to; Asymptotic to
        dictionary.put("ne",      "\u2260"); // Not equal to
        dictionary.put("equiv",   "\u2261"); // Identical to
        dictionary.put("le",      "\u2264"); // Less-than or equal to
        dictionary.put("ge",      "\u2265"); // Greater-than or equal to
        dictionary.put("sub",     "\u2282"); // Subset of
        dictionary.put("sup",     "\u2283"); // Superset of
        dictionary.put("nsub",    "\u2284"); // Not a subset of
        dictionary.put("sube",    "\u2286"); // Subset of or equal to
        dictionary.put("supe",    "\u2287"); // Superset of or equal to
        dictionary.put("oplus",   "\u2295"); // Circled plus; Direct sum
        dictionary.put("otimes",  "\u2297"); // Circled times; Vector product
        dictionary.put("perp",    "\u22A5"); // Up tack; Orthogonal to; Perpendicular
        dictionary.put("sdot",    "\u22C5"); // Dot operator

        // Arrows
        dictionary.put("larr",    "\u2190"); // Leftwards arrow
        dictionary.put("uarr",    "\u2191"); // Upwards arrow
        dictionary.put("rarr",    "\u2192"); // Rightwards arrow
        dictionary.put("darr",    "\u2193"); // Downwards arrow
        dictionary.put("harr",    "\u2194"); // Left right arrow
        dictionary.put("crarr",   "\u21B5"); // Downwards arrow with corner leftwards; Carriage return symbol
        dictionary.put("lArr",    "\u21D0"); // Leftwards double arrow
        dictionary.put("uArr",    "\u21D1"); // Upwards double arrow
        dictionary.put("rArr",    "\u21D2"); // Rightwards double arrow
        dictionary.put("dArr",    "\u21D3"); // Downwards double arrow
        dictionary.put("hArr",    "\u21D4"); // Left right double arrow

        // Greek Capital Letters
        dictionary.put("Alpha",   "\u0391"); // Greek capital letter alpha
        dictionary.put("Beta",    "\u0392"); // Greek capital letter beta
        dictionary.put("Gamma",   "\u0393"); // Greek capital letter gamma
        dictionary.put("Delta",   "\u0394"); // Greek capital letter delta
        dictionary.put("Epsilon", "\u0395"); // Greek capital letter epsilon
        dictionary.put("Zeta",    "\u0396"); // Greek capital letter zeta
        dictionary.put("Eta",     "\u0397"); // Greek capital letter eta
        dictionary.put("Theta",   "\u0398"); // Greek capital letter theta
        dictionary.put("Iota",    "\u0399"); // Greek capital letter iota
        dictionary.put("Kappa",   "\u039A"); // Greek capital letter kappa
        dictionary.put("Lambda",  "\u039B"); // Greek capital letter lambda
        dictionary.put("Mu",      "\u039C"); // Greek capital letter mu
        dictionary.put("Nu",      "\u039D"); // Greek capital letter nu
        dictionary.put("Xi",      "\u039E"); // Greek capital letter xi
        dictionary.put("Omicron", "\u039F"); // Greek capital letter omicron
        dictionary.put("Pi",      "\u03A0"); // Greek capital letter pi
        dictionary.put("Rho",     "\u03A1"); // Greek capital letter rho
        dictionary.put("Sigma",   "\u03A3"); // Greek capital letter sigma
        dictionary.put("Tau",     "\u03A4"); // Greek capital letter tau
        dictionary.put("Upsilon", "\u03A5"); // Greek capital letter upsilon
        dictionary.put("Phi",     "\u03A6"); // Greek capital letter phi
        dictionary.put("Chi",     "\u03A7"); // Greek capital letter chi
        dictionary.put("Psi",     "\u03A8"); // Greek capital letter psi
        dictionary.put("Omega",   "\u03A9"); // Greek capital letter omega

        // Greek Small Letters
        dictionary.put("alpha",   "\u03B1"); // Greek small letter alpha
        dictionary.put("beta",    "\u03B2"); // Greek small letter beta
        dictionary.put("gamma",   "\u03B3"); // Greek small letter gamma
        dictionary.put("delta",   "\u03B4"); // Greek small letter delta
        dictionary.put("epsilon", "\u03B5"); // Greek small letter epsilon
        dictionary.put("zeta",    "\u03B6"); // Greek small letter zeta
        dictionary.put("eta",     "\u03B7"); // Greek small letter eta
        dictionary.put("theta",   "\u03B8"); // Greek small letter theta
        dictionary.put("iota",    "\u03B9"); // Greek small letter iota
        dictionary.put("kappa",   "\u03BA"); // Greek small letter kappa
        dictionary.put("lambda",  "\u03BB"); // Greek small letter lambda
        dictionary.put("mu",      "\u03BC"); // Greek small letter mu
        dictionary.put("nu",      "\u03BD"); // Greek small letter nu
        dictionary.put("xi",      "\u03BE"); // Greek small letter xi
        dictionary.put("omicron", "\u03BF"); // Greek small letter omicron
        dictionary.put("pi",      "\u03C0"); // Greek small letter pi
        dictionary.put("rho",     "\u03C1"); // Greek small letter rho
        dictionary.put("sigmaf",  "\u03C2"); // Greek small letter final sigma
        dictionary.put("sigma",   "\u03C3"); // Greek small letter sigma
        dictionary.put("tau",     "\u03C4"); // Greek small letter tau
        dictionary.put("upsilon", "\u03C5"); // Greek small letter upsilon
        dictionary.put("phi",     "\u03C6"); // Greek small letter phi
        dictionary.put("chi",     "\u03C7"); // Greek small letter chi
        dictionary.put("psi",     "\u03C8"); // Greek small letter psi
        dictionary.put("omega",   "\u03C9"); // Greek small letter omega
        dictionary.put("theta",   "\u03D1"); // Greek small letter theta symbol
        dictionary.put("upsih",   "\u03D2"); // Greek upsilon with hook symbol
        dictionary.put("piv",     "\u03D6"); // Greek pi symbol

        // Latin Extended-A and Letterlike Symbols
        dictionary.put("OElig",   "\u0152"); // Latin capital ligature oe
        dictionary.put("oelig",   "\u0153"); // Latin small ligature oe
        dictionary.put("Scaron",  "\u0160"); // Latin capital letter s with caron
        dictionary.put("scaron",  "\u0161"); // Latin small letter s with caron
        dictionary.put("Yuml",    "\u0178"); // Latin capital letter y with diaeresis
        dictionary.put("fnof",    "\u0192"); // Latin small f with hook
        dictionary.put("weierp",  "\u2118"); // Script capital P; Power set; Weierstrass p
        dictionary.put("image",   "\u2111"); // Blackletter capital I; Imaginary part
        dictionary.put("real",    "\u211C"); // Blackletter capital R; Real part symbol
        dictionary.put("trade",   "\u2122"); // Trade mark sign
        dictionary.put("alefsym", "\u2135"); // Alef symbol; First transfinite cardinal

        // Miscellaneous Shapes
        dictionary.put("spades",  "\u2660"); // Black spade suit
        dictionary.put("clubs",   "\u2663"); // Black club suit; Shamrock
        dictionary.put("hearts",  "\u2665"); // Black heart suit; Valentine
        dictionary.put("diams",   "\u2666"); // Black diamond suit
        dictionary.put("loz",     "\u25CA"); // Lozenge

        // Miscellaneous Technical Symbols
        dictionary.put("lceil",   "\u2308"); // Left ceiling; Apl upstile
        dictionary.put("rceil",   "\u2309"); // Right ceiling
        dictionary.put("lfloor",  "\u230A"); // Left floor; Apl downstile
        dictionary.put("rfloor",  "\u230B"); // Right floor
        dictionary.put("lang",    "\u2329"); // Left-pointing angle bracket
        dictionary.put("rang",    "\u232A"); // Right-pointing angle bracket

        // Spacing Modifier Characters and Bi-directional Characters
        dictionary.put("circ",    "\u02C6"); // Modifier letter circumflex accent
        dictionary.put("tilde",   "\u02DC"); // Small tilde
        dictionary.put("zwnj",    "\u200C"); // Zero width non-joiner
        dictionary.put("zwj",     "\u200D"); // Zero width joiner
        dictionary.put("lrm",     "\u200E"); // Left-to-right mark
        dictionary.put("rlm",     "\u200F"); // Right-to-left mark

        // General Punctuation Set 1
        dictionary.put("bull",    "\u2022"); // Bullet; Black small circle
        dictionary.put("hellip",  "\u2026"); // Horizontal ellipsis; Three dot leader
        dictionary.put("prime",   "\u2032"); // Prime; Minutes; Feet
        dictionary.put("Prime",   "\u2033"); // Double prime; Seconds; Inches
        dictionary.put("oline",   "\u203E"); // Overline; Spacing overscore
        dictionary.put("frasl",   "\u2044"); // Fraction slash

        // General Punctuation Set 2
        dictionary.put("ensp",    "\u2002"); // En space
        dictionary.put("emsp",    "\u2003"); // Em space
        dictionary.put("thinsp",  "\u2009"); // Thin space
        dictionary.put("zwnj",    "\u200C"); // Zero width non-joiner
        dictionary.put("zwj",     "\u200D"); // Zero width joiner
        dictionary.put("lrm",     "\u200E"); // Left-to-right mark
        dictionary.put("rlm",     "\u200F"); // Right-to-left mark
        dictionary.put("ndash",   "\u2013"); // En dash
        dictionary.put("mdash",   "\u2014"); // Em dash
        dictionary.put("lsquo",   "\u2018"); // Left single quotation mark
        dictionary.put("rsquo",   "\u2019"); // Right single quotation mark
        dictionary.put("sbquo",   "\u201A"); // Single low-9 quotation mark
        dictionary.put("ldquo",   "\u201C"); // Left double quotation mark
        dictionary.put("rdquo",   "\u201D"); // Right double quotation mark
        dictionary.put("bdquo",   "\u201E"); // Double low-9 quotation mark
        dictionary.put("dagger",  "\u2020"); // Dagger
        dictionary.put("Dagger",  "\u2021"); // Double dagger
        dictionary.put("permil",  "\u2030"); // Per mille sign
        dictionary.put("lsaquo",  "\u2039"); // Single left-pointing angle quotation mark
        dictionary.put("rsaquo",  "\u203A"); // Single right-pointing angle quotation mark
        dictionary.put("euro",    "\u20AC"); // Euro

        return dictionary;
    }

    /**
     * Converts the characters that are reserved in an XML document the given string may have into
     * their corresponding references (escape characters) using the character entity reference.
     *
     * @param value A string that may contain characters that need to be escaped
     * @param positions This array of length one or two can be used to adjust the position of the
     * cursor or a text range within the string during the conversion of the reserved characters
     * @return The given string with any reserved characters converted into the escape characters
     */
    public static String escape(String value, int[] positions) {

        if ((value == null) || (value.length() == 0)) {
            return value;
        }

        StringBuilder sb = new StringBuilder(value.length());
        int startPosition = positions[0];
        int endPosition = (positions.length > 1) ? positions[1] : -1;

        for (int index = 0, count = value.length(); index < count; index++) {

            char character = value.charAt(index);

            // The character is one of the reserved character
            if (isReserved(character)) {

                // Retrieve the corresponding entity name
                String name = getEscapeCharacter(character);
                sb.append(name);

                // Adjust the position
                if (startPosition > index) {
                    // -1 for the character itself that is replaced by the entity name
                    positions[0] += (name.length() - 1);
                }

                if ((endPosition > -1) && (index < endPosition)) {
                    // -1 for the character itself that is replaced by the entity name
                    positions[1] += (name.length() - 1);
                }
            }
            else {
                sb.append(character);
            }
        }

        return sb.toString();
    }

    /**
     * Returns the Unicode character for the given reference (which is either a numeric character
     * reference or a character entity reference).
     *
     * @param reference The numeric character or character entity reference stripped of the leading
     * ampersand and trailing semi-colon
     * @return The Unicode character mapped to the given reference or <code>null</code> if the
     * reference is invalid or unknown
     */
    public static String getCharacter(String reference) {

        if (reference == null) {
            return null;
        }

        int length = reference.length();

        if (length == 0) {
            return null;
        }

        // Character reference
        if (reference.charAt(0) == '#') {

            if (length == 1) {
                return null;
            }

            // Parse the numeric value
            String value;
            int radix;

            // Hexadecimal
            if (reference.charAt(1) == 'x') {
                radix = 16;
                value = reference.substring(2);
            }
            // Decimal
            else {
                radix = 10;
                value = reference.substring(1);
            }

            // No minus accepted
            if ((value.length() == 0) || (value.charAt(0) == '-')) {
                return null;
            }

            // Convert the numeric value into the actual character
            char character = 0;

            try {
                character = (char) Integer.parseInt(value, radix);
            }
            catch (NumberFormatException ex) {
                // Simply ignore
            }

            // The null character &#0; is not permitted
            if (character == 0) {
                return null;
            }

            return String.valueOf(character);
        }

        // Entity reference
        return dictionary.get(reference);
    }

    /**
     * Returns the escaped character for the given reserved character.
     *
     * @param character The reserved character to retrieve its escape character with the entity name
     * @return The escape character with the entity name of the given character if it is a reserved
     * character; otherwise returns <code>null</code>
     */
    public static String getEscapeCharacter(char character) {

        switch (character) {
            case '<':  return LESS_THAN_ENTITY_NAME;
            case '>':  return GREATER_THAN_ENTITY_NAME;
            case '&':  return AMPERSAND_ENTITY_NAME;
            case '\'': return APOSTROPHE_ENTITY_NAME;
            case '\"': return QUOTATION_MARK_NAME;
            default:   return null;
        }
    }

    /**
     * Determines if the given character is one of the XML/HTML reserved characters.
     *
     * @param character The character to verify if it's one of the reserved characters
     * @return <code>true</code> if the given character is defined as a reserved characters;
     * <code>false</code> otherwise
     */
    public static boolean isReserved(char character) {

        switch (character) {
            case '<':
            case '>':
            case '&':
            case '\'':
            case '\"': return true;
            default:   return false;
        }
    }

    /**
     * Re-adjusts the given positions, which is based on the non-escaped version of the given
     * <em>query</em>, by making sure it is pointing at the same position within <em>query</em>,
     * which contains references (escape characters).
     * <p>
     * The escape characters are either the character entity references or the numeric character
     * references used in an XML document.
     * <p>
     * <b>Important:</b> The given query should contain the exact same amount of whitespace than the
     * query used to calculate the given positions.
     *
     * @param query The query that may contain escape characters
     * @param positions The position within the non-escaped version of the given query, which is
     * either a single element position or two positions that is used as a text range. After execution
     * contains the adjusted positions by moving it based on the difference between the escape and
     * non-escaped versions of the query
     * @since 2.5
     */
    public static void reposition(CharSequence query, int[] positions) {

        if ((query == null) || (query.length() == 0)) {
            return;
        }

        StringBuilder sb = new StringBuilder(query);

        for (int index = 0, count = sb.length(); index < count; index++) {

            char character = sb.charAt(index);

            // The beginning of the escape character
            if ((character == '&') && (index + 1 < count)) {

                // Find the ending of the escape character
                int semiColonIndex = sb.indexOf(";", index + 1);

                if (semiColonIndex > -1) {

                    // Retrieve the reference value
                    String reference = sb.substring(index + 1, semiColonIndex);

                    if (reference.length() > 0) {

                        // Retrieve the character mapped to the entity name
                        String unicodeCharacter = XmlEscapeCharacterConverter.getCharacter(reference);

                        if (unicodeCharacter != null) {

                            // length = '&' + 'reference' + ';' - 'Unicode character'
                            int length = (semiColonIndex - index);

                            // Translate both positions because a Unicode
                            // character is written with its escape character
                            if (index < positions[0]) {
                                positions[0] += length;
                                positions[1] += length;
                            }
                            // Only translate the end position because the start
                            // position is before the current index
                            else if (index < positions[1]) {
                                positions[1] += length;
                            }

                            index = semiColonIndex;
                        }
                    }
                }
            }
        }
    }

    /**
     * Converts the references (escape characters) the given string may have into their corresponding
     * Unicode characters.
     *
     * <ul>
     * <li>Character entity reference: <b>&amp;copy;</b> for <b>&copy;</b></li>
     * <li>Numeric character reference (decimal value): <b>&amp;#169;</b> for <b>&#169;</b></li>
     * <li>Numeric character reference (hexadecimal value): <b>&amp;#xA9;</b> for <b>&#xA9;</b></li>
     * </ul>
     *
     * @param value A string that may contain escape characters
     * @param position This array of length one can be used to adjust the position of the cursor
     * within the string during the conversion of the escape characters
     * @return The given string with any escape characters converted into the actual Unicode characters
     */
    public static String unescape(String value, int[] position) {

        if ((value == null) || (value.length() == 0)) {
            return value;
        }

        StringBuilder sb = new StringBuilder(value);

        for (int index = 0, count = sb.length(); index < count; index++) {

            char character = sb.charAt(index);

            // The beginning of the escape character
            if ((character == '&') && (index + 1 < count)) {

                // Find the ending of the escape character
                int semiColonIndex = sb.indexOf(";", index + 1);

                if (semiColonIndex > -1) {

                    // Retrieve the reference
                    String reference = sb.substring(index + 1, semiColonIndex);

                    if (reference.length() > 0) {

                        // Retrieve the character mapped to the reference
                        String specialCharacter = getCharacter(reference);

                        if (specialCharacter != null) {

                            // Replace the reference by the Unicode character
                            sb.replace(index, semiColonIndex + 1, specialCharacter);

                            // Make sure the count is updated
                            count -= (semiColonIndex - index);

                            // "& + reference + ; - Unicode character"
                            int length = (1 + reference.length());

                            // Adjust the position
                            // Case 1: The cursor is within the escape character, move it to the beginning
                            if ((position[0] >= index) && (position[0] <= index + length)) {
                                position[0] = index;
                            }
                            // Case 2: the cursor is after the escape character, just do an adjustment as
                            //         if it was a single character
                            else if (position[0] > index + length) {
                                position[0] -= length;
                            }
                        }
                    }
                }
            }
        }

        return sb.toString();
    }
}
