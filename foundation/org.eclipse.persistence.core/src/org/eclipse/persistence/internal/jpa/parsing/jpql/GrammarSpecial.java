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
package org.eclipse.persistence.internal.jpa.parsing.jpql;


/**
 * This class provides for versioning of the grammar
 */
public class GrammarSpecial {
    //This shows the grammar (in comment form), copied directly from "JPQLParser.g"
    public static void grammar() {

        /*
        // Added 20/12/2000 JED. Define the package for the class
        header {
        package org.eclipse.persistence.internal.jpa.parsing.jpql;
        }

        class JPQLParser extends Parser;
        options {
        exportVocab=EJBQL;
        k = 2; // This is the number of tokens to look ahead to
        buildAST = true;
        }

        tokens {
        FROM="FROM";
        WHERE="WHERE";
        OR="OR";
        AND="AND";
        TRUE="TRUE";
        FALSE="FALSE";
        BETWEEN="BETWEEN";
        CONCAT="CONCAT";
        SUBSTRING="SUBSTRING";
        LENGTH="LENGTH";
        LOCATE="LOCATE";
        ABS="ABS";
        SQRT="SQRT";
        IS="IS";
        UNKNOWN="UNKNOWN";
        LIKE="LIKE";
        NOT="NOT";
        PERCENT="%";
        UNDERSCORE="_";
        IN="IN";
        NULL="NULL";
        EMPTY="EMPTY";
        AS="AS";
        }

        document
        : (fromClause) (whereClause)?
        ;

        //================================================
        fromClause
        : from identificationVariableDeclaration (COMMA  identificationVariableDeclaration)*
        ;

        from
        : FROM {matchedFrom();}
        ;

        identificationVariableDeclaration
        : collectionMemberDeclaration
        | rangeVariableDeclaration
        ;

        collectionMemberDeclaration
        : identifier IN singleValuedNavigation
        ;

        rangeVariableDeclaration
        : abstractSchemaName (AS)? abstractSchemaIdentifier
        ;

        singleValuedPathExpression
        //    : (singleValuedNavigation | identifier) DOT^ identifier
        : singleValuedNavigation
        ;

        singleValuedNavigation
        : identifier dot (identifier dot)* identifier
        ;

        //collectionValuedPathExpression
        //    : identifier DOT^ (identifier DOT^)* identifier

        //================================================

        //from
        //    : (FROM) {matchedFrom();} abstractSchemaClause (whereClause)?
        //    ;

        //Abstract Schema
        //abstractSchemaClause
        //    : abstractSchemaName (abstractSchemaVariableClause)?
        //    ;

        abstractSchemaName
        : TEXTCHAR {matchedAbstractSchemaName();}
        ;

        abstractSchemaIdentifier
        : identifier {matchedAbstractSchemaIdentifier();}
        ;

        dot
        : DOT^ {matchedDot();}
        ;

        identifier
        : TEXTCHAR {matchedIdentifier();}
        ;

        whereClause
        : WHERE {matchedWhere();} conditionalExpression
        ;

        conditionalExpression
        : conditionalTerm (OR{matchedOr();} conditionalTerm {finishedOr();})*
        ;

        conditionalTerm
        : {conditionalTermFound();} conditionalFactor (AND{matchedAnd();} conditionalFactor {finishedAnd();})*
        ;

        conditionalFactor
        : conditionalTest
        ;

        conditionalTest
        : conditionalPrimary (isExpression (NOT)? (literalBoolean | UNKNOWN))?
        ;

        conditionalPrimary
        : simpleConditionalExpression
        | (LEFT_ROUND_BRACKET {matchedLeftRoundBracket();} conditionalExpression RIGHT_ROUND_BRACKET {matchedRightRoundBracket();})
        ;

        simpleConditionalExpression
        : comparisonLeftOperand comparisonRemainder
        ;

        comparisonLeftOperand
        : singleValuedPathExpression
        ;

        comparisonRemainder
        : equalsRemainder
        | betweenRemainder
        | likeRemainder
        | inRemainder
        | nullRemainder
        ;

        comparisonExpression
        : expressionOperandNotMagnitude equals expressionOperandNotMagnitude {finishedEquals();}
        | expressionOperandMagnitude comparisonOperator expressionOperandMagnitude {finishedComparisonExpression();}
        ;

        equalsRemainder
        : equals (stringExpression | literalNumeric | singleValuedPathExpression) {finishedEquals();}
        ;

        betweenRemainder
        : (NOT {matchedNot();})? ((BETWEEN {matchedBetween();}) literalNumeric) AND
            {matchedAndAfterBetween();} literalNumeric {finishedBetweenAnd();}
        ;

        likeRemainder
        : (NOT {matchedNot();})? (LIKE {matchedLike();}) literalString {finishedLike();}
        ;

        inRemainder
        : (NOT {matchedNot();})? (IN {matchedIn();})
            (LEFT_ROUND_BRACKET
                (literalString|literalNumeric)
                    (COMMA (literalString|literalNumeric))*
            RIGHT_ROUND_BRACKET)
            {finishedIn();}
        ;

        emptyCollectionRemainder
        : IS (NOT {matchedNot();})? EMPTY {matchedEmpty();} {finishedEmpty();}
        ;

        nullRemainder
        : IS (NOT {matchedNot();})? NULL {matchedNull();} {finishedNull();}
        ;

        arithmeticExpression
        : literal
        ;

        stringExpression
        : literalString
        ;

        expressionOperandNotMagnitude
        : literalString
        | singleValuedPathExpression
        ;

        expressionOperandMagnitude
        : literalNumeric
        //    | singleValuedReferenceExpression
        ;

        singleValueDesignator
        : singleValuedPathExpression
        ;

        singleValuedReferenceExpression
        : variableName (DOT^ variableName)?
        ;

        singleValuedDesignator
        : scalarExpression
        ;

        scalarExpression
        : arithmeticExpression
        ;

        variableName
        : TEXTCHAR {matchedVariableName();}
        ;

        isExpression
        : (IS {matchedIs();})
        ;

        //Literals and Low level stuff

        literal
        : literalNumeric
        | literalBoolean
        | literalString
        ;

        literalNumeric
        : NUM_INT {matchedInteger();}
        | NUM_FLOAT^ {matchedFloat();}
        ;

        literalBoolean
        : TRUE {matchedTRUE();}
        | FALSE {matchedFALSE();}
        ;

        // Added Jan 9, 2001 JED
        literalString
        : STRING_LITERAL {matchedString();}
        ;

        // Added 20/12/2000 JED
        comparisonOperator
        : (equals|greaterThan|greaterThanEqualTo|lessThan|lessThanEqualTo|notEqualTo)
        ;

        equals
        : (EQUALS) {matchedEquals();}
        ;

        greaterThan
        : (GREATER_THAN) {matchedGreaterThan();}
        ;

        greaterThanEqualTo
        : GREATER_THAN_EQUAL_TO {matchedGreaterThanEqualTo();}
        ;

        lessThan
        : (LESS_THAN) {matchedLessThan();}
        ;

        lessThanEqualTo
        : LESS_THAN_EQUAL_TO {matchedLessThanEqualTo();}
        ;

        notEqualTo
        : (NOT_EQUAL_TO) {matchedNotEqualTo();}
        ;

        // End of addition 20/12/2000 JED

        class JPQLLexer extends Lexer;
        options {
        k = 4;
        exportVocab=EJBQL;
        charVocabulary = '\3'..'\377';
        caseSensitive=true;
        }

        // hexadecimal digit (again, note it's protected!)
        protected
        HEX_DIGIT
        :    ('0'..'9'|'A'..'F'|'a'..'f')
        ;

        WS    : (' ' | '\t' | '\n' | '\r')+
        { $setType(Token.SKIP); } ;

        LEFT_ROUND_BRACKET
        : '('
        ;

        RIGHT_ROUND_BRACKET
        : ')'
        ;

        COMMA
        : ','
        ;

        TEXTCHAR
        : ('a'..'z' | 'A'..'Z' | '_')+
        ;

        // a numeric literal
        NUM_INT
        {boolean isDecimal=false;}
        :    '.' {_ttype = DOT;}
                (('0'..'9')+ (EXPONENT)? (FLOAT_SUFFIX)? { _ttype = NUM_FLOAT; })?
        |    (    '0' {isDecimal = true;} // special case for just '0'
                (    ('x'|'X')
                    (                                            // hex
                        // the 'e'|'E' and float suffix stuff look
                        // like hex digits, hence the (...)+ doesn't
                        // know when to stop: ambig.  ANTLR resolves
                        // it correctly by matching immediately.  It
                        // is therefor ok to hush warning.
                        options {
                            warnWhenFollowAmbig=false;
                        }
                    :    HEX_DIGIT
                    )+
                |    ('0'..'7')+                                    // octal
                )?
            |    ('1'..'9') ('0'..'9')*  {isDecimal=true;}        // non-zero decimal
            )
            (    ('l'|'L')

            // only check to see if it's a float if looks like decimal so far
            |    {isDecimal}?
                (    '.' ('0'..'9')* (EXPONENT)? (FLOAT_SUFFIX)?
                |    EXPONENT (FLOAT_SUFFIX)?
                |    FLOAT_SUFFIX
                )
                { _ttype = NUM_FLOAT; }
            )?
        ;

        // a couple protected methods to assist in matching floating point numbers
        protected
        EXPONENT
        :    ('e'|'E') ('+'|'-')? ('0'..'9')+
        ;


        protected
        FLOAT_SUFFIX
        :    'f'|'F'|'d'|'D'
        ;

        EQUALS
        : '='
        ;

        GREATER_THAN
        : '>'
        ;

        GREATER_THAN_EQUAL_TO
        : ">="
        ;

        LESS_THAN
        : '<'
        ;

        LESS_THAN_EQUAL_TO
        : "<="
        ;

        NOT_EQUAL_TO
        : "<>"
        ;

        // Added Jan 9, 2001 JED
        // string literals
        STRING_LITERAL
        : '"' (ESC|~('"'|'\\'))* '"'
        ;

        // Added Jan 9, 2001 JED
        // escape sequence -- note that this is protected; it can only be called
        //   from another lexer rule -- it will not ever directly return a token to
        //   the parser
        // There are various ambiguities hushed in this rule.  The optional
        // '0'...'9' digit matches should be matched here rather than letting
        // them go back to STRING_LITERAL to be matched.  ANTLR does the
        // right thing by matching immediately; hence, it's ok to shut off
        // the FOLLOW ambig warnings.
        protected
        ESC
        :    '\\'
            (    'n'
            |    'r'
            |    't'
            |    'b'
            |    'f'
            |    '"'
            |    '\''
            |    '\\'
            |    ('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            |    ('0'..'3')
                (
                    options {
                        warnWhenFollowAmbig = false;
                    }
                :    ('0'..'7')
                    (
                        options {
                            warnWhenFollowAmbig = false;
                        }
                    :    '0'..'7'
                    )?
                )?
            |    ('4'..'7')
                (
                    options {
                        warnWhenFollowAmbig = false;
                    }
                :    ('0'..'9')
                )?
            )
        ;


        */
    }
}
