/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink

// $ANTLR 3.5.2 org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g 2016-05-10 11:03:29

package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

import org.eclipse.persistence.internal.jpa.parsing.NodeFactory.TrimSpecification;
import org.eclipse.persistence.internal.jpa.parsing.jpql.InvalidIdentifierException;
import org.eclipse.persistence.internal.libraries.antlr.runtime.BaseRecognizer;
import org.eclipse.persistence.internal.libraries.antlr.runtime.BitSet;
import org.eclipse.persistence.internal.libraries.antlr.runtime.DFA;
import org.eclipse.persistence.internal.libraries.antlr.runtime.EarlyExitException;
import org.eclipse.persistence.internal.libraries.antlr.runtime.FailedPredicateException;
import org.eclipse.persistence.internal.libraries.antlr.runtime.IntStream;
import org.eclipse.persistence.internal.libraries.antlr.runtime.NoViableAltException;
import org.eclipse.persistence.internal.libraries.antlr.runtime.ParserRuleReturnScope;
import org.eclipse.persistence.internal.libraries.antlr.runtime.RecognitionException;
import org.eclipse.persistence.internal.libraries.antlr.runtime.RecognizerSharedState;
import org.eclipse.persistence.internal.libraries.antlr.runtime.Token;
import org.eclipse.persistence.internal.libraries.antlr.runtime.TokenStream;


