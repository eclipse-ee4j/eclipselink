/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;


/**
 * Various helper methods for generating names.
 */
public final class NameTools {

	/**
	 * Given a "root" name and a set of existing names, generate a unique,
	 * Java-legal name that is either the "root" name or some variation on
	 * the "root" name (e.g. "root2", "root3",...).
	 * The names are case-sensitive.
	 */
	public static String uniqueJavaNameFor(String rootName, Iterator existingNames) {
		Collection existingNames2 = CollectionTools.set(existingNames);
		addJavaReservedWordsTo(existingNames2);
		return uniqueNameFor(rootName, existingNames2, rootName);
	}
	
	/**
	 * Given a "root" name and a set of existing names, generate a unique,
	 * Java-legal name that is either the "root" name or some variation on
	 * the "root" name (e.g. "root2", "root3",...).
	 * The names are case-sensitive.
	 */
	public static String uniqueJavaNameFor(String rootName, Collection existingNames) {
		Collection existingNames2 = new HashSet(existingNames);
		addJavaReservedWordsTo(existingNames2);
		return uniqueNameFor(rootName, existingNames2, rootName);
	}

	/**
	 * Given a "root" name and a set of existing names, generate a unique
	 * name that is either the "root" name or some variation on the "root"
	 * name (e.g. "root2", "root3",...). The names are case-sensitive.
	 */
	public static String uniqueNameFor(String rootName, Iterator existingNames) {
		return uniqueNameFor(rootName, CollectionTools.set(existingNames));
	}
	
	/**
	 * Given a "root" name and a set of existing names, generate a unique
	 * name that is either the "root" name or some variation on the "root"
	 * name (e.g. "root2", "root3",...). The names are case-sensitive.
	 */
	public static String uniqueNameFor(String rootName, Collection existingNames) {
		return uniqueNameFor(rootName, existingNames, rootName);
	}

	/**
	 * Given a "root" name and a set of existing names, generate a unique
	 * name that is either the "root" name or some variation on the "root"
	 * name (e.g. "root2", "root3",...). The names are NOT case-sensitive.
	 */
	public static String uniqueNameForIgnoreCase(String rootName, Iterator existingNames) {
		return uniqueNameForIgnoreCase(rootName, CollectionTools.set(existingNames));
	}

	/**
	 * Given a "root" name and a set of existing names, generate a unique
	 * name that is either the "root" name or some variation on the "root"
	 * name (e.g. "root2", "root3",...). The names are NOT case-sensitive.
	 */
	public static String uniqueNameForIgnoreCase(String rootName, Collection existingNames) {
		return uniqueNameFor(rootName, convertToLowerCase(existingNames), rootName.toLowerCase());
	}

	/**
	 * use the suffixed "template" name to perform the comparisons, but RETURN
	 * the suffixed "root" name; this allows case-insensitive comparisons
	 * (i.e. the "template" name has been morphed to the same case as
	 * the "existing" names, while the "root" name has not, but the "root" name
	 * is what the client wants morphed to be unique)
	 */
	private static String uniqueNameFor(String rootName, Collection existingNames, String templateName) {
		if ( ! existingNames.contains(templateName)) {
			return rootName;
		}
		String uniqueName = templateName;
		for (int suffix = 2; true; suffix++) {
			if ( ! existingNames.contains(uniqueName + suffix)) {
				return rootName.concat(String.valueOf(suffix));
			}
		}
	}

	/**
	 * Convert the specified collection of strings to a collection of the same
	 * strings converted to lower case.
	 */
	private static Collection convertToLowerCase(Collection strings) {
		Collection result = new HashBag(strings.size());
		for (Iterator stream = strings.iterator(); stream.hasNext(); ) {
			result.add(((String) stream.next()).toLowerCase());
		}
		return result;
	}

	/**
	 * Build a fully-qualified name for the specified database object.
	 * Variations:
	 *     catalog.schema.name
	 *     catalog.name (used to be catalog..name)
	 *     schema.name
	 *     name
	 */
	public static String buildQualifiedDatabaseObjectName(String catalog, String schema, String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		if ((catalog == null) && (schema == null)) {
			return name;
		}

		StringBuffer sb = new StringBuffer(100);
		if (catalog != null) {
			sb.append(catalog);
			sb.append('.');
		}
		if (schema != null) {
			sb.append(schema);
			sb.append('.');
		}
		sb.append(name);
		return sb.toString();
	}

	/**
	 * The set of reserved words in the Java programming language.
	 * These words cannot be used as identifiers (i.e. names).
	 * http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
	 */
	public static final String[] JAVA_RESERVED_WORDS = new String[] {
				"abstract",
				"assert",  // jdk 1.4
				"boolean",
				"break",
				"byte",
				"case",
				"catch",
				"char",
				"class",
				"const",  // unused
				"continue",
				"default",
				"do",
				"double",
				"else",
				"enum",  // jdk 5.0
				"extends",
				"false",
				"final",
				"finally",
				"float",
				"for",
				"goto",  // unused
				"if",
				"implements",
				"import",
				"instanceof",
				"int",
				"interface",
				"long",
				"native",
				"new",
				"null",
				"package",
				"private",
				"protected",
				"public",
				"return",
				"short",
				"static",
				"strictfp",  // jdk 1.2
				"super",
				"switch",
				"synchronized",
				"this",
				"throw",
				"throws",
				"transient",
				"true",
				"try",
				"void",
				"volatile",
				"while"
			};

	/**
	 * The set of reserved words in the Java programming language.
	 * These words cannot be used as identifiers (i.e. names).
	 * http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
	 */
	public static final Set JAVA_RESERVED_WORDS_SET = CollectionTools.set(JAVA_RESERVED_WORDS);

	/**
	 * Return a set of the Java programming language reserved words.
	 * These words cannot be used as identifiers (i.e. names).
	 * http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
	 */
	public static void addJavaReservedWordsTo(Collection collection) {
		CollectionTools.addAll(collection, JAVA_RESERVED_WORDS);
	}

	/**
	 * Return the set of Java programming language reserved words.
	 * These words cannot be used as identifiers (i.e. names).
	 * http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
	 */
	public static Iterator javaReservedWords() {
		return new ArrayIterator(JAVA_RESERVED_WORDS);
	}

	/**
	 * Return whether the set of Java programming language reserved words
	 * contains the specified string.
	 * http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
	 */
	public static boolean javaReservedWordsContains(String string) {
		return JAVA_RESERVED_WORDS_SET.contains(string);
	}


	// ********** constructor **********

	/**
	 * Suppress default constructor, ensuring non-instantiability.
	 */
	private NameTools() {
		super();
		throw new UnsupportedOperationException();
	}

}
