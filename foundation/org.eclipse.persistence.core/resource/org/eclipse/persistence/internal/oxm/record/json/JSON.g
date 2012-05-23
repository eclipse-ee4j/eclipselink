/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/

grammar JSON;
options {
        output=AST;
}

tokens {
	OBJECT;
	PAIR;
	STRING;
	NUMBER;
	ARRAY;
	TRUE;
	FALSE;
	NULL;
}

@lexer::header {
/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record.json;
}

@header {
/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record.json;
}

// LEXER

String  : '"' Char* '"';

fragment Char   : ~('"'| '\\')
                | '\\"'
                | '\\\\'
                | '\\/'
                | '\\b'
                | '\\f'
                | '\\n'
                | '\\r'
                | '\\t'
                | '\\u' Hex_Digit Hex_Digit Hex_Digit Hex_Digit;

fragment Hex_Digit : ('0'..'9'|'a'..'f'|'A'..'F');

Number  : Int Frac? Exp?;

fragment  Int : '-'? Digits;

fragment Frac : '.' Digits;

fragment Exp : E Digits;

fragment Digits : '0'..'9'+;

fragment E :('e'|'E') ('+'|'-')?;

Whitespace : (' ' | '\r' | '\t' | '\u000C' | '\n')+ { $channel = HIDDEN; } ;

// PARSER

message : object | array;

object : '{' (pair (',' pair)*)? '}' -> ^(OBJECT pair*);

pair :String ':' value -> ^(PAIR String value);

array : '[' (value (',' value)*)? ']' -> ^(ARRAY value*);

value : String -> ^(STRING String)
        |        Number -> ^(NUMBER Number)
        |        object
        |        array
        |        'true' -> TRUE
        |        'false' -> FALSE
        |        'null' -> NULL
        ;