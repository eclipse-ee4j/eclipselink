// $ANTLR 3.0 JPQL.g 2009-06-16 13:44:28

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ABS", "ALL", "AND", "ANY", "AS", "ASC", "AVG", "BETWEEN", "BOTH", "BY", "CASE", "COALESCE", "CONCAT", "COUNT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DESC", "DELETE", "DISTINCT", "ELSE", "EMPTY", "END", "ENTRY", "ESCAPE", "EXISTS", "FALSE", "FETCH", "FROM", "GROUP", "HAVING", "IN", "INDEX", "INNER", "IS", "JOIN", "KEY", "LEADING", "LEFT", "LENGTH", "LIKE", "LOCATE", "LOWER", "MAX", "MEMBER", "MIN", "MOD", "NEW", "NOT", "NULL", "NULLIF", "OBJECT", "OF", "OR", "ORDER", "OUTER", "SELECT", "SET", "SIZE", "SQRT", "SOME", "SUBSTRING", "SUM", "THEN", "TRAILING", "TRIM", "TRUE", "TYPE", "UNKNOWN", "UPDATE", "UPPER", "VALUE", "WHEN", "WHERE", "IDENT", "COMMA", "EQUALS", "LEFT_ROUND_BRACKET", "RIGHT_ROUND_BRACKET", "DOT", "NOT_EQUAL_TO", "GREATER_THAN", "GREATER_THAN_EQUAL_TO", "LESS_THAN", "LESS_THAN_EQUAL_TO", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "INTEGER_LITERAL", "LONG_LITERAL", "FLOAT_LITERAL", "DOUBLE_LITERAL", "STRING_LITERAL_DOUBLE_QUOTED", "STRING_LITERAL_SINGLE_QUOTED", "POSITIONAL_PARAM", "NAMED_PARAM", "WS", "TEXTCHAR", "HEX_DIGIT", "HEX_LITERAL", "INTEGER_SUFFIX", "OCTAL_LITERAL", "NUMERIC_DIGITS", "DOUBLE_SUFFIX", "EXPONENT", "FLOAT_SUFFIX"
    };
    public static final int EXPONENT=109;
    public static final int FLOAT_SUFFIX=110;
    public static final int MOD=50;
    public static final int CURRENT_TIME=19;
    public static final int CASE=14;
    public static final int NEW=51;
    public static final int LEFT_ROUND_BRACKET=81;
    public static final int DOUBLE_LITERAL=96;
    public static final int COUNT=17;
    public static final int EQUALS=80;
    public static final int NOT=52;
    public static final int EOF=-1;
    public static final int TYPE=71;
    public static final int GREATER_THAN_EQUAL_TO=86;
    public static final int ESCAPE=28;
    public static final int NAMED_PARAM=100;
    public static final int BOTH=12;
    public static final int NUMERIC_DIGITS=107;
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
    public static final int OF=56;
    public static final int LEADING=41;
    public static final int INTEGER_SUFFIX=105;
    public static final int EMPTY=25;
    public static final int ABS=4;
    public static final int GROUP=33;
    public static final int NOT_EQUAL_TO=84;
    public static final int WS=101;
    public static final int FETCH=31;
    public static final int STRING_LITERAL_SINGLE_QUOTED=98;
    public static final int INTEGER_LITERAL=93;
    public static final int OR=57;
    public static final int TRIM=69;
    public static final int LESS_THAN=87;
    public static final int RIGHT_ROUND_BRACKET=82;
    public static final int POSITIONAL_PARAM=99;
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
    public static final int TEXTCHAR=102;
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
    public static final int HEX_LITERAL=104;
    public static final int EXISTS=29;
    public static final int DOT=83;
    public static final int CURRENT_TIMESTAMP=20;
    public static final int LIKE=44;
    public static final int OUTER=59;
    public static final int BY=13;
    public static final int GREATER_THAN=85;
    public static final int OCTAL_LITERAL=106;
    public static final int HEX_DIGIT=103;
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
    public static final int DOUBLE_SUFFIX=108;
    public static final int FLOAT_LITERAL=95;
    public static final int ANY=7;
    public static final int LOCATE=45;
    public static final int WHEN=76;
    public static final int DESC=21;
    public static final int BETWEEN=11;
    
        public JPQLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[110+1];
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
                
                if ( (LA10_1==EQUALS) ) {
                    alt10=1;
                }
                else if ( (LA10_1==DOT) ) {
                    alt10=2;
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
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
            case WS:
            case TEXTCHAR:
            case HEX_DIGIT:
            case HEX_LITERAL:
            case INTEGER_SUFFIX:
            case OCTAL_LITERAL:
            case NUMERIC_DIGITS:
            case DOUBLE_SUFFIX:
            case EXPONENT:
            case FLOAT_SUFFIX:
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
    // JPQL.g:360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );
    public final Object selectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:362:7: (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression )
            int alt19=5;
            switch ( input.LA(1) ) {
            case AVG:
                {
                int LA19_1 = input.LA(2);
                
                if ( (LA19_1==LEFT_ROUND_BRACKET) ) {
                    switch ( input.LA(3) ) {
                    case DISTINCT:
                        {
                        int LA19_49 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 49, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA19_50 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 50, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA19_51 = input.LA(4);
                        
                        if ( (!( aggregatesAllowed() )) ) {
                            alt19=1;
                        }
                        else if ( ( aggregatesAllowed() ) ) {
                            alt19=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 51, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 52, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 44, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 1, input);
                
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 53, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 54, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 55, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 56, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 45, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 2, input);
                
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 57, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 58, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 59, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 60, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 46, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 3, input);
                
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 61, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 62, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 63, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 64, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 47, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 4, input);
                
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 65, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 66, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 67, input);
                        
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
                                new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 68, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 48, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 5, input);
                
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
                    new NoViableAltException("360:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 0, input);
            
                throw nvae;
            }
            
            switch (alt19) {
                case 1 :
                    // JPQL.g:362:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_selectExpression1644);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:363:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_selectExpression1658);
                    n=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:364:7: OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    match(input,OBJECT,FOLLOW_OBJECT_in_selectExpression1668); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1670); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_selectExpression1676);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1678); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:365:7: n= constructorExpression
                    {
                    pushFollow(FOLLOW_constructorExpression_in_selectExpression1693);
                    n=constructorExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:366:7: n= mapEntryExpression
                    {
                    pushFollow(FOLLOW_mapEntryExpression_in_selectExpression1708);
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
    // JPQL.g:369:1: mapEntryExpression returns [Object node] : l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object mapEntryExpression() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:371:7: (l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // JPQL.g:371:7: l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,ENTRY,FOLLOW_ENTRY_in_mapEntryExpression1740); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1742); if (failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1748);
            n=variableAccessOrTypeConstant();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1750); if (failed) return node;
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
    // JPQL.g:374:1: pathExprOrVariableAccess returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )* ;
    public final Object pathExprOrVariableAccess() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:378:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )* )
            // JPQL.g:378:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )*
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1782);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:379:9: (d= DOT right= attribute )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);
                
                if ( (LA20_0==DOT) ) {
                    alt20=1;
                }
                
            
                switch (alt20) {
            	case 1 :
            	    // JPQL.g:379:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExprOrVariableAccess1797); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExprOrVariableAccess1803);
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
    // JPQL.g:384:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );
    public final Object qualifiedIdentificationVariable() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:386:7: (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
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
                    new NoViableAltException("384:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );", 21, 0, input);
            
                throw nvae;
            }
            
            switch (alt21) {
                case 1 :
                    // JPQL.g:386:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1859);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:387:7: l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,KEY,FOLLOW_KEY_in_qualifiedIdentificationVariable1873); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1875); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1881);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1883); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newKey(l.getLine(), l.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:388:7: l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,VALUE,FOLLOW_VALUE_in_qualifiedIdentificationVariable1898); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1900); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1906);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1908); if (failed) return node;
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
    // JPQL.g:391:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );
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
            // JPQL.g:399:7: (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET )
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
                    new NoViableAltException("391:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );", 27, 0, input);
            
                throw nvae;
            }
            
            switch (alt27) {
                case 1 :
                    // JPQL.g:399:7: t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t1=(Token)input.LT(1);
                    match(input,AVG,FOLLOW_AVG_in_aggregateExpression1941); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1943); if (failed) return node;
                    // JPQL.g:399:33: ( DISTINCT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);
                    
                    if ( (LA22_0==DISTINCT) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // JPQL.g:399:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1946); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1964);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1966); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAvg(t1.getLine(), t1.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:402:7: t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t2=(Token)input.LT(1);
                    match(input,MAX,FOLLOW_MAX_in_aggregateExpression1987); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1989); if (failed) return node;
                    // JPQL.g:402:33: ( DISTINCT )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    
                    if ( (LA23_0==DISTINCT) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // JPQL.g:402:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1992); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2011);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2013); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMax(t2.getLine(), t2.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:405:7: t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t3=(Token)input.LT(1);
                    match(input,MIN,FOLLOW_MIN_in_aggregateExpression2033); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2035); if (failed) return node;
                    // JPQL.g:405:33: ( DISTINCT )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);
                    
                    if ( (LA24_0==DISTINCT) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // JPQL.g:405:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2038); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2056);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2058); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMin(t3.getLine(), t3.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:408:7: t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t4=(Token)input.LT(1);
                    match(input,SUM,FOLLOW_SUM_in_aggregateExpression2078); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2080); if (failed) return node;
                    // JPQL.g:408:33: ( DISTINCT )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);
                    
                    if ( (LA25_0==DISTINCT) ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // JPQL.g:408:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2083); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2101);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2103); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newSum(t4.getLine(), t4.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:411:7: t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET
                    {
                    t5=(Token)input.LT(1);
                    match(input,COUNT,FOLLOW_COUNT_in_aggregateExpression2123); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2125); if (failed) return node;
                    // JPQL.g:411:35: ( DISTINCT )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);
                    
                    if ( (LA26_0==DISTINCT) ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // JPQL.g:411:36: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2128); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_aggregateExpression2146);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2148); if (failed) return node;
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
    // JPQL.g:416:1: constructorExpression returns [Object node] : t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET ;
    public final Object constructorExpression() throws RecognitionException {
        constructorExpression_stack.push(new constructorExpression_scope());

        Object node = null;
    
        Token t=null;
        String className = null;

        Object n = null;
        
    
         
            node = null;
            ((constructorExpression_scope)constructorExpression_stack.peek()).args = new ArrayList();
    
        try {
            // JPQL.g:424:7: (t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET )
            // JPQL.g:424:7: t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,NEW,FOLLOW_NEW_in_constructorExpression2191); if (failed) return node;
            pushFollow(FOLLOW_constructorName_in_constructorExpression2197);
            className=constructorName();
            _fsp--;
            if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2207); if (failed) return node;
            pushFollow(FOLLOW_constructorItem_in_constructorExpression2222);
            n=constructorItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            }
            // JPQL.g:427:9: ( COMMA n= constructorItem )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);
                
                if ( (LA28_0==COMMA) ) {
                    alt28=1;
                }
                
            
                switch (alt28) {
            	case 1 :
            	    // JPQL.g:427:11: COMMA n= constructorItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constructorExpression2237); if (failed) return node;
            	    pushFollow(FOLLOW_constructorItem_in_constructorExpression2243);
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

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2258); if (failed) return node;
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
    // JPQL.g:435:1: constructorName returns [String className] : i1= IDENT ( DOT i2= IDENT )* ;
    public final String constructorName() throws RecognitionException {
        constructorName_stack.push(new constructorName_scope());

        String className = null;
    
        Token i1=null;
        Token i2=null;
    
         
            className = null;
            ((constructorName_scope)constructorName_stack.peek()).buf = new StringBuffer(); 
    
        try {
            // JPQL.g:443:7: (i1= IDENT ( DOT i2= IDENT )* )
            // JPQL.g:443:7: i1= IDENT ( DOT i2= IDENT )*
            {
            i1=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_constructorName2299); if (failed) return className;
            if ( backtracking==0 ) {
               ((constructorName_scope)constructorName_stack.peek()).buf.append(i1.getText()); 
            }
            // JPQL.g:444:9: ( DOT i2= IDENT )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);
                
                if ( (LA29_0==DOT) ) {
                    alt29=1;
                }
                
            
                switch (alt29) {
            	case 1 :
            	    // JPQL.g:444:11: DOT i2= IDENT
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_constructorName2313); if (failed) return className;
            	    i2=(Token)input.LT(1);
            	    match(input,IDENT,FOLLOW_IDENT_in_constructorName2317); if (failed) return className;
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
    // JPQL.g:448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );
    public final Object constructorItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:450:7: (n= scalarExpression | n= aggregateExpression )
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
                        int LA30_46 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 46, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA30_47 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 47, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA30_48 = input.LA(4);
                        
                        if ( ( aggregatesAllowed() ) ) {
                            alt30=1;
                        }
                        else if ( (true) ) {
                            alt30=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 48, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 49, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 41, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 3, input);
                
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 50, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 51, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 52, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 53, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 42, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 4, input);
                
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 54, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 55, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 56, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 57, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 43, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 5, input);
                
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 58, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 59, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 60, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 61, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 44, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 6, input);
                
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 62, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 63, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 64, input);
                        
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
                                new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 65, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 45, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 7, input);
                
                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("448:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 0, input);
            
                throw nvae;
            }
            
            switch (alt30) {
                case 1 :
                    // JPQL.g:450:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_constructorItem2361);
                    n=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:451:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_constructorItem2375);
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
    // JPQL.g:455:1: fromClause returns [Object node] : t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* ;
    public final Object fromClause() throws RecognitionException {
        fromClause_stack.push(new fromClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((fromClause_scope)fromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g:463:7: (t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* )
            // JPQL.g:463:7: t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            {
            t=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_fromClause2409); if (failed) return node;
            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2411);
            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g:464:9: ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);
                
                if ( (LA32_0==COMMA) ) {
                    alt32=1;
                }
                
            
                switch (alt32) {
            	case 1 :
            	    // JPQL.g:464:10: COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_fromClause2423); if (failed) return node;
            	    // JPQL.g:464:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
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
            	                new NoViableAltException("464:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 31, 1, input);
            	        
            	            throw nvae;
            	        }
            	    }
            	    else if ( ((LA31_0>=ABS && LA31_0<=HAVING)||(LA31_0>=INDEX && LA31_0<=FLOAT_SUFFIX)) ) {
            	        alt31=1;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return node;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("464:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 31, 0, input);
            	    
            	        throw nvae;
            	    }
            	    switch (alt31) {
            	        case 1 :
            	            // JPQL.g:464:19: identificationVariableDeclaration[$fromClause::varDecls]
            	            {
            	            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2428);
            	            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            	            _fsp--;
            	            if (failed) return node;
            	            
            	            }
            	            break;
            	        case 2 :
            	            // JPQL.g:465:19: n= collectionMemberDeclaration
            	            {
            	            pushFollow(FOLLOW_collectionMemberDeclaration_in_fromClause2453);
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
    // JPQL.g:471:1: identificationVariableDeclaration[List varDecls] : node= rangeVariableDeclaration (node= join )* ;
    public final void identificationVariableDeclaration(List varDecls) throws RecognitionException {
        Object node = null;
        
    
        try {
            // JPQL.g:472:7: (node= rangeVariableDeclaration (node= join )* )
            // JPQL.g:472:7: node= rangeVariableDeclaration (node= join )*
            {
            pushFollow(FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2519);
            node=rangeVariableDeclaration();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               varDecls.add(node); 
            }
            // JPQL.g:473:9: (node= join )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                
                if ( (LA33_0==INNER||LA33_0==JOIN||LA33_0==LEFT) ) {
                    alt33=1;
                }
                
            
                switch (alt33) {
            	case 1 :
            	    // JPQL.g:473:11: node= join
            	    {
            	    pushFollow(FOLLOW_join_in_identificationVariableDeclaration2538);
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
    // JPQL.g:476:1: rangeVariableDeclaration returns [Object node] : schema= abstractSchemaName ( AS )? i= IDENT ;
    public final Object rangeVariableDeclaration() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:480:7: (schema= abstractSchemaName ( AS )? i= IDENT )
            // JPQL.g:480:7: schema= abstractSchemaName ( AS )? i= IDENT
            {
            pushFollow(FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2573);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g:480:35: ( AS )?
            int alt34=2;
            int LA34_0 = input.LA(1);
            
            if ( (LA34_0==AS) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // JPQL.g:480:36: AS
                    {
                    match(input,AS,FOLLOW_AS_in_rangeVariableDeclaration2576); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_rangeVariableDeclaration2582); if (failed) return node;
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
    // JPQL.g:491:1: abstractSchemaName returns [String schema] : ident= . ;
    public final String abstractSchemaName() throws RecognitionException {

        String schema = null;
    
        Token ident=null;
    
         schema = null; 
        try {
            // JPQL.g:493:7: (ident= . )
            // JPQL.g:493:7: ident= .
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
    // JPQL.g:500:1: join returns [Object node] : outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) ;
    public final Object join() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token t=null;
        boolean outerJoin = false;

        Object n = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:504:7: (outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) )
            // JPQL.g:504:7: outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            {
            pushFollow(FOLLOW_joinSpec_in_join2665);
            outerJoin=joinSpec();
            _fsp--;
            if (failed) return node;
            // JPQL.g:505:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            int alt36=2;
            int LA36_0 = input.LA(1);
            
            if ( (LA36_0==IDENT) ) {
                alt36=1;
            }
            else if ( (LA36_0==FETCH) ) {
                alt36=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("505:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )", 36, 0, input);
            
                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // JPQL.g:505:9: n= joinAssociationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2679);
                    n=joinAssociationPathExpression();
                    _fsp--;
                    if (failed) return node;
                    // JPQL.g:505:43: ( AS )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);
                    
                    if ( (LA35_0==AS) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // JPQL.g:505:44: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2682); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_join2688); if (failed) return node;
                    if ( backtracking==0 ) {
                      
                                  node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                     outerJoin, n, i.getText()); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:510:9: t= FETCH n= joinAssociationPathExpression
                    {
                    t=(Token)input.LT(1);
                    match(input,FETCH,FOLLOW_FETCH_in_join2710); if (failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2716);
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
    // JPQL.g:517:1: joinSpec returns [boolean outer] : ( LEFT ( OUTER )? | INNER )? JOIN ;
    public final boolean joinSpec() throws RecognitionException {

        boolean outer = false;
    
         outer = false; 
        try {
            // JPQL.g:519:7: ( ( LEFT ( OUTER )? | INNER )? JOIN )
            // JPQL.g:519:7: ( LEFT ( OUTER )? | INNER )? JOIN
            {
            // JPQL.g:519:7: ( LEFT ( OUTER )? | INNER )?
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
                    // JPQL.g:519:8: LEFT ( OUTER )?
                    {
                    match(input,LEFT,FOLLOW_LEFT_in_joinSpec2762); if (failed) return outer;
                    // JPQL.g:519:13: ( OUTER )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);
                    
                    if ( (LA37_0==OUTER) ) {
                        alt37=1;
                    }
                    switch (alt37) {
                        case 1 :
                            // JPQL.g:519:14: OUTER
                            {
                            match(input,OUTER,FOLLOW_OUTER_in_joinSpec2765); if (failed) return outer;
                            
                            }
                            break;
                    
                    }

                    if ( backtracking==0 ) {
                       outer = true; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:519:44: INNER
                    {
                    match(input,INNER,FOLLOW_INNER_in_joinSpec2774); if (failed) return outer;
                    
                    }
                    break;
            
            }

            match(input,JOIN,FOLLOW_JOIN_in_joinSpec2780); if (failed) return outer;
            
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
    // JPQL.g:522:1: collectionMemberDeclaration returns [Object node] : t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT ;
    public final Object collectionMemberDeclaration() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token i=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:524:7: (t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT )
            // JPQL.g:524:7: t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT
            {
            t=(Token)input.LT(1);
            match(input,IN,FOLLOW_IN_in_collectionMemberDeclaration2808); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2810); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2816);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2818); if (failed) return node;
            // JPQL.g:525:7: ( AS )?
            int alt39=2;
            int LA39_0 = input.LA(1);
            
            if ( (LA39_0==AS) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // JPQL.g:525:8: AS
                    {
                    match(input,AS,FOLLOW_AS_in_collectionMemberDeclaration2828); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_collectionMemberDeclaration2834); if (failed) return node;
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
    // JPQL.g:532:1: collectionValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object collectionValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:534:7: (n= pathExpression )
            // JPQL.g:534:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_collectionValuedPathExpression2872);
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
    // JPQL.g:537:1: associationPathExpression returns [Object node] : n= pathExpression ;
    public final Object associationPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:539:7: (n= pathExpression )
            // JPQL.g:539:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_associationPathExpression2904);
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
    // JPQL.g:542:1: joinAssociationPathExpression returns [Object node] : left= variableAccessOrTypeConstant d= DOT right= attribute ;
    public final Object joinAssociationPathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object left = null;

        Object right = null;
        
    
        
            node = null; 
    
        try {
            // JPQL.g:546:7: (left= variableAccessOrTypeConstant d= DOT right= attribute )
            // JPQL.g:546:7: left= variableAccessOrTypeConstant d= DOT right= attribute
            {
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_joinAssociationPathExpression2936);
            left=variableAccessOrTypeConstant();
            _fsp--;
            if (failed) return node;
            d=(Token)input.LT(1);
            match(input,DOT,FOLLOW_DOT_in_joinAssociationPathExpression2940); if (failed) return node;
            pushFollow(FOLLOW_attribute_in_joinAssociationPathExpression2946);
            right=attribute();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               node = factory.newDot(d.getLine(), d.getCharPositionInLine(), left, right); 
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
    // $ANTLR end joinAssociationPathExpression

    
    // $ANTLR start singleValuedPathExpression
    // JPQL.g:550:1: singleValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object singleValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:552:7: (n= pathExpression )
            // JPQL.g:552:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_singleValuedPathExpression2986);
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
    // JPQL.g:555:1: stateFieldPathExpression returns [Object node] : n= pathExpression ;
    public final Object stateFieldPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:557:7: (n= pathExpression )
            // JPQL.g:557:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_stateFieldPathExpression3018);
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
    // JPQL.g:560:1: pathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object pathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:564:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // JPQL.g:564:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExpression3050);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:565:9: (d= DOT right= attribute )+
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
            	    // JPQL.g:565:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExpression3065); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExpression3071);
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
    // $ANTLR end pathExpression

    
    // $ANTLR start attribute
    // JPQL.g:576:1: attribute returns [Object node] : i= . ;
    public final Object attribute() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g:579:7: (i= . )
            // JPQL.g:579:7: i= .
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
    // JPQL.g:586:1: variableAccessOrTypeConstant returns [Object node] : i= IDENT ;
    public final Object variableAccessOrTypeConstant() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g:588:7: (i= IDENT )
            // JPQL.g:588:7: i= IDENT
            {
            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_variableAccessOrTypeConstant3167); if (failed) return node;
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
    // JPQL.g:592:1: whereClause returns [Object node] : t= WHERE n= conditionalExpression ;
    public final Object whereClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:594:7: (t= WHERE n= conditionalExpression )
            // JPQL.g:594:7: t= WHERE n= conditionalExpression
            {
            t=(Token)input.LT(1);
            match(input,WHERE,FOLLOW_WHERE_in_whereClause3205); if (failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whereClause3211);
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
    // JPQL.g:600:1: conditionalExpression returns [Object node] : n= conditionalTerm (t= OR right= conditionalTerm )* ;
    public final Object conditionalExpression() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:604:7: (n= conditionalTerm (t= OR right= conditionalTerm )* )
            // JPQL.g:604:7: n= conditionalTerm (t= OR right= conditionalTerm )*
            {
            pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3253);
            n=conditionalTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:605:9: (t= OR right= conditionalTerm )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);
                
                if ( (LA41_0==OR) ) {
                    alt41=1;
                }
                
            
                switch (alt41) {
            	case 1 :
            	    // JPQL.g:605:10: t= OR right= conditionalTerm
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,OR,FOLLOW_OR_in_conditionalExpression3268); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3274);
            	    right=conditionalTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newOr(t.getLine(), t.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop41;
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
    // JPQL.g:610:1: conditionalTerm returns [Object node] : n= conditionalFactor (t= AND right= conditionalFactor )* ;
    public final Object conditionalTerm() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:614:7: (n= conditionalFactor (t= AND right= conditionalFactor )* )
            // JPQL.g:614:7: n= conditionalFactor (t= AND right= conditionalFactor )*
            {
            pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3329);
            n=conditionalFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:615:9: (t= AND right= conditionalFactor )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);
                
                if ( (LA42_0==AND) ) {
                    alt42=1;
                }
                
            
                switch (alt42) {
            	case 1 :
            	    // JPQL.g:615:10: t= AND right= conditionalFactor
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,AND,FOLLOW_AND_in_conditionalTerm3344); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3350);
            	    right=conditionalFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newAnd(t.getLine(), t.getCharPositionInLine(), node, right); 
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
    // $ANTLR end conditionalTerm

    
    // $ANTLR start conditionalFactor
    // JPQL.g:620:1: conditionalFactor returns [Object node] : (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) ;
    public final Object conditionalFactor() throws RecognitionException {

        Object node = null;
    
        Token n=null;
        Object n1 = null;
        
    
         node = null; 
        try {
            // JPQL.g:622:7: ( (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) )
            // JPQL.g:622:7: (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            {
            // JPQL.g:622:7: (n= NOT )?
            int alt43=2;
            int LA43_0 = input.LA(1);
            
            if ( (LA43_0==NOT) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // JPQL.g:622:8: n= NOT
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_conditionalFactor3405); if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:623:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            int alt44=2;
            int LA44_0 = input.LA(1);
            
            if ( (LA44_0==ABS||LA44_0==AVG||(LA44_0>=CASE && LA44_0<=CURRENT_TIMESTAMP)||LA44_0==FALSE||LA44_0==INDEX||LA44_0==KEY||LA44_0==LENGTH||(LA44_0>=LOCATE && LA44_0<=MAX)||(LA44_0>=MIN && LA44_0<=MOD)||LA44_0==NULLIF||(LA44_0>=SIZE && LA44_0<=SQRT)||(LA44_0>=SUBSTRING && LA44_0<=SUM)||(LA44_0>=TRIM && LA44_0<=TYPE)||(LA44_0>=UPPER && LA44_0<=VALUE)||LA44_0==IDENT||LA44_0==LEFT_ROUND_BRACKET||(LA44_0>=PLUS && LA44_0<=MINUS)||(LA44_0>=INTEGER_LITERAL && LA44_0<=NAMED_PARAM)) ) {
                alt44=1;
            }
            else if ( (LA44_0==EXISTS) ) {
                alt44=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("623:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )", 44, 0, input);
            
                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // JPQL.g:623:11: n1= conditionalPrimary
                    {
                    pushFollow(FOLLOW_conditionalPrimary_in_conditionalFactor3424);
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
                    // JPQL.g:630:11: n1= existsExpression[(n!=null)]
                    {
                    pushFollow(FOLLOW_existsExpression_in_conditionalFactor3453);
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
    // JPQL.g:634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );
    public final Object conditionalPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:636:7: ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression )
            int alt45=2;
            int LA45_0 = input.LA(1);
            
            if ( (LA45_0==LEFT_ROUND_BRACKET) ) {
                int LA45_1 = input.LA(2);
                
                if ( (LA45_1==NOT) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==LEFT_ROUND_BRACKET) ) {
                    int LA45_42 = input.LA(3);
                    
                    if ( (LA45_42==NOT) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==LEFT_ROUND_BRACKET) ) {
                        int LA45_85 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 85, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==PLUS) ) {
                        int LA45_86 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 86, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==MINUS) ) {
                        int LA45_87 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 87, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==AVG) ) {
                        int LA45_88 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 88, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==MAX) ) {
                        int LA45_89 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 89, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==MIN) ) {
                        int LA45_90 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 90, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==SUM) ) {
                        int LA45_91 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 91, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==COUNT) ) {
                        int LA45_92 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 92, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==IDENT) ) {
                        int LA45_93 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 93, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==KEY) ) {
                        int LA45_94 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 94, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==VALUE) ) {
                        int LA45_95 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 95, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==POSITIONAL_PARAM) ) {
                        int LA45_96 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 96, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==NAMED_PARAM) ) {
                        int LA45_97 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 97, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==CASE) ) {
                        int LA45_98 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 98, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==COALESCE) ) {
                        int LA45_99 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 99, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==NULLIF) ) {
                        int LA45_100 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 100, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==ABS) ) {
                        int LA45_101 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 101, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==LENGTH) ) {
                        int LA45_102 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 102, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==MOD) ) {
                        int LA45_103 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 103, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==SQRT) ) {
                        int LA45_104 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 104, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==LOCATE) ) {
                        int LA45_105 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 105, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==SIZE) ) {
                        int LA45_106 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 106, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==INDEX) ) {
                        int LA45_107 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 107, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==INTEGER_LITERAL) ) {
                        int LA45_108 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 108, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==LONG_LITERAL) ) {
                        int LA45_109 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 109, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==FLOAT_LITERAL) ) {
                        int LA45_110 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 110, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==DOUBLE_LITERAL) ) {
                        int LA45_111 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 111, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_42==CURRENT_DATE) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==CURRENT_TIME) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==CURRENT_TIMESTAMP) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==CONCAT) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==SUBSTRING) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==TRIM) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==UPPER) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==LOWER) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==STRING_LITERAL_DOUBLE_QUOTED) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==STRING_LITERAL_SINGLE_QUOTED) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==TRUE) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==FALSE) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==TYPE) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==EXISTS) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_42==SELECT) && (synpred1())) {
                        alt45=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 42, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==PLUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
                        {
                        int LA45_127 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 127, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA45_128 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 128, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA45_129 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 129, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
                        {
                        int LA45_130 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 130, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
                        {
                        int LA45_131 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 131, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA45_132 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 132, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA45_133 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 133, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA45_134 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 134, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
                        {
                        int LA45_135 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 135, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
                        {
                        int LA45_136 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 136, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CASE:
                        {
                        int LA45_137 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 137, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COALESCE:
                        {
                        int LA45_138 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 138, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NULLIF:
                        {
                        int LA45_139 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 139, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
                        {
                        int LA45_140 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 140, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
                        {
                        int LA45_141 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 141, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
                        {
                        int LA45_142 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 142, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
                        {
                        int LA45_143 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 143, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
                        {
                        int LA45_144 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 144, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
                        {
                        int LA45_145 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 145, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INDEX:
                        {
                        int LA45_146 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 146, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
                        int LA45_147 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 147, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
                        {
                        int LA45_148 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 148, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
                        {
                        int LA45_149 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 149, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
                        {
                        int LA45_150 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 150, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
                        {
                        int LA45_151 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 151, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 43, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA45_1==MINUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
                        {
                        int LA45_152 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 152, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA45_153 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 153, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA45_154 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 154, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
                        {
                        int LA45_155 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 155, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
                        {
                        int LA45_156 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 156, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA45_157 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 157, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA45_158 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 158, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA45_159 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 159, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
                        {
                        int LA45_160 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 160, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
                        {
                        int LA45_161 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 161, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CASE:
                        {
                        int LA45_162 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 162, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COALESCE:
                        {
                        int LA45_163 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 163, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NULLIF:
                        {
                        int LA45_164 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 164, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
                        {
                        int LA45_165 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 165, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
                        {
                        int LA45_166 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 166, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
                        {
                        int LA45_167 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 167, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
                        {
                        int LA45_168 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 168, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
                        {
                        int LA45_169 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 169, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
                        {
                        int LA45_170 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 170, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INDEX:
                        {
                        int LA45_171 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 171, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
                        int LA45_172 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 172, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
                        {
                        int LA45_173 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 173, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
                        {
                        int LA45_174 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 174, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
                        {
                        int LA45_175 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 175, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
                        {
                        int LA45_176 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 176, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 44, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA45_1==AVG) ) {
                    int LA45_45 = input.LA(3);
                    
                    if ( (LA45_45==LEFT_ROUND_BRACKET) ) {
                        int LA45_177 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 177, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 45, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==MAX) ) {
                    int LA45_46 = input.LA(3);
                    
                    if ( (LA45_46==LEFT_ROUND_BRACKET) ) {
                        int LA45_178 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 178, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 46, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==MIN) ) {
                    int LA45_47 = input.LA(3);
                    
                    if ( (LA45_47==LEFT_ROUND_BRACKET) ) {
                        int LA45_179 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 179, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 47, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==SUM) ) {
                    int LA45_48 = input.LA(3);
                    
                    if ( (LA45_48==LEFT_ROUND_BRACKET) ) {
                        int LA45_180 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 180, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 48, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==COUNT) ) {
                    int LA45_49 = input.LA(3);
                    
                    if ( (LA45_49==LEFT_ROUND_BRACKET) ) {
                        int LA45_181 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 181, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 49, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==IDENT) ) {
                    int LA45_50 = input.LA(3);
                    
                    if ( (LA45_50==DOT) ) {
                        int LA45_182 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 182, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_50==MULTIPLY) ) {
                        int LA45_183 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 183, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_50==DIVIDE) ) {
                        int LA45_184 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 184, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_50==PLUS) ) {
                        int LA45_185 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 185, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_50==MINUS) ) {
                        int LA45_186 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 186, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_50==EQUALS) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==NOT_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==GREATER_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==LESS_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==NOT) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==BETWEEN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==LIKE) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==IN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==MEMBER) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==IS) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_50==RIGHT_ROUND_BRACKET) ) {
                        alt45=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 50, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==KEY) ) {
                    int LA45_51 = input.LA(3);
                    
                    if ( (LA45_51==LEFT_ROUND_BRACKET) ) {
                        int LA45_200 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 200, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 51, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==VALUE) ) {
                    int LA45_52 = input.LA(3);
                    
                    if ( (LA45_52==LEFT_ROUND_BRACKET) ) {
                        int LA45_201 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 201, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 52, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==POSITIONAL_PARAM) ) {
                    int LA45_53 = input.LA(3);
                    
                    if ( (LA45_53==MULTIPLY) ) {
                        int LA45_202 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 202, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_53==DIVIDE) ) {
                        int LA45_203 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 203, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_53==PLUS) ) {
                        int LA45_204 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 204, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_53==MINUS) ) {
                        int LA45_205 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 205, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_53==RIGHT_ROUND_BRACKET) ) {
                        alt45=2;
                    }
                    else if ( (LA45_53==EQUALS) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_53==NOT_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_53==GREATER_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_53==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_53==LESS_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_53==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_53==NOT) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_53==BETWEEN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_53==LIKE) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_53==IN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_53==MEMBER) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_53==IS) && (synpred1())) {
                        alt45=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 53, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==NAMED_PARAM) ) {
                    int LA45_54 = input.LA(3);
                    
                    if ( (LA45_54==MULTIPLY) ) {
                        int LA45_219 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 219, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_54==DIVIDE) ) {
                        int LA45_220 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 220, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_54==PLUS) ) {
                        int LA45_221 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 221, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_54==MINUS) ) {
                        int LA45_222 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 222, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_54==RIGHT_ROUND_BRACKET) ) {
                        alt45=2;
                    }
                    else if ( (LA45_54==EQUALS) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_54==NOT_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_54==GREATER_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_54==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_54==LESS_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_54==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_54==NOT) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_54==BETWEEN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_54==LIKE) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_54==IN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_54==MEMBER) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_54==IS) && (synpred1())) {
                        alt45=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 54, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==CASE) ) {
                    switch ( input.LA(3) ) {
                    case WHEN:
                        {
                        int LA45_236 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 236, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA45_237 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 237, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA45_238 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 238, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA45_239 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 239, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TYPE:
                        {
                        int LA45_240 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 240, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 55, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA45_1==COALESCE) ) {
                    int LA45_56 = input.LA(3);
                    
                    if ( (LA45_56==RIGHT_ROUND_BRACKET) ) {
                        int LA45_241 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 241, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 56, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==NULLIF) ) {
                    int LA45_57 = input.LA(3);
                    
                    if ( (LA45_57==RIGHT_ROUND_BRACKET) ) {
                        int LA45_242 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 242, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 57, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==ABS) ) {
                    int LA45_58 = input.LA(3);
                    
                    if ( (LA45_58==LEFT_ROUND_BRACKET) ) {
                        int LA45_243 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 243, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 58, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==LENGTH) ) {
                    int LA45_59 = input.LA(3);
                    
                    if ( (LA45_59==LEFT_ROUND_BRACKET) ) {
                        int LA45_244 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 244, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 59, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==MOD) ) {
                    int LA45_60 = input.LA(3);
                    
                    if ( (LA45_60==LEFT_ROUND_BRACKET) ) {
                        int LA45_245 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 245, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 60, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==SQRT) ) {
                    int LA45_61 = input.LA(3);
                    
                    if ( (LA45_61==LEFT_ROUND_BRACKET) ) {
                        int LA45_246 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 246, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 61, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==LOCATE) ) {
                    int LA45_62 = input.LA(3);
                    
                    if ( (LA45_62==LEFT_ROUND_BRACKET) ) {
                        int LA45_247 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 247, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 62, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==SIZE) ) {
                    int LA45_63 = input.LA(3);
                    
                    if ( (LA45_63==LEFT_ROUND_BRACKET) ) {
                        int LA45_248 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 248, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 63, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==INDEX) ) {
                    int LA45_64 = input.LA(3);
                    
                    if ( (LA45_64==LEFT_ROUND_BRACKET) ) {
                        int LA45_249 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 249, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 64, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==INTEGER_LITERAL) ) {
                    int LA45_65 = input.LA(3);
                    
                    if ( (LA45_65==MULTIPLY) ) {
                        int LA45_250 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 250, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_65==DIVIDE) ) {
                        int LA45_251 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 251, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_65==PLUS) ) {
                        int LA45_252 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 252, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_65==MINUS) ) {
                        int LA45_253 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 253, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_65==EQUALS) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==NOT_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==GREATER_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==LESS_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==NOT) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==BETWEEN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==LIKE) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==IN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==MEMBER) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==IS) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_65==RIGHT_ROUND_BRACKET) ) {
                        alt45=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 65, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==LONG_LITERAL) ) {
                    int LA45_66 = input.LA(3);
                    
                    if ( (LA45_66==MULTIPLY) ) {
                        int LA45_267 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 267, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_66==DIVIDE) ) {
                        int LA45_268 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 268, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_66==PLUS) ) {
                        int LA45_269 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 269, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_66==MINUS) ) {
                        int LA45_270 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 270, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_66==EQUALS) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==NOT_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==GREATER_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==LESS_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==NOT) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==BETWEEN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==LIKE) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==IN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==MEMBER) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==IS) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_66==RIGHT_ROUND_BRACKET) ) {
                        alt45=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 66, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==FLOAT_LITERAL) ) {
                    int LA45_67 = input.LA(3);
                    
                    if ( (LA45_67==MULTIPLY) ) {
                        int LA45_284 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 284, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_67==DIVIDE) ) {
                        int LA45_285 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 285, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_67==PLUS) ) {
                        int LA45_286 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 286, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_67==MINUS) ) {
                        int LA45_287 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 287, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_67==RIGHT_ROUND_BRACKET) ) {
                        alt45=2;
                    }
                    else if ( (LA45_67==EQUALS) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_67==NOT_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_67==GREATER_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_67==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_67==LESS_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_67==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_67==NOT) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_67==BETWEEN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_67==LIKE) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_67==IN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_67==MEMBER) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_67==IS) && (synpred1())) {
                        alt45=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 67, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==DOUBLE_LITERAL) ) {
                    int LA45_68 = input.LA(3);
                    
                    if ( (LA45_68==MULTIPLY) ) {
                        int LA45_301 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 301, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_68==DIVIDE) ) {
                        int LA45_302 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 302, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_68==PLUS) ) {
                        int LA45_303 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 303, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_68==MINUS) ) {
                        int LA45_304 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt45=1;
                        }
                        else if ( (true) ) {
                            alt45=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 304, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA45_68==RIGHT_ROUND_BRACKET) ) {
                        alt45=2;
                    }
                    else if ( (LA45_68==EQUALS) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_68==NOT_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_68==GREATER_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_68==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_68==LESS_THAN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_68==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_68==NOT) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_68==BETWEEN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_68==LIKE) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_68==IN) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_68==MEMBER) && (synpred1())) {
                        alt45=1;
                    }
                    else if ( (LA45_68==IS) && (synpred1())) {
                        alt45=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 68, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA45_1==CURRENT_DATE) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==CURRENT_TIME) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==CURRENT_TIMESTAMP) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==CONCAT) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==SUBSTRING) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==TRIM) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==UPPER) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==LOWER) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==STRING_LITERAL_DOUBLE_QUOTED) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==STRING_LITERAL_SINGLE_QUOTED) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==TRUE) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==FALSE) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==TYPE) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==EXISTS) && (synpred1())) {
                    alt45=1;
                }
                else if ( (LA45_1==SELECT) ) {
                    alt45=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA45_0==ABS||LA45_0==AVG||(LA45_0>=CASE && LA45_0<=CURRENT_TIMESTAMP)||LA45_0==FALSE||LA45_0==INDEX||LA45_0==KEY||LA45_0==LENGTH||(LA45_0>=LOCATE && LA45_0<=MAX)||(LA45_0>=MIN && LA45_0<=MOD)||LA45_0==NULLIF||(LA45_0>=SIZE && LA45_0<=SQRT)||(LA45_0>=SUBSTRING && LA45_0<=SUM)||(LA45_0>=TRIM && LA45_0<=TYPE)||(LA45_0>=UPPER && LA45_0<=VALUE)||LA45_0==IDENT||(LA45_0>=PLUS && LA45_0<=MINUS)||(LA45_0>=INTEGER_LITERAL && LA45_0<=NAMED_PARAM)) ) {
                alt45=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("634:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 45, 0, input);
            
                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // JPQL.g:636:7: ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3510); if (failed) return node;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalPrimary3516);
                    n=conditionalExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3518); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:638:7: n= simpleConditionalExpression
                    {
                    pushFollow(FOLLOW_simpleConditionalExpression_in_conditionalPrimary3532);
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
    // JPQL.g:641:1: simpleConditionalExpression returns [Object node] : (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] );
    public final Object simpleConditionalExpression() throws RecognitionException {

        Object node = null;
    
        Object left = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:645:7: (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] )
            int alt46=2;
            int LA46_0 = input.LA(1);
            
            if ( (LA46_0==ABS||LA46_0==AVG||(LA46_0>=CASE && LA46_0<=COALESCE)||LA46_0==COUNT||LA46_0==INDEX||LA46_0==KEY||LA46_0==LENGTH||LA46_0==LOCATE||LA46_0==MAX||(LA46_0>=MIN && LA46_0<=MOD)||LA46_0==NULLIF||(LA46_0>=SIZE && LA46_0<=SQRT)||LA46_0==SUM||LA46_0==VALUE||LA46_0==IDENT||LA46_0==LEFT_ROUND_BRACKET||(LA46_0>=PLUS && LA46_0<=MINUS)||(LA46_0>=INTEGER_LITERAL && LA46_0<=DOUBLE_LITERAL)||(LA46_0>=POSITIONAL_PARAM && LA46_0<=NAMED_PARAM)) ) {
                alt46=1;
            }
            else if ( (LA46_0==CONCAT||(LA46_0>=CURRENT_DATE && LA46_0<=CURRENT_TIMESTAMP)||LA46_0==FALSE||LA46_0==LOWER||LA46_0==SUBSTRING||(LA46_0>=TRIM && LA46_0<=TYPE)||LA46_0==UPPER||(LA46_0>=STRING_LITERAL_DOUBLE_QUOTED && LA46_0<=STRING_LITERAL_SINGLE_QUOTED)) ) {
                alt46=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("641:1: simpleConditionalExpression returns [Object node] : (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] );", 46, 0, input);
            
                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // JPQL.g:645:7: left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_simpleConditionalExpression3564);
                    left=arithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3570);
                    n=simpleConditionalExpressionRemainder(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:646:7: left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3585);
                    left=nonArithmeticScalarExpression();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3591);
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
    // JPQL.g:649:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );
    public final Object simpleConditionalExpressionRemainder(Object left) throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Token n2=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:651:7: (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] )
            int alt49=3;
            switch ( input.LA(1) ) {
            case EQUALS:
            case NOT_EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL_TO:
            case LESS_THAN:
            case LESS_THAN_EQUAL_TO:
                {
                alt49=1;
                }
                break;
            case BETWEEN:
            case IN:
            case LIKE:
            case MEMBER:
            case NOT:
                {
                alt49=2;
                }
                break;
            case IS:
                {
                alt49=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("649:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );", 49, 0, input);
            
                throw nvae;
            }
            
            switch (alt49) {
                case 1 :
                    // JPQL.g:651:7: n= comparisonExpression[left]
                    {
                    pushFollow(FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3626);
                    n=comparisonExpression(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:652:7: (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left]
                    {
                    // JPQL.g:652:7: (n1= NOT )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);
                    
                    if ( (LA47_0==NOT) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // JPQL.g:652:8: n1= NOT
                            {
                            n1=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3640); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3648);
                    n=conditionWithNotExpression((n1!=null),  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:653:7: IS (n2= NOT )? n= isExpression[(n2!=null), left]
                    {
                    match(input,IS,FOLLOW_IS_in_simpleConditionalExpressionRemainder3659); if (failed) return node;
                    // JPQL.g:653:10: (n2= NOT )?
                    int alt48=2;
                    int LA48_0 = input.LA(1);
                    
                    if ( (LA48_0==NOT) ) {
                        alt48=1;
                    }
                    switch (alt48) {
                        case 1 :
                            // JPQL.g:653:11: n2= NOT
                            {
                            n2=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3664); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3672);
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
    // JPQL.g:656:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );
    public final Object conditionWithNotExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:658:7: (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] )
            int alt50=4;
            switch ( input.LA(1) ) {
            case BETWEEN:
                {
                alt50=1;
                }
                break;
            case LIKE:
                {
                alt50=2;
                }
                break;
            case IN:
                {
                alt50=3;
                }
                break;
            case MEMBER:
                {
                alt50=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("656:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );", 50, 0, input);
            
                throw nvae;
            }
            
            switch (alt50) {
                case 1 :
                    // JPQL.g:658:7: n= betweenExpression[not, left]
                    {
                    pushFollow(FOLLOW_betweenExpression_in_conditionWithNotExpression3707);
                    n=betweenExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:659:7: n= likeExpression[not, left]
                    {
                    pushFollow(FOLLOW_likeExpression_in_conditionWithNotExpression3722);
                    n=likeExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:660:7: n= inExpression[not, left]
                    {
                    pushFollow(FOLLOW_inExpression_in_conditionWithNotExpression3736);
                    n=inExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:661:7: n= collectionMemberExpression[not, left]
                    {
                    pushFollow(FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3750);
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
    // JPQL.g:664:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );
    public final Object isExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:666:7: (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] )
            int alt51=2;
            int LA51_0 = input.LA(1);
            
            if ( (LA51_0==NULL) ) {
                alt51=1;
            }
            else if ( (LA51_0==EMPTY) ) {
                alt51=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("664:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );", 51, 0, input);
            
                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // JPQL.g:666:7: n= nullComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_nullComparisonExpression_in_isExpression3785);
                    n=nullComparisonExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:667:7: n= emptyCollectionComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_emptyCollectionComparisonExpression_in_isExpression3800);
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
    // JPQL.g:670:1: betweenExpression[boolean not, Object left] returns [Object node] : t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression ;
    public final Object betweenExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object lowerBound = null;

        Object upperBound = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:674:7: (t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression )
            // JPQL.g:674:7: t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression
            {
            t=(Token)input.LT(1);
            match(input,BETWEEN,FOLLOW_BETWEEN_in_betweenExpression3833); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3847);
            lowerBound=arithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,AND,FOLLOW_AND_in_betweenExpression3849); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3855);
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
    // JPQL.g:682:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );
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
            // JPQL.g:690:8: (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET )
            int alt54=2;
            int LA54_0 = input.LA(1);
            
            if ( (LA54_0==IN) ) {
                int LA54_1 = input.LA(2);
                
                if ( (LA54_1==LEFT_ROUND_BRACKET) ) {
                    alt54=2;
                }
                else if ( ((LA54_1>=POSITIONAL_PARAM && LA54_1<=NAMED_PARAM)) ) {
                    alt54=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("682:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );", 54, 1, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("682:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );", 54, 0, input);
            
                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // JPQL.g:690:8: t= IN n= inputParameter
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_inExpression3901); if (failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_inExpression3907);
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
                    // JPQL.g:695:9: t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_inExpression3934); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3944); if (failed) return node;
                    // JPQL.g:697:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )
                    int alt53=2;
                    int LA53_0 = input.LA(1);
                    
                    if ( (LA53_0==IDENT||(LA53_0>=INTEGER_LITERAL && LA53_0<=NAMED_PARAM)) ) {
                        alt53=1;
                    }
                    else if ( (LA53_0==SELECT) ) {
                        alt53=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("697:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )", 53, 0, input);
                    
                        throw nvae;
                    }
                    switch (alt53) {
                        case 1 :
                            // JPQL.g:697:11: itemNode= inItem ( COMMA itemNode= inItem )*
                            {
                            pushFollow(FOLLOW_inItem_in_inExpression3960);
                            itemNode=inItem();
                            _fsp--;
                            if (failed) return node;
                            if ( backtracking==0 ) {
                               ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                            }
                            // JPQL.g:698:13: ( COMMA itemNode= inItem )*
                            loop52:
                            do {
                                int alt52=2;
                                int LA52_0 = input.LA(1);
                                
                                if ( (LA52_0==COMMA) ) {
                                    alt52=1;
                                }
                                
                            
                                switch (alt52) {
                            	case 1 :
                            	    // JPQL.g:698:15: COMMA itemNode= inItem
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_inExpression3978); if (failed) return node;
                            	    pushFollow(FOLLOW_inItem_in_inExpression3984);
                            	    itemNode=inItem();
                            	    _fsp--;
                            	    if (failed) return node;
                            	    if ( backtracking==0 ) {
                            	       ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                            	    }
                            	    
                            	    }
                            	    break;
                            
                            	default :
                            	    break loop52;
                                }
                            } while (true);

                            if ( backtracking==0 ) {
                              
                                              node = factory.newIn(t.getLine(), t.getCharPositionInLine(),
                                                                   not, left, ((inExpression_scope)inExpression_stack.peek()).items);
                                          
                            }
                            
                            }
                            break;
                        case 2 :
                            // JPQL.g:703:11: subqueryNode= subquery
                            {
                            pushFollow(FOLLOW_subquery_in_inExpression4019);
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

                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4053); if (failed) return node;
                    
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
    // JPQL.g:712:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant );
    public final Object inItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:714:7: (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant )
            int alt55=4;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt55=1;
                }
                break;
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt55=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt55=3;
                }
                break;
            case IDENT:
                {
                alt55=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("712:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant );", 55, 0, input);
            
                throw nvae;
            }
            
            switch (alt55) {
                case 1 :
                    // JPQL.g:714:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_inItem4083);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:715:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_inItem4097);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:716:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_inItem4111);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:717:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_inItem4125);
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
    // JPQL.g:720:1: likeExpression[boolean not, Object left] returns [Object node] : t= LIKE pattern= likeValue (escapeChars= escape )? ;
    public final Object likeExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object pattern = null;

        Object escapeChars = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:724:7: (t= LIKE pattern= likeValue (escapeChars= escape )? )
            // JPQL.g:724:7: t= LIKE pattern= likeValue (escapeChars= escape )?
            {
            t=(Token)input.LT(1);
            match(input,LIKE,FOLLOW_LIKE_in_likeExpression4157); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_likeExpression4163);
            pattern=likeValue();
            _fsp--;
            if (failed) return node;
            // JPQL.g:725:9: (escapeChars= escape )?
            int alt56=2;
            int LA56_0 = input.LA(1);
            
            if ( (LA56_0==ESCAPE) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // JPQL.g:725:10: escapeChars= escape
                    {
                    pushFollow(FOLLOW_escape_in_likeExpression4178);
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
    // JPQL.g:732:1: escape returns [Object node] : t= ESCAPE escapeClause= likeValue ;
    public final Object escape() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object escapeClause = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:736:7: (t= ESCAPE escapeClause= likeValue )
            // JPQL.g:736:7: t= ESCAPE escapeClause= likeValue
            {
            t=(Token)input.LT(1);
            match(input,ESCAPE,FOLLOW_ESCAPE_in_escape4218); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_escape4224);
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
    // JPQL.g:740:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );
    public final Object likeValue() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:742:7: (n= literalString | n= inputParameter )
            int alt57=2;
            int LA57_0 = input.LA(1);
            
            if ( ((LA57_0>=STRING_LITERAL_DOUBLE_QUOTED && LA57_0<=STRING_LITERAL_SINGLE_QUOTED)) ) {
                alt57=1;
            }
            else if ( ((LA57_0>=POSITIONAL_PARAM && LA57_0<=NAMED_PARAM)) ) {
                alt57=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("740:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );", 57, 0, input);
            
                throw nvae;
            }
            switch (alt57) {
                case 1 :
                    // JPQL.g:742:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_likeValue4264);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:743:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_likeValue4278);
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
    // JPQL.g:746:1: nullComparisonExpression[boolean not, Object left] returns [Object node] : t= NULL ;
    public final Object nullComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g:748:7: (t= NULL )
            // JPQL.g:748:7: t= NULL
            {
            t=(Token)input.LT(1);
            match(input,NULL,FOLLOW_NULL_in_nullComparisonExpression4311); if (failed) return node;
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
    // JPQL.g:752:1: emptyCollectionComparisonExpression[boolean not, Object left] returns [Object node] : t= EMPTY ;
    public final Object emptyCollectionComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g:754:7: (t= EMPTY )
            // JPQL.g:754:7: t= EMPTY
            {
            t=(Token)input.LT(1);
            match(input,EMPTY,FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4352); if (failed) return node;
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
    // JPQL.g:758:1: collectionMemberExpression[boolean not, Object left] returns [Object node] : t= MEMBER ( OF )? n= collectionValuedPathExpression ;
    public final Object collectionMemberExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:760:7: (t= MEMBER ( OF )? n= collectionValuedPathExpression )
            // JPQL.g:760:7: t= MEMBER ( OF )? n= collectionValuedPathExpression
            {
            t=(Token)input.LT(1);
            match(input,MEMBER,FOLLOW_MEMBER_in_collectionMemberExpression4393); if (failed) return node;
            // JPQL.g:760:17: ( OF )?
            int alt58=2;
            int LA58_0 = input.LA(1);
            
            if ( (LA58_0==OF) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // JPQL.g:760:18: OF
                    {
                    match(input,OF,FOLLOW_OF_in_collectionMemberExpression4396); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4404);
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
    // JPQL.g:767:1: existsExpression[boolean not] returns [Object node] : t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET ;
    public final Object existsExpression(boolean not) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object subqueryNode = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:771:7: (t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET )
            // JPQL.g:771:7: t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_existsExpression4444); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4446); if (failed) return node;
            pushFollow(FOLLOW_subquery_in_existsExpression4452);
            subqueryNode=subquery();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4454); if (failed) return node;
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
    // JPQL.g:778:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );
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
            // JPQL.g:780:7: (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("778:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );", 59, 0, input);
            
                throw nvae;
            }
            
            switch (alt59) {
                case 1 :
                    // JPQL.g:780:7: t1= EQUALS n= comparisonExpressionRightOperand
                    {
                    t1=(Token)input.LT(1);
                    match(input,EQUALS,FOLLOW_EQUALS_in_comparisonExpression4494); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4500);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newEquals(t1.getLine(), t1.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:782:7: t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t2=(Token)input.LT(1);
                    match(input,NOT_EQUAL_TO,FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4521); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4527);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newNotEquals(t2.getLine(), t2.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:784:7: t3= GREATER_THAN n= comparisonExpressionRightOperand
                    {
                    t3=(Token)input.LT(1);
                    match(input,GREATER_THAN,FOLLOW_GREATER_THAN_in_comparisonExpression4548); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4554);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThan(t3.getLine(), t3.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:786:7: t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t4=(Token)input.LT(1);
                    match(input,GREATER_THAN_EQUAL_TO,FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4575); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4581);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThanEqual(t4.getLine(), t4.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:788:7: t5= LESS_THAN n= comparisonExpressionRightOperand
                    {
                    t5=(Token)input.LT(1);
                    match(input,LESS_THAN,FOLLOW_LESS_THAN_in_comparisonExpression4602); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4608);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newLessThan(t5.getLine(), t5.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:790:7: t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t6=(Token)input.LT(1);
                    match(input,LESS_THAN_EQUAL_TO,FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4629); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4635);
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
    // JPQL.g:794:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression );
    public final Object comparisonExpressionRightOperand() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:796:7: (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression )
            int alt60=3;
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
                alt60=1;
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("794:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression );", 60, 0, input);
            
                throw nvae;
            }
            
            switch (alt60) {
                case 1 :
                    // JPQL.g:796:7: n= arithmeticExpression
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4676);
                    n=arithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:797:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4690);
                    n=nonArithmeticScalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:798:7: n= anyOrAllExpression
                    {
                    pushFollow(FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4704);
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
    // JPQL.g:801:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object arithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:803:7: (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt61=2;
            int LA61_0 = input.LA(1);
            
            if ( (LA61_0==ABS||LA61_0==AVG||(LA61_0>=CASE && LA61_0<=COALESCE)||LA61_0==COUNT||LA61_0==INDEX||LA61_0==KEY||LA61_0==LENGTH||LA61_0==LOCATE||LA61_0==MAX||(LA61_0>=MIN && LA61_0<=MOD)||LA61_0==NULLIF||(LA61_0>=SIZE && LA61_0<=SQRT)||LA61_0==SUM||LA61_0==VALUE||LA61_0==IDENT||(LA61_0>=PLUS && LA61_0<=MINUS)||(LA61_0>=INTEGER_LITERAL && LA61_0<=DOUBLE_LITERAL)||(LA61_0>=POSITIONAL_PARAM && LA61_0<=NAMED_PARAM)) ) {
                alt61=1;
            }
            else if ( (LA61_0==LEFT_ROUND_BRACKET) ) {
                int LA61_23 = input.LA(2);
                
                if ( (LA61_23==ABS||LA61_23==AVG||(LA61_23>=CASE && LA61_23<=COALESCE)||LA61_23==COUNT||LA61_23==INDEX||LA61_23==KEY||LA61_23==LENGTH||LA61_23==LOCATE||LA61_23==MAX||(LA61_23>=MIN && LA61_23<=MOD)||LA61_23==NULLIF||(LA61_23>=SIZE && LA61_23<=SQRT)||LA61_23==SUM||LA61_23==VALUE||LA61_23==IDENT||LA61_23==LEFT_ROUND_BRACKET||(LA61_23>=PLUS && LA61_23<=MINUS)||(LA61_23>=INTEGER_LITERAL && LA61_23<=DOUBLE_LITERAL)||(LA61_23>=POSITIONAL_PARAM && LA61_23<=NAMED_PARAM)) ) {
                    alt61=1;
                }
                else if ( (LA61_23==SELECT) ) {
                    alt61=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("801:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 61, 23, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("801:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 61, 0, input);
            
                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // JPQL.g:803:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4736);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:804:7: LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4746); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_arithmeticExpression4752);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4754); if (failed) return node;
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
    // JPQL.g:807:1: simpleArithmeticExpression returns [Object node] : n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* ;
    public final Object simpleArithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:811:7: (n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* )
            // JPQL.g:811:7: n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            {
            pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4786);
            n=arithmeticTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:812:9: (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            loop62:
            do {
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
            	    // JPQL.g:812:11: p= PLUS right= arithmeticTerm
            	    {
            	    p=(Token)input.LT(1);
            	    match(input,PLUS,FOLLOW_PLUS_in_simpleArithmeticExpression4802); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4808);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newPlus(p.getLine(), p.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:814:11: m= MINUS right= arithmeticTerm
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MINUS,FOLLOW_MINUS_in_simpleArithmeticExpression4837); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4843);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
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
    // $ANTLR end simpleArithmeticExpression

    
    // $ANTLR start arithmeticTerm
    // JPQL.g:819:1: arithmeticTerm returns [Object node] : n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* ;
    public final Object arithmeticTerm() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:823:7: (n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* )
            // JPQL.g:823:7: n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            {
            pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4900);
            n=arithmeticFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:824:9: (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            loop63:
            do {
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
            	    // JPQL.g:824:11: m= MULTIPLY right= arithmeticFactor
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MULTIPLY,FOLLOW_MULTIPLY_in_arithmeticTerm4916); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4922);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMultiply(m.getLine(), m.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:826:11: d= DIVIDE right= arithmeticFactor
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DIVIDE,FOLLOW_DIVIDE_in_arithmeticTerm4951); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4957);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
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
    // $ANTLR end arithmeticTerm

    
    // $ANTLR start arithmeticFactor
    // JPQL.g:831:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );
    public final Object arithmeticFactor() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:833:7: (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary )
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
                alt64=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("831:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );", 64, 0, input);
            
                throw nvae;
            }
            
            switch (alt64) {
                case 1 :
                    // JPQL.g:833:7: p= PLUS n= arithmeticPrimary
                    {
                    p=(Token)input.LT(1);
                    match(input,PLUS,FOLLOW_PLUS_in_arithmeticFactor5011); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5018);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newUnaryPlus(p.getLine(), p.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:835:7: m= MINUS n= arithmeticPrimary
                    {
                    m=(Token)input.LT(1);
                    match(input,MINUS,FOLLOW_MINUS_in_arithmeticFactor5040); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5046);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newUnaryMinus(m.getLine(), m.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:837:7: n= arithmeticPrimary
                    {
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5070);
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
    // JPQL.g:840:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric );
    public final Object arithmeticPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:842:7: ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric )
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
            case KEY:
            case VALUE:
            case IDENT:
                {
                alt65=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
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
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt65=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("840:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric );", 65, 0, input);
            
                throw nvae;
            }
            
            switch (alt65) {
                case 1 :
                    // JPQL.g:842:7: {...}?n= aggregateExpression
                    {
                    if ( !( aggregatesAllowed() ) ) {
                        if (backtracking>0) {failed=true; return node;}
                        throw new FailedPredicateException(input, "arithmeticPrimary", " aggregatesAllowed() ");
                    }
                    pushFollow(FOLLOW_aggregateExpression_in_arithmeticPrimary5104);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:843:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5118);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:844:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_arithmeticPrimary5132);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:845:7: n= caseExpression
                    {
                    pushFollow(FOLLOW_caseExpression_in_arithmeticPrimary5146);
                    n=caseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:846:7: n= functionsReturningNumerics
                    {
                    pushFollow(FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5160);
                    n=functionsReturningNumerics();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:847:7: LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5170); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5176);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5178); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 7 :
                    // JPQL.g:848:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_arithmeticPrimary5192);
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
    // JPQL.g:851:1: scalarExpression returns [Object node] : (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression );
    public final Object scalarExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null; 
        try {
            // JPQL.g:853:7: (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression )
            int alt66=2;
            int LA66_0 = input.LA(1);
            
            if ( (LA66_0==ABS||LA66_0==AVG||(LA66_0>=CASE && LA66_0<=COALESCE)||LA66_0==COUNT||LA66_0==INDEX||LA66_0==KEY||LA66_0==LENGTH||LA66_0==LOCATE||LA66_0==MAX||(LA66_0>=MIN && LA66_0<=MOD)||LA66_0==NULLIF||(LA66_0>=SIZE && LA66_0<=SQRT)||LA66_0==SUM||LA66_0==VALUE||LA66_0==IDENT||LA66_0==LEFT_ROUND_BRACKET||(LA66_0>=PLUS && LA66_0<=MINUS)||(LA66_0>=INTEGER_LITERAL && LA66_0<=DOUBLE_LITERAL)||(LA66_0>=POSITIONAL_PARAM && LA66_0<=NAMED_PARAM)) ) {
                alt66=1;
            }
            else if ( (LA66_0==CONCAT||(LA66_0>=CURRENT_DATE && LA66_0<=CURRENT_TIMESTAMP)||LA66_0==FALSE||LA66_0==LOWER||LA66_0==SUBSTRING||(LA66_0>=TRIM && LA66_0<=TYPE)||LA66_0==UPPER||(LA66_0>=STRING_LITERAL_DOUBLE_QUOTED && LA66_0<=STRING_LITERAL_SINGLE_QUOTED)) ) {
                alt66=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("851:1: scalarExpression returns [Object node] : (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression );", 66, 0, input);
            
                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // JPQL.g:853:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_scalarExpression5224);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:854:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5239);
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
    // JPQL.g:857:1: nonArithmeticScalarExpression returns [Object node] : (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= entityTypeExpression );
    public final Object nonArithmeticScalarExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null; 
        try {
            // JPQL.g:859:7: (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= entityTypeExpression )
            int alt67=5;
            switch ( input.LA(1) ) {
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
                {
                alt67=1;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt67=2;
                }
                break;
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt67=3;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt67=4;
                }
                break;
            case TYPE:
                {
                alt67=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("857:1: nonArithmeticScalarExpression returns [Object node] : (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= entityTypeExpression );", 67, 0, input);
            
                throw nvae;
            }
            
            switch (alt67) {
                case 1 :
                    // JPQL.g:859:7: n= functionsReturningDatetime
                    {
                    pushFollow(FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5271);
                    n=functionsReturningDatetime();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:860:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5285);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:861:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_nonArithmeticScalarExpression5299);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:862:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5313);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:863:7: n= entityTypeExpression
                    {
                    pushFollow(FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5327);
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
    // JPQL.g:866:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object anyOrAllExpression() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token y=null;
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:868:7: (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt68=3;
            switch ( input.LA(1) ) {
            case ALL:
                {
                alt68=1;
                }
                break;
            case ANY:
                {
                alt68=2;
                }
                break;
            case SOME:
                {
                alt68=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("866:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 68, 0, input);
            
                throw nvae;
            }
            
            switch (alt68) {
                case 1 :
                    // JPQL.g:868:7: a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,ALL,FOLLOW_ALL_in_anyOrAllExpression5357); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5359); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5365);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5367); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAll(a.getLine(), a.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:870:7: y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    y=(Token)input.LT(1);
                    match(input,ANY,FOLLOW_ANY_in_anyOrAllExpression5387); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5389); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5395);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5397); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAny(y.getLine(), y.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:872:7: s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    s=(Token)input.LT(1);
                    match(input,SOME,FOLLOW_SOME_in_anyOrAllExpression5417); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5419); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5425);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5427); if (failed) return node;
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
    // JPQL.g:876:1: entityTypeExpression returns [Object node] : n= typeDiscriminator ;
    public final Object entityTypeExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:878:7: (n= typeDiscriminator )
            // JPQL.g:878:7: n= typeDiscriminator
            {
            pushFollow(FOLLOW_typeDiscriminator_in_entityTypeExpression5467);
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
    // JPQL.g:881:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );
    public final Object typeDiscriminator() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token c=null;
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:883:7: (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET )
            int alt69=2;
            int LA69_0 = input.LA(1);
            
            if ( (LA69_0==TYPE) ) {
                int LA69_1 = input.LA(2);
                
                if ( (LA69_1==LEFT_ROUND_BRACKET) ) {
                    int LA69_2 = input.LA(3);
                    
                    if ( (LA69_2==KEY||LA69_2==VALUE||LA69_2==IDENT) ) {
                        alt69=1;
                    }
                    else if ( ((LA69_2>=POSITIONAL_PARAM && LA69_2<=NAMED_PARAM)) ) {
                        alt69=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("881:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 69, 2, input);
                    
                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("881:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 69, 1, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("881:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 69, 0, input);
            
                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // JPQL.g:883:7: a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5500); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5502); if (failed) return node;
                    pushFollow(FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5508);
                    n=variableOrSingleValuedPath();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5510); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newType(a.getLine(), a.getCharPositionInLine(), n);
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:884:7: c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET
                    {
                    c=(Token)input.LT(1);
                    match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5525); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5527); if (failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_typeDiscriminator5533);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5535); if (failed) return node;
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
    // JPQL.g:887:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );
    public final Object caseExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:889:6: (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression )
            int alt70=4;
            switch ( input.LA(1) ) {
            case CASE:
                {
                int LA70_1 = input.LA(2);
                
                if ( (LA70_1==KEY||LA70_1==TYPE||LA70_1==VALUE||LA70_1==IDENT) ) {
                    alt70=1;
                }
                else if ( (LA70_1==WHEN) ) {
                    alt70=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("887:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );", 70, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case COALESCE:
                {
                alt70=3;
                }
                break;
            case NULLIF:
                {
                alt70=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("887:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );", 70, 0, input);
            
                throw nvae;
            }
            
            switch (alt70) {
                case 1 :
                    // JPQL.g:889:6: n= simpleCaseExpression
                    {
                    pushFollow(FOLLOW_simpleCaseExpression_in_caseExpression5570);
                    n=simpleCaseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:890:6: n= generalCaseExpression
                    {
                    pushFollow(FOLLOW_generalCaseExpression_in_caseExpression5583);
                    n=generalCaseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:891:6: n= coalesceExpression
                    {
                    pushFollow(FOLLOW_coalesceExpression_in_caseExpression5596);
                    n=coalesceExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:892:6: n= nullIfExpression
                    {
                    pushFollow(FOLLOW_nullIfExpression_in_caseExpression5609);
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
    // JPQL.g:895:1: simpleCaseExpression returns [Object node] : a= CASE caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END ;
    public final Object simpleCaseExpression() throws RecognitionException {
        simpleCaseExpression_stack.push(new simpleCaseExpression_scope());

        Object node = null;
    
        Token a=null;
        Object w = null;

        Object e = null;
        
    
        
            node = null;
            ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens = new ArrayList();
    
        try {
            // JPQL.g:903:6: (a= CASE caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END )
            // JPQL.g:903:6: a= CASE caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END
            {
            a=(Token)input.LT(1);
            match(input,CASE,FOLLOW_CASE_in_simpleCaseExpression5647); if (failed) return node;
            pushFollow(FOLLOW_caseOperand_in_simpleCaseExpression5649);
            caseOperand();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5655);
            w=simpleWhenClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens.add(w);
            }
            // JPQL.g:903:93: (w= simpleWhenClause )*
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);
                
                if ( (LA71_0==WHEN) ) {
                    alt71=1;
                }
                
            
                switch (alt71) {
            	case 1 :
            	    // JPQL.g:903:94: w= simpleWhenClause
            	    {
            	    pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5664);
            	    w=simpleWhenClause();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens.add(w);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop71;
                }
            } while (true);

            match(input,ELSE,FOLLOW_ELSE_in_simpleCaseExpression5670); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleCaseExpression5676);
            e=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,END,FOLLOW_END_in_simpleCaseExpression5678); if (failed) return node;
            if ( backtracking==0 ) {
              
                             node = factory.newCaseClause(a.getLine(), a.getCharPositionInLine(), 
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
    // JPQL.g:910:1: generalCaseExpression returns [Object node] : a= CASE w= whenClause ( whenClause )* ELSE e= scalarExpression END ;
    public final Object generalCaseExpression() throws RecognitionException {
        generalCaseExpression_stack.push(new generalCaseExpression_scope());

        Object node = null;
    
        Token a=null;
        Object w = null;

        Object e = null;
        
    
        
            node = null;
            ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens = new ArrayList();
    
        try {
            // JPQL.g:918:6: (a= CASE w= whenClause ( whenClause )* ELSE e= scalarExpression END )
            // JPQL.g:918:6: a= CASE w= whenClause ( whenClause )* ELSE e= scalarExpression END
            {
            a=(Token)input.LT(1);
            match(input,CASE,FOLLOW_CASE_in_generalCaseExpression5722); if (failed) return node;
            pushFollow(FOLLOW_whenClause_in_generalCaseExpression5728);
            w=whenClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens.add(w);
            }
            // JPQL.g:918:76: ( whenClause )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);
                
                if ( (LA72_0==WHEN) ) {
                    alt72=1;
                }
                
            
                switch (alt72) {
            	case 1 :
            	    // JPQL.g:918:77: whenClause
            	    {
            	    pushFollow(FOLLOW_whenClause_in_generalCaseExpression5733);
            	    whenClause();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens.add(w);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop72;
                }
            } while (true);

            match(input,ELSE,FOLLOW_ELSE_in_generalCaseExpression5739); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_generalCaseExpression5745);
            e=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,END,FOLLOW_END_in_generalCaseExpression5747); if (failed) return node;
            if ( backtracking==0 ) {
              
                             node = factory.newCaseClause(a.getLine(), a.getCharPositionInLine(), 
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
    // JPQL.g:925:1: coalesceExpression returns [Object node] : c= COALESCE RIGHT_ROUND_BRACKET p= scalarExpression ( COMMA scalarExpression )+ LEFT_ROUND_BRACKET ;
    public final Object coalesceExpression() throws RecognitionException {
        coalesceExpression_stack.push(new coalesceExpression_scope());

        Object node = null;
    
        Token c=null;
        Object p = null;
        
    
        
            node = null;
            ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries = new ArrayList();
    
        try {
            // JPQL.g:933:6: (c= COALESCE RIGHT_ROUND_BRACKET p= scalarExpression ( COMMA scalarExpression )+ LEFT_ROUND_BRACKET )
            // JPQL.g:933:6: c= COALESCE RIGHT_ROUND_BRACKET p= scalarExpression ( COMMA scalarExpression )+ LEFT_ROUND_BRACKET
            {
            c=(Token)input.LT(1);
            match(input,COALESCE,FOLLOW_COALESCE_in_coalesceExpression5791); if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5793); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5799);
            p=scalarExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries.add(p);
            }
            // JPQL.g:933:107: ( COMMA scalarExpression )+
            int cnt73=0;
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);
                
                if ( (LA73_0==COMMA) ) {
                    alt73=1;
                }
                
            
                switch (alt73) {
            	case 1 :
            	    // JPQL.g:933:108: COMMA scalarExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_coalesceExpression5804); if (failed) return node;
            	    pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5806);
            	    scalarExpression();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries.add(p);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt73 >= 1 ) break loop73;
            	    if (backtracking>0) {failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(73, input);
                        throw eee;
                }
                cnt73++;
            } while (true);

            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5812); if (failed) return node;
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
    // JPQL.g:940:1: nullIfExpression returns [Object node] : n= NULLIF RIGHT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression LEFT_ROUND_BRACKET ;
    public final Object nullIfExpression() throws RecognitionException {

        Object node = null;
    
        Token n=null;
        Object l = null;

        Object r = null;
        
    
        node = null;
        try {
            // JPQL.g:942:6: (n= NULLIF RIGHT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression LEFT_ROUND_BRACKET )
            // JPQL.g:942:6: n= NULLIF RIGHT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression LEFT_ROUND_BRACKET
            {
            n=(Token)input.LT(1);
            match(input,NULLIF,FOLLOW_NULLIF_in_nullIfExpression5853); if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression5855); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5861);
            l=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_nullIfExpression5863); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5869);
            r=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5871); if (failed) return node;
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
    // JPQL.g:950:1: caseOperand returns [Object node] : (n= stateFieldPathExpression | n= typeDiscriminator );
    public final Object caseOperand() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:952:6: (n= stateFieldPathExpression | n= typeDiscriminator )
            int alt74=2;
            int LA74_0 = input.LA(1);
            
            if ( (LA74_0==KEY||LA74_0==VALUE||LA74_0==IDENT) ) {
                alt74=1;
            }
            else if ( (LA74_0==TYPE) ) {
                alt74=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("950:1: caseOperand returns [Object node] : (n= stateFieldPathExpression | n= typeDiscriminator );", 74, 0, input);
            
                throw nvae;
            }
            switch (alt74) {
                case 1 :
                    // JPQL.g:952:6: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_caseOperand5918);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:953:6: n= typeDiscriminator
                    {
                    pushFollow(FOLLOW_typeDiscriminator_in_caseOperand5932);
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
    // JPQL.g:956:1: whenClause returns [Object node] : w= WHEN c= conditionalExpression THEN a= scalarExpression ;
    public final Object whenClause() throws RecognitionException {

        Object node = null;
    
        Token w=null;
        Object c = null;

        Object a = null;
        
    
        node = null;
        try {
            // JPQL.g:958:6: (w= WHEN c= conditionalExpression THEN a= scalarExpression )
            // JPQL.g:958:6: w= WHEN c= conditionalExpression THEN a= scalarExpression
            {
            w=(Token)input.LT(1);
            match(input,WHEN,FOLLOW_WHEN_in_whenClause5967); if (failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whenClause5973);
            c=conditionalExpression();
            _fsp--;
            if (failed) return node;
            match(input,THEN,FOLLOW_THEN_in_whenClause5975); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_whenClause5981);
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
    // JPQL.g:965:1: simpleWhenClause returns [Object node] : w= WHEN c= scalarExpression THEN a= scalarExpression ;
    public final Object simpleWhenClause() throws RecognitionException {

        Object node = null;
    
        Token w=null;
        Object c = null;

        Object a = null;
        
    
        node = null;
        try {
            // JPQL.g:967:6: (w= WHEN c= scalarExpression THEN a= scalarExpression )
            // JPQL.g:967:6: w= WHEN c= scalarExpression THEN a= scalarExpression
            {
            w=(Token)input.LT(1);
            match(input,WHEN,FOLLOW_WHEN_in_simpleWhenClause6023); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause6029);
            c=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,THEN,FOLLOW_THEN_in_simpleWhenClause6031); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause6037);
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
    // JPQL.g:974:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );
    public final Object variableOrSingleValuedPath() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:976:7: (n= singleValuedPathExpression | n= variableAccessOrTypeConstant )
            int alt75=2;
            int LA75_0 = input.LA(1);
            
            if ( (LA75_0==IDENT) ) {
                int LA75_1 = input.LA(2);
                
                if ( (LA75_1==DOT) ) {
                    alt75=1;
                }
                else if ( (LA75_1==RIGHT_ROUND_BRACKET) ) {
                    alt75=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("974:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );", 75, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA75_0==KEY||LA75_0==VALUE) ) {
                alt75=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("974:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );", 75, 0, input);
            
                throw nvae;
            }
            switch (alt75) {
                case 1 :
                    // JPQL.g:976:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6074);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:977:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6088);
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
    // JPQL.g:980:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );
    public final Object stringPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:982:7: (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression )
            int alt76=4;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt76=1;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt76=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt76=3;
                }
                break;
            case KEY:
            case VALUE:
            case IDENT:
                {
                alt76=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("980:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );", 76, 0, input);
            
                throw nvae;
            }
            
            switch (alt76) {
                case 1 :
                    // JPQL.g:982:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_stringPrimary6120);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:983:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_stringPrimary6134);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:984:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_stringPrimary6148);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:985:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_stringPrimary6162);
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
    // JPQL.g:990:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );
    public final Object literal() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:992:7: (n= literalNumeric | n= literalBoolean | n= literalString )
            int alt77=3;
            switch ( input.LA(1) ) {
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt77=1;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt77=2;
                }
                break;
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt77=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("990:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );", 77, 0, input);
            
                throw nvae;
            }
            
            switch (alt77) {
                case 1 :
                    // JPQL.g:992:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_literal6196);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:993:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_literal6210);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:994:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_literal6224);
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
    // JPQL.g:997:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );
    public final Object literalNumeric() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token l=null;
        Token f=null;
        Token d=null;
    
         node = null; 
        try {
            // JPQL.g:999:7: (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL )
            int alt78=4;
            switch ( input.LA(1) ) {
            case INTEGER_LITERAL:
                {
                alt78=1;
                }
                break;
            case LONG_LITERAL:
                {
                alt78=2;
                }
                break;
            case FLOAT_LITERAL:
                {
                alt78=3;
                }
                break;
            case DOUBLE_LITERAL:
                {
                alt78=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("997:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );", 78, 0, input);
            
                throw nvae;
            }
            
            switch (alt78) {
                case 1 :
                    // JPQL.g:999:7: i= INTEGER_LITERAL
                    {
                    i=(Token)input.LT(1);
                    match(input,INTEGER_LITERAL,FOLLOW_INTEGER_LITERAL_in_literalNumeric6254); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newIntegerLiteral(i.getLine(), i.getCharPositionInLine(), 
                                                                   Integer.valueOf(i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1004:7: l= LONG_LITERAL
                    {
                    l=(Token)input.LT(1);
                    match(input,LONG_LITERAL,FOLLOW_LONG_LITERAL_in_literalNumeric6270); if (failed) return node;
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
                    // JPQL.g:1012:7: f= FLOAT_LITERAL
                    {
                    f=(Token)input.LT(1);
                    match(input,FLOAT_LITERAL,FOLLOW_FLOAT_LITERAL_in_literalNumeric6291); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newFloatLiteral(f.getLine(), f.getCharPositionInLine(),
                                                                 Float.valueOf(f.getText()));
                              
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1017:7: d= DOUBLE_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,DOUBLE_LITERAL,FOLLOW_DOUBLE_LITERAL_in_literalNumeric6311); if (failed) return node;
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
    // JPQL.g:1024:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );
    public final Object literalBoolean() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token f=null;
    
         node = null; 
        try {
            // JPQL.g:1026:7: (t= TRUE | f= FALSE )
            int alt79=2;
            int LA79_0 = input.LA(1);
            
            if ( (LA79_0==TRUE) ) {
                alt79=1;
            }
            else if ( (LA79_0==FALSE) ) {
                alt79=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1024:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );", 79, 0, input);
            
                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // JPQL.g:1026:7: t= TRUE
                    {
                    t=(Token)input.LT(1);
                    match(input,TRUE,FOLLOW_TRUE_in_literalBoolean6349); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newBooleanLiteral(t.getLine(), t.getCharPositionInLine(), Boolean.TRUE); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1028:7: f= FALSE
                    {
                    f=(Token)input.LT(1);
                    match(input,FALSE,FOLLOW_FALSE_in_literalBoolean6371); if (failed) return node;
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
    // JPQL.g:1032:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );
    public final Object literalString() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token s=null;
    
         node = null; 
        try {
            // JPQL.g:1034:7: (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED )
            int alt80=2;
            int LA80_0 = input.LA(1);
            
            if ( (LA80_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                alt80=1;
            }
            else if ( (LA80_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                alt80=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1032:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );", 80, 0, input);
            
                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // JPQL.g:1034:7: d= STRING_LITERAL_DOUBLE_QUOTED
                    {
                    d=(Token)input.LT(1);
                    match(input,STRING_LITERAL_DOUBLE_QUOTED,FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6410); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newStringLiteral(d.getLine(), d.getCharPositionInLine(), 
                                                                  convertStringLiteral(d.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1039:7: s= STRING_LITERAL_SINGLE_QUOTED
                    {
                    s=(Token)input.LT(1);
                    match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6431); if (failed) return node;
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

    
    // $ANTLR start inputParameter
    // JPQL.g:1046:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );
    public final Object inputParameter() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token n=null;
    
         node = null; 
        try {
            // JPQL.g:1048:7: (p= POSITIONAL_PARAM | n= NAMED_PARAM )
            int alt81=2;
            int LA81_0 = input.LA(1);
            
            if ( (LA81_0==POSITIONAL_PARAM) ) {
                alt81=1;
            }
            else if ( (LA81_0==NAMED_PARAM) ) {
                alt81=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1046:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );", 81, 0, input);
            
                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // JPQL.g:1048:7: p= POSITIONAL_PARAM
                    {
                    p=(Token)input.LT(1);
                    match(input,POSITIONAL_PARAM,FOLLOW_POSITIONAL_PARAM_in_inputParameter6469); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  // skip the leading ?
                                  String text = p.getText().substring(1);
                                  node = factory.newPositionalParameter(p.getLine(), p.getCharPositionInLine(), text); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1054:7: n= NAMED_PARAM
                    {
                    n=(Token)input.LT(1);
                    match(input,NAMED_PARAM,FOLLOW_NAMED_PARAM_in_inputParameter6489); if (failed) return node;
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
    // JPQL.g:1062:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index );
    public final Object functionsReturningNumerics() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1064:7: (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index )
            int alt82=7;
            switch ( input.LA(1) ) {
            case ABS:
                {
                alt82=1;
                }
                break;
            case LENGTH:
                {
                alt82=2;
                }
                break;
            case MOD:
                {
                alt82=3;
                }
                break;
            case SQRT:
                {
                alt82=4;
                }
                break;
            case LOCATE:
                {
                alt82=5;
                }
                break;
            case SIZE:
                {
                alt82=6;
                }
                break;
            case INDEX:
                {
                alt82=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1062:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index );", 82, 0, input);
            
                throw nvae;
            }
            
            switch (alt82) {
                case 1 :
                    // JPQL.g:1064:7: n= abs
                    {
                    pushFollow(FOLLOW_abs_in_functionsReturningNumerics6529);
                    n=abs();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1065:7: n= length
                    {
                    pushFollow(FOLLOW_length_in_functionsReturningNumerics6543);
                    n=length();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1066:7: n= mod
                    {
                    pushFollow(FOLLOW_mod_in_functionsReturningNumerics6557);
                    n=mod();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1067:7: n= sqrt
                    {
                    pushFollow(FOLLOW_sqrt_in_functionsReturningNumerics6571);
                    n=sqrt();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:1068:7: n= locate
                    {
                    pushFollow(FOLLOW_locate_in_functionsReturningNumerics6585);
                    n=locate();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:1069:7: n= size
                    {
                    pushFollow(FOLLOW_size_in_functionsReturningNumerics6599);
                    n=size();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 7 :
                    // JPQL.g:1070:7: n= index
                    {
                    pushFollow(FOLLOW_index_in_functionsReturningNumerics6613);
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
    // JPQL.g:1073:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );
    public final Object functionsReturningDatetime() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token t=null;
        Token ts=null;
    
         node = null; 
        try {
            // JPQL.g:1075:7: (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP )
            int alt83=3;
            switch ( input.LA(1) ) {
            case CURRENT_DATE:
                {
                alt83=1;
                }
                break;
            case CURRENT_TIME:
                {
                alt83=2;
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                alt83=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1073:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );", 83, 0, input);
            
                throw nvae;
            }
            
            switch (alt83) {
                case 1 :
                    // JPQL.g:1075:7: d= CURRENT_DATE
                    {
                    d=(Token)input.LT(1);
                    match(input,CURRENT_DATE,FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6643); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentDate(d.getLine(), d.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1077:7: t= CURRENT_TIME
                    {
                    t=(Token)input.LT(1);
                    match(input,CURRENT_TIME,FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6664); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentTime(t.getLine(), t.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1079:7: ts= CURRENT_TIMESTAMP
                    {
                    ts=(Token)input.LT(1);
                    match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6684); if (failed) return node;
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
    // JPQL.g:1083:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );
    public final Object functionsReturningStrings() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1085:7: (n= concat | n= substring | n= trim | n= upper | n= lower )
            int alt84=5;
            switch ( input.LA(1) ) {
            case CONCAT:
                {
                alt84=1;
                }
                break;
            case SUBSTRING:
                {
                alt84=2;
                }
                break;
            case TRIM:
                {
                alt84=3;
                }
                break;
            case UPPER:
                {
                alt84=4;
                }
                break;
            case LOWER:
                {
                alt84=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1083:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );", 84, 0, input);
            
                throw nvae;
            }
            
            switch (alt84) {
                case 1 :
                    // JPQL.g:1085:7: n= concat
                    {
                    pushFollow(FOLLOW_concat_in_functionsReturningStrings6724);
                    n=concat();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1086:7: n= substring
                    {
                    pushFollow(FOLLOW_substring_in_functionsReturningStrings6738);
                    n=substring();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1087:7: n= trim
                    {
                    pushFollow(FOLLOW_trim_in_functionsReturningStrings6752);
                    n=trim();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1088:7: n= upper
                    {
                    pushFollow(FOLLOW_upper_in_functionsReturningStrings6766);
                    n=upper();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:1089:7: n= lower
                    {
                    pushFollow(FOLLOW_lower_in_functionsReturningStrings6780);
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
    // JPQL.g:1093:1: concat returns [Object node] : c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary ( COMMA arg= stringPrimary )+ RIGHT_ROUND_BRACKET ;
    public final Object concat() throws RecognitionException {
        concat_stack.push(new concat_scope());

        Object node = null;
    
        Token c=null;
        Object firstArg = null;

        Object arg = null;
        
    
         
            node = null;
            ((concat_scope)concat_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1101:7: (c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary ( COMMA arg= stringPrimary )+ RIGHT_ROUND_BRACKET )
            // JPQL.g:1101:7: c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary ( COMMA arg= stringPrimary )+ RIGHT_ROUND_BRACKET
            {
            c=(Token)input.LT(1);
            match(input,CONCAT,FOLLOW_CONCAT_in_concat6815); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_concat6826); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_concat6841);
            firstArg=stringPrimary();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((concat_scope)concat_stack.peek()).items.add(firstArg);
            }
            // JPQL.g:1103:72: ( COMMA arg= stringPrimary )+
            int cnt85=0;
            loop85:
            do {
                int alt85=2;
                int LA85_0 = input.LA(1);
                
                if ( (LA85_0==COMMA) ) {
                    alt85=1;
                }
                
            
                switch (alt85) {
            	case 1 :
            	    // JPQL.g:1103:73: COMMA arg= stringPrimary
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_concat6846); if (failed) return node;
            	    pushFollow(FOLLOW_stringPrimary_in_concat6852);
            	    arg=stringPrimary();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((concat_scope)concat_stack.peek()).items.add(arg);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt85 >= 1 ) break loop85;
            	    if (backtracking>0) {failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(85, input);
                        throw eee;
                }
                cnt85++;
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_concat6866); if (failed) return node;
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
    // JPQL.g:1108:1: substring returns [Object node] : s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression ( COMMA lengthNode= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET ;
    public final Object substring() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object string = null;

        Object start = null;

        Object lengthNode = null;
        
    
         
            node = null;
            lengthNode = null;
    
        try {
            // JPQL.g:1113:7: (s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression ( COMMA lengthNode= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET )
            // JPQL.g:1113:7: s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression ( COMMA lengthNode= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SUBSTRING,FOLLOW_SUBSTRING_in_substring6904); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_substring6917); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_substring6931);
            string=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring6933); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_substring6947);
            start=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1117:9: ( COMMA lengthNode= simpleArithmeticExpression )?
            int alt86=2;
            int LA86_0 = input.LA(1);
            
            if ( (LA86_0==COMMA) ) {
                alt86=1;
            }
            switch (alt86) {
                case 1 :
                    // JPQL.g:1117:10: COMMA lengthNode= simpleArithmeticExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_substring6958); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_substring6964);
                    lengthNode=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_substring6976); if (failed) return node;
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
    // JPQL.g:1130:1: trim returns [Object node] : t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object trim() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        TrimSpecification trimSpecIndicator = null;

        Object trimCharNode = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1134:7: (t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1134:7: t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,TRIM,FOLLOW_TRIM_in_trim7014); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_trim7024); if (failed) return node;
            // JPQL.g:1136:9: ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )?
            int alt87=2;
            int LA87_0 = input.LA(1);
            
            if ( (LA87_0==LEADING) && (synpred2())) {
                alt87=1;
            }
            else if ( (LA87_0==TRAILING) && (synpred2())) {
                alt87=1;
            }
            else if ( (LA87_0==BOTH) && (synpred2())) {
                alt87=1;
            }
            else if ( (LA87_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                int LA87_4 = input.LA(2);
                
                if ( (LA87_4==FROM) && (synpred2())) {
                    alt87=1;
                }
            }
            else if ( (LA87_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                int LA87_5 = input.LA(2);
                
                if ( (LA87_5==FROM) && (synpred2())) {
                    alt87=1;
                }
            }
            else if ( (LA87_0==POSITIONAL_PARAM) ) {
                int LA87_6 = input.LA(2);
                
                if ( (LA87_6==FROM) && (synpred2())) {
                    alt87=1;
                }
            }
            else if ( (LA87_0==NAMED_PARAM) ) {
                int LA87_7 = input.LA(2);
                
                if ( (LA87_7==FROM) && (synpred2())) {
                    alt87=1;
                }
            }
            else if ( (LA87_0==FROM) && (synpred2())) {
                alt87=1;
            }
            switch (alt87) {
                case 1 :
                    // JPQL.g:1136:11: ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM
                    {
                    pushFollow(FOLLOW_trimSpec_in_trim7052);
                    trimSpecIndicator=trimSpec();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_trimChar_in_trim7058);
                    trimCharNode=trimChar();
                    _fsp--;
                    if (failed) return node;
                    match(input,FROM,FOLLOW_FROM_in_trim7060); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_stringPrimary_in_trim7078);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_trim7088); if (failed) return node;
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
    // JPQL.g:1145:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );
    public final TrimSpecification trimSpec() throws RecognitionException {

        TrimSpecification trimSpec = null;
    
         trimSpec = TrimSpecification.BOTH; 
        try {
            // JPQL.g:1147:7: ( LEADING | TRAILING | BOTH | )
            int alt88=4;
            switch ( input.LA(1) ) {
            case LEADING:
                {
                alt88=1;
                }
                break;
            case TRAILING:
                {
                alt88=2;
                }
                break;
            case BOTH:
                {
                alt88=3;
                }
                break;
            case FROM:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt88=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return trimSpec;}
                NoViableAltException nvae =
                    new NoViableAltException("1145:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );", 88, 0, input);
            
                throw nvae;
            }
            
            switch (alt88) {
                case 1 :
                    // JPQL.g:1147:7: LEADING
                    {
                    match(input,LEADING,FOLLOW_LEADING_in_trimSpec7124); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.LEADING; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1149:7: TRAILING
                    {
                    match(input,TRAILING,FOLLOW_TRAILING_in_trimSpec7142); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.TRAILING; 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1151:7: BOTH
                    {
                    match(input,BOTH,FOLLOW_BOTH_in_trimSpec7160); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.BOTH; 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1154:5: 
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
    // JPQL.g:1156:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );
    public final Object trimChar() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1158:7: (n= literalString | n= inputParameter | )
            int alt89=3;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt89=1;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt89=2;
                }
                break;
            case FROM:
                {
                alt89=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1156:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );", 89, 0, input);
            
                throw nvae;
            }
            
            switch (alt89) {
                case 1 :
                    // JPQL.g:1158:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_trimChar7207);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1159:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_trimChar7221);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1161:5: 
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
    // JPQL.g:1163:1: upper returns [Object node] : u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object upper() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1165:7: (u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1165:7: u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            u=(Token)input.LT(1);
            match(input,UPPER,FOLLOW_UPPER_in_upper7258); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_upper7260); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_upper7266);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_upper7268); if (failed) return node;
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
    // JPQL.g:1169:1: lower returns [Object node] : l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object lower() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1171:7: (l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1171:7: l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOWER,FOLLOW_LOWER_in_lower7306); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_lower7308); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_lower7314);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_lower7316); if (failed) return node;
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
    // JPQL.g:1176:1: abs returns [Object node] : a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object abs() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1178:7: (a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1178:7: a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            a=(Token)input.LT(1);
            match(input,ABS,FOLLOW_ABS_in_abs7355); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_abs7357); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_abs7363);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_abs7365); if (failed) return node;
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
    // JPQL.g:1182:1: length returns [Object node] : l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object length() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1184:7: (l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1184:7: l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LENGTH,FOLLOW_LENGTH_in_length7403); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_length7405); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_length7411);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_length7413); if (failed) return node;
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
    // JPQL.g:1188:1: locate returns [Object node] : l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET ;
    public final Object locate() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object pattern = null;

        Object n = null;

        Object startPos = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1192:7: (l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET )
            // JPQL.g:1192:7: l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOCATE,FOLLOW_LOCATE_in_locate7451); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_locate7461); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_locate7476);
            pattern=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_locate7478); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_locate7484);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1195:9: ( COMMA startPos= simpleArithmeticExpression )?
            int alt90=2;
            int LA90_0 = input.LA(1);
            
            if ( (LA90_0==COMMA) ) {
                alt90=1;
            }
            switch (alt90) {
                case 1 :
                    // JPQL.g:1195:11: COMMA startPos= simpleArithmeticExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_locate7496); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_locate7502);
                    startPos=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_locate7515); if (failed) return node;
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
    // JPQL.g:1203:1: size returns [Object node] : s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ;
    public final Object size() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1205:7: (s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1205:7: s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SIZE,FOLLOW_SIZE_in_size7553); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_size7564); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_size7570);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_size7572); if (failed) return node;
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
    // JPQL.g:1210:1: mod returns [Object node] : m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object mod() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Object left = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1214:7: (m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1214:7: m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            m=(Token)input.LT(1);
            match(input,MOD,FOLLOW_MOD_in_mod7610); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mod7612); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod7626);
            left=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_mod7628); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod7643);
            right=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mod7653); if (failed) return node;
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
    // JPQL.g:1221:1: sqrt returns [Object node] : s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object sqrt() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1223:7: (s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1223:7: s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SQRT,FOLLOW_SQRT_in_sqrt7691); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7702); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_sqrt7708);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7710); if (failed) return node;
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
    // JPQL.g:1228:1: index returns [Object node] : s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object index() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1230:7: (s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // JPQL.g:1230:7: s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,INDEX,FOLLOW_INDEX_in_index7752); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_index7754); if (failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_index7760);
            n=variableAccessOrTypeConstant();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_index7762); if (failed) return node;
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
    // JPQL.g:1234:1: subquery returns [Object node] : select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? ;
    public final Object subquery() throws RecognitionException {

        Object node = null;
    
        Object select = null;

        Object from = null;

        Object where = null;

        Object groupBy = null;

        Object having = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1238:7: (select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? )
            // JPQL.g:1238:7: select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )?
            {
            pushFollow(FOLLOW_simpleSelectClause_in_subquery7803);
            select=simpleSelectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_subqueryFromClause_in_subquery7818);
            from=subqueryFromClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1240:7: (where= whereClause )?
            int alt91=2;
            int LA91_0 = input.LA(1);
            
            if ( (LA91_0==WHERE) ) {
                alt91=1;
            }
            switch (alt91) {
                case 1 :
                    // JPQL.g:1240:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_subquery7833);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:1241:7: (groupBy= groupByClause )?
            int alt92=2;
            int LA92_0 = input.LA(1);
            
            if ( (LA92_0==GROUP) ) {
                alt92=1;
            }
            switch (alt92) {
                case 1 :
                    // JPQL.g:1241:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_subquery7848);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:1242:7: (having= havingClause )?
            int alt93=2;
            int LA93_0 = input.LA(1);
            
            if ( (LA93_0==HAVING) ) {
                alt93=1;
            }
            switch (alt93) {
                case 1 :
                    // JPQL.g:1242:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_subquery7864);
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
    // JPQL.g:1249:1: simpleSelectClause returns [Object node] : s= SELECT ( DISTINCT )? n= simpleSelectExpression ;
    public final Object simpleSelectClause() throws RecognitionException {
        simpleSelectClause_stack.push(new simpleSelectClause_scope());

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         
            node = null; 
            ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = false;
    
        try {
            // JPQL.g:1257:7: (s= SELECT ( DISTINCT )? n= simpleSelectExpression )
            // JPQL.g:1257:7: s= SELECT ( DISTINCT )? n= simpleSelectExpression
            {
            s=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_simpleSelectClause7907); if (failed) return node;
            // JPQL.g:1257:16: ( DISTINCT )?
            int alt94=2;
            int LA94_0 = input.LA(1);
            
            if ( (LA94_0==DISTINCT) ) {
                alt94=1;
            }
            switch (alt94) {
                case 1 :
                    // JPQL.g:1257:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_simpleSelectClause7910); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_simpleSelectExpression_in_simpleSelectClause7926);
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
    // JPQL.g:1267:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );
    public final Object simpleSelectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1269:7: (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant )
            int alt95=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA95_1 = input.LA(2);
                
                if ( (LA95_1==DOT) ) {
                    alt95=1;
                }
                else if ( (LA95_1==FROM) ) {
                    alt95=3;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1267:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );", 95, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case KEY:
            case VALUE:
                {
                alt95=1;
                }
                break;
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt95=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1267:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );", 95, 0, input);
            
                throw nvae;
            }
            
            switch (alt95) {
                case 1 :
                    // JPQL.g:1269:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_simpleSelectExpression7966);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1270:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_simpleSelectExpression7981);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1271:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression7996);
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
    // JPQL.g:1275:1: subqueryFromClause returns [Object node] : f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )* ;
    public final Object subqueryFromClause() throws RecognitionException {
        subqueryFromClause_stack.push(new subqueryFromClause_scope());

        Object node = null;
    
        Token f=null;
        Object c = null;
        
    
         
            node = null; 
            ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g:1283:7: (f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )* )
            // JPQL.g:1283:7: f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )*
            {
            f=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_subqueryFromClause8031); if (failed) return node;
            pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8033);
            subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g:1284:9: ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )*
            loop96:
            do {
                int alt96=3;
                int LA96_0 = input.LA(1);
                
                if ( (LA96_0==COMMA) ) {
                    alt96=1;
                }
                else if ( (LA96_0==IN) ) {
                    alt96=2;
                }
                
            
                switch (alt96) {
            	case 1 :
            	    // JPQL.g:1284:11: COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_subqueryFromClause8047); if (failed) return node;
            	    pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8049);
            	    subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            	    _fsp--;
            	    if (failed) return node;
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:1285:17: c= collectionMemberDeclaration
            	    {
            	    pushFollow(FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8074);
            	    c=collectionMemberDeclaration();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls.add(c);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop96;
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
    // JPQL.g:1289:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );
    public final void subselectIdentificationVariableDeclaration(List varDecls) throws RecognitionException {
        Token i=null;
        Object n = null;
        
    
         Object node; 
        try {
            // JPQL.g:1291:7: ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration )
            int alt99=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA99_1 = input.LA(2);
                
                if ( (LA99_1==DOT) ) {
                    alt99=2;
                }
                else if ( (LA99_1==AS||LA99_1==IDENT) ) {
                    alt99=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1289:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 99, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case KEY:
                {
                int LA99_2 = input.LA(2);
                
                if ( (LA99_2==LEFT_ROUND_BRACKET) ) {
                    alt99=2;
                }
                else if ( (LA99_2==AS||LA99_2==IDENT) ) {
                    alt99=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1289:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 99, 2, input);
                
                    throw nvae;
                }
                }
                break;
            case VALUE:
                {
                int LA99_3 = input.LA(2);
                
                if ( (LA99_3==LEFT_ROUND_BRACKET) ) {
                    alt99=2;
                }
                else if ( (LA99_3==AS||LA99_3==IDENT) ) {
                    alt99=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1289:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 99, 3, input);
                
                    throw nvae;
                }
                }
                break;
            case IN:
                {
                int LA99_4 = input.LA(2);
                
                if ( (LA99_4==LEFT_ROUND_BRACKET) ) {
                    alt99=3;
                }
                else if ( (LA99_4==AS||LA99_4==IDENT) ) {
                    alt99=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1289:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 99, 4, input);
                
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
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
            case WS:
            case TEXTCHAR:
            case HEX_DIGIT:
            case HEX_LITERAL:
            case INTEGER_SUFFIX:
            case OCTAL_LITERAL:
            case NUMERIC_DIGITS:
            case DOUBLE_SUFFIX:
            case EXPONENT:
            case FLOAT_SUFFIX:
                {
                alt99=1;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1289:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 99, 0, input);
            
                throw nvae;
            }
            
            switch (alt99) {
                case 1 :
                    // JPQL.g:1291:7: identificationVariableDeclaration[varDecls]
                    {
                    pushFollow(FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8112);
                    identificationVariableDeclaration(varDecls);
                    _fsp--;
                    if (failed) return ;
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1292:7: n= associationPathExpression ( AS )? i= IDENT ( join )*
                    {
                    pushFollow(FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8125);
                    n=associationPathExpression();
                    _fsp--;
                    if (failed) return ;
                    // JPQL.g:1292:37: ( AS )?
                    int alt97=2;
                    int LA97_0 = input.LA(1);
                    
                    if ( (LA97_0==AS) ) {
                        alt97=1;
                    }
                    switch (alt97) {
                        case 1 :
                            // JPQL.g:1292:38: AS
                            {
                            match(input,AS,FOLLOW_AS_in_subselectIdentificationVariableDeclaration8128); if (failed) return ;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8134); if (failed) return ;
                    // JPQL.g:1292:51: ( join )*
                    loop98:
                    do {
                        int alt98=2;
                        int LA98_0 = input.LA(1);
                        
                        if ( (LA98_0==INNER||LA98_0==JOIN||LA98_0==LEFT) ) {
                            alt98=1;
                        }
                        
                    
                        switch (alt98) {
                    	case 1 :
                    	    // JPQL.g:1292:52: join
                    	    {
                    	    pushFollow(FOLLOW_join_in_subselectIdentificationVariableDeclaration8137);
                    	    join();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {
                    	       varDecls.add(n); 
                    	    }
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    break loop98;
                        }
                    } while (true);

                    if ( backtracking==0 ) {
                       
                                  varDecls.add(factory.newVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                       n, i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1297:7: n= collectionMemberDeclaration
                    {
                    pushFollow(FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8164);
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
    // JPQL.g:1300:1: orderByClause returns [Object node] : o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* ;
    public final Object orderByClause() throws RecognitionException {
        orderByClause_stack.push(new orderByClause_scope());

        Object node = null;
    
        Token o=null;
        Object n = null;
        
    
         
            node = null; 
            ((orderByClause_scope)orderByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1308:7: (o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* )
            // JPQL.g:1308:7: o= ORDER BY n= orderByItem ( COMMA n= orderByItem )*
            {
            o=(Token)input.LT(1);
            match(input,ORDER,FOLLOW_ORDER_in_orderByClause8197); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_orderByClause8199); if (failed) return node;
            pushFollow(FOLLOW_orderByItem_in_orderByClause8213);
            n=orderByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1310:9: ( COMMA n= orderByItem )*
            loop100:
            do {
                int alt100=2;
                int LA100_0 = input.LA(1);
                
                if ( (LA100_0==COMMA) ) {
                    alt100=1;
                }
                
            
                switch (alt100) {
            	case 1 :
            	    // JPQL.g:1310:10: COMMA n= orderByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_orderByClause8228); if (failed) return node;
            	    pushFollow(FOLLOW_orderByItem_in_orderByClause8234);
            	    n=orderByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop100;
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
    // JPQL.g:1314:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );
    public final Object orderByItem() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token d=null;
        Token i=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1316:7: (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) )
            int alt103=2;
            int LA103_0 = input.LA(1);
            
            if ( (LA103_0==IDENT) ) {
                int LA103_1 = input.LA(2);
                
                if ( (LA103_1==EOF||LA103_1==ASC||LA103_1==DESC||LA103_1==COMMA) ) {
                    alt103=2;
                }
                else if ( (LA103_1==DOT) ) {
                    alt103=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1314:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );", 103, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA103_0==KEY||LA103_0==VALUE) ) {
                alt103=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1314:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );", 103, 0, input);
            
                throw nvae;
            }
            switch (alt103) {
                case 1 :
                    // JPQL.g:1316:7: n= stateFieldPathExpression (a= ASC | d= DESC | )
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_orderByItem8280);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    // JPQL.g:1317:9: (a= ASC | d= DESC | )
                    int alt101=3;
                    switch ( input.LA(1) ) {
                    case ASC:
                        {
                        alt101=1;
                        }
                        break;
                    case DESC:
                        {
                        alt101=2;
                        }
                        break;
                    case EOF:
                    case COMMA:
                        {
                        alt101=3;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("1317:9: (a= ASC | d= DESC | )", 101, 0, input);
                    
                        throw nvae;
                    }
                    
                    switch (alt101) {
                        case 1 :
                            // JPQL.g:1317:11: a= ASC
                            {
                            a=(Token)input.LT(1);
                            match(input,ASC,FOLLOW_ASC_in_orderByItem8294); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), n); 
                            }
                            
                            }
                            break;
                        case 2 :
                            // JPQL.g:1319:11: d= DESC
                            {
                            d=(Token)input.LT(1);
                            match(input,DESC,FOLLOW_DESC_in_orderByItem8323); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), n); 
                            }
                            
                            }
                            break;
                        case 3 :
                            // JPQL.g:1322:13: 
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
                    // JPQL.g:1324:8: i= IDENT (a= ASC | d= DESC | )
                    {
                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_orderByItem8385); if (failed) return node;
                    // JPQL.g:1325:9: (a= ASC | d= DESC | )
                    int alt102=3;
                    switch ( input.LA(1) ) {
                    case ASC:
                        {
                        alt102=1;
                        }
                        break;
                    case DESC:
                        {
                        alt102=2;
                        }
                        break;
                    case EOF:
                    case COMMA:
                        {
                        alt102=3;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("1325:9: (a= ASC | d= DESC | )", 102, 0, input);
                    
                        throw nvae;
                    }
                    
                    switch (alt102) {
                        case 1 :
                            // JPQL.g:1325:11: a= ASC
                            {
                            a=(Token)input.LT(1);
                            match(input,ASC,FOLLOW_ASC_in_orderByItem8399); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), i.getText()); 
                            }
                            
                            }
                            break;
                        case 2 :
                            // JPQL.g:1327:11: d= DESC
                            {
                            d=(Token)input.LT(1);
                            match(input,DESC,FOLLOW_DESC_in_orderByItem8428); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), i.getText()); 
                            }
                            
                            }
                            break;
                        case 3 :
                            // JPQL.g:1330:13: 
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
    // JPQL.g:1334:1: groupByClause returns [Object node] : g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* ;
    public final Object groupByClause() throws RecognitionException {
        groupByClause_stack.push(new groupByClause_scope());

        Object node = null;
    
        Token g=null;
        Object n = null;
        
    
         
            node = null; 
            ((groupByClause_scope)groupByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1342:7: (g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* )
            // JPQL.g:1342:7: g= GROUP BY n= groupByItem ( COMMA n= groupByItem )*
            {
            g=(Token)input.LT(1);
            match(input,GROUP,FOLLOW_GROUP_in_groupByClause8509); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_groupByClause8511); if (failed) return node;
            pushFollow(FOLLOW_groupByItem_in_groupByClause8525);
            n=groupByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1344:9: ( COMMA n= groupByItem )*
            loop104:
            do {
                int alt104=2;
                int LA104_0 = input.LA(1);
                
                if ( (LA104_0==COMMA) ) {
                    alt104=1;
                }
                
            
                switch (alt104) {
            	case 1 :
            	    // JPQL.g:1344:10: COMMA n= groupByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_groupByClause8538); if (failed) return node;
            	    pushFollow(FOLLOW_groupByItem_in_groupByClause8544);
            	    n=groupByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop104;
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
    // JPQL.g:1348:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );
    public final Object groupByItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1350:7: (n= stateFieldPathExpression | n= variableAccessOrTypeConstant )
            int alt105=2;
            int LA105_0 = input.LA(1);
            
            if ( (LA105_0==IDENT) ) {
                int LA105_1 = input.LA(2);
                
                if ( (LA105_1==EOF||LA105_1==HAVING||LA105_1==ORDER||LA105_1==COMMA||LA105_1==RIGHT_ROUND_BRACKET) ) {
                    alt105=2;
                }
                else if ( (LA105_1==DOT) ) {
                    alt105=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1348:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );", 105, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA105_0==KEY||LA105_0==VALUE) ) {
                alt105=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1348:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );", 105, 0, input);
            
                throw nvae;
            }
            switch (alt105) {
                case 1 :
                    // JPQL.g:1350:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_groupByItem8590);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1351:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_groupByItem8604);
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
    // JPQL.g:1354:1: havingClause returns [Object node] : h= HAVING n= conditionalExpression ;
    public final Object havingClause() throws RecognitionException {

        Object node = null;
    
        Token h=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1356:7: (h= HAVING n= conditionalExpression )
            // JPQL.g:1356:7: h= HAVING n= conditionalExpression
            {
            h=(Token)input.LT(1);
            match(input,HAVING,FOLLOW_HAVING_in_havingClause8634); if (failed) return node;
            if ( backtracking==0 ) {
               setAggregatesAllowed(true); 
            }
            pushFollow(FOLLOW_conditionalExpression_in_havingClause8651);
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
        // JPQL.g:636:7: ( LEFT_ROUND_BRACKET conditionalExpression )
        // JPQL.g:636:8: LEFT_ROUND_BRACKET conditionalExpression
        {
        match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_synpred13495); if (failed) return ;
        pushFollow(FOLLOW_conditionalExpression_in_synpred13497);
        conditionalExpression();
        _fsp--;
        if (failed) return ;
        
        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // JPQL.g:1136:11: ( trimSpec trimChar FROM )
        // JPQL.g:1136:13: trimSpec trimChar FROM
        {
        pushFollow(FOLLOW_trimSpec_in_synpred27039);
        trimSpec();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_trimChar_in_synpred27041);
        trimChar();
        _fsp--;
        if (failed) return ;
        match(input,FROM,FOLLOW_FROM_in_synpred27043); if (failed) return ;
        
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
    public static final BitSet FOLLOW_UPDATE_in_updateClause1006 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_updateClause1012 = new BitSet(new long[]{0x0000000000000102L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_updateClause1025 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_updateClause1033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_setClause1082 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1088 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_setClause1101 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1107 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_setAssignmentTarget_in_setAssignmentClause1165 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_EQUALS_in_setAssignmentClause1169 = new BitSet(new long[]{0xC066E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_newValue_in_setAssignmentClause1175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_setAssignmentTarget1205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_setAssignmentTarget1220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalarExpression_in_newValue1252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_newValue1266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteClause_in_deleteStatement1310 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_whereClause_in_deleteStatement1323 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_deleteStatement1333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_deleteClause1366 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_FROM_in_deleteClause1368 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_deleteClause1374 = new BitSet(new long[]{0x0000000000000102L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_deleteClause1387 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_deleteClause1393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_selectClause1440 = new BitSet(new long[]{0xC0CEE910489FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause1443 = new BitSet(new long[]{0xC0CEE910481FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_selectItem_in_selectClause1459 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_selectClause1487 = new BitSet(new long[]{0xC0CEE910481FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_selectItem_in_selectClause1493 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_selectExpression_in_selectItem1589 = new BitSet(new long[]{0x0000000000000102L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_selectItem1593 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_selectItem1601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_selectExpression1644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalarExpression_in_selectExpression1658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBJECT_in_selectExpression1668 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1670 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_selectExpression1676 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constructorExpression_in_selectExpression1693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntryExpression_in_selectExpression1708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_in_mapEntryExpression1740 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1742 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1748 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1782 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_pathExprOrVariableAccess1797 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExprOrVariableAccess1803 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KEY_in_qualifiedIdentificationVariable1873 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1875 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1881 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUE_in_qualifiedIdentificationVariable1898 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1900 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1906 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AVG_in_aggregateExpression1941 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1943 = new BitSet(new long[]{0x0000010000800000L,0x0000000000004800L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1946 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1964 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAX_in_aggregateExpression1987 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1989 = new BitSet(new long[]{0x0000010000800000L,0x0000000000004800L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1992 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2011 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MIN_in_aggregateExpression2033 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2035 = new BitSet(new long[]{0x0000010000800000L,0x0000000000004800L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2038 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2056 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUM_in_aggregateExpression2078 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2080 = new BitSet(new long[]{0x0000010000800000L,0x0000000000004800L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2083 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2101 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COUNT_in_aggregateExpression2123 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2125 = new BitSet(new long[]{0x0000010000800000L,0x0000000000004800L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2128 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_aggregateExpression2146 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_constructorExpression2191 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_constructorName_in_constructorExpression2197 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2207 = new BitSet(new long[]{0xC046E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2222 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_COMMA_in_constructorExpression2237 = new BitSet(new long[]{0xC046E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2243 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2299 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_constructorName2313 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2317 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_scalarExpression_in_constructorItem2361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_constructorItem2375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_fromClause2409 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2411 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_fromClause2423 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2428 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_fromClause2453 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2519 = new BitSet(new long[]{0x000004A000000002L});
    public static final BitSet FOLLOW_join_in_identificationVariableDeclaration2538 = new BitSet(new long[]{0x000004A000000002L});
    public static final BitSet FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2573 = new BitSet(new long[]{0x0000000000000100L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_rangeVariableDeclaration2576 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_rangeVariableDeclaration2582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_joinSpec_in_join2665 = new BitSet(new long[]{0x0000000080000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2679 = new BitSet(new long[]{0x0000000000000100L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_join2682 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_join2688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FETCH_in_join2710 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_in_joinSpec2762 = new BitSet(new long[]{0x0800008000000000L});
    public static final BitSet FOLLOW_OUTER_in_joinSpec2765 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_INNER_in_joinSpec2774 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_JOIN_in_joinSpec2780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_collectionMemberDeclaration2808 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2810 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2816 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2818 = new BitSet(new long[]{0x0000000000000100L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_collectionMemberDeclaration2828 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_collectionMemberDeclaration2834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_collectionValuedPathExpression2872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_associationPathExpression2904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_joinAssociationPathExpression2936 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_joinAssociationPathExpression2940 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_joinAssociationPathExpression2946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_singleValuedPathExpression2986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_stateFieldPathExpression3018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExpression3050 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_pathExpression3065 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExpression3071 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_IDENT_in_variableAccessOrTypeConstant3167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause3205 = new BitSet(new long[]{0xC056E910601FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_conditionalExpression_in_whereClause3211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3253 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_OR_in_conditionalExpression3268 = new BitSet(new long[]{0xC056E910601FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3274 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3329 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AND_in_conditionalTerm3344 = new BitSet(new long[]{0xC056E910601FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3350 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_NOT_in_conditionalFactor3405 = new BitSet(new long[]{0xC046E910601FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_conditionalPrimary_in_conditionalFactor3424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsExpression_in_conditionalFactor3453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3510 = new BitSet(new long[]{0xC056E910601FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalPrimary3516 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleConditionalExpression_in_conditionalPrimary3532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_simpleConditionalExpression3564 = new BitSet(new long[]{0x0011104800000800L,0x0000000001F10000L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3585 = new BitSet(new long[]{0x0011104800000800L,0x0000000001F10000L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3640 = new BitSet(new long[]{0x0001100800000800L});
    public static final BitSet FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_simpleConditionalExpressionRemainder3659 = new BitSet(new long[]{0x0030000002000000L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3664 = new BitSet(new long[]{0x0020000002000000L});
    public static final BitSet FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_betweenExpression_in_conditionWithNotExpression3707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_likeExpression_in_conditionWithNotExpression3722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inExpression_in_conditionWithNotExpression3736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullComparisonExpression_in_isExpression3785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptyCollectionComparisonExpression_in_isExpression3800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpression3833 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3847 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_AND_in_betweenExpression3849 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3901 = new BitSet(new long[]{0x0000000000000000L,0x0000001800000000L});
    public static final BitSet FOLLOW_inputParameter_in_inExpression3907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3934 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3944 = new BitSet(new long[]{0x1000000000000000L,0x0000001FE0004000L});
    public static final BitSet FOLLOW_inItem_in_inExpression3960 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression3978 = new BitSet(new long[]{0x0000000000000000L,0x0000001FE0004000L});
    public static final BitSet FOLLOW_inItem_in_inExpression3984 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_subquery_in_inExpression4019 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_inItem4083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_inItem4097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_inItem4111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_inItem4125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIKE_in_likeExpression4157 = new BitSet(new long[]{0x0000000000000000L,0x0000001E00000000L});
    public static final BitSet FOLLOW_likeValue_in_likeExpression4163 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_escape_in_likeExpression4178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_in_escape4218 = new BitSet(new long[]{0x0000000000000000L,0x0000001E00000000L});
    public static final BitSet FOLLOW_likeValue_in_escape4224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_likeValue4264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_likeValue4278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_nullComparisonExpression4311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEMBER_in_collectionMemberExpression4393 = new BitSet(new long[]{0x0100010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_OF_in_collectionMemberExpression4396 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_existsExpression4444 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4446 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_subquery_in_existsExpression4452 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_comparisonExpression4494 = new BitSet(new long[]{0xC046E910401FC4B0L,0x0000001FE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4521 = new BitSet(new long[]{0xC046E910401FC4B0L,0x0000001FE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_in_comparisonExpression4548 = new BitSet(new long[]{0xC046E910401FC4B0L,0x0000001FE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4575 = new BitSet(new long[]{0xC046E910401FC4B0L,0x0000001FE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_in_comparisonExpression4602 = new BitSet(new long[]{0xC046E910401FC4B0L,0x0000001FE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4629 = new BitSet(new long[]{0xC046E910401FC4B0L,0x0000001FE6024CE7L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4746 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_subquery_in_arithmeticExpression4752 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4786 = new BitSet(new long[]{0x0000000000000002L,0x0000000006000000L});
    public static final BitSet FOLLOW_PLUS_in_simpleArithmeticExpression4802 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4808 = new BitSet(new long[]{0x0000000000000002L,0x0000000006000000L});
    public static final BitSet FOLLOW_MINUS_in_simpleArithmeticExpression4837 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4843 = new BitSet(new long[]{0x0000000000000002L,0x0000000006000000L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4900 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_MULTIPLY_in_arithmeticTerm4916 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4922 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_DIVIDE_in_arithmeticTerm4951 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4957 = new BitSet(new long[]{0x0000000000000002L,0x0000000018000000L});
    public static final BitSet FOLLOW_PLUS_in_arithmeticFactor5011 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E0024804L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_arithmeticFactor5040 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E0024804L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_arithmeticPrimary5104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_arithmeticPrimary5132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_caseExpression_in_arithmeticPrimary5146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5170 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5176 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_arithmeticPrimary5192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_scalarExpression5224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_nonArithmeticScalarExpression5299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_anyOrAllExpression5357 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5359 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5365 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_anyOrAllExpression5387 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5389 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5395 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SOME_in_anyOrAllExpression5417 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5419 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5425 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_entityTypeExpression5467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5500 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5502 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5508 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5525 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5527 = new BitSet(new long[]{0x0000000000000000L,0x0000001800000000L});
    public static final BitSet FOLLOW_inputParameter_in_typeDiscriminator5533 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleCaseExpression_in_caseExpression5570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_generalCaseExpression_in_caseExpression5583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_coalesceExpression_in_caseExpression5596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullIfExpression_in_caseExpression5609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_simpleCaseExpression5647 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004880L});
    public static final BitSet FOLLOW_caseOperand_in_simpleCaseExpression5649 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5655 = new BitSet(new long[]{0x0000000001000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5664 = new BitSet(new long[]{0x0000000001000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ELSE_in_simpleCaseExpression5670 = new BitSet(new long[]{0xC046E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_simpleCaseExpression5676 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_END_in_simpleCaseExpression5678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_generalCaseExpression5722 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5728 = new BitSet(new long[]{0x0000000001000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5733 = new BitSet(new long[]{0x0000000001000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ELSE_in_generalCaseExpression5739 = new BitSet(new long[]{0xC046E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_generalCaseExpression5745 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_END_in_generalCaseExpression5747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COALESCE_in_coalesceExpression5791 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5793 = new BitSet(new long[]{0xC046E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5799 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_coalesceExpression5804 = new BitSet(new long[]{0xC046E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5806 = new BitSet(new long[]{0x0000000000000000L,0x0000000000028000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULLIF_in_nullIfExpression5853 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression5855 = new BitSet(new long[]{0xC046E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5861 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_nullIfExpression5863 = new BitSet(new long[]{0xC046E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5869 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_caseOperand5918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_caseOperand5932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_whenClause5967 = new BitSet(new long[]{0xC056E910601FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_conditionalExpression_in_whenClause5973 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_THEN_in_whenClause5975 = new BitSet(new long[]{0xC046E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_whenClause5981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_simpleWhenClause6023 = new BitSet(new long[]{0xC046E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause6029 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_THEN_in_simpleWhenClause6031 = new BitSet(new long[]{0xC046E910401FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause6037 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_stringPrimary6120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_stringPrimary6134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_stringPrimary6148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_stringPrimary6162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_literal6196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_literal6210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_literal6224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_LITERAL_in_literalNumeric6254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LONG_LITERAL_in_literalNumeric6270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_LITERAL_in_literalNumeric6291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_LITERAL_in_literalNumeric6311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literalBoolean6349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literalBoolean6371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POSITIONAL_PARAM_in_inputParameter6469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_PARAM_in_inputParameter6489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_abs_in_functionsReturningNumerics6529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_length_in_functionsReturningNumerics6543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mod_in_functionsReturningNumerics6557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqrt_in_functionsReturningNumerics6571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_locate_in_functionsReturningNumerics6585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_size_in_functionsReturningNumerics6599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_index_in_functionsReturningNumerics6613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_concat_in_functionsReturningStrings6724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_substring_in_functionsReturningStrings6738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trim_in_functionsReturningStrings6752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_upper_in_functionsReturningStrings6766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lower_in_functionsReturningStrings6780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONCAT_in_concat6815 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_concat6826 = new BitSet(new long[]{0x0000410000010000L,0x0000001E00004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_concat6841 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_concat6846 = new BitSet(new long[]{0x0000410000010000L,0x0000001E00004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_concat6852 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_concat6866 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSTRING_in_substring6904 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_substring6917 = new BitSet(new long[]{0x0000410000010000L,0x0000001E00004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_substring6931 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_substring6933 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring6947 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_COMMA_in_substring6958 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring6964 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_substring6976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIM_in_trim7014 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_trim7024 = new BitSet(new long[]{0x0000430100011000L,0x0000001E00004C32L});
    public static final BitSet FOLLOW_trimSpec_in_trim7052 = new BitSet(new long[]{0x0000000100000000L,0x0000001E00000000L});
    public static final BitSet FOLLOW_trimChar_in_trim7058 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_FROM_in_trim7060 = new BitSet(new long[]{0x0000410000010000L,0x0000001E00004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_trim7078 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_trim7088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEADING_in_trimSpec7124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRAILING_in_trimSpec7142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOTH_in_trimSpec7160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_trimChar7207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_trimChar7221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPPER_in_upper7258 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_upper7260 = new BitSet(new long[]{0x0000410000010000L,0x0000001E00004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_upper7266 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_upper7268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOWER_in_lower7306 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_lower7308 = new BitSet(new long[]{0x0000410000010000L,0x0000001E00004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_lower7314 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_lower7316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABS_in_abs7355 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_abs7357 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_abs7363 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_abs7365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LENGTH_in_length7403 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_length7405 = new BitSet(new long[]{0x0000410000010000L,0x0000001E00004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_length7411 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_length7413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCATE_in_locate7451 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_locate7461 = new BitSet(new long[]{0x0000410000010000L,0x0000001E00004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_locate7476 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_locate7478 = new BitSet(new long[]{0x0000410000010000L,0x0000001E00004C22L});
    public static final BitSet FOLLOW_stringPrimary_in_locate7484 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_COMMA_in_locate7496 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_locate7502 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_locate7515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIZE_in_size7553 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_size7564 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_size7570 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_size7572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mod7610 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mod7612 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod7626 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_mod7628 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod7643 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mod7653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQRT_in_sqrt7691 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7702 = new BitSet(new long[]{0xC046A9100002C410L,0x00000019E6024804L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_sqrt7708 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDEX_in_index7752 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_index7754 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_index7760 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_index7762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleSelectClause_in_subquery7803 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_subqueryFromClause_in_subquery7818 = new BitSet(new long[]{0x0000000600000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_whereClause_in_subquery7833 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_groupByClause_in_subquery7848 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_havingClause_in_subquery7864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_simpleSelectClause7907 = new BitSet(new long[]{0x0002810000820400L,0x0000000000004804L});
    public static final BitSet FOLLOW_DISTINCT_in_simpleSelectClause7910 = new BitSet(new long[]{0x0002810000020400L,0x0000000000004804L});
    public static final BitSet FOLLOW_simpleSelectExpression_in_simpleSelectClause7926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_simpleSelectExpression7966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_simpleSelectExpression7981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression7996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_subqueryFromClause8031 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8033 = new BitSet(new long[]{0x0000000800000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_subqueryFromClause8047 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8049 = new BitSet(new long[]{0x0000000800000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8074 = new BitSet(new long[]{0x0000000800000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8125 = new BitSet(new long[]{0x0000000000000100L,0x0000000000004000L});
    public static final BitSet FOLLOW_AS_in_subselectIdentificationVariableDeclaration8128 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8134 = new BitSet(new long[]{0x000004A000000002L});
    public static final BitSet FOLLOW_join_in_subselectIdentificationVariableDeclaration8137 = new BitSet(new long[]{0x000004A000000002L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_orderByClause8197 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_orderByClause8199 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8213 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_orderByClause8228 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8234 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_orderByItem8280 = new BitSet(new long[]{0x0000000000200202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem8294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem8323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_orderByItem8385 = new BitSet(new long[]{0x0000000000200202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem8399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem8428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_groupByClause8509 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_groupByClause8511 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause8525 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_groupByClause8538 = new BitSet(new long[]{0x0000010000000000L,0x0000000000004800L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause8544 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_groupByItem8590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_groupByItem8604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HAVING_in_havingClause8634 = new BitSet(new long[]{0xC056E910601FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_conditionalExpression_in_havingClause8651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_synpred13495 = new BitSet(new long[]{0xC056E910601FC410L,0x0000001FE6024CE6L});
    public static final BitSet FOLLOW_conditionalExpression_in_synpred13497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trimSpec_in_synpred27039 = new BitSet(new long[]{0x0000000100000000L,0x0000001E00000000L});
    public static final BitSet FOLLOW_trimChar_in_synpred27041 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_FROM_in_synpred27043 = new BitSet(new long[]{0x0000000000000002L});

}