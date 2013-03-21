// $ANTLR 3.2 Sep 23, 2009 12:02:23 JPQL.g 2010-10-07 15:45:34

    package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

    import java.util.List;
    import java.util.ArrayList;

    import static org.eclipse.persistence.internal.jpa.parsing.NodeFactory.*;
    import org.eclipse.persistence.internal.jpa.parsing.jpql.InvalidIdentifierException;


import org.eclipse.persistence.internal.libraries.antlr.runtime.*;

import java.util.Stack;
/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
public class JPQLParser extends org.eclipse.persistence.internal.jpa.parsing.jpql.JPQLParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ABS", "ALL", "AND", "ANY", "AS", "ASC", "AVG", "BETWEEN", "BOTH", "BY", "CASE", "COALESCE", "CONCAT", "COUNT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DESC", "DELETE", "DISTINCT", "ELSE", "EMPTY", "END", "ENTRY", "ESCAPE", "EXISTS", "FALSE", "FETCH", "FUNC", "FROM", "GROUP", "HAVING", "IN", "INDEX", "INNER", "IS", "JOIN", "KEY", "LEADING", "LEFT", "LENGTH", "LIKE", "LOCATE", "LOWER", "MAX", "MEMBER", "MIN", "MOD", "NEW", "NOT", "NULL", "NULLIF", "OBJECT", "OF", "OR", "ORDER", "OUTER", "SELECT", "SET", "SIZE", "SQRT", "SOME", "SUBSTRING", "SUM", "THEN", "TRAILING", "TREAT", "TRIM", "TRUE", "TYPE", "UNKNOWN", "UPDATE", "UPPER", "VALUE", "WHEN", "WHERE", "IDENT", "COMMA", "EQUALS", "LEFT_ROUND_BRACKET", "RIGHT_ROUND_BRACKET", "DOT", "NOT_EQUAL_TO", "GREATER_THAN", "GREATER_THAN_EQUAL_TO", "LESS_THAN", "LESS_THAN_EQUAL_TO", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "INTEGER_LITERAL", "LONG_LITERAL", "FLOAT_LITERAL", "DOUBLE_LITERAL", "STRING_LITERAL_DOUBLE_QUOTED", "STRING_LITERAL_SINGLE_QUOTED", "DATE_LITERAL", "TIME_LITERAL", "TIMESTAMP_LITERAL", "POSITIONAL_PARAM", "NAMED_PARAM", "WS", "LEFT_CURLY_BRACKET", "RIGHT_CURLY_BRACKET", "TEXTCHAR", "HEX_DIGIT", "HEX_LITERAL", "INTEGER_SUFFIX", "OCTAL_LITERAL", "NUMERIC_DIGITS", "DOUBLE_SUFFIX", "EXPONENT", "FLOAT_SUFFIX", "DATE_STRING", "TIME_STRING"
    };
    public static final int EXPONENT=116;
    public static final int DATE_STRING=118;
    public static final int FLOAT_SUFFIX=117;
    public static final int MOD=51;
    public static final int CURRENT_TIME=19;
    public static final int CASE=14;
    public static final int NEW=52;
    public static final int LEFT_ROUND_BRACKET=83;
    public static final int DOUBLE_LITERAL=98;
    public static final int TIME_LITERAL=102;
    public static final int COUNT=17;
    public static final int EQUALS=82;
    public static final int NOT=53;
    public static final int EOF=-1;
    public static final int TIME_STRING=119;
    public static final int TYPE=73;
    public static final int LEFT_CURLY_BRACKET=107;
    public static final int GREATER_THAN_EQUAL_TO=88;
    public static final int ESCAPE=28;
    public static final int NAMED_PARAM=105;
    public static final int BOTH=12;
    public static final int TIMESTAMP_LITERAL=103;
    public static final int NUMERIC_DIGITS=114;
    public static final int SELECT=61;
    public static final int DIVIDE=94;
    public static final int COALESCE=15;
    public static final int ASC=9;
    public static final int CONCAT=16;
    public static final int KEY=41;
    public static final int NULL=54;
    public static final int ELSE=24;
    public static final int TRAILING=69;
    public static final int DELETE=22;
    public static final int VALUE=77;
    public static final int DATE_LITERAL=101;
    public static final int OF=57;
    public static final int RIGHT_CURLY_BRACKET=108;
    public static final int LEADING=42;
    public static final int INTEGER_SUFFIX=112;
    public static final int EMPTY=25;
    public static final int ABS=4;
    public static final int GROUP=34;
    public static final int NOT_EQUAL_TO=86;
    public static final int WS=106;
    public static final int FETCH=31;
    public static final int STRING_LITERAL_SINGLE_QUOTED=100;
    public static final int INTEGER_LITERAL=95;
    public static final int FUNC=32;
    public static final int OR=58;
    public static final int TRIM=71;
    public static final int LESS_THAN=89;
    public static final int RIGHT_ROUND_BRACKET=84;
    public static final int POSITIONAL_PARAM=104;
    public static final int LOWER=47;
    public static final int FROM=33;
    public static final int END=26;
    public static final int FALSE=30;
    public static final int LESS_THAN_EQUAL_TO=90;
    public static final int DISTINCT=23;
    public static final int CURRENT_DATE=18;
    public static final int SIZE=63;
    public static final int UPPER=76;
    public static final int WHERE=79;
    public static final int NULLIF=55;
    public static final int MEMBER=49;
    public static final int INNER=38;
    public static final int ORDER=59;
    public static final int TEXTCHAR=109;
    public static final int MAX=48;
    public static final int UPDATE=75;
    public static final int AND=6;
    public static final int SUM=67;
    public static final int STRING_LITERAL_DOUBLE_QUOTED=99;
    public static final int LENGTH=44;
    public static final int INDEX=37;
    public static final int AS=8;
    public static final int IN=36;
    public static final int THEN=68;
    public static final int UNKNOWN=74;
    public static final int MULTIPLY=93;
    public static final int OBJECT=56;
    public static final int COMMA=81;
    public static final int IS=39;
    public static final int LEFT=43;
    public static final int AVG=10;
    public static final int SOME=65;
    public static final int ALL=5;
    public static final int IDENT=80;
    public static final int PLUS=91;
    public static final int HEX_LITERAL=111;
    public static final int EXISTS=29;
    public static final int DOT=85;
    public static final int CURRENT_TIMESTAMP=20;
    public static final int LIKE=45;
    public static final int OUTER=60;
    public static final int BY=13;
    public static final int GREATER_THAN=87;
    public static final int OCTAL_LITERAL=113;
    public static final int HEX_DIGIT=110;
    public static final int SET=62;
    public static final int HAVING=35;
    public static final int ENTRY=27;
    public static final int MIN=50;
    public static final int MINUS=92;
    public static final int SQRT=64;
    public static final int LONG_LITERAL=96;
    public static final int TRUE=72;
    public static final int JOIN=40;
    public static final int SUBSTRING=66;
    public static final int DOUBLE_SUFFIX=115;
    public static final int FLOAT_LITERAL=97;
    public static final int ANY=7;
    public static final int LOCATE=46;
    public static final int WHEN=78;
    public static final int DESC=21;
    public static final int BETWEEN=11;
    public static final int TREAT=70;

    // delegates
    // delegators


        public JPQLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public JPQLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return JPQLParser.tokenNames; }
    public String getGrammarFileName() { return "JPQL.g"; }


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

            // check remaining characters
            for (int i = 1; i < text.length(); i++) {
                if (!Character.isJavaIdentifierPart(text.charAt(i))) {
                    return false;
                }
            }
            
            return true;
        }

        protected String convertStringLiteral(String text) {
            // skip leading and trailing quotes
            String literal = text.substring(1, text.length() - 1);
            
            // convert ''s to 's
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
    // JPQL.g:199:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );
    public final void document() throws RecognitionException {
        Object root = null;


        try {
            // JPQL.g:200:5: (root= selectStatement | root= updateStatement | root= deleteStatement )
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // JPQL.g:200:7: root= selectStatement
                    {
                    pushFollow(FOLLOW_selectStatement_in_document763);
                    root=selectStatement();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      queryRoot = root;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:201:7: root= updateStatement
                    {
                    pushFollow(FOLLOW_updateStatement_in_document777);
                    root=updateStatement();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      queryRoot = root;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:202:7: root= deleteStatement
                    {
                    pushFollow(FOLLOW_deleteStatement_in_document791);
                    root=deleteStatement();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      queryRoot = root;
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
        }
        return ;
    }
    // $ANTLR end "document"


    // $ANTLR start "selectStatement"
    // JPQL.g:205:1: selectStatement returns [Object node] : select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF ;
    public final Object selectStatement() throws RecognitionException {
        Object node = null;

        Object select = null;

        Object from = null;

        Object where = null;

        Object groupBy = null;

        Object having = null;

        Object orderBy = null;


         
            node = null;

        try {
            // JPQL.g:209:5: (select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF )
            // JPQL.g:209:7: select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF
            {
            pushFollow(FOLLOW_selectClause_in_selectStatement824);
            select=selectClause();

            state._fsp--;
            if (state.failed) return node;
            pushFollow(FOLLOW_fromClause_in_selectStatement839);
            from=fromClause();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:211:7: (where= whereClause )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==WHERE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // JPQL.g:211:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_selectStatement854);
                    where=whereClause();

                    state._fsp--;
                    if (state.failed) return node;

                    }
                    break;

            }

            // JPQL.g:212:7: (groupBy= groupByClause )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==GROUP) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // JPQL.g:212:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_selectStatement869);
                    groupBy=groupByClause();

                    state._fsp--;
                    if (state.failed) return node;

                    }
                    break;

            }

            // JPQL.g:213:7: (having= havingClause )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==HAVING) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // JPQL.g:213:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_selectStatement885);
                    having=havingClause();

                    state._fsp--;
                    if (state.failed) return node;

                    }
                    break;

            }

            // JPQL.g:214:7: (orderBy= orderByClause )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==ORDER) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // JPQL.g:214:8: orderBy= orderByClause
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
        }
        return node;
    }
    // $ANTLR end "selectStatement"


    // $ANTLR start "updateStatement"
    // JPQL.g:224:1: updateStatement returns [Object node] : update= updateClause set= setClause (where= whereClause )? EOF ;
    public final Object updateStatement() throws RecognitionException {
        Object node = null;

        Object update = null;

        Object set = null;

        Object where = null;


         
            node = null; 

        try {
            // JPQL.g:228:5: (update= updateClause set= setClause (where= whereClause )? EOF )
            // JPQL.g:228:7: update= updateClause set= setClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_updateClause_in_updateStatement953);
            update=updateClause();

            state._fsp--;
            if (state.failed) return node;
            pushFollow(FOLLOW_setClause_in_updateStatement968);
            set=setClause();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:230:7: (where= whereClause )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==WHERE) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // JPQL.g:230:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_updateStatement982);
                    where=whereClause();

                    state._fsp--;
                    if (state.failed) return node;

                    }
                    break;

            }

            match(input,EOF,FOLLOW_EOF_in_updateStatement992); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newUpdateStatement(0, 0, update, set, where); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "updateStatement"


    // $ANTLR start "updateClause"
    // JPQL.g:234:1: updateClause returns [Object node] : u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object updateClause() throws RecognitionException {
        Object node = null;

        Token u=null;
        Token ident=null;
        String schema = null;


         
            node = null; 

        try {
            // JPQL.g:238:5: (u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g:238:7: u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            u=(Token)match(input,UPDATE,FOLLOW_UPDATE_in_updateClause1024); if (state.failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_updateClause1030);
            schema=abstractSchemaName();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:239:9: ( ( AS )? ident= IDENT )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==AS||LA8_0==IDENT) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // JPQL.g:239:10: ( AS )? ident= IDENT
                    {
                    // JPQL.g:239:10: ( AS )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==AS) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // JPQL.g:239:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_updateClause1043); if (state.failed) return node;

                            }
                            break;

                    }

                    ident=(Token)match(input,IDENT,FOLLOW_IDENT_in_updateClause1051); if (state.failed) return node;

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
        }
        return node;
    }
    // $ANTLR end "updateClause"

    protected static class setClause_scope {
        List assignments;
    }
    protected Stack setClause_stack = new Stack();


    // $ANTLR start "setClause"
    // JPQL.g:250:1: setClause returns [Object node] : t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* ;
    public final Object setClause() throws RecognitionException {
        setClause_stack.push(new setClause_scope());
        Object node = null;

        Token t=null;
        Object n = null;


         
            node = null; 
            ((setClause_scope)setClause_stack.peek()).assignments = new ArrayList();

        try {
            // JPQL.g:258:5: (t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* )
            // JPQL.g:258:7: t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )*
            {
            t=(Token)match(input,SET,FOLLOW_SET_in_setClause1100); if (state.failed) return node;
            pushFollow(FOLLOW_setAssignmentClause_in_setClause1106);
            n=setAssignmentClause();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
               ((setClause_scope)setClause_stack.peek()).assignments.add(n); 
            }
            // JPQL.g:259:9: ( COMMA n= setAssignmentClause )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // JPQL.g:259:10: COMMA n= setAssignmentClause
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_setClause1119); if (state.failed) return node;
            	    pushFollow(FOLLOW_setAssignmentClause_in_setClause1125);
            	    n=setAssignmentClause();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       ((setClause_scope)setClause_stack.peek()).assignments.add(n); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               node = factory.newSetClause(t.getLine(), t.getCharPositionInLine(), ((setClause_scope)setClause_stack.peek()).assignments); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            setClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "setClause"


    // $ANTLR start "setAssignmentClause"
    // JPQL.g:263:1: setAssignmentClause returns [Object node] : target= setAssignmentTarget t= EQUALS value= newValue ;
    public final Object setAssignmentClause() throws RecognitionException {
        Object node = null;

        Token t=null;
        Object target = null;

        Object value = null;


         
            node = null;

        try {
            // JPQL.g:271:5: (target= setAssignmentTarget t= EQUALS value= newValue )
            // JPQL.g:271:7: target= setAssignmentTarget t= EQUALS value= newValue
            {
            pushFollow(FOLLOW_setAssignmentTarget_in_setAssignmentClause1183);
            target=setAssignmentTarget();

            state._fsp--;
            if (state.failed) return node;
            t=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_setAssignmentClause1187); if (state.failed) return node;
            pushFollow(FOLLOW_newValue_in_setAssignmentClause1193);
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
        }
        return node;
    }
    // $ANTLR end "setAssignmentClause"


    // $ANTLR start "setAssignmentTarget"
    // JPQL.g:274:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );
    public final Object setAssignmentTarget() throws RecognitionException {
        Object node = null;

        Object n = null;


         
            node = null;

        try {
            // JPQL.g:278:5: (n= attribute | n= pathExpression )
            int alt10=2;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // JPQL.g:278:7: n= attribute
                    {
                    pushFollow(FOLLOW_attribute_in_setAssignmentTarget1223);
                    n=attribute();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:279:7: n= pathExpression
                    {
                    pushFollow(FOLLOW_pathExpression_in_setAssignmentTarget1238);
                    n=pathExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "setAssignmentTarget"


    // $ANTLR start "newValue"
    // JPQL.g:282:1: newValue returns [Object node] : (n= scalarExpression | n1= NULL );
    public final Object newValue() throws RecognitionException {
        Object node = null;

        Token n1=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:284:5: (n= scalarExpression | n1= NULL )
            int alt11=2;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    // JPQL.g:284:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_newValue1270);
                    n=scalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:285:7: n1= NULL
                    {
                    n1=(Token)match(input,NULL,FOLLOW_NULL_in_newValue1284); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newNullLiteral(n1.getLine(), n1.getCharPositionInLine()); 
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
        }
        return node;
    }
    // $ANTLR end "newValue"


    // $ANTLR start "deleteStatement"
    // JPQL.g:291:1: deleteStatement returns [Object node] : delete= deleteClause (where= whereClause )? EOF ;
    public final Object deleteStatement() throws RecognitionException {
        Object node = null;

        Object delete = null;

        Object where = null;


         
            node = null; 

        try {
            // JPQL.g:295:5: (delete= deleteClause (where= whereClause )? EOF )
            // JPQL.g:295:7: delete= deleteClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_deleteClause_in_deleteStatement1328);
            delete=deleteClause();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:296:7: (where= whereClause )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==WHERE) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // JPQL.g:296:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_deleteStatement1341);
                    where=whereClause();

                    state._fsp--;
                    if (state.failed) return node;

                    }
                    break;

            }

            match(input,EOF,FOLLOW_EOF_in_deleteStatement1351); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newDeleteStatement(0, 0, delete, where); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "deleteStatement"

    protected static class deleteClause_scope {
        String variable;
    }
    protected Stack deleteClause_stack = new Stack();


    // $ANTLR start "deleteClause"
    // JPQL.g:300:1: deleteClause returns [Object node] : t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object deleteClause() throws RecognitionException {
        deleteClause_stack.push(new deleteClause_scope());
        Object node = null;

        Token t=null;
        Token ident=null;
        String schema = null;


         
            node = null; 
            ((deleteClause_scope)deleteClause_stack.peek()).variable = null;

        try {
            // JPQL.g:308:5: (t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g:308:7: t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            t=(Token)match(input,DELETE,FOLLOW_DELETE_in_deleteClause1384); if (state.failed) return node;
            match(input,FROM,FOLLOW_FROM_in_deleteClause1386); if (state.failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_deleteClause1392);
            schema=abstractSchemaName();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:309:9: ( ( AS )? ident= IDENT )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==AS||LA14_0==IDENT) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // JPQL.g:309:10: ( AS )? ident= IDENT
                    {
                    // JPQL.g:309:10: ( AS )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==AS) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // JPQL.g:309:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_deleteClause1405); if (state.failed) return node;

                            }
                            break;

                    }

                    ident=(Token)match(input,IDENT,FOLLOW_IDENT_in_deleteClause1411); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       ((deleteClause_scope)deleteClause_stack.peek()).variable = ident.getText(); 
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               
                          node = factory.newDeleteClause(t.getLine(), t.getCharPositionInLine(), 
                                                         schema, ((deleteClause_scope)deleteClause_stack.peek()).variable); 
                      
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
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
    protected Stack selectClause_stack = new Stack();


    // $ANTLR start "selectClause"
    // JPQL.g:318:1: selectClause returns [Object node] : t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )* ;
    public final Object selectClause() throws RecognitionException {
        selectClause_stack.push(new selectClause_scope());
        Object node = null;

        Token t=null;
        JPQLParser.selectItem_return n = null;


         
            node = null;
            ((selectClause_scope)selectClause_stack.peek()).distinct = false;
            ((selectClause_scope)selectClause_stack.peek()).exprs = new ArrayList();
            ((selectClause_scope)selectClause_stack.peek()).idents = new ArrayList();

        try {
            // JPQL.g:330:5: (t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )* )
            // JPQL.g:330:7: t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )*
            {
            t=(Token)match(input,SELECT,FOLLOW_SELECT_in_selectClause1458); if (state.failed) return node;
            // JPQL.g:330:16: ( DISTINCT )?
            int alt15=2;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // JPQL.g:330:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause1461); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       ((selectClause_scope)selectClause_stack.peek()).distinct = true; 
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               setAggregatesAllowed(true); 
            }
            pushFollow(FOLLOW_selectItem_in_selectClause1480);
            n=selectItem();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {

                            ((selectClause_scope)selectClause_stack.peek()).exprs.add((n!=null?n.expr:null));
                            ((selectClause_scope)selectClause_stack.peek()).idents.add((n!=null?n.ident:null));
                        
            }
            // JPQL.g:336:11: ( COMMA n= selectItem )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==COMMA) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // JPQL.g:336:13: COMMA n= selectItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_selectClause1508); if (state.failed) return node;
            	    pushFollow(FOLLOW_selectItem_in_selectClause1514);
            	    n=selectItem();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {

            	                        ((selectClause_scope)selectClause_stack.peek()).exprs.add((n!=null?n.expr:null));
            	                        ((selectClause_scope)selectClause_stack.peek()).idents.add((n!=null?n.ident:null));
            	                     
            	    }

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               
                          setAggregatesAllowed(false); 
                          node = factory.newSelectClause(t.getLine(), t.getCharPositionInLine(), 
                                                         ((selectClause_scope)selectClause_stack.peek()).distinct, ((selectClause_scope)selectClause_stack.peek()).exprs, ((selectClause_scope)selectClause_stack.peek()).idents); 
                      
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
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
    // JPQL.g:350:1: selectItem returns [Object expr, Object ident] : e= selectExpression ( ( AS )? identifier= IDENT )? ;
    public final JPQLParser.selectItem_return selectItem() throws RecognitionException {
        JPQLParser.selectItem_return retval = new JPQLParser.selectItem_return();
        retval.start = input.LT(1);

        Token identifier=null;
        Object e = null;


        try {
            // JPQL.g:351:5: (e= selectExpression ( ( AS )? identifier= IDENT )? )
            // JPQL.g:351:7: e= selectExpression ( ( AS )? identifier= IDENT )?
            {
            pushFollow(FOLLOW_selectExpression_in_selectItem1610);
            e=selectExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JPQL.g:351:28: ( ( AS )? identifier= IDENT )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==AS||LA18_0==IDENT) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // JPQL.g:351:29: ( AS )? identifier= IDENT
                    {
                    // JPQL.g:351:29: ( AS )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==AS) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // JPQL.g:351:30: AS
                            {
                            match(input,AS,FOLLOW_AS_in_selectItem1614); if (state.failed) return retval;

                            }
                            break;

                    }

                    identifier=(Token)match(input,IDENT,FOLLOW_IDENT_in_selectItem1622); if (state.failed) return retval;

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
        }
        return retval;
    }
    // $ANTLR end "selectItem"


    // $ANTLR start "selectExpression"
    // JPQL.g:364:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );
    public final Object selectExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:366:5: (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression )
            int alt19=5;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // JPQL.g:366:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_selectExpression1666);
                    n=aggregateExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:367:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_selectExpression1680);
                    n=scalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:368:7: OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    match(input,OBJECT,FOLLOW_OBJECT_in_selectExpression1690); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1692); if (state.failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_selectExpression1698);
                    n=variableAccessOrTypeConstant();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1700); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:369:7: n= constructorExpression
                    {
                    pushFollow(FOLLOW_constructorExpression_in_selectExpression1715);
                    n=constructorExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 5 :
                    // JPQL.g:370:7: n= mapEntryExpression
                    {
                    pushFollow(FOLLOW_mapEntryExpression_in_selectExpression1730);
                    n=mapEntryExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "selectExpression"


    // $ANTLR start "mapEntryExpression"
    // JPQL.g:373:1: mapEntryExpression returns [Object node] : l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object mapEntryExpression() throws RecognitionException {
        Object node = null;

        Token l=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:375:5: (l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // JPQL.g:375:7: l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            l=(Token)match(input,ENTRY,FOLLOW_ENTRY_in_mapEntryExpression1770); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1772); if (state.failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1778);
            n=variableAccessOrTypeConstant();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1780); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newMapEntry(l.getLine(), l.getCharPositionInLine(), n);
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "mapEntryExpression"


    // $ANTLR start "pathExprOrVariableAccess"
    // JPQL.g:378:1: pathExprOrVariableAccess returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )* ;
    public final Object pathExprOrVariableAccess() throws RecognitionException {
        Object node = null;

        Token d=null;
        Object n = null;

        Object right = null;



            node = null;

        try {
            // JPQL.g:382:5: (n= qualifiedIdentificationVariable (d= DOT right= attribute )* )
            // JPQL.g:382:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )*
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1812);
            n=qualifiedIdentificationVariable();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }
            // JPQL.g:383:9: (d= DOT right= attribute )*
            loop20:
            do {
                int alt20=2;
                alt20 = dfa20.predict(input);
                switch (alt20) {
            	case 1 :
            	    // JPQL.g:383:10: d= DOT right= attribute
            	    {
            	    d=(Token)match(input,DOT,FOLLOW_DOT_in_pathExprOrVariableAccess1827); if (state.failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExprOrVariableAccess1833);
            	    right=attribute();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       node = factory.newDot(d.getLine(), d.getCharPositionInLine(), node, right); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "pathExprOrVariableAccess"


    // $ANTLR start "qualifiedIdentificationVariable"
    // JPQL.g:388:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );
    public final Object qualifiedIdentificationVariable() throws RecognitionException {
        Object node = null;

        Token l=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:390:5: (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
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
                    // JPQL.g:390:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1889);
                    n=variableAccessOrTypeConstant();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:391:7: l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)match(input,KEY,FOLLOW_KEY_in_qualifiedIdentificationVariable1903); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1905); if (state.failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1911);
                    n=variableAccessOrTypeConstant();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1913); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newKey(l.getLine(), l.getCharPositionInLine(), n); 
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:392:7: l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)match(input,VALUE,FOLLOW_VALUE_in_qualifiedIdentificationVariable1928); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1930); if (state.failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1936);
                    n=variableAccessOrTypeConstant();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1938); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = n;
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
        }
        return node;
    }
    // $ANTLR end "qualifiedIdentificationVariable"

    protected static class aggregateExpression_scope {
        boolean distinct;
    }
    protected Stack aggregateExpression_stack = new Stack();


    // $ANTLR start "aggregateExpression"
    // JPQL.g:395:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET );
    public final Object aggregateExpression() throws RecognitionException {
        aggregateExpression_stack.push(new aggregateExpression_scope());
        Object node = null;

        Token t1=null;
        Token t2=null;
        Token t3=null;
        Token t4=null;
        Token t5=null;
        Object n = null;


         
            node = null; 
            ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = false;

        try {
            // JPQL.g:403:5: (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET )
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
                    // JPQL.g:403:7: t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET
                    {
                    t1=(Token)match(input,AVG,FOLLOW_AVG_in_aggregateExpression1971); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1973); if (state.failed) return node;
                    // JPQL.g:403:33: ( DISTINCT )?
                    int alt22=2;
                    alt22 = dfa22.predict(input);
                    switch (alt22) {
                        case 1 :
                            // JPQL.g:403:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1976); if (state.failed) return node;
                            if ( state.backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_scalarExpression_in_aggregateExpression1994);
                    n=scalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1996); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newAvg(t1.getLine(), t1.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:406:7: t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET
                    {
                    t2=(Token)match(input,MAX,FOLLOW_MAX_in_aggregateExpression2017); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2019); if (state.failed) return node;
                    // JPQL.g:406:33: ( DISTINCT )?
                    int alt23=2;
                    alt23 = dfa23.predict(input);
                    switch (alt23) {
                        case 1 :
                            // JPQL.g:406:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2022); if (state.failed) return node;
                            if ( state.backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_scalarExpression_in_aggregateExpression2041);
                    n=scalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2043); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newMax(t2.getLine(), t2.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:409:7: t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET
                    {
                    t3=(Token)match(input,MIN,FOLLOW_MIN_in_aggregateExpression2063); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2065); if (state.failed) return node;
                    // JPQL.g:409:33: ( DISTINCT )?
                    int alt24=2;
                    alt24 = dfa24.predict(input);
                    switch (alt24) {
                        case 1 :
                            // JPQL.g:409:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2068); if (state.failed) return node;
                            if ( state.backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_scalarExpression_in_aggregateExpression2086);
                    n=scalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2088); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newMin(t3.getLine(), t3.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:412:7: t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET
                    {
                    t4=(Token)match(input,SUM,FOLLOW_SUM_in_aggregateExpression2108); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2110); if (state.failed) return node;
                    // JPQL.g:412:33: ( DISTINCT )?
                    int alt25=2;
                    alt25 = dfa25.predict(input);
                    switch (alt25) {
                        case 1 :
                            // JPQL.g:412:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2113); if (state.failed) return node;
                            if ( state.backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_scalarExpression_in_aggregateExpression2131);
                    n=scalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2133); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newSum(t4.getLine(), t4.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }

                    }
                    break;
                case 5 :
                    // JPQL.g:415:7: t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= scalarExpression RIGHT_ROUND_BRACKET
                    {
                    t5=(Token)match(input,COUNT,FOLLOW_COUNT_in_aggregateExpression2153); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2155); if (state.failed) return node;
                    // JPQL.g:415:35: ( DISTINCT )?
                    int alt26=2;
                    alt26 = dfa26.predict(input);
                    switch (alt26) {
                        case 1 :
                            // JPQL.g:415:36: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2158); if (state.failed) return node;
                            if ( state.backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_scalarExpression_in_aggregateExpression2176);
                    n=scalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2178); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newCount(t5.getLine(), t5.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
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
            aggregateExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "aggregateExpression"

    protected static class constructorExpression_scope {
        List args;
    }
    protected Stack constructorExpression_stack = new Stack();


    // $ANTLR start "constructorExpression"
    // JPQL.g:420:1: constructorExpression returns [Object node] : t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET ;
    public final Object constructorExpression() throws RecognitionException {
        constructorExpression_stack.push(new constructorExpression_scope());
        Object node = null;

        Token t=null;
        String className = null;

        Object n = null;


         
            node = null;
            ((constructorExpression_scope)constructorExpression_stack.peek()).args = new ArrayList();

        try {
            // JPQL.g:428:5: (t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET )
            // JPQL.g:428:7: t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET
            {
            t=(Token)match(input,NEW,FOLLOW_NEW_in_constructorExpression2221); if (state.failed) return node;
            pushFollow(FOLLOW_constructorName_in_constructorExpression2227);
            className=constructorName();

            state._fsp--;
            if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2237); if (state.failed) return node;
            pushFollow(FOLLOW_constructorItem_in_constructorExpression2252);
            n=constructorItem();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            }
            // JPQL.g:431:9: ( COMMA n= constructorItem )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==COMMA) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // JPQL.g:431:11: COMMA n= constructorItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constructorExpression2267); if (state.failed) return node;
            	    pushFollow(FOLLOW_constructorItem_in_constructorExpression2273);
            	    n=constructorItem();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2288); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               
                          node = factory.newConstructor(t.getLine(), t.getCharPositionInLine(), 
                                                        className, ((constructorExpression_scope)constructorExpression_stack.peek()).args); 
                      
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            constructorExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "constructorExpression"

    protected static class constructorName_scope {
        StringBuffer buf;
    }
    protected Stack constructorName_stack = new Stack();


    // $ANTLR start "constructorName"
    // JPQL.g:439:1: constructorName returns [String className] : i1= IDENT ( DOT i2= IDENT )* ;
    public final String constructorName() throws RecognitionException {
        constructorName_stack.push(new constructorName_scope());
        String className = null;

        Token i1=null;
        Token i2=null;

         
            className = null;
            ((constructorName_scope)constructorName_stack.peek()).buf = new StringBuffer(); 

        try {
            // JPQL.g:447:5: (i1= IDENT ( DOT i2= IDENT )* )
            // JPQL.g:447:7: i1= IDENT ( DOT i2= IDENT )*
            {
            i1=(Token)match(input,IDENT,FOLLOW_IDENT_in_constructorName2329); if (state.failed) return className;
            if ( state.backtracking==0 ) {
               ((constructorName_scope)constructorName_stack.peek()).buf.append(i1.getText()); 
            }
            // JPQL.g:448:9: ( DOT i2= IDENT )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==DOT) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // JPQL.g:448:11: DOT i2= IDENT
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_constructorName2343); if (state.failed) return className;
            	    i2=(Token)match(input,IDENT,FOLLOW_IDENT_in_constructorName2347); if (state.failed) return className;
            	    if ( state.backtracking==0 ) {
            	       ((constructorName_scope)constructorName_stack.peek()).buf.append('.').append(i2.getText()); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               className = ((constructorName_scope)constructorName_stack.peek()).buf.toString(); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            constructorName_stack.pop();
        }
        return className;
    }
    // $ANTLR end "constructorName"


    // $ANTLR start "constructorItem"
    // JPQL.g:452:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );
    public final Object constructorItem() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:454:5: (n= scalarExpression | n= aggregateExpression )
            int alt30=2;
            alt30 = dfa30.predict(input);
            switch (alt30) {
                case 1 :
                    // JPQL.g:454:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_constructorItem2391);
                    n=scalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:455:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_constructorItem2405);
                    n=aggregateExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "constructorItem"

    protected static class fromClause_scope {
        List varDecls;
    }
    protected Stack fromClause_stack = new Stack();


    // $ANTLR start "fromClause"
    // JPQL.g:459:1: fromClause returns [Object node] : t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* ;
    public final Object fromClause() throws RecognitionException {
        fromClause_stack.push(new fromClause_scope());
        Object node = null;

        Token t=null;
        Object n = null;


         
            node = null; 
            ((fromClause_scope)fromClause_stack.peek()).varDecls = new ArrayList();

        try {
            // JPQL.g:467:5: (t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* )
            // JPQL.g:467:7: t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            {
            t=(Token)match(input,FROM,FOLLOW_FROM_in_fromClause2439); if (state.failed) return node;
            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2441);
            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:468:9: ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==COMMA) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // JPQL.g:468:10: COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_fromClause2453); if (state.failed) return node;
            	    // JPQL.g:468:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
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
            	            NoViableAltException nvae =
            	                new NoViableAltException("", 31, 1, input);

            	            throw nvae;
            	        }
            	    }
            	    else if ( ((LA31_0>=ABS && LA31_0<=HAVING)||(LA31_0>=INDEX && LA31_0<=TIME_STRING)) ) {
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
            	            // JPQL.g:468:19: identificationVariableDeclaration[$fromClause::varDecls]
            	            {
            	            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2458);
            	            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);

            	            state._fsp--;
            	            if (state.failed) return node;

            	            }
            	            break;
            	        case 2 :
            	            // JPQL.g:469:19: n= collectionMemberDeclaration
            	            {
            	            pushFollow(FOLLOW_collectionMemberDeclaration_in_fromClause2483);
            	            n=collectionMemberDeclaration();

            	            state._fsp--;
            	            if (state.failed) return node;
            	            if ( state.backtracking==0 ) {
            	              ((fromClause_scope)fromClause_stack.peek()).varDecls.add(n); 
            	            }

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               node = factory.newFromClause(t.getLine(), t.getCharPositionInLine(), ((fromClause_scope)fromClause_stack.peek()).varDecls); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            fromClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "fromClause"


    // $ANTLR start "identificationVariableDeclaration"
    // JPQL.g:475:1: identificationVariableDeclaration[List varDecls] : node= rangeVariableDeclaration (node= join )* ;
    public final void identificationVariableDeclaration(List varDecls) throws RecognitionException {
        Object node = null;


        try {
            // JPQL.g:476:5: (node= rangeVariableDeclaration (node= join )* )
            // JPQL.g:476:7: node= rangeVariableDeclaration (node= join )*
            {
            pushFollow(FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2549);
            node=rangeVariableDeclaration();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               varDecls.add(node); 
            }
            // JPQL.g:477:9: (node= join )*
            loop33:
            do {
                int alt33=2;
                alt33 = dfa33.predict(input);
                switch (alt33) {
            	case 1 :
            	    // JPQL.g:477:11: node= join
            	    {
            	    pushFollow(FOLLOW_join_in_identificationVariableDeclaration2568);
            	    node=join();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	       varDecls.add(node); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "identificationVariableDeclaration"


    // $ANTLR start "rangeVariableDeclaration"
    // JPQL.g:480:1: rangeVariableDeclaration returns [Object node] : schema= abstractSchemaName ( AS )? i= IDENT ;
    public final Object rangeVariableDeclaration() throws RecognitionException {
        Object node = null;

        Token i=null;
        String schema = null;


         
            node = null; 

        try {
            // JPQL.g:484:5: (schema= abstractSchemaName ( AS )? i= IDENT )
            // JPQL.g:484:7: schema= abstractSchemaName ( AS )? i= IDENT
            {
            pushFollow(FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2603);
            schema=abstractSchemaName();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:484:35: ( AS )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==AS) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // JPQL.g:484:36: AS
                    {
                    match(input,AS,FOLLOW_AS_in_rangeVariableDeclaration2606); if (state.failed) return node;

                    }
                    break;

            }

            i=(Token)match(input,IDENT,FOLLOW_IDENT_in_rangeVariableDeclaration2612); if (state.failed) return node;
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
        }
        return node;
    }
    // $ANTLR end "rangeVariableDeclaration"


    // $ANTLR start "abstractSchemaName"
    // JPQL.g:495:1: abstractSchemaName returns [String schema] : ident= . ;
    public final String abstractSchemaName() throws RecognitionException {
        String schema = null;

        Token ident=null;

         schema = null; 
        try {
            // JPQL.g:497:5: (ident= . )
            // JPQL.g:497:7: ident= .
            {
            ident=(Token)input.LT(1);
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
        }
        return schema;
    }
    // $ANTLR end "abstractSchemaName"


    // $ANTLR start "join"
    // JPQL.g:504:1: join returns [Object node] : outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) ;
    public final Object join() throws RecognitionException {
        Object node = null;

        Token i=null;
        Token castClass=null;
        Token t=null;
        boolean outerJoin = false;

        Object n = null;


         
            node = null;

        try {
            // JPQL.g:508:5: (outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) )
            // JPQL.g:508:7: outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            {
            pushFollow(FOLLOW_joinSpec_in_join2695);
            outerJoin=joinSpec();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:509:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            int alt37=3;
            switch ( input.LA(1) ) {
            case KEY:
            case VALUE:
            case IDENT:
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
                    // JPQL.g:509:9: n= joinAssociationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2709);
                    n=joinAssociationPathExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    // JPQL.g:509:43: ( AS )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( (LA35_0==AS) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // JPQL.g:509:44: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2712); if (state.failed) return node;

                            }
                            break;

                    }

                    i=(Token)match(input,IDENT,FOLLOW_IDENT_in_join2718); if (state.failed) return node;
                    if ( state.backtracking==0 ) {

                                  node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                     outerJoin, n, i.getText(), null); 
                              
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:515:8: TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT
                    {
                    match(input,TREAT,FOLLOW_TREAT_in_join2747); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_join2749); if (state.failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2755);
                    n=joinAssociationPathExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,AS,FOLLOW_AS_in_join2757); if (state.failed) return node;
                    castClass=(Token)match(input,IDENT,FOLLOW_IDENT_in_join2763); if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_join2765); if (state.failed) return node;
                    // JPQL.g:515:108: ( AS )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);

                    if ( (LA36_0==AS) ) {
                        alt36=1;
                    }
                    switch (alt36) {
                        case 1 :
                            // JPQL.g:515:109: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2768); if (state.failed) return node;

                            }
                            break;

                    }

                    i=(Token)match(input,IDENT,FOLLOW_IDENT_in_join2774); if (state.failed) return node;
                    if ( state.backtracking==0 ) {

                                  node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                     outerJoin, n, i.getText(), castClass.getText()); 
                              
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:520:9: t= FETCH n= joinAssociationPathExpression
                    {
                    t=(Token)match(input,FETCH,FOLLOW_FETCH_in_join2797); if (state.failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2803);
                    n=joinAssociationPathExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       
                                  node = factory.newFetchJoin(t.getLine(), t.getCharPositionInLine(), 
                                                              outerJoin, n); 
                    }

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
        }
        return node;
    }
    // $ANTLR end "join"


    // $ANTLR start "joinSpec"
    // JPQL.g:527:1: joinSpec returns [boolean outer] : ( LEFT ( OUTER )? | INNER )? JOIN ;
    public final boolean joinSpec() throws RecognitionException {
        boolean outer = false;

         outer = false; 
        try {
            // JPQL.g:529:5: ( ( LEFT ( OUTER )? | INNER )? JOIN )
            // JPQL.g:529:7: ( LEFT ( OUTER )? | INNER )? JOIN
            {
            // JPQL.g:529:7: ( LEFT ( OUTER )? | INNER )?
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
                    // JPQL.g:529:8: LEFT ( OUTER )?
                    {
                    match(input,LEFT,FOLLOW_LEFT_in_joinSpec2849); if (state.failed) return outer;
                    // JPQL.g:529:13: ( OUTER )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==OUTER) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // JPQL.g:529:14: OUTER
                            {
                            match(input,OUTER,FOLLOW_OUTER_in_joinSpec2852); if (state.failed) return outer;

                            }
                            break;

                    }

                    if ( state.backtracking==0 ) {
                       outer = true; 
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:529:44: INNER
                    {
                    match(input,INNER,FOLLOW_INNER_in_joinSpec2861); if (state.failed) return outer;

                    }
                    break;

            }

            match(input,JOIN,FOLLOW_JOIN_in_joinSpec2867); if (state.failed) return outer;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return outer;
    }
    // $ANTLR end "joinSpec"


    // $ANTLR start "collectionMemberDeclaration"
    // JPQL.g:532:1: collectionMemberDeclaration returns [Object node] : t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT ;
    public final Object collectionMemberDeclaration() throws RecognitionException {
        Object node = null;

        Token t=null;
        Token i=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:534:5: (t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT )
            // JPQL.g:534:7: t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT
            {
            t=(Token)match(input,IN,FOLLOW_IN_in_collectionMemberDeclaration2895); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2897); if (state.failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2903);
            n=collectionValuedPathExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2905); if (state.failed) return node;
            // JPQL.g:535:7: ( AS )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==AS) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // JPQL.g:535:8: AS
                    {
                    match(input,AS,FOLLOW_AS_in_collectionMemberDeclaration2915); if (state.failed) return node;

                    }
                    break;

            }

            i=(Token)match(input,IDENT,FOLLOW_IDENT_in_collectionMemberDeclaration2921); if (state.failed) return node;
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
        }
        return node;
    }
    // $ANTLR end "collectionMemberDeclaration"


    // $ANTLR start "collectionValuedPathExpression"
    // JPQL.g:542:1: collectionValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object collectionValuedPathExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:544:5: (n= pathExpression )
            // JPQL.g:544:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_collectionValuedPathExpression2959);
            n=pathExpression();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "collectionValuedPathExpression"


    // $ANTLR start "associationPathExpression"
    // JPQL.g:547:1: associationPathExpression returns [Object node] : n= pathExpression ;
    public final Object associationPathExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:549:5: (n= pathExpression )
            // JPQL.g:549:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_associationPathExpression2991);
            n=pathExpression();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "associationPathExpression"


    // $ANTLR start "joinAssociationPathExpression"
    // JPQL.g:552:1: joinAssociationPathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object joinAssociationPathExpression() throws RecognitionException {
        Object node = null;

        Token d=null;
        Object n = null;

        Object right = null;



            node = null; 

        try {
            // JPQL.g:556:6: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // JPQL.g:556:8: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_joinAssociationPathExpression3024);
            n=qualifiedIdentificationVariable();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }
            // JPQL.g:557:9: (d= DOT right= attribute )+
            int cnt41=0;
            loop41:
            do {
                int alt41=2;
                alt41 = dfa41.predict(input);
                switch (alt41) {
            	case 1 :
            	    // JPQL.g:557:10: d= DOT right= attribute
            	    {
            	    d=(Token)match(input,DOT,FOLLOW_DOT_in_joinAssociationPathExpression3039); if (state.failed) return node;
            	    pushFollow(FOLLOW_attribute_in_joinAssociationPathExpression3045);
            	    right=attribute();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       node = factory.newDot(d.getLine(), d.getCharPositionInLine(), node, right); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt41 >= 1 ) break loop41;
            	    if (state.backtracking>0) {state.failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(41, input);
                        throw eee;
                }
                cnt41++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "joinAssociationPathExpression"


    // $ANTLR start "singleValuedPathExpression"
    // JPQL.g:562:1: singleValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object singleValuedPathExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:564:5: (n= pathExpression )
            // JPQL.g:564:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_singleValuedPathExpression3101);
            n=pathExpression();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "singleValuedPathExpression"


    // $ANTLR start "stateFieldPathExpression"
    // JPQL.g:567:1: stateFieldPathExpression returns [Object node] : n= pathExpression ;
    public final Object stateFieldPathExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:569:5: (n= pathExpression )
            // JPQL.g:569:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_stateFieldPathExpression3133);
            n=pathExpression();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "stateFieldPathExpression"


    // $ANTLR start "pathExpression"
    // JPQL.g:572:1: pathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object pathExpression() throws RecognitionException {
        Object node = null;

        Token d=null;
        Object n = null;

        Object right = null;


         
            node = null; 

        try {
            // JPQL.g:576:5: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // JPQL.g:576:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExpression3165);
            n=qualifiedIdentificationVariable();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }
            // JPQL.g:577:9: (d= DOT right= attribute )+
            int cnt42=0;
            loop42:
            do {
                int alt42=2;
                alt42 = dfa42.predict(input);
                switch (alt42) {
            	case 1 :
            	    // JPQL.g:577:10: d= DOT right= attribute
            	    {
            	    d=(Token)match(input,DOT,FOLLOW_DOT_in_pathExpression3180); if (state.failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExpression3186);
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
                        EarlyExitException eee =
                            new EarlyExitException(42, input);
                        throw eee;
                }
                cnt42++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "pathExpression"


    // $ANTLR start "attribute"
    // JPQL.g:588:1: attribute returns [Object node] : i= . ;
    public final Object attribute() throws RecognitionException {
        Object node = null;

        Token i=null;

         node = null; 
        try {
            // JPQL.g:591:5: (i= . )
            // JPQL.g:591:7: i= .
            {
            i=(Token)input.LT(1);
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
        }
        return node;
    }
    // $ANTLR end "attribute"


    // $ANTLR start "variableAccessOrTypeConstant"
    // JPQL.g:598:1: variableAccessOrTypeConstant returns [Object node] : i= IDENT ;
    public final Object variableAccessOrTypeConstant() throws RecognitionException {
        Object node = null;

        Token i=null;

         node = null; 
        try {
            // JPQL.g:600:5: (i= IDENT )
            // JPQL.g:600:7: i= IDENT
            {
            i=(Token)match(input,IDENT,FOLLOW_IDENT_in_variableAccessOrTypeConstant3282); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newVariableAccessOrTypeConstant(i.getLine(), i.getCharPositionInLine(), i.getText()); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "variableAccessOrTypeConstant"


    // $ANTLR start "whereClause"
    // JPQL.g:604:1: whereClause returns [Object node] : t= WHERE n= conditionalExpression ;
    public final Object whereClause() throws RecognitionException {
        Object node = null;

        Token t=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:606:5: (t= WHERE n= conditionalExpression )
            // JPQL.g:606:7: t= WHERE n= conditionalExpression
            {
            t=(Token)match(input,WHERE,FOLLOW_WHERE_in_whereClause3320); if (state.failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whereClause3326);
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
        }
        return node;
    }
    // $ANTLR end "whereClause"


    // $ANTLR start "conditionalExpression"
    // JPQL.g:612:1: conditionalExpression returns [Object node] : n= conditionalTerm (t= OR right= conditionalTerm )* ;
    public final Object conditionalExpression() throws RecognitionException {
        Object node = null;

        Token t=null;
        Object n = null;

        Object right = null;


         
            node = null; 

        try {
            // JPQL.g:616:5: (n= conditionalTerm (t= OR right= conditionalTerm )* )
            // JPQL.g:616:7: n= conditionalTerm (t= OR right= conditionalTerm )*
            {
            pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3368);
            n=conditionalTerm();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }
            // JPQL.g:617:9: (t= OR right= conditionalTerm )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==OR) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // JPQL.g:617:10: t= OR right= conditionalTerm
            	    {
            	    t=(Token)match(input,OR,FOLLOW_OR_in_conditionalExpression3383); if (state.failed) return node;
            	    pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3389);
            	    right=conditionalTerm();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       node = factory.newOr(t.getLine(), t.getCharPositionInLine(), node, right); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "conditionalExpression"


    // $ANTLR start "conditionalTerm"
    // JPQL.g:622:1: conditionalTerm returns [Object node] : n= conditionalFactor (t= AND right= conditionalFactor )* ;
    public final Object conditionalTerm() throws RecognitionException {
        Object node = null;

        Token t=null;
        Object n = null;

        Object right = null;


         
            node = null; 

        try {
            // JPQL.g:626:5: (n= conditionalFactor (t= AND right= conditionalFactor )* )
            // JPQL.g:626:7: n= conditionalFactor (t= AND right= conditionalFactor )*
            {
            pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3444);
            n=conditionalFactor();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }
            // JPQL.g:627:9: (t= AND right= conditionalFactor )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);

                if ( (LA44_0==AND) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // JPQL.g:627:10: t= AND right= conditionalFactor
            	    {
            	    t=(Token)match(input,AND,FOLLOW_AND_in_conditionalTerm3459); if (state.failed) return node;
            	    pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3465);
            	    right=conditionalFactor();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       node = factory.newAnd(t.getLine(), t.getCharPositionInLine(), node, right); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop44;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "conditionalTerm"


    // $ANTLR start "conditionalFactor"
    // JPQL.g:632:1: conditionalFactor returns [Object node] : (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) ;
    public final Object conditionalFactor() throws RecognitionException {
        Object node = null;

        Token n=null;
        Object n1 = null;


         node = null; 
        try {
            // JPQL.g:634:5: ( (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) )
            // JPQL.g:634:7: (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            {
            // JPQL.g:634:7: (n= NOT )?
            int alt45=2;
            alt45 = dfa45.predict(input);
            switch (alt45) {
                case 1 :
                    // JPQL.g:634:8: n= NOT
                    {
                    n=(Token)match(input,NOT,FOLLOW_NOT_in_conditionalFactor3520); if (state.failed) return node;

                    }
                    break;

            }

            // JPQL.g:635:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            int alt46=2;
            alt46 = dfa46.predict(input);
            switch (alt46) {
                case 1 :
                    // JPQL.g:635:11: n1= conditionalPrimary
                    {
                    pushFollow(FOLLOW_conditionalPrimary_in_conditionalFactor3539);
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
                    // JPQL.g:642:11: n1= existsExpression[(n!=null)]
                    {
                    pushFollow(FOLLOW_existsExpression_in_conditionalFactor3568);
                    n1=existsExpression((n!=null));

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n1;
                    }

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
        }
        return node;
    }
    // $ANTLR end "conditionalFactor"


    // $ANTLR start "conditionalPrimary"
    // JPQL.g:646:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );
    public final Object conditionalPrimary() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:648:5: ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression )
            int alt47=2;
            alt47 = dfa47.predict(input);
            switch (alt47) {
                case 1 :
                    // JPQL.g:648:7: ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3625); if (state.failed) return node;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalPrimary3631);
                    n=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3633); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:650:7: n= simpleConditionalExpression
                    {
                    pushFollow(FOLLOW_simpleConditionalExpression_in_conditionalPrimary3647);
                    n=simpleConditionalExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "conditionalPrimary"


    // $ANTLR start "simpleConditionalExpression"
    // JPQL.g:653:1: simpleConditionalExpression returns [Object node] : (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] );
    public final Object simpleConditionalExpression() throws RecognitionException {
        Object node = null;

        Object left = null;

        Object n = null;


         
            node = null; 

        try {
            // JPQL.g:657:5: (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] )
            int alt48=2;
            alt48 = dfa48.predict(input);
            switch (alt48) {
                case 1 :
                    // JPQL.g:657:7: left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_simpleConditionalExpression3679);
                    left=arithmeticExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3685);
                    n=simpleConditionalExpressionRemainder(left);

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:658:7: left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3700);
                    left=nonArithmeticScalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3706);
                    n=simpleConditionalExpressionRemainder(left);

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "simpleConditionalExpression"


    // $ANTLR start "simpleConditionalExpressionRemainder"
    // JPQL.g:661:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );
    public final Object simpleConditionalExpressionRemainder(Object left) throws RecognitionException {
        Object node = null;

        Token n1=null;
        Token n2=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:663:5: (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] )
            int alt51=3;
            alt51 = dfa51.predict(input);
            switch (alt51) {
                case 1 :
                    // JPQL.g:663:7: n= comparisonExpression[left]
                    {
                    pushFollow(FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3741);
                    n=comparisonExpression(left);

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:664:7: (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left]
                    {
                    // JPQL.g:664:7: (n1= NOT )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);

                    if ( (LA49_0==NOT) ) {
                        alt49=1;
                    }
                    switch (alt49) {
                        case 1 :
                            // JPQL.g:664:8: n1= NOT
                            {
                            n1=(Token)match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3755); if (state.failed) return node;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3763);
                    n=conditionWithNotExpression((n1!=null), left);

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:665:7: IS (n2= NOT )? n= isExpression[(n2!=null), left]
                    {
                    match(input,IS,FOLLOW_IS_in_simpleConditionalExpressionRemainder3774); if (state.failed) return node;
                    // JPQL.g:665:10: (n2= NOT )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==NOT) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // JPQL.g:665:11: n2= NOT
                            {
                            n2=(Token)match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3779); if (state.failed) return node;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3787);
                    n=isExpression((n2!=null), left);

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "simpleConditionalExpressionRemainder"


    // $ANTLR start "conditionWithNotExpression"
    // JPQL.g:668:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );
    public final Object conditionWithNotExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:670:5: (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] )
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
                    // JPQL.g:670:7: n= betweenExpression[not, left]
                    {
                    pushFollow(FOLLOW_betweenExpression_in_conditionWithNotExpression3822);
                    n=betweenExpression(not, left);

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:671:7: n= likeExpression[not, left]
                    {
                    pushFollow(FOLLOW_likeExpression_in_conditionWithNotExpression3837);
                    n=likeExpression(not, left);

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:672:7: n= inExpression[not, left]
                    {
                    pushFollow(FOLLOW_inExpression_in_conditionWithNotExpression3851);
                    n=inExpression(not, left);

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:673:7: n= collectionMemberExpression[not, left]
                    {
                    pushFollow(FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3865);
                    n=collectionMemberExpression(not, left);

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "conditionWithNotExpression"


    // $ANTLR start "isExpression"
    // JPQL.g:676:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );
    public final Object isExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:678:5: (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] )
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
                    // JPQL.g:678:7: n= nullComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_nullComparisonExpression_in_isExpression3900);
                    n=nullComparisonExpression(not, left);

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:679:7: n= emptyCollectionComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_emptyCollectionComparisonExpression_in_isExpression3915);
                    n=emptyCollectionComparisonExpression(not, left);

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "isExpression"


    // $ANTLR start "betweenExpression"
    // JPQL.g:682:1: betweenExpression[boolean not, Object left] returns [Object node] : t= BETWEEN lowerBound= scalarOrSubSelectExpression AND upperBound= scalarOrSubSelectExpression ;
    public final Object betweenExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;

        Token t=null;
        Object lowerBound = null;

        Object upperBound = null;



            node = null;

        try {
            // JPQL.g:686:5: (t= BETWEEN lowerBound= scalarOrSubSelectExpression AND upperBound= scalarOrSubSelectExpression )
            // JPQL.g:686:7: t= BETWEEN lowerBound= scalarOrSubSelectExpression AND upperBound= scalarOrSubSelectExpression
            {
            t=(Token)match(input,BETWEEN,FOLLOW_BETWEEN_in_betweenExpression3948); if (state.failed) return node;
            pushFollow(FOLLOW_scalarOrSubSelectExpression_in_betweenExpression3962);
            lowerBound=scalarOrSubSelectExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,AND,FOLLOW_AND_in_betweenExpression3964); if (state.failed) return node;
            pushFollow(FOLLOW_scalarOrSubSelectExpression_in_betweenExpression3970);
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
        }
        return node;
    }
    // $ANTLR end "betweenExpression"

    protected static class inExpression_scope {
        List items;
    }
    protected Stack inExpression_stack = new Stack();


    // $ANTLR start "inExpression"
    // JPQL.g:694:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= scalarOrSubSelectExpression ( COMMA itemNode= scalarOrSubSelectExpression )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );
    public final Object inExpression(boolean not, Object left) throws RecognitionException {
        inExpression_stack.push(new inExpression_scope());
        Object node = null;

        Token t=null;
        Object n = null;

        Object itemNode = null;

        Object subqueryNode = null;



            node = null;
            ((inExpression_scope)inExpression_stack.peek()).items = new ArrayList();

        try {
            // JPQL.g:702:5: (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= scalarOrSubSelectExpression ( COMMA itemNode= scalarOrSubSelectExpression )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==IN) ) {
                int LA56_1 = input.LA(2);

                if ( (LA56_1==LEFT_ROUND_BRACKET) ) {
                    alt56=2;
                }
                else if ( ((LA56_1>=POSITIONAL_PARAM && LA56_1<=NAMED_PARAM)) ) {
                    alt56=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 56, 1, input);

                    throw nvae;
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
                    // JPQL.g:702:8: t= IN n= inputParameter
                    {
                    t=(Token)match(input,IN,FOLLOW_IN_in_inExpression4016); if (state.failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_inExpression4022);
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
                    // JPQL.g:707:9: t= IN LEFT_ROUND_BRACKET (itemNode= scalarOrSubSelectExpression ( COMMA itemNode= scalarOrSubSelectExpression )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET
                    {
                    t=(Token)match(input,IN,FOLLOW_IN_in_inExpression4049); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_inExpression4059); if (state.failed) return node;
                    // JPQL.g:709:9: (itemNode= scalarOrSubSelectExpression ( COMMA itemNode= scalarOrSubSelectExpression )* | subqueryNode= subquery )
                    int alt55=2;
                    alt55 = dfa55.predict(input);
                    switch (alt55) {
                        case 1 :
                            // JPQL.g:709:11: itemNode= scalarOrSubSelectExpression ( COMMA itemNode= scalarOrSubSelectExpression )*
                            {
                            pushFollow(FOLLOW_scalarOrSubSelectExpression_in_inExpression4075);
                            itemNode=scalarOrSubSelectExpression();

                            state._fsp--;
                            if (state.failed) return node;
                            if ( state.backtracking==0 ) {
                               ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                            }
                            // JPQL.g:710:13: ( COMMA itemNode= scalarOrSubSelectExpression )*
                            loop54:
                            do {
                                int alt54=2;
                                int LA54_0 = input.LA(1);

                                if ( (LA54_0==COMMA) ) {
                                    alt54=1;
                                }


                                switch (alt54) {
                            	case 1 :
                            	    // JPQL.g:710:15: COMMA itemNode= scalarOrSubSelectExpression
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_inExpression4093); if (state.failed) return node;
                            	    pushFollow(FOLLOW_scalarOrSubSelectExpression_in_inExpression4099);
                            	    itemNode=scalarOrSubSelectExpression();

                            	    state._fsp--;
                            	    if (state.failed) return node;
                            	    if ( state.backtracking==0 ) {
                            	       ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop54;
                                }
                            } while (true);

                            if ( state.backtracking==0 ) {

                                              node = factory.newIn(t.getLine(), t.getCharPositionInLine(),
                                                                   not, left, ((inExpression_scope)inExpression_stack.peek()).items);
                                          
                            }

                            }
                            break;
                        case 2 :
                            // JPQL.g:715:11: subqueryNode= subquery
                            {
                            pushFollow(FOLLOW_subquery_in_inExpression4134);
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

                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4168); if (state.failed) return node;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            inExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "inExpression"


    // $ANTLR start "likeExpression"
    // JPQL.g:724:1: likeExpression[boolean not, Object left] returns [Object node] : t= LIKE pattern= scalarOrSubSelectExpression (escapeChars= escape )? ;
    public final Object likeExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;

        Token t=null;
        Object pattern = null;

        Object escapeChars = null;



            node = null;

        try {
            // JPQL.g:728:5: (t= LIKE pattern= scalarOrSubSelectExpression (escapeChars= escape )? )
            // JPQL.g:728:7: t= LIKE pattern= scalarOrSubSelectExpression (escapeChars= escape )?
            {
            t=(Token)match(input,LIKE,FOLLOW_LIKE_in_likeExpression4198); if (state.failed) return node;
            pushFollow(FOLLOW_scalarOrSubSelectExpression_in_likeExpression4204);
            pattern=scalarOrSubSelectExpression();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:729:9: (escapeChars= escape )?
            int alt57=2;
            alt57 = dfa57.predict(input);
            switch (alt57) {
                case 1 :
                    // JPQL.g:729:10: escapeChars= escape
                    {
                    pushFollow(FOLLOW_escape_in_likeExpression4219);
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
        }
        return node;
    }
    // $ANTLR end "likeExpression"


    // $ANTLR start "escape"
    // JPQL.g:736:1: escape returns [Object node] : t= ESCAPE escapeClause= scalarExpression ;
    public final Object escape() throws RecognitionException {
        Object node = null;

        Token t=null;
        Object escapeClause = null;


         
            node = null; 

        try {
            // JPQL.g:740:5: (t= ESCAPE escapeClause= scalarExpression )
            // JPQL.g:740:7: t= ESCAPE escapeClause= scalarExpression
            {
            t=(Token)match(input,ESCAPE,FOLLOW_ESCAPE_in_escape4259); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_escape4265);
            escapeClause=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newEscape(t.getLine(), t.getCharPositionInLine(), escapeClause); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "escape"


    // $ANTLR start "nullComparisonExpression"
    // JPQL.g:744:1: nullComparisonExpression[boolean not, Object left] returns [Object node] : t= NULL ;
    public final Object nullComparisonExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;

        Token t=null;

         node = null; 
        try {
            // JPQL.g:746:5: (t= NULL )
            // JPQL.g:746:7: t= NULL
            {
            t=(Token)match(input,NULL,FOLLOW_NULL_in_nullComparisonExpression4306); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newIsNull(t.getLine(), t.getCharPositionInLine(), not, left); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "nullComparisonExpression"


    // $ANTLR start "emptyCollectionComparisonExpression"
    // JPQL.g:750:1: emptyCollectionComparisonExpression[boolean not, Object left] returns [Object node] : t= EMPTY ;
    public final Object emptyCollectionComparisonExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;

        Token t=null;

         node = null; 
        try {
            // JPQL.g:752:5: (t= EMPTY )
            // JPQL.g:752:7: t= EMPTY
            {
            t=(Token)match(input,EMPTY,FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4347); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newIsEmpty(t.getLine(), t.getCharPositionInLine(), not, left); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "emptyCollectionComparisonExpression"


    // $ANTLR start "collectionMemberExpression"
    // JPQL.g:756:1: collectionMemberExpression[boolean not, Object left] returns [Object node] : t= MEMBER ( OF )? n= collectionValuedPathExpression ;
    public final Object collectionMemberExpression(boolean not, Object left) throws RecognitionException {
        Object node = null;

        Token t=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:758:5: (t= MEMBER ( OF )? n= collectionValuedPathExpression )
            // JPQL.g:758:7: t= MEMBER ( OF )? n= collectionValuedPathExpression
            {
            t=(Token)match(input,MEMBER,FOLLOW_MEMBER_in_collectionMemberExpression4388); if (state.failed) return node;
            // JPQL.g:758:17: ( OF )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==OF) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // JPQL.g:758:18: OF
                    {
                    match(input,OF,FOLLOW_OF_in_collectionMemberExpression4391); if (state.failed) return node;

                    }
                    break;

            }

            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4399);
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
        }
        return node;
    }
    // $ANTLR end "collectionMemberExpression"


    // $ANTLR start "existsExpression"
    // JPQL.g:765:1: existsExpression[boolean not] returns [Object node] : t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET ;
    public final Object existsExpression(boolean not) throws RecognitionException {
        Object node = null;

        Token t=null;
        Object subqueryNode = null;


         
            node = null;

        try {
            // JPQL.g:769:5: (t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET )
            // JPQL.g:769:7: t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET
            {
            t=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_existsExpression4439); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4441); if (state.failed) return node;
            pushFollow(FOLLOW_subquery_in_existsExpression4447);
            subqueryNode=subquery();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4449); if (state.failed) return node;
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
        }
        return node;
    }
    // $ANTLR end "existsExpression"


    // $ANTLR start "comparisonExpression"
    // JPQL.g:776:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );
    public final Object comparisonExpression(Object left) throws RecognitionException {
        Object node = null;

        Token t1=null;
        Token t2=null;
        Token t3=null;
        Token t4=null;
        Token t5=null;
        Token t6=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:778:5: (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand )
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
                    // JPQL.g:778:7: t1= EQUALS n= comparisonExpressionRightOperand
                    {
                    t1=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_comparisonExpression4489); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4495);
                    n=comparisonExpressionRightOperand();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newEquals(t1.getLine(), t1.getCharPositionInLine(), left, n); 
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:780:7: t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t2=(Token)match(input,NOT_EQUAL_TO,FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4516); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4522);
                    n=comparisonExpressionRightOperand();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newNotEquals(t2.getLine(), t2.getCharPositionInLine(), left, n); 
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:782:7: t3= GREATER_THAN n= comparisonExpressionRightOperand
                    {
                    t3=(Token)match(input,GREATER_THAN,FOLLOW_GREATER_THAN_in_comparisonExpression4543); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4549);
                    n=comparisonExpressionRightOperand();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newGreaterThan(t3.getLine(), t3.getCharPositionInLine(), left, n); 
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:784:7: t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t4=(Token)match(input,GREATER_THAN_EQUAL_TO,FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4570); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4576);
                    n=comparisonExpressionRightOperand();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newGreaterThanEqual(t4.getLine(), t4.getCharPositionInLine(), left, n); 
                    }

                    }
                    break;
                case 5 :
                    // JPQL.g:786:7: t5= LESS_THAN n= comparisonExpressionRightOperand
                    {
                    t5=(Token)match(input,LESS_THAN,FOLLOW_LESS_THAN_in_comparisonExpression4597); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4603);
                    n=comparisonExpressionRightOperand();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newLessThan(t5.getLine(), t5.getCharPositionInLine(), left, n); 
                    }

                    }
                    break;
                case 6 :
                    // JPQL.g:788:7: t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t6=(Token)match(input,LESS_THAN_EQUAL_TO,FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4624); if (state.failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4630);
                    n=comparisonExpressionRightOperand();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newLessThanEqual(t6.getLine(), t6.getCharPositionInLine(), left, n); 
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
        }
        return node;
    }
    // $ANTLR end "comparisonExpression"


    // $ANTLR start "comparisonExpressionRightOperand"
    // JPQL.g:792:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression );
    public final Object comparisonExpressionRightOperand() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:794:5: (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression )
            int alt60=3;
            alt60 = dfa60.predict(input);
            switch (alt60) {
                case 1 :
                    // JPQL.g:794:7: n= arithmeticExpression
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4671);
                    n=arithmeticExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:795:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4685);
                    n=nonArithmeticScalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:796:7: n= anyOrAllExpression
                    {
                    pushFollow(FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4699);
                    n=anyOrAllExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "comparisonExpressionRightOperand"


    // $ANTLR start "arithmeticExpression"
    // JPQL.g:799:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object arithmeticExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:801:5: (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt61=2;
            alt61 = dfa61.predict(input);
            switch (alt61) {
                case 1 :
                    // JPQL.g:801:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4731);
                    n=simpleArithmeticExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:802:7: LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4741); if (state.failed) return node;
                    pushFollow(FOLLOW_subquery_in_arithmeticExpression4747);
                    n=subquery();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4749); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "arithmeticExpression"


    // $ANTLR start "simpleArithmeticExpression"
    // JPQL.g:805:1: simpleArithmeticExpression returns [Object node] : n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* ;
    public final Object simpleArithmeticExpression() throws RecognitionException {
        Object node = null;

        Token p=null;
        Token m=null;
        Object n = null;

        Object right = null;


         
            node = null; 

        try {
            // JPQL.g:809:5: (n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* )
            // JPQL.g:809:7: n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            {
            pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4781);
            n=arithmeticTerm();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }
            // JPQL.g:810:9: (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            loop62:
            do {
                int alt62=3;
                alt62 = dfa62.predict(input);
                switch (alt62) {
            	case 1 :
            	    // JPQL.g:810:11: p= PLUS right= arithmeticTerm
            	    {
            	    p=(Token)match(input,PLUS,FOLLOW_PLUS_in_simpleArithmeticExpression4797); if (state.failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4803);
            	    right=arithmeticTerm();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       node = factory.newPlus(p.getLine(), p.getCharPositionInLine(), node, right); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:812:11: m= MINUS right= arithmeticTerm
            	    {
            	    m=(Token)match(input,MINUS,FOLLOW_MINUS_in_simpleArithmeticExpression4832); if (state.failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4838);
            	    right=arithmeticTerm();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       node = factory.newMinus(m.getLine(), m.getCharPositionInLine(), node, right); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop62;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "simpleArithmeticExpression"


    // $ANTLR start "arithmeticTerm"
    // JPQL.g:817:1: arithmeticTerm returns [Object node] : n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* ;
    public final Object arithmeticTerm() throws RecognitionException {
        Object node = null;

        Token m=null;
        Token d=null;
        Object n = null;

        Object right = null;


         
            node = null; 

        try {
            // JPQL.g:821:5: (n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* )
            // JPQL.g:821:7: n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            {
            pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4895);
            n=arithmeticFactor();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }
            // JPQL.g:822:9: (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            loop63:
            do {
                int alt63=3;
                alt63 = dfa63.predict(input);
                switch (alt63) {
            	case 1 :
            	    // JPQL.g:822:11: m= MULTIPLY right= arithmeticFactor
            	    {
            	    m=(Token)match(input,MULTIPLY,FOLLOW_MULTIPLY_in_arithmeticTerm4911); if (state.failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4917);
            	    right=arithmeticFactor();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       node = factory.newMultiply(m.getLine(), m.getCharPositionInLine(), node, right); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:824:11: d= DIVIDE right= arithmeticFactor
            	    {
            	    d=(Token)match(input,DIVIDE,FOLLOW_DIVIDE_in_arithmeticTerm4946); if (state.failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4952);
            	    right=arithmeticFactor();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       node = factory.newDivide(d.getLine(), d.getCharPositionInLine(), node, right); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop63;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "arithmeticTerm"


    // $ANTLR start "arithmeticFactor"
    // JPQL.g:829:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );
    public final Object arithmeticFactor() throws RecognitionException {
        Object node = null;

        Token p=null;
        Token m=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:831:5: (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary )
            int alt64=3;
            alt64 = dfa64.predict(input);
            switch (alt64) {
                case 1 :
                    // JPQL.g:831:7: p= PLUS n= arithmeticPrimary
                    {
                    p=(Token)match(input,PLUS,FOLLOW_PLUS_in_arithmeticFactor5006); if (state.failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5013);
                    n=arithmeticPrimary();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = factory.newUnaryPlus(p.getLine(), p.getCharPositionInLine(), n); 
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:833:7: m= MINUS n= arithmeticPrimary
                    {
                    m=(Token)match(input,MINUS,FOLLOW_MINUS_in_arithmeticFactor5035); if (state.failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5041);
                    n=arithmeticPrimary();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newUnaryMinus(m.getLine(), m.getCharPositionInLine(), n); 
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:835:7: n= arithmeticPrimary
                    {
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5065);
                    n=arithmeticPrimary();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "arithmeticFactor"


    // $ANTLR start "arithmeticPrimary"
    // JPQL.g:838:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric );
    public final Object arithmeticPrimary() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:840:5: ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric )
            int alt65=7;
            alt65 = dfa65.predict(input);
            switch (alt65) {
                case 1 :
                    // JPQL.g:840:7: {...}?n= aggregateExpression
                    {
                    if ( !(( aggregatesAllowed() )) ) {
                        if (state.backtracking>0) {state.failed=true; return node;}
                        throw new FailedPredicateException(input, "arithmeticPrimary", " aggregatesAllowed() ");
                    }
                    pushFollow(FOLLOW_aggregateExpression_in_arithmeticPrimary5099);
                    n=aggregateExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:841:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5113);
                    n=pathExprOrVariableAccess();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:842:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_arithmeticPrimary5127);
                    n=inputParameter();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:843:7: n= caseExpression
                    {
                    pushFollow(FOLLOW_caseExpression_in_arithmeticPrimary5141);
                    n=caseExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 5 :
                    // JPQL.g:844:7: n= functionsReturningNumerics
                    {
                    pushFollow(FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5155);
                    n=functionsReturningNumerics();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 6 :
                    // JPQL.g:845:7: LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5165); if (state.failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5171);
                    n=simpleArithmeticExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5173); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 7 :
                    // JPQL.g:846:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_arithmeticPrimary5187);
                    n=literalNumeric();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "arithmeticPrimary"


    // $ANTLR start "scalarExpression"
    // JPQL.g:849:1: scalarExpression returns [Object node] : (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression );
    public final Object scalarExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


        node = null; 
        try {
            // JPQL.g:851:5: (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression )
            int alt66=2;
            alt66 = dfa66.predict(input);
            switch (alt66) {
                case 1 :
                    // JPQL.g:851:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_scalarExpression5219);
                    n=simpleArithmeticExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:852:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5234);
                    n=nonArithmeticScalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "scalarExpression"


    // $ANTLR start "scalarOrSubSelectExpression"
    // JPQL.g:855:1: scalarOrSubSelectExpression returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression );
    public final Object scalarOrSubSelectExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


        node = null; 
        try {
            // JPQL.g:857:5: (n= arithmeticExpression | n= nonArithmeticScalarExpression )
            int alt67=2;
            alt67 = dfa67.predict(input);
            switch (alt67) {
                case 1 :
                    // JPQL.g:857:7: n= arithmeticExpression
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_scalarOrSubSelectExpression5270);
                    n=arithmeticExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:858:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_scalarOrSubSelectExpression5285);
                    n=nonArithmeticScalarExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "scalarOrSubSelectExpression"


    // $ANTLR start "nonArithmeticScalarExpression"
    // JPQL.g:861:1: nonArithmeticScalarExpression returns [Object node] : (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression );
    public final Object nonArithmeticScalarExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


        node = null; 
        try {
            // JPQL.g:863:5: (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression )
            int alt68=6;
            alt68 = dfa68.predict(input);
            switch (alt68) {
                case 1 :
                    // JPQL.g:863:7: n= functionsReturningDatetime
                    {
                    pushFollow(FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5317);
                    n=functionsReturningDatetime();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:864:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5331);
                    n=functionsReturningStrings();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:865:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_nonArithmeticScalarExpression5345);
                    n=literalString();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:866:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5359);
                    n=literalBoolean();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 5 :
                    // JPQL.g:867:7: n= literalTemporal
                    {
                    pushFollow(FOLLOW_literalTemporal_in_nonArithmeticScalarExpression5373);
                    n=literalTemporal();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 6 :
                    // JPQL.g:868:7: n= entityTypeExpression
                    {
                    pushFollow(FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5387);
                    n=entityTypeExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "nonArithmeticScalarExpression"


    // $ANTLR start "anyOrAllExpression"
    // JPQL.g:871:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object anyOrAllExpression() throws RecognitionException {
        Object node = null;

        Token a=null;
        Token y=null;
        Token s=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:873:5: (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
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
                    // JPQL.g:873:7: a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    a=(Token)match(input,ALL,FOLLOW_ALL_in_anyOrAllExpression5417); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5419); if (state.failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5425);
                    n=subquery();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5427); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newAll(a.getLine(), a.getCharPositionInLine(), n); 
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:875:7: y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    y=(Token)match(input,ANY,FOLLOW_ANY_in_anyOrAllExpression5447); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5449); if (state.failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5455);
                    n=subquery();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5457); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newAny(y.getLine(), y.getCharPositionInLine(), n); 
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:877:7: s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    s=(Token)match(input,SOME,FOLLOW_SOME_in_anyOrAllExpression5477); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5479); if (state.failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5485);
                    n=subquery();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5487); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newSome(s.getLine(), s.getCharPositionInLine(), n); 
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
        }
        return node;
    }
    // $ANTLR end "anyOrAllExpression"


    // $ANTLR start "entityTypeExpression"
    // JPQL.g:881:1: entityTypeExpression returns [Object node] : n= typeDiscriminator ;
    public final Object entityTypeExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


        node = null;
        try {
            // JPQL.g:883:5: (n= typeDiscriminator )
            // JPQL.g:883:7: n= typeDiscriminator
            {
            pushFollow(FOLLOW_typeDiscriminator_in_entityTypeExpression5527);
            n=typeDiscriminator();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = n;
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "entityTypeExpression"


    // $ANTLR start "typeDiscriminator"
    // JPQL.g:886:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );
    public final Object typeDiscriminator() throws RecognitionException {
        Object node = null;

        Token a=null;
        Token c=null;
        Object n = null;


        node = null;
        try {
            // JPQL.g:888:5: (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==TYPE) ) {
                int LA70_1 = input.LA(2);

                if ( (LA70_1==LEFT_ROUND_BRACKET) ) {
                    int LA70_2 = input.LA(3);

                    if ( ((LA70_2>=POSITIONAL_PARAM && LA70_2<=NAMED_PARAM)) ) {
                        alt70=2;
                    }
                    else if ( (LA70_2==KEY||LA70_2==VALUE||LA70_2==IDENT) ) {
                        alt70=1;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 70, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 70, 1, input);

                    throw nvae;
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
                    // JPQL.g:888:7: a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET
                    {
                    a=(Token)match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5560); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5562); if (state.failed) return node;
                    pushFollow(FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5568);
                    n=variableOrSingleValuedPath();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5570); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newType(a.getLine(), a.getCharPositionInLine(), n);
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:889:7: c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET
                    {
                    c=(Token)match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5585); if (state.failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5587); if (state.failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_typeDiscriminator5593);
                    n=inputParameter();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5595); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newType(c.getLine(), c.getCharPositionInLine(), n);
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
        }
        return node;
    }
    // $ANTLR end "typeDiscriminator"


    // $ANTLR start "caseExpression"
    // JPQL.g:892:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );
    public final Object caseExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


        node = null;
        try {
            // JPQL.g:894:4: (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression )
            int alt71=4;
            switch ( input.LA(1) ) {
            case CASE:
                {
                int LA71_1 = input.LA(2);

                if ( (LA71_1==WHEN) ) {
                    alt71=2;
                }
                else if ( (LA71_1==KEY||LA71_1==TYPE||LA71_1==VALUE||LA71_1==IDENT) ) {
                    alt71=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 71, 1, input);

                    throw nvae;
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
                    // JPQL.g:894:6: n= simpleCaseExpression
                    {
                    pushFollow(FOLLOW_simpleCaseExpression_in_caseExpression5630);
                    n=simpleCaseExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:895:6: n= generalCaseExpression
                    {
                    pushFollow(FOLLOW_generalCaseExpression_in_caseExpression5643);
                    n=generalCaseExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:896:6: n= coalesceExpression
                    {
                    pushFollow(FOLLOW_coalesceExpression_in_caseExpression5656);
                    n=coalesceExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:897:6: n= nullIfExpression
                    {
                    pushFollow(FOLLOW_nullIfExpression_in_caseExpression5669);
                    n=nullIfExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "caseExpression"

    protected static class simpleCaseExpression_scope {
        List whens;
    }
    protected Stack simpleCaseExpression_stack = new Stack();


    // $ANTLR start "simpleCaseExpression"
    // JPQL.g:900:1: simpleCaseExpression returns [Object node] : a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END ;
    public final Object simpleCaseExpression() throws RecognitionException {
        simpleCaseExpression_stack.push(new simpleCaseExpression_scope());
        Object node = null;

        Token a=null;
        Object c = null;

        Object w = null;

        Object e = null;



            node = null;
            ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens = new ArrayList();

        try {
            // JPQL.g:908:4: (a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END )
            // JPQL.g:908:6: a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END
            {
            a=(Token)match(input,CASE,FOLLOW_CASE_in_simpleCaseExpression5707); if (state.failed) return node;
            pushFollow(FOLLOW_caseOperand_in_simpleCaseExpression5713);
            c=caseOperand();

            state._fsp--;
            if (state.failed) return node;
            pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5719);
            w=simpleWhenClause();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens.add(w);
            }
            // JPQL.g:908:97: (w= simpleWhenClause )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==WHEN) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // JPQL.g:908:98: w= simpleWhenClause
            	    {
            	    pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5728);
            	    w=simpleWhenClause();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	      ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens.add(w);
            	    }

            	    }
            	    break;

            	default :
            	    break loop72;
                }
            } while (true);

            match(input,ELSE,FOLLOW_ELSE_in_simpleCaseExpression5734); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleCaseExpression5740);
            e=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,END,FOLLOW_END_in_simpleCaseExpression5742); if (state.failed) return node;
            if ( state.backtracking==0 ) {

                             node = factory.newCaseClause(a.getLine(), a.getCharPositionInLine(), c,
                                  ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens, e); 
                         
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            simpleCaseExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "simpleCaseExpression"

    protected static class generalCaseExpression_scope {
        List whens;
    }
    protected Stack generalCaseExpression_stack = new Stack();


    // $ANTLR start "generalCaseExpression"
    // JPQL.g:915:1: generalCaseExpression returns [Object node] : a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END ;
    public final Object generalCaseExpression() throws RecognitionException {
        generalCaseExpression_stack.push(new generalCaseExpression_scope());
        Object node = null;

        Token a=null;
        Object w = null;

        Object e = null;



            node = null;
            ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens = new ArrayList();

        try {
            // JPQL.g:923:4: (a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END )
            // JPQL.g:923:6: a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END
            {
            a=(Token)match(input,CASE,FOLLOW_CASE_in_generalCaseExpression5786); if (state.failed) return node;
            pushFollow(FOLLOW_whenClause_in_generalCaseExpression5792);
            w=whenClause();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens.add(w);
            }
            // JPQL.g:923:76: (w= whenClause )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( (LA73_0==WHEN) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // JPQL.g:923:77: w= whenClause
            	    {
            	    pushFollow(FOLLOW_whenClause_in_generalCaseExpression5801);
            	    w=whenClause();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	      ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens.add(w);
            	    }

            	    }
            	    break;

            	default :
            	    break loop73;
                }
            } while (true);

            match(input,ELSE,FOLLOW_ELSE_in_generalCaseExpression5807); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_generalCaseExpression5813);
            e=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,END,FOLLOW_END_in_generalCaseExpression5815); if (state.failed) return node;
            if ( state.backtracking==0 ) {

                             node = factory.newCaseClause(a.getLine(), a.getCharPositionInLine(), null,
                                  ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens, e); 
                         
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            generalCaseExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "generalCaseExpression"

    protected static class coalesceExpression_scope {
        List primaries;
    }
    protected Stack coalesceExpression_stack = new Stack();


    // $ANTLR start "coalesceExpression"
    // JPQL.g:930:1: coalesceExpression returns [Object node] : c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET ;
    public final Object coalesceExpression() throws RecognitionException {
        coalesceExpression_stack.push(new coalesceExpression_scope());
        Object node = null;

        Token c=null;
        Object p = null;

        Object s = null;



            node = null;
            ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries = new ArrayList();

        try {
            // JPQL.g:938:4: (c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET )
            // JPQL.g:938:6: c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET
            {
            c=(Token)match(input,COALESCE,FOLLOW_COALESCE_in_coalesceExpression5859); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5861); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5867);
            p=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries.add(p);
            }
            // JPQL.g:938:106: ( COMMA s= scalarExpression )+
            int cnt74=0;
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==COMMA) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // JPQL.g:938:107: COMMA s= scalarExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_coalesceExpression5872); if (state.failed) return node;
            	    pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5878);
            	    s=scalarExpression();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	      ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries.add(s);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt74 >= 1 ) break loop74;
            	    if (state.backtracking>0) {state.failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(74, input);
                        throw eee;
                }
                cnt74++;
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5884); if (state.failed) return node;
            if ( state.backtracking==0 ) {

                             node = factory.newCoalesceClause(c.getLine(), c.getCharPositionInLine(), 
                                  ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries); 
                         
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            coalesceExpression_stack.pop();
        }
        return node;
    }
    // $ANTLR end "coalesceExpression"


    // $ANTLR start "nullIfExpression"
    // JPQL.g:945:1: nullIfExpression returns [Object node] : n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object nullIfExpression() throws RecognitionException {
        Object node = null;

        Token n=null;
        Object l = null;

        Object r = null;


        node = null;
        try {
            // JPQL.g:947:4: (n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:947:6: n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET
            {
            n=(Token)match(input,NULLIF,FOLLOW_NULLIF_in_nullIfExpression5925); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5927); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5933);
            l=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_nullIfExpression5935); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5941);
            r=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression5943); if (state.failed) return node;
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
        }
        return node;
    }
    // $ANTLR end "nullIfExpression"


    // $ANTLR start "caseOperand"
    // JPQL.g:955:1: caseOperand returns [Object node] : (n= stateFieldPathExpression | n= typeDiscriminator );
    public final Object caseOperand() throws RecognitionException {
        Object node = null;

        Object n = null;


        node = null;
        try {
            // JPQL.g:957:4: (n= stateFieldPathExpression | n= typeDiscriminator )
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==KEY||LA75_0==VALUE||LA75_0==IDENT) ) {
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
                    // JPQL.g:957:6: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_caseOperand5990);
                    n=stateFieldPathExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:958:6: n= typeDiscriminator
                    {
                    pushFollow(FOLLOW_typeDiscriminator_in_caseOperand6004);
                    n=typeDiscriminator();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "caseOperand"


    // $ANTLR start "whenClause"
    // JPQL.g:961:1: whenClause returns [Object node] : w= WHEN c= conditionalExpression THEN a= scalarExpression ;
    public final Object whenClause() throws RecognitionException {
        Object node = null;

        Token w=null;
        Object c = null;

        Object a = null;


        node = null;
        try {
            // JPQL.g:963:4: (w= WHEN c= conditionalExpression THEN a= scalarExpression )
            // JPQL.g:963:6: w= WHEN c= conditionalExpression THEN a= scalarExpression
            {
            w=(Token)match(input,WHEN,FOLLOW_WHEN_in_whenClause6039); if (state.failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whenClause6045);
            c=conditionalExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,THEN,FOLLOW_THEN_in_whenClause6047); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_whenClause6053);
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
        }
        return node;
    }
    // $ANTLR end "whenClause"


    // $ANTLR start "simpleWhenClause"
    // JPQL.g:970:1: simpleWhenClause returns [Object node] : w= WHEN c= scalarExpression THEN a= scalarExpression ;
    public final Object simpleWhenClause() throws RecognitionException {
        Object node = null;

        Token w=null;
        Object c = null;

        Object a = null;


        node = null;
        try {
            // JPQL.g:972:4: (w= WHEN c= scalarExpression THEN a= scalarExpression )
            // JPQL.g:972:6: w= WHEN c= scalarExpression THEN a= scalarExpression
            {
            w=(Token)match(input,WHEN,FOLLOW_WHEN_in_simpleWhenClause6095); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause6101);
            c=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,THEN,FOLLOW_THEN_in_simpleWhenClause6103); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause6109);
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
        }
        return node;
    }
    // $ANTLR end "simpleWhenClause"


    // $ANTLR start "variableOrSingleValuedPath"
    // JPQL.g:979:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );
    public final Object variableOrSingleValuedPath() throws RecognitionException {
        Object node = null;

        Object n = null;


        node = null;
        try {
            // JPQL.g:981:5: (n= singleValuedPathExpression | n= variableAccessOrTypeConstant )
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
                    NoViableAltException nvae =
                        new NoViableAltException("", 76, 1, input);

                    throw nvae;
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
                    // JPQL.g:981:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6146);
                    n=singleValuedPathExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:982:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6160);
                    n=variableAccessOrTypeConstant();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "variableOrSingleValuedPath"


    // $ANTLR start "stringPrimary"
    // JPQL.g:985:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );
    public final Object stringPrimary() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:987:5: (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression )
            int alt77=4;
            alt77 = dfa77.predict(input);
            switch (alt77) {
                case 1 :
                    // JPQL.g:987:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_stringPrimary6192);
                    n=literalString();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:988:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_stringPrimary6206);
                    n=functionsReturningStrings();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:989:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_stringPrimary6220);
                    n=inputParameter();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:990:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_stringPrimary6234);
                    n=stateFieldPathExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "stringPrimary"


    // $ANTLR start "literal"
    // JPQL.g:995:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );
    public final Object literal() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:997:5: (n= literalNumeric | n= literalBoolean | n= literalString )
            int alt78=3;
            switch ( input.LA(1) ) {
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
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
                    // JPQL.g:997:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_literal6268);
                    n=literalNumeric();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:998:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_literal6282);
                    n=literalBoolean();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:999:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_literal6296);
                    n=literalString();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "literal"


    // $ANTLR start "literalNumeric"
    // JPQL.g:1002:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );
    public final Object literalNumeric() throws RecognitionException {
        Object node = null;

        Token i=null;
        Token l=null;
        Token f=null;
        Token d=null;

         node = null; 
        try {
            // JPQL.g:1004:5: (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL )
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
                    // JPQL.g:1004:7: i= INTEGER_LITERAL
                    {
                    i=(Token)match(input,INTEGER_LITERAL,FOLLOW_INTEGER_LITERAL_in_literalNumeric6326); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       
                                  node = factory.newIntegerLiteral(i.getLine(), i.getCharPositionInLine(), 
                                                                   Integer.valueOf(i.getText())); 
                              
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1009:7: l= LONG_LITERAL
                    {
                    l=(Token)match(input,LONG_LITERAL,FOLLOW_LONG_LITERAL_in_literalNumeric6342); if (state.failed) return node;
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
                    // JPQL.g:1017:7: f= FLOAT_LITERAL
                    {
                    f=(Token)match(input,FLOAT_LITERAL,FOLLOW_FLOAT_LITERAL_in_literalNumeric6363); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       
                                  node = factory.newFloatLiteral(f.getLine(), f.getCharPositionInLine(),
                                                                 Float.valueOf(f.getText()));
                              
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:1022:7: d= DOUBLE_LITERAL
                    {
                    d=(Token)match(input,DOUBLE_LITERAL,FOLLOW_DOUBLE_LITERAL_in_literalNumeric6383); if (state.failed) return node;
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
        }
        return node;
    }
    // $ANTLR end "literalNumeric"


    // $ANTLR start "literalBoolean"
    // JPQL.g:1029:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );
    public final Object literalBoolean() throws RecognitionException {
        Object node = null;

        Token t=null;
        Token f=null;

         node = null; 
        try {
            // JPQL.g:1031:5: (t= TRUE | f= FALSE )
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
                    // JPQL.g:1031:7: t= TRUE
                    {
                    t=(Token)match(input,TRUE,FOLLOW_TRUE_in_literalBoolean6421); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newBooleanLiteral(t.getLine(), t.getCharPositionInLine(), Boolean.TRUE); 
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1033:7: f= FALSE
                    {
                    f=(Token)match(input,FALSE,FOLLOW_FALSE_in_literalBoolean6443); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newBooleanLiteral(f.getLine(), f.getCharPositionInLine(), Boolean.FALSE); 
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
        }
        return node;
    }
    // $ANTLR end "literalBoolean"


    // $ANTLR start "literalString"
    // JPQL.g:1037:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );
    public final Object literalString() throws RecognitionException {
        Object node = null;

        Token d=null;
        Token s=null;

         node = null; 
        try {
            // JPQL.g:1039:5: (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED )
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
                    // JPQL.g:1039:7: d= STRING_LITERAL_DOUBLE_QUOTED
                    {
                    d=(Token)match(input,STRING_LITERAL_DOUBLE_QUOTED,FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6482); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       
                                  node = factory.newStringLiteral(d.getLine(), d.getCharPositionInLine(), 
                                                                  convertStringLiteral(d.getText())); 
                              
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1044:7: s= STRING_LITERAL_SINGLE_QUOTED
                    {
                    s=(Token)match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6503); if (state.failed) return node;
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
        }
        return node;
    }
    // $ANTLR end "literalString"


    // $ANTLR start "literalTemporal"
    // JPQL.g:1051:1: literalTemporal returns [Object node] : (d= DATE_LITERAL | d= TIME_LITERAL | d= TIMESTAMP_LITERAL );
    public final Object literalTemporal() throws RecognitionException {
        Object node = null;

        Token d=null;

         node = null; 
        try {
            // JPQL.g:1053:5: (d= DATE_LITERAL | d= TIME_LITERAL | d= TIMESTAMP_LITERAL )
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
                    // JPQL.g:1053:7: d= DATE_LITERAL
                    {
                    d=(Token)match(input,DATE_LITERAL,FOLLOW_DATE_LITERAL_in_literalTemporal6543); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = factory.newDateLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); 
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1054:7: d= TIME_LITERAL
                    {
                    d=(Token)match(input,TIME_LITERAL,FOLLOW_TIME_LITERAL_in_literalTemporal6557); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = factory.newTimeLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); 
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:1055:7: d= TIMESTAMP_LITERAL
                    {
                    d=(Token)match(input,TIMESTAMP_LITERAL,FOLLOW_TIMESTAMP_LITERAL_in_literalTemporal6571); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = factory.newTimeStampLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); 
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
        }
        return node;
    }
    // $ANTLR end "literalTemporal"


    // $ANTLR start "inputParameter"
    // JPQL.g:1058:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );
    public final Object inputParameter() throws RecognitionException {
        Object node = null;

        Token p=null;
        Token n=null;

         node = null; 
        try {
            // JPQL.g:1060:5: (p= POSITIONAL_PARAM | n= NAMED_PARAM )
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
                    // JPQL.g:1060:7: p= POSITIONAL_PARAM
                    {
                    p=(Token)match(input,POSITIONAL_PARAM,FOLLOW_POSITIONAL_PARAM_in_inputParameter6601); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       
                                  // skip the leading ?
                                  String text = p.getText().substring(1);
                                  node = factory.newPositionalParameter(p.getLine(), p.getCharPositionInLine(), text); 
                              
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1066:7: n= NAMED_PARAM
                    {
                    n=(Token)match(input,NAMED_PARAM,FOLLOW_NAMED_PARAM_in_inputParameter6621); if (state.failed) return node;
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
        }
        return node;
    }
    // $ANTLR end "inputParameter"


    // $ANTLR start "functionsReturningNumerics"
    // JPQL.g:1074:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index | n= func );
    public final Object functionsReturningNumerics() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:1076:5: (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index | n= func )
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
                    // JPQL.g:1076:7: n= abs
                    {
                    pushFollow(FOLLOW_abs_in_functionsReturningNumerics6661);
                    n=abs();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1077:7: n= length
                    {
                    pushFollow(FOLLOW_length_in_functionsReturningNumerics6675);
                    n=length();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:1078:7: n= mod
                    {
                    pushFollow(FOLLOW_mod_in_functionsReturningNumerics6689);
                    n=mod();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:1079:7: n= sqrt
                    {
                    pushFollow(FOLLOW_sqrt_in_functionsReturningNumerics6703);
                    n=sqrt();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 5 :
                    // JPQL.g:1080:7: n= locate
                    {
                    pushFollow(FOLLOW_locate_in_functionsReturningNumerics6717);
                    n=locate();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 6 :
                    // JPQL.g:1081:7: n= size
                    {
                    pushFollow(FOLLOW_size_in_functionsReturningNumerics6731);
                    n=size();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 7 :
                    // JPQL.g:1082:7: n= index
                    {
                    pushFollow(FOLLOW_index_in_functionsReturningNumerics6745);
                    n=index();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 8 :
                    // JPQL.g:1083:7: n= func
                    {
                    pushFollow(FOLLOW_func_in_functionsReturningNumerics6759);
                    n=func();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "functionsReturningNumerics"


    // $ANTLR start "functionsReturningDatetime"
    // JPQL.g:1086:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );
    public final Object functionsReturningDatetime() throws RecognitionException {
        Object node = null;

        Token d=null;
        Token t=null;
        Token ts=null;

         node = null; 
        try {
            // JPQL.g:1088:5: (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP )
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
                    // JPQL.g:1088:7: d= CURRENT_DATE
                    {
                    d=(Token)match(input,CURRENT_DATE,FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6789); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newCurrentDate(d.getLine(), d.getCharPositionInLine()); 
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1090:7: t= CURRENT_TIME
                    {
                    t=(Token)match(input,CURRENT_TIME,FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6810); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newCurrentTime(t.getLine(), t.getCharPositionInLine()); 
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:1092:7: ts= CURRENT_TIMESTAMP
                    {
                    ts=(Token)match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6830); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newCurrentTimestamp(ts.getLine(), ts.getCharPositionInLine()); 
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
        }
        return node;
    }
    // $ANTLR end "functionsReturningDatetime"


    // $ANTLR start "functionsReturningStrings"
    // JPQL.g:1096:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );
    public final Object functionsReturningStrings() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:1098:5: (n= concat | n= substring | n= trim | n= upper | n= lower )
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
                    // JPQL.g:1098:7: n= concat
                    {
                    pushFollow(FOLLOW_concat_in_functionsReturningStrings6870);
                    n=concat();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1099:7: n= substring
                    {
                    pushFollow(FOLLOW_substring_in_functionsReturningStrings6884);
                    n=substring();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:1100:7: n= trim
                    {
                    pushFollow(FOLLOW_trim_in_functionsReturningStrings6898);
                    n=trim();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:1101:7: n= upper
                    {
                    pushFollow(FOLLOW_upper_in_functionsReturningStrings6912);
                    n=upper();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 5 :
                    // JPQL.g:1102:7: n= lower
                    {
                    pushFollow(FOLLOW_lower_in_functionsReturningStrings6926);
                    n=lower();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "functionsReturningStrings"

    protected static class concat_scope {
        List items;
    }
    protected Stack concat_stack = new Stack();


    // $ANTLR start "concat"
    // JPQL.g:1106:1: concat returns [Object node] : c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET ;
    public final Object concat() throws RecognitionException {
        concat_stack.push(new concat_scope());
        Object node = null;

        Token c=null;
        Object firstArg = null;

        Object arg = null;


         
            node = null;
            ((concat_scope)concat_stack.peek()).items = new ArrayList();

        try {
            // JPQL.g:1114:5: (c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET )
            // JPQL.g:1114:7: c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET
            {
            c=(Token)match(input,CONCAT,FOLLOW_CONCAT_in_concat6961); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_concat6972); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_concat6987);
            firstArg=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
              ((concat_scope)concat_stack.peek()).items.add(firstArg);
            }
            // JPQL.g:1116:75: ( COMMA arg= scalarExpression )+
            int cnt87=0;
            loop87:
            do {
                int alt87=2;
                int LA87_0 = input.LA(1);

                if ( (LA87_0==COMMA) ) {
                    alt87=1;
                }


                switch (alt87) {
            	case 1 :
            	    // JPQL.g:1116:76: COMMA arg= scalarExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_concat6992); if (state.failed) return node;
            	    pushFollow(FOLLOW_scalarExpression_in_concat6998);
            	    arg=scalarExpression();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	      ((concat_scope)concat_stack.peek()).items.add(arg);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt87 >= 1 ) break loop87;
            	    if (state.backtracking>0) {state.failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(87, input);
                        throw eee;
                }
                cnt87++;
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_concat7012); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newConcat(c.getLine(), c.getCharPositionInLine(), ((concat_scope)concat_stack.peek()).items); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            concat_stack.pop();
        }
        return node;
    }
    // $ANTLR end "concat"


    // $ANTLR start "substring"
    // JPQL.g:1121:1: substring returns [Object node] : s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= scalarExpression ( COMMA lengthNode= scalarExpression )? RIGHT_ROUND_BRACKET ;
    public final Object substring() throws RecognitionException {
        Object node = null;

        Token s=null;
        Object string = null;

        Object start = null;

        Object lengthNode = null;


         
            node = null;
            lengthNode = null;

        try {
            // JPQL.g:1126:5: (s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= scalarExpression ( COMMA lengthNode= scalarExpression )? RIGHT_ROUND_BRACKET )
            // JPQL.g:1126:7: s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= scalarExpression ( COMMA lengthNode= scalarExpression )? RIGHT_ROUND_BRACKET
            {
            s=(Token)match(input,SUBSTRING,FOLLOW_SUBSTRING_in_substring7050); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_substring7063); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_substring7077);
            string=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring7079); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_substring7093);
            start=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:1130:9: ( COMMA lengthNode= scalarExpression )?
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==COMMA) ) {
                alt88=1;
            }
            switch (alt88) {
                case 1 :
                    // JPQL.g:1130:10: COMMA lengthNode= scalarExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_substring7104); if (state.failed) return node;
                    pushFollow(FOLLOW_scalarExpression_in_substring7110);
                    lengthNode=scalarExpression();

                    state._fsp--;
                    if (state.failed) return node;

                    }
                    break;

            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_substring7122); if (state.failed) return node;
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
        }
        return node;
    }
    // $ANTLR end "substring"


    // $ANTLR start "trim"
    // JPQL.g:1143:1: trim returns [Object node] : t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object trim() throws RecognitionException {
        Object node = null;

        Token t=null;
        TrimSpecification trimSpecIndicator = null;

        Object trimCharNode = null;

        Object n = null;


         
            node = null;
            trimSpecIndicator = TrimSpecification.BOTH;

        try {
            // JPQL.g:1148:5: (t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1148:7: t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET
            {
            t=(Token)match(input,TRIM,FOLLOW_TRIM_in_trim7160); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_trim7170); if (state.failed) return node;
            // JPQL.g:1150:9: (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )?
            int alt89=2;
            alt89 = dfa89.predict(input);
            switch (alt89) {
                case 1 :
                    // JPQL.g:1150:10: trimSpecIndicator= trimSpec trimCharNode= trimChar FROM
                    {
                    pushFollow(FOLLOW_trimSpec_in_trim7186);
                    trimSpecIndicator=trimSpec();

                    state._fsp--;
                    if (state.failed) return node;
                    pushFollow(FOLLOW_trimChar_in_trim7192);
                    trimCharNode=trimChar();

                    state._fsp--;
                    if (state.failed) return node;
                    match(input,FROM,FOLLOW_FROM_in_trim7194); if (state.failed) return node;

                    }
                    break;

            }

            pushFollow(FOLLOW_stringPrimary_in_trim7210);
            n=stringPrimary();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_trim7220); if (state.failed) return node;
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
        }
        return node;
    }
    // $ANTLR end "trim"


    // $ANTLR start "trimSpec"
    // JPQL.g:1159:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );
    public final TrimSpecification trimSpec() throws RecognitionException {
        TrimSpecification trimSpec = null;

         trimSpec = TrimSpecification.BOTH; 
        try {
            // JPQL.g:1161:5: ( LEADING | TRAILING | BOTH | )
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
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
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
                    // JPQL.g:1161:7: LEADING
                    {
                    match(input,LEADING,FOLLOW_LEADING_in_trimSpec7256); if (state.failed) return trimSpec;
                    if ( state.backtracking==0 ) {
                       trimSpec = TrimSpecification.LEADING; 
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1163:7: TRAILING
                    {
                    match(input,TRAILING,FOLLOW_TRAILING_in_trimSpec7274); if (state.failed) return trimSpec;
                    if ( state.backtracking==0 ) {
                       trimSpec = TrimSpecification.TRAILING; 
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:1165:7: BOTH
                    {
                    match(input,BOTH,FOLLOW_BOTH_in_trimSpec7292); if (state.failed) return trimSpec;
                    if ( state.backtracking==0 ) {
                       trimSpec = TrimSpecification.BOTH; 
                    }

                    }
                    break;
                case 4 :
                    // JPQL.g:1168:5: 
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
        }
        return trimSpec;
    }
    // $ANTLR end "trimSpec"


    // $ANTLR start "trimChar"
    // JPQL.g:1171:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );
    public final Object trimChar() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:1173:5: (n= literalString | n= inputParameter | )
            int alt91=3;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt91=1;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
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
                    // JPQL.g:1173:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_trimChar7352);
                    n=literalString();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1174:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_trimChar7366);
                    n=inputParameter();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:1176:5: 
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
        }
        return node;
    }
    // $ANTLR end "trimChar"


    // $ANTLR start "upper"
    // JPQL.g:1178:1: upper returns [Object node] : u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object upper() throws RecognitionException {
        Object node = null;

        Token u=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:1180:5: (u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1180:7: u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            u=(Token)match(input,UPPER,FOLLOW_UPPER_in_upper7409); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_upper7411); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_upper7417);
            n=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_upper7419); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newUpper(u.getLine(), u.getCharPositionInLine(), n); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "upper"


    // $ANTLR start "lower"
    // JPQL.g:1184:1: lower returns [Object node] : l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object lower() throws RecognitionException {
        Object node = null;

        Token l=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:1186:5: (l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1186:7: l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            l=(Token)match(input,LOWER,FOLLOW_LOWER_in_lower7457); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_lower7459); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_lower7465);
            n=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_lower7467); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newLower(l.getLine(), l.getCharPositionInLine(), n); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "lower"


    // $ANTLR start "abs"
    // JPQL.g:1191:1: abs returns [Object node] : a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object abs() throws RecognitionException {
        Object node = null;

        Token a=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:1193:5: (a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1193:7: a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            a=(Token)match(input,ABS,FOLLOW_ABS_in_abs7506); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_abs7508); if (state.failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_abs7514);
            n=simpleArithmeticExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_abs7516); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newAbs(a.getLine(), a.getCharPositionInLine(), n); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "abs"


    // $ANTLR start "length"
    // JPQL.g:1197:1: length returns [Object node] : l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object length() throws RecognitionException {
        Object node = null;

        Token l=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:1199:5: (l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1199:7: l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            l=(Token)match(input,LENGTH,FOLLOW_LENGTH_in_length7554); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_length7556); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_length7562);
            n=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_length7564); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newLength(l.getLine(), l.getCharPositionInLine(), n); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "length"


    // $ANTLR start "locate"
    // JPQL.g:1203:1: locate returns [Object node] : l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= scalarExpression )? RIGHT_ROUND_BRACKET ;
    public final Object locate() throws RecognitionException {
        Object node = null;

        Token l=null;
        Object pattern = null;

        Object n = null;

        Object startPos = null;


         
            node = null; 

        try {
            // JPQL.g:1207:5: (l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= scalarExpression )? RIGHT_ROUND_BRACKET )
            // JPQL.g:1207:7: l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= scalarExpression )? RIGHT_ROUND_BRACKET
            {
            l=(Token)match(input,LOCATE,FOLLOW_LOCATE_in_locate7602); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_locate7612); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_locate7627);
            pattern=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_locate7629); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_locate7635);
            n=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:1210:9: ( COMMA startPos= scalarExpression )?
            int alt92=2;
            int LA92_0 = input.LA(1);

            if ( (LA92_0==COMMA) ) {
                alt92=1;
            }
            switch (alt92) {
                case 1 :
                    // JPQL.g:1210:11: COMMA startPos= scalarExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_locate7647); if (state.failed) return node;
                    pushFollow(FOLLOW_scalarExpression_in_locate7653);
                    startPos=scalarExpression();

                    state._fsp--;
                    if (state.failed) return node;

                    }
                    break;

            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_locate7666); if (state.failed) return node;
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
        }
        return node;
    }
    // $ANTLR end "locate"


    // $ANTLR start "size"
    // JPQL.g:1218:1: size returns [Object node] : s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ;
    public final Object size() throws RecognitionException {
        Object node = null;

        Token s=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:1220:5: (s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1220:7: s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)match(input,SIZE,FOLLOW_SIZE_in_size7704); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_size7715); if (state.failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_size7721);
            n=collectionValuedPathExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_size7723); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newSize(s.getLine(), s.getCharPositionInLine(), n);
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "size"


    // $ANTLR start "mod"
    // JPQL.g:1225:1: mod returns [Object node] : m= MOD LEFT_ROUND_BRACKET left= scalarExpression COMMA right= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object mod() throws RecognitionException {
        Object node = null;

        Token m=null;
        Object left = null;

        Object right = null;


         
            node = null; 

        try {
            // JPQL.g:1229:5: (m= MOD LEFT_ROUND_BRACKET left= scalarExpression COMMA right= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1229:7: m= MOD LEFT_ROUND_BRACKET left= scalarExpression COMMA right= scalarExpression RIGHT_ROUND_BRACKET
            {
            m=(Token)match(input,MOD,FOLLOW_MOD_in_mod7761); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mod7763); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_mod7777);
            left=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_mod7779); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_mod7794);
            right=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mod7804); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newMod(m.getLine(), m.getCharPositionInLine(), left, right); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "mod"


    // $ANTLR start "sqrt"
    // JPQL.g:1236:1: sqrt returns [Object node] : s= SQRT LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object sqrt() throws RecognitionException {
        Object node = null;

        Token s=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:1238:5: (s= SQRT LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1238:7: s= SQRT LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)match(input,SQRT,FOLLOW_SQRT_in_sqrt7842); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7853); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_sqrt7859);
            n=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7861); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newSqrt(s.getLine(), s.getCharPositionInLine(), n); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "sqrt"


    // $ANTLR start "index"
    // JPQL.g:1243:1: index returns [Object node] : s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object index() throws RecognitionException {
        Object node = null;

        Token s=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:1245:5: (s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // JPQL.g:1245:7: s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            s=(Token)match(input,INDEX,FOLLOW_INDEX_in_index7903); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_index7905); if (state.failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_index7911);
            n=variableAccessOrTypeConstant();

            state._fsp--;
            if (state.failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_index7913); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               node = factory.newIndex(s.getLine(), s.getCharPositionInLine(), n); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return node;
    }
    // $ANTLR end "index"

    protected static class func_scope {
        List exprs;
    }
    protected Stack func_stack = new Stack();


    // $ANTLR start "func"
    // JPQL.g:1250:1: func returns [Object node] : f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET ;
    public final Object func() throws RecognitionException {
        func_stack.push(new func_scope());
        Object node = null;

        Token f=null;
        Token name=null;
        Object n = null;


         
            node = null; 
            ((func_scope)func_stack.peek()).exprs = new ArrayList();

        try {
            // JPQL.g:1258:5: (f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET )
            // JPQL.g:1258:7: f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET
            {
            f=(Token)match(input,FUNC,FOLLOW_FUNC_in_func7955); if (state.failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_func7957); if (state.failed) return node;
            name=(Token)match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_func7969); if (state.failed) return node;
            // JPQL.g:1260:7: ( COMMA n= newValue )*
            loop93:
            do {
                int alt93=2;
                int LA93_0 = input.LA(1);

                if ( (LA93_0==COMMA) ) {
                    alt93=1;
                }


                switch (alt93) {
            	case 1 :
            	    // JPQL.g:1260:8: COMMA n= newValue
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_func7978); if (state.failed) return node;
            	    pushFollow(FOLLOW_newValue_in_func7984);
            	    n=newValue();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {

            	                  ((func_scope)func_stack.peek()).exprs.add(n);
            	                
            	    }

            	    }
            	    break;

            	default :
            	    break loop93;
                }
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_func8016); if (state.failed) return node;
            if ( state.backtracking==0 ) {
              node = factory.newFunc(f.getLine(), f.getCharPositionInLine(), name.getText(), ((func_scope)func_stack.peek()).exprs);
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            func_stack.pop();
        }
        return node;
    }
    // $ANTLR end "func"


    // $ANTLR start "subquery"
    // JPQL.g:1269:1: subquery returns [Object node] : select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? ;
    public final Object subquery() throws RecognitionException {
        Object node = null;

        Object select = null;

        Object from = null;

        Object where = null;

        Object groupBy = null;

        Object having = null;


         
            node = null; 

        try {
            // JPQL.g:1273:5: (select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? )
            // JPQL.g:1273:7: select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )?
            {
            pushFollow(FOLLOW_simpleSelectClause_in_subquery8054);
            select=simpleSelectClause();

            state._fsp--;
            if (state.failed) return node;
            pushFollow(FOLLOW_subqueryFromClause_in_subquery8069);
            from=subqueryFromClause();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:1275:7: (where= whereClause )?
            int alt94=2;
            int LA94_0 = input.LA(1);

            if ( (LA94_0==WHERE) ) {
                alt94=1;
            }
            switch (alt94) {
                case 1 :
                    // JPQL.g:1275:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_subquery8084);
                    where=whereClause();

                    state._fsp--;
                    if (state.failed) return node;

                    }
                    break;

            }

            // JPQL.g:1276:7: (groupBy= groupByClause )?
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==GROUP) ) {
                alt95=1;
            }
            switch (alt95) {
                case 1 :
                    // JPQL.g:1276:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_subquery8099);
                    groupBy=groupByClause();

                    state._fsp--;
                    if (state.failed) return node;

                    }
                    break;

            }

            // JPQL.g:1277:7: (having= havingClause )?
            int alt96=2;
            int LA96_0 = input.LA(1);

            if ( (LA96_0==HAVING) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // JPQL.g:1277:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_subquery8115);
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
        }
        return node;
    }
    // $ANTLR end "subquery"

    protected static class simpleSelectClause_scope {
        boolean distinct;
    }
    protected Stack simpleSelectClause_stack = new Stack();


    // $ANTLR start "simpleSelectClause"
    // JPQL.g:1284:1: simpleSelectClause returns [Object node] : s= SELECT ( DISTINCT )? n= simpleSelectExpression ;
    public final Object simpleSelectClause() throws RecognitionException {
        simpleSelectClause_stack.push(new simpleSelectClause_scope());
        Object node = null;

        Token s=null;
        Object n = null;


         
            node = null; 
            ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = false;

        try {
            // JPQL.g:1292:5: (s= SELECT ( DISTINCT )? n= simpleSelectExpression )
            // JPQL.g:1292:7: s= SELECT ( DISTINCT )? n= simpleSelectExpression
            {
            s=(Token)match(input,SELECT,FOLLOW_SELECT_in_simpleSelectClause8158); if (state.failed) return node;
            // JPQL.g:1292:16: ( DISTINCT )?
            int alt97=2;
            alt97 = dfa97.predict(input);
            switch (alt97) {
                case 1 :
                    // JPQL.g:1292:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_simpleSelectClause8161); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = true; 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_simpleSelectExpression_in_simpleSelectClause8177);
            n=simpleSelectExpression();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {

                          List exprs = new ArrayList();
                          exprs.add(n);
                          node = factory.newSelectClause(s.getLine(), s.getCharPositionInLine(), 
                                                         ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct, exprs);
                      
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            simpleSelectClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "simpleSelectClause"


    // $ANTLR start "simpleSelectExpression"
    // JPQL.g:1302:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );
    public final Object simpleSelectExpression() throws RecognitionException {
        Object node = null;

        Object n = null;


         node = null; 
        try {
            // JPQL.g:1304:5: (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant )
            int alt98=3;
            alt98 = dfa98.predict(input);
            switch (alt98) {
                case 1 :
                    // JPQL.g:1304:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_simpleSelectExpression8217);
                    n=singleValuedPathExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1305:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_simpleSelectExpression8232);
                    n=aggregateExpression();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:1306:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression8247);
                    n=variableAccessOrTypeConstant();

                    state._fsp--;
                    if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                      node = n;
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
        }
        return node;
    }
    // $ANTLR end "simpleSelectExpression"

    protected static class subqueryFromClause_scope {
        List varDecls;
    }
    protected Stack subqueryFromClause_stack = new Stack();


    // $ANTLR start "subqueryFromClause"
    // JPQL.g:1310:1: subqueryFromClause returns [Object node] : f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )* ;
    public final Object subqueryFromClause() throws RecognitionException {
        subqueryFromClause_stack.push(new subqueryFromClause_scope());
        Object node = null;

        Token f=null;
        Object c = null;


         
            node = null; 
            ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls = new ArrayList();

        try {
            // JPQL.g:1318:5: (f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )* )
            // JPQL.g:1318:7: f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )*
            {
            f=(Token)match(input,FROM,FOLLOW_FROM_in_subqueryFromClause8282); if (state.failed) return node;
            pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8284);
            subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:1319:9: ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )*
            loop99:
            do {
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
            	    // JPQL.g:1320:13: COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_subqueryFromClause8311); if (state.failed) return node;
            	    pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8330);
            	    subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);

            	    state._fsp--;
            	    if (state.failed) return node;

            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:1322:19: c= collectionMemberDeclaration
            	    {
            	    pushFollow(FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8356);
            	    c=collectionMemberDeclaration();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	      ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls.add(c);
            	    }

            	    }
            	    break;

            	default :
            	    break loop99;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               node = factory.newFromClause(f.getLine(), f.getCharPositionInLine(), ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            subqueryFromClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "subqueryFromClause"


    // $ANTLR start "subselectIdentificationVariableDeclaration"
    // JPQL.g:1327:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT (node= join )* | n= collectionMemberDeclaration );
    public final void subselectIdentificationVariableDeclaration(List varDecls) throws RecognitionException {
        Token i=null;
        Object n = null;


         Object node = null; 
        try {
            // JPQL.g:1329:5: ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT (node= join )* | n= collectionMemberDeclaration )
            int alt102=3;
            alt102 = dfa102.predict(input);
            switch (alt102) {
                case 1 :
                    // JPQL.g:1329:7: identificationVariableDeclaration[varDecls]
                    {
                    pushFollow(FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8403);
                    identificationVariableDeclaration(varDecls);

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // JPQL.g:1330:7: n= associationPathExpression ( AS )? i= IDENT (node= join )*
                    {
                    pushFollow(FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8416);
                    n=associationPathExpression();

                    state._fsp--;
                    if (state.failed) return ;
                    // JPQL.g:1330:37: ( AS )?
                    int alt100=2;
                    int LA100_0 = input.LA(1);

                    if ( (LA100_0==AS) ) {
                        alt100=1;
                    }
                    switch (alt100) {
                        case 1 :
                            // JPQL.g:1330:38: AS
                            {
                            match(input,AS,FOLLOW_AS_in_subselectIdentificationVariableDeclaration8419); if (state.failed) return ;

                            }
                            break;

                    }

                    i=(Token)match(input,IDENT,FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8425); if (state.failed) return ;

                    if ( state.backtracking==0 ) {
                       
                            varDecls.add(factory.newVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                n, i.getText())); 
                              
                    }

                    // JPQL.g:1330:51: (node= join )*
                    loop101:
                    do {
                        int alt101=2;
                        alt101 = dfa101.predict(input);
                        switch (alt101) {
                    	case 1 :
                    	    // JPQL.g:1330:52: node= join
                    	    {
                    	    pushFollow(FOLLOW_join_in_subselectIdentificationVariableDeclaration8428);
                    	    node = join();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       varDecls.add(node); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop101;
                        }
                    } while (true);

                    }
                    break;
                case 3 :
                    // JPQL.g:1335:7: n= collectionMemberDeclaration
                    {
                    pushFollow(FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8455);
                    n=collectionMemberDeclaration();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       varDecls.add(n); 
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
        }
        return ;
    }
    // $ANTLR end "subselectIdentificationVariableDeclaration"

    protected static class orderByClause_scope {
        List items;
    }
    protected Stack orderByClause_stack = new Stack();


    // $ANTLR start "orderByClause"
    // JPQL.g:1338:1: orderByClause returns [Object node] : o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* ;
    public final Object orderByClause() throws RecognitionException {
        orderByClause_stack.push(new orderByClause_scope());
        Object node = null;

        Token o=null;
        Object n = null;


         
            node = null; 
            ((orderByClause_scope)orderByClause_stack.peek()).items = new ArrayList();

        try {
            // JPQL.g:1346:5: (o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* )
            // JPQL.g:1346:7: o= ORDER BY n= orderByItem ( COMMA n= orderByItem )*
            {
            o=(Token)match(input,ORDER,FOLLOW_ORDER_in_orderByClause8488); if (state.failed) return node;
            match(input,BY,FOLLOW_BY_in_orderByClause8490); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               setAggregatesAllowed(true); 
            }
            pushFollow(FOLLOW_orderByItem_in_orderByClause8507);
            n=orderByItem();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
               ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1348:9: ( COMMA n= orderByItem )*
            loop103:
            do {
                int alt103=2;
                int LA103_0 = input.LA(1);

                if ( (LA103_0==COMMA) ) {
                    alt103=1;
                }


                switch (alt103) {
            	case 1 :
            	    // JPQL.g:1348:10: COMMA n= orderByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_orderByClause8522); if (state.failed) return node;
            	    pushFollow(FOLLOW_orderByItem_in_orderByClause8528);
            	    n=orderByItem();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop103;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               
                          setAggregatesAllowed(false);
                          node = factory.newOrderByClause(o.getLine(), o.getCharPositionInLine(), ((orderByClause_scope)orderByClause_stack.peek()).items);
                      
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            orderByClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "orderByClause"


    // $ANTLR start "orderByItem"
    // JPQL.g:1355:1: orderByItem returns [Object node] : n= scalarExpression (a= ASC | d= DESC | ) ;
    public final Object orderByItem() throws RecognitionException {
        Object node = null;

        Token a=null;
        Token d=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:1357:5: (n= scalarExpression (a= ASC | d= DESC | ) )
            // JPQL.g:1357:7: n= scalarExpression (a= ASC | d= DESC | )
            {
            pushFollow(FOLLOW_scalarExpression_in_orderByItem8574);
            n=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            // JPQL.g:1358:9: (a= ASC | d= DESC | )
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
                    // JPQL.g:1358:11: a= ASC
                    {
                    a=(Token)match(input,ASC,FOLLOW_ASC_in_orderByItem8588); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), n); 
                    }

                    }
                    break;
                case 2 :
                    // JPQL.g:1360:11: d= DESC
                    {
                    d=(Token)match(input,DESC,FOLLOW_DESC_in_orderByItem8617); if (state.failed) return node;
                    if ( state.backtracking==0 ) {
                       node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), n); 
                    }

                    }
                    break;
                case 3 :
                    // JPQL.g:1363:13: 
                    {
                    if ( state.backtracking==0 ) {
                       node = factory.newAscOrdering(0, 0, n); 
                    }

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
        }
        return node;
    }
    // $ANTLR end "orderByItem"

    protected static class groupByClause_scope {
        List items;
    }
    protected Stack groupByClause_stack = new Stack();


    // $ANTLR start "groupByClause"
    // JPQL.g:1367:1: groupByClause returns [Object node] : g= GROUP BY n= scalarExpression ( COMMA n= scalarExpression )* ;
    public final Object groupByClause() throws RecognitionException {
        groupByClause_stack.push(new groupByClause_scope());
        Object node = null;

        Token g=null;
        Object n = null;


         
            node = null; 
            ((groupByClause_scope)groupByClause_stack.peek()).items = new ArrayList();

        try {
            // JPQL.g:1375:5: (g= GROUP BY n= scalarExpression ( COMMA n= scalarExpression )* )
            // JPQL.g:1375:7: g= GROUP BY n= scalarExpression ( COMMA n= scalarExpression )*
            {
            g=(Token)match(input,GROUP,FOLLOW_GROUP_in_groupByClause8698); if (state.failed) return node;
            match(input,BY,FOLLOW_BY_in_groupByClause8700); if (state.failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_groupByClause8714);
            n=scalarExpression();

            state._fsp--;
            if (state.failed) return node;
            if ( state.backtracking==0 ) {
               ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1377:9: ( COMMA n= scalarExpression )*
            loop105:
            do {
                int alt105=2;
                int LA105_0 = input.LA(1);

                if ( (LA105_0==COMMA) ) {
                    alt105=1;
                }


                switch (alt105) {
            	case 1 :
            	    // JPQL.g:1377:10: COMMA n= scalarExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_groupByClause8727); if (state.failed) return node;
            	    pushFollow(FOLLOW_scalarExpression_in_groupByClause8733);
            	    n=scalarExpression();

            	    state._fsp--;
            	    if (state.failed) return node;
            	    if ( state.backtracking==0 ) {
            	       ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop105;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               node = factory.newGroupByClause(g.getLine(), g.getCharPositionInLine(), ((groupByClause_scope)groupByClause_stack.peek()).items); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            groupByClause_stack.pop();
        }
        return node;
    }
    // $ANTLR end "groupByClause"


    // $ANTLR start "havingClause"
    // JPQL.g:1382:1: havingClause returns [Object node] : h= HAVING n= conditionalExpression ;
    public final Object havingClause() throws RecognitionException {
        Object node = null;

        Token h=null;
        Object n = null;


         node = null; 
        try {
            // JPQL.g:1384:5: (h= HAVING n= conditionalExpression )
            // JPQL.g:1384:7: h= HAVING n= conditionalExpression
            {
            h=(Token)match(input,HAVING,FOLLOW_HAVING_in_havingClause8778); if (state.failed) return node;
            if ( state.backtracking==0 ) {
               setAggregatesAllowed(true); 
            }
            pushFollow(FOLLOW_conditionalExpression_in_havingClause8795);
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
        }
        return node;
    }
    // $ANTLR end "havingClause"

    // $ANTLR start synpred1_JPQL
    public final void synpred1_JPQL_fragment() throws RecognitionException {   
        // JPQL.g:648:7: ( LEFT_ROUND_BRACKET conditionalExpression )
        // JPQL.g:648:8: LEFT_ROUND_BRACKET conditionalExpression
        {
        match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_synpred1_JPQL3610); if (state.failed) return ;
        pushFollow(FOLLOW_conditionalExpression_in_synpred1_JPQL3612);
        conditionalExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_JPQL

    // Delegated rules

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


    protected DFA10 dfa10 = new DFA10(this);
    protected DFA11 dfa11 = new DFA11(this);
    protected DFA15 dfa15 = new DFA15(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA20 dfa20 = new DFA20(this);
    protected DFA22 dfa22 = new DFA22(this);
    protected DFA23 dfa23 = new DFA23(this);
    protected DFA24 dfa24 = new DFA24(this);
    protected DFA25 dfa25 = new DFA25(this);
    protected DFA26 dfa26 = new DFA26(this);
    protected DFA30 dfa30 = new DFA30(this);
    protected DFA33 dfa33 = new DFA33(this);
    protected DFA41 dfa41 = new DFA41(this);
    protected DFA42 dfa42 = new DFA42(this);
    protected DFA45 dfa45 = new DFA45(this);
    protected DFA46 dfa46 = new DFA46(this);
    protected DFA47 dfa47 = new DFA47(this);
    protected DFA48 dfa48 = new DFA48(this);
    protected DFA51 dfa51 = new DFA51(this);
    protected DFA55 dfa55 = new DFA55(this);
    protected DFA57 dfa57 = new DFA57(this);
    protected DFA60 dfa60 = new DFA60(this);
    protected DFA61 dfa61 = new DFA61(this);
    protected DFA62 dfa62 = new DFA62(this);
    protected DFA63 dfa63 = new DFA63(this);
    protected DFA64 dfa64 = new DFA64(this);
    protected DFA65 dfa65 = new DFA65(this);
    protected DFA66 dfa66 = new DFA66(this);
    protected DFA67 dfa67 = new DFA67(this);
    protected DFA68 dfa68 = new DFA68(this);
    protected DFA77 dfa77 = new DFA77(this);
    protected DFA89 dfa89 = new DFA89(this);
    protected DFA97 dfa97 = new DFA97(this);
    protected DFA98 dfa98 = new DFA98(this);
    protected DFA102 dfa102 = new DFA102(this);
    protected DFA101 dfa101 = new DFA101(this);
    static final String DFA10_eotS =
        "\13\uffff";
    static final String DFA10_eofS =
        "\13\uffff";
    static final String DFA10_minS =
        "\1\4\3\122\7\uffff";
    static final String DFA10_maxS =
        "\1\167\1\125\2\123\7\uffff";
    static final String DFA10_acceptS =
        "\4\uffff\1\1\1\uffff\1\2\4\uffff";
    static final String DFA10_specialS =
        "\13\uffff}>";
    static final String[] DFA10_transitionS = {
            "\45\4\1\2\43\4\1\3\2\4\1\1\47\4",
            "\1\4\2\uffff\1\6",
            "\1\4\1\6",
            "\1\4\1\6",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "274:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );";
        }
    }
    static final String DFA11_eotS =
        "\56\uffff";
    static final String DFA11_eofS =
        "\56\uffff";
    static final String DFA11_minS =
        "\1\4\55\uffff";
    static final String DFA11_maxS =
        "\1\151\55\uffff";
    static final String DFA11_acceptS =
        "\1\uffff\1\1\53\uffff\1\2";
    static final String DFA11_specialS =
        "\56\uffff}>";
    static final String[] DFA11_transitionS = {
            "\1\1\5\uffff\1\1\3\uffff\7\1\11\uffff\1\1\1\uffff\1\1\4\uffff"+
            "\1\1\3\uffff\1\1\2\uffff\1\1\1\uffff\3\1\1\uffff\2\1\2\uffff"+
            "\1\55\1\1\7\uffff\2\1\1\uffff\2\1\3\uffff\3\1\2\uffff\2\1\2"+
            "\uffff\1\1\2\uffff\1\1\7\uffff\2\1\2\uffff\13\1",
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
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "282:1: newValue returns [Object node] : (n= scalarExpression | n1= NULL );";
        }
    }
    static final String DFA15_eotS =
        "\61\uffff";
    static final String DFA15_eofS =
        "\61\uffff";
    static final String DFA15_minS =
        "\1\4\60\uffff";
    static final String DFA15_maxS =
        "\1\151\60\uffff";
    static final String DFA15_acceptS =
        "\1\uffff\1\1\1\2\56\uffff";
    static final String DFA15_specialS =
        "\61\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\2\5\uffff\1\2\3\uffff\7\2\2\uffff\1\1\3\uffff\1\2\2\uffff"+
            "\1\2\1\uffff\1\2\4\uffff\1\2\3\uffff\1\2\2\uffff\1\2\1\uffff"+
            "\3\2\1\uffff\3\2\2\uffff\2\2\6\uffff\2\2\1\uffff\2\2\3\uffff"+
            "\3\2\2\uffff\2\2\2\uffff\1\2\2\uffff\1\2\7\uffff\2\2\2\uffff"+
            "\13\2",
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
            "",
            "",
            "",
            ""
    };

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "330:16: ( DISTINCT )?";
        }
    }
    static final String DFA19_eotS =
        "\u0117\uffff";
    static final String DFA19_eofS =
        "\u0117\uffff";
    static final String DFA19_minS =
        "\1\4\5\123\52\uffff\5\4\u00e1\0\1\uffff";
    static final String DFA19_maxS =
        "\1\151\5\123\52\uffff\5\151\u00e1\0\1\uffff";
    static final String DFA19_acceptS =
        "\6\uffff\1\2\46\uffff\1\3\1\4\1\5\u00e6\uffff\1\1";
    static final String DFA19_specialS =
        "\65\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30"+
        "\1\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45"+
        "\1\46\1\47\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62"+
        "\1\63\1\64\1\65\1\66\1\67\1\70\1\71\1\72\1\73\1\74\1\75\1\76\1\77"+
        "\1\100\1\101\1\102\1\103\1\104\1\105\1\106\1\107\1\110\1\111\1\112"+
        "\1\113\1\114\1\115\1\116\1\117\1\120\1\121\1\122\1\123\1\124\1\125"+
        "\1\126\1\127\1\130\1\131\1\132\1\133\1\134\1\135\1\136\1\137\1\140"+
        "\1\141\1\142\1\143\1\144\1\145\1\146\1\147\1\150\1\151\1\152\1\153"+
        "\1\154\1\155\1\156\1\157\1\160\1\161\1\162\1\163\1\164\1\165\1\166"+
        "\1\167\1\170\1\171\1\172\1\173\1\174\1\175\1\176\1\177\1\u0080\1"+
        "\u0081\1\u0082\1\u0083\1\u0084\1\u0085\1\u0086\1\u0087\1\u0088\1"+
        "\u0089\1\u008a\1\u008b\1\u008c\1\u008d\1\u008e\1\u008f\1\u0090\1"+
        "\u0091\1\u0092\1\u0093\1\u0094\1\u0095\1\u0096\1\u0097\1\u0098\1"+
        "\u0099\1\u009a\1\u009b\1\u009c\1\u009d\1\u009e\1\u009f\1\u00a0\1"+
        "\u00a1\1\u00a2\1\u00a3\1\u00a4\1\u00a5\1\u00a6\1\u00a7\1\u00a8\1"+
        "\u00a9\1\u00aa\1\u00ab\1\u00ac\1\u00ad\1\u00ae\1\u00af\1\u00b0\1"+
        "\u00b1\1\u00b2\1\u00b3\1\u00b4\1\u00b5\1\u00b6\1\u00b7\1\u00b8\1"+
        "\u00b9\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00be\1\u00bf\1\u00c0\1"+
        "\u00c1\1\u00c2\1\u00c3\1\u00c4\1\u00c5\1\u00c6\1\u00c7\1\u00c8\1"+
        "\u00c9\1\u00ca\1\u00cb\1\u00cc\1\u00cd\1\u00ce\1\u00cf\1\u00d0\1"+
        "\u00d1\1\u00d2\1\u00d3\1\u00d4\1\u00d5\1\u00d6\1\u00d7\1\u00d8\1"+
        "\u00d9\1\u00da\1\u00db\1\u00dc\1\u00dd\1\u00de\1\u00df\1\u00e0\1"+
        "\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\6\5\uffff\1\1\3\uffff\3\6\1\5\3\6\6\uffff\1\57\2\uffff\1"+
            "\6\1\uffff\1\6\4\uffff\1\6\3\uffff\1\6\2\uffff\1\6\1\uffff\2"+
            "\6\1\2\1\uffff\1\3\1\6\1\56\2\uffff\1\6\1\55\6\uffff\2\6\1\uffff"+
            "\1\6\1\4\3\uffff\3\6\2\uffff\2\6\2\uffff\1\6\2\uffff\1\6\7\uffff"+
            "\2\6\2\uffff\13\6",
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
            "\1\105\5\uffff\1\70\3\uffff\1\102\1\103\1\125\1\74\1\122\1"+
            "\123\1\124\2\uffff\1\65\6\uffff\1\135\1\uffff\1\114\4\uffff"+
            "\1\113\3\uffff\1\76\2\uffff\1\106\1\uffff\1\111\1\131\1\71\1"+
            "\uffff\1\72\1\107\3\uffff\1\104\7\uffff\1\112\1\110\1\uffff"+
            "\1\126\1\73\3\uffff\1\127\1\134\1\141\2\uffff\1\130\1\77\2\uffff"+
            "\1\75\2\uffff\1\115\7\uffff\1\66\1\67\2\uffff\1\116\1\117\1"+
            "\120\1\121\1\132\1\133\1\136\1\137\1\140\1\100\1\101",
            "\1\162\5\uffff\1\145\3\uffff\1\157\1\160\1\u0082\1\151\1\177"+
            "\1\u0080\1\u0081\2\uffff\1\142\6\uffff\1\u008a\1\uffff\1\171"+
            "\4\uffff\1\170\3\uffff\1\153\2\uffff\1\163\1\uffff\1\166\1\u0086"+
            "\1\146\1\uffff\1\147\1\164\3\uffff\1\161\7\uffff\1\167\1\165"+
            "\1\uffff\1\u0083\1\150\3\uffff\1\u0084\1\u0089\1\u008e\2\uffff"+
            "\1\u0085\1\154\2\uffff\1\152\2\uffff\1\172\7\uffff\1\143\1\144"+
            "\2\uffff\1\173\1\174\1\175\1\176\1\u0087\1\u0088\1\u008b\1\u008c"+
            "\1\u008d\1\155\1\156",
            "\1\u009f\5\uffff\1\u0092\3\uffff\1\u009c\1\u009d\1\u00af\1"+
            "\u0096\1\u00ac\1\u00ad\1\u00ae\2\uffff\1\u008f\6\uffff\1\u00b7"+
            "\1\uffff\1\u00a6\4\uffff\1\u00a5\3\uffff\1\u0098\2\uffff\1\u00a0"+
            "\1\uffff\1\u00a3\1\u00b3\1\u0093\1\uffff\1\u0094\1\u00a1\3\uffff"+
            "\1\u009e\7\uffff\1\u00a4\1\u00a2\1\uffff\1\u00b0\1\u0095\3\uffff"+
            "\1\u00b1\1\u00b6\1\u00bb\2\uffff\1\u00b2\1\u0099\2\uffff\1\u0097"+
            "\2\uffff\1\u00a7\7\uffff\1\u0090\1\u0091\2\uffff\1\u00a8\1\u00a9"+
            "\1\u00aa\1\u00ab\1\u00b4\1\u00b5\1\u00b8\1\u00b9\1\u00ba\1\u009a"+
            "\1\u009b",
            "\1\u00cc\5\uffff\1\u00bf\3\uffff\1\u00c9\1\u00ca\1\u00dc\1"+
            "\u00c3\1\u00d9\1\u00da\1\u00db\2\uffff\1\u00bc\6\uffff\1\u00e4"+
            "\1\uffff\1\u00d3\4\uffff\1\u00d2\3\uffff\1\u00c5\2\uffff\1\u00cd"+
            "\1\uffff\1\u00d0\1\u00e0\1\u00c0\1\uffff\1\u00c1\1\u00ce\3\uffff"+
            "\1\u00cb\7\uffff\1\u00d1\1\u00cf\1\uffff\1\u00dd\1\u00c2\3\uffff"+
            "\1\u00de\1\u00e3\1\u00e8\2\uffff\1\u00df\1\u00c6\2\uffff\1\u00c4"+
            "\2\uffff\1\u00d4\7\uffff\1\u00bd\1\u00be\2\uffff\1\u00d5\1\u00d6"+
            "\1\u00d7\1\u00d8\1\u00e1\1\u00e2\1\u00e5\1\u00e6\1\u00e7\1\u00c7"+
            "\1\u00c8",
            "\1\u00f9\5\uffff\1\u00ec\3\uffff\1\u00f6\1\u00f7\1\u0109\1"+
            "\u00f0\1\u0106\1\u0107\1\u0108\2\uffff\1\u00e9\6\uffff\1\u0111"+
            "\1\uffff\1\u0100\4\uffff\1\u00ff\3\uffff\1\u00f2\2\uffff\1\u00fa"+
            "\1\uffff\1\u00fd\1\u010d\1\u00ed\1\uffff\1\u00ee\1\u00fb\3\uffff"+
            "\1\u00f8\7\uffff\1\u00fe\1\u00fc\1\uffff\1\u010a\1\u00ef\3\uffff"+
            "\1\u010b\1\u0110\1\u0115\2\uffff\1\u010c\1\u00f3\2\uffff\1\u00f1"+
            "\2\uffff\1\u0101\7\uffff\1\u00ea\1\u00eb\2\uffff\1\u0102\1\u0103"+
            "\1\u0104\1\u0105\1\u010e\1\u010f\1\u0112\1\u0113\1\u0114\1\u00f4"+
            "\1\u00f5",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
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

    class DFA19 extends DFA {

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
        public String getDescription() {
            return "364:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );";
        }
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
    static final String DFA20_eotS =
        "\45\uffff";
    static final String DFA20_eofS =
        "\1\1\44\uffff";
    static final String DFA20_minS =
        "\1\6\44\uffff";
    static final String DFA20_maxS =
        "\1\136\44\uffff";
    static final String DFA20_acceptS =
        "\1\uffff\1\2\42\uffff\1\1";
    static final String DFA20_specialS =
        "\45\uffff}>";
    static final String[] DFA20_transitionS = {
            "\1\1\1\uffff\2\1\1\uffff\1\1\11\uffff\1\1\2\uffff\1\1\1\uffff"+
            "\1\1\1\uffff\1\1\4\uffff\4\1\2\uffff\1\1\5\uffff\1\1\3\uffff"+
            "\1\1\3\uffff\1\1\4\uffff\2\1\10\uffff\1\1\11\uffff\5\1\1\uffff"+
            "\1\1\1\44\11\1",
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
            ""
    };

    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;

    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }

    class DFA20 extends DFA {

        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "()* loopback of 383:9: (d= DOT right= attribute )*";
        }
    }
    static final String DFA22_eotS =
        "\56\uffff";
    static final String DFA22_eofS =
        "\56\uffff";
    static final String DFA22_minS =
        "\1\4\55\uffff";
    static final String DFA22_maxS =
        "\1\151\55\uffff";
    static final String DFA22_acceptS =
        "\1\uffff\1\1\1\2\53\uffff";
    static final String DFA22_specialS =
        "\56\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\2\5\uffff\1\2\3\uffff\7\2\2\uffff\1\1\6\uffff\1\2\1\uffff"+
            "\1\2\4\uffff\1\2\3\uffff\1\2\2\uffff\1\2\1\uffff\3\2\1\uffff"+
            "\2\2\3\uffff\1\2\7\uffff\2\2\1\uffff\2\2\3\uffff\3\2\2\uffff"+
            "\2\2\2\uffff\1\2\2\uffff\1\2\7\uffff\2\2\2\uffff\13\2",
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
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "403:33: ( DISTINCT )?";
        }
    }
    static final String DFA23_eotS =
        "\56\uffff";
    static final String DFA23_eofS =
        "\56\uffff";
    static final String DFA23_minS =
        "\1\4\55\uffff";
    static final String DFA23_maxS =
        "\1\151\55\uffff";
    static final String DFA23_acceptS =
        "\1\uffff\1\1\1\2\53\uffff";
    static final String DFA23_specialS =
        "\56\uffff}>";
    static final String[] DFA23_transitionS = {
            "\1\2\5\uffff\1\2\3\uffff\7\2\2\uffff\1\1\6\uffff\1\2\1\uffff"+
            "\1\2\4\uffff\1\2\3\uffff\1\2\2\uffff\1\2\1\uffff\3\2\1\uffff"+
            "\2\2\3\uffff\1\2\7\uffff\2\2\1\uffff\2\2\3\uffff\3\2\2\uffff"+
            "\2\2\2\uffff\1\2\2\uffff\1\2\7\uffff\2\2\2\uffff\13\2",
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
            ""
    };

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }
        public String getDescription() {
            return "406:33: ( DISTINCT )?";
        }
    }
    static final String DFA24_eotS =
        "\56\uffff";
    static final String DFA24_eofS =
        "\56\uffff";
    static final String DFA24_minS =
        "\1\4\55\uffff";
    static final String DFA24_maxS =
        "\1\151\55\uffff";
    static final String DFA24_acceptS =
        "\1\uffff\1\1\1\2\53\uffff";
    static final String DFA24_specialS =
        "\56\uffff}>";
    static final String[] DFA24_transitionS = {
            "\1\2\5\uffff\1\2\3\uffff\7\2\2\uffff\1\1\6\uffff\1\2\1\uffff"+
            "\1\2\4\uffff\1\2\3\uffff\1\2\2\uffff\1\2\1\uffff\3\2\1\uffff"+
            "\2\2\3\uffff\1\2\7\uffff\2\2\1\uffff\2\2\3\uffff\3\2\2\uffff"+
            "\2\2\2\uffff\1\2\2\uffff\1\2\7\uffff\2\2\2\uffff\13\2",
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
            ""
    };

    static final short[] DFA24_eot = DFA.unpackEncodedString(DFA24_eotS);
    static final short[] DFA24_eof = DFA.unpackEncodedString(DFA24_eofS);
    static final char[] DFA24_min = DFA.unpackEncodedStringToUnsignedChars(DFA24_minS);
    static final char[] DFA24_max = DFA.unpackEncodedStringToUnsignedChars(DFA24_maxS);
    static final short[] DFA24_accept = DFA.unpackEncodedString(DFA24_acceptS);
    static final short[] DFA24_special = DFA.unpackEncodedString(DFA24_specialS);
    static final short[][] DFA24_transition;

    static {
        int numStates = DFA24_transitionS.length;
        DFA24_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA24_transition[i] = DFA.unpackEncodedString(DFA24_transitionS[i]);
        }
    }

    class DFA24 extends DFA {

        public DFA24(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 24;
            this.eot = DFA24_eot;
            this.eof = DFA24_eof;
            this.min = DFA24_min;
            this.max = DFA24_max;
            this.accept = DFA24_accept;
            this.special = DFA24_special;
            this.transition = DFA24_transition;
        }
        public String getDescription() {
            return "409:33: ( DISTINCT )?";
        }
    }
    static final String DFA25_eotS =
        "\56\uffff";
    static final String DFA25_eofS =
        "\56\uffff";
    static final String DFA25_minS =
        "\1\4\55\uffff";
    static final String DFA25_maxS =
        "\1\151\55\uffff";
    static final String DFA25_acceptS =
        "\1\uffff\1\1\1\2\53\uffff";
    static final String DFA25_specialS =
        "\56\uffff}>";
    static final String[] DFA25_transitionS = {
            "\1\2\5\uffff\1\2\3\uffff\7\2\2\uffff\1\1\6\uffff\1\2\1\uffff"+
            "\1\2\4\uffff\1\2\3\uffff\1\2\2\uffff\1\2\1\uffff\3\2\1\uffff"+
            "\2\2\3\uffff\1\2\7\uffff\2\2\1\uffff\2\2\3\uffff\3\2\2\uffff"+
            "\2\2\2\uffff\1\2\2\uffff\1\2\7\uffff\2\2\2\uffff\13\2",
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
            ""
    };

    static final short[] DFA25_eot = DFA.unpackEncodedString(DFA25_eotS);
    static final short[] DFA25_eof = DFA.unpackEncodedString(DFA25_eofS);
    static final char[] DFA25_min = DFA.unpackEncodedStringToUnsignedChars(DFA25_minS);
    static final char[] DFA25_max = DFA.unpackEncodedStringToUnsignedChars(DFA25_maxS);
    static final short[] DFA25_accept = DFA.unpackEncodedString(DFA25_acceptS);
    static final short[] DFA25_special = DFA.unpackEncodedString(DFA25_specialS);
    static final short[][] DFA25_transition;

    static {
        int numStates = DFA25_transitionS.length;
        DFA25_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA25_transition[i] = DFA.unpackEncodedString(DFA25_transitionS[i]);
        }
    }

    class DFA25 extends DFA {

        public DFA25(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 25;
            this.eot = DFA25_eot;
            this.eof = DFA25_eof;
            this.min = DFA25_min;
            this.max = DFA25_max;
            this.accept = DFA25_accept;
            this.special = DFA25_special;
            this.transition = DFA25_transition;
        }
        public String getDescription() {
            return "412:33: ( DISTINCT )?";
        }
    }
    static final String DFA26_eotS =
        "\56\uffff";
    static final String DFA26_eofS =
        "\56\uffff";
    static final String DFA26_minS =
        "\1\4\55\uffff";
    static final String DFA26_maxS =
        "\1\151\55\uffff";
    static final String DFA26_acceptS =
        "\1\uffff\1\1\1\2\53\uffff";
    static final String DFA26_specialS =
        "\56\uffff}>";
    static final String[] DFA26_transitionS = {
            "\1\2\5\uffff\1\2\3\uffff\7\2\2\uffff\1\1\6\uffff\1\2\1\uffff"+
            "\1\2\4\uffff\1\2\3\uffff\1\2\2\uffff\1\2\1\uffff\3\2\1\uffff"+
            "\2\2\3\uffff\1\2\7\uffff\2\2\1\uffff\2\2\3\uffff\3\2\2\uffff"+
            "\2\2\2\uffff\1\2\2\uffff\1\2\7\uffff\2\2\2\uffff\13\2",
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
            ""
    };

    static final short[] DFA26_eot = DFA.unpackEncodedString(DFA26_eotS);
    static final short[] DFA26_eof = DFA.unpackEncodedString(DFA26_eofS);
    static final char[] DFA26_min = DFA.unpackEncodedStringToUnsignedChars(DFA26_minS);
    static final char[] DFA26_max = DFA.unpackEncodedStringToUnsignedChars(DFA26_maxS);
    static final short[] DFA26_accept = DFA.unpackEncodedString(DFA26_acceptS);
    static final short[] DFA26_special = DFA.unpackEncodedString(DFA26_specialS);
    static final short[][] DFA26_transition;

    static {
        int numStates = DFA26_transitionS.length;
        DFA26_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA26_transition[i] = DFA.unpackEncodedString(DFA26_transitionS[i]);
        }
    }

    class DFA26 extends DFA {

        public DFA26(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 26;
            this.eot = DFA26_eot;
            this.eof = DFA26_eof;
            this.min = DFA26_min;
            this.max = DFA26_max;
            this.accept = DFA26_accept;
            this.special = DFA26_special;
            this.transition = DFA26_transition;
        }
        public String getDescription() {
            return "415:35: ( DISTINCT )?";
        }
    }
    static final String DFA30_eotS =
        "\u0114\uffff";
    static final String DFA30_eofS =
        "\u0114\uffff";
    static final String DFA30_minS =
        "\1\4\2\uffff\5\123\45\uffff\5\4\u00e1\0\1\uffff";
    static final String DFA30_maxS =
        "\1\151\2\uffff\5\123\45\uffff\5\151\u00e1\0\1\uffff";
    static final String DFA30_acceptS =
        "\1\uffff\1\1\u0111\uffff\1\2";
    static final String DFA30_specialS =
        "\62\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30"+
        "\1\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45"+
        "\1\46\1\47\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62"+
        "\1\63\1\64\1\65\1\66\1\67\1\70\1\71\1\72\1\73\1\74\1\75\1\76\1\77"+
        "\1\100\1\101\1\102\1\103\1\104\1\105\1\106\1\107\1\110\1\111\1\112"+
        "\1\113\1\114\1\115\1\116\1\117\1\120\1\121\1\122\1\123\1\124\1\125"+
        "\1\126\1\127\1\130\1\131\1\132\1\133\1\134\1\135\1\136\1\137\1\140"+
        "\1\141\1\142\1\143\1\144\1\145\1\146\1\147\1\150\1\151\1\152\1\153"+
        "\1\154\1\155\1\156\1\157\1\160\1\161\1\162\1\163\1\164\1\165\1\166"+
        "\1\167\1\170\1\171\1\172\1\173\1\174\1\175\1\176\1\177\1\u0080\1"+
        "\u0081\1\u0082\1\u0083\1\u0084\1\u0085\1\u0086\1\u0087\1\u0088\1"+
        "\u0089\1\u008a\1\u008b\1\u008c\1\u008d\1\u008e\1\u008f\1\u0090\1"+
        "\u0091\1\u0092\1\u0093\1\u0094\1\u0095\1\u0096\1\u0097\1\u0098\1"+
        "\u0099\1\u009a\1\u009b\1\u009c\1\u009d\1\u009e\1\u009f\1\u00a0\1"+
        "\u00a1\1\u00a2\1\u00a3\1\u00a4\1\u00a5\1\u00a6\1\u00a7\1\u00a8\1"+
        "\u00a9\1\u00aa\1\u00ab\1\u00ac\1\u00ad\1\u00ae\1\u00af\1\u00b0\1"+
        "\u00b1\1\u00b2\1\u00b3\1\u00b4\1\u00b5\1\u00b6\1\u00b7\1\u00b8\1"+
        "\u00b9\1\u00ba\1\u00bb\1\u00bc\1\u00bd\1\u00be\1\u00bf\1\u00c0\1"+
        "\u00c1\1\u00c2\1\u00c3\1\u00c4\1\u00c5\1\u00c6\1\u00c7\1\u00c8\1"+
        "\u00c9\1\u00ca\1\u00cb\1\u00cc\1\u00cd\1\u00ce\1\u00cf\1\u00d0\1"+
        "\u00d1\1\u00d2\1\u00d3\1\u00d4\1\u00d5\1\u00d6\1\u00d7\1\u00d8\1"+
        "\u00d9\1\u00da\1\u00db\1\u00dc\1\u00dd\1\u00de\1\u00df\1\u00e0\1"+
        "\uffff}>";
    static final String[] DFA30_transitionS = {
            "\1\1\5\uffff\1\3\3\uffff\3\1\1\7\3\1\11\uffff\1\1\1\uffff\1"+
            "\1\4\uffff\1\1\3\uffff\1\1\2\uffff\1\1\1\uffff\2\1\1\4\1\uffff"+
            "\1\5\1\1\3\uffff\1\1\7\uffff\2\1\1\uffff\1\1\1\6\3\uffff\3\1"+
            "\2\uffff\2\1\2\uffff\1\1\2\uffff\1\1\7\uffff\2\1\2\uffff\13"+
            "\1",
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
            "\1\102\5\uffff\1\65\3\uffff\1\77\1\100\1\122\1\71\1\117\1"+
            "\120\1\121\2\uffff\1\62\6\uffff\1\132\1\uffff\1\111\4\uffff"+
            "\1\110\3\uffff\1\73\2\uffff\1\103\1\uffff\1\106\1\126\1\66\1"+
            "\uffff\1\67\1\104\3\uffff\1\101\7\uffff\1\107\1\105\1\uffff"+
            "\1\123\1\70\3\uffff\1\124\1\131\1\136\2\uffff\1\125\1\74\2\uffff"+
            "\1\72\2\uffff\1\112\7\uffff\1\63\1\64\2\uffff\1\113\1\114\1"+
            "\115\1\116\1\127\1\130\1\133\1\134\1\135\1\75\1\76",
            "\1\157\5\uffff\1\142\3\uffff\1\154\1\155\1\177\1\146\1\174"+
            "\1\175\1\176\2\uffff\1\137\6\uffff\1\u0087\1\uffff\1\166\4\uffff"+
            "\1\165\3\uffff\1\150\2\uffff\1\160\1\uffff\1\163\1\u0083\1\143"+
            "\1\uffff\1\144\1\161\3\uffff\1\156\7\uffff\1\164\1\162\1\uffff"+
            "\1\u0080\1\145\3\uffff\1\u0081\1\u0086\1\u008b\2\uffff\1\u0082"+
            "\1\151\2\uffff\1\147\2\uffff\1\167\7\uffff\1\140\1\141\2\uffff"+
            "\1\170\1\171\1\172\1\173\1\u0084\1\u0085\1\u0088\1\u0089\1\u008a"+
            "\1\152\1\153",
            "\1\u009c\5\uffff\1\u008f\3\uffff\1\u0099\1\u009a\1\u00ac\1"+
            "\u0093\1\u00a9\1\u00aa\1\u00ab\2\uffff\1\u008c\6\uffff\1\u00b4"+
            "\1\uffff\1\u00a3\4\uffff\1\u00a2\3\uffff\1\u0095\2\uffff\1\u009d"+
            "\1\uffff\1\u00a0\1\u00b0\1\u0090\1\uffff\1\u0091\1\u009e\3\uffff"+
            "\1\u009b\7\uffff\1\u00a1\1\u009f\1\uffff\1\u00ad\1\u0092\3\uffff"+
            "\1\u00ae\1\u00b3\1\u00b8\2\uffff\1\u00af\1\u0096\2\uffff\1\u0094"+
            "\2\uffff\1\u00a4\7\uffff\1\u008d\1\u008e\2\uffff\1\u00a5\1\u00a6"+
            "\1\u00a7\1\u00a8\1\u00b1\1\u00b2\1\u00b5\1\u00b6\1\u00b7\1\u0097"+
            "\1\u0098",
            "\1\u00c9\5\uffff\1\u00bc\3\uffff\1\u00c6\1\u00c7\1\u00d9\1"+
            "\u00c0\1\u00d6\1\u00d7\1\u00d8\2\uffff\1\u00b9\6\uffff\1\u00e1"+
            "\1\uffff\1\u00d0\4\uffff\1\u00cf\3\uffff\1\u00c2\2\uffff\1\u00ca"+
            "\1\uffff\1\u00cd\1\u00dd\1\u00bd\1\uffff\1\u00be\1\u00cb\3\uffff"+
            "\1\u00c8\7\uffff\1\u00ce\1\u00cc\1\uffff\1\u00da\1\u00bf\3\uffff"+
            "\1\u00db\1\u00e0\1\u00e5\2\uffff\1\u00dc\1\u00c3\2\uffff\1\u00c1"+
            "\2\uffff\1\u00d1\7\uffff\1\u00ba\1\u00bb\2\uffff\1\u00d2\1\u00d3"+
            "\1\u00d4\1\u00d5\1\u00de\1\u00df\1\u00e2\1\u00e3\1\u00e4\1\u00c4"+
            "\1\u00c5",
            "\1\u00f6\5\uffff\1\u00e9\3\uffff\1\u00f3\1\u00f4\1\u0106\1"+
            "\u00ed\1\u0103\1\u0104\1\u0105\2\uffff\1\u00e6\6\uffff\1\u010e"+
            "\1\uffff\1\u00fd\4\uffff\1\u00fc\3\uffff\1\u00ef\2\uffff\1\u00f7"+
            "\1\uffff\1\u00fa\1\u010a\1\u00ea\1\uffff\1\u00eb\1\u00f8\3\uffff"+
            "\1\u00f5\7\uffff\1\u00fb\1\u00f9\1\uffff\1\u0107\1\u00ec\3\uffff"+
            "\1\u0108\1\u010d\1\u0112\2\uffff\1\u0109\1\u00f0\2\uffff\1\u00ee"+
            "\2\uffff\1\u00fe\7\uffff\1\u00e7\1\u00e8\2\uffff\1\u00ff\1\u0100"+
            "\1\u0101\1\u0102\1\u010b\1\u010c\1\u010f\1\u0110\1\u0111\1\u00f1"+
            "\1\u00f2",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
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

    class DFA30 extends DFA {

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
        public String getDescription() {
            return "452:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );";
        }
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
    static final String DFA33_eotS =
        "\14\uffff";
    static final String DFA33_eofS =
        "\1\1\13\uffff";
    static final String DFA33_minS =
        "\1\42\13\uffff";
    static final String DFA33_maxS =
        "\1\124\13\uffff";
    static final String DFA33_acceptS =
        "\1\uffff\1\2\7\uffff\1\1\2\uffff";
    static final String DFA33_specialS =
        "\14\uffff}>";
    static final String[] DFA33_transitionS = {
            "\3\1\1\uffff\1\11\1\uffff\1\11\2\uffff\1\11\17\uffff\1\1\23"+
            "\uffff\1\1\1\uffff\1\1\2\uffff\1\1",
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

    static final short[] DFA33_eot = DFA.unpackEncodedString(DFA33_eotS);
    static final short[] DFA33_eof = DFA.unpackEncodedString(DFA33_eofS);
    static final char[] DFA33_min = DFA.unpackEncodedStringToUnsignedChars(DFA33_minS);
    static final char[] DFA33_max = DFA.unpackEncodedStringToUnsignedChars(DFA33_maxS);
    static final short[] DFA33_accept = DFA.unpackEncodedString(DFA33_acceptS);
    static final short[] DFA33_special = DFA.unpackEncodedString(DFA33_specialS);
    static final short[][] DFA33_transition;

    static {
        int numStates = DFA33_transitionS.length;
        DFA33_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA33_transition[i] = DFA.unpackEncodedString(DFA33_transitionS[i]);
        }
    }

    class DFA33 extends DFA {

        public DFA33(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 33;
            this.eot = DFA33_eot;
            this.eof = DFA33_eof;
            this.min = DFA33_min;
            this.max = DFA33_max;
            this.accept = DFA33_accept;
            this.special = DFA33_special;
            this.transition = DFA33_transition;
        }
        public String getDescription() {
            return "()* loopback of 477:9: (node= join )*";
        }
    }
    static final String DFA41_eotS =
        "\17\uffff";
    static final String DFA41_eofS =
        "\1\1\16\uffff";
    static final String DFA41_minS =
        "\1\10\16\uffff";
    static final String DFA41_maxS =
        "\1\125\16\uffff";
    static final String DFA41_acceptS =
        "\1\uffff\1\2\14\uffff\1\1";
    static final String DFA41_specialS =
        "\17\uffff}>";
    static final String[] DFA41_transitionS = {
            "\1\1\31\uffff\3\1\1\uffff\1\1\1\uffff\1\1\2\uffff\1\1\17\uffff"+
            "\1\1\23\uffff\3\1\2\uffff\1\1\1\16",
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
            ""
    };

    static final short[] DFA41_eot = DFA.unpackEncodedString(DFA41_eotS);
    static final short[] DFA41_eof = DFA.unpackEncodedString(DFA41_eofS);
    static final char[] DFA41_min = DFA.unpackEncodedStringToUnsignedChars(DFA41_minS);
    static final char[] DFA41_max = DFA.unpackEncodedStringToUnsignedChars(DFA41_maxS);
    static final short[] DFA41_accept = DFA.unpackEncodedString(DFA41_acceptS);
    static final short[] DFA41_special = DFA.unpackEncodedString(DFA41_specialS);
    static final short[][] DFA41_transition;

    static {
        int numStates = DFA41_transitionS.length;
        DFA41_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA41_transition[i] = DFA.unpackEncodedString(DFA41_transitionS[i]);
        }
    }

    class DFA41 extends DFA {

        public DFA41(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 41;
            this.eot = DFA41_eot;
            this.eof = DFA41_eof;
            this.min = DFA41_min;
            this.max = DFA41_max;
            this.accept = DFA41_accept;
            this.special = DFA41_special;
            this.transition = DFA41_transition;
        }
        public String getDescription() {
            return "()+ loopback of 557:9: (d= DOT right= attribute )+";
        }
    }
    static final String DFA42_eotS =
        "\17\uffff";
    static final String DFA42_eofS =
        "\1\1\16\uffff";
    static final String DFA42_minS =
        "\1\6\16\uffff";
    static final String DFA42_maxS =
        "\1\125\16\uffff";
    static final String DFA42_acceptS =
        "\1\uffff\1\2\14\uffff\1\1";
    static final String DFA42_specialS =
        "\17\uffff}>";
    static final String[] DFA42_transitionS = {
            "\1\1\1\uffff\1\1\30\uffff\3\1\26\uffff\2\1\10\uffff\1\1\11"+
            "\uffff\1\1\1\uffff\1\1\1\uffff\1\1\1\uffff\1\1\1\16",
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
            ""
    };

    static final short[] DFA42_eot = DFA.unpackEncodedString(DFA42_eotS);
    static final short[] DFA42_eof = DFA.unpackEncodedString(DFA42_eofS);
    static final char[] DFA42_min = DFA.unpackEncodedStringToUnsignedChars(DFA42_minS);
    static final char[] DFA42_max = DFA.unpackEncodedStringToUnsignedChars(DFA42_maxS);
    static final short[] DFA42_accept = DFA.unpackEncodedString(DFA42_acceptS);
    static final short[] DFA42_special = DFA.unpackEncodedString(DFA42_specialS);
    static final short[][] DFA42_transition;

    static {
        int numStates = DFA42_transitionS.length;
        DFA42_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA42_transition[i] = DFA.unpackEncodedString(DFA42_transitionS[i]);
        }
    }

    class DFA42 extends DFA {

        public DFA42(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 42;
            this.eot = DFA42_eot;
            this.eof = DFA42_eof;
            this.min = DFA42_min;
            this.max = DFA42_max;
            this.accept = DFA42_accept;
            this.special = DFA42_special;
            this.transition = DFA42_transition;
        }
        public String getDescription() {
            return "()+ loopback of 577:9: (d= DOT right= attribute )+";
        }
    }
    static final String DFA45_eotS =
        "\57\uffff";
    static final String DFA45_eofS =
        "\57\uffff";
    static final String DFA45_minS =
        "\1\4\56\uffff";
    static final String DFA45_maxS =
        "\1\151\56\uffff";
    static final String DFA45_acceptS =
        "\1\uffff\1\1\1\2\54\uffff";
    static final String DFA45_specialS =
        "\57\uffff}>";
    static final String[] DFA45_transitionS = {
            "\1\2\5\uffff\1\2\3\uffff\7\2\10\uffff\2\2\1\uffff\1\2\4\uffff"+
            "\1\2\3\uffff\1\2\2\uffff\1\2\1\uffff\3\2\1\uffff\2\2\1\uffff"+
            "\1\1\1\uffff\1\2\7\uffff\2\2\1\uffff\2\2\3\uffff\3\2\2\uffff"+
            "\2\2\2\uffff\1\2\2\uffff\1\2\7\uffff\2\2\2\uffff\13\2",
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
            "",
            ""
    };

    static final short[] DFA45_eot = DFA.unpackEncodedString(DFA45_eotS);
    static final short[] DFA45_eof = DFA.unpackEncodedString(DFA45_eofS);
    static final char[] DFA45_min = DFA.unpackEncodedStringToUnsignedChars(DFA45_minS);
    static final char[] DFA45_max = DFA.unpackEncodedStringToUnsignedChars(DFA45_maxS);
    static final short[] DFA45_accept = DFA.unpackEncodedString(DFA45_acceptS);
    static final short[] DFA45_special = DFA.unpackEncodedString(DFA45_specialS);
    static final short[][] DFA45_transition;

    static {
        int numStates = DFA45_transitionS.length;
        DFA45_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA45_transition[i] = DFA.unpackEncodedString(DFA45_transitionS[i]);
        }
    }

    class DFA45 extends DFA {

        public DFA45(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 45;
            this.eot = DFA45_eot;
            this.eof = DFA45_eof;
            this.min = DFA45_min;
            this.max = DFA45_max;
            this.accept = DFA45_accept;
            this.special = DFA45_special;
            this.transition = DFA45_transition;
        }
        public String getDescription() {
            return "634:7: (n= NOT )?";
        }
    }
    static final String DFA46_eotS =
        "\56\uffff";
    static final String DFA46_eofS =
        "\56\uffff";
    static final String DFA46_minS =
        "\1\4\55\uffff";
    static final String DFA46_maxS =
        "\1\151\55\uffff";
    static final String DFA46_acceptS =
        "\1\uffff\1\1\53\uffff\1\2";
    static final String DFA46_specialS =
        "\56\uffff}>";
    static final String[] DFA46_transitionS = {
            "\1\1\5\uffff\1\1\3\uffff\7\1\10\uffff\1\55\1\1\1\uffff\1\1"+
            "\4\uffff\1\1\3\uffff\1\1\2\uffff\1\1\1\uffff\3\1\1\uffff\2\1"+
            "\3\uffff\1\1\7\uffff\2\1\1\uffff\2\1\3\uffff\3\1\2\uffff\2\1"+
            "\2\uffff\1\1\2\uffff\1\1\7\uffff\2\1\2\uffff\13\1",
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
            ""
    };

    static final short[] DFA46_eot = DFA.unpackEncodedString(DFA46_eotS);
    static final short[] DFA46_eof = DFA.unpackEncodedString(DFA46_eofS);
    static final char[] DFA46_min = DFA.unpackEncodedStringToUnsignedChars(DFA46_minS);
    static final char[] DFA46_max = DFA.unpackEncodedStringToUnsignedChars(DFA46_maxS);
    static final short[] DFA46_accept = DFA.unpackEncodedString(DFA46_acceptS);
    static final short[] DFA46_special = DFA.unpackEncodedString(DFA46_specialS);
    static final short[][] DFA46_transition;

    static {
        int numStates = DFA46_transitionS.length;
        DFA46_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA46_transition[i] = DFA.unpackEncodedString(DFA46_transitionS[i]);
        }
    }

    class DFA46 extends DFA {

        public DFA46(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 46;
            this.eot = DFA46_eot;
            this.eof = DFA46_eof;
            this.min = DFA46_min;
            this.max = DFA46_max;
            this.accept = DFA46_accept;
            this.special = DFA46_special;
            this.transition = DFA46_transition;
        }
        public String getDescription() {
            return "635:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )";
        }
    }
    static final String DFA47_eotS =
        "\u014d\uffff";
    static final String DFA47_eofS =
        "\u014d\uffff";
    static final String DFA47_minS =
        "\2\4\54\uffff\2\4\5\123\1\13\2\123\2\13\1\51\12\123\1\4\4\13\22"+
        "\uffff\76\0\15\uffff\6\0\15\uffff\4\0\15\uffff\17\0\2\uffff\34\0"+
        "\21\uffff\4\0\15\uffff\4\0\15\uffff\4\0\15\uffff\4\0\15\uffff";
    static final String DFA47_maxS =
        "\2\151\54\uffff\2\151\5\123\1\136\2\123\2\136\1\120\12\123\1\151"+
        "\4\136\22\uffff\76\0\15\uffff\6\0\15\uffff\4\0\15\uffff\17\0\2\uffff"+
        "\34\0\21\uffff\4\0\15\uffff\4\0\15\uffff\4\0\15\uffff\4\0\15\uffff";
    static final String DFA47_acceptS =
        "\2\uffff\1\2\107\uffff\22\1\76\uffff\14\1\10\uffff\14\1\4\uffff"+
        "\14\1\20\uffff\2\1\34\uffff\21\1\5\uffff\14\1\5\uffff\14\1\4\uffff"+
        "\14\1\6\uffff\14\1";
    static final String DFA47_specialS =
        "\1\uffff\1\0\63\uffff\1\1\2\uffff\1\2\1\3\13\uffff\1\4\1\5\1\6"+
        "\1\7\1\10\22\uffff\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21"+
        "\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33\1\34\1\35\1\36"+
        "\1\37\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47\1\50\1\51\1\52\1\53"+
        "\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\63\1\64\1\65\1\66\1\67\1\70"+
        "\1\71\1\72\1\73\1\74\1\75\1\76\1\77\1\100\1\101\1\102\1\103\1\104"+
        "\1\105\1\106\15\uffff\1\107\1\110\1\111\1\112\1\113\1\114\15\uffff"+
        "\1\115\1\116\1\117\1\120\15\uffff\1\121\1\122\1\123\1\124\1\125"+
        "\1\126\1\127\1\130\1\131\1\132\1\133\1\134\1\135\1\136\1\137\2\uffff"+
        "\1\140\1\141\1\142\1\143\1\144\1\145\1\146\1\147\1\150\1\151\1\152"+
        "\1\153\1\154\1\155\1\156\1\157\1\160\1\161\1\162\1\163\1\164\1\165"+
        "\1\166\1\167\1\170\1\171\1\172\1\173\21\uffff\1\174\1\175\1\176"+
        "\1\177\15\uffff\1\u0080\1\u0081\1\u0082\1\u0083\15\uffff\1\u0084"+
        "\1\u0085\1\u0086\1\u0087\15\uffff\1\u0088\1\u0089\1\u008a\1\u008b"+
        "\15\uffff}>";
    static final String[] DFA47_transitionS = {
            "\1\2\5\uffff\1\2\3\uffff\7\2\11\uffff\1\2\1\uffff\1\2\4\uffff"+
            "\1\2\3\uffff\1\2\2\uffff\1\2\1\uffff\3\2\1\uffff\2\2\3\uffff"+
            "\1\2\7\uffff\2\2\1\uffff\2\2\3\uffff\3\2\2\uffff\2\2\2\uffff"+
            "\1\2\2\uffff\1\1\7\uffff\2\2\2\uffff\13\2",
            "\1\75\5\uffff\1\60\3\uffff\1\72\1\73\1\116\1\64\1\113\1\114"+
            "\1\115\10\uffff\1\133\1\126\1\uffff\1\104\4\uffff\1\103\3\uffff"+
            "\1\66\2\uffff\1\76\1\uffff\1\101\1\122\1\61\1\uffff\1\62\1\77"+
            "\1\uffff\1\112\1\uffff\1\74\5\uffff\1\2\1\uffff\1\102\1\100"+
            "\1\uffff\1\117\1\63\3\uffff\1\120\1\125\1\132\2\uffff\1\121"+
            "\1\67\2\uffff\1\65\2\uffff\1\105\7\uffff\1\56\1\57\2\uffff\1"+
            "\106\1\107\1\110\1\111\1\123\1\124\1\127\1\130\1\131\1\70\1"+
            "\71",
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
            "\1\151\5\uffff\1\134\3\uffff\1\146\1\147\1\uffff\1\140\16"+
            "\uffff\1\160\4\uffff\1\157\3\uffff\1\142\2\uffff\1\152\1\uffff"+
            "\1\155\1\uffff\1\135\1\uffff\1\136\1\153\3\uffff\1\150\7\uffff"+
            "\1\156\1\154\2\uffff\1\137\11\uffff\1\143\2\uffff\1\141\2\uffff"+
            "\1\161\13\uffff\1\162\1\163\1\164\1\165\5\uffff\1\144\1\145",
            "\1\u0083\5\uffff\1\166\3\uffff\1\u0080\1\u0081\1\uffff\1\172"+
            "\16\uffff\1\u008a\4\uffff\1\u0089\3\uffff\1\174\2\uffff\1\u0084"+
            "\1\uffff\1\u0087\1\uffff\1\167\1\uffff\1\170\1\u0085\3\uffff"+
            "\1\u0082\7\uffff\1\u0088\1\u0086\2\uffff\1\171\11\uffff\1\175"+
            "\2\uffff\1\173\2\uffff\1\u008b\13\uffff\1\u008c\1\u008d\1\u008e"+
            "\1\u008f\5\uffff\1\176\1\177",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094",
            "\1\u00a1\30\uffff\1\u00a3\2\uffff\1\u00a5\5\uffff\1\u00a2"+
            "\3\uffff\1\u00a4\3\uffff\1\u00a0\34\uffff\1\u009a\1\uffff\1"+
            "\2\1\u0095\1\u009b\1\u009c\1\u009d\1\u009e\1\u009f\1\u0098\1"+
            "\u0099\1\u0096\1\u0097",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00b5\30\uffff\1\u00b7\2\uffff\1\u00b9\5\uffff\1\u00b6"+
            "\3\uffff\1\u00b8\3\uffff\1\u00b4\34\uffff\1\u00ae\1\uffff\1"+
            "\2\1\uffff\1\u00af\1\u00b0\1\u00b1\1\u00b2\1\u00b3\1\u00ab\1"+
            "\u00ac\1\u00a9\1\u00aa",
            "\1\u00c5\30\uffff\1\u00c7\2\uffff\1\u00c9\5\uffff\1\u00c6"+
            "\3\uffff\1\u00c8\3\uffff\1\u00c4\34\uffff\1\u00be\1\uffff\1"+
            "\2\1\uffff\1\u00bf\1\u00c0\1\u00c1\1\u00c2\1\u00c3\1\u00bc\1"+
            "\u00bd\1\u00ba\1\u00bb",
            "\1\u00cc\37\uffff\1\u00ce\3\uffff\1\u00cd\1\u00cf\1\uffff"+
            "\1\u00cb",
            "\1\u00d0",
            "\1\u00d1",
            "\1\u00d2",
            "\1\u00d3",
            "\1\u00d4",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\1\u00d9",
            "\1\u00ec\5\uffff\1\u00df\3\uffff\1\u00e9\1\u00ea\1\u00fb\1"+
            "\u00e3\1\u00f8\1\u00f9\1\u00fa\10\uffff\1\u0108\1\u0103\1\uffff"+
            "\1\u00f3\4\uffff\1\u00f2\3\uffff\1\u00e5\2\uffff\1\u00ed\1\uffff"+
            "\1\u00f0\1\u00ff\1\u00e0\1\uffff\1\u00e1\1\u00ee\1\uffff\1\u00db"+
            "\1\uffff\1\u00eb\5\uffff\1\u00da\1\uffff\1\u00f1\1\u00ef\1\uffff"+
            "\1\u00fc\1\u00e2\3\uffff\1\u00fd\1\u0102\1\u0107\2\uffff\1\u00fe"+
            "\1\u00e6\2\uffff\1\u00e4\2\uffff\1\u00dc\7\uffff\1\u00dd\1\u00de"+
            "\2\uffff\1\u00f4\1\u00f5\1\u00f6\1\u00f7\1\u0100\1\u0101\1\u0104"+
            "\1\u0105\1\u0106\1\u00e7\1\u00e8",
            "\1\u0115\30\uffff\1\u0117\2\uffff\1\u0119\5\uffff\1\u0116"+
            "\3\uffff\1\u0118\3\uffff\1\u0114\34\uffff\1\u010e\1\uffff\1"+
            "\2\1\uffff\1\u010f\1\u0110\1\u0111\1\u0112\1\u0113\1\u010b\1"+
            "\u010c\1\u0109\1\u010a",
            "\1\u0126\30\uffff\1\u0128\2\uffff\1\u012a\5\uffff\1\u0127"+
            "\3\uffff\1\u0129\3\uffff\1\u0125\34\uffff\1\u011f\1\uffff\1"+
            "\2\1\uffff\1\u0120\1\u0121\1\u0122\1\u0123\1\u0124\1\u011c\1"+
            "\u011d\1\u011a\1\u011b",
            "\1\u0136\30\uffff\1\u0138\2\uffff\1\u013a\5\uffff\1\u0137"+
            "\3\uffff\1\u0139\3\uffff\1\u0135\34\uffff\1\u012f\1\uffff\1"+
            "\2\1\uffff\1\u0130\1\u0131\1\u0132\1\u0133\1\u0134\1\u012d\1"+
            "\u012e\1\u012b\1\u012c",
            "\1\u0148\30\uffff\1\u014a\2\uffff\1\u014c\5\uffff\1\u0149"+
            "\3\uffff\1\u014b\3\uffff\1\u0147\34\uffff\1\u0141\1\uffff\1"+
            "\2\1\uffff\1\u0142\1\u0143\1\u0144\1\u0145\1\u0146\1\u013e\1"+
            "\u013f\1\u013c\1\u013d",
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

    class DFA47 extends DFA {

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
        public String getDescription() {
            return "646:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA47_1 = input.LA(1);

                         
                        int index47_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_1==SELECT) ) {s = 2;}

                        else if ( (LA47_1==PLUS) ) {s = 46;}

                        else if ( (LA47_1==MINUS) ) {s = 47;}

                        else if ( (LA47_1==AVG) ) {s = 48;}

                        else if ( (LA47_1==MAX) ) {s = 49;}

                        else if ( (LA47_1==MIN) ) {s = 50;}

                        else if ( (LA47_1==SUM) ) {s = 51;}

                        else if ( (LA47_1==COUNT) ) {s = 52;}

                        else if ( (LA47_1==IDENT) ) {s = 53;}

                        else if ( (LA47_1==KEY) ) {s = 54;}

                        else if ( (LA47_1==VALUE) ) {s = 55;}

                        else if ( (LA47_1==POSITIONAL_PARAM) ) {s = 56;}

                        else if ( (LA47_1==NAMED_PARAM) ) {s = 57;}

                        else if ( (LA47_1==CASE) ) {s = 58;}

                        else if ( (LA47_1==COALESCE) ) {s = 59;}

                        else if ( (LA47_1==NULLIF) ) {s = 60;}

                        else if ( (LA47_1==ABS) ) {s = 61;}

                        else if ( (LA47_1==LENGTH) ) {s = 62;}

                        else if ( (LA47_1==MOD) ) {s = 63;}

                        else if ( (LA47_1==SQRT) ) {s = 64;}

                        else if ( (LA47_1==LOCATE) ) {s = 65;}

                        else if ( (LA47_1==SIZE) ) {s = 66;}

                        else if ( (LA47_1==INDEX) ) {s = 67;}

                        else if ( (LA47_1==FUNC) ) {s = 68;}

                        else if ( (LA47_1==LEFT_ROUND_BRACKET) ) {s = 69;}

                        else if ( (LA47_1==INTEGER_LITERAL) ) {s = 70;}

                        else if ( (LA47_1==LONG_LITERAL) ) {s = 71;}

                        else if ( (LA47_1==FLOAT_LITERAL) ) {s = 72;}

                        else if ( (LA47_1==DOUBLE_LITERAL) ) {s = 73;}

                        else if ( (LA47_1==NOT) && (synpred1_JPQL())) {s = 74;}

                        else if ( (LA47_1==CURRENT_DATE) && (synpred1_JPQL())) {s = 75;}

                        else if ( (LA47_1==CURRENT_TIME) && (synpred1_JPQL())) {s = 76;}

                        else if ( (LA47_1==CURRENT_TIMESTAMP) && (synpred1_JPQL())) {s = 77;}

                        else if ( (LA47_1==CONCAT) && (synpred1_JPQL())) {s = 78;}

                        else if ( (LA47_1==SUBSTRING) && (synpred1_JPQL())) {s = 79;}

                        else if ( (LA47_1==TRIM) && (synpred1_JPQL())) {s = 80;}

                        else if ( (LA47_1==UPPER) && (synpred1_JPQL())) {s = 81;}

                        else if ( (LA47_1==LOWER) && (synpred1_JPQL())) {s = 82;}

                        else if ( (LA47_1==STRING_LITERAL_DOUBLE_QUOTED) && (synpred1_JPQL())) {s = 83;}

                        else if ( (LA47_1==STRING_LITERAL_SINGLE_QUOTED) && (synpred1_JPQL())) {s = 84;}

                        else if ( (LA47_1==TRUE) && (synpred1_JPQL())) {s = 85;}

                        else if ( (LA47_1==FALSE) && (synpred1_JPQL())) {s = 86;}

                        else if ( (LA47_1==DATE_LITERAL) && (synpred1_JPQL())) {s = 87;}

                        else if ( (LA47_1==TIME_LITERAL) && (synpred1_JPQL())) {s = 88;}

                        else if ( (LA47_1==TIMESTAMP_LITERAL) && (synpred1_JPQL())) {s = 89;}

                        else if ( (LA47_1==TYPE) && (synpred1_JPQL())) {s = 90;}

                        else if ( (LA47_1==EXISTS) && (synpred1_JPQL())) {s = 91;}

                         
                        input.seek(index47_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA47_53 = input.LA(1);

                         
                        int index47_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_53==DOT) ) {s = 149;}

                        else if ( (LA47_53==MULTIPLY) ) {s = 150;}

                        else if ( (LA47_53==DIVIDE) ) {s = 151;}

                        else if ( (LA47_53==PLUS) ) {s = 152;}

                        else if ( (LA47_53==MINUS) ) {s = 153;}

                        else if ( (LA47_53==EQUALS) && (synpred1_JPQL())) {s = 154;}

                        else if ( (LA47_53==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 155;}

                        else if ( (LA47_53==GREATER_THAN) && (synpred1_JPQL())) {s = 156;}

                        else if ( (LA47_53==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 157;}

                        else if ( (LA47_53==LESS_THAN) && (synpred1_JPQL())) {s = 158;}

                        else if ( (LA47_53==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 159;}

                        else if ( (LA47_53==NOT) && (synpred1_JPQL())) {s = 160;}

                        else if ( (LA47_53==BETWEEN) && (synpred1_JPQL())) {s = 161;}

                        else if ( (LA47_53==LIKE) && (synpred1_JPQL())) {s = 162;}

                        else if ( (LA47_53==IN) && (synpred1_JPQL())) {s = 163;}

                        else if ( (LA47_53==MEMBER) && (synpred1_JPQL())) {s = 164;}

                        else if ( (LA47_53==IS) && (synpred1_JPQL())) {s = 165;}

                        else if ( (LA47_53==RIGHT_ROUND_BRACKET) ) {s = 2;}

                         
                        input.seek(index47_53);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA47_56 = input.LA(1);

                         
                        int index47_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_56==MULTIPLY) ) {s = 169;}

                        else if ( (LA47_56==DIVIDE) ) {s = 170;}

                        else if ( (LA47_56==PLUS) ) {s = 171;}

                        else if ( (LA47_56==MINUS) ) {s = 172;}

                        else if ( (LA47_56==RIGHT_ROUND_BRACKET) ) {s = 2;}

                        else if ( (LA47_56==EQUALS) && (synpred1_JPQL())) {s = 174;}

                        else if ( (LA47_56==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 175;}

                        else if ( (LA47_56==GREATER_THAN) && (synpred1_JPQL())) {s = 176;}

                        else if ( (LA47_56==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 177;}

                        else if ( (LA47_56==LESS_THAN) && (synpred1_JPQL())) {s = 178;}

                        else if ( (LA47_56==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 179;}

                        else if ( (LA47_56==NOT) && (synpred1_JPQL())) {s = 180;}

                        else if ( (LA47_56==BETWEEN) && (synpred1_JPQL())) {s = 181;}

                        else if ( (LA47_56==LIKE) && (synpred1_JPQL())) {s = 182;}

                        else if ( (LA47_56==IN) && (synpred1_JPQL())) {s = 183;}

                        else if ( (LA47_56==MEMBER) && (synpred1_JPQL())) {s = 184;}

                        else if ( (LA47_56==IS) && (synpred1_JPQL())) {s = 185;}

                         
                        input.seek(index47_56);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA47_57 = input.LA(1);

                         
                        int index47_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_57==MULTIPLY) ) {s = 186;}

                        else if ( (LA47_57==DIVIDE) ) {s = 187;}

                        else if ( (LA47_57==PLUS) ) {s = 188;}

                        else if ( (LA47_57==MINUS) ) {s = 189;}

                        else if ( (LA47_57==EQUALS) && (synpred1_JPQL())) {s = 190;}

                        else if ( (LA47_57==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 191;}

                        else if ( (LA47_57==GREATER_THAN) && (synpred1_JPQL())) {s = 192;}

                        else if ( (LA47_57==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 193;}

                        else if ( (LA47_57==LESS_THAN) && (synpred1_JPQL())) {s = 194;}

                        else if ( (LA47_57==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 195;}

                        else if ( (LA47_57==NOT) && (synpred1_JPQL())) {s = 196;}

                        else if ( (LA47_57==BETWEEN) && (synpred1_JPQL())) {s = 197;}

                        else if ( (LA47_57==LIKE) && (synpred1_JPQL())) {s = 198;}

                        else if ( (LA47_57==IN) && (synpred1_JPQL())) {s = 199;}

                        else if ( (LA47_57==MEMBER) && (synpred1_JPQL())) {s = 200;}

                        else if ( (LA47_57==IS) && (synpred1_JPQL())) {s = 201;}

                        else if ( (LA47_57==RIGHT_ROUND_BRACKET) ) {s = 2;}

                         
                        input.seek(index47_57);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA47_69 = input.LA(1);

                         
                        int index47_69 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_69==SELECT) && (synpred1_JPQL())) {s = 218;}

                        else if ( (LA47_69==NOT) && (synpred1_JPQL())) {s = 219;}

                        else if ( (LA47_69==LEFT_ROUND_BRACKET) ) {s = 220;}

                        else if ( (LA47_69==PLUS) ) {s = 221;}

                        else if ( (LA47_69==MINUS) ) {s = 222;}

                        else if ( (LA47_69==AVG) ) {s = 223;}

                        else if ( (LA47_69==MAX) ) {s = 224;}

                        else if ( (LA47_69==MIN) ) {s = 225;}

                        else if ( (LA47_69==SUM) ) {s = 226;}

                        else if ( (LA47_69==COUNT) ) {s = 227;}

                        else if ( (LA47_69==IDENT) ) {s = 228;}

                        else if ( (LA47_69==KEY) ) {s = 229;}

                        else if ( (LA47_69==VALUE) ) {s = 230;}

                        else if ( (LA47_69==POSITIONAL_PARAM) ) {s = 231;}

                        else if ( (LA47_69==NAMED_PARAM) ) {s = 232;}

                        else if ( (LA47_69==CASE) ) {s = 233;}

                        else if ( (LA47_69==COALESCE) ) {s = 234;}

                        else if ( (LA47_69==NULLIF) ) {s = 235;}

                        else if ( (LA47_69==ABS) ) {s = 236;}

                        else if ( (LA47_69==LENGTH) ) {s = 237;}

                        else if ( (LA47_69==MOD) ) {s = 238;}

                        else if ( (LA47_69==SQRT) ) {s = 239;}

                        else if ( (LA47_69==LOCATE) ) {s = 240;}

                        else if ( (LA47_69==SIZE) ) {s = 241;}

                        else if ( (LA47_69==INDEX) ) {s = 242;}

                        else if ( (LA47_69==FUNC) ) {s = 243;}

                        else if ( (LA47_69==INTEGER_LITERAL) ) {s = 244;}

                        else if ( (LA47_69==LONG_LITERAL) ) {s = 245;}

                        else if ( (LA47_69==FLOAT_LITERAL) ) {s = 246;}

                        else if ( (LA47_69==DOUBLE_LITERAL) ) {s = 247;}

                        else if ( (LA47_69==CURRENT_DATE) && (synpred1_JPQL())) {s = 248;}

                        else if ( (LA47_69==CURRENT_TIME) && (synpred1_JPQL())) {s = 249;}

                        else if ( (LA47_69==CURRENT_TIMESTAMP) && (synpred1_JPQL())) {s = 250;}

                        else if ( (LA47_69==CONCAT) && (synpred1_JPQL())) {s = 251;}

                        else if ( (LA47_69==SUBSTRING) && (synpred1_JPQL())) {s = 252;}

                        else if ( (LA47_69==TRIM) && (synpred1_JPQL())) {s = 253;}

                        else if ( (LA47_69==UPPER) && (synpred1_JPQL())) {s = 254;}

                        else if ( (LA47_69==LOWER) && (synpred1_JPQL())) {s = 255;}

                        else if ( (LA47_69==STRING_LITERAL_DOUBLE_QUOTED) && (synpred1_JPQL())) {s = 256;}

                        else if ( (LA47_69==STRING_LITERAL_SINGLE_QUOTED) && (synpred1_JPQL())) {s = 257;}

                        else if ( (LA47_69==TRUE) && (synpred1_JPQL())) {s = 258;}

                        else if ( (LA47_69==FALSE) && (synpred1_JPQL())) {s = 259;}

                        else if ( (LA47_69==DATE_LITERAL) && (synpred1_JPQL())) {s = 260;}

                        else if ( (LA47_69==TIME_LITERAL) && (synpred1_JPQL())) {s = 261;}

                        else if ( (LA47_69==TIMESTAMP_LITERAL) && (synpred1_JPQL())) {s = 262;}

                        else if ( (LA47_69==TYPE) && (synpred1_JPQL())) {s = 263;}

                        else if ( (LA47_69==EXISTS) && (synpred1_JPQL())) {s = 264;}

                         
                        input.seek(index47_69);
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

                        else if ( (LA47_70==RIGHT_ROUND_BRACKET) ) {s = 2;}

                        else if ( (LA47_70==EQUALS) && (synpred1_JPQL())) {s = 270;}

                        else if ( (LA47_70==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 271;}

                        else if ( (LA47_70==GREATER_THAN) && (synpred1_JPQL())) {s = 272;}

                        else if ( (LA47_70==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 273;}

                        else if ( (LA47_70==LESS_THAN) && (synpred1_JPQL())) {s = 274;}

                        else if ( (LA47_70==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 275;}

                        else if ( (LA47_70==NOT) && (synpred1_JPQL())) {s = 276;}

                        else if ( (LA47_70==BETWEEN) && (synpred1_JPQL())) {s = 277;}

                        else if ( (LA47_70==LIKE) && (synpred1_JPQL())) {s = 278;}

                        else if ( (LA47_70==IN) && (synpred1_JPQL())) {s = 279;}

                        else if ( (LA47_70==MEMBER) && (synpred1_JPQL())) {s = 280;}

                        else if ( (LA47_70==IS) && (synpred1_JPQL())) {s = 281;}

                         
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

                        else if ( (LA47_71==RIGHT_ROUND_BRACKET) ) {s = 2;}

                        else if ( (LA47_71==EQUALS) && (synpred1_JPQL())) {s = 287;}

                        else if ( (LA47_71==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 288;}

                        else if ( (LA47_71==GREATER_THAN) && (synpred1_JPQL())) {s = 289;}

                        else if ( (LA47_71==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 290;}

                        else if ( (LA47_71==LESS_THAN) && (synpred1_JPQL())) {s = 291;}

                        else if ( (LA47_71==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 292;}

                        else if ( (LA47_71==NOT) && (synpred1_JPQL())) {s = 293;}

                        else if ( (LA47_71==BETWEEN) && (synpred1_JPQL())) {s = 294;}

                        else if ( (LA47_71==LIKE) && (synpred1_JPQL())) {s = 295;}

                        else if ( (LA47_71==IN) && (synpred1_JPQL())) {s = 296;}

                        else if ( (LA47_71==MEMBER) && (synpred1_JPQL())) {s = 297;}

                        else if ( (LA47_71==IS) && (synpred1_JPQL())) {s = 298;}

                         
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

                        else if ( (LA47_73==RIGHT_ROUND_BRACKET) ) {s = 2;}

                        else if ( (LA47_73==EQUALS) && (synpred1_JPQL())) {s = 321;}

                        else if ( (LA47_73==NOT_EQUAL_TO) && (synpred1_JPQL())) {s = 322;}

                        else if ( (LA47_73==GREATER_THAN) && (synpred1_JPQL())) {s = 323;}

                        else if ( (LA47_73==GREATER_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 324;}

                        else if ( (LA47_73==LESS_THAN) && (synpred1_JPQL())) {s = 325;}

                        else if ( (LA47_73==LESS_THAN_EQUAL_TO) && (synpred1_JPQL())) {s = 326;}

                        else if ( (LA47_73==NOT) && (synpred1_JPQL())) {s = 327;}

                        else if ( (LA47_73==BETWEEN) && (synpred1_JPQL())) {s = 328;}

                        else if ( (LA47_73==LIKE) && (synpred1_JPQL())) {s = 329;}

                        else if ( (LA47_73==IN) && (synpred1_JPQL())) {s = 330;}

                        else if ( (LA47_73==MEMBER) && (synpred1_JPQL())) {s = 331;}

                        else if ( (LA47_73==IS) && (synpred1_JPQL())) {s = 332;}

                         
                        input.seek(index47_73);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA47_92 = input.LA(1);

                         
                        int index47_92 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_92);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA47_93 = input.LA(1);

                         
                        int index47_93 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_93);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA47_94 = input.LA(1);

                         
                        int index47_94 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_94);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA47_95 = input.LA(1);

                         
                        int index47_95 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_95);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA47_96 = input.LA(1);

                         
                        int index47_96 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_96);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA47_97 = input.LA(1);

                         
                        int index47_97 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_97);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA47_98 = input.LA(1);

                         
                        int index47_98 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_98);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA47_99 = input.LA(1);

                         
                        int index47_99 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_99);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA47_100 = input.LA(1);

                         
                        int index47_100 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_100);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA47_101 = input.LA(1);

                         
                        int index47_101 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_101);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA47_102 = input.LA(1);

                         
                        int index47_102 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_102);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA47_103 = input.LA(1);

                         
                        int index47_103 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_103);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA47_104 = input.LA(1);

                         
                        int index47_104 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_104);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA47_105 = input.LA(1);

                         
                        int index47_105 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_105);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA47_106 = input.LA(1);

                         
                        int index47_106 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_106);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA47_107 = input.LA(1);

                         
                        int index47_107 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_107);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA47_108 = input.LA(1);

                         
                        int index47_108 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_108);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA47_109 = input.LA(1);

                         
                        int index47_109 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_109);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA47_110 = input.LA(1);

                         
                        int index47_110 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_110);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA47_111 = input.LA(1);

                         
                        int index47_111 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_111);
                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA47_112 = input.LA(1);

                         
                        int index47_112 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_112);
                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA47_113 = input.LA(1);

                         
                        int index47_113 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_113);
                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA47_114 = input.LA(1);

                         
                        int index47_114 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_114);
                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA47_115 = input.LA(1);

                         
                        int index47_115 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_115);
                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA47_116 = input.LA(1);

                         
                        int index47_116 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_116);
                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA47_117 = input.LA(1);

                         
                        int index47_117 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_117);
                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA47_118 = input.LA(1);

                         
                        int index47_118 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_118);
                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA47_119 = input.LA(1);

                         
                        int index47_119 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_119);
                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA47_120 = input.LA(1);

                         
                        int index47_120 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_120);
                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA47_121 = input.LA(1);

                         
                        int index47_121 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_121);
                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA47_122 = input.LA(1);

                         
                        int index47_122 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_122);
                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA47_123 = input.LA(1);

                         
                        int index47_123 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_123);
                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA47_124 = input.LA(1);

                         
                        int index47_124 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_124);
                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA47_125 = input.LA(1);

                         
                        int index47_125 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_125);
                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA47_126 = input.LA(1);

                         
                        int index47_126 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_126);
                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA47_127 = input.LA(1);

                         
                        int index47_127 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_127);
                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA47_128 = input.LA(1);

                         
                        int index47_128 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_128);
                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA47_129 = input.LA(1);

                         
                        int index47_129 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_129);
                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA47_130 = input.LA(1);

                         
                        int index47_130 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_130);
                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA47_131 = input.LA(1);

                         
                        int index47_131 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_131);
                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA47_132 = input.LA(1);

                         
                        int index47_132 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_132);
                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA47_133 = input.LA(1);

                         
                        int index47_133 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_133);
                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA47_134 = input.LA(1);

                         
                        int index47_134 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_134);
                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA47_135 = input.LA(1);

                         
                        int index47_135 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_135);
                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA47_136 = input.LA(1);

                         
                        int index47_136 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_136);
                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA47_137 = input.LA(1);

                         
                        int index47_137 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_137);
                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA47_138 = input.LA(1);

                         
                        int index47_138 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_138);
                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA47_139 = input.LA(1);

                         
                        int index47_139 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_139);
                        if ( s>=0 ) return s;
                        break;
                    case 57 : 
                        int LA47_140 = input.LA(1);

                         
                        int index47_140 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_140);
                        if ( s>=0 ) return s;
                        break;
                    case 58 : 
                        int LA47_141 = input.LA(1);

                         
                        int index47_141 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_141);
                        if ( s>=0 ) return s;
                        break;
                    case 59 : 
                        int LA47_142 = input.LA(1);

                         
                        int index47_142 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_142);
                        if ( s>=0 ) return s;
                        break;
                    case 60 : 
                        int LA47_143 = input.LA(1);

                         
                        int index47_143 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_143);
                        if ( s>=0 ) return s;
                        break;
                    case 61 : 
                        int LA47_144 = input.LA(1);

                         
                        int index47_144 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_144);
                        if ( s>=0 ) return s;
                        break;
                    case 62 : 
                        int LA47_145 = input.LA(1);

                         
                        int index47_145 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_145);
                        if ( s>=0 ) return s;
                        break;
                    case 63 : 
                        int LA47_146 = input.LA(1);

                         
                        int index47_146 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_146);
                        if ( s>=0 ) return s;
                        break;
                    case 64 : 
                        int LA47_147 = input.LA(1);

                         
                        int index47_147 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_147);
                        if ( s>=0 ) return s;
                        break;
                    case 65 : 
                        int LA47_148 = input.LA(1);

                         
                        int index47_148 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_148);
                        if ( s>=0 ) return s;
                        break;
                    case 66 : 
                        int LA47_149 = input.LA(1);

                         
                        int index47_149 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_149);
                        if ( s>=0 ) return s;
                        break;
                    case 67 : 
                        int LA47_150 = input.LA(1);

                         
                        int index47_150 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_150);
                        if ( s>=0 ) return s;
                        break;
                    case 68 : 
                        int LA47_151 = input.LA(1);

                         
                        int index47_151 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_151);
                        if ( s>=0 ) return s;
                        break;
                    case 69 : 
                        int LA47_152 = input.LA(1);

                         
                        int index47_152 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_152);
                        if ( s>=0 ) return s;
                        break;
                    case 70 : 
                        int LA47_153 = input.LA(1);

                         
                        int index47_153 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_153);
                        if ( s>=0 ) return s;
                        break;
                    case 71 : 
                        int LA47_167 = input.LA(1);

                         
                        int index47_167 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_167);
                        if ( s>=0 ) return s;
                        break;
                    case 72 : 
                        int LA47_168 = input.LA(1);

                         
                        int index47_168 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_168);
                        if ( s>=0 ) return s;
                        break;
                    case 73 : 
                        int LA47_169 = input.LA(1);

                         
                        int index47_169 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_169);
                        if ( s>=0 ) return s;
                        break;
                    case 74 : 
                        int LA47_170 = input.LA(1);

                         
                        int index47_170 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_170);
                        if ( s>=0 ) return s;
                        break;
                    case 75 : 
                        int LA47_171 = input.LA(1);

                         
                        int index47_171 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_171);
                        if ( s>=0 ) return s;
                        break;
                    case 76 : 
                        int LA47_172 = input.LA(1);

                         
                        int index47_172 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_172);
                        if ( s>=0 ) return s;
                        break;
                    case 77 : 
                        int LA47_186 = input.LA(1);

                         
                        int index47_186 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_186);
                        if ( s>=0 ) return s;
                        break;
                    case 78 : 
                        int LA47_187 = input.LA(1);

                         
                        int index47_187 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_187);
                        if ( s>=0 ) return s;
                        break;
                    case 79 : 
                        int LA47_188 = input.LA(1);

                         
                        int index47_188 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_188);
                        if ( s>=0 ) return s;
                        break;
                    case 80 : 
                        int LA47_189 = input.LA(1);

                         
                        int index47_189 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_189);
                        if ( s>=0 ) return s;
                        break;
                    case 81 : 
                        int LA47_203 = input.LA(1);

                         
                        int index47_203 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_203);
                        if ( s>=0 ) return s;
                        break;
                    case 82 : 
                        int LA47_204 = input.LA(1);

                         
                        int index47_204 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_204);
                        if ( s>=0 ) return s;
                        break;
                    case 83 : 
                        int LA47_205 = input.LA(1);

                         
                        int index47_205 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_205);
                        if ( s>=0 ) return s;
                        break;
                    case 84 : 
                        int LA47_206 = input.LA(1);

                         
                        int index47_206 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_206);
                        if ( s>=0 ) return s;
                        break;
                    case 85 : 
                        int LA47_207 = input.LA(1);

                         
                        int index47_207 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_207);
                        if ( s>=0 ) return s;
                        break;
                    case 86 : 
                        int LA47_208 = input.LA(1);

                         
                        int index47_208 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_208);
                        if ( s>=0 ) return s;
                        break;
                    case 87 : 
                        int LA47_209 = input.LA(1);

                         
                        int index47_209 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_209);
                        if ( s>=0 ) return s;
                        break;
                    case 88 : 
                        int LA47_210 = input.LA(1);

                         
                        int index47_210 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_210);
                        if ( s>=0 ) return s;
                        break;
                    case 89 : 
                        int LA47_211 = input.LA(1);

                         
                        int index47_211 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_211);
                        if ( s>=0 ) return s;
                        break;
                    case 90 : 
                        int LA47_212 = input.LA(1);

                         
                        int index47_212 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_212);
                        if ( s>=0 ) return s;
                        break;
                    case 91 : 
                        int LA47_213 = input.LA(1);

                         
                        int index47_213 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_213);
                        if ( s>=0 ) return s;
                        break;
                    case 92 : 
                        int LA47_214 = input.LA(1);

                         
                        int index47_214 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_214);
                        if ( s>=0 ) return s;
                        break;
                    case 93 : 
                        int LA47_215 = input.LA(1);

                         
                        int index47_215 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_215);
                        if ( s>=0 ) return s;
                        break;
                    case 94 : 
                        int LA47_216 = input.LA(1);

                         
                        int index47_216 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_216);
                        if ( s>=0 ) return s;
                        break;
                    case 95 : 
                        int LA47_217 = input.LA(1);

                         
                        int index47_217 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_217);
                        if ( s>=0 ) return s;
                        break;
                    case 96 : 
                        int LA47_220 = input.LA(1);

                         
                        int index47_220 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_220);
                        if ( s>=0 ) return s;
                        break;
                    case 97 : 
                        int LA47_221 = input.LA(1);

                         
                        int index47_221 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_221);
                        if ( s>=0 ) return s;
                        break;
                    case 98 : 
                        int LA47_222 = input.LA(1);

                         
                        int index47_222 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_222);
                        if ( s>=0 ) return s;
                        break;
                    case 99 : 
                        int LA47_223 = input.LA(1);

                         
                        int index47_223 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_223);
                        if ( s>=0 ) return s;
                        break;
                    case 100 : 
                        int LA47_224 = input.LA(1);

                         
                        int index47_224 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_224);
                        if ( s>=0 ) return s;
                        break;
                    case 101 : 
                        int LA47_225 = input.LA(1);

                         
                        int index47_225 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_225);
                        if ( s>=0 ) return s;
                        break;
                    case 102 : 
                        int LA47_226 = input.LA(1);

                         
                        int index47_226 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_226);
                        if ( s>=0 ) return s;
                        break;
                    case 103 : 
                        int LA47_227 = input.LA(1);

                         
                        int index47_227 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_227);
                        if ( s>=0 ) return s;
                        break;
                    case 104 : 
                        int LA47_228 = input.LA(1);

                         
                        int index47_228 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_228);
                        if ( s>=0 ) return s;
                        break;
                    case 105 : 
                        int LA47_229 = input.LA(1);

                         
                        int index47_229 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_229);
                        if ( s>=0 ) return s;
                        break;
                    case 106 : 
                        int LA47_230 = input.LA(1);

                         
                        int index47_230 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_230);
                        if ( s>=0 ) return s;
                        break;
                    case 107 : 
                        int LA47_231 = input.LA(1);

                         
                        int index47_231 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_231);
                        if ( s>=0 ) return s;
                        break;
                    case 108 : 
                        int LA47_232 = input.LA(1);

                         
                        int index47_232 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_232);
                        if ( s>=0 ) return s;
                        break;
                    case 109 : 
                        int LA47_233 = input.LA(1);

                         
                        int index47_233 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_233);
                        if ( s>=0 ) return s;
                        break;
                    case 110 : 
                        int LA47_234 = input.LA(1);

                         
                        int index47_234 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_234);
                        if ( s>=0 ) return s;
                        break;
                    case 111 : 
                        int LA47_235 = input.LA(1);

                         
                        int index47_235 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_235);
                        if ( s>=0 ) return s;
                        break;
                    case 112 : 
                        int LA47_236 = input.LA(1);

                         
                        int index47_236 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_236);
                        if ( s>=0 ) return s;
                        break;
                    case 113 : 
                        int LA47_237 = input.LA(1);

                         
                        int index47_237 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_237);
                        if ( s>=0 ) return s;
                        break;
                    case 114 : 
                        int LA47_238 = input.LA(1);

                         
                        int index47_238 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_238);
                        if ( s>=0 ) return s;
                        break;
                    case 115 : 
                        int LA47_239 = input.LA(1);

                         
                        int index47_239 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_239);
                        if ( s>=0 ) return s;
                        break;
                    case 116 : 
                        int LA47_240 = input.LA(1);

                         
                        int index47_240 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_240);
                        if ( s>=0 ) return s;
                        break;
                    case 117 : 
                        int LA47_241 = input.LA(1);

                         
                        int index47_241 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_241);
                        if ( s>=0 ) return s;
                        break;
                    case 118 : 
                        int LA47_242 = input.LA(1);

                         
                        int index47_242 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_242);
                        if ( s>=0 ) return s;
                        break;
                    case 119 : 
                        int LA47_243 = input.LA(1);

                         
                        int index47_243 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_243);
                        if ( s>=0 ) return s;
                        break;
                    case 120 : 
                        int LA47_244 = input.LA(1);

                         
                        int index47_244 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_244);
                        if ( s>=0 ) return s;
                        break;
                    case 121 : 
                        int LA47_245 = input.LA(1);

                         
                        int index47_245 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_245);
                        if ( s>=0 ) return s;
                        break;
                    case 122 : 
                        int LA47_246 = input.LA(1);

                         
                        int index47_246 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_246);
                        if ( s>=0 ) return s;
                        break;
                    case 123 : 
                        int LA47_247 = input.LA(1);

                         
                        int index47_247 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_247);
                        if ( s>=0 ) return s;
                        break;
                    case 124 : 
                        int LA47_265 = input.LA(1);

                         
                        int index47_265 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_265);
                        if ( s>=0 ) return s;
                        break;
                    case 125 : 
                        int LA47_266 = input.LA(1);

                         
                        int index47_266 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_266);
                        if ( s>=0 ) return s;
                        break;
                    case 126 : 
                        int LA47_267 = input.LA(1);

                         
                        int index47_267 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_267);
                        if ( s>=0 ) return s;
                        break;
                    case 127 : 
                        int LA47_268 = input.LA(1);

                         
                        int index47_268 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_268);
                        if ( s>=0 ) return s;
                        break;
                    case 128 : 
                        int LA47_282 = input.LA(1);

                         
                        int index47_282 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_282);
                        if ( s>=0 ) return s;
                        break;
                    case 129 : 
                        int LA47_283 = input.LA(1);

                         
                        int index47_283 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_283);
                        if ( s>=0 ) return s;
                        break;
                    case 130 : 
                        int LA47_284 = input.LA(1);

                         
                        int index47_284 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_284);
                        if ( s>=0 ) return s;
                        break;
                    case 131 : 
                        int LA47_285 = input.LA(1);

                         
                        int index47_285 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_285);
                        if ( s>=0 ) return s;
                        break;
                    case 132 : 
                        int LA47_299 = input.LA(1);

                         
                        int index47_299 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_299);
                        if ( s>=0 ) return s;
                        break;
                    case 133 : 
                        int LA47_300 = input.LA(1);

                         
                        int index47_300 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_300);
                        if ( s>=0 ) return s;
                        break;
                    case 134 : 
                        int LA47_301 = input.LA(1);

                         
                        int index47_301 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_301);
                        if ( s>=0 ) return s;
                        break;
                    case 135 : 
                        int LA47_302 = input.LA(1);

                         
                        int index47_302 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_302);
                        if ( s>=0 ) return s;
                        break;
                    case 136 : 
                        int LA47_316 = input.LA(1);

                         
                        int index47_316 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_316);
                        if ( s>=0 ) return s;
                        break;
                    case 137 : 
                        int LA47_317 = input.LA(1);

                         
                        int index47_317 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_317);
                        if ( s>=0 ) return s;
                        break;
                    case 138 : 
                        int LA47_318 = input.LA(1);

                         
                        int index47_318 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_318);
                        if ( s>=0 ) return s;
                        break;
                    case 139 : 
                        int LA47_319 = input.LA(1);

                         
                        int index47_319 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_JPQL()) ) {s = 332;}

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
    static final String DFA48_eotS =
        "\55\uffff";
    static final String DFA48_eofS =
        "\55\uffff";
    static final String DFA48_minS =
        "\1\4\54\uffff";
    static final String DFA48_maxS =
        "\1\151\54\uffff";
    static final String DFA48_acceptS =
        "\1\uffff\1\1\33\uffff\1\2\17\uffff";
    static final String DFA48_specialS =
        "\55\uffff}>";
    static final String[] DFA48_transitionS = {
            "\1\1\5\uffff\1\1\3\uffff\2\1\1\35\1\1\3\35\11\uffff\1\35\1"+
            "\uffff\1\1\4\uffff\1\1\3\uffff\1\1\2\uffff\1\1\1\uffff\1\1\1"+
            "\35\1\1\1\uffff\2\1\3\uffff\1\1\7\uffff\2\1\1\uffff\1\35\1\1"+
            "\3\uffff\3\35\2\uffff\1\35\1\1\2\uffff\1\1\2\uffff\1\1\7\uffff"+
            "\2\1\2\uffff\4\1\5\35\2\1",
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
            ""
    };

    static final short[] DFA48_eot = DFA.unpackEncodedString(DFA48_eotS);
    static final short[] DFA48_eof = DFA.unpackEncodedString(DFA48_eofS);
    static final char[] DFA48_min = DFA.unpackEncodedStringToUnsignedChars(DFA48_minS);
    static final char[] DFA48_max = DFA.unpackEncodedStringToUnsignedChars(DFA48_maxS);
    static final short[] DFA48_accept = DFA.unpackEncodedString(DFA48_acceptS);
    static final short[] DFA48_special = DFA.unpackEncodedString(DFA48_specialS);
    static final short[][] DFA48_transition;

    static {
        int numStates = DFA48_transitionS.length;
        DFA48_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA48_transition[i] = DFA.unpackEncodedString(DFA48_transitionS[i]);
        }
    }

    class DFA48 extends DFA {

        public DFA48(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 48;
            this.eot = DFA48_eot;
            this.eof = DFA48_eof;
            this.min = DFA48_min;
            this.max = DFA48_max;
            this.accept = DFA48_accept;
            this.special = DFA48_special;
            this.transition = DFA48_transition;
        }
        public String getDescription() {
            return "653:1: simpleConditionalExpression returns [Object node] : (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] );";
        }
    }
    static final String DFA51_eotS =
        "\15\uffff";
    static final String DFA51_eofS =
        "\15\uffff";
    static final String DFA51_minS =
        "\1\13\14\uffff";
    static final String DFA51_maxS =
        "\1\132\14\uffff";
    static final String DFA51_acceptS =
        "\1\uffff\1\1\5\uffff\1\2\4\uffff\1\3";
    static final String DFA51_specialS =
        "\15\uffff}>";
    static final String[] DFA51_transitionS = {
            "\1\7\30\uffff\1\7\2\uffff\1\14\5\uffff\1\7\3\uffff\1\7\3\uffff"+
            "\1\7\34\uffff\1\1\3\uffff\5\1",
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

    static final short[] DFA51_eot = DFA.unpackEncodedString(DFA51_eotS);
    static final short[] DFA51_eof = DFA.unpackEncodedString(DFA51_eofS);
    static final char[] DFA51_min = DFA.unpackEncodedStringToUnsignedChars(DFA51_minS);
    static final char[] DFA51_max = DFA.unpackEncodedStringToUnsignedChars(DFA51_maxS);
    static final short[] DFA51_accept = DFA.unpackEncodedString(DFA51_acceptS);
    static final short[] DFA51_special = DFA.unpackEncodedString(DFA51_specialS);
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA51_transition[i] = DFA.unpackEncodedString(DFA51_transitionS[i]);
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }
        public String getDescription() {
            return "661:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );";
        }
    }
    static final String DFA55_eotS =
        "\56\uffff";
    static final String DFA55_eofS =
        "\56\uffff";
    static final String DFA55_minS =
        "\1\4\55\uffff";
    static final String DFA55_maxS =
        "\1\151\55\uffff";
    static final String DFA55_acceptS =
        "\1\uffff\1\1\53\uffff\1\2";
    static final String DFA55_specialS =
        "\56\uffff}>";
    static final String[] DFA55_transitionS = {
            "\1\1\5\uffff\1\1\3\uffff\7\1\11\uffff\1\1\1\uffff\1\1\4\uffff"+
            "\1\1\3\uffff\1\1\2\uffff\1\1\1\uffff\3\1\1\uffff\2\1\3\uffff"+
            "\1\1\5\uffff\1\55\1\uffff\2\1\1\uffff\2\1\3\uffff\3\1\2\uffff"+
            "\2\1\2\uffff\1\1\2\uffff\1\1\7\uffff\2\1\2\uffff\13\1",
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
            ""
    };

    static final short[] DFA55_eot = DFA.unpackEncodedString(DFA55_eotS);
    static final short[] DFA55_eof = DFA.unpackEncodedString(DFA55_eofS);
    static final char[] DFA55_min = DFA.unpackEncodedStringToUnsignedChars(DFA55_minS);
    static final char[] DFA55_max = DFA.unpackEncodedStringToUnsignedChars(DFA55_maxS);
    static final short[] DFA55_accept = DFA.unpackEncodedString(DFA55_acceptS);
    static final short[] DFA55_special = DFA.unpackEncodedString(DFA55_specialS);
    static final short[][] DFA55_transition;

    static {
        int numStates = DFA55_transitionS.length;
        DFA55_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA55_transition[i] = DFA.unpackEncodedString(DFA55_transitionS[i]);
        }
    }

    class DFA55 extends DFA {

        public DFA55(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 55;
            this.eot = DFA55_eot;
            this.eof = DFA55_eof;
            this.min = DFA55_min;
            this.max = DFA55_max;
            this.accept = DFA55_accept;
            this.special = DFA55_special;
            this.transition = DFA55_transition;
        }
        public String getDescription() {
            return "709:9: (itemNode= scalarOrSubSelectExpression ( COMMA itemNode= scalarOrSubSelectExpression )* | subqueryNode= subquery )";
        }
    }
    static final String DFA57_eotS =
        "\12\uffff";
    static final String DFA57_eofS =
        "\1\2\11\uffff";
    static final String DFA57_minS =
        "\1\6\11\uffff";
    static final String DFA57_maxS =
        "\1\124\11\uffff";
    static final String DFA57_acceptS =
        "\1\uffff\1\1\1\2\7\uffff";
    static final String DFA57_specialS =
        "\12\uffff}>";
    static final String[] DFA57_transitionS = {
            "\1\2\25\uffff\1\1\5\uffff\2\2\26\uffff\2\2\10\uffff\1\2\17"+
            "\uffff\1\2",
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

    static final short[] DFA57_eot = DFA.unpackEncodedString(DFA57_eotS);
    static final short[] DFA57_eof = DFA.unpackEncodedString(DFA57_eofS);
    static final char[] DFA57_min = DFA.unpackEncodedStringToUnsignedChars(DFA57_minS);
    static final char[] DFA57_max = DFA.unpackEncodedStringToUnsignedChars(DFA57_maxS);
    static final short[] DFA57_accept = DFA.unpackEncodedString(DFA57_acceptS);
    static final short[] DFA57_special = DFA.unpackEncodedString(DFA57_specialS);
    static final short[][] DFA57_transition;

    static {
        int numStates = DFA57_transitionS.length;
        DFA57_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA57_transition[i] = DFA.unpackEncodedString(DFA57_transitionS[i]);
        }
    }

    class DFA57 extends DFA {

        public DFA57(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 57;
            this.eot = DFA57_eot;
            this.eof = DFA57_eof;
            this.min = DFA57_min;
            this.max = DFA57_max;
            this.accept = DFA57_accept;
            this.special = DFA57_special;
            this.transition = DFA57_transition;
        }
        public String getDescription() {
            return "729:9: (escapeChars= escape )?";
        }
    }
    static final String DFA60_eotS =
        "\60\uffff";
    static final String DFA60_eofS =
        "\60\uffff";
    static final String DFA60_minS =
        "\1\4\57\uffff";
    static final String DFA60_maxS =
        "\1\151\57\uffff";
    static final String DFA60_acceptS =
        "\1\uffff\1\1\33\uffff\1\2\17\uffff\1\3\2\uffff";
    static final String DFA60_specialS =
        "\60\uffff}>";
    static final String[] DFA60_transitionS = {
            "\1\1\1\55\1\uffff\1\55\2\uffff\1\1\3\uffff\2\1\1\35\1\1\3\35"+
            "\11\uffff\1\35\1\uffff\1\1\4\uffff\1\1\3\uffff\1\1\2\uffff\1"+
            "\1\1\uffff\1\1\1\35\1\1\1\uffff\2\1\3\uffff\1\1\7\uffff\2\1"+
            "\1\55\1\35\1\1\3\uffff\3\35\2\uffff\1\35\1\1\2\uffff\1\1\2\uffff"+
            "\1\1\7\uffff\2\1\2\uffff\4\1\5\35\2\1",
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
            "",
            "",
            ""
    };

    static final short[] DFA60_eot = DFA.unpackEncodedString(DFA60_eotS);
    static final short[] DFA60_eof = DFA.unpackEncodedString(DFA60_eofS);
    static final char[] DFA60_min = DFA.unpackEncodedStringToUnsignedChars(DFA60_minS);
    static final char[] DFA60_max = DFA.unpackEncodedStringToUnsignedChars(DFA60_maxS);
    static final short[] DFA60_accept = DFA.unpackEncodedString(DFA60_acceptS);
    static final short[] DFA60_special = DFA.unpackEncodedString(DFA60_specialS);
    static final short[][] DFA60_transition;

    static {
        int numStates = DFA60_transitionS.length;
        DFA60_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA60_transition[i] = DFA.unpackEncodedString(DFA60_transitionS[i]);
        }
    }

    class DFA60 extends DFA {

        public DFA60(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 60;
            this.eot = DFA60_eot;
            this.eof = DFA60_eof;
            this.min = DFA60_min;
            this.max = DFA60_max;
            this.accept = DFA60_accept;
            this.special = DFA60_special;
            this.transition = DFA60_transition;
        }
        public String getDescription() {
            return "792:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression );";
        }
    }
    static final String DFA61_eotS =
        "\72\uffff";
    static final String DFA61_eofS =
        "\72\uffff";
    static final String DFA61_minS =
        "\1\4\27\uffff\1\4\41\uffff";
    static final String DFA61_maxS =
        "\1\151\27\uffff\1\151\41\uffff";
    static final String DFA61_acceptS =
        "\1\uffff\1\1\33\uffff\1\2\34\uffff";
    static final String DFA61_specialS =
        "\72\uffff}>";
    static final String[] DFA61_transitionS = {
            "\1\1\5\uffff\1\1\3\uffff\2\1\1\uffff\1\1\16\uffff\1\1\4\uffff"+
            "\1\1\3\uffff\1\1\2\uffff\1\1\1\uffff\1\1\1\uffff\1\1\1\uffff"+
            "\2\1\3\uffff\1\1\7\uffff\2\1\2\uffff\1\1\11\uffff\1\1\2\uffff"+
            "\1\1\2\uffff\1\30\7\uffff\2\1\2\uffff\4\1\5\uffff\2\1",
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
            "\1\1\5\uffff\1\1\3\uffff\2\1\1\uffff\1\1\16\uffff\1\1\4\uffff"+
            "\1\1\3\uffff\1\1\2\uffff\1\1\1\uffff\1\1\1\uffff\1\1\1\uffff"+
            "\2\1\3\uffff\1\1\5\uffff\1\35\1\uffff\2\1\2\uffff\1\1\11\uffff"+
            "\1\1\2\uffff\1\1\2\uffff\1\1\7\uffff\2\1\2\uffff\4\1\5\uffff"+
            "\2\1",
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
            ""
    };

    static final short[] DFA61_eot = DFA.unpackEncodedString(DFA61_eotS);
    static final short[] DFA61_eof = DFA.unpackEncodedString(DFA61_eofS);
    static final char[] DFA61_min = DFA.unpackEncodedStringToUnsignedChars(DFA61_minS);
    static final char[] DFA61_max = DFA.unpackEncodedStringToUnsignedChars(DFA61_maxS);
    static final short[] DFA61_accept = DFA.unpackEncodedString(DFA61_acceptS);
    static final short[] DFA61_special = DFA.unpackEncodedString(DFA61_specialS);
    static final short[][] DFA61_transition;

    static {
        int numStates = DFA61_transitionS.length;
        DFA61_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA61_transition[i] = DFA.unpackEncodedString(DFA61_transitionS[i]);
        }
    }

    class DFA61 extends DFA {

        public DFA61(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 61;
            this.eot = DFA61_eot;
            this.eof = DFA61_eof;
            this.min = DFA61_min;
            this.max = DFA61_max;
            this.accept = DFA61_accept;
            this.special = DFA61_special;
            this.transition = DFA61_transition;
        }
        public String getDescription() {
            return "799:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );";
        }
    }
    static final String DFA62_eotS =
        "\42\uffff";
    static final String DFA62_eofS =
        "\1\1\41\uffff";
    static final String DFA62_minS =
        "\1\6\41\uffff";
    static final String DFA62_maxS =
        "\1\134\41\uffff";
    static final String DFA62_acceptS =
        "\1\uffff\1\3\36\uffff\1\1\1\2";
    static final String DFA62_specialS =
        "\42\uffff}>";
    static final String[] DFA62_transitionS = {
            "\1\1\1\uffff\2\1\1\uffff\1\1\11\uffff\1\1\2\uffff\1\1\1\uffff"+
            "\1\1\1\uffff\1\1\4\uffff\4\1\2\uffff\1\1\5\uffff\1\1\3\uffff"+
            "\1\1\3\uffff\1\1\4\uffff\2\1\10\uffff\1\1\11\uffff\5\1\1\uffff"+
            "\1\1\1\uffff\5\1\1\40\1\41",
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
            ""
    };

    static final short[] DFA62_eot = DFA.unpackEncodedString(DFA62_eotS);
    static final short[] DFA62_eof = DFA.unpackEncodedString(DFA62_eofS);
    static final char[] DFA62_min = DFA.unpackEncodedStringToUnsignedChars(DFA62_minS);
    static final char[] DFA62_max = DFA.unpackEncodedStringToUnsignedChars(DFA62_maxS);
    static final short[] DFA62_accept = DFA.unpackEncodedString(DFA62_acceptS);
    static final short[] DFA62_special = DFA.unpackEncodedString(DFA62_specialS);
    static final short[][] DFA62_transition;

    static {
        int numStates = DFA62_transitionS.length;
        DFA62_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA62_transition[i] = DFA.unpackEncodedString(DFA62_transitionS[i]);
        }
    }

    class DFA62 extends DFA {

        public DFA62(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 62;
            this.eot = DFA62_eot;
            this.eof = DFA62_eof;
            this.min = DFA62_min;
            this.max = DFA62_max;
            this.accept = DFA62_accept;
            this.special = DFA62_special;
            this.transition = DFA62_transition;
        }
        public String getDescription() {
            return "()* loopback of 810:9: (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*";
        }
    }
    static final String DFA63_eotS =
        "\44\uffff";
    static final String DFA63_eofS =
        "\1\1\43\uffff";
    static final String DFA63_minS =
        "\1\6\43\uffff";
    static final String DFA63_maxS =
        "\1\136\43\uffff";
    static final String DFA63_acceptS =
        "\1\uffff\1\3\40\uffff\1\1\1\2";
    static final String DFA63_specialS =
        "\44\uffff}>";
    static final String[] DFA63_transitionS = {
            "\1\1\1\uffff\2\1\1\uffff\1\1\11\uffff\1\1\2\uffff\1\1\1\uffff"+
            "\1\1\1\uffff\1\1\4\uffff\4\1\2\uffff\1\1\5\uffff\1\1\3\uffff"+
            "\1\1\3\uffff\1\1\4\uffff\2\1\10\uffff\1\1\11\uffff\5\1\1\uffff"+
            "\1\1\1\uffff\7\1\1\42\1\43",
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
            ""
    };

    static final short[] DFA63_eot = DFA.unpackEncodedString(DFA63_eotS);
    static final short[] DFA63_eof = DFA.unpackEncodedString(DFA63_eofS);
    static final char[] DFA63_min = DFA.unpackEncodedStringToUnsignedChars(DFA63_minS);
    static final char[] DFA63_max = DFA.unpackEncodedStringToUnsignedChars(DFA63_maxS);
    static final short[] DFA63_accept = DFA.unpackEncodedString(DFA63_acceptS);
    static final short[] DFA63_special = DFA.unpackEncodedString(DFA63_specialS);
    static final short[][] DFA63_transition;

    static {
        int numStates = DFA63_transitionS.length;
        DFA63_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA63_transition[i] = DFA.unpackEncodedString(DFA63_transitionS[i]);
        }
    }

    class DFA63 extends DFA {

        public DFA63(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 63;
            this.eot = DFA63_eot;
            this.eof = DFA63_eof;
            this.min = DFA63_min;
            this.max = DFA63_max;
            this.accept = DFA63_accept;
            this.special = DFA63_special;
            this.transition = DFA63_transition;
        }
        public String getDescription() {
            return "()* loopback of 822:9: (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*";
        }
    }
    static final String DFA64_eotS =
        "\35\uffff";
    static final String DFA64_eofS =
        "\35\uffff";
    static final String DFA64_minS =
        "\1\4\34\uffff";
    static final String DFA64_maxS =
        "\1\151\34\uffff";
    static final String DFA64_acceptS =
        "\1\uffff\1\1\1\2\1\3\31\uffff";
    static final String DFA64_specialS =
        "\35\uffff}>";
    static final String[] DFA64_transitionS = {
            "\1\3\5\uffff\1\3\3\uffff\2\3\1\uffff\1\3\16\uffff\1\3\4\uffff"+
            "\1\3\3\uffff\1\3\2\uffff\1\3\1\uffff\1\3\1\uffff\1\3\1\uffff"+
            "\2\3\3\uffff\1\3\7\uffff\2\3\2\uffff\1\3\11\uffff\1\3\2\uffff"+
            "\1\3\2\uffff\1\3\7\uffff\1\1\1\2\2\uffff\4\3\5\uffff\2\3",
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
            ""
    };

    static final short[] DFA64_eot = DFA.unpackEncodedString(DFA64_eotS);
    static final short[] DFA64_eof = DFA.unpackEncodedString(DFA64_eofS);
    static final char[] DFA64_min = DFA.unpackEncodedStringToUnsignedChars(DFA64_minS);
    static final char[] DFA64_max = DFA.unpackEncodedStringToUnsignedChars(DFA64_maxS);
    static final short[] DFA64_accept = DFA.unpackEncodedString(DFA64_acceptS);
    static final short[] DFA64_special = DFA.unpackEncodedString(DFA64_specialS);
    static final short[][] DFA64_transition;

    static {
        int numStates = DFA64_transitionS.length;
        DFA64_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA64_transition[i] = DFA.unpackEncodedString(DFA64_transitionS[i]);
        }
    }

    class DFA64 extends DFA {

        public DFA64(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 64;
            this.eot = DFA64_eot;
            this.eof = DFA64_eof;
            this.min = DFA64_min;
            this.max = DFA64_max;
            this.accept = DFA64_accept;
            this.special = DFA64_special;
            this.transition = DFA64_transition;
        }
        public String getDescription() {
            return "829:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );";
        }
    }
    static final String DFA65_eotS =
        "\33\uffff";
    static final String DFA65_eofS =
        "\33\uffff";
    static final String DFA65_minS =
        "\1\4\32\uffff";
    static final String DFA65_maxS =
        "\1\151\32\uffff";
    static final String DFA65_acceptS =
        "\1\uffff\1\1\4\uffff\1\2\2\uffff\1\3\1\uffff\1\4\2\uffff\1\5\7"+
        "\uffff\1\6\1\7\3\uffff";
    static final String DFA65_specialS =
        "\33\uffff}>";
    static final String[] DFA65_transitionS = {
            "\1\16\5\uffff\1\1\3\uffff\2\13\1\uffff\1\1\16\uffff\1\16\4"+
            "\uffff\1\16\3\uffff\1\6\2\uffff\1\16\1\uffff\1\16\1\uffff\1"+
            "\1\1\uffff\1\1\1\16\3\uffff\1\13\7\uffff\2\16\2\uffff\1\1\11"+
            "\uffff\1\6\2\uffff\1\6\2\uffff\1\26\13\uffff\4\27\5\uffff\2"+
            "\11",
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
            ""
    };

    static final short[] DFA65_eot = DFA.unpackEncodedString(DFA65_eotS);
    static final short[] DFA65_eof = DFA.unpackEncodedString(DFA65_eofS);
    static final char[] DFA65_min = DFA.unpackEncodedStringToUnsignedChars(DFA65_minS);
    static final char[] DFA65_max = DFA.unpackEncodedStringToUnsignedChars(DFA65_maxS);
    static final short[] DFA65_accept = DFA.unpackEncodedString(DFA65_acceptS);
    static final short[] DFA65_special = DFA.unpackEncodedString(DFA65_specialS);
    static final short[][] DFA65_transition;

    static {
        int numStates = DFA65_transitionS.length;
        DFA65_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA65_transition[i] = DFA.unpackEncodedString(DFA65_transitionS[i]);
        }
    }

    class DFA65 extends DFA {

        public DFA65(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 65;
            this.eot = DFA65_eot;
            this.eof = DFA65_eof;
            this.min = DFA65_min;
            this.max = DFA65_max;
            this.accept = DFA65_accept;
            this.special = DFA65_special;
            this.transition = DFA65_transition;
        }
        public String getDescription() {
            return "838:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric );";
        }
    }
    static final String DFA66_eotS =
        "\55\uffff";
    static final String DFA66_eofS =
        "\55\uffff";
    static final String DFA66_minS =
        "\1\4\54\uffff";
    static final String DFA66_maxS =
        "\1\151\54\uffff";
    static final String DFA66_acceptS =
        "\1\uffff\1\1\33\uffff\1\2\17\uffff";
    static final String DFA66_specialS =
        "\55\uffff}>";
    static final String[] DFA66_transitionS = {
            "\1\1\5\uffff\1\1\3\uffff\2\1\1\35\1\1\3\35\11\uffff\1\35\1"+
            "\uffff\1\1\4\uffff\1\1\3\uffff\1\1\2\uffff\1\1\1\uffff\1\1\1"+
            "\35\1\1\1\uffff\2\1\3\uffff\1\1\7\uffff\2\1\1\uffff\1\35\1\1"+
            "\3\uffff\3\35\2\uffff\1\35\1\1\2\uffff\1\1\2\uffff\1\1\7\uffff"+
            "\2\1\2\uffff\4\1\5\35\2\1",
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
            ""
    };

    static final short[] DFA66_eot = DFA.unpackEncodedString(DFA66_eotS);
    static final short[] DFA66_eof = DFA.unpackEncodedString(DFA66_eofS);
    static final char[] DFA66_min = DFA.unpackEncodedStringToUnsignedChars(DFA66_minS);
    static final char[] DFA66_max = DFA.unpackEncodedStringToUnsignedChars(DFA66_maxS);
    static final short[] DFA66_accept = DFA.unpackEncodedString(DFA66_acceptS);
    static final short[] DFA66_special = DFA.unpackEncodedString(DFA66_specialS);
    static final short[][] DFA66_transition;

    static {
        int numStates = DFA66_transitionS.length;
        DFA66_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA66_transition[i] = DFA.unpackEncodedString(DFA66_transitionS[i]);
        }
    }

    class DFA66 extends DFA {

        public DFA66(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 66;
            this.eot = DFA66_eot;
            this.eof = DFA66_eof;
            this.min = DFA66_min;
            this.max = DFA66_max;
            this.accept = DFA66_accept;
            this.special = DFA66_special;
            this.transition = DFA66_transition;
        }
        public String getDescription() {
            return "849:1: scalarExpression returns [Object node] : (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression );";
        }
    }
    static final String DFA67_eotS =
        "\55\uffff";
    static final String DFA67_eofS =
        "\55\uffff";
    static final String DFA67_minS =
        "\1\4\54\uffff";
    static final String DFA67_maxS =
        "\1\151\54\uffff";
    static final String DFA67_acceptS =
        "\1\uffff\1\1\33\uffff\1\2\17\uffff";
    static final String DFA67_specialS =
        "\55\uffff}>";
    static final String[] DFA67_transitionS = {
            "\1\1\5\uffff\1\1\3\uffff\2\1\1\35\1\1\3\35\11\uffff\1\35\1"+
            "\uffff\1\1\4\uffff\1\1\3\uffff\1\1\2\uffff\1\1\1\uffff\1\1\1"+
            "\35\1\1\1\uffff\2\1\3\uffff\1\1\7\uffff\2\1\1\uffff\1\35\1\1"+
            "\3\uffff\3\35\2\uffff\1\35\1\1\2\uffff\1\1\2\uffff\1\1\7\uffff"+
            "\2\1\2\uffff\4\1\5\35\2\1",
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
            ""
    };

    static final short[] DFA67_eot = DFA.unpackEncodedString(DFA67_eotS);
    static final short[] DFA67_eof = DFA.unpackEncodedString(DFA67_eofS);
    static final char[] DFA67_min = DFA.unpackEncodedStringToUnsignedChars(DFA67_minS);
    static final char[] DFA67_max = DFA.unpackEncodedStringToUnsignedChars(DFA67_maxS);
    static final short[] DFA67_accept = DFA.unpackEncodedString(DFA67_acceptS);
    static final short[] DFA67_special = DFA.unpackEncodedString(DFA67_specialS);
    static final short[][] DFA67_transition;

    static {
        int numStates = DFA67_transitionS.length;
        DFA67_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA67_transition[i] = DFA.unpackEncodedString(DFA67_transitionS[i]);
        }
    }

    class DFA67 extends DFA {

        public DFA67(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 67;
            this.eot = DFA67_eot;
            this.eof = DFA67_eof;
            this.min = DFA67_min;
            this.max = DFA67_max;
            this.accept = DFA67_accept;
            this.special = DFA67_special;
            this.transition = DFA67_transition;
        }
        public String getDescription() {
            return "855:1: scalarOrSubSelectExpression returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression );";
        }
    }
    static final String DFA68_eotS =
        "\21\uffff";
    static final String DFA68_eofS =
        "\21\uffff";
    static final String DFA68_minS =
        "\1\20\20\uffff";
    static final String DFA68_maxS =
        "\1\147\20\uffff";
    static final String DFA68_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\4\uffff\1\3\1\uffff\1\4\1\uffff\1\5\2"+
        "\uffff\1\6";
    static final String DFA68_specialS =
        "\21\uffff}>";
    static final String[] DFA68_transitionS = {
            "\1\4\1\uffff\3\1\11\uffff\1\13\20\uffff\1\4\22\uffff\1\4\4"+
            "\uffff\1\4\1\13\1\20\2\uffff\1\4\26\uffff\2\11\3\15",
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
            ""
    };

    static final short[] DFA68_eot = DFA.unpackEncodedString(DFA68_eotS);
    static final short[] DFA68_eof = DFA.unpackEncodedString(DFA68_eofS);
    static final char[] DFA68_min = DFA.unpackEncodedStringToUnsignedChars(DFA68_minS);
    static final char[] DFA68_max = DFA.unpackEncodedStringToUnsignedChars(DFA68_maxS);
    static final short[] DFA68_accept = DFA.unpackEncodedString(DFA68_acceptS);
    static final short[] DFA68_special = DFA.unpackEncodedString(DFA68_specialS);
    static final short[][] DFA68_transition;

    static {
        int numStates = DFA68_transitionS.length;
        DFA68_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA68_transition[i] = DFA.unpackEncodedString(DFA68_transitionS[i]);
        }
    }

    class DFA68 extends DFA {

        public DFA68(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 68;
            this.eot = DFA68_eot;
            this.eof = DFA68_eof;
            this.min = DFA68_min;
            this.max = DFA68_max;
            this.accept = DFA68_accept;
            this.special = DFA68_special;
            this.transition = DFA68_transition;
        }
        public String getDescription() {
            return "861:1: nonArithmeticScalarExpression returns [Object node] : (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression );";
        }
    }
    static final String DFA77_eotS =
        "\15\uffff";
    static final String DFA77_eofS =
        "\15\uffff";
    static final String DFA77_minS =
        "\1\20\14\uffff";
    static final String DFA77_maxS =
        "\1\151\14\uffff";
    static final String DFA77_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\4\uffff\1\3\1\uffff\1\4\2\uffff";
    static final String DFA77_specialS =
        "\15\uffff}>";
    static final String[] DFA77_transitionS = {
            "\1\3\30\uffff\1\12\5\uffff\1\3\22\uffff\1\3\4\uffff\1\3\4\uffff"+
            "\1\3\1\12\2\uffff\1\12\22\uffff\2\1\3\uffff\2\10",
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

    static final short[] DFA77_eot = DFA.unpackEncodedString(DFA77_eotS);
    static final short[] DFA77_eof = DFA.unpackEncodedString(DFA77_eofS);
    static final char[] DFA77_min = DFA.unpackEncodedStringToUnsignedChars(DFA77_minS);
    static final char[] DFA77_max = DFA.unpackEncodedStringToUnsignedChars(DFA77_maxS);
    static final short[] DFA77_accept = DFA.unpackEncodedString(DFA77_acceptS);
    static final short[] DFA77_special = DFA.unpackEncodedString(DFA77_specialS);
    static final short[][] DFA77_transition;

    static {
        int numStates = DFA77_transitionS.length;
        DFA77_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA77_transition[i] = DFA.unpackEncodedString(DFA77_transitionS[i]);
        }
    }

    class DFA77 extends DFA {

        public DFA77(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 77;
            this.eot = DFA77_eot;
            this.eof = DFA77_eof;
            this.min = DFA77_min;
            this.max = DFA77_max;
            this.accept = DFA77_accept;
            this.special = DFA77_special;
            this.transition = DFA77_transition;
        }
        public String getDescription() {
            return "985:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );";
        }
    }
    static final String DFA89_eotS =
        "\31\uffff";
    static final String DFA89_eofS =
        "\31\uffff";
    static final String DFA89_minS =
        "\1\14\3\uffff\4\41\21\uffff";
    static final String DFA89_maxS =
        "\1\151\3\uffff\4\124\21\uffff";
    static final String DFA89_acceptS =
        "\1\uffff\1\1\7\uffff\1\2\17\uffff";
    static final String DFA89_specialS =
        "\31\uffff}>";
    static final String[] DFA89_transitionS = {
            "\1\1\3\uffff\1\11\20\uffff\1\1\7\uffff\1\11\1\1\4\uffff\1\11"+
            "\22\uffff\1\11\2\uffff\1\1\1\uffff\1\11\4\uffff\2\11\2\uffff"+
            "\1\11\22\uffff\1\4\1\5\3\uffff\1\6\1\7",
            "",
            "",
            "",
            "\1\1\62\uffff\1\11",
            "\1\1\62\uffff\1\11",
            "\1\1\62\uffff\1\11",
            "\1\1\62\uffff\1\11",
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
            ""
    };

    static final short[] DFA89_eot = DFA.unpackEncodedString(DFA89_eotS);
    static final short[] DFA89_eof = DFA.unpackEncodedString(DFA89_eofS);
    static final char[] DFA89_min = DFA.unpackEncodedStringToUnsignedChars(DFA89_minS);
    static final char[] DFA89_max = DFA.unpackEncodedStringToUnsignedChars(DFA89_maxS);
    static final short[] DFA89_accept = DFA.unpackEncodedString(DFA89_acceptS);
    static final short[] DFA89_special = DFA.unpackEncodedString(DFA89_specialS);
    static final short[][] DFA89_transition;

    static {
        int numStates = DFA89_transitionS.length;
        DFA89_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA89_transition[i] = DFA.unpackEncodedString(DFA89_transitionS[i]);
        }
    }

    class DFA89 extends DFA {

        public DFA89(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 89;
            this.eot = DFA89_eot;
            this.eof = DFA89_eof;
            this.min = DFA89_min;
            this.max = DFA89_max;
            this.accept = DFA89_accept;
            this.special = DFA89_special;
            this.transition = DFA89_transition;
        }
        public String getDescription() {
            return "1150:9: (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )?";
        }
    }
    static final String DFA97_eotS =
        "\12\uffff";
    static final String DFA97_eofS =
        "\12\uffff";
    static final String DFA97_minS =
        "\1\12\11\uffff";
    static final String DFA97_maxS =
        "\1\120\11\uffff";
    static final String DFA97_acceptS =
        "\1\uffff\1\1\1\2\7\uffff";
    static final String DFA97_specialS =
        "\12\uffff}>";
    static final String[] DFA97_transitionS = {
            "\1\2\6\uffff\1\2\5\uffff\1\1\21\uffff\1\2\6\uffff\1\2\1\uffff"+
            "\1\2\20\uffff\1\2\11\uffff\1\2\2\uffff\1\2",
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

    static final short[] DFA97_eot = DFA.unpackEncodedString(DFA97_eotS);
    static final short[] DFA97_eof = DFA.unpackEncodedString(DFA97_eofS);
    static final char[] DFA97_min = DFA.unpackEncodedStringToUnsignedChars(DFA97_minS);
    static final char[] DFA97_max = DFA.unpackEncodedStringToUnsignedChars(DFA97_maxS);
    static final short[] DFA97_accept = DFA.unpackEncodedString(DFA97_acceptS);
    static final short[] DFA97_special = DFA.unpackEncodedString(DFA97_specialS);
    static final short[][] DFA97_transition;

    static {
        int numStates = DFA97_transitionS.length;
        DFA97_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA97_transition[i] = DFA.unpackEncodedString(DFA97_transitionS[i]);
        }
    }

    class DFA97 extends DFA {

        public DFA97(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 97;
            this.eot = DFA97_eot;
            this.eof = DFA97_eof;
            this.min = DFA97_min;
            this.max = DFA97_max;
            this.accept = DFA97_accept;
            this.special = DFA97_special;
            this.transition = DFA97_transition;
        }
        public String getDescription() {
            return "1292:16: ( DISTINCT )?";
        }
    }
    static final String DFA98_eotS =
        "\13\uffff";
    static final String DFA98_eofS =
        "\13\uffff";
    static final String DFA98_minS =
        "\1\12\1\41\11\uffff";
    static final String DFA98_maxS =
        "\1\120\1\125\11\uffff";
    static final String DFA98_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\5\uffff\1\3";
    static final String DFA98_specialS =
        "\13\uffff}>";
    static final String[] DFA98_transitionS = {
            "\1\4\6\uffff\1\4\27\uffff\1\2\6\uffff\1\4\1\uffff\1\4\20\uffff"+
            "\1\4\11\uffff\1\2\2\uffff\1\1",
            "\1\12\63\uffff\1\2",
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

    static final short[] DFA98_eot = DFA.unpackEncodedString(DFA98_eotS);
    static final short[] DFA98_eof = DFA.unpackEncodedString(DFA98_eofS);
    static final char[] DFA98_min = DFA.unpackEncodedStringToUnsignedChars(DFA98_minS);
    static final char[] DFA98_max = DFA.unpackEncodedStringToUnsignedChars(DFA98_maxS);
    static final short[] DFA98_accept = DFA.unpackEncodedString(DFA98_acceptS);
    static final short[] DFA98_special = DFA.unpackEncodedString(DFA98_specialS);
    static final short[][] DFA98_transition;

    static {
        int numStates = DFA98_transitionS.length;
        DFA98_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA98_transition[i] = DFA.unpackEncodedString(DFA98_transitionS[i]);
        }
    }

    class DFA98 extends DFA {

        public DFA98(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 98;
            this.eot = DFA98_eot;
            this.eof = DFA98_eof;
            this.min = DFA98_min;
            this.max = DFA98_max;
            this.accept = DFA98_accept;
            this.special = DFA98_special;
            this.transition = DFA98_transition;
        }
        public String getDescription() {
            return "1302:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );";
        }
    }
    static final String DFA102_eotS =
        "\22\uffff";
    static final String DFA102_eofS =
        "\22\uffff";
    static final String DFA102_minS =
        "\1\4\4\10\15\uffff";
    static final String DFA102_maxS =
        "\1\167\1\125\3\123\15\uffff";
    static final String DFA102_acceptS =
        "\5\uffff\1\1\2\uffff\1\2\6\uffff\1\3\2\uffff";
    static final String DFA102_specialS =
        "\22\uffff}>";
    static final String[] DFA102_transitionS = {
            "\40\5\1\4\4\5\1\2\43\5\1\3\2\5\1\1\47\5",
            "\1\5\107\uffff\1\5\4\uffff\1\10",
            "\1\5\107\uffff\1\5\2\uffff\1\10",
            "\1\5\107\uffff\1\5\2\uffff\1\10",
            "\1\5\107\uffff\1\5\2\uffff\1\17",
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

    static final short[] DFA102_eot = DFA.unpackEncodedString(DFA102_eotS);
    static final short[] DFA102_eof = DFA.unpackEncodedString(DFA102_eofS);
    static final char[] DFA102_min = DFA.unpackEncodedStringToUnsignedChars(DFA102_minS);
    static final char[] DFA102_max = DFA.unpackEncodedStringToUnsignedChars(DFA102_maxS);
    static final short[] DFA102_accept = DFA.unpackEncodedString(DFA102_acceptS);
    static final short[] DFA102_special = DFA.unpackEncodedString(DFA102_specialS);
    static final short[][] DFA102_transition;

    static {
        int numStates = DFA102_transitionS.length;
        DFA102_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA102_transition[i] = DFA.unpackEncodedString(DFA102_transitionS[i]);
        }
    }

    class DFA102 extends DFA {

        public DFA102(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 102;
            this.eot = DFA102_eot;
            this.eof = DFA102_eof;
            this.min = DFA102_min;
            this.max = DFA102_max;
            this.accept = DFA102_accept;
            this.special = DFA102_special;
            this.transition = DFA102_transition;
        }
        public String getDescription() {
            return "1327:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );";
        }
    }
    static final String DFA101_eotS =
        "\12\uffff";
    static final String DFA101_eofS =
        "\12\uffff";
    static final String DFA101_minS =
        "\1\42\11\uffff";
    static final String DFA101_maxS =
        "\1\124\11\uffff";
    static final String DFA101_acceptS =
        "\1\uffff\1\2\5\uffff\1\1\2\uffff";
    static final String DFA101_specialS =
        "\12\uffff}>";
    static final String[] DFA101_transitionS = {
            "\3\1\1\uffff\1\7\1\uffff\1\7\2\uffff\1\7\43\uffff\1\1\1\uffff"+
            "\1\1\2\uffff\1\1",
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

    static final short[] DFA101_eot = DFA.unpackEncodedString(DFA101_eotS);
    static final short[] DFA101_eof = DFA.unpackEncodedString(DFA101_eofS);
    static final char[] DFA101_min = DFA.unpackEncodedStringToUnsignedChars(DFA101_minS);
    static final char[] DFA101_max = DFA.unpackEncodedStringToUnsignedChars(DFA101_maxS);
    static final short[] DFA101_accept = DFA.unpackEncodedString(DFA101_acceptS);
    static final short[] DFA101_special = DFA.unpackEncodedString(DFA101_specialS);
    static final short[][] DFA101_transition;

    static {
        int numStates = DFA101_transitionS.length;
        DFA101_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA101_transition[i] = DFA.unpackEncodedString(DFA101_transitionS[i]);
        }
    }

    class DFA101 extends DFA {

        public DFA101(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 101;
            this.eot = DFA101_eot;
            this.eof = DFA101_eof;
            this.min = DFA101_min;
            this.max = DFA101_max;
            this.accept = DFA101_accept;
            this.special = DFA101_special;
            this.transition = DFA101_transition;
        }
        public String getDescription() {
            return "()* loopback of 1330:51: ( join )*";
        }
    }
 

    public static final BitSet FOLLOW_selectStatement_in_document763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateStatement_in_document777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteStatement_in_document791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectClause_in_selectStatement824 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_fromClause_in_selectStatement839 = new BitSet(new long[]{0x0800000C00000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_whereClause_in_selectStatement854 = new BitSet(new long[]{0x0800000C00000000L});
    public static final BitSet FOLLOW_groupByClause_in_selectStatement869 = new BitSet(new long[]{0x0800000800000000L});
    public static final BitSet FOLLOW_havingClause_in_selectStatement885 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_orderByClause_in_selectStatement900 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_selectStatement910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateClause_in_updateStatement953 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_setClause_in_updateStatement968 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_whereClause_in_updateStatement982 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_updateStatement992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPDATE_in_updateClause1024 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_updateClause1030 = new BitSet(new long[]{0x0000000000000102L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_updateClause1043 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_updateClause1051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_setClause1100 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1106 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_setClause1119 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1125 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_setAssignmentTarget_in_setAssignmentClause1183 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_EQUALS_in_setAssignmentClause1187 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_newValue_in_setAssignmentClause1193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_setAssignmentTarget1223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_setAssignmentTarget1238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalarExpression_in_newValue1270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_newValue1284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteClause_in_deleteStatement1328 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_whereClause_in_deleteStatement1341 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_deleteStatement1351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_deleteClause1384 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_FROM_in_deleteClause1386 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_deleteClause1392 = new BitSet(new long[]{0x0000000000000102L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_deleteClause1405 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_deleteClause1411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_selectClause1458 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause1461 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_selectItem_in_selectClause1480 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_selectClause1508 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_selectItem_in_selectClause1514 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_selectExpression_in_selectItem1610 = new BitSet(new long[]{0x0000000000000102L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_selectItem1614 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_selectItem1622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_selectExpression1666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalarExpression_in_selectExpression1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBJECT_in_selectExpression1690 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1692 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_selectExpression1698 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constructorExpression_in_selectExpression1715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntryExpression_in_selectExpression1730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_in_mapEntryExpression1770 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1772 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1778 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1812 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_DOT_in_pathExprOrVariableAccess1827 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExprOrVariableAccess1833 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KEY_in_qualifiedIdentificationVariable1903 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1905 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1911 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUE_in_qualifiedIdentificationVariable1928 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1930 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1936 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AVG_in_aggregateExpression1971 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1973 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1976 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_aggregateExpression1994 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAX_in_aggregateExpression2017 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2019 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2022 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_aggregateExpression2041 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MIN_in_aggregateExpression2063 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2065 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2068 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_aggregateExpression2086 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUM_in_aggregateExpression2108 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2110 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2113 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_aggregateExpression2131 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COUNT_in_aggregateExpression2153 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2155 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2158 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_aggregateExpression2176 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_constructorExpression2221 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_constructorName_in_constructorExpression2227 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2237 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2252 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_COMMA_in_constructorExpression2267 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2273 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2329 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_DOT_in_constructorName2343 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2347 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_scalarExpression_in_constructorItem2391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_constructorItem2405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_fromClause2439 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2441 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_fromClause2453 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2458 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_fromClause2483 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2549 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_join_in_identificationVariableDeclaration2568 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2603 = new BitSet(new long[]{0x0000000000000100L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_rangeVariableDeclaration2606 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_rangeVariableDeclaration2612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_joinSpec_in_join2695 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2709 = new BitSet(new long[]{0x0000000000000100L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_join2712 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_join2718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREAT_in_join2747 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_join2749 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2755 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_AS_in_join2757 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_join2763 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_join2765 = new BitSet(new long[]{0x0000000000000100L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_join2768 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_join2774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FETCH_in_join2797 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_in_joinSpec2849 = new BitSet(new long[]{0x1000010000000000L});
    public static final BitSet FOLLOW_OUTER_in_joinSpec2852 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_INNER_in_joinSpec2861 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_JOIN_in_joinSpec2867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_collectionMemberDeclaration2895 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2897 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2903 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2905 = new BitSet(new long[]{0x0000000000000100L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_collectionMemberDeclaration2915 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_collectionMemberDeclaration2921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_collectionValuedPathExpression2959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_associationPathExpression2991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_joinAssociationPathExpression3024 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_DOT_in_joinAssociationPathExpression3039 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_joinAssociationPathExpression3045 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_pathExpression_in_singleValuedPathExpression3101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_stateFieldPathExpression3133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExpression3165 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_DOT_in_pathExpression3180 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExpression3186 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_IDENT_in_variableAccessOrTypeConstant3282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause3320 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_conditionalExpression_in_whereClause3326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3368 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_OR_in_conditionalExpression3383 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3389 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3444 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AND_in_conditionalTerm3459 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3465 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_NOT_in_conditionalFactor3520 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_conditionalPrimary_in_conditionalFactor3539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsExpression_in_conditionalFactor3568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3625 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalPrimary3631 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleConditionalExpression_in_conditionalPrimary3647 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_simpleConditionalExpression3679 = new BitSet(new long[]{0x0022209000000800L,0x0000000007C40000L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3700 = new BitSet(new long[]{0x0022209000000800L,0x0000000007C40000L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3755 = new BitSet(new long[]{0x0022201000000800L});
    public static final BitSet FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_simpleConditionalExpressionRemainder3774 = new BitSet(new long[]{0x0060000002000000L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3779 = new BitSet(new long[]{0x0060000002000000L});
    public static final BitSet FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_betweenExpression_in_conditionWithNotExpression3822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_likeExpression_in_conditionWithNotExpression3837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inExpression_in_conditionWithNotExpression3851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullComparisonExpression_in_isExpression3900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptyCollectionComparisonExpression_in_isExpression3915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpression3948 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarOrSubSelectExpression_in_betweenExpression3962 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_AND_in_betweenExpression3964 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarOrSubSelectExpression_in_betweenExpression3970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression4016 = new BitSet(new long[]{0x0000000000000000L,0x0000030000000000L});
    public static final BitSet FOLLOW_inputParameter_in_inExpression4022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression4049 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_inExpression4059 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarOrSubSelectExpression_in_inExpression4075 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression4093 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarOrSubSelectExpression_in_inExpression4099 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_subquery_in_inExpression4134 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIKE_in_likeExpression4198 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarOrSubSelectExpression_in_likeExpression4204 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_escape_in_likeExpression4219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_in_escape4259 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_escape4265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_nullComparisonExpression4306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEMBER_in_collectionMemberExpression4388 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_OF_in_collectionMemberExpression4391 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_existsExpression4439 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4441 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subquery_in_existsExpression4447 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_comparisonExpression4489 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4516 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_in_comparisonExpression4543 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4570 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_in_comparisonExpression4597 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4624 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4741 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subquery_in_arithmeticExpression4747 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4781 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_PLUS_in_simpleArithmeticExpression4797 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4803 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_MINUS_in_simpleArithmeticExpression4832 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4838 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4895 = new BitSet(new long[]{0x0000000000000002L,0x0000000060000000L});
    public static final BitSet FOLLOW_MULTIPLY_in_arithmeticTerm4911 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4917 = new BitSet(new long[]{0x0000000000000002L,0x0000000060000000L});
    public static final BitSet FOLLOW_DIVIDE_in_arithmeticTerm4946 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4952 = new BitSet(new long[]{0x0000000000000002L,0x0000000060000000L});
    public static final BitSet FOLLOW_PLUS_in_arithmeticFactor5006 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_arithmeticFactor5035 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_arithmeticPrimary5099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_arithmeticPrimary5127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_caseExpression_in_arithmeticPrimary5141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5165 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5171 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_arithmeticPrimary5187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_scalarExpression5219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_scalarOrSubSelectExpression5270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_scalarOrSubSelectExpression5285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_nonArithmeticScalarExpression5345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalTemporal_in_nonArithmeticScalarExpression5373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_anyOrAllExpression5417 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5419 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5425 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_anyOrAllExpression5447 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5449 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5455 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SOME_in_anyOrAllExpression5477 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5479 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5485 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_entityTypeExpression5527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5560 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5562 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5568 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5585 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5587 = new BitSet(new long[]{0x0000000000000000L,0x0000030000000000L});
    public static final BitSet FOLLOW_inputParameter_in_typeDiscriminator5593 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleCaseExpression_in_caseExpression5630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_generalCaseExpression_in_caseExpression5643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_coalesceExpression_in_caseExpression5656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullIfExpression_in_caseExpression5669 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_simpleCaseExpression5707 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_caseOperand_in_simpleCaseExpression5713 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5719 = new BitSet(new long[]{0x0000000001000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5728 = new BitSet(new long[]{0x0000000001000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_ELSE_in_simpleCaseExpression5734 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_simpleCaseExpression5740 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_END_in_simpleCaseExpression5742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_generalCaseExpression5786 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5792 = new BitSet(new long[]{0x0000000001000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5801 = new BitSet(new long[]{0x0000000001000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_ELSE_in_generalCaseExpression5807 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_generalCaseExpression5813 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_END_in_generalCaseExpression5815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COALESCE_in_coalesceExpression5859 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5861 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5867 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_coalesceExpression5872 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5878 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULLIF_in_nullIfExpression5925 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5927 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5933 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_nullIfExpression5935 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5941 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression5943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_caseOperand5990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_caseOperand6004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_whenClause6039 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_conditionalExpression_in_whenClause6045 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_THEN_in_whenClause6047 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_whenClause6053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_simpleWhenClause6095 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause6101 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_THEN_in_simpleWhenClause6103 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause6109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_stringPrimary6192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_stringPrimary6206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_stringPrimary6220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_stringPrimary6234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_literal6268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_literal6282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_literal6296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_LITERAL_in_literalNumeric6326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LONG_LITERAL_in_literalNumeric6342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_LITERAL_in_literalNumeric6363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_LITERAL_in_literalNumeric6383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literalBoolean6421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literalBoolean6443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_LITERAL_in_literalTemporal6543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIME_LITERAL_in_literalTemporal6557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIMESTAMP_LITERAL_in_literalTemporal6571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POSITIONAL_PARAM_in_inputParameter6601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_PARAM_in_inputParameter6621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_abs_in_functionsReturningNumerics6661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_length_in_functionsReturningNumerics6675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mod_in_functionsReturningNumerics6689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqrt_in_functionsReturningNumerics6703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_locate_in_functionsReturningNumerics6717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_size_in_functionsReturningNumerics6731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_index_in_functionsReturningNumerics6745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_func_in_functionsReturningNumerics6759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_concat_in_functionsReturningStrings6870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_substring_in_functionsReturningStrings6884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trim_in_functionsReturningStrings6898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_upper_in_functionsReturningStrings6912 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lower_in_functionsReturningStrings6926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONCAT_in_concat6961 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_concat6972 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_concat6987 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_concat6992 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_concat6998 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_concat7012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSTRING_in_substring7050 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_substring7063 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_substring7077 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_substring7079 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_substring7093 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_COMMA_in_substring7104 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_substring7110 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_substring7122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIM_in_trim7160 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_trim7170 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_trimSpec_in_trim7186 = new BitSet(new long[]{0x0000000200000000L,0x0000031800000000L});
    public static final BitSet FOLLOW_trimChar_in_trim7192 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_FROM_in_trim7194 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_stringPrimary_in_trim7210 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_trim7220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEADING_in_trimSpec7256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRAILING_in_trimSpec7274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOTH_in_trimSpec7292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_trimChar7352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_trimChar7366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPPER_in_upper7409 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_upper7411 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_upper7417 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_upper7419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOWER_in_lower7457 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_lower7459 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_lower7465 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_lower7467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABS_in_abs7506 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_abs7508 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_abs7514 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_abs7516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LENGTH_in_length7554 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_length7556 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_length7562 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_length7564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCATE_in_locate7602 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_locate7612 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_locate7627 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_locate7629 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_locate7635 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_COMMA_in_locate7647 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_locate7653 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_locate7666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIZE_in_size7704 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_size7715 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_size7721 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_size7723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mod7761 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mod7763 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_mod7777 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_mod7779 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_mod7794 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mod7804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQRT_in_sqrt7842 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7853 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_sqrt7859 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDEX_in_index7903 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_index7905 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_index7911 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_index7913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNC_in_func7955 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_func7957 = new BitSet(new long[]{0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_func7969 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_COMMA_in_func7978 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_newValue_in_func7984 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_func8016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleSelectClause_in_subquery8054 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_subqueryFromClause_in_subquery8069 = new BitSet(new long[]{0x0000000C00000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_whereClause_in_subquery8084 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_groupByClause_in_subquery8099 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_havingClause_in_subquery8115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_simpleSelectClause8158 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_DISTINCT_in_simpleSelectClause8161 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_simpleSelectExpression_in_simpleSelectClause8177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_simpleSelectExpression8217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_simpleSelectExpression8232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression8247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_subqueryFromClause8282 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8284 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_COMMA_in_subqueryFromClause8311 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8330 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8356 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8416 = new BitSet(new long[]{0x0000000000000100L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_subselectIdentificationVariableDeclaration8419 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8425 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_join_in_subselectIdentificationVariableDeclaration8428 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_orderByClause8488 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_orderByClause8490 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8507 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_orderByClause8522 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8528 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_scalarExpression_in_orderByItem8574 = new BitSet(new long[]{0x0000000000200202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem8588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem8617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_groupByClause8698 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_groupByClause8700 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_groupByClause8714 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_groupByClause8727 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_scalarExpression_in_groupByClause8733 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_HAVING_in_havingClause8778 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_conditionalExpression_in_havingClause8795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_synpred1_JPQL3610 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_conditionalExpression_in_synpred1_JPQL3612 = new BitSet(new long[]{0x0000000000000002L});

}
