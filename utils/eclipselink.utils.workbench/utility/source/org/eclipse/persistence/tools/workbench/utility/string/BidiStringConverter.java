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
package org.eclipse.persistence.tools.workbench.utility.string;


/**
 * Used by various "pluggable" classes to transform objects
 * into strings and vice versa.
 * 
 * If anyone can come up with a better class name
 * and/or method name, I would love to hear it.  -bjv
 */
public interface BidiStringConverter extends StringConverter {

	/**
	 * Convert the specified string into an object.
	 * The semantics of "convert to object" is determined by the
	 * contract between the client and the server.
	 * Typically, if the string is null, null is returned.
	 */
	Object convertToObject(String s);


	BidiStringConverter DEFAULT_INSTANCE =
		new BidiStringConverter() {
			/** Return the object's #toString() result. */
			public String convertToString(Object o) {
				return (o == null) ? null : o.toString();
			}
			/** Return the string as the object. */
			public Object convertToObject(String s) {
				return s;
			}
			public String toString() {
				return "DefaultBidiStringConverter";
			}
		};

	BidiStringConverter BOOLEAN_CONVERTER =
		new BidiStringConverter() {
			/** Return "true" if the Boolean is true, otherwise return "false". */
			public String convertToString(Object o) {
				return (o == null) ? null : o.toString();
			}
			/** Return Boolean.TRUE if the string is "true" (case-insensitive), otherwise return Boolean.FALSE. */
			public Object convertToObject(String s) {
				return (s == null) ? null : Boolean.valueOf(s);
			}
			public String toString() {
				return "BooleanBidiStringConverter";
			}
		};

	BidiStringConverter INTEGER_CONVERTER =
		new BidiStringConverter() {
			/** Integer's #toString() works well. */
			public String convertToString(Object o) {
				return (o == null) ? null : o.toString();
			}
			/** Convert the string to an Integer, if possible. */
			public Object convertToObject(String s) {
				return (s == null) ? null : Integer.valueOf(s);
			}
			public String toString() {
				return "IntegerBidiStringConverter";
			}
		};

}