import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("all")
public class JPQLParser extends org.eclipse.persistence.internal.jpa.parsing.jpql.JPQLParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ABS", "ALL", "AND", "ANY", "AS",
        "ASC", "AVG", "BETWEEN", "BOTH", "BY", "CASE", "COALESCE", "COMMA", "CONCAT",
        "COUNT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DATE_LITERAL",
        "DATE_STRING", "DELETE", "DESC", "DISTINCT", "DIVIDE", "DOT", "DOUBLE_LITERAL",
        "DOUBLE_SUFFIX", "ELSE", "EMPTY", "END", "ENTRY", "EQUALS", "ESCAPE",
        "EXISTS", "EXPONENT", "FALSE", "FETCH", "FLOAT_LITERAL", "FLOAT_SUFFIX",
        "FROM", "FUNC", "GREATER_THAN", "GREATER_THAN_EQUAL_TO", "GROUP", "HAVING",
        "HEX_DIGIT", "HEX_LITERAL", "IDENT", "IN", "INDEX", "INNER", "INTEGER_LITERAL",
        "INTEGER_SUFFIX", "IS", "JOIN", "KEY", "LEADING", "LEFT", "LEFT_CURLY_BRACKET",
        "LEFT_ROUND_BRACKET", "LENGTH", "LESS_THAN", "LESS_THAN_EQUAL_TO", "LIKE",
        "LOCATE", "LONG_LITERAL", "LOWER", "MAX", "MEMBER", "MIN", "MINUS", "MOD",
        "MULTIPLY", "NAMED_PARAM", "NEW", "NOT", "NOT_EQUAL_TO", "NULL", "NULLIF",
        "NUMERIC_DIGITS", "OBJECT", "OCTAL_LITERAL", "OF", "OR", "ORDER", "OUTER",
        "PLUS", "POSITIONAL_PARAM", "RIGHT_CURLY_BRACKET", "RIGHT_ROUND_BRACKET",
        "SELECT", "SET", "SIZE", "SOME", "SQRT", "STRING_LITERAL_DOUBLE_QUOTED",
        "STRING_LITERAL_SINGLE_QUOTED", "SUBSTRING", "SUM", "TEXTCHAR", "THEN",
        "TIMESTAMP_LITERAL", "TIME_LITERAL", "TIME_STRING", "TRAILING", "TREAT",
        "TRIM", "TRUE", "TYPE", "UNKNOWN", "UPDATE", "UPPER", "VALUE", "WHEN",
        "WHERE", "WS"
    };
    public static final int EOF=-1;
    public static final int ABS=4;
    public static final int ALL=5;
    public static final int AND=6;
    public static final int ANY=7;
    public static final int AS=8;
    public static final int ASC=9;
    public static final int AVG=10;
    public static final int BETWEEN=11;
    public static final int BOTH=12;
    public static final int BY=13;
    public static final int CASE=14;
    public static final int COALESCE=15;
    public static final int COMMA=16;
    public static final int CONCAT=17;
    public static final int COUNT=18;
    public static final int CURRENT_DATE=19;
    public static final int CURRENT_TIME=20;
    public static final int CURRENT_TIMESTAMP=21;
    public static final int DATE_LITERAL=22;
    public static final int DATE_STRING=23;
    public static final int DELETE=24;
    public static final int DESC=25;
    public static final int DISTINCT=26;
    public static final int DIVIDE=27;
    public static final int DOT=28;
    public static final int DOUBLE_LITERAL=29;
    public static final int DOUBLE_SUFFIX=30;
    public static final int ELSE=31;
    public static final int EMPTY=32;
    public static final int END=33;
    public static final int ENTRY=34;
    public static final int EQUALS=35;
    public static final int ESCAPE=36;
    public static final int EXISTS=37;
    public static final int EXPONENT=38;
    public static final int FALSE=39;
    public static final int FETCH=40;
    public static final int FLOAT_LITERAL=41;
    public static final int FLOAT_SUFFIX=42;
    public static final int FROM=43;
    public static final int FUNC=44;
    public static final int GREATER_THAN=45;
    public static final int GREATER_THAN_EQUAL_TO=46;
    public static final int GROUP=47;
    public static final int HAVING=48;
    public static final int HEX_DIGIT=49;
    public static final int HEX_LITERAL=50;
    public static final int IDENT=51;
    public static final int IN=52;
    public static final int INDEX=53;
    public static final int INNER=54;
    public static final int INTEGER_LITERAL=55;
    public static final int INTEGER_SUFFIX=56;
    public static final int IS=57;
    public static final int JOIN=58;
    public static final int KEY=59;
    public static final int LEADING=60;
    public static final int LEFT=61;
    public static final int LEFT_CURLY_BRACKET=62;
    public static final int LEFT_ROUND_BRACKET=63;
    public static final int LENGTH=64;
    public static final int LESS_THAN=65;
    public static final int LESS_THAN_EQUAL_TO=66;
    public static final int LIKE=67;
    public static final int LOCATE=68;
    public static final int LONG_LITERAL=69;
    public static final int LOWER=70;
    public static final int MAX=71;
    public static final int MEMBER=72;
    public static final int MIN=73;
    public static final int MINUS=74;
    public static final int MOD=75;
    public static final int MULTIPLY=76;
    public static final int NAMED_PARAM=77;
    public static final int NEW=78;
    public static final int NOT=79;
    public static final int NOT_EQUAL_TO=80;
    public static final int NULL=81;
    public static final int NULLIF=82;
    public static final int NUMERIC_DIGITS=83;
    public static final int OBJECT=84;
    public static final int OCTAL_LITERAL=85;
    public static final int OF=86;
    public static final int OR=87;
    public static final int ORDER=88;
    public static final int OUTER=89;
    public static final int PLUS=90;
    public static final int POSITIONAL_PARAM=91;
    public static final int RIGHT_CURLY_BRACKET=92;
    public static final int RIGHT_ROUND_BRACKET=93;
    public static final int SELECT=94;
    public static final int SET=95;
    public static final int SIZE=96;
    public static final int SOME=97;
    public static final int SQRT=98;
    public static final int STRING_LITERAL_DOUBLE_QUOTED=99;
    public static final int STRING_LITERAL_SINGLE_QUOTED=100;
    public static final int SUBSTRING=101;
    public static final int SUM=102;
    public static final int TEXTCHAR=103;
    public static final int THEN=104;
    public static final int TIMESTAMP_LITERAL=105;
    public static final int TIME_LITERAL=106;
    public static final int TIME_STRING=107;
    public static final int TRAILING=108;
    public static final int TREAT=109;
    public static final int TRIM=110;
    public static final int TRUE=111;
    public static final int TYPE=112;
    public static final int UNKNOWN=113;
    public static final int UPDATE=114;
    public static final int UPPER=115;
    public static final int VALUE=116;
    public static final int WHEN=117;
    public static final int WHERE=118;
    public static final int WS=119;

    // delegate
    public org.eclipse.persistence.internal.jpa.parsing.jpql.JPQLParser[] getDelegates() {
        return new org.eclipse.persistence.internal.jpa.parsing.jpql.JPQLParser[] {};
    }

    // delegator


    public JPQLParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public JPQLParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    @Override public String[] getTokenNames() { return JPQLParser.tokenNames; }
    @Override public String getGrammarFileName() { return "org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g"; }


        /** The root node of the parsed EJBQL query. */
        private Object queryRoot;

        /** Flag indicating whether aggregates are allowed. */
        private boolean aggregatesAllowed = false;

        /** */
        protected void setAggregatesAllowed(boolean allowed) {
            this.aggregatesAllowed = allowed;
        }

        /** */
        protected boolean aggregatesAllowed() {
            return aggregatesAllowed;
        }

        /** */
        protected void validateAbstractSchemaName(Token token)
            throws RecognitionException {
            String text = token.getText();
            if (!isValidJavaIdentifier(token.getText())) {
                throw new InvalidIdentifierException(token);
            }
        }

        /** */
        protected void validateAttributeName(Token token)
            throws RecognitionException {
            String text = token.getText();
            if (!isValidJavaIdentifier(token.getText())) {
                throw new InvalidIdentifierException(token);
            }
        }

        /** */
        protected boolean isValidJavaIdentifier(String text) {
            if ((text == null) || text.equals(""))
                return false;

            // check first char
            if (!Character.isJavaIdentifierStart(text.charAt(0)))
                return false;

            // check remaining character
            for (int i = 1; i < text.length(); i++) {
                if (!Character.isJavaIdentifierPart(text.charAt(i))) {
                    return false;
                }
            }

            return true;
        }

        protected String convertStringLiteral(String text) {
            // skip leading and trailing quote
            String literal = text.substring(1, text.length() - 1);

            // convert ''s to '
            while (true) {
                int index = literal.indexOf("''");
                if (index == -1) {
                    break;
                }
                literal = literal.substring(0, index) +
                          literal.substring(index + 1, literal.length());
            }

            return literal;
        }

        /** */
        public Object getRootNode() {
            return queryRoot;
        }



    // $ANTLR start "document"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:216:1: document : (root= selectStatement |root= updateStatement |root= deleteStatement );
    public final void document() throws RecognitionException {
        Object root =null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:217:5: (root= selectStatement |root= updateStatement |root= deleteStatement )
            int alt1=3;
            switch ( input.LA(1) ) {
            case SELECT:
                {
                alt1=1;
                }
                break;
            case UPDATE:
                {
                alt1=2;
                }
                break;
            case DELETE:
                {
                alt1=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);
                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:217:7: root= selectStatement
                    {
                    pushFollow(FOLLOW_selectStatement_in_document763);
                    root=selectStatement();
                    state._fsp--;
                    if (state.failed) return;
                    if ( state.backtracking==0 ) {queryRoot = root;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:218:7: root= updateStatement
                    {
                    pushFollow(FOLLOW_updateStatement_in_document777);
                    root=updateStatement();
                    state._fsp--;
                    if (state.failed) return;
                    if ( state.backtracking==0 ) {queryRoot = root;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:219:7: root= deleteStatement
                    {
                    pushFollow(FOLLOW_deleteStatement_in_document791);
                    root=deleteStatement();
                    state._fsp--;
                    if (state.failed) return;
                    if ( state.backtracking==0 ) {queryRoot = root;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "document"



    // $ANTLR start "selectStatement"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:222:1: selectStatement returns [Object node] : select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF ;
    public final Object selectStatement() throws RecognitionException {
        Object node = null;


        Object select =null;
        Object from =null;
        Object where =null;
        Object groupBy =null;
        Object having =null;
        Object orderBy =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:226:5: (select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:226:7: select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF
            {
            pushFollow(FOLLOW_selectClause_in_selectStatement824);
            select=selectClause();
            state._fsp--;
            if (state.failed) return node;
            pushFollow(FOLLOW_fromClause_in_selectStatement839);
            from=fromClause();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:228:7: (where= whereClause )?
            int alt2=2;
            int LA2_0 = input.LA(1);
            if ( (LA2_0==WHERE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:228:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_selectStatement854);
                    where=whereClause();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:229:7: (groupBy= groupByClause )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0==GROUP) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:229:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_selectStatement869);
                    groupBy=groupByClause();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:230:7: (having= havingClause )?
            int alt4=2;
            int LA4_0 = input.LA(1);
            if ( (LA4_0==HAVING) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:230:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_selectStatement885);
                    having=havingClause();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:231:7: (orderBy= orderByClause )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( (LA5_0==ORDER) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:231:8: orderBy= orderByClause
                    {
                    pushFollow(FOLLOW_orderByClause_in_selectStatement900);
                    orderBy=orderByClause();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            match(input,EOF,FOLLOW_EOF_in_selectStatement910); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        node = factory.newSelectStatement(0, 0, select, from, where,
                                                          groupBy, having, orderBy);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "selectStatement"



    // $ANTLR start "updateStatement"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:241:1: updateStatement returns [Object node] : update= updateClause set= setClause (where= whereClause )? EOF ;
    public final Object updateStatement() throws RecognitionException {
        Object node = null;


        Object update =null;
        Object set =null;
        Object where =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:245:5: (update= updateClause set= setClause (where= whereClause )? EOF )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:245:7: update= updateClause set= setClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_updateClause_in_updateStatement952);
            update=updateClause();
            state._fsp--;
            if (state.failed) return node;
            pushFollow(FOLLOW_setClause_in_updateStatement967);
            set=setClause();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:247:7: (where= whereClause )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0==WHERE) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:247:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_updateStatement981);
                    where=whereClause();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            match(input,EOF,FOLLOW_EOF_in_updateStatement991); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newUpdateStatement(0, 0, update, set, where); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "updateStatement"



    // $ANTLR start "updateClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:251:1: updateClause returns [Object node] : u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object updateClause() throws RecognitionException {
        Object node = null;


        Token u=null;
        Token ident=null;
        String schema =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:255:5: (u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:255:7: u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            u=(Token)match(input,UPDATE,FOLLOW_UPDATE_in_updateClause1023); if (state.failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_updateClause1029);
            schema=abstractSchemaName();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:256:9: ( ( AS )? ident= IDENT )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            if ( (LA8_0==AS||LA8_0==IDENT) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:256:10: ( AS )? ident= IDENT
                    {
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:256:10: ( AS )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);
                    if ( (LA7_0==AS) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:256:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_updateClause1041); if (state.failed) return node;
                            }
                            break;

                    }

                    ident=(Token)match(input,IDENT,FOLLOW_IDENT_in_updateClause1049); if (state.failed) return node;
                    }
                    break;

            }

            if ( state.backtracking==0 ) {
                        String schemaName = null;
                        if (ident != null){
                            schemaName = ident.getText();
                        }
                        node = factory.newUpdateClause(u.getLine(), u.getCharPositionInLine(),
                                                       schema, schemaName);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "updateClause"


    protected static class setClause_scope {
        List assignments;
    }
    protected Stack<setClause_scope> setClause_stack = new Stack<setClause_scope>();


    // $ANTLR start "setClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:267:1: setClause returns [Object node] : t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* ;
    public final Object setClause() throws RecognitionException {
        setClause_stack.push(new setClause_scope());
        Object node = null;


        Token t=null;
        Object n =null;


            node = null;
            setClause_stack.peek().assignments = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:275:5: (t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:275:7: t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )*
            {
            t=(Token)match(input,SET,FOLLOW_SET_in_setClause1095); if (state.failed) return node;
            pushFollow(FOLLOW_setAssignmentClause_in_setClause1101);
            n=setAssignmentClause();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) { setClause_stack.peek().assignments.add(n); }
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:276:9: ( COMMA n= setAssignmentClause )*
            loop9:
            while (true) {
                int alt9=2;
                int LA9_0 = input.LA(1);
                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }

                switch (alt9) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:276:10: COMMA n= setAssignmentClause
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_setClause1114); if (state.failed) return node;
                    pushFollow(FOLLOW_setAssignmentClause_in_setClause1120);
                    n=setAssignmentClause();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { setClause_stack.peek().assignments.add(n); }
                    }
                    break;

                default :
                    break loop9;
                }
            }

            if ( state.backtracking==0 ) { node = factory.newSetClause(t.getLine(), t.getCharPositionInLine(), setClause_stack.peek().assignments); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            setClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "setClause"



    // $ANTLR start "setAssignmentClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:280:1: setAssignmentClause returns [Object node] : target= setAssignmentTarget t= EQUALS value= newValue ;
    public final Object setAssignmentClause() throws RecognitionException {
        Object node = null;


        Token t=null;
        Object target =null;
        Object value =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:288:5: (target= setAssignmentTarget t= EQUALS value= newValue )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:288:7: target= setAssignmentTarget t= EQUALS value= newValue
            {
            pushFollow(FOLLOW_setAssignmentTarget_in_setAssignmentClause1178);
            target=setAssignmentTarget();
            state._fsp--;
            if (state.failed) return node;
            t=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_setAssignmentClause1182); if (state.failed) return node;
            pushFollow(FOLLOW_newValue_in_setAssignmentClause1188);
            value=newValue();
            state._fsp--;
            if (state.failed) return node;
            }

            if ( state.backtracking==0 ) {
                        node = factory.newSetAssignmentClause(t.getLine(), t.getCharPositionInLine(),
                                                              target, value);
                    }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "setAssignmentClause"



    // $ANTLR start "setAssignmentTarget"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:291:1: setAssignmentTarget returns [Object node] : (n= attribute |n= pathExpression );
    public final Object setAssignmentTarget() throws RecognitionException {
        Object node = null;


        Object n =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:295:5: (n= attribute |n= pathExpression )
            int alt10=2;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA10_1 = input.LA(2);
                if ( (LA10_1==EQUALS) ) {
                    alt10=1;
                }
                else if ( (LA10_1==DOT) ) {
                    alt10=2;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 1, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

                }
                break;
            case KEY:
                {
                int LA10_2 = input.LA(2);
                if ( (LA10_2==LEFT_ROUND_BRACKET) ) {
                    alt10=2;
                }
                else if ( (LA10_2==EQUALS) ) {
                    alt10=1;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 2, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

                }
                break;
            case VALUE:
                {
                int LA10_3 = input.LA(2);
                if ( (LA10_3==LEFT_ROUND_BRACKET) ) {
                    alt10=2;
                }
                else if ( (LA10_3==EQUALS) ) {
                    alt10=1;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 3, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

                }
                break;
            case ABS:
            case ALL:
            case AND:
            case ANY:
            case AS:
            case ASC:
            case AVG:
            case BETWEEN:
            case BOTH:
            case BY:
            case CASE:
            case COALESCE:
            case COMMA:
            case CONCAT:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DATE_LITERAL:
            case DATE_STRING:
            case DELETE:
            case DESC:
            case DISTINCT:
            case DIVIDE:
            case DOT:
            case DOUBLE_LITERAL:
            case DOUBLE_SUFFIX:
            case ELSE:
            case EMPTY:
            case END:
            case ENTRY:
            case EQUALS:
            case ESCAPE:
            case EXISTS:
            case EXPONENT:
            case FALSE:
            case FETCH:
            case FLOAT_LITERAL:
            case FLOAT_SUFFIX:
            case FROM:
            case FUNC:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL_TO:
            case GROUP:
            case HAVING:
            case HEX_DIGIT:
            case HEX_LITERAL:
            case IN:
            case INDEX:
            case INNER:
            case INTEGER_LITERAL:
            case INTEGER_SUFFIX:
            case IS:
            case JOIN:
            case LEADING:
            case LEFT:
            case LEFT_CURLY_BRACKET:
            case LEFT_ROUND_BRACKET:
            case LENGTH:
            case LESS_THAN:
            case LESS_THAN_EQUAL_TO:
            case LIKE:
            case LOCATE:
            case LONG_LITERAL:
            case LOWER:
            case MAX:
            case MEMBER:
            case MIN:
            case MINUS:
            case MOD:
            case MULTIPLY:
            case NAMED_PARAM:
            case NEW:
            case NOT:
            case NOT_EQUAL_TO:
            case NULL:
            case NULLIF:
            case NUMERIC_DIGITS:
            case OBJECT:
            case OCTAL_LITERAL:
            case OF:
            case OR:
            case ORDER:
            case OUTER:
            case PLUS:
            case POSITIONAL_PARAM:
            case RIGHT_CURLY_BRACKET:
            case RIGHT_ROUND_BRACKET:
            case SELECT:
            case SET:
            case SIZE:
            case SOME:
            case SQRT:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case SUBSTRING:
            case SUM:
            case TEXTCHAR:
            case THEN:
            case TIMESTAMP_LITERAL:
            case TIME_LITERAL:
            case TIME_STRING:
            case TRAILING:
            case TREAT:
            case TRIM:
            case TRUE:
            case TYPE:
            case UNKNOWN:
            case UPDATE:
            case UPPER:
            case WHEN:
            case WHERE:
            case WS:
                {
                alt10=1;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);
                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:295:7: n= attribute
                    {
                    pushFollow(FOLLOW_attribute_in_setAssignmentTarget1218);
                    n=attribute();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:296:7: n= pathExpression
                    {
                    pushFollow(FOLLOW_pathExpression_in_setAssignmentTarget1233);
                    n=pathExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "setAssignmentTarget"



    // $ANTLR start "newValue"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:299:1: newValue returns [Object node] : (n= scalarExpression |n1= NULL );
    public final Object newValue() throws RecognitionException {
        Object node = null;


        Token n1=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:301:5: (n= scalarExpression |n1= NULL )
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( (LA11_0==ABS||LA11_0==AVG||(LA11_0 >= CASE && LA11_0 <= COALESCE)||(LA11_0 >= CONCAT && LA11_0 <= DATE_LITERAL)||LA11_0==DOUBLE_LITERAL||LA11_0==FALSE||LA11_0==FLOAT_LITERAL||LA11_0==FUNC||LA11_0==IDENT||LA11_0==INDEX||LA11_0==INTEGER_LITERAL||LA11_0==KEY||(LA11_0 >= LEFT_ROUND_BRACKET && LA11_0 <= LENGTH)||(LA11_0 >= LOCATE && LA11_0 <= MAX)||(LA11_0 >= MIN && LA11_0 <= MOD)||LA11_0==NAMED_PARAM||LA11_0==NULLIF||(LA11_0 >= PLUS && LA11_0 <= POSITIONAL_PARAM)||LA11_0==SIZE||(LA11_0 >= SQRT && LA11_0 <= SUM)||(LA11_0 >= TIMESTAMP_LITERAL && LA11_0 <= TIME_LITERAL)||(LA11_0 >= TRIM && LA11_0 <= TYPE)||(LA11_0 >= UPPER && LA11_0 <= VALUE)) ) {
                alt11=1;
            }
            else if ( (LA11_0==NULL) ) {
                alt11=2;
            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);
                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:301:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_newValue1265);
                    n=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:302:7: n1= NULL
                    {
                    n1=(Token)match(input,NULL,FOLLOW_NULL_in_newValue1279); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newNullLiteral(n1.getLine(), n1.getCharPositionInLine()); }
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "newValue"



    // $ANTLR start "deleteStatement"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:308:1: deleteStatement returns [Object node] : delete= deleteClause (where= whereClause )? EOF ;
    public final Object deleteStatement() throws RecognitionException {
        Object node = null;


        Object delete =null;
        Object where =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:312:5: (delete= deleteClause (where= whereClause )? EOF )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:312:7: delete= deleteClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_deleteClause_in_deleteStatement1321);
            delete=deleteClause();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:313:7: (where= whereClause )?
            int alt12=2;
            int LA12_0 = input.LA(1);
            if ( (LA12_0==WHERE) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:313:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_deleteStatement1334);
                    where=whereClause();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            match(input,EOF,FOLLOW_EOF_in_deleteStatement1344); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newDeleteStatement(0, 0, delete, where); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "deleteStatement"


    protected static class deleteClause_scope {
        String variable;
    }
    protected Stack<deleteClause_scope> deleteClause_stack = new Stack<deleteClause_scope>();


    // $ANTLR start "deleteClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:317:1: deleteClause returns [Object node] : t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object deleteClause() throws RecognitionException {
        deleteClause_stack.push(new deleteClause_scope());
        Object node = null;


        Token t=null;
        Token ident=null;
        String schema =null;


            node = null;
            deleteClause_stack.peek().variable = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:325:5: (t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:325:7: t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            t=(Token)match(input,DELETE,FOLLOW_DELETE_in_deleteClause1377); if (state.failed) return node;
            match(input,FROM,FOLLOW_FROM_in_deleteClause1379); if (state.failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_deleteClause1385);
            schema=abstractSchemaName();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:326:9: ( ( AS )? ident= IDENT )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0==AS||LA14_0==IDENT) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:326:10: ( AS )? ident= IDENT
                    {
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:326:10: ( AS )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    if ( (LA13_0==AS) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:326:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_deleteClause1397); if (state.failed) return node;
                            }
                            break;

                    }

                    ident=(Token)match(input,IDENT,FOLLOW_IDENT_in_deleteClause1403); if (state.failed) return node;
                    if ( state.backtracking==0 ) { deleteClause_stack.peek().variable = ident.getText(); }
                    }
                    break;

            }

            if ( state.backtracking==0 ) {
                        node = factory.newDeleteClause(t.getLine(), t.getCharPositionInLine(),
                                                       schema, deleteClause_stack.peek().variable);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            deleteClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "deleteClause"


    protected static class selectClause_scope {
        boolean distinct;
        List exprs;
        List idents;
    }
    protected Stack<selectClause_scope> selectClause_stack = new Stack<selectClause_scope>();


    // $ANTLR start "selectClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:335:1: selectClause returns [Object node] : t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )* ;
    public final Object selectClause() throws RecognitionException {
        selectClause_stack.push(new selectClause_scope());
        Object node = null;


        Token t=null;
        ParserRuleReturnScope n =null;


            node = null;
            selectClause_stack.peek().distinct = false;
            selectClause_stack.peek().exprs = new ArrayList();
            selectClause_stack.peek().idents = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:347:5: (t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:347:7: t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )*
            {
            t=(Token)match(input,SELECT,FOLLOW_SELECT_in_selectClause1450); if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:347:16: ( DISTINCT )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( (LA15_0==DISTINCT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:347:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause1453); if (state.failed) return node;
                    if ( state.backtracking==0 ) { selectClause_stack.peek().distinct = true; }
                    }
                    break;

            }

            if ( state.backtracking==0 ) { setAggregatesAllowed(true); }
            pushFollow(FOLLOW_selectItem_in_selectClause1471);
            n=selectItem();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
                          selectClause_stack.peek().exprs.add((n!=null?((JPQLParser.selectItem_return)n).expr:null));
                          selectClause_stack.peek().idents.add((n!=null?((JPQLParser.selectItem_return)n).ident:null));
                      }
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:353:11: ( COMMA n= selectItem )*
            loop16:
            while (true) {
                int alt16=2;
                int LA16_0 = input.LA(1);
                if ( (LA16_0==COMMA) ) {
                    alt16=1;
                }

                switch (alt16) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:353:13: COMMA n= selectItem
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_selectClause1497); if (state.failed) return node;
                    pushFollow(FOLLOW_selectItem_in_selectClause1503);
                    n=selectItem();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                      selectClause_stack.peek().exprs.add((n!=null?((JPQLParser.selectItem_return)n).expr:null));
                                      selectClause_stack.peek().idents.add((n!=null?((JPQLParser.selectItem_return)n).ident:null));
                                   }
                    }
                    break;

                default :
                    break loop16;
                }
            }

            if ( state.backtracking==0 ) {
                        setAggregatesAllowed(false);
                        node = factory.newSelectClause(t.getLine(), t.getCharPositionInLine(),
                                                       selectClause_stack.peek().distinct, selectClause_stack.peek().exprs, selectClause_stack.peek().idents);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            selectClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "selectClause"


    public static class selectItem_return extends ParserRuleReturnScope {
        public Object expr;
        public Object ident;
    };


    // $ANTLR start "selectItem"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:367:1: selectItem returns [Object expr, Object ident] : e= selectExpression ( ( AS )? identifier= IDENT )? ;
    public final JPQLParser.selectItem_return selectItem() throws RecognitionException {
        JPQLParser.selectItem_return retval = new JPQLParser.selectItem_return();
        retval.start = input.LT(1);

        Token identifier=null;
        Object e =null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:368:5: (e= selectExpression ( ( AS )? identifier= IDENT )? )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:368:7: e= selectExpression ( ( AS )? identifier= IDENT )?
            {
            pushFollow(FOLLOW_selectExpression_in_selectItem1570);
            e=selectExpression();
            state._fsp--;
            if (state.failed) return retval;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:368:28: ( ( AS )? identifier= IDENT )?
            int alt18=2;
            int LA18_0 = input.LA(1);
            if ( (LA18_0==AS||LA18_0==IDENT) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:368:29: ( AS )? identifier= IDENT
                    {
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:368:29: ( AS )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);
                    if ( (LA17_0==AS) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:368:30: AS
                            {
                            match(input,AS,FOLLOW_AS_in_selectItem1574); if (state.failed) return retval;
                            }
                            break;

                    }

                    identifier=(Token)match(input,IDENT,FOLLOW_IDENT_in_selectItem1582); if (state.failed) return retval;
                    }
                    break;

            }

            if ( state.backtracking==0 ) {
                        retval.expr = e;
                        if (identifier == null){
                            retval.ident = null;
                        } else {
                            retval.ident = identifier.getText();
                        }

                    }
            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "selectItem"



    // $ANTLR start "selectExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:381:1: selectExpression returns [Object node] : (n= aggregateExpression |n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET |n= constructorExpression |n= mapEntryExpression );
    public final Object selectExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:383:5: (n= aggregateExpression |n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET |n= constructorExpression |n= mapEntryExpression )
            int alt19=5;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:383:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_selectExpression1625);
                    n=aggregateExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:384:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_selectExpression1639);
                    n=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:385:7: OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    match(input,OBJECT,FOLLOW_OBJECT_in_selectExpression1649); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1651); if (state.failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_selectExpression1657);
                    n=variableAccessOrTypeConstant();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1659); if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:386:7: n= constructorExpression
                    {
                    pushFollow(FOLLOW_constructorExpression_in_selectExpression1674);
                    n=constructorExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 5 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:387:7: n= mapEntryExpression
                    {
                    pushFollow(FOLLOW_mapEntryExpression_in_selectExpression1689);
                    n=mapEntryExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "selectExpression"



    // $ANTLR start "mapEntryExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:390:1: mapEntryExpression returns [Object node] : l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object mapEntryExpression() throws RecognitionException {
        Object node = null;


        Token l=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:392:5: (l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:392:7: l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            l=(Token)match(input,ENTRY,FOLLOW_ENTRY_in_mapEntryExpression1721); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1723); if (state.failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1729);
            n=variableAccessOrTypeConstant();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1731); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newMapEntry(l.getLine(), l.getCharPositionInLine(), n);}
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "mapEntryExpression"



    // $ANTLR start "pathExprOrVariableAccess"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:395:1: pathExprOrVariableAccess returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )* ;
    public final Object pathExprOrVariableAccess() throws RecognitionException {
        Object node = null;


        Token d=null;
        Object n =null;
        Object right =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:399:5: (n= qualifiedIdentificationVariable (d= DOT right= attribute )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:399:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )*
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1763);
            n=qualifiedIdentificationVariable();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:400:9: (d= DOT right= attribute )*
            loop20:
            while (true) {
                int alt20=2;
                int LA20_0 = input.LA(1);
                if ( (LA20_0==DOT) ) {
                    alt20=1;
                }

                switch (alt20) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:400:10: d= DOT right= attribute
                    {
                    d=(Token)match(input,DOT,FOLLOW_DOT_in_pathExprOrVariableAccess1778); if (state.failed) return node;
                    pushFollow(FOLLOW_attribute_in_pathExprOrVariableAccess1784);
                    right=attribute();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newDot(d.getLine(), d.getCharPositionInLine(), node, right); }
                    }
                    break;

                default :
                    break loop20;
                }
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "pathExprOrVariableAccess"



    // $ANTLR start "qualifiedIdentificationVariable"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:405:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant |l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET |l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );
    public final Object qualifiedIdentificationVariable() throws RecognitionException {
        Object node = null;


        Token l=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:407:5: (n= variableAccessOrTypeConstant |l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET |l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            int alt21=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                alt21=1;
                }
                break;
            case KEY:
                {
                alt21=2;
                }
                break;
            case VALUE:
                {
                alt21=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);
                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:407:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1839);
                    n=variableAccessOrTypeConstant();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:408:7: l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)match(input,KEY,FOLLOW_KEY_in_qualifiedIdentificationVariable1853); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1855); if (state.failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1861);
                    n=variableAccessOrTypeConstant();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1863); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newKey(l.getLine(), l.getCharPositionInLine(), n); }
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:409:7: l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)match(input,VALUE,FOLLOW_VALUE_in_qualifiedIdentificationVariable1878); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1880); if (state.failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1886);
                    n=variableAccessOrTypeConstant();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1888); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "qualifiedIdentificationVariable"


    protected static class aggregateExpression_scope {
        boolean distinct;
    }
    protected Stack<aggregateExpression_scope> aggregateExpression_stack = new Stack<aggregateExpression_scope>();


    // $ANTLR start "aggregateExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:412:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET |t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET |t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET |t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET |t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET );
    public final Object aggregateExpression() throws RecognitionException {
        aggregateExpression_stack.push(new aggregateExpression_scope());
        Object node = null;


        Token t1=null;
        Token t2=null;
        Token t3=null;
        Token t4=null;
        Token t5=null;
        Object n =null;


            node = null;
            aggregateExpression_stack.peek().distinct = false;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:420:5: (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET |t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET |t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET |t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET |t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET )
            int alt27=5;
            switch ( input.LA(1) ) {
            case AVG:
                {
                alt27=1;
                }
                break;
            case MAX:
                {
                alt27=2;
                }
                break;
            case MIN:
                {
                alt27=3;
                }
                break;
            case SUM:
                {
                alt27=4;
                }
                break;
            case COUNT:
                {
                alt27=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);
                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:420:7: t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET
                    {
                    t1=(Token)match(input,AVG,FOLLOW_AVG_in_aggregateExpression1921); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1923); if (state.failed) return node;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:420:33: ( DISTINCT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);
                    if ( (LA22_0==DISTINCT) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:420:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1926); if (state.failed) return node;
                            if ( state.backtracking==0 ) { aggregateExpression_stack.peek().distinct = true; }
                            }
                            break;

                    }

                    pushFollow(FOLLOW_scalarExpression_in_aggregateExpression1944);
                    n=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1946); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newAvg(t1.getLine(), t1.getCharPositionInLine(), aggregateExpression_stack.peek().distinct, n); }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:423:7: t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET
                    {
                    t2=(Token)match(input,MAX,FOLLOW_MAX_in_aggregateExpression1966); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1968); if (state.failed) return node;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:423:33: ( DISTINCT )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    if ( (LA23_0==DISTINCT) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:423:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1971); if (state.failed) return node;
                            if ( state.backtracking==0 ) { aggregateExpression_stack.peek().distinct = true; }
                            }
                            break;

                    }

                    pushFollow(FOLLOW_scalarExpression_in_aggregateExpression1989);
                    n=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1991); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newMax(t2.getLine(), t2.getCharPositionInLine(), aggregateExpression_stack.peek().distinct, n); }
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:426:7: t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET
                    {
                    t3=(Token)match(input,MIN,FOLLOW_MIN_in_aggregateExpression2011); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2013); if (state.failed) return node;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:426:33: ( DISTINCT )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);
                    if ( (LA24_0==DISTINCT) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:426:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2016); if (state.failed) return node;
                            if ( state.backtracking==0 ) { aggregateExpression_stack.peek().distinct = true; }
                            }
                            break;

                    }

                    pushFollow(FOLLOW_scalarExpression_in_aggregateExpression2034);
                    n=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2036); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newMin(t3.getLine(), t3.getCharPositionInLine(), aggregateExpression_stack.peek().distinct, n); }
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:429:7: t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET
                    {
                    t4=(Token)match(input,SUM,FOLLOW_SUM_in_aggregateExpression2056); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2058); if (state.failed) return node;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:429:33: ( DISTINCT )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);
                    if ( (LA25_0==DISTINCT) ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:429:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2061); if (state.failed) return node;
                            if ( state.backtracking==0 ) { aggregateExpression_stack.peek().distinct = true; }
                            }
                            break;

                    }

                    pushFollow(FOLLOW_scalarExpression_in_aggregateExpression2079);
                    n=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2081); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newSum(t4.getLine(), t4.getCharPositionInLine(), aggregateExpression_stack.peek().distinct, n); }
                    }
                    break;
                case 5 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:432:7: t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET
                    {
                    t5=(Token)match(input,COUNT,FOLLOW_COUNT_in_aggregateExpression2101); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2103); if (state.failed) return node;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:432:35: ( DISTINCT )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);
                    if ( (LA26_0==DISTINCT) ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:432:36: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2106); if (state.failed) return node;
                            if ( state.backtracking==0 ) { aggregateExpression_stack.peek().distinct = true; }
                            }
                            break;

                    }

                    pushFollow(FOLLOW_scalarExpression_in_aggregateExpression2124);
                    n=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2126); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newCount(t5.getLine(), t5.getCharPositionInLine(), aggregateExpression_stack.peek().distinct, n); }
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            aggregateExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "aggregateExpression"


    protected static class constructorExpression_scope {
        List args;
    }
    protected Stack<constructorExpression_scope> constructorExpression_stack = new Stack<constructorExpression_scope>();


    // $ANTLR start "constructorExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:437:1: constructorExpression returns [Object node] : t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET ;
    public final Object constructorExpression() throws RecognitionException {
        constructorExpression_stack.push(new constructorExpression_scope());
        Object node = null;


        Token t=null;
        String className =null;
        Object n =null;


            node = null;
            constructorExpression_stack.peek().args = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:445:5: (t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:445:7: t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET
            {
            t=(Token)match(input,NEW,FOLLOW_NEW_in_constructorExpression2169); if (state.failed) return node;
            pushFollow(FOLLOW_constructorName_in_constructorExpression2175);
            className=constructorName();
            state._fsp--;
            if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2185); if (state.failed) return node;
            pushFollow(FOLLOW_constructorItem_in_constructorExpression2199);
            n=constructorItem();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {constructorExpression_stack.peek().args.add(n); }
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:448:9: ( COMMA n= constructorItem )*
            loop28:
            while (true) {
                int alt28=2;
                int LA28_0 = input.LA(1);
                if ( (LA28_0==COMMA) ) {
                    alt28=1;
                }

                switch (alt28) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:448:11: COMMA n= constructorItem
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_constructorExpression2213); if (state.failed) return node;
                    pushFollow(FOLLOW_constructorItem_in_constructorExpression2219);
                    n=constructorItem();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { constructorExpression_stack.peek().args.add(n); }
                    }
                    break;

                default :
                    break loop28;
                }
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2234); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        node = factory.newConstructor(t.getLine(), t.getCharPositionInLine(),
                                                      className, constructorExpression_stack.peek().args);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            constructorExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "constructorExpression"


    protected static class constructorName_scope {
        StringBuffer buf;
    }
    protected Stack<constructorName_scope> constructorName_stack = new Stack<constructorName_scope>();


    // $ANTLR start "constructorName"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:456:1: constructorName returns [String className] : i1= IDENT ( DOT i2= IDENT )* ;
    public final String constructorName() throws RecognitionException {
        constructorName_stack.push(new constructorName_scope());
        String className = null;


        Token i1=null;
        Token i2=null;


            className = null;
            constructorName_stack.peek().buf = new StringBuffer();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:464:5: (i1= IDENT ( DOT i2= IDENT )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:464:7: i1= IDENT ( DOT i2= IDENT )*
            {
            i1=(Token)match(input,IDENT,FOLLOW_IDENT_in_constructorName2275); if (state.failed) return className;
            if ( state.backtracking==0 ) { constructorName_stack.peek().buf.append(i1.getText()); }
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:465:9: ( DOT i2= IDENT )*
            loop29:
            while (true) {
                int alt29=2;
                int LA29_0 = input.LA(1);
                if ( (LA29_0==DOT) ) {
                    alt29=1;
                }

                switch (alt29) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:465:11: DOT i2= IDENT
                    {
                    match(input,DOT,FOLLOW_DOT_in_constructorName2289); if (state.failed) return className;
                    i2=(Token)match(input,IDENT,FOLLOW_IDENT_in_constructorName2293); if (state.failed) return className;
                    if ( state.backtracking==0 ) { constructorName_stack.peek().buf.append('.').append(i2.getText()); }
                    }
                    break;

                default :
                    break loop29;
                }
            }

            if ( state.backtracking==0 ) { className = constructorName_stack.peek().buf.toString(); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            constructorName_stack.pop();
        }
        return className;
    }
    // $ANTLR end "constructorName"



    // $ANTLR start "constructorItem"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:469:1: constructorItem returns [Object node] : (n= scalarExpression |n= aggregateExpression );
    public final Object constructorItem() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:471:5: (n= scalarExpression |n= aggregateExpression )
            int alt30=2;
            alt30 = dfa30.predict(input);
            switch (alt30) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:471:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_constructorItem2337);
                    n=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:472:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_constructorItem2351);
                    n=aggregateExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "constructorItem"


    protected static class fromClause_scope {
        List varDecls;
    }
    protected Stack<fromClause_scope> fromClause_stack = new Stack<fromClause_scope>();


    // $ANTLR start "fromClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:476:1: fromClause returns [Object node] : t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] |n= collectionMemberDeclaration ) )* ;
    public final Object fromClause() throws RecognitionException {
        fromClause_stack.push(new fromClause_scope());
        Object node = null;


        Token t=null;
        Object n =null;


            node = null;
            fromClause_stack.peek().varDecls = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:484:5: (t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] |n= collectionMemberDeclaration ) )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:484:7: t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] |n= collectionMemberDeclaration ) )*
            {
            t=(Token)match(input,FROM,FOLLOW_FROM_in_fromClause2385); if (state.failed) return node;
            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2387);
            identificationVariableDeclaration(fromClause_stack.peek().varDecls);
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:485:9: ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] |n= collectionMemberDeclaration ) )*
            loop32:
            while (true) {
                int alt32=2;
                int LA32_0 = input.LA(1);
                if ( (LA32_0==COMMA) ) {
                    alt32=1;
                }

                switch (alt32) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:485:10: COMMA ( identificationVariableDeclaration[$fromClause::varDecls] |n= collectionMemberDeclaration )
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_fromClause2399); if (state.failed) return node;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:485:17: ( identificationVariableDeclaration[$fromClause::varDecls] |n= collectionMemberDeclaration )
                    int alt31=2;
                    int LA31_0 = input.LA(1);
                    if ( (LA31_0==IN) ) {
                        int LA31_1 = input.LA(2);
                        if ( (LA31_1==LEFT_ROUND_BRACKET) ) {
                            alt31=2;
                        }
                        else if ( (LA31_1==AS||LA31_1==IDENT) ) {
                            alt31=1;
                        }

                        else {
                            if (state.backtracking>0) {state.failed=true; return node;}
                            int nvaeMark = input.mark();
                            try {
                                input.consume();
                                NoViableAltException nvae =
                                    new NoViableAltException("", 31, 1, input);
                                throw nvae;
                            } finally {
                                input.rewind(nvaeMark);
                            }
                        }

                    }
                    else if ( ((LA31_0 >= ABS && LA31_0 <= IDENT)||(LA31_0 >= INDEX && LA31_0 <= WS)) ) {
                        alt31=1;
                    }

                    else {
                        if (state.backtracking>0) {state.failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 31, 0, input);
                        throw nvae;
                    }

                    switch (alt31) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:485:19: identificationVariableDeclaration[$fromClause::varDecls]
                            {
                            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2404);
                            identificationVariableDeclaration(fromClause_stack.peek().varDecls);
                            state._fsp--;
                            if (state.failed) return node;
                            }
                            break;
                        case 2 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:486:19: n= collectionMemberDeclaration
                            {
                            pushFollow(FOLLOW_collectionMemberDeclaration_in_fromClause2429);
                            n=collectionMemberDeclaration();
                            state._fsp--;
                            if (state.failed) return node;
                            if ( state.backtracking==0 ) {fromClause_stack.peek().varDecls.add(n); }
                            }
                            break;

                    }

                    }
                    break;

                default :
                    break loop32;
                }
            }

            if ( state.backtracking==0 ) { node = factory.newFromClause(t.getLine(), t.getCharPositionInLine(), fromClause_stack.peek().varDecls); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            fromClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "fromClause"



    // $ANTLR start "identificationVariableDeclaration"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:492:1: identificationVariableDeclaration[List varDecls] : node= rangeVariableDeclaration (node= join )* ;
    public final void identificationVariableDeclaration(List varDecls) throws RecognitionException {
        Object node =null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:493:5: (node= rangeVariableDeclaration (node= join )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:493:7: node= rangeVariableDeclaration (node= join )*
            {
            pushFollow(FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2494);
            node=rangeVariableDeclaration();
            state._fsp--;
            if (state.failed) return;
            if ( state.backtracking==0 ) { varDecls.add(node); }
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:494:9: (node= join )*
            loop33:
            while (true) {
                int alt33=2;
                int LA33_0 = input.LA(1);
                if ( (LA33_0==INNER||LA33_0==JOIN||LA33_0==LEFT) ) {
                    alt33=1;
                }

                switch (alt33) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:494:11: node= join
                    {
                    pushFollow(FOLLOW_join_in_identificationVariableDeclaration2512);
                    node=join();
                    state._fsp--;
                    if (state.failed) return;
                    if ( state.backtracking==0 ) { varDecls.add(node); }
                    }
                    break;

                default :
                    break loop33;
                }
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "identificationVariableDeclaration"



    // $ANTLR start "rangeVariableDeclaration"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:497:1: rangeVariableDeclaration returns [Object node] : schema= abstractSchemaName ( AS )? i= IDENT ;
    public final Object rangeVariableDeclaration() throws RecognitionException {
        Object node = null;


        Token i=null;
        String schema =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:501:5: (schema= abstractSchemaName ( AS )? i= IDENT )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:501:7: schema= abstractSchemaName ( AS )? i= IDENT
            {
            pushFollow(FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2547);
            schema=abstractSchemaName();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:501:35: ( AS )?
            int alt34=2;
            int LA34_0 = input.LA(1);
            if ( (LA34_0==AS) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:501:36: AS
                    {
                    match(input,AS,FOLLOW_AS_in_rangeVariableDeclaration2550); if (state.failed) return node;
                    }
                    break;

            }

            i=(Token)match(input,IDENT,FOLLOW_IDENT_in_rangeVariableDeclaration2556); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        node = factory.newRangeVariableDecl(i.getLine(), i.getCharPositionInLine(),
                                                            schema, i.getText());
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "rangeVariableDeclaration"



    // $ANTLR start "abstractSchemaName"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:512:1: abstractSchemaName returns [String schema] : ident= . ;
    public final String abstractSchemaName() throws RecognitionException {
        String schema = null;


        Token ident=null;

         schema = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:514:5: (ident= . )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:514:7: ident= .
            {
            ident=input.LT(1);
            matchAny(input); if (state.failed) return schema;
            if ( state.backtracking==0 ) {
                        schema = ident.getText();
                        validateAbstractSchemaName(ident);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return schema;
    }
    // $ANTLR end "abstractSchemaName"



    // $ANTLR start "join"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:521:1: join returns [Object node] : outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT |t= FETCH n= joinAssociationPathExpression ) ;
    public final Object join() throws RecognitionException {
        Object node = null;


        Token i=null;
        Token castClass=null;
        Token t=null;
        boolean outerJoin =false;
        Object n =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:525:5: (outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT |t= FETCH n= joinAssociationPathExpression ) )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:525:7: outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT |t= FETCH n= joinAssociationPathExpression )
            {
            pushFollow(FOLLOW_joinSpec_in_join2638);
            outerJoin=joinSpec();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:526:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT |t= FETCH n= joinAssociationPathExpression )
            int alt37=3;
            switch ( input.LA(1) ) {
            case IDENT:
            case KEY:
            case VALUE:
                {
                alt37=1;
                }
                break;
            case TREAT:
                {
                alt37=2;
                }
                break;
            case FETCH:
                {
                alt37=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);
                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:526:9: n= joinAssociationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2652);
                    n=joinAssociationPathExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:526:43: ( AS )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);
                    if ( (LA35_0==AS) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:526:44: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2655); if (state.failed) return node;
                            }
                            break;

                    }

                    i=(Token)match(input,IDENT,FOLLOW_IDENT_in_join2661); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(),
                                                                   outerJoin, n, i.getText(), null);
                            }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:532:8: TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT
                    {
                    match(input,TREAT,FOLLOW_TREAT_in_join2688); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_join2690); if (state.failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2696);
                    n=joinAssociationPathExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,AS,FOLLOW_AS_in_join2698); if (state.failed) return node;
                    castClass=(Token)match(input,IDENT,FOLLOW_IDENT_in_join2704); if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_join2706); if (state.failed) return node;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:532:108: ( AS )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);
                    if ( (LA36_0==AS) ) {
                        alt36=1;
                    }
                    switch (alt36) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:532:109: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2709); if (state.failed) return node;
                            }
                            break;

                    }

                    i=(Token)match(input,IDENT,FOLLOW_IDENT_in_join2715); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(),
                                                                   outerJoin, n, i.getText(), castClass.getText());
                            }
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:537:9: t= FETCH n= joinAssociationPathExpression
                    {
                    t=(Token)match(input,FETCH,FOLLOW_FETCH_in_join2737); if (state.failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2743);
                    n=joinAssociationPathExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                node = factory.newFetchJoin(t.getLine(), t.getCharPositionInLine(),
                                                            outerJoin, n); }
                    }
                    break;

            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "join"



    // $ANTLR start "joinSpec"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:544:1: joinSpec returns [boolean outer] : ( LEFT ( OUTER )? | INNER )? JOIN ;
    public final boolean joinSpec() throws RecognitionException {
        boolean outer = false;


         outer = false;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:546:5: ( ( LEFT ( OUTER )? | INNER )? JOIN )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:546:7: ( LEFT ( OUTER )? | INNER )? JOIN
            {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:546:7: ( LEFT ( OUTER )? | INNER )?
            int alt39=3;
            int LA39_0 = input.LA(1);
            if ( (LA39_0==LEFT) ) {
                alt39=1;
            }
            else if ( (LA39_0==INNER) ) {
                alt39=2;
            }
            switch (alt39) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:546:8: LEFT ( OUTER )?
                    {
                    match(input,LEFT,FOLLOW_LEFT_in_joinSpec2788); if (state.failed) return outer;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:546:13: ( OUTER )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);
                    if ( (LA38_0==OUTER) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:546:14: OUTER
                            {
                            match(input,OUTER,FOLLOW_OUTER_in_joinSpec2791); if (state.failed) return outer;
                            }
                            break;

                    }

                    if ( state.backtracking==0 ) { outer = true; }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:546:44: INNER
                    {
                    match(input,INNER,FOLLOW_INNER_in_joinSpec2800); if (state.failed) return outer;
                    }
                    break;

            }

            match(input,JOIN,FOLLOW_JOIN_in_joinSpec2806); if (state.failed) return outer;
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return outer;
    }
    // $ANTLR end "joinSpec"



    // $ANTLR start "collectionMemberDeclaration"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:549:1: collectionMemberDeclaration returns [Object node] : t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT ;
    public final Object collectionMemberDeclaration() throws RecognitionException {
        Object node = null;


        Token t=null;
        Token i=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:551:5: (t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:551:7: t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT
            {
            t=(Token)match(input,IN,FOLLOW_IN_in_collectionMemberDeclaration2834); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2836); if (state.failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2842);
            n=collectionValuedPathExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2844); if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:552:7: ( AS )?
            int alt40=2;
            int LA40_0 = input.LA(1);
            if ( (LA40_0==AS) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:552:8: AS
                    {
                    match(input,AS,FOLLOW_AS_in_collectionMemberDeclaration2853); if (state.failed) return node;
                    }
                    break;

            }

            i=(Token)match(input,IDENT,FOLLOW_IDENT_in_collectionMemberDeclaration2859); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                      node = factory.newCollectionMemberVariableDecl(
                            t.getLine(), t.getCharPositionInLine(), n, i.getText());
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "collectionMemberDeclaration"



    // $ANTLR start "collectionValuedPathExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:559:1: collectionValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object collectionValuedPathExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:561:5: (n= pathExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:561:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_collectionValuedPathExpression2897);
            n=pathExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "collectionValuedPathExpression"



    // $ANTLR start "associationPathExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:564:1: associationPathExpression returns [Object node] : n= pathExpression ;
    public final Object associationPathExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:566:5: (n= pathExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:566:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_associationPathExpression2929);
            n=pathExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "associationPathExpression"



    // $ANTLR start "joinAssociationPathExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:569:1: joinAssociationPathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object joinAssociationPathExpression() throws RecognitionException {
        Object node = null;


        Token d=null;
        Object n =null;
        Object right =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:573:9: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:573:11: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_joinAssociationPathExpression2965);
            n=qualifiedIdentificationVariable();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:574:9: (d= DOT right= attribute )+
            int cnt41=0;
            loop41:
            while (true) {
                int alt41=2;
                int LA41_0 = input.LA(1);
                if ( (LA41_0==DOT) ) {
                    alt41=1;
                }

                switch (alt41) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:574:10: d= DOT right= attribute
                    {
                    d=(Token)match(input,DOT,FOLLOW_DOT_in_joinAssociationPathExpression2980); if (state.failed) return node;
                    pushFollow(FOLLOW_attribute_in_joinAssociationPathExpression2986);
                    right=attribute();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newDot(d.getLine(), d.getCharPositionInLine(), node, right); }
                    }
                    break;

                default :
                    if ( cnt41 >= 1 ) break loop41;
                    if (state.backtracking>0) {state.failed=true; return node;}
                    EarlyExitException eee = new EarlyExitException(41, input);
                    throw eee;
                }
                cnt41++;
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "joinAssociationPathExpression"



    // $ANTLR start "singleValuedPathExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:579:1: singleValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object singleValuedPathExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:581:5: (n= pathExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:581:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_singleValuedPathExpression3041);
            n=pathExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "singleValuedPathExpression"



    // $ANTLR start "stateFieldPathExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:584:1: stateFieldPathExpression returns [Object node] : n= pathExpression ;
    public final Object stateFieldPathExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:586:5: (n= pathExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:586:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_stateFieldPathExpression3073);
            n=pathExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "stateFieldPathExpression"



    // $ANTLR start "pathExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:589:1: pathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object pathExpression() throws RecognitionException {
        Object node = null;


        Token d=null;
        Object n =null;
        Object right =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:593:5: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:593:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExpression3105);
            n=qualifiedIdentificationVariable();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:594:9: (d= DOT right= attribute )+
            int cnt42=0;
            loop42:
            while (true) {
                int alt42=2;
                int LA42_0 = input.LA(1);
                if ( (LA42_0==DOT) ) {
                    alt42=1;
                }

                switch (alt42) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:594:10: d= DOT right= attribute
                    {
                    d=(Token)match(input,DOT,FOLLOW_DOT_in_pathExpression3120); if (state.failed) return node;
                    pushFollow(FOLLOW_attribute_in_pathExpression3126);
                    right=attribute();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                    node = factory.newDot(d.getLine(), d.getCharPositionInLine(), node, right);
                                }
                    }
                    break;

                default :
                    if ( cnt42 >= 1 ) break loop42;
                    if (state.backtracking>0) {state.failed=true; return node;}
                    EarlyExitException eee = new EarlyExitException(42, input);
                    throw eee;
                }
                cnt42++;
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "pathExpression"



    // $ANTLR start "attribute"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:605:1: attribute returns [Object node] : i= . ;
    public final Object attribute() throws RecognitionException {
        Object node = null;


        Token i=null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:608:5: (i= . )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:608:7: i= .
            {
            i=input.LT(1);
            matchAny(input); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        validateAttributeName(i);
                        node = factory.newAttribute(i.getLine(), i.getCharPositionInLine(), i.getText());
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "attribute"



    // $ANTLR start "variableAccessOrTypeConstant"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:615:1: variableAccessOrTypeConstant returns [Object node] : i= IDENT ;
    public final Object variableAccessOrTypeConstant() throws RecognitionException {
        Object node = null;


        Token i=null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:617:5: (i= IDENT )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:617:7: i= IDENT
            {
            i=(Token)match(input,IDENT,FOLLOW_IDENT_in_variableAccessOrTypeConstant3222); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newVariableAccessOrTypeConstant(i.getLine(), i.getCharPositionInLine(), i.getText()); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "variableAccessOrTypeConstant"



    // $ANTLR start "whereClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:621:1: whereClause returns [Object node] : t= WHERE n= conditionalExpression ;
    public final Object whereClause() throws RecognitionException {
        Object node = null;


        Token t=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:623:5: (t= WHERE n= conditionalExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:623:7: t= WHERE n= conditionalExpression
            {
            t=(Token)match(input,WHERE,FOLLOW_WHERE_in_whereClause3260); if (state.failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whereClause3266);
            n=conditionalExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        node = factory.newWhereClause(t.getLine(), t.getCharPositionInLine(), n);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "whereClause"



    // $ANTLR start "conditionalExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:629:1: conditionalExpression returns [Object node] : n= conditionalTerm (t= OR right= conditionalTerm )* ;
    public final Object conditionalExpression() throws RecognitionException {
        Object node = null;


        Token t=null;
        Object n =null;
        Object right =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:633:5: (n= conditionalTerm (t= OR right= conditionalTerm )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:633:7: n= conditionalTerm (t= OR right= conditionalTerm )*
            {
            pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3306);
            n=conditionalTerm();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:634:9: (t= OR right= conditionalTerm )*
            loop43:
            while (true) {
                int alt43=2;
                int LA43_0 = input.LA(1);
                if ( (LA43_0==OR) ) {
                    alt43=1;
                }

                switch (alt43) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:634:10: t= OR right= conditionalTerm
                    {
                    t=(Token)match(input,OR,FOLLOW_OR_in_conditionalExpression3321); if (state.failed) return node;
                    pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3327);
                    right=conditionalTerm();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newOr(t.getLine(), t.getCharPositionInLine(), node, right); }
                    }
                    break;

                default :
                    break loop43;
                }
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "conditionalExpression"



    // $ANTLR start "conditionalTerm"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:639:1: conditionalTerm returns [Object node] : n= conditionalFactor (t= AND right= conditionalFactor )* ;
    public final Object conditionalTerm() throws RecognitionException {
        Object node = null;


        Token t=null;
        Object n =null;
        Object right =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:643:5: (n= conditionalFactor (t= AND right= conditionalFactor )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:643:7: n= conditionalFactor (t= AND right= conditionalFactor )*
            {
            pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3382);
            n=conditionalFactor();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:644:9: (t= AND right= conditionalFactor )*
            loop44:
            while (true) {
                int alt44=2;
                int LA44_0 = input.LA(1);
                if ( (LA44_0==AND) ) {
                    alt44=1;
                }

                switch (alt44) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:644:10: t= AND right= conditionalFactor
                    {
                    t=(Token)match(input,AND,FOLLOW_AND_in_conditionalTerm3397); if (state.failed) return node;
                    pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3403);
                    right=conditionalFactor();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newAnd(t.getLine(), t.getCharPositionInLine(), node, right); }
                    }
                    break;

                default :
                    break loop44;
                }
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "conditionalTerm"



    // $ANTLR start "conditionalFactor"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:649:1: conditionalFactor returns [Object node] : (n= NOT )? (n1= conditionalPrimary |n1= existsExpression[(n!=null)] ) ;
    public final Object conditionalFactor() throws RecognitionException {
        Object node = null;


        Token n=null;
        Object n1 =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:651:5: ( (n= NOT )? (n1= conditionalPrimary |n1= existsExpression[(n!=null)] ) )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:651:7: (n= NOT )? (n1= conditionalPrimary |n1= existsExpression[(n!=null)] )
            {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:651:7: (n= NOT )?
            int alt45=2;
            int LA45_0 = input.LA(1);
            if ( (LA45_0==NOT) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:651:8: n= NOT
                    {
                    n=(Token)match(input,NOT,FOLLOW_NOT_in_conditionalFactor3457); if (state.failed) return node;
                    }
                    break;

            }

            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:652:9: (n1= conditionalPrimary |n1= existsExpression[(n!=null)] )
            int alt46=2;
            int LA46_0 = input.LA(1);
            if ( (LA46_0==ABS||LA46_0==AVG||(LA46_0 >= CASE && LA46_0 <= COALESCE)||(LA46_0 >= CONCAT && LA46_0 <= DATE_LITERAL)||LA46_0==DOUBLE_LITERAL||LA46_0==FALSE||LA46_0==FLOAT_LITERAL||LA46_0==FUNC||LA46_0==IDENT||LA46_0==INDEX||LA46_0==INTEGER_LITERAL||LA46_0==KEY||(LA46_0 >= LEFT_ROUND_BRACKET && LA46_0 <= LENGTH)||(LA46_0 >= LOCATE && LA46_0 <= MAX)||(LA46_0 >= MIN && LA46_0 <= MOD)||LA46_0==NAMED_PARAM||LA46_0==NULLIF||(LA46_0 >= PLUS && LA46_0 <= POSITIONAL_PARAM)||LA46_0==SIZE||(LA46_0 >= SQRT && LA46_0 <= SUM)||(LA46_0 >= TIMESTAMP_LITERAL && LA46_0 <= TIME_LITERAL)||(LA46_0 >= TRIM && LA46_0 <= TYPE)||(LA46_0 >= UPPER && LA46_0 <= VALUE)) ) {
                alt46=1;
            }
            else if ( (LA46_0==EXISTS) ) {
                alt46=2;
            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);
                throw nvae;
            }

            switch (alt46) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:652:11: n1= conditionalPrimary
                    {
                    pushFollow(FOLLOW_conditionalPrimary_in_conditionalFactor3475);
                    n1=conditionalPrimary();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                  node = n1;
                                  if (n != null) {
                                      node = factory.newNot(n.getLine(), n.getCharPositionInLine(), n1);
                                  }
                              }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:659:11: n1= existsExpression[(n!=null)]
                    {
                    pushFollow(FOLLOW_existsExpression_in_conditionalFactor3503);
                    n1=existsExpression((n!=null));
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n1;}
                    }
                    break;

            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "conditionalFactor"



    // $ANTLR start "conditionalPrimary"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:663:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET |n= simpleConditionalExpression );
    public final Object conditionalPrimary() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:665:5: ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET |n= simpleConditionalExpression )
            int alt47=2;
            alt47 = dfa47.predict(input);
            switch (alt47) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:665:7: ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3560); if (state.failed) return node;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalPrimary3566);
                    n=conditionalExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3568); if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:667:7: n= simpleConditionalExpression
                    {
                    pushFollow(FOLLOW_simpleConditionalExpression_in_conditionalPrimary3582);
                    n=simpleConditionalExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "conditionalPrimary"



    // $ANTLR start "simpleConditionalExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:670:1: simpleConditionalExpression returns [Object node] : (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] |left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] );
    public final Object simpleConditionalExpression() throws RecognitionException {
        Object node = null;


        Object left =null;
        Object n =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:674:5: (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] |left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] )
            int alt48=2;
            int LA48_0 = input.LA(1);
            if ( (LA48_0==ABS||LA48_0==AVG||(LA48_0 >= CASE && LA48_0 <= COALESCE)||LA48_0==COUNT||LA48_0==DOUBLE_LITERAL||LA48_0==FLOAT_LITERAL||LA48_0==FUNC||LA48_0==IDENT||LA48_0==INDEX||LA48_0==INTEGER_LITERAL||LA48_0==KEY||(LA48_0 >= LEFT_ROUND_BRACKET && LA48_0 <= LENGTH)||(LA48_0 >= LOCATE && LA48_0 <= LONG_LITERAL)||LA48_0==MAX||(LA48_0 >= MIN && LA48_0 <= MOD)||LA48_0==NAMED_PARAM||LA48_0==NULLIF||(LA48_0 >= PLUS && LA48_0 <= POSITIONAL_PARAM)||LA48_0==SIZE||LA48_0==SQRT||LA48_0==SUM||LA48_0==VALUE) ) {
                alt48=1;
            }
            else if ( (LA48_0==CONCAT||(LA48_0 >= CURRENT_DATE && LA48_0 <= DATE_LITERAL)||LA48_0==FALSE||LA48_0==LOWER||(LA48_0 >= STRING_LITERAL_DOUBLE_QUOTED && LA48_0 <= SUBSTRING)||(LA48_0 >= TIMESTAMP_LITERAL && LA48_0 <= TIME_LITERAL)||(LA48_0 >= TRIM && LA48_0 <= TYPE)||LA48_0==UPPER) ) {
                alt48=2;
            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 48, 0, input);
                throw nvae;
            }

            switch (alt48) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:674:7: left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_simpleConditionalExpression3614);
                    left=arithmeticExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3620);
                    n=simpleConditionalExpressionRemainder(left);
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:675:7: left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3635);
                    left=nonArithmeticScalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3641);
                    n=simpleConditionalExpressionRemainder(left);
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "simpleConditionalExpression"



    // $ANTLR start "simpleConditionalExpressionRemainder"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:678:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );
    public final Object simpleConditionalExpressionRemainder(Object left) throws RecognitionException {
        Object node = null;


        Token n1=null;
        Token n2=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:680:5: (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] )
            int alt51=3;
            switch ( input.LA(1) ) {
            case EQUALS:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL_TO:
            case LESS_THAN:
            case LESS_THAN_EQUAL_TO:
            case NOT_EQUAL_TO:
                {
                alt51=1;
                }
                break;
            case BETWEEN:
            case IN:
            case LIKE:
            case MEMBER:
            case NOT:
                {
                alt51=2;
                }
                break;
            case IS:
                {
                alt51=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);
                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:680:7: n= comparisonExpression[left]
                    {
                    pushFollow(FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3676);
                    n=comparisonExpression(left);
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:681:7: (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left]
                    {
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:681:7: (n1= NOT )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);
                    if ( (LA49_0==NOT) ) {
                        alt49=1;
                    }
                    switch (alt49) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:681:8: n1= NOT
                            {
                            n1=(Token)match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3690); if (state.failed) return node;
                            }
                            break;

                    }

                    pushFollow(FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3698);
                    n=conditionWithNotExpression((n1!=null), left);
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:682:7: IS (n2= NOT )? n= isExpression[(n2!=null), left]
                    {
                    match(input,IS,FOLLOW_IS_in_simpleConditionalExpressionRemainder3709); if (state.failed) return node;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:682:10: (n2= NOT )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);
                    if ( (LA50_0==NOT) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:682:11: n2= NOT
                            {
                            n2=(Token)match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3714); if (state.failed) return node;
                            }
                            break;

                    }

                    pushFollow(FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3722);
                    n=isExpression((n2!=null), left);
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "simpleConditionalExpressionRemainder"



    // $ANTLR start "conditionWithNotExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:685:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] |n= likeExpression[not, left] |n= inExpression[not, left] |n= collectionMemberExpression[not, left] );
    public final Object conditionWithNotExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:687:5: (n= betweenExpression[not, left] |n= likeExpression[not, left] |n= inExpression[not, left] |n= collectionMemberExpression[not, left] )
            int alt52=4;
            switch ( input.LA(1) ) {
            case BETWEEN:
                {
                alt52=1;
                }
                break;
            case LIKE:
                {
                alt52=2;
                }
                break;
            case IN:
                {
                alt52=3;
                }
                break;
            case MEMBER:
                {
                alt52=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);
                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:687:7: n= betweenExpression[not, left]
                    {
                    pushFollow(FOLLOW_betweenExpression_in_conditionWithNotExpression3757);
                    n=betweenExpression(not, left);
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:688:7: n= likeExpression[not, left]
                    {
                    pushFollow(FOLLOW_likeExpression_in_conditionWithNotExpression3772);
                    n=likeExpression(not, left);
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:689:7: n= inExpression[not, left]
                    {
                    pushFollow(FOLLOW_inExpression_in_conditionWithNotExpression3786);
                    n=inExpression(not, left);
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:690:7: n= collectionMemberExpression[not, left]
                    {
                    pushFollow(FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3800);
                    n=collectionMemberExpression(not, left);
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "conditionWithNotExpression"



    // $ANTLR start "isExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:693:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] |n= emptyCollectionComparisonExpression[not, left] );
    public final Object isExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:695:5: (n= nullComparisonExpression[not, left] |n= emptyCollectionComparisonExpression[not, left] )
            int alt53=2;
            int LA53_0 = input.LA(1);
            if ( (LA53_0==NULL) ) {
                alt53=1;
            }
            else if ( (LA53_0==EMPTY) ) {
                alt53=2;
            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);
                throw nvae;
            }

            switch (alt53) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:695:7: n= nullComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_nullComparisonExpression_in_isExpression3835);
                    n=nullComparisonExpression(not, left);
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:696:7: n= emptyCollectionComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_emptyCollectionComparisonExpression_in_isExpression3850);
                    n=emptyCollectionComparisonExpression(not, left);
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "isExpression"



    // $ANTLR start "betweenExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:699:1: betweenExpression[boolean not, Object left] returns [Object node] : t= BETWEEN lowerBound= scalarOrSubSelectExpression AND upperBound= scalarOrSubSelectExpression ;
    public final Object betweenExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;


        Token t=null;
        Object lowerBound =null;
        Object upperBound =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:703:5: (t= BETWEEN lowerBound= scalarOrSubSelectExpression AND upperBound= scalarOrSubSelectExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:703:7: t= BETWEEN lowerBound= scalarOrSubSelectExpression AND upperBound= scalarOrSubSelectExpression
            {
            t=(Token)match(input,BETWEEN,FOLLOW_BETWEEN_in_betweenExpression3883); if (state.failed) return node;
            pushFollow(FOLLOW_scalarOrSubSelectExpression_in_betweenExpression3897);
            lowerBound=scalarOrSubSelectExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,AND,FOLLOW_AND_in_betweenExpression3899); if (state.failed) return node;
            pushFollow(FOLLOW_scalarOrSubSelectExpression_in_betweenExpression3905);
            upperBound=scalarOrSubSelectExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        node = factory.newBetween(t.getLine(), t.getCharPositionInLine(),
                                                  not, left, lowerBound, upperBound);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "betweenExpression"


    protected static class inExpression_scope {
        List items;
    }
    protected Stack<inExpression_scope> inExpression_stack = new Stack<inExpression_scope>();


    // $ANTLR start "inExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:711:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter |t= IN LEFT_ROUND_BRACKET (itemNode= scalarOrSubSelectExpression ( COMMA itemNode= scalarOrSubSelectExpression )* |subqueryNode= subquery ) RIGHT_ROUND_BRACKET );
    public final Object inExpression(boolean not, Object left) throws RecognitionException {
        inExpression_stack.push(new inExpression_scope());
        Object node = null;


        Token t=null;
        Object n =null;
        Object itemNode =null;
        Object subqueryNode =null;


            node = null;
            inExpression_stack.peek().items = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:719:5: (t= IN n= inputParameter |t= IN LEFT_ROUND_BRACKET (itemNode= scalarOrSubSelectExpression ( COMMA itemNode= scalarOrSubSelectExpression )* |subqueryNode= subquery ) RIGHT_ROUND_BRACKET )
            int alt56=2;
            int LA56_0 = input.LA(1);
            if ( (LA56_0==IN) ) {
                int LA56_1 = input.LA(2);
                if ( (LA56_1==LEFT_ROUND_BRACKET) ) {
                    alt56=2;
                }
                else if ( (LA56_1==NAMED_PARAM||LA56_1==POSITIONAL_PARAM) ) {
                    alt56=1;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 56, 1, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);
                throw nvae;
            }

            switch (alt56) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:719:8: t= IN n= inputParameter
                    {
                    t=(Token)match(input,IN,FOLLOW_IN_in_inExpression3951); if (state.failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_inExpression3957);
                    n=inputParameter();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                    node = factory.newIn(t.getLine(), t.getCharPositionInLine(),
                                                         not, left, n);
                                }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:724:9: t= IN LEFT_ROUND_BRACKET (itemNode= scalarOrSubSelectExpression ( COMMA itemNode= scalarOrSubSelectExpression )* |subqueryNode= subquery ) RIGHT_ROUND_BRACKET
                    {
                    t=(Token)match(input,IN,FOLLOW_IN_in_inExpression3983); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3993); if (state.failed) return node;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:726:9: (itemNode= scalarOrSubSelectExpression ( COMMA itemNode= scalarOrSubSelectExpression )* |subqueryNode= subquery )
                    int alt55=2;
                    int LA55_0 = input.LA(1);
                    if ( (LA55_0==ABS||LA55_0==AVG||(LA55_0 >= CASE && LA55_0 <= COALESCE)||(LA55_0 >= CONCAT && LA55_0 <= DATE_LITERAL)||LA55_0==DOUBLE_LITERAL||LA55_0==FALSE||LA55_0==FLOAT_LITERAL||LA55_0==FUNC||LA55_0==IDENT||LA55_0==INDEX||LA55_0==INTEGER_LITERAL||LA55_0==KEY||(LA55_0 >= LEFT_ROUND_BRACKET && LA55_0 <= LENGTH)||(LA55_0 >= LOCATE && LA55_0 <= MAX)||(LA55_0 >= MIN && LA55_0 <= MOD)||LA55_0==NAMED_PARAM||LA55_0==NULLIF||(LA55_0 >= PLUS && LA55_0 <= POSITIONAL_PARAM)||LA55_0==SIZE||(LA55_0 >= SQRT && LA55_0 <= SUM)||(LA55_0 >= TIMESTAMP_LITERAL && LA55_0 <= TIME_LITERAL)||(LA55_0 >= TRIM && LA55_0 <= TYPE)||(LA55_0 >= UPPER && LA55_0 <= VALUE)) ) {
                        alt55=1;
                    }
                    else if ( (LA55_0==SELECT) ) {
                        alt55=2;
                    }

                    else {
                        if (state.backtracking>0) {state.failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 55, 0, input);
                        throw nvae;
                    }

                    switch (alt55) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:726:11: itemNode= scalarOrSubSelectExpression ( COMMA itemNode= scalarOrSubSelectExpression )*
                            {
                            pushFollow(FOLLOW_scalarOrSubSelectExpression_in_inExpression4009);
                            itemNode=scalarOrSubSelectExpression();
                            state._fsp--;
                            if (state.failed) return node;
                            if ( state.backtracking==0 ) { inExpression_stack.peek().items.add(itemNode); }
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:727:13: ( COMMA itemNode= scalarOrSubSelectExpression )*
                            loop54:
                            while (true) {
                                int alt54=2;
                                int LA54_0 = input.LA(1);
                                if ( (LA54_0==COMMA) ) {
                                    alt54=1;
                                }

                                switch (alt54) {
                                case 1 :
                                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:727:15: COMMA itemNode= scalarOrSubSelectExpression
                                    {
                                    match(input,COMMA,FOLLOW_COMMA_in_inExpression4027); if (state.failed) return node;
                                    pushFollow(FOLLOW_scalarOrSubSelectExpression_in_inExpression4033);
                                    itemNode=scalarOrSubSelectExpression();
                                    state._fsp--;
                                    if (state.failed) return node;
                                    if ( state.backtracking==0 ) { inExpression_stack.peek().items.add(itemNode); }
                                    }
                                    break;

                                default :
                                    break loop54;
                                }
                            }

                            if ( state.backtracking==0 ) {
                                            node = factory.newIn(t.getLine(), t.getCharPositionInLine(),
                                                                 not, left, inExpression_stack.peek().items);
                                        }
                            }
                            break;
                        case 2 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:732:11: subqueryNode= subquery
                            {
                            pushFollow(FOLLOW_subquery_in_inExpression4068);
                            subqueryNode=subquery();
                            state._fsp--;
                            if (state.failed) return node;
                            if ( state.backtracking==0 ) {
                                            node = factory.newIn(t.getLine(), t.getCharPositionInLine(),
                                                                 not, left, subqueryNode);
                                        }
                            }
                            break;

                    }

                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4102); if (state.failed) return node;
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            inExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "inExpression"



    // $ANTLR start "likeExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:741:1: likeExpression[boolean not, Object left] returns [Object node] : t= LIKE pattern= scalarOrSubSelectExpression (escapeChars= escape )? ;
    public final Object likeExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;


        Token t=null;
        Object pattern =null;
        Object escapeChars =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:745:5: (t= LIKE pattern= scalarOrSubSelectExpression (escapeChars= escape )? )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:745:7: t= LIKE pattern= scalarOrSubSelectExpression (escapeChars= escape )?
            {
            t=(Token)match(input,LIKE,FOLLOW_LIKE_in_likeExpression4132); if (state.failed) return node;
            pushFollow(FOLLOW_scalarOrSubSelectExpression_in_likeExpression4138);
            pattern=scalarOrSubSelectExpression();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:746:9: (escapeChars= escape )?
            int alt57=2;
            int LA57_0 = input.LA(1);
            if ( (LA57_0==ESCAPE) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:746:10: escapeChars= escape
                    {
                    pushFollow(FOLLOW_escape_in_likeExpression4153);
                    escapeChars=escape();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            if ( state.backtracking==0 ) {
                        node = factory.newLike(t.getLine(), t.getCharPositionInLine(), not,
                                               left, pattern, escapeChars);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "likeExpression"



    // $ANTLR start "escape"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:753:1: escape returns [Object node] : t= ESCAPE escapeClause= scalarExpression ;
    public final Object escape() throws RecognitionException {
        Object node = null;


        Token t=null;
        Object escapeClause =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:757:5: (t= ESCAPE escapeClause= scalarExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:757:7: t= ESCAPE escapeClause= scalarExpression
            {
            t=(Token)match(input,ESCAPE,FOLLOW_ESCAPE_in_escape4193); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_escape4199);
            escapeClause=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newEscape(t.getLine(), t.getCharPositionInLine(), escapeClause); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "escape"



    // $ANTLR start "nullComparisonExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:761:1: nullComparisonExpression[boolean not, Object left] returns [Object node] : t= NULL ;
    public final Object nullComparisonExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;


        Token t=null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:763:5: (t= NULL )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:763:7: t= NULL
            {
            t=(Token)match(input,NULL,FOLLOW_NULL_in_nullComparisonExpression4240); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newIsNull(t.getLine(), t.getCharPositionInLine(), not, left); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "nullComparisonExpression"



    // $ANTLR start "emptyCollectionComparisonExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:767:1: emptyCollectionComparisonExpression[boolean not, Object left] returns [Object node] : t= EMPTY ;
    public final Object emptyCollectionComparisonExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;


        Token t=null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:769:5: (t= EMPTY )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:769:7: t= EMPTY
            {
            t=(Token)match(input,EMPTY,FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4281); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newIsEmpty(t.getLine(), t.getCharPositionInLine(), not, left); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "emptyCollectionComparisonExpression"



    // $ANTLR start "collectionMemberExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:773:1: collectionMemberExpression[boolean not, Object left] returns [Object node] : t= MEMBER ( OF )? n= collectionValuedPathExpression ;
    public final Object collectionMemberExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;


        Token t=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:775:5: (t= MEMBER ( OF )? n= collectionValuedPathExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:775:7: t= MEMBER ( OF )? n= collectionValuedPathExpression
            {
            t=(Token)match(input,MEMBER,FOLLOW_MEMBER_in_collectionMemberExpression4322); if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:775:17: ( OF )?
            int alt58=2;
            int LA58_0 = input.LA(1);
            if ( (LA58_0==OF) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:775:18: OF
                    {
                    match(input,OF,FOLLOW_OF_in_collectionMemberExpression4325); if (state.failed) return node;
                    }
                    break;

            }

            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4333);
            n=collectionValuedPathExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        node = factory.newMemberOf(t.getLine(), t.getCharPositionInLine(),
                                                   not, left, n);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "collectionMemberExpression"



    // $ANTLR start "existsExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:782:1: existsExpression[boolean not] returns [Object node] : t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET ;
    public final Object existsExpression(boolean not) throws RecognitionException {
        Object node = null;


        Token t=null;
        Object subqueryNode =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:786:5: (t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:786:7: t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET
            {
            t=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_existsExpression4373); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4375); if (state.failed) return node;
            pushFollow(FOLLOW_subquery_in_existsExpression4381);
            subqueryNode=subquery();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4383); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        node = factory.newExists(t.getLine(), t.getCharPositionInLine(),
                                                 not, subqueryNode);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "existsExpression"



    // $ANTLR start "comparisonExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:793:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand |t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand |t3= GREATER_THAN n= comparisonExpressionRightOperand |t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand |t5= LESS_THAN n= comparisonExpressionRightOperand |t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );
    public final Object comparisonExpression(Object left) throws RecognitionException {
        Object node = null;


        Token t1=null;
        Token t2=null;
        Token t3=null;
        Token t4=null;
        Token t5=null;
        Token t6=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:795:5: (t1= EQUALS n= comparisonExpressionRightOperand |t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand |t3= GREATER_THAN n= comparisonExpressionRightOperand |t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand |t5= LESS_THAN n= comparisonExpressionRightOperand |t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand )
            int alt59=6;
            switch ( input.LA(1) ) {
            case EQUALS:
                {
                alt59=1;
                }
                break;
            case NOT_EQUAL_TO:
                {
                alt59=2;
                }
                break;
            case GREATER_THAN:
                {
                alt59=3;
                }
                break;
            case GREATER_THAN_EQUAL_TO:
                {
                alt59=4;
                }
                break;
            case LESS_THAN:
                {
                alt59=5;
                }
                break;
            case LESS_THAN_EQUAL_TO:
                {
                alt59=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);
                throw nvae;
            }
            switch (alt59) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:795:7: t1= EQUALS n= comparisonExpressionRightOperand
                    {
                    t1=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_comparisonExpression4423); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4429);
                    n=comparisonExpressionRightOperand();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newEquals(t1.getLine(), t1.getCharPositionInLine(), left, n); }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:797:7: t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t2=(Token)match(input,NOT_EQUAL_TO,FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4449); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4455);
                    n=comparisonExpressionRightOperand();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newNotEquals(t2.getLine(), t2.getCharPositionInLine(), left, n); }
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:799:7: t3= GREATER_THAN n= comparisonExpressionRightOperand
                    {
                    t3=(Token)match(input,GREATER_THAN,FOLLOW_GREATER_THAN_in_comparisonExpression4475); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4481);
                    n=comparisonExpressionRightOperand();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newGreaterThan(t3.getLine(), t3.getCharPositionInLine(), left, n); }
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:801:7: t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t4=(Token)match(input,GREATER_THAN_EQUAL_TO,FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4501); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4507);
                    n=comparisonExpressionRightOperand();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newGreaterThanEqual(t4.getLine(), t4.getCharPositionInLine(), left, n); }
                    }
                    break;
                case 5 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:803:7: t5= LESS_THAN n= comparisonExpressionRightOperand
                    {
                    t5=(Token)match(input,LESS_THAN,FOLLOW_LESS_THAN_in_comparisonExpression4527); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4533);
                    n=comparisonExpressionRightOperand();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newLessThan(t5.getLine(), t5.getCharPositionInLine(), left, n); }
                    }
                    break;
                case 6 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:805:7: t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t6=(Token)match(input,LESS_THAN_EQUAL_TO,FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4553); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4559);
                    n=comparisonExpressionRightOperand();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newLessThanEqual(t6.getLine(), t6.getCharPositionInLine(), left, n); }
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "comparisonExpression"



    // $ANTLR start "comparisonExpressionRightOperand"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:809:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression |n= nonArithmeticScalarExpression |n= anyOrAllExpression );
    public final Object comparisonExpressionRightOperand() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:811:5: (n= arithmeticExpression |n= nonArithmeticScalarExpression |n= anyOrAllExpression )
            int alt60=3;
            switch ( input.LA(1) ) {
            case ABS:
            case AVG:
            case CASE:
            case COALESCE:
            case COUNT:
            case DOUBLE_LITERAL:
            case FLOAT_LITERAL:
            case FUNC:
            case IDENT:
            case INDEX:
            case INTEGER_LITERAL:
            case KEY:
            case LEFT_ROUND_BRACKET:
            case LENGTH:
            case LOCATE:
            case LONG_LITERAL:
            case MAX:
            case MIN:
            case MINUS:
            case MOD:
            case NAMED_PARAM:
            case NULLIF:
            case PLUS:
            case POSITIONAL_PARAM:
            case SIZE:
            case SQRT:
            case SUM:
            case VALUE:
                {
                alt60=1;
                }
                break;
            case CONCAT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DATE_LITERAL:
            case FALSE:
            case LOWER:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case SUBSTRING:
            case TIMESTAMP_LITERAL:
            case TIME_LITERAL:
            case TRIM:
            case TRUE:
            case TYPE:
            case UPPER:
                {
                alt60=2;
                }
                break;
            case ALL:
            case ANY:
            case SOME:
                {
                alt60=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 60, 0, input);
                throw nvae;
            }
            switch (alt60) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:811:7: n= arithmeticExpression
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4599);
                    n=arithmeticExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:812:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4613);
                    n=nonArithmeticScalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:813:7: n= anyOrAllExpression
                    {
                    pushFollow(FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4627);
                    n=anyOrAllExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "comparisonExpressionRightOperand"



    // $ANTLR start "arithmeticExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:816:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object arithmeticExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:818:5: (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt61=2;
            int LA61_0 = input.LA(1);
            if ( (LA61_0==ABS||LA61_0==AVG||(LA61_0 >= CASE && LA61_0 <= COALESCE)||LA61_0==COUNT||LA61_0==DOUBLE_LITERAL||LA61_0==FLOAT_LITERAL||LA61_0==FUNC||LA61_0==IDENT||LA61_0==INDEX||LA61_0==INTEGER_LITERAL||LA61_0==KEY||LA61_0==LENGTH||(LA61_0 >= LOCATE && LA61_0 <= LONG_LITERAL)||LA61_0==MAX||(LA61_0 >= MIN && LA61_0 <= MOD)||LA61_0==NAMED_PARAM||LA61_0==NULLIF||(LA61_0 >= PLUS && LA61_0 <= POSITIONAL_PARAM)||LA61_0==SIZE||LA61_0==SQRT||LA61_0==SUM||LA61_0==VALUE) ) {
                alt61=1;
            }
            else if ( (LA61_0==LEFT_ROUND_BRACKET) ) {
                int LA61_24 = input.LA(2);
                if ( (LA61_24==ABS||LA61_24==AVG||(LA61_24 >= CASE && LA61_24 <= COALESCE)||LA61_24==COUNT||LA61_24==DOUBLE_LITERAL||LA61_24==FLOAT_LITERAL||LA61_24==FUNC||LA61_24==IDENT||LA61_24==INDEX||LA61_24==INTEGER_LITERAL||LA61_24==KEY||(LA61_24 >= LEFT_ROUND_BRACKET && LA61_24 <= LENGTH)||(LA61_24 >= LOCATE && LA61_24 <= LONG_LITERAL)||LA61_24==MAX||(LA61_24 >= MIN && LA61_24 <= MOD)||LA61_24==NAMED_PARAM||LA61_24==NULLIF||(LA61_24 >= PLUS && LA61_24 <= POSITIONAL_PARAM)||LA61_24==SIZE||LA61_24==SQRT||LA61_24==SUM||LA61_24==VALUE) ) {
                    alt61=1;
                }
                else if ( (LA61_24==SELECT) ) {
                    alt61=2;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 61, 24, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 61, 0, input);
                throw nvae;
            }

            switch (alt61) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:818:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4659);
                    n=simpleArithmeticExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:819:7: LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4669); if (state.failed) return node;
                    pushFollow(FOLLOW_subquery_in_arithmeticExpression4675);
                    n=subquery();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4677); if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "arithmeticExpression"



    // $ANTLR start "simpleArithmeticExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:822:1: simpleArithmeticExpression returns [Object node] : n= arithmeticTerm (p= PLUS right= arithmeticTerm |m= MINUS right= arithmeticTerm )* ;
    public final Object simpleArithmeticExpression() throws RecognitionException {
        Object node = null;


        Token p=null;
        Token m=null;
        Object n =null;
        Object right =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:826:5: (n= arithmeticTerm (p= PLUS right= arithmeticTerm |m= MINUS right= arithmeticTerm )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:826:7: n= arithmeticTerm (p= PLUS right= arithmeticTerm |m= MINUS right= arithmeticTerm )*
            {
            pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4709);
            n=arithmeticTerm();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:827:9: (p= PLUS right= arithmeticTerm |m= MINUS right= arithmeticTerm )*
            loop62:
            while (true) {
                int alt62=3;
                int LA62_0 = input.LA(1);
                if ( (LA62_0==PLUS) ) {
                    alt62=1;
                }
                else if ( (LA62_0==MINUS) ) {
                    alt62=2;
                }

                switch (alt62) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:827:11: p= PLUS right= arithmeticTerm
                    {
                    p=(Token)match(input,PLUS,FOLLOW_PLUS_in_simpleArithmeticExpression4725); if (state.failed) return node;
                    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4731);
                    right=arithmeticTerm();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newPlus(p.getLine(), p.getCharPositionInLine(), node, right); }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:829:11: m= MINUS right= arithmeticTerm
                    {
                    m=(Token)match(input,MINUS,FOLLOW_MINUS_in_simpleArithmeticExpression4759); if (state.failed) return node;
                    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4765);
                    right=arithmeticTerm();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newMinus(m.getLine(), m.getCharPositionInLine(), node, right); }
                    }
                    break;

                default :
                    break loop62;
                }
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "simpleArithmeticExpression"



    // $ANTLR start "arithmeticTerm"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:834:1: arithmeticTerm returns [Object node] : n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor |d= DIVIDE right= arithmeticFactor )* ;
    public final Object arithmeticTerm() throws RecognitionException {
        Object node = null;


        Token m=null;
        Token d=null;
        Object n =null;
        Object right =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:838:5: (n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor |d= DIVIDE right= arithmeticFactor )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:838:7: n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor |d= DIVIDE right= arithmeticFactor )*
            {
            pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4821);
            n=arithmeticFactor();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:839:9: (m= MULTIPLY right= arithmeticFactor |d= DIVIDE right= arithmeticFactor )*
            loop63:
            while (true) {
                int alt63=3;
                int LA63_0 = input.LA(1);
                if ( (LA63_0==MULTIPLY) ) {
                    alt63=1;
                }
                else if ( (LA63_0==DIVIDE) ) {
                    alt63=2;
                }

                switch (alt63) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:839:11: m= MULTIPLY right= arithmeticFactor
                    {
                    m=(Token)match(input,MULTIPLY,FOLLOW_MULTIPLY_in_arithmeticTerm4837); if (state.failed) return node;
                    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4843);
                    right=arithmeticFactor();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newMultiply(m.getLine(), m.getCharPositionInLine(), node, right); }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:841:11: d= DIVIDE right= arithmeticFactor
                    {
                    d=(Token)match(input,DIVIDE,FOLLOW_DIVIDE_in_arithmeticTerm4871); if (state.failed) return node;
                    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4877);
                    right=arithmeticFactor();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newDivide(d.getLine(), d.getCharPositionInLine(), node, right); }
                    }
                    break;

                default :
                    break loop63;
                }
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "arithmeticTerm"



    // $ANTLR start "arithmeticFactor"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:846:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary |m= MINUS n= arithmeticPrimary |n= arithmeticPrimary );
    public final Object arithmeticFactor() throws RecognitionException {
        Object node = null;


        Token p=null;
        Token m=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:848:5: (p= PLUS n= arithmeticPrimary |m= MINUS n= arithmeticPrimary |n= arithmeticPrimary )
            int alt64=3;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt64=1;
                }
                break;
            case MINUS:
                {
                alt64=2;
                }
                break;
            case ABS:
            case AVG:
            case CASE:
            case COALESCE:
            case COUNT:
            case DOUBLE_LITERAL:
            case FLOAT_LITERAL:
            case FUNC:
            case IDENT:
            case INDEX:
            case INTEGER_LITERAL:
            case KEY:
            case LEFT_ROUND_BRACKET:
            case LENGTH:
            case LOCATE:
            case LONG_LITERAL:
            case MAX:
            case MIN:
            case MOD:
            case NAMED_PARAM:
            case NULLIF:
            case POSITIONAL_PARAM:
            case SIZE:
            case SQRT:
            case SUM:
            case VALUE:
                {
                alt64=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);
                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:848:7: p= PLUS n= arithmeticPrimary
                    {
                    p=(Token)match(input,PLUS,FOLLOW_PLUS_in_arithmeticFactor4930); if (state.failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4937);
                    n=arithmeticPrimary();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = factory.newUnaryPlus(p.getLine(), p.getCharPositionInLine(), n); }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:850:7: m= MINUS n= arithmeticPrimary
                    {
                    m=(Token)match(input,MINUS,FOLLOW_MINUS_in_arithmeticFactor4957); if (state.failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4963);
                    n=arithmeticPrimary();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newUnaryMinus(m.getLine(), m.getCharPositionInLine(), n); }
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:852:7: n= arithmeticPrimary
                    {
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4985);
                    n=arithmeticPrimary();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "arithmeticFactor"



    // $ANTLR start "arithmeticPrimary"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:855:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression |n= pathExprOrVariableAccess |n= inputParameter |n= caseExpression |n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET |n= literalNumeric );
    public final Object arithmeticPrimary() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:857:5: ({...}?n= aggregateExpression |n= pathExprOrVariableAccess |n= inputParameter |n= caseExpression |n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET |n= literalNumeric )
            int alt65=7;
            switch ( input.LA(1) ) {
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt65=1;
                }
                break;
            case IDENT:
            case KEY:
            case VALUE:
                {
                alt65=2;
                }
                break;
            case NAMED_PARAM:
            case POSITIONAL_PARAM:
                {
                alt65=3;
                }
                break;
            case CASE:
            case COALESCE:
            case NULLIF:
                {
                alt65=4;
                }
                break;
            case ABS:
            case FUNC:
            case INDEX:
            case LENGTH:
            case LOCATE:
            case MOD:
            case SIZE:
            case SQRT:
                {
                alt65=5;
                }
                break;
            case LEFT_ROUND_BRACKET:
                {
                alt65=6;
                }
                break;
            case DOUBLE_LITERAL:
            case FLOAT_LITERAL:
            case INTEGER_LITERAL:
            case LONG_LITERAL:
                {
                alt65=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 65, 0, input);
                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:857:7: {...}?n= aggregateExpression
                    {
                    if ( !(( aggregatesAllowed() )) ) {
                        if (state.backtracking>0) {state.failed=true; return node;}
                        throw new FailedPredicateException(input, "arithmeticPrimary", " aggregatesAllowed() ");
                    }
                    pushFollow(FOLLOW_aggregateExpression_in_arithmeticPrimary5019);
                    n=aggregateExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:858:7: n= pathExprOrVariableAcce
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5033);
                    n=pathExprOrVariableAccess();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:859:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_arithmeticPrimary5047);
                    n=inputParameter();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:860:7: n= caseExpression
                    {
                    pushFollow(FOLLOW_caseExpression_in_arithmeticPrimary5061);
                    n=caseExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 5 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:861:7: n= functionsReturningNumeric
                    {
                    pushFollow(FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5075);
                    n=functionsReturningNumerics();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 6 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:862:7: LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5085); if (state.failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5091);
                    n=simpleArithmeticExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5093); if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 7 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:863:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_arithmeticPrimary5107);
                    n=literalNumeric();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "arithmeticPrimary"



    // $ANTLR start "scalarExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:866:1: scalarExpression returns [Object node] : (n= simpleArithmeticExpression |n= nonArithmeticScalarExpression );
    public final Object scalarExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

        node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:868:5: (n= simpleArithmeticExpression |n= nonArithmeticScalarExpression )
            int alt66=2;
            int LA66_0 = input.LA(1);
            if ( (LA66_0==ABS||LA66_0==AVG||(LA66_0 >= CASE && LA66_0 <= COALESCE)||LA66_0==COUNT||LA66_0==DOUBLE_LITERAL||LA66_0==FLOAT_LITERAL||LA66_0==FUNC||LA66_0==IDENT||LA66_0==INDEX||LA66_0==INTEGER_LITERAL||LA66_0==KEY||(LA66_0 >= LEFT_ROUND_BRACKET && LA66_0 <= LENGTH)||(LA66_0 >= LOCATE && LA66_0 <= LONG_LITERAL)||LA66_0==MAX||(LA66_0 >= MIN && LA66_0 <= MOD)||LA66_0==NAMED_PARAM||LA66_0==NULLIF||(LA66_0 >= PLUS && LA66_0 <= POSITIONAL_PARAM)||LA66_0==SIZE||LA66_0==SQRT||LA66_0==SUM||LA66_0==VALUE) ) {
                alt66=1;
            }
            else if ( (LA66_0==CONCAT||(LA66_0 >= CURRENT_DATE && LA66_0 <= DATE_LITERAL)||LA66_0==FALSE||LA66_0==LOWER||(LA66_0 >= STRING_LITERAL_DOUBLE_QUOTED && LA66_0 <= SUBSTRING)||(LA66_0 >= TIMESTAMP_LITERAL && LA66_0 <= TIME_LITERAL)||(LA66_0 >= TRIM && LA66_0 <= TYPE)||LA66_0==UPPER) ) {
                alt66=2;
            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);
                throw nvae;
            }

            switch (alt66) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:868:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_scalarExpression5139);
                    n=simpleArithmeticExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:869:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5153);
                    n=nonArithmeticScalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "scalarExpression"



    // $ANTLR start "scalarOrSubSelectExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:872:1: scalarOrSubSelectExpression returns [Object node] : (n= arithmeticExpression |n= nonArithmeticScalarExpression );
    public final Object scalarOrSubSelectExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

        node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:874:5: (n= arithmeticExpression |n= nonArithmeticScalarExpression )
            int alt67=2;
            int LA67_0 = input.LA(1);
            if ( (LA67_0==ABS||LA67_0==AVG||(LA67_0 >= CASE && LA67_0 <= COALESCE)||LA67_0==COUNT||LA67_0==DOUBLE_LITERAL||LA67_0==FLOAT_LITERAL||LA67_0==FUNC||LA67_0==IDENT||LA67_0==INDEX||LA67_0==INTEGER_LITERAL||LA67_0==KEY||(LA67_0 >= LEFT_ROUND_BRACKET && LA67_0 <= LENGTH)||(LA67_0 >= LOCATE && LA67_0 <= LONG_LITERAL)||LA67_0==MAX||(LA67_0 >= MIN && LA67_0 <= MOD)||LA67_0==NAMED_PARAM||LA67_0==NULLIF||(LA67_0 >= PLUS && LA67_0 <= POSITIONAL_PARAM)||LA67_0==SIZE||LA67_0==SQRT||LA67_0==SUM||LA67_0==VALUE) ) {
                alt67=1;
            }
            else if ( (LA67_0==CONCAT||(LA67_0 >= CURRENT_DATE && LA67_0 <= DATE_LITERAL)||LA67_0==FALSE||LA67_0==LOWER||(LA67_0 >= STRING_LITERAL_DOUBLE_QUOTED && LA67_0 <= SUBSTRING)||(LA67_0 >= TIMESTAMP_LITERAL && LA67_0 <= TIME_LITERAL)||(LA67_0 >= TRIM && LA67_0 <= TYPE)||LA67_0==UPPER) ) {
                alt67=2;
            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 67, 0, input);
                throw nvae;
            }

            switch (alt67) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:874:7: n= arithmeticExpression
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_scalarOrSubSelectExpression5185);
                    n=arithmeticExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:875:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_scalarOrSubSelectExpression5199);
                    n=nonArithmeticScalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "scalarOrSubSelectExpression"



    // $ANTLR start "nonArithmeticScalarExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:878:1: nonArithmeticScalarExpression returns [Object node] : (n= functionsReturningDatetime |n= functionsReturningStrings |n= literalString |n= literalBoolean |n= literalTemporal |n= entityTypeExpression );
    public final Object nonArithmeticScalarExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

        node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:880:5: (n= functionsReturningDatetime |n= functionsReturningStrings |n= literalString |n= literalBoolean |n= literalTemporal |n= entityTypeExpression )
            int alt68=6;
            switch ( input.LA(1) ) {
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
                {
                alt68=1;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt68=2;
                }
                break;
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt68=3;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt68=4;
                }
                break;
            case DATE_LITERAL:
            case TIMESTAMP_LITERAL:
            case TIME_LITERAL:
                {
                alt68=5;
                }
                break;
            case TYPE:
                {
                alt68=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 68, 0, input);
                throw nvae;
            }
            switch (alt68) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:880:7: n= functionsReturningDatetime
                    {
                    pushFollow(FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5231);
                    n=functionsReturningDatetime();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:881:7: n= functionsReturningString
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5245);
                    n=functionsReturningStrings();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:882:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_nonArithmeticScalarExpression5259);
                    n=literalString();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:883:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5273);
                    n=literalBoolean();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 5 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:884:7: n= literalTemporal
                    {
                    pushFollow(FOLLOW_literalTemporal_in_nonArithmeticScalarExpression5287);
                    n=literalTemporal();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 6 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:885:7: n= entityTypeExpression
                    {
                    pushFollow(FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5301);
                    n=entityTypeExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "nonArithmeticScalarExpression"



    // $ANTLR start "anyOrAllExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:888:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET |y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET |s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object anyOrAllExpression() throws RecognitionException {
        Object node = null;


        Token a=null;
        Token y=null;
        Token s=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:890:5: (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET |y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET |s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt69=3;
            switch ( input.LA(1) ) {
            case ALL:
                {
                alt69=1;
                }
                break;
            case ANY:
                {
                alt69=2;
                }
                break;
            case SOME:
                {
                alt69=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 69, 0, input);
                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:890:7: a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    a=(Token)match(input,ALL,FOLLOW_ALL_in_anyOrAllExpression5331); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5333); if (state.failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5339);
                    n=subquery();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5341); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newAll(a.getLine(), a.getCharPositionInLine(), n); }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:892:7: y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    y=(Token)match(input,ANY,FOLLOW_ANY_in_anyOrAllExpression5361); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5363); if (state.failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5369);
                    n=subquery();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5371); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newAny(y.getLine(), y.getCharPositionInLine(), n); }
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:894:7: s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    s=(Token)match(input,SOME,FOLLOW_SOME_in_anyOrAllExpression5391); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5393); if (state.failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5399);
                    n=subquery();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5401); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newSome(s.getLine(), s.getCharPositionInLine(), n); }
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "anyOrAllExpression"



    // $ANTLR start "entityTypeExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:898:1: entityTypeExpression returns [Object node] : n= typeDiscriminator ;
    public final Object entityTypeExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

        node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:900:5: (n= typeDiscriminator )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:900:7: n= typeDiscriminator
            {
            pushFollow(FOLLOW_typeDiscriminator_in_entityTypeExpression5441);
            n=typeDiscriminator();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {node = n;}
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "entityTypeExpression"



    // $ANTLR start "typeDiscriminator"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:903:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET |c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );
    public final Object typeDiscriminator() throws RecognitionException {
        Object node = null;


        Token a=null;
        Token c=null;
        Object n =null;

        node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:905:5: (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET |c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET )
            int alt70=2;
            int LA70_0 = input.LA(1);
            if ( (LA70_0==TYPE) ) {
                int LA70_1 = input.LA(2);
                if ( (LA70_1==LEFT_ROUND_BRACKET) ) {
                    int LA70_2 = input.LA(3);
                    if ( (LA70_2==IDENT||LA70_2==KEY||LA70_2==VALUE) ) {
                        alt70=1;
                    }
                    else if ( (LA70_2==NAMED_PARAM||LA70_2==POSITIONAL_PARAM) ) {
                        alt70=2;
                    }

                    else {
                        if (state.backtracking>0) {state.failed=true; return node;}
                        int nvaeMark = input.mark();
                        try {
                            for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
                                input.consume();
                            }
                            NoViableAltException nvae =
                                new NoViableAltException("", 70, 2, input);
                            throw nvae;
                        } finally {
                            input.rewind(nvaeMark);
                        }
                    }

                }

                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 70, 1, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 70, 0, input);
                throw nvae;
            }

            switch (alt70) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:905:7: a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET
                    {
                    a=(Token)match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5474); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5476); if (state.failed) return node;
                    pushFollow(FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5482);
                    n=variableOrSingleValuedPath();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5484); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newType(a.getLine(), a.getCharPositionInLine(), n);}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:906:7: c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET
                    {
                    c=(Token)match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5499); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5501); if (state.failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_typeDiscriminator5507);
                    n=inputParameter();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5509); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newType(c.getLine(), c.getCharPositionInLine(), n);}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "typeDiscriminator"



    // $ANTLR start "caseExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:909:1: caseExpression returns [Object node] : (n= simpleCaseExpression |n= generalCaseExpression |n= coalesceExpression |n= nullIfExpression );
    public final Object caseExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

        node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:911:4: (n= simpleCaseExpression |n= generalCaseExpression |n= coalesceExpression |n= nullIfExpression )
            int alt71=4;
            switch ( input.LA(1) ) {
            case CASE:
                {
                int LA71_1 = input.LA(2);
                if ( (LA71_1==IDENT||LA71_1==KEY||LA71_1==TYPE||LA71_1==VALUE) ) {
                    alt71=1;
                }
                else if ( (LA71_1==WHEN) ) {
                    alt71=2;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 71, 1, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

                }
                break;
            case COALESCE:
                {
                alt71=3;
                }
                break;
            case NULLIF:
                {
                alt71=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);
                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:911:6: n= simpleCaseExpression
                    {
                    pushFollow(FOLLOW_simpleCaseExpression_in_caseExpression5540);
                    n=simpleCaseExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:912:6: n= generalCaseExpression
                    {
                    pushFollow(FOLLOW_generalCaseExpression_in_caseExpression5553);
                    n=generalCaseExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:913:6: n= coalesceExpression
                    {
                    pushFollow(FOLLOW_coalesceExpression_in_caseExpression5566);
                    n=coalesceExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:914:6: n= nullIfExpression
                    {
                    pushFollow(FOLLOW_nullIfExpression_in_caseExpression5579);
                    n=nullIfExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "caseExpression"


    protected static class simpleCaseExpression_scope {
        List whens;
    }
    protected Stack<simpleCaseExpression_scope> simpleCaseExpression_stack = new Stack<simpleCaseExpression_scope>();


    // $ANTLR start "simpleCaseExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:917:1: simpleCaseExpression returns [Object node] : a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END ;
    public final Object simpleCaseExpression() throws RecognitionException {
        simpleCaseExpression_stack.push(new simpleCaseExpression_scope());
        Object node = null;


        Token a=null;
        Object c =null;
        Object w =null;
        Object e =null;


            node = null;
            simpleCaseExpression_stack.peek().whens = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:925:4: (a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:925:6: a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END
            {
            a=(Token)match(input,CASE,FOLLOW_CASE_in_simpleCaseExpression5612); if (state.failed) return node;
            pushFollow(FOLLOW_caseOperand_in_simpleCaseExpression5618);
            c=caseOperand();
            state._fsp--;
            if (state.failed) return node;
            pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5624);
            w=simpleWhenClause();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {simpleCaseExpression_stack.peek().whens.add(w);}
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:925:97: (w= simpleWhenClause )*
            loop72:
            while (true) {
                int alt72=2;
                int LA72_0 = input.LA(1);
                if ( (LA72_0==WHEN) ) {
                    alt72=1;
                }

                switch (alt72) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:925:98: w= simpleWhenClause
                    {
                    pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5633);
                    w=simpleWhenClause();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {simpleCaseExpression_stack.peek().whens.add(w);}
                    }
                    break;

                default :
                    break loop72;
                }
            }

            match(input,ELSE,FOLLOW_ELSE_in_simpleCaseExpression5639); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleCaseExpression5645);
            e=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,END,FOLLOW_END_in_simpleCaseExpression5647); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                           node = factory.newCaseClause(a.getLine(), a.getCharPositionInLine(), c,
                                simpleCaseExpression_stack.peek().whens, e);
                       }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            simpleCaseExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "simpleCaseExpression"


    protected static class generalCaseExpression_scope {
        List whens;
    }
    protected Stack<generalCaseExpression_scope> generalCaseExpression_stack = new Stack<generalCaseExpression_scope>();


    // $ANTLR start "generalCaseExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:932:1: generalCaseExpression returns [Object node] : a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END ;
    public final Object generalCaseExpression() throws RecognitionException {
        generalCaseExpression_stack.push(new generalCaseExpression_scope());
        Object node = null;


        Token a=null;
        Object w =null;
        Object e =null;


            node = null;
            generalCaseExpression_stack.peek().whens = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:940:4: (a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:940:6: a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END
            {
            a=(Token)match(input,CASE,FOLLOW_CASE_in_generalCaseExpression5691); if (state.failed) return node;
            pushFollow(FOLLOW_whenClause_in_generalCaseExpression5697);
            w=whenClause();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {generalCaseExpression_stack.peek().whens.add(w);}
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:940:76: (w= whenClause )*
            loop73:
            while (true) {
                int alt73=2;
                int LA73_0 = input.LA(1);
                if ( (LA73_0==WHEN) ) {
                    alt73=1;
                }

                switch (alt73) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:940:77: w= whenClause
                    {
                    pushFollow(FOLLOW_whenClause_in_generalCaseExpression5706);
                    w=whenClause();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {generalCaseExpression_stack.peek().whens.add(w);}
                    }
                    break;

                default :
                    break loop73;
                }
            }

            match(input,ELSE,FOLLOW_ELSE_in_generalCaseExpression5712); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_generalCaseExpression5718);
            e=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,END,FOLLOW_END_in_generalCaseExpression5720); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                           node = factory.newCaseClause(a.getLine(), a.getCharPositionInLine(), null,
                                generalCaseExpression_stack.peek().whens, e);
                       }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            generalCaseExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "generalCaseExpression"


    protected static class coalesceExpression_scope {
        List primaries;
    }
    protected Stack<coalesceExpression_scope> coalesceExpression_stack = new Stack<coalesceExpression_scope>();


    // $ANTLR start "coalesceExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:947:1: coalesceExpression returns [Object node] : c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET ;
    public final Object coalesceExpression() throws RecognitionException {
        coalesceExpression_stack.push(new coalesceExpression_scope());
        Object node = null;


        Token c=null;
        Object p =null;
        Object s =null;


            node = null;
            coalesceExpression_stack.peek().primaries = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:955:4: (c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:955:6: c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET
            {
            c=(Token)match(input,COALESCE,FOLLOW_COALESCE_in_coalesceExpression5764); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5766); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5772);
            p=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {coalesceExpression_stack.peek().primaries.add(p);}
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:955:106: ( COMMA s= scalarExpression )+
            int cnt74=0;
            loop74:
            while (true) {
                int alt74=2;
                int LA74_0 = input.LA(1);
                if ( (LA74_0==COMMA) ) {
                    alt74=1;
                }

                switch (alt74) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:955:107: COMMA s= scalarExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_coalesceExpression5777); if (state.failed) return node;
                    pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5783);
                    s=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {coalesceExpression_stack.peek().primaries.add(s);}
                    }
                    break;

                default :
                    if ( cnt74 >= 1 ) break loop74;
                    if (state.backtracking>0) {state.failed=true; return node;}
                    EarlyExitException eee = new EarlyExitException(74, input);
                    throw eee;
                }
                cnt74++;
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5789); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                           node = factory.newCoalesceClause(c.getLine(), c.getCharPositionInLine(),
                                coalesceExpression_stack.peek().primaries);
                       }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            coalesceExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "coalesceExpression"



    // $ANTLR start "nullIfExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:962:1: nullIfExpression returns [Object node] : n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object nullIfExpression() throws RecognitionException {
        Object node = null;


        Token n=null;
        Object l =null;
        Object r =null;

        node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:964:4: (n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:964:6: n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET
            {
            n=(Token)match(input,NULLIF,FOLLOW_NULLIF_in_nullIfExpression5830); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5832); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5838);
            l=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_nullIfExpression5840); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5846);
            r=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression5848); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                           node = factory.newNullIfClause(n.getLine(), n.getCharPositionInLine(),
                                l, r);
                       }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "nullIfExpression"



    // $ANTLR start "caseOperand"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:972:1: caseOperand returns [Object node] : (n= stateFieldPathExpression |n= typeDiscriminator );
    public final Object caseOperand() throws RecognitionException {
        Object node = null;


        Object n =null;

        node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:974:4: (n= stateFieldPathExpression |n= typeDiscriminator )
            int alt75=2;
            int LA75_0 = input.LA(1);
            if ( (LA75_0==IDENT||LA75_0==KEY||LA75_0==VALUE) ) {
                alt75=1;
            }
            else if ( (LA75_0==TYPE) ) {
                alt75=2;
            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 75, 0, input);
                throw nvae;
            }

            switch (alt75) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:974:6: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_caseOperand5890);
                    n=stateFieldPathExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:975:6: n= typeDiscriminator
                    {
                    pushFollow(FOLLOW_typeDiscriminator_in_caseOperand5904);
                    n=typeDiscriminator();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "caseOperand"



    // $ANTLR start "whenClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:978:1: whenClause returns [Object node] : w= WHEN c= conditionalExpression THEN a= scalarExpression ;
    public final Object whenClause() throws RecognitionException {
        Object node = null;


        Token w=null;
        Object c =null;
        Object a =null;

        node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:980:4: (w= WHEN c= conditionalExpression THEN a= scalarExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:980:6: w= WHEN c= conditionalExpression THEN a= scalarExpression
            {
            w=(Token)match(input,WHEN,FOLLOW_WHEN_in_whenClause5934); if (state.failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whenClause5940);
            c=conditionalExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,THEN,FOLLOW_THEN_in_whenClause5942); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_whenClause5948);
            a=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
                       node = factory.newWhenClause(w.getLine(), w.getCharPositionInLine(),
                           c, a);
                   }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "whenClause"



    // $ANTLR start "simpleWhenClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:987:1: simpleWhenClause returns [Object node] : w= WHEN c= scalarExpression THEN a= scalarExpression ;
    public final Object simpleWhenClause() throws RecognitionException {
        Object node = null;


        Token w=null;
        Object c =null;
        Object a =null;

        node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:989:4: (w= WHEN c= scalarExpression THEN a= scalarExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:989:6: w= WHEN c= scalarExpression THEN a= scalarExpression
            {
            w=(Token)match(input,WHEN,FOLLOW_WHEN_in_simpleWhenClause5985); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause5991);
            c=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,THEN,FOLLOW_THEN_in_simpleWhenClause5993); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause5999);
            a=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
                       node = factory.newWhenClause(w.getLine(), w.getCharPositionInLine(),
                           c, a);
                   }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "simpleWhenClause"



    // $ANTLR start "variableOrSingleValuedPath"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:996:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression |n= variableAccessOrTypeConstant );
    public final Object variableOrSingleValuedPath() throws RecognitionException {
        Object node = null;


        Object n =null;

        node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:998:5: (n= singleValuedPathExpression |n= variableAccessOrTypeConstant )
            int alt76=2;
            int LA76_0 = input.LA(1);
            if ( (LA76_0==IDENT) ) {
                int LA76_1 = input.LA(2);
                if ( (LA76_1==DOT) ) {
                    alt76=1;
                }
                else if ( (LA76_1==RIGHT_ROUND_BRACKET) ) {
                    alt76=2;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 76, 1, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

            }
            else if ( (LA76_0==KEY||LA76_0==VALUE) ) {
                alt76=1;
            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);
                throw nvae;
            }

            switch (alt76) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:998:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6036);
                    n=singleValuedPathExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:999:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6050);
                    n=variableAccessOrTypeConstant();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "variableOrSingleValuedPath"



    // $ANTLR start "stringPrimary"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1002:1: stringPrimary returns [Object node] : (n= literalString |n= functionsReturningStrings |n= inputParameter |n= stateFieldPathExpression );
    public final Object stringPrimary() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1004:5: (n= literalString |n= functionsReturningStrings |n= inputParameter |n= stateFieldPathExpression )
            int alt77=4;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt77=1;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt77=2;
                }
                break;
            case NAMED_PARAM:
            case POSITIONAL_PARAM:
                {
                alt77=3;
                }
                break;
            case IDENT:
            case KEY:
            case VALUE:
                {
                alt77=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);
                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1004:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_stringPrimary6082);
                    n=literalString();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1005:7: n= functionsReturningString
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_stringPrimary6096);
                    n=functionsReturningStrings();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1006:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_stringPrimary6110);
                    n=inputParameter();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1007:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_stringPrimary6124);
                    n=stateFieldPathExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "stringPrimary"



    // $ANTLR start "literal"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1012:1: literal returns [Object node] : (n= literalNumeric |n= literalBoolean |n= literalString );
    public final Object literal() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1014:5: (n= literalNumeric |n= literalBoolean |n= literalString )
            int alt78=3;
            switch ( input.LA(1) ) {
            case DOUBLE_LITERAL:
            case FLOAT_LITERAL:
            case INTEGER_LITERAL:
            case LONG_LITERAL:
                {
                alt78=1;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt78=2;
                }
                break;
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt78=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 78, 0, input);
                throw nvae;
            }
            switch (alt78) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1014:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_literal6158);
                    n=literalNumeric();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1015:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_literal6172);
                    n=literalBoolean();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1016:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_literal6186);
                    n=literalString();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "literal"



    // $ANTLR start "literalNumeric"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1019:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL |l= LONG_LITERAL |f= FLOAT_LITERAL |d= DOUBLE_LITERAL );
    public final Object literalNumeric() throws RecognitionException {
        Object node = null;


        Token i=null;
        Token l=null;
        Token f=null;
        Token d=null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1021:5: (i= INTEGER_LITERAL |l= LONG_LITERAL |f= FLOAT_LITERAL |d= DOUBLE_LITERAL )
            int alt79=4;
            switch ( input.LA(1) ) {
            case INTEGER_LITERAL:
                {
                alt79=1;
                }
                break;
            case LONG_LITERAL:
                {
                alt79=2;
                }
                break;
            case FLOAT_LITERAL:
                {
                alt79=3;
                }
                break;
            case DOUBLE_LITERAL:
                {
                alt79=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 79, 0, input);
                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1021:7: i= INTEGER_LITERAL
                    {
                    i=(Token)match(input,INTEGER_LITERAL,FOLLOW_INTEGER_LITERAL_in_literalNumeric6216); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                node = factory.newIntegerLiteral(i.getLine(), i.getCharPositionInLine(),
                                                                 Integer.valueOf(i.getText()));
                            }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1026:7: l= LONG_LITERAL
                    {
                    l=(Token)match(input,LONG_LITERAL,FOLLOW_LONG_LITERAL_in_literalNumeric6232); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                String text = l.getText();
                                // skip the tailing 'l'
                                text = text.substring(0, text.length() - 1);
                                node = factory.newLongLiteral(l.getLine(), l.getCharPositionInLine(),
                                                              Long.valueOf(text));
                            }
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1034:7: f= FLOAT_LITERAL
                    {
                    f=(Token)match(input,FLOAT_LITERAL,FOLLOW_FLOAT_LITERAL_in_literalNumeric6252); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                node = factory.newFloatLiteral(f.getLine(), f.getCharPositionInLine(),
                                                               Float.valueOf(f.getText()));
                            }
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1039:7: d= DOUBLE_LITERAL
                    {
                    d=(Token)match(input,DOUBLE_LITERAL,FOLLOW_DOUBLE_LITERAL_in_literalNumeric6272); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                node = factory.newDoubleLiteral(d.getLine(), d.getCharPositionInLine(),
                                                                Double.valueOf(d.getText()));
                            }
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "literalNumeric"



    // $ANTLR start "literalBoolean"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1046:1: literalBoolean returns [Object node] : (t= TRUE |f= FALSE );
    public final Object literalBoolean() throws RecognitionException {
        Object node = null;


        Token t=null;
        Token f=null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1048:5: (t= TRUE |f= FALSE )
            int alt80=2;
            int LA80_0 = input.LA(1);
            if ( (LA80_0==TRUE) ) {
                alt80=1;
            }
            else if ( (LA80_0==FALSE) ) {
                alt80=2;
            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);
                throw nvae;
            }

            switch (alt80) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1048:7: t= TRUE
                    {
                    t=(Token)match(input,TRUE,FOLLOW_TRUE_in_literalBoolean6310); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newBooleanLiteral(t.getLine(), t.getCharPositionInLine(), Boolean.TRUE); }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1050:7: f= FALSE
                    {
                    f=(Token)match(input,FALSE,FOLLOW_FALSE_in_literalBoolean6330); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newBooleanLiteral(f.getLine(), f.getCharPositionInLine(), Boolean.FALSE); }
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "literalBoolean"



    // $ANTLR start "literalString"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1054:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED |s= STRING_LITERAL_SINGLE_QUOTED );
    public final Object literalString() throws RecognitionException {
        Object node = null;


        Token d=null;
        Token s=null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1056:5: (d= STRING_LITERAL_DOUBLE_QUOTED |s= STRING_LITERAL_SINGLE_QUOTED )
            int alt81=2;
            int LA81_0 = input.LA(1);
            if ( (LA81_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                alt81=1;
            }
            else if ( (LA81_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                alt81=2;
            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);
                throw nvae;
            }

            switch (alt81) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1056:7: d= STRING_LITERAL_DOUBLE_QUOTED
                    {
                    d=(Token)match(input,STRING_LITERAL_DOUBLE_QUOTED,FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6368); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                node = factory.newStringLiteral(d.getLine(), d.getCharPositionInLine(),
                                                                convertStringLiteral(d.getText()));
                            }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1061:7: s= STRING_LITERAL_SINGLE_QUOTED
                    {
                    s=(Token)match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6388); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                node = factory.newStringLiteral(s.getLine(), s.getCharPositionInLine(),
                                                                convertStringLiteral(s.getText()));
                            }
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "literalString"



    // $ANTLR start "literalTemporal"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1068:1: literalTemporal returns [Object node] : (d= DATE_LITERAL |d= TIME_LITERAL |d= TIMESTAMP_LITERAL );
    public final Object literalTemporal() throws RecognitionException {
        Object node = null;


        Token d=null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1070:5: (d= DATE_LITERAL |d= TIME_LITERAL |d= TIMESTAMP_LITERAL )
            int alt82=3;
            switch ( input.LA(1) ) {
            case DATE_LITERAL:
                {
                alt82=1;
                }
                break;
            case TIME_LITERAL:
                {
                alt82=2;
                }
                break;
            case TIMESTAMP_LITERAL:
                {
                alt82=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 82, 0, input);
                throw nvae;
            }
            switch (alt82) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1070:7: d= DATE_LITERAL
                    {
                    d=(Token)match(input,DATE_LITERAL,FOLLOW_DATE_LITERAL_in_literalTemporal6428); if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = factory.newDateLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1071:7: d= TIME_LITERAL
                    {
                    d=(Token)match(input,TIME_LITERAL,FOLLOW_TIME_LITERAL_in_literalTemporal6442); if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = factory.newTimeLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); }
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1072:7: d= TIMESTAMP_LITERAL
                    {
                    d=(Token)match(input,TIMESTAMP_LITERAL,FOLLOW_TIMESTAMP_LITERAL_in_literalTemporal6456); if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = factory.newTimeStampLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); }
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "literalTemporal"



    // $ANTLR start "inputParameter"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1075:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM |n= NAMED_PARAM );
    public final Object inputParameter() throws RecognitionException {
        Object node = null;


        Token p=null;
        Token n=null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1077:5: (p= POSITIONAL_PARAM |n= NAMED_PARAM )
            int alt83=2;
            int LA83_0 = input.LA(1);
            if ( (LA83_0==POSITIONAL_PARAM) ) {
                alt83=1;
            }
            else if ( (LA83_0==NAMED_PARAM) ) {
                alt83=2;
            }

            else {
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 83, 0, input);
                throw nvae;
            }

            switch (alt83) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1077:7: p= POSITIONAL_PARAM
                    {
                    p=(Token)match(input,POSITIONAL_PARAM,FOLLOW_POSITIONAL_PARAM_in_inputParameter6486); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                // skip the leading ?
                                String text = p.getText().substring(1);
                                node = factory.newPositionalParameter(p.getLine(), p.getCharPositionInLine(), text);
                            }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1083:7: n= NAMED_PARAM
                    {
                    n=(Token)match(input,NAMED_PARAM,FOLLOW_NAMED_PARAM_in_inputParameter6506); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                // skip the leading :
                                String text = n.getText().substring(1);
                                node = factory.newNamedParameter(n.getLine(), n.getCharPositionInLine(), text);
                            }
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "inputParameter"



    // $ANTLR start "functionsReturningNumerics"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1091:1: functionsReturningNumerics returns [Object node] : (n= abs |n= length |n= mod |n= sqrt |n= locate |n= size |n= index |n= func );
    public final Object functionsReturningNumerics() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1093:5: (n= abs |n= length |n= mod |n= sqrt |n= locate |n= size |n= index |n= func )
            int alt84=8;
            switch ( input.LA(1) ) {
            case ABS:
                {
                alt84=1;
                }
                break;
            case LENGTH:
                {
                alt84=2;
                }
                break;
            case MOD:
                {
                alt84=3;
                }
                break;
            case SQRT:
                {
                alt84=4;
                }
                break;
            case LOCATE:
                {
                alt84=5;
                }
                break;
            case SIZE:
                {
                alt84=6;
                }
                break;
            case INDEX:
                {
                alt84=7;
                }
                break;
            case FUNC:
                {
                alt84=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 84, 0, input);
                throw nvae;
            }
            switch (alt84) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1093:7: n= ab
                    {
                    pushFollow(FOLLOW_abs_in_functionsReturningNumerics6546);
                    n=abs();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1094:7: n= length
                    {
                    pushFollow(FOLLOW_length_in_functionsReturningNumerics6560);
                    n=length();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1095:7: n= mod
                    {
                    pushFollow(FOLLOW_mod_in_functionsReturningNumerics6574);
                    n=mod();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1096:7: n= sqrt
                    {
                    pushFollow(FOLLOW_sqrt_in_functionsReturningNumerics6588);
                    n=sqrt();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 5 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1097:7: n= locate
                    {
                    pushFollow(FOLLOW_locate_in_functionsReturningNumerics6602);
                    n=locate();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 6 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1098:7: n= size
                    {
                    pushFollow(FOLLOW_size_in_functionsReturningNumerics6616);
                    n=size();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 7 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1099:7: n= index
                    {
                    pushFollow(FOLLOW_index_in_functionsReturningNumerics6630);
                    n=index();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 8 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1100:7: n= func
                    {
                    pushFollow(FOLLOW_func_in_functionsReturningNumerics6644);
                    n=func();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "functionsReturningNumerics"



    // $ANTLR start "functionsReturningDatetime"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1103:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE |t= CURRENT_TIME |ts= CURRENT_TIMESTAMP );
    public final Object functionsReturningDatetime() throws RecognitionException {
        Object node = null;


        Token d=null;
        Token t=null;
        Token ts=null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1105:5: (d= CURRENT_DATE |t= CURRENT_TIME |ts= CURRENT_TIMESTAMP )
            int alt85=3;
            switch ( input.LA(1) ) {
            case CURRENT_DATE:
                {
                alt85=1;
                }
                break;
            case CURRENT_TIME:
                {
                alt85=2;
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                alt85=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 85, 0, input);
                throw nvae;
            }
            switch (alt85) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1105:7: d= CURRENT_DATE
                    {
                    d=(Token)match(input,CURRENT_DATE,FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6674); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newCurrentDate(d.getLine(), d.getCharPositionInLine()); }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1107:7: t= CURRENT_TIME
                    {
                    t=(Token)match(input,CURRENT_TIME,FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6694); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newCurrentTime(t.getLine(), t.getCharPositionInLine()); }
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1109:7: ts= CURRENT_TIMESTAMP
                    {
                    ts=(Token)match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6714); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newCurrentTimestamp(ts.getLine(), ts.getCharPositionInLine()); }
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "functionsReturningDatetime"



    // $ANTLR start "functionsReturningStrings"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1113:1: functionsReturningStrings returns [Object node] : (n= concat |n= substring |n= trim |n= upper |n= lower );
    public final Object functionsReturningStrings() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1115:5: (n= concat |n= substring |n= trim |n= upper |n= lower )
            int alt86=5;
            switch ( input.LA(1) ) {
            case CONCAT:
                {
                alt86=1;
                }
                break;
            case SUBSTRING:
                {
                alt86=2;
                }
                break;
            case TRIM:
                {
                alt86=3;
                }
                break;
            case UPPER:
                {
                alt86=4;
                }
                break;
            case LOWER:
                {
                alt86=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 86, 0, input);
                throw nvae;
            }
            switch (alt86) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1115:7: n= concat
                    {
                    pushFollow(FOLLOW_concat_in_functionsReturningStrings6754);
                    n=concat();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1116:7: n= substring
                    {
                    pushFollow(FOLLOW_substring_in_functionsReturningStrings6768);
                    n=substring();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1117:7: n= trim
                    {
                    pushFollow(FOLLOW_trim_in_functionsReturningStrings6782);
                    n=trim();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1118:7: n= upper
                    {
                    pushFollow(FOLLOW_upper_in_functionsReturningStrings6796);
                    n=upper();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 5 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1119:7: n= lower
                    {
                    pushFollow(FOLLOW_lower_in_functionsReturningStrings6810);
                    n=lower();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "functionsReturningStrings"


    protected static class concat_scope {
        List items;
    }
    protected Stack<concat_scope> concat_stack = new Stack<concat_scope>();


    // $ANTLR start "concat"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1123:1: concat returns [Object node] : c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET ;
    public final Object concat() throws RecognitionException {
        concat_stack.push(new concat_scope());
        Object node = null;


        Token c=null;
        Object firstArg =null;
        Object arg =null;


            node = null;
            concat_stack.peek().items = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1131:5: (c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1131:7: c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET
            {
            c=(Token)match(input,CONCAT,FOLLOW_CONCAT_in_concat6845); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_concat6855); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_concat6869);
            firstArg=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {concat_stack.peek().items.add(firstArg);}
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1133:75: ( COMMA arg= scalarExpression )+
            int cnt87=0;
            loop87:
            while (true) {
                int alt87=2;
                int LA87_0 = input.LA(1);
                if ( (LA87_0==COMMA) ) {
                    alt87=1;
                }

                switch (alt87) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1133:76: COMMA arg= scalarExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_concat6874); if (state.failed) return node;
                    pushFollow(FOLLOW_scalarExpression_in_concat6880);
                    arg=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {concat_stack.peek().items.add(arg);}
                    }
                    break;

                default :
                    if ( cnt87 >= 1 ) break loop87;
                    if (state.backtracking>0) {state.failed=true; return node;}
                    EarlyExitException eee = new EarlyExitException(87, input);
                    throw eee;
                }
                cnt87++;
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_concat6894); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newConcat(c.getLine(), c.getCharPositionInLine(), concat_stack.peek().items); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            concat_stack.pop();
        }
        return node;
    }
    // $ANTLR end "concat"



    // $ANTLR start "substring"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1138:1: substring returns [Object node] : s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= scalarExpression ( COMMA lengthNode= scalarExpression )? RIGHT_ROUND_BRACKET ;
    public final Object substring() throws RecognitionException {
        Object node = null;


        Token s=null;
        Object string =null;
        Object start =null;
        Object lengthNode =null;


            node = null;
            lengthNode = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1143:5: (s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= scalarExpression ( COMMA lengthNode= scalarExpression )? RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1143:7: s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= scalarExpression ( COMMA lengthNode= scalarExpression )? RIGHT_ROUND_BRACKET
            {
            s=(Token)match(input,SUBSTRING,FOLLOW_SUBSTRING_in_substring6932); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_substring6942); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_substring6956);
            string=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring6958); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_substring6972);
            start=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1147:9: ( COMMA lengthNode= scalarExpression )?
            int alt88=2;
            int LA88_0 = input.LA(1);
            if ( (LA88_0==COMMA) ) {
                alt88=1;
            }
            switch (alt88) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1147:10: COMMA lengthNode= scalarExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_substring6983); if (state.failed) return node;
                    pushFollow(FOLLOW_scalarExpression_in_substring6989);
                    lengthNode=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_substring7001); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        if (lengthNode != null){
                            node = factory.newSubstring(s.getLine(), s.getCharPositionInLine(),
                                                    string, start, lengthNode);
                        } else {
                            node = factory.newSubstring(s.getLine(), s.getCharPositionInLine(),
                                                    string, start, null);
                        }
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "substring"



    // $ANTLR start "trim"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1160:1: trim returns [Object node] : t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object trim() throws RecognitionException {
        Object node = null;


        Token t=null;
        TrimSpecification trimSpecIndicator =null;
        Object trimCharNode =null;
        Object n =null;


            node = null;
            trimSpecIndicator = TrimSpecification.BOTH;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1165:5: (t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1165:7: t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET
            {
            t=(Token)match(input,TRIM,FOLLOW_TRIM_in_trim7039); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_trim7049); if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1167:9: (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )?
            int alt89=2;
            switch ( input.LA(1) ) {
                case BOTH:
                case FROM:
                case LEADING:
                case TRAILING:
                    {
                    alt89=1;
                    }
                    break;
                case STRING_LITERAL_DOUBLE_QUOTED:
                    {
                    int LA89_4 = input.LA(2);
                    if ( (LA89_4==FROM) ) {
                        alt89=1;
                    }
                    }
                    break;
                case STRING_LITERAL_SINGLE_QUOTED:
                    {
                    int LA89_5 = input.LA(2);
                    if ( (LA89_5==FROM) ) {
                        alt89=1;
                    }
                    }
                    break;
                case POSITIONAL_PARAM:
                    {
                    int LA89_6 = input.LA(2);
                    if ( (LA89_6==FROM) ) {
                        alt89=1;
                    }
                    }
                    break;
                case NAMED_PARAM:
                    {
                    int LA89_7 = input.LA(2);
                    if ( (LA89_7==FROM) ) {
                        alt89=1;
                    }
                    }
                    break;
            }
            switch (alt89) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1167:10: trimSpecIndicator= trimSpec trimCharNode= trimChar FROM
                    {
                    pushFollow(FOLLOW_trimSpec_in_trim7064);
                    trimSpecIndicator=trimSpec();
                    state._fsp--;
                    if (state.failed) return node;
                    pushFollow(FOLLOW_trimChar_in_trim7070);
                    trimCharNode=trimChar();
                    state._fsp--;
                    if (state.failed) return node;
                    match(input,FROM,FOLLOW_FROM_in_trim7072); if (state.failed) return node;
                    }
                    break;

            }

            pushFollow(FOLLOW_stringPrimary_in_trim7088);
            n=stringPrimary();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_trim7098); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        node = factory.newTrim(t.getLine(), t.getCharPositionInLine(),
                                               trimSpecIndicator, trimCharNode, n);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "trim"



    // $ANTLR start "trimSpec"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1176:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH |);
    public final TrimSpecification trimSpec() throws RecognitionException {
        TrimSpecification trimSpec = null;


         trimSpec = TrimSpecification.BOTH;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1178:5: ( LEADING | TRAILING | BOTH |)
            int alt90=4;
            switch ( input.LA(1) ) {
            case LEADING:
                {
                alt90=1;
                }
                break;
            case TRAILING:
                {
                alt90=2;
                }
                break;
            case BOTH:
                {
                alt90=3;
                }
                break;
            case FROM:
            case NAMED_PARAM:
            case POSITIONAL_PARAM:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt90=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return trimSpec;}
                NoViableAltException nvae =
                    new NoViableAltException("", 90, 0, input);
                throw nvae;
            }
            switch (alt90) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1178:7: LEADING
                    {
                    match(input,LEADING,FOLLOW_LEADING_in_trimSpec7134); if (state.failed) return trimSpec;
                    if ( state.backtracking==0 ) { trimSpec = TrimSpecification.LEADING; }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1180:7: TRAILING
                    {
                    match(input,TRAILING,FOLLOW_TRAILING_in_trimSpec7152); if (state.failed) return trimSpec;
                    if ( state.backtracking==0 ) { trimSpec = TrimSpecification.TRAILING; }
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1182:7: BOTH
                    {
                    match(input,BOTH,FOLLOW_BOTH_in_trimSpec7170); if (state.failed) return trimSpec;
                    if ( state.backtracking==0 ) { trimSpec = TrimSpecification.BOTH; }
                    }
                    break;
                case 4 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1185:5:
                    {
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return trimSpec;
    }
    // $ANTLR end "trimSpec"



    // $ANTLR start "trimChar"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1188:1: trimChar returns [Object node] : (n= literalString |n= inputParameter |);
    public final Object trimChar() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1190:5: (n= literalString |n= inputParameter |)
            int alt91=3;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt91=1;
                }
                break;
            case NAMED_PARAM:
            case POSITIONAL_PARAM:
                {
                alt91=2;
                }
                break;
            case FROM:
                {
                alt91=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 91, 0, input);
                throw nvae;
            }
            switch (alt91) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1190:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_trimChar7218);
                    n=literalString();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1191:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_trimChar7232);
                    n=inputParameter();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1193:5:
                    {
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "trimChar"



    // $ANTLR start "upper"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1195:1: upper returns [Object node] : u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object upper() throws RecognitionException {
        Object node = null;


        Token u=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1197:5: (u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1197:7: u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            u=(Token)match(input,UPPER,FOLLOW_UPPER_in_upper7269); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_upper7271); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_upper7277);
            n=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_upper7279); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newUpper(u.getLine(), u.getCharPositionInLine(), n); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "upper"



    // $ANTLR start "lower"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1201:1: lower returns [Object node] : l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object lower() throws RecognitionException {
        Object node = null;


        Token l=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1203:5: (l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1203:7: l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            l=(Token)match(input,LOWER,FOLLOW_LOWER_in_lower7317); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_lower7319); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_lower7325);
            n=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_lower7327); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newLower(l.getLine(), l.getCharPositionInLine(), n); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "lower"



    // $ANTLR start "abs"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1208:1: abs returns [Object node] : a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object abs() throws RecognitionException {
        Object node = null;


        Token a=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1210:5: (a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1210:7: a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            a=(Token)match(input,ABS,FOLLOW_ABS_in_abs7366); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_abs7368); if (state.failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_abs7374);
            n=simpleArithmeticExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_abs7376); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newAbs(a.getLine(), a.getCharPositionInLine(), n); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "abs"



    // $ANTLR start "length"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1214:1: length returns [Object node] : l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object length() throws RecognitionException {
        Object node = null;


        Token l=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1216:5: (l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1216:7: l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            l=(Token)match(input,LENGTH,FOLLOW_LENGTH_in_length7414); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_length7416); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_length7422);
            n=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_length7424); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newLength(l.getLine(), l.getCharPositionInLine(), n); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "length"



    // $ANTLR start "locate"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1220:1: locate returns [Object node] : l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= scalarExpression )? RIGHT_ROUND_BRACKET ;
    public final Object locate() throws RecognitionException {
        Object node = null;


        Token l=null;
        Object pattern =null;
        Object n =null;
        Object startPos =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1224:5: (l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= scalarExpression )? RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1224:7: l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= scalarExpression )? RIGHT_ROUND_BRACKET
            {
            l=(Token)match(input,LOCATE,FOLLOW_LOCATE_in_locate7462); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_locate7472); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_locate7486);
            pattern=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_locate7488); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_locate7494);
            n=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1227:9: ( COMMA startPos= scalarExpression )?
            int alt92=2;
            int LA92_0 = input.LA(1);
            if ( (LA92_0==COMMA) ) {
                alt92=1;
            }
            switch (alt92) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1227:11: COMMA startPos= scalarExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_locate7506); if (state.failed) return node;
                    pushFollow(FOLLOW_scalarExpression_in_locate7512);
                    startPos=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_locate7525); if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        node = factory.newLocate(l.getLine(), l.getCharPositionInLine(),
                                                 pattern, n, startPos);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "locate"



    // $ANTLR start "size"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1235:1: size returns [Object node] : s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ;
    public final Object size() throws RecognitionException {
        Object node = null;


        Token s=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1237:5: (s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1237:7: s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)match(input,SIZE,FOLLOW_SIZE_in_size7563); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_size7573); if (state.failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_size7579);
            n=collectionValuedPathExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_size7581); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newSize(s.getLine(), s.getCharPositionInLine(), n);}
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "size"



    // $ANTLR start "mod"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1242:1: mod returns [Object node] : m= MOD LEFT_ROUND_BRACKET left= scalarExpression COMMA right= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object mod() throws RecognitionException {
        Object node = null;


        Token m=null;
        Object left =null;
        Object right =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1246:5: (m= MOD LEFT_ROUND_BRACKET left= scalarExpression COMMA right= scalarExpression RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1246:7: m= MOD LEFT_ROUND_BRACKET left= scalarExpression COMMA right= scalarExpression RIGHT_ROUND_BRACKET
            {
            m=(Token)match(input,MOD,FOLLOW_MOD_in_mod7619); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mod7621); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_mod7635);
            left=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_mod7637); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_mod7651);
            right=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mod7661); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newMod(m.getLine(), m.getCharPositionInLine(), left, right); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "mod"



    // $ANTLR start "sqrt"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1253:1: sqrt returns [Object node] : s= SQRT LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object sqrt() throws RecognitionException {
        Object node = null;


        Token s=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1255:5: (s= SQRT LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1255:7: s= SQRT LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)match(input,SQRT,FOLLOW_SQRT_in_sqrt7699); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7709); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_sqrt7715);
            n=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7717); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newSqrt(s.getLine(), s.getCharPositionInLine(), n); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "sqrt"



    // $ANTLR start "index"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1260:1: index returns [Object node] : s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object index() throws RecognitionException {
        Object node = null;


        Token s=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1262:5: (s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1262:7: s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            s=(Token)match(input,INDEX,FOLLOW_INDEX_in_index7755); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_index7757); if (state.failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_index7763);
            n=variableAccessOrTypeConstant();
            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_index7765); if (state.failed) return node;
            if ( state.backtracking==0 ) { node = factory.newIndex(s.getLine(), s.getCharPositionInLine(), n); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "index"


    protected static class func_scope {
        List exprs;
    }
    protected Stack<func_scope> func_stack = new Stack<func_scope>();


    // $ANTLR start "func"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1267:1: func returns [Object node] : f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET ;
    public final Object func() throws RecognitionException {
        func_stack.push(new func_scope());
        Object node = null;


        Token f=null;
        Token name=null;
        Object n =null;


            node = null;
            func_stack.peek().exprs = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1275:5: (f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1275:7: f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET
            {
            f=(Token)match(input,FUNC,FOLLOW_FUNC_in_func7807); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_func7809); if (state.failed) return node;
            name=(Token)match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_func7821); if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1277:7: ( COMMA n= newValue )*
            loop93:
            while (true) {
                int alt93=2;
                int LA93_0 = input.LA(1);
                if ( (LA93_0==COMMA) ) {
                    alt93=1;
                }

                switch (alt93) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1277:8: COMMA n= newValue
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_func7830); if (state.failed) return node;
                    pushFollow(FOLLOW_newValue_in_func7836);
                    n=newValue();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                                func_stack.peek().exprs.add(n);
                              }
                    }
                    break;

                default :
                    break loop93;
                }
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_func7868); if (state.failed) return node;
            if ( state.backtracking==0 ) {node = factory.newFunc(f.getLine(), f.getCharPositionInLine(), name.getText(), func_stack.peek().exprs);}
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            func_stack.pop();
        }
        return node;
    }
    // $ANTLR end "func"



    // $ANTLR start "subquery"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1286:1: subquery returns [Object node] : select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? ;
    public final Object subquery() throws RecognitionException {
        Object node = null;


        Object select =null;
        Object from =null;
        Object where =null;
        Object groupBy =null;
        Object having =null;


            node = null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1290:5: (select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1290:7: select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )?
            {
            pushFollow(FOLLOW_simpleSelectClause_in_subquery7906);
            select=simpleSelectClause();
            state._fsp--;
            if (state.failed) return node;
            pushFollow(FOLLOW_subqueryFromClause_in_subquery7921);
            from=subqueryFromClause();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1292:7: (where= whereClause )?
            int alt94=2;
            int LA94_0 = input.LA(1);
            if ( (LA94_0==WHERE) ) {
                alt94=1;
            }
            switch (alt94) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1292:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_subquery7936);
                    where=whereClause();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1293:7: (groupBy= groupByClause )?
            int alt95=2;
            int LA95_0 = input.LA(1);
            if ( (LA95_0==GROUP) ) {
                alt95=1;
            }
            switch (alt95) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1293:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_subquery7951);
                    groupBy=groupByClause();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1294:7: (having= havingClause )?
            int alt96=2;
            int LA96_0 = input.LA(1);
            if ( (LA96_0==HAVING) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1294:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_subquery7967);
                    having=havingClause();
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;

            }

            if ( state.backtracking==0 ) {
                        node = factory.newSubquery(0, 0, select, from,
                                                   where, groupBy, having);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "subquery"


    protected static class simpleSelectClause_scope {
        boolean distinct;
    }
    protected Stack<simpleSelectClause_scope> simpleSelectClause_stack = new Stack<simpleSelectClause_scope>();


    // $ANTLR start "simpleSelectClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1301:1: simpleSelectClause returns [Object node] : s= SELECT ( DISTINCT )? n= simpleSelectExpression ;
    public final Object simpleSelectClause() throws RecognitionException {
        simpleSelectClause_stack.push(new simpleSelectClause_scope());
        Object node = null;


        Token s=null;
        Object n =null;


            node = null;
            simpleSelectClause_stack.peek().distinct = false;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1309:5: (s= SELECT ( DISTINCT )? n= simpleSelectExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1309:7: s= SELECT ( DISTINCT )? n= simpleSelectExpression
            {
            s=(Token)match(input,SELECT,FOLLOW_SELECT_in_simpleSelectClause8010); if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1309:16: ( DISTINCT )?
            int alt97=2;
            int LA97_0 = input.LA(1);
            if ( (LA97_0==DISTINCT) ) {
                alt97=1;
            }
            switch (alt97) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1309:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_simpleSelectClause8013); if (state.failed) return node;
                    if ( state.backtracking==0 ) { simpleSelectClause_stack.peek().distinct = true; }
                    }
                    break;

            }

            pushFollow(FOLLOW_simpleSelectExpression_in_simpleSelectClause8029);
            n=simpleSelectExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        List exprs = new ArrayList();
                        exprs.add(n);
                        node = factory.newSelectClause(s.getLine(), s.getCharPositionInLine(),
                                                       simpleSelectClause_stack.peek().distinct, exprs);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            simpleSelectClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "simpleSelectClause"



    // $ANTLR start "simpleSelectExpression"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1319:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression |n= aggregateExpression |n= variableAccessOrTypeConstant );
    public final Object simpleSelectExpression() throws RecognitionException {
        Object node = null;


        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1321:5: (n= singleValuedPathExpression |n= aggregateExpression |n= variableAccessOrTypeConstant )
            int alt98=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA98_1 = input.LA(2);
                if ( (LA98_1==DOT) ) {
                    alt98=1;
                }
                else if ( (LA98_1==FROM) ) {
                    alt98=3;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 98, 1, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

                }
                break;
            case KEY:
            case VALUE:
                {
                alt98=1;
                }
                break;
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt98=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 98, 0, input);
                throw nvae;
            }
            switch (alt98) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1321:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_simpleSelectExpression8069);
                    n=singleValuedPathExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1322:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_simpleSelectExpression8084);
                    n=aggregateExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1323:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression8099);
                    n=variableAccessOrTypeConstant();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {node = n;}
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "simpleSelectExpression"


    protected static class subqueryFromClause_scope {
        List varDecls;
    }
    protected Stack<subqueryFromClause_scope> subqueryFromClause_stack = new Stack<subqueryFromClause_scope>();


    // $ANTLR start "subqueryFromClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1327:1: subqueryFromClause returns [Object node] : f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] |c= collectionMemberDeclaration )* ;
    public final Object subqueryFromClause() throws RecognitionException {
        subqueryFromClause_stack.push(new subqueryFromClause_scope());
        Object node = null;


        Token f=null;
        Object c =null;


            node = null;
            subqueryFromClause_stack.peek().varDecls = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1335:5: (f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] |c= collectionMemberDeclaration )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1335:7: f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] |c= collectionMemberDeclaration )*
            {
            f=(Token)match(input,FROM,FOLLOW_FROM_in_subqueryFromClause8134); if (state.failed) return node;
            pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8136);
            subselectIdentificationVariableDeclaration(subqueryFromClause_stack.peek().varDecls);
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1336:9: ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] |c= collectionMemberDeclaration )*
            loop99:
            while (true) {
                int alt99=3;
                int LA99_0 = input.LA(1);
                if ( (LA99_0==COMMA) ) {
                    alt99=1;
                }
                else if ( (LA99_0==IN) ) {
                    alt99=2;
                }

                switch (alt99) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1337:13: COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls]
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_subqueryFromClause8161); if (state.failed) return node;
                    pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8179);
                    subselectIdentificationVariableDeclaration(subqueryFromClause_stack.peek().varDecls);
                    state._fsp--;
                    if (state.failed) return node;
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1339:19: c= collectionMemberDeclaration
                    {
                    pushFollow(FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8204);
                    c=collectionMemberDeclaration();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {subqueryFromClause_stack.peek().varDecls.add(c);}
                    }
                    break;

                default :
                    break loop99;
                }
            }

            if ( state.backtracking==0 ) { node = factory.newFromClause(f.getLine(), f.getCharPositionInLine(), subqueryFromClause_stack.peek().varDecls); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            subqueryFromClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "subqueryFromClause"



    // $ANTLR start "subselectIdentificationVariableDeclaration"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1344:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] |n= associationPathExpression ( AS )? i= IDENT (node= join )* |n= collectionMemberDeclaration );
    public final void subselectIdentificationVariableDeclaration(List varDecls) throws RecognitionException {
        Token i=null;
        Object n =null;
        Object node =null;

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1345:5: ( identificationVariableDeclaration[varDecls] |n= associationPathExpression ( AS )? i= IDENT (node= join )* |n= collectionMemberDeclaration )
            int alt102=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA102_1 = input.LA(2);
                if ( (LA102_1==AS||LA102_1==IDENT) ) {
                    alt102=1;
                }
                else if ( (LA102_1==DOT) ) {
                    alt102=2;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 102, 1, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

                }
                break;
            case KEY:
                {
                int LA102_2 = input.LA(2);
                if ( (LA102_2==LEFT_ROUND_BRACKET) ) {
                    alt102=2;
                }
                else if ( (LA102_2==AS||LA102_2==IDENT) ) {
                    alt102=1;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 102, 2, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

                }
                break;
            case VALUE:
                {
                int LA102_3 = input.LA(2);
                if ( (LA102_3==LEFT_ROUND_BRACKET) ) {
                    alt102=2;
                }
                else if ( (LA102_3==AS||LA102_3==IDENT) ) {
                    alt102=1;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 102, 3, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

                }
                break;
            case IN:
                {
                int LA102_4 = input.LA(2);
                if ( (LA102_4==LEFT_ROUND_BRACKET) ) {
                    alt102=3;
                }
                else if ( (LA102_4==AS||LA102_4==IDENT) ) {
                    alt102=1;
                }

                else {
                    if (state.backtracking>0) {state.failed=true; return;}
                    int nvaeMark = input.mark();
                    try {
                        input.consume();
                        NoViableAltException nvae =
                            new NoViableAltException("", 102, 4, input);
                        throw nvae;
                    } finally {
                        input.rewind(nvaeMark);
                    }
                }

                }
                break;
            case ABS:
            case ALL:
            case AND:
            case ANY:
            case AS:
            case ASC:
            case AVG:
            case BETWEEN:
            case BOTH:
            case BY:
            case CASE:
            case COALESCE:
            case COMMA:
            case CONCAT:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DATE_LITERAL:
            case DATE_STRING:
            case DELETE:
            case DESC:
            case DISTINCT:
            case DIVIDE:
            case DOT:
            case DOUBLE_LITERAL:
            case DOUBLE_SUFFIX:
            case ELSE:
            case EMPTY:
            case END:
            case ENTRY:
            case EQUALS:
            case ESCAPE:
            case EXISTS:
            case EXPONENT:
            case FALSE:
            case FETCH:
            case FLOAT_LITERAL:
            case FLOAT_SUFFIX:
            case FROM:
            case FUNC:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL_TO:
            case GROUP:
            case HAVING:
            case HEX_DIGIT:
            case HEX_LITERAL:
            case INDEX:
            case INNER:
            case INTEGER_LITERAL:
            case INTEGER_SUFFIX:
            case IS:
            case JOIN:
            case LEADING:
            case LEFT:
            case LEFT_CURLY_BRACKET:
            case LEFT_ROUND_BRACKET:
            case LENGTH:
            case LESS_THAN:
            case LESS_THAN_EQUAL_TO:
            case LIKE:
            case LOCATE:
            case LONG_LITERAL:
            case LOWER:
            case MAX:
            case MEMBER:
            case MIN:
            case MINUS:
            case MOD:
            case MULTIPLY:
            case NAMED_PARAM:
            case NEW:
            case NOT:
            case NOT_EQUAL_TO:
            case NULL:
            case NULLIF:
            case NUMERIC_DIGITS:
            case OBJECT:
            case OCTAL_LITERAL:
            case OF:
            case OR:
            case ORDER:
            case OUTER:
            case PLUS:
            case POSITIONAL_PARAM:
            case RIGHT_CURLY_BRACKET:
            case RIGHT_ROUND_BRACKET:
            case SELECT:
            case SET:
            case SIZE:
            case SOME:
            case SQRT:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case SUBSTRING:
            case SUM:
            case TEXTCHAR:
            case THEN:
            case TIMESTAMP_LITERAL:
            case TIME_LITERAL:
            case TIME_STRING:
            case TRAILING:
            case TREAT:
            case TRIM:
            case TRUE:
            case TYPE:
            case UNKNOWN:
            case UPDATE:
            case UPPER:
            case WHEN:
            case WHERE:
            case WS:
                {
                alt102=1;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return;}
                NoViableAltException nvae =
                    new NoViableAltException("", 102, 0, input);
                throw nvae;
            }
            switch (alt102) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1345:7: identificationVariableDeclaration[varDecls]
                    {
                    pushFollow(FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8246);
                    identificationVariableDeclaration(varDecls);
                    state._fsp--;
                    if (state.failed) return;
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1346:7: n= associationPathExpression ( AS )? i= IDENT (node= join )*
                    {
                    pushFollow(FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8259);
                    n=associationPathExpression();
                    state._fsp--;
                    if (state.failed) return;
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1346:37: ( AS )?
                    int alt100=2;
                    int LA100_0 = input.LA(1);
                    if ( (LA100_0==AS) ) {
                        alt100=1;
                    }
                    switch (alt100) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1346:38: AS
                            {
                            match(input,AS,FOLLOW_AS_in_subselectIdentificationVariableDeclaration8262); if (state.failed) return;
                            }
                            break;

                    }

                    i=(Token)match(input,IDENT,FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8268); if (state.failed) return;
                    if ( state.backtracking==0 ) {
                                varDecls.add(factory.newVariableDecl(i.getLine(), i.getCharPositionInLine(),
                                                                     n, i.getText()));
                            }
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1351:9: (node= join )*
                    loop101:
                    while (true) {
                        int alt101=2;
                        int LA101_0 = input.LA(1);
                        if ( (LA101_0==INNER||LA101_0==JOIN||LA101_0==LEFT) ) {
                            alt101=1;
                        }

                        switch (alt101) {
                        case 1 :
                            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1351:11: node= join
                            {
                            pushFollow(FOLLOW_join_in_subselectIdentificationVariableDeclaration8294);
                            node=join();
                            state._fsp--;
                            if (state.failed) return;
                            if ( state.backtracking==0 ) { varDecls.add(node); }
                            }
                            break;

                        default :
                            break loop101;
                        }
                    }

                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1352:7: n= collectionMemberDeclaration
                    {
                    pushFollow(FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8311);
                    n=collectionMemberDeclaration();
                    state._fsp--;
                    if (state.failed) return;
                    if ( state.backtracking==0 ) { varDecls.add(n); }
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "subselectIdentificationVariableDeclaration"


    protected static class orderByClause_scope {
        List items;
    }
    protected Stack<orderByClause_scope> orderByClause_stack = new Stack<orderByClause_scope>();


    // $ANTLR start "orderByClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1355:1: orderByClause returns [Object node] : o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* ;
    public final Object orderByClause() throws RecognitionException {
        orderByClause_stack.push(new orderByClause_scope());
        Object node = null;


        Token o=null;
        Object n =null;


            node = null;
            orderByClause_stack.peek().items = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1363:5: (o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1363:7: o= ORDER BY n= orderByItem ( COMMA n= orderByItem )*
            {
            o=(Token)match(input,ORDER,FOLLOW_ORDER_in_orderByClause8344); if (state.failed) return node;
            match(input,BY,FOLLOW_BY_in_orderByClause8346); if (state.failed) return node;
            if ( state.backtracking==0 ) { setAggregatesAllowed(true); }
            pushFollow(FOLLOW_orderByItem_in_orderByClause8362);
            n=orderByItem();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) { orderByClause_stack.peek().items.add(n); }
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1365:9: ( COMMA n= orderByItem )*
            loop103:
            while (true) {
                int alt103=2;
                int LA103_0 = input.LA(1);
                if ( (LA103_0==COMMA) ) {
                    alt103=1;
                }

                switch (alt103) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1365:10: COMMA n= orderByItem
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_orderByClause8376); if (state.failed) return node;
                    pushFollow(FOLLOW_orderByItem_in_orderByClause8382);
                    n=orderByItem();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { orderByClause_stack.peek().items.add(n); }
                    }
                    break;

                default :
                    break loop103;
                }
            }

            if ( state.backtracking==0 ) {
                        setAggregatesAllowed(false);
                        node = factory.newOrderByClause(o.getLine(), o.getCharPositionInLine(), orderByClause_stack.peek().items);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            orderByClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "orderByClause"



    // $ANTLR start "orderByItem"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1372:1: orderByItem returns [Object node] : n= scalarExpression (a= ASC |d= DESC |) ;
    public final Object orderByItem() throws RecognitionException {
        Object node = null;


        Token a=null;
        Token d=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1374:5: (n= scalarExpression (a= ASC |d= DESC |) )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1374:7: n= scalarExpression (a= ASC |d= DESC |)
            {
            pushFollow(FOLLOW_scalarExpression_in_orderByItem8427);
            n=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1375:9: (a= ASC |d= DESC |)
            int alt104=3;
            switch ( input.LA(1) ) {
            case ASC:
                {
                alt104=1;
                }
                break;
            case DESC:
                {
                alt104=2;
                }
                break;
            case EOF:
            case COMMA:
                {
                alt104=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("", 104, 0, input);
                throw nvae;
            }
            switch (alt104) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1375:11: a= ASC
                    {
                    a=(Token)match(input,ASC,FOLLOW_ASC_in_orderByItem8441); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), n); }
                    }
                    break;
                case 2 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1377:11: d= DESC
                    {
                    d=(Token)match(input,DESC,FOLLOW_DESC_in_orderByItem8469); if (state.failed) return node;
                    if ( state.backtracking==0 ) { node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), n); }
                    }
                    break;
                case 3 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1380:13:
                    {
                    if ( state.backtracking==0 ) { node = factory.newAscOrdering(0, 0, n); }
                    }
                    break;

            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "orderByItem"


    protected static class groupByClause_scope {
        List items;
    }
    protected Stack<groupByClause_scope> groupByClause_stack = new Stack<groupByClause_scope>();


    // $ANTLR start "groupByClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1384:1: groupByClause returns [Object node] : g= GROUP BY n= scalarExpression ( COMMA n= scalarExpression )* ;
    public final Object groupByClause() throws RecognitionException {
        groupByClause_stack.push(new groupByClause_scope());
        Object node = null;


        Token g=null;
        Object n =null;


            node = null;
            groupByClause_stack.peek().items = new ArrayList();

        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1392:5: (g= GROUP BY n= scalarExpression ( COMMA n= scalarExpression )* )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1392:7: g= GROUP BY n= scalarExpression ( COMMA n= scalarExpression )*
            {
            g=(Token)match(input,GROUP,FOLLOW_GROUP_in_groupByClause8550); if (state.failed) return node;
            match(input,BY,FOLLOW_BY_in_groupByClause8552); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_groupByClause8566);
            n=scalarExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) { groupByClause_stack.peek().items.add(n); }
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1394:9: ( COMMA n= scalarExpression )*
            loop105:
            while (true) {
                int alt105=2;
                int LA105_0 = input.LA(1);
                if ( (LA105_0==COMMA) ) {
                    alt105=1;
                }

                switch (alt105) {
                case 1 :
                    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1394:10: COMMA n= scalarExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_groupByClause8579); if (state.failed) return node;
                    pushFollow(FOLLOW_scalarExpression_in_groupByClause8585);
                    n=scalarExpression();
                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) { groupByClause_stack.peek().items.add(n); }
                    }
                    break;

                default :
                    break loop105;
                }
            }

            if ( state.backtracking==0 ) { node = factory.newGroupByClause(g.getLine(), g.getCharPositionInLine(), groupByClause_stack.peek().items); }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
            groupByClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "groupByClause"



    // $ANTLR start "havingClause"
    // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1399:1: havingClause returns [Object node] : h= HAVING n= conditionalExpression ;
    public final Object havingClause() throws RecognitionException {
        Object node = null;


        Token h=null;
        Object n =null;

         node = null;
        try {
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1401:5: (h= HAVING n= conditionalExpression )
            // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1401:7: h= HAVING n= conditionalExpression
            {
            h=(Token)match(input,HAVING,FOLLOW_HAVING_in_havingClause8630); if (state.failed) return node;
            if ( state.backtracking==0 ) { setAggregatesAllowed(true); }
            pushFollow(FOLLOW_conditionalExpression_in_havingClause8646);
            n=conditionalExpression();
            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
                        setAggregatesAllowed(false);
                        node = factory.newHavingClause(h.getLine(), h.getCharPositionInLine(), n);
                    }
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            // do for sure before leaving
        }
        return node;
    }
    // $ANTLR end "havingClause"

    // $ANTLR start synpred1_JPQL
    public final void synpred1_JPQL_fragment() throws RecognitionException {
        // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:665:7: ( LEFT_ROUND_BRACKET conditionalExpression )
        // org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:665:8: LEFT_ROUND_BRACKET conditionalExpression
        {
        match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_synpred1_JPQL3545); if (state.failed) return;
        pushFollow(FOLLOW_conditionalExpression_in_synpred1_JPQL3547);
        conditionalExpression();
        state._fsp--;
        if (state.failed) return;
        }

    }
    // $ANTLR end synpred1_JPQL

    // Delegated rule

    public final boolean synpred1_JPQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_JPQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA19 dfa19 = new DFA19(this);
    protected DFA30 dfa30 = new DFA30(this);
    protected DFA47 dfa47 = new DFA47(this);
    static final String DFA19_eotS =
        "\u0117\uffff";
    static final String DFA19_eofS =
        "\u0117\uffff";
    static final String DFA19_minS =
        "\1\4\5\77\52\uffff\5\4\u00e1\0\1\uffff";
    static final String DFA19_maxS =
        "\1\164\5\77\52\uffff\5\164\u00e1\0\1\uffff";
    static final String DFA19_acceptS =
        "\6\uffff\1\2\46\uffff\1\3\1\4\1\5\u00e6\uffff\1\1";
    static final String DFA19_specialS =
        "\65\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
        "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33"+
        "\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47\1\50\1\51"+
        "\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\63\1\64\1\65\1\66\1\67"+
        "\1\70\1\71\1\72\1\73\1\74\1\75\1\76\1\77\1\100\1\101\1\102\1\103\1\104"+
        "\1\105\1\106\1\107\1\110\1\111\1\112\1\113\1\114\1\115\1\116\1\117\1\120"+
        "\1\121\1\122\1\123\1\124\1\125\1\126\1\127\1\130\1\131\1\132\1\133\1\134"+
        "\1\135\1\136\1\137\1\140\1\141\1\142\1\143\1\144\1\145\1\146\1\147\1\150"+
        "\1\151\1\152\1\153\1\154\1\155\1\156\1\157\1\160\1\161\1\162\1\163\1\164"+
        "\1\165\1\166\1\167\1\170\1\171\1\172\1\173\1\174\1\175\1\176\1\177\1\u0080"+
        "\1\u0081\1\u0082\1\u0083\1\u0084\1\u0085\1\u0086\1\u0087\1\u0088\1\u0089"+
        "\1\u008a\1\u008b\1\u008c\1\u008d\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092"+
        "\1\u0093\1\u0094\1\u0095\1\u0096\1\u0097\1\u0098\1\u0099\1\u009a\1\u009b"+
        "\1\u009c\1\u009d\1\u009e\1\u009f\1\u00a0\1\u00a1\1\u00a2\1\u00a3\1\u00a4"+
        "\1\u00a5\1\u00a6\1\u00a7\1\u00a8\1\u00a9\1\u00aa\1\u00ab\1\u00ac\1\u00ad"+
        "\1\u00ae\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00b4\1\u00b5\1\u00b6"+
        "\1\u00b7\1\u00b8\1\u00b9\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00be\1\u00bf"+
        "\1\u00c0\1\u00c1\1\u00c2\1\u00c3\1\u00c4\1\u00c5\1\u00c6\1\u00c7\1\u00c8"+
        "\1\u00c9\1\u00ca\1\u00cb\1\u00cc\1\u00cd\1\u00ce\1\u00cf\1\u00d0\1\u00d1"+
        "\1\u00d2\1\u00d3\1\u00d4\1\u00d5\1\u00d6\1\u00d7\1\u00d8\1\u00d9\1\u00da"+
        "\1\u00db\1\u00dc\1\u00dd\1\u00de\1\u00df\1\u00e0\1\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\6\5\uffff\1\1\3\uffff\2\6\1\uffff\1\6\1\5\4\6\6\uffff\1\6\4\uffff"+
            "\1\57\4\uffff\1\6\1\uffff\1\6\2\uffff\1\6\6\uffff\1\6\1\uffff\1\6\1\uffff"+
            "\1\6\3\uffff\1\6\3\uffff\2\6\3\uffff\3\6\1\2\1\uffff\1\3\2\6\1\uffff"+
            "\1\6\1\56\3\uffff\1\6\1\uffff\1\55\5\uffff\2\6\4\uffff\1\6\1\uffff\4"+
            "\6\1\4\2\uffff\2\6\3\uffff\3\6\2\uffff\2\6",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\105\5\uffff\1\70\3\uffff\1\102\1\103\1\uffff\1\125\1\74\1\122\1\123"+
            "\1\124\1\136\3\uffff\1\65\2\uffff\1\121\11\uffff\1\135\1\uffff\1\120"+
            "\2\uffff\1\114\6\uffff\1\75\1\uffff\1\113\1\uffff\1\116\3\uffff\1\76"+
            "\3\uffff\1\115\1\106\3\uffff\1\111\1\117\1\131\1\71\1\uffff\1\72\1\67"+
            "\1\107\1\uffff\1\101\4\uffff\1\104\7\uffff\1\66\1\100\4\uffff\1\112\1"+
            "\uffff\1\110\1\132\1\133\1\126\1\73\2\uffff\1\140\1\137\3\uffff\1\127"+
            "\1\134\1\141\2\uffff\1\130\1\77",
            "\1\162\5\uffff\1\145\3\uffff\1\157\1\160\1\uffff\1\u0082\1\151\1\177"+
            "\1\u0080\1\u0081\1\u008b\3\uffff\1\142\2\uffff\1\176\11\uffff\1\u008a"+
            "\1\uffff\1\175\2\uffff\1\171\6\uffff\1\152\1\uffff\1\170\1\uffff\1\173"+
            "\3\uffff\1\153\3\uffff\1\172\1\163\3\uffff\1\166\1\174\1\u0086\1\146"+
            "\1\uffff\1\147\1\144\1\164\1\uffff\1\156\4\uffff\1\161\7\uffff\1\143"+
            "\1\155\4\uffff\1\167\1\uffff\1\165\1\u0087\1\u0088\1\u0083\1\150\2\uffff"+
            "\1\u008d\1\u008c\3\uffff\1\u0084\1\u0089\1\u008e\2\uffff\1\u0085\1\154",
            "\1\u009f\5\uffff\1\u0092\3\uffff\1\u009c\1\u009d\1\uffff\1\u00af\1\u0096"+
            "\1\u00ac\1\u00ad\1\u00ae\1\u00b8\3\uffff\1\u008f\2\uffff\1\u00ab\11\uffff"+
            "\1\u00b7\1\uffff\1\u00aa\2\uffff\1\u00a6\6\uffff\1\u0097\1\uffff\1\u00a5"+
            "\1\uffff\1\u00a8\3\uffff\1\u0098\3\uffff\1\u00a7\1\u00a0\3\uffff\1\u00a3"+
            "\1\u00a9\1\u00b3\1\u0093\1\uffff\1\u0094\1\u0091\1\u00a1\1\uffff\1\u009b"+
            "\4\uffff\1\u009e\7\uffff\1\u0090\1\u009a\4\uffff\1\u00a4\1\uffff\1\u00a2"+
            "\1\u00b4\1\u00b5\1\u00b0\1\u0095\2\uffff\1\u00ba\1\u00b9\3\uffff\1\u00b1"+
            "\1\u00b6\1\u00bb\2\uffff\1\u00b2\1\u0099",
            "\1\u00cc\5\uffff\1\u00bf\3\uffff\1\u00c9\1\u00ca\1\uffff\1\u00dc\1\u00c3"+
            "\1\u00d9\1\u00da\1\u00db\1\u00e5\3\uffff\1\u00bc\2\uffff\1\u00d8\11\uffff"+
            "\1\u00e4\1\uffff\1\u00d7\2\uffff\1\u00d3\6\uffff\1\u00c4\1\uffff\1\u00d2"+
            "\1\uffff\1\u00d5\3\uffff\1\u00c5\3\uffff\1\u00d4\1\u00cd\3\uffff\1\u00d0"+
            "\1\u00d6\1\u00e0\1\u00c0\1\uffff\1\u00c1\1\u00be\1\u00ce\1\uffff\1\u00c8"+
            "\4\uffff\1\u00cb\7\uffff\1\u00bd\1\u00c7\4\uffff\1\u00d1\1\uffff\1\u00cf"+
            "\1\u00e1\1\u00e2\1\u00dd\1\u00c2\2\uffff\1\u00e7\1\u00e6\3\uffff\1\u00de"+
            "\1\u00e3\1\u00e8\2\uffff\1\u00df\1\u00c6",
            "\1\u00f9\5\uffff\1\u00ec\3\uffff\1\u00f6\1\u00f7\1\uffff\1\u0109\1\u00f0"+
            "\1\u0106\1\u0107\1\u0108\1\u0112\3\uffff\1\u00e9\2\uffff\1\u0105\11\uffff"+
            "\1\u0111\1\uffff\1\u0104\2\uffff\1\u0100\6\uffff\1\u00f1\1\uffff\1\u00ff"+
            "\1\uffff\1\u0102\3\uffff\1\u00f2\3\uffff\1\u0101\1\u00fa\3\uffff\1\u00fd"+
            "\1\u0103\1\u010d\1\u00ed\1\uffff\1\u00ee\1\u00eb\1\u00fb\1\uffff\1\u00f5"+
            "\4\uffff\1\u00f8\7\uffff\1\u00ea\1\u00f4\4\uffff\1\u00fe\1\uffff\1\u00fc"+
            "\1\u010e\1\u010f\1\u010a\1\u00ef\2\uffff\1\u0114\1\u0113\3\uffff\1\u010b"+
            "\1\u0110\1\u0115\2\uffff\1\u010c\1\u00f3",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    protected class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        @Override
        public String getDescription() {
            return "381:1: selectExpression returns [Object node] : (n= aggregateExpression |n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET |n= constructorExpression |n= mapEntryExpression );";
        }
        @Override
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
            int _s = s;
            switch ( s ) {
                    case 0 :
                        int LA19_53 = input.LA(1);

                        int index19_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_53);
                        if ( s>=0 ) return s;
                        break;

                    case 1 :
                        int LA19_54 = input.LA(1);

                        int index19_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_54);
                        if ( s>=0 ) return s;
                        break;

                    case 2 :
                        int LA19_55 = input.LA(1);

                        int index19_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_55);
                        if ( s>=0 ) return s;
                        break;

                    case 3 :
                        int LA19_56 = input.LA(1);

                        int index19_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_56);
                        if ( s>=0 ) return s;
                        break;

                    case 4 :
                        int LA19_57 = input.LA(1);

                        int index19_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_57);
                        if ( s>=0 ) return s;
                        break;

                    case 5 :
                        int LA19_58 = input.LA(1);

                        int index19_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_58);
                        if ( s>=0 ) return s;
                        break;

                    case 6 :
                        int LA19_59 = input.LA(1);

                        int index19_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_59);
                        if ( s>=0 ) return s;
                        break;

                    case 7 :
                        int LA19_60 = input.LA(1);

                        int index19_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_60);
                        if ( s>=0 ) return s;
                        break;

                    case 8 :
                        int LA19_61 = input.LA(1);

                        int index19_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_61);
                        if ( s>=0 ) return s;
                        break;

                    case 9 :
                        int LA19_62 = input.LA(1);

                        int index19_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_62);
                        if ( s>=0 ) return s;
                        break;

                    case 10 :
                        int LA19_63 = input.LA(1);

                        int index19_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_63);
                        if ( s>=0 ) return s;
                        break;

                    case 11 :
                        int LA19_64 = input.LA(1);

                        int index19_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_64);
                        if ( s>=0 ) return s;
                        break;

                    case 12 :
                        int LA19_65 = input.LA(1);

                        int index19_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_65);
                        if ( s>=0 ) return s;
                        break;

                    case 13 :
                        int LA19_66 = input.LA(1);

                        int index19_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_66);
                        if ( s>=0 ) return s;
                        break;

                    case 14 :
                        int LA19_67 = input.LA(1);

                        int index19_67 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_67);
                        if ( s>=0 ) return s;
                        break;

                    case 15 :
                        int LA19_68 = input.LA(1);

                        int index19_68 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_68);
                        if ( s>=0 ) return s;
                        break;

                    case 16 :
                        int LA19_69 = input.LA(1);

                        int index19_69 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_69);
                        if ( s>=0 ) return s;
                        break;

                    case 17 :
                        int LA19_70 = input.LA(1);

                        int index19_70 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_70);
                        if ( s>=0 ) return s;
                        break;

                    case 18 :
                        int LA19_71 = input.LA(1);

                        int index19_71 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_71);
                        if ( s>=0 ) return s;
                        break;

                    case 19 :
                        int LA19_72 = input.LA(1);

                        int index19_72 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_72);
                        if ( s>=0 ) return s;
                        break;

                    case 20 :
                        int LA19_73 = input.LA(1);

                        int index19_73 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_73);
                        if ( s>=0 ) return s;
                        break;

                    case 21 :
                        int LA19_74 = input.LA(1);

                        int index19_74 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_74);
                        if ( s>=0 ) return s;
                        break;

                    case 22 :
                        int LA19_75 = input.LA(1);

                        int index19_75 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_75);
                        if ( s>=0 ) return s;
                        break;

                    case 23 :
                        int LA19_76 = input.LA(1);

                        int index19_76 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_76);
                        if ( s>=0 ) return s;
                        break;

                    case 24 :
                        int LA19_77 = input.LA(1);

                        int index19_77 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_77);
                        if ( s>=0 ) return s;
                        break;

                    case 25 :
                        int LA19_78 = input.LA(1);

                        int index19_78 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_78);
                        if ( s>=0 ) return s;
                        break;

                    case 26 :
                        int LA19_79 = input.LA(1);

                        int index19_79 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_79);
                        if ( s>=0 ) return s;
                        break;

                    case 27 :
                        int LA19_80 = input.LA(1);

                        int index19_80 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_80);
                        if ( s>=0 ) return s;
                        break;

                    case 28 :
                        int LA19_81 = input.LA(1);

                        int index19_81 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_81);
                        if ( s>=0 ) return s;
                        break;

                    case 29 :
                        int LA19_82 = input.LA(1);

                        int index19_82 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_82);
                        if ( s>=0 ) return s;
                        break;

                    case 30 :
                        int LA19_83 = input.LA(1);

                        int index19_83 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_83);
                        if ( s>=0 ) return s;
                        break;

                    case 31 :
                        int LA19_84 = input.LA(1);

                        int index19_84 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_84);
                        if ( s>=0 ) return s;
                        break;

                    case 32 :
                        int LA19_85 = input.LA(1);

                        int index19_85 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_85);
                        if ( s>=0 ) return s;
                        break;

                    case 33 :
                        int LA19_86 = input.LA(1);

                        int index19_86 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_86);
                        if ( s>=0 ) return s;
                        break;

                    case 34 :
                        int LA19_87 = input.LA(1);

                        int index19_87 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_87);
                        if ( s>=0 ) return s;
                        break;

                    case 35 :
                        int LA19_88 = input.LA(1);

                        int index19_88 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_88);
                        if ( s>=0 ) return s;
                        break;

                    case 36 :
                        int LA19_89 = input.LA(1);

                        int index19_89 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_89);
                        if ( s>=0 ) return s;
                        break;

                    case 37 :
                        int LA19_90 = input.LA(1);

                        int index19_90 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_90);
                        if ( s>=0 ) return s;
                        break;

                    case 38 :
                        int LA19_91 = input.LA(1);

                        int index19_91 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_91);
                        if ( s>=0 ) return s;
                        break;

                    case 39 :
                        int LA19_92 = input.LA(1);

                        int index19_92 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_92);
                        if ( s>=0 ) return s;
                        break;

                    case 40 :
                        int LA19_93 = input.LA(1);

                        int index19_93 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_93);
                        if ( s>=0 ) return s;
                        break;

                    case 41 :
                        int LA19_94 = input.LA(1);

                        int index19_94 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_94);
                        if ( s>=0 ) return s;
                        break;

                    case 42 :
                        int LA19_95 = input.LA(1);

                        int index19_95 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_95);
                        if ( s>=0 ) return s;
                        break;

                    case 43 :
                        int LA19_96 = input.LA(1);

                        int index19_96 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_96);
                        if ( s>=0 ) return s;
                        break;

                    case 44 :
                        int LA19_97 = input.LA(1);

                        int index19_97 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_97);
                        if ( s>=0 ) return s;
                        break;

                    case 45 :
                        int LA19_98 = input.LA(1);

                        int index19_98 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_98);
                        if ( s>=0 ) return s;
                        break;

                    case 46 :
                        int LA19_99 = input.LA(1);

                        int index19_99 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_99);
                        if ( s>=0 ) return s;
                        break;

                    case 47 :
                        int LA19_100 = input.LA(1);

                        int index19_100 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_100);
                        if ( s>=0 ) return s;
                        break;

                    case 48 :
                        int LA19_101 = input.LA(1);

                        int index19_101 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_101);
                        if ( s>=0 ) return s;
                        break;

                    case 49 :
                        int LA19_102 = input.LA(1);

                        int index19_102 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_102);
                        if ( s>=0 ) return s;
                        break;

                    case 50 :
                        int LA19_103 = input.LA(1);

                        int index19_103 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_103);
                        if ( s>=0 ) return s;
                        break;

                    case 51 :
                        int LA19_104 = input.LA(1);

                        int index19_104 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_104);
                        if ( s>=0 ) return s;
                        break;

                    case 52 :
                        int LA19_105 = input.LA(1);

                        int index19_105 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_105);
                        if ( s>=0 ) return s;
                        break;

                    case 53 :
                        int LA19_106 = input.LA(1);

                        int index19_106 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_106);
                        if ( s>=0 ) return s;
                        break;

                    case 54 :
                        int LA19_107 = input.LA(1);

                        int index19_107 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_107);
                        if ( s>=0 ) return s;
                        break;

                    case 55 :
                        int LA19_108 = input.LA(1);

                        int index19_108 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_108);
                        if ( s>=0 ) return s;
                        break;

                    case 56 :
                        int LA19_109 = input.LA(1);

                        int index19_109 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_109);
                        if ( s>=0 ) return s;
                        break;

                    case 57 :
                        int LA19_110 = input.LA(1);

                        int index19_110 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_110);
                        if ( s>=0 ) return s;
                        break;

                    case 58 :
                        int LA19_111 = input.LA(1);

                        int index19_111 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_111);
                        if ( s>=0 ) return s;
                        break;

                    case 59 :
                        int LA19_112 = input.LA(1);

                        int index19_112 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_112);
                        if ( s>=0 ) return s;
                        break;

                    case 60 :
                        int LA19_113 = input.LA(1);

                        int index19_113 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_113);
                        if ( s>=0 ) return s;
                        break;

                    case 61 :
                        int LA19_114 = input.LA(1);

                        int index19_114 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_114);
                        if ( s>=0 ) return s;
                        break;

                    case 62 :
                        int LA19_115 = input.LA(1);

                        int index19_115 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_115);
                        if ( s>=0 ) return s;
                        break;

                    case 63 :
                        int LA19_116 = input.LA(1);

                        int index19_116 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_116);
                        if ( s>=0 ) return s;
                        break;

                    case 64 :
                        int LA19_117 = input.LA(1);

                        int index19_117 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_117);
                        if ( s>=0 ) return s;
                        break;

                    case 65 :
                        int LA19_118 = input.LA(1);

                        int index19_118 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_118);
                        if ( s>=0 ) return s;
                        break;

                    case 66 :
                        int LA19_119 = input.LA(1);

                        int index19_119 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_119);
                        if ( s>=0 ) return s;
                        break;

                    case 67 :
                        int LA19_120 = input.LA(1);

                        int index19_120 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_120);
                        if ( s>=0 ) return s;
                        break;

                    case 68 :
                        int LA19_121 = input.LA(1);

                        int index19_121 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_121);
                        if ( s>=0 ) return s;
                        break;

                    case 69 :
                        int LA19_122 = input.LA(1);

                        int index19_122 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_122);
                        if ( s>=0 ) return s;
                        break;

                    case 70 :
                        int LA19_123 = input.LA(1);

                        int index19_123 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_123);
                        if ( s>=0 ) return s;
                        break;

                    case 71 :
                        int LA19_124 = input.LA(1);

                        int index19_124 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_124);
                        if ( s>=0 ) return s;
                        break;

                    case 72 :
                        int LA19_125 = input.LA(1);

                        int index19_125 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_125);
                        if ( s>=0 ) return s;
                        break;

                    case 73 :
                        int LA19_126 = input.LA(1);

                        int index19_126 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_126);
                        if ( s>=0 ) return s;
                        break;

                    case 74 :
                        int LA19_127 = input.LA(1);

                        int index19_127 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_127);
                        if ( s>=0 ) return s;
                        break;

                    case 75 :
                        int LA19_128 = input.LA(1);

                        int index19_128 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_128);
                        if ( s>=0 ) return s;
                        break;

                    case 76 :
                        int LA19_129 = input.LA(1);

                        int index19_129 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_129);
                        if ( s>=0 ) return s;
                        break;

                    case 77 :
                        int LA19_130 = input.LA(1);

                        int index19_130 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_130);
                        if ( s>=0 ) return s;
                        break;

                    case 78 :
                        int LA19_131 = input.LA(1);

                        int index19_131 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_131);
                        if ( s>=0 ) return s;
                        break;

                    case 79 :
                        int LA19_132 = input.LA(1);

                        int index19_132 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_132);
                        if ( s>=0 ) return s;
                        break;

                    case 80 :
                        int LA19_133 = input.LA(1);

                        int index19_133 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_133);
                        if ( s>=0 ) return s;
                        break;

                    case 81 :
                        int LA19_134 = input.LA(1);

                        int index19_134 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_134);
                        if ( s>=0 ) return s;
                        break;

                    case 82 :
                        int LA19_135 = input.LA(1);

                        int index19_135 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_135);
                        if ( s>=0 ) return s;
                        break;

                    case 83 :
                        int LA19_136 = input.LA(1);

                        int index19_136 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_136);
                        if ( s>=0 ) return s;
                        break;

                    case 84 :
                        int LA19_137 = input.LA(1);

                        int index19_137 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_137);
                        if ( s>=0 ) return s;
                        break;

                    case 85 :
                        int LA19_138 = input.LA(1);

                        int index19_138 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_138);
                        if ( s>=0 ) return s;
                        break;

                    case 86 :
                        int LA19_139 = input.LA(1);

                        int index19_139 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_139);
                        if ( s>=0 ) return s;
                        break;

                    case 87 :
                        int LA19_140 = input.LA(1);

                        int index19_140 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_140);
                        if ( s>=0 ) return s;
                        break;

                    case 88 :
                        int LA19_141 = input.LA(1);

                        int index19_141 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_141);
                        if ( s>=0 ) return s;
                        break;

                    case 89 :
                        int LA19_142 = input.LA(1);

                        int index19_142 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_142);
                        if ( s>=0 ) return s;
                        break;

                    case 90 :
                        int LA19_143 = input.LA(1);

                        int index19_143 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_143);
                        if ( s>=0 ) return s;
                        break;

                    case 91 :
                        int LA19_144 = input.LA(1);

                        int index19_144 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_144);
                        if ( s>=0 ) return s;
                        break;

                    case 92 :
                        int LA19_145 = input.LA(1);

                        int index19_145 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_145);
                        if ( s>=0 ) return s;
                        break;

                    case 93 :
                        int LA19_146 = input.LA(1);

                        int index19_146 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_146);
                        if ( s>=0 ) return s;
                        break;

                    case 94 :
                        int LA19_147 = input.LA(1);

                        int index19_147 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_147);
                        if ( s>=0 ) return s;
                        break;

                    case 95 :
                        int LA19_148 = input.LA(1);

                        int index19_148 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_148);
                        if ( s>=0 ) return s;
                        break;

                    case 96 :
                        int LA19_149 = input.LA(1);

                        int index19_149 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_149);
                        if ( s>=0 ) return s;
                        break;

                    case 97 :
                        int LA19_150 = input.LA(1);

                        int index19_150 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_150);
                        if ( s>=0 ) return s;
                        break;

                    case 98 :
                        int LA19_151 = input.LA(1);

                        int index19_151 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_151);
                        if ( s>=0 ) return s;
                        break;

                    case 99 :
                        int LA19_152 = input.LA(1);

                        int index19_152 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_152);
                        if ( s>=0 ) return s;
                        break;

                    case 100 :
                        int LA19_153 = input.LA(1);

                        int index19_153 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_153);
                        if ( s>=0 ) return s;
                        break;

                    case 101 :
                        int LA19_154 = input.LA(1);

                        int index19_154 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_154);
                        if ( s>=0 ) return s;
                        break;

                    case 102 :
                        int LA19_155 = input.LA(1);

                        int index19_155 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_155);
                        if ( s>=0 ) return s;
                        break;

                    case 103 :
                        int LA19_156 = input.LA(1);

                        int index19_156 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_156);
                        if ( s>=0 ) return s;
                        break;

                    case 104 :
                        int LA19_157 = input.LA(1);

                        int index19_157 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_157);
                        if ( s>=0 ) return s;
                        break;

                    case 105 :
                        int LA19_158 = input.LA(1);

                        int index19_158 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_158);
                        if ( s>=0 ) return s;
                        break;

                    case 106 :
                        int LA19_159 = input.LA(1);

                        int index19_159 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_159);
                        if ( s>=0 ) return s;
                        break;

                    case 107 :
                        int LA19_160 = input.LA(1);

                        int index19_160 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_160);
                        if ( s>=0 ) return s;
                        break;

                    case 108 :
                        int LA19_161 = input.LA(1);

                        int index19_161 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_161);
                        if ( s>=0 ) return s;
                        break;

                    case 109 :
                        int LA19_162 = input.LA(1);

                        int index19_162 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_162);
                        if ( s>=0 ) return s;
                        break;

                    case 110 :
                        int LA19_163 = input.LA(1);

                        int index19_163 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_163);
                        if ( s>=0 ) return s;
                        break;

                    case 111 :
                        int LA19_164 = input.LA(1);

                        int index19_164 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_164);
                        if ( s>=0 ) return s;
                        break;

                    case 112 :
                        int LA19_165 = input.LA(1);

                        int index19_165 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_165);
                        if ( s>=0 ) return s;
                        break;

                    case 113 :
                        int LA19_166 = input.LA(1);

                        int index19_166 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_166);
                        if ( s>=0 ) return s;
                        break;

                    case 114 :
                        int LA19_167 = input.LA(1);

                        int index19_167 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_167);
                        if ( s>=0 ) return s;
                        break;

                    case 115 :
                        int LA19_168 = input.LA(1);

                        int index19_168 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_168);
                        if ( s>=0 ) return s;
                        break;

                    case 116 :
                        int LA19_169 = input.LA(1);

                        int index19_169 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_169);
                        if ( s>=0 ) return s;
                        break;

                    case 117 :
                        int LA19_170 = input.LA(1);

                        int index19_170 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_170);
                        if ( s>=0 ) return s;
                        break;

                    case 118 :
                        int LA19_171 = input.LA(1);

                        int index19_171 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_171);
                        if ( s>=0 ) return s;
                        break;

                    case 119 :
                        int LA19_172 = input.LA(1);

                        int index19_172 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_172);
                        if ( s>=0 ) return s;
                        break;

                    case 120 :
                        int LA19_173 = input.LA(1);

                        int index19_173 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_173);
                        if ( s>=0 ) return s;
                        break;

                    case 121 :
                        int LA19_174 = input.LA(1);

                        int index19_174 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_174);
                        if ( s>=0 ) return s;
                        break;

                    case 122 :
                        int LA19_175 = input.LA(1);

                        int index19_175 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_175);
                        if ( s>=0 ) return s;
                        break;

                    case 123 :
                        int LA19_176 = input.LA(1);

                        int index19_176 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_176);
                        if ( s>=0 ) return s;
                        break;

                    case 124 :
                        int LA19_177 = input.LA(1);

                        int index19_177 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_177);
                        if ( s>=0 ) return s;
                        break;

                    case 125 :
                        int LA19_178 = input.LA(1);

                        int index19_178 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_178);
                        if ( s>=0 ) return s;
                        break;

                    case 126 :
                        int LA19_179 = input.LA(1);

                        int index19_179 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_179);
                        if ( s>=0 ) return s;
                        break;

                    case 127 :
                        int LA19_180 = input.LA(1);

                        int index19_180 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_180);
                        if ( s>=0 ) return s;
                        break;

                    case 128 :
                        int LA19_181 = input.LA(1);

                        int index19_181 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_181);
                        if ( s>=0 ) return s;
                        break;

                    case 129 :
                        int LA19_182 = input.LA(1);

                        int index19_182 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_182);
                        if ( s>=0 ) return s;
                        break;

                    case 130 :
                        int LA19_183 = input.LA(1);

                        int index19_183 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_183);
                        if ( s>=0 ) return s;
                        break;

                    case 131 :
                        int LA19_184 = input.LA(1);

                        int index19_184 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_184);
                        if ( s>=0 ) return s;
                        break;

                    case 132 :
                        int LA19_185 = input.LA(1);

                        int index19_185 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_185);
                        if ( s>=0 ) return s;
                        break;

                    case 133 :
                        int LA19_186 = input.LA(1);

                        int index19_186 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_186);
                        if ( s>=0 ) return s;
                        break;

                    case 134 :
                        int LA19_187 = input.LA(1);

                        int index19_187 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_187);
                        if ( s>=0 ) return s;
                        break;

                    case 135 :
                        int LA19_188 = input.LA(1);

                        int index19_188 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_188);
                        if ( s>=0 ) return s;
                        break;

                    case 136 :
                        int LA19_189 = input.LA(1);

                        int index19_189 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_189);
                        if ( s>=0 ) return s;
                        break;

                    case 137 :
                        int LA19_190 = input.LA(1);

                        int index19_190 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_190);
                        if ( s>=0 ) return s;
                        break;

                    case 138 :
                        int LA19_191 = input.LA(1);

                        int index19_191 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_191);
                        if ( s>=0 ) return s;
                        break;

                    case 139 :
                        int LA19_192 = input.LA(1);

                        int index19_192 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_192);
                        if ( s>=0 ) return s;
                        break;

                    case 140 :
                        int LA19_193 = input.LA(1);

                        int index19_193 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_193);
                        if ( s>=0 ) return s;
                        break;

                    case 141 :
                        int LA19_194 = input.LA(1);

                        int index19_194 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_194);
                        if ( s>=0 ) return s;
                        break;

                    case 142 :
                        int LA19_195 = input.LA(1);

                        int index19_195 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_195);
                        if ( s>=0 ) return s;
                        break;

                    case 143 :
                        int LA19_196 = input.LA(1);

                        int index19_196 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_196);
                        if ( s>=0 ) return s;
                        break;

                    case 144 :
                        int LA19_197 = input.LA(1);

                        int index19_197 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_197);
                        if ( s>=0 ) return s;
                        break;

                    case 145 :
                        int LA19_198 = input.LA(1);

                        int index19_198 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_198);
                        if ( s>=0 ) return s;
                        break;

                    case 146 :
                        int LA19_199 = input.LA(1);

                        int index19_199 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_199);
                        if ( s>=0 ) return s;
                        break;

                    case 147 :
                        int LA19_200 = input.LA(1);

                        int index19_200 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_200);
                        if ( s>=0 ) return s;
                        break;

                    case 148 :
                        int LA19_201 = input.LA(1);

                        int index19_201 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_201);
                        if ( s>=0 ) return s;
                        break;

                    case 149 :
                        int LA19_202 = input.LA(1);

                        int index19_202 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_202);
                        if ( s>=0 ) return s;
                        break;

                    case 150 :
                        int LA19_203 = input.LA(1);

                        int index19_203 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_203);
                        if ( s>=0 ) return s;
                        break;

                    case 151 :
                        int LA19_204 = input.LA(1);

                        int index19_204 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_204);
                        if ( s>=0 ) return s;
                        break;

                    case 152 :
                        int LA19_205 = input.LA(1);

                        int index19_205 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_205);
                        if ( s>=0 ) return s;
                        break;

                    case 153 :
                        int LA19_206 = input.LA(1);

                        int index19_206 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_206);
                        if ( s>=0 ) return s;
                        break;

                    case 154 :
                        int LA19_207 = input.LA(1);

                        int index19_207 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_207);
                        if ( s>=0 ) return s;
                        break;

                    case 155 :
                        int LA19_208 = input.LA(1);

                        int index19_208 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_208);
                        if ( s>=0 ) return s;
                        break;

                    case 156 :
                        int LA19_209 = input.LA(1);

                        int index19_209 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_209);
                        if ( s>=0 ) return s;
                        break;

                    case 157 :
                        int LA19_210 = input.LA(1);

                        int index19_210 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_210);
                        if ( s>=0 ) return s;
                        break;

                    case 158 :
                        int LA19_211 = input.LA(1);

                        int index19_211 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_211);
                        if ( s>=0 ) return s;
                        break;

                    case 159 :
                        int LA19_212 = input.LA(1);

                        int index19_212 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_212);
                        if ( s>=0 ) return s;
                        break;

                    case 160 :
                        int LA19_213 = input.LA(1);

                        int index19_213 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_213);
                        if ( s>=0 ) return s;
                        break;

                    case 161 :
                        int LA19_214 = input.LA(1);

                        int index19_214 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_214);
                        if ( s>=0 ) return s;
                        break;

                    case 162 :
                        int LA19_215 = input.LA(1);

                        int index19_215 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_215);
                        if ( s>=0 ) return s;
                        break;

                    case 163 :
                        int LA19_216 = input.LA(1);

                        int index19_216 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_216);
                        if ( s>=0 ) return s;
                        break;

                    case 164 :
                        int LA19_217 = input.LA(1);

                        int index19_217 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_217);
                        if ( s>=0 ) return s;
                        break;

                    case 165 :
                        int LA19_218 = input.LA(1);

                        int index19_218 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_218);
                        if ( s>=0 ) return s;
                        break;

                    case 166 :
                        int LA19_219 = input.LA(1);

                        int index19_219 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_219);
                        if ( s>=0 ) return s;
                        break;

                    case 167 :
                        int LA19_220 = input.LA(1);

                        int index19_220 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_220);
                        if ( s>=0 ) return s;
                        break;

                    case 168 :
                        int LA19_221 = input.LA(1);

                        int index19_221 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_221);
                        if ( s>=0 ) return s;
                        break;

                    case 169 :
                        int LA19_222 = input.LA(1);

                        int index19_222 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_222);
                        if ( s>=0 ) return s;
                        break;

                    case 170 :
                        int LA19_223 = input.LA(1);

                        int index19_223 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_223);
                        if ( s>=0 ) return s;
                        break;

                    case 171 :
                        int LA19_224 = input.LA(1);

                        int index19_224 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_224);
                        if ( s>=0 ) return s;
                        break;

                    case 172 :
                        int LA19_225 = input.LA(1);

                        int index19_225 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_225);
                        if ( s>=0 ) return s;
                        break;

                    case 173 :
                        int LA19_226 = input.LA(1);

                        int index19_226 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_226);
                        if ( s>=0 ) return s;
                        break;

                    case 174 :
                        int LA19_227 = input.LA(1);

                        int index19_227 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_227);
                        if ( s>=0 ) return s;
                        break;

                    case 175 :
                        int LA19_228 = input.LA(1);

                        int index19_228 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_228);
                        if ( s>=0 ) return s;
                        break;

                    case 176 :
                        int LA19_229 = input.LA(1);

                        int index19_229 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_229);
                        if ( s>=0 ) return s;
                        break;

                    case 177 :
                        int LA19_230 = input.LA(1);

                        int index19_230 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_230);
                        if ( s>=0 ) return s;
                        break;

                    case 178 :
                        int LA19_231 = input.LA(1);

                        int index19_231 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_231);
                        if ( s>=0 ) return s;
                        break;

                    case 179 :
                        int LA19_232 = input.LA(1);

                        int index19_232 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_232);
                        if ( s>=0 ) return s;
                        break;

                    case 180 :
                        int LA19_233 = input.LA(1);

                        int index19_233 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_233);
                        if ( s>=0 ) return s;
                        break;

                    case 181 :
                        int LA19_234 = input.LA(1);

                        int index19_234 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_234);
                        if ( s>=0 ) return s;
                        break;

                    case 182 :
                        int LA19_235 = input.LA(1);

                        int index19_235 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_235);
                        if ( s>=0 ) return s;
                        break;

                    case 183 :
                        int LA19_236 = input.LA(1);

                        int index19_236 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_236);
                        if ( s>=0 ) return s;
                        break;

                    case 184 :
                        int LA19_237 = input.LA(1);

                        int index19_237 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_237);
                        if ( s>=0 ) return s;
                        break;

                    case 185 :
                        int LA19_238 = input.LA(1);

                        int index19_238 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_238);
                        if ( s>=0 ) return s;
                        break;

                    case 186 :
                        int LA19_239 = input.LA(1);

                        int index19_239 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_239);
                        if ( s>=0 ) return s;
                        break;

                    case 187 :
                        int LA19_240 = input.LA(1);

                        int index19_240 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_240);
                        if ( s>=0 ) return s;
                        break;

                    case 188 :
                        int LA19_241 = input.LA(1);

                        int index19_241 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_241);
                        if ( s>=0 ) return s;
                        break;

                    case 189 :
                        int LA19_242 = input.LA(1);

                        int index19_242 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_242);
                        if ( s>=0 ) return s;
                        break;

                    case 190 :
                        int LA19_243 = input.LA(1);

                        int index19_243 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_243);
                        if ( s>=0 ) return s;
                        break;

                    case 191 :
                        int LA19_244 = input.LA(1);

                        int index19_244 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_244);
                        if ( s>=0 ) return s;
                        break;

                    case 192 :
                        int LA19_245 = input.LA(1);

                        int index19_245 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_245);
                        if ( s>=0 ) return s;
                        break;

                    case 193 :
                        int LA19_246 = input.LA(1);

                        int index19_246 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_246);
                        if ( s>=0 ) return s;
                        break;

                    case 194 :
                        int LA19_247 = input.LA(1);

                        int index19_247 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_247);
                        if ( s>=0 ) return s;
                        break;

                    case 195 :
                        int LA19_248 = input.LA(1);

                        int index19_248 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_248);
                        if ( s>=0 ) return s;
                        break;

                    case 196 :
                        int LA19_249 = input.LA(1);

                        int index19_249 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_249);
                        if ( s>=0 ) return s;
                        break;

                    case 197 :
                        int LA19_250 = input.LA(1);

                        int index19_250 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_250);
                        if ( s>=0 ) return s;
                        break;

                    case 198 :
                        int LA19_251 = input.LA(1);

                        int index19_251 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_251);
                        if ( s>=0 ) return s;
                        break;

                    case 199 :
                        int LA19_252 = input.LA(1);

                        int index19_252 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_252);
                        if ( s>=0 ) return s;
                        break;

                    case 200 :
                        int LA19_253 = input.LA(1);

                        int index19_253 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_253);
                        if ( s>=0 ) return s;
                        break;

                    case 201 :
                        int LA19_254 = input.LA(1);

                        int index19_254 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_254);
                        if ( s>=0 ) return s;
                        break;

                    case 202 :
                        int LA19_255 = input.LA(1);

                        int index19_255 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_255);
                        if ( s>=0 ) return s;
                        break;

                    case 203 :
                        int LA19_256 = input.LA(1);

                        int index19_256 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_256);
                        if ( s>=0 ) return s;
                        break;

                    case 204 :
                        int LA19_257 = input.LA(1);

                        int index19_257 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_257);
                        if ( s>=0 ) return s;
                        break;

                    case 205 :
                        int LA19_258 = input.LA(1);

                        int index19_258 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_258);
                        if ( s>=0 ) return s;
                        break;

                    case 206 :
                        int LA19_259 = input.LA(1);

                        int index19_259 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_259);
                        if ( s>=0 ) return s;
                        break;

                    case 207 :
                        int LA19_260 = input.LA(1);

                        int index19_260 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_260);
                        if ( s>=0 ) return s;
                        break;

                    case 208 :
                        int LA19_261 = input.LA(1);

                        int index19_261 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_261);
                        if ( s>=0 ) return s;
                        break;

                    case 209 :
                        int LA19_262 = input.LA(1);

                        int index19_262 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_262);
                        if ( s>=0 ) return s;
                        break;

                    case 210 :
                        int LA19_263 = input.LA(1);

                        int index19_263 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_263);
                        if ( s>=0 ) return s;
                        break;

                    case 211 :
                        int LA19_264 = input.LA(1);

                        int index19_264 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_264);
                        if ( s>=0 ) return s;
                        break;

                    case 212 :
                        int LA19_265 = input.LA(1);

                        int index19_265 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_265);
                        if ( s>=0 ) return s;
                        break;

                    case 213 :
                        int LA19_266 = input.LA(1);

                        int index19_266 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_266);
                        if ( s>=0 ) return s;
                        break;

                    case 214 :
                        int LA19_267 = input.LA(1);

                        int index19_267 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_267);
                        if ( s>=0 ) return s;
                        break;

                    case 215 :
                        int LA19_268 = input.LA(1);

                        int index19_268 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_268);
                        if ( s>=0 ) return s;
                        break;

                    case 216 :
                        int LA19_269 = input.LA(1);

                        int index19_269 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_269);
                        if ( s>=0 ) return s;
                        break;

                    case 217 :
                        int LA19_270 = input.LA(1);

                        int index19_270 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_270);
                        if ( s>=0 ) return s;
                        break;

                    case 218 :
                        int LA19_271 = input.LA(1);

                        int index19_271 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_271);
                        if ( s>=0 ) return s;
                        break;

                    case 219 :
                        int LA19_272 = input.LA(1);

                        int index19_272 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_272);
                        if ( s>=0 ) return s;
                        break;

                    case 220 :
                        int LA19_273 = input.LA(1);

                        int index19_273 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_273);
                        if ( s>=0 ) return s;
                        break;

                    case 221 :
                        int LA19_274 = input.LA(1);

                        int index19_274 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_274);
                        if ( s>=0 ) return s;
                        break;

                    case 222 :
                        int LA19_275 = input.LA(1);

                        int index19_275 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_275);
                        if ( s>=0 ) return s;
                        break;

                    case 223 :
                        int LA19_276 = input.LA(1);

                        int index19_276 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_276);
                        if ( s>=0 ) return s;
                        break;

                    case 224 :
                        int LA19_277 = input.LA(1);

                        int index19_277 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!((( aggregatesAllowed() )))) ) {s = 278;}
                        else if ( (( aggregatesAllowed() )) ) {s = 6;}

                        input.seek(index19_277);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 19, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    static final String DFA30_eotS =
        "\u0114\uffff";
    static final String DFA30_eofS =
        "\u0114\uffff";
    static final String DFA30_minS =
        "\1\4\2\uffff\5\77\45\uffff\5\4\u00e1\0\1\uffff";
    static final String DFA30_maxS =
        "\1\164\2\uffff\5\77\45\uffff\5\164\u00e1\0\1\uffff";
    static final String DFA30_acceptS =
        "\1\uffff\1\1\u0111\uffff\1\2";
    static final String DFA30_specialS =
        "\62\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
        "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33"+
        "\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47\1\50\1\51"+
        "\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\63\1\64\1\65\1\66\1\67"+
        "\1\70\1\71\1\72\1\73\1\74\1\75\1\76\1\77\1\100\1\101\1\102\1\103\1\104"+
        "\1\105\1\106\1\107\1\110\1\111\1\112\1\113\1\114\1\115\1\116\1\117\1\120"+
        "\1\121\1\122\1\123\1\124\1\125\1\126\1\127\1\130\1\131\1\132\1\133\1\134"+
        "\1\135\1\136\1\137\1\140\1\141\1\142\1\143\1\144\1\145\1\146\1\147\1\150"+
        "\1\151\1\152\1\153\1\154\1\155\1\156\1\157\1\160\1\161\1\162\1\163\1\164"+
        "\1\165\1\166\1\167\1\170\1\171\1\172\1\173\1\174\1\175\1\176\1\177\1\u0080"+
        "\1\u0081\1\u0082\1\u0083\1\u0084\1\u0085\1\u0086\1\u0087\1\u0088\1\u0089"+
        "\1\u008a\1\u008b\1\u008c\1\u008d\1\u008e\1\u008f\1\u0090\1\u0091\1\u0092"+
        "\1\u0093\1\u0094\1\u0095\1\u0096\1\u0097\1\u0098\1\u0099\1\u009a\1\u009b"+
        "\1\u009c\1\u009d\1\u009e\1\u009f\1\u00a0\1\u00a1\1\u00a2\1\u00a3\1\u00a4"+
        "\1\u00a5\1\u00a6\1\u00a7\1\u00a8\1\u00a9\1\u00aa\1\u00ab\1\u00ac\1\u00ad"+
        "\1\u00ae\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00b4\1\u00b5\1\u00b6"+
        "\1\u00b7\1\u00b8\1\u00b9\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00be\1\u00bf"+
        "\1\u00c0\1\u00c1\1\u00c2\1\u00c3\1\u00c4\1\u00c5\1\u00c6\1\u00c7\1\u00c8"+
        "\1\u00c9\1\u00ca\1\u00cb\1\u00cc\1\u00cd\1\u00ce\1\u00cf\1\u00d0\1\u00d1"+
        "\1\u00d2\1\u00d3\1\u00d4\1\u00d5\1\u00d6\1\u00d7\1\u00d8\1\u00d9\1\u00da"+
        "\1\u00db\1\u00dc\1\u00dd\1\u00de\1\u00df\1\u00e0\1\uffff}>";
    static final String[] DFA30_transitionS = {
            "\1\1\5\uffff\1\3\3\uffff\2\1\1\uffff\1\1\1\7\4\1\6\uffff\1\1\11\uffff"+
            "\1\1\1\uffff\1\1\2\uffff\1\1\6\uffff\1\1\1\uffff\1\1\1\uffff\1\1\3\uffff"+
            "\1\1\3\uffff\2\1\3\uffff\3\1\1\4\1\uffff\1\5\2\1\1\uffff\1\1\4\uffff"+
            "\1\1\7\uffff\2\1\4\uffff\1\1\1\uffff\4\1\1\6\2\uffff\2\1\3\uffff\3\1"+
            "\2\uffff\2\1",
            "",
            "",
            "\1\55",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\102\5\uffff\1\65\3\uffff\1\77\1\100\1\uffff\1\122\1\71\1\117\1\120"+
            "\1\121\1\133\3\uffff\1\62\2\uffff\1\116\11\uffff\1\132\1\uffff\1\115"+
            "\2\uffff\1\111\6\uffff\1\72\1\uffff\1\110\1\uffff\1\113\3\uffff\1\73"+
            "\3\uffff\1\112\1\103\3\uffff\1\106\1\114\1\126\1\66\1\uffff\1\67\1\64"+
            "\1\104\1\uffff\1\76\4\uffff\1\101\7\uffff\1\63\1\75\4\uffff\1\107\1\uffff"+
            "\1\105\1\127\1\130\1\123\1\70\2\uffff\1\135\1\134\3\uffff\1\124\1\131"+
            "\1\136\2\uffff\1\125\1\74",
            "\1\157\5\uffff\1\142\3\uffff\1\154\1\155\1\uffff\1\177\1\146\1\174\1"+
            "\175\1\176\1\u0088\3\uffff\1\137\2\uffff\1\173\11\uffff\1\u0087\1\uffff"+
            "\1\172\2\uffff\1\166\6\uffff\1\147\1\uffff\1\165\1\uffff\1\170\3\uffff"+
            "\1\150\3\uffff\1\167\1\160\3\uffff\1\163\1\171\1\u0083\1\143\1\uffff"+
            "\1\144\1\141\1\161\1\uffff\1\153\4\uffff\1\156\7\uffff\1\140\1\152\4"+
            "\uffff\1\164\1\uffff\1\162\1\u0084\1\u0085\1\u0080\1\145\2\uffff\1\u008a"+
            "\1\u0089\3\uffff\1\u0081\1\u0086\1\u008b\2\uffff\1\u0082\1\151",
            "\1\u009c\5\uffff\1\u008f\3\uffff\1\u0099\1\u009a\1\uffff\1\u00ac\1\u0093"+
            "\1\u00a9\1\u00aa\1\u00ab\1\u00b5\3\uffff\1\u008c\2\uffff\1\u00a8\11\uffff"+
            "\1\u00b4\1\uffff\1\u00a7\2\uffff\1\u00a3\6\uffff\1\u0094\1\uffff\1\u00a2"+
            "\1\uffff\1\u00a5\3\uffff\1\u0095\3\uffff\1\u00a4\1\u009d\3\uffff\1\u00a0"+
            "\1\u00a6\1\u00b0\1\u0090\1\uffff\1\u0091\1\u008e\1\u009e\1\uffff\1\u0098"+
            "\4\uffff\1\u009b\7\uffff\1\u008d\1\u0097\4\uffff\1\u00a1\1\uffff\1\u009f"+
            "\1\u00b1\1\u00b2\1\u00ad\1\u0092\2\uffff\1\u00b7\1\u00b6\3\uffff\1\u00ae"+
            "\1\u00b3\1\u00b8\2\uffff\1\u00af\1\u0096",
            "\1\u00c9\5\uffff\1\u00bc\3\uffff\1\u00c6\1\u00c7\1\uffff\1\u00d9\1\u00c0"+
            "\1\u00d6\1\u00d7\1\u00d8\1\u00e2\3\uffff\1\u00b9\2\uffff\1\u00d5\11\uffff"+
            "\1\u00e1\1\uffff\1\u00d4\2\uffff\1\u00d0\6\uffff\1\u00c1\1\uffff\1\u00cf"+
            "\1\uffff\1\u00d2\3\uffff\1\u00c2\3\uffff\1\u00d1\1\u00ca\3\uffff\1\u00cd"+
            "\1\u00d3\1\u00dd\1\u00bd\1\uffff\1\u00be\1\u00bb\1\u00cb\1\uffff\1\u00c5"+
            "\4\uffff\1\u00c8\7\uffff\1\u00ba\1\u00c4\4\uffff\1\u00ce\1\uffff\1\u00cc"+
            "\1\u00de\1\u00df\1\u00da\1\u00bf\2\uffff\1\u00e4\1\u00e3\3\uffff\1\u00db"+
            "\1\u00e0\1\u00e5\2\uffff\1\u00dc\1\u00c3",
            "\1\u00f6\5\uffff\1\u00e9\3\uffff\1\u00f3\1\u00f4\1\uffff\1\u0106\1\u00ed"+
            "\1\u0103\1\u0104\1\u0105\1\u010f\3\uffff\1\u00e6\2\uffff\1\u0102\11\uffff"+
            "\1\u010e\1\uffff\1\u0101\2\uffff\1\u00fd\6\uffff\1\u00ee\1\uffff\1\u00fc"+
            "\1\uffff\1\u00ff\3\uffff\1\u00ef\3\uffff\1\u00fe\1\u00f7\3\uffff\1\u00fa"+
            "\1\u0100\1\u010a\1\u00ea\1\uffff\1\u00eb\1\u00e8\1\u00f8\1\uffff\1\u00f2"+
            "\4\uffff\1\u00f5\7\uffff\1\u00e7\1\u00f1\4\uffff\1\u00fb\1\uffff\1\u00f9"+
            "\1\u010b\1\u010c\1\u0107\1\u00ec\2\uffff\1\u0111\1\u0110\3\uffff\1\u0108"+
            "\1\u010d\1\u0112\2\uffff\1\u0109\1\u00f0",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            ""
    };

    static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
    static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
    static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
    static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
    static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
    static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
        }
    }

    protected class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }
        @Override
        public String getDescription() {
            return "469:1: constructorItem returns [Object node] : (n= scalarExpression |n= aggregateExpression );";
        }
        @Override
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
            int _s = s;
            switch ( s ) {
                    case 0 :
                        int LA30_50 = input.LA(1);

                        int index30_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_50);
                        if ( s>=0 ) return s;
                        break;

                    case 1 :
                        int LA30_51 = input.LA(1);

                        int index30_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_51);
                        if ( s>=0 ) return s;
                        break;

                    case 2 :
                        int LA30_52 = input.LA(1);

                        int index30_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_52);
                        if ( s>=0 ) return s;
                        break;

                    case 3 :
                        int LA30_53 = input.LA(1);

                        int index30_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_53);
                        if ( s>=0 ) return s;
                        break;

                    case 4 :
                        int LA30_54 = input.LA(1);

                        int index30_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_54);
                        if ( s>=0 ) return s;
                        break;

                    case 5 :
                        int LA30_55 = input.LA(1);

                        int index30_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_55);
                        if ( s>=0 ) return s;
                        break;

                    case 6 :
                        int LA30_56 = input.LA(1);

                        int index30_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_56);
                        if ( s>=0 ) return s;
                        break;

                    case 7 :
                        int LA30_57 = input.LA(1);

                        int index30_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_57);
                        if ( s>=0 ) return s;
                        break;

                    case 8 :
                        int LA30_58 = input.LA(1);

                        int index30_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_58);
                        if ( s>=0 ) return s;
                        break;

                    case 9 :
                        int LA30_59 = input.LA(1);

                        int index30_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_59);
                        if ( s>=0 ) return s;
                        break;

                    case 10 :
                        int LA30_60 = input.LA(1);

                        int index30_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_60);
                        if ( s>=0 ) return s;
                        break;

                    case 11 :
                        int LA30_61 = input.LA(1);

                        int index30_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_61);
                        if ( s>=0 ) return s;
                        break;

                    case 12 :
                        int LA30_62 = input.LA(1);

                        int index30_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_62);
                        if ( s>=0 ) return s;
                        break;

                    case 13 :
                        int LA30_63 = input.LA(1);

                        int index30_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_63);
                        if ( s>=0 ) return s;
                        break;

                    case 14 :
                        int LA30_64 = input.LA(1);

                        int index30_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_64);
                        if ( s>=0 ) return s;
                        break;

                    case 15 :
                        int LA30_65 = input.LA(1);

                        int index30_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_65);
                        if ( s>=0 ) return s;
                        break;

                    case 16 :
                        int LA30_66 = input.LA(1);

                        int index30_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_66);
                        if ( s>=0 ) return s;
                        break;

                    case 17 :
                        int LA30_67 = input.LA(1);

                        int index30_67 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_67);
                        if ( s>=0 ) return s;
                        break;

                    case 18 :
                        int LA30_68 = input.LA(1);

                        int index30_68 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_68);
                        if ( s>=0 ) return s;
                        break;

                    case 19 :
                        int LA30_69 = input.LA(1);

                        int index30_69 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_69);
                        if ( s>=0 ) return s;
                        break;

                    case 20 :
                        int LA30_70 = input.LA(1);

                        int index30_70 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_70);
                        if ( s>=0 ) return s;
                        break;

                    case 21 :
                        int LA30_71 = input.LA(1);

                        int index30_71 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_71);
                        if ( s>=0 ) return s;
                        break;

                    case 22 :
                        int LA30_72 = input.LA(1);

                        int index30_72 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_72);
                        if ( s>=0 ) return s;
                        break;

                    case 23 :
                        int LA30_73 = input.LA(1);

                        int index30_73 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_73);
                        if ( s>=0 ) return s;
                        break;

                    case 24 :
                        int LA30_74 = input.LA(1);

                        int index30_74 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_74);
                        if ( s>=0 ) return s;
                        break;

                    case 25 :
                        int LA30_75 = input.LA(1);

                        int index30_75 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_75);
                        if ( s>=0 ) return s;
                        break;

                    case 26 :
                        int LA30_76 = input.LA(1);

                        int index30_76 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_76);
                        if ( s>=0 ) return s;
                        break;

                    case 27 :
                        int LA30_77 = input.LA(1);

                        int index30_77 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_77);
                        if ( s>=0 ) return s;
                        break;

                    case 28 :
                        int LA30_78 = input.LA(1);

                        int index30_78 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_78);
                        if ( s>=0 ) return s;
                        break;

                    case 29 :
                        int LA30_79 = input.LA(1);

                        int index30_79 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_79);
                        if ( s>=0 ) return s;
                        break;

                    case 30 :
                        int LA30_80 = input.LA(1);

                        int index30_80 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_80);
                        if ( s>=0 ) return s;
                        break;

                    case 31 :
                        int LA30_81 = input.LA(1);

                        int index30_81 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_81);
                        if ( s>=0 ) return s;
                        break;

                    case 32 :
                        int LA30_82 = input.LA(1);

                        int index30_82 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_82);
                        if ( s>=0 ) return s;
                        break;

                    case 33 :
                        int LA30_83 = input.LA(1);

                        int index30_83 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_83);
                        if ( s>=0 ) return s;
                        break;

                    case 34 :
                        int LA30_84 = input.LA(1);

                        int index30_84 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_84);
                        if ( s>=0 ) return s;
                        break;

                    case 35 :
                        int LA30_85 = input.LA(1);

                        int index30_85 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_85);
                        if ( s>=0 ) return s;
                        break;

                    case 36 :
                        int LA30_86 = input.LA(1);

                        int index30_86 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_86);
                        if ( s>=0 ) return s;
                        break;

                    case 37 :
                        int LA30_87 = input.LA(1);

                        int index30_87 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_87);
                        if ( s>=0 ) return s;
                        break;

                    case 38 :
                        int LA30_88 = input.LA(1);

                        int index30_88 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_88);
                        if ( s>=0 ) return s;
                        break;

                    case 39 :
                        int LA30_89 = input.LA(1);

                        int index30_89 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_89);
                        if ( s>=0 ) return s;
                        break;

                    case 40 :
                        int LA30_90 = input.LA(1);

                        int index30_90 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_90);
                        if ( s>=0 ) return s;
                        break;

                    case 41 :
                        int LA30_91 = input.LA(1);

                        int index30_91 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_91);
                        if ( s>=0 ) return s;
                        break;

                    case 42 :
                        int LA30_92 = input.LA(1);

                        int index30_92 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_92);
                        if ( s>=0 ) return s;
                        break;

                    case 43 :
                        int LA30_93 = input.LA(1);

                        int index30_93 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_93);
                        if ( s>=0 ) return s;
                        break;

                    case 44 :
                        int LA30_94 = input.LA(1);

                        int index30_94 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_94);
                        if ( s>=0 ) return s;
                        break;

                    case 45 :
                        int LA30_95 = input.LA(1);

                        int index30_95 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_95);
                        if ( s>=0 ) return s;
                        break;

                    case 46 :
                        int LA30_96 = input.LA(1);

                        int index30_96 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_96);
                        if ( s>=0 ) return s;
                        break;

                    case 47 :
                        int LA30_97 = input.LA(1);

                        int index30_97 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_97);
                        if ( s>=0 ) return s;
                        break;

                    case 48 :
                        int LA30_98 = input.LA(1);

                        int index30_98 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_98);
                        if ( s>=0 ) return s;
                        break;

                    case 49 :
                        int LA30_99 = input.LA(1);

                        int index30_99 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_99);
                        if ( s>=0 ) return s;
                        break;

                    case 50 :
                        int LA30_100 = input.LA(1);

                        int index30_100 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_100);
                        if ( s>=0 ) return s;
                        break;

                    case 51 :
                        int LA30_101 = input.LA(1);

                        int index30_101 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_101);
                        if ( s>=0 ) return s;
                        break;

                    case 52 :
                        int LA30_102 = input.LA(1);

                        int index30_102 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_102);
                        if ( s>=0 ) return s;
                        break;

                    case 53 :
                        int LA30_103 = input.LA(1);

                        int index30_103 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_103);
                        if ( s>=0 ) return s;
                        break;

                    case 54 :
                        int LA30_104 = input.LA(1);

                        int index30_104 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_104);
                        if ( s>=0 ) return s;
                        break;

                    case 55 :
                        int LA30_105 = input.LA(1);

                        int index30_105 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_105);
                        if ( s>=0 ) return s;
                        break;

                    case 56 :
                        int LA30_106 = input.LA(1);

                        int index30_106 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_106);
                        if ( s>=0 ) return s;
                        break;

                    case 57 :
                        int LA30_107 = input.LA(1);

                        int index30_107 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_107);
                        if ( s>=0 ) return s;
                        break;

                    case 58 :
                        int LA30_108 = input.LA(1);

                        int index30_108 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_108);
                        if ( s>=0 ) return s;
                        break;

                    case 59 :
                        int LA30_109 = input.LA(1);

                        int index30_109 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_109);
                        if ( s>=0 ) return s;
                        break;

                    case 60 :
                        int LA30_110 = input.LA(1);

                        int index30_110 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_110);
                        if ( s>=0 ) return s;
                        break;

                    case 61 :
                        int LA30_111 = input.LA(1);

                        int index30_111 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_111);
                        if ( s>=0 ) return s;
                        break;

                    case 62 :
                        int LA30_112 = input.LA(1);

                        int index30_112 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_112);
                        if ( s>=0 ) return s;
                        break;

                    case 63 :
                        int LA30_113 = input.LA(1);

                        int index30_113 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_113);
                        if ( s>=0 ) return s;
                        break;

                    case 64 :
                        int LA30_114 = input.LA(1);

                        int index30_114 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_114);
                        if ( s>=0 ) return s;
                        break;

                    case 65 :
                        int LA30_115 = input.LA(1);

                        int index30_115 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_115);
                        if ( s>=0 ) return s;
                        break;

                    case 66 :
                        int LA30_116 = input.LA(1);

                        int index30_116 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_116);
                        if ( s>=0 ) return s;
                        break;

                    case 67 :
                        int LA30_117 = input.LA(1);

                        int index30_117 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_117);
                        if ( s>=0 ) return s;
                        break;

                    case 68 :
                        int LA30_118 = input.LA(1);

                        int index30_118 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_118);
                        if ( s>=0 ) return s;
                        break;

                    case 69 :
                        int LA30_119 = input.LA(1);

                        int index30_119 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_119);
                        if ( s>=0 ) return s;
                        break;

                    case 70 :
                        int LA30_120 = input.LA(1);

                        int index30_120 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_120);
                        if ( s>=0 ) return s;
                        break;

                    case 71 :
                        int LA30_121 = input.LA(1);

                        int index30_121 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_121);
                        if ( s>=0 ) return s;
                        break;

                    case 72 :
                        int LA30_122 = input.LA(1);

                        int index30_122 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_122);
                        if ( s>=0 ) return s;
                        break;

                    case 73 :
                        int LA30_123 = input.LA(1);

                        int index30_123 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_123);
                        if ( s>=0 ) return s;
                        break;

                    case 74 :
                        int LA30_124 = input.LA(1);

                        int index30_124 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_124);
                        if ( s>=0 ) return s;
                        break;

                    case 75 :
                        int LA30_125 = input.LA(1);

                        int index30_125 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_125);
                        if ( s>=0 ) return s;
                        break;

                    case 76 :
                        int LA30_126 = input.LA(1);

                        int index30_126 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_126);
                        if ( s>=0 ) return s;
                        break;

                    case 77 :
                        int LA30_127 = input.LA(1);

                        int index30_127 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_127);
                        if ( s>=0 ) return s;
                        break;

                    case 78 :
                        int LA30_128 = input.LA(1);

                        int index30_128 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_128);
                        if ( s>=0 ) return s;
                        break;

                    case 79 :
                        int LA30_129 = input.LA(1);

                        int index30_129 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_129);
                        if ( s>=0 ) return s;
                        break;

                    case 80 :
                        int LA30_130 = input.LA(1);

                        int index30_130 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_130);
                        if ( s>=0 ) return s;
                        break;

                    case 81 :
                        int LA30_131 = input.LA(1);

                        int index30_131 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_131);
                        if ( s>=0 ) return s;
                        break;

                    case 82 :
                        int LA30_132 = input.LA(1);

                        int index30_132 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_132);
                        if ( s>=0 ) return s;
                        break;

                    case 83 :
                        int LA30_133 = input.LA(1);

                        int index30_133 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_133);
                        if ( s>=0 ) return s;
                        break;

                    case 84 :
                        int LA30_134 = input.LA(1);

                        int index30_134 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_134);
                        if ( s>=0 ) return s;
                        break;

                    case 85 :
                        int LA30_135 = input.LA(1);

                        int index30_135 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_135);
                        if ( s>=0 ) return s;
                        break;

                    case 86 :
                        int LA30_136 = input.LA(1);

                        int index30_136 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_136);
                        if ( s>=0 ) return s;
                        break;

                    case 87 :
                        int LA30_137 = input.LA(1);

                        int index30_137 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_137);
                        if ( s>=0 ) return s;
                        break;

                    case 88 :
                        int LA30_138 = input.LA(1);

                        int index30_138 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_138);
                        if ( s>=0 ) return s;
                        break;

                    case 89 :
                        int LA30_139 = input.LA(1);

                        int index30_139 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_139);
                        if ( s>=0 ) return s;
                        break;

                    case 90 :
                        int LA30_140 = input.LA(1);

                        int index30_140 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_140);
                        if ( s>=0 ) return s;
                        break;

                    case 91 :
                        int LA30_141 = input.LA(1);

                        int index30_141 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_141);
                        if ( s>=0 ) return s;
                        break;

                    case 92 :
                        int LA30_142 = input.LA(1);

                        int index30_142 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_142);
                        if ( s>=0 ) return s;
                        break;

                    case 93 :
                        int LA30_143 = input.LA(1);

                        int index30_143 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_143);
                        if ( s>=0 ) return s;
                        break;

                    case 94 :
                        int LA30_144 = input.LA(1);

                        int index30_144 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_144);
                        if ( s>=0 ) return s;
                        break;

                    case 95 :
                        int LA30_145 = input.LA(1);

                        int index30_145 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_145);
                        if ( s>=0 ) return s;
                        break;

                    case 96 :
                        int LA30_146 = input.LA(1);

                        int index30_146 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_146);
                        if ( s>=0 ) return s;
                        break;

                    case 97 :
                        int LA30_147 = input.LA(1);

                        int index30_147 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_147);
                        if ( s>=0 ) return s;
                        break;

                    case 98 :
                        int LA30_148 = input.LA(1);

                        int index30_148 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_148);
                        if ( s>=0 ) return s;
                        break;

                    case 99 :
                        int LA30_149 = input.LA(1);

                        int index30_149 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_149);
                        if ( s>=0 ) return s;
                        break;

                    case 100 :
                        int LA30_150 = input.LA(1);

                        int index30_150 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_150);
                        if ( s>=0 ) return s;
                        break;

                    case 101 :
                        int LA30_151 = input.LA(1);

                        int index30_151 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_151);
                        if ( s>=0 ) return s;
                        break;

                    case 102 :
                        int LA30_152 = input.LA(1);

                        int index30_152 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_152);
                        if ( s>=0 ) return s;
                        break;

                    case 103 :
                        int LA30_153 = input.LA(1);

                        int index30_153 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_153);
                        if ( s>=0 ) return s;
                        break;

                    case 104 :
                        int LA30_154 = input.LA(1);

                        int index30_154 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_154);
                        if ( s>=0 ) return s;
                        break;

                    case 105 :
                        int LA30_155 = input.LA(1);

                        int index30_155 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_155);
                        if ( s>=0 ) return s;
                        break;

                    case 106 :
                        int LA30_156 = input.LA(1);

                        int index30_156 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_156);
                        if ( s>=0 ) return s;
                        break;

                    case 107 :
                        int LA30_157 = input.LA(1);

                        int index30_157 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_157);
                        if ( s>=0 ) return s;
                        break;

                    case 108 :
                        int LA30_158 = input.LA(1);

                        int index30_158 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_158);
                        if ( s>=0 ) return s;
                        break;

                    case 109 :
                        int LA30_159 = input.LA(1);

                        int index30_159 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_159);
                        if ( s>=0 ) return s;
                        break;

                    case 110 :
                        int LA30_160 = input.LA(1);

                        int index30_160 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_160);
                        if ( s>=0 ) return s;
                        break;

                    case 111 :
                        int LA30_161 = input.LA(1);

                        int index30_161 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_161);
                        if ( s>=0 ) return s;
                        break;

                    case 112 :
                        int LA30_162 = input.LA(1);

                        int index30_162 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_162);
                        if ( s>=0 ) return s;
                        break;

                    case 113 :
                        int LA30_163 = input.LA(1);

                        int index30_163 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_163);
                        if ( s>=0 ) return s;
                        break;

                    case 114 :
                        int LA30_164 = input.LA(1);

                        int index30_164 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_164);
                        if ( s>=0 ) return s;
                        break;

                    case 115 :
                        int LA30_165 = input.LA(1);

                        int index30_165 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_165);
                        if ( s>=0 ) return s;
                        break;

                    case 116 :
                        int LA30_166 = input.LA(1);

                        int index30_166 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_166);
                        if ( s>=0 ) return s;
                        break;

                    case 117 :
                        int LA30_167 = input.LA(1);

                        int index30_167 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_167);
                        if ( s>=0 ) return s;
                        break;

                    case 118 :
                        int LA30_168 = input.LA(1);

                        int index30_168 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_168);
                        if ( s>=0 ) return s;
                        break;

                    case 119 :
                        int LA30_169 = input.LA(1);

                        int index30_169 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_169);
                        if ( s>=0 ) return s;
                        break;

                    case 120 :
                        int LA30_170 = input.LA(1);

                        int index30_170 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_170);
                        if ( s>=0 ) return s;
                        break;

                    case 121 :
                        int LA30_171 = input.LA(1);

                        int index30_171 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_171);
                        if ( s>=0 ) return s;
                        break;

                    case 122 :
                        int LA30_172 = input.LA(1);

                        int index30_172 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_172);
                        if ( s>=0 ) return s;
                        break;

                    case 123 :
                        int LA30_173 = input.LA(1);

                        int index30_173 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_173);
                        if ( s>=0 ) return s;
                        break;

                    case 124 :
                        int LA30_174 = input.LA(1);

                        int index30_174 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_174);
                        if ( s>=0 ) return s;
                        break;

                    case 125 :
                        int LA30_175 = input.LA(1);

                        int index30_175 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_175);
                        if ( s>=0 ) return s;
                        break;

                    case 126 :
                        int LA30_176 = input.LA(1);

                        int index30_176 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_176);
                        if ( s>=0 ) return s;
                        break;

                    case 127 :
                        int LA30_177 = input.LA(1);

                        int index30_177 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_177);
                        if ( s>=0 ) return s;
                        break;

                    case 128 :
                        int LA30_178 = input.LA(1);

                        int index30_178 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_178);
                        if ( s>=0 ) return s;
                        break;

                    case 129 :
                        int LA30_179 = input.LA(1);

                        int index30_179 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_179);
                        if ( s>=0 ) return s;
                        break;

                    case 130 :
                        int LA30_180 = input.LA(1);

                        int index30_180 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_180);
                        if ( s>=0 ) return s;
                        break;

                    case 131 :
                        int LA30_181 = input.LA(1);

                        int index30_181 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_181);
                        if ( s>=0 ) return s;
                        break;

                    case 132 :
                        int LA30_182 = input.LA(1);

                        int index30_182 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_182);
                        if ( s>=0 ) return s;
                        break;

                    case 133 :
                        int LA30_183 = input.LA(1);

                        int index30_183 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_183);
                        if ( s>=0 ) return s;
                        break;

                    case 134 :
                        int LA30_184 = input.LA(1);

                        int index30_184 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_184);
                        if ( s>=0 ) return s;
                        break;

                    case 135 :
                        int LA30_185 = input.LA(1);

                        int index30_185 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_185);
                        if ( s>=0 ) return s;
                        break;

                    case 136 :
                        int LA30_186 = input.LA(1);

                        int index30_186 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_186);
                        if ( s>=0 ) return s;
                        break;

                    case 137 :
                        int LA30_187 = input.LA(1);

                        int index30_187 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_187);
                        if ( s>=0 ) return s;
                        break;

                    case 138 :
                        int LA30_188 = input.LA(1);

                        int index30_188 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_188);
                        if ( s>=0 ) return s;
                        break;

                    case 139 :
                        int LA30_189 = input.LA(1);

                        int index30_189 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_189);
                        if ( s>=0 ) return s;
                        break;

                    case 140 :
                        int LA30_190 = input.LA(1);

                        int index30_190 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_190);
                        if ( s>=0 ) return s;
                        break;

                    case 141 :
                        int LA30_191 = input.LA(1);

                        int index30_191 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_191);
                        if ( s>=0 ) return s;
                        break;

                    case 142 :
                        int LA30_192 = input.LA(1);

                        int index30_192 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_192);
                        if ( s>=0 ) return s;
                        break;

                    case 143 :
                        int LA30_193 = input.LA(1);

                        int index30_193 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_193);
                        if ( s>=0 ) return s;
                        break;

                    case 144 :
                        int LA30_194 = input.LA(1);

                        int index30_194 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_194);
                        if ( s>=0 ) return s;
                        break;

                    case 145 :
                        int LA30_195 = input.LA(1);

                        int index30_195 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_195);
                        if ( s>=0 ) return s;
                        break;

                    case 146 :
                        int LA30_196 = input.LA(1);

                        int index30_196 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_196);
                        if ( s>=0 ) return s;
                        break;

                    case 147 :
                        int LA30_197 = input.LA(1);

                        int index30_197 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_197);
                        if ( s>=0 ) return s;
                        break;

                    case 148 :
                        int LA30_198 = input.LA(1);

                        int index30_198 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_198);
                        if ( s>=0 ) return s;
                        break;

                    case 149 :
                        int LA30_199 = input.LA(1);

                        int index30_199 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_199);
                        if ( s>=0 ) return s;
                        break;

                    case 150 :
                        int LA30_200 = input.LA(1);

                        int index30_200 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_200);
                        if ( s>=0 ) return s;
                        break;

                    case 151 :
                        int LA30_201 = input.LA(1);

                        int index30_201 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_201);
                        if ( s>=0 ) return s;
                        break;

                    case 152 :
                        int LA30_202 = input.LA(1);

                        int index30_202 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_202);
                        if ( s>=0 ) return s;
                        break;

                    case 153 :
                        int LA30_203 = input.LA(1);

                        int index30_203 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_203);
                        if ( s>=0 ) return s;
                        break;

                    case 154 :
                        int LA30_204 = input.LA(1);

                        int index30_204 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_204);
                        if ( s>=0 ) return s;
                        break;

                    case 155 :
                        int LA30_205 = input.LA(1);

                        int index30_205 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_205);
                        if ( s>=0 ) return s;
                        break;

                    case 156 :
                        int LA30_206 = input.LA(1);

                        int index30_206 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_206);
                        if ( s>=0 ) return s;
                        break;

                    case 157 :
                        int LA30_207 = input.LA(1);

                        int index30_207 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_207);
                        if ( s>=0 ) return s;
                        break;

                    case 158 :
                        int LA30_208 = input.LA(1);

                        int index30_208 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_208);
                        if ( s>=0 ) return s;
                        break;

                    case 159 :
                        int LA30_209 = input.LA(1);

                        int index30_209 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_209);
                        if ( s>=0 ) return s;
                        break;

                    case 160 :
                        int LA30_210 = input.LA(1);

                        int index30_210 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_210);
                        if ( s>=0 ) return s;
                        break;

                    case 161 :
                        int LA30_211 = input.LA(1);

                        int index30_211 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_211);
                        if ( s>=0 ) return s;
                        break;

                    case 162 :
                        int LA30_212 = input.LA(1);

                        int index30_212 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_212);
                        if ( s>=0 ) return s;
                        break;

                    case 163 :
                        int LA30_213 = input.LA(1);

                        int index30_213 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_213);
                        if ( s>=0 ) return s;
                        break;

                    case 164 :
                        int LA30_214 = input.LA(1);

                        int index30_214 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_214);
                        if ( s>=0 ) return s;
                        break;

                    case 165 :
                        int LA30_215 = input.LA(1);

                        int index30_215 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_215);
                        if ( s>=0 ) return s;
                        break;

                    case 166 :
                        int LA30_216 = input.LA(1);

                        int index30_216 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_216);
                        if ( s>=0 ) return s;
                        break;

                    case 167 :
                        int LA30_217 = input.LA(1);

                        int index30_217 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_217);
                        if ( s>=0 ) return s;
                        break;

                    case 168 :
                        int LA30_218 = input.LA(1);

                        int index30_218 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_218);
                        if ( s>=0 ) return s;
                        break;

                    case 169 :
                        int LA30_219 = input.LA(1);

                        int index30_219 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_219);
                        if ( s>=0 ) return s;
                        break;

                    case 170 :
                        int LA30_220 = input.LA(1);

                        int index30_220 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_220);
                        if ( s>=0 ) return s;
                        break;

                    case 171 :
                        int LA30_221 = input.LA(1);

                        int index30_221 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_221);
                        if ( s>=0 ) return s;
                        break;

                    case 172 :
                        int LA30_222 = input.LA(1);

                        int index30_222 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_222);
                        if ( s>=0 ) return s;
                        break;

                    case 173 :
                        int LA30_223 = input.LA(1);

                        int index30_223 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_223);
                        if ( s>=0 ) return s;
                        break;

                    case 174 :
                        int LA30_224 = input.LA(1);

                        int index30_224 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_224);
                        if ( s>=0 ) return s;
                        break;

                    case 175 :
                        int LA30_225 = input.LA(1);

                        int index30_225 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_225);
                        if ( s>=0 ) return s;
                        break;

                    case 176 :
                        int LA30_226 = input.LA(1);

                        int index30_226 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_226);
                        if ( s>=0 ) return s;
                        break;

                    case 177 :
                        int LA30_227 = input.LA(1);

                        int index30_227 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_227);
                        if ( s>=0 ) return s;
                        break;

                    case 178 :
                        int LA30_228 = input.LA(1);

                        int index30_228 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_228);
                        if ( s>=0 ) return s;
                        break;

                    case 179 :
                        int LA30_229 = input.LA(1);

                        int index30_229 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_229);
                        if ( s>=0 ) return s;
                        break;

                    case 180 :
                        int LA30_230 = input.LA(1);

                        int index30_230 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_230);
                        if ( s>=0 ) return s;
                        break;

                    case 181 :
                        int LA30_231 = input.LA(1);

                        int index30_231 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_231);
                        if ( s>=0 ) return s;
                        break;

                    case 182 :
                        int LA30_232 = input.LA(1);

                        int index30_232 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_232);
                        if ( s>=0 ) return s;
                        break;

                    case 183 :
                        int LA30_233 = input.LA(1);

                        int index30_233 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_233);
                        if ( s>=0 ) return s;
                        break;

                    case 184 :
                        int LA30_234 = input.LA(1);

                        int index30_234 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_234);
                        if ( s>=0 ) return s;
                        break;

                    case 185 :
                        int LA30_235 = input.LA(1);

                        int index30_235 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_235);
                        if ( s>=0 ) return s;
                        break;

                    case 186 :
                        int LA30_236 = input.LA(1);

                        int index30_236 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_236);
                        if ( s>=0 ) return s;
                        break;

                    case 187 :
                        int LA30_237 = input.LA(1);

                        int index30_237 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_237);
                        if ( s>=0 ) return s;
                        break;

                    case 188 :
                        int LA30_238 = input.LA(1);

                        int index30_238 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_238);
                        if ( s>=0 ) return s;
                        break;

                    case 189 :
                        int LA30_239 = input.LA(1);

                        int index30_239 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_239);
                        if ( s>=0 ) return s;
                        break;

                    case 190 :
                        int LA30_240 = input.LA(1);

                        int index30_240 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_240);
                        if ( s>=0 ) return s;
                        break;

                    case 191 :
                        int LA30_241 = input.LA(1);

                        int index30_241 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_241);
                        if ( s>=0 ) return s;
                        break;

                    case 192 :
                        int LA30_242 = input.LA(1);

                        int index30_242 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_242);
                        if ( s>=0 ) return s;
                        break;

                    case 193 :
                        int LA30_243 = input.LA(1);

                        int index30_243 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_243);
                        if ( s>=0 ) return s;
                        break;

                    case 194 :
                        int LA30_244 = input.LA(1);

                        int index30_244 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_244);
                        if ( s>=0 ) return s;
                        break;

                    case 195 :
                        int LA30_245 = input.LA(1);

                        int index30_245 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_245);
                        if ( s>=0 ) return s;
                        break;

                    case 196 :
                        int LA30_246 = input.LA(1);

                        int index30_246 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_246);
                        if ( s>=0 ) return s;
                        break;

                    case 197 :
                        int LA30_247 = input.LA(1);

                        int index30_247 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_247);
                        if ( s>=0 ) return s;
                        break;

                    case 198 :
                        int LA30_248 = input.LA(1);

                        int index30_248 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_248);
                        if ( s>=0 ) return s;
                        break;

                    case 199 :
                        int LA30_249 = input.LA(1);

                        int index30_249 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_249);
                        if ( s>=0 ) return s;
                        break;

                    case 200 :
                        int LA30_250 = input.LA(1);

                        int index30_250 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_250);
                        if ( s>=0 ) return s;
                        break;

                    case 201 :
                        int LA30_251 = input.LA(1);

                        int index30_251 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_251);
                        if ( s>=0 ) return s;
                        break;

                    case 202 :
                        int LA30_252 = input.LA(1);

                        int index30_252 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_252);
                        if ( s>=0 ) return s;
                        break;

                    case 203 :
                        int LA30_253 = input.LA(1);

                        int index30_253 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_253);
                        if ( s>=0 ) return s;
                        break;

                    case 204 :
                        int LA30_254 = input.LA(1);

                        int index30_254 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_254);
                        if ( s>=0 ) return s;
                        break;

                    case 205 :
                        int LA30_255 = input.LA(1);

                        int index30_255 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_255);
                        if ( s>=0 ) return s;
                        break;

                    case 206 :
                        int LA30_256 = input.LA(1);

                        int index30_256 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_256);
                        if ( s>=0 ) return s;
                        break;

                    case 207 :
                        int LA30_257 = input.LA(1);

                        int index30_257 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_257);
                        if ( s>=0 ) return s;
                        break;

                    case 208 :
                        int LA30_258 = input.LA(1);

                        int index30_258 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_258);
                        if ( s>=0 ) return s;
                        break;

                    case 209 :
                        int LA30_259 = input.LA(1);

                        int index30_259 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_259);
                        if ( s>=0 ) return s;
                        break;

                    case 210 :
                        int LA30_260 = input.LA(1);

                        int index30_260 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_260);
                        if ( s>=0 ) return s;
                        break;

                    case 211 :
                        int LA30_261 = input.LA(1);

                        int index30_261 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_261);
                        if ( s>=0 ) return s;
                        break;

                    case 212 :
                        int LA30_262 = input.LA(1);

                        int index30_262 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_262);
                        if ( s>=0 ) return s;
                        break;

                    case 213 :
                        int LA30_263 = input.LA(1);

                        int index30_263 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_263);
                        if ( s>=0 ) return s;
                        break;

                    case 214 :
                        int LA30_264 = input.LA(1);

                        int index30_264 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_264);
                        if ( s>=0 ) return s;
                        break;

                    case 215 :
                        int LA30_265 = input.LA(1);

                        int index30_265 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_265);
                        if ( s>=0 ) return s;
                        break;

                    case 216 :
                        int LA30_266 = input.LA(1);

                        int index30_266 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_266);
                        if ( s>=0 ) return s;
                        break;

                    case 217 :
                        int LA30_267 = input.LA(1);

                        int index30_267 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_267);
                        if ( s>=0 ) return s;
                        break;

                    case 218 :
                        int LA30_268 = input.LA(1);

                        int index30_268 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_268);
                        if ( s>=0 ) return s;
                        break;

                    case 219 :
                        int LA30_269 = input.LA(1);

                        int index30_269 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_269);
                        if ( s>=0 ) return s;
                        break;

                    case 220 :
                        int LA30_270 = input.LA(1);

                        int index30_270 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_270);
                        if ( s>=0 ) return s;
                        break;

                    case 221 :
                        int LA30_271 = input.LA(1);

                        int index30_271 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_271);
                        if ( s>=0 ) return s;
                        break;

                    case 222 :
                        int LA30_272 = input.LA(1);

                        int index30_272 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_272);
                        if ( s>=0 ) return s;
                        break;

                    case 223 :
                        int LA30_273 = input.LA(1);

                        int index30_273 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_273);
                        if ( s>=0 ) return s;
                        break;

                    case 224 :
                        int LA30_274 = input.LA(1);

                        int index30_274 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (( aggregatesAllowed() )) ) {s = 1;}
                        else if ( (true) ) {s = 275;}

                        input.seek(index30_274);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 30, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    static final String DFA47_eotS =
        "\u014d\uffff";
    static final String DFA47_eofS =
        "\u014d\uffff";
    static final String DFA47_minS =
        "\2\4\54\uffff\3\4\5\77\1\13\2\77\2\13\1\63\12\77\4\13\23\uffff\34\0\22"+
        "\uffff\76\0\15\uffff\6\0\15\uffff\4\0\15\uffff\23\0\15\uffff\4\0\15\uffff"+
        "\4\0\15\uffff\4\0\15\uffff";
    static final String DFA47_maxS =
        "\2\164\54\uffff\3\164\5\77\1\135\2\77\2\135\1\165\12\77\4\135\23\uffff"+
        "\34\0\22\uffff\76\0\15\uffff\6\0\15\uffff\4\0\15\uffff\23\0\15\uffff\4"+
        "\0\15\uffff\4\0\15\uffff\4\0\15\uffff";
    static final String DFA47_acceptS =
        "\2\uffff\1\2\52\uffff\1\1\34\uffff\21\1\1\uffff\1\1\34\uffff\22\1\76\uffff"+
        "\14\1\7\uffff\14\1\5\uffff\14\1\24\uffff\14\1\5\uffff\14\1\5\uffff\14"+
        "\1\5\uffff\14\1\1\uffff";
    static final String DFA47_specialS =
        "\1\uffff\1\0\54\uffff\1\1\7\uffff\1\2\2\uffff\1\3\1\4\13\uffff\1\5\1\6"+
        "\1\7\1\10\23\uffff\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1"+
        "\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1"+
        "\41\1\42\1\43\1\44\22\uffff\1\45\1\46\1\47\1\50\1\51\1\52\1\53\1\54\1"+
        "\55\1\56\1\57\1\60\1\61\1\62\1\63\1\64\1\65\1\66\1\67\1\70\1\71\1\72\1"+
        "\73\1\74\1\75\1\76\1\77\1\100\1\101\1\102\1\103\1\104\1\105\1\106\1\107"+
        "\1\110\1\111\1\112\1\113\1\114\1\115\1\116\1\117\1\120\1\121\1\122\1\123"+
        "\1\124\1\125\1\126\1\127\1\130\1\131\1\132\1\133\1\134\1\135\1\136\1\137"+
        "\1\140\1\141\1\142\15\uffff\1\143\1\144\1\145\1\146\1\147\1\150\15\uffff"+
        "\1\151\1\152\1\153\1\154\15\uffff\1\155\1\156\1\157\1\160\1\161\1\162"+
        "\1\163\1\164\1\165\1\166\1\167\1\170\1\171\1\172\1\173\1\174\1\175\1\176"+
        "\1\177\15\uffff\1\u0080\1\u0081\1\u0082\1\u0083\15\uffff\1\u0084\1\u0085"+
        "\1\u0086\1\u0087\15\uffff\1\u0088\1\u0089\1\u008a\1\u008b\15\uffff}>";
    static final String[] DFA47_transitionS = {
            "\1\2\5\uffff\1\2\3\uffff\2\2\1\uffff\6\2\6\uffff\1\2\11\uffff\1\2\1\uffff"+
            "\1\2\2\uffff\1\2\6\uffff\1\2\1\uffff\1\2\1\uffff\1\2\3\uffff\1\2\3\uffff"+
            "\1\1\1\2\3\uffff\4\2\1\uffff\3\2\1\uffff\1\2\4\uffff\1\2\7\uffff\2\2"+
            "\4\uffff\1\2\1\uffff\5\2\2\uffff\2\2\3\uffff\3\2\2\uffff\2\2",
            "\1\76\5\uffff\1\61\3\uffff\1\73\1\74\1\uffff\1\115\1\65\1\112\1\113"+
            "\1\114\1\126\6\uffff\1\111\7\uffff\1\132\1\uffff\1\125\1\uffff\1\110"+
            "\2\uffff\1\105\6\uffff\1\66\1\uffff\1\104\1\uffff\1\106\3\uffff\1\67"+
            "\3\uffff\1\56\1\77\3\uffff\1\102\1\107\1\121\1\62\1\uffff\1\63\1\60\1"+
            "\100\1\uffff\1\72\1\uffff\1\55\2\uffff\1\75\7\uffff\1\57\1\71\2\uffff"+
            "\1\2\1\uffff\1\103\1\uffff\1\101\1\122\1\123\1\116\1\64\2\uffff\1\130"+
            "\1\127\3\uffff\1\117\1\124\1\131\2\uffff\1\120\1\70",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\155\5\uffff\1\140\3\uffff\1\152\1\153\1\uffff\1\174\1\144\1\171\1"+
            "\172\1\173\1\u0085\6\uffff\1\170\7\uffff\1\u0089\1\uffff\1\u0084\1\uffff"+
            "\1\167\2\uffff\1\164\6\uffff\1\145\1\uffff\1\163\1\uffff\1\165\3\uffff"+
            "\1\146\3\uffff\1\135\1\156\3\uffff\1\161\1\166\1\u0080\1\141\1\uffff"+
            "\1\142\1\137\1\157\1\uffff\1\151\1\uffff\1\134\2\uffff\1\154\7\uffff"+
            "\1\136\1\150\2\uffff\1\u008a\1\uffff\1\162\1\uffff\1\160\1\u0081\1\u0082"+
            "\1\175\1\143\2\uffff\1\u0087\1\u0086\3\uffff\1\176\1\u0083\1\u0088\2"+
            "\uffff\1\177\1\147",
            "\1\u0098\5\uffff\1\u008b\3\uffff\1\u0095\1\u0096\2\uffff\1\u008f\12"+
            "\uffff\1\u00a4\13\uffff\1\u00a3\2\uffff\1\u009f\6\uffff\1\u0090\1\uffff"+
            "\1\u009e\1\uffff\1\u00a1\3\uffff\1\u0091\3\uffff\1\u00a0\1\u0099\3\uffff"+
            "\1\u009c\1\u00a2\1\uffff\1\u008c\1\uffff\1\u008d\1\uffff\1\u009a\1\uffff"+
            "\1\u0094\4\uffff\1\u0097\10\uffff\1\u0093\4\uffff\1\u009d\1\uffff\1\u009b"+
            "\3\uffff\1\u008e\15\uffff\1\u0092",
            "\1\u00b2\5\uffff\1\u00a5\3\uffff\1\u00af\1\u00b0\2\uffff\1\u00a9\12"+
            "\uffff\1\u00be\13\uffff\1\u00bd\2\uffff\1\u00b9\6\uffff\1\u00aa\1\uffff"+
            "\1\u00b8\1\uffff\1\u00bb\3\uffff\1\u00ab\3\uffff\1\u00ba\1\u00b3\3\uffff"+
            "\1\u00b6\1\u00bc\1\uffff\1\u00a6\1\uffff\1\u00a7\1\uffff\1\u00b4\1\uffff"+
            "\1\u00ae\4\uffff\1\u00b1\10\uffff\1\u00ad\4\uffff\1\u00b7\1\uffff\1\u00b5"+
            "\3\uffff\1\u00a8\15\uffff\1\u00ac",
            "\1\u00bf",
            "\1\u00c0",
            "\1\u00c1",
            "\1\u00c2",
            "\1\u00c3",
            "\1\u00d0\17\uffff\1\u00c6\1\u00c4\6\uffff\1\u00c9\11\uffff\1\u00cb\1"+
            "\u00cc\5\uffff\1\u00d2\4\uffff\1\u00d4\7\uffff\1\u00cd\1\u00ce\1\u00d1"+
            "\4\uffff\1\u00d3\1\uffff\1\u00c8\1\uffff\1\u00c5\2\uffff\1\u00cf\1\u00ca"+
            "\11\uffff\1\u00c7\2\uffff\1\2",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00e3\17\uffff\1\u00d9\7\uffff\1\u00dc\11\uffff\1\u00de\1\u00df\5"+
            "\uffff\1\u00e5\4\uffff\1\u00e7\7\uffff\1\u00e0\1\u00e1\1\u00e4\4\uffff"+
            "\1\u00e6\1\uffff\1\u00db\1\uffff\1\u00d8\2\uffff\1\u00e2\1\u00dd\11\uffff"+
            "\1\u00da\2\uffff\1\2",
            "\1\u00f4\17\uffff\1\u00ea\7\uffff\1\u00ed\11\uffff\1\u00ef\1\u00f0\5"+
            "\uffff\1\u00f6\4\uffff\1\u00f8\7\uffff\1\u00f1\1\u00f2\1\u00f5\4\uffff"+
            "\1\u00f7\1\uffff\1\u00ec\1\uffff\1\u00e9\2\uffff\1\u00f3\1\u00ee\11\uffff"+
            "\1\u00eb\2\uffff\1\2",
            "\1\u00fa\7\uffff\1\u00fb\64\uffff\1\u00fd\3\uffff\1\u00fc\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "\1\u0101",
            "\1\u0102",
            "\1\u0103",
            "\1\u0104",
            "\1\u0105",
            "\1\u0106",
            "\1\u0107",
            "\1\u0108",
            "\1\u0114\17\uffff\1\u010a\7\uffff\1\u010d\11\uffff\1\u010f\1\u0110\5"+
            "\uffff\1\u0116\4\uffff\1\u0118\7\uffff\1\u0111\1\u0112\1\u0115\4\uffff"+
            "\1\u0117\1\uffff\1\u010c\1\uffff\1\u0109\2\uffff\1\u0113\1\u010e\11\uffff"+
            "\1\u010b\2\uffff\1\2",
            "\1\u0125\17\uffff\1\u011b\7\uffff\1\u011e\11\uffff\1\u0120\1\u0121\5"+
            "\uffff\1\u0127\4\uffff\1\u0129\7\uffff\1\u0122\1\u0123\1\u0126\4\uffff"+
            "\1\u0128\1\uffff\1\u011d\1\uffff\1\u011a\2\uffff\1\u0124\1\u011f\11\uffff"+
            "\1\u011c\2\uffff\1\2",
            "\1\u0136\17\uffff\1\u012c\7\uffff\1\u012f\11\uffff\1\u0131\1\u0132\5"+
            "\uffff\1\u0138\4\uffff\1\u013a\7\uffff\1\u0133\1\u0134\1\u0137\4\uffff"+
            "\1\u0139\1\uffff\1\u012e\1\uffff\1\u012b\2\uffff\1\u0135\1\u0130\11\uffff"+
            "\1\u012d\2\uffff\1\2",
            "\1\u0147\17\uffff\1\u013d\7\uffff\1\u0140\11\uffff\1\u0142\1\u0143\5"+
            "\uffff\1\u0149\4\uffff\1\u014b\7\uffff\1\u0144\1\u0145\1\u0148\4\uffff"+
            "\1\u014a\1\uffff\1\u013f\1\uffff\1\u013c\2\uffff\1\u0146\1\u0141\11\uffff"+
            "\1\u013e\2\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA47_eot = DFA.unpackEncodedString(DFA47_eotS);
    static final short[] DFA47_eof = DFA.unpackEncodedString(DFA47_eofS);
    static final char[] DFA47_min = DFA.unpackEncodedStringToUnsignedChars(DFA47_minS);
    static final char[] DFA47_max = DFA.unpackEncodedStringToUnsignedChars(DFA47_maxS);
    static final short[] DFA47_accept = DFA.unpackEncodedString(DFA47_acceptS);
    static final short[] DFA47_special = DFA.unpackEncodedString(DFA47_specialS);
    static final short[][] DFA47_transition;

    static {
        int numStates = DFA47_transitionS.length;
        DFA47_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA47_transition[i] = DFA.unpackEncodedString(DFA47_transitionS[i]);
        }
    }

    protected class DFA47 extends DFA {

        public DFA47(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 47;
            this.eot = DFA47_eot;
            this.eof = DFA47_eof;
            this.min = DFA47_min;
            this.max = DFA47_max;
            this.accept = DFA47_accept;
            this.special = DFA47_special;
            this.transition = DFA47_transition;
        }
        @Override
        public String getDescription() {
            return "663:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET |n= simpleConditionalExpression );";
        }
        @Override
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
            int _s = s;
            switch ( s ) {
                    case 0 :
                        int LA47_1 = input.LA(1);

                        int index47_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_1==NOT) && (synpred1_JPQL())) {s = 45;}
                        else if ( (LA47_1==LEFT_ROUND_BRACKET) ) {s = 46;}
                        else if ( (LA47_1==PLUS) ) {s = 47;}
                        else if ( (LA47_1==MINUS) ) {s = 48;}
                        else if ( (LA47_1==AVG) ) {s = 49;}
                        else if ( (LA47_1==MAX) ) {s = 50;}
                        else if ( (LA47_1==MIN) ) {s = 51;}
                        else if ( (LA47_1==SUM) ) {s = 52;}
                        else if ( (LA47_1==COUNT) ) {s = 53;}
                        else if ( (LA47_1==IDENT) ) {s = 54;}
                        else if ( (LA47_1==KEY) ) {s = 55;}
                        else if ( (LA47_1==VALUE) ) {s = 56;}
                        else if ( (LA47_1==POSITIONAL_PARAM) ) {s = 57;}
                        else if ( (LA47_1==NAMED_PARAM) ) {s = 58;}
                        else if ( (LA47_1==CASE) ) {s = 59;}
                        else if ( (LA47_1==COALESCE) ) {s = 60;}
                        else if ( (LA47_1==NULLIF) ) {s = 61;}
                        else if ( (LA47_1==ABS) ) {s = 62;}
                        else if ( (LA47_1==LENGTH) ) {s = 63;}
                        else if ( (LA47_1==MOD) ) {s = 64;}
                        else if ( (LA47_1==SQRT) ) {s = 65;}
                        else if ( (LA47_1==LOCATE) ) {s = 66;}
                        else if ( (LA47_1==SIZE) ) {s = 67;}
                        else if ( (LA47_1==INDEX) ) {s = 68;}
                        else if ( (LA47_1==FUNC) ) {s = 69;}
                        else if ( (LA47_1==INTEGER_LITERAL) ) {s = 70;}
                        else if ( (LA47_1==LONG_LITERAL) ) {s = 71;}
                        else if ( (LA47_1==FLOAT_LITERAL) ) {s = 72;}
                        else if ( (LA47_1==DOUBLE_LITERAL) ) {s = 73;}
                        else if ( (LA47_1==CURRENT_DATE) && (synpred1_JPQL())) {s = 74;}
                        else if ( (LA47_1==CURRENT_TIME) && (synpred1_JPQL())) {s = 75;}
                        else if ( (LA47_1==CURRENT_TIMESTAMP) && (synpred1_JPQL())) {s = 76;}
                        else if ( (LA47_1==CONCAT) && (synpred1_JPQL())) {s = 77;}
                        else if ( (LA47_1==SUBSTRING) && (synpred1_JPQL())) {s = 78;}
                        else if ( (LA47_1==TRIM) && (synpred1_JPQL())) {s = 79;}
                        else if ( (LA47_1==UPPER) && (synpred1_JPQL())) {s = 80;}
                        else if ( (LA47_1==LOWER) && (synpred1_JPQL())) {s = 81;}
                        else if ( (LA47_1==STRING_LITERAL_DOUBLE_QUOTED) && (synpred1_JPQL())) {s = 82;}
                        else if ( (LA47_1==STRING_LITERAL_SINGLE_QUOTED) && (synpred1_JPQL())) {s = 83;}
                        else if ( (LA47_1==TRUE) && (synpred1_JPQL())) {s = 84;}
                        else if ( (LA47_1==FALSE) && (synpred1_JPQL())) {s = 85;}
                        else if ( (LA47_1==DATE_LITERAL) && (synpred1_JPQL())) {s = 86;}
                        else if ( (LA47_1==TIME_LITERAL) && (synpred1_JPQL())) {s = 87;}
                        else if ( (LA47_1==TIMESTAMP_LITERAL) && (synpred1_JPQL())) {s = 88;}
                        else if ( (LA47_1==TYPE) && (synpred1_JPQL())) {s = 89;}
                        else if ( (LA47_1==EXISTS) && (synpred1_JPQL())) {s = 90;}
                        else if ( (LA47_1==SELECT) ) {s = 2;}

                        input.seek(index47_1);
                        if ( s>=0 ) return s;
                        break;

                    case 1 :
                        int LA47_46 = input.LA(1);

                        int index47_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_46==NOT) && (synpred1_JPQL())) {s = 92;}
                        else if ( (LA47_46==LEFT_ROUND_BRACKET) ) {s = 93;}
                        else if ( (LA47_46==PLUS) ) {s = 94;}
                        else if ( (LA47_46==MINUS) ) {s = 95;}
                        else if ( (LA47_46==AVG) ) {s = 96;}
                        else if ( (LA47_46==MAX) ) {s = 97;}
                        else if ( (LA47_46==MIN) ) {s = 98;}
                        else if ( (LA47_46==SUM) ) {s = 99;}
                        else if ( (LA47_46==COUNT) ) {s = 100;}
                        else if ( (LA47_46==IDENT) ) {s = 101;}
                        else if ( (LA47_46==KEY) ) {s = 102;}
                        else if ( (LA47_46==VALUE) ) {s = 103;}
                        else if ( (LA47_46==POSITIONAL_PARAM) ) {s = 104;}
                        else if ( (LA47_46==NAMED_PARAM) ) {s = 105;}
                        else if ( (LA47_46==CASE) ) {s = 106;}
                        else if ( (LA47_46==COALESCE) ) {s = 107;}
                        else if ( (LA47_46==NULLIF) ) {s = 108;}
                        else if ( (LA47_46==ABS) ) {s = 109;}
                        else if ( (LA47_46==LENGTH) ) {s = 110;}
                        else if ( (LA47_46==MOD) ) {s = 111;}
                        else if ( (LA47_46==SQRT) ) {s = 112;}
                        else if ( (LA47_46==LOCATE) ) {s = 113;}
                        else if ( (LA47_46==SIZE) ) {s = 114;}
                        else if ( (LA47_46==INDEX) ) {s = 115;}
                        else if ( (LA47_46==FUNC) ) {s = 116;}
                        else if ( (LA47_46==INTEGER_LITERAL) ) {s = 117;}
                        else if ( (LA47_46==LONG_LITERAL) ) {s = 118;}
                        else if ( (LA47_46==FLOAT_LITERAL) ) {s = 119;}
                        else if ( (LA47_46==DOUBLE_LITERAL) ) {s = 120;}
                        else if ( (LA47_46==CURRENT_DATE) && (synpred1_JPQL())) {s = 121;}
                        else if ( (LA47_46==CURRENT_TIME) && (synpred1_JPQL())) {s = 122;}
                        else if ( (LA47_46==CURRENT_TIMESTAMP) && (synpred1_JPQL())) {s = 123;}
                        else if ( (LA47_46==CONCAT) && (synpred1_JPQL())) {s = 124;}
                        else if ( (LA47_46==SUBSTRING) && (synpred1_JPQL())) {s = 125;}
                        else if ( (LA47_46==TRIM) && (synpred1_JPQL())) {s = 126;}
                        else if ( (LA47_46==UPPER) && (synpred1_JPQL())) {s = 127;}
                        else if ( (LA47_46==LOWER) && (synpred1_JPQL())) {s = 128;}
                        else if ( (LA47_46==STRING_LITERAL_DOUBLE_QUOTED) && (synpred1_JPQL())) {s = 129;}
                        else if ( (LA47_46==STRING_LITERAL_SINGLE_QUOTED) && (synpred1_JPQL())) {s = 130;}
                        else if ( (LA47_46==TRUE) && (synpred1_JPQL())) {s = 131;}
                        else if ( (LA47_46==FALSE) && (synpred1_JPQL())) {s = 132;}
                        else if ( (LA47_46==DATE_LITERAL) && (synpred1_JPQL())) {s = 133;}
                        else if ( (LA47_46==TIME_LITERAL) && (synpred1_JPQL())) {s = 134;}
                        else if ( (LA47_46==TIMESTAMP_LITERAL) && (synpred1_JPQL())) {s = 135;}
                        else if ( (LA47_46==TYPE) && (synpred1_JPQL())) {s = 136;}
                        else if ( (LA47_46==EXISTS) && (synpred1_JPQL())) {s = 137;}
                        else if ( (LA47_46==SELECT) && (synpred1_JPQL())) {s = 138;}

                        input.seek(index47_46);
                        if ( s>=0 ) return s;
                        break;

                    case 2 :
                        int LA47_54 = input.LA(1);

                        int index47_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_54==DOT) ) {s = 196;}
                        else if ( (LA47_54==MULTIPLY) ) {s = 197;}
                        else if ( (LA47_54==DIVIDE) ) {s = 198;}
                        else if ( (LA47_54==PLUS) ) {s = 199;}
                        else if ( (LA47_54==MINUS) ) {s = 200;}
                        else if ( (LA47_54==EQUALS) && (synpred1_JPQL())) {s = 201;}
                        else if ( (LA47_54==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 202;}
                        else if ( (LA47_54==GREATER_THAN) && (synpred1_JPQL())) {s = 203;}
                        else if ( (LA47_54==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 204;}
                        else if ( (LA47_54==LESS_THAN) && (synpred1_JPQL())) {s = 205;}
                        else if ( (LA47_54==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 206;}
                        else if ( (LA47_54==NOT) && (synpred1_JPQL())) {s = 207;}
                        else if ( (LA47_54==BETWEEN) && (synpred1_JPQL())) {s = 208;}
                        else if ( (LA47_54==LIKE) && (synpred1_JPQL())) {s = 209;}
                        else if ( (LA47_54==IN) && (synpred1_JPQL())) {s = 210;}
                        else if ( (LA47_54==MEMBER) && (synpred1_JPQL())) {s = 211;}
                        else if ( (LA47_54==IS) && (synpred1_JPQL())) {s = 212;}
                        else if ( (LA47_54==RIGHT_ROUND_BRACKET) ) {s = 2;}

                        input.seek(index47_54);
                        if ( s>=0 ) return s;
                        break;

                    case 3 :
                        int LA47_57 = input.LA(1);

                        int index47_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_57==MULTIPLY) ) {s = 216;}
                        else if ( (LA47_57==DIVIDE) ) {s = 217;}
                        else if ( (LA47_57==PLUS) ) {s = 218;}
                        else if ( (LA47_57==MINUS) ) {s = 219;}
                        else if ( (LA47_57==EQUALS) && (synpred1_JPQL())) {s = 220;}
                        else if ( (LA47_57==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 221;}
                        else if ( (LA47_57==GREATER_THAN) && (synpred1_JPQL())) {s = 222;}
                        else if ( (LA47_57==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 223;}
                        else if ( (LA47_57==LESS_THAN) && (synpred1_JPQL())) {s = 224;}
                        else if ( (LA47_57==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 225;}
                        else if ( (LA47_57==NOT) && (synpred1_JPQL())) {s = 226;}
                        else if ( (LA47_57==BETWEEN) && (synpred1_JPQL())) {s = 227;}
                        else if ( (LA47_57==LIKE) && (synpred1_JPQL())) {s = 228;}
                        else if ( (LA47_57==IN) && (synpred1_JPQL())) {s = 229;}
                        else if ( (LA47_57==MEMBER) && (synpred1_JPQL())) {s = 230;}
                        else if ( (LA47_57==IS) && (synpred1_JPQL())) {s = 231;}
                        else if ( (LA47_57==RIGHT_ROUND_BRACKET) ) {s = 2;}

                        input.seek(index47_57);
                        if ( s>=0 ) return s;
                        break;

                    case 4 :
                        int LA47_58 = input.LA(1);

                        int index47_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_58==MULTIPLY) ) {s = 233;}
                        else if ( (LA47_58==DIVIDE) ) {s = 234;}
                        else if ( (LA47_58==PLUS) ) {s = 235;}
                        else if ( (LA47_58==MINUS) ) {s = 236;}
                        else if ( (LA47_58==EQUALS) && (synpred1_JPQL())) {s = 237;}
                        else if ( (LA47_58==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 238;}
                        else if ( (LA47_58==GREATER_THAN) && (synpred1_JPQL())) {s = 239;}
                        else if ( (LA47_58==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 240;}
                        else if ( (LA47_58==LESS_THAN) && (synpred1_JPQL())) {s = 241;}
                        else if ( (LA47_58==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 242;}
                        else if ( (LA47_58==NOT) && (synpred1_JPQL())) {s = 243;}
                        else if ( (LA47_58==BETWEEN) && (synpred1_JPQL())) {s = 244;}
                        else if ( (LA47_58==LIKE) && (synpred1_JPQL())) {s = 245;}
                        else if ( (LA47_58==IN) && (synpred1_JPQL())) {s = 246;}
                        else if ( (LA47_58==MEMBER) && (synpred1_JPQL())) {s = 247;}
                        else if ( (LA47_58==IS) && (synpred1_JPQL())) {s = 248;}
                        else if ( (LA47_58==RIGHT_ROUND_BRACKET) ) {s = 2;}

                        input.seek(index47_58);
                        if ( s>=0 ) return s;
                        break;

                    case 5 :
                        int LA47_70 = input.LA(1);

                        int index47_70 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_70==MULTIPLY) ) {s = 265;}
                        else if ( (LA47_70==DIVIDE) ) {s = 266;}
                        else if ( (LA47_70==PLUS) ) {s = 267;}
                        else if ( (LA47_70==MINUS) ) {s = 268;}
                        else if ( (LA47_70==EQUALS) && (synpred1_JPQL())) {s = 269;}
                        else if ( (LA47_70==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 270;}
                        else if ( (LA47_70==GREATER_THAN) && (synpred1_JPQL())) {s = 271;}
                        else if ( (LA47_70==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 272;}
                        else if ( (LA47_70==LESS_THAN) && (synpred1_JPQL())) {s = 273;}
                        else if ( (LA47_70==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 274;}
                        else if ( (LA47_70==NOT) && (synpred1_JPQL())) {s = 275;}
                        else if ( (LA47_70==BETWEEN) && (synpred1_JPQL())) {s = 276;}
                        else if ( (LA47_70==LIKE) && (synpred1_JPQL())) {s = 277;}
                        else if ( (LA47_70==IN) && (synpred1_JPQL())) {s = 278;}
                        else if ( (LA47_70==MEMBER) && (synpred1_JPQL())) {s = 279;}
                        else if ( (LA47_70==IS) && (synpred1_JPQL())) {s = 280;}
                        else if ( (LA47_70==RIGHT_ROUND_BRACKET) ) {s = 2;}

                        input.seek(index47_70);
                        if ( s>=0 ) return s;
                        break;

                    case 6 :
                        int LA47_71 = input.LA(1);

                        int index47_71 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_71==MULTIPLY) ) {s = 282;}
                        else if ( (LA47_71==DIVIDE) ) {s = 283;}
                        else if ( (LA47_71==PLUS) ) {s = 284;}
                        else if ( (LA47_71==MINUS) ) {s = 285;}
                        else if ( (LA47_71==EQUALS) && (synpred1_JPQL())) {s = 286;}
                        else if ( (LA47_71==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 287;}
                        else if ( (LA47_71==GREATER_THAN) && (synpred1_JPQL())) {s = 288;}
                        else if ( (LA47_71==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 289;}
                        else if ( (LA47_71==LESS_THAN) && (synpred1_JPQL())) {s = 290;}
                        else if ( (LA47_71==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 291;}
                        else if ( (LA47_71==NOT) && (synpred1_JPQL())) {s = 292;}
                        else if ( (LA47_71==BETWEEN) && (synpred1_JPQL())) {s = 293;}
                        else if ( (LA47_71==LIKE) && (synpred1_JPQL())) {s = 294;}
                        else if ( (LA47_71==IN) && (synpred1_JPQL())) {s = 295;}
                        else if ( (LA47_71==MEMBER) && (synpred1_JPQL())) {s = 296;}
                        else if ( (LA47_71==IS) && (synpred1_JPQL())) {s = 297;}
                        else if ( (LA47_71==RIGHT_ROUND_BRACKET) ) {s = 2;}

                        input.seek(index47_71);
                        if ( s>=0 ) return s;
                        break;

                    case 7 :
                        int LA47_72 = input.LA(1);

                        int index47_72 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_72==MULTIPLY) ) {s = 299;}
                        else if ( (LA47_72==DIVIDE) ) {s = 300;}
                        else if ( (LA47_72==PLUS) ) {s = 301;}
                        else if ( (LA47_72==MINUS) ) {s = 302;}
                        else if ( (LA47_72==EQUALS) && (synpred1_JPQL())) {s = 303;}
                        else if ( (LA47_72==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 304;}
                        else if ( (LA47_72==GREATER_THAN) && (synpred1_JPQL())) {s = 305;}
                        else if ( (LA47_72==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 306;}
                        else if ( (LA47_72==LESS_THAN) && (synpred1_JPQL())) {s = 307;}
                        else if ( (LA47_72==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 308;}
                        else if ( (LA47_72==NOT) && (synpred1_JPQL())) {s = 309;}
                        else if ( (LA47_72==BETWEEN) && (synpred1_JPQL())) {s = 310;}
                        else if ( (LA47_72==LIKE) && (synpred1_JPQL())) {s = 311;}
                        else if ( (LA47_72==IN) && (synpred1_JPQL())) {s = 312;}
                        else if ( (LA47_72==MEMBER) && (synpred1_JPQL())) {s = 313;}
                        else if ( (LA47_72==IS) && (synpred1_JPQL())) {s = 314;}
                        else if ( (LA47_72==RIGHT_ROUND_BRACKET) ) {s = 2;}

                        input.seek(index47_72);
                        if ( s>=0 ) return s;
                        break;

                    case 8 :
                        int LA47_73 = input.LA(1);

                        int index47_73 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_73==MULTIPLY) ) {s = 316;}
                        else if ( (LA47_73==DIVIDE) ) {s = 317;}
                        else if ( (LA47_73==PLUS) ) {s = 318;}
                        else if ( (LA47_73==MINUS) ) {s = 319;}
                        else if ( (LA47_73==EQUALS) && (synpred1_JPQL())) {s = 320;}
                        else if ( (LA47_73==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 321;}
                        else if ( (LA47_73==GREATER_THAN) && (synpred1_JPQL())) {s = 322;}
                        else if ( (LA47_73==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 323;}
                        else if ( (LA47_73==LESS_THAN) && (synpred1_JPQL())) {s = 324;}
                        else if ( (LA47_73==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 325;}
                        else if ( (LA47_73==NOT) && (synpred1_JPQL())) {s = 326;}
                        else if ( (LA47_73==BETWEEN) && (synpred1_JPQL())) {s = 327;}
                        else if ( (LA47_73==LIKE) && (synpred1_JPQL())) {s = 328;}
                        else if ( (LA47_73==IN) && (synpred1_JPQL())) {s = 329;}
                        else if ( (LA47_73==MEMBER) && (synpred1_JPQL())) {s = 330;}
                        else if ( (LA47_73==IS) && (synpred1_JPQL())) {s = 331;}
                        else if ( (LA47_73==RIGHT_ROUND_BRACKET) ) {s = 2;}

                        input.seek(index47_73);
                        if ( s>=0 ) return s;
                        break;

                    case 9 :
                        int LA47_93 = input.LA(1);

                        int index47_93 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_93);
                        if ( s>=0 ) return s;
                        break;

                    case 10 :
                        int LA47_94 = input.LA(1);

                        int index47_94 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_94);
                        if ( s>=0 ) return s;
                        break;

                    case 11 :
                        int LA47_95 = input.LA(1);

                        int index47_95 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_95);
                        if ( s>=0 ) return s;
                        break;

                    case 12 :
                        int LA47_96 = input.LA(1);

                        int index47_96 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_96);
                        if ( s>=0 ) return s;
                        break;

                    case 13 :
                        int LA47_97 = input.LA(1);

                        int index47_97 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_97);
                        if ( s>=0 ) return s;
                        break;

                    case 14 :
                        int LA47_98 = input.LA(1);

                        int index47_98 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_98);
                        if ( s>=0 ) return s;
                        break;

                    case 15 :
                        int LA47_99 = input.LA(1);

                        int index47_99 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_99);
                        if ( s>=0 ) return s;
                        break;

                    case 16 :
                        int LA47_100 = input.LA(1);

                        int index47_100 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_100);
                        if ( s>=0 ) return s;
                        break;

                    case 17 :
                        int LA47_101 = input.LA(1);

                        int index47_101 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_101);
                        if ( s>=0 ) return s;
                        break;

                    case 18 :
                        int LA47_102 = input.LA(1);

                        int index47_102 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_102);
                        if ( s>=0 ) return s;
                        break;

                    case 19 :
                        int LA47_103 = input.LA(1);

                        int index47_103 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_103);
                        if ( s>=0 ) return s;
                        break;

                    case 20 :
                        int LA47_104 = input.LA(1);

                        int index47_104 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_104);
                        if ( s>=0 ) return s;
                        break;

                    case 21 :
                        int LA47_105 = input.LA(1);

                        int index47_105 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_105);
                        if ( s>=0 ) return s;
                        break;

                    case 22 :
                        int LA47_106 = input.LA(1);

                        int index47_106 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_106);
                        if ( s>=0 ) return s;
                        break;

                    case 23 :
                        int LA47_107 = input.LA(1);

                        int index47_107 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_107);
                        if ( s>=0 ) return s;
                        break;

                    case 24 :
                        int LA47_108 = input.LA(1);

                        int index47_108 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_108);
                        if ( s>=0 ) return s;
                        break;

                    case 25 :
                        int LA47_109 = input.LA(1);

                        int index47_109 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_109);
                        if ( s>=0 ) return s;
                        break;

                    case 26 :
                        int LA47_110 = input.LA(1);

                        int index47_110 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_110);
                        if ( s>=0 ) return s;
                        break;

                    case 27 :
                        int LA47_111 = input.LA(1);

                        int index47_111 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_111);
                        if ( s>=0 ) return s;
                        break;

                    case 28 :
                        int LA47_112 = input.LA(1);

                        int index47_112 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_112);
                        if ( s>=0 ) return s;
                        break;

                    case 29 :
                        int LA47_113 = input.LA(1);

                        int index47_113 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_113);
                        if ( s>=0 ) return s;
                        break;

                    case 30 :
                        int LA47_114 = input.LA(1);

                        int index47_114 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_114);
                        if ( s>=0 ) return s;
                        break;

                    case 31 :
                        int LA47_115 = input.LA(1);

                        int index47_115 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_115);
                        if ( s>=0 ) return s;
                        break;

                    case 32 :
                        int LA47_116 = input.LA(1);

                        int index47_116 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_116);
                        if ( s>=0 ) return s;
                        break;

                    case 33 :
                        int LA47_117 = input.LA(1);

                        int index47_117 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_117);
                        if ( s>=0 ) return s;
                        break;

                    case 34 :
                        int LA47_118 = input.LA(1);

                        int index47_118 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_118);
                        if ( s>=0 ) return s;
                        break;

                    case 35 :
                        int LA47_119 = input.LA(1);

                        int index47_119 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_119);
                        if ( s>=0 ) return s;
                        break;

                    case 36 :
                        int LA47_120 = input.LA(1);

                        int index47_120 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_120);
                        if ( s>=0 ) return s;
                        break;

                    case 37 :
                        int LA47_139 = input.LA(1);

                        int index47_139 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_139);
                        if ( s>=0 ) return s;
                        break;

                    case 38 :
                        int LA47_140 = input.LA(1);

                        int index47_140 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_140);
                        if ( s>=0 ) return s;
                        break;

                    case 39 :
                        int LA47_141 = input.LA(1);

                        int index47_141 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_141);
                        if ( s>=0 ) return s;
                        break;

                    case 40 :
                        int LA47_142 = input.LA(1);

                        int index47_142 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_142);
                        if ( s>=0 ) return s;
                        break;

                    case 41 :
                        int LA47_143 = input.LA(1);

                        int index47_143 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_143);
                        if ( s>=0 ) return s;
                        break;

                    case 42 :
                        int LA47_144 = input.LA(1);

                        int index47_144 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_144);
                        if ( s>=0 ) return s;
                        break;

                    case 43 :
                        int LA47_145 = input.LA(1);

                        int index47_145 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_145);
                        if ( s>=0 ) return s;
                        break;

                    case 44 :
                        int LA47_146 = input.LA(1);

                        int index47_146 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_146);
                        if ( s>=0 ) return s;
                        break;

                    case 45 :
                        int LA47_147 = input.LA(1);

                        int index47_147 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_147);
                        if ( s>=0 ) return s;
                        break;

                    case 46 :
                        int LA47_148 = input.LA(1);

                        int index47_148 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_148);
                        if ( s>=0 ) return s;
                        break;

                    case 47 :
                        int LA47_149 = input.LA(1);

                        int index47_149 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_149);
                        if ( s>=0 ) return s;
                        break;

                    case 48 :
                        int LA47_150 = input.LA(1);

                        int index47_150 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_150);
                        if ( s>=0 ) return s;
                        break;

                    case 49 :
                        int LA47_151 = input.LA(1);

                        int index47_151 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_151);
                        if ( s>=0 ) return s;
                        break;

                    case 50 :
                        int LA47_152 = input.LA(1);

                        int index47_152 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_152);
                        if ( s>=0 ) return s;
                        break;

                    case 51 :
                        int LA47_153 = input.LA(1);

                        int index47_153 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_153);
                        if ( s>=0 ) return s;
                        break;

                    case 52 :
                        int LA47_154 = input.LA(1);

                        int index47_154 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_154);
                        if ( s>=0 ) return s;
                        break;

                    case 53 :
                        int LA47_155 = input.LA(1);

                        int index47_155 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_155);
                        if ( s>=0 ) return s;
                        break;

                    case 54 :
                        int LA47_156 = input.LA(1);

                        int index47_156 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_156);
                        if ( s>=0 ) return s;
                        break;

                    case 55 :
                        int LA47_157 = input.LA(1);

                        int index47_157 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_157);
                        if ( s>=0 ) return s;
                        break;

                    case 56 :
                        int LA47_158 = input.LA(1);

                        int index47_158 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_158);
                        if ( s>=0 ) return s;
                        break;

                    case 57 :
                        int LA47_159 = input.LA(1);

                        int index47_159 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_159);
                        if ( s>=0 ) return s;
                        break;

                    case 58 :
                        int LA47_160 = input.LA(1);

                        int index47_160 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_160);
                        if ( s>=0 ) return s;
                        break;

                    case 59 :
                        int LA47_161 = input.LA(1);

                        int index47_161 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_161);
                        if ( s>=0 ) return s;
                        break;

                    case 60 :
                        int LA47_162 = input.LA(1);

                        int index47_162 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_162);
                        if ( s>=0 ) return s;
                        break;

                    case 61 :
                        int LA47_163 = input.LA(1);

                        int index47_163 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_163);
                        if ( s>=0 ) return s;
                        break;

                    case 62 :
                        int LA47_164 = input.LA(1);

                        int index47_164 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_164);
                        if ( s>=0 ) return s;
                        break;

                    case 63 :
                        int LA47_165 = input.LA(1);

                        int index47_165 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_165);
                        if ( s>=0 ) return s;
                        break;

                    case 64 :
                        int LA47_166 = input.LA(1);

                        int index47_166 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_166);
                        if ( s>=0 ) return s;
                        break;

                    case 65 :
                        int LA47_167 = input.LA(1);

                        int index47_167 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_167);
                        if ( s>=0 ) return s;
                        break;

                    case 66 :
                        int LA47_168 = input.LA(1);

                        int index47_168 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_168);
                        if ( s>=0 ) return s;
                        break;

                    case 67 :
                        int LA47_169 = input.LA(1);

                        int index47_169 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_169);
                        if ( s>=0 ) return s;
                        break;

                    case 68 :
                        int LA47_170 = input.LA(1);

                        int index47_170 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_170);
                        if ( s>=0 ) return s;
                        break;

                    case 69 :
                        int LA47_171 = input.LA(1);

                        int index47_171 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_171);
                        if ( s>=0 ) return s;
                        break;

                    case 70 :
                        int LA47_172 = input.LA(1);

                        int index47_172 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_172);
                        if ( s>=0 ) return s;
                        break;

                    case 71 :
                        int LA47_173 = input.LA(1);

                        int index47_173 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_173);
                        if ( s>=0 ) return s;
                        break;

                    case 72 :
                        int LA47_174 = input.LA(1);

                        int index47_174 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_174);
                        if ( s>=0 ) return s;
                        break;

                    case 73 :
                        int LA47_175 = input.LA(1);

                        int index47_175 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_175);
                        if ( s>=0 ) return s;
                        break;

                    case 74 :
                        int LA47_176 = input.LA(1);

                        int index47_176 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_176);
                        if ( s>=0 ) return s;
                        break;

                    case 75 :
                        int LA47_177 = input.LA(1);

                        int index47_177 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_177);
                        if ( s>=0 ) return s;
                        break;

                    case 76 :
                        int LA47_178 = input.LA(1);

                        int index47_178 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_178);
                        if ( s>=0 ) return s;
                        break;

                    case 77 :
                        int LA47_179 = input.LA(1);

                        int index47_179 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_179);
                        if ( s>=0 ) return s;
                        break;

                    case 78 :
                        int LA47_180 = input.LA(1);

                        int index47_180 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_180);
                        if ( s>=0 ) return s;
                        break;

                    case 79 :
                        int LA47_181 = input.LA(1);

                        int index47_181 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_181);
                        if ( s>=0 ) return s;
                        break;

                    case 80 :
                        int LA47_182 = input.LA(1);

                        int index47_182 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_182);
                        if ( s>=0 ) return s;
                        break;

                    case 81 :
                        int LA47_183 = input.LA(1);

                        int index47_183 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_183);
                        if ( s>=0 ) return s;
                        break;

                    case 82 :
                        int LA47_184 = input.LA(1);

                        int index47_184 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_184);
                        if ( s>=0 ) return s;
                        break;

                    case 83 :
                        int LA47_185 = input.LA(1);

                        int index47_185 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_185);
                        if ( s>=0 ) return s;
                        break;

                    case 84 :
                        int LA47_186 = input.LA(1);

                        int index47_186 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_186);
                        if ( s>=0 ) return s;
                        break;

                    case 85 :
                        int LA47_187 = input.LA(1);

                        int index47_187 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_187);
                        if ( s>=0 ) return s;
                        break;

                    case 86 :
                        int LA47_188 = input.LA(1);

                        int index47_188 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_188);
                        if ( s>=0 ) return s;
                        break;

                    case 87 :
                        int LA47_189 = input.LA(1);

                        int index47_189 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_189);
                        if ( s>=0 ) return s;
                        break;

                    case 88 :
                        int LA47_190 = input.LA(1);

                        int index47_190 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_190);
                        if ( s>=0 ) return s;
                        break;

                    case 89 :
                        int LA47_191 = input.LA(1);

                        int index47_191 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_191);
                        if ( s>=0 ) return s;
                        break;

                    case 90 :
                        int LA47_192 = input.LA(1);

                        int index47_192 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_192);
                        if ( s>=0 ) return s;
                        break;

                    case 91 :
                        int LA47_193 = input.LA(1);

                        int index47_193 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_193);
                        if ( s>=0 ) return s;
                        break;

                    case 92 :
                        int LA47_194 = input.LA(1);

                        int index47_194 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_194);
                        if ( s>=0 ) return s;
                        break;

                    case 93 :
                        int LA47_195 = input.LA(1);

                        int index47_195 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_195);
                        if ( s>=0 ) return s;
                        break;

                    case 94 :
                        int LA47_196 = input.LA(1);

                        int index47_196 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_196);
                        if ( s>=0 ) return s;
                        break;

                    case 95 :
                        int LA47_197 = input.LA(1);

                        int index47_197 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_197);
                        if ( s>=0 ) return s;
                        break;

                    case 96 :
                        int LA47_198 = input.LA(1);

                        int index47_198 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_198);
                        if ( s>=0 ) return s;
                        break;

                    case 97 :
                        int LA47_199 = input.LA(1);

                        int index47_199 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_199);
                        if ( s>=0 ) return s;
                        break;

                    case 98 :
                        int LA47_200 = input.LA(1);

                        int index47_200 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_200);
                        if ( s>=0 ) return s;
                        break;

                    case 99 :
                        int LA47_214 = input.LA(1);

                        int index47_214 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_214);
                        if ( s>=0 ) return s;
                        break;

                    case 100 :
                        int LA47_215 = input.LA(1);

                        int index47_215 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_215);
                        if ( s>=0 ) return s;
                        break;

                    case 101 :
                        int LA47_216 = input.LA(1);

                        int index47_216 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_216);
                        if ( s>=0 ) return s;
                        break;

                    case 102 :
                        int LA47_217 = input.LA(1);

                        int index47_217 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_217);
                        if ( s>=0 ) return s;
                        break;

                    case 103 :
                        int LA47_218 = input.LA(1);

                        int index47_218 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_218);
                        if ( s>=0 ) return s;
                        break;

                    case 104 :
                        int LA47_219 = input.LA(1);

                        int index47_219 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_219);
                        if ( s>=0 ) return s;
                        break;

                    case 105 :
                        int LA47_233 = input.LA(1);

                        int index47_233 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_233);
                        if ( s>=0 ) return s;
                        break;

                    case 106 :
                        int LA47_234 = input.LA(1);

                        int index47_234 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_234);
                        if ( s>=0 ) return s;
                        break;

                    case 107 :
                        int LA47_235 = input.LA(1);

                        int index47_235 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_235);
                        if ( s>=0 ) return s;
                        break;

                    case 108 :
                        int LA47_236 = input.LA(1);

                        int index47_236 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_236);
                        if ( s>=0 ) return s;
                        break;

                    case 109 :
                        int LA47_250 = input.LA(1);

                        int index47_250 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_250);
                        if ( s>=0 ) return s;
                        break;

                    case 110 :
                        int LA47_251 = input.LA(1);

                        int index47_251 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_251);
                        if ( s>=0 ) return s;
                        break;

                    case 111 :
                        int LA47_252 = input.LA(1);

                        int index47_252 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_252);
                        if ( s>=0 ) return s;
                        break;

                    case 112 :
                        int LA47_253 = input.LA(1);

                        int index47_253 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_253);
                        if ( s>=0 ) return s;
                        break;

                    case 113 :
                        int LA47_254 = input.LA(1);

                        int index47_254 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_254);
                        if ( s>=0 ) return s;
                        break;

                    case 114 :
                        int LA47_255 = input.LA(1);

                        int index47_255 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_255);
                        if ( s>=0 ) return s;
                        break;

                    case 115 :
                        int LA47_256 = input.LA(1);

                        int index47_256 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_256);
                        if ( s>=0 ) return s;
                        break;

                    case 116 :
                        int LA47_257 = input.LA(1);

                        int index47_257 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_257);
                        if ( s>=0 ) return s;
                        break;

                    case 117 :
                        int LA47_258 = input.LA(1);

                        int index47_258 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_258);
                        if ( s>=0 ) return s;
                        break;

                    case 118 :
                        int LA47_259 = input.LA(1);

                        int index47_259 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_259);
                        if ( s>=0 ) return s;
                        break;

                    case 119 :
                        int LA47_260 = input.LA(1);

                        int index47_260 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_260);
                        if ( s>=0 ) return s;
                        break;

                    case 120 :
                        int LA47_261 = input.LA(1);

                        int index47_261 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_261);
                        if ( s>=0 ) return s;
                        break;

                    case 121 :
                        int LA47_262 = input.LA(1);

                        int index47_262 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_262);
                        if ( s>=0 ) return s;
                        break;

                    case 122 :
                        int LA47_263 = input.LA(1);

                        int index47_263 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_263);
                        if ( s>=0 ) return s;
                        break;

                    case 123 :
                        int LA47_264 = input.LA(1);

                        int index47_264 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_264);
                        if ( s>=0 ) return s;
                        break;

                    case 124 :
                        int LA47_265 = input.LA(1);

                        int index47_265 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_265);
                        if ( s>=0 ) return s;
                        break;

                    case 125 :
                        int LA47_266 = input.LA(1);

                        int index47_266 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_266);
                        if ( s>=0 ) return s;
                        break;

                    case 126 :
                        int LA47_267 = input.LA(1);

                        int index47_267 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_267);
                        if ( s>=0 ) return s;
                        break;

                    case 127 :
                        int LA47_268 = input.LA(1);

                        int index47_268 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_268);
                        if ( s>=0 ) return s;
                        break;

                    case 128 :
                        int LA47_282 = input.LA(1);

                        int index47_282 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_282);
                        if ( s>=0 ) return s;
                        break;

                    case 129 :
                        int LA47_283 = input.LA(1);

                        int index47_283 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_283);
                        if ( s>=0 ) return s;
                        break;

                    case 130 :
                        int LA47_284 = input.LA(1);

                        int index47_284 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_284);
                        if ( s>=0 ) return s;
                        break;

                    case 131 :
                        int LA47_285 = input.LA(1);

                        int index47_285 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_285);
                        if ( s>=0 ) return s;
                        break;

                    case 132 :
                        int LA47_299 = input.LA(1);

                        int index47_299 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_299);
                        if ( s>=0 ) return s;
                        break;

                    case 133 :
                        int LA47_300 = input.LA(1);

                        int index47_300 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_300);
                        if ( s>=0 ) return s;
                        break;

                    case 134 :
                        int LA47_301 = input.LA(1);

                        int index47_301 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_301);
                        if ( s>=0 ) return s;
                        break;

                    case 135 :
                        int LA47_302 = input.LA(1);

                        int index47_302 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_302);
                        if ( s>=0 ) return s;
                        break;

                    case 136 :
                        int LA47_316 = input.LA(1);

                        int index47_316 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_316);
                        if ( s>=0 ) return s;
                        break;

                    case 137 :
                        int LA47_317 = input.LA(1);

                        int index47_317 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_317);
                        if ( s>=0 ) return s;
                        break;

                    case 138 :
                        int LA47_318 = input.LA(1);

                        int index47_318 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_318);
                        if ( s>=0 ) return s;
                        break;

                    case 139 :
                        int LA47_319 = input.LA(1);

                        int index47_319 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 331;}
                        else if ( (true) ) {s = 2;}

                        input.seek(index47_319);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 47, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    public static final BitSet FOLLOW_selectStatement_in_document763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateStatement_in_document777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteStatement_in_document791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectClause_in_selectStatement824 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_fromClause_in_selectStatement839 = new BitSet(new long[]{0x0001800000000000L,0x0040000001000000L});
    public static final BitSet FOLLOW_whereClause_in_selectStatement854 = new BitSet(new long[]{0x0001800000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_groupByClause_in_selectStatement869 = new BitSet(new long[]{0x0001000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_havingClause_in_selectStatement885 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_orderByClause_in_selectStatement900 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_selectStatement910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateClause_in_updateStatement952 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_setClause_in_updateStatement967 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_whereClause_in_updateStatement981 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_updateStatement991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPDATE_in_updateClause1023 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_updateClause1029 = new BitSet(new long[]{0x0008000000000102L});
    public static final BitSet FOLLOW_AS_in_updateClause1041 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_IDENT_in_updateClause1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_setClause1095 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1101 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_COMMA_in_setClause1114 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1120 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_setAssignmentTarget_in_setAssignmentClause1178 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_EQUALS_in_setAssignmentClause1182 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C062EF1L});
    public static final BitSet FOLLOW_newValue_in_setAssignmentClause1188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_setAssignmentTarget1218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_setAssignmentTarget1233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalarExpression_in_newValue1265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_newValue1279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteClause_in_deleteStatement1321 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_whereClause_in_deleteStatement1334 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_deleteStatement1344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_deleteClause1377 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_FROM_in_deleteClause1379 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_deleteClause1385 = new BitSet(new long[]{0x0008000000000102L});
    public static final BitSet FOLLOW_AS_in_deleteClause1397 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_IDENT_in_deleteClause1403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_selectClause1450 = new BitSet(new long[]{0x88A81284247EC410L,0x0019C67D0C146EF1L});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause1453 = new BitSet(new long[]{0x88A81284207EC410L,0x0019C67D0C146EF1L});
    public static final BitSet FOLLOW_selectItem_in_selectClause1471 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_COMMA_in_selectClause1497 = new BitSet(new long[]{0x88A81284207EC410L,0x0019C67D0C146EF1L});
    public static final BitSet FOLLOW_selectItem_in_selectClause1503 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_selectExpression_in_selectItem1570 = new BitSet(new long[]{0x0008000000000102L});
    public static final BitSet FOLLOW_AS_in_selectItem1574 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_IDENT_in_selectItem1582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_selectExpression1625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalarExpression_in_selectExpression1639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBJECT_in_selectExpression1649 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1651 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_selectExpression1657 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constructorExpression_in_selectExpression1674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntryExpression_in_selectExpression1689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_in_mapEntryExpression1721 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1723 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1729 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1763 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_DOT_in_pathExprOrVariableAccess1778 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExprOrVariableAccess1784 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KEY_in_qualifiedIdentificationVariable1853 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1855 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1861 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUE_in_qualifiedIdentificationVariable1878 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1880 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1886 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AVG_in_aggregateExpression1921 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1923 = new BitSet(new long[]{0x88A81280247EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1926 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_aggregateExpression1944 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAX_in_aggregateExpression1966 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1968 = new BitSet(new long[]{0x88A81280247EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1971 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_aggregateExpression1989 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MIN_in_aggregateExpression2011 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2013 = new BitSet(new long[]{0x88A81280247EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2016 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_aggregateExpression2034 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUM_in_aggregateExpression2056 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2058 = new BitSet(new long[]{0x88A81280247EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2061 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_aggregateExpression2079 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COUNT_in_aggregateExpression2101 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2103 = new BitSet(new long[]{0x88A81280247EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2106 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_aggregateExpression2124 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_constructorExpression2169 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_constructorName_in_constructorExpression2175 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2185 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2199 = new BitSet(new long[]{0x0000000000010000L,0x0000000020000000L});
    public static final BitSet FOLLOW_COMMA_in_constructorExpression2213 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2219 = new BitSet(new long[]{0x0000000000010000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2275 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_DOT_in_constructorName2289 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2293 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_scalarExpression_in_constructorItem2337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_constructorItem2351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_fromClause2385 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2387 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_COMMA_in_fromClause2399 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2404 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_fromClause2429 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2494 = new BitSet(new long[]{0x2440000000000002L});
    public static final BitSet FOLLOW_join_in_identificationVariableDeclaration2512 = new BitSet(new long[]{0x2440000000000002L});
    public static final BitSet FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2547 = new BitSet(new long[]{0x0008000000000100L});
    public static final BitSet FOLLOW_AS_in_rangeVariableDeclaration2550 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_IDENT_in_rangeVariableDeclaration2556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_joinSpec_in_join2638 = new BitSet(new long[]{0x0808010000000000L,0x0010200000000000L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2652 = new BitSet(new long[]{0x0008000000000100L});
    public static final BitSet FOLLOW_AS_in_join2655 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_IDENT_in_join2661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREAT_in_join2688 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_join2690 = new BitSet(new long[]{0x0808000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2696 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_AS_in_join2698 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_IDENT_in_join2704 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_join2706 = new BitSet(new long[]{0x0008000000000100L});
    public static final BitSet FOLLOW_AS_in_join2709 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_IDENT_in_join2715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FETCH_in_join2737 = new BitSet(new long[]{0x0808000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_in_joinSpec2788 = new BitSet(new long[]{0x0400000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_OUTER_in_joinSpec2791 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_INNER_in_joinSpec2800 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_JOIN_in_joinSpec2806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_collectionMemberDeclaration2834 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2836 = new BitSet(new long[]{0x0808000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2842 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2844 = new BitSet(new long[]{0x0008000000000100L});
    public static final BitSet FOLLOW_AS_in_collectionMemberDeclaration2853 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_IDENT_in_collectionMemberDeclaration2859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_collectionValuedPathExpression2897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_associationPathExpression2929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_joinAssociationPathExpression2965 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_DOT_in_joinAssociationPathExpression2980 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_joinAssociationPathExpression2986 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_pathExpression_in_singleValuedPathExpression3041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_stateFieldPathExpression3073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExpression3105 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_DOT_in_pathExpression3120 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExpression3126 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_IDENT_in_variableAccessOrTypeConstant3222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause3260 = new BitSet(new long[]{0x88A812A0207EC410L,0x0019C67D0C04AEF1L});
    public static final BitSet FOLLOW_conditionalExpression_in_whereClause3266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3306 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_OR_in_conditionalExpression3321 = new BitSet(new long[]{0x88A812A0207EC410L,0x0019C67D0C04AEF1L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3327 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3382 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AND_in_conditionalTerm3397 = new BitSet(new long[]{0x88A812A0207EC410L,0x0019C67D0C04AEF1L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3403 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_NOT_in_conditionalFactor3457 = new BitSet(new long[]{0x88A812A0207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_conditionalPrimary_in_conditionalFactor3475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsExpression_in_conditionalFactor3503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3560 = new BitSet(new long[]{0x88A812A0207EC410L,0x0019C67D0C04AEF1L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalPrimary3566 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleConditionalExpression_in_conditionalPrimary3582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_simpleConditionalExpression3614 = new BitSet(new long[]{0x0210600800000800L,0x000000000001810EL});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3635 = new BitSet(new long[]{0x0210600800000800L,0x000000000001810EL});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3690 = new BitSet(new long[]{0x0010000000000800L,0x0000000000000108L});
    public static final BitSet FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_simpleConditionalExpressionRemainder3709 = new BitSet(new long[]{0x0000000100000000L,0x0000000000028000L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3714 = new BitSet(new long[]{0x0000000100000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_betweenExpression_in_conditionWithNotExpression3757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_likeExpression_in_conditionWithNotExpression3772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inExpression_in_conditionWithNotExpression3786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullComparisonExpression_in_isExpression3835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptyCollectionComparisonExpression_in_isExpression3850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpression3883 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarOrSubSelectExpression_in_betweenExpression3897 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_AND_in_betweenExpression3899 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarOrSubSelectExpression_in_betweenExpression3905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3951 = new BitSet(new long[]{0x0000000000000000L,0x0000000008002000L});
    public static final BitSet FOLLOW_inputParameter_in_inExpression3957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3983 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3993 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D4C042EF1L});
    public static final BitSet FOLLOW_scalarOrSubSelectExpression_in_inExpression4009 = new BitSet(new long[]{0x0000000000010000L,0x0000000020000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression4027 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarOrSubSelectExpression_in_inExpression4033 = new BitSet(new long[]{0x0000000000010000L,0x0000000020000000L});
    public static final BitSet FOLLOW_subquery_in_inExpression4068 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIKE_in_likeExpression4132 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarOrSubSelectExpression_in_likeExpression4138 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_escape_in_likeExpression4153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_in_escape4193 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_escape4199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_nullComparisonExpression4240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEMBER_in_collectionMemberExpression4322 = new BitSet(new long[]{0x0808000000000000L,0x0010000000400000L});
    public static final BitSet FOLLOW_OF_in_collectionMemberExpression4325 = new BitSet(new long[]{0x0808000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_existsExpression4373 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4375 = new BitSet(new long[]{0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_subquery_in_existsExpression4381 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_comparisonExpression4423 = new BitSet(new long[]{0x88A81280207EC4B0L,0x0019C67F0C042EF1L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4429 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4449 = new BitSet(new long[]{0x88A81280207EC4B0L,0x0019C67F0C042EF1L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_in_comparisonExpression4475 = new BitSet(new long[]{0x88A81280207EC4B0L,0x0019C67F0C042EF1L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4501 = new BitSet(new long[]{0x88A81280207EC4B0L,0x0019C67F0C042EF1L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_in_comparisonExpression4527 = new BitSet(new long[]{0x88A81280207EC4B0L,0x0019C67F0C042EF1L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4553 = new BitSet(new long[]{0x88A81280207EC4B0L,0x0019C67F0C042EF1L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4669 = new BitSet(new long[]{0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_subquery_in_arithmeticExpression4675 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4709 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000400L});
    public static final BitSet FOLLOW_PLUS_in_simpleArithmeticExpression4725 = new BitSet(new long[]{0x88A812002004C410L,0x001000450C042EB1L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4731 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000400L});
    public static final BitSet FOLLOW_MINUS_in_simpleArithmeticExpression4759 = new BitSet(new long[]{0x88A812002004C410L,0x001000450C042EB1L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4765 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000400L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4821 = new BitSet(new long[]{0x0000000008000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_MULTIPLY_in_arithmeticTerm4837 = new BitSet(new long[]{0x88A812002004C410L,0x001000450C042EB1L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4843 = new BitSet(new long[]{0x0000000008000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_DIVIDE_in_arithmeticTerm4871 = new BitSet(new long[]{0x88A812002004C410L,0x001000450C042EB1L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4877 = new BitSet(new long[]{0x0000000008000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_PLUS_in_arithmeticFactor4930 = new BitSet(new long[]{0x88A812002004C410L,0x0010004508042AB1L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_arithmeticFactor4957 = new BitSet(new long[]{0x88A812002004C410L,0x0010004508042AB1L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_arithmeticPrimary5019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_arithmeticPrimary5047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_caseExpression_in_arithmeticPrimary5061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5085 = new BitSet(new long[]{0x88A812002004C410L,0x001000450C042EB1L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5091 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_arithmeticPrimary5107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_scalarExpression5139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_scalarOrSubSelectExpression5185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_scalarOrSubSelectExpression5199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_nonArithmeticScalarExpression5259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalTemporal_in_nonArithmeticScalarExpression5287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_anyOrAllExpression5331 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5333 = new BitSet(new long[]{0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5339 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_anyOrAllExpression5361 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5363 = new BitSet(new long[]{0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5369 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SOME_in_anyOrAllExpression5391 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5393 = new BitSet(new long[]{0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5399 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_entityTypeExpression5441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5474 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5476 = new BitSet(new long[]{0x0808000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5482 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5499 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5501 = new BitSet(new long[]{0x0000000000000000L,0x0000000008002000L});
    public static final BitSet FOLLOW_inputParameter_in_typeDiscriminator5507 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleCaseExpression_in_caseExpression5540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_generalCaseExpression_in_caseExpression5553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_coalesceExpression_in_caseExpression5566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullIfExpression_in_caseExpression5579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_simpleCaseExpression5612 = new BitSet(new long[]{0x0808000000000000L,0x0011000000000000L});
    public static final BitSet FOLLOW_caseOperand_in_simpleCaseExpression5618 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5624 = new BitSet(new long[]{0x0000000080000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5633 = new BitSet(new long[]{0x0000000080000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_ELSE_in_simpleCaseExpression5639 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_simpleCaseExpression5645 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_END_in_simpleCaseExpression5647 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_generalCaseExpression5691 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5697 = new BitSet(new long[]{0x0000000080000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5706 = new BitSet(new long[]{0x0000000080000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_ELSE_in_generalCaseExpression5712 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_generalCaseExpression5718 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_END_in_generalCaseExpression5720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COALESCE_in_coalesceExpression5764 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5766 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5772 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_coalesceExpression5777 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5783 = new BitSet(new long[]{0x0000000000010000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULLIF_in_nullIfExpression5830 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5832 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5838 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_nullIfExpression5840 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5846 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression5848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_caseOperand5890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_caseOperand5904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_whenClause5934 = new BitSet(new long[]{0x88A812A0207EC410L,0x0019C67D0C04AEF1L});
    public static final BitSet FOLLOW_conditionalExpression_in_whenClause5940 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_THEN_in_whenClause5942 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_whenClause5948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_simpleWhenClause5985 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause5991 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_THEN_in_simpleWhenClause5993 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause5999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_stringPrimary6082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_stringPrimary6096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_stringPrimary6110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_stringPrimary6124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_literal6158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_literal6172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_literal6186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_LITERAL_in_literalNumeric6216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LONG_LITERAL_in_literalNumeric6232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_LITERAL_in_literalNumeric6252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_LITERAL_in_literalNumeric6272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literalBoolean6310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literalBoolean6330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_LITERAL_in_literalTemporal6428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIME_LITERAL_in_literalTemporal6442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIMESTAMP_LITERAL_in_literalTemporal6456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POSITIONAL_PARAM_in_inputParameter6486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_PARAM_in_inputParameter6506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_abs_in_functionsReturningNumerics6546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_length_in_functionsReturningNumerics6560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mod_in_functionsReturningNumerics6574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqrt_in_functionsReturningNumerics6588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_locate_in_functionsReturningNumerics6602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_size_in_functionsReturningNumerics6616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_index_in_functionsReturningNumerics6630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_func_in_functionsReturningNumerics6644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_concat_in_functionsReturningStrings6754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_substring_in_functionsReturningStrings6768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trim_in_functionsReturningStrings6782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_upper_in_functionsReturningStrings6796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lower_in_functionsReturningStrings6810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONCAT_in_concat6845 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_concat6855 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_concat6869 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_concat6874 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_concat6880 = new BitSet(new long[]{0x0000000000010000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_concat6894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSTRING_in_substring6932 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_substring6942 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_substring6956 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_substring6958 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_substring6972 = new BitSet(new long[]{0x0000000000010000L,0x0000000020000000L});
    public static final BitSet FOLLOW_COMMA_in_substring6983 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_substring6989 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_substring7001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIM_in_trim7039 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_trim7049 = new BitSet(new long[]{0x1808080000021000L,0x0018503808002040L});
    public static final BitSet FOLLOW_trimSpec_in_trim7064 = new BitSet(new long[]{0x0000080000000000L,0x0000001808002000L});
    public static final BitSet FOLLOW_trimChar_in_trim7070 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_FROM_in_trim7072 = new BitSet(new long[]{0x0808000000020000L,0x0018403808002040L});
    public static final BitSet FOLLOW_stringPrimary_in_trim7088 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_trim7098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEADING_in_trimSpec7134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRAILING_in_trimSpec7152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOTH_in_trimSpec7170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_trimChar7218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_trimChar7232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPPER_in_upper7269 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_upper7271 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_upper7277 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_upper7279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOWER_in_lower7317 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_lower7319 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_lower7325 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_lower7327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABS_in_abs7366 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_abs7368 = new BitSet(new long[]{0x88A812002004C410L,0x001000450C042EB1L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_abs7374 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_abs7376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LENGTH_in_length7414 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_length7416 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_length7422 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_length7424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCATE_in_locate7462 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_locate7472 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_locate7486 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_locate7488 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_locate7494 = new BitSet(new long[]{0x0000000000010000L,0x0000000020000000L});
    public static final BitSet FOLLOW_COMMA_in_locate7506 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_locate7512 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_locate7525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIZE_in_size7563 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_size7573 = new BitSet(new long[]{0x0808000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_size7579 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_size7581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mod7619 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mod7621 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_mod7635 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_mod7637 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_mod7651 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mod7661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQRT_in_sqrt7699 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7709 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_sqrt7715 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDEX_in_index7755 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_index7757 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_index7763 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_index7765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNC_in_func7807 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_func7809 = new BitSet(new long[]{0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_func7821 = new BitSet(new long[]{0x0000000000010000L,0x0000000020000000L});
    public static final BitSet FOLLOW_COMMA_in_func7830 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C062EF1L});
    public static final BitSet FOLLOW_newValue_in_func7836 = new BitSet(new long[]{0x0000000000010000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_func7868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleSelectClause_in_subquery7906 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_subqueryFromClause_in_subquery7921 = new BitSet(new long[]{0x0001800000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_whereClause_in_subquery7936 = new BitSet(new long[]{0x0001800000000002L});
    public static final BitSet FOLLOW_groupByClause_in_subquery7951 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_havingClause_in_subquery7967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_simpleSelectClause8010 = new BitSet(new long[]{0x0808000004040400L,0x0010004000000280L});
    public static final BitSet FOLLOW_DISTINCT_in_simpleSelectClause8013 = new BitSet(new long[]{0x0808000000040400L,0x0010004000000280L});
    public static final BitSet FOLLOW_simpleSelectExpression_in_simpleSelectClause8029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_simpleSelectExpression8069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_simpleSelectExpression8084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression8099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_subqueryFromClause8134 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8136 = new BitSet(new long[]{0x0010000000010002L});
    public static final BitSet FOLLOW_COMMA_in_subqueryFromClause8161 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8179 = new BitSet(new long[]{0x0010000000010002L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8204 = new BitSet(new long[]{0x0010000000010002L});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8259 = new BitSet(new long[]{0x0008000000000100L});
    public static final BitSet FOLLOW_AS_in_subselectIdentificationVariableDeclaration8262 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8268 = new BitSet(new long[]{0x2440000000000002L});
    public static final BitSet FOLLOW_join_in_subselectIdentificationVariableDeclaration8294 = new BitSet(new long[]{0x2440000000000002L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_orderByClause8344 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_orderByClause8346 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8362 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_COMMA_in_orderByClause8376 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8382 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_scalarExpression_in_orderByItem8427 = new BitSet(new long[]{0x0000000002000202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem8441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem8469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_groupByClause8550 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_groupByClause8552 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_groupByClause8566 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_COMMA_in_groupByClause8579 = new BitSet(new long[]{0x88A81280207EC410L,0x0019C67D0C042EF1L});
    public static final BitSet FOLLOW_scalarExpression_in_groupByClause8585 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_HAVING_in_havingClause8630 = new BitSet(new long[]{0x88A812A0207EC410L,0x0019C67D0C04AEF1L});
    public static final BitSet FOLLOW_conditionalExpression_in_havingClause8646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_synpred1_JPQL3545 = new BitSet(new long[]{0x88A812A0207EC410L,0x0019C67D0C04AEF1L});
    public static final BitSet FOLLOW_conditionalExpression_in_synpred1_JPQL3547 = new BitSet(new long[]{0x0000000000000002L});
}
