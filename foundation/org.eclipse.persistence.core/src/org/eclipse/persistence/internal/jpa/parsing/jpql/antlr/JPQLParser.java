// $ANTLR 3.0 JPQL.g 2010-04-26 08:45:14

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
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
    // JPQL.g:199:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );
    public final void document() throws RecognitionException {
        Object root = null;
        
    
        try {
            // JPQL.g:200:7: (root= selectStatement | root= updateStatement | root= deleteStatement )
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
                    new NoViableAltException("199:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );", 1, 0, input);
            
                throw nvae;
            }
            
            switch (alt1) {
                case 1 :
                    // JPQL.g:200:7: root= selectStatement
                    {
                    pushFollow(FOLLOW_selectStatement_in_document763);
                    root=selectStatement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      queryRoot = root;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:201:7: root= updateStatement
                    {
                    pushFollow(FOLLOW_updateStatement_in_document777);
                    root=updateStatement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      queryRoot = root;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:202:7: root= deleteStatement
                    {
                    pushFollow(FOLLOW_deleteStatement_in_document791);
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
            // JPQL.g:209:7: (select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF )
            // JPQL.g:209:7: select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF
            {
            pushFollow(FOLLOW_selectClause_in_selectStatement824);
            select=selectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_fromClause_in_selectStatement839);
            from=fromClause();
            _fsp--;
            if (failed) return node;
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
                    _fsp--;
                    if (failed) return node;
                    
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
                    _fsp--;
                    if (failed) return node;
                    
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
                    _fsp--;
                    if (failed) return node;
                    
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
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_selectStatement910); if (failed) return node;
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
    // JPQL.g:224:1: updateStatement returns [Object node] : update= updateClause set= setClause (where= whereClause )? EOF ;
    public final Object updateStatement() throws RecognitionException {

        Object node = null;
    
        Object update = null;

        Object set = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:228:7: (update= updateClause set= setClause (where= whereClause )? EOF )
            // JPQL.g:228:7: update= updateClause set= setClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_updateClause_in_updateStatement953);
            update=updateClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_setClause_in_updateStatement968);
            set=setClause();
            _fsp--;
            if (failed) return node;
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
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_updateStatement992); if (failed) return node;
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
    // JPQL.g:234:1: updateClause returns [Object node] : u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object updateClause() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Token ident=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:238:7: (u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g:238:7: u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            u=(Token)input.LT(1);
            match(input,UPDATE,FOLLOW_UPDATE_in_updateClause1024); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_updateClause1030);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
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
                            match(input,AS,FOLLOW_AS_in_updateClause1043); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_updateClause1051); if (failed) return node;
                    
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
    // JPQL.g:250:1: setClause returns [Object node] : t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* ;
    public final Object setClause() throws RecognitionException {
        setClause_stack.push(new setClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((setClause_scope)setClause_stack.peek()).assignments = new ArrayList();
    
        try {
            // JPQL.g:258:7: (t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* )
            // JPQL.g:258:7: t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )*
            {
            t=(Token)input.LT(1);
            match(input,SET,FOLLOW_SET_in_setClause1100); if (failed) return node;
            pushFollow(FOLLOW_setAssignmentClause_in_setClause1106);
            n=setAssignmentClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
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
            	    match(input,COMMA,FOLLOW_COMMA_in_setClause1119); if (failed) return node;
            	    pushFollow(FOLLOW_setAssignmentClause_in_setClause1125);
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
    // JPQL.g:263:1: setAssignmentClause returns [Object node] : target= setAssignmentTarget t= EQUALS value= newValue ;
    public final Object setAssignmentClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object target = null;

        Object value = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:271:7: (target= setAssignmentTarget t= EQUALS value= newValue )
            // JPQL.g:271:7: target= setAssignmentTarget t= EQUALS value= newValue
            {
            pushFollow(FOLLOW_setAssignmentTarget_in_setAssignmentClause1183);
            target=setAssignmentTarget();
            _fsp--;
            if (failed) return node;
            t=(Token)input.LT(1);
            match(input,EQUALS,FOLLOW_EQUALS_in_setAssignmentClause1187); if (failed) return node;
            pushFollow(FOLLOW_newValue_in_setAssignmentClause1193);
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
    // JPQL.g:274:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );
    public final Object setAssignmentTarget() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:278:7: (n= attribute | n= pathExpression )
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
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("274:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 1, input);
                
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
                        new NoViableAltException("274:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 2, input);
                
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
                        new NoViableAltException("274:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 3, input);
                
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
            case FUNC:
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
            case TREAT:
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
                    new NoViableAltException("274:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 0, input);
            
                throw nvae;
            }
            
            switch (alt10) {
                case 1 :
                    // JPQL.g:278:7: n= attribute
                    {
                    pushFollow(FOLLOW_attribute_in_setAssignmentTarget1223);
                    n=attribute();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:279:7: n= pathExpression
                    {
                    pushFollow(FOLLOW_pathExpression_in_setAssignmentTarget1238);
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
    // JPQL.g:282:1: newValue returns [Object node] : (n= scalarExpression | n1= NULL );
    public final Object newValue() throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:284:7: (n= scalarExpression | n1= NULL )
            int alt11=2;
            int LA11_0 = input.LA(1);
            
            if ( (LA11_0==ABS||LA11_0==AVG||(LA11_0>=CASE && LA11_0<=CURRENT_TIMESTAMP)||LA11_0==FALSE||LA11_0==FUNC||LA11_0==INDEX||LA11_0==KEY||LA11_0==LENGTH||(LA11_0>=LOCATE && LA11_0<=MAX)||(LA11_0>=MIN && LA11_0<=MOD)||LA11_0==NULLIF||(LA11_0>=SIZE && LA11_0<=SQRT)||(LA11_0>=SUBSTRING && LA11_0<=SUM)||(LA11_0>=TRIM && LA11_0<=TYPE)||(LA11_0>=UPPER && LA11_0<=VALUE)||LA11_0==IDENT||LA11_0==LEFT_ROUND_BRACKET||(LA11_0>=PLUS && LA11_0<=MINUS)||(LA11_0>=INTEGER_LITERAL && LA11_0<=NAMED_PARAM)) ) {
                alt11=1;
            }
            else if ( (LA11_0==NULL) ) {
                alt11=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("282:1: newValue returns [Object node] : (n= scalarExpression | n1= NULL );", 11, 0, input);
            
                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // JPQL.g:284:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_newValue1270);
                    n=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:285:7: n1= NULL
                    {
                    n1=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_newValue1284); if (failed) return node;
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
    // JPQL.g:291:1: deleteStatement returns [Object node] : delete= deleteClause (where= whereClause )? EOF ;
    public final Object deleteStatement() throws RecognitionException {

        Object node = null;
    
        Object delete = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:295:7: (delete= deleteClause (where= whereClause )? EOF )
            // JPQL.g:295:7: delete= deleteClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_deleteClause_in_deleteStatement1328);
            delete=deleteClause();
            _fsp--;
            if (failed) return node;
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
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_deleteStatement1351); if (failed) return node;
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
            // JPQL.g:308:7: (t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g:308:7: t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            t=(Token)input.LT(1);
            match(input,DELETE,FOLLOW_DELETE_in_deleteClause1384); if (failed) return node;
            match(input,FROM,FOLLOW_FROM_in_deleteClause1386); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_deleteClause1392);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
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
                            match(input,AS,FOLLOW_AS_in_deleteClause1405); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_deleteClause1411); if (failed) return node;
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
    // JPQL.g:318:1: selectClause returns [Object node] : t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )* ;
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
            // JPQL.g:330:7: (t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )* )
            // JPQL.g:330:7: t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )*
            {
            t=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_selectClause1458); if (failed) return node;
            // JPQL.g:330:16: ( DISTINCT )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            
            if ( (LA15_0==DISTINCT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // JPQL.g:330:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause1461); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((selectClause_scope)selectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_selectItem_in_selectClause1477);
            n=selectItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              
                            ((selectClause_scope)selectClause_stack.peek()).exprs.add(n.expr);
                            ((selectClause_scope)selectClause_stack.peek()).idents.add(n.ident);
                        
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
            	    match(input,COMMA,FOLLOW_COMMA_in_selectClause1505); if (failed) return node;
            	    pushFollow(FOLLOW_selectItem_in_selectClause1511);
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
    // JPQL.g:349:1: selectItem returns [Object expr, Object ident] : e= selectExpression ( ( AS )? identifier= IDENT )? ;
    public final selectItem_return selectItem() throws RecognitionException {
        selectItem_return retval = new selectItem_return();
        retval.start = input.LT(1);
    
        Token identifier=null;
        Object e = null;
        
    
        try {
            // JPQL.g:350:7: (e= selectExpression ( ( AS )? identifier= IDENT )? )
            // JPQL.g:350:7: e= selectExpression ( ( AS )? identifier= IDENT )?
            {
            pushFollow(FOLLOW_selectExpression_in_selectItem1607);
            e=selectExpression();
            _fsp--;
            if (failed) return retval;
            // JPQL.g:350:28: ( ( AS )? identifier= IDENT )?
            int alt18=2;
            int LA18_0 = input.LA(1);
            
            if ( (LA18_0==AS||LA18_0==IDENT) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // JPQL.g:350:29: ( AS )? identifier= IDENT
                    {
                    // JPQL.g:350:29: ( AS )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);
                    
                    if ( (LA17_0==AS) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // JPQL.g:350:30: AS
                            {
                            match(input,AS,FOLLOW_AS_in_selectItem1611); if (failed) return retval;
                            
                            }
                            break;
                    
                    }

                    identifier=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_selectItem1619); if (failed) return retval;
                    
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
    // JPQL.g:363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );
    public final Object selectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:365:7: (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression )
            int alt19=5;
            switch ( input.LA(1) ) {
            case AVG:
                {
                int LA19_1 = input.LA(2);
                
                if ( (LA19_1==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 53, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 54, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 55, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 56, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 48, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 1, input);
                
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 57, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 58, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 59, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 60, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 49, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 2, input);
                
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 61, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 62, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 63, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 64, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 50, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 3, input);
                
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 65, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 66, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 67, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 68, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 51, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 4, input);
                
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 69, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 70, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 71, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA19_72 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 72, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 52, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 5, input);
                
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
            case FUNC:
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
                    new NoViableAltException("363:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 0, input);
            
                throw nvae;
            }
            
            switch (alt19) {
                case 1 :
                    // JPQL.g:365:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_selectExpression1663);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:366:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_selectExpression1677);
                    n=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:367:7: OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    match(input,OBJECT,FOLLOW_OBJECT_in_selectExpression1687); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1689); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_selectExpression1695);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1697); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:368:7: n= constructorExpression
                    {
                    pushFollow(FOLLOW_constructorExpression_in_selectExpression1712);
                    n=constructorExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:369:7: n= mapEntryExpression
                    {
                    pushFollow(FOLLOW_mapEntryExpression_in_selectExpression1727);
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
    // JPQL.g:372:1: mapEntryExpression returns [Object node] : l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object mapEntryExpression() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:374:7: (l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // JPQL.g:374:7: l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,ENTRY,FOLLOW_ENTRY_in_mapEntryExpression1759); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1761); if (failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1767);
            n=variableAccessOrTypeConstant();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1769); if (failed) return node;
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
    // JPQL.g:377:1: pathExprOrVariableAccess returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )* ;
    public final Object pathExprOrVariableAccess() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:381:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )* )
            // JPQL.g:381:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )*
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1801);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:382:9: (d= DOT right= attribute )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);
                
                if ( (LA20_0==DOT) ) {
                    alt20=1;
                }
                
            
                switch (alt20) {
            	case 1 :
            	    // JPQL.g:382:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExprOrVariableAccess1816); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExprOrVariableAccess1822);
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
    // JPQL.g:387:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );
    public final Object qualifiedIdentificationVariable() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:389:7: (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
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
                    new NoViableAltException("387:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );", 21, 0, input);
            
                throw nvae;
            }
            
            switch (alt21) {
                case 1 :
                    // JPQL.g:389:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1878);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:390:7: l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,KEY,FOLLOW_KEY_in_qualifiedIdentificationVariable1892); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1894); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1900);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1902); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newKey(l.getLine(), l.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:391:7: l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,VALUE,FOLLOW_VALUE_in_qualifiedIdentificationVariable1917); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1919); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1925);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1927); if (failed) return node;
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
    // JPQL.g:394:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );
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
            // JPQL.g:402:7: (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET )
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
                    new NoViableAltException("394:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );", 27, 0, input);
            
                throw nvae;
            }
            
            switch (alt27) {
                case 1 :
                    // JPQL.g:402:7: t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t1=(Token)input.LT(1);
                    match(input,AVG,FOLLOW_AVG_in_aggregateExpression1960); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1962); if (failed) return node;
                    // JPQL.g:402:33: ( DISTINCT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);
                    
                    if ( (LA22_0==DISTINCT) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // JPQL.g:402:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1965); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1983);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1985); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAvg(t1.getLine(), t1.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:405:7: t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t2=(Token)input.LT(1);
                    match(input,MAX,FOLLOW_MAX_in_aggregateExpression2006); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2008); if (failed) return node;
                    // JPQL.g:405:33: ( DISTINCT )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    
                    if ( (LA23_0==DISTINCT) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // JPQL.g:405:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2011); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2030);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2032); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMax(t2.getLine(), t2.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:408:7: t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t3=(Token)input.LT(1);
                    match(input,MIN,FOLLOW_MIN_in_aggregateExpression2052); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2054); if (failed) return node;
                    // JPQL.g:408:33: ( DISTINCT )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);
                    
                    if ( (LA24_0==DISTINCT) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // JPQL.g:408:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2057); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2075);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2077); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMin(t3.getLine(), t3.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:411:7: t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t4=(Token)input.LT(1);
                    match(input,SUM,FOLLOW_SUM_in_aggregateExpression2097); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2099); if (failed) return node;
                    // JPQL.g:411:33: ( DISTINCT )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);
                    
                    if ( (LA25_0==DISTINCT) ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // JPQL.g:411:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2102); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2120);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2122); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newSum(t4.getLine(), t4.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:414:7: t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET
                    {
                    t5=(Token)input.LT(1);
                    match(input,COUNT,FOLLOW_COUNT_in_aggregateExpression2142); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2144); if (failed) return node;
                    // JPQL.g:414:35: ( DISTINCT )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);
                    
                    if ( (LA26_0==DISTINCT) ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // JPQL.g:414:36: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2147); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_aggregateExpression2165);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2167); if (failed) return node;
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
    // JPQL.g:419:1: constructorExpression returns [Object node] : t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET ;
    public final Object constructorExpression() throws RecognitionException {
        constructorExpression_stack.push(new constructorExpression_scope());

        Object node = null;
    
        Token t=null;
        String className = null;

        Object n = null;
        
    
         
            node = null;
            ((constructorExpression_scope)constructorExpression_stack.peek()).args = new ArrayList();
    
        try {
            // JPQL.g:427:7: (t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET )
            // JPQL.g:427:7: t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,NEW,FOLLOW_NEW_in_constructorExpression2210); if (failed) return node;
            pushFollow(FOLLOW_constructorName_in_constructorExpression2216);
            className=constructorName();
            _fsp--;
            if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2226); if (failed) return node;
            pushFollow(FOLLOW_constructorItem_in_constructorExpression2241);
            n=constructorItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            }
            // JPQL.g:430:9: ( COMMA n= constructorItem )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);
                
                if ( (LA28_0==COMMA) ) {
                    alt28=1;
                }
                
            
                switch (alt28) {
            	case 1 :
            	    // JPQL.g:430:11: COMMA n= constructorItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constructorExpression2256); if (failed) return node;
            	    pushFollow(FOLLOW_constructorItem_in_constructorExpression2262);
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

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2277); if (failed) return node;
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
    // JPQL.g:438:1: constructorName returns [String className] : i1= IDENT ( DOT i2= IDENT )* ;
    public final String constructorName() throws RecognitionException {
        constructorName_stack.push(new constructorName_scope());

        String className = null;
    
        Token i1=null;
        Token i2=null;
    
         
            className = null;
            ((constructorName_scope)constructorName_stack.peek()).buf = new StringBuffer(); 
    
        try {
            // JPQL.g:446:7: (i1= IDENT ( DOT i2= IDENT )* )
            // JPQL.g:446:7: i1= IDENT ( DOT i2= IDENT )*
            {
            i1=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_constructorName2318); if (failed) return className;
            if ( backtracking==0 ) {
               ((constructorName_scope)constructorName_stack.peek()).buf.append(i1.getText()); 
            }
            // JPQL.g:447:9: ( DOT i2= IDENT )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);
                
                if ( (LA29_0==DOT) ) {
                    alt29=1;
                }
                
            
                switch (alt29) {
            	case 1 :
            	    // JPQL.g:447:11: DOT i2= IDENT
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_constructorName2332); if (failed) return className;
            	    i2=(Token)input.LT(1);
            	    match(input,IDENT,FOLLOW_IDENT_in_constructorName2336); if (failed) return className;
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
    // JPQL.g:451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );
    public final Object constructorItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:453:7: (n= scalarExpression | n= aggregateExpression )
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
            case FUNC:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 50, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 51, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 52, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 53, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 45, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 3, input);
                
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 54, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 55, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 56, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 57, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 46, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 4, input);
                
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 58, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 59, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 60, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 61, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 47, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 5, input);
                
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 62, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 63, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 64, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 65, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 48, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 6, input);
                
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 66, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 67, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 68, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA30_69 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 69, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 49, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 7, input);
                
                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("451:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 0, input);
            
                throw nvae;
            }
            
            switch (alt30) {
                case 1 :
                    // JPQL.g:453:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_constructorItem2380);
                    n=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:454:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_constructorItem2394);
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
    // JPQL.g:458:1: fromClause returns [Object node] : t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* ;
    public final Object fromClause() throws RecognitionException {
        fromClause_stack.push(new fromClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((fromClause_scope)fromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g:466:7: (t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* )
            // JPQL.g:466:7: t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            {
            t=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_fromClause2428); if (failed) return node;
            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2430);
            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g:467:9: ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);
                
                if ( (LA32_0==COMMA) ) {
                    alt32=1;
                }
                
            
                switch (alt32) {
            	case 1 :
            	    // JPQL.g:467:10: COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_fromClause2442); if (failed) return node;
            	    // JPQL.g:467:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
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
            	                new NoViableAltException("467:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 31, 1, input);
            	        
            	            throw nvae;
            	        }
            	    }
            	    else if ( ((LA31_0>=ABS && LA31_0<=HAVING)||(LA31_0>=INDEX && LA31_0<=TIME_STRING)) ) {
            	        alt31=1;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return node;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("467:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 31, 0, input);
            	    
            	        throw nvae;
            	    }
            	    switch (alt31) {
            	        case 1 :
            	            // JPQL.g:467:19: identificationVariableDeclaration[$fromClause::varDecls]
            	            {
            	            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2447);
            	            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            	            _fsp--;
            	            if (failed) return node;
            	            
            	            }
            	            break;
            	        case 2 :
            	            // JPQL.g:468:19: n= collectionMemberDeclaration
            	            {
            	            pushFollow(FOLLOW_collectionMemberDeclaration_in_fromClause2472);
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
    // JPQL.g:474:1: identificationVariableDeclaration[List varDecls] : node= rangeVariableDeclaration (node= join )* ;
    public final void identificationVariableDeclaration(List varDecls) throws RecognitionException {
        Object node = null;
        
    
        try {
            // JPQL.g:475:7: (node= rangeVariableDeclaration (node= join )* )
            // JPQL.g:475:7: node= rangeVariableDeclaration (node= join )*
            {
            pushFollow(FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2538);
            node=rangeVariableDeclaration();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               varDecls.add(node); 
            }
            // JPQL.g:476:9: (node= join )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                
                if ( (LA33_0==INNER||LA33_0==JOIN||LA33_0==LEFT) ) {
                    alt33=1;
                }
                
            
                switch (alt33) {
            	case 1 :
            	    // JPQL.g:476:11: node= join
            	    {
            	    pushFollow(FOLLOW_join_in_identificationVariableDeclaration2557);
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
    // JPQL.g:479:1: rangeVariableDeclaration returns [Object node] : schema= abstractSchemaName ( AS )? i= IDENT ;
    public final Object rangeVariableDeclaration() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:483:7: (schema= abstractSchemaName ( AS )? i= IDENT )
            // JPQL.g:483:7: schema= abstractSchemaName ( AS )? i= IDENT
            {
            pushFollow(FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2592);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g:483:35: ( AS )?
            int alt34=2;
            int LA34_0 = input.LA(1);
            
            if ( (LA34_0==AS) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // JPQL.g:483:36: AS
                    {
                    match(input,AS,FOLLOW_AS_in_rangeVariableDeclaration2595); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_rangeVariableDeclaration2601); if (failed) return node;
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
    // JPQL.g:494:1: abstractSchemaName returns [String schema] : ident= . ;
    public final String abstractSchemaName() throws RecognitionException {

        String schema = null;
    
        Token ident=null;
    
         schema = null; 
        try {
            // JPQL.g:496:7: (ident= . )
            // JPQL.g:496:7: ident= .
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
    // JPQL.g:503:1: join returns [Object node] : outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) ;
    public final Object join() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token castClass=null;
        Token t=null;
        boolean outerJoin = false;

        Object n = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:507:7: (outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) )
            // JPQL.g:507:7: outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            {
            pushFollow(FOLLOW_joinSpec_in_join2684);
            outerJoin=joinSpec();
            _fsp--;
            if (failed) return node;
            // JPQL.g:508:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("508:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )", 37, 0, input);
            
                throw nvae;
            }
            
            switch (alt37) {
                case 1 :
                    // JPQL.g:508:9: n= joinAssociationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2698);
                    n=joinAssociationPathExpression();
                    _fsp--;
                    if (failed) return node;
                    // JPQL.g:508:43: ( AS )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);
                    
                    if ( (LA35_0==AS) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // JPQL.g:508:44: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2701); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_join2707); if (failed) return node;
                    if ( backtracking==0 ) {
                      
                                  node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                     outerJoin, n, i.getText(), null); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:514:8: TREAT LEFT_ROUND_BRACKET n= joinAssociationPathExpression AS castClass= IDENT RIGHT_ROUND_BRACKET ( AS )? i= IDENT
                    {
                    match(input,TREAT,FOLLOW_TREAT_in_join2736); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_join2738); if (failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2744);
                    n=joinAssociationPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,AS,FOLLOW_AS_in_join2746); if (failed) return node;
                    castClass=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_join2752); if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_join2754); if (failed) return node;
                    // JPQL.g:514:108: ( AS )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);
                    
                    if ( (LA36_0==AS) ) {
                        alt36=1;
                    }
                    switch (alt36) {
                        case 1 :
                            // JPQL.g:514:109: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2757); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_join2763); if (failed) return node;
                    if ( backtracking==0 ) {
                      
                                  node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                     outerJoin, n, i.getText(), castClass.getText()); 
                              
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:519:9: t= FETCH n= joinAssociationPathExpression
                    {
                    t=(Token)input.LT(1);
                    match(input,FETCH,FOLLOW_FETCH_in_join2786); if (failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2792);
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
    // JPQL.g:526:1: joinSpec returns [boolean outer] : ( LEFT ( OUTER )? | INNER )? JOIN ;
    public final boolean joinSpec() throws RecognitionException {

        boolean outer = false;
    
         outer = false; 
        try {
            // JPQL.g:528:7: ( ( LEFT ( OUTER )? | INNER )? JOIN )
            // JPQL.g:528:7: ( LEFT ( OUTER )? | INNER )? JOIN
            {
            // JPQL.g:528:7: ( LEFT ( OUTER )? | INNER )?
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
                    // JPQL.g:528:8: LEFT ( OUTER )?
                    {
                    match(input,LEFT,FOLLOW_LEFT_in_joinSpec2838); if (failed) return outer;
                    // JPQL.g:528:13: ( OUTER )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);
                    
                    if ( (LA38_0==OUTER) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // JPQL.g:528:14: OUTER
                            {
                            match(input,OUTER,FOLLOW_OUTER_in_joinSpec2841); if (failed) return outer;
                            
                            }
                            break;
                    
                    }

                    if ( backtracking==0 ) {
                       outer = true; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:528:44: INNER
                    {
                    match(input,INNER,FOLLOW_INNER_in_joinSpec2850); if (failed) return outer;
                    
                    }
                    break;
            
            }

            match(input,JOIN,FOLLOW_JOIN_in_joinSpec2856); if (failed) return outer;
            
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
    // JPQL.g:531:1: collectionMemberDeclaration returns [Object node] : t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT ;
    public final Object collectionMemberDeclaration() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token i=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:533:7: (t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT )
            // JPQL.g:533:7: t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT
            {
            t=(Token)input.LT(1);
            match(input,IN,FOLLOW_IN_in_collectionMemberDeclaration2884); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2886); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2892);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2894); if (failed) return node;
            // JPQL.g:534:7: ( AS )?
            int alt40=2;
            int LA40_0 = input.LA(1);
            
            if ( (LA40_0==AS) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // JPQL.g:534:8: AS
                    {
                    match(input,AS,FOLLOW_AS_in_collectionMemberDeclaration2904); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_collectionMemberDeclaration2910); if (failed) return node;
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
    // JPQL.g:541:1: collectionValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object collectionValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:543:7: (n= pathExpression )
            // JPQL.g:543:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_collectionValuedPathExpression2948);
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
    // JPQL.g:546:1: associationPathExpression returns [Object node] : n= pathExpression ;
    public final Object associationPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:548:7: (n= pathExpression )
            // JPQL.g:548:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_associationPathExpression2980);
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
    // JPQL.g:551:1: joinAssociationPathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object joinAssociationPathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
        
            node = null; 
    
        try {
            // JPQL.g:555:8: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // JPQL.g:555:8: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_joinAssociationPathExpression3013);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:556:9: (d= DOT right= attribute )+
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
            	    // JPQL.g:556:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_joinAssociationPathExpression3028); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_joinAssociationPathExpression3034);
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
    // $ANTLR end joinAssociationPathExpression

    
    // $ANTLR start singleValuedPathExpression
    // JPQL.g:561:1: singleValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object singleValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:563:7: (n= pathExpression )
            // JPQL.g:563:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_singleValuedPathExpression3090);
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
    // JPQL.g:566:1: stateFieldPathExpression returns [Object node] : n= pathExpression ;
    public final Object stateFieldPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:568:7: (n= pathExpression )
            // JPQL.g:568:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_stateFieldPathExpression3122);
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
    // JPQL.g:571:1: pathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object pathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:575:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // JPQL.g:575:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExpression3154);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:576:9: (d= DOT right= attribute )+
            int cnt42=0;
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);
                
                if ( (LA42_0==DOT) ) {
                    alt42=1;
                }
                
            
                switch (alt42) {
            	case 1 :
            	    // JPQL.g:576:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExpression3169); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExpression3175);
            	    right=attribute();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      
            	                      node = factory.newDot(d.getLine(), d.getCharPositionInLine(), node, right); 
            	                  
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt42 >= 1 ) break loop42;
            	    if (backtracking>0) {failed=true; return node;}
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
    // $ANTLR end pathExpression

    
    // $ANTLR start attribute
    // JPQL.g:587:1: attribute returns [Object node] : i= . ;
    public final Object attribute() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g:590:7: (i= . )
            // JPQL.g:590:7: i= .
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
    // JPQL.g:597:1: variableAccessOrTypeConstant returns [Object node] : i= IDENT ;
    public final Object variableAccessOrTypeConstant() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g:599:7: (i= IDENT )
            // JPQL.g:599:7: i= IDENT
            {
            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_variableAccessOrTypeConstant3271); if (failed) return node;
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
    // JPQL.g:603:1: whereClause returns [Object node] : t= WHERE n= conditionalExpression ;
    public final Object whereClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:605:7: (t= WHERE n= conditionalExpression )
            // JPQL.g:605:7: t= WHERE n= conditionalExpression
            {
            t=(Token)input.LT(1);
            match(input,WHERE,FOLLOW_WHERE_in_whereClause3309); if (failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whereClause3315);
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
    // JPQL.g:611:1: conditionalExpression returns [Object node] : n= conditionalTerm (t= OR right= conditionalTerm )* ;
    public final Object conditionalExpression() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:615:7: (n= conditionalTerm (t= OR right= conditionalTerm )* )
            // JPQL.g:615:7: n= conditionalTerm (t= OR right= conditionalTerm )*
            {
            pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3357);
            n=conditionalTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:616:9: (t= OR right= conditionalTerm )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                
                if ( (LA43_0==OR) ) {
                    alt43=1;
                }
                
            
                switch (alt43) {
            	case 1 :
            	    // JPQL.g:616:10: t= OR right= conditionalTerm
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,OR,FOLLOW_OR_in_conditionalExpression3372); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3378);
            	    right=conditionalTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
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
    // $ANTLR end conditionalExpression

    
    // $ANTLR start conditionalTerm
    // JPQL.g:621:1: conditionalTerm returns [Object node] : n= conditionalFactor (t= AND right= conditionalFactor )* ;
    public final Object conditionalTerm() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:625:7: (n= conditionalFactor (t= AND right= conditionalFactor )* )
            // JPQL.g:625:7: n= conditionalFactor (t= AND right= conditionalFactor )*
            {
            pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3433);
            n=conditionalFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:626:9: (t= AND right= conditionalFactor )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);
                
                if ( (LA44_0==AND) ) {
                    alt44=1;
                }
                
            
                switch (alt44) {
            	case 1 :
            	    // JPQL.g:626:10: t= AND right= conditionalFactor
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,AND,FOLLOW_AND_in_conditionalTerm3448); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3454);
            	    right=conditionalFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
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
    // $ANTLR end conditionalTerm

    
    // $ANTLR start conditionalFactor
    // JPQL.g:631:1: conditionalFactor returns [Object node] : (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) ;
    public final Object conditionalFactor() throws RecognitionException {

        Object node = null;
    
        Token n=null;
        Object n1 = null;
        
    
         node = null; 
        try {
            // JPQL.g:633:7: ( (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) )
            // JPQL.g:633:7: (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            {
            // JPQL.g:633:7: (n= NOT )?
            int alt45=2;
            int LA45_0 = input.LA(1);
            
            if ( (LA45_0==NOT) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // JPQL.g:633:8: n= NOT
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_conditionalFactor3509); if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:634:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            int alt46=2;
            int LA46_0 = input.LA(1);
            
            if ( (LA46_0==ABS||LA46_0==AVG||(LA46_0>=CASE && LA46_0<=CURRENT_TIMESTAMP)||LA46_0==FALSE||LA46_0==FUNC||LA46_0==INDEX||LA46_0==KEY||LA46_0==LENGTH||(LA46_0>=LOCATE && LA46_0<=MAX)||(LA46_0>=MIN && LA46_0<=MOD)||LA46_0==NULLIF||(LA46_0>=SIZE && LA46_0<=SQRT)||(LA46_0>=SUBSTRING && LA46_0<=SUM)||(LA46_0>=TRIM && LA46_0<=TYPE)||(LA46_0>=UPPER && LA46_0<=VALUE)||LA46_0==IDENT||LA46_0==LEFT_ROUND_BRACKET||(LA46_0>=PLUS && LA46_0<=MINUS)||(LA46_0>=INTEGER_LITERAL && LA46_0<=NAMED_PARAM)) ) {
                alt46=1;
            }
            else if ( (LA46_0==EXISTS) ) {
                alt46=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("634:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )", 46, 0, input);
            
                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // JPQL.g:634:11: n1= conditionalPrimary
                    {
                    pushFollow(FOLLOW_conditionalPrimary_in_conditionalFactor3528);
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
                    // JPQL.g:641:11: n1= existsExpression[(n!=null)]
                    {
                    pushFollow(FOLLOW_existsExpression_in_conditionalFactor3557);
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
    // JPQL.g:645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );
    public final Object conditionalPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:647:7: ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression )
            int alt47=2;
            int LA47_0 = input.LA(1);
            
            if ( (LA47_0==LEFT_ROUND_BRACKET) ) {
                int LA47_1 = input.LA(2);
                
                if ( (LA47_1==NOT) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==LEFT_ROUND_BRACKET) ) {
                    int LA47_46 = input.LA(3);
                    
                    if ( (LA47_46==SELECT) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==PLUS) ) {
                        int LA47_93 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 93, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==MINUS) ) {
                        int LA47_94 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 94, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==AVG) ) {
                        int LA47_95 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 95, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==MAX) ) {
                        int LA47_96 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 96, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==MIN) ) {
                        int LA47_97 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 97, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==SUM) ) {
                        int LA47_98 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 98, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==COUNT) ) {
                        int LA47_99 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 99, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==IDENT) ) {
                        int LA47_100 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 100, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==KEY) ) {
                        int LA47_101 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 101, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==VALUE) ) {
                        int LA47_102 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 102, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==POSITIONAL_PARAM) ) {
                        int LA47_103 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 103, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==NAMED_PARAM) ) {
                        int LA47_104 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 104, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==CASE) ) {
                        int LA47_105 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 105, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==COALESCE) ) {
                        int LA47_106 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 106, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==NULLIF) ) {
                        int LA47_107 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 107, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==ABS) ) {
                        int LA47_108 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 108, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==LENGTH) ) {
                        int LA47_109 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 109, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==MOD) ) {
                        int LA47_110 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 110, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==SQRT) ) {
                        int LA47_111 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 111, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==LOCATE) ) {
                        int LA47_112 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 112, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==SIZE) ) {
                        int LA47_113 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 113, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==INDEX) ) {
                        int LA47_114 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 114, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==FUNC) ) {
                        int LA47_115 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 115, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==LEFT_ROUND_BRACKET) ) {
                        int LA47_116 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 116, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==INTEGER_LITERAL) ) {
                        int LA47_117 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 117, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==LONG_LITERAL) ) {
                        int LA47_118 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 118, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==FLOAT_LITERAL) ) {
                        int LA47_119 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 119, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==DOUBLE_LITERAL) ) {
                        int LA47_120 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 120, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_46==NOT) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==CURRENT_DATE) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==CURRENT_TIME) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==CURRENT_TIMESTAMP) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==CONCAT) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==SUBSTRING) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==TRIM) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==UPPER) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==LOWER) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==STRING_LITERAL_DOUBLE_QUOTED) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==STRING_LITERAL_SINGLE_QUOTED) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==TRUE) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==FALSE) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==DATE_LITERAL) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==TIME_LITERAL) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==TIMESTAMP_LITERAL) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==TYPE) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_46==EXISTS) && (synpred1())) {
                        alt47=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 46, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==PLUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
                        {
                        int LA47_139 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 139, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA47_140 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 140, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA47_141 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 141, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
                        {
                        int LA47_142 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 142, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
                        {
                        int LA47_143 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 143, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA47_144 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 144, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA47_145 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 145, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA47_146 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 146, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
                        {
                        int LA47_147 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 147, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
                        {
                        int LA47_148 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 148, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CASE:
                        {
                        int LA47_149 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 149, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COALESCE:
                        {
                        int LA47_150 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 150, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NULLIF:
                        {
                        int LA47_151 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 151, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
                        {
                        int LA47_152 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 152, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
                        {
                        int LA47_153 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 153, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
                        {
                        int LA47_154 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 154, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
                        {
                        int LA47_155 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 155, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
                        {
                        int LA47_156 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 156, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
                        {
                        int LA47_157 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 157, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INDEX:
                        {
                        int LA47_158 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 158, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FUNC:
                        {
                        int LA47_159 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 159, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
                        int LA47_160 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 160, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
                        {
                        int LA47_161 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 161, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
                        {
                        int LA47_162 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 162, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
                        {
                        int LA47_163 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 163, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
                        {
                        int LA47_164 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 164, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 47, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA47_1==MINUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
                        {
                        int LA47_165 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 165, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA47_166 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 166, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA47_167 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 167, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
                        {
                        int LA47_168 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 168, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
                        {
                        int LA47_169 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 169, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA47_170 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 170, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA47_171 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 171, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA47_172 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 172, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
                        {
                        int LA47_173 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 173, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
                        {
                        int LA47_174 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 174, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CASE:
                        {
                        int LA47_175 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 175, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COALESCE:
                        {
                        int LA47_176 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 176, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NULLIF:
                        {
                        int LA47_177 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 177, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
                        {
                        int LA47_178 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 178, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
                        {
                        int LA47_179 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 179, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
                        {
                        int LA47_180 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 180, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
                        {
                        int LA47_181 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 181, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
                        {
                        int LA47_182 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 182, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
                        {
                        int LA47_183 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 183, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INDEX:
                        {
                        int LA47_184 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 184, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FUNC:
                        {
                        int LA47_185 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 185, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
                        int LA47_186 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 186, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
                        {
                        int LA47_187 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 187, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
                        {
                        int LA47_188 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 188, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
                        {
                        int LA47_189 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 189, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
                        {
                        int LA47_190 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 190, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 48, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA47_1==AVG) ) {
                    int LA47_49 = input.LA(3);
                    
                    if ( (LA47_49==LEFT_ROUND_BRACKET) ) {
                        int LA47_191 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 191, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 49, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==MAX) ) {
                    int LA47_50 = input.LA(3);
                    
                    if ( (LA47_50==LEFT_ROUND_BRACKET) ) {
                        int LA47_192 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 192, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 50, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==MIN) ) {
                    int LA47_51 = input.LA(3);
                    
                    if ( (LA47_51==LEFT_ROUND_BRACKET) ) {
                        int LA47_193 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 193, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 51, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==SUM) ) {
                    int LA47_52 = input.LA(3);
                    
                    if ( (LA47_52==LEFT_ROUND_BRACKET) ) {
                        int LA47_194 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 194, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 52, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==COUNT) ) {
                    int LA47_53 = input.LA(3);
                    
                    if ( (LA47_53==LEFT_ROUND_BRACKET) ) {
                        int LA47_195 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 195, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 53, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==IDENT) ) {
                    int LA47_54 = input.LA(3);
                    
                    if ( (LA47_54==DOT) ) {
                        int LA47_196 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 196, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_54==MULTIPLY) ) {
                        int LA47_197 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 197, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_54==DIVIDE) ) {
                        int LA47_198 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 198, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_54==PLUS) ) {
                        int LA47_199 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 199, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_54==MINUS) ) {
                        int LA47_200 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 200, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_54==RIGHT_ROUND_BRACKET) ) {
                        alt47=2;
                    }
                    else if ( (LA47_54==EQUALS) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_54==NOT_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_54==GREATER_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_54==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_54==LESS_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_54==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_54==NOT) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_54==BETWEEN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_54==LIKE) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_54==IN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_54==MEMBER) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_54==IS) && (synpred1())) {
                        alt47=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 54, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==KEY) ) {
                    int LA47_55 = input.LA(3);
                    
                    if ( (LA47_55==LEFT_ROUND_BRACKET) ) {
                        int LA47_214 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 214, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 55, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==VALUE) ) {
                    int LA47_56 = input.LA(3);
                    
                    if ( (LA47_56==LEFT_ROUND_BRACKET) ) {
                        int LA47_215 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 215, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 56, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==POSITIONAL_PARAM) ) {
                    int LA47_57 = input.LA(3);
                    
                    if ( (LA47_57==MULTIPLY) ) {
                        int LA47_216 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 216, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_57==DIVIDE) ) {
                        int LA47_217 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 217, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_57==PLUS) ) {
                        int LA47_218 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 218, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_57==MINUS) ) {
                        int LA47_219 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 219, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_57==RIGHT_ROUND_BRACKET) ) {
                        alt47=2;
                    }
                    else if ( (LA47_57==EQUALS) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_57==NOT_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_57==GREATER_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_57==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_57==LESS_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_57==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_57==NOT) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_57==BETWEEN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_57==LIKE) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_57==IN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_57==MEMBER) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_57==IS) && (synpred1())) {
                        alt47=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 57, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==NAMED_PARAM) ) {
                    int LA47_58 = input.LA(3);
                    
                    if ( (LA47_58==MULTIPLY) ) {
                        int LA47_233 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 233, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_58==DIVIDE) ) {
                        int LA47_234 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 234, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_58==PLUS) ) {
                        int LA47_235 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 235, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_58==MINUS) ) {
                        int LA47_236 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 236, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_58==EQUALS) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==NOT_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==GREATER_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==LESS_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==NOT) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==BETWEEN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==LIKE) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==IN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==MEMBER) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==IS) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_58==RIGHT_ROUND_BRACKET) ) {
                        alt47=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 58, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==CASE) ) {
                    switch ( input.LA(3) ) {
                    case IDENT:
                        {
                        int LA47_250 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 250, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA47_251 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 251, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA47_252 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 252, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TYPE:
                        {
                        int LA47_253 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 253, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case WHEN:
                        {
                        int LA47_254 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 254, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 59, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA47_1==COALESCE) ) {
                    int LA47_60 = input.LA(3);
                    
                    if ( (LA47_60==LEFT_ROUND_BRACKET) ) {
                        int LA47_255 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 255, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 60, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==NULLIF) ) {
                    int LA47_61 = input.LA(3);
                    
                    if ( (LA47_61==LEFT_ROUND_BRACKET) ) {
                        int LA47_256 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 256, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 61, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==ABS) ) {
                    int LA47_62 = input.LA(3);
                    
                    if ( (LA47_62==LEFT_ROUND_BRACKET) ) {
                        int LA47_257 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 257, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 62, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==LENGTH) ) {
                    int LA47_63 = input.LA(3);
                    
                    if ( (LA47_63==LEFT_ROUND_BRACKET) ) {
                        int LA47_258 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 258, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 63, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==MOD) ) {
                    int LA47_64 = input.LA(3);
                    
                    if ( (LA47_64==LEFT_ROUND_BRACKET) ) {
                        int LA47_259 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 259, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 64, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==SQRT) ) {
                    int LA47_65 = input.LA(3);
                    
                    if ( (LA47_65==LEFT_ROUND_BRACKET) ) {
                        int LA47_260 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 260, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 65, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==LOCATE) ) {
                    int LA47_66 = input.LA(3);
                    
                    if ( (LA47_66==LEFT_ROUND_BRACKET) ) {
                        int LA47_261 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 261, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 66, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==SIZE) ) {
                    int LA47_67 = input.LA(3);
                    
                    if ( (LA47_67==LEFT_ROUND_BRACKET) ) {
                        int LA47_262 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 262, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 67, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==INDEX) ) {
                    int LA47_68 = input.LA(3);
                    
                    if ( (LA47_68==LEFT_ROUND_BRACKET) ) {
                        int LA47_263 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 263, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 68, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==FUNC) ) {
                    int LA47_69 = input.LA(3);
                    
                    if ( (LA47_69==LEFT_ROUND_BRACKET) ) {
                        int LA47_264 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 264, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 69, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==INTEGER_LITERAL) ) {
                    int LA47_70 = input.LA(3);
                    
                    if ( (LA47_70==MULTIPLY) ) {
                        int LA47_265 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 265, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_70==DIVIDE) ) {
                        int LA47_266 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 266, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_70==PLUS) ) {
                        int LA47_267 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 267, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_70==MINUS) ) {
                        int LA47_268 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 268, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_70==EQUALS) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==NOT_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==GREATER_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==LESS_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==NOT) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==BETWEEN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==LIKE) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==IN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==MEMBER) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==IS) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_70==RIGHT_ROUND_BRACKET) ) {
                        alt47=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 70, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==LONG_LITERAL) ) {
                    int LA47_71 = input.LA(3);
                    
                    if ( (LA47_71==MULTIPLY) ) {
                        int LA47_282 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 282, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_71==DIVIDE) ) {
                        int LA47_283 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 283, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_71==PLUS) ) {
                        int LA47_284 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 284, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_71==MINUS) ) {
                        int LA47_285 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 285, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_71==RIGHT_ROUND_BRACKET) ) {
                        alt47=2;
                    }
                    else if ( (LA47_71==EQUALS) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_71==NOT_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_71==GREATER_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_71==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_71==LESS_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_71==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_71==NOT) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_71==BETWEEN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_71==LIKE) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_71==IN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_71==MEMBER) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_71==IS) && (synpred1())) {
                        alt47=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 71, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==FLOAT_LITERAL) ) {
                    int LA47_72 = input.LA(3);
                    
                    if ( (LA47_72==MULTIPLY) ) {
                        int LA47_299 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 299, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_72==DIVIDE) ) {
                        int LA47_300 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 300, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_72==PLUS) ) {
                        int LA47_301 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 301, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_72==MINUS) ) {
                        int LA47_302 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 302, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_72==RIGHT_ROUND_BRACKET) ) {
                        alt47=2;
                    }
                    else if ( (LA47_72==EQUALS) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_72==NOT_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_72==GREATER_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_72==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_72==LESS_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_72==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_72==NOT) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_72==BETWEEN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_72==LIKE) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_72==IN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_72==MEMBER) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_72==IS) && (synpred1())) {
                        alt47=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 72, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==DOUBLE_LITERAL) ) {
                    int LA47_73 = input.LA(3);
                    
                    if ( (LA47_73==MULTIPLY) ) {
                        int LA47_316 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 316, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_73==DIVIDE) ) {
                        int LA47_317 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 317, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_73==PLUS) ) {
                        int LA47_318 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 318, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_73==MINUS) ) {
                        int LA47_319 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt47=1;
                        }
                        else if ( (true) ) {
                            alt47=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 319, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA47_73==RIGHT_ROUND_BRACKET) ) {
                        alt47=2;
                    }
                    else if ( (LA47_73==EQUALS) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_73==NOT_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_73==GREATER_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_73==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_73==LESS_THAN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_73==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_73==NOT) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_73==BETWEEN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_73==LIKE) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_73==IN) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_73==MEMBER) && (synpred1())) {
                        alt47=1;
                    }
                    else if ( (LA47_73==IS) && (synpred1())) {
                        alt47=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 73, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA47_1==CURRENT_DATE) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==CURRENT_TIME) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==CURRENT_TIMESTAMP) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==CONCAT) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==SUBSTRING) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==TRIM) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==UPPER) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==LOWER) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==STRING_LITERAL_DOUBLE_QUOTED) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==STRING_LITERAL_SINGLE_QUOTED) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==TRUE) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==FALSE) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==DATE_LITERAL) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==TIME_LITERAL) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==TIMESTAMP_LITERAL) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==TYPE) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==EXISTS) && (synpred1())) {
                    alt47=1;
                }
                else if ( (LA47_1==SELECT) ) {
                    alt47=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA47_0==ABS||LA47_0==AVG||(LA47_0>=CASE && LA47_0<=CURRENT_TIMESTAMP)||LA47_0==FALSE||LA47_0==FUNC||LA47_0==INDEX||LA47_0==KEY||LA47_0==LENGTH||(LA47_0>=LOCATE && LA47_0<=MAX)||(LA47_0>=MIN && LA47_0<=MOD)||LA47_0==NULLIF||(LA47_0>=SIZE && LA47_0<=SQRT)||(LA47_0>=SUBSTRING && LA47_0<=SUM)||(LA47_0>=TRIM && LA47_0<=TYPE)||(LA47_0>=UPPER && LA47_0<=VALUE)||LA47_0==IDENT||(LA47_0>=PLUS && LA47_0<=MINUS)||(LA47_0>=INTEGER_LITERAL && LA47_0<=NAMED_PARAM)) ) {
                alt47=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("645:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 47, 0, input);
            
                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // JPQL.g:647:7: ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3614); if (failed) return node;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalPrimary3620);
                    n=conditionalExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3622); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:649:7: n= simpleConditionalExpression
                    {
                    pushFollow(FOLLOW_simpleConditionalExpression_in_conditionalPrimary3636);
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
    // JPQL.g:652:1: simpleConditionalExpression returns [Object node] : (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] );
    public final Object simpleConditionalExpression() throws RecognitionException {

        Object node = null;
    
        Object left = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:656:7: (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] )
            int alt48=2;
            int LA48_0 = input.LA(1);
            
            if ( (LA48_0==ABS||LA48_0==AVG||(LA48_0>=CASE && LA48_0<=COALESCE)||LA48_0==COUNT||LA48_0==FUNC||LA48_0==INDEX||LA48_0==KEY||LA48_0==LENGTH||LA48_0==LOCATE||LA48_0==MAX||(LA48_0>=MIN && LA48_0<=MOD)||LA48_0==NULLIF||(LA48_0>=SIZE && LA48_0<=SQRT)||LA48_0==SUM||LA48_0==VALUE||LA48_0==IDENT||LA48_0==LEFT_ROUND_BRACKET||(LA48_0>=PLUS && LA48_0<=MINUS)||(LA48_0>=INTEGER_LITERAL && LA48_0<=DOUBLE_LITERAL)||(LA48_0>=POSITIONAL_PARAM && LA48_0<=NAMED_PARAM)) ) {
                alt48=1;
            }
            else if ( (LA48_0==CONCAT||(LA48_0>=CURRENT_DATE && LA48_0<=CURRENT_TIMESTAMP)||LA48_0==FALSE||LA48_0==LOWER||LA48_0==SUBSTRING||(LA48_0>=TRIM && LA48_0<=TYPE)||LA48_0==UPPER||(LA48_0>=STRING_LITERAL_DOUBLE_QUOTED && LA48_0<=TIMESTAMP_LITERAL)) ) {
                alt48=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("652:1: simpleConditionalExpression returns [Object node] : (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] );", 48, 0, input);
            
                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // JPQL.g:656:7: left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_simpleConditionalExpression3668);
                    left=arithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3674);
                    n=simpleConditionalExpressionRemainder(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:657:7: left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3689);
                    left=nonArithmeticScalarExpression();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3695);
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
    // JPQL.g:660:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );
    public final Object simpleConditionalExpressionRemainder(Object left) throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Token n2=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:662:7: (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] )
            int alt51=3;
            switch ( input.LA(1) ) {
            case EQUALS:
            case NOT_EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL_TO:
            case LESS_THAN:
            case LESS_THAN_EQUAL_TO:
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("660:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );", 51, 0, input);
            
                throw nvae;
            }
            
            switch (alt51) {
                case 1 :
                    // JPQL.g:662:7: n= comparisonExpression[left]
                    {
                    pushFollow(FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3730);
                    n=comparisonExpression(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:663:7: (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left]
                    {
                    // JPQL.g:663:7: (n1= NOT )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);
                    
                    if ( (LA49_0==NOT) ) {
                        alt49=1;
                    }
                    switch (alt49) {
                        case 1 :
                            // JPQL.g:663:8: n1= NOT
                            {
                            n1=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3744); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3752);
                    n=conditionWithNotExpression((n1!=null),  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:664:7: IS (n2= NOT )? n= isExpression[(n2!=null), left]
                    {
                    match(input,IS,FOLLOW_IS_in_simpleConditionalExpressionRemainder3763); if (failed) return node;
                    // JPQL.g:664:10: (n2= NOT )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);
                    
                    if ( (LA50_0==NOT) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // JPQL.g:664:11: n2= NOT
                            {
                            n2=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3768); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3776);
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
    // JPQL.g:667:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );
    public final Object conditionWithNotExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:669:7: (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("667:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );", 52, 0, input);
            
                throw nvae;
            }
            
            switch (alt52) {
                case 1 :
                    // JPQL.g:669:7: n= betweenExpression[not, left]
                    {
                    pushFollow(FOLLOW_betweenExpression_in_conditionWithNotExpression3811);
                    n=betweenExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:670:7: n= likeExpression[not, left]
                    {
                    pushFollow(FOLLOW_likeExpression_in_conditionWithNotExpression3826);
                    n=likeExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:671:7: n= inExpression[not, left]
                    {
                    pushFollow(FOLLOW_inExpression_in_conditionWithNotExpression3840);
                    n=inExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:672:7: n= collectionMemberExpression[not, left]
                    {
                    pushFollow(FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3854);
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
    // JPQL.g:675:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );
    public final Object isExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:677:7: (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] )
            int alt53=2;
            int LA53_0 = input.LA(1);
            
            if ( (LA53_0==NULL) ) {
                alt53=1;
            }
            else if ( (LA53_0==EMPTY) ) {
                alt53=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("675:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );", 53, 0, input);
            
                throw nvae;
            }
            switch (alt53) {
                case 1 :
                    // JPQL.g:677:7: n= nullComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_nullComparisonExpression_in_isExpression3889);
                    n=nullComparisonExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:678:7: n= emptyCollectionComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_emptyCollectionComparisonExpression_in_isExpression3904);
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
    // JPQL.g:681:1: betweenExpression[boolean not, Object left] returns [Object node] : t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression ;
    public final Object betweenExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object lowerBound = null;

        Object upperBound = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:685:7: (t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression )
            // JPQL.g:685:7: t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression
            {
            t=(Token)input.LT(1);
            match(input,BETWEEN,FOLLOW_BETWEEN_in_betweenExpression3937); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3951);
            lowerBound=arithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,AND,FOLLOW_AND_in_betweenExpression3953); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3959);
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
    // JPQL.g:693:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );
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
            // JPQL.g:701:8: (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET )
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
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("693:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );", 56, 1, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("693:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );", 56, 0, input);
            
                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // JPQL.g:701:8: t= IN n= inputParameter
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_inExpression4005); if (failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_inExpression4011);
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
                    // JPQL.g:706:9: t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_inExpression4038); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_inExpression4048); if (failed) return node;
                    // JPQL.g:708:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )
                    int alt55=2;
                    int LA55_0 = input.LA(1);
                    
                    if ( (LA55_0==IDENT||(LA55_0>=INTEGER_LITERAL && LA55_0<=STRING_LITERAL_SINGLE_QUOTED)||(LA55_0>=POSITIONAL_PARAM && LA55_0<=NAMED_PARAM)) ) {
                        alt55=1;
                    }
                    else if ( (LA55_0==SELECT) ) {
                        alt55=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("708:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )", 55, 0, input);
                    
                        throw nvae;
                    }
                    switch (alt55) {
                        case 1 :
                            // JPQL.g:708:11: itemNode= inItem ( COMMA itemNode= inItem )*
                            {
                            pushFollow(FOLLOW_inItem_in_inExpression4064);
                            itemNode=inItem();
                            _fsp--;
                            if (failed) return node;
                            if ( backtracking==0 ) {
                               ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                            }
                            // JPQL.g:709:13: ( COMMA itemNode= inItem )*
                            loop54:
                            do {
                                int alt54=2;
                                int LA54_0 = input.LA(1);
                                
                                if ( (LA54_0==COMMA) ) {
                                    alt54=1;
                                }
                                
                            
                                switch (alt54) {
                            	case 1 :
                            	    // JPQL.g:709:15: COMMA itemNode= inItem
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_inExpression4082); if (failed) return node;
                            	    pushFollow(FOLLOW_inItem_in_inExpression4088);
                            	    itemNode=inItem();
                            	    _fsp--;
                            	    if (failed) return node;
                            	    if ( backtracking==0 ) {
                            	       ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                            	    }
                            	    
                            	    }
                            	    break;
                            
                            	default :
                            	    break loop54;
                                }
                            } while (true);

                            if ( backtracking==0 ) {
                              
                                              node = factory.newIn(t.getLine(), t.getCharPositionInLine(),
                                                                   not, left, ((inExpression_scope)inExpression_stack.peek()).items);
                                          
                            }
                            
                            }
                            break;
                        case 2 :
                            // JPQL.g:714:11: subqueryNode= subquery
                            {
                            pushFollow(FOLLOW_subquery_in_inExpression4123);
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

                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4157); if (failed) return node;
                    
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
    // JPQL.g:723:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant );
    public final Object inItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:725:7: (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant )
            int alt57=4;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt57=1;
                }
                break;
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt57=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt57=3;
                }
                break;
            case IDENT:
                {
                alt57=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("723:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant );", 57, 0, input);
            
                throw nvae;
            }
            
            switch (alt57) {
                case 1 :
                    // JPQL.g:725:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_inItem4187);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:726:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_inItem4201);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:727:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_inItem4215);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:728:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_inItem4229);
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
    // JPQL.g:731:1: likeExpression[boolean not, Object left] returns [Object node] : t= LIKE pattern= likeValue (escapeChars= escape )? ;
    public final Object likeExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object pattern = null;

        Object escapeChars = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:735:7: (t= LIKE pattern= likeValue (escapeChars= escape )? )
            // JPQL.g:735:7: t= LIKE pattern= likeValue (escapeChars= escape )?
            {
            t=(Token)input.LT(1);
            match(input,LIKE,FOLLOW_LIKE_in_likeExpression4261); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_likeExpression4267);
            pattern=likeValue();
            _fsp--;
            if (failed) return node;
            // JPQL.g:736:9: (escapeChars= escape )?
            int alt58=2;
            int LA58_0 = input.LA(1);
            
            if ( (LA58_0==ESCAPE) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // JPQL.g:736:10: escapeChars= escape
                    {
                    pushFollow(FOLLOW_escape_in_likeExpression4282);
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
    // JPQL.g:743:1: escape returns [Object node] : t= ESCAPE escapeClause= likeValue ;
    public final Object escape() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object escapeClause = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:747:7: (t= ESCAPE escapeClause= likeValue )
            // JPQL.g:747:7: t= ESCAPE escapeClause= likeValue
            {
            t=(Token)input.LT(1);
            match(input,ESCAPE,FOLLOW_ESCAPE_in_escape4322); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_escape4328);
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
    // JPQL.g:751:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );
    public final Object likeValue() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:753:7: (n= literalString | n= inputParameter )
            int alt59=2;
            int LA59_0 = input.LA(1);
            
            if ( ((LA59_0>=STRING_LITERAL_DOUBLE_QUOTED && LA59_0<=STRING_LITERAL_SINGLE_QUOTED)) ) {
                alt59=1;
            }
            else if ( ((LA59_0>=POSITIONAL_PARAM && LA59_0<=NAMED_PARAM)) ) {
                alt59=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("751:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );", 59, 0, input);
            
                throw nvae;
            }
            switch (alt59) {
                case 1 :
                    // JPQL.g:753:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_likeValue4368);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:754:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_likeValue4382);
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
    // JPQL.g:757:1: nullComparisonExpression[boolean not, Object left] returns [Object node] : t= NULL ;
    public final Object nullComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g:759:7: (t= NULL )
            // JPQL.g:759:7: t= NULL
            {
            t=(Token)input.LT(1);
            match(input,NULL,FOLLOW_NULL_in_nullComparisonExpression4415); if (failed) return node;
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
    // JPQL.g:763:1: emptyCollectionComparisonExpression[boolean not, Object left] returns [Object node] : t= EMPTY ;
    public final Object emptyCollectionComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g:765:7: (t= EMPTY )
            // JPQL.g:765:7: t= EMPTY
            {
            t=(Token)input.LT(1);
            match(input,EMPTY,FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4456); if (failed) return node;
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
    // JPQL.g:769:1: collectionMemberExpression[boolean not, Object left] returns [Object node] : t= MEMBER ( OF )? n= collectionValuedPathExpression ;
    public final Object collectionMemberExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:771:7: (t= MEMBER ( OF )? n= collectionValuedPathExpression )
            // JPQL.g:771:7: t= MEMBER ( OF )? n= collectionValuedPathExpression
            {
            t=(Token)input.LT(1);
            match(input,MEMBER,FOLLOW_MEMBER_in_collectionMemberExpression4497); if (failed) return node;
            // JPQL.g:771:17: ( OF )?
            int alt60=2;
            int LA60_0 = input.LA(1);
            
            if ( (LA60_0==OF) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // JPQL.g:771:18: OF
                    {
                    match(input,OF,FOLLOW_OF_in_collectionMemberExpression4500); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4508);
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
    // JPQL.g:778:1: existsExpression[boolean not] returns [Object node] : t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET ;
    public final Object existsExpression(boolean not) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object subqueryNode = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:782:7: (t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET )
            // JPQL.g:782:7: t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_existsExpression4548); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4550); if (failed) return node;
            pushFollow(FOLLOW_subquery_in_existsExpression4556);
            subqueryNode=subquery();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4558); if (failed) return node;
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
    // JPQL.g:789:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );
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
            // JPQL.g:791:7: (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand )
            int alt61=6;
            switch ( input.LA(1) ) {
            case EQUALS:
                {
                alt61=1;
                }
                break;
            case NOT_EQUAL_TO:
                {
                alt61=2;
                }
                break;
            case GREATER_THAN:
                {
                alt61=3;
                }
                break;
            case GREATER_THAN_EQUAL_TO:
                {
                alt61=4;
                }
                break;
            case LESS_THAN:
                {
                alt61=5;
                }
                break;
            case LESS_THAN_EQUAL_TO:
                {
                alt61=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("789:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );", 61, 0, input);
            
                throw nvae;
            }
            
            switch (alt61) {
                case 1 :
                    // JPQL.g:791:7: t1= EQUALS n= comparisonExpressionRightOperand
                    {
                    t1=(Token)input.LT(1);
                    match(input,EQUALS,FOLLOW_EQUALS_in_comparisonExpression4598); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4604);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newEquals(t1.getLine(), t1.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:793:7: t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t2=(Token)input.LT(1);
                    match(input,NOT_EQUAL_TO,FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4625); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4631);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newNotEquals(t2.getLine(), t2.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:795:7: t3= GREATER_THAN n= comparisonExpressionRightOperand
                    {
                    t3=(Token)input.LT(1);
                    match(input,GREATER_THAN,FOLLOW_GREATER_THAN_in_comparisonExpression4652); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4658);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThan(t3.getLine(), t3.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:797:7: t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t4=(Token)input.LT(1);
                    match(input,GREATER_THAN_EQUAL_TO,FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4679); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4685);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThanEqual(t4.getLine(), t4.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:799:7: t5= LESS_THAN n= comparisonExpressionRightOperand
                    {
                    t5=(Token)input.LT(1);
                    match(input,LESS_THAN,FOLLOW_LESS_THAN_in_comparisonExpression4706); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4712);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newLessThan(t5.getLine(), t5.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:801:7: t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t6=(Token)input.LT(1);
                    match(input,LESS_THAN_EQUAL_TO,FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4733); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4739);
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
    // JPQL.g:805:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression );
    public final Object comparisonExpressionRightOperand() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:807:7: (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression )
            int alt62=3;
            switch ( input.LA(1) ) {
            case ABS:
            case AVG:
            case CASE:
            case COALESCE:
            case COUNT:
            case FUNC:
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
                alt62=1;
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
                alt62=2;
                }
                break;
            case ALL:
            case ANY:
            case SOME:
                {
                alt62=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("805:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression );", 62, 0, input);
            
                throw nvae;
            }
            
            switch (alt62) {
                case 1 :
                    // JPQL.g:807:7: n= arithmeticExpression
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4780);
                    n=arithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:808:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4794);
                    n=nonArithmeticScalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:809:7: n= anyOrAllExpression
                    {
                    pushFollow(FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4808);
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
    // JPQL.g:812:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object arithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:814:7: (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt63=2;
            int LA63_0 = input.LA(1);
            
            if ( (LA63_0==ABS||LA63_0==AVG||(LA63_0>=CASE && LA63_0<=COALESCE)||LA63_0==COUNT||LA63_0==FUNC||LA63_0==INDEX||LA63_0==KEY||LA63_0==LENGTH||LA63_0==LOCATE||LA63_0==MAX||(LA63_0>=MIN && LA63_0<=MOD)||LA63_0==NULLIF||(LA63_0>=SIZE && LA63_0<=SQRT)||LA63_0==SUM||LA63_0==VALUE||LA63_0==IDENT||(LA63_0>=PLUS && LA63_0<=MINUS)||(LA63_0>=INTEGER_LITERAL && LA63_0<=DOUBLE_LITERAL)||(LA63_0>=POSITIONAL_PARAM && LA63_0<=NAMED_PARAM)) ) {
                alt63=1;
            }
            else if ( (LA63_0==LEFT_ROUND_BRACKET) ) {
                int LA63_24 = input.LA(2);
                
                if ( (LA63_24==ABS||LA63_24==AVG||(LA63_24>=CASE && LA63_24<=COALESCE)||LA63_24==COUNT||LA63_24==FUNC||LA63_24==INDEX||LA63_24==KEY||LA63_24==LENGTH||LA63_24==LOCATE||LA63_24==MAX||(LA63_24>=MIN && LA63_24<=MOD)||LA63_24==NULLIF||(LA63_24>=SIZE && LA63_24<=SQRT)||LA63_24==SUM||LA63_24==VALUE||LA63_24==IDENT||LA63_24==LEFT_ROUND_BRACKET||(LA63_24>=PLUS && LA63_24<=MINUS)||(LA63_24>=INTEGER_LITERAL && LA63_24<=DOUBLE_LITERAL)||(LA63_24>=POSITIONAL_PARAM && LA63_24<=NAMED_PARAM)) ) {
                    alt63=1;
                }
                else if ( (LA63_24==SELECT) ) {
                    alt63=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("812:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 63, 24, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("812:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 63, 0, input);
            
                throw nvae;
            }
            switch (alt63) {
                case 1 :
                    // JPQL.g:814:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4840);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:815:7: LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4850); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_arithmeticExpression4856);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4858); if (failed) return node;
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
    // JPQL.g:818:1: simpleArithmeticExpression returns [Object node] : n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* ;
    public final Object simpleArithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:822:7: (n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* )
            // JPQL.g:822:7: n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            {
            pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4890);
            n=arithmeticTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:823:9: (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            loop64:
            do {
                int alt64=3;
                int LA64_0 = input.LA(1);
                
                if ( (LA64_0==PLUS) ) {
                    alt64=1;
                }
                else if ( (LA64_0==MINUS) ) {
                    alt64=2;
                }
                
            
                switch (alt64) {
            	case 1 :
            	    // JPQL.g:823:11: p= PLUS right= arithmeticTerm
            	    {
            	    p=(Token)input.LT(1);
            	    match(input,PLUS,FOLLOW_PLUS_in_simpleArithmeticExpression4906); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4912);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newPlus(p.getLine(), p.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:825:11: m= MINUS right= arithmeticTerm
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MINUS,FOLLOW_MINUS_in_simpleArithmeticExpression4941); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4947);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMinus(m.getLine(), m.getCharPositionInLine(), node, right); 
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
    // $ANTLR end simpleArithmeticExpression

    
    // $ANTLR start arithmeticTerm
    // JPQL.g:830:1: arithmeticTerm returns [Object node] : n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* ;
    public final Object arithmeticTerm() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:834:7: (n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* )
            // JPQL.g:834:7: n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            {
            pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm5004);
            n=arithmeticFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:835:9: (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            loop65:
            do {
                int alt65=3;
                int LA65_0 = input.LA(1);
                
                if ( (LA65_0==MULTIPLY) ) {
                    alt65=1;
                }
                else if ( (LA65_0==DIVIDE) ) {
                    alt65=2;
                }
                
            
                switch (alt65) {
            	case 1 :
            	    // JPQL.g:835:11: m= MULTIPLY right= arithmeticFactor
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MULTIPLY,FOLLOW_MULTIPLY_in_arithmeticTerm5020); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm5026);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMultiply(m.getLine(), m.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:837:11: d= DIVIDE right= arithmeticFactor
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DIVIDE,FOLLOW_DIVIDE_in_arithmeticTerm5055); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm5061);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newDivide(d.getLine(), d.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop65;
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
    // JPQL.g:842:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );
    public final Object arithmeticFactor() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:844:7: (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary )
            int alt66=3;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt66=1;
                }
                break;
            case MINUS:
                {
                alt66=2;
                }
                break;
            case ABS:
            case AVG:
            case CASE:
            case COALESCE:
            case COUNT:
            case FUNC:
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
                alt66=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("842:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );", 66, 0, input);
            
                throw nvae;
            }
            
            switch (alt66) {
                case 1 :
                    // JPQL.g:844:7: p= PLUS n= arithmeticPrimary
                    {
                    p=(Token)input.LT(1);
                    match(input,PLUS,FOLLOW_PLUS_in_arithmeticFactor5115); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5122);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newUnaryPlus(p.getLine(), p.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:846:7: m= MINUS n= arithmeticPrimary
                    {
                    m=(Token)input.LT(1);
                    match(input,MINUS,FOLLOW_MINUS_in_arithmeticFactor5144); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5150);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newUnaryMinus(m.getLine(), m.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:848:7: n= arithmeticPrimary
                    {
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5174);
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
    // JPQL.g:851:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric );
    public final Object arithmeticPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:853:7: ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric )
            int alt67=7;
            switch ( input.LA(1) ) {
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt67=1;
                }
                break;
            case KEY:
            case VALUE:
            case IDENT:
                {
                alt67=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt67=3;
                }
                break;
            case CASE:
            case COALESCE:
            case NULLIF:
                {
                alt67=4;
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
                alt67=5;
                }
                break;
            case LEFT_ROUND_BRACKET:
                {
                alt67=6;
                }
                break;
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt67=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("851:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric );", 67, 0, input);
            
                throw nvae;
            }
            
            switch (alt67) {
                case 1 :
                    // JPQL.g:853:7: {...}?n= aggregateExpression
                    {
                    if ( !( aggregatesAllowed() ) ) {
                        if (backtracking>0) {failed=true; return node;}
                        throw new FailedPredicateException(input, "arithmeticPrimary", " aggregatesAllowed() ");
                    }
                    pushFollow(FOLLOW_aggregateExpression_in_arithmeticPrimary5208);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:854:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5222);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:855:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_arithmeticPrimary5236);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:856:7: n= caseExpression
                    {
                    pushFollow(FOLLOW_caseExpression_in_arithmeticPrimary5250);
                    n=caseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:857:7: n= functionsReturningNumerics
                    {
                    pushFollow(FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5264);
                    n=functionsReturningNumerics();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:858:7: LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5274); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5280);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5282); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 7 :
                    // JPQL.g:859:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_arithmeticPrimary5296);
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
    // JPQL.g:862:1: scalarExpression returns [Object node] : (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression );
    public final Object scalarExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null; 
        try {
            // JPQL.g:864:7: (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression )
            int alt68=2;
            int LA68_0 = input.LA(1);
            
            if ( (LA68_0==ABS||LA68_0==AVG||(LA68_0>=CASE && LA68_0<=COALESCE)||LA68_0==COUNT||LA68_0==FUNC||LA68_0==INDEX||LA68_0==KEY||LA68_0==LENGTH||LA68_0==LOCATE||LA68_0==MAX||(LA68_0>=MIN && LA68_0<=MOD)||LA68_0==NULLIF||(LA68_0>=SIZE && LA68_0<=SQRT)||LA68_0==SUM||LA68_0==VALUE||LA68_0==IDENT||LA68_0==LEFT_ROUND_BRACKET||(LA68_0>=PLUS && LA68_0<=MINUS)||(LA68_0>=INTEGER_LITERAL && LA68_0<=DOUBLE_LITERAL)||(LA68_0>=POSITIONAL_PARAM && LA68_0<=NAMED_PARAM)) ) {
                alt68=1;
            }
            else if ( (LA68_0==CONCAT||(LA68_0>=CURRENT_DATE && LA68_0<=CURRENT_TIMESTAMP)||LA68_0==FALSE||LA68_0==LOWER||LA68_0==SUBSTRING||(LA68_0>=TRIM && LA68_0<=TYPE)||LA68_0==UPPER||(LA68_0>=STRING_LITERAL_DOUBLE_QUOTED && LA68_0<=TIMESTAMP_LITERAL)) ) {
                alt68=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("862:1: scalarExpression returns [Object node] : (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression );", 68, 0, input);
            
                throw nvae;
            }
            switch (alt68) {
                case 1 :
                    // JPQL.g:864:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_scalarExpression5328);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:865:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5343);
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
    // JPQL.g:868:1: nonArithmeticScalarExpression returns [Object node] : (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression );
    public final Object nonArithmeticScalarExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null; 
        try {
            // JPQL.g:870:7: (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression )
            int alt69=6;
            switch ( input.LA(1) ) {
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
                {
                alt69=1;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt69=2;
                }
                break;
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt69=3;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt69=4;
                }
                break;
            case DATE_LITERAL:
            case TIME_LITERAL:
            case TIMESTAMP_LITERAL:
                {
                alt69=5;
                }
                break;
            case TYPE:
                {
                alt69=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("868:1: nonArithmeticScalarExpression returns [Object node] : (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression );", 69, 0, input);
            
                throw nvae;
            }
            
            switch (alt69) {
                case 1 :
                    // JPQL.g:870:7: n= functionsReturningDatetime
                    {
                    pushFollow(FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5375);
                    n=functionsReturningDatetime();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:871:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5389);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:872:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_nonArithmeticScalarExpression5403);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:873:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5417);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:874:7: n= literalTemporal
                    {
                    pushFollow(FOLLOW_literalTemporal_in_nonArithmeticScalarExpression5431);
                    n=literalTemporal();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:875:7: n= entityTypeExpression
                    {
                    pushFollow(FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5445);
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
    // JPQL.g:878:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object anyOrAllExpression() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token y=null;
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:880:7: (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt70=3;
            switch ( input.LA(1) ) {
            case ALL:
                {
                alt70=1;
                }
                break;
            case ANY:
                {
                alt70=2;
                }
                break;
            case SOME:
                {
                alt70=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("878:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 70, 0, input);
            
                throw nvae;
            }
            
            switch (alt70) {
                case 1 :
                    // JPQL.g:880:7: a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,ALL,FOLLOW_ALL_in_anyOrAllExpression5475); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5477); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5483);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5485); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAll(a.getLine(), a.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:882:7: y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    y=(Token)input.LT(1);
                    match(input,ANY,FOLLOW_ANY_in_anyOrAllExpression5505); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5507); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5513);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5515); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAny(y.getLine(), y.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:884:7: s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    s=(Token)input.LT(1);
                    match(input,SOME,FOLLOW_SOME_in_anyOrAllExpression5535); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5537); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5543);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5545); if (failed) return node;
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
    // JPQL.g:888:1: entityTypeExpression returns [Object node] : n= typeDiscriminator ;
    public final Object entityTypeExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:890:7: (n= typeDiscriminator )
            // JPQL.g:890:7: n= typeDiscriminator
            {
            pushFollow(FOLLOW_typeDiscriminator_in_entityTypeExpression5585);
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
    // JPQL.g:893:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );
    public final Object typeDiscriminator() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token c=null;
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:895:7: (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET )
            int alt71=2;
            int LA71_0 = input.LA(1);
            
            if ( (LA71_0==TYPE) ) {
                int LA71_1 = input.LA(2);
                
                if ( (LA71_1==LEFT_ROUND_BRACKET) ) {
                    int LA71_2 = input.LA(3);
                    
                    if ( ((LA71_2>=POSITIONAL_PARAM && LA71_2<=NAMED_PARAM)) ) {
                        alt71=2;
                    }
                    else if ( (LA71_2==KEY||LA71_2==VALUE||LA71_2==IDENT) ) {
                        alt71=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("893:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 71, 2, input);
                    
                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("893:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 71, 1, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("893:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 71, 0, input);
            
                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // JPQL.g:895:7: a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5618); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5620); if (failed) return node;
                    pushFollow(FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5626);
                    n=variableOrSingleValuedPath();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5628); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newType(a.getLine(), a.getCharPositionInLine(), n);
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:896:7: c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET
                    {
                    c=(Token)input.LT(1);
                    match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5643); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5645); if (failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_typeDiscriminator5651);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5653); if (failed) return node;
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
    // JPQL.g:899:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );
    public final Object caseExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:901:6: (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression )
            int alt72=4;
            switch ( input.LA(1) ) {
            case CASE:
                {
                int LA72_1 = input.LA(2);
                
                if ( (LA72_1==WHEN) ) {
                    alt72=2;
                }
                else if ( (LA72_1==KEY||LA72_1==TYPE||LA72_1==VALUE||LA72_1==IDENT) ) {
                    alt72=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("899:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );", 72, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case COALESCE:
                {
                alt72=3;
                }
                break;
            case NULLIF:
                {
                alt72=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("899:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );", 72, 0, input);
            
                throw nvae;
            }
            
            switch (alt72) {
                case 1 :
                    // JPQL.g:901:6: n= simpleCaseExpression
                    {
                    pushFollow(FOLLOW_simpleCaseExpression_in_caseExpression5688);
                    n=simpleCaseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:902:6: n= generalCaseExpression
                    {
                    pushFollow(FOLLOW_generalCaseExpression_in_caseExpression5701);
                    n=generalCaseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:903:6: n= coalesceExpression
                    {
                    pushFollow(FOLLOW_coalesceExpression_in_caseExpression5714);
                    n=coalesceExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:904:6: n= nullIfExpression
                    {
                    pushFollow(FOLLOW_nullIfExpression_in_caseExpression5727);
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
    // JPQL.g:907:1: simpleCaseExpression returns [Object node] : a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END ;
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
            // JPQL.g:915:6: (a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END )
            // JPQL.g:915:6: a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END
            {
            a=(Token)input.LT(1);
            match(input,CASE,FOLLOW_CASE_in_simpleCaseExpression5765); if (failed) return node;
            pushFollow(FOLLOW_caseOperand_in_simpleCaseExpression5771);
            c=caseOperand();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5777);
            w=simpleWhenClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens.add(w);
            }
            // JPQL.g:915:97: (w= simpleWhenClause )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);
                
                if ( (LA73_0==WHEN) ) {
                    alt73=1;
                }
                
            
                switch (alt73) {
            	case 1 :
            	    // JPQL.g:915:98: w= simpleWhenClause
            	    {
            	    pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5786);
            	    w=simpleWhenClause();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens.add(w);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop73;
                }
            } while (true);

            match(input,ELSE,FOLLOW_ELSE_in_simpleCaseExpression5792); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleCaseExpression5798);
            e=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,END,FOLLOW_END_in_simpleCaseExpression5800); if (failed) return node;
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
    // JPQL.g:922:1: generalCaseExpression returns [Object node] : a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END ;
    public final Object generalCaseExpression() throws RecognitionException {
        generalCaseExpression_stack.push(new generalCaseExpression_scope());

        Object node = null;
    
        Token a=null;
        Object w = null;

        Object e = null;
        
    
        
            node = null;
            ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens = new ArrayList();
    
        try {
            // JPQL.g:930:6: (a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END )
            // JPQL.g:930:6: a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END
            {
            a=(Token)input.LT(1);
            match(input,CASE,FOLLOW_CASE_in_generalCaseExpression5844); if (failed) return node;
            pushFollow(FOLLOW_whenClause_in_generalCaseExpression5850);
            w=whenClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens.add(w);
            }
            // JPQL.g:930:76: (w= whenClause )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);
                
                if ( (LA74_0==WHEN) ) {
                    alt74=1;
                }
                
            
                switch (alt74) {
            	case 1 :
            	    // JPQL.g:930:77: w= whenClause
            	    {
            	    pushFollow(FOLLOW_whenClause_in_generalCaseExpression5859);
            	    w=whenClause();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens.add(w);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop74;
                }
            } while (true);

            match(input,ELSE,FOLLOW_ELSE_in_generalCaseExpression5865); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_generalCaseExpression5871);
            e=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,END,FOLLOW_END_in_generalCaseExpression5873); if (failed) return node;
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
    // JPQL.g:937:1: coalesceExpression returns [Object node] : c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET ;
    public final Object coalesceExpression() throws RecognitionException {
        coalesceExpression_stack.push(new coalesceExpression_scope());

        Object node = null;
    
        Token c=null;
        Object p = null;

        Object s = null;
        
    
        
            node = null;
            ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries = new ArrayList();
    
        try {
            // JPQL.g:945:6: (c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET )
            // JPQL.g:945:6: c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET
            {
            c=(Token)input.LT(1);
            match(input,COALESCE,FOLLOW_COALESCE_in_coalesceExpression5917); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5919); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5925);
            p=scalarExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries.add(p);
            }
            // JPQL.g:945:106: ( COMMA s= scalarExpression )+
            int cnt75=0;
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);
                
                if ( (LA75_0==COMMA) ) {
                    alt75=1;
                }
                
            
                switch (alt75) {
            	case 1 :
            	    // JPQL.g:945:107: COMMA s= scalarExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_coalesceExpression5930); if (failed) return node;
            	    pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5936);
            	    s=scalarExpression();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries.add(s);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt75 >= 1 ) break loop75;
            	    if (backtracking>0) {failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(75, input);
                        throw eee;
                }
                cnt75++;
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5942); if (failed) return node;
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
    // JPQL.g:952:1: nullIfExpression returns [Object node] : n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object nullIfExpression() throws RecognitionException {

        Object node = null;
    
        Token n=null;
        Object l = null;

        Object r = null;
        
    
        node = null;
        try {
            // JPQL.g:954:6: (n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:954:6: n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET
            {
            n=(Token)input.LT(1);
            match(input,NULLIF,FOLLOW_NULLIF_in_nullIfExpression5983); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5985); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5991);
            l=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_nullIfExpression5993); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5999);
            r=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression6001); if (failed) return node;
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
    // JPQL.g:962:1: caseOperand returns [Object node] : (n= stateFieldPathExpression | n= typeDiscriminator );
    public final Object caseOperand() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:964:6: (n= stateFieldPathExpression | n= typeDiscriminator )
            int alt76=2;
            int LA76_0 = input.LA(1);
            
            if ( (LA76_0==KEY||LA76_0==VALUE||LA76_0==IDENT) ) {
                alt76=1;
            }
            else if ( (LA76_0==TYPE) ) {
                alt76=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("962:1: caseOperand returns [Object node] : (n= stateFieldPathExpression | n= typeDiscriminator );", 76, 0, input);
            
                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // JPQL.g:964:6: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_caseOperand6048);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:965:6: n= typeDiscriminator
                    {
                    pushFollow(FOLLOW_typeDiscriminator_in_caseOperand6062);
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
    // JPQL.g:968:1: whenClause returns [Object node] : w= WHEN c= conditionalExpression THEN a= scalarExpression ;
    public final Object whenClause() throws RecognitionException {

        Object node = null;
    
        Token w=null;
        Object c = null;

        Object a = null;
        
    
        node = null;
        try {
            // JPQL.g:970:6: (w= WHEN c= conditionalExpression THEN a= scalarExpression )
            // JPQL.g:970:6: w= WHEN c= conditionalExpression THEN a= scalarExpression
            {
            w=(Token)input.LT(1);
            match(input,WHEN,FOLLOW_WHEN_in_whenClause6097); if (failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whenClause6103);
            c=conditionalExpression();
            _fsp--;
            if (failed) return node;
            match(input,THEN,FOLLOW_THEN_in_whenClause6105); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_whenClause6111);
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
    // JPQL.g:977:1: simpleWhenClause returns [Object node] : w= WHEN c= scalarExpression THEN a= scalarExpression ;
    public final Object simpleWhenClause() throws RecognitionException {

        Object node = null;
    
        Token w=null;
        Object c = null;

        Object a = null;
        
    
        node = null;
        try {
            // JPQL.g:979:6: (w= WHEN c= scalarExpression THEN a= scalarExpression )
            // JPQL.g:979:6: w= WHEN c= scalarExpression THEN a= scalarExpression
            {
            w=(Token)input.LT(1);
            match(input,WHEN,FOLLOW_WHEN_in_simpleWhenClause6153); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause6159);
            c=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,THEN,FOLLOW_THEN_in_simpleWhenClause6161); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause6167);
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
    // JPQL.g:986:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );
    public final Object variableOrSingleValuedPath() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:988:7: (n= singleValuedPathExpression | n= variableAccessOrTypeConstant )
            int alt77=2;
            int LA77_0 = input.LA(1);
            
            if ( (LA77_0==IDENT) ) {
                int LA77_1 = input.LA(2);
                
                if ( (LA77_1==RIGHT_ROUND_BRACKET) ) {
                    alt77=2;
                }
                else if ( (LA77_1==DOT) ) {
                    alt77=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("986:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );", 77, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA77_0==KEY||LA77_0==VALUE) ) {
                alt77=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("986:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );", 77, 0, input);
            
                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // JPQL.g:988:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6204);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:989:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6218);
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
    // JPQL.g:992:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );
    public final Object stringPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:994:7: (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression )
            int alt78=4;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt78=1;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt78=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt78=3;
                }
                break;
            case KEY:
            case VALUE:
            case IDENT:
                {
                alt78=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("992:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );", 78, 0, input);
            
                throw nvae;
            }
            
            switch (alt78) {
                case 1 :
                    // JPQL.g:994:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_stringPrimary6250);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:995:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_stringPrimary6264);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:996:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_stringPrimary6278);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:997:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_stringPrimary6292);
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
    // JPQL.g:1002:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );
    public final Object literal() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1004:7: (n= literalNumeric | n= literalBoolean | n= literalString )
            int alt79=3;
            switch ( input.LA(1) ) {
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt79=1;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt79=2;
                }
                break;
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt79=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1002:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );", 79, 0, input);
            
                throw nvae;
            }
            
            switch (alt79) {
                case 1 :
                    // JPQL.g:1004:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_literal6326);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1005:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_literal6340);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1006:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_literal6354);
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
    // JPQL.g:1009:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );
    public final Object literalNumeric() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token l=null;
        Token f=null;
        Token d=null;
    
         node = null; 
        try {
            // JPQL.g:1011:7: (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL )
            int alt80=4;
            switch ( input.LA(1) ) {
            case INTEGER_LITERAL:
                {
                alt80=1;
                }
                break;
            case LONG_LITERAL:
                {
                alt80=2;
                }
                break;
            case FLOAT_LITERAL:
                {
                alt80=3;
                }
                break;
            case DOUBLE_LITERAL:
                {
                alt80=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1009:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );", 80, 0, input);
            
                throw nvae;
            }
            
            switch (alt80) {
                case 1 :
                    // JPQL.g:1011:7: i= INTEGER_LITERAL
                    {
                    i=(Token)input.LT(1);
                    match(input,INTEGER_LITERAL,FOLLOW_INTEGER_LITERAL_in_literalNumeric6384); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newIntegerLiteral(i.getLine(), i.getCharPositionInLine(), 
                                                                   Integer.valueOf(i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1016:7: l= LONG_LITERAL
                    {
                    l=(Token)input.LT(1);
                    match(input,LONG_LITERAL,FOLLOW_LONG_LITERAL_in_literalNumeric6400); if (failed) return node;
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
                    // JPQL.g:1024:7: f= FLOAT_LITERAL
                    {
                    f=(Token)input.LT(1);
                    match(input,FLOAT_LITERAL,FOLLOW_FLOAT_LITERAL_in_literalNumeric6421); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newFloatLiteral(f.getLine(), f.getCharPositionInLine(),
                                                                 Float.valueOf(f.getText()));
                              
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1029:7: d= DOUBLE_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,DOUBLE_LITERAL,FOLLOW_DOUBLE_LITERAL_in_literalNumeric6441); if (failed) return node;
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
    // JPQL.g:1036:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );
    public final Object literalBoolean() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token f=null;
    
         node = null; 
        try {
            // JPQL.g:1038:7: (t= TRUE | f= FALSE )
            int alt81=2;
            int LA81_0 = input.LA(1);
            
            if ( (LA81_0==TRUE) ) {
                alt81=1;
            }
            else if ( (LA81_0==FALSE) ) {
                alt81=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1036:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );", 81, 0, input);
            
                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // JPQL.g:1038:7: t= TRUE
                    {
                    t=(Token)input.LT(1);
                    match(input,TRUE,FOLLOW_TRUE_in_literalBoolean6479); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newBooleanLiteral(t.getLine(), t.getCharPositionInLine(), Boolean.TRUE); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1040:7: f= FALSE
                    {
                    f=(Token)input.LT(1);
                    match(input,FALSE,FOLLOW_FALSE_in_literalBoolean6501); if (failed) return node;
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
    // JPQL.g:1044:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );
    public final Object literalString() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token s=null;
    
         node = null; 
        try {
            // JPQL.g:1046:7: (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED )
            int alt82=2;
            int LA82_0 = input.LA(1);
            
            if ( (LA82_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                alt82=1;
            }
            else if ( (LA82_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                alt82=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1044:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );", 82, 0, input);
            
                throw nvae;
            }
            switch (alt82) {
                case 1 :
                    // JPQL.g:1046:7: d= STRING_LITERAL_DOUBLE_QUOTED
                    {
                    d=(Token)input.LT(1);
                    match(input,STRING_LITERAL_DOUBLE_QUOTED,FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6540); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newStringLiteral(d.getLine(), d.getCharPositionInLine(), 
                                                                  convertStringLiteral(d.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1051:7: s= STRING_LITERAL_SINGLE_QUOTED
                    {
                    s=(Token)input.LT(1);
                    match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6561); if (failed) return node;
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
    // JPQL.g:1058:1: literalTemporal returns [Object node] : (d= DATE_LITERAL | d= TIME_LITERAL | d= TIMESTAMP_LITERAL );
    public final Object literalTemporal() throws RecognitionException {

        Object node = null;
    
        Token d=null;
    
         node = null; 
        try {
            // JPQL.g:1060:7: (d= DATE_LITERAL | d= TIME_LITERAL | d= TIMESTAMP_LITERAL )
            int alt83=3;
            switch ( input.LA(1) ) {
            case DATE_LITERAL:
                {
                alt83=1;
                }
                break;
            case TIME_LITERAL:
                {
                alt83=2;
                }
                break;
            case TIMESTAMP_LITERAL:
                {
                alt83=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1058:1: literalTemporal returns [Object node] : (d= DATE_LITERAL | d= TIME_LITERAL | d= TIMESTAMP_LITERAL );", 83, 0, input);
            
                throw nvae;
            }
            
            switch (alt83) {
                case 1 :
                    // JPQL.g:1060:7: d= DATE_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,DATE_LITERAL,FOLLOW_DATE_LITERAL_in_literalTemporal6601); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newDateLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1061:7: d= TIME_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,TIME_LITERAL,FOLLOW_TIME_LITERAL_in_literalTemporal6615); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newTimeLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1062:7: d= TIMESTAMP_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,TIMESTAMP_LITERAL,FOLLOW_TIMESTAMP_LITERAL_in_literalTemporal6629); if (failed) return node;
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
    // JPQL.g:1065:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );
    public final Object inputParameter() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token n=null;
    
         node = null; 
        try {
            // JPQL.g:1067:7: (p= POSITIONAL_PARAM | n= NAMED_PARAM )
            int alt84=2;
            int LA84_0 = input.LA(1);
            
            if ( (LA84_0==POSITIONAL_PARAM) ) {
                alt84=1;
            }
            else if ( (LA84_0==NAMED_PARAM) ) {
                alt84=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1065:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );", 84, 0, input);
            
                throw nvae;
            }
            switch (alt84) {
                case 1 :
                    // JPQL.g:1067:7: p= POSITIONAL_PARAM
                    {
                    p=(Token)input.LT(1);
                    match(input,POSITIONAL_PARAM,FOLLOW_POSITIONAL_PARAM_in_inputParameter6659); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  // skip the leading ?
                                  String text = p.getText().substring(1);
                                  node = factory.newPositionalParameter(p.getLine(), p.getCharPositionInLine(), text); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1073:7: n= NAMED_PARAM
                    {
                    n=(Token)input.LT(1);
                    match(input,NAMED_PARAM,FOLLOW_NAMED_PARAM_in_inputParameter6679); if (failed) return node;
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
    // JPQL.g:1081:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index | n= func );
    public final Object functionsReturningNumerics() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1083:7: (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index | n= func )
            int alt85=8;
            switch ( input.LA(1) ) {
            case ABS:
                {
                alt85=1;
                }
                break;
            case LENGTH:
                {
                alt85=2;
                }
                break;
            case MOD:
                {
                alt85=3;
                }
                break;
            case SQRT:
                {
                alt85=4;
                }
                break;
            case LOCATE:
                {
                alt85=5;
                }
                break;
            case SIZE:
                {
                alt85=6;
                }
                break;
            case INDEX:
                {
                alt85=7;
                }
                break;
            case FUNC:
                {
                alt85=8;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1081:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index | n= func );", 85, 0, input);
            
                throw nvae;
            }
            
            switch (alt85) {
                case 1 :
                    // JPQL.g:1083:7: n= abs
                    {
                    pushFollow(FOLLOW_abs_in_functionsReturningNumerics6719);
                    n=abs();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1084:7: n= length
                    {
                    pushFollow(FOLLOW_length_in_functionsReturningNumerics6733);
                    n=length();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1085:7: n= mod
                    {
                    pushFollow(FOLLOW_mod_in_functionsReturningNumerics6747);
                    n=mod();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1086:7: n= sqrt
                    {
                    pushFollow(FOLLOW_sqrt_in_functionsReturningNumerics6761);
                    n=sqrt();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:1087:7: n= locate
                    {
                    pushFollow(FOLLOW_locate_in_functionsReturningNumerics6775);
                    n=locate();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:1088:7: n= size
                    {
                    pushFollow(FOLLOW_size_in_functionsReturningNumerics6789);
                    n=size();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 7 :
                    // JPQL.g:1089:7: n= index
                    {
                    pushFollow(FOLLOW_index_in_functionsReturningNumerics6803);
                    n=index();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 8 :
                    // JPQL.g:1090:7: n= func
                    {
                    pushFollow(FOLLOW_func_in_functionsReturningNumerics6817);
                    n=func();
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
    // JPQL.g:1093:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );
    public final Object functionsReturningDatetime() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token t=null;
        Token ts=null;
    
         node = null; 
        try {
            // JPQL.g:1095:7: (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP )
            int alt86=3;
            switch ( input.LA(1) ) {
            case CURRENT_DATE:
                {
                alt86=1;
                }
                break;
            case CURRENT_TIME:
                {
                alt86=2;
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                alt86=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1093:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );", 86, 0, input);
            
                throw nvae;
            }
            
            switch (alt86) {
                case 1 :
                    // JPQL.g:1095:7: d= CURRENT_DATE
                    {
                    d=(Token)input.LT(1);
                    match(input,CURRENT_DATE,FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6847); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentDate(d.getLine(), d.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1097:7: t= CURRENT_TIME
                    {
                    t=(Token)input.LT(1);
                    match(input,CURRENT_TIME,FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6868); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentTime(t.getLine(), t.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1099:7: ts= CURRENT_TIMESTAMP
                    {
                    ts=(Token)input.LT(1);
                    match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6888); if (failed) return node;
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
    // JPQL.g:1103:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );
    public final Object functionsReturningStrings() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1105:7: (n= concat | n= substring | n= trim | n= upper | n= lower )
            int alt87=5;
            switch ( input.LA(1) ) {
            case CONCAT:
                {
                alt87=1;
                }
                break;
            case SUBSTRING:
                {
                alt87=2;
                }
                break;
            case TRIM:
                {
                alt87=3;
                }
                break;
            case UPPER:
                {
                alt87=4;
                }
                break;
            case LOWER:
                {
                alt87=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1103:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );", 87, 0, input);
            
                throw nvae;
            }
            
            switch (alt87) {
                case 1 :
                    // JPQL.g:1105:7: n= concat
                    {
                    pushFollow(FOLLOW_concat_in_functionsReturningStrings6928);
                    n=concat();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1106:7: n= substring
                    {
                    pushFollow(FOLLOW_substring_in_functionsReturningStrings6942);
                    n=substring();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1107:7: n= trim
                    {
                    pushFollow(FOLLOW_trim_in_functionsReturningStrings6956);
                    n=trim();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1108:7: n= upper
                    {
                    pushFollow(FOLLOW_upper_in_functionsReturningStrings6970);
                    n=upper();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:1109:7: n= lower
                    {
                    pushFollow(FOLLOW_lower_in_functionsReturningStrings6984);
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
    // JPQL.g:1113:1: concat returns [Object node] : c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET ;
    public final Object concat() throws RecognitionException {
        concat_stack.push(new concat_scope());

        Object node = null;
    
        Token c=null;
        Object firstArg = null;

        Object arg = null;
        
    
         
            node = null;
            ((concat_scope)concat_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1121:7: (c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET )
            // JPQL.g:1121:7: c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET
            {
            c=(Token)input.LT(1);
            match(input,CONCAT,FOLLOW_CONCAT_in_concat7019); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_concat7030); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_concat7045);
            firstArg=scalarExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((concat_scope)concat_stack.peek()).items.add(firstArg);
            }
            // JPQL.g:1123:75: ( COMMA arg= scalarExpression )+
            int cnt88=0;
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);
                
                if ( (LA88_0==COMMA) ) {
                    alt88=1;
                }
                
            
                switch (alt88) {
            	case 1 :
            	    // JPQL.g:1123:76: COMMA arg= scalarExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_concat7050); if (failed) return node;
            	    pushFollow(FOLLOW_scalarExpression_in_concat7056);
            	    arg=scalarExpression();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((concat_scope)concat_stack.peek()).items.add(arg);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt88 >= 1 ) break loop88;
            	    if (backtracking>0) {failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(88, input);
                        throw eee;
                }
                cnt88++;
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_concat7070); if (failed) return node;
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
    // JPQL.g:1128:1: substring returns [Object node] : s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= scalarExpression ( COMMA lengthNode= scalarExpression )? RIGHT_ROUND_BRACKET ;
    public final Object substring() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object string = null;

        Object start = null;

        Object lengthNode = null;
        
    
         
            node = null;
            lengthNode = null;
    
        try {
            // JPQL.g:1133:7: (s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= scalarExpression ( COMMA lengthNode= scalarExpression )? RIGHT_ROUND_BRACKET )
            // JPQL.g:1133:7: s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= scalarExpression ( COMMA lengthNode= scalarExpression )? RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SUBSTRING,FOLLOW_SUBSTRING_in_substring7108); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_substring7121); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_substring7135);
            string=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring7137); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_substring7151);
            start=scalarExpression();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1137:9: ( COMMA lengthNode= scalarExpression )?
            int alt89=2;
            int LA89_0 = input.LA(1);
            
            if ( (LA89_0==COMMA) ) {
                alt89=1;
            }
            switch (alt89) {
                case 1 :
                    // JPQL.g:1137:10: COMMA lengthNode= scalarExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_substring7162); if (failed) return node;
                    pushFollow(FOLLOW_scalarExpression_in_substring7168);
                    lengthNode=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_substring7180); if (failed) return node;
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
    // JPQL.g:1150:1: trim returns [Object node] : t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object trim() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        TrimSpecification trimSpecIndicator = null;

        Object trimCharNode = null;

        Object n = null;
        
    
         
            node = null;
            trimSpecIndicator = TrimSpecification.BOTH;
    
        try {
            // JPQL.g:1155:7: (t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1155:7: t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,TRIM,FOLLOW_TRIM_in_trim7218); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_trim7228); if (failed) return node;
            // JPQL.g:1157:9: (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )?
            int alt90=2;
            switch ( input.LA(1) ) {
                case BOTH:
                case FROM:
                case LEADING:
                case TRAILING:
                    {
                    alt90=1;
                    }
                    break;
                case STRING_LITERAL_DOUBLE_QUOTED:
                    {
                    int LA90_4 = input.LA(2);
                    
                    if ( (LA90_4==FROM) ) {
                        alt90=1;
                    }
                    }
                    break;
                case STRING_LITERAL_SINGLE_QUOTED:
                    {
                    int LA90_5 = input.LA(2);
                    
                    if ( (LA90_5==FROM) ) {
                        alt90=1;
                    }
                    }
                    break;
                case POSITIONAL_PARAM:
                    {
                    int LA90_6 = input.LA(2);
                    
                    if ( (LA90_6==FROM) ) {
                        alt90=1;
                    }
                    }
                    break;
                case NAMED_PARAM:
                    {
                    int LA90_7 = input.LA(2);
                    
                    if ( (LA90_7==FROM) ) {
                        alt90=1;
                    }
                    }
                    break;
            }
            
            switch (alt90) {
                case 1 :
                    // JPQL.g:1157:10: trimSpecIndicator= trimSpec trimCharNode= trimChar FROM
                    {
                    pushFollow(FOLLOW_trimSpec_in_trim7244);
                    trimSpecIndicator=trimSpec();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_trimChar_in_trim7250);
                    trimCharNode=trimChar();
                    _fsp--;
                    if (failed) return node;
                    match(input,FROM,FOLLOW_FROM_in_trim7252); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_stringPrimary_in_trim7268);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_trim7278); if (failed) return node;
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
    // JPQL.g:1166:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );
    public final TrimSpecification trimSpec() throws RecognitionException {

        TrimSpecification trimSpec = null;
    
         trimSpec = TrimSpecification.BOTH; 
        try {
            // JPQL.g:1168:7: ( LEADING | TRAILING | BOTH | )
            int alt91=4;
            switch ( input.LA(1) ) {
            case LEADING:
                {
                alt91=1;
                }
                break;
            case TRAILING:
                {
                alt91=2;
                }
                break;
            case BOTH:
                {
                alt91=3;
                }
                break;
            case FROM:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt91=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return trimSpec;}
                NoViableAltException nvae =
                    new NoViableAltException("1166:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );", 91, 0, input);
            
                throw nvae;
            }
            
            switch (alt91) {
                case 1 :
                    // JPQL.g:1168:7: LEADING
                    {
                    match(input,LEADING,FOLLOW_LEADING_in_trimSpec7314); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.LEADING; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1170:7: TRAILING
                    {
                    match(input,TRAILING,FOLLOW_TRAILING_in_trimSpec7332); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.TRAILING; 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1172:7: BOTH
                    {
                    match(input,BOTH,FOLLOW_BOTH_in_trimSpec7350); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.BOTH; 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1175:5: 
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
    // JPQL.g:1177:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );
    public final Object trimChar() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1179:7: (n= literalString | n= inputParameter | )
            int alt92=3;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt92=1;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt92=2;
                }
                break;
            case FROM:
                {
                alt92=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1177:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );", 92, 0, input);
            
                throw nvae;
            }
            
            switch (alt92) {
                case 1 :
                    // JPQL.g:1179:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_trimChar7397);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1180:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_trimChar7411);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1182:5: 
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
    // JPQL.g:1184:1: upper returns [Object node] : u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object upper() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1186:7: (u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1186:7: u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            u=(Token)input.LT(1);
            match(input,UPPER,FOLLOW_UPPER_in_upper7448); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_upper7450); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_upper7456);
            n=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_upper7458); if (failed) return node;
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
    // JPQL.g:1190:1: lower returns [Object node] : l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object lower() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1192:7: (l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1192:7: l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOWER,FOLLOW_LOWER_in_lower7496); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_lower7498); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_lower7504);
            n=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_lower7506); if (failed) return node;
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
    // JPQL.g:1197:1: abs returns [Object node] : a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object abs() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1199:7: (a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1199:7: a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            a=(Token)input.LT(1);
            match(input,ABS,FOLLOW_ABS_in_abs7545); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_abs7547); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_abs7553);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_abs7555); if (failed) return node;
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
    // JPQL.g:1203:1: length returns [Object node] : l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object length() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1205:7: (l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1205:7: l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LENGTH,FOLLOW_LENGTH_in_length7593); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_length7595); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_length7601);
            n=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_length7603); if (failed) return node;
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
    // JPQL.g:1209:1: locate returns [Object node] : l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= scalarExpression )? RIGHT_ROUND_BRACKET ;
    public final Object locate() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object pattern = null;

        Object n = null;

        Object startPos = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1213:7: (l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= scalarExpression )? RIGHT_ROUND_BRACKET )
            // JPQL.g:1213:7: l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= scalarExpression )? RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOCATE,FOLLOW_LOCATE_in_locate7641); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_locate7651); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_locate7666);
            pattern=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_locate7668); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_locate7674);
            n=scalarExpression();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1216:9: ( COMMA startPos= scalarExpression )?
            int alt93=2;
            int LA93_0 = input.LA(1);
            
            if ( (LA93_0==COMMA) ) {
                alt93=1;
            }
            switch (alt93) {
                case 1 :
                    // JPQL.g:1216:11: COMMA startPos= scalarExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_locate7686); if (failed) return node;
                    pushFollow(FOLLOW_scalarExpression_in_locate7692);
                    startPos=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_locate7705); if (failed) return node;
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
    // JPQL.g:1224:1: size returns [Object node] : s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ;
    public final Object size() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1226:7: (s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1226:7: s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SIZE,FOLLOW_SIZE_in_size7743); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_size7754); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_size7760);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_size7762); if (failed) return node;
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
    // JPQL.g:1231:1: mod returns [Object node] : m= MOD LEFT_ROUND_BRACKET left= scalarExpression COMMA right= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object mod() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Object left = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1235:7: (m= MOD LEFT_ROUND_BRACKET left= scalarExpression COMMA right= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1235:7: m= MOD LEFT_ROUND_BRACKET left= scalarExpression COMMA right= scalarExpression RIGHT_ROUND_BRACKET
            {
            m=(Token)input.LT(1);
            match(input,MOD,FOLLOW_MOD_in_mod7800); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mod7802); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_mod7816);
            left=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_mod7818); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_mod7833);
            right=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mod7843); if (failed) return node;
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
    // JPQL.g:1242:1: sqrt returns [Object node] : s= SQRT LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object sqrt() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1244:7: (s= SQRT LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1244:7: s= SQRT LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SQRT,FOLLOW_SQRT_in_sqrt7881); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7892); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_sqrt7898);
            n=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7900); if (failed) return node;
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
    // JPQL.g:1249:1: index returns [Object node] : s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object index() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1251:7: (s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // JPQL.g:1251:7: s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,INDEX,FOLLOW_INDEX_in_index7942); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_index7944); if (failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_index7950);
            n=variableAccessOrTypeConstant();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_index7952); if (failed) return node;
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

    protected static class func_scope {
        List exprs;
    }
    protected Stack func_stack = new Stack();
    
    
    // $ANTLR start func
    // JPQL.g:1256:1: func returns [Object node] : f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET ;
    public final Object func() throws RecognitionException {
        func_stack.push(new func_scope());

        Object node = null;
    
        Token f=null;
        Token name=null;
        Object n = null;
        
    
         
            node = null; 
            ((func_scope)func_stack.peek()).exprs = new ArrayList();
    
        try {
            // JPQL.g:1264:7: (f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET )
            // JPQL.g:1264:7: f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET
            {
            f=(Token)input.LT(1);
            match(input,FUNC,FOLLOW_FUNC_in_func7994); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_func7996); if (failed) return node;
            name=(Token)input.LT(1);
            match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_func8008); if (failed) return node;
            // JPQL.g:1266:7: ( COMMA n= newValue )*
            loop94:
            do {
                int alt94=2;
                int LA94_0 = input.LA(1);
                
                if ( (LA94_0==COMMA) ) {
                    alt94=1;
                }
                
            
                switch (alt94) {
            	case 1 :
            	    // JPQL.g:1266:8: COMMA n= newValue
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_func8017); if (failed) return node;
            	    pushFollow(FOLLOW_newValue_in_func8023);
            	    n=newValue();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      
            	                  ((func_scope)func_stack.peek()).exprs.add(n);
            	                
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop94;
                }
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_func8055); if (failed) return node;
            if ( backtracking==0 ) {
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
    // $ANTLR end func

    
    // $ANTLR start subquery
    // JPQL.g:1275:1: subquery returns [Object node] : select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? ;
    public final Object subquery() throws RecognitionException {

        Object node = null;
    
        Object select = null;

        Object from = null;

        Object where = null;

        Object groupBy = null;

        Object having = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1279:7: (select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? )
            // JPQL.g:1279:7: select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )?
            {
            pushFollow(FOLLOW_simpleSelectClause_in_subquery8093);
            select=simpleSelectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_subqueryFromClause_in_subquery8108);
            from=subqueryFromClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1281:7: (where= whereClause )?
            int alt95=2;
            int LA95_0 = input.LA(1);
            
            if ( (LA95_0==WHERE) ) {
                alt95=1;
            }
            switch (alt95) {
                case 1 :
                    // JPQL.g:1281:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_subquery8123);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:1282:7: (groupBy= groupByClause )?
            int alt96=2;
            int LA96_0 = input.LA(1);
            
            if ( (LA96_0==GROUP) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // JPQL.g:1282:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_subquery8138);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:1283:7: (having= havingClause )?
            int alt97=2;
            int LA97_0 = input.LA(1);
            
            if ( (LA97_0==HAVING) ) {
                alt97=1;
            }
            switch (alt97) {
                case 1 :
                    // JPQL.g:1283:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_subquery8154);
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
    // JPQL.g:1290:1: simpleSelectClause returns [Object node] : s= SELECT ( DISTINCT )? n= simpleSelectExpression ;
    public final Object simpleSelectClause() throws RecognitionException {
        simpleSelectClause_stack.push(new simpleSelectClause_scope());

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         
            node = null; 
            ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = false;
    
        try {
            // JPQL.g:1298:7: (s= SELECT ( DISTINCT )? n= simpleSelectExpression )
            // JPQL.g:1298:7: s= SELECT ( DISTINCT )? n= simpleSelectExpression
            {
            s=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_simpleSelectClause8197); if (failed) return node;
            // JPQL.g:1298:16: ( DISTINCT )?
            int alt98=2;
            int LA98_0 = input.LA(1);
            
            if ( (LA98_0==DISTINCT) ) {
                alt98=1;
            }
            switch (alt98) {
                case 1 :
                    // JPQL.g:1298:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_simpleSelectClause8200); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_simpleSelectExpression_in_simpleSelectClause8216);
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
    // JPQL.g:1308:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );
    public final Object simpleSelectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1310:7: (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant )
            int alt99=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA99_1 = input.LA(2);
                
                if ( (LA99_1==DOT) ) {
                    alt99=1;
                }
                else if ( (LA99_1==FROM) ) {
                    alt99=3;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1308:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );", 99, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case KEY:
            case VALUE:
                {
                alt99=1;
                }
                break;
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt99=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1308:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );", 99, 0, input);
            
                throw nvae;
            }
            
            switch (alt99) {
                case 1 :
                    // JPQL.g:1310:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_simpleSelectExpression8256);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1311:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_simpleSelectExpression8271);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1312:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression8286);
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
    // JPQL.g:1316:1: subqueryFromClause returns [Object node] : f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )* ;
    public final Object subqueryFromClause() throws RecognitionException {
        subqueryFromClause_stack.push(new subqueryFromClause_scope());

        Object node = null;
    
        Token f=null;
        Object c = null;
        
    
         
            node = null; 
            ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g:1324:7: (f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )* )
            // JPQL.g:1324:7: f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )*
            {
            f=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_subqueryFromClause8321); if (failed) return node;
            pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8323);
            subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g:1325:9: ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )*
            loop100:
            do {
                int alt100=3;
                int LA100_0 = input.LA(1);
                
                if ( (LA100_0==COMMA) ) {
                    alt100=1;
                }
                else if ( (LA100_0==IN) ) {
                    alt100=2;
                }
                
            
                switch (alt100) {
            	case 1 :
            	    // JPQL.g:1326:13: COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_subqueryFromClause8350); if (failed) return node;
            	    pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8369);
            	    subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            	    _fsp--;
            	    if (failed) return node;
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:1328:19: c= collectionMemberDeclaration
            	    {
            	    pushFollow(FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8395);
            	    c=collectionMemberDeclaration();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls.add(c);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop100;
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
    // JPQL.g:1333:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );
    public final void subselectIdentificationVariableDeclaration(List varDecls) throws RecognitionException {
        Token i=null;
        Object n = null;
        
    
         Object node; 
        try {
            // JPQL.g:1335:7: ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration )
            int alt103=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA103_1 = input.LA(2);
                
                if ( (LA103_1==AS||LA103_1==IDENT) ) {
                    alt103=1;
                }
                else if ( (LA103_1==DOT) ) {
                    alt103=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1333:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 103, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case KEY:
                {
                int LA103_2 = input.LA(2);
                
                if ( (LA103_2==LEFT_ROUND_BRACKET) ) {
                    alt103=2;
                }
                else if ( (LA103_2==AS||LA103_2==IDENT) ) {
                    alt103=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1333:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 103, 2, input);
                
                    throw nvae;
                }
                }
                break;
            case VALUE:
                {
                int LA103_3 = input.LA(2);
                
                if ( (LA103_3==LEFT_ROUND_BRACKET) ) {
                    alt103=2;
                }
                else if ( (LA103_3==AS||LA103_3==IDENT) ) {
                    alt103=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1333:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 103, 3, input);
                
                    throw nvae;
                }
                }
                break;
            case IN:
                {
                int LA103_4 = input.LA(2);
                
                if ( (LA103_4==LEFT_ROUND_BRACKET) ) {
                    alt103=3;
                }
                else if ( (LA103_4==AS||LA103_4==IDENT) ) {
                    alt103=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1333:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 103, 4, input);
                
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
            case FUNC:
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
            case TREAT:
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
                alt103=1;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1333:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 103, 0, input);
            
                throw nvae;
            }
            
            switch (alt103) {
                case 1 :
                    // JPQL.g:1335:7: identificationVariableDeclaration[varDecls]
                    {
                    pushFollow(FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8442);
                    identificationVariableDeclaration(varDecls);
                    _fsp--;
                    if (failed) return ;
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1336:7: n= associationPathExpression ( AS )? i= IDENT ( join )*
                    {
                    pushFollow(FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8455);
                    n=associationPathExpression();
                    _fsp--;
                    if (failed) return ;
                    // JPQL.g:1336:37: ( AS )?
                    int alt101=2;
                    int LA101_0 = input.LA(1);
                    
                    if ( (LA101_0==AS) ) {
                        alt101=1;
                    }
                    switch (alt101) {
                        case 1 :
                            // JPQL.g:1336:38: AS
                            {
                            match(input,AS,FOLLOW_AS_in_subselectIdentificationVariableDeclaration8458); if (failed) return ;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8464); if (failed) return ;
                    // JPQL.g:1336:51: ( join )*
                    loop102:
                    do {
                        int alt102=2;
                        int LA102_0 = input.LA(1);
                        
                        if ( (LA102_0==INNER||LA102_0==JOIN||LA102_0==LEFT) ) {
                            alt102=1;
                        }
                        
                    
                        switch (alt102) {
                    	case 1 :
                    	    // JPQL.g:1336:52: join
                    	    {
                    	    pushFollow(FOLLOW_join_in_subselectIdentificationVariableDeclaration8467);
                    	    join();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {
                    	       varDecls.add(n); 
                    	    }
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    break loop102;
                        }
                    } while (true);

                    if ( backtracking==0 ) {
                       
                                  varDecls.add(factory.newVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                       n, i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1341:7: n= collectionMemberDeclaration
                    {
                    pushFollow(FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8494);
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
    // JPQL.g:1344:1: orderByClause returns [Object node] : o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* ;
    public final Object orderByClause() throws RecognitionException {
        orderByClause_stack.push(new orderByClause_scope());

        Object node = null;
    
        Token o=null;
        Object n = null;
        
    
         
            node = null; 
            ((orderByClause_scope)orderByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1352:7: (o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* )
            // JPQL.g:1352:7: o= ORDER BY n= orderByItem ( COMMA n= orderByItem )*
            {
            o=(Token)input.LT(1);
            match(input,ORDER,FOLLOW_ORDER_in_orderByClause8527); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_orderByClause8529); if (failed) return node;
            pushFollow(FOLLOW_orderByItem_in_orderByClause8543);
            n=orderByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1354:9: ( COMMA n= orderByItem )*
            loop104:
            do {
                int alt104=2;
                int LA104_0 = input.LA(1);
                
                if ( (LA104_0==COMMA) ) {
                    alt104=1;
                }
                
            
                switch (alt104) {
            	case 1 :
            	    // JPQL.g:1354:10: COMMA n= orderByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_orderByClause8558); if (failed) return node;
            	    pushFollow(FOLLOW_orderByItem_in_orderByClause8564);
            	    n=orderByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop104;
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
    // JPQL.g:1358:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );
    public final Object orderByItem() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token d=null;
        Token i=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1360:7: (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) )
            int alt107=2;
            int LA107_0 = input.LA(1);
            
            if ( (LA107_0==IDENT) ) {
                int LA107_1 = input.LA(2);
                
                if ( (LA107_1==DOT) ) {
                    alt107=1;
                }
                else if ( (LA107_1==EOF||LA107_1==ASC||LA107_1==DESC||LA107_1==COMMA) ) {
                    alt107=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1358:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );", 107, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA107_0==KEY||LA107_0==VALUE) ) {
                alt107=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1358:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );", 107, 0, input);
            
                throw nvae;
            }
            switch (alt107) {
                case 1 :
                    // JPQL.g:1360:7: n= stateFieldPathExpression (a= ASC | d= DESC | )
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_orderByItem8610);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    // JPQL.g:1361:9: (a= ASC | d= DESC | )
                    int alt105=3;
                    switch ( input.LA(1) ) {
                    case ASC:
                        {
                        alt105=1;
                        }
                        break;
                    case DESC:
                        {
                        alt105=2;
                        }
                        break;
                    case EOF:
                    case COMMA:
                        {
                        alt105=3;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("1361:9: (a= ASC | d= DESC | )", 105, 0, input);
                    
                        throw nvae;
                    }
                    
                    switch (alt105) {
                        case 1 :
                            // JPQL.g:1361:11: a= ASC
                            {
                            a=(Token)input.LT(1);
                            match(input,ASC,FOLLOW_ASC_in_orderByItem8624); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), n); 
                            }
                            
                            }
                            break;
                        case 2 :
                            // JPQL.g:1363:11: d= DESC
                            {
                            d=(Token)input.LT(1);
                            match(input,DESC,FOLLOW_DESC_in_orderByItem8653); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), n); 
                            }
                            
                            }
                            break;
                        case 3 :
                            // JPQL.g:1366:13: 
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
                    // JPQL.g:1368:8: i= IDENT (a= ASC | d= DESC | )
                    {
                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_orderByItem8715); if (failed) return node;
                    // JPQL.g:1369:9: (a= ASC | d= DESC | )
                    int alt106=3;
                    switch ( input.LA(1) ) {
                    case ASC:
                        {
                        alt106=1;
                        }
                        break;
                    case DESC:
                        {
                        alt106=2;
                        }
                        break;
                    case EOF:
                    case COMMA:
                        {
                        alt106=3;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("1369:9: (a= ASC | d= DESC | )", 106, 0, input);
                    
                        throw nvae;
                    }
                    
                    switch (alt106) {
                        case 1 :
                            // JPQL.g:1369:11: a= ASC
                            {
                            a=(Token)input.LT(1);
                            match(input,ASC,FOLLOW_ASC_in_orderByItem8729); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), i.getText()); 
                            }
                            
                            }
                            break;
                        case 2 :
                            // JPQL.g:1371:11: d= DESC
                            {
                            d=(Token)input.LT(1);
                            match(input,DESC,FOLLOW_DESC_in_orderByItem8758); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), i.getText()); 
                            }
                            
                            }
                            break;
                        case 3 :
                            // JPQL.g:1374:13: 
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
    // JPQL.g:1378:1: groupByClause returns [Object node] : g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* ;
    public final Object groupByClause() throws RecognitionException {
        groupByClause_stack.push(new groupByClause_scope());

        Object node = null;
    
        Token g=null;
        Object n = null;
        
    
         
            node = null; 
            ((groupByClause_scope)groupByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1386:7: (g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* )
            // JPQL.g:1386:7: g= GROUP BY n= groupByItem ( COMMA n= groupByItem )*
            {
            g=(Token)input.LT(1);
            match(input,GROUP,FOLLOW_GROUP_in_groupByClause8839); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_groupByClause8841); if (failed) return node;
            pushFollow(FOLLOW_groupByItem_in_groupByClause8855);
            n=groupByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1388:9: ( COMMA n= groupByItem )*
            loop108:
            do {
                int alt108=2;
                int LA108_0 = input.LA(1);
                
                if ( (LA108_0==COMMA) ) {
                    alt108=1;
                }
                
            
                switch (alt108) {
            	case 1 :
            	    // JPQL.g:1388:10: COMMA n= groupByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_groupByClause8868); if (failed) return node;
            	    pushFollow(FOLLOW_groupByItem_in_groupByClause8874);
            	    n=groupByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop108;
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
    // JPQL.g:1392:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );
    public final Object groupByItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1394:7: (n= stateFieldPathExpression | n= variableAccessOrTypeConstant )
            int alt109=2;
            int LA109_0 = input.LA(1);
            
            if ( (LA109_0==IDENT) ) {
                int LA109_1 = input.LA(2);
                
                if ( (LA109_1==DOT) ) {
                    alt109=1;
                }
                else if ( (LA109_1==EOF||LA109_1==HAVING||LA109_1==ORDER||LA109_1==COMMA||LA109_1==RIGHT_ROUND_BRACKET) ) {
                    alt109=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1392:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );", 109, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA109_0==KEY||LA109_0==VALUE) ) {
                alt109=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1392:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );", 109, 0, input);
            
                throw nvae;
            }
            switch (alt109) {
                case 1 :
                    // JPQL.g:1394:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_groupByItem8920);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1395:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_groupByItem8934);
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
    // JPQL.g:1398:1: havingClause returns [Object node] : h= HAVING n= conditionalExpression ;
    public final Object havingClause() throws RecognitionException {

        Object node = null;
    
        Token h=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1400:7: (h= HAVING n= conditionalExpression )
            // JPQL.g:1400:7: h= HAVING n= conditionalExpression
            {
            h=(Token)input.LT(1);
            match(input,HAVING,FOLLOW_HAVING_in_havingClause8964); if (failed) return node;
            if ( backtracking==0 ) {
               setAggregatesAllowed(true); 
            }
            pushFollow(FOLLOW_conditionalExpression_in_havingClause8981);
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
        // JPQL.g:647:7: ( LEFT_ROUND_BRACKET conditionalExpression )
        // JPQL.g:647:8: LEFT_ROUND_BRACKET conditionalExpression
        {
        match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_synpred13599); if (failed) return ;
        pushFollow(FOLLOW_conditionalExpression_in_synpred13601);
        conditionalExpression();
        _fsp--;
        if (failed) return ;
        
        }
    }
    // $ANTLR end synpred1

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
    public static final BitSet FOLLOW_EQUALS_in_setAssignmentClause1187 = new BitSet(new long[]{0x80CDD221401FC410L,0x000003FF9809338DL});
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
    public static final BitSet FOLLOW_SELECT_in_selectClause1458 = new BitSet(new long[]{0x819DD221489FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause1461 = new BitSet(new long[]{0x819DD221481FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_selectItem_in_selectClause1477 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_selectClause1505 = new BitSet(new long[]{0x819DD221481FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_selectItem_in_selectClause1511 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_selectExpression_in_selectItem1607 = new BitSet(new long[]{0x0000000000000102L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_selectItem1611 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_selectItem1619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_selectExpression1663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalarExpression_in_selectExpression1677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBJECT_in_selectExpression1687 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1689 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_selectExpression1695 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constructorExpression_in_selectExpression1712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntryExpression_in_selectExpression1727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_in_mapEntryExpression1759 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1761 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1767 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1801 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_DOT_in_pathExprOrVariableAccess1816 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExprOrVariableAccess1822 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KEY_in_qualifiedIdentificationVariable1892 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1894 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1900 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUE_in_qualifiedIdentificationVariable1917 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1919 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1925 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AVG_in_aggregateExpression1960 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1962 = new BitSet(new long[]{0x0000020000800000L,0x0000000000012000L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1965 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1983 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAX_in_aggregateExpression2006 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2008 = new BitSet(new long[]{0x0000020000800000L,0x0000000000012000L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2011 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2030 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MIN_in_aggregateExpression2052 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2054 = new BitSet(new long[]{0x0000020000800000L,0x0000000000012000L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2057 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2075 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUM_in_aggregateExpression2097 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2099 = new BitSet(new long[]{0x0000020000800000L,0x0000000000012000L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2102 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2120 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COUNT_in_aggregateExpression2142 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2144 = new BitSet(new long[]{0x0000020000800000L,0x0000000000012000L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2147 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_aggregateExpression2165 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_constructorExpression2210 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_constructorName_in_constructorExpression2216 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2226 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2241 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_COMMA_in_constructorExpression2256 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2262 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2318 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_DOT_in_constructorName2332 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2336 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_scalarExpression_in_constructorItem2380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_constructorItem2394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_fromClause2428 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2430 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_fromClause2442 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2447 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_fromClause2472 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2538 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_join_in_identificationVariableDeclaration2557 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2592 = new BitSet(new long[]{0x0000000000000100L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_rangeVariableDeclaration2595 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_rangeVariableDeclaration2601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_joinSpec_in_join2684 = new BitSet(new long[]{0x0000020080000000L,0x0000000000012040L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2698 = new BitSet(new long[]{0x0000000000000100L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_join2701 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_join2707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREAT_in_join2736 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_join2738 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2744 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_AS_in_join2746 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_join2752 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_join2754 = new BitSet(new long[]{0x0000000000000100L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_join2757 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_join2763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FETCH_in_join2786 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_in_joinSpec2838 = new BitSet(new long[]{0x1000010000000000L});
    public static final BitSet FOLLOW_OUTER_in_joinSpec2841 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_INNER_in_joinSpec2850 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_JOIN_in_joinSpec2856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_collectionMemberDeclaration2884 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2886 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2892 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2894 = new BitSet(new long[]{0x0000000000000100L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_collectionMemberDeclaration2904 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_collectionMemberDeclaration2910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_collectionValuedPathExpression2948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_associationPathExpression2980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_joinAssociationPathExpression3013 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_DOT_in_joinAssociationPathExpression3028 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_joinAssociationPathExpression3034 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_pathExpression_in_singleValuedPathExpression3090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_stateFieldPathExpression3122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExpression3154 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_DOT_in_pathExpression3169 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExpression3175 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_IDENT_in_variableAccessOrTypeConstant3271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause3309 = new BitSet(new long[]{0x80ADD221601FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_conditionalExpression_in_whereClause3315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3357 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_OR_in_conditionalExpression3372 = new BitSet(new long[]{0x80ADD221601FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3378 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3433 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AND_in_conditionalTerm3448 = new BitSet(new long[]{0x80ADD221601FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3454 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_NOT_in_conditionalFactor3509 = new BitSet(new long[]{0x808DD221601FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_conditionalPrimary_in_conditionalFactor3528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsExpression_in_conditionalFactor3557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3614 = new BitSet(new long[]{0x80ADD221601FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalPrimary3620 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleConditionalExpression_in_conditionalPrimary3636 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_simpleConditionalExpression3668 = new BitSet(new long[]{0x0022209000000800L,0x0000000007C40000L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3689 = new BitSet(new long[]{0x0022209000000800L,0x0000000007C40000L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3744 = new BitSet(new long[]{0x0002201000000800L});
    public static final BitSet FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_simpleConditionalExpressionRemainder3763 = new BitSet(new long[]{0x0060000002000000L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3768 = new BitSet(new long[]{0x0040000002000000L});
    public static final BitSet FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_betweenExpression_in_conditionWithNotExpression3811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_likeExpression_in_conditionWithNotExpression3826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inExpression_in_conditionWithNotExpression3840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullComparisonExpression_in_isExpression3889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptyCollectionComparisonExpression_in_isExpression3904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpression3937 = new BitSet(new long[]{0x808D52210002C410L,0x0000030798092009L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3951 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_AND_in_betweenExpression3953 = new BitSet(new long[]{0x808D52210002C410L,0x0000030798092009L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression4005 = new BitSet(new long[]{0x0000000000000000L,0x0000030000000000L});
    public static final BitSet FOLLOW_inputParameter_in_inExpression4011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression4038 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_inExpression4048 = new BitSet(new long[]{0x2000000000000000L,0x0000031F80010000L});
    public static final BitSet FOLLOW_inItem_in_inExpression4064 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression4082 = new BitSet(new long[]{0x0000000000000000L,0x0000031F80010000L});
    public static final BitSet FOLLOW_inItem_in_inExpression4088 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_subquery_in_inExpression4123 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_inItem4187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_inItem4201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_inItem4215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_inItem4229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIKE_in_likeExpression4261 = new BitSet(new long[]{0x0000000000000000L,0x0000031800000000L});
    public static final BitSet FOLLOW_likeValue_in_likeExpression4267 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_escape_in_likeExpression4282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_in_escape4322 = new BitSet(new long[]{0x0000000000000000L,0x0000031800000000L});
    public static final BitSet FOLLOW_likeValue_in_escape4328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_likeValue4368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_likeValue4382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_nullComparisonExpression4415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEMBER_in_collectionMemberExpression4497 = new BitSet(new long[]{0x0200020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_OF_in_collectionMemberExpression4500 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_existsExpression4548 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4550 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_subquery_in_existsExpression4556 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_comparisonExpression4598 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000003FF9809338FL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4625 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000003FF9809338FL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_in_comparisonExpression4652 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000003FF9809338FL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4679 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000003FF9809338FL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_in_comparisonExpression4706 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000003FF9809338FL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4733 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000003FF9809338FL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4850 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_subquery_in_arithmeticExpression4856 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4890 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_PLUS_in_simpleArithmeticExpression4906 = new BitSet(new long[]{0x808D52210002C410L,0x0000030798092009L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4912 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_MINUS_in_simpleArithmeticExpression4941 = new BitSet(new long[]{0x808D52210002C410L,0x0000030798092009L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4947 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm5004 = new BitSet(new long[]{0x0000000000000002L,0x0000000060000000L});
    public static final BitSet FOLLOW_MULTIPLY_in_arithmeticTerm5020 = new BitSet(new long[]{0x808D52210002C410L,0x0000030798092009L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm5026 = new BitSet(new long[]{0x0000000000000002L,0x0000000060000000L});
    public static final BitSet FOLLOW_DIVIDE_in_arithmeticTerm5055 = new BitSet(new long[]{0x808D52210002C410L,0x0000030798092009L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm5061 = new BitSet(new long[]{0x0000000000000002L,0x0000000060000000L});
    public static final BitSet FOLLOW_PLUS_in_arithmeticFactor5115 = new BitSet(new long[]{0x808D52210002C410L,0x0000030780092009L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_arithmeticFactor5144 = new BitSet(new long[]{0x808D52210002C410L,0x0000030780092009L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_arithmeticPrimary5208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_arithmeticPrimary5236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_caseExpression_in_arithmeticPrimary5250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5274 = new BitSet(new long[]{0x808D52210002C410L,0x0000030798092009L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5280 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_arithmeticPrimary5296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_scalarExpression5328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_nonArithmeticScalarExpression5403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalTemporal_in_nonArithmeticScalarExpression5431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_anyOrAllExpression5475 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5477 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5483 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_anyOrAllExpression5505 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5507 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5513 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SOME_in_anyOrAllExpression5535 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5537 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5543 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_entityTypeExpression5585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5618 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5620 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5626 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5643 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5645 = new BitSet(new long[]{0x0000000000000000L,0x0000030000000000L});
    public static final BitSet FOLLOW_inputParameter_in_typeDiscriminator5651 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleCaseExpression_in_caseExpression5688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_generalCaseExpression_in_caseExpression5701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_coalesceExpression_in_caseExpression5714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullIfExpression_in_caseExpression5727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_simpleCaseExpression5765 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012200L});
    public static final BitSet FOLLOW_caseOperand_in_simpleCaseExpression5771 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5777 = new BitSet(new long[]{0x0000000001000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5786 = new BitSet(new long[]{0x0000000001000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_ELSE_in_simpleCaseExpression5792 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_simpleCaseExpression5798 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_END_in_simpleCaseExpression5800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_generalCaseExpression5844 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5850 = new BitSet(new long[]{0x0000000001000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5859 = new BitSet(new long[]{0x0000000001000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_ELSE_in_generalCaseExpression5865 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_generalCaseExpression5871 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_END_in_generalCaseExpression5873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COALESCE_in_coalesceExpression5917 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5919 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5925 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_coalesceExpression5930 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5936 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULLIF_in_nullIfExpression5983 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5985 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5991 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_nullIfExpression5993 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5999 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression6001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_caseOperand6048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_caseOperand6062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_whenClause6097 = new BitSet(new long[]{0x80ADD221601FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_conditionalExpression_in_whenClause6103 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_THEN_in_whenClause6105 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_whenClause6111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_simpleWhenClause6153 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause6159 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_THEN_in_simpleWhenClause6161 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause6167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_stringPrimary6250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_stringPrimary6264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_stringPrimary6278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_stringPrimary6292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_literal6326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_literal6340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_literal6354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_LITERAL_in_literalNumeric6384 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LONG_LITERAL_in_literalNumeric6400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_LITERAL_in_literalNumeric6421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_LITERAL_in_literalNumeric6441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literalBoolean6479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literalBoolean6501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_LITERAL_in_literalTemporal6601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIME_LITERAL_in_literalTemporal6615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIMESTAMP_LITERAL_in_literalTemporal6629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POSITIONAL_PARAM_in_inputParameter6659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_PARAM_in_inputParameter6679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_abs_in_functionsReturningNumerics6719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_length_in_functionsReturningNumerics6733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mod_in_functionsReturningNumerics6747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqrt_in_functionsReturningNumerics6761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_locate_in_functionsReturningNumerics6775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_size_in_functionsReturningNumerics6789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_index_in_functionsReturningNumerics6803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_func_in_functionsReturningNumerics6817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_concat_in_functionsReturningStrings6928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_substring_in_functionsReturningStrings6942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trim_in_functionsReturningStrings6956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_upper_in_functionsReturningStrings6970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lower_in_functionsReturningStrings6984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONCAT_in_concat7019 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_concat7030 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_concat7045 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_concat7050 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_concat7056 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_concat7070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSTRING_in_substring7108 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_substring7121 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_substring7135 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_substring7137 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_substring7151 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_COMMA_in_substring7162 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_substring7168 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_substring7180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIM_in_trim7218 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_trim7228 = new BitSet(new long[]{0x0000860200011000L,0x00000318000130A4L});
    public static final BitSet FOLLOW_trimSpec_in_trim7244 = new BitSet(new long[]{0x0000000200000000L,0x0000031800000000L});
    public static final BitSet FOLLOW_trimChar_in_trim7250 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_FROM_in_trim7252 = new BitSet(new long[]{0x0000820000010000L,0x0000031800013084L});
    public static final BitSet FOLLOW_stringPrimary_in_trim7268 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_trim7278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEADING_in_trimSpec7314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRAILING_in_trimSpec7332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOTH_in_trimSpec7350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_trimChar7397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_trimChar7411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPPER_in_upper7448 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_upper7450 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_upper7456 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_upper7458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOWER_in_lower7496 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_lower7498 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_lower7504 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_lower7506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABS_in_abs7545 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_abs7547 = new BitSet(new long[]{0x808D52210002C410L,0x0000030798092009L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_abs7553 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_abs7555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LENGTH_in_length7593 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_length7595 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_length7601 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_length7603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCATE_in_locate7641 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_locate7651 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_locate7666 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_locate7668 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_locate7674 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_COMMA_in_locate7686 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_locate7692 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_locate7705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIZE_in_size7743 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_size7754 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_size7760 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_size7762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mod7800 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mod7802 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_mod7816 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_mod7818 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_mod7833 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mod7843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQRT_in_sqrt7881 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7892 = new BitSet(new long[]{0x808DD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_scalarExpression_in_sqrt7898 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDEX_in_index7942 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_index7944 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_index7950 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_index7952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNC_in_func7994 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_func7996 = new BitSet(new long[]{0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_func8008 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_COMMA_in_func8017 = new BitSet(new long[]{0x80CDD221401FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_newValue_in_func8023 = new BitSet(new long[]{0x0000000000000000L,0x0000000000120000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_func8055 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleSelectClause_in_subquery8093 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_subqueryFromClause_in_subquery8108 = new BitSet(new long[]{0x0000000C00000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_whereClause_in_subquery8123 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_groupByClause_in_subquery8138 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_havingClause_in_subquery8154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_simpleSelectClause8197 = new BitSet(new long[]{0x0005020000820400L,0x0000000000012008L});
    public static final BitSet FOLLOW_DISTINCT_in_simpleSelectClause8200 = new BitSet(new long[]{0x0005020000020400L,0x0000000000012008L});
    public static final BitSet FOLLOW_simpleSelectExpression_in_simpleSelectClause8216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_simpleSelectExpression8256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_simpleSelectExpression8271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression8286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_subqueryFromClause8321 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8323 = new BitSet(new long[]{0x0000001000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_subqueryFromClause8350 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00FFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8369 = new BitSet(new long[]{0x0000001000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8395 = new BitSet(new long[]{0x0000001000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8455 = new BitSet(new long[]{0x0000000000000100L,0x0000000000010000L});
    public static final BitSet FOLLOW_AS_in_subselectIdentificationVariableDeclaration8458 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8464 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_join_in_subselectIdentificationVariableDeclaration8467 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_orderByClause8527 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_orderByClause8529 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8543 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_orderByClause8558 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8564 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_orderByItem8610 = new BitSet(new long[]{0x0000000000200202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem8624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem8653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_orderByItem8715 = new BitSet(new long[]{0x0000000000200202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem8729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem8758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_groupByClause8839 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_groupByClause8841 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause8855 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_groupByClause8868 = new BitSet(new long[]{0x0000020000000000L,0x0000000000012000L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause8874 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_groupByItem8920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_groupByItem8934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HAVING_in_havingClause8964 = new BitSet(new long[]{0x80ADD221601FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_conditionalExpression_in_havingClause8981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_synpred13599 = new BitSet(new long[]{0x80ADD221601FC410L,0x000003FF9809338DL});
    public static final BitSet FOLLOW_conditionalExpression_in_synpred13601 = new BitSet(new long[]{0x0000000000000002L});

}