// $ANTLR 3.0 JPQL.g 2010-02-25 13:18:48

    package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

    import java.util.List;
    import java.util.ArrayList;

    import static org.eclipse.persistence.internal.jpa.parsing.NodeFactory.*;
    import org.eclipse.persistence.internal.jpa.parsing.jpql.InvalidIdentifierException;
    import org.eclipse.persistence.exceptions.JPQLException;


import org.eclipse.persistence.internal.libraries.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
public class JPQLParser extends org.eclipse.persistence.internal.jpa.parsing.jpql.JPQLParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ABS", "ALL", "AND", "ANY", "AS", "ASC", "AVG", "BETWEEN", "BOTH", "BY", "CASE", "COALESCE", "CONCAT", "COUNT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DESC", "DELETE", "DISTINCT", "ELSE", "EMPTY", "END", "ENTRY", "ESCAPE", "EXISTS", "FALSE", "FETCH", "FROM", "GROUP", "HAVING", "IN", "INDEX", "INNER", "IS", "JOIN", "KEY", "LEADING", "LEFT", "LENGTH", "LIKE", "LOCATE", "LOWER", "MAX", "MEMBER", "MIN", "MOD", "NEW", "NOT", "NULL", "NULLIF", "OBJECT", "OF", "OR", "ORDER", "OUTER", "SELECT", "SET", "SIZE", "SQRT", "SOME", "SUBSTRING", "SUM", "THEN", "TRAILING", "TRIM", "TRUE", "TYPE", "UNKNOWN", "UPDATE", "UPPER", "VALUE", "WHEN", "WHERE", "IDENT", "COMMA", "EQUALS", "LEFT_ROUND_BRACKET", "RIGHT_ROUND_BRACKET", "DOT", "NOT_EQUAL_TO", "GREATER_THAN", "GREATER_THAN_EQUAL_TO", "LESS_THAN", "LESS_THAN_EQUAL_TO", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "INTEGER_LITERAL", "LONG_LITERAL", "FLOAT_LITERAL", "DOUBLE_LITERAL", "STRING_LITERAL_DOUBLE_QUOTED", "STRING_LITERAL_SINGLE_QUOTED", "DATE_LITERAL", "TIME_LITERAL", "TIMESTAMP_LITERAL", "POSITIONAL_PARAM", "NAMED_PARAM", "WS", "LEFT_CURLY_BRACKET", "RIGHT_CURLY_BRACKET", "TEXTCHAR", "HEX_DIGIT", "HEX_LITERAL", "INTEGER_SUFFIX", "OCTAL_LITERAL", "NUMERIC_DIGITS", "DOUBLE_SUFFIX", "EXPONENT", "FLOAT_SUFFIX", "DATE_STRING", "TIME_STRING"
    };
    public static final int EXPONENT=114;
    public static final int DATE_STRING=116;
    public static final int FLOAT_SUFFIX=115;
    public static final int MOD=50;
    public static final int CURRENT_TIME=19;
    public static final int CASE=14;
    public static final int NEW=51;
    public static final int LEFT_ROUND_BRACKET=81;
    public static final int DOUBLE_LITERAL=96;
    public static final int TIME_LITERAL=100;
    public static final int COUNT=17;
    public static final int EQUALS=80;
    public static final int NOT=52;
    public static final int EOF=-1;
    public static final int TIME_STRING=117;
    public static final int TYPE=71;
    public static final int LEFT_CURLY_BRACKET=105;
    public static final int GREATER_THAN_EQUAL_TO=86;
    public static final int ESCAPE=28;
    public static final int NAMED_PARAM=103;
    public static final int BOTH=12;
    public static final int TIMESTAMP_LITERAL=101;
    public static final int NUMERIC_DIGITS=112;
    public static final int SELECT=60;
    public static final int DIVIDE=92;
    public static final int COALESCE=15;
    public static final int ASC=9;
    public static final int CONCAT=16;
    public static final int KEY=40;
    public static final int NULL=53;
    public static final int ELSE=24;
    public static final int TRAILING=68;
    public static final int DELETE=22;
    public static final int VALUE=75;
    public static final int DATE_LITERAL=99;
    public static final int OF=56;
    public static final int RIGHT_CURLY_BRACKET=106;
    public static final int LEADING=41;
    public static final int INTEGER_SUFFIX=110;
    public static final int EMPTY=25;
    public static final int ABS=4;
    public static final int GROUP=33;
    public static final int NOT_EQUAL_TO=84;
    public static final int WS=104;
    public static final int FETCH=31;
    public static final int STRING_LITERAL_SINGLE_QUOTED=98;
    public static final int INTEGER_LITERAL=93;
    public static final int OR=57;
    public static final int TRIM=69;
    public static final int LESS_THAN=87;
    public static final int RIGHT_ROUND_BRACKET=82;
    public static final int POSITIONAL_PARAM=102;
    public static final int LOWER=46;
    public static final int FROM=32;
    public static final int END=26;
    public static final int FALSE=30;
    public static final int LESS_THAN_EQUAL_TO=88;
    public static final int DISTINCT=23;
    public static final int CURRENT_DATE=18;
    public static final int SIZE=62;
    public static final int UPPER=74;
    public static final int WHERE=77;
    public static final int NULLIF=54;
    public static final int MEMBER=48;
    public static final int INNER=37;
    public static final int ORDER=58;
    public static final int TEXTCHAR=107;
    public static final int MAX=47;
    public static final int UPDATE=73;
    public static final int AND=6;
    public static final int SUM=66;
    public static final int STRING_LITERAL_DOUBLE_QUOTED=97;
    public static final int LENGTH=43;
    public static final int INDEX=36;
    public static final int AS=8;
    public static final int IN=35;
    public static final int THEN=67;
    public static final int UNKNOWN=72;
    public static final int OBJECT=55;
    public static final int MULTIPLY=91;
    public static final int COMMA=79;
    public static final int IS=38;
    public static final int LEFT=42;
    public static final int AVG=10;
    public static final int SOME=64;
    public static final int ALL=5;
    public static final int IDENT=78;
    public static final int PLUS=89;
    public static final int HEX_LITERAL=109;
    public static final int EXISTS=29;
    public static final int DOT=83;
    public static final int CURRENT_TIMESTAMP=20;
    public static final int LIKE=44;
    public static final int OUTER=59;
    public static final int BY=13;
    public static final int GREATER_THAN=85;
    public static final int OCTAL_LITERAL=111;
    public static final int HEX_DIGIT=108;
    public static final int SET=61;
    public static final int HAVING=34;
    public static final int ENTRY=27;
    public static final int MIN=49;
    public static final int SQRT=63;
    public static final int MINUS=90;
    public static final int LONG_LITERAL=94;
    public static final int TRUE=70;
    public static final int JOIN=39;
    public static final int SUBSTRING=65;
    public static final int DOUBLE_SUFFIX=113;
    public static final int FLOAT_LITERAL=95;
    public static final int ANY=7;
    public static final int LOCATE=45;
    public static final int WHEN=76;
    public static final int DESC=21;
    public static final int BETWEEN=11;
    
        public JPQLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[111+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
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


    
    // $ANTLR start document
    // JPQL.g:197:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );
    public final void document() throws RecognitionException {
        Object root = null;
        
    
        try {
            // JPQL.g:198:7: (root= selectStatement | root= updateStatement | root= deleteStatement )
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
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("197:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );", 1, 0, input);
            
                throw nvae;
            }
            
            switch (alt1) {
                case 1 :
                    // JPQL.g:198:7: root= selectStatement
                    {
                    pushFollow(FOLLOW_selectStatement_in_document745);
                    root=selectStatement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      queryRoot = root;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:199:7: root= updateStatement
                    {
                    pushFollow(FOLLOW_updateStatement_in_document759);
                    root=updateStatement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      queryRoot = root;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:200:7: root= deleteStatement
                    {
                    pushFollow(FOLLOW_deleteStatement_in_document773);
                    root=deleteStatement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
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
    // $ANTLR end document

    
    // $ANTLR start selectStatement
    // JPQL.g:203:1: selectStatement returns [Object node] : select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF ;
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
            // JPQL.g:207:7: (select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF )
            // JPQL.g:207:7: select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF
            {
            pushFollow(FOLLOW_selectClause_in_selectStatement806);
            select=selectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_fromClause_in_selectStatement821);
            from=fromClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:209:7: (where= whereClause )?
            int alt2=2;
            int LA2_0 = input.LA(1);
            
            if ( (LA2_0==WHERE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // JPQL.g:209:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_selectStatement836);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:210:7: (groupBy= groupByClause )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            
            if ( (LA3_0==GROUP) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // JPQL.g:210:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_selectStatement851);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:211:7: (having= havingClause )?
            int alt4=2;
            int LA4_0 = input.LA(1);
            
            if ( (LA4_0==HAVING) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // JPQL.g:211:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_selectStatement867);
                    having=havingClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:212:7: (orderBy= orderByClause )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            
            if ( (LA5_0==ORDER) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // JPQL.g:212:8: orderBy= orderByClause
                    {
                    pushFollow(FOLLOW_orderByClause_in_selectStatement882);
                    orderBy=orderByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_selectStatement892); if (failed) return node;
            if ( backtracking==0 ) {
               
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
    // $ANTLR end selectStatement

    
    // $ANTLR start updateStatement
    // JPQL.g:222:1: updateStatement returns [Object node] : update= updateClause set= setClause (where= whereClause )? EOF ;
    public final Object updateStatement() throws RecognitionException {

        Object node = null;
    
        Object update = null;

        Object set = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:226:7: (update= updateClause set= setClause (where= whereClause )? EOF )
            // JPQL.g:226:7: update= updateClause set= setClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_updateClause_in_updateStatement935);
            update=updateClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_setClause_in_updateStatement950);
            set=setClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:228:7: (where= whereClause )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            
            if ( (LA6_0==WHERE) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // JPQL.g:228:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_updateStatement964);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_updateStatement974); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end updateStatement

    
    // $ANTLR start updateClause
    // JPQL.g:232:1: updateClause returns [Object node] : u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object updateClause() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Token ident=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:236:7: (u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g:236:7: u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            u=(Token)input.LT(1);
            match(input,UPDATE,FOLLOW_UPDATE_in_updateClause1006); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_updateClause1012);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g:237:9: ( ( AS )? ident= IDENT )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            
            if ( (LA8_0==AS||LA8_0==IDENT) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // JPQL.g:237:10: ( AS )? ident= IDENT
                    {
                    // JPQL.g:237:10: ( AS )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);
                    
                    if ( (LA7_0==AS) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // JPQL.g:237:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_updateClause1025); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_updateClause1033); if (failed) return node;
                    
                    }
                    break;
            
            }

            if ( backtracking==0 ) {
               
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
    // $ANTLR end updateClause

    protected static class setClause_scope {
        List assignments;
    }
    protected Stack setClause_stack = new Stack();
    
    
    // $ANTLR start setClause
    // JPQL.g:248:1: setClause returns [Object node] : t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* ;
    public final Object setClause() throws RecognitionException {
        setClause_stack.push(new setClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((setClause_scope)setClause_stack.peek()).assignments = new ArrayList();
    
        try {
            // JPQL.g:256:7: (t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* )
            // JPQL.g:256:7: t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )*
            {
            t=(Token)input.LT(1);
            match(input,SET,FOLLOW_SET_in_setClause1082); if (failed) return node;
            pushFollow(FOLLOW_setAssignmentClause_in_setClause1088);
            n=setAssignmentClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((setClause_scope)setClause_stack.peek()).assignments.add(n); 
            }
            // JPQL.g:257:9: ( COMMA n= setAssignmentClause )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);
                
                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }
                
            
                switch (alt9) {
            	case 1 :
            	    // JPQL.g:257:10: COMMA n= setAssignmentClause
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_setClause1101); if (failed) return node;
            	    pushFollow(FOLLOW_setAssignmentClause_in_setClause1107);
            	    n=setAssignmentClause();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((setClause_scope)setClause_stack.peek()).assignments.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop9;
                }
            } while (true);

            if ( backtracking==0 ) {
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
    // $ANTLR end setClause

    
    // $ANTLR start setAssignmentClause
    // JPQL.g:261:1: setAssignmentClause returns [Object node] : target= setAssignmentTarget t= EQUALS value= newValue ;
    public final Object setAssignmentClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object target = null;

        Object value = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:269:7: (target= setAssignmentTarget t= EQUALS value= newValue )
            // JPQL.g:269:7: target= setAssignmentTarget t= EQUALS value= newValue
            {
            pushFollow(FOLLOW_setAssignmentTarget_in_setAssignmentClause1165);
            target=setAssignmentTarget();
            _fsp--;
            if (failed) return node;
            t=(Token)input.LT(1);
            match(input,EQUALS,FOLLOW_EQUALS_in_setAssignmentClause1169); if (failed) return node;
            pushFollow(FOLLOW_newValue_in_setAssignmentClause1175);
            value=newValue();
            _fsp--;
            if (failed) return node;
            
            }
    
            if ( backtracking==0 ) {
               
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
    // $ANTLR end setAssignmentClause

    
    // $ANTLR start setAssignmentTarget
    // JPQL.g:272:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );
    public final Object setAssignmentTarget() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:276:7: (n= attribute | n= pathExpression )
            int alt10=2;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA10_1 = input.LA(2);
                
                if ( (LA10_1==DOT) ) {
                    alt10=2;
                }
                else if ( (LA10_1==EQUALS) ) {
                    alt10=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("272:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 1, input);
                
                    throw nvae;
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
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("272:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 2, input);
                
                    throw nvae;
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
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("272:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 3, input);
                
                    throw nvae;
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
            case CONCAT:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DESC:
            case DELETE:
            case DISTINCT:
            case ELSE:
            case EMPTY:
            case END:
            case ENTRY:
            case ESCAPE:
            case EXISTS:
            case FALSE:
            case FETCH:
            case FROM:
            case GROUP:
            case HAVING:
            case IN:
            case INDEX:
            case INNER:
            case IS:
            case JOIN:
            case LEADING:
            case LEFT:
            case LENGTH:
            case LIKE:
            case LOCATE:
            case LOWER:
            case MAX:
            case MEMBER:
            case MIN:
            case MOD:
            case NEW:
            case NOT:
            case NULL:
            case NULLIF:
            case OBJECT:
            case OF:
            case OR:
            case ORDER:
            case OUTER:
            case SELECT:
            case SET:
            case SIZE:
            case SQRT:
            case SOME:
            case SUBSTRING:
            case SUM:
            case THEN:
            case TRAILING:
            case TRIM:
            case TRUE:
            case TYPE:
            case UNKNOWN:
            case UPDATE:
            case UPPER:
            case WHEN:
            case WHERE:
            case COMMA:
            case EQUALS:
            case LEFT_ROUND_BRACKET:
            case RIGHT_ROUND_BRACKET:
            case DOT:
            case NOT_EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL_TO:
            case LESS_THAN:
            case LESS_THAN_EQUAL_TO:
            case PLUS:
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case DATE_LITERAL:
            case TIME_LITERAL:
            case TIMESTAMP_LITERAL:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
            case WS:
            case LEFT_CURLY_BRACKET:
            case RIGHT_CURLY_BRACKET:
            case TEXTCHAR:
            case HEX_DIGIT:
            case HEX_LITERAL:
            case INTEGER_SUFFIX:
            case OCTAL_LITERAL:
            case NUMERIC_DIGITS:
            case DOUBLE_SUFFIX:
            case EXPONENT:
            case FLOAT_SUFFIX:
            case DATE_STRING:
            case TIME_STRING:
                {
                alt10=1;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("272:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 0, input);
            
                throw nvae;
            }
            
            switch (alt10) {
                case 1 :
                    // JPQL.g:276:7: n= attribute
                    {
                    pushFollow(FOLLOW_attribute_in_setAssignmentTarget1205);
                    n=attribute();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:277:7: n= pathExpression
                    {
                    pushFollow(FOLLOW_pathExpression_in_setAssignmentTarget1220);
                    n=pathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end setAssignmentTarget

    
    // $ANTLR start newValue
    // JPQL.g:280:1: newValue returns [Object node] : (n= scalarExpression | n1= NULL );
    public final Object newValue() throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:282:7: (n= scalarExpression | n1= NULL )
            int alt11=2;
            int LA11_0 = input.LA(1);
            
            if ( (LA11_0==ABS||LA11_0==AVG||(LA11_0>=CASE && LA11_0<=CURRENT_TIMESTAMP)||LA11_0==FALSE||LA11_0==INDEX||LA11_0==KEY||LA11_0==LENGTH||(LA11_0>=LOCATE && LA11_0<=MAX)||(LA11_0>=MIN && LA11_0<=MOD)||LA11_0==NULLIF||(LA11_0>=SIZE && LA11_0<=SQRT)||(LA11_0>=SUBSTRING && LA11_0<=SUM)||(LA11_0>=TRIM && LA11_0<=TYPE)||(LA11_0>=UPPER && LA11_0<=VALUE)||LA11_0==IDENT||LA11_0==LEFT_ROUND_BRACKET||(LA11_0>=PLUS && LA11_0<=MINUS)||(LA11_0>=INTEGER_LITERAL && LA11_0<=NAMED_PARAM)) ) {
                alt11=1;
            }
            else if ( (LA11_0==NULL) ) {
                alt11=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("280:1: newValue returns [Object node] : (n= scalarExpression | n1= NULL );", 11, 0, input);
            
                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // JPQL.g:282:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_newValue1252);
                    n=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:283:7: n1= NULL
                    {
                    n1=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_newValue1266); if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end newValue

    
    // $ANTLR start deleteStatement
    // JPQL.g:289:1: deleteStatement returns [Object node] : delete= deleteClause (where= whereClause )? EOF ;
    public final Object deleteStatement() throws RecognitionException {

        Object node = null;
    
        Object delete = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:293:7: (delete= deleteClause (where= whereClause )? EOF )
            // JPQL.g:293:7: delete= deleteClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_deleteClause_in_deleteStatement1310);
            delete=deleteClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:294:7: (where= whereClause )?
            int alt12=2;
            int LA12_0 = input.LA(1);
            
            if ( (LA12_0==WHERE) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // JPQL.g:294:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_deleteStatement1323);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_deleteStatement1333); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end deleteStatement

    protected static class deleteClause_scope {
        String variable;
    }
    protected Stack deleteClause_stack = new Stack();
    
    
    // $ANTLR start deleteClause
    // JPQL.g:298:1: deleteClause returns [Object node] : t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object deleteClause() throws RecognitionException {
        deleteClause_stack.push(new deleteClause_scope());

        Object node = null;
    
        Token t=null;
        Token ident=null;
        String schema = null;
        
    
         
            node = null; 
            ((deleteClause_scope)deleteClause_stack.peek()).variable = null;
    
        try {
            // JPQL.g:306:7: (t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g:306:7: t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            t=(Token)input.LT(1);
            match(input,DELETE,FOLLOW_DELETE_in_deleteClause1366); if (failed) return node;
            match(input,FROM,FOLLOW_FROM_in_deleteClause1368); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_deleteClause1374);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g:307:9: ( ( AS )? ident= IDENT )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            
            if ( (LA14_0==AS||LA14_0==IDENT) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // JPQL.g:307:10: ( AS )? ident= IDENT
                    {
                    // JPQL.g:307:10: ( AS )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    
                    if ( (LA13_0==AS) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // JPQL.g:307:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_deleteClause1387); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_deleteClause1393); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((deleteClause_scope)deleteClause_stack.peek()).variable = ident.getText(); 
                    }
                    
                    }
                    break;
            
            }

            if ( backtracking==0 ) {
               
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
    // $ANTLR end deleteClause

    protected static class selectClause_scope {
        boolean distinct;
        List exprs;
        List idents;
    }
    protected Stack selectClause_stack = new Stack();
    
    
    // $ANTLR start selectClause
    // JPQL.g:316:1: selectClause returns [Object node] : t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )* ;
    public final Object selectClause() throws RecognitionException {
        selectClause_stack.push(new selectClause_scope());

        Object node = null;
    
        Token t=null;
        selectItem_return n = null;
        
    
         
            node = null;
            ((selectClause_scope)selectClause_stack.peek()).distinct = false;
            ((selectClause_scope)selectClause_stack.peek()).exprs = new ArrayList();
            ((selectClause_scope)selectClause_stack.peek()).idents = new ArrayList();
    
        try {
            // JPQL.g:328:7: (t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )* )
            // JPQL.g:328:7: t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )*
            {
            t=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_selectClause1440); if (failed) return node;
            // JPQL.g:328:16: ( DISTINCT )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            
            if ( (LA15_0==DISTINCT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // JPQL.g:328:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause1443); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((selectClause_scope)selectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_selectItem_in_selectClause1459);
            n=selectItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              
                            ((selectClause_scope)selectClause_stack.peek()).exprs.add(n.expr);
                            ((selectClause_scope)selectClause_stack.peek()).idents.add(n.ident);
                        
            }
            // JPQL.g:334:11: ( COMMA n= selectItem )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);
                
                if ( (LA16_0==COMMA) ) {
                    alt16=1;
                }
                
            
                switch (alt16) {
            	case 1 :
            	    // JPQL.g:334:13: COMMA n= selectItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_selectClause1487); if (failed) return node;
            	    pushFollow(FOLLOW_selectItem_in_selectClause1493);
            	    n=selectItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      
            	                        ((selectClause_scope)selectClause_stack.peek()).exprs.add(n.expr);
            	                        ((selectClause_scope)selectClause_stack.peek()).idents.add(n.ident);
            	                     
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop16;
                }
            } while (true);

            if ( backtracking==0 ) {
               
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
    // $ANTLR end selectClause

    public static class selectItem_return extends ParserRuleReturnScope {
        public Object expr;
        public Object ident;
    };
    
    // $ANTLR start selectItem
    // JPQL.g:347:1: selectItem returns [Object expr, Object ident] : e= selectExpression ( ( AS )? identifier= IDENT )? ;
    public final selectItem_return selectItem() throws RecognitionException {
        selectItem_return retval = new selectItem_return();
        retval.start = input.LT(1);
    
        Token identifier=null;
        Object e = null;
        
    
        try {
            // JPQL.g:348:7: (e= selectExpression ( ( AS )? identifier= IDENT )? )
            // JPQL.g:348:7: e= selectExpression ( ( AS )? identifier= IDENT )?
            {
            pushFollow(FOLLOW_selectExpression_in_selectItem1589);
            e=selectExpression();
            _fsp--;
            if (failed) return retval;
            // JPQL.g:348:28: ( ( AS )? identifier= IDENT )?
            int alt18=2;
            int LA18_0 = input.LA(1);
            
            if ( (LA18_0==AS||LA18_0==IDENT) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // JPQL.g:348:29: ( AS )? identifier= IDENT
                    {
                    // JPQL.g:348:29: ( AS )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);
                    
                    if ( (LA17_0==AS) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // JPQL.g:348:30: AS
                            {
                            match(input,AS,FOLLOW_AS_in_selectItem1593); if (failed) return retval;
                            
                            }
                            break;
                    
                    }

                    identifier=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_selectItem1601); if (failed) return retval;
                    
                    }
                    break;
            
            }

            if ( backtracking==0 ) {
              
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
    // $ANTLR end selectItem

    
    // $ANTLR start selectExpression
    // JPQL.g:361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );
    public final Object selectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:363:7: (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression )
            int alt19=5;
            switch ( input.LA(1) ) {
            case AVG:
                {
                int LA19_1 = input.LA(2);
                
                if ( (LA19_1==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
                        {
                        int LA19_52 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 52, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA19_53 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 53, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA19_54 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 54, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA19_55 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 55, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 47, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case MAX:
                {
                int LA19_2 = input.LA(2);
                
                if ( (LA19_2==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
                        {
                        int LA19_56 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 56, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA19_57 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 57, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA19_58 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 58, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA19_59 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 59, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 48, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 2, input);
                
                    throw nvae;
                }
                }
                break;
            case MIN:
                {
                int LA19_3 = input.LA(2);
                
                if ( (LA19_3==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
                        {
                        int LA19_60 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 60, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA19_61 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 61, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA19_62 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 62, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA19_63 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 63, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 49, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 3, input);
                
                    throw nvae;
                }
                }
                break;
            case SUM:
                {
                int LA19_4 = input.LA(2);
                
                if ( (LA19_4==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
                        {
                        int LA19_64 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 64, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA19_65 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 65, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA19_66 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 66, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA19_67 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 67, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 50, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 4, input);
                
                    throw nvae;
                }
                }
                break;
            case COUNT:
                {
                int LA19_5 = input.LA(2);
                
                if ( (LA19_5==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
                        {
                        int LA19_68 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 68, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA19_69 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 69, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA19_70 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 70, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA19_71 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 71, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 51, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 5, input);
                
                    throw nvae;
                }
                }
                break;
            case ABS:
            case CASE:
            case COALESCE:
            case CONCAT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case FALSE:
            case INDEX:
            case KEY:
            case LENGTH:
            case LOCATE:
            case LOWER:
            case MOD:
            case NULLIF:
            case SIZE:
            case SQRT:
            case SUBSTRING:
            case TRIM:
            case TRUE:
            case TYPE:
            case UPPER:
            case VALUE:
            case IDENT:
            case LEFT_ROUND_BRACKET:
            case PLUS:
            case MINUS:
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case DATE_LITERAL:
            case TIME_LITERAL:
            case TIMESTAMP_LITERAL:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt19=2;
                }
                break;
            case OBJECT:
                {
                alt19=3;
                }
                break;
            case NEW:
                {
                alt19=4;
                }
                break;
            case ENTRY:
                {
                alt19=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("361:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 0, input);
            
                throw nvae;
            }
            
            switch (alt19) {
                case 1 :
                    // JPQL.g:363:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_selectExpression1645);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:364:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_selectExpression1659);
                    n=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:365:7: OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    match(input,OBJECT,FOLLOW_OBJECT_in_selectExpression1669); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1671); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_selectExpression1677);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1679); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:366:7: n= constructorExpression
                    {
                    pushFollow(FOLLOW_constructorExpression_in_selectExpression1694);
                    n=constructorExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:367:7: n= mapEntryExpression
                    {
                    pushFollow(FOLLOW_mapEntryExpression_in_selectExpression1709);
                    n=mapEntryExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end selectExpression

    
    // $ANTLR start mapEntryExpression
    // JPQL.g:370:1: mapEntryExpression returns [Object node] : l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object mapEntryExpression() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:372:7: (l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // JPQL.g:372:7: l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,ENTRY,FOLLOW_ENTRY_in_mapEntryExpression1741); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1743); if (failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1749);
            n=variableAccessOrTypeConstant();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1751); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end mapEntryExpression

    
    // $ANTLR start pathExprOrVariableAccess
    // JPQL.g:375:1: pathExprOrVariableAccess returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )* ;
    public final Object pathExprOrVariableAccess() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:379:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )* )
            // JPQL.g:379:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )*
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1783);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:380:9: (d= DOT right= attribute )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);
                
                if ( (LA20_0==DOT) ) {
                    alt20=1;
                }
                
            
                switch (alt20) {
            	case 1 :
            	    // JPQL.g:380:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExprOrVariableAccess1798); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExprOrVariableAccess1804);
            	    right=attribute();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
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
    // $ANTLR end pathExprOrVariableAccess

    
    // $ANTLR start qualifiedIdentificationVariable
    // JPQL.g:385:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );
    public final Object qualifiedIdentificationVariable() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:387:7: (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("385:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );", 21, 0, input);
            
                throw nvae;
            }
            
            switch (alt21) {
                case 1 :
                    // JPQL.g:387:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1860);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:388:7: l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,KEY,FOLLOW_KEY_in_qualifiedIdentificationVariable1874); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1876); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1882);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1884); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newKey(l.getLine(), l.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:389:7: l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,VALUE,FOLLOW_VALUE_in_qualifiedIdentificationVariable1899); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1901); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1907);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1909); if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end qualifiedIdentificationVariable

    protected static class aggregateExpression_scope {
        boolean distinct;
    }
    protected Stack aggregateExpression_stack = new Stack();
    
    
    // $ANTLR start aggregateExpression
    // JPQL.g:392:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );
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
            // JPQL.g:400:7: (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("392:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );", 27, 0, input);
            
                throw nvae;
            }
            
            switch (alt27) {
                case 1 :
                    // JPQL.g:400:7: t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t1=(Token)input.LT(1);
                    match(input,AVG,FOLLOW_AVG_in_aggregateExpression1942); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1944); if (failed) return node;
                    // JPQL.g:400:33: ( DISTINCT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);
                    
                    if ( (LA22_0==DISTINCT) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // JPQL.g:400:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1947); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1965);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1967); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAvg(t1.getLine(), t1.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:403:7: t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t2=(Token)input.LT(1);
                    match(input,MAX,FOLLOW_MAX_in_aggregateExpression1988); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1990); if (failed) return node;
                    // JPQL.g:403:33: ( DISTINCT )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    
                    if ( (LA23_0==DISTINCT) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // JPQL.g:403:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1993); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2012);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2014); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMax(t2.getLine(), t2.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:406:7: t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t3=(Token)input.LT(1);
                    match(input,MIN,FOLLOW_MIN_in_aggregateExpression2034); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2036); if (failed) return node;
                    // JPQL.g:406:33: ( DISTINCT )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);
                    
                    if ( (LA24_0==DISTINCT) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // JPQL.g:406:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2039); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2057);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2059); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMin(t3.getLine(), t3.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:409:7: t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t4=(Token)input.LT(1);
                    match(input,SUM,FOLLOW_SUM_in_aggregateExpression2079); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2081); if (failed) return node;
                    // JPQL.g:409:33: ( DISTINCT )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);
                    
                    if ( (LA25_0==DISTINCT) ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // JPQL.g:409:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2084); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2102);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2104); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newSum(t4.getLine(), t4.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:412:7: t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET
                    {
                    t5=(Token)input.LT(1);
                    match(input,COUNT,FOLLOW_COUNT_in_aggregateExpression2124); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2126); if (failed) return node;
                    // JPQL.g:412:35: ( DISTINCT )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);
                    
                    if ( (LA26_0==DISTINCT) ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // JPQL.g:412:36: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2129); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_aggregateExpression2147);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2149); if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end aggregateExpression

    protected static class constructorExpression_scope {
        List args;
    }
    protected Stack constructorExpression_stack = new Stack();
    
    
    // $ANTLR start constructorExpression
    // JPQL.g:417:1: constructorExpression returns [Object node] : t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET ;
    public final Object constructorExpression() throws RecognitionException {
        constructorExpression_stack.push(new constructorExpression_scope());

        Object node = null;
    
        Token t=null;
        String className = null;

        Object n = null;
        
    
         
            node = null;
            ((constructorExpression_scope)constructorExpression_stack.peek()).args = new ArrayList();
    
        try {
            // JPQL.g:425:7: (t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET )
            // JPQL.g:425:7: t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,NEW,FOLLOW_NEW_in_constructorExpression2192); if (failed) return node;
            pushFollow(FOLLOW_constructorName_in_constructorExpression2198);
            className=constructorName();
            _fsp--;
            if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2208); if (failed) return node;
            pushFollow(FOLLOW_constructorItem_in_constructorExpression2223);
            n=constructorItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            }
            // JPQL.g:428:9: ( COMMA n= constructorItem )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);
                
                if ( (LA28_0==COMMA) ) {
                    alt28=1;
                }
                
            
                switch (alt28) {
            	case 1 :
            	    // JPQL.g:428:11: COMMA n= constructorItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constructorExpression2238); if (failed) return node;
            	    pushFollow(FOLLOW_constructorItem_in_constructorExpression2244);
            	    n=constructorItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop28;
                }
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2259); if (failed) return node;
            if ( backtracking==0 ) {
               
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
    // $ANTLR end constructorExpression

    protected static class constructorName_scope {
        StringBuffer buf;
    }
    protected Stack constructorName_stack = new Stack();
    
    
    // $ANTLR start constructorName
    // JPQL.g:436:1: constructorName returns [String className] : i1= IDENT ( DOT i2= IDENT )* ;
    public final String constructorName() throws RecognitionException {
        constructorName_stack.push(new constructorName_scope());

        String className = null;
    
        Token i1=null;
        Token i2=null;
    
         
            className = null;
            ((constructorName_scope)constructorName_stack.peek()).buf = new StringBuffer(); 
    
        try {
            // JPQL.g:444:7: (i1= IDENT ( DOT i2= IDENT )* )
            // JPQL.g:444:7: i1= IDENT ( DOT i2= IDENT )*
            {
            i1=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_constructorName2300); if (failed) return className;
            if ( backtracking==0 ) {
               ((constructorName_scope)constructorName_stack.peek()).buf.append(i1.getText()); 
            }
            // JPQL.g:445:9: ( DOT i2= IDENT )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);
                
                if ( (LA29_0==DOT) ) {
                    alt29=1;
                }
                
            
                switch (alt29) {
            	case 1 :
            	    // JPQL.g:445:11: DOT i2= IDENT
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_constructorName2314); if (failed) return className;
            	    i2=(Token)input.LT(1);
            	    match(input,IDENT,FOLLOW_IDENT_in_constructorName2318); if (failed) return className;
            	    if ( backtracking==0 ) {
            	       ((constructorName_scope)constructorName_stack.peek()).buf.append('.').append(i2.getText()); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop29;
                }
            } while (true);

            if ( backtracking==0 ) {
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
    // $ANTLR end constructorName

    
    // $ANTLR start constructorItem
    // JPQL.g:449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );
    public final Object constructorItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:451:7: (n= scalarExpression | n= aggregateExpression )
            int alt30=2;
            switch ( input.LA(1) ) {
            case ABS:
            case CASE:
            case COALESCE:
            case CONCAT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case FALSE:
            case INDEX:
            case KEY:
            case LENGTH:
            case LOCATE:
            case LOWER:
            case MOD:
            case NULLIF:
            case SIZE:
            case SQRT:
            case SUBSTRING:
            case TRIM:
            case TRUE:
            case TYPE:
            case UPPER:
            case VALUE:
            case IDENT:
            case LEFT_ROUND_BRACKET:
            case PLUS:
            case MINUS:
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case DATE_LITERAL:
            case TIME_LITERAL:
            case TIMESTAMP_LITERAL:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt30=1;
                }
                break;
            case AVG:
                {
                int LA30_3 = input.LA(2);
                
                if ( (LA30_3==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
                        {
                        int LA30_49 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 49, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA30_50 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 50, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA30_51 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 51, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA30_52 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 52, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 44, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 3, input);
                
                    throw nvae;
                }
                }
                break;
            case MAX:
                {
                int LA30_4 = input.LA(2);
                
                if ( (LA30_4==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
                        {
                        int LA30_53 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 53, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA30_54 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 54, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA30_55 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 55, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA30_56 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 56, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 45, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 4, input);
                
                    throw nvae;
                }
                }
                break;
            case MIN:
                {
                int LA30_5 = input.LA(2);
                
                if ( (LA30_5==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
                        {
                        int LA30_57 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 57, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA30_58 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 58, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA30_59 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 59, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA30_60 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 60, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 46, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 5, input);
                
                    throw nvae;
                }
                }
                break;
            case SUM:
                {
                int LA30_6 = input.LA(2);
                
                if ( (LA30_6==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
                        {
                        int LA30_61 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 61, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA30_62 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 62, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA30_63 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 63, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA30_64 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 64, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 47, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 6, input);
                
                    throw nvae;
                }
                }
                break;
            case COUNT:
                {
                int LA30_7 = input.LA(2);
                
                if ( (LA30_7==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
                        {
                        int LA30_65 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 65, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA30_66 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 66, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA30_67 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 67, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA30_68 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 68, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 48, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 7, input);
                
                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("449:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 0, input);
            
                throw nvae;
            }
            
            switch (alt30) {
                case 1 :
                    // JPQL.g:451:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_constructorItem2362);
                    n=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:452:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_constructorItem2376);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end constructorItem

    protected static class fromClause_scope {
        List varDecls;
    }
    protected Stack fromClause_stack = new Stack();
    
    
    // $ANTLR start fromClause
    // JPQL.g:456:1: fromClause returns [Object node] : t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* ;
    public final Object fromClause() throws RecognitionException {
        fromClause_stack.push(new fromClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((fromClause_scope)fromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g:464:7: (t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* )
            // JPQL.g:464:7: t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            {
            t=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_fromClause2410); if (failed) return node;
            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2412);
            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g:465:9: ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);
                
                if ( (LA32_0==COMMA) ) {
                    alt32=1;
                }
                
            
                switch (alt32) {
            	case 1 :
            	    // JPQL.g:465:10: COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_fromClause2424); if (failed) return node;
            	    // JPQL.g:465:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
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
            	            if (backtracking>0) {failed=true; return node;}
            	            NoViableAltException nvae =
            	                new NoViableAltException("465:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 31, 1, input);
            	        
            	            throw nvae;
            	        }
            	    }
            	    else if ( ((LA31_0>=ABS && LA31_0<=HAVING)||(LA31_0>=INDEX && LA31_0<=TIME_STRING)) ) {
            	        alt31=1;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return node;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("465:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 31, 0, input);
            	    
            	        throw nvae;
            	    }
            	    switch (alt31) {
            	        case 1 :
            	            // JPQL.g:465:19: identificationVariableDeclaration[$fromClause::varDecls]
            	            {
            	            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2429);
            	            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            	            _fsp--;
            	            if (failed) return node;
            	            
            	            }
            	            break;
            	        case 2 :
            	            // JPQL.g:466:19: n= collectionMemberDeclaration
            	            {
            	            pushFollow(FOLLOW_collectionMemberDeclaration_in_fromClause2454);
            	            n=collectionMemberDeclaration();
            	            _fsp--;
            	            if (failed) return node;
            	            if ( backtracking==0 ) {
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

            if ( backtracking==0 ) {
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
    // $ANTLR end fromClause

    
    // $ANTLR start identificationVariableDeclaration
    // JPQL.g:472:1: identificationVariableDeclaration[List varDecls] : node= rangeVariableDeclaration (node= join )* ;
    public final void identificationVariableDeclaration(List varDecls) throws RecognitionException {
        Object node = null;
        
    
        try {
            // JPQL.g:473:7: (node= rangeVariableDeclaration (node= join )* )
            // JPQL.g:473:7: node= rangeVariableDeclaration (node= join )*
            {
            pushFollow(FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2520);
            node=rangeVariableDeclaration();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               varDecls.add(node); 
            }
            // JPQL.g:474:9: (node= join )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                
                if ( (LA33_0==INNER||LA33_0==JOIN||LA33_0==LEFT) ) {
                    alt33=1;
                }
                
            
                switch (alt33) {
            	case 1 :
            	    // JPQL.g:474:11: node= join
            	    {
            	    pushFollow(FOLLOW_join_in_identificationVariableDeclaration2539);
            	    node=join();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
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
    // $ANTLR end identificationVariableDeclaration

    
    // $ANTLR start rangeVariableDeclaration
    // JPQL.g:477:1: rangeVariableDeclaration returns [Object node] : schema= abstractSchemaName ( AS )? i= IDENT ;
    public final Object rangeVariableDeclaration() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:481:7: (schema= abstractSchemaName ( AS )? i= IDENT )
            // JPQL.g:481:7: schema= abstractSchemaName ( AS )? i= IDENT
            {
            pushFollow(FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2574);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g:481:35: ( AS )?
            int alt34=2;
            int LA34_0 = input.LA(1);
            
            if ( (LA34_0==AS) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // JPQL.g:481:36: AS
                    {
                    match(input,AS,FOLLOW_AS_in_rangeVariableDeclaration2577); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_rangeVariableDeclaration2583); if (failed) return node;
            if ( backtracking==0 ) {
               
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
    // $ANTLR end rangeVariableDeclaration

    
    // $ANTLR start abstractSchemaName
    // JPQL.g:492:1: abstractSchemaName returns [String schema] : ident= . ;
    public final String abstractSchemaName() throws RecognitionException {

        String schema = null;
    
        Token ident=null;
    
         schema = null; 
        try {
            // JPQL.g:494:7: (ident= . )
            // JPQL.g:494:7: ident= .
            {
            ident=(Token)input.LT(1);
            matchAny(input); if (failed) return schema;
            if ( backtracking==0 ) {
              
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
    // $ANTLR end abstractSchemaName

    
    // $ANTLR start join
    // JPQL.g:501:1: join returns [Object node] : outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) ;
    public final Object join() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token t=null;
        boolean outerJoin = false;

        Object n = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:505:7: (outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) )
            // JPQL.g:505:7: outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            {
            pushFollow(FOLLOW_joinSpec_in_join2666);
            outerJoin=joinSpec();
            _fsp--;
            if (failed) return node;
            // JPQL.g:506:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            int alt36=2;
            int LA36_0 = input.LA(1);
            
            if ( (LA36_0==KEY||LA36_0==VALUE||LA36_0==IDENT) ) {
                alt36=1;
            }
            else if ( (LA36_0==FETCH) ) {
                alt36=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("506:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )", 36, 0, input);
            
                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // JPQL.g:506:9: n= joinAssociationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2680);
                    n=joinAssociationPathExpression();
                    _fsp--;
                    if (failed) return node;
                    // JPQL.g:506:43: ( AS )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);
                    
                    if ( (LA35_0==AS) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // JPQL.g:506:44: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2683); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_join2689); if (failed) return node;
                    if ( backtracking==0 ) {
                      
                                  node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                     outerJoin, n, i.getText()); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:511:9: t= FETCH n= joinAssociationPathExpression
                    {
                    t=(Token)input.LT(1);
                    match(input,FETCH,FOLLOW_FETCH_in_join2711); if (failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2717);
                    n=joinAssociationPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       
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
    // $ANTLR end join

    
    // $ANTLR start joinSpec
    // JPQL.g:518:1: joinSpec returns [boolean outer] : ( LEFT ( OUTER )? | INNER )? JOIN ;
    public final boolean joinSpec() throws RecognitionException {

        boolean outer = false;
    
         outer = false; 
        try {
            // JPQL.g:520:7: ( ( LEFT ( OUTER )? | INNER )? JOIN )
            // JPQL.g:520:7: ( LEFT ( OUTER )? | INNER )? JOIN
            {
            // JPQL.g:520:7: ( LEFT ( OUTER )? | INNER )?
            int alt38=3;
            int LA38_0 = input.LA(1);
            
            if ( (LA38_0==LEFT) ) {
                alt38=1;
            }
            else if ( (LA38_0==INNER) ) {
                alt38=2;
            }
            switch (alt38) {
                case 1 :
                    // JPQL.g:520:8: LEFT ( OUTER )?
                    {
                    match(input,LEFT,FOLLOW_LEFT_in_joinSpec2763); if (failed) return outer;
                    // JPQL.g:520:13: ( OUTER )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);
                    
                    if ( (LA37_0==OUTER) ) {
                        alt37=1;
                    }
                    switch (alt37) {
                        case 1 :
                            // JPQL.g:520:14: OUTER
                            {
                            match(input,OUTER,FOLLOW_OUTER_in_joinSpec2766); if (failed) return outer;
                            
                            }
                            break;
                    
                    }

                    if ( backtracking==0 ) {
                       outer = true; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:520:44: INNER
                    {
                    match(input,INNER,FOLLOW_INNER_in_joinSpec2775); if (failed) return outer;
                    
                    }
                    break;
            
            }

            match(input,JOIN,FOLLOW_JOIN_in_joinSpec2781); if (failed) return outer;
            
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
    // $ANTLR end joinSpec

    
    // $ANTLR start collectionMemberDeclaration
    // JPQL.g:523:1: collectionMemberDeclaration returns [Object node] : t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT ;
    public final Object collectionMemberDeclaration() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token i=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:525:7: (t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT )
            // JPQL.g:525:7: t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT
            {
            t=(Token)input.LT(1);
            match(input,IN,FOLLOW_IN_in_collectionMemberDeclaration2809); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2811); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2817);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2819); if (failed) return node;
            // JPQL.g:526:7: ( AS )?
            int alt39=2;
            int LA39_0 = input.LA(1);
            
            if ( (LA39_0==AS) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // JPQL.g:526:8: AS
                    {
                    match(input,AS,FOLLOW_AS_in_collectionMemberDeclaration2829); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_collectionMemberDeclaration2835); if (failed) return node;
            if ( backtracking==0 ) {
               
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
    // $ANTLR end collectionMemberDeclaration

    
    // $ANTLR start collectionValuedPathExpression
    // JPQL.g:533:1: collectionValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object collectionValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:535:7: (n= pathExpression )
            // JPQL.g:535:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_collectionValuedPathExpression2873);
            n=pathExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end collectionValuedPathExpression

    
    // $ANTLR start associationPathExpression
    // JPQL.g:538:1: associationPathExpression returns [Object node] : n= pathExpression ;
    public final Object associationPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:540:7: (n= pathExpression )
            // JPQL.g:540:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_associationPathExpression2905);
            n=pathExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end associationPathExpression

    
    // $ANTLR start joinAssociationPathExpression
    // JPQL.g:543:1: joinAssociationPathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object joinAssociationPathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
        
            node = null; 
    
        try {
            // JPQL.g:547:8: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // JPQL.g:547:8: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_joinAssociationPathExpression2938);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:548:9: (d= DOT right= attribute )+
            int cnt40=0;
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);
                
                if ( (LA40_0==DOT) ) {
                    alt40=1;
                }
                
            
                switch (alt40) {
            	case 1 :
            	    // JPQL.g:548:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_joinAssociationPathExpression2953); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_joinAssociationPathExpression2959);
            	    right=attribute();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newDot(d.getLine(), d.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt40 >= 1 ) break loop40;
            	    if (backtracking>0) {failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(40, input);
                        throw eee;
                }
                cnt40++;
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
    // $ANTLR end joinAssociationPathExpression

    
    // $ANTLR start singleValuedPathExpression
    // JPQL.g:553:1: singleValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object singleValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:555:7: (n= pathExpression )
            // JPQL.g:555:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_singleValuedPathExpression3015);
            n=pathExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end singleValuedPathExpression

    
    // $ANTLR start stateFieldPathExpression
    // JPQL.g:558:1: stateFieldPathExpression returns [Object node] : n= pathExpression ;
    public final Object stateFieldPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:560:7: (n= pathExpression )
            // JPQL.g:560:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_stateFieldPathExpression3047);
            n=pathExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end stateFieldPathExpression

    
    // $ANTLR start pathExpression
    // JPQL.g:563:1: pathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object pathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:567:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // JPQL.g:567:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExpression3079);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:568:9: (d= DOT right= attribute )+
            int cnt41=0;
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);
                
                if ( (LA41_0==DOT) ) {
                    alt41=1;
                }
                
            
                switch (alt41) {
            	case 1 :
            	    // JPQL.g:568:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExpression3094); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExpression3100);
            	    right=attribute();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      
            	                      node = factory.newDot(d.getLine(), d.getCharPositionInLine(), node, right); 
            	                  
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt41 >= 1 ) break loop41;
            	    if (backtracking>0) {failed=true; return node;}
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
    // $ANTLR end pathExpression

    
    // $ANTLR start attribute
    // JPQL.g:579:1: attribute returns [Object node] : i= . ;
    public final Object attribute() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g:582:7: (i= . )
            // JPQL.g:582:7: i= .
            {
            i=(Token)input.LT(1);
            matchAny(input); if (failed) return node;
            if ( backtracking==0 ) {
               
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
    // $ANTLR end attribute

    
    // $ANTLR start variableAccessOrTypeConstant
    // JPQL.g:589:1: variableAccessOrTypeConstant returns [Object node] : i= IDENT ;
    public final Object variableAccessOrTypeConstant() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g:591:7: (i= IDENT )
            // JPQL.g:591:7: i= IDENT
            {
            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_variableAccessOrTypeConstant3196); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end variableAccessOrTypeConstant

    
    // $ANTLR start whereClause
    // JPQL.g:595:1: whereClause returns [Object node] : t= WHERE n= conditionalExpression ;
    public final Object whereClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:597:7: (t= WHERE n= conditionalExpression )
            // JPQL.g:597:7: t= WHERE n= conditionalExpression
            {
            t=(Token)input.LT(1);
            match(input,WHERE,FOLLOW_WHERE_in_whereClause3234); if (failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whereClause3240);
            n=conditionalExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              
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
    // $ANTLR end whereClause

    
    // $ANTLR start conditionalExpression
    // JPQL.g:603:1: conditionalExpression returns [Object node] : n= conditionalTerm (t= OR right= conditionalTerm )* ;
    public final Object conditionalExpression() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:607:7: (n= conditionalTerm (t= OR right= conditionalTerm )* )
            // JPQL.g:607:7: n= conditionalTerm (t= OR right= conditionalTerm )*
            {
            pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3282);
            n=conditionalTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:608:9: (t= OR right= conditionalTerm )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);
                
                if ( (LA42_0==OR) ) {
                    alt42=1;
                }
                
            
                switch (alt42) {
            	case 1 :
            	    // JPQL.g:608:10: t= OR right= conditionalTerm
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,OR,FOLLOW_OR_in_conditionalExpression3297); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3303);
            	    right=conditionalTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newOr(t.getLine(), t.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop42;
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
    // $ANTLR end conditionalExpression

    
    // $ANTLR start conditionalTerm
    // JPQL.g:613:1: conditionalTerm returns [Object node] : n= conditionalFactor (t= AND right= conditionalFactor )* ;
    public final Object conditionalTerm() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:617:7: (n= conditionalFactor (t= AND right= conditionalFactor )* )
            // JPQL.g:617:7: n= conditionalFactor (t= AND right= conditionalFactor )*
            {
            pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3358);
            n=conditionalFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:618:9: (t= AND right= conditionalFactor )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                
                if ( (LA43_0==AND) ) {
                    alt43=1;
                }
                
            
                switch (alt43) {
            	case 1 :
            	    // JPQL.g:618:10: t= AND right= conditionalFactor
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,AND,FOLLOW_AND_in_conditionalTerm3373); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3379);
            	    right=conditionalFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newAnd(t.getLine(), t.getCharPositionInLine(), node, right); 
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
    // $ANTLR end conditionalTerm

    
    // $ANTLR start conditionalFactor
    // JPQL.g:623:1: conditionalFactor returns [Object node] : (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) ;
    public final Object conditionalFactor() throws RecognitionException {

        Object node = null;
    
        Token n=null;
        Object n1 = null;
        
    
         node = null; 
        try {
            // JPQL.g:625:7: ( (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) )
            // JPQL.g:625:7: (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            {
            // JPQL.g:625:7: (n= NOT )?
            int alt44=2;
            int LA44_0 = input.LA(1);
            
            if ( (LA44_0==NOT) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // JPQL.g:625:8: n= NOT
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_conditionalFactor3434); if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:626:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            int alt45=2;
            int LA45_0 = input.LA(1);
            
            if ( (LA45_0==ABS||LA45_0==AVG||(LA45_0>=CASE && LA45_0<=CURRENT_TIMESTAMP)||LA45_0==FALSE||LA45_0==INDEX||LA45_0==KEY||LA45_0==LENGTH||(LA45_0>=LOCATE && LA45_0<=MAX)||(LA45_0>=MIN && LA45_0<=MOD)||LA45_0==NULLIF||(LA45_0>=SIZE && LA45_0<=SQRT)||(LA45_0>=SUBSTRING && LA45_0<=SUM)||(LA45_0>=TRIM && LA45_0<=TYPE)||(LA45_0>=UPPER && LA45_0<=VALUE)||LA45_0==IDENT||LA45_0==LEFT_ROUND_BRACKET||(LA45_0>=PLUS && LA45_0<=MINUS)||(LA45_0>=INTEGER_LITERAL && LA45_0<=NAMED_PARAM)) ) {
                alt45=1;
            }
            else if ( (LA45_0==EXISTS) ) {
                alt45=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("626:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )", 45, 0, input);
            
                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // JPQL.g:626:11: n1= conditionalPrimary
                    {
                    pushFollow(FOLLOW_conditionalPrimary_in_conditionalFactor3453);
                    n1=conditionalPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      
                                    node = n1; 
                                    if (n != null) {
                                        node = factory.newNot(n.getLine(), n.getCharPositionInLine(), n1); 
                                    }
                                
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:633:11: n1= existsExpression[(n!=null)]
                    {
                    pushFollow(FOLLOW_existsExpression_in_conditionalFactor3482);
                    n1=existsExpression((n!=null));
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end conditionalFactor

    
    // $ANTLR start conditionalPrimary
    // JPQL.g:637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );
    public final Object conditionalPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:639:7: ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression )
            int alt46=2;
            int LA46_0 = input.LA(1);
            
            if ( (LA46_0==LEFT_ROUND_BRACKET) ) {
                int LA46_1 = input.LA(2);
                
                if ( (LA46_1==NOT) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==LEFT_ROUND_BRACKET) ) {
                    int LA46_45 = input.LA(3);
                    
                    if ( (LA46_45==PLUS) ) {
                        int LA46_90 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 90, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==MINUS) ) {
                        int LA46_91 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 91, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==AVG) ) {
                        int LA46_92 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 92, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==MAX) ) {
                        int LA46_93 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 93, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==MIN) ) {
                        int LA46_94 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 94, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==SUM) ) {
                        int LA46_95 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 95, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==COUNT) ) {
                        int LA46_96 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 96, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==IDENT) ) {
                        int LA46_97 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 97, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==KEY) ) {
                        int LA46_98 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 98, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==VALUE) ) {
                        int LA46_99 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 99, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==POSITIONAL_PARAM) ) {
                        int LA46_100 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 100, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==NAMED_PARAM) ) {
                        int LA46_101 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 101, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==CASE) ) {
                        int LA46_102 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 102, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==COALESCE) ) {
                        int LA46_103 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 103, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==NULLIF) ) {
                        int LA46_104 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 104, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==ABS) ) {
                        int LA46_105 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 105, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==LENGTH) ) {
                        int LA46_106 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 106, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==MOD) ) {
                        int LA46_107 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 107, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==SQRT) ) {
                        int LA46_108 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 108, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==LOCATE) ) {
                        int LA46_109 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 109, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==SIZE) ) {
                        int LA46_110 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 110, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==INDEX) ) {
                        int LA46_111 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 111, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==LEFT_ROUND_BRACKET) ) {
                        int LA46_112 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 112, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==INTEGER_LITERAL) ) {
                        int LA46_113 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 113, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==LONG_LITERAL) ) {
                        int LA46_114 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 114, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==FLOAT_LITERAL) ) {
                        int LA46_115 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 115, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==DOUBLE_LITERAL) ) {
                        int LA46_116 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 116, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_45==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==CURRENT_DATE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==CURRENT_TIME) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==CURRENT_TIMESTAMP) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==CONCAT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==SUBSTRING) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==TRIM) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==UPPER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==LOWER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==STRING_LITERAL_DOUBLE_QUOTED) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==STRING_LITERAL_SINGLE_QUOTED) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==TRUE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==FALSE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==DATE_LITERAL) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==TIME_LITERAL) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==TIMESTAMP_LITERAL) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==TYPE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==EXISTS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_45==SELECT) && (synpred1())) {
                        alt46=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 45, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==PLUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
                        {
                        int LA46_136 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 136, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA46_137 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 137, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA46_138 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 138, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
                        {
                        int LA46_139 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 139, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
                        {
                        int LA46_140 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 140, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA46_141 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 141, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA46_142 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 142, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA46_143 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 143, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
                        {
                        int LA46_144 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 144, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
                        {
                        int LA46_145 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 145, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CASE:
                        {
                        int LA46_146 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 146, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COALESCE:
                        {
                        int LA46_147 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 147, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NULLIF:
                        {
                        int LA46_148 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 148, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
                        {
                        int LA46_149 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 149, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
                        {
                        int LA46_150 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 150, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
                        {
                        int LA46_151 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 151, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
                        {
                        int LA46_152 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 152, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
                        {
                        int LA46_153 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 153, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
                        {
                        int LA46_154 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 154, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INDEX:
                        {
                        int LA46_155 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 155, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
                        int LA46_156 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 156, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
                        {
                        int LA46_157 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 157, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
                        {
                        int LA46_158 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 158, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
                        {
                        int LA46_159 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 159, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
                        {
                        int LA46_160 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 160, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 46, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA46_1==MINUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
                        {
                        int LA46_161 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 161, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA46_162 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 162, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA46_163 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 163, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
                        {
                        int LA46_164 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 164, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
                        {
                        int LA46_165 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 165, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA46_166 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 166, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA46_167 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 167, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA46_168 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 168, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
                        {
                        int LA46_169 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 169, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
                        {
                        int LA46_170 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 170, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CASE:
                        {
                        int LA46_171 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 171, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COALESCE:
                        {
                        int LA46_172 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 172, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NULLIF:
                        {
                        int LA46_173 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 173, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
                        {
                        int LA46_174 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 174, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
                        {
                        int LA46_175 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 175, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
                        {
                        int LA46_176 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 176, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
                        {
                        int LA46_177 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 177, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
                        {
                        int LA46_178 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 178, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
                        {
                        int LA46_179 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 179, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INDEX:
                        {
                        int LA46_180 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 180, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
                        int LA46_181 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 181, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
                        {
                        int LA46_182 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 182, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
                        {
                        int LA46_183 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 183, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
                        {
                        int LA46_184 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 184, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
                        {
                        int LA46_185 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 185, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 47, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA46_1==AVG) ) {
                    int LA46_48 = input.LA(3);
                    
                    if ( (LA46_48==LEFT_ROUND_BRACKET) ) {
                        int LA46_186 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 186, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 48, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==MAX) ) {
                    int LA46_49 = input.LA(3);
                    
                    if ( (LA46_49==LEFT_ROUND_BRACKET) ) {
                        int LA46_187 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 187, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 49, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==MIN) ) {
                    int LA46_50 = input.LA(3);
                    
                    if ( (LA46_50==LEFT_ROUND_BRACKET) ) {
                        int LA46_188 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 188, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 50, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==SUM) ) {
                    int LA46_51 = input.LA(3);
                    
                    if ( (LA46_51==LEFT_ROUND_BRACKET) ) {
                        int LA46_189 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 189, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 51, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==COUNT) ) {
                    int LA46_52 = input.LA(3);
                    
                    if ( (LA46_52==LEFT_ROUND_BRACKET) ) {
                        int LA46_190 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 190, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 52, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==IDENT) ) {
                    int LA46_53 = input.LA(3);
                    
                    if ( (LA46_53==DOT) ) {
                        int LA46_191 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 191, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_53==MULTIPLY) ) {
                        int LA46_192 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 192, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_53==DIVIDE) ) {
                        int LA46_193 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 193, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_53==PLUS) ) {
                        int LA46_194 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 194, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_53==MINUS) ) {
                        int LA46_195 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 195, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_53==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
                    }
                    else if ( (LA46_53==EQUALS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_53==NOT_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_53==GREATER_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_53==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_53==LESS_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_53==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_53==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_53==BETWEEN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_53==LIKE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_53==IN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_53==MEMBER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_53==IS) && (synpred1())) {
                        alt46=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 53, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==KEY) ) {
                    int LA46_54 = input.LA(3);
                    
                    if ( (LA46_54==LEFT_ROUND_BRACKET) ) {
                        int LA46_209 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 209, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 54, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==VALUE) ) {
                    int LA46_55 = input.LA(3);
                    
                    if ( (LA46_55==LEFT_ROUND_BRACKET) ) {
                        int LA46_210 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 210, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 55, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==POSITIONAL_PARAM) ) {
                    int LA46_56 = input.LA(3);
                    
                    if ( (LA46_56==MULTIPLY) ) {
                        int LA46_211 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 211, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_56==DIVIDE) ) {
                        int LA46_212 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 212, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_56==PLUS) ) {
                        int LA46_213 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 213, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_56==MINUS) ) {
                        int LA46_214 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 214, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_56==EQUALS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==NOT_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==GREATER_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==LESS_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==BETWEEN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==LIKE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==IN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==MEMBER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==IS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_56==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 56, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==NAMED_PARAM) ) {
                    int LA46_57 = input.LA(3);
                    
                    if ( (LA46_57==MULTIPLY) ) {
                        int LA46_228 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 228, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_57==DIVIDE) ) {
                        int LA46_229 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 229, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_57==PLUS) ) {
                        int LA46_230 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 230, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_57==MINUS) ) {
                        int LA46_231 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 231, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_57==EQUALS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==NOT_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==GREATER_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==LESS_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==BETWEEN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==LIKE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==IN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==MEMBER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==IS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_57==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 57, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==CASE) ) {
                    switch ( input.LA(3) ) {
                    case IDENT:
                        {
                        int LA46_245 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 245, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA46_246 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 246, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA46_247 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 247, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TYPE:
                        {
                        int LA46_248 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 248, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case WHEN:
                        {
                        int LA46_249 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 249, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 58, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA46_1==COALESCE) ) {
                    int LA46_59 = input.LA(3);
                    
                    if ( (LA46_59==LEFT_ROUND_BRACKET) ) {
                        int LA46_250 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 250, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 59, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==NULLIF) ) {
                    int LA46_60 = input.LA(3);
                    
                    if ( (LA46_60==LEFT_ROUND_BRACKET) ) {
                        int LA46_251 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 251, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 60, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==ABS) ) {
                    int LA46_61 = input.LA(3);
                    
                    if ( (LA46_61==LEFT_ROUND_BRACKET) ) {
                        int LA46_252 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 252, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 61, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==LENGTH) ) {
                    int LA46_62 = input.LA(3);
                    
                    if ( (LA46_62==LEFT_ROUND_BRACKET) ) {
                        int LA46_253 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 253, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 62, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==MOD) ) {
                    int LA46_63 = input.LA(3);
                    
                    if ( (LA46_63==LEFT_ROUND_BRACKET) ) {
                        int LA46_254 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 254, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 63, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==SQRT) ) {
                    int LA46_64 = input.LA(3);
                    
                    if ( (LA46_64==LEFT_ROUND_BRACKET) ) {
                        int LA46_255 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 255, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 64, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==LOCATE) ) {
                    int LA46_65 = input.LA(3);
                    
                    if ( (LA46_65==LEFT_ROUND_BRACKET) ) {
                        int LA46_256 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 256, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 65, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==SIZE) ) {
                    int LA46_66 = input.LA(3);
                    
                    if ( (LA46_66==LEFT_ROUND_BRACKET) ) {
                        int LA46_257 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 257, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 66, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==INDEX) ) {
                    int LA46_67 = input.LA(3);
                    
                    if ( (LA46_67==LEFT_ROUND_BRACKET) ) {
                        int LA46_258 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 258, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 67, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==INTEGER_LITERAL) ) {
                    int LA46_68 = input.LA(3);
                    
                    if ( (LA46_68==MULTIPLY) ) {
                        int LA46_259 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 259, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_68==DIVIDE) ) {
                        int LA46_260 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 260, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_68==PLUS) ) {
                        int LA46_261 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 261, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_68==MINUS) ) {
                        int LA46_262 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 262, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_68==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
                    }
                    else if ( (LA46_68==EQUALS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_68==NOT_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_68==GREATER_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_68==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_68==LESS_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_68==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_68==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_68==BETWEEN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_68==LIKE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_68==IN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_68==MEMBER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_68==IS) && (synpred1())) {
                        alt46=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 68, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==LONG_LITERAL) ) {
                    int LA46_69 = input.LA(3);
                    
                    if ( (LA46_69==MULTIPLY) ) {
                        int LA46_276 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 276, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_69==DIVIDE) ) {
                        int LA46_277 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 277, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_69==PLUS) ) {
                        int LA46_278 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 278, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_69==MINUS) ) {
                        int LA46_279 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 279, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_69==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
                    }
                    else if ( (LA46_69==EQUALS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_69==NOT_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_69==GREATER_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_69==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_69==LESS_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_69==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_69==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_69==BETWEEN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_69==LIKE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_69==IN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_69==MEMBER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_69==IS) && (synpred1())) {
                        alt46=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 69, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==FLOAT_LITERAL) ) {
                    int LA46_70 = input.LA(3);
                    
                    if ( (LA46_70==MULTIPLY) ) {
                        int LA46_293 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 293, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_70==DIVIDE) ) {
                        int LA46_294 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 294, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_70==PLUS) ) {
                        int LA46_295 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 295, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_70==MINUS) ) {
                        int LA46_296 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 296, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_70==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
                    }
                    else if ( (LA46_70==EQUALS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_70==NOT_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_70==GREATER_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_70==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_70==LESS_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_70==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_70==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_70==BETWEEN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_70==LIKE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_70==IN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_70==MEMBER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_70==IS) && (synpred1())) {
                        alt46=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 70, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==DOUBLE_LITERAL) ) {
                    int LA46_71 = input.LA(3);
                    
                    if ( (LA46_71==MULTIPLY) ) {
                        int LA46_310 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 310, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_71==DIVIDE) ) {
                        int LA46_311 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 311, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_71==PLUS) ) {
                        int LA46_312 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 312, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_71==MINUS) ) {
                        int LA46_313 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 313, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_71==EQUALS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==NOT_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==GREATER_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==LESS_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==BETWEEN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==LIKE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==IN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==MEMBER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==IS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_71==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 71, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==CURRENT_DATE) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==CURRENT_TIME) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==CURRENT_TIMESTAMP) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==CONCAT) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==SUBSTRING) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==TRIM) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==UPPER) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==LOWER) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==STRING_LITERAL_DOUBLE_QUOTED) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==STRING_LITERAL_SINGLE_QUOTED) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==TRUE) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==FALSE) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==DATE_LITERAL) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==TIME_LITERAL) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==TIMESTAMP_LITERAL) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==TYPE) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==EXISTS) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==SELECT) ) {
                    alt46=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA46_0==ABS||LA46_0==AVG||(LA46_0>=CASE && LA46_0<=CURRENT_TIMESTAMP)||LA46_0==FALSE||LA46_0==INDEX||LA46_0==KEY||LA46_0==LENGTH||(LA46_0>=LOCATE && LA46_0<=MAX)||(LA46_0>=MIN && LA46_0<=MOD)||LA46_0==NULLIF||(LA46_0>=SIZE && LA46_0<=SQRT)||(LA46_0>=SUBSTRING && LA46_0<=SUM)||(LA46_0>=TRIM && LA46_0<=TYPE)||(LA46_0>=UPPER && LA46_0<=VALUE)||LA46_0==IDENT||(LA46_0>=PLUS && LA46_0<=MINUS)||(LA46_0>=INTEGER_LITERAL && LA46_0<=NAMED_PARAM)) ) {
                alt46=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("637:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 0, input);
            
                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // JPQL.g:639:7: ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3539); if (failed) return node;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalPrimary3545);
                    n=conditionalExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3547); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:641:7: n= simpleConditionalExpression
                    {
                    pushFollow(FOLLOW_simpleConditionalExpression_in_conditionalPrimary3561);
                    n=simpleConditionalExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end conditionalPrimary

    
    // $ANTLR start simpleConditionalExpression
    // JPQL.g:644:1: simpleConditionalExpression returns [Object node] : (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] );
    public final Object simpleConditionalExpression() throws RecognitionException {

        Object node = null;
    
        Object left = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:648:7: (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] )
            int alt47=2;
            int LA47_0 = input.LA(1);
            
            if ( (LA47_0==ABS||LA47_0==AVG||(LA47_0>=CASE && LA47_0<=COALESCE)||LA47_0==COUNT||LA47_0==INDEX||LA47_0==KEY||LA47_0==LENGTH||LA47_0==LOCATE||LA47_0==MAX||(LA47_0>=MIN && LA47_0<=MOD)||LA47_0==NULLIF||(LA47_0>=SIZE && LA47_0<=SQRT)||LA47_0==SUM||LA47_0==VALUE||LA47_0==IDENT||LA47_0==LEFT_ROUND_BRACKET||(LA47_0>=PLUS && LA47_0<=MINUS)||(LA47_0>=INTEGER_LITERAL && LA47_0<=DOUBLE_LITERAL)||(LA47_0>=POSITIONAL_PARAM && LA47_0<=NAMED_PARAM)) ) {
                alt47=1;
            }
            else if ( (LA47_0==CONCAT||(LA47_0>=CURRENT_DATE && LA47_0<=CURRENT_TIMESTAMP)||LA47_0==FALSE||LA47_0==LOWER||LA47_0==SUBSTRING||(LA47_0>=TRIM && LA47_0<=TYPE)||LA47_0==UPPER||(LA47_0>=STRING_LITERAL_DOUBLE_QUOTED && LA47_0<=TIMESTAMP_LITERAL)) ) {
                alt47=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("644:1: simpleConditionalExpression returns [Object node] : (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] );", 47, 0, input);
            
                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // JPQL.g:648:7: left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_simpleConditionalExpression3593);
                    left=arithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3599);
                    n=simpleConditionalExpressionRemainder(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:649:7: left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3614);
                    left=nonArithmeticScalarExpression();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3620);
                    n=simpleConditionalExpressionRemainder(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end simpleConditionalExpression

    
    // $ANTLR start simpleConditionalExpressionRemainder
    // JPQL.g:652:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );
    public final Object simpleConditionalExpressionRemainder(Object left) throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Token n2=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:654:7: (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] )
            int alt50=3;
            switch ( input.LA(1) ) {
            case EQUALS:
            case NOT_EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL_TO:
            case LESS_THAN:
            case LESS_THAN_EQUAL_TO:
                {
                alt50=1;
                }
                break;
            case BETWEEN:
            case IN:
            case LIKE:
            case MEMBER:
            case NOT:
                {
                alt50=2;
                }
                break;
            case IS:
                {
                alt50=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("652:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );", 50, 0, input);
            
                throw nvae;
            }
            
            switch (alt50) {
                case 1 :
                    // JPQL.g:654:7: n= comparisonExpression[left]
                    {
                    pushFollow(FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3655);
                    n=comparisonExpression(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:655:7: (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left]
                    {
                    // JPQL.g:655:7: (n1= NOT )?
                    int alt48=2;
                    int LA48_0 = input.LA(1);
                    
                    if ( (LA48_0==NOT) ) {
                        alt48=1;
                    }
                    switch (alt48) {
                        case 1 :
                            // JPQL.g:655:8: n1= NOT
                            {
                            n1=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3669); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3677);
                    n=conditionWithNotExpression((n1!=null),  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:656:7: IS (n2= NOT )? n= isExpression[(n2!=null), left]
                    {
                    match(input,IS,FOLLOW_IS_in_simpleConditionalExpressionRemainder3688); if (failed) return node;
                    // JPQL.g:656:10: (n2= NOT )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);
                    
                    if ( (LA49_0==NOT) ) {
                        alt49=1;
                    }
                    switch (alt49) {
                        case 1 :
                            // JPQL.g:656:11: n2= NOT
                            {
                            n2=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3693); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3701);
                    n=isExpression((n2!=null),  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end simpleConditionalExpressionRemainder

    
    // $ANTLR start conditionWithNotExpression
    // JPQL.g:659:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );
    public final Object conditionWithNotExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:661:7: (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] )
            int alt51=4;
            switch ( input.LA(1) ) {
            case BETWEEN:
                {
                alt51=1;
                }
                break;
            case LIKE:
                {
                alt51=2;
                }
                break;
            case IN:
                {
                alt51=3;
                }
                break;
            case MEMBER:
                {
                alt51=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("659:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );", 51, 0, input);
            
                throw nvae;
            }
            
            switch (alt51) {
                case 1 :
                    // JPQL.g:661:7: n= betweenExpression[not, left]
                    {
                    pushFollow(FOLLOW_betweenExpression_in_conditionWithNotExpression3736);
                    n=betweenExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:662:7: n= likeExpression[not, left]
                    {
                    pushFollow(FOLLOW_likeExpression_in_conditionWithNotExpression3751);
                    n=likeExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:663:7: n= inExpression[not, left]
                    {
                    pushFollow(FOLLOW_inExpression_in_conditionWithNotExpression3765);
                    n=inExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:664:7: n= collectionMemberExpression[not, left]
                    {
                    pushFollow(FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3779);
                    n=collectionMemberExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end conditionWithNotExpression

    
    // $ANTLR start isExpression
    // JPQL.g:667:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );
    public final Object isExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:669:7: (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] )
            int alt52=2;
            int LA52_0 = input.LA(1);
            
            if ( (LA52_0==NULL) ) {
                alt52=1;
            }
            else if ( (LA52_0==EMPTY) ) {
                alt52=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("667:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );", 52, 0, input);
            
                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // JPQL.g:669:7: n= nullComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_nullComparisonExpression_in_isExpression3814);
                    n=nullComparisonExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:670:7: n= emptyCollectionComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_emptyCollectionComparisonExpression_in_isExpression3829);
                    n=emptyCollectionComparisonExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end isExpression

    
    // $ANTLR start betweenExpression
    // JPQL.g:673:1: betweenExpression[boolean not, Object left] returns [Object node] : t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression ;
    public final Object betweenExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object lowerBound = null;

        Object upperBound = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:677:7: (t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression )
            // JPQL.g:677:7: t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression
            {
            t=(Token)input.LT(1);
            match(input,BETWEEN,FOLLOW_BETWEEN_in_betweenExpression3862); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3876);
            lowerBound=arithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,AND,FOLLOW_AND_in_betweenExpression3878); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3884);
            upperBound=arithmeticExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              
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
    // $ANTLR end betweenExpression

    protected static class inExpression_scope {
        List items;
    }
    protected Stack inExpression_stack = new Stack();
    
    
    // $ANTLR start inExpression
    // JPQL.g:685:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );
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
            // JPQL.g:693:8: (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET )
            int alt55=2;
            int LA55_0 = input.LA(1);
            
            if ( (LA55_0==IN) ) {
                int LA55_1 = input.LA(2);
                
                if ( (LA55_1==LEFT_ROUND_BRACKET) ) {
                    alt55=2;
                }
                else if ( ((LA55_1>=POSITIONAL_PARAM && LA55_1<=NAMED_PARAM)) ) {
                    alt55=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("685:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );", 55, 1, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("685:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );", 55, 0, input);
            
                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // JPQL.g:693:8: t= IN n= inputParameter
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_inExpression3930); if (failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_inExpression3936);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      
                                      node = factory.newIn(t.getLine(), t.getCharPositionInLine(),
                                                           not, left, n);
                                  
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:698:9: t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_inExpression3963); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3973); if (failed) return node;
                    // JPQL.g:700:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )
                    int alt54=2;
                    int LA54_0 = input.LA(1);
                    
                    if ( (LA54_0==IDENT||(LA54_0>=INTEGER_LITERAL && LA54_0<=STRING_LITERAL_SINGLE_QUOTED)||(LA54_0>=POSITIONAL_PARAM && LA54_0<=NAMED_PARAM)) ) {
                        alt54=1;
                    }
                    else if ( (LA54_0==SELECT) ) {
                        alt54=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("700:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )", 54, 0, input);
                    
                        throw nvae;
                    }
                    switch (alt54) {
                        case 1 :
                            // JPQL.g:700:11: itemNode= inItem ( COMMA itemNode= inItem )*
                            {
                            pushFollow(FOLLOW_inItem_in_inExpression3989);
                            itemNode=inItem();
                            _fsp--;
                            if (failed) return node;
                            if ( backtracking==0 ) {
                               ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                            }
                            // JPQL.g:701:13: ( COMMA itemNode= inItem )*
                            loop53:
                            do {
                                int alt53=2;
                                int LA53_0 = input.LA(1);
                                
                                if ( (LA53_0==COMMA) ) {
                                    alt53=1;
                                }
                                
                            
                                switch (alt53) {
                            	case 1 :
                            	    // JPQL.g:701:15: COMMA itemNode= inItem
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_inExpression4007); if (failed) return node;
                            	    pushFollow(FOLLOW_inItem_in_inExpression4013);
                            	    itemNode=inItem();
                            	    _fsp--;
                            	    if (failed) return node;
                            	    if ( backtracking==0 ) {
                            	       ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                            	    }
                            	    
                            	    }
                            	    break;
                            
                            	default :
                            	    break loop53;
                                }
                            } while (true);

                            if ( backtracking==0 ) {
                              
                                              node = factory.newIn(t.getLine(), t.getCharPositionInLine(),
                                                                   not, left, ((inExpression_scope)inExpression_stack.peek()).items);
                                          
                            }
                            
                            }
                            break;
                        case 2 :
                            // JPQL.g:706:11: subqueryNode= subquery
                            {
                            pushFollow(FOLLOW_subquery_in_inExpression4048);
                            subqueryNode=subquery();
                            _fsp--;
                            if (failed) return node;
                            if ( backtracking==0 ) {
                              
                                              node = factory.newIn(t.getLine(), t.getCharPositionInLine(),
                                                                   not, left, subqueryNode);
                                          
                            }
                            
                            }
                            break;
                    
                    }

                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4082); if (failed) return node;
                    
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
    // $ANTLR end inExpression

    
    // $ANTLR start inItem
    // JPQL.g:715:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant );
    public final Object inItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:717:7: (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant )
            int alt56=4;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt56=1;
                }
                break;
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt56=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt56=3;
                }
                break;
            case IDENT:
                {
                alt56=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("715:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant );", 56, 0, input);
            
                throw nvae;
            }
            
            switch (alt56) {
                case 1 :
                    // JPQL.g:717:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_inItem4112);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:718:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_inItem4126);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:719:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_inItem4140);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:720:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_inItem4154);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end inItem

    
    // $ANTLR start likeExpression
    // JPQL.g:723:1: likeExpression[boolean not, Object left] returns [Object node] : t= LIKE pattern= likeValue (escapeChars= escape )? ;
    public final Object likeExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object pattern = null;

        Object escapeChars = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:727:7: (t= LIKE pattern= likeValue (escapeChars= escape )? )
            // JPQL.g:727:7: t= LIKE pattern= likeValue (escapeChars= escape )?
            {
            t=(Token)input.LT(1);
            match(input,LIKE,FOLLOW_LIKE_in_likeExpression4186); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_likeExpression4192);
            pattern=likeValue();
            _fsp--;
            if (failed) return node;
            // JPQL.g:728:9: (escapeChars= escape )?
            int alt57=2;
            int LA57_0 = input.LA(1);
            
            if ( (LA57_0==ESCAPE) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // JPQL.g:728:10: escapeChars= escape
                    {
                    pushFollow(FOLLOW_escape_in_likeExpression4207);
                    escapeChars=escape();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            if ( backtracking==0 ) {
              
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
    // $ANTLR end likeExpression

    
    // $ANTLR start escape
    // JPQL.g:735:1: escape returns [Object node] : t= ESCAPE escapeClause= likeValue ;
    public final Object escape() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object escapeClause = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:739:7: (t= ESCAPE escapeClause= likeValue )
            // JPQL.g:739:7: t= ESCAPE escapeClause= likeValue
            {
            t=(Token)input.LT(1);
            match(input,ESCAPE,FOLLOW_ESCAPE_in_escape4247); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_escape4253);
            escapeClause=likeValue();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end escape

    
    // $ANTLR start likeValue
    // JPQL.g:743:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );
    public final Object likeValue() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:745:7: (n= literalString | n= inputParameter )
            int alt58=2;
            int LA58_0 = input.LA(1);
            
            if ( ((LA58_0>=STRING_LITERAL_DOUBLE_QUOTED && LA58_0<=STRING_LITERAL_SINGLE_QUOTED)) ) {
                alt58=1;
            }
            else if ( ((LA58_0>=POSITIONAL_PARAM && LA58_0<=NAMED_PARAM)) ) {
                alt58=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("743:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );", 58, 0, input);
            
                throw nvae;
            }
            switch (alt58) {
                case 1 :
                    // JPQL.g:745:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_likeValue4293);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:746:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_likeValue4307);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end likeValue

    
    // $ANTLR start nullComparisonExpression
    // JPQL.g:749:1: nullComparisonExpression[boolean not, Object left] returns [Object node] : t= NULL ;
    public final Object nullComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g:751:7: (t= NULL )
            // JPQL.g:751:7: t= NULL
            {
            t=(Token)input.LT(1);
            match(input,NULL,FOLLOW_NULL_in_nullComparisonExpression4340); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end nullComparisonExpression

    
    // $ANTLR start emptyCollectionComparisonExpression
    // JPQL.g:755:1: emptyCollectionComparisonExpression[boolean not, Object left] returns [Object node] : t= EMPTY ;
    public final Object emptyCollectionComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g:757:7: (t= EMPTY )
            // JPQL.g:757:7: t= EMPTY
            {
            t=(Token)input.LT(1);
            match(input,EMPTY,FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4381); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end emptyCollectionComparisonExpression

    
    // $ANTLR start collectionMemberExpression
    // JPQL.g:761:1: collectionMemberExpression[boolean not, Object left] returns [Object node] : t= MEMBER ( OF )? n= collectionValuedPathExpression ;
    public final Object collectionMemberExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:763:7: (t= MEMBER ( OF )? n= collectionValuedPathExpression )
            // JPQL.g:763:7: t= MEMBER ( OF )? n= collectionValuedPathExpression
            {
            t=(Token)input.LT(1);
            match(input,MEMBER,FOLLOW_MEMBER_in_collectionMemberExpression4422); if (failed) return node;
            // JPQL.g:763:17: ( OF )?
            int alt59=2;
            int LA59_0 = input.LA(1);
            
            if ( (LA59_0==OF) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // JPQL.g:763:18: OF
                    {
                    match(input,OF,FOLLOW_OF_in_collectionMemberExpression4425); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4433);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               
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
    // $ANTLR end collectionMemberExpression

    
    // $ANTLR start existsExpression
    // JPQL.g:770:1: existsExpression[boolean not] returns [Object node] : t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET ;
    public final Object existsExpression(boolean not) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object subqueryNode = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:774:7: (t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET )
            // JPQL.g:774:7: t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_existsExpression4473); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4475); if (failed) return node;
            pushFollow(FOLLOW_subquery_in_existsExpression4481);
            subqueryNode=subquery();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4483); if (failed) return node;
            if ( backtracking==0 ) {
               
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
    // $ANTLR end existsExpression

    
    // $ANTLR start comparisonExpression
    // JPQL.g:781:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );
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
            // JPQL.g:783:7: (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand )
            int alt60=6;
            switch ( input.LA(1) ) {
            case EQUALS:
                {
                alt60=1;
                }
                break;
            case NOT_EQUAL_TO:
                {
                alt60=2;
                }
                break;
            case GREATER_THAN:
                {
                alt60=3;
                }
                break;
            case GREATER_THAN_EQUAL_TO:
                {
                alt60=4;
                }
                break;
            case LESS_THAN:
                {
                alt60=5;
                }
                break;
            case LESS_THAN_EQUAL_TO:
                {
                alt60=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("781:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );", 60, 0, input);
            
                throw nvae;
            }
            
            switch (alt60) {
                case 1 :
                    // JPQL.g:783:7: t1= EQUALS n= comparisonExpressionRightOperand
                    {
                    t1=(Token)input.LT(1);
                    match(input,EQUALS,FOLLOW_EQUALS_in_comparisonExpression4523); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4529);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newEquals(t1.getLine(), t1.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:785:7: t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t2=(Token)input.LT(1);
                    match(input,NOT_EQUAL_TO,FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4550); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4556);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newNotEquals(t2.getLine(), t2.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:787:7: t3= GREATER_THAN n= comparisonExpressionRightOperand
                    {
                    t3=(Token)input.LT(1);
                    match(input,GREATER_THAN,FOLLOW_GREATER_THAN_in_comparisonExpression4577); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4583);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThan(t3.getLine(), t3.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:789:7: t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t4=(Token)input.LT(1);
                    match(input,GREATER_THAN_EQUAL_TO,FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4604); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4610);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThanEqual(t4.getLine(), t4.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:791:7: t5= LESS_THAN n= comparisonExpressionRightOperand
                    {
                    t5=(Token)input.LT(1);
                    match(input,LESS_THAN,FOLLOW_LESS_THAN_in_comparisonExpression4631); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4637);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newLessThan(t5.getLine(), t5.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:793:7: t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t6=(Token)input.LT(1);
                    match(input,LESS_THAN_EQUAL_TO,FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4658); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4664);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end comparisonExpression

    
    // $ANTLR start comparisonExpressionRightOperand
    // JPQL.g:797:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression );
    public final Object comparisonExpressionRightOperand() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:799:7: (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression )
            int alt61=3;
            switch ( input.LA(1) ) {
            case ABS:
            case AVG:
            case CASE:
            case COALESCE:
            case COUNT:
            case INDEX:
            case KEY:
            case LENGTH:
            case LOCATE:
            case MAX:
            case MIN:
            case MOD:
            case NULLIF:
            case SIZE:
            case SQRT:
            case SUM:
            case VALUE:
            case IDENT:
            case LEFT_ROUND_BRACKET:
            case PLUS:
            case MINUS:
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt61=1;
                }
                break;
            case CONCAT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case FALSE:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case TRUE:
            case TYPE:
            case UPPER:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case DATE_LITERAL:
            case TIME_LITERAL:
            case TIMESTAMP_LITERAL:
                {
                alt61=2;
                }
                break;
            case ALL:
            case ANY:
            case SOME:
                {
                alt61=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("797:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression );", 61, 0, input);
            
                throw nvae;
            }
            
            switch (alt61) {
                case 1 :
                    // JPQL.g:799:7: n= arithmeticExpression
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4705);
                    n=arithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:800:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4719);
                    n=nonArithmeticScalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:801:7: n= anyOrAllExpression
                    {
                    pushFollow(FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4733);
                    n=anyOrAllExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end comparisonExpressionRightOperand

    
    // $ANTLR start arithmeticExpression
    // JPQL.g:804:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object arithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:806:7: (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt62=2;
            int LA62_0 = input.LA(1);
            
            if ( (LA62_0==ABS||LA62_0==AVG||(LA62_0>=CASE && LA62_0<=COALESCE)||LA62_0==COUNT||LA62_0==INDEX||LA62_0==KEY||LA62_0==LENGTH||LA62_0==LOCATE||LA62_0==MAX||(LA62_0>=MIN && LA62_0<=MOD)||LA62_0==NULLIF||(LA62_0>=SIZE && LA62_0<=SQRT)||LA62_0==SUM||LA62_0==VALUE||LA62_0==IDENT||(LA62_0>=PLUS && LA62_0<=MINUS)||(LA62_0>=INTEGER_LITERAL && LA62_0<=DOUBLE_LITERAL)||(LA62_0>=POSITIONAL_PARAM && LA62_0<=NAMED_PARAM)) ) {
                alt62=1;
            }
            else if ( (LA62_0==LEFT_ROUND_BRACKET) ) {
                int LA62_23 = input.LA(2);
                
                if ( (LA62_23==ABS||LA62_23==AVG||(LA62_23>=CASE && LA62_23<=COALESCE)||LA62_23==COUNT||LA62_23==INDEX||LA62_23==KEY||LA62_23==LENGTH||LA62_23==LOCATE||LA62_23==MAX||(LA62_23>=MIN && LA62_23<=MOD)||LA62_23==NULLIF||(LA62_23>=SIZE && LA62_23<=SQRT)||LA62_23==SUM||LA62_23==VALUE||LA62_23==IDENT||LA62_23==LEFT_ROUND_BRACKET||(LA62_23>=PLUS && LA62_23<=MINUS)||(LA62_23>=INTEGER_LITERAL && LA62_23<=DOUBLE_LITERAL)||(LA62_23>=POSITIONAL_PARAM && LA62_23<=NAMED_PARAM)) ) {
                    alt62=1;
                }
                else if ( (LA62_23==SELECT) ) {
                    alt62=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("804:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 62, 23, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("804:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 62, 0, input);
            
                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // JPQL.g:806:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4765);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:807:7: LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4775); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_arithmeticExpression4781);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4783); if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end arithmeticExpression

    
    // $ANTLR start simpleArithmeticExpression
    // JPQL.g:810:1: simpleArithmeticExpression returns [Object node] : n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* ;
    public final Object simpleArithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:814:7: (n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* )
            // JPQL.g:814:7: n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            {
            pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4815);
            n=arithmeticTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:815:9: (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            loop63:
            do {
                int alt63=3;
                int LA63_0 = input.LA(1);
                
                if ( (LA63_0==PLUS) ) {
                    alt63=1;
                }
                else if ( (LA63_0==MINUS) ) {
                    alt63=2;
                }
                
            
                switch (alt63) {
            	case 1 :
            	    // JPQL.g:815:11: p= PLUS right= arithmeticTerm
            	    {
            	    p=(Token)input.LT(1);
            	    match(input,PLUS,FOLLOW_PLUS_in_simpleArithmeticExpression4831); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4837);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newPlus(p.getLine(), p.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:817:11: m= MINUS right= arithmeticTerm
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MINUS,FOLLOW_MINUS_in_simpleArithmeticExpression4866); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4872);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMinus(m.getLine(), m.getCharPositionInLine(), node, right); 
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
    // $ANTLR end simpleArithmeticExpression

    
    // $ANTLR start arithmeticTerm
    // JPQL.g:822:1: arithmeticTerm returns [Object node] : n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* ;
    public final Object arithmeticTerm() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:826:7: (n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* )
            // JPQL.g:826:7: n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            {
            pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4929);
            n=arithmeticFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:827:9: (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            loop64:
            do {
                int alt64=3;
                int LA64_0 = input.LA(1);
                
                if ( (LA64_0==MULTIPLY) ) {
                    alt64=1;
                }
                else if ( (LA64_0==DIVIDE) ) {
                    alt64=2;
                }
                
            
                switch (alt64) {
            	case 1 :
            	    // JPQL.g:827:11: m= MULTIPLY right= arithmeticFactor
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MULTIPLY,FOLLOW_MULTIPLY_in_arithmeticTerm4945); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4951);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMultiply(m.getLine(), m.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:829:11: d= DIVIDE right= arithmeticFactor
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DIVIDE,FOLLOW_DIVIDE_in_arithmeticTerm4980); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4986);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newDivide(d.getLine(), d.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop64;
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
    // $ANTLR end arithmeticTerm

    
    // $ANTLR start arithmeticFactor
    // JPQL.g:834:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );
    public final Object arithmeticFactor() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:836:7: (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary )
            int alt65=3;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt65=1;
                }
                break;
            case MINUS:
                {
                alt65=2;
                }
                break;
            case ABS:
            case AVG:
            case CASE:
            case COALESCE:
            case COUNT:
            case INDEX:
            case KEY:
            case LENGTH:
            case LOCATE:
            case MAX:
            case MIN:
            case MOD:
            case NULLIF:
            case SIZE:
            case SQRT:
            case SUM:
            case VALUE:
            case IDENT:
            case LEFT_ROUND_BRACKET:
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt65=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("834:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );", 65, 0, input);
            
                throw nvae;
            }
            
            switch (alt65) {
                case 1 :
                    // JPQL.g:836:7: p= PLUS n= arithmeticPrimary
                    {
                    p=(Token)input.LT(1);
                    match(input,PLUS,FOLLOW_PLUS_in_arithmeticFactor5040); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5047);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newUnaryPlus(p.getLine(), p.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:838:7: m= MINUS n= arithmeticPrimary
                    {
                    m=(Token)input.LT(1);
                    match(input,MINUS,FOLLOW_MINUS_in_arithmeticFactor5069); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5075);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newUnaryMinus(m.getLine(), m.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:840:7: n= arithmeticPrimary
                    {
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5099);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end arithmeticFactor

    
    // $ANTLR start arithmeticPrimary
    // JPQL.g:843:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric );
    public final Object arithmeticPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:845:7: ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric )
            int alt66=7;
            switch ( input.LA(1) ) {
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt66=1;
                }
                break;
            case KEY:
            case VALUE:
            case IDENT:
                {
                alt66=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt66=3;
                }
                break;
            case CASE:
            case COALESCE:
            case NULLIF:
                {
                alt66=4;
                }
                break;
            case ABS:
            case INDEX:
            case LENGTH:
            case LOCATE:
            case MOD:
            case SIZE:
            case SQRT:
                {
                alt66=5;
                }
                break;
            case LEFT_ROUND_BRACKET:
                {
                alt66=6;
                }
                break;
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt66=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("843:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric );", 66, 0, input);
            
                throw nvae;
            }
            
            switch (alt66) {
                case 1 :
                    // JPQL.g:845:7: {...}?n= aggregateExpression
                    {
                    if ( !( aggregatesAllowed() ) ) {
                        if (backtracking>0) {failed=true; return node;}
                        throw new FailedPredicateException(input, "arithmeticPrimary", " aggregatesAllowed() ");
                    }
                    pushFollow(FOLLOW_aggregateExpression_in_arithmeticPrimary5133);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:846:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5147);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:847:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_arithmeticPrimary5161);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:848:7: n= caseExpression
                    {
                    pushFollow(FOLLOW_caseExpression_in_arithmeticPrimary5175);
                    n=caseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:849:7: n= functionsReturningNumerics
                    {
                    pushFollow(FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5189);
                    n=functionsReturningNumerics();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:850:7: LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5199); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5205);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5207); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 7 :
                    // JPQL.g:851:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_arithmeticPrimary5221);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end arithmeticPrimary

    
    // $ANTLR start scalarExpression
    // JPQL.g:854:1: scalarExpression returns [Object node] : (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression );
    public final Object scalarExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null; 
        try {
            // JPQL.g:856:7: (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression )
            int alt67=2;
            int LA67_0 = input.LA(1);
            
            if ( (LA67_0==ABS||LA67_0==AVG||(LA67_0>=CASE && LA67_0<=COALESCE)||LA67_0==COUNT||LA67_0==INDEX||LA67_0==KEY||LA67_0==LENGTH||LA67_0==LOCATE||LA67_0==MAX||(LA67_0>=MIN && LA67_0<=MOD)||LA67_0==NULLIF||(LA67_0>=SIZE && LA67_0<=SQRT)||LA67_0==SUM||LA67_0==VALUE||LA67_0==IDENT||LA67_0==LEFT_ROUND_BRACKET||(LA67_0>=PLUS && LA67_0<=MINUS)||(LA67_0>=INTEGER_LITERAL && LA67_0<=DOUBLE_LITERAL)||(LA67_0>=POSITIONAL_PARAM && LA67_0<=NAMED_PARAM)) ) {
                alt67=1;
            }
            else if ( (LA67_0==CONCAT||(LA67_0>=CURRENT_DATE && LA67_0<=CURRENT_TIMESTAMP)||LA67_0==FALSE||LA67_0==LOWER||LA67_0==SUBSTRING||(LA67_0>=TRIM && LA67_0<=TYPE)||LA67_0==UPPER||(LA67_0>=STRING_LITERAL_DOUBLE_QUOTED && LA67_0<=TIMESTAMP_LITERAL)) ) {
                alt67=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("854:1: scalarExpression returns [Object node] : (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression );", 67, 0, input);
            
                throw nvae;
            }
            switch (alt67) {
                case 1 :
                    // JPQL.g:856:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_scalarExpression5253);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:857:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5268);
                    n=nonArithmeticScalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end scalarExpression

    
    // $ANTLR start nonArithmeticScalarExpression
    // JPQL.g:860:1: nonArithmeticScalarExpression returns [Object node] : (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression );
    public final Object nonArithmeticScalarExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null; 
        try {
            // JPQL.g:862:7: (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression )
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
            case TIME_LITERAL:
            case TIMESTAMP_LITERAL:
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("860:1: nonArithmeticScalarExpression returns [Object node] : (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression );", 68, 0, input);
            
                throw nvae;
            }
            
            switch (alt68) {
                case 1 :
                    // JPQL.g:862:7: n= functionsReturningDatetime
                    {
                    pushFollow(FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5300);
                    n=functionsReturningDatetime();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:863:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5314);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:864:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_nonArithmeticScalarExpression5328);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:865:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5342);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:866:7: n= literalTemporal
                    {
                    pushFollow(FOLLOW_literalTemporal_in_nonArithmeticScalarExpression5356);
                    n=literalTemporal();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:867:7: n= entityTypeExpression
                    {
                    pushFollow(FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5370);
                    n=entityTypeExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end nonArithmeticScalarExpression

    
    // $ANTLR start anyOrAllExpression
    // JPQL.g:870:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object anyOrAllExpression() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token y=null;
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:872:7: (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("870:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 69, 0, input);
            
                throw nvae;
            }
            
            switch (alt69) {
                case 1 :
                    // JPQL.g:872:7: a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,ALL,FOLLOW_ALL_in_anyOrAllExpression5400); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5402); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5408);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5410); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAll(a.getLine(), a.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:874:7: y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    y=(Token)input.LT(1);
                    match(input,ANY,FOLLOW_ANY_in_anyOrAllExpression5430); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5432); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5438);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5440); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAny(y.getLine(), y.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:876:7: s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    s=(Token)input.LT(1);
                    match(input,SOME,FOLLOW_SOME_in_anyOrAllExpression5460); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5462); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5468);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5470); if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end anyOrAllExpression

    
    // $ANTLR start entityTypeExpression
    // JPQL.g:880:1: entityTypeExpression returns [Object node] : n= typeDiscriminator ;
    public final Object entityTypeExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:882:7: (n= typeDiscriminator )
            // JPQL.g:882:7: n= typeDiscriminator
            {
            pushFollow(FOLLOW_typeDiscriminator_in_entityTypeExpression5510);
            n=typeDiscriminator();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end entityTypeExpression

    
    // $ANTLR start typeDiscriminator
    // JPQL.g:885:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );
    public final Object typeDiscriminator() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token c=null;
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:887:7: (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET )
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
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("885:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 70, 2, input);
                    
                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("885:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 70, 1, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("885:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 70, 0, input);
            
                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // JPQL.g:887:7: a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5543); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5545); if (failed) return node;
                    pushFollow(FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5551);
                    n=variableOrSingleValuedPath();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5553); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newType(a.getLine(), a.getCharPositionInLine(), n);
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:888:7: c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET
                    {
                    c=(Token)input.LT(1);
                    match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5568); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5570); if (failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_typeDiscriminator5576);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5578); if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end typeDiscriminator

    
    // $ANTLR start caseExpression
    // JPQL.g:891:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );
    public final Object caseExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:893:6: (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression )
            int alt71=4;
            switch ( input.LA(1) ) {
            case CASE:
                {
                int LA71_1 = input.LA(2);
                
                if ( (LA71_1==KEY||LA71_1==TYPE||LA71_1==VALUE||LA71_1==IDENT) ) {
                    alt71=1;
                }
                else if ( (LA71_1==WHEN) ) {
                    alt71=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("891:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );", 71, 1, input);
                
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("891:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );", 71, 0, input);
            
                throw nvae;
            }
            
            switch (alt71) {
                case 1 :
                    // JPQL.g:893:6: n= simpleCaseExpression
                    {
                    pushFollow(FOLLOW_simpleCaseExpression_in_caseExpression5613);
                    n=simpleCaseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:894:6: n= generalCaseExpression
                    {
                    pushFollow(FOLLOW_generalCaseExpression_in_caseExpression5626);
                    n=generalCaseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:895:6: n= coalesceExpression
                    {
                    pushFollow(FOLLOW_coalesceExpression_in_caseExpression5639);
                    n=coalesceExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:896:6: n= nullIfExpression
                    {
                    pushFollow(FOLLOW_nullIfExpression_in_caseExpression5652);
                    n=nullIfExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end caseExpression

    protected static class simpleCaseExpression_scope {
        List whens;
    }
    protected Stack simpleCaseExpression_stack = new Stack();
    
    
    // $ANTLR start simpleCaseExpression
    // JPQL.g:899:1: simpleCaseExpression returns [Object node] : a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END ;
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
            // JPQL.g:907:6: (a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END )
            // JPQL.g:907:6: a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END
            {
            a=(Token)input.LT(1);
            match(input,CASE,FOLLOW_CASE_in_simpleCaseExpression5690); if (failed) return node;
            pushFollow(FOLLOW_caseOperand_in_simpleCaseExpression5696);
            c=caseOperand();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5702);
            w=simpleWhenClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens.add(w);
            }
            // JPQL.g:907:97: (w= simpleWhenClause )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);
                
                if ( (LA72_0==WHEN) ) {
                    alt72=1;
                }
                
            
                switch (alt72) {
            	case 1 :
            	    // JPQL.g:907:98: w= simpleWhenClause
            	    {
            	    pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5711);
            	    w=simpleWhenClause();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens.add(w);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop72;
                }
            } while (true);

            match(input,ELSE,FOLLOW_ELSE_in_simpleCaseExpression5717); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleCaseExpression5723);
            e=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,END,FOLLOW_END_in_simpleCaseExpression5725); if (failed) return node;
            if ( backtracking==0 ) {
              
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
    // $ANTLR end simpleCaseExpression

    protected static class generalCaseExpression_scope {
        List whens;
    }
    protected Stack generalCaseExpression_stack = new Stack();
    
    
    // $ANTLR start generalCaseExpression
    // JPQL.g:914:1: generalCaseExpression returns [Object node] : a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END ;
    public final Object generalCaseExpression() throws RecognitionException {
        generalCaseExpression_stack.push(new generalCaseExpression_scope());

        Object node = null;
    
        Token a=null;
        Object w = null;

        Object e = null;
        
    
        
            node = null;
            ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens = new ArrayList();
    
        try {
            // JPQL.g:922:6: (a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END )
            // JPQL.g:922:6: a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END
            {
            a=(Token)input.LT(1);
            match(input,CASE,FOLLOW_CASE_in_generalCaseExpression5769); if (failed) return node;
            pushFollow(FOLLOW_whenClause_in_generalCaseExpression5775);
            w=whenClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens.add(w);
            }
            // JPQL.g:922:76: (w= whenClause )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);
                
                if ( (LA73_0==WHEN) ) {
                    alt73=1;
                }
                
            
                switch (alt73) {
            	case 1 :
            	    // JPQL.g:922:77: w= whenClause
            	    {
            	    pushFollow(FOLLOW_whenClause_in_generalCaseExpression5784);
            	    w=whenClause();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens.add(w);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop73;
                }
            } while (true);

            match(input,ELSE,FOLLOW_ELSE_in_generalCaseExpression5790); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_generalCaseExpression5796);
            e=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,END,FOLLOW_END_in_generalCaseExpression5798); if (failed) return node;
            if ( backtracking==0 ) {
              
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
    // $ANTLR end generalCaseExpression

    protected static class coalesceExpression_scope {
        List primaries;
    }
    protected Stack coalesceExpression_stack = new Stack();
    
    
    // $ANTLR start coalesceExpression
    // JPQL.g:929:1: coalesceExpression returns [Object node] : c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET ;
    public final Object coalesceExpression() throws RecognitionException {
        coalesceExpression_stack.push(new coalesceExpression_scope());

        Object node = null;
    
        Token c=null;
        Object p = null;

        Object s = null;
        
    
        
            node = null;
            ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries = new ArrayList();
    
        try {
            // JPQL.g:937:6: (c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET )
            // JPQL.g:937:6: c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET
            {
            c=(Token)input.LT(1);
            match(input,COALESCE,FOLLOW_COALESCE_in_coalesceExpression5842); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5844); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5850);
            p=scalarExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries.add(p);
            }
            // JPQL.g:937:106: ( COMMA s= scalarExpression )+
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
            	    // JPQL.g:937:107: COMMA s= scalarExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_coalesceExpression5855); if (failed) return node;
            	    pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5861);
            	    s=scalarExpression();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries.add(s);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt74 >= 1 ) break loop74;
            	    if (backtracking>0) {failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(74, input);
                        throw eee;
                }
                cnt74++;
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5867); if (failed) return node;
            if ( backtracking==0 ) {
              
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
    // $ANTLR end coalesceExpression

    
    // $ANTLR start nullIfExpression
    // JPQL.g:944:1: nullIfExpression returns [Object node] : n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object nullIfExpression() throws RecognitionException {

        Object node = null;
    
        Token n=null;
        Object l = null;

        Object r = null;
        
    
        node = null;
        try {
            // JPQL.g:946:6: (n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:946:6: n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET
            {
            n=(Token)input.LT(1);
            match(input,NULLIF,FOLLOW_NULLIF_in_nullIfExpression5908); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5910); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5916);
            l=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_nullIfExpression5918); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5924);
            r=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression5926); if (failed) return node;
            if ( backtracking==0 ) {
              
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
    // $ANTLR end nullIfExpression

    
    // $ANTLR start caseOperand
    // JPQL.g:954:1: caseOperand returns [Object node] : (n= stateFieldPathExpression | n= typeDiscriminator );
    public final Object caseOperand() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:956:6: (n= stateFieldPathExpression | n= typeDiscriminator )
            int alt75=2;
            int LA75_0 = input.LA(1);
            
            if ( (LA75_0==KEY||LA75_0==VALUE||LA75_0==IDENT) ) {
                alt75=1;
            }
            else if ( (LA75_0==TYPE) ) {
                alt75=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("954:1: caseOperand returns [Object node] : (n= stateFieldPathExpression | n= typeDiscriminator );", 75, 0, input);
            
                throw nvae;
            }
            switch (alt75) {
                case 1 :
                    // JPQL.g:956:6: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_caseOperand5973);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:957:6: n= typeDiscriminator
                    {
                    pushFollow(FOLLOW_typeDiscriminator_in_caseOperand5987);
                    n=typeDiscriminator();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end caseOperand

    
    // $ANTLR start whenClause
    // JPQL.g:960:1: whenClause returns [Object node] : w= WHEN c= conditionalExpression THEN a= scalarExpression ;
    public final Object whenClause() throws RecognitionException {

        Object node = null;
    
        Token w=null;
        Object c = null;

        Object a = null;
        
    
        node = null;
        try {
            // JPQL.g:962:6: (w= WHEN c= conditionalExpression THEN a= scalarExpression )
            // JPQL.g:962:6: w= WHEN c= conditionalExpression THEN a= scalarExpression
            {
            w=(Token)input.LT(1);
            match(input,WHEN,FOLLOW_WHEN_in_whenClause6022); if (failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whenClause6028);
            c=conditionalExpression();
            _fsp--;
            if (failed) return node;
            match(input,THEN,FOLLOW_THEN_in_whenClause6030); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_whenClause6036);
            a=scalarExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              
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
    // $ANTLR end whenClause

    
    // $ANTLR start simpleWhenClause
    // JPQL.g:969:1: simpleWhenClause returns [Object node] : w= WHEN c= scalarExpression THEN a= scalarExpression ;
    public final Object simpleWhenClause() throws RecognitionException {

        Object node = null;
    
        Token w=null;
        Object c = null;

        Object a = null;
        
    
        node = null;
        try {
            // JPQL.g:971:6: (w= WHEN c= scalarExpression THEN a= scalarExpression )
            // JPQL.g:971:6: w= WHEN c= scalarExpression THEN a= scalarExpression
            {
            w=(Token)input.LT(1);
            match(input,WHEN,FOLLOW_WHEN_in_simpleWhenClause6078); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause6084);
            c=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,THEN,FOLLOW_THEN_in_simpleWhenClause6086); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause6092);
            a=scalarExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              
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
    // $ANTLR end simpleWhenClause

    
    // $ANTLR start variableOrSingleValuedPath
    // JPQL.g:978:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );
    public final Object variableOrSingleValuedPath() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:980:7: (n= singleValuedPathExpression | n= variableAccessOrTypeConstant )
            int alt76=2;
            int LA76_0 = input.LA(1);
            
            if ( (LA76_0==IDENT) ) {
                int LA76_1 = input.LA(2);
                
                if ( (LA76_1==RIGHT_ROUND_BRACKET) ) {
                    alt76=2;
                }
                else if ( (LA76_1==DOT) ) {
                    alt76=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("978:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );", 76, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA76_0==KEY||LA76_0==VALUE) ) {
                alt76=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("978:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );", 76, 0, input);
            
                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // JPQL.g:980:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6129);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:981:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6143);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end variableOrSingleValuedPath

    
    // $ANTLR start stringPrimary
    // JPQL.g:984:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );
    public final Object stringPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:986:7: (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression )
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
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt77=3;
                }
                break;
            case KEY:
            case VALUE:
            case IDENT:
                {
                alt77=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("984:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );", 77, 0, input);
            
                throw nvae;
            }
            
            switch (alt77) {
                case 1 :
                    // JPQL.g:986:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_stringPrimary6175);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:987:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_stringPrimary6189);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:988:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_stringPrimary6203);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:989:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_stringPrimary6217);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end stringPrimary

    
    // $ANTLR start literal
    // JPQL.g:994:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );
    public final Object literal() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:996:7: (n= literalNumeric | n= literalBoolean | n= literalString )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("994:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );", 78, 0, input);
            
                throw nvae;
            }
            
            switch (alt78) {
                case 1 :
                    // JPQL.g:996:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_literal6251);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:997:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_literal6265);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:998:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_literal6279);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end literal

    
    // $ANTLR start literalNumeric
    // JPQL.g:1001:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );
    public final Object literalNumeric() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token l=null;
        Token f=null;
        Token d=null;
    
         node = null; 
        try {
            // JPQL.g:1003:7: (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1001:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );", 79, 0, input);
            
                throw nvae;
            }
            
            switch (alt79) {
                case 1 :
                    // JPQL.g:1003:7: i= INTEGER_LITERAL
                    {
                    i=(Token)input.LT(1);
                    match(input,INTEGER_LITERAL,FOLLOW_INTEGER_LITERAL_in_literalNumeric6309); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newIntegerLiteral(i.getLine(), i.getCharPositionInLine(), 
                                                                   Integer.valueOf(i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1008:7: l= LONG_LITERAL
                    {
                    l=(Token)input.LT(1);
                    match(input,LONG_LITERAL,FOLLOW_LONG_LITERAL_in_literalNumeric6325); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  String text = l.getText();
                                  // skip the tailing 'l'
                                  text = text.substring(0, text.length() - 1);
                                  node = factory.newLongLiteral(l.getLine(), l.getCharPositionInLine(), 
                                                                Long.valueOf(text)); 
                              
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1016:7: f= FLOAT_LITERAL
                    {
                    f=(Token)input.LT(1);
                    match(input,FLOAT_LITERAL,FOLLOW_FLOAT_LITERAL_in_literalNumeric6346); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newFloatLiteral(f.getLine(), f.getCharPositionInLine(),
                                                                 Float.valueOf(f.getText()));
                              
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1021:7: d= DOUBLE_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,DOUBLE_LITERAL,FOLLOW_DOUBLE_LITERAL_in_literalNumeric6366); if (failed) return node;
                    if ( backtracking==0 ) {
                       
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
    // $ANTLR end literalNumeric

    
    // $ANTLR start literalBoolean
    // JPQL.g:1028:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );
    public final Object literalBoolean() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token f=null;
    
         node = null; 
        try {
            // JPQL.g:1030:7: (t= TRUE | f= FALSE )
            int alt80=2;
            int LA80_0 = input.LA(1);
            
            if ( (LA80_0==TRUE) ) {
                alt80=1;
            }
            else if ( (LA80_0==FALSE) ) {
                alt80=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1028:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );", 80, 0, input);
            
                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // JPQL.g:1030:7: t= TRUE
                    {
                    t=(Token)input.LT(1);
                    match(input,TRUE,FOLLOW_TRUE_in_literalBoolean6404); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newBooleanLiteral(t.getLine(), t.getCharPositionInLine(), Boolean.TRUE); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1032:7: f= FALSE
                    {
                    f=(Token)input.LT(1);
                    match(input,FALSE,FOLLOW_FALSE_in_literalBoolean6426); if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end literalBoolean

    
    // $ANTLR start literalString
    // JPQL.g:1036:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );
    public final Object literalString() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token s=null;
    
         node = null; 
        try {
            // JPQL.g:1038:7: (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED )
            int alt81=2;
            int LA81_0 = input.LA(1);
            
            if ( (LA81_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                alt81=1;
            }
            else if ( (LA81_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                alt81=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1036:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );", 81, 0, input);
            
                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // JPQL.g:1038:7: d= STRING_LITERAL_DOUBLE_QUOTED
                    {
                    d=(Token)input.LT(1);
                    match(input,STRING_LITERAL_DOUBLE_QUOTED,FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6465); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newStringLiteral(d.getLine(), d.getCharPositionInLine(), 
                                                                  convertStringLiteral(d.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1043:7: s= STRING_LITERAL_SINGLE_QUOTED
                    {
                    s=(Token)input.LT(1);
                    match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6486); if (failed) return node;
                    if ( backtracking==0 ) {
                       
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
    // $ANTLR end literalString

    
    // $ANTLR start literalTemporal
    // JPQL.g:1050:1: literalTemporal returns [Object node] : (d= DATE_LITERAL | d= TIME_LITERAL | d= TIMESTAMP_LITERAL );
    public final Object literalTemporal() throws RecognitionException {

        Object node = null;
    
        Token d=null;
    
         node = null; 
        try {
            // JPQL.g:1052:7: (d= DATE_LITERAL | d= TIME_LITERAL | d= TIMESTAMP_LITERAL )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1050:1: literalTemporal returns [Object node] : (d= DATE_LITERAL | d= TIME_LITERAL | d= TIMESTAMP_LITERAL );", 82, 0, input);
            
                throw nvae;
            }
            
            switch (alt82) {
                case 1 :
                    // JPQL.g:1052:7: d= DATE_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,DATE_LITERAL,FOLLOW_DATE_LITERAL_in_literalTemporal6526); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newDateLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1053:7: d= TIME_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,TIME_LITERAL,FOLLOW_TIME_LITERAL_in_literalTemporal6540); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newTimeLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1054:7: d= TIMESTAMP_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,TIMESTAMP_LITERAL,FOLLOW_TIMESTAMP_LITERAL_in_literalTemporal6554); if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end literalTemporal

    
    // $ANTLR start inputParameter
    // JPQL.g:1057:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );
    public final Object inputParameter() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token n=null;
    
         node = null; 
        try {
            // JPQL.g:1059:7: (p= POSITIONAL_PARAM | n= NAMED_PARAM )
            int alt83=2;
            int LA83_0 = input.LA(1);
            
            if ( (LA83_0==POSITIONAL_PARAM) ) {
                alt83=1;
            }
            else if ( (LA83_0==NAMED_PARAM) ) {
                alt83=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1057:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );", 83, 0, input);
            
                throw nvae;
            }
            switch (alt83) {
                case 1 :
                    // JPQL.g:1059:7: p= POSITIONAL_PARAM
                    {
                    p=(Token)input.LT(1);
                    match(input,POSITIONAL_PARAM,FOLLOW_POSITIONAL_PARAM_in_inputParameter6584); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  // skip the leading ?
                                  String text = p.getText().substring(1);
                                  node = factory.newPositionalParameter(p.getLine(), p.getCharPositionInLine(), text); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1065:7: n= NAMED_PARAM
                    {
                    n=(Token)input.LT(1);
                    match(input,NAMED_PARAM,FOLLOW_NAMED_PARAM_in_inputParameter6604); if (failed) return node;
                    if ( backtracking==0 ) {
                       
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
    // $ANTLR end inputParameter

    
    // $ANTLR start functionsReturningNumerics
    // JPQL.g:1073:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index );
    public final Object functionsReturningNumerics() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1075:7: (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index )
            int alt84=7;
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
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1073:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index );", 84, 0, input);
            
                throw nvae;
            }
            
            switch (alt84) {
                case 1 :
                    // JPQL.g:1075:7: n= abs
                    {
                    pushFollow(FOLLOW_abs_in_functionsReturningNumerics6644);
                    n=abs();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1076:7: n= length
                    {
                    pushFollow(FOLLOW_length_in_functionsReturningNumerics6658);
                    n=length();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1077:7: n= mod
                    {
                    pushFollow(FOLLOW_mod_in_functionsReturningNumerics6672);
                    n=mod();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1078:7: n= sqrt
                    {
                    pushFollow(FOLLOW_sqrt_in_functionsReturningNumerics6686);
                    n=sqrt();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:1079:7: n= locate
                    {
                    pushFollow(FOLLOW_locate_in_functionsReturningNumerics6700);
                    n=locate();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:1080:7: n= size
                    {
                    pushFollow(FOLLOW_size_in_functionsReturningNumerics6714);
                    n=size();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 7 :
                    // JPQL.g:1081:7: n= index
                    {
                    pushFollow(FOLLOW_index_in_functionsReturningNumerics6728);
                    n=index();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end functionsReturningNumerics

    
    // $ANTLR start functionsReturningDatetime
    // JPQL.g:1084:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );
    public final Object functionsReturningDatetime() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token t=null;
        Token ts=null;
    
         node = null; 
        try {
            // JPQL.g:1086:7: (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1084:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );", 85, 0, input);
            
                throw nvae;
            }
            
            switch (alt85) {
                case 1 :
                    // JPQL.g:1086:7: d= CURRENT_DATE
                    {
                    d=(Token)input.LT(1);
                    match(input,CURRENT_DATE,FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6758); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentDate(d.getLine(), d.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1088:7: t= CURRENT_TIME
                    {
                    t=(Token)input.LT(1);
                    match(input,CURRENT_TIME,FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6779); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentTime(t.getLine(), t.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1090:7: ts= CURRENT_TIMESTAMP
                    {
                    ts=(Token)input.LT(1);
                    match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6799); if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end functionsReturningDatetime

    
    // $ANTLR start functionsReturningStrings
    // JPQL.g:1094:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );
    public final Object functionsReturningStrings() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1096:7: (n= concat | n= substring | n= trim | n= upper | n= lower )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1094:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );", 86, 0, input);
            
                throw nvae;
            }
            
            switch (alt86) {
                case 1 :
                    // JPQL.g:1096:7: n= concat
                    {
                    pushFollow(FOLLOW_concat_in_functionsReturningStrings6839);
                    n=concat();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1097:7: n= substring
                    {
                    pushFollow(FOLLOW_substring_in_functionsReturningStrings6853);
                    n=substring();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1098:7: n= trim
                    {
                    pushFollow(FOLLOW_trim_in_functionsReturningStrings6867);
                    n=trim();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1099:7: n= upper
                    {
                    pushFollow(FOLLOW_upper_in_functionsReturningStrings6881);
                    n=upper();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:1100:7: n= lower
                    {
                    pushFollow(FOLLOW_lower_in_functionsReturningStrings6895);
                    n=lower();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end functionsReturningStrings

    protected static class concat_scope {
        List items;
    }
    protected Stack concat_stack = new Stack();
    
    
    // $ANTLR start concat
    // JPQL.g:1104:1: concat returns [Object node] : c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary ( COMMA arg= stringPrimary )+ RIGHT_ROUND_BRACKET ;
    public final Object concat() throws RecognitionException {
        concat_stack.push(new concat_scope());

        Object node = null;
    
        Token c=null;
        Object firstArg = null;

        Object arg = null;
        
    
         
            node = null;
            ((concat_scope)concat_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1112:7: (c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary ( COMMA arg= stringPrimary )+ RIGHT_ROUND_BRACKET )
            // JPQL.g:1112:7: c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary ( COMMA arg= stringPrimary )+ RIGHT_ROUND_BRACKET
            {
            c=(Token)input.LT(1);
            match(input,CONCAT,FOLLOW_CONCAT_in_concat6930); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_concat6941); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_concat6956);
            firstArg=stringPrimary();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((concat_scope)concat_stack.peek()).items.add(firstArg);
            }
            // JPQL.g:1114:72: ( COMMA arg= stringPrimary )+
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
            	    // JPQL.g:1114:73: COMMA arg= stringPrimary
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_concat6961); if (failed) return node;
            	    pushFollow(FOLLOW_stringPrimary_in_concat6967);
            	    arg=stringPrimary();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((concat_scope)concat_stack.peek()).items.add(arg);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt87 >= 1 ) break loop87;
            	    if (backtracking>0) {failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(87, input);
                        throw eee;
                }
                cnt87++;
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_concat6981); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end concat

    
    // $ANTLR start substring
    // JPQL.g:1119:1: substring returns [Object node] : s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression ( COMMA lengthNode= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET ;
    public final Object substring() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object string = null;

        Object start = null;

        Object lengthNode = null;
        
    
         
            node = null;
            lengthNode = null;
    
        try {
            // JPQL.g:1124:7: (s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression ( COMMA lengthNode= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET )
            // JPQL.g:1124:7: s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression ( COMMA lengthNode= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SUBSTRING,FOLLOW_SUBSTRING_in_substring7019); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_substring7032); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_substring7046);
            string=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring7048); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_substring7062);
            start=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1128:9: ( COMMA lengthNode= simpleArithmeticExpression )?
            int alt88=2;
            int LA88_0 = input.LA(1);
            
            if ( (LA88_0==COMMA) ) {
                alt88=1;
            }
            switch (alt88) {
                case 1 :
                    // JPQL.g:1128:10: COMMA lengthNode= simpleArithmeticExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_substring7073); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_substring7079);
                    lengthNode=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_substring7091); if (failed) return node;
            if ( backtracking==0 ) {
               
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
    // $ANTLR end substring

    
    // $ANTLR start trim
    // JPQL.g:1141:1: trim returns [Object node] : t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object trim() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        TrimSpecification trimSpecIndicator = null;

        Object trimCharNode = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1145:7: (t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1145:7: t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,TRIM,FOLLOW_TRIM_in_trim7129); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_trim7139); if (failed) return node;
            // JPQL.g:1147:9: ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )?
            int alt89=2;
            int LA89_0 = input.LA(1);
            
            if ( (LA89_0==LEADING) && (synpred2())) {
                alt89=1;
            }
            else if ( (LA89_0==TRAILING) && (synpred2())) {
                alt89=1;
            }
            else if ( (LA89_0==BOTH) && (synpred2())) {
                alt89=1;
            }
            else if ( (LA89_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                int LA89_4 = input.LA(2);
                
                if ( (LA89_4==FROM) && (synpred2())) {
                    alt89=1;
                }
            }
            else if ( (LA89_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                int LA89_5 = input.LA(2);
                
                if ( (LA89_5==FROM) && (synpred2())) {
                    alt89=1;
                }
            }
            else if ( (LA89_0==POSITIONAL_PARAM) ) {
                int LA89_6 = input.LA(2);
                
                if ( (LA89_6==FROM) && (synpred2())) {
                    alt89=1;
                }
            }
            else if ( (LA89_0==NAMED_PARAM) ) {
                int LA89_7 = input.LA(2);
                
                if ( (LA89_7==FROM) && (synpred2())) {
                    alt89=1;
                }
            }
            else if ( (LA89_0==FROM) && (synpred2())) {
                alt89=1;
            }
            switch (alt89) {
                case 1 :
                    // JPQL.g:1147:11: ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM
                    {
                    pushFollow(FOLLOW_trimSpec_in_trim7167);
                    trimSpecIndicator=trimSpec();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_trimChar_in_trim7173);
                    trimCharNode=trimChar();
                    _fsp--;
                    if (failed) return node;
                    match(input,FROM,FOLLOW_FROM_in_trim7175); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_stringPrimary_in_trim7193);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_trim7203); if (failed) return node;
            if ( backtracking==0 ) {
              
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
    // $ANTLR end trim

    
    // $ANTLR start trimSpec
    // JPQL.g:1156:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );
    public final TrimSpecification trimSpec() throws RecognitionException {

        TrimSpecification trimSpec = null;
    
         trimSpec = TrimSpecification.BOTH; 
        try {
            // JPQL.g:1158:7: ( LEADING | TRAILING | BOTH | )
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
                if (backtracking>0) {failed=true; return trimSpec;}
                NoViableAltException nvae =
                    new NoViableAltException("1156:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );", 90, 0, input);
            
                throw nvae;
            }
            
            switch (alt90) {
                case 1 :
                    // JPQL.g:1158:7: LEADING
                    {
                    match(input,LEADING,FOLLOW_LEADING_in_trimSpec7239); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.LEADING; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1160:7: TRAILING
                    {
                    match(input,TRAILING,FOLLOW_TRAILING_in_trimSpec7257); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.TRAILING; 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1162:7: BOTH
                    {
                    match(input,BOTH,FOLLOW_BOTH_in_trimSpec7275); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.BOTH; 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1165:5: 
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
    // $ANTLR end trimSpec

    
    // $ANTLR start trimChar
    // JPQL.g:1167:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );
    public final Object trimChar() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1169:7: (n= literalString | n= inputParameter | )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1167:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );", 91, 0, input);
            
                throw nvae;
            }
            
            switch (alt91) {
                case 1 :
                    // JPQL.g:1169:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_trimChar7322);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1170:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_trimChar7336);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1172:5: 
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
    // $ANTLR end trimChar

    
    // $ANTLR start upper
    // JPQL.g:1174:1: upper returns [Object node] : u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object upper() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1176:7: (u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1176:7: u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            u=(Token)input.LT(1);
            match(input,UPPER,FOLLOW_UPPER_in_upper7373); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_upper7375); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_upper7381);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_upper7383); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end upper

    
    // $ANTLR start lower
    // JPQL.g:1180:1: lower returns [Object node] : l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object lower() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1182:7: (l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1182:7: l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOWER,FOLLOW_LOWER_in_lower7421); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_lower7423); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_lower7429);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_lower7431); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end lower

    
    // $ANTLR start abs
    // JPQL.g:1187:1: abs returns [Object node] : a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object abs() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1189:7: (a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1189:7: a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            a=(Token)input.LT(1);
            match(input,ABS,FOLLOW_ABS_in_abs7470); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_abs7472); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_abs7478);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_abs7480); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end abs

    
    // $ANTLR start length
    // JPQL.g:1193:1: length returns [Object node] : l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object length() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1195:7: (l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1195:7: l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LENGTH,FOLLOW_LENGTH_in_length7518); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_length7520); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_length7526);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_length7528); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end length

    
    // $ANTLR start locate
    // JPQL.g:1199:1: locate returns [Object node] : l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET ;
    public final Object locate() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object pattern = null;

        Object n = null;

        Object startPos = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1203:7: (l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET )
            // JPQL.g:1203:7: l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOCATE,FOLLOW_LOCATE_in_locate7566); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_locate7576); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_locate7591);
            pattern=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_locate7593); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_locate7599);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1206:9: ( COMMA startPos= simpleArithmeticExpression )?
            int alt92=2;
            int LA92_0 = input.LA(1);
            
            if ( (LA92_0==COMMA) ) {
                alt92=1;
            }
            switch (alt92) {
                case 1 :
                    // JPQL.g:1206:11: COMMA startPos= simpleArithmeticExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_locate7611); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_locate7617);
                    startPos=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_locate7630); if (failed) return node;
            if ( backtracking==0 ) {
               
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
    // $ANTLR end locate

    
    // $ANTLR start size
    // JPQL.g:1214:1: size returns [Object node] : s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ;
    public final Object size() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1216:7: (s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1216:7: s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SIZE,FOLLOW_SIZE_in_size7668); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_size7679); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_size7685);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_size7687); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end size

    
    // $ANTLR start mod
    // JPQL.g:1221:1: mod returns [Object node] : m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object mod() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Object left = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1225:7: (m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1225:7: m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            m=(Token)input.LT(1);
            match(input,MOD,FOLLOW_MOD_in_mod7725); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mod7727); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod7741);
            left=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_mod7743); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod7758);
            right=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mod7768); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end mod

    
    // $ANTLR start sqrt
    // JPQL.g:1232:1: sqrt returns [Object node] : s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object sqrt() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1234:7: (s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1234:7: s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SQRT,FOLLOW_SQRT_in_sqrt7806); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7817); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_sqrt7823);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7825); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end sqrt

    
    // $ANTLR start index
    // JPQL.g:1239:1: index returns [Object node] : s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object index() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1241:7: (s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // JPQL.g:1241:7: s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,INDEX,FOLLOW_INDEX_in_index7867); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_index7869); if (failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_index7875);
            n=variableAccessOrTypeConstant();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_index7877); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end index

    
    // $ANTLR start subquery
    // JPQL.g:1245:1: subquery returns [Object node] : select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? ;
    public final Object subquery() throws RecognitionException {

        Object node = null;
    
        Object select = null;

        Object from = null;

        Object where = null;

        Object groupBy = null;

        Object having = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1249:7: (select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? )
            // JPQL.g:1249:7: select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )?
            {
            pushFollow(FOLLOW_simpleSelectClause_in_subquery7918);
            select=simpleSelectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_subqueryFromClause_in_subquery7933);
            from=subqueryFromClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1251:7: (where= whereClause )?
            int alt93=2;
            int LA93_0 = input.LA(1);
            
            if ( (LA93_0==WHERE) ) {
                alt93=1;
            }
            switch (alt93) {
                case 1 :
                    // JPQL.g:1251:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_subquery7948);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:1252:7: (groupBy= groupByClause )?
            int alt94=2;
            int LA94_0 = input.LA(1);
            
            if ( (LA94_0==GROUP) ) {
                alt94=1;
            }
            switch (alt94) {
                case 1 :
                    // JPQL.g:1252:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_subquery7963);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:1253:7: (having= havingClause )?
            int alt95=2;
            int LA95_0 = input.LA(1);
            
            if ( (LA95_0==HAVING) ) {
                alt95=1;
            }
            switch (alt95) {
                case 1 :
                    // JPQL.g:1253:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_subquery7979);
                    having=havingClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            if ( backtracking==0 ) {
               
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
    // $ANTLR end subquery

    protected static class simpleSelectClause_scope {
        boolean distinct;
    }
    protected Stack simpleSelectClause_stack = new Stack();
    
    
    // $ANTLR start simpleSelectClause
    // JPQL.g:1260:1: simpleSelectClause returns [Object node] : s= SELECT ( DISTINCT )? n= simpleSelectExpression ;
    public final Object simpleSelectClause() throws RecognitionException {
        simpleSelectClause_stack.push(new simpleSelectClause_scope());

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         
            node = null; 
            ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = false;
    
        try {
            // JPQL.g:1268:7: (s= SELECT ( DISTINCT )? n= simpleSelectExpression )
            // JPQL.g:1268:7: s= SELECT ( DISTINCT )? n= simpleSelectExpression
            {
            s=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_simpleSelectClause8022); if (failed) return node;
            // JPQL.g:1268:16: ( DISTINCT )?
            int alt96=2;
            int LA96_0 = input.LA(1);
            
            if ( (LA96_0==DISTINCT) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // JPQL.g:1268:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_simpleSelectClause8025); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_simpleSelectExpression_in_simpleSelectClause8041);
            n=simpleSelectExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              
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
    // $ANTLR end simpleSelectClause

    
    // $ANTLR start simpleSelectExpression
    // JPQL.g:1278:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );
    public final Object simpleSelectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1280:7: (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant )
            int alt97=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA97_1 = input.LA(2);
                
                if ( (LA97_1==FROM) ) {
                    alt97=3;
                }
                else if ( (LA97_1==DOT) ) {
                    alt97=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1278:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );", 97, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case KEY:
            case VALUE:
                {
                alt97=1;
                }
                break;
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt97=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1278:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );", 97, 0, input);
            
                throw nvae;
            }
            
            switch (alt97) {
                case 1 :
                    // JPQL.g:1280:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_simpleSelectExpression8081);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1281:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_simpleSelectExpression8096);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1282:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression8111);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end simpleSelectExpression

    protected static class subqueryFromClause_scope {
        List varDecls;
    }
    protected Stack subqueryFromClause_stack = new Stack();
    
    
    // $ANTLR start subqueryFromClause
    // JPQL.g:1286:1: subqueryFromClause returns [Object node] : f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )* ;
    public final Object subqueryFromClause() throws RecognitionException {
        subqueryFromClause_stack.push(new subqueryFromClause_scope());

        Object node = null;
    
        Token f=null;
        Object c = null;
        
    
         
            node = null; 
            ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g:1294:7: (f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )* )
            // JPQL.g:1294:7: f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )*
            {
            f=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_subqueryFromClause8146); if (failed) return node;
            pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8148);
            subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g:1295:9: ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )*
            loop98:
            do {
                int alt98=3;
                int LA98_0 = input.LA(1);
                
                if ( (LA98_0==COMMA) ) {
                    alt98=1;
                }
                else if ( (LA98_0==IN) ) {
                    alt98=2;
                }
                
            
                switch (alt98) {
            	case 1 :
            	    // JPQL.g:1296:13: COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_subqueryFromClause8175); if (failed) return node;
            	    pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8194);
            	    subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            	    _fsp--;
            	    if (failed) return node;
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:1298:19: c= collectionMemberDeclaration
            	    {
            	    pushFollow(FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8220);
            	    c=collectionMemberDeclaration();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls.add(c);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop98;
                }
            } while (true);

            if ( backtracking==0 ) {
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
    // $ANTLR end subqueryFromClause

    
    // $ANTLR start subselectIdentificationVariableDeclaration
    // JPQL.g:1303:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );
    public final void subselectIdentificationVariableDeclaration(List varDecls) throws RecognitionException {
        Token i=null;
        Object n = null;
        
    
         Object node; 
        try {
            // JPQL.g:1305:7: ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration )
            int alt101=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA101_1 = input.LA(2);
                
                if ( (LA101_1==AS||LA101_1==IDENT) ) {
                    alt101=1;
                }
                else if ( (LA101_1==DOT) ) {
                    alt101=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1303:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 101, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case KEY:
                {
                int LA101_2 = input.LA(2);
                
                if ( (LA101_2==LEFT_ROUND_BRACKET) ) {
                    alt101=2;
                }
                else if ( (LA101_2==AS||LA101_2==IDENT) ) {
                    alt101=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1303:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 101, 2, input);
                
                    throw nvae;
                }
                }
                break;
            case VALUE:
                {
                int LA101_3 = input.LA(2);
                
                if ( (LA101_3==LEFT_ROUND_BRACKET) ) {
                    alt101=2;
                }
                else if ( (LA101_3==AS||LA101_3==IDENT) ) {
                    alt101=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1303:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 101, 3, input);
                
                    throw nvae;
                }
                }
                break;
            case IN:
                {
                int LA101_4 = input.LA(2);
                
                if ( (LA101_4==LEFT_ROUND_BRACKET) ) {
                    alt101=3;
                }
                else if ( (LA101_4==AS||LA101_4==IDENT) ) {
                    alt101=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1303:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 101, 4, input);
                
                    throw nvae;
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
            case CONCAT:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DESC:
            case DELETE:
            case DISTINCT:
            case ELSE:
            case EMPTY:
            case END:
            case ENTRY:
            case ESCAPE:
            case EXISTS:
            case FALSE:
            case FETCH:
            case FROM:
            case GROUP:
            case HAVING:
            case INDEX:
            case INNER:
            case IS:
            case JOIN:
            case LEADING:
            case LEFT:
            case LENGTH:
            case LIKE:
            case LOCATE:
            case LOWER:
            case MAX:
            case MEMBER:
            case MIN:
            case MOD:
            case NEW:
            case NOT:
            case NULL:
            case NULLIF:
            case OBJECT:
            case OF:
            case OR:
            case ORDER:
            case OUTER:
            case SELECT:
            case SET:
            case SIZE:
            case SQRT:
            case SOME:
            case SUBSTRING:
            case SUM:
            case THEN:
            case TRAILING:
            case TRIM:
            case TRUE:
            case TYPE:
            case UNKNOWN:
            case UPDATE:
            case UPPER:
            case WHEN:
            case WHERE:
            case COMMA:
            case EQUALS:
            case LEFT_ROUND_BRACKET:
            case RIGHT_ROUND_BRACKET:
            case DOT:
            case NOT_EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL_TO:
            case LESS_THAN:
            case LESS_THAN_EQUAL_TO:
            case PLUS:
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case DATE_LITERAL:
            case TIME_LITERAL:
            case TIMESTAMP_LITERAL:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
            case WS:
            case LEFT_CURLY_BRACKET:
            case RIGHT_CURLY_BRACKET:
            case TEXTCHAR:
            case HEX_DIGIT:
            case HEX_LITERAL:
            case INTEGER_SUFFIX:
            case OCTAL_LITERAL:
            case NUMERIC_DIGITS:
            case DOUBLE_SUFFIX:
            case EXPONENT:
            case FLOAT_SUFFIX:
            case DATE_STRING:
            case TIME_STRING:
                {
                alt101=1;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1303:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 101, 0, input);
            
                throw nvae;
            }
            
            switch (alt101) {
                case 1 :
                    // JPQL.g:1305:7: identificationVariableDeclaration[varDecls]
                    {
                    pushFollow(FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8267);
                    identificationVariableDeclaration(varDecls);
                    _fsp--;
                    if (failed) return ;
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1306:7: n= associationPathExpression ( AS )? i= IDENT ( join )*
                    {
                    pushFollow(FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8280);
                    n=associationPathExpression();
                    _fsp--;
                    if (failed) return ;
                    // JPQL.g:1306:37: ( AS )?
                    int alt99=2;
                    int LA99_0 = input.LA(1);
                    
                    if ( (LA99_0==AS) ) {
                        alt99=1;
                    }
                    switch (alt99) {
                        case 1 :
                            // JPQL.g:1306:38: AS
                            {
                            match(input,AS,FOLLOW_AS_in_subselectIdentificationVariableDeclaration8283); if (failed) return ;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8289); if (failed) return ;
                    // JPQL.g:1306:51: ( join )*
                    loop100:
                    do {
                        int alt100=2;
                        int LA100_0 = input.LA(1);
                        
                        if ( (LA100_0==INNER||LA100_0==JOIN||LA100_0==LEFT) ) {
                            alt100=1;
                        }
                        
                    
                        switch (alt100) {
                    	case 1 :
                    	    // JPQL.g:1306:52: join
                    	    {
                    	    pushFollow(FOLLOW_join_in_subselectIdentificationVariableDeclaration8292);
                    	    join();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {
                    	       varDecls.add(n); 
                    	    }
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    break loop100;
                        }
                    } while (true);

                    if ( backtracking==0 ) {
                       
                                  varDecls.add(factory.newVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                       n, i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1311:7: n= collectionMemberDeclaration
                    {
                    pushFollow(FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8319);
                    n=collectionMemberDeclaration();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
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
    // $ANTLR end subselectIdentificationVariableDeclaration

    protected static class orderByClause_scope {
        List items;
    }
    protected Stack orderByClause_stack = new Stack();
    
    
    // $ANTLR start orderByClause
    // JPQL.g:1314:1: orderByClause returns [Object node] : o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* ;
    public final Object orderByClause() throws RecognitionException {
        orderByClause_stack.push(new orderByClause_scope());

        Object node = null;
    
        Token o=null;
        Object n = null;
        
    
         
            node = null; 
            ((orderByClause_scope)orderByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1322:7: (o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* )
            // JPQL.g:1322:7: o= ORDER BY n= orderByItem ( COMMA n= orderByItem )*
            {
            o=(Token)input.LT(1);
            match(input,ORDER,FOLLOW_ORDER_in_orderByClause8352); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_orderByClause8354); if (failed) return node;
            pushFollow(FOLLOW_orderByItem_in_orderByClause8368);
            n=orderByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1324:9: ( COMMA n= orderByItem )*
            loop102:
            do {
                int alt102=2;
                int LA102_0 = input.LA(1);
                
                if ( (LA102_0==COMMA) ) {
                    alt102=1;
                }
                
            
                switch (alt102) {
            	case 1 :
            	    // JPQL.g:1324:10: COMMA n= orderByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_orderByClause8383); if (failed) return node;
            	    pushFollow(FOLLOW_orderByItem_in_orderByClause8389);
            	    n=orderByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop102;
                }
            } while (true);

            if ( backtracking==0 ) {
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
    // $ANTLR end orderByClause

    
    // $ANTLR start orderByItem
    // JPQL.g:1328:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );
    public final Object orderByItem() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token d=null;
        Token i=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1330:7: (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) )
            int alt105=2;
            int LA105_0 = input.LA(1);
            
            if ( (LA105_0==IDENT) ) {
                int LA105_1 = input.LA(2);
                
                if ( (LA105_1==EOF||LA105_1==ASC||LA105_1==DESC||LA105_1==COMMA) ) {
                    alt105=2;
                }
                else if ( (LA105_1==DOT) ) {
                    alt105=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1328:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );", 105, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA105_0==KEY||LA105_0==VALUE) ) {
                alt105=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1328:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );", 105, 0, input);
            
                throw nvae;
            }
            switch (alt105) {
                case 1 :
                    // JPQL.g:1330:7: n= stateFieldPathExpression (a= ASC | d= DESC | )
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_orderByItem8435);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    // JPQL.g:1331:9: (a= ASC | d= DESC | )
                    int alt103=3;
                    switch ( input.LA(1) ) {
                    case ASC:
                        {
                        alt103=1;
                        }
                        break;
                    case DESC:
                        {
                        alt103=2;
                        }
                        break;
                    case EOF:
                    case COMMA:
                        {
                        alt103=3;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("1331:9: (a= ASC | d= DESC | )", 103, 0, input);
                    
                        throw nvae;
                    }
                    
                    switch (alt103) {
                        case 1 :
                            // JPQL.g:1331:11: a= ASC
                            {
                            a=(Token)input.LT(1);
                            match(input,ASC,FOLLOW_ASC_in_orderByItem8449); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), n); 
                            }
                            
                            }
                            break;
                        case 2 :
                            // JPQL.g:1333:11: d= DESC
                            {
                            d=(Token)input.LT(1);
                            match(input,DESC,FOLLOW_DESC_in_orderByItem8478); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), n); 
                            }
                            
                            }
                            break;
                        case 3 :
                            // JPQL.g:1336:13: 
                            {
                            if ( backtracking==0 ) {
                               node = factory.newAscOrdering(0, 0, n); 
                            }
                            
                            }
                            break;
                    
                    }

                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1338:8: i= IDENT (a= ASC | d= DESC | )
                    {
                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_orderByItem8540); if (failed) return node;
                    // JPQL.g:1339:9: (a= ASC | d= DESC | )
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
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("1339:9: (a= ASC | d= DESC | )", 104, 0, input);
                    
                        throw nvae;
                    }
                    
                    switch (alt104) {
                        case 1 :
                            // JPQL.g:1339:11: a= ASC
                            {
                            a=(Token)input.LT(1);
                            match(input,ASC,FOLLOW_ASC_in_orderByItem8554); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), i.getText()); 
                            }
                            
                            }
                            break;
                        case 2 :
                            // JPQL.g:1341:11: d= DESC
                            {
                            d=(Token)input.LT(1);
                            match(input,DESC,FOLLOW_DESC_in_orderByItem8583); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), i.getText()); 
                            }
                            
                            }
                            break;
                        case 3 :
                            // JPQL.g:1344:13: 
                            {
                            if ( backtracking==0 ) {
                               node = factory.newAscOrdering(0, 0, i.getText()); 
                            }
                            
                            }
                            break;
                    
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
    // $ANTLR end orderByItem

    protected static class groupByClause_scope {
        List items;
    }
    protected Stack groupByClause_stack = new Stack();
    
    
    // $ANTLR start groupByClause
    // JPQL.g:1348:1: groupByClause returns [Object node] : g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* ;
    public final Object groupByClause() throws RecognitionException {
        groupByClause_stack.push(new groupByClause_scope());

        Object node = null;
    
        Token g=null;
        Object n = null;
        
    
         
            node = null; 
            ((groupByClause_scope)groupByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1356:7: (g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* )
            // JPQL.g:1356:7: g= GROUP BY n= groupByItem ( COMMA n= groupByItem )*
            {
            g=(Token)input.LT(1);
            match(input,GROUP,FOLLOW_GROUP_in_groupByClause8664); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_groupByClause8666); if (failed) return node;
            pushFollow(FOLLOW_groupByItem_in_groupByClause8680);
            n=groupByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1358:9: ( COMMA n= groupByItem )*
            loop106:
            do {
                int alt106=2;
                int LA106_0 = input.LA(1);
                
                if ( (LA106_0==COMMA) ) {
                    alt106=1;
                }
                
            
                switch (alt106) {
            	case 1 :
            	    // JPQL.g:1358:10: COMMA n= groupByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_groupByClause8693); if (failed) return node;
            	    pushFollow(FOLLOW_groupByItem_in_groupByClause8699);
            	    n=groupByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop106;
                }
            } while (true);

            if ( backtracking==0 ) {
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
    // $ANTLR end groupByClause

    
    // $ANTLR start groupByItem
    // JPQL.g:1362:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );
    public final Object groupByItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1364:7: (n= stateFieldPathExpression | n= variableAccessOrTypeConstant )
            int alt107=2;
            int LA107_0 = input.LA(1);
            
            if ( (LA107_0==IDENT) ) {
                int LA107_1 = input.LA(2);
                
                if ( (LA107_1==EOF||LA107_1==HAVING||LA107_1==ORDER||LA107_1==COMMA||LA107_1==RIGHT_ROUND_BRACKET) ) {
                    alt107=2;
                }
                else if ( (LA107_1==DOT) ) {
                    alt107=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1362:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );", 107, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA107_0==KEY||LA107_0==VALUE) ) {
                alt107=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1362:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );", 107, 0, input);
            
                throw nvae;
            }
            switch (alt107) {
                case 1 :
                    // JPQL.g:1364:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_groupByItem8745);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1365:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_groupByItem8759);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
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
    // $ANTLR end groupByItem

    
    // $ANTLR start havingClause
    // JPQL.g:1368:1: havingClause returns [Object node] : h= HAVING n= conditionalExpression ;
    public final Object havingClause() throws RecognitionException {

        Object node = null;
    
        Token h=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1370:7: (h= HAVING n= conditionalExpression )
            // JPQL.g:1370:7: h= HAVING n= conditionalExpression
            {
            h=(Token)input.LT(1);
            match(input,HAVING,FOLLOW_HAVING_in_havingClause8789); if (failed) return node;
            if ( backtracking==0 ) {
               setAggregatesAllowed(true); 
            }
            pushFollow(FOLLOW_conditionalExpression_in_havingClause8806);
            n=conditionalExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               
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
    // $ANTLR end havingClause

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // JPQL.g:639:7: ( LEFT_ROUND_BRACKET conditionalExpression )
        // JPQL.g:639:8: LEFT_ROUND_BRACKET conditionalExpression
        {
        match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_synpred13524); if (failed) return ;
        pushFollow(FOLLOW_conditionalExpression_in_synpred13526);
        conditionalExpression();
        _fsp--;
        if (failed) return ;
        
        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // JPQL.g:1147:11: ( trimSpec trimChar FROM )
        // JPQL.g:1147:13: trimSpec trimChar FROM
        {
        pushFollow(FOLLOW_trimSpec_in_synpred27154);
        trimSpec();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_trimChar_in_synpred27156);
        trimChar();
        _fsp--;
        if (failed) return ;
        match(input,FROM,FOLLOW_FROM_in_synpred27158); if (failed) return ;
        
        }
    }
    // $ANTLR end synpred2

    public final boolean synpred1() {
        backtracking++;
        int start = input.mark();
        try {
            synpred1_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred2() {
        backtracking++;
        int start = input.mark();
        try {
            synpred2_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_selectStatement_in_document745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateStatement_in_document759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteStatement_in_document773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectClause_in_selectStatement806 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_fromClause_in_selectStatement821 = new BitSet(new long[]{0x0400000600000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_whereClause_in_selectStatement836 = new BitSet(new long[]{0x0400000600000000L});
    public static final BitSet FOLLOW_groupByClause_in_selectStatement851 = new BitSet(new long[]{0x0400000400000000L});
    public static final BitSet FOLLOW_havingClause_in_selectStatement867 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_orderByClause_in_selectStatement882 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_selectStatement892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateClause_in_updateStatement935 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_setClause_in_updateStatement950 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_whereClause_in_updateStatement964 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_updateStatement974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPDATE_in_updateClause1006 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x003FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_updateClause1012 = new BitSet(new long[]{0x0000000000000102L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_updateClause1025 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_updateClause1033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_setClause1082 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x003FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1088 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_setClause1101 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x003FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1107 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_setAssignmentTarget_in_setAssignmentClause1165 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_EQUALS_in_setAssignmentClause1169 = new BitSet(new long[]{0xC066E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_newValue_in_setAssignmentClause1175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_setAssignmentTarget1205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_setAssignmentTarget1220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalarExpression_in_newValue1252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_newValue1266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteClause_in_deleteStatement1310 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_whereClause_in_deleteStatement1323 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_deleteStatement1333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_deleteClause1366 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_FROM_in_deleteClause1368 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x003FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_deleteClause1374 = new BitSet(new long[]{0x0000000000000102L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_deleteClause1387 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_deleteClause1393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_selectClause1440 = new BitSet(new long[]{0xC0CEE910489FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause1443 = new BitSet(new long[]{0xC0CEE910481FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_selectItem_in_selectClause1459 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_selectClause1487 = new BitSet(new long[]{0xC0CEE910481FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_selectItem_in_selectClause1493 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_selectExpression_in_selectItem1589 = new BitSet(new long[]{0x0000000000000102L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_selectItem1593 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_selectItem1601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_selectExpression1645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalarExpression_in_selectExpression1659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBJECT_in_selectExpression1669 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1671 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_selectExpression1677 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constructorExpression_in_selectExpression1694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntryExpression_in_selectExpression1709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_in_mapEntryExpression1741 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1743 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1749 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1783 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_pathExprOrVariableAccess1798 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x003FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExprOrVariableAccess1804 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KEY_in_qualifiedIdentificationVariable1874 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1876 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1882 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUE_in_qualifiedIdentificationVariable1899 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1901 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1907 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AVG_in_aggregateExpression1942 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1944 = new BitSet(new long[]{0x0000010000800000L,0x0000000000004800L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1947 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1965 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAX_in_aggregateExpression1988 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1990 = new BitSet(new long[]{0x0000010000800000L,0x0000000000004800L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1993 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2012 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MIN_in_aggregateExpression2034 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2036 = new BitSet(new long[]{0x0000010000800000L,0x0000000000004800L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2039 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2057 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUM_in_aggregateExpression2079 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2081 = new BitSet(new long[]{0x0000010000800000L,0x0000000000004800L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2084 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2102 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COUNT_in_aggregateExpression2124 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2126 = new BitSet(new long[]{0x0000010000800000L,0x0000000000004800L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2129 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_aggregateExpression2147 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_constructorExpression2192 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_constructorName_in_constructorExpression2198 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2208 = new BitSet(new long[]{0xC046E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2223 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_COMMA_in_constructorExpression2238 = new BitSet(new long[]{0xC046E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2244 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2300 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_constructorName2314 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2318 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_scalarExpression_in_constructorItem2362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_constructorItem2376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_fromClause2410 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x003FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2412 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_fromClause2424 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x003FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2429 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_fromClause2454 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2520 = new BitSet(new long[]{0x000004A000000002L});
    public static final BitSet FOLLOW_join_in_identificationVariableDeclaration2539 = new BitSet(new long[]{0x000004A000000002L});
    public static final BitSet FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2574 = new BitSet(new long[]{0x0000000000000100L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_rangeVariableDeclaration2577 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_rangeVariableDeclaration2583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_joinSpec_in_join2666 = new BitSet(new long[]{0x0000010080000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2680 = new BitSet(new long[]{0x0000000000000100L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_join2683 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_join2689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FETCH_in_join2711 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_in_joinSpec2763 = new BitSet(new long[]{0x0800008000000000L});
    public static final BitSet FOLLOW_OUTER_in_joinSpec2766 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_INNER_in_joinSpec2775 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_JOIN_in_joinSpec2781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_collectionMemberDeclaration2809 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2811 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2817 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2819 = new BitSet(new long[]{0x0000000000000100L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_collectionMemberDeclaration2829 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_collectionMemberDeclaration2835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_collectionValuedPathExpression2873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_associationPathExpression2905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_joinAssociationPathExpression2938 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_joinAssociationPathExpression2953 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x003FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_joinAssociationPathExpression2959 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_pathExpression_in_singleValuedPathExpression3015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_stateFieldPathExpression3047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExpression3079 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_pathExpression3094 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x003FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExpression3100 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_IDENT_in_variableAccessOrTypeConstant3196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause3234 = new BitSet(new long[]{0xC056E910601FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_conditionalExpression_in_whereClause3240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3282 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_OR_in_conditionalExpression3297 = new BitSet(new long[]{0xC056E910601FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3303 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3358 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AND_in_conditionalTerm3373 = new BitSet(new long[]{0xC056E910601FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3379 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_NOT_in_conditionalFactor3434 = new BitSet(new long[]{0xC046E910601FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_conditionalPrimary_in_conditionalFactor3453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsExpression_in_conditionalFactor3482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3539 = new BitSet(new long[]{0xC056E910601FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalPrimary3545 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleConditionalExpression_in_conditionalPrimary3561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_simpleConditionalExpression3593 = new BitSet(new long[]{0x0011104800000800L,0x0000000001F10000L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3614 = new BitSet(new long[]{0x0011104800000800L,0x0000000001F10000L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3669 = new BitSet(new long[]{0x0001100800000800L});
    public static final BitSet FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_simpleConditionalExpressionRemainder3688 = new BitSet(new long[]{0x0030000002000000L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3693 = new BitSet(new long[]{0x0020000002000000L});
    public static final BitSet FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_betweenExpression_in_conditionWithNotExpression3736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_likeExpression_in_conditionWithNotExpression3751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inExpression_in_conditionWithNotExpression3765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullComparisonExpression_in_isExpression3814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptyCollectionComparisonExpression_in_isExpression3829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpression3862 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3876 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_AND_in_betweenExpression3878 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3930 = new BitSet(new long[]{0x0000000000000000L,0x000000C000000000L});
    public static final BitSet FOLLOW_inputParameter_in_inExpression3936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3963 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3973 = new BitSet(new long[]{0x1000000000000000L,0x000000C7E0004000L});
    public static final BitSet FOLLOW_inItem_in_inExpression3989 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression4007 = new BitSet(new long[]{0x0000000000000000L,0x000000C7E0004000L});
    public static final BitSet FOLLOW_inItem_in_inExpression4013 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_subquery_in_inExpression4048 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_inItem4112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_inItem4126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_inItem4140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_inItem4154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIKE_in_likeExpression4186 = new BitSet(new long[]{0x0000000000000000L,0x000000C600000000L});
    public static final BitSet FOLLOW_likeValue_in_likeExpression4192 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_escape_in_likeExpression4207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_in_escape4247 = new BitSet(new long[]{0x0000000000000000L,0x000000C600000000L});
    public static final BitSet FOLLOW_likeValue_in_escape4253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_likeValue4293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_likeValue4307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_nullComparisonExpression4340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEMBER_in_collectionMemberExpression4422 = new BitSet(new long[]{0x0100010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_OF_in_collectionMemberExpression4425 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_existsExpression4473 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4475 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_subquery_in_existsExpression4481 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_comparisonExpression4523 = new BitSet(new long[]{0xC046E910401FC4B0L,0x000000FFE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4550 = new BitSet(new long[]{0xC046E910401FC4B0L,0x000000FFE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_in_comparisonExpression4577 = new BitSet(new long[]{0xC046E910401FC4B0L,0x000000FFE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4604 = new BitSet(new long[]{0xC046E910401FC4B0L,0x000000FFE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_in_comparisonExpression4631 = new BitSet(new long[]{0xC046E910401FC4B0L,0x000000FFE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4658 = new BitSet(new long[]{0xC046E910401FC4B0L,0x000000FFE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4775 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_subquery_in_arithmeticExpression4781 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4815 = new BitSet(new long[]{0x0000000000000002L,0x0000000006000000L});
    public static final BitSet FOLLOW_PLUS_in_simpleArithmeticExpression4831 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4837 = new BitSet(new long[]{0x0000000000000002L,0x0000000006000000L});
    public static final BitSet FOLLOW_MINUS_in_simpleArithmeticExpression4866 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4872 = new BitSet(new long[]{0x0000000000000002L,0x0000000006000000L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4929 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_MULTIPLY_in_arithmeticTerm4945 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4951 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_DIVIDE_in_arithmeticTerm4980 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4986 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_PLUS_in_arithmeticFactor5040 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E0024804L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_arithmeticFactor5069 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E0024804L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_arithmeticPrimary5133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_arithmeticPrimary5161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_caseExpression_in_arithmeticPrimary5175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5199 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5205 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_arithmeticPrimary5221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_scalarExpression5253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_nonArithmeticScalarExpression5328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalTemporal_in_nonArithmeticScalarExpression5356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_anyOrAllExpression5400 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5402 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5408 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_anyOrAllExpression5430 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5432 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5438 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SOME_in_anyOrAllExpression5460 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5462 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5468 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_entityTypeExpression5510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5543 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5545 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5551 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5568 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5570 = new BitSet(new long[]{0x0000000000000000L,0x000000C000000000L});
    public static final BitSet FOLLOW_inputParameter_in_typeDiscriminator5576 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleCaseExpression_in_caseExpression5613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_generalCaseExpression_in_caseExpression5626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_coalesceExpression_in_caseExpression5639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullIfExpression_in_caseExpression5652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_simpleCaseExpression5690 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004880L});
    public static final BitSet FOLLOW_caseOperand_in_simpleCaseExpression5696 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5702 = new BitSet(new long[]{0x0000000001000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5711 = new BitSet(new long[]{0x0000000001000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ELSE_in_simpleCaseExpression5717 = new BitSet(new long[]{0xC046E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_simpleCaseExpression5723 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_END_in_simpleCaseExpression5725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_generalCaseExpression5769 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5775 = new BitSet(new long[]{0x0000000001000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5784 = new BitSet(new long[]{0x0000000001000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ELSE_in_generalCaseExpression5790 = new BitSet(new long[]{0xC046E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_generalCaseExpression5796 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_END_in_generalCaseExpression5798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COALESCE_in_coalesceExpression5842 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5844 = new BitSet(new long[]{0xC046E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5850 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_coalesceExpression5855 = new BitSet(new long[]{0xC046E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5861 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULLIF_in_nullIfExpression5908 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5910 = new BitSet(new long[]{0xC046E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5916 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_nullIfExpression5918 = new BitSet(new long[]{0xC046E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5924 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression5926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_caseOperand5973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_caseOperand5987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_whenClause6022 = new BitSet(new long[]{0xC056E910601FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_conditionalExpression_in_whenClause6028 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_THEN_in_whenClause6030 = new BitSet(new long[]{0xC046E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_whenClause6036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_simpleWhenClause6078 = new BitSet(new long[]{0xC046E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause6084 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_THEN_in_simpleWhenClause6086 = new BitSet(new long[]{0xC046E910401FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause6092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_stringPrimary6175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_stringPrimary6189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_stringPrimary6203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_stringPrimary6217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_literal6251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_literal6265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_literal6279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_LITERAL_in_literalNumeric6309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LONG_LITERAL_in_literalNumeric6325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_LITERAL_in_literalNumeric6346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_LITERAL_in_literalNumeric6366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literalBoolean6404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literalBoolean6426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_LITERAL_in_literalTemporal6526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIME_LITERAL_in_literalTemporal6540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIMESTAMP_LITERAL_in_literalTemporal6554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POSITIONAL_PARAM_in_inputParameter6584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_PARAM_in_inputParameter6604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_abs_in_functionsReturningNumerics6644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_length_in_functionsReturningNumerics6658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mod_in_functionsReturningNumerics6672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqrt_in_functionsReturningNumerics6686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_locate_in_functionsReturningNumerics6700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_size_in_functionsReturningNumerics6714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_index_in_functionsReturningNumerics6728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_concat_in_functionsReturningStrings6839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_substring_in_functionsReturningStrings6853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trim_in_functionsReturningStrings6867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_upper_in_functionsReturningStrings6881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lower_in_functionsReturningStrings6895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONCAT_in_concat6930 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_concat6941 = new BitSet(new long[]{0x0000410000010000L,0x000000C600004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_concat6956 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_concat6961 = new BitSet(new long[]{0x0000410000010000L,0x000000C600004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_concat6967 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_concat6981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSTRING_in_substring7019 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_substring7032 = new BitSet(new long[]{0x0000410000010000L,0x000000C600004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_substring7046 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_substring7048 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring7062 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_COMMA_in_substring7073 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring7079 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_substring7091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIM_in_trim7129 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_trim7139 = new BitSet(new long[]{0x0000430100011000L,0x000000C600004C32L});
    public static final BitSet FOLLOW_trimSpec_in_trim7167 = new BitSet(new long[]{0x0000000100000000L,0x000000C600000000L});
    public static final BitSet FOLLOW_trimChar_in_trim7173 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_FROM_in_trim7175 = new BitSet(new long[]{0x0000410000010000L,0x000000C600004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_trim7193 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_trim7203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEADING_in_trimSpec7239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRAILING_in_trimSpec7257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOTH_in_trimSpec7275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_trimChar7322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_trimChar7336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPPER_in_upper7373 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_upper7375 = new BitSet(new long[]{0x0000410000010000L,0x000000C600004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_upper7381 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_upper7383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOWER_in_lower7421 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_lower7423 = new BitSet(new long[]{0x0000410000010000L,0x000000C600004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_lower7429 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_lower7431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABS_in_abs7470 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_abs7472 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_abs7478 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_abs7480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LENGTH_in_length7518 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_length7520 = new BitSet(new long[]{0x0000410000010000L,0x000000C600004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_length7526 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_length7528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCATE_in_locate7566 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_locate7576 = new BitSet(new long[]{0x0000410000010000L,0x000000C600004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_locate7591 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_locate7593 = new BitSet(new long[]{0x0000410000010000L,0x000000C600004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_locate7599 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_COMMA_in_locate7611 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_locate7617 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_locate7630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIZE_in_size7668 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_size7679 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_size7685 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_size7687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mod7725 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mod7727 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod7741 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_mod7743 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod7758 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mod7768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQRT_in_sqrt7806 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7817 = new BitSet(new long[]{0xC046A9100002C410L,0x000000C1E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_sqrt7823 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDEX_in_index7867 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_index7869 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_index7875 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_index7877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleSelectClause_in_subquery7918 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_subqueryFromClause_in_subquery7933 = new BitSet(new long[]{0x0000000600000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_whereClause_in_subquery7948 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_groupByClause_in_subquery7963 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_havingClause_in_subquery7979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_simpleSelectClause8022 = new BitSet(new long[]{0x0002810000820400L,0x0000000000004804L});
    public static final BitSet FOLLOW_DISTINCT_in_simpleSelectClause8025 = new BitSet(new long[]{0x0002810000020400L,0x0000000000004804L});
    public static final BitSet FOLLOW_simpleSelectExpression_in_simpleSelectClause8041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_simpleSelectExpression8081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_simpleSelectExpression8096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression8111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_subqueryFromClause8146 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x003FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8148 = new BitSet(new long[]{0x0000000800000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_subqueryFromClause8175 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x003FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8194 = new BitSet(new long[]{0x0000000800000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8220 = new BitSet(new long[]{0x0000000800000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8280 = new BitSet(new long[]{0x0000000000000100L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_subselectIdentificationVariableDeclaration8283 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8289 = new BitSet(new long[]{0x000004A000000002L});
    public static final BitSet FOLLOW_join_in_subselectIdentificationVariableDeclaration8292 = new BitSet(new long[]{0x000004A000000002L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_orderByClause8352 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_orderByClause8354 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8368 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_orderByClause8383 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8389 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_orderByItem8435 = new BitSet(new long[]{0x0000000000200202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem8449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem8478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_orderByItem8540 = new BitSet(new long[]{0x0000000000200202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem8554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem8583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_groupByClause8664 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_groupByClause8666 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause8680 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_groupByClause8693 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause8699 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_groupByItem8745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_groupByItem8759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HAVING_in_havingClause8789 = new BitSet(new long[]{0xC056E910601FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_conditionalExpression_in_havingClause8806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_synpred13524 = new BitSet(new long[]{0xC056E910601FC410L,0x000000FFE6024CE6L});
    public static final BitSet FOLLOW_conditionalExpression_in_synpred13526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trimSpec_in_synpred27154 = new BitSet(new long[]{0x0000000100000000L,0x000000C600000000L});
    public static final BitSet FOLLOW_trimChar_in_synpred27156 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_FROM_in_synpred27158 = new BitSet(new long[]{0x0000000000000002L});

}